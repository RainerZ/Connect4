package connect4game;

import java.util.Optional;

// The Connect4 player
abstract class Connect4Player {

    { System.out.println("new Connect4Player()"); }

    final String name;
    final Connect4Board.Piece piece;
    
    public Connect4Player(Connect4Board.Piece piece, String name) {
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

    abstract Optional<Integer> computeMove( Connect4Board board);
    
} // Connect4Player
