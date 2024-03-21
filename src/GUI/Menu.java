package GUI;

import java.io.File;
import java.io.IOException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
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

public class Menu {
    static Stage window;

    public static Scene menu;


    public static GridPane paths;

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

        paths = new GridPane();
        paths.setAlignment(Pos.CENTER_LEFT);
        paths.setPadding(new Insets(0, 0, 0, 0));
        paths.setHgap(10);
        paths.setVgap(10);
        paths.setPrefSize(window.getWidth(), 250);
        // paths.getStyleClass().addAll("editing");

        int maxRow = 5;



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
                } else {
                    System.out.println("auto already exists");
                }
            } catch (IOException e1) {
                System.out.println("Unexpected Error - addPath button");
                e1.printStackTrace();
            }
        });

        
        updatePathingGUI();
        
        path.getChildren().addAll(pathTop, pathSeperator, paths);



        grid.add(path, 0, 0);


        root.setTop(General.createMenu(window));
        root.setCenter(grid);
        menu = new Scene(root, window.getWidth(), window.getHeight());

        menu.getStylesheets().addAll("GUI/CSS/Menu.css");
    }


    static String oldName = null;

    public static void updatePathingGUI() {
        paths.getChildren().clear();

        for(int i = 0; i < getFileCount(Constants.pathDir); i++) {

            File f = new File(Constants.pathDir);
            File[] pathFiles = f.listFiles();

            BorderPane pathBox = new BorderPane();
            // pathBox.setPadding(new Insets(15, 0, 0, 15));
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

            paths.add(pathBox, i, 0);

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
                window.setScene(PathEditor.editor);
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
