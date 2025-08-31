# Comunicación síncrona con Spring Boot, Eureka y OpenFeign

Este repositorio contiene un ejemplo mínimo de comunicación entre microservicios
usando:

- Eureka Server (descubrimiento de servicios)
- 2 microservicios: `service-provider` y `service-consumer`
- OpenFeign para llamadas HTTP entre servicios usando descubrimiento por nombre

Cada módulo tiene su propio README con pasos de ejecución y detalles.

## Versiones

- Java 21
- Spring Boot 3.5.0
- Spring Cloud 2025.0.1

## Módulos

- `eureka-server`: servidor de descubrimiento en `:8761`.
- `service-provider`: expone `GET /api/hello` en `:8081` y se registra en
  Eureka.
- `service-consumer`: expone `GET /api/proxy` en `:8080` y llama al provider vía
  Feign.

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

Para más detalles y opciones de configuración, revisa los README de cada módulo.

## Ejecutar con Docker Compose

Se incluye un archivo `docker-compose.yml` que levanta los 3 servicios en
paralelo y configura las dependencias básicas de arranque.

Comandos:

- Construir imágenes: `docker compose build`
- Levantar servicios: `docker compose up`
- Probar:
    - `curl http://localhost:8761` (panel de Eureka)
    - `curl http://localhost:8081/api/hello`
    - `curl http://localhost:8080/api/proxy`

Notas:

- Los clientes usan la variable de entorno
  `EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://eureka-server:8761/eureka` para
  descubrir Eureka dentro de la red de Docker.
- `depends_on` asegura el orden de arranque (Eureka → Provider → Consumer). Aun
  así, los clientes reintentan el registro hasta que Eureka esté disponible.
