package com.hostflow.maintenance.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AssetEntityTest {

    @Test
    void isUnderWarranty_trueWhenExpiryInFuture() {
        Asset asset = new Asset(UUID.randomUUID(), "AC Unit", "HVAC", "SN123",
                LocalDate.now().minusYears(1), LocalDate.now().plusYears(1));

        assertThat(asset.isUnderWarranty()).isTrue();
    }

    @Test
    void isUnderWarranty_falseWhenExpired() {
        Asset asset = new Asset(UUID.randomUUID(), "AC Unit", "HVAC", "SN123",
                LocalDate.now().minusYears(3), LocalDate.now().minusDays(1));

        assertThat(asset.isUnderWarranty()).isFalse();
    }

    @Test
    void isUnderWarranty_falseWhenNoExpiryDateSet() {
        Asset asset = new Asset(UUID.randomUUID(), "Old Fridge", "Appliance", null, null, null);

        assertThat(asset.isUnderWarranty()).isFalse();
    }
}
