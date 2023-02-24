package com.fedex.api.aggregation.repository.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(value = "${pricing.api.name}", url = "${pricing.api.url}", fallback = PricingApiFallback.class)
public interface PricingApiClient {

    @GetMapping(path = "/pricing")
    Map<String, BigDecimal> retrievePricing(@RequestParam(name = "q") List<String> countries);

}
