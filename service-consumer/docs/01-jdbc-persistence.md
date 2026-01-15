# Ejercicio 1: Persistencia JDBC

## 🎯 Objetivo

El objetivo de este ejercicio es implementar la capa de persistencia utilizando `JdbcTemplate` para interactuar con una base de datos H2. Aprenderás a realizar operaciones CRUD básicas sin la complejidad de un ORM completo como JPA/Hibernate.

## 📝 Instrucciones

1. Abre la clase `com.example.consumer.db.ItemRepository` en `service-consumer`.
2. Verás que los métodos `insert` y `findAll` lanzan `UnsupportedOperationException`.
3. Implementa estos métodos utilizando el objeto `JdbcTemplate` inyectado.

### 1. Implementar `insert`

Este método debe insertar un nuevo registro en la tabla `items`.

- **SQL**: `INSERT INTO items(name) VALUES (?)`
- **Método JdbcTemplate**: `jdbc.update(...)`

### 2. Implementar `findAll`

Este método debe devolver una lista con todos los nombres de los items.

- **SQL**: `SELECT name FROM items`
- **Método JdbcTemplate**: `jdbc.query(...)`
- **Mapeo**: Necesitarás mapear el `ResultSet` a `String`.

## ✅ Verificación

Ejecuta el test para verificar tu solución:

```bash
mvn -pl service-consumer -Dtest=ItemRepositoryTest test
```

Si el test pasa (verde), ¡has completado el ejercicio!
