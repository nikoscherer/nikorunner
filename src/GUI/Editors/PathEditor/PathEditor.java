package GUI.Editors.PathEditor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;

import GUI.Path;
import GUI.PathingJson;
import GUI.Spline;
import GUI.WindowsMenu;
import GUI.Editors.Editor;
import GUI.PlaceholderTypes.Command;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import nikorunnerlib.src.Geometry.Point2d;
import nikorunnerlib.src.Geometry.Pose2d;
import nikorunnerlib.src.Geometry.Vector2d;
import nikorunnerlib.src.Other.Util;
import nikorunnerlib.src.Pathing.SplineGenerator;

// TODO Uncenter pathing canvas, when more waypoints are added, and the menu is open the pathing canvas gets pushed down
public class PathEditor {

    static Stage window;

    public static Scene editor;
    public static GraphicsContext gc;

    public static ArrayList<Spline> splines;
    public static ArrayList<Command> commands;

    // Pathing
    // selected, index, point (p0-p3), if pose then heading
    static int[] selected = { 0, 0, 0, 0 };
    static Point2d offset = new Point2d(0, 0);

    static File file;

    static Label pathName;

    public static void init(Stage primaryStage, File pathFile) {
        window = primaryStage;
        file = pathFile;

        BorderPane root = WindowsMenu.create(window);

        pathName = new Label("Unknown Path");
        pathName.getStyleClass().addAll("label-pathName");

        if (pathFile != null) {
            try {
                Path path = PathingJson.convertToPath(pathFile);

                splines = path.getSplines();
                commands = path.getCommands();
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                System.err.println("Path file does not exist");
            }

            pathName.setText(pathFile.getName().replace("_", " ").replace(".json", ""));
        } else {

            splines = new ArrayList<>();
            commands = new ArrayList<>();

            // Field Relative
            Pose2d defaultStartPose = new Pose2d(-12, -18, Math.toRadians(0));
            Pose2d defaultEndPose = new Pose2d(12, 18, Math.toRadians(0));

            // Off of poses
            Vector2d defaultFirstControl = new Vector2d(10, Math.toRadians(90));
            Vector2d defaultSecondControl = new Vector2d(10, Math.toRadians(-90));

            splines.add(new Spline(defaultStartPose, defaultFirstControl, defaultSecondControl, defaultEndPose));
        }

        StackPane pathing = new StackPane();
        Canvas pathingCanvas = new Canvas(Editor.size, Editor.size);
        gc = pathingCanvas.getGraphicsContext2D();

        gc.drawImage(Editor.centerstage, 0, 0);
        pathing.getChildren().addAll(pathingCanvas);

        redrawPath(gc);

        root.setTop(WindowsMenu.createMenu(primaryStage));
        root.setCenter(pathing);
        root.setLeft(PathEditorGUI.createGUI(window));

        PathEditorGUI.update();

        editor = new Scene(root, window.getWidth(), window.getHeight());
        editor.setFill(Color.TRANSPARENT);
        editor.getStylesheets().add("GUI/CSS/PathEditorGUI.css");

        // on click
        pathingCanvas.setOnMousePressed(e -> {
            Point2d fieldRelativeCursor = Editor.EditorFunctions
                    .orientateToField(Editor.EditorFunctions.convertToInches(new Point2d(e.getX(), e.getY())));

            if (e.getButton() == MouseButton.PRIMARY && selected[0] == 0) {
                if (e.getClickCount() == 2) {
                    Spline lastWaypoint = splines.get(splines.size() - 1);
                    Spline waypoint = new Spline(
                            lastWaypoint.getEndPose(),
                            new Vector2d(10, Util.getOppositeAngle(lastWaypoint.getSecondControl().getDirection())),
                            new Vector2d(10, Math.toRadians(0)),
                            new Pose2d(fieldRelativeCursor, Math.toRadians(0)));
                    splines.add(waypoint);
                    PathEditorGUI.update();
                } else if (e.getClickCount() == 1) { // Move Point
                    for (int i = 0; i < splines.size(); i++) {

                        Point2d startPoseDifference = fieldRelativeCursor.toVector2d()
                                .minus(splines.get(i).getStartPose().getVector2d()).toPoint2d();
                        Point2d startPoseHeadingDifference = fieldRelativeCursor.toVector2d()
                                .minus(getHeading(splines.get(i).getStartPose(), gc).toVector2d()).toPoint2d();
                        Point2d firstControlDifference = Editor.EditorFunctions
                                .relateTo(splines.get(i).getStartPose().getPoint2d(),
                                        splines.get(i).getFirstControl())
                                .toVector2d().minus(fieldRelativeCursor.toVector2d())
                                .toPoint2d();
                        Point2d secondControlDifference = Editor.EditorFunctions
                                .relateTo(splines.get(i).getEndPose().getPoint2d(),
                                        splines.get(i).getSecondControl())
                                .toVector2d().minus(fieldRelativeCursor.toVector2d())
                                .toPoint2d();
                        Point2d endPoseHeadingDifference = fieldRelativeCursor.toVector2d()
                                .minus(getHeading(splines.get(i).getEndPose(), gc).toVector2d()).toPoint2d();
                        Point2d endPoseDifference = fieldRelativeCursor.toVector2d()
                                .minus(splines.get(i).getEndPose().getVector2d()).toPoint2d();

                        if (inRange(startPoseDifference, (Editor.robotPointSize - 5) / Editor.pixelPerIn)) {
                            setSelected(1, i, 0, 0);
                        } else if (inRange(firstControlDifference, Editor.controlPointSize / Editor.pixelPerIn)) {
                            setSelected(1, i, 1, 0);
                        } else if (inRange(secondControlDifference, Editor.controlPointSize / Editor.pixelPerIn)) {
                            setSelected(1, i, 2, 0);
                        } else if (inRange(endPoseDifference, (Editor.robotPointSize - 5) / Editor.pixelPerIn)) {
                            setSelected(1, i, 3, 0);
                        }

                        if (i == 0 && splines.size() == 1) {
                            if (inRange(startPoseHeadingDifference, (Editor.robotPointSize - 5) / Editor.pixelPerIn)) {
                                setSelected(1, i, 0, 1);
                            } else if (inRange(endPoseHeadingDifference,
                                    (Editor.robotPointSize - 5) / Editor.pixelPerIn)) {
                                setSelected(1, i, 0, 1);
                            }
                        } else if (i == 0) {
                            if (inRange(startPoseHeadingDifference, (Editor.robotPointSize - 5) / Editor.pixelPerIn)) {
                                setSelected(1, i, 0, 1);
                            }
                        } else if (i == splines.size() - 1) {
                            if (inRange(endPoseHeadingDifference, (Editor.robotPointSize - 5) / Editor.pixelPerIn)) {
                                setSelected(1, i, 0, 1);
                            }
                        }

                        offset = fieldRelativeCursor;
                    }
                }
            } else if (e.getButton() == MouseButton.SECONDARY) {
                // NEEDS EDITING
                for (int i = 0; i < splines.size(); i++) {

                    Point2d startPoseDifference = fieldRelativeCursor.toVector2d()
                            .minus(splines.get(i).getStartPose().getVector2d()).toPoint2d();
                    Point2d firstControlDifference = Editor.EditorFunctions
                            .relateTo(splines.get(i).getStartPose().getPoint2d(),
                                    splines.get(i).getFirstControl())
                            .toVector2d().minus(fieldRelativeCursor.toVector2d())
                            .toPoint2d();
                    Point2d secondControlDifference = Editor.EditorFunctions
                            .relateTo(splines.get(i).getEndPose().getPoint2d(),
                                    splines.get(i).getSecondControl())
                            .toVector2d().minus(fieldRelativeCursor.toVector2d())
                            .toPoint2d();
                    Point2d endPoseDifference = fieldRelativeCursor.toVector2d()
                            .minus(splines.get(i).getEndPose().getVector2d()).toPoint2d();

                    if (i > 0 && i < splines.size()) {
                        if (inRange(startPoseDifference, (Editor.robotPointSize - 5) / Editor.pixelPerIn)) {
                            deleteSpline(i);
                        } else if (inRange(firstControlDifference, Editor.controlPointSize / Editor.pixelPerIn)) {
                            deleteSpline(i);
                        } else if (inRange(secondControlDifference, Editor.controlPointSize / Editor.pixelPerIn)) {
                            deleteSpline(i);
                        } else if (inRange(endPoseDifference, (Editor.robotPointSize - 5) / Editor.pixelPerIn)) {
                            deleteSpline(i);
                        }
                    }
                }
                PathEditorGUI.update();
            }
            redrawPath(gc);
        });

        pathingCanvas.setOnMouseDragged(e -> {
            if (selected[0] == 1 && e.getButton() != MouseButton.SECONDARY) {
                Point2d fieldRelativeCursor = Editor.EditorFunctions
                        .orientateToField(Editor.EditorFunctions.convertToInches(new Point2d(e.getX(), e.getY())));

                Point2d currentPoint = splines.get(selected[1]).getIndexVector(selected[2]);

                currentPoint = fieldRelativeCursor;

                if (selected[2] == 0) {
                    splines.get(selected[1]).startPose.setPoint2d(currentPoint.getX(), currentPoint.getY());
                } else if (selected[2] == 1) {
                    Vector2d nonRelative = currentPoint.toVector2d()
                            .minus(splines.get(selected[1]).getStartPose().getVector2d());
                    splines.get(selected[1]).firstControl.setXY(nonRelative.getX(), nonRelative.getY());
                } else if (selected[2] == 2) {
                    Vector2d nonRelative = currentPoint.toVector2d()
                            .minus(splines.get(selected[1]).getEndPose().getVector2d());
                    splines.get(selected[1]).secondControl.setXY(nonRelative.getX(), nonRelative.getY());
                } else if (selected[2] == 3) {
                    splines.get(selected[1]).endPose.setPoint2d(currentPoint.getX(), currentPoint.getY());
                }

                // (make pathing continuous)
                if (splines.size() > 1) {
                    if (selected[1] != 0 || (selected[2] == 2 && selected[1] == 0)) {
                        if (selected[2] == 1) {
                            splines.get(selected[1] - 1).getSecondControl().setDirection(
                                    Util.getOppositeAngle(splines.get(selected[1]).getFirstControl().getDirection()));
                        } else if (selected[2] == 2 && (selected[1] + 1 < splines.size())) {
                            splines.get(selected[1] + 1).getFirstControl().setDirection(
                                    Util.getOppositeAngle(splines.get(selected[1]).getSecondControl().getDirection()));
                        }

                        if (selected[2] == 0) {
                            splines.get(selected[1] - 1).getEndPose().setPose(splines.get(selected[1]).getStartPose());
                        }
                    }
                }

                redrawPath(gc);
            }
        });

        pathingCanvas.setOnMouseReleased(e -> {
            setSelected(0, 0, 0, 0);
        });
    }

    public static void deleteSpline(int index) {

        if (index != 0 && splines.size() != 2 && index != splines.size() - 1) {
            splines.get(index + 1).setStartPose(splines.get(index - 1).getEndPose());
            splines.get(index - 1).setEndPose(splines.get(index + 1).getStartPose());
            splines.get(index + 1)
                    .setFirstControl(new Vector2d(splines.get(index - 1).getSecondControl().getMagnitude(),
                            Util.getOppositeAngle(splines.get(index - 1).getFirstControl().getDirection())));
            splines.get(index - 1)
                    .setSecondControl(new Vector2d(splines.get(index + 1).getFirstControl().getMagnitude(),
                            splines.get(index - 1).getFirstControl().getDirection()));
        }

        splines.remove(index);
        redrawPath(gc);
    }

    public static void setSelected(int a, int b, int c, int d) {
        selected[0] = a;
        selected[1] = b;
        selected[2] = c;
        selected[3] = d;
    }

    public static void redrawPath(GraphicsContext gc) {

        PathEditorGUI.updateGUIText(selected[1], selected[2]);

        gc.clearRect(0, 0, Editor.size, Editor.size);

        gc.drawImage(Editor.centerstage, 0, 0, Editor.size, Editor.size);
        Editor.EditorFunctions.redrawPath(gc, splines, true);

        drawCommandWaypoints(gc);

        updateJSON();
    }

    public static void drawCommandWaypoints(GraphicsContext gc) {
        // Could probably make this a lot easier
        for (Command command : commands) {
            if (!(command.equals("Sequencial Command Group") || command.equals("Parallel Command Group"))) {
                double totalDistance = 0;

                for (Spline spline : splines) {
                    SplineGenerator splineGen = new SplineGenerator(
                            spline.getStartPose(),
                            Editor.EditorFunctions
                                    .relateTo(spline.getStartPose().getPoint2d(), spline.getFirstControl())
                                    .toVector2d(),
                            Editor.EditorFunctions.relateTo(spline.getEndPose().getPoint2d(), spline.getSecondControl())
                                    .toVector2d(),
                            spline.getEndPose());

                    totalDistance += splineGen.calculateTotalLength(splineGen.getSpline().getPoints());
                }

                double targetDistance = totalDistance * command.getRunAt();

                ArrayList<Point2d> combinedPoints = new ArrayList<>();
                for (Spline spline : splines) {
                    SplineGenerator splineGen = new SplineGenerator(
                            spline.getStartPose(),
                            Editor.EditorFunctions
                                    .relateTo(spline.getStartPose().getPoint2d(), spline.getFirstControl())
                                    .toVector2d(),
                            Editor.EditorFunctions.relateTo(spline.getEndPose().getPoint2d(), spline.getSecondControl())
                                    .toVector2d(),
                            spline.getEndPose());

                    for (Point2d point : splineGen.getSpline().getPoints()) {
                        combinedPoints.add(point);
                    }
                }

                double currentDistance = 0;

                Point2d point = new Point2d();

                for (int i = 0; i < combinedPoints.size() - 1; i++) {
                    double segmentLength = combinedPoints.get(i).toVector2d()
                            .getDistance(combinedPoints.get(i + 1).toVector2d());
                    if (currentDistance + segmentLength >= targetDistance) {
                        point = Editor.EditorFunctions
                                .convertToPixels(
                                        Editor.EditorFunctions.orientateToCanvas(combinedPoints.get(i)));
                        break;
                    }
                    currentDistance += segmentLength;
                }

                gc.moveTo(point.getX(), point.getY());

                gc.setStroke(Color.rgb(0, 0, 0));
                gc.setLineWidth(3);
                gc.setFill(Color.rgb(255, 100, 100));

                gc.strokeOval(point.getX() - (Editor.controlPointSize / 2),
                        point.getY() - (Editor.controlPointSize / 2),
                        Editor.controlPointSize, Editor.controlPointSize);
                gc.fillOval(point.getX() - (Editor.controlPointSize / 2),
                        point.getY() - (Editor.controlPointSize / 2),
                        Editor.controlPointSize, Editor.controlPointSize);
            }
        }
    }

    public static void update() {
        redrawPath(gc);
    }

    public static void updateJSON() {

        if (file != null) {
            try {
                PathingJson.convertPathToJson(new Path(splines, commands), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Rotating is wrong, may be from when called
    public static Point2d getHeading(Pose2d pose, GraphicsContext gc) {
        Point2d topLeft = new Point2d(pose.getX() - ((Editor.robotSize[0] * Editor.pixelPerIn) / 2),
                pose.getY() - ((Editor.robotSize[1] * Editor.pixelPerIn) / 2));
        Point2d topRight = new Point2d(pose.getX() + ((Editor.robotSize[0] * Editor.pixelPerIn) / 2),
                pose.getY() - ((Editor.robotSize[1] * Editor.pixelPerIn) / 2));

        Point2d topLeftRotated = new Point2d(
                pose.getX() + (topLeft.getX() - pose.getX()) * Math.cos(pose.getHeading())
                        - (topLeft.getY() - pose.getY()) * Math.sin(pose.getHeading()),
                pose.getY() + (topLeft.getX() - pose.getX()) * Math.sin(pose.getHeading())
                        + (topLeft.getY() - pose.getY()) * Math.cos(pose.getHeading()));

        Point2d topRightRotated = new Point2d(
                pose.getX() + (topRight.getX() - pose.getX()) * Math.cos(pose.getHeading())
                        - (topRight.getY() - pose.getY()) * Math.sin(pose.getHeading()),
                pose.getY() + (topRight.getX() - pose.getX()) * Math.sin(pose.getHeading())
                        + (topRight.getY() - pose.getY()) * Math.cos(pose.getHeading()));

        gc.setStroke(Color.rgb(0, 0, 0));
        gc.setLineWidth(3);
        Editor.EditorFunctions.drawPoint(gc,
                Editor.EditorFunctions.convertToPixels(Editor.EditorFunctions.orientateToCanvas(
                        Util.vectorLerp(topLeftRotated.toVector2d(), topRightRotated.toVector2d(), .5).toPoint2d())),
                Editor.robotPointSize);
        return Util.vectorLerp(topLeftRotated.toVector2d(), topRightRotated.toVector2d(), .5).toPoint2d();
    }

    public static boolean inRange(Point2d point, double radius) {
        if (point.toVector2d().getMagnitude() <= radius) {
            return true;
        }
        return false;
    }
}