package com.fedex.api.aggregation.repository;

import com.fedex.api.aggregation.repository.client.PricingApiClient;
import com.fedex.api.aggregation.repository.queue.ParameterQueue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PricingRepository {

    private final ParameterQueue pricingQueue;
    private final PricingApiClient pricingApiClient;

    public void enqueue(@NonNull List<String> countries) {
        for (String country : countries) {
            pricingQueue.enqueue(country);
        }
    }

    @Async
    public CompletableFuture<Map<String, BigDecimal>> retrievePricing() {
        List<String> countries = getCountries();

        if (CollectionUtils.isEmpty(countries)) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.completedFuture(pricingApiClient.retrievePricing(countries));
    }

    protected List<String> getCountries() {
        List<String> countries = pricingQueue.getParameters();
        log.info("Retrieve pricing for: {}", StringUtils.join(countries, ","));

        return countries;
    }
}
