package com.energy.monitor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class DailyStats {

    private Double totalConsumption;
    private Double totalProduction;
    private Double peakConsumption;
    private Double peakProduction;

    public DailyStats() {
        this.totalConsumption = 0.0;
        this.totalProduction = 0.0;
        this.peakConsumption = 0.0;
        this.peakProduction = 0.0;
    }

    public void updateWithReading(CurrentReading reading) {
        this.totalConsumption += reading.getGridConsumption();
        this.totalProduction += reading.getSolarProduction();

        if(reading.getGridConsumption() > this.peakConsumption) {
            this.peakConsumption = reading.getGridConsumption();
        }
        if(reading.getSolarProduction() > this.peakProduction) {
            this.peakProduction = reading.getSolarProduction();
        }
    }
}
