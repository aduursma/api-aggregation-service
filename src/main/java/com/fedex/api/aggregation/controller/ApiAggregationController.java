package com.fedex.api.aggregation.controller;

import com.fedex.api.aggregation.dto.AggregatedResult;
import com.fedex.api.aggregation.dto.SearchCriteria;
import com.fedex.api.aggregation.service.ApiAggregationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class ApiAggregationController {

    private final ApiAggregationService service;

    @GetMapping(path = "/aggregation")
    public ResponseEntity<AggregatedResult> getAggregatedResults(@Valid SearchCriteria searchCriteria) {
        log.info("Aggregating results with params:");
        log.info("pricing  : {}", StringUtils.join(searchCriteria.getPricing()));
        log.info("track    : {}", StringUtils.join(searchCriteria.getTrack()));
        log.info("shipments: {}", StringUtils.join(searchCriteria.getShipments()));

        AggregatedResult result = service.aggregateResults(searchCriteria);

        log.info("Done aggregating results...");

        return ResponseEntity.ok(result);
    }

}
