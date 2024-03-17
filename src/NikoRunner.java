import GUI.*; 

import javafx.application.Application;
import javafx.stage.Stage;

public class NikoRunner extends Application  {


    public static int[] defaultSize = {1400, 800};

    Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;

        window.setTitle("NikoRunner");


        
        Menu.init(window);
        PathEditor.init(window);
        
        window.setScene(Menu.menu);

        window.show();
    }
}