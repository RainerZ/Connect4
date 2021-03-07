package connect4player;

import connect4board.Connect4Piece;

// A human player
public class Connect4HumanPlayer extends Connect4Player {

    public Connect4HumanPlayer(Connect4Piece piece, String name) {
        super(piece, name);
    }

    @Override
    public boolean isHuman() {
        return true;
    }

}
