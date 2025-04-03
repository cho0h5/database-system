package org.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

class Metadata {
    private Pointer firstRecordPointer;
    private List<Field> fields;

    public Metadata(Pointer firstRecordPointer, List<Field> fields) {
        this.firstRecordPointer = firstRecordPointer;
        this.fields = fields;
    }

    public Metadata(ByteBuffer headerBlock) {
        this.firstRecordPointer = new Pointer(headerBlock);

        final int fieldCount = Byte.toUnsignedInt(headerBlock.get());
        for (int i = 0; i < fieldCount; i++) {
            int nameLength = Byte.toUnsignedInt(headerBlock.get());
            byte[] nameBytes = new byte[nameLength];
            headerBlock.get(nameBytes);
            String fieldName = new String(nameBytes);

            int fieldSize = Byte.toUnsignedInt(headerBlock.get());

            fields.add(new Field(fieldName, fieldSize));
        }
    }

    public void write(ByteBuffer headerBlock) {
        // pointer
        firstRecordPointer.write(headerBlock);

        // number of fields
        headerBlock.put((byte) fields.size());

        // fields
        for (Field field : fields) {
            field.write(headerBlock);
        }
    }
}

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
}
