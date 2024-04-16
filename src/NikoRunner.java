import GUI.*;
import GUI.NEW.NEWMenu;
import GUI.OpenCVNodes.NodeEditor;
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

        window.initStyle(StageStyle.TRANSPARENT);
        
        Menu.init(window);
        PathEditor.init(window, null);
        NEWPathEditor.init(window, null);
        NodeEditor.init(window);
        NEWMenu.init(window);
        
        // window.setScene(Menu.menu);
        window.setScene(NEWMenu.menu);
        // window.setScene(NEWPathEditor.editor);
        // window.setScene(NodeEditor.nodeEditor);

        window.show();
    }
}