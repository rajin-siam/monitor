package com.energy.monitor.service;

import com.energy.monitor.model.CurrentReading;
import com.energy.monitor.model.EnergyMeter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class SimulationService {


    private final MeterService meterService;

    public  SimulationService(MeterService meterService) {
        this.meterService = meterService;
    }


    private Random random = new Random();

    @Scheduled(fixedRate = 10000)  // Run every 10 seconds
    public void simulateReadings() {
        try {
            List<EnergyMeter> meters = meterService.getAllMeters();

            for (EnergyMeter meter : meters) {
                CurrentReading reading = generateReading(meter.getMeterType());

                meterService.updateReading(meter.getMeterId(), reading);

                System.out.println("Updated reading for meter: " + meter.getMeterId());
            }

            if (!meters.isEmpty()) {
                System.out.println("Simulated readings for " + meters.size() + " meters");
            }

        } catch (Exception e) {
            System.err.println("Error in simulation: " + e.getMessage());
        }
    }

    private CurrentReading generateReading(String meterType) {
        CurrentReading reading = new CurrentReading();

        double consumption = 1.0 + (random.nextDouble() * 5.0);
        double production = 0.0;

        switch (meterType.toLowerCase()) {
            case "solar":
                production = 2.0 + (random.nextDouble() * 6.0);
                consumption = 0.5 + (random.nextDouble() * 2.0);
                break;

            case "grid":
                production = 0.0;
                break;

            case "hybrid":
                production = 1.0 + (random.nextDouble() * 4.0);
                break;
        }

        reading.setGridConsumption(roundToTwoDecimals(consumption));
        reading.setSolarProduction(roundToTwoDecimals(production));

        double voltage = random.nextDouble() < 0.1 ?
                210 + (random.nextDouble() * 10) :
                220 + (random.nextDouble() * 20);
        reading.setVoltage(roundToTwoDecimals(voltage));

        double current = 10 + (random.nextDouble() * 10);
        reading.setCurrent(roundToTwoDecimals(current));

        double powerFactor = 0.85 + (random.nextDouble() * 0.13);
        reading.setPowerFactor(roundToTwoDecimals(powerFactor));

        if (!meterType.equalsIgnoreCase("grid")) {
            int batteryLevel = random.nextDouble() < 0.05 ?
                    10 + random.nextInt(10) :
                    50 + random.nextInt(51);
            reading.setBatteryLevel(batteryLevel);
        }

        return reading;
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
