package ru.shintar.atm.exception;

public class CannotDispenseAmountException extends Exception {
    public CannotDispenseAmountException(String message) {
        super(message);
    }
}
