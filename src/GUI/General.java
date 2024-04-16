package GUI;

import java.io.File;
import java.io.IOException;

import org.json.simple.parser.ParseException;

import GUI.OpenCVNodes.NodeEditor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class General {

    static Stage window;

    public static double xOffset = 0;
    public static double yOffset = 0;

    public static boolean windowMaximized = false;
    
    public static VBox createMenu(Stage primaryStage) {
        window = primaryStage;

        VBox root = new VBox();

        HBox menu = new HBox();

        menu.setAlignment(Pos.CENTER_LEFT);
        menu.setPrefSize(window.getWidth(), 60);
        menu.setPadding(new Insets(10, 25, 10, 25));

        menu.setSpacing(0);
        menu.getStyleClass().add("root");

        Button menuButton = new Button("Menu");
        
        menuButton.setPrefSize(100, 25);
        menuButton.getStyleClass().addAll("button-color", "button-menu");


        menu.setPadding(new Insets(8, 8, 8, 8));

        Rectangle labelSeperator = new Rectangle(10, 20);
        labelSeperator.setFill(Color.TRANSPARENT);

        Label menuLabel = new Label("NikoRunner");


        // TODO fix all of this, and redo menus
        Button nodeEditorButton = new Button("Node Editor (FOR NOW)");
        nodeEditorButton.setPrefWidth(400);
        nodeEditorButton.setMaxWidth(400);
        nodeEditorButton.setPadding(new Insets(0, 0, 0, 20));

        nodeEditorButton.setOnAction(e -> {
            window.setScene(NodeEditor.nodeEditor);
        });

        nodeEditorButton.getStyleClass().addAll("button-menu", "button-color");



        // Normal Width 1010
        Rectangle seperator = new Rectangle(200, 20);
        // seperator.widthProperty().bind(window.widthProperty().subtract(390));
        seperator.setFill(Color.TRANSPARENT);
        
        // - - - - - - - - - - - - - - - - - - -


        int appSize = 40;

        Image minimizeImage = new Image("imgs/minimize.png");
        ImageView minimizeImageView = new ImageView(minimizeImage);
        minimizeImageView.setFitHeight(15);
        minimizeImageView.setFitWidth(15);
        Button minimizeApplicationButton = new Button();
        minimizeApplicationButton.setPrefSize(appSize, appSize);
        minimizeApplicationButton.setMinSize(appSize, appSize);
        minimizeApplicationButton.setMaxSize(appSize, appSize);

        minimizeApplicationButton.setGraphic(minimizeImageView);
        minimizeApplicationButton.getStyleClass().add("button-sizeApp");

        Image maximizeImage = new Image("imgs/maximize.png");
        ImageView maximizeImageView = new ImageView(maximizeImage);
        maximizeImageView.setFitHeight(15);
        maximizeImageView.setFitWidth(15);
        Button maximizeApplicationButton = new Button();
        maximizeApplicationButton.setPrefSize(appSize, appSize);
        maximizeApplicationButton.setMinSize(appSize, appSize);
        maximizeApplicationButton.setMaxSize(appSize, appSize);

        maximizeApplicationButton.setGraphic(maximizeImageView);
        maximizeApplicationButton.getStyleClass().add("button-sizeApp");
        
        Button closeApplicationButton = new Button("X");
        closeApplicationButton.setPrefSize(appSize, appSize);
        closeApplicationButton.setMinSize(appSize, appSize);
        closeApplicationButton.setMaxSize(appSize, appSize);
        closeApplicationButton.getStyleClass().add("button-closeApp");


        menuButton.setOnAction(e -> {
            if(window.getScene() == Menu.menu) {

            } else {
                window.setScene(Menu.menu);
                Menu.updatePathingGUI();
            }
        });


        minimizeApplicationButton.setOnAction(e -> {
            window.setIconified(true);
        });

        maximizeApplicationButton.setOnAction(e -> {
            if(windowMaximized) {
                window.setMaximized(false);
                windowMaximized = false;
            } else {
                window.setMaximized(true);
                windowMaximized = true;
            }
        });

        closeApplicationButton.setOnAction(e -> {
            window.close();
        });


        menu.setOnMousePressed(e -> {
            xOffset = window.getX() - e.getScreenX();
            yOffset = window.getY() - e.getScreenY();
        });

        menu.setOnMouseDragged(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                window.setX(e.getScreenX() + xOffset);
                window.setY(e.getScreenY() + yOffset);
            }
        });



        Line menuLine = new Line();
        menuLine.setStrokeWidth(1);
        menuLine.setStartX(0);
        menuLine.setStartY(60);
        menuLine.setEndX(window.getWidth());
        menuLine.setEndY(60);
        menuLine.endXProperty().bind(window.widthProperty());
        menuLine.getStyleClass().addAll("line-menu");


        HBox.setHgrow(menu, Priority.ALWAYS);
        HBox.setHgrow(seperator, Priority.ALWAYS);

        menu.getChildren().addAll(menuButton, labelSeperator, menuLabel, nodeEditorButton, seperator, minimizeApplicationButton, maximizeApplicationButton, closeApplicationButton);
        root.getChildren().addAll(menu, menuLine);

        menu.getStylesheets().addAll("GUI/CSS/WindowMenu.css");

        return root;
    }
}
