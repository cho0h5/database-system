package org.example;

import java.util.List;

interface DatabaseManager {
    public void createTable(String fileName, List<Field> fields);

    public void insertRecords(String fileName, List<Record> records);

    public void searchField(String fileName, String fieldName);

    public void searchRecord(String fileName, String fieldName, String minValue,
            String maxValue);
}
