package dev.ateeq.othello.environment;

import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;

/**
 * With this interface we tell the GUI to place new tokens on board and to flip existing tokens
 */
public interface MovableOthelloBoard {
	/**
	 *
	 * @return x-dim of board
	 */
    int getDimX();

	/**
	 *
	 * @return y-dim of board
	 */
    int getDimY();

	/**
	 * puts token at given place on board
	 *
	 */
    void placeToken(int x, int y, OthelloPlayerType color);

	/**
	 * 	shortcut to setup board from history
	 */
    void setupBoard(OthelloPlayerType [][] arr);

	/**
	 * Marks legal board positions for given color and updates status of gui with current players' turn
	 */
    void markLegalMoves(OthelloPlayerType color, BoardLocation [] legalMoves);

	/**
	 * Clears previously set legal moves
	 */
	void clearAllLegalMoves();

	/**
	 * Calls repaint on components
	 */
	void repaintBoard();
}
