package org.example;

import java.nio.ByteBuffer;

class Field {
    String name;
    int size;

    public Field(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public void write(ByteBuffer byteBuffer) {
        byte[] nameBytes = this.name.getBytes();

        byteBuffer.put((byte) nameBytes.length); // length of field name
        byteBuffer.put(nameBytes); // field name
        byteBuffer.put((byte) this.size); // field type
    }
}
