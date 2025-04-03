package org.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

class MyDatabaseManager implements DatabaseManager {
    @Override
    public void createTable(String fileName, List<Field> fields) {
        try (BlockManager blockManager = new BlockManager(fileName)) {
            ByteBuffer headerBlock = blockManager.readBlock(0);
            headerBlock.clear(); // FIXME: this gonna overwrite file

            final Pointer firstRecordPointer = new Pointer(0, 0);

            final Metadata metadata = new Metadata(firstRecordPointer, fields);
            metadata.write(headerBlock);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertRecord(BlockManager blockManager, Record record) {
        ByteBuffer headerBlock = blockManager.readBlock(0);
        Metadata metadata = new Metadata(headerBlock);

        Pointer lastRecordPointer = findLastRecordPointer(blockManager, metadata);

        // 1. find last record
        // 2. check if there is enough space for new record
        // if not, create new block
        // 3. put it there
    }

    @Override
    public void insertRecords(String fileName, List<Record> records) {
        try (BlockManager blockManager = new BlockManager(fileName)) {
            for (Record record : records) {
                insertRecord(blockManager, record);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    Pointer findLastRecordPointer(BlockManager blockManager, final Metadata metadata) {
        if (metadata.getFirstRecordPointer().isNullPointer()) {
            return metadata.getFirstRecordPointer();
        }

        Pointer pointer = metadata.getFirstRecordPointer();
        Record record = new Record(blockManager, metadata.getFields(), pointer);

        while (record.getNextPointer().get().isNullPointer()) {
            pointer = record.getNextPointer().get();
            record = new Record(blockManager, metadata.getFields(), pointer);
        }

        return pointer;
    }
}
