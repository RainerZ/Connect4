package connect4;
// The application

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Connect4 extends Application {

    private final boolean AUTOPLAY = true;
    
    Connect4Game game;
    
    @Override
    public void start(Stage stage) throws Exception {
        
        game = new Connect4Game(); // The pure game logic
        final Connect4Frame frame = new Connect4Frame(game); // The game GUI element for our scene
        stage.setScene(new Scene(frame)); 
        stage.show();
        
        if (AUTOPLAY) {
            Connect4Game game2 = new Connect4Game(); // The pure game logic
            Timeline timer = new Timeline(new KeyFrame(Duration.seconds(2), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (game.isOver()) return;
                    int p = game.getNextPlayer();
                    int c = game.calcBestMove(p);
                    game.move(c, p);
                }
            }));
            timer.setCycleCount(Timeline.INDEFINITE);
            timer.play();
        }
        
    }

    public static void main(String[] args) {
        launch(args); // JavaFX launch ....
    }
}
