package com.example.consumer.exercises;

import static org.assertj.core.api.Assertions.assertThat;
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
@org.springframework.context.annotation.Import(TimeLimiterTest.TestConfig.class)
class TimeLimiterTest {

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
    void shouldTimeoutAndReturnFallback() {
        // Dado que: El proveedor tarda más que el tiempo límite (500ms)
        when(providerClient.unreliable()).thenAnswer(invocation -> {
            Thread.sleep(1000);
            return "Respuesta Retrasada";
        });

        // Cuando: Llamamos al endpoint
        String result = "";
        try {
            result = proxyController.proxyTimeLimiter().join();
        } catch (Exception e) {
            // Si TimeLimiter lanza TimeoutException directamente sin fallback, la
            // capturamos aquí.
            // Si el controlador no tiene fallback, podría devolver "Respuesta Retrasada"
            // después de 1s.
            // O lanzar ExecutionException envolviendo TimeoutException si está anotado.
        }

        // Entonces: Deberíamos obtener la respuesta de fallback debido al timeout
        // Pero como falta la anotación, probablemente tendrá éxito con "Respuesta
        // Retrasada"
        // Así que el test falla (lo cual es correcto).
        // Pero si queremos asegurar que funciona cuando se resuelva:
        assertThat(result).contains("fallback");
        assertThat(result).contains("TimeoutException");
    }
}
