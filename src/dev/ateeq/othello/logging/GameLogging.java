package dev.ateeq.othello.logging;

import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;
import dev.ateeq.othello.environment.OthelloPlayerType;

public interface GameLogging {

    /**
     * Log each move
     */
    void LogNewMove(OthelloPlayerType player, BoardLocation loc);

    /**
     * Log additional statistics about board: number of pieces of each type
     */
    void LogBoardPieces(int numWhite, int numBlack);

    void LogGameBoard(OthelloPlayerType[][] board);
}
