# Ejercicio 3: Patrón Circuit Breaker

## 🎯 Objetivo

Aprender a proteger tu servicio de fallos en cascada cuando un servicio dependiente (el proveedor) falla repetidamente. Utilizarás Resilience4j para implementar el patrón Circuit Breaker.

## 📝 Instrucciones

1. Abre `com.example.consumer.web.ProxyController`.
2. Localiza el método `proxyCircuitBreaker`.
3. Descomenta (o añade) la anotación `@CircuitBreaker`.

### Configuración

- **Nombre**: "providerUnreliable" (debe coincidir con la configuración en `application.yml`).
- **Fallback**: Define un método de fallback (ej. `unreliableFallback`) para manejar la respuesta cuando el circuito esté ABIERTO.

### Implementar Fallback

Crea un método privado en el controlador que coincida con la firma del método original pero aceptando una `Throwable` (o `Exception`) como último argumento.

- Debe devolver un valor por defecto o un mensaje indicando que el servicio no está disponible.

## ✅ Verificación

```bash
mvn -pl service-consumer -Dtest=CircuitBreakerTest test
```

Este test simula fallos continuos y verifica que el Circuit Breaker cambia su estado a ABIERTO.
