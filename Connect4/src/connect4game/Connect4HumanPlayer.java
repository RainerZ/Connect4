package connect4game;

public class Connect4HumanPlayer extends Connect4Player {

    public Connect4HumanPlayer(Connect4Board board, Connect4Board.Piece piece, String name) {
        super(board, piece, name);
    }
    
    { System.out.println("new Connect4HumanPlayer()"); }


    @Override
    boolean doMove(int c) {
        return board.move(piece,c);
    }

    boolean isComputer() {
        return false;
    }


}
