package dev.ateeq.othello.player;

import dev.ateeq.othello.environment.DecodableMouseEventsBoard;
import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;
import dev.ateeq.othello.environment.OthelloPlayerType;
import dev.ateeq.othello.feature.QuadrantType;
import dev.ateeq.othello.feature.SimpleFeaturePackager;
import dev.ateeq.othello.logging.STMLogging;
import dev.ateeq.othello.stm.SimpleStateTransitionMatrix;
import dev.ateeq.othello.stm.SimpleStimulant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;

/**
 * @author ateeq
 */
public class SimpleStateTransitionMatrixPlayer extends AbstractPlayer {

    //these need to be set
    public float compensationReward; //positive reward
    public float compensationPenalty; //negative reward
    public boolean doLogging;//do we log data
    protected DecodableMouseEventsBoard gui;
    protected boolean isWaiting; //are we waiting for human to move?
    SimpleStimulant move; //global move set by our player
    private Random rand;
    private QuadrantType lval;
    private BoardLocation loc;
    private SimpleStateTransitionMatrix stm;
    private SimpleFeaturePackager packager;


    public SimpleStateTransitionMatrixPlayer() {
        playerName = "random-stm";
        rand = new Random();

        //logs get registered afterwards
        packager = null;
        stm = null;

        //this is just as a test
        compensationReward = 0.2f;
        compensationPenalty = -0.1f;

        doLogging = false;
    }

    public void registerSTMLogger(STMLogging log) {
        packager = new SimpleFeaturePackager();
        stm = new SimpleStateTransitionMatrix(log, null);

        //if available, i'd like to restore stm from log
        if (doLogging) {
            stm.restoreLog();
        }
    }

    public void doLog(boolean logOut) {
        if (logOut)  //write out stm
        {
            stm.doLog();
        } else  //read in stm
        {
            stm.restoreLog();
        }
    }


    @Override
    public void postGame(int numWhite, int numBlack) {
        //also, we need to update the stm with our compensation
        stm.updateSTM(calcCompensation(numWhite, numBlack));


        //our stm player might want to dump out the stm
        if (doLogging) {
            stm.doLog();
        }
    }

    @Override
    public void load(String fileName) {

    }

    @Override
    public void save(String fileName) {

    }

    @Override
    public void doStrategy() {
        //int rand = //random works better for me
        //this.rand.nextInt(legalMoves.length);
        //playerMove = legalMoves[rand];

        //we need to verify that the given Stimulant corresponds to 1(or more) valid moves
        Hashtable<BoardLocation, ArrayList<BoardLocation>> vmoves = features.extractValidMoves(player);

        //now take our move and translate it to 4 quads and see if it matches
        BoardLocation[] arr = move.translateStimulantToBoardLocations();

        //and check against vmoves if any matches #flipped
        //at least 1 should, if >1, we've got a hashing collision
        //TODO: log hashing collision
        for (int i = 0; i < arr.length; i++) {
            if (vmoves.containsKey(arr[i]) && vmoves.get(arr[i]).size() == move.numFlipped) {
                //thats the one we want
                playerMove = arr[i];
                return;
            }
        }
        System.out.println("Error in SimpleStateTransitionMatrixPlayer: not found matching move");
        System.exit(-1);

    }

    @Override
    public boolean playerTurn() {
        boolean canPlay = super.playerTurn();
        //SimpleStimulant move = null;

        //send inputs to STM
        HashSet<SimpleStimulant> stimulus = packager.processFeatures(features.extractValidMoves(player));


        if (stimulus.size() != 0) {
            move = stm.acceptStimulus(stimulus);
        } else {
            move = null;
        }


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

    //returns compensation value to update the stm with
    //based on final outcome of board
    public float calcCompensation(int numWhite, int numBlack) {
        //our initial stab at compensation:
        //if #pieces of our colour >, return rewardCompensation
        if ((player == OthelloPlayerType.BLACK && numBlack > numWhite) ||
                (player == OthelloPlayerType.WHITE && numWhite > numBlack)) {
            return compensationReward;
        }

        //if equal, return 1/2 of penalty compensation
        //this is to avoid rewarding stalemates
        if (numWhite == numBlack) {
            return compensationPenalty * 0.5f;
        }

        //otherwise, return compensation penalty
        return compensationPenalty;
    }
}
