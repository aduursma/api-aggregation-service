package com.fedex.api.aggregation.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@JsonPropertyOrder( {"pricing", "track", "shipments"} )
public class AggregatedResult {

    private Map<String, BigDecimal> pricing;
    private Map<String, String> track;
    private Map<String, List<String>> shipments;

}
