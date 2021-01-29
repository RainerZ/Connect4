package connect4game;

import connect4game.Connect4Board.Piece;

public class Connect4HumanPlayer extends Connect4Player {

    public Connect4HumanPlayer(Connect4Board board, Piece piece, String name) {
        super(board, piece, name);
    }

    @Override
    boolean doMove(int c) {
        return board.move(piece,c);
    }

    boolean isComputer() {
        return false;
    }


}
