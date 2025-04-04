package org.example;

import java.io.FileNotFoundException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

interface Command {
    void execute();
}

class CreateTableCommand implements Command {
    private final DatabaseManager databaseManager;
    private final String fileName;
    private final List<Field> fields;

    public CreateTableCommand(DatabaseManager databaseManager, String fileName, List<Field> fields) {
        this.databaseManager = databaseManager;
        this.fileName = fileName;
        this.fields = fields;
    }

    public void execute() {
        databaseManager.createTable(fileName, fields);
    }
}

class InsertRecordCommand implements Command {
    private final DatabaseManager databaseManager;
    private final String fileName;
    private final List<Record> records;

    public InsertRecordCommand(DatabaseManager databaseManager, String fileName, List<Record> records) {
        this.databaseManager = databaseManager;
        this.fileName = fileName;
        this.records = records;
    }

    public void execute() {
        databaseManager.insertRecords(fileName, records);
    }
}

class SearchFieldCommand implements Command {
    private final DatabaseManager databaseManager;
    private final String fileName;
    private final String fieldName;

    public SearchFieldCommand(DatabaseManager databaseManager, String fileName, String fieldName) {
        this.databaseManager = databaseManager;
        this.fileName = fileName;
        this.fieldName = fieldName;
    }

    public void execute() {
        databaseManager.searchField(fileName, fieldName);
    }
}

class SearchRecordCommand implements Command {
    private final DatabaseManager databaseManager;
    private final String fileName;
    private final String fieldName;
    private final String minValue;
    private final String maxValue;

    public SearchRecordCommand(DatabaseManager databaseManager, String fileName, String fieldName, String minValue,
            String maxValue) {
        this.databaseManager = databaseManager;
        this.fileName = fileName;
        this.fieldName = fieldName;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public void execute() {
        databaseManager.searchRecord(fileName, fieldName, minValue, maxValue);
    }
}

class CommandParser {
    public static List<Command> parseCommands(DatabaseManager databaseManager, String filePath)
            throws FileNotFoundException {
        final List<Command> commands = new ArrayList<>();
        final Scanner scanner = new Scanner(new File(filePath));
        final int commandCount = nextValidLine(scanner).map(Integer::parseInt).orElseThrow();
        for (int i = 0; i < commandCount && scanner.hasNextLine(); i++) {
            commands.add(parseCommands(databaseManager, scanner));
        }
        return commands;
    }

    public static Command parseCommands(DatabaseManager databaseManager, Scanner scanner) {
        final String commandType = nextValidLine(scanner).orElseThrow();
        final String fileName = nextValidLine(scanner).orElseThrow();
        return switch (commandType) {
            case "create-table" -> parseCreateTableCommand(databaseManager, scanner, fileName);
            case "insert-record" -> parseInsertRecordCommand(databaseManager, scanner, fileName);
            case "search-field" -> parseSearchFieldCommand(databaseManager, scanner, fileName);
            case "search-record" -> parseSearchRecordCommand(databaseManager, scanner, fileName);
            default -> throw new IllegalArgumentException("Unknown command: " + commandType);
        };
    }

    public static Command parseCreateTableCommand(DatabaseManager databaseManager, Scanner scanner, String fileName) {
        final List<Field> fields = new ArrayList<>();
        final int fieldCount = nextValidLine(scanner).map(Integer::parseInt).orElseThrow();
        for (int i = 0; i < fieldCount; i++) {
            final String[] parts = nextValidLine(scanner).orElseThrow().split(" ");
            fields.add(new Field(parts[0], Integer.parseInt(parts[1])));
        }
        return new CreateTableCommand(databaseManager, fileName, fields);
    }

    public static Command parseInsertRecordCommand(DatabaseManager databaseManager, Scanner scanner, String fileName) {
        final List<Record> records = new ArrayList<>();
        final int recordCount = nextValidLine(scanner).map(Integer::parseInt).orElseThrow();
        for (int i = 0; i < recordCount; i++) {
            List<Optional<String>> fields = Arrays.stream(nextValidLine(scanner).orElseThrow().split(";"))
                    .map(String::trim)
                    .map(field -> field.equals("null") ? Optional.empty() : Optional.of(field))
                    .map(option -> option.map(String::valueOf))
                    .collect(Collectors.toList());
            records.add(new Record(fields));
        }
        return new InsertRecordCommand(databaseManager, fileName, records);
    }

    public static Command parseSearchFieldCommand(DatabaseManager databaseManager, Scanner scanner, String fileName) {
        final String fieldName = nextValidLine(scanner).orElseThrow();
        return new SearchFieldCommand(databaseManager, fileName, fieldName);
    }

    public static Command parseSearchRecordCommand(DatabaseManager databaseManager, Scanner scanner, String fileName) {
        final String fieldName = nextValidLine(scanner).orElseThrow();
        final String minRange = nextValidLine(scanner).orElseThrow();
        final String maxRange = nextValidLine(scanner).orElseThrow();
        return new SearchRecordCommand(databaseManager, fileName, fieldName, minRange, maxRange);
    }

    private static Optional<String> nextValidLine(Scanner scanner) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty() && !line.startsWith("//")) {
                return Optional.of(line.split("//")[0].trim());
            }
        }
        return Optional.empty();
    }
}