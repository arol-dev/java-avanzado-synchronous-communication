package com.example.consumer.exercises;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.example.consumer.db.ItemRepository;
import com.example.consumer.web.ProxyController;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "db.init.enabled=false"
})
@org.springframework.context.annotation.Import(ItemRepositoryTest.TestConfig.class)
class ItemRepositoryTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        ProxyController proxyController() {
            return Mockito.mock(ProxyController.class);
        }
    }

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ProxyController proxyController;

    @Test
    void shouldPersistAndRetrieveItems() {
        // Asegurar que la tabla existe
        itemRepository.initSchema();

        itemRepository.insert("TestItem");

        List<String> items = itemRepository.findAll();

        assertThat(items).hasSize(1);
        assertThat(items.get(0)).isEqualTo("TestItem");
    }
}
