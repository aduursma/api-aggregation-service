package com.fedex.api.aggregation.repository.client;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class TrackingApiFallback implements TrackingApiClient {

    @Override
    public Map<String, String> retrieveTracks(List<String> orderNumbers) {
        return null;
    }

}
