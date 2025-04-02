package org.example;

import java.util.List;

public class MyDatabaseManager implements DatabaseManager {
    @Override
    public void createTable(String fileName, List<Field> fields) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createTable'");
    }

    @Override
    public void insertRecords(String fileName, List<Record> records) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertRecords'");
    }

    @Override
    public void searchField(String fileName, String fieldName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchField'");
    }

    @Override
    public void searchRecord(String fileName, String fieldName, String minValue, String maxValue) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchRecord'");
    }
}
