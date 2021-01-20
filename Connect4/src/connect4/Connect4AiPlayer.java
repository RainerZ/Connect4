package connect4;
// A player with minmax ai algorithm


public class Connect4AiPlayer extends Connect4Player {

 
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
    public boolean calcMove() {
        System.out.println(name+" is thinking (depth="+maxDepth+") ...");
        if (board.move(piece, minmax(pieceValue, 0, -1000000, +1000000))) {
            setOptimalMaxDepth();
            return true;   
        }
        return false;
    }

    // Minmax algo with alpha/beta pruning (thanks c't)
    private int minmax(int p, int depth, int alpha, int beta) {

        int s = getBoardScore(p);
        if (depth >= maxDepth || board.totPieces >= Connect4Board.ROWS * Connect4Board.COLS || s == +board.WIN_SCORE
                || s == -board.WIN_SCORE)
            return s; // depth or game over

        int s_max = -1000000;
        int c_max = -1;
        for (int i = 0; i < Connect4Board.COLS; i++) {
            int c = colOrder[i];
            if (board.colPieces[c] < Connect4Board.ROWS) {

                board.board[c][board.colPieces[c]++] = p;
                board.totPieces++;
                
                s = -minmax(-p, depth + 1, -beta, -alpha);

                board.board[c][--board.colPieces[c]] = 0;
                board.totPieces--;

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
            if (s_max == +board.WIN_SCORE) {
                board.statusUpdate("I will win!");
                System.out.println(name + " is sure he will win");
            } else if (s_max == -board.WIN_SCORE) {
                board.statusUpdate("Uups ...");
                System.out.println(name + " is afraid to loose");
                // In this case create a move which will not loose immediately, human players
                // might make faults
                if (maxDepth != 2) {
                    maxDepth = 2;
                    return minmax(p, 0, -1000000, +1000000);
                }

            }
            return c_max;
        } else {
            return s_max; // Return score on other levels
        }
    }


     // Increase max depth when game advances
    private void setOptimalMaxDepth() {
        int n = 0;
        for (int i=0;i<Connect4Board.COLS;i++) if (board.colPieces[i]>=Connect4Board.ROWS) n++;
        maxDepth = initialMaxDepth;
        switch (n) {
        case 0: 
        case 1: 
            if (board.totPieces>16) maxDepth += 1;
            break;
        case 2: 
            maxDepth += 2; 
            break;
        default: 
            maxDepth = 24; 
        }
        if (maxDepth>Connect4Board.COLS*Connect4Board.ROWS-board.totPieces) maxDepth = Connect4Board.COLS*Connect4Board.ROWS-board.totPieces;
    }
    
    // Get the current board score, -1000 ... +1000 given for a winning combination,
    // player1 = -player2 score
    protected int getBoardScore(int p) {
        int s = 0;
        for (Connect4Board.Line l : board.lines) {
            int s1 = l.count();
            if (s1 == -4 || s1 == +4) {
                return p * s1 * board.WIN_SCORE/4;
            }
            s += s1;
        }
        return p * s;
    }
    
} // Connect4AiPlayer
