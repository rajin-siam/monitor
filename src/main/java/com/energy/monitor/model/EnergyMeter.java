package com.energy.monitor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnergyMeter {
    private String meterId;
    private String meterType;
    private Location location;
    private CurrentReading currentReading;
    private DailyStats dailyStats;
    private List<Alert> alerts;

    public EnergyMeter(String meterId, String meterType, Location location) {
        this.meterId = meterId;
        this.meterType = meterType;
        this.location = location;
        this.alerts = new ArrayList<>();
        this.dailyStats = new DailyStats();
    }
}
