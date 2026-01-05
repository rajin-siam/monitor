package com.energy.monitor.controller;

import com.energy.monitor.model.*;
import com.energy.monitor.service.MeterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/meters")
public class MeterController {

    @Autowired
    private MeterService meterService;

    @PostMapping
    public ResponseEntity<EnergyMeter> registerMeter(@RequestBody EnergyMeter meter) {
        try {
            EnergyMeter registered = meterService.registerMeter(meter);
            return ResponseEntity.status(HttpStatus.CREATED).body(registered);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<EnergyMeter>> getAllMeters() {
        try {
            List<EnergyMeter> meters = meterService.getAllMeters();

            return ResponseEntity.ok(meters);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/{meterId}")
    public ResponseEntity<EnergyMeter> getMeter(@PathVariable String meterId) {
        try {
            EnergyMeter meter = meterService.getMeter(meterId);

            if (meter == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(meter);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{meterId}/reading")
    public ResponseEntity<EnergyMeter> updateReading(
            @PathVariable String meterId,
            @RequestBody CurrentReading reading) {
        try {
            // Update meter with new reading
            EnergyMeter updated = meterService.updateReading(meterId, reading);

            // Return 200 OK with updated meter
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            // Meter not found
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{meterId}")
    public ResponseEntity<Void> deleteMeter(@PathVariable String meterId) {
        try {
            boolean deleted = meterService.deleteMeter(meterId);

            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<EnergyMeter>> getMetersByType(@PathVariable String type) {
        try {
            List<EnergyMeter> meters = meterService.getMetersByType(type);
            return ResponseEntity.ok(meters);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{meterId}/alerts")
    public ResponseEntity<List<Alert>> getAlerts(@PathVariable String meterId) {
        try {
            EnergyMeter meter = meterService.getMeter(meterId);

            if (meter == null) {
                return ResponseEntity.notFound().build();
            }

            // Return alerts list
            return ResponseEntity.ok(meter.getAlerts());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
