package com.energy.monitor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MeterMaintenanceScheduleDto {
    @NotBlank(message = "Meter ID is required")
    private String meterId;
    
    @NotBlank(message = "Meter type is required")
    private String meterType;
    
    @NotBlank(message = "Maintenance type is required")
    private String maintenanceType;
    
    @NotNull(message = "Scheduled date is required")
    private LocalDateTime scheduledDate;
    
    private String status = "SCHEDULED";
    
    @NotBlank(message = "Assigned technician is required")
    private String assignedTechnician;
    
    private String description;
}