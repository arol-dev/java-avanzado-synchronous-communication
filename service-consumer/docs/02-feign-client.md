# Ejercicio 2: Cliente Feign Declarativo

## 🎯 Objetivo

El objetivo es conectar el microservicio `service-consumer` con el `service-provider` utilizando OpenFeign. Esto permite crear clientes HTTP declarativos, simplificando significativamente el código de comunicación entre servicios.

## 📝 Instrucciones

1. Abre la interfaz `com.example.consumer.client.ProviderClient` en `service-consumer`.
2. Añade las anotaciones necesarias para indicar que es un Cliente Feign y mapear los endpoints.

### 1. @FeignClient

Anota la interfaz con `@FeignClient`.

- **Nombre**: Debe coincidir con el nombre de la aplicación `service-provider` registrado en Eureka.
- **Valor**: `service-provider`.

### 2. @GetMapping

Anota los métodos con `@GetMapping` para que coincidan con la API del proveedor.

- `hello()` -> `/api/hello`
- `unreliable()` -> `/api/unreliable`
- `items()` -> `/api/db/items`

## ✅ Verificación

El test `ProviderClientTest` usa WireMock para simular el proveedor.

```bash
mvn -pl service-consumer -Dtest=ProviderClientTest test
```
