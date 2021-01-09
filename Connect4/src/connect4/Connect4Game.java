package connect4;

import java.util.ArrayList;
import java.util.List;

public class Connect4Game {

    public final int COLS = 7;
    public final int ROWS = 6;
    
    public final int RED = +1;
    public final int YELLOW = -1;
    
    private final int EMPTY = 0;

    private int[][] board;
    private int[] colPieces;
    private int totPieces;
    private List<Line> lines;
    private String statusText;
    private boolean gameOver;
    private int nextPlayer;
    private int maxDepth;
    private int[] colOrder = { 3, 4, 2, 1, 5, 0, 6 };

    private class Field {
        
        final int row, col;

        Field(int col, int row) {
            this.row = row;
            this.col = col;
        }

        public int get() { return board[col][row]; }
    }

    private class Line {

        private List<Field> fields;

        Line(int col, int row, int colo, int rowo) {
            fields = new ArrayList<Field>();
            for (int i = 0; i < 4; i++) {
                fields.add(new Field(col + i * colo, row + i * rowo));
            }
        }

        public int sum() { 

            int s = 0;
            for (Field f : fields) {
                int sf = f.get();
                if (s * sf < 0) {
                    return 0;
                } else {
                    s += sf;
                }
            }
            return s;
        }
    }

    
    Connect4Game() {

        // Create board
        board = new int[COLS][ROWS];
        colPieces = new int[COLS];

        // Create all lines
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

        newGame();
    }

    public void newGame() {

        totPieces = 0;
        for (int c = 0; c < COLS; c++) {
            colPieces[c] = 0;
            for (int r = 0; r < ROWS; r++) {
                board[c][r] = EMPTY;
            }
        }

        statusText = "";
        gameOver = false;
        nextPlayer = RED;
        setOptimalMaxDepth();
    }

    public boolean isOver() {
        return gameOver;
    }

    public String getStatusText() {
        return statusText;
    }

    public int getNextPlayer() {
        return nextPlayer;
    }

    public void markWinningLine(Connect4Frame gui) {

        for (Line l : lines) {
            int s = l.sum();
            if (s == -4 || s == +4) {
                for (Field f : l.fields) {
                    gui.addMarker(f.col, f.row);
                }
                return;
            }
        }

    }

    public int calcBestMove(int p) {
        int c = minmax(p, 0, -100000, +100000);
        return c;
    }

    public int move(int c, int p) {

        if (colPieces[c] < ROWS && !gameOver && nextPlayer == p) {
            doMove(c, p);
            int s = getScore(RED);
            if (s <= -1000) {
                statusText = "YELLOW wins!";
                gameOver = true;
            } else if (s >= +1000) {
                statusText = "RED wins!";
                gameOver = true;
            } else if (totPieces >= ROWS * COLS) {
                statusText = "Game over!";
                gameOver = true;
            }
            nextPlayer = -nextPlayer;
            setOptimalMaxDepth();
            return colPieces[c] - 1;
        } else {
            return -1;
        }
    }

    
    private int minmax(int p, int depth, int alpha, int beta) {

        int s = getScore(p);
        if (depth >= maxDepth)
            return s;
        if (totPieces >= ROWS * COLS || s == -1000 || s == +1000)
            return s; // Game over
        int s_max = -100000;
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
            if (s_max == +1000)
                statusText = "Computer will win!";
            if (s_max == -1000)
                statusText = "Computer will loose!";
            return c_max;
        }
        return s_max; // Return score on other levels
    }

    private void doMove(int c, int p) {
        board[c][colPieces[c]++] = p;
        totPieces++;
    }


    private void undoMove(int c) {
        board[c][--colPieces[c]] = 0;
        totPieces--;
    }


    private int getScore(int p) {
        int s = 0;
        for (Line l : lines) {
            int s1 = l.sum();
            if (s1 == -4 || s1 == +4) return p * s1 * 250;
            s += s1;
        }
        return p * s;
    }

    private void setOptimalMaxDepth() {
        if (ROWS * COLS - totPieces < 42 / 3) {
            maxDepth = 20;
        } else if (ROWS * COLS - totPieces < 42 / 2) {
            maxDepth = 15;
        } else {
            maxDepth = 10;
        }
        int n = 0;
        for (int i = 0; i < COLS; i++) {
            if (colPieces[i] == ROWS)
                n++;
        }
        maxDepth += n / 2;
    }

}
