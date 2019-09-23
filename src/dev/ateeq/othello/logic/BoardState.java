package dev.ateeq.othello.logic;

import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;
import dev.ateeq.othello.environment.OthelloPlayerType;

import java.io.Serializable;
import java.util.ArrayList;

public class BoardState implements Serializable {
    private static final long serialVersionUID = -6846803326737641965L;

    byte[][] map;

    BoardState(int size) {
        map = new byte[size][size];
    }

    public BoardState(OthelloPlayerType[][] board) {
        map = new byte[board.length][board.length];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                switch (board[i][j]) {
                    case BLACK:
                        map[i][j] = 'B';
                        break;
                    case WHITE:
                        map[i][j] = 'W';
                        break;
                    default:
                        map[i][j] = 0;
                }

            }
        }
    }

    public BoardState(BoardState s) {
        map = new byte[s.map.length][s.map.length];
        for (int i = 0; i < s.map.length; i++)
            for (int j = 0; j < s.map[i].length; j++)
                map[i][j] = s.map[i][j];
    }

    public BoardState(BoardState s, BoardLocation a, OthelloPlayerType player, ArrayList<BoardLocation> flipped) {
        map = new byte[s.map.length][s.map.length];
        for (int i = 0; i < s.map.length; i++)
            for (int j = 0; j < s.map[i].length; j++)
                map[i][j] = s.map[i][j];


        byte p = 'B';
        if (player == OthelloPlayerType.BLACK) {
            p = 'B';
        } else {
            p = 'W';
        }

        map[a.X][a.Y] = p;

        for (BoardLocation i : flipped)
            map[i.X][i.Y] = p;
    }

    public int hashCode() {
        int count = 0;
		for (byte[] bytes : map)
			for (byte aByte : bytes)
				if (aByte == 'B')
					count++;
        return count;
    }

    public boolean equals(Object o) {
        if (o.getClass() == this.getClass()) {
            BoardState bs = (BoardState) (o);
            if (bs.map.length < map.length)
                return false;

            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map.length; j++) {
                    if (bs.map[i][j] != map[i][j])
                        return false;
                }
            }

            return true;
        }
        return false;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                if (map[j][i] == 0)
                    buf.append("0 ");
                else
                    buf.append((char) map[j][i] + " ");
            }
            buf.append("\n");
        }

        return buf.toString();
    }
}
