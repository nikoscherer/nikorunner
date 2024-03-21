import GUI.*; 

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

        window.setWidth(Constants.defaultSize[0]);
        window.setHeight(Constants.defaultSize[1]);
        window.setResizable(true);

        window.initStyle(StageStyle.UNDECORATED);
        
        Menu.init(window);
        PathEditorOLD.init(window);
        PathEditor.init(window);
        
        // window.setScene(Menu.menu);
        window.setScene(PathEditor.editor);

        /* TESTING */


        window.show();
    }
}