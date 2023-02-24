package com.fedex.api.aggregation.repository;

import com.fedex.api.aggregation.repository.client.ShipmentsApiClient;
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
class ShipmentsRepositoryTest {

    @Autowired
    ShipmentsRepository repository;

    @MockBean
    @Qualifier("shipmentsQueue")
    ParameterQueue shipmentsQueue;

    @MockBean
    ShipmentsApiClient shipmentsApiClient;

    @Test
    void verifyEnqueueWithOrderNumbersParameter() {
        repository.enqueue(List.of("109347263", "123456891"));

        verify(shipmentsQueue, timeout(500).times(2)).enqueue(any(String.class));
    }

    @Test
    void verifyEnqueueWithNullOrderNumbersParameterThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> repository.enqueue(null));

        verify(shipmentsQueue, never()).enqueue(any(String.class));
    }

    @Test
    void verifyEnqueueWithEmptyOrderNumbersParameter() {
        repository.enqueue(Lists.newArrayList());

        verify(shipmentsQueue, never()).enqueue(any(String.class));
    }

    @Test
    void verifyRetrieveShipmentsWithNullOrderNumbersParameter() throws Exception {
        when(shipmentsQueue.getParameters()).thenReturn(null);

        CompletableFuture<Map<String, List<String>>> result = repository.retrieveShipments();

        assertThat(result.get())
            .isNull();

        verify(shipmentsQueue, never()).enqueue(any(String.class));
    }

    @Test
    void verifyRetrieveShipmentsWithEmptyOrderNumbersParameter() throws Exception {
        when(shipmentsQueue.getParameters()).thenReturn(Lists.newArrayList());

        CompletableFuture<Map<String, List<String>>> result = repository.retrieveShipments();

        assertThat(result.get())
            .isNull();

        verify(shipmentsQueue, never()).enqueue(any(String.class));
    }

    @Test
    void verifyRetrieveShipmentsWithOrderNumbersParameter() throws Exception {
        Map<String, List<String>> shipments = new HashMap<>();
        shipments.put("109347263", List.of("box", "box", "pallet"));
        shipments.put("123456891", null);

        when(shipmentsQueue.getParameters()).thenReturn(Lists.newArrayList("109347263", "123456891"));
        when(shipmentsApiClient.retrieveShipments(anyList())).thenReturn(shipments);

        CompletableFuture<Map<String, List<String>>> result = repository.retrieveShipments();

        assertThat(result.get())
            .isNotEmpty()
            .hasSize(2)
            .contains(entry("109347263", List.of("box", "box", "pallet")), entry("123456891", null));

        verify(shipmentsQueue, times(1)).getParameters();
        verify(shipmentsApiClient, times(1)).retrieveShipments(anyList());
    }

    @Test
    void verifyRetrieveShipmentsWithCountriesParameterAndClientThrowsException() {
        when(shipmentsQueue.getParameters()).thenReturn(Lists.newArrayList("109347263", "123456891"));
        when(shipmentsApiClient.retrieveShipments(anyList())).thenThrow(new RuntimeException());

        Assertions.assertThrows(Exception.class, () -> {
            CompletableFuture<Map<String, List<String>>> result = repository.retrieveShipments();

            assertThat(result.get())
                .isNull();
        });

        verify(shipmentsQueue, times(1)).getParameters();
        verify(shipmentsApiClient, times(1)).retrieveShipments(anyList());
    }

}
