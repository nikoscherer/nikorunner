package GUI;

import java.util.ArrayList;

import GUI.PlaceholderTypes.Command;

public class Path {
    ArrayList<Spline> splines;
    ArrayList<Command> commands;

    public Path(ArrayList<Spline> splines, ArrayList<Command> commands) {
        this.splines = splines;
        this.commands = commands;
    }

    public Spline getSplineIndex(int index) {
        return splines.get(index);
    }

    public Command getCommandIndex(int index) {
        return commands.get(index);
    }

    public ArrayList<Spline> getSplines() {
        return splines;
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }
}