# Restful Booker API Automation

This repository contains an automated API test suite for the Restful Booker demo API using Java, Maven, Cucumber, Rest Assured, and ExtentReports.

## Overview

The project implements BDD-style API test automation for the Restful Booker service. It covers authentication and booking-related API flows, including:

- auth token generation
- fetching bookings
- creating bookings
- updating bookings
- partially updating bookings
- deleting bookings
- negative / error scenarios

## Tech Stack

- Java 11
- Maven
- Rest Assured
- Cucumber JVM
- JUnit 4
- Jackson
- Lombok
- Log4j2
- ExtentReports for Cucumber

## Project Structure

- `src/test/java/com/booker/config` - API and test configuration helpers
- `src/test/java/com/booker/models` - request / response model classes
- `src/test/java/com/booker/runners` - Cucumber JUnit runner
- `src/test/java/com/booker/stepdefs` - step definition implementations
- `src/test/java/com/booker/utils` - utility helpers
- `src/test/resources/features` - Cucumber feature files
- `src/test/resources/config.properties` - base URL and auth credentials

## Key Files

- `pom.xml` - Maven build and dependency configuration
- `src/test/java/com/booker/runners/TestRunner.java` - primary Cucumber runner
- `src/test/resources/config.properties` - environment configuration
- `src/test/resources/features/auth.feature` - authentication feature scenarios
- `src/test/resources/features/booking.feature` - booking feature scenarios

## Prerequisites

- Java 21 or later installed
- Maven installed
- Internet access to run API tests against `https://restful-booker.herokuapp.com`

## Running Tests

From the repository root:

```powershell
mvn clean test
```

The current runner is configured to execute scenarios tagged with `@auth` by default in `src/test/java/com/booker/runners/TestRunner.java`.

If you want to run all feature scenarios, update the `tags` value in `TestRunner.java` or create additional runner classes for different tag groups.

## Configuration

Configuration values are stored in `src/test/resources/config.properties`:

- `base.url` - API base URL
- `admin.username` - admin username for auth
- `admin.password` - admin password for auth
- `request.timeout` - request timeout in milliseconds

## CI/CD

This repository includes a GitHub Actions workflow at `.github/workflows/ci.yml`.
The workflow runs on `push` and `pull_request` events targeting the `main` branch and performs:

- checkout of repository code
- JDK 11 setup
- Maven dependency caching
- `mvn -B clean test`
- upload of generated Cucumber report artifacts

## Reports

After test execution, reports are generated under:

- `target/cucumber-reports/cucumber.html`
- `target/cucumber-reports/cucumber.json`
- `target/cucumber-reports/cucumber.xml`
- ExtentReports output under `target/extent-reports` (via the Cucumber adapter)

## Notes

- The framework is currently focused on API automation for the Restful Booker demo service.
- Feature tags are used to group smoke, positive, and negative scenarios.
- To extend the suite, add new feature files under `src/test/resources/features` and implement matching step definitions.
