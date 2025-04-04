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
        this.nextPointer = Optional.empty();
    }

    public Record(BlockManager blockManager, final List<Field> fields, final Pointer pointer) {
        ByteBuffer byteBuffer = blockManager.readBlock(pointer.getBlock());
        byteBuffer.position(pointer.getOffset());

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

    public void write(BlockManager blockManager, final List<Field> fields, final Pointer pointer) {
        ByteBuffer byteBuffer = blockManager.readBlock(pointer.getBlock());
        byteBuffer.position(pointer.getOffset());

        int nullBitmap = 0;
        for (int i = 0; i < fields.size(); i++) {
            if (this.fields.get(i).isPresent()) {
                continue;
            }

            nullBitmap |= (1 << (7 - i));
        }

        byteBuffer.put((byte) nullBitmap);

        for (int i = 0; i < fields.size(); i++) {
            if (!this.fields.get(i).isPresent()) {
                continue;
            }

            byteBuffer.put(this.fields.get(i).get().getBytes());
        }

        this.nextPointer.get().write(byteBuffer);
    }

    public Optional<Pointer> getNextPointer() {
        return nextPointer;
    }

    public void setNextPointer(Optional<Pointer> nextPointer) {
        this.nextPointer = nextPointer;
    }

    public int size() {
        int totalFieldSize = 0;

        for (Optional<String> fieldValue : fields) {
            if (fieldValue.isPresent()) {
                totalFieldSize += fieldValue.get().length();
            }
        }

        return 1 + totalFieldSize + Pointer.size();
    }
}
