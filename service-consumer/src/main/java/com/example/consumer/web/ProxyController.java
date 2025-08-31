package com.example.consumer.web;

import com.example.consumer.client.ProviderClient;
import com.example.consumer.db.ItemRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class ProxyController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ProxyController.class);

    private final ItemRepository itemRepository;

    private final ProviderClient providerClient;

    @Autowired
    public ProxyController(ItemRepository itemRepository, ProviderClient providerClient) {
        this.itemRepository = itemRepository;
        this.providerClient = providerClient;
    }

    @GetMapping("/proxy")
    public String proxy() {
        return "proxy controller: " + providerClient.hello();
    }


    /**
     * Handles an HTTP GET request to call an unreliable provider service through the proxy.
     * The method leverages Resilience4j's Circuit Breaker, Time Limiter, and Retry mechanisms
     * to provide fault tolerance and limit the impact of failures in the underlying service.
     * If the provider service is unavailable or fails, a fallback method is invoked to handle the failure.
     *
     * @return A {@link CompletableFuture} containing either the response from the provider service
     * or a fallback response if the provider service call fails.
     */
    @GetMapping("/proxy-unreliable")
    @CircuitBreaker(name = "providerUnreliable", fallbackMethod = "unreliableFallback")
    @TimeLimiter(name = "providerUnreliable")
    @Retry(name = "providerUnreliable")
    public CompletableFuture<String> proxyUnreliable() {
        logger.info("Calling unreliable provider");
        return CompletableFuture.supplyAsync(() -> "proxy unreliable: " + providerClient.unreliable());
    }

    // Fallback must return the same type and accept Throwable as last arg
    private CompletableFuture<String> unreliableFallback(Throwable t) {
        logger.warn("Fallback called: {}", t.getClass().getSimpleName());
        return CompletableFuture.completedFuture("fallback: provider unavailable - " + t.getClass().getSimpleName());
    }

    // Local DB endpoint to exercise JDBC spans in the consumer
    @GetMapping("/db/items")
    public List<String> items() {
        List<String> providedItems = providerClient.items();
        logger.info("Remote DB returned {} items", providedItems.size());
        List<String> localItemList = itemRepository.findAll();
        logger.info("Local DB returned: {} items", localItemList.size());
        localItemList.addAll(providedItems);
        return localItemList;
    }
}
