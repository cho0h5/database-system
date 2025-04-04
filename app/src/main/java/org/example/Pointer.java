package org.example;

import java.nio.ByteBuffer;

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

    public boolean isNullPointer() {
        return this.block == 0 && this.offset == 0;
    }

    public int getBlock() {
        return this.block;
    }

    public int getOffset() {
        return this.offset;
    }

    static public int size() {
        return 4;
    }
}
