package com.fedex.api.aggregation.controller;

import com.fedex.api.aggregation.AggregatedResultBuilder;
import com.fedex.api.aggregation.SearchCriteriaBuilder;
import com.fedex.api.aggregation.dto.SearchCriteria;
import com.fedex.api.aggregation.service.ApiAggregationService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.fedex.api.aggregation.TestConstants.AGGREGATION_URI;
import static com.fedex.api.aggregation.TestConstants.PRICING_PARAM;
import static com.fedex.api.aggregation.TestConstants.SHIPMENTS_PARAM;
import static com.fedex.api.aggregation.TestConstants.TRACKING_PARAM;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiAggregationController.class)
class ApiAggregationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ApiAggregationService apiAggregationService;

    @BeforeEach
    void setUp() {
        when(apiAggregationService.aggregateResults(any(SearchCriteria.class))).thenReturn(AggregatedResultBuilder.validResult());
    }

    @Test
    void verifyInvalidPricingParameterProducesErrorResponse() throws Exception {
        doVerifyErrorResponse(PRICING_PARAM, "NLD");
    }

    @Test
    void verifyInvalidTrackParameterProducesErrorResponse() throws Exception {
        doVerifyErrorResponse(TRACKING_PARAM, "123");
    }

    @Test
    void verifyInvalidShipmentsParameterProducesErrorResponse() throws Exception {
        doVerifyErrorResponse(SHIPMENTS_PARAM, "123");
    }

    @Test
    void verifyValidResponseWhenValidSearchCriteria() throws Exception {
        SearchCriteria searchCriteria = SearchCriteriaBuilder.validCriteria();

        mockMvc
            .perform(get(AGGREGATION_URI)
                .param(PRICING_PARAM, StringUtils.join(searchCriteria.getPricing(), ","))
                .param(TRACKING_PARAM, StringUtils.join(searchCriteria.getTrack(), ","))
                .param(SHIPMENTS_PARAM, StringUtils.join(searchCriteria.getShipments(), ",")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pricing.NL").value(14.242090605778))
            .andExpect(jsonPath("$.pricing.CN").value(20.503467806384))
            .andExpect(jsonPath("$.track.109347263").value(nullValue()))
            .andExpect(jsonPath("$.track.123456891").value("COLLECTING"))
            .andExpect(jsonPath("$.shipments.109347263").value(hasItems("box", "box", "pallet")))
            .andExpect(jsonPath("$.shipments.123456891").value(nullValue()));

        verify(apiAggregationService, times(1)).aggregateResults(any(SearchCriteria.class));
    }

    void doVerifyErrorResponse(String paramName, String paramValue) throws Exception {
        mockMvc
            .perform(get(AGGREGATION_URI).param(paramName, paramValue))
            .andExpect(status().isBadRequest());

        verify(apiAggregationService, times(0)).aggregateResults(any(SearchCriteria.class));
    }

}
