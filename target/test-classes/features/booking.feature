@booking
Feature: Booking API
  As a user of Restful Booker
  I want to manage hotel bookings
  So that I can create, retrieve, update, and delete bookings

  Background:
    Given the API base URL is configured
    And I am authenticated with valid credentials

  # ===================== GET Bookings =====================

  @smoke @positive
  Scenario: Get all booking IDs
    When I send a GET request to "/booking"
    Then the response status code should be 200
    And the response should return a list of booking IDs

  @positive
  Scenario: Get bookings filtered by first name
    When I send a GET request to "/booking" with query param "firstname" as "Jim"
    Then the response status code should be 200
    And the response should return a list of booking IDs

  @positive
  Scenario: Get bookings filtered by check-in date
    When I send a GET request to "/booking" with query param "checkin" as "2024-01-01"
    Then the response status code should be 200
    And the response should return a list of booking IDs

  # ===================== GET Booking by ID =====================

  @smoke @positive
  Scenario: Get a specific booking by ID
    Given a booking exists
    When I send a GET request to the booking
    Then the response status code should be 200
    And the booking response should have "firstname" as "James"
    And the booking response should have "lastname" as "Brown"

  @negative
  Scenario: Get booking with non-existent ID
   # When I send a GET request to "/booking/9999999"
    Then the response status code should be 404

  # ===================== POST Create Booking =====================

  @smoke @positive
  Scenario: Create a new booking with all fields
    When I create a booking with the following details:
      | firstname       | James          |
      | lastname        | Brown          |
      | totalprice      | 150            |
      | depositpaid     | true           |
      | checkin         | 2025-01-01     |
      | checkout        | 2025-01-07     |
      | additionalneeds | Breakfast      |
    Then the response status code should be 200
    And the response should contain a booking ID
    And the booking firstname should be "James"
    And the booking lastname should be "Brown"

  @positive
  Scenario: Create a booking without additional needs
    When I create a booking with the following details:
      | firstname       | Alice          |
      | lastname        | Smith          |
      | totalprice      | 200            |
      | depositpaid     | false          |
      | checkin         | 2025-03-01     |
      | checkout        | 2025-03-05     |
      | additionalneeds |                |
    Then the response status code should be 200
    And the response should contain a booking ID

  @negative
  Scenario: Create booking with missing required fields
    When I send an invalid POST request to "/booking" with body:
      """
      {
        "firstname": "John"
      }
      """
    Then the response status code should be 500

  # ===================== PUT Update Booking =====================

  @positive
  Scenario: Update an existing booking fully
    Given a booking exists
    When I update the booking with the following details:
      | firstname       | Updated        |
      | lastname        | Name           |
      | totalprice      | 300            |
      | depositpaid     | true           |
      | checkin         | 2025-06-01     |
      | checkout        | 2025-06-10     |
      | additionalneeds | Lunch          |
    Then the response status code should be 200
    And the booking firstname should be "Updated"

  @negative
  Scenario: Update booking without auth token
    Given a booking exists
    When I update the booking without authentication
    Then the response status code should be 403

  # ===================== PATCH Partial Update =====================

  @positive
  Scenario: Partially update a booking's first name
    Given a booking exists
    When I partially update the booking with:
      | firstname | Patched |
    Then the response status code should be 200
    And the booking firstname should be "Patched"

  @negative
  Scenario: Partial update without auth token
    Given a booking exists
    When I partially update the booking without authentication
    Then the response status code should be 403

  # ===================== DELETE Booking =====================

  @positive
  Scenario: Delete an existing booking
    Given a booking exists
    When I delete the booking
    Then the response status code should be 201

  @negative
  Scenario: Delete a booking without auth token
    Given a booking exists
    When I delete the booking without authentication
    Then the response status code should be 403

  @negative
  Scenario: Delete non-existent booking
    When I delete booking with ID 9999999
    Then the response status code should be 405
