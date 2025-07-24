package ru.otus;

public class UpDownSequence implements Sequence {
    private int current = 1;
    private boolean goingUp = true;
    private int cyclesCompleted = 0;
    private final int maxCycles;
    private final boolean infinite;
    private static final int MIN = 1;
    private static final int MAX = 10;

    public UpDownSequence(int maxCycles) {
        this.maxCycles = maxCycles;
        this.infinite = (maxCycles == 0);
    }

    @Override
    public int next() {
        if (!infinite && cyclesCompleted >= maxCycles) {
            return -1;
        }

        int result = current;

        if (goingUp) {
            current++;
            if (current > MAX) {
                current = MAX - 1;
                goingUp = false;
            }
        } else {
            current--;
            if (current < MIN) {
                current = MIN + 1;
                goingUp = true;
                cyclesCompleted++;
            }
        }

        return result;
    }

    @Override
    public boolean hasNext() {
        return infinite || cyclesCompleted < maxCycles;
    }
}
