package connect4game;
// The Connect4 player


class Connect4Player {

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

    boolean isComputer() {
        return false;
    }

    Connect4Board.Piece getPiece() {
        return piece;
    }

    boolean doMove(int c) {
        return board.move(piece,c);
    }

    boolean calcMove() {
        return false;
    }

} // Connect4Player
