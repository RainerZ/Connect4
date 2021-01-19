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
    
    public final int WIN_SCORE  = 1000;  // Score (Stellungsbewertung)

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
    private int[][] board; // Piece field values -1,0,+1
    private int[] colPieces; // Number of pieces in a column
    private int totPieces; // Overall number pieces on the board
    private final List<Line> lines; // Array list of all possible line combinations

    // Status
    private boolean gameOver;
    private Stack<Field> moveStack; // Move stack,for undo

    // Field (field array element boxing class)
    private class Field {

        private final int row, col;

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
    private class Line {

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
        public void Update(Piece piece, boolean isNew, boolean marker, int column, int row);        
    };     
    public void registerBoardUpdateListener( BoardUpdateListener l ) {
        boardUpdateListener = l;
    }
    protected void boardUpdate(Piece piece, boolean isNew, boolean marker, int col, int row) {
        if (boardUpdateListener!=null) boardUpdateListener.Update(piece,isNew,marker,col,row); 
    }

    // Notify somebody (GUI) on status changes
    private StatusUpdateListener statusUpdateListener;
    public interface StatusUpdateListener {
        public void PrintStatus(String s);        
    };   
    public void registerStatusUpdateListener( StatusUpdateListener l ) {
        statusUpdateListener = l;
    }
    protected void statusUpdate(String s) {
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
    public boolean move(Piece piece, int col) {

        System.out.println("board.move("+piece.name()+"," + col + ")");

        int r = colPieces[col];
        if (r < ROWS && !gameOver) {
            putPiece(col, piece);
            boardUpdate(piece, true, false, col, r);
            moveStack.push(new Field(col, r));
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

        if (totPieces > 0) {
            if (gameOver) {
                markWinningLine(false); // Remove winning line markers
                gameOver = false;
            }
            Field f = moveStack.pop();
            int r = f.row;
            int c = f.col;
            Piece p = Piece.ofFieldValue(board[c][r]);
            System.out.println( p + " " + c);
            removePiece(c);
            boardUpdate(Piece.EMPTY, false, false, c, r);
            
            statusUpdate("");
        }
    }

    // Get a piece
    public Piece getPiece(int col, int row ) {
        return Piece.ofFieldValue(board[col][row]);
    }
    protected int getPieceValue(int col, int row ) {
        return board[col][row];
    }

    // Put a piece
    protected void putPiece(int col, Piece piece) {
        putPieceValue(col,piece.fieldValue);
    }
    protected void putPieceValue(int col, int p) {
        board[col][colPieces[col]++] = p;
        totPieces++;
    }

    // Remove a piece
    protected void removePiece(int col) {
        board[col][--colPieces[col]] = 0;
        totPieces--;
    }

    // Get the current board score, -1000 ... +1000 given for a winning combination,
    // player1 = -player2 score
    protected int getScore(int p) {
        int s = 0;
        for (Connect4Board.Line l : lines) {
            int s1 = l.count();
            if (s1 == -4 || s1 == +4) {
                return p * s1 * WIN_SCORE/4;
            }
            s += s1;
        }
        return p * s;
    }
    
    protected int getColPieces(int col) {
        return colPieces[col];
    }
    
    protected int getTotPieces() {
        return totPieces;
    }
}
