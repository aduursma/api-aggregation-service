package com.fedex.api.aggregation.dto;

import com.fedex.api.aggregation.SearchCriteriaBuilder;
import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

class SearchCriteriaTest {

    Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void verifyPricingCountryCodeCannotBeShorterThanMinLength() {
        doVerifyPricingCountryCode(new SearchCriteriaBuilder().pricing(List.of("N")).build());
    }

    @Test
    void verifyPricingCountryCodeCannotBeLongerThanMaxLength() {
        doVerifyPricingCountryCode(new SearchCriteriaBuilder().pricing(List.of("NLD")).build());
    }

    @Test
    void verifyTrackOrderNumberCannotBeShorterThanMinLength() {
        doVerifyTrackOrderNumber(new SearchCriteriaBuilder().track(List.of("12345678")).build());
    }

    @Test
    void verifyTrackOrderNumberCannotBeLongerThanMaxLength() {
        doVerifyTrackOrderNumber(new SearchCriteriaBuilder().track(List.of("1234567890")).build());
    }

    @Test
    void verifyTrackOrderNumberMustBeNumeric() {
        doVerifyTrackOrderNumber(new SearchCriteriaBuilder().track(List.of("12A456B89")).build());
    }

    @Test
    void verifyShipmentsOrderNumberCannotBeShorterThanMinLength() {
        doVerifyShipmentsOrderNumber(new SearchCriteriaBuilder().shipments(List.of("12345678")).build());
    }

    @Test
    void verifyShipmentsOrderNumberCannotBeLongerThanMaxLength() {
        doVerifyShipmentsOrderNumber(new SearchCriteriaBuilder().shipments(List.of("1234567890")).build());
    }

    @Test
    void verifyShipmentsOrderNumberMustBeNumeric() {
        doVerifyShipmentsOrderNumber(new SearchCriteriaBuilder().shipments(List.of("12A456B89")).build());
    }

    @Test
    void verifyEquality() {
        new EqualsTester()
            .addEqualityGroup(SearchCriteriaBuilder.validCriteria(), SearchCriteriaBuilder.validCriteria())
            .addEqualityGroup(new SearchCriteriaBuilder().pricing(List.of("NL")).track(List.of("123456891")).shipments(List.of("109347263")))
            .testEquals();
    }

    void doVerifyPricingCountryCode(SearchCriteria searchCriteria) {
        ConstraintViolation<SearchCriteria> violation = getViolation(searchCriteria);

        assertThat(violation.getPropertyPath().iterator().next().toString(), equalTo("pricing[0]"));
        assertThat(violation.getMessage(), equalTo("size must be between 2 and 2"));
    }

    void doVerifyTrackOrderNumber(SearchCriteria searchCriteria) {
        ConstraintViolation<SearchCriteria> violation = getViolation(searchCriteria);

        assertThat(violation.getPropertyPath().iterator().next().toString(), equalTo("track[0]"));
        assertThat(violation.getMessage(), equalTo("must match \"^\\d{9}$\""));
    }

    void doVerifyShipmentsOrderNumber(SearchCriteria searchCriteria) {
        ConstraintViolation<SearchCriteria> violation = getViolation(searchCriteria);

        assertThat(violation.getPropertyPath().iterator().next().toString(), equalTo("shipments[0]"));
        assertThat(violation.getMessage(), equalTo("must match \"^\\d{9}$\""));
    }

    ConstraintViolation<SearchCriteria> getViolation(SearchCriteria searchCriteria) {
        Set<ConstraintViolation<SearchCriteria>> violations = validator.validate(searchCriteria);

        assertThat(violations, is(notNullValue()));
        assertThat(violations.size(), equalTo(1));

        return violations.iterator().next();
    }

}
