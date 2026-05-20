package com.booker.stepdefs;

import com.booker.utils.RestAssuredSetup;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Hooks {

    private static final Logger logger = LogManager.getLogger(Hooks.class);
    private static boolean initialized = false;

    @Before(order = 0)
    public void globalSetup() {
        if (!initialized) {
            RestAssuredSetup.initialize();
            initialized = true;
        }
    }

    @Before(order = 1)
    public void beforeScenario(Scenario scenario) {
        logger.info("=== Starting Scenario: {} [{}] ===", scenario.getName(), scenario.getStatus());
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            logger.error("=== FAILED Scenario: {} ===", scenario.getName());
        } else {
            logger.info("=== PASSED Scenario: {} ===", scenario.getName());
        }
    }
}
