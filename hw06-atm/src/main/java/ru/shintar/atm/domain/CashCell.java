package ru.shintar.atm.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import ru.shintar.atm.exception.InsufficientFundsException;

@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public class CashCell {

    private final Denomination denomination;
    private int count;

    public CashCell(Denomination denomination, int initialCount) {
        this.denomination = denomination;
        this.count = Math.max(0, initialCount);
    }

    public void add(int n) {
        if (n < 0) throw new IllegalArgumentException("Cannot add negative count");
        count += n;
    }

    public void remove(int n) throws InsufficientFundsException {
        if (n < 0) throw new IllegalArgumentException("Cannot remove negative count");
        if (n > count) throw new InsufficientFundsException(denomination);
        count -= n;
    }
}
