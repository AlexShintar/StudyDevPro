package ru.shintar;

public class Main {

    public static void main(String[] args) {
        TestRunner.runTests(SimpleTests.class);
        TestRunner.runTests(RandomizedLifecycleTests.class);
    }
}
