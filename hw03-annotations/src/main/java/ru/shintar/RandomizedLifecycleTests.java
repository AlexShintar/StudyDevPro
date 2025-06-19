package ru.shintar;

import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shintar.annotations.After;
import ru.shintar.annotations.Before;
import ru.shintar.annotations.Test;

public class RandomizedLifecycleTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(RandomizedLifecycleTests.class);
    private static final Random RANDOM = new Random();

    @Before
    public void prepareDatabase() {
        LOGGER.info("Preparing database...");
        randomlyFail("prepareDatabase");
    }

    @Before
    public void openConnections() {
        LOGGER.info("Opening connections...");
        randomlyFail("openConnections");
    }

    @Test
    public void runMainTest() {
        LOGGER.info("Running main test logic");
    }

    @After
    public void closeConnections() {
        LOGGER.info("Closing connections...");
        randomlyFail("closeConnections");
    }

    @After
    public void cleanup() {
        LOGGER.info("Cleaning up...");
        randomlyFail("cleanup");
    }

    private void randomlyFail(String methodName) {
        if (RANDOM.nextDouble() < 0.2) { // 20% chance of failure
            throw new RuntimeException("Random failure in " + methodName);
        }
    }
}
