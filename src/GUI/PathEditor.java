package GUI;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class PathEditor {

    static Stage window;
    
    public static Scene editor;


    public static void init(Stage primaryStage) {
        window = primaryStage;

        HBox topMenu = new HBox();


        Button menuButton = new Button("Menu");
        
        menuButton.setPadding(new Insets(5, 8, 5, 8));
        menuButton.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        menuButton.setPrefSize(80, 30);
        menuButton.setTextFill(Color.color(0, 0, 0));


        topMenu.setPadding(new Insets(8, 8, 8, 8));
        topMenu.getChildren().addAll(menuButton);



        Canvas autoCanvas = new Canvas(1000, 600);
        GraphicsContext gc = autoCanvas.getGraphicsContext2D();
        drawShapes(gc);


        BorderPane borderPane = new BorderPane();
        borderPane.setTop(topMenu);
        borderPane.setCenter(autoCanvas);


        menuButton.setOnAction(e -> {
            System.out.println("Went to Menu");
            window.setScene(Menu.menu);
        });

        borderPane.setStyle("-fx-background-color: #263c52;");
        editor = new Scene(borderPane, Constants.defaultSize[0], Constants.defaultSize[1]);
    }

    private static void drawShapes(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 60, 30, 30);
        gc.strokeOval(60, 60, 30, 30);
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        gc.fillPolygon(new double[]{10, 40, 10, 40},
                       new double[]{210, 210, 240, 240}, 4);
        gc.strokePolygon(new double[]{60, 90, 60, 90},
                         new double[]{210, 210, 240, 240}, 4);
        gc.strokePolyline(new double[]{110, 140, 110, 140},
                          new double[]{210, 210, 240, 240}, 4);
    }
}
