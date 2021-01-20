package connect4;
// The javafx gui

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;


public class Connect4Frame extends Parent {

    private final int DISC_SIZE = 80;
    
    private Pane discRoot;
    private Text statusText1;
    private Text statusText2;

    private Connect4Game game;

    Connect4Frame() {

        super();
        
        Pane gamePane = new Pane();
        discRoot = new Pane();
        gamePane.getChildren().add(discRoot);
        gamePane.getChildren().add(makeGrid());
        gamePane.getChildren().addAll(makeColumns());
        
        GridPane grid = new GridPane();
        grid.setMinSize(125, 125);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10));
        grid.add(gamePane, 0, 0);

        Button b0 = new Button("Human vs Computer");
        b0.setOnAction( (e) -> newGame(false,true) );
        Button b1 = new Button("Human vs Human");
        b1.setOnAction( (e) -> newGame(false,false) );
        Button b2 = new Button("Computer vs Computer");
        b2.setOnAction( (e) -> newGame(true,true) );
        Button b3 = new Button("Undo");
        b3.setOnAction( (e) -> { if (game!=null) game.undo(); } );
        statusText1 = new Text();
        statusText2 = new Text();
        VBox v = new VBox(b0,b1,b2,b3,statusText1,statusText2);
        VBox.setMargin(b3, new Insets(10, 0, 0, 0));
        grid.add(v, 1, 0);
        getChildren().add(grid);
        
    }

    private Shape makeGrid() {
        Shape shape = new Rectangle((Connect4Board.COLS + 1) * DISC_SIZE, (Connect4Board.ROWS + 1) * DISC_SIZE);
        for (int y = 0; y < Connect4Board.ROWS; y++) {
            for (int x = 0; x < Connect4Board.COLS; x++) {
                Circle circle = new Circle(DISC_SIZE / 2);
                circle.setCenterX(DISC_SIZE / 2);
                circle.setCenterY(DISC_SIZE / 2);
                circle.setTranslateX(x * (DISC_SIZE + 5) + DISC_SIZE / 4);
                circle.setTranslateY(y * (DISC_SIZE + 5) + DISC_SIZE / 4);
                shape = Shape.subtract(shape, circle);
            }
        }
        shape.setFill(Color.DARKBLUE);
        return shape;
    }

    private List<Rectangle> makeColumns() {
        List<Rectangle> list = new ArrayList<>();
        for (int x = 0; x < Connect4Board.COLS; x++) {
            Rectangle rect = new Rectangle(DISC_SIZE, (Connect4Board.ROWS + 1) * DISC_SIZE);
            rect.setTranslateX(x * (DISC_SIZE + 5) + DISC_SIZE / 4);
            rect.setFill(Color.TRANSPARENT);
            rect.setOnMouseEntered(e -> rect.setFill(Color.rgb(200, 200, 200, 0.2)));
            rect.setOnMouseExited(e -> rect.setFill(Color.TRANSPARENT));
            final int column = x;
            rect.setOnMouseClicked(e -> humanMove(column));
            list.add(rect);
        }
        return list;
    }

    private void newGame(boolean c1, boolean c2) {
        discRoot.getChildren().clear();
        game = new Connect4Game(c1, c2);
        game.registerBoardUpdateListener((Connect4Board.Piece p, boolean animated, boolean marker, int column,
                int row) -> placeDisc(p, animated, marker, column, row));
        game.registerStatusUpdateListener((String s) -> statusText2.setText(s));
        if (c1 && c2) computerMove();
    }

    private void humanMove(int col) {
        if (game != null) {
            Connect4Player player = game.getPlayer();
            if (!game.isOver() && !player.isComputer()) {
                if (player.doMove(col)) {
                    game.nextPlayer();
                    if (game.getPlayer().isComputer()) {
                        // Computer move in 1 second to complete human move animation
                        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), (e) -> computerMove()));
                        timer.play();
                    }
                }
            }
        }
    }

    private void computerMove() {
        Connect4Player player = game.getPlayer();
        if (!game.isOver() && player.isComputer()) {
            if (player.calcMove()) {
                game.nextPlayer();
                if (game.getPlayer().isComputer()) {
                    // Another Computer move in 1 second
                    Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), (e) -> computerMove()));
                    timer.play();
                }
            }
        }
    }

    private void placeDisc(Connect4Board.Piece p, boolean animated, boolean marked, int column, int row) {  
        Circle disc = new Circle(DISC_SIZE / (marked?4:2), p.getColor());
        disc.setCenterX(DISC_SIZE / 2);
        disc.setCenterY(DISC_SIZE / 2);
        discRoot.getChildren().add(disc);
        disc.setTranslateX(column * (DISC_SIZE + 5) + DISC_SIZE / 4);
        if (marked || !animated) {
            disc.setTranslateY((Connect4Board.ROWS-row-1) * (DISC_SIZE + 5) + DISC_SIZE / 4);            
        }
        else { // Animate drop
            TranslateTransition animation = new TranslateTransition(Duration.seconds(0.6), disc);
            animation.setToY((Connect4Board.ROWS-row-1) * (DISC_SIZE + 5) + DISC_SIZE / 4);
            animation.play();
        }
    }
    
}
