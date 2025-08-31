package com.example.consumer.web;

import com.example.consumer.client.ProviderClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class ProxyController {


    private final ProviderClient providerClient;

    public ProxyController(ProviderClient providerClient) {
        this.providerClient = providerClient;
    }

    @GetMapping("/proxy")
    public String proxy() {
        return "proxy controller: " + providerClient.hello();
    }

    // New endpoint calling the unreliable provider method with CircuitBreaker + TimeLimiter + Retry
    @GetMapping("/proxy-unreliable")
    @CircuitBreaker(name = "providerUnreliable", fallbackMethod = "unreliableFallback")
    @TimeLimiter(name = "providerUnreliable")
    @Retry(name = "providerUnreliable")
    public CompletableFuture<String> proxyUnreliable() {
        return CompletableFuture.supplyAsync(() -> "proxy unreliable: " + providerClient.unreliable());
    }

    // Fallback must return the same type and accept Throwable as last arg
    private CompletableFuture<String> unreliableFallback(Throwable t) {
        return CompletableFuture.completedFuture("fallback: provider unavailable - " + t.getClass().getSimpleName());
    }
}
