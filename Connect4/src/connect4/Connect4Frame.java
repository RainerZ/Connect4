package connect4;

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
    private Connect4Game game;
    private Text statusText1;
    private Text statusText2;
    Timeline timer;
    
    Connect4Frame(final Connect4Game game) {

        super();

        this.game = game;
        
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

        Button b0 = new Button("New Game");
        b0.setMinWidth(100);
        b0.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                discRoot.getChildren().clear();
                game.newGame();
                statusText1.setText("");
                statusText2.setText("");
            }
        });
        Button b1 = new Button("Undo");
        b1.setMinWidth(100);
        final Connect4Frame f = this;
        b1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                game.undo(f);    
            }
        });
        statusText1 = new Text();
        statusText2 = new Text();
        VBox v = new VBox(b0,b1,statusText1,statusText2);
        VBox.setMargin(b0, new Insets(2, 2, 2, 2));
        VBox.setMargin(b1, new Insets(2, 2, 2, 2));
        grid.add(v, 1, 0);

        getChildren().add(grid);

        timer = new Timeline(new KeyFrame(Duration.seconds(0.5), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                computerMove();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.pause();
    }

    private Shape makeGrid() {
        Shape shape = new Rectangle((game.COLS + 1) * DISC_SIZE, (game.ROWS + 1) * DISC_SIZE);
        for (int y = 0; y < game.ROWS; y++) {
            for (int x = 0; x < game.COLS; x++) {
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
        for (int x = 0; x < game.COLS; x++) {
            Rectangle rect = new Rectangle(DISC_SIZE, (game.ROWS + 1) * DISC_SIZE);
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

    
    private void humanMove(int col) {
        if (!game.isOver()) {
            int row = game.move(col, game.RED); 
            if (row >= 0) {
                timer.pause();
                placeDisc(game.RED, false, col, row);  
            }
        }
    }

    private void computerMove() {
        if (!game.isOver() && game.getNextPlayer()==game.YELLOW) {
            int col = game.calcBestMove(game.YELLOW);
            int row = game.move(col, game.YELLOW);
            if (row >= 0) {
                placeDisc(game.YELLOW, false, col, row);
            }
        }
    }

    private void printGameStatus() {
        statusText1.setText(game.getStatusText());
        statusText2.setText("");
        if (game.isOver()) {
            game.markWinningLine(this,true);
        }
    }

    public void placeDisc(int player, boolean marker, int column, int row) {  
        Circle disc = new Circle(DISC_SIZE / (marker?4:2), player==game.EMPTY ? Color.WHITE : player==game.RED ? Color.RED : Color.YELLOW);
        disc.setCenterX(DISC_SIZE / 2);
        disc.setCenterY(DISC_SIZE / 2);
        discRoot.getChildren().add(disc);
        disc.setTranslateX(column * (DISC_SIZE + 5) + DISC_SIZE / 4);
        if (marker) {
            disc.setTranslateY((game.ROWS-row-1) * (DISC_SIZE + 5) + DISC_SIZE / 4);            
        }
        else {
            TranslateTransition animation = new TranslateTransition(Duration.seconds(0.3), disc);
            animation.setToY((game.ROWS-row-1) * (DISC_SIZE + 5) + DISC_SIZE / 4);
            animation.setOnFinished(e -> { printGameStatus(); timer.play(); });
            animation.play();
        }
    }
    
}
