package com.fedex.api.aggregation.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWireMock(port = 0, files = "classpath:/stubs")
abstract class WiremockTestBase {

    @LocalServerPort
    Integer serverPort;

    @BeforeEach
    public void setUpRestAssured() {
        RestAssured.port = serverPort;
    }

}
