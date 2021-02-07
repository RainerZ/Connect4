package connect4game;

import java.util.Optional;

// A player with minmax ai algorithm
public class Connect4AiPlayer extends Connect4Player {

    { System.out.println("new Connect4AiPlayer()"); }

    private final static int WIN_SCORE  = 1000;  // Score (Stellungsbewertung)
    private final static int[] colOrder = { 3, 4, 2, 1, 5, 0, 6 }; // Column priority (helps alpha/beta)

    private int maxDepth; // max search depth
    private final int initialMaxDepth;
    private final int pieceValue;
    private final Connect4Game game;
    //private final Connect4Board board;

    Connect4AiPlayer(Connect4Game game, Connect4Board.Piece p, String name, int maxDepth) {
        super(p,name);
        this.game = game;
        //this.board = board;
        this.initialMaxDepth = this.maxDepth = maxDepth;
        this.pieceValue = piece.getFieldValue();
    }

    @Override
    boolean isComputer() {
        return true;
    }
    
    @Override
    Optional<Integer> computeMove(Connect4Board board) {
        setOptimalMaxDepth(board);
        System.out.println(name+" is thinking (depth="+maxDepth+",lines="+board.getLineCount()+",pieces="+board.getTotPieces()+") ...");
        int col = minmax(board, pieceValue, 0, -1000000, +1000000);
        if (col>=0) return Optional.of(col);
        else return Optional.empty();
    }

    // Minmax algo with alpha/beta pruning (thanks c't)
    private int minmax(Connect4Board board, int p, int depth, int alpha, int beta) {

        int s = getBoardScore(board,p);
        if (board.getTotPieces() >= Connect4Game.ROWS * Connect4Game.COLS) {
            assert(depth!=0);
            if (depth==0) throw new IllegalArgumentException();
            return s;
        }
        if (depth >= maxDepth || s == +WIN_SCORE || s == -WIN_SCORE) return s; // max depth reached or won

        int s_max = -1000000;
        int c_max = -1;
        for (int i = 0; i < Connect4Game.COLS; i++) {
            int c = colOrder[i];
            if (board.getColPieces(c) < Connect4Game.ROWS) {
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
                game.statusUpdate(name+" will win!");
            } 
            else if (s_max == -WIN_SCORE) {
                game.statusUpdate(name+" may loose");
                // In this case create a move which will not loose immediately, human players
                // might make faults
                if (maxDepth != 2) {
                    maxDepth = 2;
                    return minmax(board, p, 0, -1000000, +1000000);
                }

            }
            else {
              game.statusUpdate(c_max+"/"+s_max);
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
        for (int i=0;i<Connect4Game.COLS;i++) if (board.getColPieces(i)>=Connect4Game.ROWS) n++;
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
        if (maxDepth>Connect4Game.COLS*Connect4Game.ROWS-board.getTotPieces()) maxDepth = Connect4Game.COLS*Connect4Game.ROWS-board.getTotPieces();
    }

} // Connect4AiPlayer
