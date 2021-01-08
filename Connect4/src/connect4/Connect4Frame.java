package connect4;



import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import connect4.Connect4Game.Field;




public class Connect4Frame extends Parent {


    private final int TILE_SIZE = 80;
    
   
    
    private Pane discRoot;

    private Connect4Game game;
    
    Text boardStatusText;
    
    Connect4Frame(final Connect4Game game) {

        super();

        this.game = game;
        
        Pane gamePane = new Pane();
        discRoot = new Pane();
        gamePane.getChildren().add(discRoot);

        Shape gridShape = makeGrid();
        gamePane.getChildren().add(gridShape);
        gamePane.getChildren().addAll(makeColumns());
        
        // right
        GridPane grid = new GridPane();
        grid.setMinSize(125, 125);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10));
        grid.add(gamePane, 0, 0);

        // left
        Button b0 = new Button("New Game");
        b0.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                discRoot.getChildren().clear();
                game.newGame();
                printStatus("");
            }
        });
        boardStatusText = new Text();
        boardStatusText.setText("Red Player starts");
        VBox v = new VBox(b0,boardStatusText);
        VBox.setMargin(b0, new Insets(2, 2, 2, 2));
        VBox.setMargin(boardStatusText, new Insets(10, 10, 10, 10));
        grid.add(v, 1, 0);

        getChildren().add(grid);
    }

    private Shape makeGrid() {
        Shape shape = new Rectangle((game.COLS + 1) * TILE_SIZE, (game.ROWS + 1) * TILE_SIZE);

        for (int y = 0; y < game.ROWS; y++) {
            for (int x = 0; x < game.COLS; x++) {
                Circle circle = new Circle(TILE_SIZE / 2);
                circle.setCenterX(TILE_SIZE / 2);
                circle.setCenterY(TILE_SIZE / 2);
                circle.setTranslateX(x * (TILE_SIZE + 5) + TILE_SIZE / 4);
                circle.setTranslateY(y * (TILE_SIZE + 5) + TILE_SIZE / 4);

                shape = Shape.subtract(shape, circle);
            }
        }

        Light.Distant light = new Light.Distant();
        light.setAzimuth(45.0);
        light.setElevation(30.0);

        Lighting lighting = new Lighting();
        lighting.setLight(light);
        lighting.setSurfaceScale(5.0);

        shape.setFill(Color.BLUE);
        shape.setEffect(lighting);

        return shape;
    }

    private List<Rectangle> makeColumns() {
        List<Rectangle> list = new ArrayList<>();

        for (int x = 0; x < game.COLS; x++) {
            Rectangle rect = new Rectangle(TILE_SIZE, (game.ROWS + 1) * TILE_SIZE);
            rect.setTranslateX(x * (TILE_SIZE + 5) + TILE_SIZE / 4);
            rect.setFill(Color.TRANSPARENT);

            rect.setOnMouseEntered(e -> rect.setFill(Color.rgb(200, 200, 50, 0.3)));
            rect.setOnMouseExited(e -> rect.setFill(Color.TRANSPARENT));

            final int column = x;
            rect.setOnMouseClicked(e -> move(column));

            list.add(rect);
        }

        return list;
    }

    
    private void move(int column) {

        if (game.isOver()) {
            printStatus("Game over");
        }
        else {
            int row = game.move(column, game.RED); 
            if (row >= 0) {
                placeDisc(new Disc(true), column, row, true);  
            }
        }
    }

    private void printStatus(String text) {
        boardStatusText.setText(text);
    }

    private void printGameStatus() {
        boardStatusText.setText(game.getStatus());
        
        if (game.isOver()) {
            Connect4Game.Line l = game.getWiningLine();
            if (l!=null) {
                for (Connect4Game.Field f : l.getLine()) {
                  discRoot.getChildren().add(new Marker(f.getCol(), f.getRow()));
                }
            }
        }
    }

    private void placeDisc(Disc disc, int column, int row, boolean playAnimation) {
                
        discRoot.getChildren().add(disc);
        disc.setTranslateX(column * (TILE_SIZE + 5) + TILE_SIZE / 4);

        if (!playAnimation) {
            disc.setTranslateY((game.ROWS-row-1) * (TILE_SIZE + 5) + TILE_SIZE / 4);
        }
        else {
              
            TranslateTransition animation = new TranslateTransition(Duration.seconds(0.3), disc);
            animation.setToY((game.ROWS-row-1) * (TILE_SIZE + 5) + TILE_SIZE / 4);
            
            animation.setOnFinished(e -> {
                printGameStatus();
                if (!game.isOver()) {
                    int c = game.calcBestMove(game.YELLOW);
                    int r = game.move(c, game.YELLOW);
                    if (r >= 0) {
                        placeDisc(new Disc(false), c,r, false);   
                    }
                    printGameStatus();
                }                
            });
            
            animation.play();
        }
        
    }

    private class Disc extends Circle {
        public Disc(boolean red) {
            super(TILE_SIZE / 2, red ? Color.RED : Color.YELLOW);
            setCenterX(TILE_SIZE / 2);
            setCenterY(TILE_SIZE / 2);
        }
    }

    private class Marker extends Circle {
        public Marker( int column, int row) {
            super(TILE_SIZE / 4, Color.BLACK);
            setCenterX(TILE_SIZE / 2);
            setCenterY(TILE_SIZE / 2);
            setTranslateX(column * (TILE_SIZE + 5) + TILE_SIZE / 4);
            setTranslateY((game.ROWS-row-1) * (TILE_SIZE + 5) + TILE_SIZE / 4);
        }
    }

    
}
