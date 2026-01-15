# Ejercicio 5: Patrón Retry

## 🎯 Objetivo

Manejar fallos transitorios (ej. un parpadeo en la red) reintentando la operación automáticamente antes de rendirse.

## 📝 Instrucciones

1. Abre `com.example.consumer.web.ProxyController`.
2. Localiza el método `proxyRetry`.
3. Añade la anotación `@Retry`.

### Configuración

- **Nombre**: "providerUnreliable".
- **Comportamiento**: Según `application.yml`, está configurado para reintentar 3 veces con una espera de 100ms entre intentos.

### Consideraciones

El patrón Retry es útil para operaciones idempotentes y fallos temporales. No debe usarse si el fallo es permanente (ej. error 400 Bad Request) o si la operación no es segura de repetir.

## ✅ Verificación

```bash
mvn -pl service-consumer -Dtest=RetryTest test
```

El test verificará que el cliente del proveedor es invocado múltiples veces cuando falla la primera vez.
