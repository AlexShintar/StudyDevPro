package ru.otus.processor.homework;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.exception.EvenSecondException;
import ru.otus.model.Message;

class ProcessorThrowOnEvenSecondTest {

    @Test
    @DisplayName("Should throw EvenSecondException on even second")
    void shouldThrowOnEvenSecond() {
        DateTimeProvider provider = () -> LocalTime.of(0, 0, 2);
        var processor = new ProcessorThrowOnEvenSecond(provider);
        Message msgEven = Message.builder().id(2L).build();
        assertThrows(EvenSecondException.class, () -> processor.process(msgEven), "Expected exception on even second");
    }

    @Test
    @DisplayName("Should return original message on odd second")
    void shouldNotThrowOnOddSecond() {
        DateTimeProvider provider = () -> LocalTime.of(0, 0, 7);
        var processor = new ProcessorThrowOnEvenSecond(provider);
        Message msgOdd = Message.builder().id(7L).build();
        assertSame(msgOdd, processor.process(msgOdd), "Expected original message on odd second");
    }
}
