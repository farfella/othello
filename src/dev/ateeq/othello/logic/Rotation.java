package dev.ateeq.othello.logic;

import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;

public class Rotation {
    public static BoardState rotateBoard90(BoardState input) {
        BoardState output = new BoardState(input.map.length);
        output.map = new byte[input.map.length][input.map.length];
        for (int i = input.map.length - 1, k = 0; i >= 0; i--, k++) {
            for (int j = 0, l = 0; j <= input.map.length - 1; j++, l++)
                output.map[k][l] = input.map[j][i];
        }

        return output;
    }

    public static BoardState rotateBoard180(BoardState input) {
        BoardState step;
        step = rotateBoard90(input);
        return rotateBoard90(step);
    }

    public static BoardState rotate270(BoardState input) {
        BoardState step;
        step = rotateBoard180(input);
        return rotateBoard90(step);
    }

    public static BoardState rotateBoard(BoardState input, int angle) {
        angle %= 360;
        switch (angle) {
            case 90:
                return rotateBoard90(input);
            case 180:
                return rotateBoard180(input);
            case 270:
                return rotate270(input);
        }

        return input;
    }

    public static BoardLocation rotateMove90(BoardLocation input, int size) {
        BoardLocation y = new BoardLocation(size - input.Y - 1, input.X);
        return y;
    }

    public static BoardLocation rotateMove(BoardLocation input, int angle, int size) throws Exception {
        if (angle < 0 || angle % 90 != 0)
            throw new Exception("Angle is " + angle + ".... must be mod 90 = 0");

        BoardLocation location = new BoardLocation(input.X, input.Y);
        for (; angle > 0; angle -= 90) {
            location = rotateMove90(location, size);
        }

        return location;
    }

}
