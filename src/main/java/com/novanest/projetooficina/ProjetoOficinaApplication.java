package com.novanest.projetooficina;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProjetoOficinaApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjetoOficinaApplication.class, args);
    }
}
