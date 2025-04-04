package org.example;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
            if ((nullBitmap & (1 << (7 - i))) != 0) {
                this.fields.add(Optional.empty());
                continue;
            }

            Field field = fields.get(i);
            byte[] fieldValue = new byte[field.getSize()];
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
            if (!this.fields.get(i).isPresent()) {
                nullBitmap |= (1 << (7 - i));
            }
        }

        byteBuffer.put((byte) nullBitmap);

        for (int i = 0; i < fields.size(); i++) {
            if (this.fields.get(i).isPresent()) {
                byteBuffer.put(this.fields.get(i).get().getBytes());
            }
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

    public static void printRecords(final Metadata metadata, final List<Record> records) {
        List<Integer> columnWidths = metadata.getFields().stream()
                .map(Field::getSize)
                .toList();

        IntStream.range(0, metadata.getFields().size()).forEach(
                i -> System.out.printf("%-" + columnWidths.get(i) + "s ", metadata.getFields().get(i).getName()));
        System.out.println();

        IntStream.range(0, metadata.getFields().size())
                .forEach(i -> System.out.printf("-".repeat(columnWidths.get(i)) + " "));
        System.out.println();

        records.stream().forEach(record -> {
            for (int i = 0; i < record.fields.size(); i++) {
                Optional<String> fieldValue = record.fields.get(i);
                String value = fieldValue.orElse("null");
                System.out.printf("%-" + columnWidths.get(i) + "s ", value);
            }
            System.out.println();
        });
    }

    public static void printFieldValues(final Field field, final List<String> values) {
        System.out.println(field.getName());
        System.out.println("-".repeat(field.getSize()));
        values.forEach(System.out::println);
    }
}

class RecordIterator implements Iterator<Record> {
    private BlockManager blockManager;
    private final List<Field> fields;
    private Pointer currentPointer;

    public RecordIterator(BlockManager blockManager, List<Field> fields, Pointer firstPointer) {
        this.blockManager = blockManager;
        this.fields = fields;
        this.currentPointer = firstPointer;
    }

    @Override
    public boolean hasNext() {
        return !currentPointer.isNullPointer();
    }

    @Override
    public Record next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        Record record = new Record(blockManager, fields, currentPointer);
        currentPointer = record.getNextPointer().get();
        return record;
    }
}

class RecordIterable implements Iterable<Record> {
    private BlockManager blockManager;
    private final List<Field> fields;
    private Pointer currentPointer;

    public RecordIterable(BlockManager blockManager, List<Field> fields, Pointer firstPointer) {
        this.blockManager = blockManager;
        this.fields = fields;
        this.currentPointer = firstPointer;
    }

    @Override
    public Iterator<Record> iterator() {
        return new RecordIterator(blockManager, fields, currentPointer);
    }

    public Stream<Record> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }
}
