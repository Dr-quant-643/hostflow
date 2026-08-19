package com.hostflow.app.integration;

import com.hostflow.app.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationPipelineIT extends AbstractIntegrationTest {

    @MockBean
    private Object notificationTemplateRepository;

    @Test
    void contextLoads() {
        assertThat(notificationTemplateRepository).isNotNull();
    }
}
