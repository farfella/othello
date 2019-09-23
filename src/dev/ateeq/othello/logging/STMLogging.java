package dev.ateeq.othello.logging;

import dev.ateeq.othello.stm.SimpleStimulant;

import java.util.ArrayList;
import java.util.HashSet;


public interface STMLogging {
    /**
     * Logs stm out to file
     */
    public void LogSTM(final ArrayList<HashSet<SimpleStimulant>> stm, String fname);

    /**
     * Read log from file
     */
    public ArrayList<HashSet<SimpleStimulant>> ReadSTM(String stmName);
}
