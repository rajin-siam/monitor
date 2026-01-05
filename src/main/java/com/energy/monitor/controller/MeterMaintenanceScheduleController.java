package com.energy.monitor.controller;

import com.energy.monitor.dto.MeterMaintenanceScheduleDto;
import com.energy.monitor.model.MeterMaintenanceSchedule;
import com.energy.monitor.service.MeterMaintenanceScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/maintenance/schedules")
@RequiredArgsConstructor
public class MeterMaintenanceScheduleController {

    private final MeterMaintenanceScheduleService maintenanceScheduleService;

    @PostMapping
    public ResponseEntity<MeterMaintenanceSchedule> createSchedule(@Valid @RequestBody MeterMaintenanceScheduleDto dto) {
        var schedule = maintenanceScheduleService.createSchedule(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(schedule);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeterMaintenanceSchedule> getSchedule(@PathVariable String id) {
        var schedule = maintenanceScheduleService.getSchedule(id);
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(schedule);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeterMaintenanceSchedule> updateSchedule(
            @PathVariable String id, 
            @Valid @RequestBody MeterMaintenanceScheduleDto dto) {
        var schedule = maintenanceScheduleService.updateSchedule(id, dto);
        if (schedule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(schedule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable String id) {
        boolean deleted = maintenanceScheduleService.deleteSchedule(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}