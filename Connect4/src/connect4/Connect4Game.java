package connect4;
// The game logic

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;

public class Connect4Game {

    // Parameters and constants
    public final int COLS   = 7;        // Board
    public final int ROWS   = 6;
    public final int EMPTY  = 0;

    public final int RED    = +1;       // Player
    public final int YELLOW = -1; 
    
    public final int WIN_SCORE = 1000;  // Score (Stellungsbewertung)
    public final int RED_WIN       = (WIN_SCORE*RED);
    public final int YELLOW_WIN    = (WIN_SCORE*YELLOW);

    private final int[] colOrder = { 3, 4, 2, 1, 5, 0, 6 }; // Column priority (helps alpha/beta)

    // The playboard
    private int[][] board;
    private int[] colPieces; // Pieces in a col
    private int totPieces; // Overall pieces
    private final List<Line> lines; // All line combinations

    // The game status
    private boolean gameOver;
    private int nextPlayer;
    private int maxDepth;
    private Stack<Field> moveStack; // For undo

    // Array field boxing class (help me java ?? is there a better way ?)
    private class Field {
        
        final int row, col;

        Field(int col, int row) {
            this.row = row;
            this.col = col;
        }

        public int get() { return board[col][row]; }
    }

    // Wining lines 
    private class Line {

        private final List<Field> fields;
        private final int scoreOffset;
        
        Line(int col, int row, int colo, int rowo) { // Create a winning line starting at (col,rol) in direction (colo,rowo)
            scoreOffset = ROWS-row;
            fields = new ArrayList<Field>();
            for (int i = 0; i < 4; i++) {
                fields.add(new Field(col + i * colo, row + i * rowo));
            }
        }

        public int sum() { // Calculate score for a single line
            int s = 0;
            for (Field f : fields) {
                int sf = f.get();
                if (s * sf < 0) return 0;
                s += sf;
            }
            if (s==-4 || s==+4) return s*WIN_SCORE/4; 
            if (s!=0) s = s*8+scoreOffset;
            return s;
        }
    }

    // Notify somebody (GUI) on board changes
    BoardUpdateListener boardUpdateListener;
    public interface BoardUpdateListener {
        public void Update(int player, boolean animated, boolean marker, int column, int row);        
    };     
    public void registerBoardUpdateListener( BoardUpdateListener l ) {
        boardUpdateListener = l;
    }
    private void boardUpdate(int player, boolean animated, boolean marker, int col, int row) {
        if (boardUpdateListener!=null) boardUpdateListener.Update(player,animated,marker,col,row); 
    }

    // Notify somebody (GUI) on status changes
    StatusUpdateListener statusUpdateListener;
    public interface StatusUpdateListener {
        public void PrintStatus(String s);        
    };   
    public void registerStatusUpdateListener( StatusUpdateListener l ) {
        statusUpdateListener = l;
    }
    private void printStatus(String s) {
        if (statusUpdateListener!=null) statusUpdateListener.PrintStatus(s);
    }
      
    // Create a game
    Connect4Game( int maxDepth) {

        this.maxDepth = maxDepth;
        
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

        newGame(); // Go
    }

    // New game
    public void newGame() {

        totPieces = 0;
        for (int c = 0; c < COLS; c++) {
            colPieces[c] = 0;
            for (int r = 0; r < ROWS; r++) {
                board[c][r] = EMPTY;
            }
        }

        gameOver = false;
        nextPlayer = RED;
        moveStack = new Stack<Field>();
        printStatus("New Game");
    }

    // Check game is over
    public boolean isOver() {
        return gameOver;
    }
    
    // Get next player
    public int getNextPlayer() {
        return nextPlayer;
    }

    // Nicely mark the winners combination on GUI
    public void markWinningLine(boolean mark) {
        for (Line l : lines) {
            int s = l.sum();
            if (s == YELLOW_WIN || s == RED_WIN) {
                for (Field f : l.fields) {
                    if (mark) {
                        boardUpdate(EMPTY, false, true, f.col, f.row);
                    } else {
                        boardUpdate(board[f.col][f.row], false, false, f.col, f.row);
                    }
                }
                return;
            }
        }

    }

    // Calculate best move for player p, start the minmax recursion
    public int calcBestMove(int p) {
        int c = minmax(p, 0, -1000000, +1000000);
        return c;
    }

    // Do a move c for player p
    public boolean move(int c, int p) {

        int r = colPieces[c];
        if ( r<ROWS && !gameOver && nextPlayer == p) {
            doMove(c, p);
            boardUpdate(p, true, false, c, r);
            moveStack.push(new Field(c,r));
            int s = getScore(RED);
            if (s == YELLOW_WIN) {
                printStatus("YELLOW wins!");
                markWinningLine(true);
                gameOver = true;
            } 
            else if (s == RED_WIN) {
                printStatus("RED wins!");
                markWinningLine(true);
                gameOver = true;
            } 
            else if (totPieces >= ROWS * COLS) {
                printStatus("Game over!");
                gameOver = true;
            }
            nextPlayer = -nextPlayer;
            setOptimalMaxDepth();
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
                undoMove(c);
                boardUpdate(EMPTY, false, false, c, r);
            }
            printStatus("");
        }
    }

    // Minmax algo with alpha/beta pruning (thanks c't)
    private int minmax(int p, int depth, int alpha, int beta) {

        int s = getScore(p);
        if (depth >= maxDepth)
            return s;
        if (totPieces >= ROWS * COLS || s == RED_WIN || s == YELLOW_WIN)
            return s; // Game over
        int s_max = -1000000;
        int c_max = -1;
        for (int i = 0; i < COLS; i++) {
            int c = colOrder[i];
            if (colPieces[c] < ROWS) {
                doMove(c, p);
                s = -minmax(-p, depth + 1, -beta, -alpha);
                if (s > s_max) {
                    s_max = s;
                    c_max = c;
                }
                if (s > alpha) {
                    alpha = s;
                    if (alpha > beta && depth>0) {
                        undoMove(c);
                        break;
                    }
                }
                undoMove(c);
            }
        }
        if (depth == 0) { // Return best move for actual board and player on level 0
            if (s_max == RED_WIN)
                printStatus("Computer will win!");
            if (s_max == YELLOW_WIN)
                printStatus("Computer may loose!");
            return c_max;
        }
        return s_max; // Return score on other levels
    }

    // Do a temporary move
    private void doMove(int c, int p) {
        board[c][colPieces[c]++] = p;
        totPieces++;
    }

    // Undo a temporary move
    private void undoMove(int c) {
        board[c][--colPieces[c]] = 0;
        totPieces--;
    }

    // Get the current board score, -1000 ... +1000 given for a winning combination, player1 = -player2 score
    private int getScore(int p) {        
        int s = 0;
        for (Line l : lines) {
            int s1 = l.sum();
            if (s1==RED_WIN || s1==YELLOW_WIN) return p * s1;
            s += s1;
        }
        return p * s;
    }

    // Increase max depth when game advances 
    private void setOptimalMaxDepth() {
        if (ROWS * COLS - totPieces < 42 / 3) {
            maxDepth = 20;
        } else if (ROWS * COLS - totPieces < 42 / 2) {
            maxDepth = 15;
        } 
    }

}
