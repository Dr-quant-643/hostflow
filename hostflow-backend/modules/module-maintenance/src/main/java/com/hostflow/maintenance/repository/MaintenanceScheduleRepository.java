package com.hostflow.maintenance.repository;

import com.hostflow.maintenance.entity.MaintenanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MaintenanceScheduleRepository extends JpaRepository<MaintenanceSchedule, UUID> {
    List<MaintenanceSchedule> findByActiveTrueAndNextDueDateLessThanEqual(LocalDate date);
}
