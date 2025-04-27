package ru.shintar.aop.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shintar.aop.annotation.Log;

public class TestLogging implements TestLoggingInterface {

    private static final Logger logger = LoggerFactory.getLogger(TestLogging.class);

    @Log
    public void calculation(int param) {
        int result = param * param;
        logger.info("Result of calculation(int param): {}", result);
    }

    public void calculation(int param1, int param2) {
        int sum = param1 + param2;
        logger.info("Result of calculation(int param1, int param2): {}", sum);
    }

    @Log
    public void calculation(int param1, int param2, String param3) {
        String result = param3 + ": " + (param1 * param2);
        logger.info("Result of calculation(int param1, int param2, String param3): {}", result);
    }
}
