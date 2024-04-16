package GUI.OpenCVNodes;

import java.util.ArrayList;

import GUI.WindowsMenu;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

// for OpenCV?
public class NodeEditor {
    
    static Stage window;

    public static Scene nodeEditor;

    static ArrayList<Node> nodesArray = new ArrayList<>();


    static Pane nodePane = new Pane();

    static double xOffset;
    static double yOffset;

    public static void init(Stage primaryStage) {
        window = primaryStage;

        BorderPane root = new BorderPane();

        StackPane nodes = new StackPane();

        nodePane.setPrefWidth(window.getWidth());
        nodePane.setPrefHeight(window.getHeight());

        
        

        nodes.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.SECONDARY) {
                openMenu(e.getSceneX(), e.getSceneY());
            }
        });


        // Add all nodes to screen
        nodesArray.add(new Node(NodeType.OUTPUT));
        nodePane.getChildren().addAll(nodesArray);

        nodes.getChildren().addAll(nodePane);

        Canvas nodeCanvas = new Canvas(nodePane.getWidth(), nodePane.getHeight());
        GraphicsContext gc = nodeCanvas.getGraphicsContext2D();
        gc.setStroke(Color.rgb(0, 0, 0));
        gc.setLineWidth(3);
        gc.lineTo(100, 100);

        // add canvas to screen
        nodes.getChildren().addAll(nodeCanvas);

        HBox topMenu = WindowsMenu.createMenu(window);
        root.setTop(topMenu);
        root.setCenter(nodes);
        nodeEditor = new Scene(root, window.getWidth(), window.getHeight());
        nodeEditor.getStylesheets().addAll("GUI/CSS/NodeEditor.css");
    }

    // x and y are off
    public static void openMenu(double x, double y) {
        ContextMenu menu = new ContextMenu();
        menu.setPrefWidth(450);
        
        for(int i = 0; i < NodeType.values().length; i++) {
            MenuItem item = new MenuItem(NodeType.values()[i].toString());

            item.setOnAction(e -> {
                Node node = new Node(NodeType.valueOf(item.getText()));
                node.setTranslateX(x);
                node.setTranslateY(y);
                nodesArray.add(node);
                
                update();
            });

            menu.getItems().addAll(item);
        }
        nodePane.setOnMouseClicked(e -> {
            if(e.getButton() == MouseButton.SECONDARY) {
                menu.show(window, e.getScreenX(), e.getScreenY());
            }
        });
    }

    public static void update() {
        nodePane.getChildren().setAll(nodesArray);
    }
}
