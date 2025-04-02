package org.example;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

class Field {
    String name;
    int size;

    public Field(String name, int size) {
        this.name = name;
        this.size = size;
    }
}

class Record {
    List<Optional<String>> fields;

    public Record(List<Optional<String>> fields) {
        this.fields = fields;
    }
}

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
