package com.fedex.api.aggregation.repository.client;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class PricingApiFallback implements PricingApiClient {

    @Override
    public Map<String, BigDecimal> retrievePricing(List<String> countries) {
        return null;
    }

}
