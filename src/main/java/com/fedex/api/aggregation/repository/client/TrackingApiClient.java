package com.fedex.api.aggregation.repository.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "${tracking.api.name}", url = "${tracking.api.url}", fallback = TrackingApiFallback.class)
public interface TrackingApiClient {

    @GetMapping(path = "/track")
    Map<String, String> retrieveTracks(@RequestParam(name = "q") List<String> orderNumbers);

}
