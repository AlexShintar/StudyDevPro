package ru.shintar.atm.service;

import java.util.Map;
import ru.shintar.atm.domain.Denomination;
import ru.shintar.atm.exception.CannotDispenseAmountException;
import ru.shintar.atm.exception.InsufficientFundsException;

public interface Atm {
    void deposit(Denomination denomination, int count);

    Map<Denomination, Integer> getBalance();

    int getTotalBalance();

    Map<Denomination, Integer> withdraw(int amount) throws CannotDispenseAmountException, InsufficientFundsException;
}
