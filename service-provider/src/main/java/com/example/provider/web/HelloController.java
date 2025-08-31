package com.example.provider.web;

import com.example.provider.db.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api")
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    private final ItemRepository itemRepository;

    @Value("${spring.application.name}")
    private String appName;

    public HelloController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hola desde " + appName;
    }


    /**
     * Simulates an unreliable service that fails 70% of the time. Upon invocation,
     * this method generates a random number to determine whether to respond with
     * a success or an internal server error. Success responses include the application
     * name along with the generated random number.
     *
     * @return ResponseEntity containing either a success message with an HTTP 200
     * status or an error message with an HTTP 500 status.
     */
    @GetMapping("/unreliable")
    public ResponseEntity<String> unreliable() {
        logger.info("Calling unreliable service");
        int rnd = ThreadLocalRandom.current().nextInt(100);
        if (rnd < 70) { // fail 70% of requests
            logger.warn("Service failure (simulated)");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Service failure (simulated). Random=" + rnd);
        }
        return ResponseEntity.ok("Success from " + appName + " (Random=" + rnd + ")");
    }

    // Simple DB-backed endpoint to exercise JDBC spans
    @GetMapping("/db/items")
    public List<String> items() {
        logger.info("Calling DB");
        return itemRepository.findAll();
    }
}
