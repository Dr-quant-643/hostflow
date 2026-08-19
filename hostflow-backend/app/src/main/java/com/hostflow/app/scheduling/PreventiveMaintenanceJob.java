package com.hostflow.app.scheduling;

import com.hostflow.maintenance.entity.MaintenanceSchedule;
import com.hostflow.maintenance.entity.WorkOrderPriority;
import com.hostflow.maintenance.repository.MaintenanceScheduleRepository;
import com.hostflow.maintenance.repository.WorkOrderRepository;
import com.hostflow.maintenance.entity.WorkOrder;
import com.hostflow.tenancy.context.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Closes "preventive maintenance" from the vision doc for real: daily job scans
 * ALL tenants (cross-tenant, platformAdminJdbcTemplate) for due schedules, then
 * switches to normal RLS-scoped repositories per row (TenantContext set, same
 * pattern as ExpireStalePendingBookingsJob) to create the WorkOrder and roll the
 * schedule forward.
 */
@Component
public class PreventiveMaintenanceJob {

    private static final Logger log = LoggerFactory.getLogger(PreventiveMaintenanceJob.class);

    private final JdbcTemplate platformAdminJdbcTemplate;
    private final MaintenanceScheduleRepository scheduleRepository;
    private final WorkOrderRepository workOrderRepository;

    public PreventiveMaintenanceJob(@Qualifier("platformAdminJdbcTemplate") JdbcTemplate platformAdminJdbcTemplate,
                                     MaintenanceScheduleRepository scheduleRepository,
                                     WorkOrderRepository workOrderRepository) {
        this.platformAdminJdbcTemplate = platformAdminJdbcTemplate;
        this.scheduleRepository = scheduleRepository;
        this.workOrderRepository = workOrderRepository;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void generateDueWorkOrders() {
        List<Object[]> dueSchedules = platformAdminJdbcTemplate.query(
                "SELECT id, tenant_id FROM maintenance_schedules WHERE active = true AND next_due_date <= CURRENT_DATE",
                (rs, rowNum) -> new Object[]{UUID.fromString(rs.getString("id")), UUID.fromString(rs.getString("tenant_id"))});

        int generated = 0;
        for (Object[] row : dueSchedules) {
            UUID scheduleId = (UUID) row[0];
            UUID tenantId = (UUID) row[1];

            TenantContext.set(tenantId);
            try {
                generateOne(scheduleId);
                generated++;
            } finally {
                TenantContext.clear();
            }
        }
        if (generated > 0) {
            log.info("Preventive maintenance: generated {} work order(s) from due schedules", generated);
        }
    }

    @Transactional
    protected void generateOne(UUID scheduleId) {
        MaintenanceSchedule schedule = scheduleRepository.findById(scheduleId).orElse(null);
        if (schedule == null) return;

        WorkOrder workOrder = new WorkOrder(schedule.getPropertyId(), null, schedule.getCategory(),
                "[Preventive] " + schedule.getTitle(), "Auto-generated from recurring maintenance schedule",
                WorkOrderPriority.MEDIUM);
        workOrderRepository.save(workOrder);

        schedule.rollToNextDueDate();
    }
}
