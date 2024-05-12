package GUI.PlaceholderTypes;

import java.util.ArrayList;

public interface Command {

    default void initialize() {

    }

    default void execute() {

    }

    default void end(boolean interrupted) {

    }

    default boolean isFinished() {
        return false;
    }

    default void setName(String newName) {

    }

    default String getName() {
        return "";
    }

    default void setPath(String path) {

    }

    default String getPath() {
        return "";
    }

    default ArrayList<Command> getCommands() {
        return new ArrayList<Command>();
    }

    default void addCommand(Command command) {

    }

    default void setRunAt(double runAt) {

    }

    default double getRunAt() {
        return 0;
    }
}