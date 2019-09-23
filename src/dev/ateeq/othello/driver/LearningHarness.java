package dev.ateeq.othello.driver;

import dev.ateeq.othello.environment.EnvironmentLogicImplementation;
import dev.ateeq.othello.environment.OthelloPlayerType;
import dev.ateeq.othello.logging.OthelloLogger;
import dev.ateeq.othello.player.AbstractPlayer;
import dev.ateeq.othello.player.GreedyPlayer;
import dev.ateeq.othello.player.SimpleStateTransitionMatrixPlayer;

//this runs the SimpleDriver code in a loop
public class LearningHarness {


    /**
     * @param args
     */
    //unit-test main: tests out SimpleGUI
    public static void main(String[] args) {
        int numTrials = 2000;
        LearningHarness harness = new LearningHarness();
        OthelloPlayerType winner;
        int numWhite = 0, numBlack = 0, numTie = 0;

        //setup 1 greedy player, 1 human player to play each other
        //AbstractPlayer player1 = new RandomPlayer();
        AbstractPlayer player1 = new GreedyPlayer();
        SimpleStateTransitionMatrixPlayer player2 = new SimpleStateTransitionMatrixPlayer();
        player2.doLogging = true;

        //create logging service
        OthelloLogger log = new OthelloLogger("logs/test");

        //we want to restore the log....
        player2.registerSTMLogger(log);

        //but not write it out after every game
        player2.doLogging = false;


        for (int i = 0; i < numTrials; i++) {
            winner = harness.runTrial(new String[0], i, player1, player2, log);
            if (winner == OthelloPlayerType.BLACK) numBlack++;
            else if (winner == OthelloPlayerType.WHITE) numWhite++;
            else numTie++;
        }

        //dump out player2 stm
        ((SimpleStateTransitionMatrixPlayer) player2).doLog(true);

        System.out.println("Results of trial runs: " + numWhite + "W " + numBlack + "B " + numTie + "T");

        //log.CloseLogs();
    }

    //returns the winning player type
    public OthelloPlayerType runTrial(String args[], int gameID, AbstractPlayer player1, AbstractPlayer player2, OthelloLogger log) {
        //create new logic implementation: this one will not use a gui
        EnvironmentLogicImplementation env = new EnvironmentLogicImplementation(null);

        //have the 2 objects share same view of board
        //gui.setupBoard(env.getBoard());

        //gui.setInit(env);

        //init the system
        env.initialize();

        player1.registerEnvironment(env);
        player1.registerFeatureExtractor(env);
        //player1.registerGui(gui);
        player1.setOrientation(OthelloPlayerType.WHITE);

        player2.registerEnvironment(env);
        //player2.registerGui(gui);
        player2.registerFeatureExtractor(env);
        player2.setOrientation(OthelloPlayerType.BLACK);

        //env.registerGameLogger(log);


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
        OthelloPlayerType winner;

        if (player1Pieces > player2Pieces) {
            System.out.println(player1.getPlayerName() + " wins!");
            winner = player1.getOrientation();
        } else if (player1Pieces < player2Pieces) {
            System.out.println(player2.getPlayerName() + " wins!");
            winner = player2.getOrientation();
        } else {
            System.out.println("It's a tie. You both suck. :-/");
            winner = OthelloPlayerType.BLANK;
        }
        //handle player notifications:
        player1.postGame(player1Pieces, player2Pieces);
        player2.postGame(player1Pieces, player2Pieces);

        System.out.println(player1.getPlayerName() + " " + player1Pieces + "; " + player1.getOrientation());
        System.out.println(player2.getPlayerName() + " " + player2Pieces + "; " + player2.getOrientation());

        return winner;
    }

}