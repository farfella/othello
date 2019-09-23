package dev.ateeq.othello.environment;

import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;

/**
 * Control move execution by the players
 */
public interface EnvironmentMoveControl {

	/**
	 * Performs move to location with corresponding player token
	 * @param loc location to put player token
 	 * @param player player performing move
	 */
	void playerMove(BoardLocation loc, OthelloPlayerType player);

	/**
	 *
	 * @return player whose turn it is now
	 */
	OthelloPlayerType getPlayersTurn();

    //TODO: refactor this; it creates a very leaky abstraction that we don't want!
    OthelloPlayerType[][] getBoard();

}
