package connect4;

public class Connect4Game {
    
    public final int COLS;
    public final int ROWS;
    public enum Symbol { E, R, Y; }
    
    private Symbol[][] board;
    
    Connect4Game( int cols, int rows) {
        
        COLS = cols;
        ROWS = rows;
        
        board = new Symbol[ROWS][COLS];
        newGame();
    }
    
    public void newGame() {

        for (int r=0; r<ROWS; r++) {
            for (int c=0; c<COLS; c++) {
              board[r][c] = Symbol.E;
            }
        }

    }
    
    public boolean doMove( int c, Symbol piece ) {
        for (int r=0;r<ROWS;r++) {
            if (board[r][c]==Symbol.E) {
                board[r][c]= piece;
print();
                return true;
            }
        }
        return false;
        
    }
    
    public int calcMove() {
        for (int c=0; c<COLS; c++) {
            for (int r=0; r<ROWS; r++) {
                if (board[r][c]==Symbol.E) return c;
            }
        }
        return -1;
    }
    
    
    private void print() {
        System.out.println("_______");
        for (int c=0; c<COLS; c++) {
          for (int r=0; r<ROWS; r++) {
                System.out.print(board[r][c]);
            }
            System.out.println("");
        }
    
    }
    
    
    
}
