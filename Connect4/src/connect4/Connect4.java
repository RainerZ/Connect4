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
 Winning combinations:
[3, 3, 4, 5, 3, 3, 3, 3, 1, 2, 2, 2, 2, 2, 4, 5, 4, 4, 4, 1, 0, 5, 5, 1, 5, 4, 5, 2, 0, 0, 1, 1, 1, 0, 0, 0, 6, 6, 6, 6, 6]
[4, 3, 3, 3, 3, 3, 6, 3, 0, 4, 4, 4, 1, 4, 1, 0, 2, 4, 6, 1, 1, 6, 6, 1, 1, 0, 0, 0, 0, 6, 6, 2, 2]


*/