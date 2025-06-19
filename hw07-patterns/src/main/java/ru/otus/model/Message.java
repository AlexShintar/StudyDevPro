package ru.otus.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@SuppressWarnings({"java:S107", "java:S1135"})
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Message {
    @EqualsAndHashCode.Include
    long id;

    String field1;
    String field2;
    String field3;
    String field4;
    String field5;
    String field6;
    String field7;
    String field8;
    String field9;
    String field10;
    String field11;
    String field12;
    ObjectForMessage field13;

    public static Message copyOf(Message other) {
        if (other == null) {
            return null;
        }
        return other.toBuilder()
                .field13(ObjectForMessage.copyOf(other.getField13()))
                .build();
    }
}
