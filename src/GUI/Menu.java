package GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Menu {

    static Stage window;

    public static Scene menu;

    public static void init(Stage primaryStage) {
        window = primaryStage;

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(8, 8, 8, 8));

        grid.setHgap(10);
        grid.setVgap(10);



        HBox path = new HBox();
        path.setPadding(new Insets(25, 25, 25, 25));
        path.setSpacing(10);

        Label pathLabel = new Label("Paths");
        pathLabel.setTextFill(Color.color(1, 1, 1));

        Button addPath = new Button("+");
        addPath.setPrefSize(25, 25);
        addPath.setTextFill(Color.color(0, 0, 0));
        addPath.setAlignment(Pos.CENTER_RIGHT);

        path.getChildren().addAll(pathLabel, addPath);

        grid.add(path, 0, 0);

        
        HBox auto = new HBox();
        auto.setPadding(new Insets(25, 25, 25, 25));
        auto.setSpacing(10);

        Label autoLabel = new Label("Autos");
        autoLabel.setTextFill(Color.color(1, 1, 1));

        Button addAuto = new Button("+");
        addAuto.setPrefSize(25, 25);
        addAuto.setTextFill(Color.color(0, 0, 0));
        addAuto.setAlignment(Pos.CENTER_RIGHT);

        auto.getChildren().addAll(autoLabel, addAuto);

        grid.add(auto, 0, 1);


        addPath.setOnAction(e -> {
            System.out.println("Created New Path");
            window.setScene(PathEditor.editor);
        });

        addAuto.setOnAction(e -> {
            System.out.println("Created New Autonomous");
        });

        menu = new Scene(grid, Constants.defaultSize[0], Constants.defaultSize[1]);

        menu.getStylesheets().add("imgs/Theme.css");
    }
}