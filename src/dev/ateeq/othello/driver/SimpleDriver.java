package dev.ateeq.othello.driver;

import dev.ateeq.othello.environment.DynamicGui;
import dev.ateeq.othello.environment.EnvironmentLogicImplementation;
import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;
import dev.ateeq.othello.environment.OthelloPlayerType;
import dev.ateeq.othello.logging.OthelloLogger;
import dev.ateeq.othello.logic.Algorithm;
import dev.ateeq.othello.logic.BoardState;
import dev.ateeq.othello.player.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

//This is the basic refactored SimpleDriver
//It should really have gone in its own scope
public class SimpleDriver {
    public static void test() {
        byte[][] mat = new byte[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                mat[i][j] = 0;

        mat[0][0] = 'W';
        mat[1][1] = 'B';
        mat[2][2] = 'W';

        Algorithm.print(mat);

        byte[][] innie = new byte[8][8];
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                innie[i][j] = 0;

        innie[2][3] = 'W';
        innie[3][4] = 'B';
        innie[4][5] = 'W';

        Algorithm.print(innie);

        int[] rc = Algorithm.find(mat, innie);

        System.out.println("row " + rc[0] + "; col " + rc[1]);
    }


    public static int play(String[] args, HashMap<BoardState, TreeMap<Double, LinkedList<BoardLocation>>> stm, String file, boolean save) {
        //create gui
        DynamicGui gui = new DynamicGui();
        //do we want to mark legal moves???
        gui.toggleMarkLegalMoves(false);

        //create new logic implementation: this one will use the gui
        EnvironmentLogicImplementation env = new EnvironmentLogicImplementation(gui);

        //have the 2 objects share same view of board
        gui.setupBoard(env.getBoard());

        gui.setInit(env);

        //init the system
        env.initialize();

        //setup 1 greedy player, 1 human player to play each other
        //AbstractPlayer player1 = new RandomPlayer();
        AbstractPlayer player1 = new GreedyPlayer();
        //AbstractPlayer player1 = new HumanPlayer("Hume");
        player1.registerEnvironment(env);
        player1.registerFeatureExtractor(env);
        player1.registerGui(gui);
        player1.setOrientation(OthelloPlayerType.WHITE);

        //AbstractPlayer player2 = new HumanPlayer("Human");
        //AbstractPlayer player2 = new RandomPlayer();
        AbstractPlayer player2 = new LearningPlayer(stm);

        if (stm == null)
            player2.load(file);

        player2.registerEnvironment(env);
        player2.registerGui(gui);
        player2.registerFeatureExtractor(env);
        player2.setOrientation(OthelloPlayerType.BLACK);

        //create logging service
        OthelloLogger log = null;

        if (args.length < 1)
            log = new OthelloLogger("logs/test");
        else if (!args[0].equals("nolog"))
            log = new OthelloLogger(args[0]);

        if (log != null) {
            log.LogNewGame(player1.getPlayerName(), player2.getPlayerName(), "game001");

            //register logging service with environment: this is how it'll catch every move
            env.registerGameLogger(log);
        }

        boolean player1CanPlay, player2CanPlay;

        player1CanPlay = player2CanPlay = true;
        while (player1CanPlay || player2CanPlay) {
            if (env.getPlayersTurn() == OthelloPlayerType.WHITE) //player1 goes
            {
                player1CanPlay = player1.playerTurn();
            } else //player2 goes
            {
                player2CanPlay = player2.playerTurn();
            }
        }

        int player1Pieces = env.getNumberOfPieces()[0];
        int player2Pieces = env.getNumberOfPieces()[1];

        int ret = 0;

        if (player1Pieces > player2Pieces) {
//    		System.out.println(player1.getPlayerName() +" wins!");
            ret = 1;
            player2.lose();
            player1.win();
        } else if (player1Pieces < player2Pieces) {
            //   		System.out.println(player2.getPlayerName() +" wins!");
            ret = 2;
            player2.win();
            player1.lose();
        } else {
//    		System.out.println("It's a tie. You both suck. :-/");
            player2.win();
            player1.win();
        }

        //   	System.out.println(player1.getPlayerName() + " " + player1Pieces + "; " + player1.getOrientation());
        //  	System.out.println(player2.getPlayerName() + " " + player2Pieces + "; " + player2.getOrientation());

        //stop logging
        if (log != null) {
            log.LogOutcome(player1Pieces, player2Pieces);
            log.CloseLogs();
        }

        if (save) {
            player1.save("c:\\Work\\learning1.dat");
            player2.save(file);
        } else {
            player1.save(null);
            player2.save(null);
        }

        gui.close();
        return ret;
    }

    private static int startPlayer1VPlayer2Game(AbstractPlayer player1, AbstractPlayer player2) {
        DynamicGui gui = new DynamicGui();

        gui.toggleMarkLegalMoves(true);
        EnvironmentLogicImplementation environment = new EnvironmentLogicImplementation(gui);
        gui.setupBoard(environment.getBoard());
        gui.setInit(environment);
        environment.initialize();

        player1.registerEnvironment(environment);
        player1.registerFeatureExtractor(environment);
        player1.registerGui(gui);
        player1.setOrientation(OthelloPlayerType.WHITE);

        player2.registerEnvironment(environment);
        player2.registerGui(gui);
        player2.registerFeatureExtractor(environment);
        player2.setOrientation(OthelloPlayerType.BLACK);

        boolean player1CanPlay = true;
        boolean player2CanPlay = true;
        while (player1CanPlay || player2CanPlay) {
            if (environment.getPlayersTurn() == OthelloPlayerType.WHITE) //player1 goes
            {
                player1CanPlay = player1.playerTurn();
            } else {
                player2CanPlay = player2.playerTurn();
            }
        }

        int player1Pieces = environment.getNumberOfPieces()[0];
        int player2Pieces = environment.getNumberOfPieces()[1];

        int ret = 0;

        if (player1Pieces > player2Pieces) {
            System.out.println(player1.getPlayerName() + " wins!");
            ret = 1;
            player2.lose();
            player1.win();
        } else if (player1Pieces < player2Pieces) {
            System.out.println(player2.getPlayerName() + " wins!");
            ret = 2;
            player2.win();
            player1.lose();
        } else {
            System.out.println("It's a tie. You both suck. :-/");
            player2.win();
            player1.win();
        }

        System.out.println(player1.getPlayerName() + " " + player1Pieces + "; " + player1.getOrientation());
        System.out.println(player2.getPlayerName() + " " + player2Pieces + "; " + player2.getOrientation());

        gui.close();
        gui = null;
        return ret;
    }

    private static int playGreedyVsHuman() {
    	return startPlayer1VPlayer2Game(new GreedyPlayer(), new HumanPlayer("Human"));
    }

    private static int playRandomVsHuman() {
    	return startPlayer1VPlayer2Game(new RandomPlayer(), new HumanPlayer("Human"));
    }

    public static void main(String[] args) {
        // example ways to play
        //playRandomVsHuman();
        playGreedyVsHuman();
		//startPlayer1VPlayer2Game(new GreedyPlayer(), new GreedyPlayer());

        // if you want to play a game with LearningPlayer, load learned data
        // startPlayer1VPlayer2Game(new HumanPlayer("Human"), new LearningPlayer(LearningPlayer.loadStateTransitionMatrix("C:\\Work\\__learning2_8x8_0.1_0.9000000119209289.dat")));

        //trainPlayer(args);
    }

    public static void trainPlayer(String[] args) {
        // for validating.
        if (false) {
            int ct = 0;
            for (int i = 0; i < 1000; i++) {
                int ret = play(args, null, null, false);
                if (ret == 2)
                    ct++;

            }
            System.out.println(ct);
        }

        if (true) {
            int player1Wins = 0, player2Wins = 0;

            FileWriter fw = null;
            try {
                fw = new FileWriter(new File("c:\\Work\\gstatistics.txt"), true);
            } catch (IOException ioex) {
            }

            for (double df = 0.1; df <= 1.05; df += 0.1f)
            //for (double df = 0.5; df <= 0.5; df += 0.1f)
            {
                LearningPlayer.DISCOUNT_FACTOR = df;
                for (double lr = 0.1; lr <= 1.05; lr += 0.1f)
                //for (double lr = 0.8; lr <= 0.8; lr += 0.1f)
                {
                    LearningPlayer.LEARNING_RATE = lr;

                    HashMap<BoardState, TreeMap<Double, LinkedList<BoardLocation>>> stm = null;
                    //String file = "c:\\learning2_4x4" + df + "_" + lr + ".dat";
                    String file = "c:\\Work\\__learning2_8x8_" + df + "_" + lr + ".dat";
                    //String file = "c:\\Work\\__learning2_4x4_" + df + "_" + lr + ".dat";
                    stm = LearningPlayer.loadStateTransitionMatrix(file);
                    if (stm == null) {
                        stm = new HashMap<BoardState, TreeMap<Double, LinkedList<BoardLocation>>>();
                    }

                    // only save at the last play. You can increase MAX_PLAYS to say 500, which will lead to
                    // better knowledge for LearningPlayer
                    final int MAX_PLAYS = 20;
                    for (int i = 0; i < MAX_PLAYS; i++) {
                        int count = play(args, stm, file, i == MAX_PLAYS-1);
                        if (count == 1)
                            player1Wins++;  // there can be ties
                        else if (count == 2)
                            player2Wins++;

                        System.err.println(i);
                    }

                    System.out.println(player1Wins + "   " + player2Wins);
                    try {
                        fw.write(df + "\t|\t" + lr + "\t|\t");
                        fw.write(player2Wins + "\t|\t" + player1Wins + "\r\n");
                        fw.flush();

                        player2Wins = 0;
                        player1Wins = 0;

                        System.gc();
                    } catch (IOException ioex) {
                    }


                }
            }

            try {
                fw.close();
            } catch (IOException ioex) {
            }
        }
    }
}
