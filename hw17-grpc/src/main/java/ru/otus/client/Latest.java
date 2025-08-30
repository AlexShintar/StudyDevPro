package ru.otus.client;

public final class Latest {
    final int value;

    public final int seq;

    public Latest(int value, int seq) {
        this.value = value;
        this.seq = seq;
    }
}
