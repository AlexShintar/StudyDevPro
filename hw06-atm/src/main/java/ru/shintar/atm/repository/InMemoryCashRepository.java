package ru.shintar.atm.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import ru.shintar.atm.domain.CashCell;
import ru.shintar.atm.domain.Denomination;
import ru.shintar.atm.exception.InsufficientFundsException;

public class InMemoryCashRepository implements CashRepository {

    private final Map<Denomination, CashCell> cells = new HashMap<>();

    public InMemoryCashRepository(Map<Denomination, Integer> initial) {
        initial.forEach((denomination, count) -> cells.put(denomination, new CashCell(denomination, count)));
    }

    @Override
    public void deposit(Denomination denomination, int count) {
        cells.computeIfAbsent(denomination, d -> new CashCell(d, 0)).add(count);
    }

    @Override
    public Map<Denomination, Integer> getInventory() {
        Map<Denomination, Integer> inventory = new HashMap<>();
        cells.forEach((d, cell) -> inventory.put(d, cell.getCount()));
        return Collections.unmodifiableMap(inventory);
    }

    @Override
    public void remove(Map<Denomination, Integer> dispensed) throws InsufficientFundsException {
        for (var entry : dispensed.entrySet()) {
            CashCell cell = cells.get(entry.getKey());
            if (cell == null || cell.getCount() < entry.getValue()) {
                throw new InsufficientFundsException(entry.getKey());
            }
        }
        for (var entry : dispensed.entrySet()) {
            cells.get(entry.getKey()).remove(entry.getValue());
        }
    }
}
