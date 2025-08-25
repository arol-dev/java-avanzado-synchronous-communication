# Service Provider

Este microservicio expone un endpoint HTTP simple y se registra en Eureka.

## Requisitos

- Java 21
- Maven 3.9+
- Servidor Eureka en ejecución en `http://localhost:8761`

## Endpoints

- `GET /api/hello` → Devuelve un saludo indicando el nombre de la aplicación.

## Cómo ejecutar

1. Compila el proyecto desde la raíz:
    - `mvn -q -DskipTests package`
2. Asegúrate de que `eureka-server` está corriendo.
3. Ejecuta este servicio:
    - `mvn -q -pl service-provider -am spring-boot:run`
4. Probar:
    - `curl http://localhost:8081/api/hello`

## Configuración

- Puerto: `8081`
- Nombre de la app: `service-provider`
- Registro en Eureka:
  `eureka.client.service-url.defaultZone=http://localhost:8761/eureka`
