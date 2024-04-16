package GUI.OpenCVNodes;


import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import nikorunnerlib.src.Geometry.Point2d;

public class Node extends VBox {

    NodeType type;

    double xOffset;
    double yOffset;


    float nodeConnectorRadius = 5;

    ArrayList<Point2d> ioPoints = new ArrayList<>();
    
    public Node(NodeType type) {
        if(type == null) {
            try {
                throw new Exception("type cannot be null");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.type = type;

        this.setSpacing(0);


        Label header = new Label(type.getName());
        header.getStyleClass().addAll("node-header");




        // IO
        HBox IO = new HBox();
        IO.setSpacing(10);

        // Inputs
        VBox InputsIO = new VBox();
        InputsIO.setSpacing(5);

        for (int i = 0; i < type.getInputs().length; i++) {
            InputsIO.getChildren().add(addIO(type.getInputAtIndex(i), true));
        }

        // Outputs
        VBox OutputsIO = new VBox();
        OutputsIO.setSpacing(5);

        for (int i = 0; i < type.getOutputs().length; i++) {
            OutputsIO.getChildren().add(addIO(type.getOutputAtIndex(i), false));
        }


        IO.getChildren().addAll(InputsIO, OutputsIO);



        this.getChildren().addAll(header, IO);

        this.getStyleClass().addAll("node");




        this.setOnMousePressed(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                xOffset = e.getX();
                yOffset = e.getY();
            }
        });

        this.setOnMouseDragged(e -> {
            if(e.getButton() == MouseButton.PRIMARY) {
                Point2d difference = null;

                for(int i = 0; i < ioPoints.size(); i++) {
                    difference = new Point2d(ioPoints.get(i).getX() - e.getX(), ioPoints.get(i).getY() - e.getY());

                    if(!inRange(difference, 10)) {
                        difference = null;
                    }
                }
                if(difference == null) {
                    this.setTranslateX(this.getTranslateX() + e.getX() - xOffset);
                    this.setTranslateY(this.getTranslateY() + e.getY() - yOffset);
                }
            }
        });
    }

    boolean editingNode = false;

    private HBox addIO(NodeIO type, boolean input) {

        HBox root = new HBox();
        root.setPadding(new Insets(0, 8, 0, 8));
        root.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(type.getName());
        label.getStyleClass().addAll("node-io-label");


        Circle nodeConnectorBorder;
        Circle nodeConnectorInner;

        StackPane nodeConnector = new StackPane();
        
        if(type.getColorCode() != null) {
            nodeConnectorBorder = new Circle(nodeConnectorRadius, type.getColorCode());

            nodeConnectorInner = new Circle(nodeConnectorRadius - 1, Color.BLACK);

            nodeConnector.getChildren().addAll(nodeConnectorBorder, nodeConnectorInner);

            if(input) {
                root.setAlignment(Pos.CENTER_LEFT);
                root.getChildren().addAll(nodeConnector, label);
            } else {
                root.setAlignment(Pos.CENTER_RIGHT);
                root.getChildren().addAll(label, nodeConnector);
            }

            // not right
            ioPoints.add(
                new Point2d(
                    nodeConnector.getLayoutX(), 
                    nodeConnector.getLayoutY()
            ));
        } else {
            if(type == NodeIO.TEXT) {
                TextField IOText = new TextField();
                IOText.setMaxWidth(40);

                if(input) {
                    root.getChildren().addAll(IOText, label);
                } else {
                    root.getChildren().addAll(label, IOText);
                }

                IOText.textProperty().addListener((observable, oldValue, newValue) -> {
                    if(editingNode == false) {
                        if(oldValue != newValue) {
                            editingNode = true;
                        }
                    }
                });

                IOText.setOnKeyPressed(e -> {
                    if(e.getCode() == KeyCode.ESCAPE || e.getCode() == KeyCode.ENTER) {
                        IOText.getParent().requestFocus();
                        editingNode = false;
                    }
                });
            }
        }

        return root;
    }


    public Point2d getIOConnector(int index) {

        return null;
    }


    private boolean inRange(Point2d point, double radius) {
        if(point.toVector2d().getMagnitude() <= radius) {
            return true;
        }
        return false;
    }
}