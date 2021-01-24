package connect4game;
// The board

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javafx.scene.paint.Color;


class Connect4Board {

    final static int WIN_SCORE  = 1000;  // Score (Stellungsbewertung)

    // The piece
    static enum Piece {
        
        RED(+1,Color.RED), YELLOW(-1,Color.YELLOW), EMPTY(0,Color.WHITE); 
       
        private final int fieldValue;
        private final Color color;
        
        private Piece(int fieldValue, Color color) {
              this.fieldValue = fieldValue;
              this.color = color;
        }

        int getFieldValue() {
            return fieldValue;
        }
        
        static Piece ofFieldValue( int f ) {
            switch (f) {
            case -1: return YELLOW;
            case +1: return RED;
            default: return EMPTY;
            }
        }

        Color getColor() {
            return color;
          }
    }

    
    // Board data
    int[][] board; // Piece field values -1,0,+1
    int[] colPieces; // Number of pieces in a column
    int totPieces; // Overall number pieces on the board
    List<Line> lines; // Array list of all still possible line combinations

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
    class Line {

        private final List<Field> fields;
        
        Line(int col, int row, int colo, int rowo) { // Create a winning line starting at (col,rol) in direction (colo,rowo)
            fields = new ArrayList<Field>();
            for (int i = 0; i < 4; i++) {
                fields.add(new Field(col + i * colo, row + i * rowo));
            }
        }

        int value() { // Count number of unique pieces in this line
            int s = 0;
            for (Field f : fields) {
                int sf = f.getFieldValue();
                if (s * sf < 0) return 0;
                s += sf;
            }
            return s;
        }

        int count() { // Count number of pieces in this line
            int n = 0;
            for (Field f : fields) {
                if (f.getFieldValue()!=0) n++;
            }
            return n;
        }
    }

    // Create all winning line combinations of an empty field
    private List<Line> buildLines() {
        List<Line> lines = new ArrayList<Line>();
        for (int r = 0; r < Connect4Game.ROWS; r++) {
            for (int c = 0; c < Connect4Game.COLS; c++) {
                if (r + 4 <= Connect4Game.ROWS)
                    lines.add(new Line(c, r, 0, 1)); // Vertical
                if (c + 4 <= Connect4Game.COLS)
                    lines.add(new Line(c, r, 1, 0)); // Horizontal
                if (r + 4 <= Connect4Game.ROWS && c + 4 <= Connect4Game.COLS)
                    lines.add(new Line(c, r, 1, 1)); // Diagonal
                if (r + 4 <= Connect4Game.ROWS && c - 3 >= 0)
                    lines.add(new Line(c, r, -1, 1));
            }
        }
        return lines;
    }

    // Remove all lines which currently do not have any more impact on the game
    void updateLines() {       
        Iterator<Line> i = lines.iterator();
        while (i.hasNext()) {
            Line l = i.next();
            if (l.count() != 0 && l.value() == 0) {
                i.remove();
            }
        }
    }

    // Create a game
    Connect4Board() {

        // Create board
        board = new int[Connect4Game.COLS][Connect4Game.ROWS];
        colPieces = new int[Connect4Game.COLS];
        totPieces = 0;
        for (int c = 0; c < Connect4Game.COLS; c++) {
            colPieces[c] = 0;
            for (int r = 0; r < Connect4Game.ROWS; r++) {
                board[c][r] = Piece.EMPTY.fieldValue;
            }
        }
        lines = buildLines();
        gameOver = false;
        moveStack = new Stack<Field>();

        statusUpdate("");
    }
         
    boolean isGameOver() {
        return gameOver;
    }
    
    // Find a line completed with 4 pieces
    private Line getWinningLine() {
        for (Line l : lines) {
            int s = l.value();
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
    boolean move(Piece piece, int col) {

        System.out.println("board.move("+piece.name()+"," + col + ")");

        int r = colPieces[col];
        if (r < Connect4Game.ROWS && !gameOver) {
            putPiece(col, piece);
            boardUpdate(piece, true, false, col, r);
            moveStack.push(new Field(col, r));
            Line l = getWinningLine();
            if (l!=null) {
                statusUpdate(l.fields.get(0).getPiece() + " wins!");
                markWinningLine(true);
                gameOver = true;
            } 
            else if (totPieces >= Connect4Game.ROWS * Connect4Game.COLS) {
                statusUpdate("Game over!");
                gameOver = true;
            }
            return true;
        } 
        else {
            return false;
        }
    }

    // Undo the last moves of player 1
    void undo() {

        if (totPieces > 0) {
                if (gameOver) {
                    markWinningLine(false); // Remove winning line markers
                    gameOver = false;
                }
                Field f = moveStack.pop();
                int r = f.row;
                int c = f.col;
                Piece p = Piece.ofFieldValue(board[c][r]);
                System.out.println(p + " " + c);
                removePiece(c);
                lines = buildLines(); // Rebuild the line list
                updateLines();
                boardUpdate(Piece.EMPTY, false, false, c, r);
                statusUpdate("");
        }
    }

    // Put a piece
    void putPiece(int col, Piece piece) {
        board[col][colPieces[col]++] = piece.getFieldValue();
        totPieces++;
        updateLines();
    }
    
    // Remove a piece
    void removePiece(int col) {
        board[col][--colPieces[col]] = 0;
        totPieces--;
        updateLines();
    }

 
    // Notify somebody (GUI) on board changes
    private Connect4Game.BoardUpdateListener boardUpdateListener;
        
    void registerBoardUpdateListener( Connect4Game.BoardUpdateListener l ) {
        boardUpdateListener = l;
    }
    void boardUpdate(Piece piece, boolean isNew, boolean marker, int col, int row) {
        if (boardUpdateListener!=null) boardUpdateListener.Update(piece.color,isNew,marker,col,row); 
    }

    // Notify somebody (GUI) on status changes
    private Connect4Game.StatusUpdateListener statusUpdateListener;
     
    void registerStatusUpdateListener( Connect4Game.StatusUpdateListener l ) {
        statusUpdateListener = l;
    }
    void statusUpdate(String s) {
        if (statusUpdateListener!=null) statusUpdateListener.PrintStatus(s);
    }
      

}
