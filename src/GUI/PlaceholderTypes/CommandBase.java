package GUI.PlaceholderTypes;

import java.util.ArrayList;

public class CommandBase implements Command {
    String name;
    String path;

    double runAt;

    ArrayList<Command> commands;

    public CommandBase(String name) {
        this.name = name;
        this.path = "";
        this.commands = new ArrayList<Command>();
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getName() {
        return name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    public void setRunAt(double runAt) {
        this.runAt = runAt;
    }

    public double getRunAt() {
        return runAt;
    }
}
