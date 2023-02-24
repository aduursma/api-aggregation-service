package com.fedex.api.aggregation.controller;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.fedex.api.aggregation.TestConstants.AGGREGATION_URI;
import static com.fedex.api.aggregation.TestConstants.PRICING_PARAM;
import static com.fedex.api.aggregation.TestConstants.SHIPMENTS_PARAM;
import static com.fedex.api.aggregation.TestConstants.SLA_TIMEOUT;
import static com.fedex.api.aggregation.TestConstants.TRACKING_PARAM;
import static io.restassured.RestAssured.given;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.nullValue;

class ApiAggregationControllerIT extends IntegrationTestBase {

    @Test
    void verifyValidResponse() {
        Map<String, String> params = new HashMap<>();
        params.put(PRICING_PARAM, "NL,CN");
        params.put(TRACKING_PARAM, "109347263,123456891");
        params.put(SHIPMENTS_PARAM, "109347263,123456891");

        given()
            .log()
            .ifValidationFails()
            .queryParams(params)
        .when()
            .get(AGGREGATION_URI)
        .then()
            .log()
            .ifValidationFails()
            .statusCode(200)
            .time(lessThan(SLA_TIMEOUT), SECONDS)
            .body("pricing.NL", equalTo(14.242090605778))
            .body("pricing.CN", equalTo(20.503467806384))
            .body("track.109347263", equalTo("NEW"))
            .body("track.123456891", equalTo("COLLECTING"))
            .body("shipments.109347263.size()", is(3))
            .body("shipments.109347263", hasItems("box", "box", "pallet"))
            .body("shipments.123456891", hasItems("envelope"));
    }

    @Test
    void verifyValidResponseWhenServiceUnavailable() {
        Map<String, String> params = new HashMap<>();
        params.put(PRICING_PARAM, "BE");
        params.put(TRACKING_PARAM, "986543211");
        params.put(SHIPMENTS_PARAM, "986543211");

        given()
            .log()
            .ifValidationFails()
            .queryParams(params)
        .when()
            .get(AGGREGATION_URI)
        .then()
            .log()
            .ifValidationFails()
            .statusCode(200)
            .time(lessThan(SLA_TIMEOUT), SECONDS)
            .body("pricing", nullValue())
            .body("track", nullValue())
            .body("shipments", nullValue());
    }

    @Test
    void verifyValidResponseWhenPricingApiTimeoutOccurs() {
        Map<String, String> params = new HashMap<>();
        params.put(PRICING_PARAM, "DE");
        params.put(TRACKING_PARAM, "109347263,123456891");
        params.put(SHIPMENTS_PARAM, "109347263,123456891");

        given()
            .log()
            .ifValidationFails()
            .queryParams(params)
        .when()
            .get(AGGREGATION_URI)
        .then()
            .log()
            .ifValidationFails()
            .statusCode(200)
            .time(lessThan(SLA_TIMEOUT), SECONDS)
            .body("pricing", nullValue())
            .body("track.109347263", equalTo("NEW"))
            .body("track.123456891", equalTo("COLLECTING"))
            .body("shipments.109347263.size()", is(3))
            .body("shipments.109347263", hasItems("box", "box", "pallet"))
            .body("shipments.123456891", hasItems("envelope"));
    }

    @Test
    void verifyResponseDoesNotContainDuplicates() {
        Map<String, String> params = new HashMap<>();
        params.put(PRICING_PARAM, "NL,NL,NL,NL,NL");
        params.put(TRACKING_PARAM, "109347263,123456891");
        params.put(SHIPMENTS_PARAM, "109347263,123456891");

        given()
            .log()
            .ifValidationFails()
            .queryParams(params)
        .when()
            .get(AGGREGATION_URI)
        .then()
            .log()
            .all()
            .statusCode(200)
            .time(lessThan(SLA_TIMEOUT), SECONDS)
            .body("pricing.size()", equalTo(1))
            .body("pricing.NL", equalTo(14.242090605778))
            .body("track.109347263", equalTo("NEW"))
            .body("track.123456891", equalTo("COLLECTING"))
            .body("shipments.109347263.size()", is(3))
            .body("shipments.109347263", hasItems("box", "box", "pallet"))
            .body("shipments.123456891", hasItems("envelope"));
    }

    @Test
    void verifyNoRetainedParametersWhenMaxNumberOfParametersIsExceeded() {
        Map<String, String> params = new HashMap<>();
        params.put(PRICING_PARAM, "CZ,PT,IT,FR,LU,ES,HU,HR,PL");

        given()
            .log()
            .ifValidationFails()
            .queryParams(params)
        .when()
            .get(AGGREGATION_URI)
        .then()
            .log()
            .ifValidationFails()
            .statusCode(200)
            .time(lessThan(SLA_TIMEOUT), SECONDS)
            .body("pricing.size()", equalTo(5))
            .body("pricing.CZ", equalTo(54.73263674577634))
            .body("pricing.PT", equalTo(53.81785697100816))
            .body("pricing.IT", equalTo(33.76128465045609))
            .body("pricing.FR", equalTo(1.8169885101357197))
            .body("pricing.LU", equalTo(59.80409850807781))
            .body("track", nullValue())
            .body("shipments", nullValue());

        Map<String, String> newParams = new HashMap<>();
        newParams.put(PRICING_PARAM, "NL,CN");

        given()
            .log()
            .ifValidationFails()
            .queryParams(newParams)
        .when()
            .get(AGGREGATION_URI)
        .then()
            .log()
            .ifValidationFails()
            .statusCode(200)
            .time(lessThan(SLA_TIMEOUT), SECONDS)
            .body("pricing.size()", equalTo(2))
            .body("pricing.NL", equalTo(14.242090605778))
            .body("pricing.CN", equalTo(20.503467806384))
            .body("track", nullValue())
            .body("shipments", nullValue());;
    }

}
