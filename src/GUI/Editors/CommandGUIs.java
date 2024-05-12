package GUI.Editors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;

import GUI.Constants;
import GUI.MenuCommands;
import GUI.PathingJson;
import GUI.Editors.AutoEditor.AutoEditor;
import GUI.Editors.AutoEditor.AutoEditorGUI;
import GUI.Editors.PathEditor.PathEditor;
import GUI.Editors.PathEditor.PathEditorGUI;
import GUI.PlaceholderTypes.Command;
import GUI.PlaceholderTypes.CommandBase;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class CommandGUIs {

    public static ArrayList<Node> commandsGUI(boolean showCommands, ArrayList<Command> commands, HBox commandsLabel,
            boolean auto) {
        ArrayList<Node> commandsBoxNodes = new ArrayList<>();
        commandsBoxNodes.add(commandsLabel);

        AutoEditor.paths = new ArrayList<>();
        VBox commandsVBox = createFULLCommands(commands, auto);

        if (showCommands) {
            commandsBoxNodes.add(commandsVBox);
        }

        if (auto) {
            AutoEditor.updateJSON();
        }

        return commandsBoxNodes;
    }

    // Loops through command tree
    public static VBox createFULLCommands(ArrayList<Command> commands, boolean auto) {
        VBox commandsBox = new VBox();
        commandsBox.setPadding(new Insets(5, 5, 5, 5));

        int index = 0;

        for (Command command : commands) {
            index = index + 1;

            MenuCommands menuCommands = new MenuCommands(command, auto);

            VBox commandBox = new VBox();
            commandBox.setPadding(new Insets(1, 1, 1, 1));

            HBox commandLabel = new HBox();
            commandLabel.getChildren().addAll(menuCommands);


            Region deleteSeperator = new Region();
            HBox.setHgrow(deleteSeperator, Priority.SOMETIMES);

            Image commandTrashImage = new Image("imgs/trash.png");
            ImageView commandTrashImageView = new ImageView(commandTrashImage);
            commandTrashImageView.setFitWidth(15);
            commandTrashImageView.setFitHeight(16);
            Button commandTrash = new Button();
            commandTrash.setGraphic(commandTrashImageView);
            commandTrash.getStyleClass().addAll("pathing-trash");

            commandTrash.setOnAction(e -> {
                // TODO delete command
            });

            commandBox.setPadding(new Insets(7));
            commandBox.getStyleClass().addAll("commands-group");

            commandsBox.getChildren().addAll(commandBox);

            commandBox.getChildren().addAll(commandLabel);

            // if Command is a command group
            if (command.getName().equals("Sequencial Command Group")
                    || command.getName().equals("Parallel Command Group")) {

                VBox temp = new VBox();
                temp.getChildren().addAll(menuCommands, createFULLCommands(command.getCommands(), auto));

                Region commandButtonSeperator = new Region();
                HBox.setHgrow(commandButtonSeperator, Priority.ALWAYS);

                // TODO change to image
                Button newCommandButton = new Button("+");
                newCommandButton.getStyleClass().addAll("button-option");

                newCommandButton.setOnAction(e -> {
                    CommandBase newCommand = new CommandBase(Constants.getCommands(auto).get(0));
                    command.addCommand(newCommand);

                    PathEditorGUI.update();
                    AutoEditorGUI.update();
                });

                HBox commandBoxLabel = new HBox();

                commandBoxLabel.getChildren().addAll(temp, commandButtonSeperator, newCommandButton);

                VBox transfer = new VBox();
                transfer.getChildren().addAll(commandBoxLabel);

                commandBox = transfer;
            } else if (command.getName().equals("Pathing Command")) {
                Menu menu = new Menu();

                if (!command.getPath().equals("")) {
                    menu.setText(command.getPath());
                } else {
                    menu.setText(Constants.getPathingCommands().get(Constants.getPathingCommands().size() - 1));
                }

                for (String pathType : Constants.getPathingCommands()) {
                    MenuItem item = new MenuItem(pathType);

                    item.setOnAction(e -> {
                        menu.setText(item.getText());

                        command.setPath(item.getText());
                        AutoEditorGUI.update();
                    });

                    menu.getItems().addAll(item);
                }

                try {
                    AutoEditor.paths.add(PathingJson.convertToPath(new File(Constants.pathDir + menu.getText())));
                } catch (IOException | ParseException e1) {
                    e1.printStackTrace();
                }

                MenuBar pathMenu = new MenuBar(menu);

                commandLabel.getChildren().addAll(pathMenu);
            } else if(!auto) {
                Slider runAtSlider = new Slider(0, 1, command.getRunAt());
                runAtSlider.setPadding(new Insets(10, 10, 10, 10));
                HBox.setHgrow(runAtSlider, Priority.SOMETIMES);
                runAtSlider.setShowTickMarks(true);
                runAtSlider.setShowTickLabels(true);
                runAtSlider.setMajorTickUnit(.1);
                runAtSlider.setMinorTickCount(1);

                runAtSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
                    runAtSlider.valueProperty().set(Math.floor(newValue.doubleValue() / .05) * .05);
                    command.setRunAt(runAtSlider.getValue());
                    PathEditor.update();
                });

                commandBox.setSpacing(10);
                commandBox.getChildren().addAll(runAtSlider);
            }

            commandLabel.getChildren().addAll(deleteSeperator, commandTrash);
        }

        if (auto) {
            AutoEditor.redrawAuto(AutoEditor.gc);
        } else {
            PathEditor.updateJSON();
        }

        return commandsBox;
    }
}