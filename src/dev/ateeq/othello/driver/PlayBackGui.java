package dev.ateeq.othello.driver;

import dev.ateeq.othello.environment.Othello;
import dev.ateeq.othello.environment.OthelloPlayerType;
import dev.ateeq.othello.environment.SimpleGui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;


/*
 * This example is shamefully stolen from: http://java.sun.com/docs/books/tutorial/uiswing/examples/components/ButtonDemoProject/src/components/ButtonDemo.java
 */
public class PlayBackGui extends JPanel
        implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = 5277571626026322967L;
    protected JButton stop, back, forw, backall, forwall;
    private SimpleGui gui;
    private OthelloPlayerType[][] board = new OthelloPlayerType[Othello.BOARD_SIZE][Othello.BOARD_SIZE];
    private ArrayList<String> moveList;  //our history of move lists
    private int currMove;

    public PlayBackGui() {
        moveList = new ArrayList<String>(0);  //it should be empty at this point

        ImageIcon istop = createImageIcon("resources/stop.JPG");
        ImageIcon ibackall = createImageIcon("resources/backall.JPG");
        ImageIcon iback = createImageIcon("resources/back.JPG");
        ImageIcon iforw = createImageIcon("resources/frw.JPG");
        ImageIcon iforwall = createImageIcon("resources/frwall.JPG");

        backall = new JButton(ibackall);
        backall.setActionCommand("backall");

        back = new JButton(iback);
        back.setActionCommand("back");

        stop = new JButton(istop);
        stop.setActionCommand("stop");

        forw = new JButton(iforw);
        forw.setActionCommand("forw");

        forwall = new JButton(iforwall);
        forwall.setActionCommand("forwall");

        //Listen for actions on all valid buttons
        backall.addActionListener(this);
        back.addActionListener(this);
        stop.addActionListener(this);
        forw.addActionListener(this);
        forwall.addActionListener(this);

        //Add Components to this container, using the default FlowLayout.
        add(backall);
        add(back);
        add(stop);
        add(forw);
        add(forwall);

        for (int i = 0; i < Othello.BOARD_SIZE; i++) {
            for (int j = 0; j < Othello.BOARD_SIZE; j++) {
                board[i][j] = OthelloPlayerType.BLANK;
            }
        }

        //board[Othello.BOARD_SIZE/2-1][Othello.BOARD_SIZE/2-1] = OthelloPlayerType.WHITE;
        //board[Othello.BOARD_SIZE/2-1][Othello.BOARD_SIZE/2] = OthelloPlayerType.BLACK;
        //board[Othello.BOARD_SIZE/2][Othello.BOARD_SIZE/2] = OthelloPlayerType.WHITE;
        //board[Othello.BOARD_SIZE/2][Othello.BOARD_SIZE/2-1] = OthelloPlayerType.BLACK;
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    protected static ImageIcon createImageIcon(String path) {

        ImageIcon ret = new ImageIcon(path);
        return ret;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {

        //Create and set up the window.
        final JFrame frame = new JFrame("Play Back Gui");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        final PlayBackGui newContentPane = new PlayBackGui();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);


        JMenuBar menubar = new JMenuBar();

        JMenuItem newItem = new JMenuItem("Open");
        newItem.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                //the code for this was ripped from: http://java.sun.com/j2se/1.5.0/docs/api/
                //and: http://java.sun.com/docs/books/tutorial/uiswing/components/filechooser.html
                final JFileChooser fc = new JFileChooser("./logs");
                int returnVal = fc.showOpenDialog(frame);

                if (returnVal == JFileChooser.APPROVE_OPTION && fc.getSelectedFile().getName().endsWith(".gamelog")) {
                    System.out.println("filename selected " + fc.getSelectedFile().getName());
                    newContentPane.readFile(fc.getSelectedFile());
                }
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
        frame.setJMenuBar(menubar);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    //moves pieces around to match the configuration corresponding to 'currMove'
    private void processCurrMove() {
        for (int i = 0; i < Othello.BOARD_SIZE; i++) {
            for (int j = 0; j < Othello.BOARD_SIZE; j++) {
                board[i][j] = OthelloPlayerType.BLANK;
            }
        }


        String str = moveList.get(currMove);
        for (int i = 0; i < str.length(); i += 3) {
            int x = str.charAt(i) - '0';
            int y = str.charAt(i + 1) - '0';
            char colour = str.charAt(i + 2);

            if (colour == 'W') {
                board[x][y] = OthelloPlayerType.WHITE;
            } else {
                board[x][y] = OthelloPlayerType.BLACK;
            }
        }

        gui.repaintBoard();
    }

    public void readFile(File file) {
        System.out.println("reading file " + file.getName());

        BufferedReader br = null;
        String str = null;
        try {
            br = new BufferedReader(new FileReader(file));

            //insert first state into board:
            moveList.add("33W34B43B44W");
            currMove = 0;

            while (br.ready()) {
                //this could be the final move
                str = br.readLine();
                if (str.indexOf("wins,") != -1) {
                    break;
                }

                br.readLine();
                str = br.readLine();//we want the 3rd line of every move

                //System.out.println("state of board="+str);
                moveList.add(str);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return; //premature return - dont bother displaying gui
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return; //premature return - dont bother displaying gui
        }


        //after we read file, we can display gui
        gui = new SimpleGui();
        gui.toggleMarkLegalMoves(false);
        gui.setupBoard(board);
    }

    public void actionPerformed(ActionEvent e) {
        if ("backall".equals(e.getActionCommand())) {
            currMove = 0;
            processCurrMove();
        } else if ("back".equals(e.getActionCommand()) && currMove > 0) {
            currMove--;
            processCurrMove();
        } else if ("forw".equals(e.getActionCommand()) && currMove < moveList.size() - 1) {
            currMove++;
            processCurrMove();
        } else if ("forwall".equals(e.getActionCommand()) && currMove < moveList.size() - 1) {
            currMove = moveList.size() - 1;
            processCurrMove();
        }
    }
}