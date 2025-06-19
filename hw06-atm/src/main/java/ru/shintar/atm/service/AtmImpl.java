package ru.shintar.atm.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import ru.shintar.atm.domain.Denomination;
import ru.shintar.atm.exception.CannotDispenseAmountException;
import ru.shintar.atm.repository.CashRepository;
import ru.shintar.atm.strategy.DispenseStrategy;

@RequiredArgsConstructor
public class AtmImpl implements Atm {
    private final CashRepository repository;
    private final DispenseStrategy strategy;

    @Override
    public void deposit(Denomination denomination, int count) {
        repository.deposit(denomination, count);
    }

    @Override
    public Map<Denomination, Integer> getBalance() {
        return repository.getInventory();
    }

    @Override
    public int getTotalBalance() {
        return repository.calculateTotal();
    }

    @Override
    public Map<Denomination, Integer> withdraw(int amount)
            throws CannotDispenseAmountException {
        if (amount > getTotalBalance()) {
            throw new CannotDispenseAmountException("Insufficient funds");
        }
        Map<Denomination, Integer> toDispense = strategy.dispense(amount, getBalance());
        repository.remove(toDispense);
        return toDispense;
    }
}
