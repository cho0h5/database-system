package org.example;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class Record {
    List<Optional<String>> fields;
    Optional<Pointer> nextPointer;

    public Record(final List<Optional<String>> fields) {
        this.fields = fields;
    }

    public Record(BlockManager blockManager, final List<Field> fields, final Pointer pointer) {
        ByteBuffer byteBuffer = blockManager.readBlock(pointer.getBlock());
        this.fields = new ArrayList<>();

        final int nullBitmap = Byte.toUnsignedInt(byteBuffer.get());

        for (int i = 0; i < fields.size(); i++) {
            if ((nullBitmap & (1 << (7 - i))) == 0) {
                this.fields.add(Optional.empty());
                continue;
            }

            Field field = fields.get(i);
            byte[] fieldValue = new byte[field.size];
            byteBuffer.get(fieldValue);
            this.fields.add(Optional.of(new String(fieldValue)));
        }

        this.nextPointer = Optional.of(new Pointer(byteBuffer));
    }
}
