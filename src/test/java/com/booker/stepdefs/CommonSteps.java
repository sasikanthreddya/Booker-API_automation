package com.booker.stepdefs;

import com.booker.config.ConfigManager;
import com.booker.models.AuthRequest;
import com.booker.utils.RestAssuredSetup;
import com.booker.utils.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;

public class CommonSteps {

    private static final Logger logger = LogManager.getLogger(CommonSteps.class);
    private final ScenarioContext context;

    public CommonSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("the API base URL is configured")
    public void theApiBaseUrlIsConfigured() {
        String baseUrl = ConfigManager.getInstance().getBaseUrl();
        logger.info("Base URL configured: {}", baseUrl);
        assertNotNull("Base URL must be set", baseUrl);
    }

    @Given("I am authenticated with valid credentials")
    public void iAmAuthenticatedWithValidCredentials() {
        ConfigManager config = ConfigManager.getInstance();
        AuthRequest authRequest = AuthRequest.builder()
                .username(config.getAdminUsername())
                .password(config.getAdminPassword())
                .build();

        Response response = given()
                .spec(RestAssuredSetup.buildBaseRequestSpec())
                .body(authRequest)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract().response();

        String token = response.jsonPath().getString("token");
        context.setAuthToken(token);
        logger.info("Authenticated. Token acquired: {}", token.substring(0, 5) + "****");
    }
}
