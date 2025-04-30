package ru.otus.processor.homework;

import java.time.LocalTime;

public interface DateTimeProvider {
    LocalTime now();
}
