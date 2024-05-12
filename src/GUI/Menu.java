package GUI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import GUI.Editors.AutoEditor.AutoEditor;
import GUI.Editors.PathEditor.PathEditor;
import GUI.PlaceholderTypes.Command;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

    static GridPane paths;
    static GridPane autos;

    public static void init(Stage primaryStage) {
        window = primaryStage;

        BorderPane root = WindowsMenu.create(window);

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

        HBox autoEditorTopBar = new HBox();
        autoEditorTopBar.setPadding(new Insets(15, 17, 0, 20));
        autoEditorTopBar.setAlignment(Pos.CENTER_LEFT);
        Label autoEditorLabel = new Label("Autos");
        Region autoEditorSpacer = new Region();
        HBox.setHgrow(autoEditorSpacer, Priority.ALWAYS);

        // TODO change to image
        Button newAuto = new Button("+");
        newAuto.setPrefSize(45, 45);
        newAuto.getStyleClass().addAll("pathing-new");

        autos = new GridPane();
        autos.setPrefWidth(pathEditor.getPrefWidth());
        autos.setVgap(10);
        autos.setHgap(10);
        autos.setPadding(new Insets(10, 10, 10, 10));

        autoEditorTopBar.getChildren().addAll(autoEditorLabel, autoEditorSpacer, newAuto);
        autoEditor.getChildren().addAll(autoEditorTopBar, autos);

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

            while (new File(Constants.pathDir + name).isFile()) {
                name = add + name;
            }

            File newPathFile = new File(Constants.pathDir + name);

            try {
                if (newPathFile.createNewFile()) {

                    updateGUI();

                    ArrayList<Spline> newPathSpline = new ArrayList<>();
                    newPathSpline.add(
                            // Default
                            new Spline(
                                    new Pose2d(-12, -18, Math.toRadians(0)),
                                    new Vector2d(10, Math.toRadians(0)),
                                    new Vector2d(10, Math.toRadians(-180)),
                                    new Pose2d(12, 18, Math.toRadians(0))));

                    PathingJson.convertPathToJson(new Path(newPathSpline, new ArrayList<Command>()), newPathFile);
                } else {
                    System.out.println("path already exists");
                }
            } catch (IOException e1) {
                System.out.println("Unexpected Error - addPath button");
                e1.printStackTrace();
            }
        });

        // Used in old GUI, could cause issues
        newAuto.setOnAction(e -> {
            String name = "New_Auto.json";

            String add = "New_";

            while (new File(Constants.autoDir + name).isFile()) {
                name = add + name;
            }

            File newAutoFile = new File(Constants.autoDir + name);

            try {
                if (newAutoFile.createNewFile()) {
                    updateGUI();
                } else {
                    System.out.println("auto already exists");
                }
            } catch (IOException e1) {
                System.out.println("Unexpected Error - addAuto button");
                e1.printStackTrace();
            }
        });
    }

    static int nodeWidth = 200;
    static int nodeHeight = 150;

    static void updateGUI() {
        File pathDirectory = new File(Constants.pathDir);
        if (!pathDirectory.exists()) {
            pathDirectory.mkdir();
        }

        File autoDirectory = new File(Constants.autoDir);
        if(!autoDirectory.exists()) {
            autoDirectory.mkdir();
        }

        // Paths
        paths.getChildren().clear();

        File[] pathFiles = new File(Constants.pathDir).listFiles();

        int countPerRow = (int) (paths.getPrefWidth() / (nodeWidth + 20)) - 1;

        int column = 0;
        int row = 0;

        if (pathFiles != null) {
            for (int i = 0; i < pathFiles.length; i++) {

                paths.add(createPath(pathFiles[i]), row, column);
                row++;
                if (row > countPerRow) {
                    column++;
                    row = 0;
                }
            }
        }

        // Autos
        autos.getChildren().clear();

        pathFiles = new File(Constants.autoDir).listFiles();

        countPerRow = (int) (autos.getPrefWidth() / (nodeWidth + 20)) - 1;

        column = 0;
        row = 0;

        if (pathFiles != null) {
            for (int i = 0; i < pathFiles.length; i++) {

                autos.add(createAuto(pathFiles[i]), row, column);
                row++;
                if (row > countPerRow) {
                    column++;
                    row = 0;
                }
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
            if (oldName == null) {
                if (newValue != oldValue) {
                    oldName = oldValue.replace(" ", "_") + ".json";
                }
            }
        });

        pathName.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                File oldFile = new File(Constants.pathDir + oldName);
                if (!new File(Constants.pathDir + pathName.getText().replace(" ", "_").replace(".json", "") + ".json")
                        .isFile()) {
                    oldFile.renameTo(new File(
                            Constants.pathDir + pathName.getText().replace(" ", "_").replace(".json", "") + ".json"));
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

    static VBox createAuto(File autoFile) {
        VBox root = new VBox();
        root.getStyleClass().addAll("pathing-box");
        root.setAlignment(Pos.TOP_CENTER);

        root.setPrefSize(nodeWidth, nodeHeight);

        HBox topBar = new HBox();
        topBar.setPadding(new Insets(5, 5, 0, 5));
        topBar.setAlignment(Pos.CENTER_LEFT);

        TextField autoName = new TextField(autoFile.getName().replace("_", " ").replace(".json", ""));
        autoName.setAlignment(Pos.CENTER);
        autoName.setPadding(new Insets(5, 20, 0, 20));
        autoName.getStyleClass().addAll("pathing-text");

        Image autoTrashImage = new Image("imgs/trash.png");
        ImageView autoTrashImageView = new ImageView(autoTrashImage);
        autoTrashImageView.setFitWidth(15);
        autoTrashImageView.setFitHeight(16);
        Button autoTrash = new Button();
        autoTrash.setGraphic(autoTrashImageView);
        autoTrash.getStyleClass().addAll("pathing-trash");

        topBar.getChildren().addAll(autoName, autoTrash);

        StackPane openautoPane = new StackPane();
        openautoPane.setPadding(new Insets(10, 10, 10, 10));
        VBox.setVgrow(openautoPane, Priority.ALWAYS);
        openautoPane.setAlignment(Pos.CENTER);
        // TODO change to image
        Button autoOpen = new Button("Open");
        autoOpen.setPrefSize(nodeWidth, nodeHeight - 20);
        autoOpen.getStyleClass().addAll("pathing-open");

        openautoPane.getChildren().addAll(autoOpen);

        root.getChildren().addAll(topBar, openautoPane);

        autoName.textProperty().addListener((obs, oldValue, newValue) -> {
            if (oldName == null) {
                if (newValue != oldValue) {
                    oldName = oldValue.replace(" ", "_") + ".json";
                }
            }
        });

        autoName.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                File oldFile = new File(Constants.autoDir + oldName);
                if (!new File(Constants.autoDir + autoName.getText().replace(" ", "_").replace(".json", "") + ".json")
                        .isFile()) {
                    oldFile.renameTo(new File(
                            Constants.autoDir + autoName.getText().replace(" ", "_").replace(".json", "") + ".json"));
                    oldName = null;
                    autoName.getParent().requestFocus();
                } else {
                    System.out.println("file already exists");
                }
            } else if (e.getCode() == KeyCode.ESCAPE) {
                autoName.setText(oldName.replace("_", " ").replace(".json", ""));
                oldName = null;
                autoName.getParent().requestFocus();
            }
        });

        autoOpen.setOnAction(e -> {
            AutoEditor.init(window, new File(Constants.autoDir + autoName.getText().replace(" ", "_") + ".json"));
            window.setScene(AutoEditor.editor);
        });

        autoTrash.setOnAction(e -> {
            File targetFile = new File(Constants.autoDir + autoName.getText().replace(" ", "_") + ".json");
            targetFile.delete();

            updateGUI();
        });

        return root;
    }
}