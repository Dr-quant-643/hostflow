package com.hostflow.tenancy.context;

import com.hostflow.common.exception.TenantContextMissingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TenantContextTest {

    @AfterEach
    void cleanup() {
        TenantContext.clear();
    }

    @Test
    void setAndGet_returnsTheSameTenantId() {
        UUID tenantId = UUID.randomUUID();
        TenantContext.set(tenantId);

        assertThat(TenantContext.get()).isEqualTo(tenantId);
        assertThat(TenantContext.isSet()).isTrue();
    }

    @Test
    void get_returnsNull_whenNothingSet() {
        assertThat(TenantContext.get()).isNull();
        assertThat(TenantContext.isSet()).isFalse();
    }

    @Test
    void require_throwsTenantContextMissingException_whenUnset() {
        assertThatThrownBy(TenantContext::require)
                .isInstanceOf(TenantContextMissingException.class);
    }

    @Test
    void require_returnsTenantId_whenSet() {
        UUID tenantId = UUID.randomUUID();
        TenantContext.set(tenantId);

        assertThat(TenantContext.require()).isEqualTo(tenantId);
    }

    @Test
    void clear_removesTenantContext() {
        TenantContext.set(UUID.randomUUID());
        TenantContext.clear();

        assertThat(TenantContext.isSet()).isFalse();
    }
}
