package GUI;

import java.io.File;
import java.util.ArrayList;

public class Constants {
    public static int[] defaultSize = { 1400, 800 };

    // TODO make saveable, probably in a settings.json
    public static String pathingDir = 
        "C:\\Users\\8101\\Documents\\FTC8101\\2024-2025\\2024 Offseason\\TeamCode\\src\\main\\java\\org\\firstinspires\\ftc\\teamcode\\pathing\\";
    public static String pathDir = 
        pathingDir + "paths\\";
    public static String autoDir = 
        pathingDir + "autos\\";

    public static String commandsDir =
        "C:\\Users\\8101\\Documents\\FTC8101\\2024-2025\\2024 Offseason\\TeamCode\\src\\main\\java\\org\\firstinspires\\ftc\\teamcode\\common\\commandbase";


    public static ArrayList<String> getCommands(boolean auto) {
        ArrayList<String> commands = new ArrayList<>();

        if(auto) {
            commands.add("Pathing Command");
        }

        File cmdsDirectory = new File(Constants.commandsDir);
        if (!cmdsDirectory.exists()) {
            cmdsDirectory.mkdir();
        }
        
        getDirCommands(new File(Constants.commandsDir), commands);

        commands.add("Sequencial Command Group");
        commands.add("Parallel Command Group");

        for(int i = 0; i < commands.size(); i++) {
            commands.set(i, commands.get(i).replace(".java", "").replace(".json", ""));
        }

        return commands;
    }

    private static void getDirCommands(File directory, ArrayList<String> commands) {
        File[] dirCommands = directory.listFiles();

        for(int i = 0; i < dirCommands.length; i++) {
            if(dirCommands[i].isDirectory()) {

                // TODO make it so you can set the unallowed file path
                if(!(dirCommands[i].getName().equals("teleopcommands"))) {
                    getDirCommands(dirCommands[i], commands);
                }
            } else if(!dirCommands[i].getName().equals("NikoRunnerCommand.java")) {
                commands.add(dirCommands[i].getName());
            }
        }
    }

    public static ArrayList<String> getPathingCommands() {
        ArrayList<String> pathingCommands = new ArrayList<>();

        getDirCommands(new File(pathDir), pathingCommands);

        return pathingCommands;
    }
}