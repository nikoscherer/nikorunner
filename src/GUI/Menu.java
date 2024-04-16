package GUI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import nikorunnerlib.src.Geometry.Pose2d;
import nikorunnerlib.src.Geometry.Vector2d;

public class Menu {
    static Stage window;

    public static Scene menu;

    static int borderWidth = 5;

    static boolean windowScaling = false;
    static int location = 0;



    static GridPane paths;

    public static void init(Stage primaryStage) {
        window = primaryStage;

        BorderPane root = new BorderPane();
        root.setPrefWidth(window.getWidth());
        root.setPrefHeight(window.getHeight());

        HBox menuBar = WindowsMenu.createMenu(window);



        HBox editor = new HBox();
        editor.setPadding(new Insets(10, 10, 10, 10));
        editor.setSpacing(20);


        // PATH
        VBox pathEditor = new VBox();

        // TODO make it seperately scalable
        pathEditor.setPrefWidth(window.getWidth() / 2);
        VBox.setVgrow(pathEditor, Priority.ALWAYS);
        pathEditor.getStyleClass().addAll("editor");

        HBox pathEditorTopBar = new HBox();
        pathEditorTopBar.setPadding(new Insets(15, 17, 0, 20));
        pathEditorTopBar.setAlignment(Pos.CENTER_LEFT);
        Label pathEditorLabel = new Label("Paths");
        Region pathEditorSpacer = new Region();
        HBox.setHgrow(pathEditorSpacer, Priority.ALWAYS);

        // TODO change to image
        Button newPath = new Button("+");
        newPath.setPrefSize(45, 45);
        newPath.getStyleClass().addAll("pathing-new");

        
        paths = new GridPane();
        paths.setPrefWidth(pathEditor.getPrefWidth());
        paths.setVgap(10);
        paths.setHgap(10);
        paths.setPadding(new Insets(10, 10, 10, 10));

        pathEditorTopBar.getChildren().addAll(pathEditorLabel, pathEditorSpacer, newPath);
        pathEditor.getChildren().addAll(pathEditorTopBar, paths);


        // AUTO
        VBox autoEditor = new VBox();
        autoEditor.setPrefWidth(window.getWidth() / 2);
        VBox.setVgrow(autoEditor, Priority.ALWAYS);
        autoEditor.getStyleClass().addAll("editor");

        Label autoEditorLabel = new Label("Autos");
        autoEditorLabel.setPadding(new Insets(5, 0, 0, 10));
        
        autoEditor.getChildren().addAll(autoEditorLabel);


        editor.getChildren().addAll(pathEditor, autoEditor);

        updateGUI();

        root.setTop(menuBar);
        root.setCenter(editor);
        menu = new Scene(root, window.getWidth(), window.getHeight());
        menu.setFill(Color.TRANSPARENT);
        menu.getStylesheets().addAll("GUI/CSS/Menu.css");



        root.widthProperty().addListener(e -> {
            pathEditor.setPrefWidth(window.getWidth() / 2);
            pathEditor.setPrefHeight(window.getHeight() / 2);
            paths.setPrefWidth(pathEditor.getPrefWidth());

            autoEditor.setPrefWidth(window.getWidth() / 2);
            autoEditor.setPrefHeight(window.getHeight() / 2);

            updateGUI();
        });


        // Used in old GUI, could cause issues
        newPath.setOnAction(e -> {
            String name = "New_Path.json";

            String add = "New_";

            while(new File(Constants.pathDir + name).isFile()) {
                name = add + name;
            }

            File newPathFile = new File(Constants.pathDir + name);

            try {
                if(newPathFile.createNewFile()) {

                    updateGUI();

                    ArrayList<Spline> newPathSpline = new ArrayList<>();
                    newPathSpline.add(
                        // Default
                        new Spline(
                            new Pose2d(-12, -18, Math.toRadians(0)),
                            new Vector2d(10, Math.toRadians(0)),
                            new Vector2d(10, Math.toRadians(-180)),
                            new Pose2d(12, 18, Math.toRadians(0))
                        ));

                    PathingJson.convertPathToJson(newPathSpline, newPathFile);
                } else {
                    System.out.println("path already exists");
                }
            } catch (IOException e1) {
                System.out.println("Unexpected Error - addPath button");
                e1.printStackTrace();
            }
        });


        // TODO change cursor when hovering
        // TODO move to window bar, so it can be used everywhere
        root.setOnMousePressed(e -> {
            // if at edge
            if (e.getSceneX() <= borderWidth || e.getSceneY() <= borderWidth
                    || e.getSceneX() >= window.getWidth() - borderWidth
                    || e.getSceneY() >= window.getHeight() - borderWidth) {
                windowScaling = true;
                if (e.getSceneX() <= borderWidth && e.getSceneY() <= borderWidth) { // grabbed top left
                    menu.setCursor(Cursor.NW_RESIZE);
                    location = 0;
                } else if (e.getSceneX() >= window.getWidth() - borderWidth && e.getSceneY() <= borderWidth) { // grabbed top right
                    menu.setCursor(Cursor.NE_RESIZE);
                    location = 2;
                } else if (e.getSceneX() >= window.getWidth() - borderWidth
                        && e.getSceneY() >= window.getHeight() - borderWidth) { // grabbed bottom right
                    menu.setCursor(Cursor.SE_RESIZE);
                    location = 4;
                } else if (e.getSceneX() <= borderWidth && e.getSceneY() >= window.getHeight() - borderWidth) { // grabbed bottom left
                    menu.setCursor(Cursor.SW_RESIZE);
                    location = 6;
                } else if (e.getSceneY() <= borderWidth) { // grabbed top
                    menu.setCursor(Cursor.N_RESIZE);
                    location = 1;
                } else if (e.getSceneX() >= window.getWidth() - borderWidth) { // grabbed right
                    menu.setCursor(Cursor.E_RESIZE);
                    location = 3;
                } else if (e.getSceneY() >= window.getHeight() - borderWidth) { // grabbed bottom
                    menu.setCursor(Cursor.S_RESIZE);
                    location = 5;
                } else if (e.getSceneX() <= borderWidth) { // grabbed left
                    menu.setCursor(Cursor.W_RESIZE);
                    location = 7;
                }
            }
        });

        root.setOnMouseReleased(e -> {
            // set bool to false
            menu.setCursor(Cursor.DEFAULT);
            windowScaling = false;
        });

        root.setOnMouseDragged(e -> {
            // if edge grabbed, set bool to true
            if (windowScaling) {
                if (location == 0) { // top left
                    window.setHeight(window.getHeight() - (e.getScreenY() - window.getY()));
                    window.setY(e.getScreenY());
                } else if (location == 1) { // top

                } else if (location == 2) { // top right

                } else if (location == 3) { // right
                    window.setWidth(window.getWidth() + (e.getSceneX() - window.getWidth()));
                } else if (location == 4) { // bottom right
                    window.setWidth(window.getWidth() + (e.getSceneX() - window.getWidth()));
                    window.setHeight(window.getHeight() + (e.getSceneY() - window.getHeight()));
                } else if (location == 5) { // bottom
                    window.setHeight(window.getHeight() + (e.getSceneY() - window.getHeight()));
                } else if (location == 6) { // bottom left
                    window.setWidth(window.getWidth() - (e.getScreenX() - window.getX()));
                    window.setX(e.getScreenX());
                    window.setHeight(window.getHeight() + (e.getSceneY() - window.getHeight()));
                } else if (location == 7) { // left
                    window.setWidth(window.getWidth() - (e.getScreenX() - window.getX()));
                    window.setX(e.getScreenX());
                }

                if (window.getWidth() < 200) {
                    window.setWidth(200);
                }
                if (window.getHeight() < 150) {
                    window.setHeight(150);
                }
            }
        });
    }


    static int nodeWidth = 200;
    static int nodeHeight = 150;

    static void updateGUI() {
        paths.getChildren().clear();

        File[] pathFiles = new File(Constants.pathDir).listFiles();

        int countPerRow = (int) (paths.getPrefWidth() / (nodeWidth + 20)) - 1;


        int column = 0;
        int row = 0;

        for(int i = 0; i < pathFiles.length; i++) {
            
            paths.add(createPath(pathFiles[i]), row, column);
            row++;
            if(row > countPerRow) {
                column++;
                row = 0;
            }
        }
    }

    static String oldName = null;

    static VBox createPath(File pathFile) {
        VBox root = new VBox();
        root.getStyleClass().addAll("pathing-box");
        root.setAlignment(Pos.TOP_CENTER);

        root.setPrefSize(nodeWidth, nodeHeight);

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(5, 5, 0, 5));
        topBar.setAlignment(Pos.CENTER_LEFT);

        TextField pathName = new TextField(pathFile.getName().replace("_", " ").replace(".json", ""));
        pathName.setAlignment(Pos.CENTER);
        pathName.setPadding(new Insets(5, 20, 0, 20));
        pathName.getStyleClass().addAll("pathing-text");

        Image pathTrashImage = new Image("imgs/trash.png");
        ImageView pathTrashImageView = new ImageView(pathTrashImage);
        pathTrashImageView.setFitWidth(15);
        pathTrashImageView.setFitHeight(16);
        Button pathTrash = new Button();
        pathTrash.setGraphic(pathTrashImageView);
        pathTrash.getStyleClass().addAll("pathing-trash");

        topBar.getChildren().addAll(pathName, pathTrash);


        StackPane openPathPane = new StackPane();
        openPathPane.setPadding(new Insets(10, 10, 10, 10));
        VBox.setVgrow(openPathPane, Priority.ALWAYS);
        openPathPane.setAlignment(Pos.CENTER);
        // TODO change to image
        Button pathOpen = new Button("Open");
        pathOpen.setPrefSize(nodeWidth, nodeHeight - 20);
        pathOpen.getStyleClass().addAll("pathing-open");


        openPathPane.getChildren().addAll(pathOpen);

        root.getChildren().addAll(topBar, openPathPane);

        pathName.textProperty().addListener((obs, oldValue, newValue) -> {
            if(oldName == null) {
                if(newValue != oldValue) {
                    oldName = oldValue.replace(" ", "_") + ".json";
                }
            }
        });

        pathName.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER) {
                File oldFile = new File(Constants.pathDir + oldName);
                if(!new File(Constants.pathDir + pathName.getText().replace(" ", "_").replace(".json", "") + ".json").isFile()) {
                    oldFile.renameTo(new File(Constants.pathDir + pathName.getText().replace(" ", "_").replace(".json", "") + ".json"));
                    oldName = null;
                    pathName.getParent().requestFocus();
                } else {
                    System.out.println("file already exists");
                }
            } else if (e.getCode() == KeyCode.ESCAPE) {
                pathName.setText(oldName.replace("_", " ").replace(".json", ""));
                oldName = null;
                pathName.getParent().requestFocus();
            }
        });


        pathOpen.setOnAction(e -> {
            PathEditor.init(window, new File(Constants.pathDir + pathName.getText().replace(" ", "_") + ".json"));
            window.setScene(PathEditor.editor);
        });


        pathTrash.setOnAction(e -> {
            File targetFile = new File(Constants.pathDir + pathName.getText().replace(" ", "_") + ".json");
            targetFile.delete();

            updateGUI();
        });


        return root;
    }
}