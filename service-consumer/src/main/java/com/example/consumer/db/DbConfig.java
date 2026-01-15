package com.example.consumer.db;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbConfig {
    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name = "db.init.enabled", havingValue = "true", matchIfMissing = true)
    CommandLineRunner init(ItemRepository repo) {
        return args -> {
            repo.initSchema();
            repo.insert("GAMMA");
            repo.insert("DELTA");
        };
    }
}
