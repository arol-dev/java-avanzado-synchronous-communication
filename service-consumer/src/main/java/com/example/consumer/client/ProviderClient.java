package com.example.consumer.client;

import java.util.List;

// TODO: Ejercicio 2 - Añadir la anotación @FeignClient
// @FeignClient(name = "service-provider")
public interface ProviderClient {

    // TODO: Ejercicio 2 - Añadir la anotación @GetMapping
    // @GetMapping("/api/hello")
    String hello();

    // TODO: Ejercicio 2 - Añadir la anotación @GetMapping
    // @GetMapping("/api/unreliable")
    String unreliable();

    // TODO: Ejercicio 2 - Añadir la anotación @GetMapping
    // @GetMapping("/api/db/items")
    List<String> items();
}
