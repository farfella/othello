package dev.ateeq.othello.environment;

import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * Adds event listeners for mouse clicks. You can add/remove pieces from the board.
 */
public class DynamicGui extends SimpleGui implements DecodableMouseEventsBoard {

    private static final long serialVersionUID = 1695542369870942950L;

    public DynamicGui() {
        //we basically want same behavior.....
        super();

        //but we also implement DecodableMouseEventsBoard for human gui players

		/*
		//buuut, we also want to capture mouse clicks
		f.addMouseListener(  new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				// Auto-generated method stub
				System.out.println(e.getX() + ":" + e.getY());
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// Auto-generated method stub

			}
			@Override
			public void mouseExited(MouseEvent e) {
				// Auto-generated method stub

			}
			@Override
			public void mousePressed(MouseEvent e) {
				// Auto-generated method stub

			}
			@Override
			public void mouseReleased(MouseEvent e) {
				// Auto-generated method stub

			}
		}
		);
		*/
    }

    @Override
    public void addMouseListenerToBoard(MouseListener listener) {
        jframe.addMouseListener(listener);

    }

    @Override
    public BoardLocation decodeMouseEventToBoardLocation(MouseEvent event) {
        // @TODO
        int x = event.getX() - 8;   // we seem to be off by this much in the gui i've constructed, dont know why
        int y = event.getY() - 58;  // we seem to be off by this much in the gui i've constructed, dont know why
        BoardLocation loc = new BoardLocation(x / 50, y / 50);

        System.out.println("x,y " + event.getX() + "," + event.getY());

        return loc;
    }

}
