package dev.ateeq.othello.logic;

import dev.ateeq.othello.environment.ExtractableFeatures.BoardLocation;

import java.util.LinkedList;

public class Algorithm {
    public static void print(byte[][] map) {
		for (byte[] bytes : map) {
			for (byte aByte : bytes) {
				if (aByte == 0)
					System.out.print("0 ");
				else
					System.out.print((char) aByte + " ");
			}
			System.out.println();
		}

        System.out.println("--");
    }

    public static void print(LinkedList<BoardLocation> moves) {
        for (BoardLocation i : moves) {
            System.out.println("move [x,y]: " + i.X + "," + i.Y);
        }
    }

    // find a small matrix in a large matrix
    public static int[] find(byte[][] map, byte[][] in) {
        final int max = in.length;
        final int small = map.length;

        for (int k = 0; k <= max - small; k++) {
            for (int j = 0; j <= max - small; j++) {
                if (memcmp(map[0], in[k], j) == 0) {
                    boolean matched = true;
                    for (int i = 1; i <= small - 1; i++) {
                        if (memcmp(map[i], in[k + i], j) != 0) {
                            matched = false;
                            break;
                        }
                    }

                    if (matched) {
						/* keep in mind, there can be other matches in the board... have to return all instead of breaking here */
                        return new int[]{k, j};
                    }
                }
            }
        }

        return new int[]{-1, -1};
    }

	/**
	 *
	 * @return 0 if match, -1 if not match
	 */
    private static int memcmp(byte[] map, byte[] in, int offset) {
        for (int i = 0; i < map.length; i++)
            if (map[i] != in[i + offset])
                return -1;

        return 0;
    }
}
