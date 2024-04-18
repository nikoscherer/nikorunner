package GUI;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Scrollable;

import org.json.simple.parser.ParseException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import nikorunnerlib.src.Geometry.Point2d;
import nikorunnerlib.src.Geometry.Pose2d;
import nikorunnerlib.src.Geometry.Vector2d;
import nikorunnerlib.src.Other.Util;

// DID NOT CHANGE PATHING CODE, ONLY GUI

// TODO when path has greater than 2 splines, and you delete a spline it moves the control points randomly
// TODO when path has greater than 2 splines and last splines start control point is deleted, error is given.

// TODO when user returns to editor, sometimes they cannot rename or delete the path that was previously opened.
public class PathEditor {
    
    static Stage window;
    
    public static Scene editor;


    static ArrayList<Spline> splines;

    // Pathing
    // selected, index, point (p0-p3), if pose then heading
    static int[] selected = {0, 0, 0, 0};
    static Point2d offset = new Point2d(0, 0);


    
    // Constants (MOVE SOME TO CONSTANTS LATER)
    public static final Vector2d fieldCenter = new Point2d(72, 72).toVector2d();
    static final double[] robotSize = {16, 16};

    // Waypoint Sizes
    static final int robotPointSize = 22; // pixels
    static final int controlPointSize = 18; // pixels




    // Canvas size
    static int size = 700;

    //4.8611111
    static double pixelPerIn = (double) (size / 144.0);

    static final Image centerstage = new Image("imgs/Fields/Centerstage.png", size, size, true, true);


    static File file;



    // window settings
    static int borderWidth = 5;

    static boolean windowScaling = false;
    static int location = 0;

    public static void init(Stage primaryStage, File pathFile)  {
        window = primaryStage;
        file = pathFile;

        splines = new ArrayList<>();


        BorderPane root = new BorderPane();

        Label pathName = new Label("Unknown Path");
        pathName.getStyleClass().addAll("label-pathName");

        if(pathFile != null) {
            pathName.setText(pathFile.getName().replace("_", " ").replace(".json", ""));

            // Field Relative
            Pose2d defaultStartPose = new Pose2d(-12, -18, Math.toRadians(0));
            Pose2d defaultEndPose = new Pose2d(12, 18, Math.toRadians(0));
            
            // Off of poses
            Vector2d defaultFirstControl = new Vector2d(10, Math.toRadians(90));
            Vector2d defaultSecondControl = new Vector2d(10, Math.toRadians(-90));
            
            splines.add(new Spline(defaultStartPose, defaultFirstControl, defaultSecondControl, defaultEndPose));
        } else {
            try {
                splines = PathingJson.convertToPath(pathFile);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
                System.err.println("Path file does not exist");
            }

        }

        StackPane pathing = new StackPane();
        Canvas pathingCanvas = new Canvas(size, size);
        GraphicsContext gc = pathingCanvas.getGraphicsContext2D();

        gc.drawImage(centerstage, 0, 0);
        pathing.getChildren().addAll(pathingCanvas);

        redrawPath(gc);



        VBox sideMenu = new VBox();
        sideMenu.setPrefWidth(500);
        VBox.setVgrow(sideMenu, Priority.ALWAYS);
        sideMenu.getStyleClass().addAll("sideMenu");

        sideMenu.setPadding(new Insets(10, 10, 10, 10));
        sideMenu.setSpacing(10);

        HBox sideMenuBar = new HBox();
        sideMenuBar.setAlignment(Pos.CENTER_LEFT);
        sideMenuBar.setSpacing(10);
        // sideMenuBar.getStyleClass().addAll("editing");

        Image backImage = new Image("imgs/back.png");
        ImageView backImageView = new ImageView(backImage);
        backImageView.setFitWidth(25);
        backImageView.setFitHeight(25);
        Button backButton = new Button();
        backButton.setGraphic(backImageView);
        backButton.getStyleClass().addAll("button-back");

        sideMenuBar.getChildren().addAll(backButton, pathName);



        VBox waypointsBox = new VBox();
        HBox.setHgrow(waypointsBox, Priority.ALWAYS);
        waypointsBox.setPadding(new Insets(10, 10, 10, 10));
        waypointsBox.setSpacing(10);
        waypointsBox.setAlignment(Pos.CENTER_LEFT);
        waypointsBox.getStyleClass().addAll("waypoints-box");

        Label waypointsLabel = new Label("Waypoints");
        waypointsLabel.setPadding(new Insets(1, 5, 1, 5));
        waypointsLabel.getStyleClass().addAll("waypoints-label");


        // waypoint

        // x coordinate
        // y coordinate
        // control magnitudes
        // control direction

        // waypoint test
        VBox waypointBox = new VBox();
        waypointBox.setSpacing(6);
        waypointBox.setPadding(new Insets(5, 5, 5, 5));
        waypointBox.getStyleClass().addAll("waypoint-coord-box");

        Label waypoint1Label = new Label("Waypoint 1");
        waypoint1Label.setPadding(new Insets(5, 5, 5, 10));
        waypoint1Label.getStyleClass().addAll("waypoint-label");

        HBox waypoint1Coordinates = new HBox();
        waypoint1Coordinates.setSpacing(10);
        waypoint1Coordinates.setPadding(new Insets(5, 5, 5, 5));
        waypoint1Coordinates.setAlignment(Pos.CENTER);
        waypoint1Coordinates.getStyleClass().addAll("waypoint-coord-box");

        StackPane waypointXPane = new StackPane();
        Label waypointXLabel = new Label("X Position (in):");
        waypointXLabel.setPadding(new Insets(5, 5, 5, 5));
        waypointXLabel.setTranslateX(-20);
        waypointXLabel.setTranslateY(-12 - (10 - 2));
        waypointXLabel.getStyleClass().addAll("waypoint-coord-label");
        TextField waypointX = new TextField("0");
        waypointX.setPadding(new Insets(10, 10, 10, 10));
        waypointX.getStyleClass().addAll("waypoint-coord-textfield");

        StackPane waypointYPane = new StackPane();
        Label waypointYLabel = new Label("Y Position (in):");
        waypointYLabel.setPadding(new Insets(5, 5, 5, 5));
        waypointYLabel.setTranslateX(-20);
        waypointYLabel.setTranslateY(-12 - (10 - 2));
        waypointYLabel.getStyleClass().addAll("waypoint-coord-label");
        TextField waypointY = new TextField("0");
        waypointY.setPadding(new Insets(10, 10, 10, 10));
        waypointY.getStyleClass().addAll("waypoint-coord-textfield");

        waypointXPane.getChildren().addAll(waypointX, waypointXLabel);
        waypointYPane.getChildren().addAll(waypointY, waypointYLabel);

        waypoint1Coordinates.getChildren().addAll(waypointXPane, waypointYPane);


        waypointBox.getChildren().addAll(waypoint1Label, waypoint1Coordinates);

        waypointsBox.getChildren().addAll(waypointsLabel, waypointBox);


        sideMenu.getChildren().addAll(sideMenuBar, waypointsBox);
        


        root.setTop(WindowsMenu.createMenu(primaryStage));
        root.setCenter(pathing);
        root.setLeft(sideMenu);

        editor = new Scene(root, window.getWidth(), window.getHeight());
        editor.setFill(Color.TRANSPARENT);
        editor.getStylesheets().add("GUI/CSS/PathEditor.css");




        backButton.setOnAction(e -> {
            window.setScene(Menu.menu);
            init(window, null);
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
                    editor.setCursor(Cursor.NW_RESIZE);
                    location = 0;
                } else if (e.getSceneX() >= window.getWidth() - borderWidth && e.getSceneY() <= borderWidth) { // grabbed top right
                    editor.setCursor(Cursor.NE_RESIZE);
                    location = 2;
                } else if (e.getSceneX() >= window.getWidth() - borderWidth
                        && e.getSceneY() >= window.getHeight() - borderWidth) { // grabbed bottom right
                    editor.setCursor(Cursor.SE_RESIZE);
                    location = 4;
                } else if (e.getSceneX() <= borderWidth && e.getSceneY() >= window.getHeight() - borderWidth) { // grabbed bottom left
                    editor.setCursor(Cursor.SW_RESIZE);
                    location = 6;
                } else if (e.getSceneY() <= borderWidth) { // grabbed top
                    editor.setCursor(Cursor.N_RESIZE);
                    location = 1;
                } else if (e.getSceneX() >= window.getWidth() - borderWidth) { // grabbed right
                    editor.setCursor(Cursor.E_RESIZE);
                    location = 3;
                } else if (e.getSceneY() >= window.getHeight() - borderWidth) { // grabbed bottom
                    editor.setCursor(Cursor.S_RESIZE);
                    location = 5;
                } else if (e.getSceneX() <= borderWidth) { // grabbed left
                    editor.setCursor(Cursor.W_RESIZE);
                    location = 7;
                }
            }
        });

        root.setOnMouseReleased(e -> {
            // set bool to false
            editor.setCursor(Cursor.DEFAULT);
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


        // on click
        pathingCanvas.setOnMousePressed(e -> {
            Point2d fieldRelativeCursor = orientateToField(convertToInches(new Point2d(e.getX(), e.getY())));

            if(e.getButton() == MouseButton.PRIMARY && selected[0] == 0) {
                if(e.getClickCount() == 2) {
                    Spline lastWaypoint = splines.get(splines.size() - 1);
                    Spline waypoint = new Spline(
                        lastWaypoint.getEndPose(),
                        new Vector2d(10, Util.getOppositeAngle(lastWaypoint.getSecondControl().getDirection())), 
                        new Vector2d(10, Math.toRadians(0)), 
                        new Pose2d(fieldRelativeCursor, Math.toRadians(0))
                    );
                    splines.add(waypoint);
                } else if(e.getClickCount() == 1) { // Move Point
                    for (int i = 0; i < splines.size(); i++) {

                        Point2d startPoseDifference = fieldRelativeCursor.toVector2d().minus(splines.get(i).getStartPose().getVector2d()).toPoint2d();
                        Point2d startPoseHeadingDifference = fieldRelativeCursor.toVector2d().minus(getHeading(splines.get(i).getStartPose(), gc).toVector2d()).toPoint2d();
                        Point2d firstControlDifference = relateTo(splines.get(i).getStartPose().getPoint2d(), splines.get(i).getFirstControl()).toVector2d().minus(fieldRelativeCursor.toVector2d()).toPoint2d();
                        Point2d secondControlDifference = relateTo(splines.get(i).getEndPose().getPoint2d(), splines.get(i).getSecondControl()).toVector2d().minus(fieldRelativeCursor.toVector2d()).toPoint2d();
                        Point2d endPoseHeadingDifference = fieldRelativeCursor.toVector2d().minus(getHeading(splines.get(i).getEndPose(), gc).toVector2d()).toPoint2d();
                        Point2d endPoseDifference = fieldRelativeCursor.toVector2d().minus(splines.get(i).getEndPose().getVector2d()).toPoint2d();

                        System.out.println(startPoseHeadingDifference.toVector2d().getMagnitude());
                        System.out.println(getHeading(splines.get(i).getStartPose(), gc).getX());
                        System.out.println(getHeading(splines.get(i).getStartPose(), gc).getY());

                        if(inRange(startPoseDifference, (robotPointSize - 5) / pixelPerIn)) {
                            setSelected(1, i, 0, 0);
                        } else if (inRange(firstControlDifference, controlPointSize / pixelPerIn)) {
                            setSelected(1, i, 1, 0);
                        } else if (inRange(secondControlDifference, controlPointSize / pixelPerIn)) {
                            setSelected(1, i, 2, 0);
                        } else if (inRange(endPoseDifference, (robotPointSize - 5) / pixelPerIn)) {
                            setSelected(1, i, 3, 0);
                        }
                        
                        if(i == 0 && splines.size() == 1) {
                            if(inRange(startPoseHeadingDifference, (robotPointSize - 5) / pixelPerIn)) {
                                setSelected(1, i, 0, 1);
                            } else if(inRange(endPoseHeadingDifference, (robotPointSize - 5) / pixelPerIn)) {
                                setSelected(1, i, 0, 1);
                            }
                        } else if(i == 0){
                            if(inRange(startPoseHeadingDifference, (robotPointSize - 5) / pixelPerIn)) {
                                setSelected(1, i, 0, 1);
                            }
                        } else if (i == splines.size() - 1) {
                            if(inRange(endPoseHeadingDifference, (robotPointSize - 5) / pixelPerIn)) {
                                setSelected(1, i, 0, 1);
                            }
                        }

                        offset = fieldRelativeCursor;
                    }
                }
            } else if (e.getButton() == MouseButton.SECONDARY) {
                // NEEDS EDITING
                for (int i = 0; i < splines.size(); i++) {

                    Point2d startPoseDifference = fieldRelativeCursor.toVector2d().minus(splines.get(i).getStartPose().getVector2d()).toPoint2d();
                    Point2d firstControlDifference = relateTo(splines.get(i).getStartPose().getPoint2d(), splines.get(i).getFirstControl()).toVector2d().minus(fieldRelativeCursor.toVector2d()).toPoint2d();
                    Point2d secondControlDifference = relateTo(splines.get(i).getEndPose().getPoint2d(), splines.get(i).getSecondControl()).toVector2d().minus(fieldRelativeCursor.toVector2d()).toPoint2d();
                    Point2d endPoseDifference = fieldRelativeCursor.toVector2d().minus(splines.get(i).getEndPose().getVector2d()).toPoint2d();

                    if(i > 0 && i < splines.size()) {
                        if(inRange(startPoseDifference, (robotPointSize - 5) / pixelPerIn)) {
                            deleteSpline(i);
                        } else if (inRange(firstControlDifference, controlPointSize / pixelPerIn)) {
                            deleteSpline(i);
                        } else if (inRange(secondControlDifference, controlPointSize / pixelPerIn)) {
                            deleteSpline(i);
                        } else if (inRange(endPoseDifference, (robotPointSize - 5) / pixelPerIn)) {
                            deleteSpline(i);
                        }
                    }
                }
            }
            redrawPath(gc);
        });

        pathingCanvas.setOnMouseDragged(e -> {
            if(selected[0] == 1 && e.getButton() != MouseButton.SECONDARY) {
                Point2d fieldRelativeCursor = orientateToField(convertToInches(new Point2d(e.getX(), e.getY())));

                Point2d currentPoint = splines.get(selected[1]).getIndexVector(selected[2]);

                currentPoint = fieldRelativeCursor;

                if(selected[2] == 0) {
                    splines.get(selected[1]).startPose.setPoint2d(currentPoint.getX(), currentPoint.getY());
                } else if (selected[2] == 1) {
                    Vector2d nonRelative = currentPoint.toVector2d().minus(splines.get(selected[1]).getStartPose().getVector2d());
                    splines.get(selected[1]).firstControl.setXY(nonRelative.getX(), nonRelative.getY());
                } else if (selected[2] == 2) {
                    Vector2d nonRelative = currentPoint.toVector2d().minus(splines.get(selected[1]).getEndPose().getVector2d());
                    splines.get(selected[1]).secondControl.setXY(nonRelative.getX(), nonRelative.getY());
                } else if(selected[2] == 3) {
                    splines.get(selected[1]).endPose.setPoint2d(currentPoint.getX(), currentPoint.getY());
                }

                // (make pathing continuous)
                if(splines.size() > 1) {
                    if(selected[1] != 0 || (selected[2] == 2 && selected[1] == 0)) {
                        if(selected[2] == 1) {
                            splines.get(selected[1] - 1).getSecondControl().setDirection(Util.getOppositeAngle(splines.get(selected[1]).getFirstControl().getDirection()));
                        } else if(selected[2] == 2 && (selected[1] + 1 < splines.size())) {
                            splines.get(selected[1] + 1).getFirstControl().setDirection(Util.getOppositeAngle(splines.get(selected[1]).getSecondControl().getDirection()));
                        }

                        if(selected[2] == 0) {
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

        if(index != 0 && splines.size() != 2) {
            splines.get(index + 1).setStartPose(splines.get(index - 1).getEndPose());
            splines.get(index - 1).setEndPose(splines.get(index + 1).getStartPose());
            splines.get(index + 1).setFirstControl(new Vector2d(splines.get(index - 1).getSecondControl().getMagnitude(), Util.getOppositeAngle(splines.get(index - 1).getSecondControl().getDirection())));
            splines.get(index - 1).setSecondControl(new Vector2d(splines.get(index + 1).getFirstControl().getMagnitude(), Util.getOppositeAngle(splines.get(index - 1).getFirstControl().getDirection())));
        }

        splines.remove(index);
    }

    public static void setSelected(int a, int b, int c, int d) {
        selected[0] = a;
        selected[1] = b;
        selected[2] = c;
        selected[3] = d;
    }

    public static void redrawPath(GraphicsContext gc) {
        gc.clearRect(0, 0, size, size);

        gc.drawImage(centerstage, 0, 0, size, size);

        for (int i = 0; i < splines.size(); i++) {
            drawSpline(gc, splines.get(i));
        }

        for (int i = 0; i < splines.size(); i++) {
            if(splines.size() == 1) {
                drawRobot(gc, splines.get(i).getStartPose(), splines.get(i).getFirstControl(), true);
                drawRobot(gc, splines.get(i).getEndPose(), splines.get(i).getSecondControl(), false);
            } else {
                if (i == 0 && i == splines.size() - 1) {
                    drawRobot(gc, splines.get(i).getStartPose(), splines.get(i).getFirstControl(), true);
                    drawRobot(gc, splines.get(i).getEndPose(), splines.get(i).getSecondControl(), false);
                    drawWaypoint(gc, splines.get(i).getStartPose().getPoint2d(), splines.get(i - 1).getSecondControl(), splines.get(i).getFirstControl());
                } else if (i == 0) {
                    drawRobot(gc, splines.get(i).getStartPose(), splines.get(i).getFirstControl(), true);
                } else if (i == splines.size() - 1) {
                    drawWaypoint(gc, splines.get(i).getStartPose().getPoint2d(), splines.get(i - 1).getSecondControl(), splines.get(i).getFirstControl());
                    drawRobot(gc, splines.get(i).getEndPose(), splines.get(i).getSecondControl(), false);
                } else {
                    drawWaypoint(gc, splines.get(i).getStartPose().getPoint2d(), splines.get(i - 1).getSecondControl(), splines.get(i).getFirstControl());
                }
            }
        }

        updateJSON();
    }

    public static void updateJSON() {

        if(file != null) {
            try {
                PathingJson.convertPathToJson(splines, file);
            } catch (IOException e) {
                
                e.printStackTrace();
            }
        }
    }

    // Rotating is wrong, may be from when called
    public static Point2d getHeading(Pose2d pose, GraphicsContext gc) {
        Point2d topLeft = new Point2d(pose.getX() - ((robotSize[0] * pixelPerIn) / 2), pose.getY() - ((robotSize[1] * pixelPerIn) / 2));
        Point2d topRight = new Point2d(pose.getX() + ((robotSize[0] * pixelPerIn) / 2), pose.getY() - ((robotSize[1] * pixelPerIn) / 2));

        Point2d topLeftRotated = new Point2d(
            pose.getX() + (topLeft.getX() - pose.getX()) * Math.cos(pose.getHeading()) - (topLeft.getY() - pose.getY()) * Math.sin(pose.getHeading()), 
            pose.getY() + (topLeft.getX() - pose.getX()) * Math.sin(pose.getHeading()) + (topLeft.getY() - pose.getY()) * Math.cos(pose.getHeading())
        );

        Point2d topRightRotated = new Point2d(
            pose.getX() + (topRight.getX() - pose.getX()) * Math.cos(pose.getHeading()) - (topRight.getY() - pose.getY()) * Math.sin(pose.getHeading()), 
            pose.getY() + (topRight.getX() - pose.getX()) * Math.sin(pose.getHeading()) + (topRight.getY() - pose.getY()) * Math.cos(pose.getHeading())
        );
        
        gc.setStroke(Color.rgb(0, 0, 0));
        gc.setLineWidth(3);
        drawPoint(gc, convertToPixels(orientateToCanvas(Util.vectorLerp(topLeftRotated.toVector2d(), topRightRotated.toVector2d(), .5).toPoint2d())), robotPointSize);
        return Util.vectorLerp(topLeftRotated.toVector2d(), topRightRotated.toVector2d(), .5).toPoint2d();
    }

    public static void drawRobot(GraphicsContext gc, Pose2d pose, Vector2d controlPoint, boolean startPose) {
        Point2d updatedPoint = orientateToCanvas(pose.getPoint2d());
        Point2d pointInPixels = updatedPoint.toVector2d().times(pixelPerIn).toPoint2d();

        // Draw Robot Perimeter
        if(startPose) {
            gc.setStroke(Color.rgb(31, 215, 43));
        } else {
            gc.setStroke(Color.rgb(255, 71, 83));
        }
        gc.setLineWidth(3);
        gc.setFill(Color.TRANSPARENT);

        Point2d topLeftInPixels = new Point2d(pointInPixels.getX() - ((robotSize[0] * pixelPerIn) / 2), pointInPixels.getY() - ((robotSize[1] * pixelPerIn) / 2));
        Point2d topRightInPixels = new Point2d(pointInPixels.getX() + ((robotSize[0] * pixelPerIn) / 2), pointInPixels.getY() - ((robotSize[1] * pixelPerIn) / 2));
        Point2d bottomLeftInPixels = new Point2d(pointInPixels.getX() - ((robotSize[0] * pixelPerIn) / 2), pointInPixels.getY() + ((robotSize[1] * pixelPerIn) / 2));
        Point2d bottomRightInPixels = new Point2d(pointInPixels.getX() + ((robotSize[0] * pixelPerIn) / 2), pointInPixels.getY() + ((robotSize[1] * pixelPerIn) / 2));

        // Rotate Points (sin and cos may be reversed).
        Point2d topLeftInPixelsRotated = new Point2d(
            pointInPixels.getX() + (topLeftInPixels.getX() - pointInPixels.getX()) * Math.cos(pose.getHeading()) - (topLeftInPixels.getY() - pointInPixels.getY()) * Math.sin(pose.getHeading()), 
            pointInPixels.getY() + (topLeftInPixels.getX() - pointInPixels.getX()) * Math.sin(pose.getHeading()) + (topLeftInPixels.getY() - pointInPixels.getY()) * Math.cos(pose.getHeading())
        );
        Point2d topRightInPixelsRotated = new Point2d(
            pointInPixels.getX() + (topRightInPixels.getX() - pointInPixels.getX()) * Math.cos(pose.getHeading()) - (topRightInPixels.getY() - pointInPixels.getY()) * Math.sin(pose.getHeading()), 
            pointInPixels.getY() + (topRightInPixels.getX() - pointInPixels.getX()) * Math.sin(pose.getHeading()) + (topRightInPixels.getY() - pointInPixels.getY()) * Math.cos(pose.getHeading())
        );
        Point2d bottomLeftInPixelsRotated = new Point2d(
            pointInPixels.getX() + (bottomLeftInPixels.getX() - pointInPixels.getX()) * Math.cos(pose.getHeading()) - (bottomLeftInPixels.getY() - pointInPixels.getY()) * Math.sin(pose.getHeading()), 
            pointInPixels.getY() + (bottomLeftInPixels.getX() - pointInPixels.getX()) * Math.sin(pose.getHeading()) + (bottomLeftInPixels.getY() - pointInPixels.getY()) * Math.cos(pose.getHeading())
        );
        Point2d bottomRightInPixelsRotated = new Point2d(
            pointInPixels.getX() + (bottomRightInPixels.getX() - pointInPixels.getX()) * Math.cos(pose.getHeading()) - (bottomRightInPixels.getY() - pointInPixels.getY()) * Math.sin(pose.getHeading()), 
            pointInPixels.getY() + (bottomRightInPixels.getX() - pointInPixels.getX()) * Math.sin(pose.getHeading()) + (bottomRightInPixels.getY() - pointInPixels.getY()) * Math.cos(pose.getHeading())
        );

        gc.strokeLine(topLeftInPixelsRotated.getX(), topLeftInPixelsRotated.getY(), topRightInPixelsRotated.getX(), topRightInPixelsRotated.getY());
        gc.strokeLine(topRightInPixelsRotated.getX(), topRightInPixelsRotated.getY(), bottomRightInPixelsRotated.getX(), bottomRightInPixelsRotated.getY());
        gc.strokeLine(bottomRightInPixelsRotated.getX(), bottomRightInPixelsRotated.getY(), bottomLeftInPixelsRotated.getX(), bottomLeftInPixelsRotated.getY());
        gc.strokeLine(bottomLeftInPixelsRotated.getX(), bottomLeftInPixelsRotated.getY(), topLeftInPixelsRotated.getX(), topLeftInPixelsRotated.getY());

        // Draw Control to Center
        Point2d relatedPointInPixels = convertToPixels(orientateToCanvas(relateTo(pose.getPoint2d(), controlPoint)));
        gc.setStroke(Color.rgb(255, 255, 255));

        gc.setLineWidth(3);
        gc.beginPath();
        gc.moveTo(pointInPixels.getX(), pointInPixels.getY());
        gc.lineTo(relatedPointInPixels.getX(), relatedPointInPixels.getY());
        gc.stroke();
        gc.closePath();

        // Draw Heading
        gc.setStroke(Color.rgb(0, 0, 0));
        gc.setLineWidth(3);
        if(startPose) {
            gc.setFill(Color.rgb(31, 215, 43));
        } else {
            gc.setFill(Color.rgb(255, 71, 83));
        }
        Point2d heading = Util.vectorLerp(topLeftInPixelsRotated.toVector2d(), topRightInPixelsRotated.toVector2d(), .5).toPoint2d();
        drawPoint(gc, heading, controlPointSize - 5);

        // Draw Robot Center
        gc.setStroke(Color.rgb(0, 0, 0));
        gc.setLineWidth(3);
        if(startPose) {
            gc.setFill(Color.rgb(31, 215, 43));
        } else {
            gc.setFill(Color.rgb(255, 71, 83));
        }
        drawPoint(gc, pointInPixels, robotPointSize);

        // Draw Control point
        gc.setStroke(Color.rgb(0, 0, 0));
        gc.setLineWidth(3);
        gc.setFill(Color.rgb(220, 220, 220));
        drawPoint(gc, relatedPointInPixels, controlPointSize);
    }

    public static void drawWaypoint(GraphicsContext gc, Point2d anchor, Vector2d leftVector, Vector2d rightVector) {
        Point2d anchorPointInPixels = convertToPixels(orientateToCanvas(anchor));

        Point2d leftPoint = convertToPixels(orientateToCanvas(relateTo(anchor, leftVector)));
        Point2d rightPoint = convertToPixels(orientateToCanvas(relateTo(anchor, rightVector)));

        // Draw Line
        gc.setStroke(Color.rgb(255, 255, 255));

        gc.setLineWidth(3);
        gc.beginPath();
        gc.moveTo(leftPoint.getX(), leftPoint.getY());
        gc.lineTo(rightPoint.getX(), rightPoint.getY());
        gc.stroke();
        gc.closePath();


        // Draw Middle
        gc.setStroke(Color.rgb(0, 0, 0));
        gc.setLineWidth(3);
        gc.setFill(Color.rgb(220, 220, 220));

        drawPoint(gc, anchorPointInPixels, controlPointSize);


        // Draw Left/Right
        gc.setStroke(Color.rgb(0, 0, 0));
        gc.setLineWidth(3);
        gc.setFill(Color.rgb(220, 220, 220));

        drawPoint(gc, leftPoint, controlPointSize);
        drawPoint(gc, rightPoint, controlPointSize);
    }
 
    public static void drawSpline(GraphicsContext gc, Spline spline) {
        Point2d firstControl = convertToPixels(orientateToCanvas(relateTo(spline.getStartPose().getPoint2d(), spline.getFirstControl())));
        Point2d secondControl = convertToPixels(orientateToCanvas(relateTo(spline.getEndPose().getPoint2d(), spline.getSecondControl())));

        Point2d firstPoint = convertToPixels(orientateToCanvas(spline.getStartPose().getPoint2d()));
        Point2d secondPoint = convertToPixels(orientateToCanvas(spline.getEndPose().getPoint2d()));


        gc.setStroke(Color.rgb(255, 255, 255));

        gc.setLineWidth(4);
        gc.beginPath();
        gc.moveTo(firstPoint.getX(), firstPoint.getY());
        gc.bezierCurveTo(firstControl.getX(), firstControl.getY(), secondControl.getX(), secondControl.getY(), secondPoint.getX(), secondPoint.getY());
        gc.stroke();
        gc.closePath();
    }

    public static void drawPoint(GraphicsContext gc, Point2d point, int size) {
        gc.strokeOval(point.getX() - (size / 2), point.getY() - (size / 2), size, size);
        gc.fillOval(point.getX() - (size / 2), point.getY() - (size / 2), size, size);
    }


    public static Point2d relateTo(Point2d relatedPoint, Vector2d vector) {
        return relatedPoint.toVector2d().plus(vector).toPoint2d();
    }

    public static boolean inRange(Point2d point, double radius) {
        if(point.toVector2d().getMagnitude() <= radius) {
            return true;
        }
        return false;
    }


    public static Point2d orientateToField(Point2d inches) {
        return new Point2d(inches.getX() - fieldCenter.getX(), fieldCenter.getY() - inches.getY());
    }
    public static Point2d orientateToCanvas(Point2d inches) {
        return new Point2d(fieldCenter.getX() + inches.toVector2d().getX(), fieldCenter.getY() - inches.toVector2d().getY());
    }


    public static Point2d convertToInches(Point2d pixels) {
        return pixels.toVector2d().divide(pixelPerIn).toPoint2d();
    }
    public static Point2d convertToPixels(Point2d inches) {
        return inches.toVector2d().times(pixelPerIn).toPoint2d();
    }
}