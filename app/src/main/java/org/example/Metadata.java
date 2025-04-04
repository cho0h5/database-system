package org.example;

import java.nio.ByteBuffer;
import java.util.ArrayList;
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

        this.fields = new ArrayList<>();
        final int fieldCount = Byte.toUnsignedInt(headerBlock.get());
        for (int i = 0; i < fieldCount; i++) {
            fields.add(new Field(headerBlock));
        }
    }

    public void write(ByteBuffer headerBlock) {
        headerBlock.clear();

        // pointer
        firstRecordPointer.write(headerBlock);

        // number of fields
        headerBlock.put((byte) fields.size());

        // fields
        for (Field field : fields) {
            field.write(headerBlock);
        }
    }

    public Pointer getFirstRecordPointer() {
        return firstRecordPointer;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFirstRecordPointer(Pointer firstRecordPointer) {
        this.firstRecordPointer = firstRecordPointer;
    }
}
