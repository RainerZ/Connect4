package connect4;
// The application


import connect4gui.Connect4Frame;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Connect4 extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
                
        // Create GUI
        final Connect4Frame frame = new Connect4Frame(); 
        stage.setScene(new Scene(frame)); 
        stage.show();     
        System.out.println("Connect 4");
    }

    public static void main(String[] args) {
        launch(args); // JavaFX launch ....
    }
}


/*
YELLOW 2
RED 0
YELLOW 0
RED 1
YELLOW 1
RED 6
YELLOW 6
RED 6
YELLOW 2
RED 0
YELLOW 1
RED 1
YELLOW 4
RED 6
YELLOW 4
RED 1
YELLOW 0
RED 1
YELLOW 4
RED 4
YELLOW 3
RED 4
YELLOW 4
RED 5
YELLOW 3
RED 6
YELLOW 3
RED 3
YELLOW 3
RED 3
*/