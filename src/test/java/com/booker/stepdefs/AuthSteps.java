package com.booker.stepdefs;

import com.booker.config.ConfigManager;
import com.booker.models.AuthRequest;
import com.booker.utils.RestAssuredSetup;
import com.booker.utils.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AuthSteps {

    private static final Logger logger = LogManager.getLogger(AuthSteps.class);
    private final ScenarioContext context;

    public AuthSteps(ScenarioContext context) {
        this.context = context;
    }

    @When("I send a POST request to {string} with valid credentials")
    public void iSendAuthRequestWithValidCredentials(String endpoint) {
        ConfigManager config = ConfigManager.getInstance();
        AuthRequest request = AuthRequest.builder()
                .username(config.getAdminUsername())
                .password(config.getAdminPassword())
                .build();

        Response response = given()
                .spec(RestAssuredSetup.buildBaseRequestSpec())
                .body(request)
                .when()
                .post(endpoint)
                .then()
                .extract().response();

        context.setLastResponse(response);
        logger.info("Auth response: {}", response.asString());
    }

    @When("I send a POST request to {string} with invalid credentials")
    public void iSendAuthRequestWithInvalidCredentials(String endpoint) {
        AuthRequest request = AuthRequest.builder()
                .username("invalidUser")
                .password("wrongPass")
                .build();

        Response response = given()
                .spec(RestAssuredSetup.buildBaseRequestSpec())
                .body(request)
                .when()
                .post(endpoint)
                .then()
                .extract().response();

        context.setLastResponse(response);
    }

    @When("I send a POST request to {string} with username {string} and password {string}")
    public void iSendAuthRequestWithUsernameAndPassword(String endpoint, String username, String password) {
        AuthRequest request = AuthRequest.builder()
                .username(username)
                .password(password)
                .build();

        Response response = given()
                .spec(RestAssuredSetup.buildBaseRequestSpec())
                .body(request)
                .when()
                .post(endpoint)
                .then()
                .extract().response();

        context.setLastResponse(response);
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int statusCode) {
        int actual = context.getLastResponse().getStatusCode();
        logger.info("Expected status: {}, Actual status: {}", statusCode, actual);
        assertEquals("Status code mismatch", statusCode, actual);
    }

    @And("the response should contain a valid token")
    public void theResponseShouldContainAValidToken() {
        String token = context.getLastResponse().jsonPath().getString("token");
        assertThat("Token should not be null or empty", token, not(emptyOrNullString()));
        logger.info("Valid token received");
    }

    @And("the response body should contain {string}")
    public void theResponseBodyShouldContain(String expectedText) {
        String body = context.getLastResponse().asString();
        assertTrue("Response body should contain: " + expectedText, body.contains(expectedText));
    }
}
