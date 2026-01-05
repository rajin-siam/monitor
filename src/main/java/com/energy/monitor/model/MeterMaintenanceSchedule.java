package com.energy.monitor.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeterMaintenanceSchedule {
    private String id;
    private String meterId;
    private String meterType;
    private String maintenanceType;
    private LocalDateTime scheduledDate;
    private String status;
    private String assignedTechnician;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}