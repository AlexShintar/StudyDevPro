package ru.shintar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.shintar.annotations.After;
import ru.shintar.annotations.Before;
import ru.shintar.annotations.Test;

public class SimpleTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleTests.class);

    @Before
    public void setUp() {
        LOGGER.info("Setting up before test");
    }

    @Test
    public void testSuccess() {
        LOGGER.info("Running testSuccess");
    }

    @Test
    public void testFailure() {
        LOGGER.info("Running testFailure");
        throw new RuntimeException("This test is expected to fail");
    }

    @After
    public void tearDown() {
        LOGGER.info("Cleaning up after test");
    }
}
