package org.example;

import java.io.FileNotFoundException;
import java.util.List;

public class App {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Wrong argument");
            return;
        }

        String fileName = args[0];
        DatabaseManager databaseManager = new MyDatabaseManager();

        try {
            List<Command> commands = CommandParser.parseCommands(databaseManager, fileName);

            for (Command command : commands) {
                command.execute();
            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
    }
}
