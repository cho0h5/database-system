package org.example;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class BlockManager implements AutoCloseable {
    public static final int BLOCK_SIZE = 4096;

    private final FileChannel fileChannel;
    private final Map<Integer, ByteBuffer> cache = new HashMap<>();

    public BlockManager(String fileName) throws IOException {
        this.fileChannel = FileChannel.open(Paths.get(fileName), StandardOpenOption.READ, StandardOpenOption.WRITE,
                StandardOpenOption.CREATE);
    }

    public ByteBuffer readBlock(int blockNum) {
        return cache.computeIfAbsent(blockNum, index -> {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(BLOCK_SIZE);
                fileChannel.position((long) blockNum * BLOCK_SIZE);
                fileChannel.read(buffer);
                buffer.flip();
                return buffer;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void flush() throws IOException {
        for (Map.Entry<Integer, ByteBuffer> entry : cache.entrySet()) {
            fileChannel.position((long) entry.getKey() * BLOCK_SIZE);
            fileChannel.write(entry.getValue());
        }
    }

    @Override
    public void close() throws IOException {
        flush();
        fileChannel.force(true);
        fileChannel.close();
    }
}
