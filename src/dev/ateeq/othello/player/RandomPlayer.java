package dev.ateeq.othello.player;

import dev.ateeq.othello.environment.DecodableMouseEventsBoard;

import java.util.Random;


/**
 * @author ateeq
 */
public class RandomPlayer extends AbstractPlayer {
    protected DecodableMouseEventsBoard gui;
    private Random random = new Random();

    public RandomPlayer() {
        playerName = "Random";
        //random.setSeed(12345678);
    }

    /**
     * @see AbstractPlayer#doStrategy()
     */
    @Override
    public void doStrategy() {
        int rand = Math.abs(random.nextInt() % legalMoves.length);
        playerMove = legalMoves[rand];
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
        } else {
            environment.playerMove(null, player);
        }

        return canPlay;

    }

    /**
     * @param fileName unused
     */
    @Override
    public void load(String fileName) {

    }

    /**
     * @param fileName unused
     */
    @Override
    public void save(String fileName) {

    }

}
