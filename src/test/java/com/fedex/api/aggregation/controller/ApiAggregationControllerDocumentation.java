package com.fedex.api.aggregation.controller;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.fedex.api.aggregation.TestConstants.AGGREGATION_URI;
import static com.fedex.api.aggregation.TestConstants.PRICING_PARAM;
import static com.fedex.api.aggregation.TestConstants.SHIPMENTS_PARAM;
import static com.fedex.api.aggregation.TestConstants.TRACKING_PARAM;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;

class ApiAggregationControllerDocumentation extends DocumentationTestBase {

    public static final String PRICING_PARAM_DESCRIPTION = "Country code(s) for which to retrieve pricing information.";
    public static final String TRACKING_PARAM_DESCRIPTION = "Order number(s) for which to retrieve tracking information.";
    public static final String SHIPMENTS_PARAM_DESCRIPTION = "Order number(s) for which to retrieve shipment information.";

    public static final String PRICING_FIELD = PRICING_PARAM;
    public static final String TRACKING_FIELD = TRACKING_PARAM;
    public static final String SHIPMENTS_FIELD = SHIPMENTS_PARAM;

    public static final String PRICING_FIELD_DESCRIPTION = "The requested pricing results from the Pricing API.";
    public static final String TRACKING_FIELD_DESCRIPTION = "The requested tracking results from the Track API.";
    public static final String SHIPMENTS_FIELD_DESCRIPTION = "The requested results from the Shipments API.";

    @Test
    void aggregation() {
        Map<String, String> params = new HashMap<>();
        params.put(PRICING_PARAM, "NL,CN");
        params.put(TRACKING_PARAM, "109347263,123456891");
        params.put(SHIPMENTS_PARAM, "109347263,123456891");

        given(this.documentationSpec)
            .filter(document("{method-name}",
                requestParameters(
                    parameterWithName(PRICING_PARAM).description(PRICING_PARAM_DESCRIPTION),
                    parameterWithName(TRACKING_PARAM).description(TRACKING_PARAM_DESCRIPTION),
                    parameterWithName(SHIPMENTS_PARAM).description(SHIPMENTS_PARAM_DESCRIPTION)),
                relaxedResponseFields(
                    fieldWithPath(PRICING_FIELD).description(PRICING_FIELD_DESCRIPTION),
                    fieldWithPath(TRACKING_FIELD).description(TRACKING_FIELD_DESCRIPTION),
                    fieldWithPath(SHIPMENTS_FIELD).description(SHIPMENTS_FIELD_DESCRIPTION))))
            .queryParams(params)
        .when()
            .get(AGGREGATION_URI)
        .then()
            .statusCode(200);
    }

    @Test
    void aggregationWhenServiceUnavailable() {
        Map<String, String> params = new HashMap<>();
        params.put(PRICING_PARAM, "BE");
        params.put(TRACKING_PARAM, "109347263,123456891");
        params.put(SHIPMENTS_PARAM, "109347263,123456891");

        given(this.documentationSpec)
            .filter(document("{method-name}",
                requestParameters(
                    parameterWithName(PRICING_PARAM).description(PRICING_PARAM_DESCRIPTION),
                    parameterWithName(TRACKING_PARAM).description(TRACKING_PARAM_DESCRIPTION),
                    parameterWithName(SHIPMENTS_PARAM).description(SHIPMENTS_PARAM_DESCRIPTION)),
                relaxedResponseFields(
                    fieldWithPath(PRICING_FIELD).description(PRICING_FIELD_DESCRIPTION),
                    fieldWithPath(TRACKING_FIELD).description(TRACKING_FIELD_DESCRIPTION),
                    fieldWithPath(SHIPMENTS_FIELD).description(SHIPMENTS_FIELD_DESCRIPTION))))
            .queryParams(params)
        .when()
            .get(AGGREGATION_URI)
        .then()
            .statusCode(200);
    }

}
