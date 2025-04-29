package ru.shintar.atm.strategy;

import java.util.*;
import ru.shintar.atm.domain.Denomination;
import ru.shintar.atm.exception.CannotDispenseAmountException;

public class GreedyDispenseStrategy implements DispenseStrategy {

    private final List<Denomination> sortedDenominations;

    public GreedyDispenseStrategy(Collection<Denomination> denominations) {
        this.sortedDenominations = new ArrayList<>(denominations);
        this.sortedDenominations.sort(Denomination.DESC);
    }

    @Override
    public Map<Denomination, Integer> dispense(int amount, Map<Denomination, Integer> available)
            throws CannotDispenseAmountException {

        int remaining = amount;

        Map<Denomination, Integer> result = new HashMap<>();

        for (Denomination d : sortedDenominations) {
            int count = available.getOrDefault(d, 0);
            int cnt = Math.min(remaining / d.value(), count);
            if (cnt > 0) {
                result.put(d, cnt);
                remaining -= cnt * d.value();
            }
        }

        if (remaining != 0) {
            throw new CannotDispenseAmountException("Cannot dispense exact amount: " + amount);
        }
        return result;
    }
}
