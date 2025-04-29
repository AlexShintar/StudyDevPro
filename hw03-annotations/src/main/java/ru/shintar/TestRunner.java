package ru.shintar;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shintar.annotations.After;
import ru.shintar.annotations.Before;
import ru.shintar.annotations.Test;

public class TestRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunner.class);

    public static void runTests(Class<?> testClass) {
        TestContext context = prepare(testClass);
        executeTests(testClass, context);
        printSummary(context);
    }

    private static TestContext prepare(Class<?> testClass) {
        Method[] allMethods = testClass.getDeclaredMethods();

        List<Method> beforeMethods = Arrays.stream(allMethods)
                .filter(m -> m.isAnnotationPresent(Before.class))
                .collect(Collectors.toList());

        List<Method> testMethods = Arrays.stream(allMethods)
                .filter(m -> m.isAnnotationPresent(Test.class))
                .collect(Collectors.toList());

        List<Method> afterMethods = Arrays.stream(allMethods)
                .filter(m -> m.isAnnotationPresent(After.class))
                .collect(Collectors.toList());

        return new TestContext(beforeMethods, testMethods, afterMethods);
    }

    private static void executeTests(Class<?> testClass, TestContext context) {
        for (Method test : context.testMethods) {
            LOGGER.info("=== Running test: {} ===", test.getName());
            boolean beforePassed = true;
            Object instance;

            try {
                instance = testClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                LOGGER.error("[SKIP] {} due to error creating instance: {}", test.getName(), e.getMessage());
                markTestFailed(context, test.getName());
                continue;
            }

            try {
                for (Method before : context.beforeMethods) {
                    before.invoke(instance);
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                LOGGER.error("[SKIP] {} due to error in @Before", test.getName());
                markTestFailed(context, test.getName());
                beforePassed = false;
            }

            if (beforePassed) {
                try {
                    test.invoke(instance);
                    LOGGER.info("[PASS] {}", test.getName());
                    context.passed++;
                    context.passedTests.add(test.getName());
                } catch (InvocationTargetException e) {
                    LOGGER.error("[FAIL] {}", test.getName());
                    markTestFailed(context, test.getName());
                } catch (Exception e) {
                    LOGGER.error("[ERROR] {}", test.getName());
                    markTestFailed(context, test.getName());
                }

                for (Method after : context.afterMethods) {
                    try {
                        after.invoke(instance);
                    } catch (Exception e) {
                        LOGGER.warn("[ERROR in @After] {}", after.getName());
                    }
                }
            }
        }
    }

    private static void printSummary(TestContext context) {
        LOGGER.info("=== Test Summary ===");
        LOGGER.info(
                "Total: {}, Passed: {}, Failed: {}", context.passed + context.failed, context.passed, context.failed);

        if (!context.passedTests.isEmpty()) {
            LOGGER.info("Passed tests: {}", context.passedTests);
        }
        if (!context.failedTests.isEmpty()) {
            LOGGER.info("Failed tests: {}", context.failedTests);
        }
    }

    private static void markTestFailed(TestContext context, String testName) {
        context.failed++;
        context.failedTests.add(testName);
    }

    private static class TestContext {
        final List<Method> beforeMethods;
        final List<Method> testMethods;
        final List<Method> afterMethods;
        int passed = 0;
        int failed = 0;
        final List<String> passedTests = new ArrayList<>();
        final List<String> failedTests = new ArrayList<>();

        TestContext(List<Method> beforeMethods, List<Method> testMethods, List<Method> afterMethods) {
            this.beforeMethods = beforeMethods;
            this.testMethods = testMethods;
            this.afterMethods = afterMethods;
        }
    }
}
