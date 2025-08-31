# Service Consumer

Este microservicio consume al `service-provider` usando OpenFeign y se registra
en Eureka. También incluye una BD H2 en memoria accesible vía JDBC y está
instrumentado con Micrometer Tracing + OpenTelemetry (HTTP y JDBC).

## Requisitos

- Java 21
- Maven 3.9+
- Servidor Eureka en ejecución en `http://localhost:8761`
- `service-provider` corriendo y registrado en Eureka

## Endpoints

- `GET /api/proxy` → Llama a `service-provider` (`/api/hello`) vía Feign y
  devuelve su respuesta.
- `GET /api/proxy-unreliable` → Llama a `/api/unreliable` con
  CircuitBreaker/Retry/TimeLimiter.
- `GET /api/db/items` → Lee elementos desde H2 (JDBC) y genera spans de JDBC.

## Cómo ejecutar

1. Compila el proyecto desde la raíz:
    - `mvn -q -DskipTests package`
2. Asegúrate de que `eureka-server` y `service-provider` están corriendo.
3. Ejecuta este servicio:
    - `mvn -q -pl service-consumer -am spring-boot:run`
4. Probar:
    - `curl http://localhost:8080/api/proxy`
   - `curl http://localhost:8080/api/db/items`
   - `curl http://localhost:8080/api/proxy-unreliable`

## Observabilidad

- Muestreo de trazas al 100% (dev):
  `management.tracing.sampling.probability=1.0`.
- Exportación OTLP a Collector local:
  `management.otlp.tracing.endpoint=http://localhost:4317`.
- Logs con traceId/spanId.
- H2 console (dev): `http://localhost:8080/h2-console`.

## Configuración

- Puerto: `8080`
- Nombre de la app: `service-consumer`
- Registro en Eureka:
  `eureka.client.service-url.defaultZone=http://localhost:8761/eureka`
- Feign auto-descubre `service-provider` por su nombre en Eureka.
- Datasource H2 en memoria: ver `src/main/resources/application.yml`.
