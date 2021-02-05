package connect4game;
// The game logic

import javafx.scene.paint.Color;

final public class Connect4Game {

    // Parameters and constants
    public final static int COLS   = 7;        // Board
    public final static int ROWS   = 6;

    // The players
    private final Connect4Player player1;
    private final Connect4Player player2;
    private Connect4Player nextPlayer;

    // The board
    private final Connect4Board board;

    // Create a game, a game has a board and two players
    public Connect4Game(boolean computer1, boolean computer2, BoardUpdateListener bl, StatusUpdateListener sl ) {
        
        // Create board
        board = new Connect4Board(bl,sl);

        // Create players
        if (computer1) {
            player1 = new Connect4AiPlayer(board, Connect4Board.Piece.RED, "Computer (Red)",11);
        }
        else {
            player1 = new Connect4HumanPlayer(board, Connect4Board.Piece.RED, "Human (Red)");
        }
        if (computer2) {
            player2 = new Connect4AiPlayer(board, Connect4Board.Piece.YELLOW, "Computer (Yellow)",10);
        }
        else {
            player2 = new Connect4HumanPlayer(board, Connect4Board.Piece.YELLOW, "Human (Yellow)");
        }

        nextPlayer = player1; // Player1 starts
        board.statusUpdate(player1.getName()+" starts");
    }

    { System.out.println("new Connect4Game()"); }

    // Notify somebody (GUI) on game board changes
    public interface BoardUpdateListener {
        public void Update(Color color, boolean isNew, boolean marker, int column, int row);        
    };     
    
    // Notify somebody (GUI) on game status changes
    public interface StatusUpdateListener {
        public void PrintStatus(String s);        
    };   
       
    // Switch players
    private void nextPlayer() {
        if (!isOver()) {
            nextPlayer = (nextPlayer==player1) ? player2:player1;
            //board.statusUpdate(nextPlayer.getName());
        }
    }
            
    // Check game is over
    public boolean isOver() {
        return board.isGameOver();
    }
    
    // Check next player is computer
    public boolean nextIsComputer() {
        return nextPlayer.isComputer();
    }
    
    // Do a move for next human player
    public boolean humanMove(int col) {
        if (!isOver()) {
            if (nextPlayer.doMove(col)) {
                nextPlayer();
                return true;
            }
        }
        return false;
    }

    // Do a move for next computer player
    public boolean computerMove() {
        if (!isOver()) {
            if (nextPlayer.doMove()) {
                nextPlayer();
                return true;
            }
        }
        return false;
    }
    
    // Undo two moves 
    public void undo() {
        boolean wasOver = isOver();
        board.undo();
        board.undo();
        if (wasOver) nextPlayer();
    }    


}
