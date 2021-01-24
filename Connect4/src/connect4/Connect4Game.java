package connect4;
// The game logic

final public class Connect4Game {

    // The players
    private final Connect4Player player1;
    private final Connect4Player player2;
    private Connect4Player nextPlayer;

    // The board
    private final Connect4Board board;

      
    // Create a game
    Connect4Game(boolean computer1, boolean computer2) {

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

   
    // Undo
    void undo() {
        if (nextPlayer.isComputer()) return;
        board.undo();
        nextPlayer();
        if (player1.isComputer()||player2.isComputer()) {
          board.undo();
          nextPlayer();
        }
    }
    
    // Register callbacks for board changes and status text changes
    void registerBoardUpdateListener( Connect4Board.BoardUpdateListener l ) {
        board.registerBoardUpdateListener(l);
    }
    void registerStatusUpdateListener( Connect4Board.StatusUpdateListener l ) {
        board.registerStatusUpdateListener(l);
    }

    // Get current player
    Connect4Player getPlayer() {
      return nextPlayer;
    }
    
    void nextPlayer() {
        nextPlayer = (nextPlayer==player1) ? player2:player1;
        System.out.println("Next player is " + nextPlayer.getName());
    }
            
    // Check game is over
    boolean isOver() {
        return board.isGameOver();
    }

}
