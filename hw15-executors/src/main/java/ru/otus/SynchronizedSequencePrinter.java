package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronizedSequencePrinter implements SequencePrinter {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizedSequencePrinter.class);
    private int currentThread = 1;

    public synchronized void printSequence(Sequence sequence, int threadId) {
        while (sequence.hasNext() && !Thread.currentThread().isInterrupted()) {
            try {
                while (currentThread != threadId && sequence.hasNext()) {
                    this.wait();
                }

                if (sequence.hasNext() && !Thread.currentThread().isInterrupted()) {
                    int number = sequence.next();
                    logger.info("Поток {}: {}", threadId, number);

                    currentThread = threadId == 1 ? 2 : 1;
                    sleep();
                    notifyAll();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        logger.info("Поток {} завершил работу", threadId);
    }

    private static void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
