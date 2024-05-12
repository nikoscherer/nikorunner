package GUI.Editors;

import java.util.ArrayList;

import GUI.Path;
import GUI.Spline;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import nikorunnerlib.src.Geometry.Point2d;
import nikorunnerlib.src.Geometry.Pose2d;
import nikorunnerlib.src.Geometry.Vector2d;
import nikorunnerlib.src.Other.Util;

public class Editor {
    // Canvas size
    public static int size = 700;

    // 4.861111
    public static double pixelPerIn = (double) (Editor.size / 144.0);

    // Field image
    public static final Image centerstage = new Image("imgs/Fields/Centerstage.png", Editor.size, Editor.size, true,
            true);

    // Waypoint Sizes
    public static final int robotPointSize = 22; // pixels
    public static final int controlPointSize = 18; // pixels

    // Other
    public static final Vector2d fieldCenter = new Point2d(72, 72).toVector2d();
    public static final double[] robotSize = { 16, 16 };

    public class EditorFunctions {

        public static void redrawPath(GraphicsContext gc, ArrayList<Spline> splines, boolean poses) {

            for (int i = 0; i < splines.size(); i++) {
                Editor.EditorFunctions.drawSpline(gc, splines.get(i));
            }

            for (int i = 0; i < splines.size(); i++) {
                if (splines.size() == 1) {
                    if (poses) {
                        Editor.EditorFunctions.drawRobot(gc, splines.get(i).getStartPose(),
                                splines.get(i).getFirstControl(), true);
                        Editor.EditorFunctions.drawRobot(gc, splines.get(i).getEndPose(),
                                splines.get(i).getSecondControl(),
                                false);
                    }
                } else {
                    if (i == 0 && i == splines.size() - 1) {
                        if (poses) {
                            Editor.EditorFunctions.drawRobot(gc, splines.get(i).getStartPose(),
                                    splines.get(i).getFirstControl(), true);
                            Editor.EditorFunctions.drawRobot(gc, splines.get(i).getEndPose(),
                                    splines.get(i).getSecondControl(), false);
                        }
                        Editor.EditorFunctions.drawWaypoint(gc, splines.get(i).getStartPose().getPoint2d(),
                                splines.get(i - 1).getSecondControl(),
                                splines.get(i).getFirstControl());
                    } else if (i == 0) {
                        if (poses) {
                            Editor.EditorFunctions.drawRobot(gc, splines.get(i).getStartPose(),
                                    splines.get(i).getFirstControl(), true);
                        }
                    } else if (i == splines.size() - 1) {
                        Editor.EditorFunctions.drawWaypoint(gc, splines.get(i).getStartPose().getPoint2d(),
                                splines.get(i - 1).getSecondControl(),
                                splines.get(i).getFirstControl());
                        if (poses) {
                            Editor.EditorFunctions.drawRobot(gc, splines.get(i).getEndPose(),
                                    splines.get(i).getSecondControl(), false);
                        }
                    } else {
                        Editor.EditorFunctions.drawWaypoint(gc, splines.get(i).getStartPose().getPoint2d(),
                                splines.get(i - 1).getSecondControl(),
                                splines.get(i).getFirstControl());
                    }
                }
            }
        }

        public static void redrawAutoPath(GraphicsContext gc, ArrayList<Path> paths) {

            for (int i = 0; i < paths.size(); i++) {
                for (int j = 0; j < paths.get(i).getSplines().size(); j++) {
                    redrawPath(gc, paths.get(i).getSplines(), false);
                }
            }

            for (int i = 0; i < paths.size(); i++) {
                if (i == 0 && i == paths.size() - 1) { // If only 1 path inside auto
                    drawRobot(
                            gc, paths.get(i).getSplineIndex(0).getStartPose(),
                            paths.get(i).getSplineIndex(0).getFirstControl(), 1);

                    drawRobot(
                            gc, paths.get(i).getSplineIndex(paths.get(i).getSplines().size() - 1).getEndPose(),
                            paths.get(i).getSplineIndex(paths.get(i).getSplines().size() - 1).getSecondControl(), -1);
                } else if (i == 0) { // if first path in auto
                    drawRobot(
                            gc, paths.get(i).getSplineIndex(0).getStartPose(),
                            paths.get(i).getSplineIndex(0).getFirstControl(), 1);
                    drawRobot(
                            gc, paths.get(i).getSplineIndex(paths.get(i).getSplines().size() - 1).getEndPose(),
                            paths.get(i).getSplineIndex(paths.get(i).getSplines().size() - 1).getSecondControl(), 0);
                } else if (i == paths.size() - 1) { // if last path in auto
                    drawRobot(
                            gc, paths.get(i).getSplineIndex(0).getStartPose(),
                            paths.get(i).getSplineIndex(0).getFirstControl(), 0);
                    drawRobot(
                            gc, paths.get(i).getSplineIndex(paths.get(i).getSplines().size() - 1).getEndPose(),
                            paths.get(i).getSplineIndex(paths.get(i).getSplines().size() - 1).getSecondControl(), -1);
                } else { // if middle path in auto
                    drawRobot(
                            gc, paths.get(i).getSplineIndex(0).getStartPose(),
                            paths.get(i).getSplineIndex(0).getFirstControl(), 0);
                        
                    drawRobot(
                            gc, paths.get(i).getSplineIndex(paths.get(i).getSplines().size() - 1).getEndPose(),
                            paths.get(i).getSplineIndex(paths.get(i).getSplines().size() - 1).getSecondControl(), 0);
                }
            }
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

            drawPoint(gc, anchorPointInPixels, Editor.controlPointSize);

            // Draw Left/Right
            gc.setStroke(Color.rgb(0, 0, 0));
            gc.setLineWidth(3);
            gc.setFill(Color.rgb(220, 220, 220));

            drawPoint(gc, leftPoint, Editor.controlPointSize);
            drawPoint(gc, rightPoint, Editor.controlPointSize);
        }

        public static void drawSpline(GraphicsContext gc, Spline spline) {
            Point2d firstControl = convertToPixels(
                    orientateToCanvas(relateTo(spline.getStartPose().getPoint2d(), spline.getFirstControl())));
            Point2d secondControl = convertToPixels(
                    orientateToCanvas(relateTo(spline.getEndPose().getPoint2d(), spline.getSecondControl())));

            Point2d firstPoint = convertToPixels(orientateToCanvas(spline.getStartPose().getPoint2d()));
            Point2d secondPoint = convertToPixels(orientateToCanvas(spline.getEndPose().getPoint2d()));

            gc.setStroke(Color.rgb(255, 255, 255));

            gc.setLineWidth(4);
            gc.beginPath();
            gc.moveTo(firstPoint.getX(), firstPoint.getY());
            gc.bezierCurveTo(firstControl.getX(), firstControl.getY(), secondControl.getX(), secondControl.getY(),
                    secondPoint.getX(), secondPoint.getY());
            gc.stroke();
            gc.closePath();
        }

        public static void drawRobot(GraphicsContext gc, Pose2d pose, Vector2d controlPoint, boolean startPose) {
            Point2d updatedPoint = Editor.EditorFunctions.orientateToCanvas(pose.getPoint2d());
            Point2d pointInPixels = updatedPoint.toVector2d().times(Editor.pixelPerIn).toPoint2d();

            // Draw Robot Perimeter
            if (startPose) {
                gc.setStroke(Color.rgb(31, 215, 43));
            } else {
                gc.setStroke(Color.rgb(255, 71, 83));
            }
            gc.setLineWidth(3);
            gc.setFill(Color.TRANSPARENT);

            Point2d topLeftInPixels = new Point2d(
                    pointInPixels.getX() - ((Editor.robotSize[0] * Editor.pixelPerIn) / 2),
                    pointInPixels.getY() - ((Editor.robotSize[1] * Editor.pixelPerIn) / 2));
            Point2d topRightInPixels = new Point2d(
                    pointInPixels.getX() + ((Editor.robotSize[0] * Editor.pixelPerIn) / 2),
                    pointInPixels.getY() - ((Editor.robotSize[1] * Editor.pixelPerIn) / 2));
            Point2d bottomLeftInPixels = new Point2d(
                    pointInPixels.getX() - ((Editor.robotSize[0] * Editor.pixelPerIn) / 2),
                    pointInPixels.getY() + ((Editor.robotSize[1] * Editor.pixelPerIn) / 2));
            Point2d bottomRightInPixels = new Point2d(
                    pointInPixels.getX() + ((Editor.robotSize[0] * Editor.pixelPerIn) / 2),
                    pointInPixels.getY() + ((Editor.robotSize[1] * Editor.pixelPerIn) / 2));

            // Rotate Points (sin and cos may be reversed).
            Point2d topLeftInPixelsRotated = new Point2d(
                    pointInPixels.getX() + (topLeftInPixels.getX() - pointInPixels.getX()) * Math.cos(pose.getHeading())
                            - (topLeftInPixels.getY() - pointInPixels.getY()) * Math.sin(pose.getHeading()),
                    pointInPixels.getY() + (topLeftInPixels.getX() - pointInPixels.getX()) * Math.sin(pose.getHeading())
                            + (topLeftInPixels.getY() - pointInPixels.getY()) * Math.cos(pose.getHeading()));
            Point2d topRightInPixelsRotated = new Point2d(
                    pointInPixels.getX()
                            + (topRightInPixels.getX() - pointInPixels.getX()) * Math.cos(pose.getHeading())
                            - (topRightInPixels.getY() - pointInPixels.getY()) * Math.sin(pose.getHeading()),
                    pointInPixels.getY()
                            + (topRightInPixels.getX() - pointInPixels.getX()) * Math.sin(pose.getHeading())
                            + (topRightInPixels.getY() - pointInPixels.getY()) * Math.cos(pose.getHeading()));
            Point2d bottomLeftInPixelsRotated = new Point2d(
                    pointInPixels.getX()
                            + (bottomLeftInPixels.getX() - pointInPixels.getX()) * Math.cos(pose.getHeading())
                            - (bottomLeftInPixels.getY() - pointInPixels.getY()) * Math.sin(pose.getHeading()),
                    pointInPixels.getY()
                            + (bottomLeftInPixels.getX() - pointInPixels.getX()) * Math.sin(pose.getHeading())
                            + (bottomLeftInPixels.getY() - pointInPixels.getY()) * Math.cos(pose.getHeading()));
            Point2d bottomRightInPixelsRotated = new Point2d(
                    pointInPixels.getX()
                            + (bottomRightInPixels.getX() - pointInPixels.getX()) * Math.cos(pose.getHeading())
                            - (bottomRightInPixels.getY() - pointInPixels.getY()) * Math.sin(pose.getHeading()),
                    pointInPixels.getY()
                            + (bottomRightInPixels.getX() - pointInPixels.getX()) * Math.sin(pose.getHeading())
                            + (bottomRightInPixels.getY() - pointInPixels.getY()) * Math.cos(pose.getHeading()));

            gc.strokeLine(topLeftInPixelsRotated.getX(), topLeftInPixelsRotated.getY(), topRightInPixelsRotated.getX(),
                    topRightInPixelsRotated.getY());
            gc.strokeLine(topRightInPixelsRotated.getX(), topRightInPixelsRotated.getY(),
                    bottomRightInPixelsRotated.getX(),
                    bottomRightInPixelsRotated.getY());
            gc.strokeLine(bottomRightInPixelsRotated.getX(), bottomRightInPixelsRotated.getY(),
                    bottomLeftInPixelsRotated.getX(), bottomLeftInPixelsRotated.getY());
            gc.strokeLine(bottomLeftInPixelsRotated.getX(), bottomLeftInPixelsRotated.getY(),
                    topLeftInPixelsRotated.getX(),
                    topLeftInPixelsRotated.getY());

            // Draw Control to Center
            Point2d relatedPointInPixels = Editor.EditorFunctions.convertToPixels(Editor.EditorFunctions
                    .orientateToCanvas(Editor.EditorFunctions.relateTo(pose.getPoint2d(), controlPoint)));
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
            if (startPose) {
                gc.setFill(Color.rgb(31, 215, 43));
            } else {
                gc.setFill(Color.rgb(255, 71, 83));
            }
            Point2d heading = Util
                    .vectorLerp(topLeftInPixelsRotated.toVector2d(), topRightInPixelsRotated.toVector2d(), .5)
                    .toPoint2d();
            Editor.EditorFunctions.drawPoint(gc, heading, Editor.controlPointSize - 5);

            // Draw Robot Center
            gc.setStroke(Color.rgb(0, 0, 0));
            gc.setLineWidth(3);
            if (startPose) {
                gc.setFill(Color.rgb(31, 215, 43));
            } else {
                gc.setFill(Color.rgb(255, 71, 83));
            }
            Editor.EditorFunctions.drawPoint(gc, pointInPixels, Editor.robotPointSize);

            // Draw Control point
            gc.setStroke(Color.rgb(0, 0, 0));
            gc.setLineWidth(3);
            gc.setFill(Color.rgb(220, 220, 220));
            Editor.EditorFunctions.drawPoint(gc, relatedPointInPixels, Editor.controlPointSize);
        }

        public static void drawRobot(GraphicsContext gc, Pose2d pose, Vector2d controlPoint, int type) {
            Point2d updatedPoint = Editor.EditorFunctions.orientateToCanvas(pose.getPoint2d());
            Point2d pointInPixels = updatedPoint.toVector2d().times(Editor.pixelPerIn).toPoint2d();

            // Draw Robot Perimeter
            if (type == 1) {
                gc.setStroke(Color.rgb(31, 215, 43));
            } else if (type == -1) {
                gc.setStroke(Color.rgb(255, 71, 83));
            } else {
                gc.setStroke(Color.rgb(75, 75, 75));
            }

            gc.setLineWidth(3);
            gc.setFill(Color.TRANSPARENT);

            Point2d topLeftInPixels = new Point2d(
                    pointInPixels.getX() - ((Editor.robotSize[0] * Editor.pixelPerIn) / 2),
                    pointInPixels.getY() - ((Editor.robotSize[1] * Editor.pixelPerIn) / 2));
            Point2d topRightInPixels = new Point2d(
                    pointInPixels.getX() + ((Editor.robotSize[0] * Editor.pixelPerIn) / 2),
                    pointInPixels.getY() - ((Editor.robotSize[1] * Editor.pixelPerIn) / 2));
            Point2d bottomLeftInPixels = new Point2d(
                    pointInPixels.getX() - ((Editor.robotSize[0] * Editor.pixelPerIn) / 2),
                    pointInPixels.getY() + ((Editor.robotSize[1] * Editor.pixelPerIn) / 2));
            Point2d bottomRightInPixels = new Point2d(
                    pointInPixels.getX() + ((Editor.robotSize[0] * Editor.pixelPerIn) / 2),
                    pointInPixels.getY() + ((Editor.robotSize[1] * Editor.pixelPerIn) / 2));

            // Rotate Points (sin and cos may be reversed).
            Point2d topLeftInPixelsRotated = new Point2d(
                    pointInPixels.getX() + (topLeftInPixels.getX() - pointInPixels.getX()) * Math.cos(pose.getHeading())
                            - (topLeftInPixels.getY() - pointInPixels.getY()) * Math.sin(pose.getHeading()),
                    pointInPixels.getY() + (topLeftInPixels.getX() - pointInPixels.getX()) * Math.sin(pose.getHeading())
                            + (topLeftInPixels.getY() - pointInPixels.getY()) * Math.cos(pose.getHeading()));
            Point2d topRightInPixelsRotated = new Point2d(
                    pointInPixels.getX()
                            + (topRightInPixels.getX() - pointInPixels.getX()) * Math.cos(pose.getHeading())
                            - (topRightInPixels.getY() - pointInPixels.getY()) * Math.sin(pose.getHeading()),
                    pointInPixels.getY()
                            + (topRightInPixels.getX() - pointInPixels.getX()) * Math.sin(pose.getHeading())
                            + (topRightInPixels.getY() - pointInPixels.getY()) * Math.cos(pose.getHeading()));
            Point2d bottomLeftInPixelsRotated = new Point2d(
                    pointInPixels.getX()
                            + (bottomLeftInPixels.getX() - pointInPixels.getX()) * Math.cos(pose.getHeading())
                            - (bottomLeftInPixels.getY() - pointInPixels.getY()) * Math.sin(pose.getHeading()),
                    pointInPixels.getY()
                            + (bottomLeftInPixels.getX() - pointInPixels.getX()) * Math.sin(pose.getHeading())
                            + (bottomLeftInPixels.getY() - pointInPixels.getY()) * Math.cos(pose.getHeading()));
            Point2d bottomRightInPixelsRotated = new Point2d(
                    pointInPixels.getX()
                            + (bottomRightInPixels.getX() - pointInPixels.getX()) * Math.cos(pose.getHeading())
                            - (bottomRightInPixels.getY() - pointInPixels.getY()) * Math.sin(pose.getHeading()),
                    pointInPixels.getY()
                            + (bottomRightInPixels.getX() - pointInPixels.getX()) * Math.sin(pose.getHeading())
                            + (bottomRightInPixels.getY() - pointInPixels.getY()) * Math.cos(pose.getHeading()));

            gc.strokeLine(topLeftInPixelsRotated.getX(), topLeftInPixelsRotated.getY(), topRightInPixelsRotated.getX(),
                    topRightInPixelsRotated.getY());
            gc.strokeLine(topRightInPixelsRotated.getX(), topRightInPixelsRotated.getY(),
                    bottomRightInPixelsRotated.getX(),
                    bottomRightInPixelsRotated.getY());
            gc.strokeLine(bottomRightInPixelsRotated.getX(), bottomRightInPixelsRotated.getY(),
                    bottomLeftInPixelsRotated.getX(), bottomLeftInPixelsRotated.getY());
            gc.strokeLine(bottomLeftInPixelsRotated.getX(), bottomLeftInPixelsRotated.getY(),
                    topLeftInPixelsRotated.getX(),
                    topLeftInPixelsRotated.getY());

            // Draw Control to Center
            Point2d relatedPointInPixels = Editor.EditorFunctions.convertToPixels(Editor.EditorFunctions
                    .orientateToCanvas(Editor.EditorFunctions.relateTo(pose.getPoint2d(), controlPoint)));
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
            if (type == 1) {
                gc.setFill(Color.rgb(31, 215, 43));
            } else if (type == -1) {
                gc.setFill(Color.rgb(255, 71, 83));
            } else {
                gc.setFill(Color.rgb(75, 75, 75));
            }
            Point2d heading = Util
                    .vectorLerp(topLeftInPixelsRotated.toVector2d(), topRightInPixelsRotated.toVector2d(), .5)
                    .toPoint2d();
            Editor.EditorFunctions.drawPoint(gc, heading, Editor.controlPointSize - 5);

            // Draw Robot Center
            gc.setStroke(Color.rgb(0, 0, 0));
            gc.setLineWidth(3);
            if (type == 1) {
                gc.setFill(Color.rgb(31, 215, 43));
            } else if (type == -1) {
                gc.setFill(Color.rgb(255, 71, 83));
            } else {
                gc.setFill(Color.rgb(75, 75, 75));
            }
            Editor.EditorFunctions.drawPoint(gc, pointInPixels, Editor.robotPointSize);

            // Draw Control point
            gc.setStroke(Color.rgb(0, 0, 0));
            gc.setLineWidth(3);
            gc.setFill(Color.rgb(220, 220, 220));
            Editor.EditorFunctions.drawPoint(gc, relatedPointInPixels, Editor.controlPointSize);
        }

        public static void drawPoint(GraphicsContext gc, Point2d point, int size) {
            gc.strokeOval(point.getX() - (size / 2), point.getY() - (size / 2), size, size);
            gc.fillOval(point.getX() - (size / 2), point.getY() - (size / 2), size, size);
        }

        public static Point2d relateTo(Point2d relatedPoint, Vector2d vector) {
            return relatedPoint.toVector2d().plus(vector).toPoint2d();
        }

        public static Point2d orientateToField(Point2d inches) {
            return new Point2d(inches.getX() - fieldCenter.getX(), fieldCenter.getY() - inches.getY());
        }

        public static Point2d orientateToCanvas(Point2d inches) {
            return new Point2d(fieldCenter.getX() + inches.toVector2d().getX(),
                    fieldCenter.getY() - inches.toVector2d().getY());
        }

        public static Point2d convertToInches(Point2d pixels) {
            return pixels.toVector2d().divide(Editor.pixelPerIn).toPoint2d();
        }

        public static Point2d convertToPixels(Point2d inches) {
            return inches.toVector2d().times(Editor.pixelPerIn).toPoint2d();
        }
    }
}
