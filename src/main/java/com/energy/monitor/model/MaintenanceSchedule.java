package com.energy.monitor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceSchedule {
    private LocalDate lastServiced;
    private LocalDate nextDue;
    private List<String> notes;
    private String status;

    public MaintenanceSchedule(LocalDate lastServiced, LocalDate nextDue) {
        this.lastServiced = lastServiced;
        this.nextDue = nextDue;
        this.notes = new ArrayList<>();
        this.status = "active";
    }
}