package com.energy.monitor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentReading {
    private LocalDateTime timestamp;
    private Double gridConsumption;
    private Double solarProduction;
    private Double netUsage;
    private Double voltage;
    private Double current;
    private Double powerFactor;
    private Integer batteryLevel;

    public void calculateNetUsage() {
        if (gridConsumption != null && solarProduction != null) {
            this.netUsage = gridConsumption - solarProduction;
        }
    }
}
