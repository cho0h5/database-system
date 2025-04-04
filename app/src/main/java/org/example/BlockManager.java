package org.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class BlockManager implements AutoCloseable {
    private final int blockSize;
    private final FileChannel fileChannel;
    private final Map<Integer, ByteBuffer> cache = new HashMap<>();

    public BlockManager(final int blockSize, String fileName) throws IOException {
        this.blockSize = blockSize;
        this.fileChannel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE);
    }

    public ByteBuffer readBlock(int blockNum) {
        ByteBuffer byteBuffer = cache.computeIfAbsent(blockNum, index -> {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(blockSize);
                fileChannel.position((long) blockNum * blockSize);
                fileChannel.read(buffer);
                buffer.flip();
                return buffer;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        byteBuffer.clear();
        return byteBuffer;
    }

    public void flush() throws IOException {
        for (Map.Entry<Integer, ByteBuffer> entry : cache.entrySet()) {
            fileChannel.position((long) entry.getKey() * blockSize);
            fileChannel.write(entry.getValue().rewind());
        }
    }

    @Override
    public void close() throws IOException {
        flush();
        fileChannel.force(true);
        fileChannel.close();
    }
}
