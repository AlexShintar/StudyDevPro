package ru.shintar.atm.exception;

import ru.shintar.atm.domain.Denomination;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(Denomination denomination) {
        super("Not enough bills of " + denomination);
    }
}
