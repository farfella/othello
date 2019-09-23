package dev.ateeq.othello.player;

import dev.ateeq.othello.environment.DecodableMouseEventsBoard;
import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author ateeq
 */
public class GreedyPlayer extends AbstractPlayer {
    protected DecodableMouseEventsBoard gui;

    public GreedyPlayer() {
        setPlayerName("Greedy");
    }

    @Override
    public void doStrategy() {
        int size = 0;
        ArrayList<Integer> bestMoves = new ArrayList<>(3);

        for (int i = 0; i < legalMoves.length; i++) {
            ArrayList<BoardLocation> temp = features.tokensFlipped(player, legalMoves[i]);
            if (temp.size() > size) {
                size = temp.size();
                bestMoves.clear(); // first clear list
            }

            if (temp.size() == size) {
                bestMoves.add(i); // now insert all moves of that length in it
            }
        }

        //pick legal move at random from best-moves
        playerMove = legalMoves[new Random().nextInt(bestMoves.size())];

    }

    /**
     * @see AbstractPlayer#playerTurn()
     */
    @Override
    public boolean playerTurn() {
        boolean canPlay = super.playerTurn();
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
     * Greedy player does not load its strategy from a file.
     *
     * @param fileName unused
     */
    @Override
    public void load(String fileName) {

    }

    /**
     * Greedy player cannot save its strategy to a file.
     *
     * @param fileName unused
     */
    @Override
    public void save(String fileName) {

    }

}
