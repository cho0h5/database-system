package org.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class MyDatabaseManager implements DatabaseManager {
    @Override
    public void createTable(String fileName, List<Field> fields) {
        try (BlockManager blockManager = new BlockManager(fileName)) {
            ByteBuffer headerBlock = blockManager.readBlock(0);
            headerBlock.clear(); // FIXME: this gonna overwrite file

            // pointer
            headerBlock.putShort((short) 0); // block
            headerBlock.putShort((short) 0); // offset

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
