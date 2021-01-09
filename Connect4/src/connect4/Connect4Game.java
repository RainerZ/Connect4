package connect4;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Connect4Game {
    
    
    public final int COLS;
    public final int ROWS;
    public final int RED = +1;
    public final int YELLOW = -1;
    private final int EMPTY = 0;
    
    private int[][] board;
    private int[] col_pieces;
    private int pieces;    
    private List<Line> lines;
    private String status;
    private boolean over;
    private int nextPlayer;
    private int maxDepth;
    private int[] ci = { 3,4,2,1,5,0,6};
   
    public class Field {
        int row,col;
        Field( int col, int row) {
          this.row = row;
          this.col = col;
        }
        public int get() { return board[col][row]; }
        public int getRow() {return row; }
        public int getCol() {return col; }
    }
   
    
    public class Line {
        
        private List<Field> line;
        
        public Line(int col, int row, int colo, int rowo) {
            line = new ArrayList<Field>();
            for (int i=0; i<4;i++) {
              line.add(new Field(col+i*colo,row+i*rowo));
            }
        }

        public List<Field> getLine() { return line; }
        
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
                if (c+4<=COLS) lines.add(new Line(c,r,1,0)); // Horizontal
                if (r+4<=ROWS && c+4<=COLS) lines.add(new Line(c,r,1,1)); // Diagonal
                if (r+4<=ROWS && c-3>=0)lines.add(new Line(c,r,-1,1));
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
        
        status = "";
        over = false;
        nextPlayer = RED;
        setOptimalMaxDepth();
    }
    
    public boolean isOver() {
        return over;
    }
    
    public String getStatus() {
        return status;
    }
    
    public Line getWiningLine() {
        for (Line l : lines) {
            int s1 = l.sum();
            if (s1==-4 || s1 ==+4) {
                return l;
            }
        }
        return null;
    }
    
    public int move(int c, int p) {
      
        if (col_pieces[c] < ROWS && !over && nextPlayer==p) {
            doMove(c, p);
            int s = getScore(RED);
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

            print();
            System.out.println("Status = " + status );
            nextPlayer = -nextPlayer;
            setOptimalMaxDepth();
            return col_pieces[c] - 1;
        } 
        else {
            return -1;
        }
    }

    private void doMove(int c, int p) {
     
        board[c][col_pieces[c]++] = p;
        pieces++;
    }
    


private int minmax( int p, int depth, int alpha, int beta ) {
        
        int s = getScore(p);
        if (depth >= maxDepth) return s; 
        if (pieces >= ROWS*COLS || s == -1000 || s == +1000) return s; // Game over
        int s_max = -100000;
        int c_max = -1;
        for (int i = 0; i < COLS; i++) {
            int c = ci[i];
            if (col_pieces[c] < ROWS) {
                doMove(c,p);
                s = -minmax(-p,depth+1,-beta,-alpha);
                if (s > s_max) {
                    s_max = s;
                    c_max = c;
                }
                if (s > alpha) {
                    alpha = s;
                    if (alpha>beta) {
                        undoMove(c);        
                        break;
                    }
                }
                undoMove(c);
            }
        }
        if (depth==0) { // Return best move for actual board and player on level 0
                if (s_max==+1000) status = "Computer will win!";
                if (s_max==-1000) status = "Computer will loose!";
                return c_max;
        }
        return s_max; // Return score on other levels
    }

    private void undoMove(int c) {    
        board[c][--col_pieces[c]] = 0;
        pieces--;
    }
    
    public int calcBestMove(int p) {
        int c = minmax(p,0,-100000,+100000);       
        return c;
    }

    private int getScore(int p) {
        int s = 0;
        for (Line l : lines) {
            int s1 = l.sum();
            if (s1==-4 || s1 ==+4) return p*s1*250;
            s += s1;
        }
        return p*s;
    }
       
    private void setOptimalMaxDepth() {
        if (ROWS*COLS-pieces<42/3) {
            maxDepth = 20;
        }
        else if (ROWS*COLS-pieces<42/2) {
            maxDepth = 15;
        }
        else {
            maxDepth = 10;
        }
        for (int i=0;i<COLS;i++) {
            if (col_pieces[i]==ROWS) maxDepth++;
        }
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
        System.out.println("RED Score = " + getScore(RED));
    } 
    
}




