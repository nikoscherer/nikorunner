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
import nikorunnerlib.src.Geometry.BiVector2d;
import nikorunnerlib.src.Geometry.Point2d;
import nikorunnerlib.src.Geometry.Pose2d;
import nikorunnerlib.src.Geometry.Vector2d;
import nikorunnerlib.src.Other.Util;
import nikorunnerlib.src.Pathing.PathSegment;

public class PathEditorOLD {

    // DOESNT FULLY WORK :(

    // need to redo once it gets working.


    static Stage window;
    
    public static Scene editor;


    static ArrayList<PathSegment> poses;
    static ArrayList<Vector2d> waypoints;

    // Check if selected, pose or waypoint, index, control point.
    static int[] selected = {0, 0, 0, 0};
    static Point2d offset = new Point2d(0, 0);


    
    // Constants
    public static final Vector2d fieldCenter = new Point2d(72, 72).toVector2d();
    static final int[] robotSize = {15, 15};


    static final double pixelPerIn = 4.861111;

    // Switch to all pixels or inches
    static final int poseSize = 25; // pixels
    static final int waypointSize = 18; // pixels
    static final int controlSize = 15; // pixels
    static final double dragRadius = 2.5; // in



    public static void init(Stage primaryStage) {
        window = primaryStage;

        poses = new ArrayList<>();
        waypoints = new ArrayList<>();


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

        int size = 700;
        GridPane pathing = new GridPane();
        pathing.setAlignment(Pos.CENTER);
        pathing.setPrefSize(size, size);
        Canvas pathingCanvas = new Canvas(size, size);
        GraphicsContext gc = pathingCanvas.getGraphicsContext2D();
        Image centerstage = new Image("imgs/Fields/Centerstage.png", size, size, true, true);
        gc.drawImage(centerstage, 0, 0);
        pathing.add(pathingCanvas, 0, 0);



        // Pathing
        
        // Default Poses
        Pose2d defStartPose = new Pose2d(fieldCenter.getX() - 8, fieldCenter.getY() + 18, 0);
        Pose2d defEndPose = new Pose2d(fieldCenter.getX() + 8, fieldCenter.getY() - 18, 0);

        // NOT relative to robot
        BiVector2d defStartControl = new BiVector2d(0, Util.getOppositeAngle(Math.toRadians(0)), 20);
        BiVector2d defEndControl = new BiVector2d(20, Math.toRadians(-180), 0);

        poses.add(new PathSegment(new Pose2d(orientateToField(defStartPose.getPoint2d()), defStartPose.getHeading()), defStartControl));
        poses.add(new PathSegment(new Pose2d(orientateToField(defEndPose.getPoint2d()), defEndPose.getHeading()), defEndControl));

        // drawSpline(
        //     gc, 
        //     poses.get(0).getPose(), 
        //     new Point2d(poses.get(0).getPose().getX() - poses.get(0).getTangents().getVector2().getX(), 
        //     poses.get(0).getPose().getY() - poses.get(0).getTangents().getVector2().getY()),
        //     new Point2d(poses.get(1).getPose().getX() - poses.get(1).getTangents().getVector1().getX(), 
        //     poses.get(1).getPose().getY() - poses.get(1).getTangents().getVector1().getY()),
        //     poses.get(1).getPose()
        // );

        drawSpline(gc, 
            poses.get(0).getPose(), 
            relateTo(poses.get(0).getPose().getPoint2d(), poses.get(0).getTangents().getVector2()), 
            relateTo(poses.get(1).getPose().getPoint2d(), poses.get(1).getTangents().getVector1()),  
            poses.get(1).getPose()
        );

        drawPose(gc, poses.get(0), true);
        drawPose(gc, poses.get(1), false);


        pathingCanvas.setOnMousePressed(e -> {
            if(e.getButton() ==  MouseButton.PRIMARY && selected[0] == 0) {
                // Clicking twice should make new point, not just once.

                Point2d cursorOnGrid = new Point2d((e.getX() / pixelPerIn) - fieldCenter.getX(), (e.getY() / pixelPerIn) - fieldCenter.getY());

                System.out.println("Position (INCH): " + cursorOnGrid.getX() + ", " + cursorOnGrid.getY());
                System.out.println("Position (PIXEL): " + cursorOnGrid.getX() * pixelPerIn + ", " + cursorOnGrid.getY() * pixelPerIn);

                if(e.getClickCount() == 2) {
                    drawWaypoint(gc, cursorOnGrid);
                    waypoints.add(cursorOnGrid.toVector2d());
                } else if(e.getClickCount() == 1) {
                    for (int i = 1; i <= poses.size(); i++) {
                        Point2d poseDifference = poses.get(i - 1).getPose().getVector2d().minus(cursorOnGrid.toVector2d()).toPoint2d();
                        Point2d controlPoint = new Point2d(poses.get(i - 1).getPose().getX() - poses.get(i - 1).getTangents().getVector2().getX(), 
                        poses.get(i - 1).getPose().getY() - poses.get(i - 1).getTangents().getVector2().getY());
                        Point2d poseDifference2 = controlPoint.toVector2d().minus(cursorOnGrid.toVector2d()).toPoint2d();
    
                        if(Math.abs(poseDifference.getX()) <= dragRadius && Math.abs(poseDifference.getY()) <= dragRadius) {
                            offset = poseDifference;
                            selected[0] = 1;
                            selected[1] = 0;
                            selected[2] = i - 1;
                            selected[3] = 0;
                        } else if (Math.abs(poseDifference2.getX()) <= dragRadius && Math.abs(poseDifference2.getY()) <= dragRadius) {
                            offset = poseDifference2;
                            selected[0] = 1;
                            selected[1] = 0;
                            selected[2] = i - 1;
                            selected[3] = 2;
                        }
                    }
                    for (int j = 1; j <= waypoints.size(); j++) {
                        Point2d poseDifference = waypoints.get(j - 1).minus(cursorOnGrid.toVector2d()).toPoint2d();
    
                        if(Math.abs(poseDifference.getX()) <= dragRadius && Math.abs(poseDifference.getY()) <= dragRadius) {
                            offset = poseDifference;
                            selected[0] = 1;
                            selected[1] = 1;
                            selected[2] = j - 1;
                            selected[3] = 0;
                        }
                    }
                }
            }
        });

        pathingCanvas.setOnMouseDragged(e -> {
            if(selected[0] == 1) {
                Point2d cursorOnGrid = new Point2d((e.getX() / pixelPerIn) - fieldCenter.getX(), (e.getY() / pixelPerIn) - fieldCenter.getY());

                if(selected[1] == 0) {
                    if(selected[3] == 2) {


                        // control point on field
                        Point2d control = new Point2d(
                            poses.get(selected[2]).getPose().getVector2d().getX() - poses.get(selected[2]).getTangents().getVector2().getX(),
                            poses.get(selected[2]).getPose().getVector2d().getY() - poses.get(selected[2]).getTangents().getVector2().getY()
                        );
                        // Point2d control = poses.get(selected[2]).getPose().getVector2d().minus(poses.get(selected[2]).getTangents().getVector2()).toPoint2d();
                        // Point2d control = relateTo(poses.get(selected[2]).getPose().getPoint2d(), poses.get(selected[2]).getTangents().getVector2());

                        // added offset
                        Point2d offsetPoint = new Point2d(cursorOnGrid.getX() - offset.getX(), cursorOnGrid.getY() + offset.getY());

                        // addon + and to vector
                        Vector2d controlVector = new Vector2d(
                            new Point2d(
                                (control.getX() + offsetPoint.getX()) - poses.get(selected[2]).getPose().getX(),
                                (control.getY() - offsetPoint.getY()) - poses.get(selected[2]).getPose().getY()
                                )
                        );
                        // Vector2d controlVector = control.toVector2d().minus(offsetPoint.toVector2d()).minus(poses.get(selected[2]).getPose().getVector2d());

                        System.out.println(control.getX());
                        System.out.println(control.getY());

                        poses.get(selected[2]).getTangents().getVector2().setX(controlVector.getX());
                        poses.get(selected[2]).getTangents().getVector2().setY(controlVector.getY());
                    } else if (selected[3] == 1){

                    } else {
                        poses.get(selected[2]).getPose().setPoint2d(cursorOnGrid.getX() - offset.getX(), cursorOnGrid.getY() - offset.getY());
                    }
                } else {
                    waypoints.get(selected[2]).setXY(cursorOnGrid.getX() - offset.getX(), cursorOnGrid.getY() - offset.getY());
                }
                gc.clearRect(0, 0, size, size);
                gc.drawImage(centerstage, 0, 0);


                // Point2d control = new Point2d((pixelPose.getX() - (waypointSize / 2) - controlVector.getX()), (pixelPose.getY() - (waypointSize / 2)) - controlVector.getY());

                drawSpline(gc, 
                    poses.get(0).getPose(), 
                    relateTo(poses.get(0).getPose().getPoint2d(), poses.get(0).getTangents().getVector2()), 
                    relateTo(poses.get(1).getPose().getPoint2d(), poses.get(1).getTangents().getVector1()),  
                    poses.get(1).getPose()
            );

                drawPose(gc, poses.get(0), true);
                drawPose(gc, poses.get(1), false);

                for (int i = 1; i <= waypoints.size(); i++) {
                    // Needs work
                    drawWaypoint(gc, waypoints.get(i - 1).toPoint2d());
                }
            }
        });

        pathingCanvas.setOnMouseReleased(e -> {
            selected[0] = 0;
            selected[1] = 0;
            selected[2] = 0;
            selected[3] = 0;
        });


        root.setTop(General.createMenu(primaryStage));
        root.setRight(sideMenu);
        root.setCenter(pathing);

        editor = new Scene(root, window.getWidth(), window.getHeight());
        editor.getStylesheets().add("GUI/CSS/PathEditor.css");
    }

    public static void drawPose(GraphicsContext gc, PathSegment segment, boolean startPose) {

        Pose2d pixelPose = new Pose2d(convertToPixels(orientateToCanvas(segment.getPose().getPoint2d())), segment.getPose().getHeading());

        // DRAW robot perimeter (will rotate later)
        if(startPose) {
            gc.setStroke(Color.rgb(31, 215, 43));
        } else {
            gc.setStroke(Color.rgb(255, 71, 83));
        }
        gc.setLineWidth(3);
        gc.setFill(Color.TRANSPARENT);
        Point2d startPoint = new Point2d(pixelPose.getX() - ((robotSize[0] * pixelPerIn) / 2), pixelPose.getY() - ((robotSize[0] * pixelPerIn) / 2));
        gc.strokeRect(startPoint.getX(), startPoint.getY(), robotSize[0] * pixelPerIn, robotSize[1] * pixelPerIn);

        // Draw center point
        gc.setStroke(Color.rgb(0, 0, 0));
        gc.setLineWidth(3);
        if(startPose) {
            gc.setFill(Color.rgb(31, 215, 43));
        } else {
            gc.setFill(Color.rgb(255, 71, 83));
        }
        drawPoint(gc, new Point2d(pixelPose.getX() - (poseSize / 2), pixelPose.getY() - (poseSize / 2)), poseSize);

        // Draw control point
        gc.setStroke(Color.rgb(0, 0, 0));
        gc.setLineWidth(3);
        gc.setFill(Color.rgb(255, 255, 255));

        Point2d controlPoint;
        if(startPose) {
            controlPoint = orientateToCanvas(relateTo(segment.getPose().getPoint2d(), segment.getTangents().getVector2()));
        } else {
            controlPoint = orientateToCanvas(relateTo(segment.getPose().getPoint2d(), segment.getTangents().getVector1()));
        }

        // to pixel and canvas
        controlPoint = new Point2d(
            convertToPixels(controlPoint).toVector2d().getX() - (waypointSize / 2),
            convertToPixels(controlPoint).toVector2d().getY() - (waypointSize / 2)
        );
        drawPoint(gc, controlPoint, waypointSize);
    }

    
    // point needs to from center of field
    public static void drawWaypoint(GraphicsContext gc, Point2d point) {
        Point2d pixelPoint = convertToPixels(orientateToCanvas(point));

        gc.setStroke(Color.rgb(0, 0, 0));
        gc.setLineWidth(3);
        gc.setFill(Color.rgb(220, 220, 220));
        pixelPoint.setX(pixelPoint.getX() - (waypointSize / 2));
        pixelPoint.setY(pixelPoint.getY() - (waypointSize / 2));
        drawPoint(gc, pixelPoint, waypointSize);
    }

    // point needs to from center of field
    public static void drawControl(GraphicsContext gc, Point2d waypoint, BiVector2d controls) {

    }


    // point needs to from center of field
    public static void drawPoint(GraphicsContext gc, Point2d point, int size) {
        gc.strokeOval(point.getX(), point.getY(), size, size);
        gc.fillOval(point.getX(), point.getY(), size, size);
    }


    public static void drawSpline(GraphicsContext gc, Pose2d startPose, Point2d startTangent, Point2d endTangent, Pose2d endPose) {


        // Convert to canvas pixel coordinates
        startTangent = convertToPixels(orientateToCanvas(startTangent));
        endTangent = convertToPixels(orientateToCanvas(endTangent));

        startPose = new Pose2d(convertToPixels(orientateToCanvas(startPose.getPoint2d())), startPose.getHeading());
        endPose = new Pose2d(convertToPixels(orientateToCanvas(endPose.getPoint2d())), endPose.getHeading());

        gc.setStroke(Color.rgb(255, 255, 255));

        gc.setLineWidth(4);
        gc.beginPath();
        gc.moveTo(startPose.getX(), startPose.getY());
        gc.bezierCurveTo(startTangent.getX(), startTangent.getY(), endTangent.getX(), endTangent.getY(), endPose.getX(), endPose.getY());
        gc.stroke();
        gc.closePath();
    }

    public static Point2d relateTo(Point2d relation, Vector2d vector) {
        return new Point2d(relation.getX() + vector.getX(), relation.getY() - vector.getY());
    }

    // INCHES
    public static Point2d orientateToField(Point2d inches) {
        return inches.toVector2d().minus(fieldCenter).toPoint2d();
    }
    public static Point2d orientateToCanvas(Point2d inches) {
        return inches.toVector2d().plus(fieldCenter).toPoint2d();
    }

    public static Point2d convertToInches(Point2d pixels) {
        return pixels.toVector2d().divide(pixelPerIn).toPoint2d();
    }
    public static Point2d convertToPixels(Point2d inches) {
        return inches.toVector2d().times(pixelPerIn).toPoint2d();
    }
}