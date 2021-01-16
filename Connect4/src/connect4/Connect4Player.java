package connect4;

// The Connect4 player
class Connect4Player {

    protected final Connect4Board board;
    protected final Connect4Board.Piece piece;
    protected final String name;
    
    Connect4Player(Connect4Board board, Connect4Board.Piece piece, String name) {
        this.board = board;
        this.piece = piece;
        this.name = name;
    }

    public String name() {
        return name;
    }

    public boolean isComputer() {
        return false;
    }

    public Connect4Board.Piece getPiece() {
        return piece;
    }

    public boolean doMove(int c) {
        return board.move(piece,c);
    }

    public boolean doMove() {
        return false;
    }

} // Connect4Player
