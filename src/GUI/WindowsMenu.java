package GUI;

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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class WindowsMenu {

    static Stage window;

    static Stage settingWindow;

    static BorderPane root;

    static Scene settingScene;

    public static double xOffset = 0;
    public static double yOffset = 0;

    public static boolean windowMaximized = false;

    static int borderWidth = 5;

    static boolean windowScaling = false;
    static int location = 0;

    public static boolean showSettings = false;

    // TODO change cursor when hovering
    public static BorderPane create(Stage primaryStage) {
        root = new BorderPane();

        root.onMouseDragOverProperty().addListener((obs, oldValue, newValue) -> {
            // TODO I dont think this is the correct listener, but move window cursor should
            // go on mouse move
        });

        root.setOnMouseDragged(e -> {
            if (e.getSceneX() <= borderWidth || e.getSceneY() <= borderWidth
                    || e.getSceneX() >= window.getWidth() - borderWidth
                    || e.getSceneY() >= window.getHeight() - borderWidth) {
                windowScaling = true;
                if (e.getSceneX() <= borderWidth && e.getSceneY() <= borderWidth) { // grabbed top left
                    root.setCursor(Cursor.NW_RESIZE);
                    location = 0;
                } else if (e.getSceneX() >= window.getWidth() - borderWidth && e.getSceneY() <= borderWidth) { // grabbed
                                                                                                               // top
                                                                                                               // right
                    root.setCursor(Cursor.NE_RESIZE);
                    location = 2;
                } else if (e.getSceneX() >= window.getWidth() - borderWidth
                        && e.getSceneY() >= window.getHeight() - borderWidth) { // grabbed bottom right
                    root.setCursor(Cursor.SE_RESIZE);
                    location = 4;
                } else if (e.getSceneX() <= borderWidth && e.getSceneY() >= window.getHeight() - borderWidth) { // grabbed
                                                                                                                // bottom
                                                                                                                // left
                    root.setCursor(Cursor.SW_RESIZE);
                    location = 6;
                } else if (e.getSceneY() <= borderWidth) { // grabbed top
                    root.setCursor(Cursor.N_RESIZE);
                    location = 1;
                } else if (e.getSceneX() >= window.getWidth() - borderWidth) { // grabbed right
                    root.setCursor(Cursor.E_RESIZE);
                    location = 3;
                } else if (e.getSceneY() >= window.getHeight() - borderWidth) { // grabbed bottom
                    root.setCursor(Cursor.S_RESIZE);
                    location = 5;
                } else if (e.getSceneX() <= borderWidth) { // grabbed left
                    root.setCursor(Cursor.W_RESIZE);
                    location = 7;
                }
            }
        });

        root.setOnMousePressed(e -> {
            // if at edge
            if (e.getSceneX() <= borderWidth || e.getSceneY() <= borderWidth
                    || e.getSceneX() >= window.getWidth() - borderWidth
                    || e.getSceneY() >= window.getHeight() - borderWidth) {
                windowScaling = true;
                if (e.getSceneX() <= borderWidth && e.getSceneY() <= borderWidth) { // grabbed top left
                    root.setCursor(Cursor.NW_RESIZE);
                    location = 0;
                } else if (e.getSceneX() >= window.getWidth() - borderWidth && e.getSceneY() <= borderWidth) { // grabbed
                                                                                                               // top
                                                                                                               // right
                    root.setCursor(Cursor.NE_RESIZE);
                    location = 2;
                } else if (e.getSceneX() >= window.getWidth() - borderWidth
                        && e.getSceneY() >= window.getHeight() - borderWidth) { // grabbed bottom right
                    root.setCursor(Cursor.SE_RESIZE);
                    location = 4;
                } else if (e.getSceneX() <= borderWidth && e.getSceneY() >= window.getHeight() - borderWidth) { // grabbed
                                                                                                                // bottom
                                                                                                                // left
                    root.setCursor(Cursor.SW_RESIZE);
                    location = 6;
                } else if (e.getSceneY() <= borderWidth) { // grabbed top
                    root.setCursor(Cursor.N_RESIZE);
                    location = 1;
                } else if (e.getSceneX() >= window.getWidth() - borderWidth) { // grabbed right
                    root.setCursor(Cursor.E_RESIZE);
                    location = 3;
                } else if (e.getSceneY() >= window.getHeight() - borderWidth) { // grabbed bottom
                    root.setCursor(Cursor.S_RESIZE);
                    location = 5;
                } else if (e.getSceneX() <= borderWidth) { // grabbed left
                    root.setCursor(Cursor.W_RESIZE);
                    location = 7;
                }
            }
        });

        root.setOnMouseReleased(e -> {
            // set bool to false
            root.setCursor(Cursor.DEFAULT);
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

        return root;
    }

    public static HBox createMenu(Stage primaryStage) {
        window = primaryStage;

        HBox menu = new HBox();
        menu.setAlignment(Pos.CENTER_LEFT);
        // menu.setPrefHeight(xOffset);

        menu.setPrefHeight(45);
        menu.getStyleClass().addAll("app-menu");

        int buttonWidth = 35;

        Image menuImage = new Image("imgs/menu.png");
        ImageView menuImageView = new ImageView(menuImage);
        menuImageView.setFitHeight(20);
        menuImageView.setFitWidth(20);

        Button menuApplicationButton = new Button();
        menuApplicationButton.setPrefSize(buttonWidth, buttonWidth);
        menuApplicationButton.setMinSize(buttonWidth, buttonWidth);
        menuApplicationButton.setMaxSize(buttonWidth, buttonWidth);

        menuApplicationButton.setGraphic(menuImageView);
        menuApplicationButton.getStyleClass().add("button-control");

        menuApplicationButton.setOnAction(e -> {
            showSettings = !showSettings;
            update();
        });

        Label labelApp = new Label("Nikorunner");
        labelApp.getStyleClass().addAll("label-app");
        labelApp.setPadding(new Insets(0, 0, 0, 10));

        Image minimizeImage = new Image("imgs/minimize.png");
        ImageView minimizeImageView = new ImageView(minimizeImage);
        minimizeImageView.setFitHeight(15);
        minimizeImageView.setFitWidth(15);
        Button minimizeApplicationButton = new Button();
        minimizeApplicationButton.setPrefSize(buttonWidth, buttonWidth);
        minimizeApplicationButton.setMinSize(buttonWidth, buttonWidth);
        minimizeApplicationButton.setMaxSize(buttonWidth, buttonWidth);

        minimizeApplicationButton.setGraphic(minimizeImageView);
        minimizeApplicationButton.getStyleClass().add("button-control");

        Image maximizeImage = new Image("imgs/maximize.png");
        ImageView maximizeImageView = new ImageView(maximizeImage);
        maximizeImageView.setFitHeight(15);
        maximizeImageView.setFitWidth(15);
        Button maximizeApplicationButton = new Button();
        maximizeApplicationButton.setPrefSize(buttonWidth, buttonWidth);
        maximizeApplicationButton.setMinSize(buttonWidth, buttonWidth);
        maximizeApplicationButton.setMaxSize(buttonWidth, buttonWidth);

        maximizeApplicationButton.setGraphic(maximizeImageView);
        maximizeApplicationButton.getStyleClass().add("button-control");

        // change to image
        Image closeImage = new Image("imgs/close.png");
        ImageView closeImageView = new ImageView(closeImage);
        closeImageView.setFitHeight(15);
        closeImageView.setFitWidth(15);
        Button closeApplicationButton = new Button();
        closeApplicationButton.setPrefSize(buttonWidth, buttonWidth);
        closeApplicationButton.setMinSize(buttonWidth, buttonWidth);
        closeApplicationButton.setMaxSize(buttonWidth, buttonWidth);
        closeApplicationButton.setGraphic(closeImageView);
        closeApplicationButton.getStyleClass().add("button-control");
        closeApplicationButton.getStyleClass().add("button-close");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        minimizeApplicationButton.setOnAction(e -> {
            window.setIconified(true);
        });

        maximizeApplicationButton.setOnAction(e -> {
            window.setMaximized(!window.isMaximized());
        });

        closeApplicationButton.setOnAction(e -> {
            window.close();
        });

        menu.setPadding(new Insets(0, 5, 0, 10));
        // menu.setSpacing(10);

        menu.getChildren().addAll(menuApplicationButton, labelApp, spacer, minimizeApplicationButton,
                maximizeApplicationButton, closeApplicationButton);

        menu.setOnMousePressed(e -> {
            xOffset = window.getX() - e.getScreenX();
            yOffset = window.getY() - e.getScreenY();
        });

        menu.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                window.setX(e.getScreenX() + xOffset);
                window.setY(e.getScreenY() + yOffset);
            }
        });

        menu.getStylesheets().addAll("GUI/CSS/WindowsMenu.css");

        createSettings();

        return menu;
    }

    public static void createSettings() {

        VBox settingsRoot = new VBox();

        HBox menu = new HBox();
        menu.setAlignment(Pos.CENTER_LEFT);
        menu.setPrefHeight(30);
        menu.getStyleClass().addAll("settings-menu");

        int buttonWidth = 20;

        Label labelApp = new Label("Nikorunner - Settings");
        labelApp.getStyleClass().addAll("label-settings");
        labelApp.setPadding(new Insets(0, 0, 0, 0));

        // change to image
        Image closeImage = new Image("imgs/close.png");
        ImageView closeImageView = new ImageView(closeImage);
        closeImageView.setFitHeight(15);
        closeImageView.setFitWidth(15);
        Button closeApplicationButton = new Button();
        closeApplicationButton.setPrefSize(buttonWidth, buttonWidth);
        closeApplicationButton.setMinSize(buttonWidth, buttonWidth);
        closeApplicationButton.setMaxSize(buttonWidth, buttonWidth);
        closeApplicationButton.setGraphic(closeImageView);
        closeApplicationButton.getStyleClass().add("button-control");
        closeApplicationButton.getStyleClass().add("button-close");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        closeApplicationButton.setOnAction(e -> {
            showSettings = false;
            settingWindow.close();
        });

        menu.setPadding(new Insets(0, 5, 0, 10));
        // menu.setSpacing(10);

        menu.getChildren().addAll(labelApp, spacer, closeApplicationButton);

        menu.setOnMousePressed(e -> {
            xOffset = settingWindow.getX() - e.getScreenX();
            yOffset = settingWindow.getY() - e.getScreenY();
        });

        menu.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                settingWindow.setX(e.getScreenX() + xOffset);
                settingWindow.setY(e.getScreenY() + yOffset);
            }
        });

        VBox pathingDirectoryBox = new VBox();
        Label pathingDirectoryLabel = new Label("Pathing Directory");
        TextField pathingDirectoryText = new TextField(Constants.pathingDir);

        pathingDirectoryBox.getChildren().addAll(pathingDirectoryLabel, pathingDirectoryText);

        pathingDirectoryLabel.getStyleClass().addAll("setting-label");
        pathingDirectoryBox.getStyleClass().addAll("setting-box");

        VBox commandsDirectoryBox = new VBox();
        Label commandsDirectoryLabel = new Label("Pathing Directory");
        TextField commandsDirectoryText = new TextField(Constants.commandsDir);

        commandsDirectoryBox.getChildren().addAll(commandsDirectoryLabel, commandsDirectoryText);

        commandsDirectoryLabel.getStyleClass().addAll("setting-label");
        commandsDirectoryBox.getStyleClass().addAll("setting-box");

        settingsRoot.getChildren().addAll(menu, pathingDirectoryBox, commandsDirectoryBox);
        settingScene = new Scene(settingsRoot, window.getWidth() / 3, window.getHeight() / 1.5);
        settingScene.getStylesheets().addAll("GUI/CSS/Settings.css");

        settingScene.setFill(Color.TRANSPARENT);

        pathingDirectoryText.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.ENTER) {
                Constants.pathingDir = pathingDirectoryText.getText();
                if(!Constants.pathingDir.endsWith("\\")) {
                    Constants.pathingDir = Constants.pathingDir + "\\";
                }
                Constants.pathDir = Constants.pathingDir + "paths\\";
                Constants.autoDir = Constants.pathingDir + "autos\\";

                pathingDirectoryText.getParent().requestFocus();

                Menu.updateGUI();
            }
        });

        commandsDirectoryText.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.ENTER) {
                Constants.commandsDir = commandsDirectoryText.getText();
                if(!Constants.commandsDir.endsWith("\\")) {
                    Constants.commandsDir = Constants.commandsDir + "\\";
                }

                commandsDirectoryText.getParent().requestFocus();
            }
        });

        settingWindow = new Stage();
        settingWindow.initStyle(StageStyle.TRANSPARENT);
        settingWindow.setScene(settingScene);
        settingWindow.hide();
    }

    public static void update() {
        if (showSettings) {
            settingWindow.show();
        } else if (settingWindow.isShowing()) {
            settingWindow.hide();
        }
    }
}
