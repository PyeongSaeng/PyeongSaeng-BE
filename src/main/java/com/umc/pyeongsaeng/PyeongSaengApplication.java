package com.umc.pyeongsaeng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PyeongSaengApplication {

    public static void main(String[] args) {
        SpringApplication.run(PyeongSaengApplication.class, args);
    }

}
