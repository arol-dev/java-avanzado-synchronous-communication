package com.example.consumer.db;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ItemRepository {
    private final JdbcTemplate jdbc;

    public ItemRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void initSchema() {
        jdbc.execute("CREATE TABLE IF NOT EXISTS items (id IDENTITY PRIMARY KEY, name VARCHAR(100))");
    }

    public void insert(String name) {
        // TODO: Ejercicio 1 - Implementar insert usando JdbcTemplate
        // Consejo: Usa jdbc.update("INSERT INTO items(name) VALUES (?)", name);
        throw new UnsupportedOperationException("Ejercicio 1 no implementado");
    }

    public List<String> findAll() {
        // TODO: Ejercicio 1 - Implementar findAll usando JdbcTemplate
        // Consejo: Usa jdbc.query("SELECT name FROM items", (rs, i) ->
        // rs.getString(1));
        throw new UnsupportedOperationException("Ejercicio 1 no implementado");
    }
}
