package com.jml.reconciliation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.jml.reconciliation.entity")
@EnableJpaRepositories(basePackages = "com.jml.reconciliation.repository")
public class JmlReconciliationApplication {

    public static void main(String[] args) {
        SpringApplication.run(JmlReconciliationApplication.class, args);
    }
}
