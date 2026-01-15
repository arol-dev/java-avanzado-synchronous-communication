package com.example.consumer.exercises;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.example.consumer.client.ProviderClient;
import com.example.consumer.web.ProxyController;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "db.init.enabled=false" // Asumiendo que añado esta propiedad a DbConfig
})
@org.springframework.context.annotation.Import(CircuitBreakerTest.TestConfig.class)
class CircuitBreakerTest {

    @Autowired
    private ProxyController proxyController;

    @Autowired
    private CircuitBreakerRegistry registry;

    @Autowired
    private ProviderClient providerClient;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        ProviderClient providerClient() {
            return Mockito.mock(ProviderClient.class);
        }
    }

    @Test
    void shouldOpenCircuitBreakerAfterFailures() {
        // Dado: El proveedor falla consistentemente
        when(providerClient.unreliable()).thenThrow(new RuntimeException("Fallo Simulado"));

        // Cuando: Hacemos suficientes llamadas para abrir el circuito
        IntStream.range(0, 10).forEach(i -> {
            try {
                proxyController.proxyCircuitBreaker().join();
            } catch (Exception e) {
                // Ignorar errores de ejecución
            }
        });

        // Entonces: El Circuit Breaker debería estar ABIERTO (OPEN)
        CircuitBreaker cb = registry.circuitBreaker("providerUnreliable");
        assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }
}
