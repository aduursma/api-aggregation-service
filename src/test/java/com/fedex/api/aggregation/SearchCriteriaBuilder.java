package com.fedex.api.aggregation;

import com.fedex.api.aggregation.dto.SearchCriteria;

import java.util.ArrayList;
import java.util.List;

public class SearchCriteriaBuilder {

    private List<String> pricing = new ArrayList<>();
    private List<String> track = new ArrayList<>();
    private List<String> shipments = new ArrayList<>();

    public SearchCriteriaBuilder() {
    }

    public SearchCriteriaBuilder pricing(List<String> pricing) {
        this.pricing = pricing;
        return this;
    }

    public SearchCriteriaBuilder track(List<String> track) {
        this.track = track;
        return this;
    }

    public SearchCriteriaBuilder shipments(List<String> shipments) {
        this.shipments = shipments;
        return this;
    }

    public SearchCriteria build() {
        SearchCriteria searchCriteria = new SearchCriteria();
        searchCriteria.setPricing(pricing);
        searchCriteria.setTrack(track);
        searchCriteria.setShipments(shipments);

        return searchCriteria;
    }

    public static SearchCriteria validCriteria() {
        return new SearchCriteriaBuilder()
            .pricing(List.of("NL", "CN"))
            .track(List.of("109347263", "123456891"))
            .shipments(List.of("109347263", "123456891"))
            .build();
    }

}
