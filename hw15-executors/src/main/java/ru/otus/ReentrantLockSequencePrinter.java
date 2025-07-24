package ru.otus;

import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReentrantLockSequencePrinter implements SequencePrinter {
    private static final Logger logger = LoggerFactory.getLogger(ReentrantLockSequencePrinter.class);

    private final ReentrantLock lock = new ReentrantLock();
    private int currentThread = 1;

    @Override
    public void printSequence(Sequence sequence, int threadId) {
        while (sequence.hasNext() && !Thread.currentThread().isInterrupted()) {
            lock.lock();
            try {
                if (currentThread == threadId) {
                    int number = sequence.next();
                    logger.info("Поток {}: {}", threadId, number);
                    currentThread = 3 - threadId;
                    sleep(500);
                }
            } finally {
                lock.unlock();
            }
            sleep(5);
        }
        logger.info("Поток {} завершил работу", threadId);
    }

    private static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
