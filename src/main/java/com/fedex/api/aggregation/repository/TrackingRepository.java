package com.fedex.api.aggregation.repository;

import com.fedex.api.aggregation.repository.client.TrackingApiClient;
import com.fedex.api.aggregation.repository.queue.ParameterQueue;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingRepository {

    private final ParameterQueue trackingQueue;
    private final TrackingApiClient trackingApiClient;

    public void enqueue(@NonNull List<String> orderNumbers) {
        for (String orderNumber : orderNumbers) {
            trackingQueue.enqueue(orderNumber);
        }
    }

    @Async
    public CompletableFuture<Map<String, String>> retrieveTracks() {
        List<String> orderNumbers = getOrderNumbers();

        if (CollectionUtils.isEmpty(orderNumbers)) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.completedFuture(trackingApiClient.retrieveTracks(orderNumbers));
    }

    protected List<String> getOrderNumbers() {
        List<String> orderNumbers = trackingQueue.getParameters();
        log.info("Retrieve tracks for: {}", StringUtils.join(orderNumbers, ","));

        return orderNumbers;
    }

}
