package connect4game;
// The game logic

import java.util.Optional;
import java.util.Stack;

import connect4game.Connect4Board.Piece;
import javafx.scene.paint.Color;

final public class Connect4Game {

    // The players
    private final Connect4Player player1;
    private final Connect4Player player2;
    private Connect4Player nextPlayer;

    // The board
    private final Connect4Board board;

    // Game status
    boolean gameOver;

    // Undo stack
    private Stack<Integer> moveStack = new Stack<Integer>(); // Column stack,for move and undo

    public static final int getRows() { return Connect4Board.ROWS; }
    public static final int getCols() { return Connect4Board.COLS; }
    
    // Create a game, a game has a board and two players
    public Connect4Game(boolean computer1, boolean computer2, BoardUpdateListener bl, StatusUpdateListener sl) {

        boardUpdateListener = Optional.of(bl);
        statusUpdateListener = Optional.of(sl);
        gameOver = false;

        // Create a board
        board = new Connect4Board();

        // Create players
        if (computer1) {
            player1 = new Connect4AiPlayer(this, Connect4Board.Piece.RED, "Computer (Red)", 11);
        } else {
            player1 = new Connect4HumanPlayer(Connect4Board.Piece.RED, "Human (Red)");
        }
        if (computer2) {
            player2 = new Connect4AiPlayer(this, Connect4Board.Piece.YELLOW, "Computer (Yellow)", 11);
        } else {
            player2 = new Connect4HumanPlayer(Connect4Board.Piece.YELLOW, "Human (Yellow)");
        }

        nextPlayer = player1; // Player1 starts
        statusUpdate(player1.getName() + " starts");
    }

    // Notify somebody (GUI) on game board changes
    public interface BoardUpdateListener {
        public void Update(Color color, boolean isNew, boolean marker, int column, int row);
    };
    private final Optional<Connect4Game.BoardUpdateListener> boardUpdateListener;
    private void boardUpdate(Piece piece, boolean isNew, boolean marker, int col, int row) {
        boardUpdateListener.ifPresent(l -> l.Update(piece.getColor(), isNew, marker, col, row));
    }

    // Notify somebody (GUI) on game status changes
    public interface StatusUpdateListener {
        public void PrintStatus(String s);
    };
    private final Optional<Connect4Game.StatusUpdateListener> statusUpdateListener;
    void statusUpdate(String s) {
        statusUpdateListener.ifPresent(l -> l.PrintStatus(s));
    }

    // Switch players
    private void nextPlayer() {
        if (!isOver()) {
            nextPlayer = (nextPlayer == player1) ? player2 : player1;
        }
    }

    // Check game is over
    public boolean isOver() {
        return gameOver;
    }

    // Check next player is computer
    public boolean nextIsComputer() {
        return nextPlayer.isComputer();
    }

    // Do a move for next human player
    public boolean humanMove(int col) {
        if (!gameOver) {
            if (doMove(nextPlayer.getPiece(), col)) {
                nextPlayer();
                return true;
            }
        }
        return false;
    }

    // Do a move for next computer player
    public boolean computerMove() {
        if (!gameOver) {
            Optional<Integer> col = nextPlayer.computeMove(board);
            if (col.isPresent()) {
                if (doMove(nextPlayer.getPiece(), col.get())) {
                    nextPlayer();
                    return true;
                }
            }
        }
        return false;
    }

    // Undo two moves
    public void undo() {
        boolean wasOver = gameOver;
        undoMove();
        undoMove();
        if (wasOver)
            nextPlayer();
    }

    // Do a move, check and update game status, push to undo stack
    private boolean doMove(Connect4Board.Piece piece, int col) {
        if (gameOver) return false;
        if (!board.putPiece(col, piece)) return false;
        System.out.println(piece.name() + ":" + col);
        boardUpdate(piece, true, false, col, board.getColPieces(col) - 1);
        moveStack.push(col);
        if (board.gameWon()) {
            statusUpdate(piece.name() + " wins!");
            board.processWinningLine((c, r) -> { boardUpdate(Connect4Board.Piece.EMPTY, false, true, c, r); });
            gameOver = true;
        } else if (board.gameOver()) {
            statusUpdate("Game over!");
            gameOver = true;
        }
        return true;
    }

    // Undo the last move
    private void undoMove() {

        if (board.getTotPieces() > 0) {
            if (gameOver) {
                board.processWinningLine( (col,row) -> { boardUpdate(board.getPiece(col, row), false, false, col, row); } ); // Remove winning line markers
                gameOver = false;
            }
            System.out.println("History: " + moveStack.toString());
            int c = moveStack.pop();
            int r = board.getColPieces(c) - 1;
            System.out.println("Undo: " + board.getPiece(c, r) + ":" + c);
            board.removePiece(c);
            boardUpdate(Connect4Board.Piece.EMPTY, false, false, c, r);
            statusUpdate("");
        }
    }
}
