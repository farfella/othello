package dev.ateeq.othello.environment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Defines how we get our extractable features out
 */
public interface ExtractableFeatures {

    /**
     * sets up board
     */
    void initialize();

    /**
     * @return hash representing current valid moves for given player. data looks like: BoardLocation => pieces flipped
     */
    Hashtable<BoardLocation, ArrayList<BoardLocation>> extractValidMoves(OthelloPlayerType player);

    ArrayList<BoardLocation> tokensFlipped(OthelloPlayerType player, BoardLocation loc);

    class BoardLocation implements Cloneable, Comparable, Serializable {
        /**
         *
         */
        private static final long serialVersionUID = 8867481838266126902L;
        public int X;
        public int Y;

        public BoardLocation(int x, int y) {
            X = x;
            Y = y;
        }

        public int hashCode() {
            return Y * Othello.BOARD_SIZE + X;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj.getClass() == this.getClass()) {
                BoardLocation temp = (BoardLocation) obj;
                return temp.X == this.X && temp.Y == this.Y;
            }

            return false;
        }

        @Override
        public Object clone() {
            final BoardLocation clone;
            try {
                clone = (BoardLocation)super.clone();
            } catch (CloneNotSupportedException exception) {
                throw new RuntimeException(exception);
            }

            clone.X = this.X;
            clone.Y = this.Y;
            return clone;
        }

        @Override
        public int compareTo(Object arg0) {
            if (arg0.getClass() == this.getClass()) {
                BoardLocation rhs = (BoardLocation) (arg0);

                return Integer.compare(this.hashCode(), rhs.hashCode());

            } else
                throw new ClassCastException("Cannot convert arg0 to BoardLocation");
        }

        public String toString() {
            return "[x:" + X + ",y:" + Y + "]";
        }

    }

}
