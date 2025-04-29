package ru.shintar.atm.domain;

import java.util.Comparator;

public record Denomination(int value) implements Comparable<Denomination> {

    public static final Comparator<Denomination> DESC =
            Comparator.comparingInt(Denomination::value).reversed();

    public Denomination {
        if (value <= 0) throw new IllegalArgumentException("Denomination value must be positive");
    }

    @Override
    public int compareTo(Denomination other) {
        return DESC.compare(this, other);
    }
}
