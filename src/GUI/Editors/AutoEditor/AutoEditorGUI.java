package GUI.Editors.AutoEditor;

import GUI.Constants;
import GUI.Menu;
import GUI.Editors.CommandGUIs;
import GUI.PlaceholderTypes.CommandBase;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AutoEditorGUI {
    static Stage window;

    // GUI Nodes
    static VBox commandsBox;
    static HBox commandsLabel;

    // GUI Constants
    static boolean showWaypoints = false;
    static boolean showCommands = false;

    public static VBox createGUI(Stage primaryStage) {
        window = primaryStage;

        VBox sideMenu = new VBox();
        sideMenu.setPrefWidth(500);
        VBox.setVgrow(sideMenu, Priority.ALWAYS);
        sideMenu.getStyleClass().addAll("sidebar");

        sideMenu.setPadding(new Insets(10, 10, 10, 10));
        sideMenu.setSpacing(10);

        HBox sideMenuBar = new HBox();
        sideMenuBar.setAlignment(Pos.CENTER_LEFT);
        sideMenuBar.setSpacing(10);

        Image backImage = new Image("imgs/back.png");
        ImageView backImageView = new ImageView(backImage);
        backImageView.setFitWidth(25);
        backImageView.setFitHeight(25);
        Button backButton = new Button();
        backButton.setGraphic(backImageView);
        backButton.getStyleClass().addAll("button-back");

        sideMenuBar.getChildren().addAll(backButton, AutoEditor.autoName);

        initCommands();
        update();

        sideMenu.getChildren().addAll(sideMenuBar, commandsBox);

        backButton.setOnAction(e -> {
            window.setScene(Menu.menu);
            AutoEditor.init(window, null);
        });

        return sideMenu;
    }

    private static void initCommands() {
        commandsBox = new VBox();
        HBox.setHgrow(commandsBox, Priority.ALWAYS);
        commandsBox.setPadding(new Insets(10, 10, 10, 10));
        commandsBox.setSpacing(10);
        commandsBox.setAlignment(Pos.CENTER_LEFT);
        commandsBox.getStyleClass().addAll("sidebar-option-box");

        commandsLabel = new HBox();
        commandsLabel.setSpacing(10);
        commandsLabel.setAlignment(Pos.CENTER_LEFT);

        Region commandButtonSeperator = new Region();

        HBox.setHgrow(commandButtonSeperator, Priority.SOMETIMES);

        // TODO change to image
        Button newCommandButton = new Button("+");
        newCommandButton.getStyleClass().addAll("button-option");

        Label commandsLabelText = new Label("Commands");
        commandsLabelText.setPadding(new Insets(1, 5, 1, 5));
        commandsLabelText.getStyleClass().addAll("sidebar-label");

        // TODO change to image
        Button commandsDropdown = new Button("^");
        commandsDropdown.getStyleClass().addAll("button-option");

        commandsLabel.getChildren().addAll(commandsLabelText, commandButtonSeperator, newCommandButton,
                commandsDropdown);

        newCommandButton.setOnAction(e -> {
            CommandBase command = new CommandBase(Constants.getCommands(true).get(0));
            AutoEditor.commands.add(command);

            showCommands = true;

            showWaypoints = false;

            commandsDropdown.setText("v");

            update();
        });

        commandsDropdown.setOnAction(e -> {
            showCommands = !showCommands;

            showWaypoints = false;

            commandsDropdown.setText(showCommands? "v" : "^");

            update();
        });
    }

    // Could be done on a seperate thread to reduce lag

    public static void update() {
        commandsBox.getChildren()
                .setAll(CommandGUIs.commandsGUI(showCommands, AutoEditor.commands, commandsLabel, true));
    }

    // TODO casting is wrong
    public static void updateGUIText(int spline, int waypoint) {
        // waypointsBox > waypoint > coordinates > x & y
        // VBox waypointBox = (VBox) waypointsBox.getChildren().get(1 + spline);
        // HBox waypointCoordinates = (HBox) waypointBox.getChildren().get(1);
        // StackPane waypointXPane = (StackPane)
        // waypointCoordinates.getChildren().get(0);
        // StackPane waypointYPane = (StackPane)
        // waypointCoordinates.getChildren().get(1);

        // TextField waypointX = (TextField) waypointXPane.getChildren().get(0);
        // TextField waypointY = (TextField) waypointYPane.getChildren().get(0);

        // System.out.println(spline);
        // System.out.println(waypoint);
        // System.out.println(" ");

        // if(waypoint == 0 || waypoint == 3) {
        // waypointX.setText(Double.toString(splines.get(spline).getIndexVector(waypoint).getX()));
        // System.out.println(splines.get(spline).getIndexVector(waypoint).getX());
        // waypointY.setText(Double.toString(splines.get(spline).getIndexVector(waypoint).getY()));
        // } else {
        // if(spline > 0) {
        // HBox controlBox = (HBox) waypointBox.getChildren().get(2);
        // // first control
        // StackPane controlMag1Pane = (StackPane) controlBox.getChildren().get(0);
        // TextField controlMag1 = (TextField) controlMag1Pane.getChildren().get(0);

        // // second control
        // StackPane controlMag2Pane = (StackPane) controlBox.getChildren().get(1);
        // TextField controlMag2 = (TextField) controlMag2Pane.getChildren().get(0);

        // if(waypoint == 1) {
        // if(spline >= splines.size() - 1) {

        // }
        // controlMag1.setText(Double.toString(splines.get(spline -
        // 1).getSecondControl().getMagnitude()));
        // controlMag2.setText(Double.toString(splines.get(spline).getFirstControl().getMagnitude()));
        // } else {
        // if(spline < splines.size() - 1) {
        // controlMag1.setText(Double.toString(splines.get(spline).getSecondControl().getMagnitude()));
        // controlMag2.setText(Double.toString(splines.get(spline +
        // 1).getFirstControl().getMagnitude()));
        // }
        // }
        // }
        // }
    }
}
