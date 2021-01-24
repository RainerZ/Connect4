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
    public Connect4Game(boolean computer1, boolean computer2) {

        // Create board
        board = new Connect4Board();
        
        // Create players
        if (computer1) {
            player1 = new Connect4AiPlayer(board, Connect4Board.Piece.RED, "Computer (Red)",11);
        }
        else {
            player1 = new Connect4Player(board, Connect4Board.Piece.RED, "Human (Red)");
        }
        if (computer2) {
            player2 = new Connect4AiPlayer(board, Connect4Board.Piece.YELLOW, "Computer (Yellow)",10);
        }
        else {
            player2 = new Connect4Player(board, Connect4Board.Piece.YELLOW, "Human (Yellow)");
        }

        nextPlayer = player1; // Player1 starts
    }

   
    // Notify somebody (GUI) on game board changes
    public interface BoardUpdateListener {
        public void Update(Color color, boolean isNew, boolean marker, int column, int row);        
    };     
    
    // Notify somebody (GUI) on game status changes
    public interface StatusUpdateListener {
        public void PrintStatus(String s);        
    };   
    
    // Register callbacks for board changes and status changes
    public void registerBoardUpdateListener( BoardUpdateListener l ) {
        board.registerBoardUpdateListener(l);
    }
    public void registerStatusUpdateListener( StatusUpdateListener l ) {
        board.registerStatusUpdateListener(l);
    }

   
    // Switch players
    private void nextPlayer() {
        nextPlayer = (nextPlayer==player1) ? player2:player1;
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
        if (!isOver() && !nextPlayer.isComputer()) {
            if (nextPlayer.doMove(col)) {
                nextPlayer();
                System.out.println("Next player is " + nextPlayer.getName());
                return true;
            }
        }
        return false;
    }

    // Do a move for next computer player
    public boolean computerMove() {
        if (!isOver() && nextPlayer.isComputer()) {
            if (nextPlayer.calcMove()) {
                nextPlayer();
                System.out.println("Next player is " + nextPlayer.getName());
                return true;
            }
        }
        return false;
    }
    
    // Undo one (human vs human) or two (human vs computer) moves 
    public void undo() {
        if (nextPlayer.isComputer()) return;
        board.undo();
        nextPlayer();
        if (player1.isComputer()||player2.isComputer()) {
          board.undo();
          nextPlayer();
        }
    }
    


}
