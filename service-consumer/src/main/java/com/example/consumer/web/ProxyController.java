package com.example.consumer.web;

import com.example.consumer.client.ProviderClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProxyController {


    private final ProviderClient providerClient;

    public ProxyController(ProviderClient providerClient) {
        this.providerClient = providerClient;
    }

    @GetMapping("/proxy")
    public String proxy() {
        return providerClient.hello();
    }
}
