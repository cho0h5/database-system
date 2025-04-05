package org.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

class MyDatabaseManager implements DatabaseManager {
    public static final int BLOCK_SIZE = 128;

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
            // update metadata
            metadata.setFirstRecordPointer(newPointer);
            metadata.write(headerBlock);
        } else {
            // find free space for new record
            Record lastRecord = new Record(blockManager, metadata.getFields(), lastRecordPointer);
            final int usedSpace = lastRecordPointer.getOffset() + lastRecord.size();
            final int freeSpace = BLOCK_SIZE - usedSpace;

            if (freeSpace < newRecord.size()) {
                newPointer = new Pointer(lastRecordPointer.getBlock() + 1, 0);
            } else {
                newPointer = new Pointer(lastRecordPointer.getBlock(),
                        lastRecordPointer.getOffset() + lastRecord.size());
            }

            // update last record's next pointer
            lastRecord.setNextPointer(Optional.of(newPointer));
            lastRecord.write(blockManager, metadata.getFields(), lastRecordPointer);
        }

        // insert new record
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
        try (BlockManager blockManager = new BlockManager(BLOCK_SIZE, fileName)) {
            ByteBuffer headerBlock = blockManager.readBlock(0);
            Metadata metadata = new Metadata(headerBlock);
            final int fieldIndex = Field.indexOf(metadata.getFields(), fieldName);

            new RecordIterable(blockManager, metadata.getFields(), metadata.getFirstRecordPointer())
                    .stream()
                    .map(record -> record.fields.get(fieldIndex))
                    .map(opt -> opt.orElse("null"))
                    .forEach(System.out::println);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void searchRecord(String fileName, String fieldName, String minValue, String maxValue) {
        // TODO: Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchRecord'");
    }

    Pointer findLastRecordPointer(BlockManager blockManager, final Metadata metadata) {
        Pointer current = metadata.getFirstRecordPointer();
        Pointer last = current;

        for (Record record : new RecordIterable(blockManager, metadata.getFields(),
                current)) {
            last = current;
            current = record.getNextPointer().get();
        }

        return last;
    }
}
