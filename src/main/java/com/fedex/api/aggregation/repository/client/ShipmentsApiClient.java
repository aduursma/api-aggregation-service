package com.fedex.api.aggregation.repository.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "${shipments.api.name}", url = "${shipments.api.url}", fallback = ShipmentsApiFallback.class)
public interface ShipmentsApiClient {

    @GetMapping(path = "/shipments")
    Map<String, List<String>> retrieveShipments(@RequestParam(name = "q") List<String> orderNumbers);

}
