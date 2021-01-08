package connect4;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;



public class Connect4Game {
    
    
    public final int COLS;
    public final int ROWS;

    private final int MAX_DEPTH = 8;

    public final int RED = +1;
    public final int YELLOW = -1;
    
    private final int EMPTY = 0;
    
    private int[][] board;
    private int[] col_pieces;
    private int pieces;
    
    private List<Line> lines;
    
    private String status;
    private boolean over;
   
    private class Field {
        int row,col;
        Field( int col, int row) {
          this.row = row;
          this.col = col;
        }
        public int get() {
            return board[col][row];
        }
    }
   
    
    private class Line {
        
        List<Field> line;
        
        public Line(int col, int row, int colo, int rowo) {
            line = new ArrayList<Field>();
            for (int i=0; i<4;i++) {
              line.add(new Field(col+i*colo,row+i*rowo));
            }
            print();
        }

        public void print() {
            System.out.print("Line ");
            for (Field f: line) { 
                System.out.print("("+f.row+","+f.col+") ");
            }
            System.out.println();
        }

        public int sum() {
            
            int s = 0;
            for (Field f : line) {
                int sf = f.get();
                if (s*sf<0) {
                    return 0;
                }
                else {
                    s += sf;
                }
            }
            return s;
        }
    }
    
    
    
    Connect4Game( int cols, int rows) {
        
        COLS = cols;
        ROWS = rows;
        
        // Create board
        board = new int[COLS][ROWS];
        col_pieces = new int[COLS];
        
        // Create all lines
        lines = new ArrayList<Line>();
        for (int r=0; r<ROWS; r++) {
            for (int c=0; c<COLS; c++) {
                if (r+4<=ROWS) lines.add(new Line(c,r,0,1)); // Vertical
                if (c+4<=COLS) lines.add(new Line(c,r,1,0));
                if (r+4<=ROWS && c+4<=COLS) lines.add(new Line(c,r,1,1));
                if (r+4<=ROWS && c-4>=0)lines.add(new Line(c,r,-1,1));
            }
        }
       
        
        newGame();
    }
    
    public void newGame() {

        pieces = 0;
        for (int c=0; c<COLS; c++) {
            col_pieces[c] = 0;
            for (int r=0; r<ROWS; r++) {
              board[c][r] = EMPTY;
            }
        }
        
        status = "Start";
        over = false;
    }
    
    public boolean isOver() {
        return over;
    }
    
    public String getStatus() {
        return status;
    }
    
    
    public int move(int c, int p) {
      
        if (col_pieces[c] < ROWS && !over) {
            doMove(c, p);
            int s = getScore();
            if (s <= -1000) {
                status = "YELLOW wins!";
                over = true;
            } else if (s >= +1000) {
                status = "RED wins!";
                over = true;
            } else if (pieces >= ROWS * COLS) {
                status = "Game over!";
                over = true;
            }
            return col_pieces[c] - 1;
        } else {
            status = "Illegal move";
            return -1;
        }
    }

    private void doMove(int c, int p) {
     
        board[c][col_pieces[c]++] = p;
        pieces++;
    }
    

    private void undoMove(int c) {
      
        board[c][--col_pieces[c]] = 0;
        pieces--;
    }
    
    public int calcBestMove(int p) {

        int c = minmax(p,0);
        System.out.println("Best move for " + p + " is " + c );
        print();
        return c;
    }

    private int minmax( int p, int depth ) {
        
        int s = getScore();
        if (depth >= MAX_DEPTH || pieces >= ROWS*COLS || s == -1000 || s == +1000) return s;
        
        int s_max = -100000;
        int s_min = +100000;
        int c_min = -1;
        int c_max = -1;
        for (int c = 0; c < COLS; c++) {
            if (col_pieces[c] < ROWS) {
                doMove(c,p);
                s = minmax(-p,depth+1);
                if (s > s_max) {
                    s_max = s;
                    c_max = c;
                }
                if (s < s_min) {
                    s_min = s;
                    c_min = c;
                }
                if (depth==0) System.out.println(c + ":" + s );
                undoMove(c);
            }
        }
        if (depth==0) { // Return best move on level 0
            if (p>0) { // RED
                if (s_max==+1000) status = "RED will win!";
                if (s_max==-1000) status = "RED will loose!";
                return c_max;
            }
            else { // YELLOW
                if (s_min==-1000) status = "YELLOW will win!";
                if (s_min==+1000) status = "YELLOW will loose!";
                return c_min;
            }
        }
        return p>0 ? s_max : s_min;
    }

    private int getScore() {
        int s = 0;
        for (Line l : lines) {
            int s1 = l.sum();
            if (s1==-4 || s1 ==+4) return s1*250;
            s += s1;
        }
        return s;
    }
       
    private void print() {
        System.out.println("--------");
        for (int c = 0; c < COLS; c++) {
            System.out.print(col_pieces[c] + "|");
            for (int r = 0; r < ROWS; r++) {
                System.out.print(board[c][r] == 0 ? " " : board[c][r] == 1 ? "R" : "Y");
            }
            System.out.println("");
        }
        System.out.println("--------");
        System.out.println("Score = " + getScore());
    } 
    
}
