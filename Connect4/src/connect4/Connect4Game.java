package connect4;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;



public class Connect4Game {
    
    
    public final int COLS;
    public final int ROWS;

    public final int MAX_DEPTH = 7;

    public final int RED = +1;
    public final int YELLOW = -1;
    public final int EMPTY = 0;
    
    private int[][] board;
    private int[] col_pieces;
    private int pieces;
    
    List<Line> lines;
    
   
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

        public int getScore() {
            
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
            if (s==+3) s = +10;
            if (s==-3) s = -10;
            if (s==+4) s = +1000;
            if (s==-4) s = -1000;
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

    }
    
    public boolean isOver( ) {
        int s = getScore();
        return s<=-1000 || s>=+1000 || pieces >= ROWS*COLS;
    }
    
    
    public int doMove(int c, int p) {
        if (col_pieces[c] < ROWS) {
            int r = col_pieces[c];
            board[c][r] = p;
            col_pieces[c]++;
            pieces++;
            return r;
        }
        return -1;
    }

    public void undoMove(int c) {
        int r = --col_pieces[c];
        board[c][r] = 0;
        pieces--;
    }
    
    public int getScore() {
        int s = 0;
        for (Line l : lines) {
            int s1 = l.getScore();
            if (s1==-1000||s1==+1000) return s1;
            s += s1;
        }
        return s;
    }
    
    public int calcBestMove( int p ) {
        int c_max = -1;
        int s_max = -100000;
        for (int c=0; c<COLS; c++) {
           if (col_pieces[c]<ROWS) {
           int s = minmax(c,p,0);
           System.out.println(c+": "+s);
           if ((s*p)>s_max) {
                   s_max = s*p;
                   c_max = c;
                   System.out.println(" *"+s_max);
           }
           }
        }
        System.out.println("Best move for "+p+" is "+c_max+" score "+p*s_max);
        print();
        return c_max;
    }
    
    private int minmax( int c, int p, int depth ) {
        
        int s = 0;

        doMove(c, p);
        
        s = getScore();

        if (depth >= MAX_DEPTH || pieces>=ROWS*COLS || s==-1000 || s==+1000) {
            } 
            else {
                int s_max = -100000;
                for (int c1 = 0; c1 < COLS; c1++) {
                    if (col_pieces[c1]<ROWS) {
                    s = p*minmax(c1, -p, depth + 1);
                    if (s > s_max) {
                        s_max = s;
                    }
                    }
                }
                s = p*s_max;
            }

         undoMove(c);
        
         return s;
        
    }

private void print() {
        System.out.println("--------");
        for (int c=0; c<COLS; c++) {
          System.out.print(col_pieces[c]+"|");
          for (int r=0; r<ROWS; r++) {
                System.out.print(board[c][r]==0?" ":board[c][r]==1?"R":"Y");
            }
            System.out.println("");
        }
        System.out.println("--------");
        System.out.println("Score = "+getScore());
    
    }
    
    
}
