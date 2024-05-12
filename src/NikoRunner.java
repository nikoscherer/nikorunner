import GUI.*;
import GUI.Editors.AutoEditor.AutoEditor;
import GUI.Editors.PathEditor.PathEditor;
import GUI.OpenCVNodes.NodeEditor;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class NikoRunner extends Application {

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

        window.initStyle(StageStyle.TRANSPARENT);

        Menu.init(window);
        PathEditor.init(window, null);
        AutoEditor.init(window, null);
        NodeEditor.init(window);

        window.setScene(Menu.menu);

        window.show();
    }
}