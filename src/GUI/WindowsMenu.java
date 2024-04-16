package GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class WindowsMenu {

    static Stage window;

    public static double xOffset = 0;
    public static double yOffset = 0;

    public static boolean windowMaximized = false;

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

        menu.getChildren().addAll(menuApplicationButton, labelApp, spacer, minimizeApplicationButton, maximizeApplicationButton, closeApplicationButton);


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

        return menu;
    }
}
