package dev.ateeq.othello.environment;

import dev.ateeq.othello.logging.GameLogging;

import java.util.ArrayList;
import java.util.Hashtable;

public class EnvironmentLogicImplementation implements ExtractableFeatures, EnvironmentMoveControl {
    /* Our view of the board environment */
    protected OthelloPlayerType[][] board = new OthelloPlayerType[Othello.BOARD_SIZE][Othello.BOARD_SIZE];
    protected MovableOthelloBoard environment; /* This is how we manipulate board gui */
    private Hashtable<BoardLocation, ArrayList<BoardLocation>> cachedMoves = null;
    private OthelloPlayerType playerTurn;
    private GameLogging logger;

    /* Our view of running total of pieces: we should keep track of this */
    private int numBlack;
    private int numWhite;

    /**
     * @param env environment
     */
    public EnvironmentLogicImplementation(MovableOthelloBoard env) {
        environment = env;
        cachedMoves = null;
        logger = null;
    }

    public EnvironmentLogicImplementation() {
        environment = null;
        cachedMoves = null;
        logger = null;
    }

    /**
     * @param log logging facility
     */
    public void registerGameLogger(GameLogging log) {
        logger = log;
    }

    @Override
    public void initialize() {
        /* Setup board to be BLANK first */
        for (int i = 0; i < Othello.BOARD_SIZE; i++) {
            for (int j = 0; j < Othello.BOARD_SIZE; j++) {
                board[i][j] = OthelloPlayerType.BLANK;
            }
        }

        board[Othello.BOARD_SIZE / 2 - 1][Othello.BOARD_SIZE / 2 - 1] = OthelloPlayerType.WHITE;
        board[Othello.BOARD_SIZE / 2 - 1][Othello.BOARD_SIZE / 2] = OthelloPlayerType.BLACK;
        board[Othello.BOARD_SIZE / 2][Othello.BOARD_SIZE / 2] = OthelloPlayerType.WHITE;
        board[Othello.BOARD_SIZE / 2][Othello.BOARD_SIZE / 2 - 1] = OthelloPlayerType.BLACK;

        /* Setup running totals of pieces on board */
        numBlack = 2;
        numWhite = 2;

        /* Black player always starts first */
        playerTurn = OthelloPlayerType.BLACK;
    }

    /**
     * @param player player type
     * @return hash representing current valid moves for given player. Data looks like: BoardLocation => pieces flipped
     */
    @Override
    public Hashtable<BoardLocation, ArrayList<BoardLocation>> extractValidMoves(OthelloPlayerType player) {
        /* check our cache if it contains anything */
        if (player == playerTurn && cachedMoves != null) {
            return cachedMoves;
        }

        Hashtable<BoardLocation, ArrayList<BoardLocation>> validMoves = new Hashtable<>();

        if (player != OthelloPlayerType.BLACK && player != OthelloPlayerType.WHITE) {
            System.out.println("Error: no moves available for <Blank> player");
            System.exit(-1);
        }

        /* Find all possible adjacent empty spots on board */
        ArrayList<BoardLocation> moves = extractAllEmptyPositions();
        /* Figure out which of those represent legal moves */
        for (BoardLocation move : moves) {
            ArrayList<BoardLocation> temp = tokensFlipped(player, move);
            if (temp.size() > 0) {
                validMoves.put(move, temp);
            }
        }

        /* Cache precalculated moves here: we can reuse it to validate player moves */
        cachedMoves = validMoves;

        /* Also, display legal moves on gui, if available */
        if (environment != null) {

            environment.markLegalMoves(getPlayersTurn(), cachedMoves.keySet().toArray(new BoardLocation[0]));
            environment.repaintBoard();
        }

        return validMoves;
    }

    /**
     *
     * @param player
     * @param loc
     * @return array of BoardLocations that should be flipped to make that move for the given player if illegal move,
     * that value is less-than/equal to 0 magnitude of array is how many tokens get flipped for that move
     */
    public ArrayList<BoardLocation> tokensFlipped(OthelloPlayerType player, BoardLocation loc) {
        int sum = 0;
        int tSum = 0;
        int x, y;
        OthelloPlayerType opp;
        ArrayList<BoardLocation> ret = new ArrayList<BoardLocation>();


        if (player == OthelloPlayerType.BLACK) {
            opp = OthelloPlayerType.WHITE;
        } else {
            opp = OthelloPlayerType.BLACK;
        }

        /* Check vertical moves */
        x = loc.X;
        y = loc.Y - 1;

        while (y > -1 && board[x][y] == opp) {
            y--;
            tSum++;
        }

        if (y > -1 && board[x][y] == player && tSum > 0) {
            sum += tSum;
            for (int i = 1; i <= tSum; i++) {
                ret.add(new BoardLocation(x, y + i));
            }
        }

        tSum = 0;
        y = loc.Y + 1;
        while (y < Othello.BOARD_SIZE && board[x][y] == opp) {
            y++;
            tSum++;
        }
        if (y < Othello.BOARD_SIZE && board[x][y] == player && tSum > 0) {
            sum += tSum;
            for (int i = 1; i <= tSum; i++) {
                ret.add(new BoardLocation(x, y - i));
            }
        }

        /* check horizontal moves */
        tSum = 0;
        x = loc.X - 1;
        y = loc.Y;
        while (x > -1 && board[x][y] == opp) {
            x--;
            tSum++;
        }
        if (x > -1 && board[x][y] == player && tSum > 0) {
            sum += tSum;
            for (int i = 1; i <= tSum; i++) {
                ret.add(new BoardLocation(x + i, y));
            }
        }

        tSum = 0;
        x = loc.X + 1;
        while (x < Othello.BOARD_SIZE && board[x][y] == opp) {
            x++;
            tSum++;
        }
        if (x < Othello.BOARD_SIZE && board[x][y] == player && tSum > 0) {
            sum += tSum;
            for (int i = 1; i <= tSum; i++) {
                ret.add(new BoardLocation(x - i, y));
            }
        }

        /* Check diagonal moves: right-to-left and down */
        tSum = 0;
        x = loc.X - 1;
        y = loc.Y + 1;
        while (x > -1 && y < Othello.BOARD_SIZE && board[x][y] == opp) {
            x--;
            y++;
            tSum++;
        }
        if (x > -1 && y < Othello.BOARD_SIZE && board[x][y] == player && tSum > 0) {
            sum += tSum;
            for (int i = 1; i <= tSum; i++) {
                ret.add(new BoardLocation(x + i, y - i));
            }
        }

        /* Check diagonal moves: right-to-left and up */
        tSum = 0;
        x = loc.X + 1;
        y = loc.Y - 1;
        while (x < Othello.BOARD_SIZE && y > -1 && board[x][y] == opp) {
            x++;
            y--;
            tSum++;
        }
        if (y > -1 && x < Othello.BOARD_SIZE && board[x][y] == player && tSum > 0) {
            sum += tSum;
            for (int i = 1; i <= tSum; i++) {
                ret.add(new BoardLocation(x - i, y + i));
            }
        }

        /* Check diagonal moves: left-to-right and down */
        tSum = 0;
        x = loc.X + 1;
        y = loc.Y + 1;
        while (x < Othello.BOARD_SIZE && y < Othello.BOARD_SIZE && board[x][y] == opp) {
            x++;
            y++;
            tSum++;
        }
        if (x < Othello.BOARD_SIZE && y < Othello.BOARD_SIZE && board[x][y] == player && tSum > 0) {
            sum += tSum;
            for (int i = 1; i <= tSum; i++) {
                ret.add(new BoardLocation(x - i, y - i));
            }
        }

        /* Check diagonal moves: left-to-right and up */
        tSum = 0;
        x = loc.X - 1;
        y = loc.Y - 1;
        while (x > -1 && y > -1 && board[x][y] == opp) {
            x--;
            y--;
            tSum++;
        }

        if (x > -1 && y > -1 && board[x][y] == player && tSum > 0) {
            sum += tSum;
            for (int i = 1; i <= tSum; i++) {
                ret.add(new BoardLocation(x + i, y + i));
            }
        }


        return ret;
    }


    /**
     * @return list of all the valid empty board positions that are adjacent to any game pieces already on the board
     */
    private ArrayList<BoardLocation> extractAllEmptyPositions() {
        ArrayList<BoardLocation> adjacents = new ArrayList<BoardLocation>();
        OthelloPlayerType[][] temp = new OthelloPlayerType[Othello.BOARD_SIZE][Othello.BOARD_SIZE];
        for (int i = 0; i < Othello.BOARD_SIZE; i++) {
            for (int j = 0; j < Othello.BOARD_SIZE; j++) {
                temp[i][j] = board[i][j];
            }
        }

        for (int i = 0; i < Othello.BOARD_SIZE; i++) {
            for (int j = 0; j < Othello.BOARD_SIZE; j++) {
                if (board[i][j] != OthelloPlayerType.BLANK && board[i][j] != OthelloPlayerType.MARKED) {
                    extractAdjacentEmptyPositions(new BoardLocation(i, j), temp, adjacents);
                }
            }
        }
        return adjacents;
    }

    /**
     * Extracts all empty positions around location=loc and places them in ts
     *
     * @param location
     * @param temp
     * @param ts
     */
    private void extractAdjacentEmptyPositions(BoardLocation location, OthelloPlayerType[][] temp, ArrayList<BoardLocation> ts) {
        /* first line */
        try {
            if (temp[location.X - 1][location.Y - 1] == OthelloPlayerType.BLANK)
                ts.add(new BoardLocation(location.X - 1, location.Y - 1));
            temp[location.X - 1][location.Y - 1] = OthelloPlayerType.MARKED;
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        try {
            if (temp[location.X][location.Y - 1] == OthelloPlayerType.BLANK)
                ts.add(new BoardLocation(location.X, location.Y - 1));
            temp[location.X][location.Y - 1] = OthelloPlayerType.MARKED;
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        try {
            if (temp[location.X + 1][location.Y - 1] == OthelloPlayerType.BLANK)
                ts.add(new BoardLocation(location.X + 1, location.Y - 1));
            temp[location.X + 1][location.Y - 1] = OthelloPlayerType.MARKED;
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        /* second line */
        try {
            if (temp[location.X - 1][location.Y] == OthelloPlayerType.BLANK)
                ts.add(new BoardLocation(location.X - 1, location.Y));
            temp[location.X - 1][location.Y] = OthelloPlayerType.MARKED;
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        try {
            if (temp[location.X][location.Y] == OthelloPlayerType.BLANK)
                ts.add(new BoardLocation(location.X, location.Y));
            temp[location.X][location.Y] = OthelloPlayerType.MARKED;
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        try {
            if (temp[location.X + 1][location.Y] == OthelloPlayerType.BLANK)
                ts.add(new BoardLocation(location.X + 1, location.Y));
            temp[location.X + 1][location.Y] = OthelloPlayerType.MARKED;
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        /* third line */
        try {
            if (temp[location.X - 1][location.Y + 1] == OthelloPlayerType.BLANK)
                ts.add(new BoardLocation(location.X - 1, location.Y + 1));
            temp[location.X - 1][location.Y + 1] = OthelloPlayerType.MARKED;
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        try {
            if (temp[location.X][location.Y + 1] == OthelloPlayerType.BLANK)
                ts.add(new BoardLocation(location.X, location.Y + 1));
            temp[location.X][location.Y + 1] = OthelloPlayerType.MARKED;
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        try {
            if (temp[location.X + 1][location.Y + 1] == OthelloPlayerType.BLANK)
                ts.add(new BoardLocation(location.X + 1, location.Y + 1));
            temp[location.X + 1][location.Y + 1] = OthelloPlayerType.MARKED;
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
    }

    @Override
    public OthelloPlayerType getPlayersTurn() {
        return playerTurn;
    }

//	@Override
//	public void playerPass(OthelloPlayerType player)
//	{
//		if ( playerTurn != player) //sanity check player-turn
//		{
//			System.out.println("Error: invalid player turn " + player.toString());
//			System.exit(-1);
//		}
//		
//		//log event
//		if ( logger != null ) 
//		{
//			logger.LogNewMove(player, null);
//		}
//		
//		playerTurn = player == OthelloPlayerType.WHITE ? OthelloPlayerType.BLACK : OthelloPlayerType.WHITE;
//		cachedMoves = null;
//	}


    @Override
    public void playerMove(BoardLocation location, OthelloPlayerType player) {
        if (playerTurn != player) /* Sanity check player-turn */ {
            System.out.println("Error: invalid player turn " + player.toString());
            System.exit(-1);
        }

        if (logger != null) {
            logger.LogNewMove(player, location);
        }

        /* The player is actually passing: no valid moves found */
        if (location == null) {
            playerTurn = player == OthelloPlayerType.WHITE ? OthelloPlayerType.BLACK : OthelloPlayerType.WHITE;
            cachedMoves = null;

            if (logger != null) {
                logger.LogBoardPieces(numWhite, numBlack);
                logger.LogGameBoard(board);
            }
            return;
        }


        /* Sanity check */
        boolean isMatch = cachedMoves.containsKey(location);
        if (!isMatch) {
            System.out.println("Error: invalid player move chosen: " + location);
        }

        /* First, we clear all the "marked" moves on the gui-board if available */
        if (environment != null) environment.clearAllLegalMoves();

        /* Next we update board */
        board[location.X][location.Y] = player;

        ArrayList<BoardLocation> flipped = cachedMoves.get(location);

        for (BoardLocation flip : flipped) {
            board[flip.X][flip.Y] = player;
        }

        /* We update the number of pieces on the board */
        if (player == OthelloPlayerType.WHITE) {
            numWhite += flipped.size() + 1;
            numBlack -= flipped.size();
        } else {
            numBlack += flipped.size() + 1;
            numWhite -= flipped.size();
        }

        if (logger != null) /* Log event */ {
            logger.LogBoardPieces(numWhite, numBlack);
            logger.LogGameBoard(board);
        }

        cachedMoves = null; /* For a new move, we invalidate all saved moves calculated previously */

        /* Also, the turn now goes to the other player */
        playerTurn = player == OthelloPlayerType.WHITE ? OthelloPlayerType.BLACK : OthelloPlayerType.WHITE;
    }

    /**
     * @return the board
     */
    public OthelloPlayerType[][] getBoard() {
        return board;
    }

    /**
     * @param board the board to set
     */
    public void setBoard(OthelloPlayerType[][] board) {
        this.board = board;
    }

    /**
     * @return getters for numWhite,numBlack
     */
    public int[] getNumberOfPieces() {
        return new int[]{numWhite, numBlack};
    }

}
