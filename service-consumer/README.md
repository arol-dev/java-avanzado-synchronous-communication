# Service Consumer

Este microservicio consume al `service-provider` usando OpenFeign y se registra
en Eureka.

## Requisitos

- Java 21
- Maven 3.9+
- Servidor Eureka en ejecución en `http://localhost:8761`
- `service-provider` corriendo y registrado en Eureka

## Endpoints

- `GET /api/proxy` → Llama a `service-provider` (`/api/hello`) vía Feign y
  devuelve su respuesta.

## Cómo ejecutar

1. Compila el proyecto desde la raíz:
    - `mvn -q -DskipTests package`
2. Asegúrate de que `eureka-server` y `service-provider` están corriendo.
3. Ejecuta este servicio:
    - `mvn -q -pl service-consumer -am spring-boot:run`
4. Probar:
    - `curl http://localhost:8080/api/proxy`

## Configuración

- Puerto: `8080`
- Nombre de la app: `service-consumer`
- Registro en Eureka:
  `eureka.client.service-url.defaultZone=http://localhost:8761/eureka`
- Feign auto-descubre `service-provider` por su nombre en Eureka.
