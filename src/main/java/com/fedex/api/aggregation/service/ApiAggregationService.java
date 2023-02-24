package com.fedex.api.aggregation.service;

import com.fedex.api.aggregation.dto.AggregatedResult;
import com.fedex.api.aggregation.dto.SearchCriteria;
import com.fedex.api.aggregation.repository.PricingRepository;
import com.fedex.api.aggregation.repository.ShipmentsRepository;
import com.fedex.api.aggregation.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class ApiAggregationService {

    private final PricingRepository pricingRepository;
    private final TrackingRepository trackingRepository;
    private final ShipmentsRepository shipmentsRepository;

    public AggregatedResult aggregateResults(@Valid SearchCriteria searchCriteria) {
        enqueueParameters(searchCriteria);
        return getAggregatedResults();
    }

    protected void enqueueParameters(SearchCriteria searchCriteria) {
        enqueuePricingParameters(searchCriteria.getPricing());
        enqueueTrackParameters(searchCriteria.getTrack());
        enqueueShipmentsParameters(searchCriteria.getShipments());
    }

    protected void enqueuePricingParameters(List<String> countries) {
        if (CollectionUtils.isEmpty(countries)) {
            return;
        }
        pricingRepository.enqueue(countries);
    }

    protected void enqueueTrackParameters(List<String> orderNumbers) {
        if (CollectionUtils.isEmpty(orderNumbers)) {
            return;
        }
        trackingRepository.enqueue(orderNumbers);
    }

    protected void enqueueShipmentsParameters(List<String> orderNumbers) {
        if (CollectionUtils.isEmpty(orderNumbers)) {
            return;
        }
        shipmentsRepository.enqueue(orderNumbers);
    }

    @SneakyThrows
    protected AggregatedResult getAggregatedResults() {
        CompletableFuture<Map<String, BigDecimal>> pricingResults = pricingRepository.retrievePricing();
        CompletableFuture<Map<String, String>> trackResults = trackingRepository.retrieveTracks();
        CompletableFuture<Map<String, List<String>>> shipmentsResults = shipmentsRepository.retrieveShipments();

        CompletableFuture.allOf(pricingResults, trackResults, shipmentsResults).join();
        return createAggregatedResult(pricingResults.get(), trackResults.get(), shipmentsResults.get());
    }

    protected AggregatedResult createAggregatedResult(Map<String, BigDecimal> pricing, Map<String, String> track, Map<String, List<String>> shipments) {
        AggregatedResult result = new AggregatedResult();
        result.setPricing(pricing);
        result.setTrack(track);
        result.setShipments(shipments);

        return result;
    }

}
