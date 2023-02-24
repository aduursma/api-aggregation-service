package com.fedex.api.aggregation.config;

import com.fedex.api.aggregation.repository.queue.ParameterQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfig {

    @Value("${parameter.queue.capacity:5}")
    private int capacity;

    @Value("${parameter.queue.timeout:5}")
    private int timeout;


    @Bean
    public ParameterQueue pricingQueue() {
        return new ParameterQueue(capacity, timeout);
    }

    @Bean
    public ParameterQueue trackingQueue() {
        return new ParameterQueue(capacity, timeout);
    }

    @Bean
    public ParameterQueue shipmentsQueue() {
        return new ParameterQueue(capacity, timeout);
    }

}
