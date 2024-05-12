package GUI.Editors.PathEditor;

import java.util.ArrayList;

import GUI.Spline;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import nikorunnerlib.src.Other.Util;

public class WaypointGUIs {

    private static int defaultPadding = 5;

    public static VBox createPose(ArrayList<Spline> splines, int splineIndex, boolean start) {
        VBox waypointBox = new VBox();
        waypointBox.setSpacing(6);
        waypointBox.setPadding(new Insets(defaultPadding, defaultPadding, defaultPadding, defaultPadding));
        waypointBox.getStyleClass().addAll("waypoint-coord-box");

        // needs to change
        Label waypointLabel = new Label((start ? "Start" : "End") + " Pose");
        waypointLabel.setPadding(new Insets(defaultPadding, defaultPadding, 0, 10));
        waypointLabel.getStyleClass().addAll("sidebar-selection-label");

        HBox waypointCoordinates = new HBox();
        waypointCoordinates.setSpacing(10);
        waypointCoordinates.setPadding(new Insets(0, defaultPadding, defaultPadding, defaultPadding));
        waypointCoordinates.setAlignment(Pos.CENTER);
        waypointCoordinates.getStyleClass().addAll("waypoint-coord-box");

        StackPane waypointXPane = new StackPane();
        Label waypointXLabel = new Label("X Position (in):");
        waypointXLabel.setPadding(new Insets(defaultPadding, defaultPadding, defaultPadding, defaultPadding));
        waypointXLabel.setTranslateX(-20);
        waypointXLabel.setTranslateY(-12 - (10 - 2));
        waypointXLabel.getStyleClass().addAll("waypoint-coord-label");
        TextField waypointX = new TextField(
                start ? Double.toString(splines.get(splineIndex).getStartPose().getX())
                        : Double.toString(splines.get(splineIndex).getEndPose().getX()));
        waypointX.setPadding(new Insets(10, 10, 10, 10));
        waypointX.getStyleClass().addAll("waypoint-coord-textfield");

        StackPane waypointYPane = new StackPane();
        Label waypointYLabel = new Label("Y Position (in):");
        waypointYLabel.setPadding(new Insets(defaultPadding, defaultPadding, defaultPadding, defaultPadding));
        waypointYLabel.setTranslateX(-20);
        waypointYLabel.setTranslateY(-12 - (10 - 2));
        waypointYLabel.getStyleClass().addAll("waypoint-coord-label");
        TextField waypointY = new TextField(start ? Double.toString(splines.get(splineIndex).getStartPose().getY())
                : Double.toString(splines.get(splineIndex).getEndPose().getY()));
        waypointY.setPadding(new Insets(10, 10, 10, 10));
        waypointY.getStyleClass().addAll("waypoint-coord-textfield");

        waypointXPane.getChildren().addAll(waypointX, waypointXLabel);
        waypointYPane.getChildren().addAll(waypointY, waypointYLabel);

        waypointCoordinates.getChildren().addAll(waypointXPane, waypointYPane);

        HBox controlBox = new HBox();
        controlBox.setPadding(new Insets(0, 2, 10, 2));
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setSpacing(10);
        controlBox.getStyleClass().addAll("waypoint-coord-box");

        StackPane controlMagPane = new StackPane();
        Label controlMagLabel = new Label("Magnitude: ");
        controlMagLabel.setPadding(new Insets(defaultPadding, defaultPadding, defaultPadding, defaultPadding));
        controlMagLabel.setTranslateX(-35);
        controlMagLabel.setTranslateY(-12 - (defaultPadding - 2));
        controlMagLabel.getStyleClass().addAll("waypoint-control-label");
        TextField controlMag = new TextField(
                start ? Double.toString(splines.get(splineIndex).getFirstControl().getMagnitude())
                        : Double.toString(splines.get(splineIndex).getSecondControl().getMagnitude()));
        controlMag.setPadding(new Insets(defaultPadding, defaultPadding, defaultPadding, defaultPadding));
        controlMag.getStyleClass().addAll("waypoint-coord-textfield");

        StackPane controlDirPane = new StackPane();
        Label controlDirLabel = new Label("Direction: ");
        controlDirLabel.setPadding(new Insets(defaultPadding, defaultPadding, defaultPadding, defaultPadding));
        controlDirLabel.setTranslateX(-35);
        controlDirLabel.setTranslateY(-12 - (defaultPadding - 2));
        controlDirLabel.getStyleClass().addAll("waypoint-control-label");
        TextField controlDir = new TextField(
                start ? Double.toString(Math.toDegrees(splines.get(splineIndex).getFirstControl().getDirection()))
                        : Double.toString(Math.toDegrees(splines.get(splineIndex).getSecondControl().getDirection())));
        controlDir.setPadding(new Insets(defaultPadding, defaultPadding, defaultPadding, defaultPadding));
        controlDir.getStyleClass().addAll("waypoint-coord-textfield");

        controlMagPane.getChildren().addAll(controlMag, controlMagLabel);
        controlDirPane.getChildren().addAll(controlDir, controlDirLabel);

        controlBox.getChildren().addAll(controlMagPane, controlDirPane);

        waypointBox.getChildren().addAll(waypointLabel, waypointCoordinates, controlBox);

        waypointX.textProperty().addListener((obs, newValue, oldValue) -> {
            if (oldValue != newValue) {
                if (waypointX.getText() != null && waypointY.getText().replace("-", "").length() > 0) {
                    if (waypointX.getText().replace("-", "").length() > 0) {
                        if (start) {
                            splines.get(splineIndex).getStartPose().setX(Double.parseDouble(waypointX.getText()));
                        } else {
                            splines.get(splineIndex).getEndPose().setX(Double.parseDouble(waypointX.getText()));
                        }
                        PathEditor.redrawPath(PathEditor.gc);
                    }
                }
            }
        });

        waypointY.textProperty().addListener((obs, newValue, oldValue) -> {
            if (oldValue != newValue) {
                if (waypointY.getText() != null && waypointY.getText().length() > 0) {
                    if (waypointY.getText().replace("-", "").length() > 0) {
                        if (start) {
                            splines.get(splineIndex).getStartPose().setY(Double.parseDouble(waypointY.getText()));
                        } else {
                            splines.get(splineIndex).getEndPose().setY(Double.parseDouble(waypointY.getText()));
                        }
                        PathEditor.redrawPath(PathEditor.gc);
                    }
                }
            }
        });

        controlMag.textProperty().addListener((obs, newValue, oldValue) -> {
            if (oldValue != newValue) {
                if (controlMag.getText() != null && controlMag.getText().length() > 0) {
                    if (controlMag.getText().replace("-", "").length() > 0) {
                        if (start) {
                            splines.get(splineIndex).getFirstControl()
                                    .setMagnitude(Double.parseDouble(controlMag.getText()));
                        } else {
                            splines.get(splineIndex).getSecondControl()
                                    .setMagnitude(Double.parseDouble(controlMag.getText()));
                        }
                        PathEditor.redrawPath(PathEditor.gc);
                    }
                }
            }
        });

        controlDir.textProperty().addListener((obs, newValue, oldValue) -> {
            if (oldValue != newValue) {
                if (controlDir.getText() != null && controlDir.getText().length() > 0) {
                    if (controlDir.getText().replace("-", "").length() > 0) {
                        if (start) {
                            splines.get(splineIndex).getFirstControl()
                                    .setDirection(Math.toRadians(Double.parseDouble(controlDir.getText())));
                        } else {
                            splines.get(splineIndex).getSecondControl()
                                    .setDirection(Math.toRadians(Double.parseDouble(controlDir.getText())));
                        }
                        PathEditor.redrawPath(PathEditor.gc);
                    }
                }
            }
        });

        return waypointBox;
    }

    public static VBox createWaypoint(ArrayList<Spline> splines, int splineIndex) {
        VBox waypointBox = new VBox();
        waypointBox.setSpacing(6);
        waypointBox.setPadding(new Insets(defaultPadding, defaultPadding, defaultPadding, defaultPadding));
        waypointBox.getStyleClass().addAll("waypoint-coord-box");

        // needs to change
        Label waypointLabel = new Label("Waypoint " + Integer.toString(splineIndex));
        waypointLabel.setPadding(new Insets(defaultPadding, defaultPadding, 0, 10));
        waypointLabel.getStyleClass().addAll("sidebar-selection-label");

        HBox waypointCoordinates = new HBox();
        waypointCoordinates.setSpacing(10);
        waypointCoordinates.setPadding(new Insets(0, defaultPadding, defaultPadding, defaultPadding));
        waypointCoordinates.setAlignment(Pos.CENTER);
        waypointCoordinates.getStyleClass().addAll("waypoint-coord-box");

        StackPane waypointXPane = new StackPane();
        Label waypointXLabel = new Label("X Position (in):");
        waypointXLabel.setPadding(new Insets(defaultPadding, defaultPadding, defaultPadding, defaultPadding));
        waypointXLabel.setTranslateX(-20);
        waypointXLabel.setTranslateY(-12 - (10 - 2));
        waypointXLabel.getStyleClass().addAll("waypoint-coord-label");
        TextField waypointX = new TextField(Double.toString(splines.get(splineIndex).getStartPose().getX()));
        waypointX.setPadding(new Insets(10, 10, 10, 10));
        waypointX.getStyleClass().addAll("waypoint-coord-textfield");

        StackPane waypointYPane = new StackPane();
        Label waypointYLabel = new Label("Y Position (in):");
        waypointYLabel.setPadding(new Insets(defaultPadding, defaultPadding, defaultPadding, defaultPadding));
        waypointYLabel.setTranslateX(-20);
        waypointYLabel.setTranslateY(-12 - (10 - 2));
        waypointYLabel.getStyleClass().addAll("waypoint-coord-label");
        TextField waypointY = new TextField(Double.toString(splines.get(splineIndex).getStartPose().getY()));
        waypointY.setPadding(new Insets(10, 10, 10, 10));
        waypointY.getStyleClass().addAll("waypoint-coord-textfield");

        waypointXPane.getChildren().addAll(waypointX, waypointXLabel);
        waypointYPane.getChildren().addAll(waypointY, waypointYLabel);

        waypointCoordinates.getChildren().addAll(waypointXPane, waypointYPane);

        HBox controlBox = new HBox();
        controlBox.setPadding(new Insets(0, 2, 10, 2));
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setSpacing(10);
        controlBox.getStyleClass().addAll("waypoint-coord-box");

        StackPane controlMag1Pane = new StackPane();
        Label controlMag1Label = new Label("First Control Magnitude: ");
        controlMag1Label.setPadding(new Insets(defaultPadding, defaultPadding, defaultPadding, defaultPadding));
        controlMag1Label.setTranslateX(-13);
        controlMag1Label.setTranslateY(-12 - (defaultPadding - 2));
        controlMag1Label.getStyleClass().addAll("waypoint-control-label");
        TextField controlMag1 = new TextField(
                Double.toString(splines.get(splineIndex - 1).getSecondControl().getMagnitude()));
        controlMag1.setPadding(new Insets(defaultPadding, defaultPadding, defaultPadding, defaultPadding));
        controlMag1.getStyleClass().addAll("waypoint-coord-textfield");

        StackPane controlMag2Pane = new StackPane();
        Label controlMag2Label = new Label("Second Control Magnitude: ");
        controlMag2Label.setPadding(new Insets(defaultPadding, defaultPadding, defaultPadding, defaultPadding));
        controlMag2Label.setTranslateX(-13);
        controlMag2Label.setTranslateY(-12 - (defaultPadding - 2));
        controlMag2Label.getStyleClass().addAll("waypoint-control-label");
        TextField controlMag2 = new TextField(
                Double.toString(splines.get(splineIndex).getFirstControl().getMagnitude()));
        controlMag2.setPadding(new Insets(defaultPadding, defaultPadding, defaultPadding, defaultPadding));
        controlMag2.getStyleClass().addAll("waypoint-coord-textfield");

        StackPane controlDirPane = new StackPane();
        Label controlDirLabel = new Label("Direction: ");
        controlDirLabel.setPadding(new Insets(defaultPadding, defaultPadding, defaultPadding, defaultPadding));
        controlDirLabel.setTranslateX(-35);
        controlDirLabel.setTranslateY(-12 - (defaultPadding - 2));
        controlDirLabel.getStyleClass().addAll("waypoint-control-label");
        TextField controlDir = new TextField(
                Double.toString(Math.toDegrees(splines.get(splineIndex).getFirstControl().getDirection())));
        controlDir.setPadding(new Insets(defaultPadding, defaultPadding, defaultPadding, defaultPadding));
        controlDir.getStyleClass().addAll("waypoint-coord-textfield");

        controlMag1Pane.getChildren().addAll(controlMag1, controlMag1Label);
        controlMag2Pane.getChildren().addAll(controlMag2, controlMag2Label);

        controlDirPane.getChildren().addAll(controlDir, controlDirLabel);

        controlBox.getChildren().addAll(controlMag1Pane, controlMag2Pane, controlDirPane);

        waypointBox.getChildren().addAll(waypointLabel, waypointCoordinates, controlBox);

        waypointX.textProperty().addListener((obs, newValue, oldValue) -> {
            if (oldValue != newValue) {
                if (waypointX.getText() != null && waypointY.getText().replace("-", "").length() > 0) {
                    if (waypointX.getText().replace("-", "").length() > 0) {
                        splines.get(splineIndex - 1).getEndPose().setX(Double.parseDouble(waypointX.getText()));
                        splines.get(splineIndex).getStartPose().setX(Double.parseDouble(waypointX.getText()));
                        PathEditor.redrawPath(PathEditor.gc);
                    }
                }
            }
        });

        waypointY.textProperty().addListener((obs, newValue, oldValue) -> {
            if (oldValue != newValue) {
                if (waypointY.getText() != null && waypointY.getText().length() > 0) {
                    if (waypointY.getText().replace("-", "").length() > 0) {
                        splines.get(splineIndex - 1).getEndPose().setY(Double.parseDouble(waypointY.getText()));
                        splines.get(splineIndex).getStartPose().setY(Double.parseDouble(waypointY.getText()));
                        PathEditor.redrawPath(PathEditor.gc);
                    }
                }
            }
        });

        controlMag1.textProperty().addListener((obs, newValue, oldValue) -> {
            if (oldValue != newValue) {
                if (controlMag1.getText() != null && controlMag1.getText().length() > 0) {
                    if (waypointY.getText().replace("-", "").length() > 0) {
                        splines.get(splineIndex - 1).getSecondControl()
                                .setMagnitude(Double.parseDouble(controlMag1.getText()));
                        PathEditor.redrawPath(PathEditor.gc);
                    }
                }
            }
        });

        controlMag2.textProperty().addListener((obs, newValue, oldValue) -> {
            if (oldValue != newValue) {
                if (controlMag2.getText() != null && controlMag2.getText().length() > 0) {
                    if (waypointY.getText().replace("-", "").length() > 0) {
                        splines.get(splineIndex).getFirstControl()
                                .setMagnitude(Double.parseDouble(controlMag2.getText()));
                        PathEditor.redrawPath(PathEditor.gc);
                    }
                }
            }
        });

        controlDir.textProperty().addListener((obs, newValue, oldValue) -> {
            if (oldValue != newValue) {
                if (controlDir.getText() != null && controlDir.getText().length() > 0) {
                    if (waypointY.getText().replace("-", "").length() > 0) {
                        splines.get(splineIndex - 1).getSecondControl().setDirection(
                                Util.getOppositeAngle(Math.toRadians(Double.parseDouble(controlDir.getText()))));
                        splines.get(splineIndex).getFirstControl()
                                .setDirection(Math.toRadians(Double.parseDouble(controlDir.getText())));
                        PathEditor.redrawPath(PathEditor.gc);
                    }
                }
            }
        });

        return waypointBox;
    }
}