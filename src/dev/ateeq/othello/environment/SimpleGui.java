package dev.ateeq.othello.environment;

import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * This class demonstrates how to load an Image from an external file
 */
public class SimpleGui extends Component implements MovableOthelloBoard {

    private static final long serialVersionUID = 7489827306258712388L;
    protected JFrame jframe;
    private BufferedImage boardImage; //main board image
    protected OthelloPlayerType[][] board = new OthelloPlayerType[Othello.BOARD_SIZE][Othello.BOARD_SIZE];

    private BufferedImage whiteToken;  //white token to use for drawing
    private BufferedImage blackToken;  //black token to use for drawing
    private BufferedImage legalToken;  //token to mark a currently legal move

    private BoardLocation [] legalMoves;  //cached locations we marked as legal on main board
    private boolean toMarkMoves = false; //do we display marked moves or not

    private ExtractableFeatures init;

    public SimpleGui() {

        legalMoves = null;

        /* Setup board to be blank first */
        for (int i = 0; i < Othello.BOARD_SIZE; i++) {
            for (int j = 0; j < Othello.BOARD_SIZE; j++) {
                board[i][j] = OthelloPlayerType.BLANK;
            }
        }

        try {
            boardImage = ImageIO.read(new File("resources/board.JPG"));
            whiteToken = createNewGameToken(OthelloPlayerType.WHITE);
            blackToken = createNewGameToken(OthelloPlayerType.BLACK);
            legalToken = createNewGameToken(OthelloPlayerType.MARKED);
        } catch (IOException e) {
            e.printStackTrace();
        }

        jframe = new JFrame("Othello");
        jframe.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        {
            JMenuBar menubar = new JMenuBar();

            JMenuItem newItem = new JMenuItem("New");
            newItem.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    init.initialize();
                    repaint();
                }
            });

            JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    System.exit(0);
                }
            });

            JMenu file = new JMenu("File");
            file.add(newItem);
            file.add(exitItem);
            menubar.add(file);
            jframe.setJMenuBar(menubar);
        }


        jframe.add(this);
        jframe.pack();
        jframe.setVisible(true);

    }

    public void setInit(ExtractableFeatures i) {
        init = i;
    }

    public void toggleMarkLegalMoves(boolean toMark) {
        toMarkMoves = toMark;
    }

    public void paint(Graphics g) {
        g.drawImage(boardImage, 0, 0, null);

        //draw tokens where they are on board
        for (int i = 0; i < Othello.BOARD_SIZE; i++) {
            for (int j = 0; j < Othello.BOARD_SIZE; j++) {
                if (board[j][i] == OthelloPlayerType.BLACK) {
                    g.drawImage(blackToken, (j * 50) + 7, (i * 50) + 7, null);
                } else if (board[j][i] == OthelloPlayerType.WHITE) {
                    g.drawImage(whiteToken, (j * 50) + 7, (i * 50) + 7, null);
                } else if (toMarkMoves && board[j][i] == OthelloPlayerType.MARKED) {
                    g.drawImage(legalToken, (j * 50) + 7, (i * 50) + 7, null);
                }
            }
        }
    }

    public Dimension getPreferredSize() {
        if (boardImage == null) {
            return new Dimension(100, 100);
        } else {
            return new Dimension(boardImage.getWidth(null), boardImage.getHeight(null) + jframe.getJMenuBar().getHeight());
        }
    }

    private BufferedImage createNewGameToken(OthelloPlayerType type) throws IOException {
        if (type == OthelloPlayerType.WHITE) {
            return ImageIO.read(new File("resources/white.JPG"));
        } else if (type == OthelloPlayerType.BLACK) {
            return ImageIO.read(new File("resources/black.JPG"));  //load black token
        } else if (type == OthelloPlayerType.MARKED) {
            return ImageIO.read(new File("resources/marked.JPG"));  //load black token
        }
        throw new IOException();  /* this should not occur */
    }


    @Override
    public void placeToken(int x, int y, OthelloPlayerType color) {
        board[x][y] = color;
    }

    @Override
    public int getDimX() {
        return Othello.BOARD_SIZE;
    }

    @Override
    public int getDimY() {
        return Othello.BOARD_SIZE;
    }

    @Override
    public void setupBoard(OthelloPlayerType[][] arr) {
        board = arr;
    }

    @Override
    public void markLegalMoves(OthelloPlayerType color, BoardLocation [] moves) {
        //first update gui with new players' turn
        jframe.setTitle(color.toString() + " player's move");

        //then, if doing marking, process board
        if (!toMarkMoves) return;

        legalMoves = moves;
        for (BoardLocation loc : legalMoves) {
            board[loc.X][loc.Y] = OthelloPlayerType.MARKED;
        }

    }

    @Override
    public void clearAllLegalMoves() {
        if (legalMoves == null) return;

        for (BoardLocation loc : legalMoves) {
            board[loc.X][loc.Y] = OthelloPlayerType.BLANK;
        }
    }

    @Override
    public void repaintBoard() {
        repaint();
    }

    public void close() {
        jframe.dispose();
    }
}
