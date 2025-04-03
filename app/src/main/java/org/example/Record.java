package org.example;

import java.util.List;
import java.util.Optional;

class Record {
    List<Optional<String>> fields;

    public Record(List<Optional<String>> fields) {
        this.fields = fields;
    }
}
