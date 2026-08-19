package com.hostflow.maintenance.entity;

import com.hostflow.common.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WorkOrderEntityTest {

    private WorkOrder newWorkOrder() {
        return new WorkOrder(UUID.randomUUID(), UUID.randomUUID(), MaintenanceCategory.PLUMBING,
                "Leaking faucet", "Kitchen sink leaking", WorkOrderPriority.MEDIUM);
    }

    @Test
    void newWorkOrder_startsOpen() {
        assertThat(newWorkOrder().getStatus()).isEqualTo(WorkOrderStatus.OPEN);
    }

    @Test
    void fullLifecycle_openToAssignedToInProgressToCompleted() {
        WorkOrder wo = newWorkOrder();
        UUID techId = UUID.randomUUID();

        wo.assign(techId);
        assertThat(wo.getStatus()).isEqualTo(WorkOrderStatus.ASSIGNED);

        wo.startWork();
        assertThat(wo.getStatus()).isEqualTo(WorkOrderStatus.IN_PROGRESS);

        wo.complete("Replaced washer");
        assertThat(wo.getStatus()).isEqualTo(WorkOrderStatus.COMPLETED);
        assertThat(wo.getResolutionNotes()).isEqualTo("Replaced washer");
    }

    @Test
    void startWork_beforeAssigned_throws() {
        assertThatThrownBy(newWorkOrder()::startWork)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("expected ASSIGNED");
    }

    @Test
    void cancel_afterCompleted_throws() {
        WorkOrder wo = newWorkOrder();
        wo.assign(UUID.randomUUID());
        wo.startWork();
        wo.complete("done");

        assertThatThrownBy(wo::cancel)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("COMPLETED");
    }

    @Test
    void cancel_fromOpen_isAllowed() {
        WorkOrder wo = newWorkOrder();

        wo.cancel();

        assertThat(wo.getStatus()).isEqualTo(WorkOrderStatus.CANCELLED);
    }
}
