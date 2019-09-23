package dev.ateeq.othello.player;

import dev.ateeq.othello.environment.DecodableMouseEventsBoard;
import dev.ateeq.othello.environment.Othello;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class HumanPlayer extends AbstractPlayer {
    protected DecodableMouseEventsBoard gui;

    @SuppressWarnings("WeakerAccess")
    protected boolean isWaiting; // Are we waiting for human player to move?

    public HumanPlayer(String name) {
        super();
        isWaiting = false;
        playerName = name;
    }

    /**
     * @param gui users' view of gui: for decoding keystrokes into positions
     */
    public void registerGui(DecodableMouseEventsBoard gui) {
        this.gui = gui;
        this.gui.addMouseListenerToBoard(new PlayerMouseListener());
    }

    /**
     * Human player does not load its strategy from a file.
     *
     * @param fileName unused
     */
    @Override
    public void load(String fileName) {

    }

    /**
     * Human player cannot save his strategy to a file.
     *
     * @param fileName unused
     */
    @Override
    public void save(String fileName) {

    }

    /**
     * @return true is player is able to move
     */
    @Override
    public boolean playerTurn() {
        //for our players turn, we call out to env to get a list of moves
        //and call doStrategy to process that list to come up with a single move
        //then, call env to make that move
        //and finally yield back control

        boolean canPlay = super.playerTurn();
        if (canPlay) {

            //toggle flag to capture keystrokes starting.....now!
            isWaiting = true;
            while (isWaiting) {
                try {
                    Thread.sleep(Othello.NON_BUSY_WAIT);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }

            // When we break out of loop, we expect "playerMove" to have been set to a valid move by the MouseListener
            // So, we just accept what the user gave us and continue.
            doStrategy();

            environment.playerMove(playerMove, player);
        } else
            environment.playerMove(null, player);

        return canPlay;

    }


    /**
     * No implementation, since the user chooses plays
     */
    @Override
    public void doStrategy() {
    }

    public class PlayerMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            // ignore clicks when its not your turn
            if (environment.getPlayersTurn() != player && !isWaiting) return;

            playerMove = gui.decodeMouseEventToBoardLocation(e);

            for (int i = 0; i < legalMoves.length; i++) {
                if (legalMoves[i].X == playerMove.X && legalMoves[i].Y == playerMove.Y) {
                    isWaiting = false;
                }
            }


            System.out.println(playerMove.X + "," + playerMove.Y);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }
    }


}
