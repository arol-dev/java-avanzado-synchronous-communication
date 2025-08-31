package com.example.provider.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

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
        jdbc.update("INSERT INTO items(name) VALUES (?)", name);
    }

    public List<String> findAll() {
        return jdbc.query("SELECT name FROM items", (rs, i) -> rs.getString(1));
    }
}
