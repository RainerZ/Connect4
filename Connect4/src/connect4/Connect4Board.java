package connect4;
// The board

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javafx.scene.paint.Color;


class Connect4Board {

    // Parameters and constants
    public final static int COLS   = 7;        // Board
    public final static int ROWS   = 6;
    
    // The piece
    public static enum Piece {
        
        RED(+1,Color.RED), YELLOW(-1,Color.YELLOW), EMPTY(0,Color.WHITE); 
       
        private final int fieldValue;
        private final Color color;
        
        private Piece(int fieldValue, Color color) {
              this.fieldValue = fieldValue;
              this.color = color;
        }

        public int getFieldValue() {
            return fieldValue;
        }
        
        public static Piece ofFieldValue( int f ) {
            switch (f) {
            case -1: return YELLOW;
            case +1: return RED;
            default: return EMPTY;
            }
        }

        public Color getColor() {
            return color;
          }
    }

    
    // Board info
    public int[][] board; // Piece field values -1,0,+1
    public int[] colPieces; // Number of pieces in a column
    public int totPieces; // Overall number pieces on the board
    public final List<Line> lines; // Array list of all possible line combinations

    // Status
    private boolean gameOver;
    private Stack<Field> moveStack; // Move stack,for undo

    // Field (field array element boxing class)
    public class Field {

        final int row, col;

        Field(int col, int row) {
            this.row = row;
            this.col = col;
        }

        public int getFieldValue() { 
            return board[col][row]; 
        }

        public Piece getPiece() { 
            return Piece.ofFieldValue(board[col][row]);
        }
    }

    // Line (a winning combination of 4 fields) 
    public class Line {

        private final List<Field> fields;
        
        Line(int col, int row, int colo, int rowo) { // Create a winning line starting at (col,rol) in direction (colo,rowo)
            fields = new ArrayList<Field>();
            for (int i = 0; i < 4; i++) {
                fields.add(new Field(col + i * colo, row + i * rowo));
            }
        }

        public int count() { // Count number of unique pieces in this line
            int s = 0;
            for (Field f : fields) {
                int sf = f.getFieldValue();
                if (s * sf < 0) return 0;
                s += sf;
            }
            return s;
        }
    }

    // Notify somebody (GUI) on board changes
    private BoardUpdateListener boardUpdateListener;
    public interface BoardUpdateListener {
        public void Update(Piece p, boolean animated, boolean marker, int column, int row);        
    };     
    public void registerBoardUpdateListener( BoardUpdateListener l ) {
        boardUpdateListener = l;
    }
    public void boardUpdate(Piece p, boolean animated, boolean marker, int col, int row) {
        if (boardUpdateListener!=null) boardUpdateListener.Update(p,animated,marker,col,row); 
    }

    // Notify somebody (GUI) on status changes
    private StatusUpdateListener statusUpdateListener;
    public interface StatusUpdateListener {
        public void PrintStatus(String s);        
    };   
    public void registerStatusUpdateListener( StatusUpdateListener l ) {
        statusUpdateListener = l;
    }
    public void statusUpdate(String s) {
        if (statusUpdateListener!=null) statusUpdateListener.PrintStatus(s);
    }
      
    // Create a game
    Connect4Board() {

        // Create board
        board = new int[COLS][ROWS];
        colPieces = new int[COLS];

        // Create all winning line combinations once
        lines = new ArrayList<Line>();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (r + 4 <= ROWS)
                    lines.add(new Line(c, r, 0, 1)); // Vertical
                if (c + 4 <= COLS)
                    lines.add(new Line(c, r, 1, 0)); // Horizontal
                if (r + 4 <= ROWS && c + 4 <= COLS)
                    lines.add(new Line(c, r, 1, 1)); // Diagonal
                if (r + 4 <= ROWS && c - 3 >= 0)
                    lines.add(new Line(c, r, -1, 1));
            }
        }

        init(); // Go
    }

    // New game
    public void init() {

        totPieces = 0;
        for (int c = 0; c < COLS; c++) {
            colPieces[c] = 0;
            for (int r = 0; r < ROWS; r++) {
                board[c][r] = Piece.EMPTY.fieldValue;
            }
        }

        gameOver = false;
        moveStack = new Stack<Field>();

        statusUpdate("");
    }
         
    public boolean isGameOver() {
        return gameOver;
    }
    
    // Find a line completed with 4 pieces
    private Line getWinningLine() {
        for (Line l : lines) {
            int s = l.count();
            if (s == -4 || s == +4) return l;
        }
        return null;
    }

    // Nicely mark the winning combination on GUI
    private void markWinningLine(boolean mark) {
        Line l = getWinningLine();
        if (l != null) {
            for (Field f : l.fields) {
                if (mark) {
                    boardUpdate(Piece.EMPTY, false, true, f.col, f.row);
                } else {
                    boardUpdate(Piece.ofFieldValue(board[f.col][f.row]), false, false, f.col, f.row);
                }
            }
        }
    }

    // Do a move, check and update game status, push to undo stack
    public boolean move(Piece p, int c) {

        System.out.println("board.move("+p.name()+"," + c + ")");

        int r = colPieces[c];
        if (r < ROWS && !gameOver) {
            put(c, p.getFieldValue());
            boardUpdate(p, true, false, c, r);
            moveStack.push(new Field(c, r));
            Line l = getWinningLine();
            if (l!=null) {
                statusUpdate(l.fields.get(0).getPiece() + " wins!");
                markWinningLine(true);
                gameOver = true;
            } 
            else if (totPieces >= ROWS * COLS) {
                statusUpdate("Game over!");
                gameOver = true;
            }
            return true;
        } 
        else {
            return false;
        }
    }

    // Undo the last 2 moves
    public void undo() {

        if (totPieces >= 2) {
            if (gameOver) {
                markWinningLine(false); // Remove winning line markers
                gameOver = false;
            }
            for (int i = 0; i < 2; i++) { // Undo player and computer move and place a grey disc
                Field f = moveStack.pop();
                int r = f.row;
                int c = f.col;
                remove(c);
                boardUpdate(Piece.EMPTY, false, false, c, r);
            }
            statusUpdate("");
        }
    }


    // Put a piece
    public void put(int c, int fieldValue) {
        board[c][colPieces[c]++] = fieldValue;
        totPieces++;
    }

    // Remove a piece
    public void remove(int c) {
        board[c][--colPieces[c]] = 0;
        totPieces--;
    }

}
