package ru.shintar.atm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.shintar.atm.domain.Denomination;
import ru.shintar.atm.exception.CannotDispenseAmountException;
import ru.shintar.atm.exception.InsufficientFundsException;
import ru.shintar.atm.repository.CashRepository;
import ru.shintar.atm.repository.InMemoryCashRepository;
import ru.shintar.atm.service.Atm;
import ru.shintar.atm.service.AtmImpl;
import ru.shintar.atm.strategy.GreedyDispenseStrategy;

@DisplayName("ATM integration tests for denominations 100, 200, 500, 1000")
public class AtmIntegrationTest {
    private Atm atm;
    private Denomination d100;
    private Denomination d200;
    private Denomination d500;
    private Denomination d1000;

    @BeforeEach
    void setup() {
        d100 = new Denomination(100);
        d200 = new Denomination(200);
        d500 = new Denomination(500);
        d1000 = new Denomination(1000);
        Map<Denomination, Integer> initial = new HashMap<>();
        initial.put(d100, 2);
        initial.put(d200, 2);
        initial.put(d500, 1);
        initial.put(d1000, 1);
        CashRepository repository = new InMemoryCashRepository(initial);
        atm = new AtmImpl(repository, new GreedyDispenseStrategy(initial.keySet()));
    }

    @Test
    @DisplayName("Should increase total balance when depositing notes")
    void shouldIncreaseBalanceAfterDeposit() {
        int before = atm.getTotalBalance();
        atm.deposit(d200, 3);
        assertEquals(before + 3 * 200, atm.getTotalBalance(), "Total balance should increase by deposited amount");
    }

    @Test
    @DisplayName("Should dispense correct denominations and update inventory on withdrawal")
    void shouldDispenseAndUpdateInventory() throws Exception {
        int amount = 1600;
        Map<Denomination, Integer> dispensed = atm.withdraw(amount);
        Map<Denomination, Integer> expectedDispensed = Map.of(
                d1000, 1,
                d500, 1,
                d100, 1);
        assertEquals(expectedDispensed, dispensed, "Dispensed denominations should match greedy strategy");
        assertEquals(0, atm.getBalance().getOrDefault(d1000, 0), "1000 notes should be depleted");
        assertEquals(0, atm.getBalance().getOrDefault(d500, 0), "500 notes should be depleted");
        assertEquals(1, atm.getBalance().getOrDefault(d100, 0), "100 notes should decrement by 1");
        assertEquals(2, atm.getBalance().getOrDefault(d200, 0), "200 notes should remain unchanged");
    }

    @Test
    @DisplayName("Should throw CannotDispenseAmountException for non-dispensable amount")
    void shouldThrowCannotDispenseException() {
        assertThrows(
                CannotDispenseAmountException.class,
                () -> atm.withdraw(50),
                "Withdrawing amount not possible with available denominations should fail");
    }

    @Test
    @DisplayName("Should throw InsufficientFundsException when withdrawing more than total balance")
    void shouldThrowInsufficientFundsException() {
        int largeAmount = atm.getTotalBalance() + 500;
        assertThrows(
                InsufficientFundsException.class,
                () -> atm.withdraw(largeAmount),
                "Withdrawing more than total balance should fail with InsufficientFundsException");
    }
}
