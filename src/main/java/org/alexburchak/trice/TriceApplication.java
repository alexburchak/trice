package org.alexburchak.trice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class TriceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TriceApplication.class, args);
    }
}
