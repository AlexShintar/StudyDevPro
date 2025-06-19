package ru.shintar.atm.repository;

import java.util.Map;
import ru.shintar.atm.domain.Denomination;
import ru.shintar.atm.exception.InsufficientFundsException;

public interface CashRepository {

    void deposit(Denomination denomination, int count);

    Map<Denomination, Integer> getInventory();

    void remove(Map<Denomination, Integer> dispensed) throws InsufficientFundsException;

    default int calculateTotal() {
        return getInventory().entrySet().stream()
                .mapToInt(e -> e.getKey().value() * e.getValue())
                .sum();
    }
}
