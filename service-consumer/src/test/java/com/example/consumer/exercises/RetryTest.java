package com.example.consumer.exercises;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.example.consumer.client.ProviderClient;
import com.example.consumer.web.ProxyController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "db.init.enabled=false"
})
@org.springframework.context.annotation.Import(RetryTest.TestConfig.class)
class RetryTest {

    @Autowired
    private ProxyController proxyController;

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
    void shouldRetryThreeTimes() {
        // Dado: El proveedor falla
        when(providerClient.unreliable()).thenThrow(new RuntimeException("Fallo Transitorio"));

        // Cuando: Llamamos al endpoint
        try {
            proxyController.proxyRetry().join();
        } catch (Exception e) {
            // Se espera que falle después de agotar los reintentos (o inmediatamente si no
            // hay retry)
        }

        // Entonces: El cliente debería ser llamado 3 veces (1 inicial + 2 reintentos)
        verify(providerClient, times(3)).unreliable();
    }
}
