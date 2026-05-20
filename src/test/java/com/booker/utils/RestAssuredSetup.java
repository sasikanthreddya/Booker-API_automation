package com.booker.utils;

import com.booker.config.ConfigManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RestAssuredSetup {

    private static final Logger logger = LogManager.getLogger(RestAssuredSetup.class);

    public static void initialize() {
        ConfigManager config = ConfigManager.getInstance();
        RestAssured.baseURI = config.getBaseUrl();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        logger.info("RestAssured initialized with base URI: {}", config.getBaseUrl());
    }

    public static RequestSpecification buildBaseRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getInstance().getBaseUrl())
                .setContentType(ContentType.JSON)
                .addHeader("Accept", "application/json")
                .log(LogDetail.ALL)
                .build();
    }

    public static RequestSpecification buildAuthRequestSpec(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(buildBaseRequestSpec())
                .addHeader("Cookie", "token=" + token)
                .build();
    }

    public static ResponseSpecification buildBaseResponseSpec(int statusCode) {
        return new ResponseSpecBuilder()
                .expectStatusCode(statusCode)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
    }
}
