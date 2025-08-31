# Eureka Server

Este módulo contiene el servidor de descubrimiento (Eureka) para registrar y
descubrir microservicios. También incluye Micrometer Tracing + OpenTelemetry
configurado para exportar trazas (por ejemplo, del servidor embebido y
peticiones al panel).

## Requisitos

- Java 21
- Maven 3.9+

## Cómo ejecutar

1. Compila el proyecto desde la raíz del repositorio:
    - `mvn -q -DskipTests package`
2. Inicia el servidor Eureka:
    - `mvn -q -pl eureka-server -am spring-boot:run`
3. Abre el panel web de Eureka en:
    - http://localhost:8761

## Observabilidad

- Muestreo de trazas al 100% (dev):
  `management.tracing.sampling.probability=1.0`.
- Exportación OTLP a Collector local:
  `management.otlp.tracing.endpoint=http://localhost:4317`.
- Logs con traceId/spanId.

## Configuración

- Puerto: `8761`
- No se auto-registra ni consume el registro (modo servidor puro).
- Archivo: `src/main/resources/application.yml`.

## ¿Qué es Eureka?

Eureka es un servicio de descubrimiento. Los clientes (microservicios) se
registran allí y pueden descubrirse por nombre lógico. En este ejemplo,
`service-provider` y `service-consumer` se registran en este servidor.
