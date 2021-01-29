package connect4game;
// The Connect4 player


abstract class Connect4Player {

    final Connect4Board board;
    final Connect4Board.Piece piece;
    final String name;
    
    public Connect4Player(Connect4Board board, Connect4Board.Piece piece, String name) {
        this.board = board;
        this.piece = piece;
        this.name = name;
    }

    String getName() {
        return name;
    }

    Connect4Board.Piece getPiece() {
        return piece;
    }

    abstract boolean isComputer();

    boolean doMove(int col) { return false; }
    boolean doMove() { return false; }
    
    

} // Connect4Player
