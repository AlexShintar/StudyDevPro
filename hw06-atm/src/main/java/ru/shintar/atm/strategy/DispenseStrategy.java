package ru.shintar.atm.strategy;

import java.util.Map;
import ru.shintar.atm.domain.Denomination;
import ru.shintar.atm.exception.CannotDispenseAmountException;

public interface DispenseStrategy {

    Map<Denomination, Integer> dispense(int amount, Map<Denomination, Integer> available)
            throws CannotDispenseAmountException;
}
