package connect4;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Connect4 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        
        final Connect4Game game = new Connect4Game(); // The game logic
        final Connect4Frame frame = new Connect4Frame(game); // The game GUI element
        
        stage.setScene(new Scene(frame)); 
        stage.show();
    }

    public static void main(String[] args) {
        launch(args); // JavaFX launch
    }
}
