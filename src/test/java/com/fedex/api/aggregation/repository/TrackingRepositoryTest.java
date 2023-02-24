package com.fedex.api.aggregation.repository;

import com.fedex.api.aggregation.repository.client.TrackingApiClient;
import com.fedex.api.aggregation.repository.queue.ParameterQueue;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
class TrackingRepositoryTest {

    @Autowired
    TrackingRepository repository;

    @MockBean
    @Qualifier("trackingQueue")
    ParameterQueue trackingQueue;

    @MockBean
    TrackingApiClient trackingApiClient;

    @Test
    void verifyEnqueueWithOrderNumbersParameter() {
        repository.enqueue(List.of("109347263", "123456891"));

        verify(trackingQueue, timeout(500).times(2)).enqueue(any(String.class));
    }

    @Test
    void verifyEnqueueWithNullOrderNumbersParameterThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> repository.enqueue(null));

        verify(trackingQueue, never()).enqueue(any(String.class));
    }

    @Test
    void verifyEnqueueWithEmptyOrderNumbersParameter() {
        repository.enqueue(Lists.newArrayList());

        verify(trackingQueue, never()).enqueue(any(String.class));
    }

    @Test
    void verifyRetrieveTracksWithNullOrderNumbersParameter() throws Exception {
        when(trackingQueue.getParameters()).thenReturn(null);

        CompletableFuture<Map<String, String>> result = repository.retrieveTracks();

        assertThat(result.get())
            .isNull();

        verify(trackingQueue, never()).enqueue(any(String.class));
    }

    @Test
    void verifyRetrieveTracksWithEmptyOrderNumbersParameter() throws Exception {
        when(trackingQueue.getParameters()).thenReturn(Lists.newArrayList());

        CompletableFuture<Map<String, String>> result = repository.retrieveTracks();

        assertThat(result.get())
            .isNull();

        verify(trackingQueue, never()).enqueue(any(String.class));
    }

    @Test
    void verifyRetrieveTracksWithOrderNumbersParameter() throws Exception {
        Map<String, String> tracks = new HashMap<>();
        tracks.put("109347263", null);
        tracks.put("123456891", "COLLECTING");

        when(trackingQueue.getParameters()).thenReturn(Lists.newArrayList("109347263", "123456891"));
        when(trackingApiClient.retrieveTracks(anyList())).thenReturn(tracks);

        CompletableFuture<Map<String, String>> result = repository.retrieveTracks();

        assertThat(result.get())
            .isNotEmpty()
            .hasSize(2)
            .contains(entry("109347263", null), entry("123456891", "COLLECTING"));

        verify(trackingQueue, times(1)).getParameters();
        verify(trackingApiClient, times(1)).retrieveTracks(anyList());
    }

    @Test
    void verifyRetrievePricingWithCountriesParameterAndClientThrowsException() {
        when(trackingQueue.getParameters()).thenReturn(Lists.newArrayList("109347263", "123456891"));
        when(trackingApiClient.retrieveTracks(anyList())).thenThrow(new RuntimeException());

        Assertions.assertThrows(Exception.class, () -> {
            CompletableFuture<Map<String, String>> result = repository.retrieveTracks();

            assertThat(result.get())
                .isNull();
        });

        verify(trackingQueue, times(1)).getParameters();
        verify(trackingApiClient, times(1)).retrieveTracks(anyList());
    }

}
