package com.novanest.projetooficina;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProjetoOficinaApplicationTests {

    @Value("${app.jwt.secret}")
    private String secret;

    @Test
    void contextLoads() {

        System.out.println(secret);

    }



}
