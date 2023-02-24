package com.fedex.api.aggregation.controller;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.restassured3.RestDocumentationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured3.RestAssuredRestDocumentation.documentationConfiguration;

@ExtendWith( {RestDocumentationExtension.class, SpringExtension.class} )
@ActiveProfiles("documentation-test")
abstract class DocumentationTestBase extends WiremockTestBase {

    RequestSpecification documentationSpec;
    RestDocumentationFilter documentationFilter;

    @BeforeEach
    public void setUpRestDocs(RestDocumentationContextProvider restDocumentation) {
        this.documentationFilter = document("{method-name}",
            preprocessRequest(prettyPrint(), modifyHostname(), removeRequestHeaders()),
            preprocessResponse(prettyPrint(), modifyHostname(), removeResponseHeaders())
        );

        this.documentationSpec = new RequestSpecBuilder()
            .addFilter(documentationConfiguration(restDocumentation))
            .addFilter(this.documentationFilter)
            .build();
    }

    public static OperationPreprocessor modifyHostname() {
        return modifyUris().scheme("http").host("localhost").port(8080);
    }

    public static OperationPreprocessor removeRequestHeaders() {
        return removeHeaders("Accept");
    }

    public static OperationPreprocessor removeResponseHeaders() {
        return removeHeaders("X-Application-Context");
    }

}
