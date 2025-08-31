package com.example.provider.db;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbConfig {
    @Bean
    CommandLineRunner init(ItemRepository repo) {
        return args -> {
            repo.initSchema();
            repo.insert("alpha");
            repo.insert("beta");
        };
    }
}
