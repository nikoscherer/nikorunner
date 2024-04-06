package GUI;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

// for OpenCV?
public class NodeEditor {
    
    static Stage window;

    public static Scene nodeTest;


    static double xOffset;
    static double yOffset;

    public static void init(Stage primaryStage) {
        window = primaryStage;

        BorderPane root = new BorderPane();

        Pane nodePane = new Pane();
        nodePane.setPrefWidth(window.getWidth());
        nodePane.setPrefHeight(window.getHeight());
        nodePane.getStyleClass().addAll("editing");

        Label testLabel1 = new Label("Test Label 1");
        Label testLabel2 = new Label("Test Label 2");
        testLabel1.setTranslateX(100);
        testLabel1.setTranslateX(100);

        testLabel1.getStyleClass().addAll("label-border");
        testLabel2.getStyleClass().addAll("label-border");

        nodePane.getChildren().addAll(testLabel1, testLabel2);


        testLabel1.setOnMousePressed(e -> {
            xOffset = e.getX();
            yOffset = e.getY();
            System.out.println("detected");
        });

        testLabel1.setOnMouseDragged(e -> {
            testLabel1.setTranslateX(testLabel1.getTranslateX() + e.getX() - xOffset);
            testLabel1.setTranslateY(testLabel1.getTranslateY() + e.getY() - yOffset);
        });

        testLabel2.setOnMousePressed(e -> {
            xOffset = e.getX();
            yOffset = e.getY();
            System.out.println("detected");
        });

        testLabel2.setOnMouseDragged(e -> {
            testLabel2.setTranslateX(testLabel2.getTranslateX() + e.getX() - xOffset);
            testLabel2.setTranslateY(testLabel2.getTranslateY() + e.getY() - yOffset);
        });

        root.setTop(General.createMenu(primaryStage));
        root.setCenter(nodePane);
        nodeTest = new Scene(root, window.getWidth(), window.getHeight());
        nodeTest.getStylesheets().addAll("GUI/CSS/NodeTest.css");
    }
}
