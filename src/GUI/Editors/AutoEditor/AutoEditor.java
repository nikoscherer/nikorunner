package GUI.Editors.AutoEditor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;

import GUI.Path;
import GUI.PathingJson;
import GUI.WindowsMenu;
import GUI.Editors.Editor;
import GUI.PlaceholderTypes.Command;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

// TODO Uncenter pathing canvas, when more waypoints are added, and the menu is open the pathing canvas gets pushed down
public class AutoEditor {

    static Stage window;

    public static Scene editor;
    public static GraphicsContext gc;

    public static ArrayList<Path> paths;
    public static ArrayList<Command> commands;


    static File file;

    static Label autoName;

    public static void init(Stage primaryStage, File autoFile) {
        window = primaryStage;
        file = autoFile;

        BorderPane root = WindowsMenu.create(window);

        autoName = new Label("Unknown Auto");   
        autoName.getStyleClass().addAll("label-pathName");

        if (autoFile != null) {
            try {
                commands = PathingJson.convertToAuto(autoFile);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                System.err.println("Path file does not exist");
            }

            autoName.setText(autoFile.getName().replace("_", " ").replace(".json", ""));
        } else {
            paths = new ArrayList<>();
            commands = new ArrayList<>();
        }

        StackPane pathing = new StackPane();
        Canvas pathingCanvas = new Canvas(Editor.size, Editor.size);
        gc = pathingCanvas.getGraphicsContext2D();

        gc.drawImage(Editor.centerstage, 0, 0);
        pathing.getChildren().addAll(pathingCanvas);

        root.setTop(WindowsMenu.createMenu(primaryStage));
        root.setCenter(pathing);
        root.setLeft(AutoEditorGUI.createGUI(window));

        editor = new Scene(root, window.getWidth(), window.getHeight());
        editor.setFill(Color.TRANSPARENT);
        editor.getStylesheets().add("GUI/CSS/PathEditorGUI.css");
    }

    public static void redrawAuto(GraphicsContext gc) {
        gc.clearRect(0, 0, Editor.size, Editor.size);
        gc.drawImage(Editor.centerstage, 0, 0, Editor.size, Editor.size);

        Editor.EditorFunctions.redrawAutoPath(gc, paths);
    }

    // TODO save commands too
    public static void updateJSON() {
        if (file != null) {
            try {
                PathingJson.convertAutoToJson(commands, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}