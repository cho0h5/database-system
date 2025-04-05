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
        DatabaseManager myDatabaseManager = new MyDatabaseManager();
        DatabaseManager mysqlDatabaseManager = new MysqlDatabaseManager();
        DatabaseManager duelDatabaseManager = new DuelDatabaseManager(myDatabaseManager, mysqlDatabaseManager);

        try {
            List<Command> commands = CommandParser.parseCommands(duelDatabaseManager, fileName);

            for (Command command : commands) {
                command.execute();
            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
    }
}
