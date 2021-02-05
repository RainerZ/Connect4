package connect4game;
// The board

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

import javafx.scene.paint.Color;


class Connect4Board {

    // Status
    private boolean gameOver = false;

    // Create a board
    Connect4Board(Connect4Game.BoardUpdateListener bl,Connect4Game.StatusUpdateListener sl) {

        boardUpdateListener = Optional.of(bl);
        statusUpdateListener = Optional.of(sl);
        statusUpdate("");
    }
    
    static { System.out.println("load Connect4Board() class"); }
    { System.out.println("new Connect4Board()"); }

    // Notify somebody (GUI) on board changes
    private final Optional<Connect4Game.BoardUpdateListener> boardUpdateListener;
    void boardUpdate(Piece piece, boolean isNew, boolean marker, int col, int row) {
        boardUpdateListener.ifPresent( l -> l.Update(piece.color,isNew,marker,col,row)); 
    }

    // Notify somebody (GUI) on status changes
    private final Optional<Connect4Game.StatusUpdateListener> statusUpdateListener;
    void statusUpdate(String s) {
        statusUpdateListener.ifPresent( l -> l.PrintStatus(s));
    }
      

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
    } // Piece

    
    // Board data
    private int[][] board = new int[Connect4Game.COLS][Connect4Game.ROWS]; // Piece field values -1,0,+1
    private int[] colPieces = new int[Connect4Game.COLS]; // Number of pieces in a column
    private int totPieces = 0; // Overall number pieces on the board

    // Init board
    {
        for (int c = 0; c < Connect4Game.COLS; c++) {
            colPieces[c] = 0;
            for (int r = 0; r < Connect4Game.ROWS; r++) {
                board[c][r] = Piece.EMPTY.fieldValue;
            }
        }
    }

    // Put a piece
    void putPiece(int col, int p) {
        board[col][colPieces[col]++] = p;
        totPieces = getTotPieces() + 1;
    }
    
    // Remove a piece
    void removePiece(int col) {
        board[col][--colPieces[col]] = 0;
        totPieces = getTotPieces() - 1;
    }

    int getColPieces(int col) {
        return colPieces[col];
    }

    int getTotPieces() {
        return totPieces;
    }


    // Field (field array element boxing class)
    private class Field {

        private final int row, col;

        Field(int col, int row) {
            this.row = row;
            this.col = col;
        }

        int getFieldValue() { 
            return board[col][row]; 
        }

        Piece getPiece() { 
            return Piece.ofFieldValue(board[col][row]);
        }
    } // Field

    
    // Line (a winning combination of 4 fields) 
    class Line {

        private final List<Field> fields = new ArrayList<Field>(4);
        
        private Line(int col, int row, int colo, int rowo) { // Create a winning line starting at (col,rol) in direction (colo,rowo)
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
    } // Line

    private List<Line> lines = buildLines(); // Array list of all still possible line combinations


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

    List<Line> getLines() {
        return lines;
    }

    // Remove all lines which currently do not have any more impact on the game
    private void updateLines() {       
        Iterator<Line> i = getLines().iterator();
        while (i.hasNext()) {
            Line l = i.next();
            if (l.count() != 0 && l.value() == 0) {
                i.remove();
            }
        }
    }

    // Game is over
    boolean isGameOver() {
        return gameOver;
    }
    
    // Find any line completed with 4 pieces
    private Optional<Line> getWinningLine() {
        for (Line l : getLines()) {
            int s = l.value();
            if (s == -4 || s == +4) return Optional.of(l);
        }
        return Optional.empty();
    }

    // Nicely mark the winning combination on GUI
    private void markWinningLine(boolean mark) {
        getWinningLine().ifPresent(l -> {
            for (Field f : l.fields) {
                if (mark) {
                    boardUpdate(Piece.EMPTY, false, true, f.col, f.row);
                } else {
                    boardUpdate(Piece.ofFieldValue(board[f.col][f.row]), false, false, f.col, f.row);
                }
            }
        });
    }

    private Stack<Field> moveStack = new Stack<Field>(); // Move stack,for move and undo

    // Do a move, check and update game status, push to undo stack
    boolean move(Piece piece, int col) {
        int r = getColPieces(col);
        if (r < Connect4Game.ROWS && !gameOver) {
            System.out.println(piece.name()+":"+col);
            putPiece(col, piece.getFieldValue());
            updateLines();
            boardUpdate(piece, true, false, col, r);
            moveStack.push(new Field(col, r));
            getWinningLine().ifPresent( l -> {
                statusUpdate(l.fields.get(0).getPiece() + " wins!");
                markWinningLine(true);
                gameOver = true;
            });
            if (!gameOver && getTotPieces() >= Connect4Game.ROWS * Connect4Game.COLS) {
                statusUpdate("Game over!");
                gameOver = true;
            } 
            return true;
        } 
        else {
            return false;
        }
    }

    // Undo the last move
    void undo() {

        if (getTotPieces() > 0) {
                if (gameOver) {
                    markWinningLine(false); // Remove winning line markers
                    gameOver = false;
                }
                Field f = moveStack.pop();
                int r = f.row;
                int c = f.col;
                Piece p = Piece.ofFieldValue(board[c][r]);
                System.out.println(p+":"+ c);
                removePiece(c);
                lines = buildLines(); // Rebuild the line list
                updateLines();
                boardUpdate(Piece.EMPTY, false, false, c, r);
                statusUpdate("");
        }
    }

}
