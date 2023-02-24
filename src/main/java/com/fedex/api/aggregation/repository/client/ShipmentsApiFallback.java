package com.fedex.api.aggregation.repository.client;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ShipmentsApiFallback implements ShipmentsApiClient {

    @Override
    public Map<String, List<String>> retrieveShipments(List<String> orderNumbers) {
        return null;
    }

}
