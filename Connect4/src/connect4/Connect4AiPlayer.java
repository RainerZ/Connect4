package connect4;
// A player with minmax ai algorithm


public class Connect4AiPlayer extends Connect4Player {

    private final int WIN_SCORE  = 1000;  // Score (Stellungsbewertung)

    private final int[] colOrder = { 3, 4, 2, 1, 5, 0, 6 }; // Column priority (helps alpha/beta)

    private int maxDepth; // max search depth
    private final int initialMaxDepth;
    private final int pieceValue;
    
    Connect4AiPlayer(Connect4Board board, Connect4Board.Piece p, String name, int maxDepth) {
        super(board,p,name);
        this.initialMaxDepth = this.maxDepth = maxDepth;
        pieceValue = piece.getFieldValue();
    }

    @Override
    public boolean isComputer() {
        return true;
    }

    @Override
    public boolean doMove() {
        System.out.println(name+" is thinking (depth="+maxDepth+") ...");
        if (board.move(piece, minmax(pieceValue, 0, -1000000, +1000000))) {
            setOptimalMaxDepth();
            return true;   
        }
        return false;
    }

    // Minmax algo with alpha/beta pruning (thanks c't)
    private int minmax(int p, int depth, int alpha, int beta) {

        int s = getScore(p);
        if (depth >= maxDepth) return s;
        if (board.totPieces >= Connect4Board.ROWS * Connect4Board.COLS || s == +WIN_SCORE || s == -WIN_SCORE)
            return s; // Game over
        int s_max = -1000000;
        int c_max = -1;
        for (int i = 0; i < Connect4Board.COLS; i++) {
            int c = colOrder[i];
            if (board.colPieces[c] < Connect4Board.ROWS) {
                board.put(c, p);
                s = -minmax(-p, depth + 1, -beta, -alpha);
                if (s > s_max) {
                    s_max = s;
                    c_max = c;
                }
                if (s > alpha) {
                    alpha = s;
                    if (alpha > beta && depth > 0) {
                        board.remove(c);
                        break;
                    }
                }
                board.remove(c);
            }
        }
        if (depth == 0) { // Return best move for actual board and player on level 0
            if (s_max == +WIN_SCORE) {
                board.statusUpdate("I will win!");
                System.out.println(name + " is sure he will win");
            }
            else if (s_max == -WIN_SCORE) {
                board.statusUpdate("Uups ...");
                System.out.println(name + " is afraid to loose");
            }
            return c_max;
        }
        return s_max; // Return score on other levels
    }

    // Get the current board score, -1000 ... +1000 given for a winning combination,
    // player1 = -player2 score
    private int getScore(int p) {
        int s = 0;
        for (Connect4Board.Line l : board.lines) {
            int s1 = l.count();
            if (s1 == -4 || s1 == +4) {
                return p * s1 * WIN_SCORE/4;
            }
            s += s1;
        }
        return p * s;
    }

    // Increase max depth when game advances
    private void setOptimalMaxDepth() {
        if (Connect4Board.ROWS * Connect4Board.COLS - board.totPieces < 42 / 3) {
            maxDepth = initialMaxDepth + 10;
        } else if (Connect4Board.ROWS * Connect4Board.COLS - board.totPieces < 42 / 2) {
            maxDepth = initialMaxDepth + 5;
        } else {
            maxDepth = initialMaxDepth;
        }
    }

} // Connect4AiPlayer
