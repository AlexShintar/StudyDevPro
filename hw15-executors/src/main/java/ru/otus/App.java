package ru.otus;

public class App {
    public static void main(String[] args) throws InterruptedException {
        SequencePrinter printerS = new SynchronizedSequencePrinter();
        SequencePrinter printerR = new ReentrantLockSequencePrinter();

        Thread t1 = new Thread(() -> printerS.printSequence(new UpDownSequence(1), 1), "Thread-1");
        Thread t2 = new Thread(() -> printerS.printSequence(new UpDownSequence(1), 2), "Thread-2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        t1 = new Thread(() -> printerR.printSequence(new UpDownSequence(0), 1), "Thread-1");
        t2 = new Thread(() -> printerR.printSequence(new UpDownSequence(0), 2), "Thread-2");

        t1.start();
        t2.start();
    }
}
