package dev.ateeq.othello.player;

import dev.ateeq.othello.environment.*;
import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;

/**
 * Base class for all player types
 */
public abstract class AbstractPlayer {
    protected EnvironmentMoveControl environment;
    protected OthelloPlayerType player;
    protected BoardLocation playerMove;

    @SuppressWarnings("WeakerAccess")
    protected ExtractableFeatures features;

    @SuppressWarnings("WeakerAccess")
    protected BoardLocation[] legalMoves;

    @SuppressWarnings("WeakerAccess")
    protected String playerName;

    @SuppressWarnings("WeakerAccess")
    protected boolean winner = false;

    /**
     * Encapsulates strategy pattern: all move calculations should be done here
     */
    public abstract void doStrategy();

    /**
     * Callback to let player know its time for his move
     *
     * @return true if player has legal moves available
     */
    public boolean playerTurn() {
        try {
            Thread.sleep(Othello.WAIT_PER_MOVE);
        } catch (InterruptedException iex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(iex);
        }

        legalMoves = features.extractValidMoves(player).keySet().toArray(new BoardLocation[0]);

        //sanity-check condition: also, end-of-game check
        if (legalMoves.length < 1 && Othello.DEBUGGING) {
            System.out.println(player.toString() + " has no valid moves... must pass.");
        }

        return legalMoves.length > 0;
    }

    /**
     * @param features reference to feature extraction
     */
    public void registerFeatureExtractor(ExtractableFeatures features) {
        this.features = features;
    }

    /**
     * @param environment reference to environment
     */
    public void registerEnvironment(EnvironmentMoveControl environment) {
        this.environment = environment;
    }

    /**
     * @return whether player is black or white
     */
    public OthelloPlayerType getOrientation() {
        return this.player;
    }

    /**
     * @param player set whether player is black or white
     */
    public void setOrientation(OthelloPlayerType player) {
        this.player = player;
    }

    /**
     * @param gui unused
     */
    public void registerGui(DecodableMouseEventsBoard gui) {

    }

    /**
     * @return player's name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @param name Player's name
     */
    @SuppressWarnings("unused")
    public void setPlayerName(String name) {
        playerName = name;
    }

    /**
     * this is for hooks for feedback to players
     *
     * @param numWhite unused
     * @param numBlack unused
     */
    public void postGame(int numWhite, int numBlack) {
    }

    /**
     * Mark player has won
     */
    public void win() {
        winner = true;
    }

    public void lose() {

    }

    abstract public void load(String fileName);

    abstract public void save(String fileName);

}
