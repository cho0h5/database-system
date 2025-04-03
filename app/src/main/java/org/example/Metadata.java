package org.example;

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
            fields.add(new Field(headerBlock));
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
