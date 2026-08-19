package com.hostflow.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.hostflow")
@EnableJpaRepositories(basePackages = {
    "com.hostflow.billing.repository",
    "com.hostflow.identity.repository",
    "com.hostflow.property.repository",
    "com.hostflow.booking.repository",
    "com.hostflow.notification.repository",
    "com.hostflow.crm.repository",
    "com.hostflow.marketing.repository",
    "com.hostflow.analytics.repository",
    "com.hostflow.persistence.repository",
    "com.hostflow.tenancy.repository",
    "com.hostflow.maintenance.repository",
    "com.hostflow.rental.repository",
    "com.hostflow.office.repository",
    "com.hostflow.mall.repository",
    "com.hostflow.review.repository",
    "com.hostflow.platformadmin.repository"
})
@EntityScan(basePackages = {
    "com.hostflow.billing.entity",
    "com.hostflow.identity.entity",
    "com.hostflow.property.entity",
    "com.hostflow.booking.entity",
    "com.hostflow.notification.entity",
    "com.hostflow.crm.entity",
    "com.hostflow.marketing.entity",
    "com.hostflow.analytics.entity",
    "com.hostflow.persistence.entity",
    "com.hostflow.tenancy.entity",
    "com.hostflow.maintenance.entity",
    "com.hostflow.rental.entity",
    "com.hostflow.office.entity",
    "com.hostflow.mall.entity",
    "com.hostflow.review.entity",
    "com.hostflow.platformadmin.entity"
})
@EnableScheduling
public class HostFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(HostFlowApplication.class, args);
    }
}
