package dev.ateeq.othello.stm;

import dev.ateeq.othello.logging.STMLogging;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author ateeq
 */
public class SimpleStateTransitionMatrix {
    //our stm is basically a large arraylist of rows of stimulus
    protected ArrayList<HashSet<SimpleStimulant>> stateTransitionMatrix;
    protected STMLogging stateTransitionMatrixLogger;
    protected String stmName;
    protected ArrayList<HistoricalMove> history;
    public SimpleStateTransitionMatrix(STMLogging log, String name) {
        stateTransitionMatrix = new ArrayList<HashSet<SimpleStimulant>>(10);
        stateTransitionMatrixLogger = log;

        if (name != null) {
            stmName = name;
        } else {
            stmName = "stm_test";
        }

        history = new ArrayList<HistoricalMove>(16);
    }

    public void doLog() {
        stateTransitionMatrixLogger.LogSTM(stateTransitionMatrix, stmName);
    }

    public void restoreLog() {
        //testing: restore log
        stateTransitionMatrix = stateTransitionMatrixLogger.ReadSTM(stmName);
        if (stateTransitionMatrix == null) {
            System.out.println("Failed to restore stm with stm-name=" + stmName);
            stateTransitionMatrix = new ArrayList<HashSet<SimpleStimulant>>(10);
        }
    }

    //input: stimulus
    //output: Stimulant to be acted on
    //note that it will have to be translated to a BoardLocation
    //before it can be used
    //as a stimulant, it is a relative board location
    public SimpleStimulant acceptStimulus(HashSet<SimpleStimulant> stimulus) {
        SimpleStimulant move = null;

        int foundIndex = -1;
        for (int i = 0; i < stateTransitionMatrix.size(); i++) {
            if (stateTransitionMatrix.get(i).equals(stimulus)) {
                foundIndex = i;
                break;
            }
        }

        if (foundIndex != -1) {
            //take the biggest probability move
            System.out.println("Found matching stim=");
            move = findGreatestStimulant(stateTransitionMatrix.get(foundIndex));
        } else {
            //add row to STM
            stateTransitionMatrix.add(stimulus);
            //remember to update foundIndex; it is now last element in the stm
            foundIndex = stateTransitionMatrix.size() - 1;

            move = findGreatestStimulant(stateTransitionMatrix.get(stateTransitionMatrix.size() - 1));
        }

        //record move in our history list
        HistoricalMove hmove = new HistoricalMove();
        hmove.indexInStm = foundIndex;
        hmove.stim = move;
        history.add(hmove);

        System.out.println("Size of stm=" + stateTransitionMatrix.size());

        return move;
    }

    //input: stimulus
    //output: finds biggest SimpleStimulant in list and returns it
    private SimpleStimulant findGreatestStimulant(HashSet<SimpleStimulant> stimulus) {
        float max = -1;
        int maxIndex = -1;

        SimpleStimulant arr[] = stimulus.toArray(new SimpleStimulant[0]);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].calcProbability > max) {
                max = arr[i].calcProbability;
                maxIndex = i;
            }
        }

        return arr[maxIndex];
    }

    //this is how we update the history of the stm
    //we've precalculated the compensation given to us by the enviro
    //and now we need to go through history and update stm fields
    public void updateSTM(float compensation) {
        for (HistoricalMove historicalMove : history) {
            HashSet<SimpleStimulant> row = stateTransitionMatrix.get(historicalMove.indexInStm);
            for (SimpleStimulant simpleStimulant : row) {
                if (simpleStimulant.equals(historicalMove.stim)) {
                    //System.out.println("Found match!");
                    //update the stimulant according to the compensation provided
                    simpleStimulant.calcProbability += compensation;
                    break;
                }
            }
        }
    }

    //we store our history of moves in this structure
    private class HistoricalMove {
        public int indexInStm;  //index into STM
        public SimpleStimulant stim; //particular stimulant that was chosen from that list
    }

}