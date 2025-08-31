package com.example.consumer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "service-provider")
public interface ProviderClient {

    @GetMapping("/api/hello")
    String hello();

    @GetMapping("/api/unreliable")
    String unreliable();

    @GetMapping("/api/db/items")
    List<String> items();
}
