package ru.otus.core.repository;

public class DataTemplateException extends RuntimeException {
    public DataTemplateException(Exception ex) {
        super(ex);
    }

    public DataTemplateException(String message) {
        super(message);
    }

    public DataTemplateException(String message, Throwable cause) {
        super(message, cause);
    }
}
