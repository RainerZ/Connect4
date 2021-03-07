package connect4player;

import java.util.Optional;
import java.util.function.Consumer;

import connect4board.Connect4Board;
import connect4board.Connect4Piece;

// A player with minmax ai algorithm
public class Connect4AiPlayer extends Connect4Player {

    private final static int WIN_SCORE  = 1000;  // Score (Stellungsbewertung)
    private final int[] colOrder = {3,4,2,1,0,5,6,7,8};
    private int maxDepth; // max search depth
    private final int initialMaxDepth;
    private final int pieceValue;

    // Print status
    private Consumer<String> sl;
    private void printStatus( String s) { sl.accept(s); }

    public Connect4AiPlayer(Consumer<String> sl, Connect4Piece p, String name, int maxDepth) {
        super(p,name);
        this.sl = sl;
        this.initialMaxDepth = this.maxDepth = maxDepth;
        this.pieceValue = piece.getFieldValue();
    }

    @Override
    public boolean isComputer() {
        return true;
    }
    
    @Override
    public Optional<Integer> computeMove(Connect4Board board) {
        setOptimalMaxDepth(board);
        System.out.println(name+" is thinking (depth="+maxDepth+",lines="+board.getLineCount()+",pieces="+board.getTotPieces()+") ...");
        int col = minmax(board, pieceValue, 0, -1000000, +1000000);
        if (col>=0) return Optional.of(col);
        else return Optional.empty();
    }

    // Minmax algo with alpha/beta pruning (thanks c't)
    private int minmax(Connect4Board board, int p, int depth, int alpha, int beta) {

        int s = getBoardScore(board,p);
        if (board.getTotPieces() >= Connect4Board.ROWS * Connect4Board.COLS) {
            assert(depth!=0);
            if (depth==0) throw new IllegalArgumentException();
            return s;
        }
        if (depth >= maxDepth || s == +WIN_SCORE || s == -WIN_SCORE) return s; // max depth reached or won

        int s_max = -1000000;
        int c_max = -1;
        for (int i = 0; i < Connect4Board.COLS; i++) {
            int c = colOrder[i];
            if (board.getColPieces(c) < Connect4Board.ROWS) {
                board.put_(c,p);
                s = -minmax(board, -p, depth + 1, -beta, -alpha);
                board.remove_(c);
                if (s > s_max) {
                    s_max = s;
                    c_max = c;
                }
                if (s > alpha) {
                    alpha = s;
                    if (alpha > beta && depth > 0)
                        break;
                }
            }
        }

        if (depth == 0) { // Return best move for actual board and player on level 0
            if (s_max == +WIN_SCORE) {
                printStatus(name+" will win!");
            } 
            else if (s_max == -WIN_SCORE) {
                printStatus(name+" may loose");
                // In this case create a move which will not loose immediately, human players
                // might make faults
                if (maxDepth != 2) {
                    maxDepth = 2;
                    return minmax(board, p, 0, -1000000, +1000000);
                }

            }
            else {
              printStatus(c_max+"/"+s_max);
            }
            return c_max;
        } else {
            return s_max; // Return score on other levels
        }
    }


    // Get the current board score, -1000 ... +1000 given for a winning combination,
    // player1 = -player2 score
    private int getBoardScore(Connect4Board board, int p) {
        int s = 0;
        for (Connect4Board.Line l : board.getLines()) {
            int s1 = l.value();
            if (s1 == -4 || s1 == +4) {
                return p * s1 * WIN_SCORE/4;
            }
            s += s1;
        }
        return p * s;
    }
    
    // Increase max depth heuristic when game advances
    private void setOptimalMaxDepth(Connect4Board board) {
        int n = 0;
        for (int i=0;i<Connect4Board.COLS;i++) if (board.getColPieces(i)>=Connect4Board.ROWS) n++;
        maxDepth = initialMaxDepth;
        switch (n) {
        case 0: 
        case 1: 
            if (board.getTotPieces()>16) maxDepth += 1;
            break;
        case 2: 
            maxDepth += 2; 
            break;
        default: 
            maxDepth = 18; 
        }
        if (maxDepth>Connect4Board.COLS*Connect4Board.ROWS-board.getTotPieces()) maxDepth = Connect4Board.COLS*Connect4Board.ROWS-board.getTotPieces();
    }

} // Connect4AiPlayer
