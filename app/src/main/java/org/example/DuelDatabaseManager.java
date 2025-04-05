package org.example;

import java.util.List;

public class DuelDatabaseManager implements DatabaseManager {
    private final DatabaseManager a;
    private final DatabaseManager b;

    public DuelDatabaseManager(final DatabaseManager a, final DatabaseManager b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public void createTable(final String fileName, final List<Field> fields) {
        a.createTable(fileName, fields);
        b.createTable(fileName, fields);
    }

    @Override
    public void insertRecords(final String fileName, final List<Record> records) {
        a.insertRecords(fileName, records);
        b.insertRecords(fileName, records);
    }

    @Override
    public void searchField(final String fileName, final String fieldName) {
        System.out.println("======== My DB ========");
        a.searchField(fileName, fieldName);
        System.out.println("======== MySQL ========");
        b.searchField(fileName, fieldName);
    }

    @Override
    public void searchRecord(final String fileName, final String fieldName, final String minValue,
            final String maxValue) {
        System.out.println("======== My DB ========");
        a.searchRecord(fileName, fieldName, minValue, maxValue);
        System.out.println("======== MySQL ========");
        b.searchRecord(fileName, fieldName, minValue, maxValue);
    }
}
