package GUI;

import java.util.ArrayList;

import GUI.Editors.AutoEditor.AutoEditorGUI;
import GUI.Editors.PathEditor.PathEditorGUI;
import GUI.PlaceholderTypes.Command;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuCommands extends MenuBar {

    public MenuCommands(Command command, boolean auto) {

        ArrayList<String> validCommands = Constants.getCommands(auto);

        Menu menu = new Menu(command.getName());

        ArrayList<MenuItem> commandItems = new ArrayList<>();

        for(String vCommand : validCommands) {
            MenuItem item = new MenuItem(vCommand);

            item.setOnAction(e -> {
                command.setName(item.getText());

                if(auto) {
                    AutoEditorGUI.update();
                } else {
                    PathEditorGUI.update();
                }
            });

            commandItems.add(item);
        }

        menu.getStyleClass().addAll("commands-item");

        menu.getItems().addAll(commandItems);

        super(menu);
    }
}
