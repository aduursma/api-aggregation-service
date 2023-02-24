package com.fedex.api.aggregation.repository;

import com.fedex.api.aggregation.repository.client.PricingApiClient;
import com.fedex.api.aggregation.repository.queue.ParameterQueue;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;

@SpringBootTest(webEnvironment = MOCK)
class PricingRepositoryTest {

    @Autowired
    PricingRepository repository;

    @MockBean
    @Qualifier("pricingQueue")
    ParameterQueue pricingQueue;

    @MockBean
    PricingApiClient pricingApiClient;

    @Test
    void verifyEnqueueWithCountriesParameter() {
        repository.enqueue(List.of("NL", "CN"));

        verify(pricingQueue, timeout(500).times(2)).enqueue(any(String.class));
    }

    @Test
    void verifyEnqueueWithNullCountriesParameterThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> repository.enqueue(null));

        verify(pricingQueue, never()).enqueue(any(String.class));
    }

    @Test
    void verifyEnqueueWithEmptyCountriesParameter() {
        repository.enqueue(Lists.newArrayList());

        verify(pricingQueue, never()).enqueue(any(String.class));
    }

    @Test
    void verifyRetrievePricingWithNullCountriesParameter() throws Exception {
        when(pricingQueue.getParameters()).thenReturn(null);

        CompletableFuture<Map<String, BigDecimal>> result = repository.retrievePricing();

        assertThat(result.get())
            .isNull();

        verify(pricingQueue, never()).enqueue(any(String.class));
    }

    @Test
    void verifyRetrievePricingWithEmptyCountriesParameter() throws Exception {
        when(pricingQueue.getParameters()).thenReturn(Lists.newArrayList());

        CompletableFuture<Map<String, BigDecimal>> result = repository.retrievePricing();

        assertThat(result.get())
            .isNull();

        verify(pricingQueue, never()).enqueue(any(String.class));
    }

    @Test
    void verifyRetrievePricingWithCountriesParameter() throws Exception {
        Map<String, BigDecimal> pricing = new HashMap<>();
        pricing.put("NL", new BigDecimal("14.242090605778"));
        pricing.put("CN", new BigDecimal("20.503467806384"));

        when(pricingQueue.getParameters()).thenReturn(Lists.newArrayList("NL", "CN"));
        when(pricingApiClient.retrievePricing(anyList())).thenReturn(pricing);

        CompletableFuture<Map<String, BigDecimal>> result = repository.retrievePricing();

        assertThat(result.get())
            .isNotEmpty()
            .hasSize(2)
            .contains(entry("NL", new BigDecimal("14.242090605778")), entry("CN", new BigDecimal("20.503467806384")));

        verify(pricingQueue, times(1)).getParameters();
        verify(pricingApiClient, times(1)).retrievePricing(anyList());
    }

    @Test
    void verifyRetrievePricingWithCountriesParameterAndClientThrowsException() {
        when(pricingQueue.getParameters()).thenReturn(Lists.newArrayList("NL", "CN"));
        when(pricingApiClient.retrievePricing(anyList())).thenThrow(new RuntimeException());

        Assertions.assertThrows(Exception.class, () -> {
            CompletableFuture<Map<String, BigDecimal>> result = repository.retrievePricing();

            assertThat(result.get())
                .isNull();
        });

        verify(pricingQueue, times(1)).getParameters();
        verify(pricingApiClient, times(1)).retrievePricing(anyList());
    }

}
