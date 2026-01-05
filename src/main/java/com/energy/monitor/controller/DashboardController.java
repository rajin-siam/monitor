package com.energy.monitor.controller;

import com.energy.monitor.dto.DashboardSummary;
import com.energy.monitor.model.EnergyMeter;
import com.energy.monitor.service.MeterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Dashboard Controller
 *
 * Provides aggregated data and statistics for dashboard display.
 * This shows overall system health and metrics.
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final MeterService meterService;

    public  DashboardController(MeterService meterService) {
        this.meterService = meterService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getDashboardSummary() {
        try {
            List<EnergyMeter> allMeters = meterService.getAllMeters();

            DashboardSummary summary = new DashboardSummary();

            summary.setTotalMeters(allMeters.size());

            double totalConsumption = 0.0;
            double totalProduction = 0.0;
            double totalVoltage = 0.0;
            int meterWithReadings = 0;

            int solarCount = 0;
            int gridCount = 0;
            int hybridCount = 0;

            for (EnergyMeter meter : allMeters) {
                switch (meter.getMeterType().toLowerCase()) {
                    case "solar":
                        solarCount++;
                        break;
                    case "grid":
                        gridCount++;
                        break;
                    case "hybrid":
                        hybridCount++;
                        break;
                }

                if (meter.getCurrentReading() != null) {
                    totalConsumption += meter.getCurrentReading().getGridConsumption();
                    totalProduction += meter.getCurrentReading().getSolarProduction();
                    totalVoltage += meter.getCurrentReading().getVoltage();
                    meterWithReadings++;
                }
            }

            summary.setTotalConsumption(totalConsumption);
            summary.setTotalProduction(totalProduction);

            if (meterWithReadings > 0) {
                summary.setAverageVoltage(totalVoltage / meterWithReadings);
            } else {
                summary.setAverageVoltage(0.0);
            }


            if (totalConsumption > 0) {
                summary.setEfficiencyPercentage((totalProduction / totalConsumption) * 100);
            } else {
                summary.setEfficiencyPercentage(0.0);
            }


            summary.setSolarMeters(solarCount);
            summary.setGridMeters(gridCount);
            summary.setHybridMeters(hybridCount);

            return ResponseEntity.ok(summary);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}

