package ru.otus.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ObjectForMessage {
    private List<String> data;

    private ObjectForMessage(ObjectForMessage other) {
        List<String> otherData = other.getData();
        this.data = (otherData == null) ? null : new ArrayList<>(otherData);
    }

    public static ObjectForMessage copyOf(ObjectForMessage other) {
        return (other == null) ? null : new ObjectForMessage(other);
    }
}
