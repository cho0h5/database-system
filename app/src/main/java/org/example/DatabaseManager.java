package org.example;

import java.util.List;

interface DatabaseManager {
    public void createTable(final String fileName, final List<Field> fields);

    public void insertRecords(final String fileName, final List<Record> records);

    public void searchField(final String fileName, final String fieldName);

    public void searchRecord(final String fileName, final String fieldName, final String minValue,
            String maxValue);
}
