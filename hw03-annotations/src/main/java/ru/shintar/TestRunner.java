package ru.shintar;

import java.lang.reflect.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shintar.annotations.After;
import ru.shintar.annotations.Before;
import ru.shintar.annotations.Test;

public class TestRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRunner.class);

    public static void runTests(Class<?> testClass) {
        Object instance;
        try {
            instance = testClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate test class", e);
        }

        List<Method> beforeMethods = new ArrayList<>();
        List<Method> testMethods = new ArrayList<>();
        List<Method> afterMethods = new ArrayList<>();

        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Before.class)) beforeMethods.add(method);
            if (method.isAnnotationPresent(Test.class)) testMethods.add(method);
            if (method.isAnnotationPresent(After.class)) afterMethods.add(method);
        }

        int passed = 0;
        int failed = 0;
        List<String> passedTests = new ArrayList<>();
        List<String> failedTests = new ArrayList<>();

        for (Method test : testMethods) {
            LOGGER.info("=== Running test: {} ===", test.getName());
            boolean beforePassed = true;

            try {
                for (Method before : beforeMethods) {
                    before.invoke(instance);
                }
            } catch (InvocationTargetException | IllegalAccessException e) {
                LOGGER.error("[SKIP] {} due to error in @Before", test.getName());
                failed++;
                failedTests.add(test.getName());
                beforePassed = false;
            }

            if (beforePassed) {
                try {
                    test.invoke(instance);
                    LOGGER.info("[PASS] {}", test.getName());
                    passed++;
                    passedTests.add(test.getName());

                } catch (InvocationTargetException e) {
                    LOGGER.error("[FAIL] {}", test.getName());
                    failed++;
                    failedTests.add(test.getName());

                } catch (Exception e) {
                    LOGGER.error("[ERROR] {}", test.getName());
                    failed++;
                    failedTests.add(test.getName());
                }

                for (Method after : afterMethods) {
                    try {
                        after.invoke(instance);
                    } catch (Exception e) {
                        LOGGER.warn("[ERROR in @After] {}", after.getName());
                    }
                }
            }
        }

        LOGGER.info("=== Test Summary ===");
        LOGGER.info("Total: {}, Passed: {}, Failed: {}", passed + failed, passed, failed);

        if (!passedTests.isEmpty()) {
            LOGGER.info("Passed tests: {}", passedTests);
        }
        if (!failedTests.isEmpty()) {
            LOGGER.info("Failed tests: {}", failedTests);
        }
    }
}
