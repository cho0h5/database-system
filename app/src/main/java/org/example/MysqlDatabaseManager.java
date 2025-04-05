package org.example;

import java.util.List;

public class MysqlDatabaseManager implements DatabaseManager {
    @Override
    public void createTable(final String fileName, final List<Field> fields) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createTable'");
    }

    @Override
    public void insertRecords(final String fileName, final List<Record> records) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'insertRecords'");
    }

    @Override
    public void searchField(final String fileName, final String fieldName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchField'");
    }

    @Override
    public void searchRecord(final String fileName, final String fieldName, final String minValue, final String maxValue) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchRecord'");
    }
}
