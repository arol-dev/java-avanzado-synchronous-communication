# Comunicación síncrona con Spring Boot, Eureka y OpenFeign

Este repositorio contiene un ejemplo mínimo de comunicación entre microservicios
usando:

- Eureka Server (descubrimiento de servicios)
- 2 microservicios: `service-provider` y `service-consumer`
- OpenFeign para llamadas HTTP entre servicios usando descubrimiento por nombre
- Micrometer Tracing + OpenTelemetry para trazas distribuidas (HTTP y JDBC)
- Base de datos en memoria H2 sin ORM (JDBC puro), instrumentada con tracing

Cada módulo tiene su propio README con pasos de ejecución y detalles.

## API Gateway (Spring Cloud Gateway)

El módulo `api-gateway` actúa como puerta de entrada (edge service) para exponer
los microservicios detrás de endpoints unificados y con balanceo de carga.
Está integrado con Eureka para descubrimiento dinámico de servicios.

- Puerto: `8082`
- Descubrimiento: habilitado con `spring.cloud.gateway.discovery.locator.enabled=true`.
- Rutas definidas (application.yml):
  - `/provider/api/unreliable` → `lb://service-provider` con filtros `StripPrefix=1` y `CircuitBreaker` (fallback a `forward:/fallback/provider-unreliable`).
  - `/provider/**` → `lb://service-provider` con `StripPrefix=1`.
  - `/consumer/**` → `lb://service-consumer` con `StripPrefix=1`.

Ejemplos:
- `curl http://localhost:8082/provider/api/hello`
- `curl http://localhost:8082/consumer/api/proxy`
- `curl -i http://localhost:8082/provider/api/unreliable`  # CircuitBreaker activo; puede devolver fallback

Observabilidad:
- Hereda la configuración de Micrometer Tracing/OTLP. En Docker Compose exporta
  a `otel-collector` y puedes ver las trazas del `api-gateway` en Jaeger.

## Versiones

- Java 21
- Spring Boot 3.5.0
- Spring Cloud 2025.0.1

## Módulos

- `eureka-server`: servidor de descubrimiento en `:8761`.
- `service-provider`: expone `GET /api/hello` en `:8081` y se registra en
  Eureka. Incluye H2 + JDBC con endpoint `/api/db/items`.
- `service-consumer`: expone `GET /api/proxy` en `:8080` y llama al provider vía
  Feign. Incluye H2 + JDBC con endpoint `/api/db/items` local. También añade un
  ejemplo de resiliencia con CircuitBreaker/Retry/TimeLimiter en
  `/api/proxy-unreliable`.
- `api-gateway` (Spring Cloud Gateway): puerta de entrada en `:8082` que enruta
  tráfico a servicios registrados en Eureka. Permite acceder a los servicios con
  paths amigables y balanceo de carga vía `lb://`.

## Observabilidad y Trazas

Se han añadido dependencias de Micrometer Tracing y OpenTelemetry (OTLP). Por
defecto:

- Se genera muestreo al 100% (configurable).
- Se exportan trazas vía OTLP gRPC a `http://localhost:4317` si hay un
  Collector.
- Se incluyen `traceId` y `spanId` en los logs.

Endpoints instrumentados:

- HTTP server/client: controladores y llamadas Feign.
- JDBC: consultas realizadas por `JdbcTemplate` contra H2.

### Ejecutar un OpenTelemetry Collector + Jaeger (opcional)

Puedes usar Docker para ver las trazas en Jaeger:

Docker Compose (ejemplo mínimo):

```yaml
version: "3"
services:
  otel-collector:
    image: otel/opentelemetry-collector:0.104.0
    command: ["--config=/etc/otelcol-config.yaml"]
    volumes:
      - ./otelcol.yaml:/etc/otelcol-config.yaml
    ports:
      - "4317:4317"       # OTLP gRPC
      - "4318:4318"       # OTLP HTTP (opcional)
  jaeger:
    image: jaegertracing/all-in-one:1.57
    ports:
      - "16686:16686"     # Jaeger UI
```

Archivo `otelcol.yaml`:

```yaml
receivers:
  otlp:
    protocols:
      grpc:
      http:
exporters:
  otlp:
    endpoint: jaeger:4317
    tls:
      insecure: true
service:
  pipelines:
    traces:
      receivers: [otlp]
      exporters: [otlp]
```

Abre Jaeger en http://localhost:16686 y busca por los servicios
`service-provider`, `service-consumer` y `eureka-server`.

## Cómo ejecutar rápidamente

1. Terminal 1 – arrancar Eureka:
    - `mvn -q -pl eureka-server -am spring-boot:run`
2. Terminal 2 – arrancar provider:
    - `mvn -q -pl service-provider -am spring-boot:run`
3. Terminal 3 – arrancar consumer:
    - `mvn -q -pl service-consumer -am spring-boot:run`
4. Probar:
    - `curl http://localhost:8081/api/hello`
    - `curl http://localhost:8080/api/proxy`
   - `curl http://localhost:8081/api/db/items` (JDBC + H2 en provider)
   - `curl http://localhost:8080/api/db/items` (JDBC + H2 en consumer)
   - `curl http://localhost:8080/api/proxy-unreliable` (resiliencia)

Para más detalles y opciones de configuración, revisa los README de cada módulo.

## Ejecutar con Docker Compose

Se incluye un archivo `docker-compose.yml` que levanta:

- Eureka, Provider, Consumer y API Gateway
- Un OpenTelemetry Collector (OTLP gRPC/HTTP)
- Jaeger (UI para ver trazas)

y configura los servicios para exportar trazas al Collector dentro de la red de
Docker.

Comandos:

- Construir imágenes: `docker compose build`
- Levantar servicios: `docker compose up`
- Probar:
    - Panel de Eureka http://localhost:8761
    - `curl http://localhost:8081/api/hello`
    - `curl http://localhost:8080/api/proxy`
    - Gateway en http://localhost:8082
    - A través del Gateway:
        - `curl http://localhost:8082/provider/api/hello`
        - `curl http://localhost:8082/consumer/api/proxy`
        - `curl -i http://localhost:8082/provider/api/unreliable`  # aplica CircuitBreaker + fallback
    - Abre Jaeger UI: http://localhost:16686 (busca servicios: service-provider,
      service-consumer, eureka-server, api-gateway)

Notas:

- Los clientes usan la variable de entorno
  `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:8761/eureka` para
  descubrir Eureka dentro de la red de Docker.
- Las apps están configuradas con
  `MANAGEMENT_OTLP_TRACING_ENDPOINT=http://otel-collector:4317` para exportar
  trazas al Collector del Compose.
- `depends_on` asegura el orden de arranque (Collector/Jaeger → Eureka →
  Provider → Consumer → API Gateway). Aun así, los clientes reintentan el
  registro hasta que Eureka esté disponible.
