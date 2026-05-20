@auth
Feature: Authentication API
  As a user of Restful Booker
  I want to authenticate and obtain a token
  So that I can perform secured operations

  Background:
    Given the API base URL is configured

  @smoke @positive
  Scenario: Generate auth token with valid credentials
    When I send a POST request to "/auth" with valid credentials
    Then the response status code should be 200
    And the response should contain a valid token

  @negative
  Scenario: Generate auth token with invalid credentials
    When I send a POST request to "/auth" with invalid credentials
    Then the response status code should be 200
    And the response body should contain "Bad credentials"

  @negative
  Scenario Outline: Auth fails for missing fields
    When I send a POST request to "/auth" with username "<username>" and password "<password>"
    Then the response status code should be 200
    And the response body should contain "Bad credentials"

    Examples:
      | username | password |
      |          | password123 |
      | admin    |             |
