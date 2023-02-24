package com.fedex.api.aggregation;

import com.fedex.api.aggregation.dto.AggregatedResult;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregatedResultBuilder {

    private Map<String, BigDecimal> pricing = new HashMap<>();
    private Map<String, String> track = new HashMap<>();
    private Map<String, List<String>> shipments = new HashMap<>();

    public AggregatedResultBuilder() {
    }

    public AggregatedResultBuilder pricing(Map<String, BigDecimal> pricing) {
        this.pricing = pricing;
        return this;
    }

    public AggregatedResultBuilder track(Map<String, String> track) {
        this.track = track;
        return this;
    }

    public AggregatedResultBuilder shipments(Map<String, List<String>> shipments) {
        this.shipments = shipments;
        return this;
    }

    public AggregatedResult build() {
        AggregatedResult aggregatedResult = new AggregatedResult();
        aggregatedResult.setPricing(pricing);
        aggregatedResult.setTrack(track);
        aggregatedResult.setShipments(shipments);

        return aggregatedResult;
    }

    public static AggregatedResult validResult() {
        Map<String, BigDecimal> pricing = new HashMap<>();
        pricing.put("NL", new BigDecimal("14.242090605778"));
        pricing.put("CN", new BigDecimal("20.503467806384"));

        Map<String, String> track = new HashMap<>();
        track.put("109347263", null);
        track.put("123456891", "COLLECTING");

        Map<String, List<String>> shipments = new HashMap<>();
        shipments.put("109347263", List.of("box", "box", "pallet"));
        shipments.put("123456891", null);

        return new AggregatedResultBuilder()
            .pricing(pricing)
            .track(track)
            .shipments(shipments)
            .build();
    }

}
