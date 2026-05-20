package com.booker.stepdefs;

import com.booker.models.Booking;
import com.booker.utils.RestAssuredSetup;
import com.booker.utils.ScenarioContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BookingSteps {

    private static final Logger logger = LogManager.getLogger(BookingSteps.class);
    private final ScenarioContext context;

    public BookingSteps(ScenarioContext context) {
        this.context = context;
    }

    // ===================== Helper =====================

    private Booking buildBookingFromMap(Map<String, String> details) {
        return Booking.builder()
                .firstname(details.get("firstname"))
                .lastname(details.get("lastname"))
                .totalPrice(Integer.parseInt(details.getOrDefault("totalprice", "100")))
                .depositPaid(Boolean.parseBoolean(details.getOrDefault("depositpaid", "false")))
                .bookingDates(Booking.BookingDates.builder()
                        .checkin(details.get("checkin"))
                        .checkout(details.get("checkout"))
                        .build())
                .additionalNeeds(details.get("additionalneeds"))
                .build();
    }

    private Booking defaultBooking() {
        return Booking.builder()
                .firstname("James")
                .lastname("Brown")
                .totalPrice(150)
                .depositPaid(true)
                .bookingDates(Booking.BookingDates.builder()
                        .checkin("2025-01-01")
                        .checkout("2025-01-07")
                        .build())
                .additionalNeeds("Breakfast")
                .build();
    }

    // ===================== Given =====================

    @Given("a booking exists")
    public void aBookingExists() {
        Response response = given()
                .spec(RestAssuredSetup.buildBaseRequestSpec())
                .body(defaultBooking())
                .when()
                .post("/booking")
                .then()
                .statusCode(200)
                .extract().response();

        int bookingId = response.jsonPath().getInt("bookingid");
        context.setLastBookingId(bookingId);
        logger.info("Created test booking with ID: {}", bookingId);
    }

    // ===================== GET =====================

    @When("I send a GET request to {string}")
    public void iSendGETRequest(String endpoint) {
        Response response = given()
                .spec(RestAssuredSetup.buildBaseRequestSpec())
                .when()
                .get(endpoint)
                .then()
                .extract().response();

        context.setLastResponse(response);
    }

    @When("I send a GET request to {string} with query param {string} as {string}")
    public void iSendGETRequestWithQueryParam(String endpoint, String paramName, String paramValue) {
        Response response = given()
                .spec(RestAssuredSetup.buildBaseRequestSpec())
                .queryParam(paramName, paramValue)
                .when()
                .get(endpoint)
                .then()
                .extract().response();

        context.setLastResponse(response);
    }

    @When("I send a GET request to the booking")
    public void iSendGETRequestForBookingByIdFromContext() {
        int bookingId = context.getLastBookingId();
        iSendGETRequest("/booking/" + bookingId);
    }

    // ===================== POST Create =====================

    @When("I create a booking with the following details:")
    public void iCreateABookingWithDetails(Map<String, String> details) {
        Booking booking = buildBookingFromMap(details);

        Response response = given()
                .spec(RestAssuredSetup.buildBaseRequestSpec())
                .body(booking)
                .when()
                .post("/booking")
                .then()
                .extract().response();

        context.setLastResponse(response);
        if (response.getStatusCode() == 200) {
            int bookingId = response.jsonPath().getInt("bookingid");
            context.setLastBookingId(bookingId);
            logger.info("Created booking with ID: {}", bookingId);
        }
    }

    @When("I send an invalid POST request to {string} with body:")
    public void iSendInvalidPostRequest(String endpoint, String body) {
        Response response = given()
                .spec(RestAssuredSetup.buildBaseRequestSpec())
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .extract().response();

        context.setLastResponse(response);
    }

    // ===================== PUT Update =====================

    @When("I update the booking with the following details:")
    public void iUpdateTheBookingWithDetails(Map<String, String> details) {
        Booking booking = buildBookingFromMap(details);
        int bookingId = context.getLastBookingId();

        Response response = given()
                .spec(RestAssuredSetup.buildAuthRequestSpec(context.getAuthToken()))
                .body(booking)
                .when()
                .put("/booking/" + bookingId)
                .then()
                .extract().response();

        context.setLastResponse(response);
        logger.info("PUT /booking/{} => {}", bookingId, response.getStatusCode());
    }

    @When("I update the booking without authentication")
    public void iUpdateTheBookingWithoutAuthentication() {
        int bookingId = context.getLastBookingId();

        Response response = given()
                .spec(RestAssuredSetup.buildBaseRequestSpec())
                .body(defaultBooking())
                .when()
                .put("/booking/" + bookingId)
                .then()
                .extract().response();

        context.setLastResponse(response);
    }

    // ===================== PATCH Partial Update =====================

    @When("I partially update the booking with:")
    public void iPartiallyUpdateTheBookingWith(Map<String, String> fields) {
        int bookingId = context.getLastBookingId();

        Response response = given()
                .spec(RestAssuredSetup.buildAuthRequestSpec(context.getAuthToken()))
                .body(fields)
                .when()
                .patch("/booking/" + bookingId)
                .then()
                .extract().response();

        context.setLastResponse(response);
        logger.info("PATCH /booking/{} => {}", bookingId, response.getStatusCode());
    }

    @When("I partially update the booking without authentication")
    public void iPartiallyUpdateWithoutAuth() {
        int bookingId = context.getLastBookingId();

        Response response = given()
                .spec(RestAssuredSetup.buildBaseRequestSpec())
                .body(Map.of("firstname", "Unauthorized"))
                .when()
                .patch("/booking/" + bookingId)
                .then()
                .extract().response();

        context.setLastResponse(response);
    }

    // ===================== DELETE =====================

    @When("I delete the booking")
    public void iDeleteTheBooking() {
        int bookingId = context.getLastBookingId();

        Response response = given()
                .spec(RestAssuredSetup.buildAuthRequestSpec(context.getAuthToken()))
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .extract().response();

        context.setLastResponse(response);
        logger.info("DELETE /booking/{} => {}", bookingId, response.getStatusCode());
    }

    @When("I delete the booking without authentication")
    public void iDeleteTheBookingWithoutAuthentication() {
        int bookingId = context.getLastBookingId();

        Response response = given()
                .spec(RestAssuredSetup.buildBaseRequestSpec())
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .extract().response();

        context.setLastResponse(response);
    }

    @When("I delete booking with ID {int}")
    public void iDeleteBookingWithId(int bookingId) {
        Response response = given()
                .spec(RestAssuredSetup.buildAuthRequestSpec(context.getAuthToken()))
                .when()
                .delete("/booking/" + bookingId)
                .then()
                .extract().response();

        context.setLastResponse(response);
    }

    // ===================== Assertions =====================

    @Then("the response should return a list of booking IDs")
    public void theResponseShouldReturnAListOfBookingIds() {
        List<?> bookingIds = context.getLastResponse().jsonPath().getList("$");
        assertNotNull("Booking list should not be null", bookingIds);
        logger.info("Retrieved {} booking(s)", bookingIds.size());
    }

    @And("the response should contain a booking ID")
    public void theResponseShouldContainABookingId() {
        int bookingId = context.getLastResponse().jsonPath().getInt("bookingid");
        assertThat("Booking ID should be positive", bookingId, greaterThan(0));
    }

    @And("the booking response should have {string} as {string}")
    public void theBookingResponseShouldHaveFieldAs(String field, String expectedValue) {
        String actualValue = context.getLastResponse().jsonPath().getString(field);
        assertEquals("Field '" + field + "' mismatch", expectedValue, actualValue);
    }

    @And("the booking firstname should be {string}")
    public void theBookingFirstnameShouldBe(String expected) {
        String actual = context.getLastResponse().jsonPath().getString("firstname");
        // PUT/PATCH returns the booking directly; POST returns it nested under "booking"
        if (actual == null) {
            actual = context.getLastResponse().jsonPath().getString("booking.firstname");
        }
        assertEquals("Firstname mismatch", expected, actual);
        logger.info("Firstname verified: {}", actual);
    }

    @And("the booking lastname should be {string}")
    public void theBookingLastnameShouldBe(String expected) {
        String actual = context.getLastResponse().jsonPath().getString("lastname");
        if (actual == null) {
            actual = context.getLastResponse().jsonPath().getString("booking.lastname");
        }
        assertEquals("Lastname mismatch", expected, actual);
    }

   
}
