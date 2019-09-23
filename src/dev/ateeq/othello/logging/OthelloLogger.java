package dev.ateeq.othello.logging;

import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;
import dev.ateeq.othello.environment.Othello;
import dev.ateeq.othello.environment.OthelloPlayerType;
import dev.ateeq.othello.stm.SimpleStimulant;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 * This class provides the logging facilities for recording/playing back games of othello, is responsible for
 * serializing/deserializing the stm and various other thing.s
 */
public class OthelloLogger implements GameLogging, STMLogging {
    private String CurrWorkingPath;  //where we log things
    private File gameLog;  //log of our current game: our moves go there
    private FileOutputStream gameOutput; //how we write to gameLog

    public OthelloLogger(String folder) {
        CurrWorkingPath = folder;

        /* verify that folder exists: if not create it */
        File file = new File(CurrWorkingPath);
        if (!file.isDirectory()) {
            if (!file.mkdirs()) {
                System.out.println("Invalid file path selected: " + CurrWorkingPath + ": path is not a folder");
                System.exit(-1);
            }
        }
    }


    @Override
    public ArrayList<HashSet<SimpleStimulant>> ReadSTM(String stmName) {

        File stmLog = new File(CurrWorkingPath + "/" + stmName + ".stm");
        BufferedReader stmRead;

        //this is what we'll be building up dynamically from a file
        ArrayList<HashSet<SimpleStimulant>> stm = new ArrayList<>(5);

        try {
            stmRead = new BufferedReader(new FileReader(stmLog));

            while (stmRead.ready()) {
                /* create a new Hashset for insertion into stm */
                HashSet<SimpleStimulant> hash = new HashSet<>(5);

                String line = stmRead.readLine();
                while (!line.equals("")) {
                    /* create a new Stimulant to insert into just-created Hashset */
                    SimpleStimulant stim = new SimpleStimulant();

                    String[] str = line.split(" ");
                    stim.distFromCenter.X = Integer.parseInt(str[0]);
                    stim.distFromCenter.Y = Integer.parseInt(str[1]);
                    stim.numFlipped = Integer.parseInt(str[2]);
                    stim.calcProbability = Float.parseFloat(str[3]);

                    hash.add(stim);

                    line = stmRead.readLine();
                }

                /* We just read a newline: read another one (two newlines seperate each hash record) */
                line = stmRead.readLine();

                /* and add hashset to our stimulus */
                stm.add(hash);
            }

        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            System.out.println("Failed to restore log...");
            return null;
        } catch (IOException e) {
            System.out.println("Failed to restore log..." + e.getMessage());
            return null;
        }

        System.out.println("Size of stm=" + stm.size());
        return stm;
    }


    @Override
    public void LogSTM(ArrayList<HashSet<SimpleStimulant>> stm, String fileName) {
        String log;
        if (fileName == null) {
            log = gameLog.getName().replace(".gamelog", ".stm");
        } else {
            log = fileName + ".stm";
        }
        File stmLog = new File(CurrWorkingPath + "/" + log);
        FileOutputStream stmStream;

        try {
            stmStream = new FileOutputStream(stmLog);

            /* Format of stm is simple: each stimulant is printed out on its own line
               each hashset is seperated by 2 newlines
            */
            for (HashSet<SimpleStimulant> simpleStimulants : stm) {

                SimpleStimulant[] keys = simpleStimulants.toArray(new SimpleStimulant[0]);
                for (SimpleStimulant key : keys) {
                    stmStream.write(("" + key.distFromCenter.X + " " +
                            key.distFromCenter.Y + " " + key.numFlipped + " " +
                            key.calcProbability + "\n").getBytes());
                }
                stmStream.write("\n\n".getBytes());
            }

            System.out.println("Size of stm=" + stm.size());

            stmStream.flush();
            stmStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * create log that corresponds to a new game
     */
    public void LogNewGame(String player1Name, String player2Name, String gameID) {
        String log = new SimpleDateFormat("yyyy MMMd K-mm-s ").format(new Date()) + player1Name + "_vs_" + player2Name + " " + gameID + ".gamelog";
        gameLog = new File(CurrWorkingPath + "/" + log);

        try {
            gameOutput = new FileOutputStream(gameLog);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Create a new entry in log moves file in the case of when the player is passing their turn, loc=null
     */
    @Override
    public void LogNewMove(OthelloPlayerType player, BoardLocation loc) {
        try {
            if (loc != null) {
                gameOutput.write((player.toString() + " " + loc.X + "," + loc.Y + "\n").getBytes());
            } else {
                gameOutput.write((player.toString() + " " + "PASS\n").getBytes());
            }

            gameOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void LogBoardPieces(int numWhite, int numBlack) {
        try {
            gameOutput.write(("white=" + numWhite + " black=" + numBlack + "\n").getBytes());
            gameOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs final state of game at finish. Winner is whoever has more pieces
     */
    public void LogOutcome(int numWhite, int numBlack) {
        try {
            if (numWhite > numBlack)
                gameOutput.write((OthelloPlayerType.WHITE.toString() + " wins, " + numWhite + ":" + numBlack + "\n").getBytes());
            else if (numWhite < numBlack)
                gameOutput.write((OthelloPlayerType.BLACK.toString() + " wins, " + numWhite + ":" + numBlack + "\n").getBytes());
            else gameOutput.write(("Nobody wins, " + numWhite + ":" + numBlack + "\n").getBytes());

            gameOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //close all open logs: mainly STM logging, game logging
    public void CloseLogs() {
        try {
            gameOutput.flush();
            gameOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * We want to log the board because it will make traversing the history much easier.
     */
    @Override
    public void LogGameBoard(OthelloPlayerType[][] board) {
        try {
            for (int i = 0; i < Othello.BOARD_SIZE; i++) {
                for (int j = 0; j < Othello.BOARD_SIZE; j++) {
                    if (board[i][j] == OthelloPlayerType.BLACK) {
                        gameOutput.write((i + "" + j + "B").getBytes());
                    } else if (board[i][j] == OthelloPlayerType.WHITE) {
                        gameOutput.write((i + "" + j + "W").getBytes());
                    }
                }
            }
            gameOutput.write("\n".getBytes());
            gameOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
