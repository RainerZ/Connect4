package connect4player;

import java.util.Optional;

import connect4board.Connect4Board;
import connect4board.Connect4Piece;

// The Connect4 player
public abstract class Connect4Player {

    final String name;
    final Connect4Piece piece;
    
    public Connect4Player(Connect4Piece piece, String name) {
        this.piece = piece;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Connect4Piece getPiece() {
        return piece;
    }

    public boolean isComputer() { return false; }
    public boolean isRemote() { return false; }
    public boolean isHuman() { return false; }
    
    public Optional<Integer> computeMove(Connect4Board board) {
        return Optional.empty();
    }

    public void setOponentMove( int col) {};
   
    
} // Connect4Player
