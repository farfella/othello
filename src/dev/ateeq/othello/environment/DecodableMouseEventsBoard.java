package dev.ateeq.othello.environment;

import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * This is a view for the OthelloBoard that is much more limited than MovableOthelloBoard. This is for human GUI players
 * only. It can do two things: Register mouse event handler and decode mouse. Click events into (x,y)-tuple which
 * forms a human players' move on the board.
 */
public interface DecodableMouseEventsBoard {
    void addMouseListenerToBoard(MouseListener listener);

    BoardLocation decodeMouseEventToBoardLocation(MouseEvent event);
}
