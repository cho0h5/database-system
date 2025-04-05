package org.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

class MyDatabaseManager implements DatabaseManager {
    public static final int BLOCK_SIZE = 128;

    @Override
    public void createTable(final String fileName, final List<Field> fields) {
        try (BlockManager blockManager = new BlockManager(BLOCK_SIZE, fileName)) {
            final ByteBuffer headerBlock = blockManager.readBlock(0);
            headerBlock.clear(); // FIXME: this gonna overwrite file

            final Pointer firstRecordPointer = new Pointer(0, 0);

            final Metadata metadata = new Metadata(firstRecordPointer, fields);
            metadata.write(headerBlock);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertRecord(final BlockManager blockManager, final Record newRecord) {
        final ByteBuffer headerBlock = blockManager.readBlock(0);
        final Metadata metadata = new Metadata(headerBlock);

        // find last record
        final Pointer lastRecordPointer = findLastRecordPointer(blockManager, metadata);

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
    public void insertRecords(final String fileName, final List<Record> records) {
        try (BlockManager blockManager = new BlockManager(BLOCK_SIZE, fileName)) {
            for (Record record : records) {
                insertRecord(blockManager, record);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void searchField(final String fileName, final String fieldName) {
        try (BlockManager blockManager = new BlockManager(BLOCK_SIZE, fileName)) {
            final ByteBuffer headerBlock = blockManager.readBlock(0);
            final Metadata metadata = new Metadata(headerBlock);
            final int fieldIndex = Field.indexOf(metadata.getFields(), fieldName);

            List<String> values = new RecordIterable(blockManager, metadata.getFields(),
                    metadata.getFirstRecordPointer())
                    .stream()
                    .map(record -> record.fields.get(fieldIndex))
                    .map(opt -> opt.orElse("null"))
                    .toList();

            Record.printFieldValues(metadata.getFields().get(fieldIndex), values);
            System.out.println();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void searchRecord(final String fileName, final String fieldName, final String minValue,
            final String maxValue) {
        try (BlockManager blockManager = new BlockManager(BLOCK_SIZE, fileName)) {
            final ByteBuffer headerBlock = blockManager.readBlock(0);
            final Metadata metadata = new Metadata(headerBlock);
            final int fieldIndex = Field.indexOf(metadata.getFields(), fieldName);

            final List<Record> filtered = new RecordIterable(blockManager, metadata.getFields(),
                    metadata.getFirstRecordPointer())
                    .stream()
                    .filter(record -> {
                        Optional<String> option = record.fields.get(fieldIndex);
                        return option.isPresent() && option.get().compareTo(minValue) >= 0
                                && option.get().compareTo(maxValue) <= 0;
                    })
                    .toList();

            Record.printRecords(metadata, filtered);
            System.out.println();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Pointer findLastRecordPointer(final BlockManager blockManager, final Metadata metadata) {
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
