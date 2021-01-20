package connect4;
// The application


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
        
    }

    public static void main(String[] args) {
        launch(args); // JavaFX launch ....
    }
}
