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
    Connect4Game() {

        // Create board
        board = new Connect4Board();
        
        // Create players
        player1 = new Connect4Player(board, Connect4Board.Piece.RED, "Human (Red)");
        player2 = new Connect4AiPlayer(board, Connect4Board.Piece.YELLOW, "Computer (Yellow)",11);            

        newGame(); // Go
    }

    // New game
    public void newGame() {
        board.init();
        nextPlayer = player1; // Player1 starts
    }

    // Undo
    public void undo() {
        board.undo();
    }
    
    // Register callbacks for board changes and status text changes
    public void registerBoardUpdateListener( Connect4Board.BoardUpdateListener l ) {
        board.registerBoardUpdateListener(l);
    }
    public void registerStatusUpdateListener( Connect4Board.StatusUpdateListener l ) {
        board.registerStatusUpdateListener(l);
    }

    // Get next player
    public Connect4Player getNextPlayer() {
        Connect4Player p = nextPlayer;
        nextPlayer = (p==player1) ? player2:player1;
        System.out.println("Next player is " + p.name());
        return p;
    }
            
    // Check game is over
    public boolean isOver() {
        return board.isGameOver();
    }

}
