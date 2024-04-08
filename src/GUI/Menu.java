package GUI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.parser.ParseException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import nikorunnerlib.src.Geometry.Pose2d;
import nikorunnerlib.src.Geometry.Vector2d;

public class Menu {
    static Stage window;

    public static Scene menu;

    public static HBox pathContent;

    static double xOffset;
    static double yOffset;


    // Should redo at some point
    public static void init(Stage primaryStage) {
        window = primaryStage;

        BorderPane root = new BorderPane();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30, 0, 0, 15));
        grid.setPrefSize(600, 40);

        VBox path = new VBox();
        path.setPrefSize(window.getWidth(), 500);


        HBox pathTop = new HBox();
        pathTop.setPadding(new Insets(0, 0, 0, 30));
        pathTop.setAlignment(Pos.CENTER_LEFT); 
        pathTop.setPrefSize(400, 70);
        pathTop.setMaxSize(400, 70);
        pathTop.getStyleClass().addAll("border-color", "border-menuType");
        Label pathLabel = new Label("Paths");
        pathLabel.getStyleClass().addAll("label-menuType");
        Rectangle pathSpacer = new Rectangle(225, 40);
        pathSpacer.setFill(Color.TRANSPARENT);
        Button addPath = new Button("+");
        addPath.setPrefSize(30, 30);
        addPath.getStyleClass().addAll("button-color", "button-menuCreateType");

        pathTop.getChildren().addAll(pathLabel, pathSpacer, addPath);

        
        Line pathSeperator = new Line();
        pathSeperator.setStrokeWidth(4);
        pathSeperator.setStartX(40);
        pathSeperator.setEndX(window.getWidth() - 300);
        pathSeperator.setTranslateY(10); // MIGHT CAUSE ISSUES 
        pathSeperator.setStroke(Color.rgb(20, 20, 20));

        ScrollPane paths = new ScrollPane();
        paths.minWidthProperty().bind(window.widthProperty());
        paths.setFitToWidth(true);
        paths.setPrefHeight(300);
        pathContent = new HBox();
        pathContent.setAlignment(Pos.CENTER_LEFT);  
        pathContent.setPadding(new Insets(20, 0, 0, 10));
        pathContent.setSpacing(10);
        pathContent.setPrefSize(window.getWidth(), 250);

        addPath.setOnAction(e -> {
            String name = "New_Path.json";

            String add = "New_";

            while(new File(Constants.pathDir + name).isFile()) {
                name = add + name;
            }

            File newPath = new File(Constants.pathDir + name);

            try {
                if(newPath.createNewFile()) {

                    updatePathingGUI();

                    ArrayList<Spline> newPathSpline = new ArrayList<>();
                    newPathSpline.add(
                        // Default
                        new Spline(
                            new Pose2d(-12, -18, Math.toRadians(0)),
                            new Vector2d(10, Math.toRadians(0)),
                            new Vector2d(10, Math.toRadians(-180)),
                            new Pose2d(12, 18, Math.toRadians(0))
                        ));

                    PathingJson.convertPathToJson(newPathSpline, newPath);
                } else {
                    System.out.println("path already exists");
                }
            } catch (IOException e1) {
                System.out.println("Unexpected Error - addPath button");
                e1.printStackTrace();
            }
        });

        
        updatePathingGUI();
        
        paths.setContent(pathContent);
        path.getChildren().addAll(pathTop, pathSeperator, paths);



        grid.add(path, 0, 0);

        
        VBox topMenu = General.createMenu(window);

        root.setTop(topMenu);
        root.setCenter(grid);
        menu = new Scene(root, window.getWidth(), window.getHeight());

        menu.getStylesheets().addAll("GUI/CSS/Menu.css");
    }


    static String oldName = null;


    // TODO After file is opened, it cannot be renamed nor deleted?
    public static void updatePathingGUI() {
        pathContent.getChildren().clear();

        for(int i = 0; i < getFileCount(Constants.pathDir); i++) {

            File f = new File(Constants.pathDir);
            File[] pathFiles = f.listFiles();

            BorderPane pathBox = new BorderPane();
            pathBox.setPrefSize(310, 200);
            pathBox.setMinSize(310, 200);
            pathBox.getStyleClass().addAll("border-type", "border-color");

            HBox topPanel = new HBox();
            topPanel.setAlignment(Pos.CENTER_LEFT);
            topPanel.setPadding(new Insets(15, 0, 0, 15));
            topPanel.setSpacing(10);
            topPanel.setPrefSize(300, 50);

            TextField pathName = new TextField(pathFiles[i].getName().replace(".json", "").replace("_", " "));
            pathName.setMinSize(215, 40);
            pathName.getStyleClass().addAll("label-type");

            MenuButton pathMenu = new MenuButton();
            pathMenu.setPrefSize(20, 40);
            pathMenu.getStyleClass().addAll("button-menuType");
            MenuItem deletePath = new MenuItem("Delete");
            deletePath.getStyleClass().addAll("button-pathingMenuOption"); 
            pathMenu.getItems().addAll(deletePath);



            Button openPath = new Button("Open");
            openPath.getStyleClass().addAll("button-color", "button-openType");

            topPanel.getChildren().addAll(pathName, pathMenu);
            pathBox.setTop(topPanel);
            pathBox.setCenter(openPath);

            pathContent.getChildren().add(pathBox);


            pathName.textProperty().addListener((observable, oldValue, newValue) -> {
                if(oldName == null) {
                    if(newValue != oldValue) {
                        oldName = oldValue.replace(" ", "_");
                    }
                }
            });


            pathName.setOnKeyPressed(e -> {
                if(e.getCode() == KeyCode.ENTER) {
                    if(pathName.getText() != "" && pathName.getText() != null && !new File(Constants.pathDir + pathName.getText().replace(" ", "_") + ".json").isFile()) {
                        File file = new File(Constants.pathDir + oldName + ".json");
                        file.renameTo(new File(Constants.pathDir + pathName.getText().replace(" ", "_") + ".json"));
                        oldName = null;
                        pathName.getParent().requestFocus();
                    }
                } else if (e.getCode() == KeyCode.ESCAPE) {
                    if(oldName != null) {
                        pathName.setText(oldName);
                        oldName = null;
                    }

                    pathName.getParent().requestFocus();
                }
            });

            openPath.setOnAction(e -> {
                try {
                    PathEditor.init(window, new File(Constants.pathDir + pathName.getText().replace(" ", "_") + ".json"));
                    window.setScene(PathEditor.editor);
                } catch (IOException | ParseException e1) {
                    System.out.println("error opening file");
                    e1.printStackTrace();
                }
            });


            deletePath.setOnAction(e -> {
                File file = new File(Constants.pathDir + pathName.getText().replace(" ", "_") + ".json");
                file.delete();
                updatePathingGUI();
            });
        }
    }


    public static int getFileCount(String dirPath) {
        int count = 0;

        File f = new File(dirPath);
        File[] files = f.listFiles();

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                count++;
            }
        }

        return count;
    }
}
