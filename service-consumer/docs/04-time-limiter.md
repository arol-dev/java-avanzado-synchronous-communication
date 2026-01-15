# Ejercicio 4: Patrón Time Limiter

## 🎯 Objetivo

Evitar que tu servicio se quede bloqueado esperando indefinidamente a un servicio lento. Implementarás un límite de tiempo (Timeout) para las llamadas.

## 📝 Instrucciones

1. Abre `com.example.consumer.web.ProxyController`.
2. Localiza el método `proxyTimeLimiter`.
3. Añade la anotación `@TimeLimiter`.

### Configuración

- **Nombre**: "providerUnreliable".
- **Fallback**: Similar al Circuit Breaker, es recomendable tener un método de fallback para manejar la `TimeoutException`. Nota: `@TimeLimiter` suele usarse junto con `@CircuitBreaker` o devolviendo un `CompletableFuture`/`Mono`/`Flux`.

### Detalles

En este ejercicio, el método devuelve un `CompletableFuture<String>`. Resilience4j gestionará la ejecución asíncrona y cancelará la tarea si excede el tiempo configurado (500ms en `application.yml`).

## ✅ Verificación

```bash
mvn -pl service-consumer -Dtest=TimeLimiterTest test
```

El test fuerza un retardo de 1 segundo en el proveedor mockeado, lo que debería disparar el Time Limiter.
