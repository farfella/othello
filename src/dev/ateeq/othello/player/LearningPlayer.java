package dev.ateeq.othello.player;

import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;
import dev.ateeq.othello.environment.Othello;
import dev.ateeq.othello.environment.OthelloPlayerType;
import dev.ateeq.othello.logic.BoardState;
import dev.ateeq.othello.logic.Rotation;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * @author ateeq
 */
public class LearningPlayer extends AbstractPlayer {
    // #1 = cost
    // #2 = locations

    public static double FIXED_REWARD = 0.0;
    public static double REWARD_WIN = 100;
    public static double REWARD_LOSE = -100;
    public static double DISCOUNT_FACTOR = 0.5; // gamma
    public static double LEARNING_RATE = 0.5; // alpha
    protected HashMap<BoardState, TreeMap<Double, LinkedList<BoardLocation>>> stateTransitionMatrix;
    private BoardState state;

    //	private boolean firstTime;
    private Double previousValue;
    //	private Double previousScore;
    private BoardLocation action;
    private int[] previousMoveOffset = {0, 0};
    private int previousAngle = 0;
    private int previousSize = 0;

    public LearningPlayer(HashMap<BoardState, TreeMap<Double, LinkedList<BoardLocation>>> stm) {
        playerName = "Learning Player";
//		firstTime = true;

        if (stm == null)
            stateTransitionMatrix = new HashMap<BoardState, TreeMap<Double, LinkedList<BoardLocation>>>();
        else
            stateTransitionMatrix = stm;
    }

    public static HashMap<BoardState, TreeMap<Double, LinkedList<BoardLocation>>> loadStateTransitionMatrix(String fileName) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;

        HashMap<BoardState, TreeMap<Double, LinkedList<BoardLocation>>> stm = null;
        try {
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);
            stm = (HashMap<BoardState, TreeMap<Double, LinkedList<BoardLocation>>>) ois.readObject();
            ois.close();
        } catch (FileNotFoundException ignored) {
        } catch (IOException | ClassNotFoundException ioex) {
            ioex.printStackTrace();
        }

        return stm;
    }

    // returns max(Q....)
    public double max(BoardState s_prime) {
        TreeMap<Double, LinkedList<BoardLocation>> as = null;

        as = stateTransitionMatrix.get(s_prime);
		/*
		for (BoardState i : stateTransitionMatrix.keySet())
		{
			if (i.equals(s_prime))
			{
				System.out.println("i == s'");
				as = stateTransitionMatrix.get(i);
				break;
			}
			else if (i.toString() == s_prime.toString())
			{
				System.out.println("i.ts = s'.ts");
				as = stateTransitionMatrix.get(i);
				break;
			}
		}
		*/

        double max = FIXED_REWARD;
        if (as != null) {
            max = as.firstKey();
            for (Double d : as.keySet())
                if (d > max)
                    max = d;
        }

        return max;
    }

    public double Q(BoardState s, BoardLocation a) {
        ArrayList<BoardLocation> al = features.tokensFlipped(player, a);
        BoardState s_prime = new BoardState(s, a, player, al);

        // Q(s_t, a_t) = Q(s, a) + alpha(s, a) * [ r(s, a) + gamma * max_a Q(s_t+1, a_t+1) - Q(s, a) ]
        //               old v     rate            expected discount                         old value
        //double score = r(s, a) + DISCOUNT_FACTOR * max(s_prime);

        double oldScore = r(s, a);
        double score = oldScore + LEARNING_RATE * (r(s, a) + DISCOUNT_FACTOR * max(s_prime) - oldScore);

        if (score > REWARD_WIN) score = REWARD_WIN;
        else if (score < REWARD_LOSE) score = REWARD_LOSE;

        evaluate2(s, a, score);

        return score;
    }

    public double Q(BoardState s, BoardLocation a, BoardState news, BoardLocation newa) {
        ArrayList<BoardLocation> al = features.tokensFlipped(player, a);
        BoardState s_prime = news;//new BoardState(news, newa, player, al);

        // Q(s_t, a_t) = Q(s, a) + alpha(s, a) * [ r(s, a) + gamma * max_a Q(s_t+1, a_t+1) - Q(s, a) ]
        //               old v     rate            expected discount                         old value
        //double score = r(s, a) + DISCOUNT_FACTOR * max(s_prime);

        double oldScore = r(s, a);
        double score = oldScore + LEARNING_RATE * (r(s, a) + DISCOUNT_FACTOR * max(s_prime) - oldScore);

        if (score > REWARD_WIN) score = REWARD_WIN;
        else if (score < REWARD_LOSE) score = REWARD_LOSE;

        evaluate2(s, a, score);

        return score;
    }

    public double r(BoardState s, BoardLocation a) {
        TreeMap<Double, LinkedList<BoardLocation>> as = stateTransitionMatrix.get(s);
        if (as != null) {
            for (Double d : as.keySet()) {
                LinkedList<BoardLocation> bl = as.get(d);
                if (bl != null) {
                    for (BoardLocation l : bl)
                        if (l.equals(a))
                            return d;
                }
            }
        }

        return FIXED_REWARD;
    }

    /**
     * evaluate()
     * give(stimulus)
     * respond()
     */
    public boolean playerTurn() {
        boolean canPlay = super.playerTurn();

        //if (!firstTime)
        //	evaluate();
        //else
        //	firstTime = false;

        if (canPlay) {
            //when we break out of loop, we expect "playerMove" to have been set to
            //a valid move by the MouseListener above
            //so, we just accept what the user gave us and continue
            doStrategy();

            environment.playerMove(playerMove, player);
        } else
            environment.playerMove(null, player);

        return canPlay;
    }

    /**
     * EVALUATION
     */
    void evaluate() {
        Double score = getCurrentScore();

        if (Othello.DEBUGGING) {
            System.out.println("score: " + getCurrentScore());
//			System.out.println("previous score: " + previousScore);
            System.out.println();
        }

        if (score == previousValue)
            return;

        // if prev = 0, and score > 0, store
        // if prev > 0, and score > 0, return
        // if prev < 0, and score > prev
        if (previousValue > 0 && score > previousValue)
            return;

        if (previousValue < 0 && score > previousValue)
            return;

        if (stateTransitionMatrix.get(state) != null) {
            // remove the previous move from the previous value
            if (stateTransitionMatrix.get(state).get(previousValue) != null) {
                stateTransitionMatrix.get(state).get(previousValue).remove(action);
                if (stateTransitionMatrix.get(state).get(previousValue).size() == 0)
                    stateTransitionMatrix.get(state).remove(previousValue);
            } else {
                System.err.println("error: could not remove " + action);
            }

            // add the previous move to the new score
            if (stateTransitionMatrix.get(state).get(score) == null)
                stateTransitionMatrix.get(state).put(score, new LinkedList<BoardLocation>());
            stateTransitionMatrix.get(state).get(score).add(action);

            // cleanup
            if (stateTransitionMatrix.get(state).size() == 0) {
                if (Othello.DEBUGGING) {
                    System.out.println("removing empty state:");
                    System.out.print(state);
                }

                stateTransitionMatrix.remove(state);
            }
        }
    }

    void evaluate2(BoardState s, BoardLocation a, Double score) {
        // find state in STM
        TreeMap<Double, LinkedList<BoardLocation>> as = stateTransitionMatrix.get(s);

        // not found in STM, add state
        if (as == null) {
            as = new TreeMap<Double, LinkedList<BoardLocation>>();
            stateTransitionMatrix.put(s, as);
        }

        // find this action from all the scores lists
        if (as.keySet() != null) {
            LinkedList<Double> dr = new LinkedList<Double>();
            for (Double d : as.keySet()) {
                LinkedList<BoardLocation> bl = as.get(d);
                // remove the previous score
                if (bl != null) {
                    LinkedList<BoardLocation> remove = new LinkedList<BoardLocation>();
                    for (BoardLocation l : bl)
                        if (l.equals(a))
                            remove.add(l);

                    for (BoardLocation l : remove)
                        bl.remove(l);
                }

                if (bl.size() == 0)
                    dr.add(d);
            }

            for (Double r : dr)
                as.remove(r);
        }

        // add the new score
        LinkedList<BoardLocation> loc = as.get(score);
        if (loc == null) {
            loc = new LinkedList<BoardLocation>();
            as.put(score, loc);
        }

        loc.add(a);

        //System.out.println("score: " + score + ", action: " + a + ", state:");
        //System.out.println(s.toString());
    }

    /**
     * doStrategy:
     * stimulus = current board state
     * <p>
     * give(stimulus)
     * respond()
     */
    @Override
    public void doStrategy() {
        BoardState stimulus = new BoardState(environment.getBoard());
        //Algorithm.print(stimulus.map);

        give(stimulus);
        response();

    }

    /**
     * Give (stimulus)
     */
    void give(final BoardState stimulus) {
        TreeMap<Double, LinkedList<BoardLocation>> respondents = null;

        previousMoveOffset = new int[]{0, 0};

        int[] a = {-1, -1};    // offset array

        class p {
            public TreeMap<Double, LinkedList<BoardLocation>> r;
            public int[] off;
            public int a;
            public int s;

            public p(TreeMap<Double, LinkedList<BoardLocation>> r, int[] off, int a, int s) {
                this.r = r;
                this.off = off;
                this.a = a;
                this.s = s;
            }
        }

        ArrayList<p> ap = new ArrayList<p>();

		/*
		for (BoardState i : stateTransitionMatrix.keySet())
		{
			// rotate your state four times, 90 degrees each time, to see if it exists
			for (int angle = 0; angle < 360; angle += 90)
			{
				BoardState rot = Rotation.rotateBoard(i, angle);
				a = Algorithm.find(rot.map, stimulus.map);
				if (a[0] != -1 && a[1] != -1)
				{
					if (Othello.DEBUGGING && angle != 0)
					{
						System.out.print("original:\n" + i);
						System.out.print("rotated:\n" + rot);
						System.out.println("offset: " + a[0] + "," + a[1] + " with angle=" + angle);
					}

					respondents = stateTransitionMatrix.get(i);
					previousMoveOffset = a;
					previousAngle = angle;	// we have a state, and an associated angle... all moves will need to be rotated
					previousSize = rot.map.length;

					// add everything to ap that you find in the stm
					ap.add(new p(stateTransitionMatrix.get(i), a, angle, rot.map.length));
				}
			}
		}*/

        LinkedList<BoardLocation> possibleMoves = null;

        // we did not find anything in the STM
		/*
		if (ap.size() == 0)
		{
			respondents = new TreeMap<Double, LinkedList<BoardLocation>>();

			possibleMoves = getMoves();
			LinkedList<BoardLocation> nm = (LinkedList<BoardLocation>) possibleMoves.clone();
			respondents.put(new Double(0), nm);

			stateTransitionMatrix.put(stimulus, respondents);

			if (Othello.DEBUGGING)
			{
				System.out.println("not found in stm. new: ");
				System.out.println(stimulus);
			}

			previousValue = 0.0;
			previousAngle = 0;
			previousSize = Othello.BOARD_SIZE;
		}
		else // something already exists in the stm
		{
			Double highValue = null;
			for (p i : ap)
			{
				if (highValue == null || highValue < i.r.descendingKeySet().first())
				{
					respondents = i.r;
					highValue = i.r.descendingKeySet().first();

					LinkedList<BoardLocation> bl = i.r.get(highValue);

					possibleMoves = (LinkedList<BoardLocation>)(bl.clone());

					previousMoveOffset = i.off;
					previousAngle = i.a;
					previousSize = i.s;
				}
			}

			// our highest value is a negative
			if (highValue < 0)
			{
				LinkedList<BoardLocation> alternate = getMoves();

				blb: for (BoardLocation bl : respondents.get(highValue))
				{
					for (BoardLocation bm : alternate)
					{
						if (bl.equals(bm) == false)
						{
							possibleMoves = new LinkedList<BoardLocation>();
							possibleMoves.add(bm);

							respondents = new TreeMap<Double, LinkedList<BoardLocation>>();
							respondents.put(new Double(0), possibleMoves);

							stateTransitionMatrix.put(stimulus, respondents);

							if (Othello.DEBUGGING)
							{
								System.out.println("new response: " + bm);
								System.out.println(stimulus);
							}

							previousValue = 0.0;
							previousAngle = 0;
							previousSize = Othello.BOARD_SIZE;
							previousMoveOffset = new int []{0, 0};
							break blb;
						}
					}
				}
			}
			else // highest value isn't negative
			{
				previousValue = highValue;
			}
		}
		*/

        BoardState previousState = state;
        BoardLocation previousAction = action;
        state = new BoardState(stimulus);


        possibleMoves = getMoves();
        if (possibleMoves.size() > 0) {

            action = possibleMoves.getFirst();
            double score = Q(state, action);

            if (score < 0)
                System.out.println("score " + score + " for " + action);

            // previousAction = possibleMoves.getFirst(); // just take the first one
            for (BoardLocation i : possibleMoves) {
                double q = Q(state, i);

                if (q != 0.0)
                    System.out.println(q);
                if (q > score) {
                    action = i;
                    score = q;
                }
            }

            if (score != 0)
                System.out.println("Highest Q value: " + score + ", action: " + action);

            // iterate previous
            //Q(previousBoardState, previousAction, sprime)
            if (previousState != null && previousAction != null)
                Q(previousState, previousAction, state, action);
        }

        //System.out.println("unshifted move: " + previousAction);

    }

    /**
     * Response
     */
    void response() {
        playerMove = action;


        int angle = previousAngle;
        if (previousAngle > 0) {
            // things are shifted +... some clockwise
            // angle = 360 - previousAngle;
            // angle = previousAngle;

            angle = previousAngle;

            try {
                //	System.out.println(Rotation.rotateMove(playerMove, 90, previousSize));
                //	System.out.println(Rotation.rotateMove(playerMove, 180, previousSize));
                //	System.out.println(Rotation.rotateMove(playerMove, 270, previousSize));
                //	System.out.println(Rotation.rotateMove(playerMove, 360, previousSize));
                playerMove = Rotation.rotateMove(playerMove, angle, previousSize);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }

            //System.out.println("rotated move: " + playerMove);
        }


        playerMove = new BoardLocation(playerMove.X + previousMoveOffset[0], playerMove.Y + previousMoveOffset[1]);

        if (Othello.DEBUGGING && angle != 0 && previousMoveOffset[0] != 0) {
            System.out.println("original move: " + action);
            System.out.println("original angle: " + previousAngle);
            System.out.println("new angle: " + angle);
            System.out.println("new move: " + playerMove);
            System.out.println("Learning Player moves " + playerMove + ", angle:" + angle);
        }
    }

    Double getCurrentScore() {
        OthelloPlayerType[][] board = environment.getBoard();

        double pieces = 0;
        double opposingpieces = 0;

        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board[i].length; j++)
                switch (board[i][j]) {
                    case WHITE:
                        if (player == OthelloPlayerType.BLACK)
                            opposingpieces++;
                        else
                            pieces++;
                        break;
                    case BLACK:
                        if (player == OthelloPlayerType.WHITE)
                            opposingpieces++;
                        else
                            pieces++;
                        break;
                }


        return pieces - opposingpieces;
    }

    LinkedList<BoardLocation> getMoves() {
        LinkedList<BoardLocation> bl = new LinkedList<BoardLocation>();
        for (BoardLocation move : legalMoves)
            bl.add(move);
        return bl;
    }

    /**
     * Save the STM
     */
    public void save(String fileName) {
        if (true) {
            if (winner) {
                evaluate2(state, action, REWARD_WIN);
            } else {
                evaluate2(state, action, REWARD_LOSE);
            }

            if (fileName != null) {
                FileOutputStream fos = null;
                ObjectOutputStream oos = null;
                try {
                    fos = new FileOutputStream(fileName);
                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(stateTransitionMatrix);
                    oos.close();
                } catch (IOException ioex) {
                    ioex.printStackTrace();
                }
            }

            System.out.println("saved");
        }

        if (Othello.MORE_DEBUGGING) {
            for (BoardState i : stateTransitionMatrix.keySet()) {
                System.out.println("BoardState:");
                System.out.print(i);
                System.out.print("Value: ");

                System.out.print("|");
                for (Double j : stateTransitionMatrix.get(i).keySet()) {
                    System.out.print(j + "|");
                }
                System.out.println();
                System.out.println();
            }

            BoardState stimulus = new BoardState(environment.getBoard());
            System.out.println(stimulus);
            if (stateTransitionMatrix.get(stimulus) != null)
                System.out.println("FOUND");
            else
                System.out.println("NOT FOUND");
        }
    }

    /**
     * Load the STM
     */
    @SuppressWarnings("unchecked")
    public void load(String fileName) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        if (fileName == null) {
            return;
        }
        try {
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);
            stateTransitionMatrix = (HashMap<BoardState, TreeMap<Double, LinkedList<BoardLocation>>>) ois.readObject();
            ois.close();
        } catch (FileNotFoundException fnfex) {

        } catch (IOException ioex) {
            ioex.printStackTrace();
        } catch (ClassNotFoundException cnfex) {
            cnfex.printStackTrace();
        }

        if (Othello.MORE_DEBUGGING)
            for (BoardState i : stateTransitionMatrix.keySet()) {
                System.out.println("BoardState:");
                System.out.print(i);
                System.out.print("Value: ");

                System.out.print("|");
                for (Double j : stateTransitionMatrix.get(i).keySet()) {
                    System.out.print(j + "|");
                }
                System.out.println();
                System.out.println();
            }
    }
}
