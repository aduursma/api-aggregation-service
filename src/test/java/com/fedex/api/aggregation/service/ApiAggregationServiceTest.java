package com.fedex.api.aggregation.service;

import com.fedex.api.aggregation.AggregatedResultBuilder;
import com.fedex.api.aggregation.SearchCriteriaBuilder;
import com.fedex.api.aggregation.dto.AggregatedResult;
import com.fedex.api.aggregation.repository.PricingRepository;
import com.fedex.api.aggregation.repository.ShipmentsRepository;
import com.fedex.api.aggregation.repository.TrackingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@SpringBootTest(webEnvironment = MOCK)
class ApiAggregationServiceTest {

    @Autowired
    ApiAggregationService apiAggregationService;

    @MockBean
    PricingRepository pricingRepository;

    @MockBean
    TrackingRepository trackingRepository;

    @MockBean
    ShipmentsRepository shipmentsRepository;

    @Test
    void verifyResultsWithValidSearchCriteria() {
        when(pricingRepository.retrievePricing()).thenReturn(CompletableFuture.completedFuture(AggregatedResultBuilder.validResult().getPricing()));
        when(trackingRepository.retrieveTracks()).thenReturn(CompletableFuture.completedFuture(AggregatedResultBuilder.validResult().getTrack()));
        when(shipmentsRepository.retrieveShipments()).thenReturn(CompletableFuture.completedFuture(AggregatedResultBuilder.validResult().getShipments()));

        AggregatedResult result = apiAggregationService.aggregateResults(SearchCriteriaBuilder.validCriteria());

        assertThat(result.getPricing())
            .isNotEmpty()
            .hasSize(2)
            .contains(entry("NL", new BigDecimal("14.242090605778")), entry("CN", new BigDecimal("20.503467806384")));

        assertThat(result.getTrack())
            .isNotEmpty()
            .hasSize(2)
            .contains(entry("109347263", null), entry("123456891", "COLLECTING"));

        assertThat(result.getShipments())
            .isNotEmpty()
            .hasSize(2)
            .contains(entry("109347263", List.of("box", "box", "pallet")), entry("123456891", null));

        verify(pricingRepository, times(1)).enqueue(anyList());
        verify(pricingRepository, times(1)).retrievePricing();

        verify(trackingRepository, times(1)).enqueue(anyList());
        verify(trackingRepository, times(1)).retrieveTracks();

        verify(shipmentsRepository, times(1)).enqueue(anyList());
        verify(shipmentsRepository, times(1)).retrieveShipments();
    }

    @Test
    void verifyResultsWithoutSearchCriteria() {
        when(pricingRepository.retrievePricing()).thenReturn(CompletableFuture.completedFuture(null));
        when(trackingRepository.retrieveTracks()).thenReturn(CompletableFuture.completedFuture(null));
        when(shipmentsRepository.retrieveShipments()).thenReturn(CompletableFuture.completedFuture(null));

        AggregatedResult result = apiAggregationService.aggregateResults(new SearchCriteriaBuilder().build());

        assertThat(result.getPricing()).isNull();
        assertThat(result.getTrack()).isNull();
        assertThat(result.getShipments()).isNull();

        verify(pricingRepository, times(0)).enqueue(anyList());
        verify(pricingRepository, times(1)).retrievePricing();

        verify(trackingRepository, times(0)).enqueue(anyList());
        verify(trackingRepository, times(1)).retrieveTracks();

        verify(shipmentsRepository, times(0)).enqueue(anyList());
        verify(shipmentsRepository, times(1)).retrieveShipments();
    }

}
