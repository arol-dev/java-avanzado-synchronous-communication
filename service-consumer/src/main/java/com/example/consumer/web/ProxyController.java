package com.example.consumer.web;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.consumer.client.ProviderClient;
import com.example.consumer.db.ItemRepository;

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
     * Maneja una solicitud HTTP GET para llamar a un proveedor de servicios poco
     * fiable a través de
     * el proxy.
     * El método aprovecha los mecanismos de Circuit Breaker, Time Limiter y Retry
     * de Resilience4j
     * para proporcionar tolerancia a fallos y limitar el impacto de los fallos en
     * el servicio
     * subyacente.
     * Si el proveedor de servicios no está disponible o falla, se invoca un método
     * de resguardo (fallback)
     * para manejar el fallo.
     *
     * @return Un {@link CompletableFuture} que contiene la respuesta del
     *         proveedor de servicios o una respuesta de resguardo si la llamada
     *         falla.
     */
    /**
     * Ejercicio 3: Circuit Breaker
     */
    @GetMapping("/proxy-circuit-breaker")
    // TODO: Añadir @CircuitBreaker(name = "providerUnreliable", fallbackMethod =
    // "unreliableFallback")
    public CompletableFuture<String> proxyCircuitBreaker() {
        logger.info("Calling unreliable provider (Circuit Breaker)");
        return CompletableFuture.supplyAsync(() -> "proxy circuit breaker: " + providerClient.unreliable());
    }

    /**
     * Ejercicio 4: Time Limiter
     */
    @GetMapping("/proxy-time-limiter")
    // TODO: Añadir @TimeLimiter(name = "providerUnreliable")
    // TODO: Añadir @CircuitBreaker(name = "providerUnreliable", fallbackMethod =
    // "unreliableFallback") para manejar la excepción de tiempo de espera como
    // fallback o simplemente dejar que falle?
    // Usualmente TimeLimiter se usa con CircuitBreaker o devolviendo un Future.
    public CompletableFuture<String> proxyTimeLimiter() {
        logger.info("Calling unreliable provider (Time Limiter)");
        return CompletableFuture.supplyAsync(() -> "proxy time limiter: " + providerClient.unreliable());
    }

    /**
     * Ejercicio 5: Retry
     */
    @GetMapping("/proxy-retry")
    // TODO: Añadir @Retry(name = "providerUnreliable")
    public CompletableFuture<String> proxyRetry() {
        logger.info("Calling unreliable provider (Retry)");
        return CompletableFuture.supplyAsync(() -> "proxy retry: " + providerClient.unreliable());
    }

    // El Fallback debe devolver el mismo tipo y aceptar Throwable como último
    // argumento
    private CompletableFuture<String> unreliableFallback(Throwable t) {
        logger.warn("Fallback called: {}", t.getClass().getSimpleName());
        return CompletableFuture.completedFuture("fallback: provider unavailable - " + t.getClass().getSimpleName());
    }

    // Endpoint de BD local para ejercitar los spans de JDBC en el consumidor
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
