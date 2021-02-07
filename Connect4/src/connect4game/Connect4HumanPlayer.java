package connect4game;

import java.util.Optional;

// A human player
public class Connect4HumanPlayer extends Connect4Player {

    public Connect4HumanPlayer(Connect4Board.Piece piece, String name) {
        super(piece, name);
    }
    
    @Override
    Optional<Integer> computeMove(Connect4Board board) {
        return Optional.empty();
    }

    @Override
    boolean isComputer() {
        return false;
    }


}
