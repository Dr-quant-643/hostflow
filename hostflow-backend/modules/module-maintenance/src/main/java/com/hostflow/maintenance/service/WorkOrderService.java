package com.hostflow.maintenance.service;

import com.hostflow.common.exception.ResourceNotFoundException;
import com.hostflow.maintenance.dto.CreateWorkOrderRequest;
import com.hostflow.maintenance.entity.WorkOrder;
import com.hostflow.maintenance.repository.WorkOrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WorkOrderService {

    private final WorkOrderRepository workOrderRepository;

    public WorkOrderService(WorkOrderRepository workOrderRepository) {
        this.workOrderRepository = workOrderRepository;
    }

    @Transactional
    public WorkOrder create(UUID reportedByUserId, CreateWorkOrderRequest request) {
        WorkOrder wo = new WorkOrder(request.propertyId(), reportedByUserId, request.category(),
                request.title(), request.description(), request.priority());
        return workOrderRepository.save(wo);
    }

    @Transactional(readOnly = true)
    public WorkOrder getById(UUID id) {
        return workOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("WorkOrder", id));
    }

    @Transactional(readOnly = true)
    public Page<WorkOrder> listByProperty(UUID propertyId, int limit, int offset) {
        return workOrderRepository.findByPropertyId(propertyId, PageRequest.of(offset / Math.max(limit, 1), limit));
    }

    @Transactional(readOnly = true)
    public Page<WorkOrder> listByTechnician(UUID technicianUserId, int limit, int offset) {
        return workOrderRepository.findByAssignedTechnicianUserId(technicianUserId, PageRequest.of(offset / Math.max(limit, 1), limit));
    }

    @Transactional
    public WorkOrder assign(UUID id, UUID technicianUserId) {
        WorkOrder wo = getById(id);
        wo.assign(technicianUserId);
        return wo;
    }

    @Transactional
    public WorkOrder startWork(UUID id) {
        WorkOrder wo = getById(id);
        wo.startWork();
        return wo;
    }

    @Transactional
    public WorkOrder complete(UUID id, String resolutionNotes) {
        WorkOrder wo = getById(id);
        wo.complete(resolutionNotes);
        return wo;
    }

    @Transactional
    public WorkOrder cancel(UUID id) {
        WorkOrder wo = getById(id);
        wo.cancel();
        return wo;
    }
}
