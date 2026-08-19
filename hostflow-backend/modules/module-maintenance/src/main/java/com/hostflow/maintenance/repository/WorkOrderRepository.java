package com.hostflow.maintenance.repository;

import com.hostflow.maintenance.entity.WorkOrder;
import com.hostflow.maintenance.entity.WorkOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID> {
    Page<WorkOrder> findByPropertyId(UUID propertyId, Pageable pageable);
    Page<WorkOrder> findByStatus(WorkOrderStatus status, Pageable pageable);
    Page<WorkOrder> findByAssignedTechnicianUserId(UUID technicianUserId, Pageable pageable);
}
