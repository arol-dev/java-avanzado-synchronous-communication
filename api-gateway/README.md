# API Gateway (Spring Cloud Gateway)

Este módulo actúa como puerta de entrada (edge service) para exponer los
microservicios detrás de endpoints unificados y con balanceo de carga. Está
integrado con Eureka para descubrimiento dinámico de servicios y está
instrumentado con Micrometer Tracing + OpenTelemetry.

## Requisitos

- Java 21
- Maven 3.9+
- Servidor Eureka en ejecución en `http://localhost:8761`
- `service-provider` y `service-consumer` corriendo y registrados en Eureka (o
  arráncalos desde este repositorio)

## Rutas y comportamiento

Configuración principal (ver `src/main/resources/application.yml`):

- Puerto: `8082`
- Descubrimiento dinámico: `spring.cloud.gateway.discovery.locator.enabled=true`
  (con `lower-case-service-id=true`)
- Rutas estáticas definidas:
  - `Path=/provider/api/unreliable` → `lb://service-provider` con filtros:
    `StripPrefix=1` y `CircuitBreaker` (fallback a `forward:/fallback/provider-unreliable`).
  - `Path=/provider/**` → `lb://service-provider` con `StripPrefix=1`.
  - `Path=/consumer/**` → `lb://service-consumer` con `StripPrefix=1`.

Esto permite acceder a los servicios detrás del gateway con prefijos amigables y
balanceo de carga (scheme `lb://`). El filtro `StripPrefix=1` elimina el primer
segmento (`/provider` o `/consumer`) antes de reenviar la solicitud al servicio
backend. Para `/provider/api/unreliable`, además se aplica un CircuitBreaker con
un endpoint de fallback.

## Cómo ejecutar

1. Compila el proyecto desde la raíz del repositorio:
   - `mvn -q -DskipTests package`
2. Asegúrate de que `eureka-server` está corriendo.
3. Inicia el API Gateway:
   - `mvn -q -pl api-gateway -am spring-boot:run`
4. Probar:
   - `curl http://localhost:8082/provider/api/hello`
   - `curl http://localhost:8082/consumer/api/proxy`
   - `curl -i http://localhost:8082/provider/api/unreliable`  # CircuitBreaker activo; puede devolver fallback

## Ejecutar con Docker Compose (opcional)

Desde la raíz del repositorio:

- Construir imágenes: `docker compose build`
- Levantar servicios: `docker compose up`
- Probar:
  - Panel de Eureka: http://localhost:8761
  - Gateway: http://localhost:8082
  - A través del Gateway:
    - `curl http://localhost:8082/provider/api/hello`
    - `curl http://localhost:8082/consumer/api/proxy`
    - `curl -i http://localhost:8082/provider/api/unreliable`  # aplica CircuitBreaker + fallback

## Observabilidad

- Muestreo de trazas al 100% (dev):
  `management.tracing.sampling.probability=1.0`.
- Exportación OTLP a Collector local:
  `management.otlp.tracing.endpoint=http://localhost:4317` y `transport=grpc`.
- Logs con traceId/spanId.
- En Docker Compose, las trazas se envían a `otel-collector` y puedes verlas en
  Jaeger: http://localhost:16686 (servicio: `api-gateway`).

## Integración con Eureka

- Se registra como `api-gateway`.
- Descubre servicios por nombre lógico (p. ej. `service-provider`,
  `service-consumer`). Con `lower-case-service-id=true` los IDs se normalizan en
  minúscula para evitar discrepancias.

## Solución de problemas

- 404 inmediato tras iniciar: espera a que los servicios se registren en Eureka.
  El gateway arranca rápido, pero los destinos pueden no estar listos.
- Prefijos y reescritura: si obtienes 404 en el destino, verifica que el path
  final (tras `RewritePath`) exista en el servicio backend.
- Puertos ocupados: asegúrate de que el `8082` está libre o ajusta `server.port`.

## Archivo de configuración

Consulta `src/main/resources/application.yml` para ver la configuración completa
(de rutas, Eureka y tracing).