package org.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

class MyDatabaseManager implements DatabaseManager {
    public static final int BLOCK_SIZE = 64;

    @Override
    public void createTable(String fileName, List<Field> fields) {
        try (BlockManager blockManager = new BlockManager(BLOCK_SIZE, fileName)) {
            ByteBuffer headerBlock = blockManager.readBlock(0);
            headerBlock.clear(); // FIXME: this gonna overwrite file

            final Pointer firstRecordPointer = new Pointer(0, 0);

            final Metadata metadata = new Metadata(firstRecordPointer, fields);
            metadata.write(headerBlock);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertRecord(BlockManager blockManager, Record newRecord) {
        ByteBuffer headerBlock = blockManager.readBlock(0);
        Metadata metadata = new Metadata(headerBlock);

        // find last record
        Pointer lastRecordPointer = findLastRecordPointer(blockManager, metadata);

        // determine next record position
        Pointer newPointer = new Pointer(1, 0);
        if (lastRecordPointer.isNullPointer()) {
            metadata.setFirstRecordPointer(newPointer);
        } else {
            Record lastRecord = new Record(blockManager, metadata.getFields(), lastRecordPointer);
            final int usedSpace = lastRecordPointer.getOffset() + lastRecord.size();
            final int freeSpace = BLOCK_SIZE - usedSpace;

            if (freeSpace < newRecord.size()) {
                newPointer = new Pointer(lastRecordPointer.getBlock() + 1, 0);
            } else {
                newPointer = new Pointer(lastRecordPointer.getBlock(),
                        lastRecordPointer.getOffset() + lastRecord.size());
            }
        }

        // insert new record
        metadata.write(headerBlock);
        newRecord.setNextPointer(Optional.of(new Pointer(0, 0)));
        newRecord.write(blockManager, metadata.getFields(), newPointer);
    }

    @Override
    public void insertRecords(String fileName, List<Record> records) {
        try (BlockManager blockManager = new BlockManager(BLOCK_SIZE, fileName)) {
            for (Record record : records) {
                insertRecord(blockManager, record);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void searchField(String fileName, String fieldName) {
        // TODO: Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchField'");
    }

    @Override
    public void searchRecord(String fileName, String fieldName, String minValue, String maxValue) {
        // TODO: Auto-generated method stub
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
