package GUI;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import nikorunnerlib.src.Geometry.Point2d;
import nikorunnerlib.src.Geometry.Pose2d;
import nikorunnerlib.src.Geometry.Vector2d;
import nikorunnerlib.src.Other.Util;

public class PathEditor {
    
    static Stage window;
    
    public static Scene editor;


    static ArrayList<Spline> splines;

    // Pathing
    // index, point (p0-p3)
    static int[] selected = {0, 0, 0};
    static Point2d offset = new Point2d(0, 0);


    
    // Constants (MOVE SOME TO CONSTANTS LATER)
    public static final Vector2d fieldCenter = new Point2d(72, 72).toVector2d();
    static final int[] robotSize = {15, 15};

    // Waypoint Sizes
    static final int robotPointSize = 22; // pixels
    static final int controlPointSize = 18; // pixels


    static final double pixelPerIn = 4.861111;

    // Canvas size
    static final int size = 700;
    static final Image centerstage = new Image("imgs/Fields/Centerstage.png", size, size, true, true);




    public static void init(Stage primaryStage) {
        window = primaryStage;

        splines = new ArrayList<>();


        BorderPane root = new BorderPane();

        VBox sideMenu = new VBox();
        sideMenu.setPadding(new Insets(10, 5, 0, 5));
        sideMenu.setSpacing(15);
        sideMenu.setPrefSize(500, 0);
        sideMenu.getStyleClass().addAll("border-fullMenu");

        VBox waypointsMenu = new VBox();
        waypointsMenu.setPrefSize(460, 75);
        waypointsMenu.getStyleClass().addAll("border-color", "border-menu");
        Label waypointsLabel = new Label("Waypoints");
        waypointsLabel.setPadding(new Insets(0, 0, 0, 15));
        waypointsLabel.setPrefSize(200, 60);
        waypointsLabel.getStyleClass().addAll("label-menu");

        waypointsMenu.getChildren().addAll(waypointsLabel);
        sideMenu.getChildren().addAll(waypointsMenu);


        GridPane pathing = new GridPane();
        pathing.setAlignment(Pos.CENTER);
        pathing.setPrefSize(size, size);
        Canvas pathingCanvas = new Canvas(size, size);
        GraphicsContext gc = pathingCanvas.getGraphicsContext2D();

        gc.drawImage(centerstage, 0, 0);
        pathing.add(pathingCanvas, 0, 0);


        root.setTop(General.createMenu(primaryStage));
        root.setRight(sideMenu);
        root.setCenter(pathing);

        editor = new Scene(root, window.getWidth(), window.getHeight());
        editor.getStylesheets().add("GUI/CSS/PathEditor.css");


        // PATHING

        // Field Relative
        Pose2d defaultStartPose = new Pose2d(-12, -18, 0);
        Pose2d defaultEndPose = new Pose2d(12, 18, 0);

        // Off of poses
        Vector2d defaultFirstControl = new Vector2d(10, Math.toRadians(90));
        Vector2d defaultSecondControl = new Vector2d(10, Math.toRadians(-90));

        splines.add(new Spline(defaultStartPose, defaultFirstControl, defaultSecondControl, defaultEndPose));
        redrawPath(gc);


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
                        Point2d firstControlDifference = relateTo(splines.get(i).getStartPose().getPoint2d(), splines.get(i).getFirstControl()).toVector2d().minus(fieldRelativeCursor.toVector2d()).toPoint2d();
                        Point2d secondControlDifference = relateTo(splines.get(i).getEndPose().getPoint2d(), splines.get(i).getSecondControl()).toVector2d().minus(fieldRelativeCursor.toVector2d()).toPoint2d();
                        Point2d endPoseDifference = fieldRelativeCursor.toVector2d().minus(splines.get(i).getEndPose().getVector2d()).toPoint2d();

                        if(inRange(startPoseDifference, (robotPointSize - 5) / pixelPerIn)) {
                            System.out.println("Start Pose");
                            setSelected(1, i, 0);
                        } else if (inRange(firstControlDifference, controlPointSize / pixelPerIn)) {
                            System.out.println("First Control");
                            setSelected(1, i, 1);
                        } else if (inRange(secondControlDifference, controlPointSize / pixelPerIn)) {
                            System.out.println("Second Control");
                            setSelected(1, i, 2);
                        } else if (inRange(endPoseDifference, (robotPointSize - 5) / pixelPerIn)) {
                            System.out.println("End Pose");
                            setSelected(1, i, 3);
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
            System.out.println("redrew");
        });

        pathingCanvas.setOnMouseDragged(e -> {
            if(selected[0] == 1 && e.getButton() != MouseButton.SECONDARY) {
                Point2d fieldRelativeCursor = orientateToField(convertToInches(new Point2d(e.getX(), e.getY())));

                Point2d currentPoint = splines.get(selected[1]).getIndexVector(selected[2]);

                currentPoint = fieldRelativeCursor;

                if(selected[2] == 0) {
                    splines.get(selected[1]).startPose.setPoint2d(currentPoint.getX(), currentPoint.getY());
                } else if (selected[2] == 1) {
                    // turn into non-relative point?
                    Vector2d nonRelative = currentPoint.toVector2d().minus(splines.get(selected[1]).getStartPose().getVector2d());
                    splines.get(selected[1]).firstControl.setXY(nonRelative.getX(), nonRelative.getY());
                } else if (selected[2] == 2) {
                    // turn into non-relative point?
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
                    }
                }

                redrawPath(gc);
            }
        });

        pathingCanvas.setOnMouseReleased(e -> {
            setSelected(0, 0, 0);
        });
    }

    public static void deleteSpline(int index) {
        // if(splines != 0) {
        //     splines.get(index + 1).setStartPose(splines.get(index - 1).getEndPose());
        //     splines.get(index - 1).setEndPose(splines.get(index + 1).getStartPose());
        // }
        if(index > 1) {
            splines.get(index + 1).setStartPose(splines.get(index - 1).getEndPose());
            splines.get(index - 1).setEndPose(splines.get(index + 1).getStartPose());
        } else if (index == 1) {
            splines.get(index - 1).setEndPose(splines.get(index + 1).getStartPose());
        }
        System.out.println(index);

        splines.remove(index);
    }

    public static void setSelected(int a, int b, int c) {
        selected[0] = a;
        selected[1] = b;
        selected[2] = c;
    }

    public static void redrawPath(GraphicsContext gc) {
        gc.clearRect(0, 0, size, size);

        gc.drawImage(centerstage, 0, 0);

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

        Point2d topLeftInPixels = new Point2d(pointInPixels.getX() - ((robotSize[0] * pixelPerIn) / 2), pointInPixels.getY() - ((robotSize[0] * pixelPerIn) / 2));
        gc.strokeRect(topLeftInPixels.getX(), topLeftInPixels.getY(), robotSize[0] * pixelPerIn, robotSize[1] * pixelPerIn);

        // Draw Control to Center
        Point2d relatedPointInPixels = convertToPixels(orientateToCanvas(relateTo(pose.getPoint2d(), controlPoint)));
        gc.setStroke(Color.rgb(255, 255, 255));

        gc.setLineWidth(3);
        gc.beginPath();
        gc.moveTo(pointInPixels.getX(), pointInPixels.getY());
        gc.lineTo(relatedPointInPixels.getX(), relatedPointInPixels.getY());
        gc.stroke();
        gc.closePath();

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


    /* TESTING */



}
