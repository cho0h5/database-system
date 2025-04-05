package org.example;

import java.util.List;

public class DuelDatabaseManager implements DatabaseManager {
    private final DatabaseManager a;
    private final DatabaseManager b;

    public DuelDatabaseManager(DatabaseManager a, DatabaseManager b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public void createTable(String fileName, List<Field> fields) {
        a.createTable(fileName, fields);
        b.createTable(fileName, fields);
    }

    @Override
    public void insertRecords(String fileName, List<Record> records) {
        a.insertRecords(fileName, records);
        b.insertRecords(fileName, records);
    }

    @Override
    public void searchField(String fileName, String fieldName) {
        a.searchField(fileName, fieldName);
        b.searchField(fileName, fieldName);
    }

    @Override
    public void searchRecord(String fileName, String fieldName, String minValue, String maxValue) {
        a.searchRecord(fileName, fieldName, minValue, maxValue);
        b.searchRecord(fileName, fieldName, minValue, maxValue);
    }
}
