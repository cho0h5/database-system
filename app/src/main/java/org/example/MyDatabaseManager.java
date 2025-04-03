package org.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

class Metadata {
    private Pointer firstRecordPointer;
    private final List<Field> fields = new ArrayList<>();;

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
}

class Pointer {
    final int block;
    final int offset;

    public Pointer(int block, int offset) {
        this.block = block;
        this.offset = offset;
    }

    public Pointer(ByteBuffer byteBuffer) {
        this.block = Short.toUnsignedInt(byteBuffer.getShort());
        this.offset = Short.toUnsignedInt(byteBuffer.getShort());
    }

    public void write(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) this.block); // block
        byteBuffer.putShort((short) this.block); // offset
    }
}

class MyDatabaseManager implements DatabaseManager {
    @Override
    public void createTable(String fileName, List<Field> fields) {
        try (BlockManager blockManager = new BlockManager(fileName)) {
            ByteBuffer headerBlock = blockManager.readBlock(0);
            headerBlock.clear(); // FIXME: this gonna overwrite file

            // pointer
            final Pointer firstRecordPointer = new Pointer(0, 0);
            firstRecordPointer.write(headerBlock);

            // number of fields
            headerBlock.put((byte) fields.size());

            // fields
            for (Field field : fields) {
                byte[] nameBytes = field.name.getBytes();

                headerBlock.put((byte) nameBytes.length); // length of field name
                headerBlock.put(nameBytes); // field name
                headerBlock.put((byte) field.size); // field type
            }
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
