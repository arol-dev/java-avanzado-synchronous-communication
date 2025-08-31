# Service Provider

Este microservicio expone endpoints HTTP, accede a una base de datos en memoria
H2 vía JDBC y se registra en Eureka. Las llamadas HTTP y las consultas JDBC
están instrumentadas con Micrometer Tracing + OpenTelemetry.

## Requisitos

- Java 21
- Maven 3.9+
- Servidor Eureka en ejecución en `http://localhost:8761`

## Endpoints

- `GET /api/hello` → Devuelve un saludo indicando el nombre de la aplicación.
- `GET /api/unreliable` → Falla aleatoriamente (70%) para pruebas de
  resiliencia.
- `GET /api/db/items` → Devuelve elementos desde H2 (JDBC) y genera spans de
  JDBC.

## Cómo ejecutar

1. Compila el proyecto desde la raíz:
    - `mvn -q -DskipTests package`
2. Asegúrate de que `eureka-server` está corriendo.
3. Ejecuta este servicio:
    - `mvn -q -pl service-provider -am spring-boot:run`
4. Probar:
    - `curl http://localhost:8081/api/hello`
   - `curl http://localhost:8081/api/db/items`

## Observabilidad

- Muestreo de trazas al 100% (dev):
  `management.tracing.sampling.probability=1.0`.
- Exportación OTLP a Collector local:
  `management.otlp.tracing.endpoint=http://localhost:4317`.
- Logs con traceId/spanId.
- H2 console (dev): `http://localhost:8081/h2-console`.

## Configuración

- Puerto: `8081`
- Nombre de la app: `service-provider`
- Registro en Eureka:
  `eureka.client.service-url.defaultZone=http://localhost:8761/eureka`
- Datasource H2 en memoria: ver `src/main/resources/application.yml`.
