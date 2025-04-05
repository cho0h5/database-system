package org.example;

import java.nio.ByteBuffer;
import java.util.List;

class Field {
    String name;
    int size;

    public Field(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public Field(ByteBuffer headerBlock) {
        int nameLength = Byte.toUnsignedInt(headerBlock.get());
        byte[] nameBytes = new byte[nameLength];
        headerBlock.get(nameBytes);
        this.name = new String(nameBytes);
        this.size = Byte.toUnsignedInt(headerBlock.get());
    }

    public void write(ByteBuffer byteBuffer) {
        byte[] nameBytes = this.name.getBytes();

        byteBuffer.put((byte) nameBytes.length); // length of field name
        byteBuffer.put(nameBytes); // field name
        byteBuffer.put((byte) this.size); // field type
    }

    public static int indexOf(List<Field> fields, String fieldName) {
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).name.equals(fieldName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Field not found: " + fieldName);
    }
}
