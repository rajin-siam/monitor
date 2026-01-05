package com.energy.monitor.service;

import com.energy.monitor.model.Alert;
import com.energy.monitor.model.CurrentReading;
import com.energy.monitor.model.DailyStats;
import com.energy.monitor.model.EnergyMeter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MeterService {


    private final RedisTemplate<String, Object> redisTemplate;
    private final String METER_KEY_PREFIX = "meter:";

    public MeterService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public EnergyMeter registerMeter(EnergyMeter meter) {
        if(meter.getMeterId() == null || meter.getMeterId().isEmpty()) {
           meter.setMeterId("METER_" + UUID.randomUUID().toString().substring(0, 8));
        }

        if(meter.getAlerts() == null) {
            meter.setAlerts(new ArrayList<>());
        }

        if(meter.getDailyStats() == null) {
            meter.setDailyStats(new DailyStats());
        }

        String key = METER_KEY_PREFIX + meter.getMeterId();

        redisTemplate.opsForValue().set(key, meter);

        return  meter;
    }

    public EnergyMeter getMeter(String meterId) {
        String key = METER_KEY_PREFIX + meterId;
        Object value = redisTemplate.opsForValue().get(key);
        return coverToMeter(value);
    }

    public List<EnergyMeter> getAllMeters() {
        Set<String> keys = redisTemplate.keys(METER_KEY_PREFIX + "*");
        List<Object> values = redisTemplate.opsForValue().multiGet(keys);
        return values.stream()
                .map(this::coverToMeter)
                .filter(Objects::nonNull)
                .toList();

    }

    public EnergyMeter updateReading(String meterId, CurrentReading reading) {
        EnergyMeter meter = getMeter(meterId);

        if(meter == null) {
            throw  new RuntimeException("Meter with id " + meterId + " not found");
        }

        reading.setTimestamp(LocalDateTime.now());
        reading.calculateNetUsage();
        meter.setCurrentReading(reading);

        if(meter.getDailyStats() != null) {
            meter.getDailyStats().updateWithReading(reading);
        }
        checkAndAddAlerts(meter, reading);

        String key = METER_KEY_PREFIX + meterId;
        redisTemplate.opsForValue().set(key, meter);
        return meter;

    }

    private void checkAndAddAlerts(EnergyMeter meter, CurrentReading reading) {
        List<Alert> alerts = meter.getAlerts();
        if(reading.getGridConsumption() > 5.0) {
           Alert alert = new Alert(
                     "High Consumption",
                     "Grid consumption exceeded 5 kW: " + reading.getGridConsumption() + " kW",
                     LocalDateTime.now(),
                   "medium"
           );
           alerts.add(alert);
        }

        if (reading.getVoltage() < 220.0) {
            Alert alert = new Alert(
                    "low_voltage",
                    "Voltage dropped below 220V: " + reading.getVoltage() + " V",
                    LocalDateTime.now(),
                    "high"
            );
            alerts.add(alert);
        }

        if (reading.getBatteryLevel() != null && reading.getBatteryLevel() < 20) {
            Alert alert = new Alert(
                    "low_battery",
                    "Battery level critical: " + reading.getBatteryLevel() + "%",
                    LocalDateTime.now(),
                    "high"
            );
            alerts.add(alert);
        }
        if(alerts.size() > 10) {
            alerts.subList(0, alerts.size() - 10).clear();
        }
    }

    public boolean deleteMeter(String meterId) {
        String key = METER_KEY_PREFIX + meterId;
        return redisTemplate.delete(key);
    }

    public List<EnergyMeter> getMetersByType(String meterType) {
        return getAllMeters().stream()
                .filter(meter -> meter.getMeterType().equalsIgnoreCase(meterType))
                .toList();
    }

    private EnergyMeter coverToMeter(Object obj) {
        if(obj == null) return null;
        try {
            if(obj instanceof EnergyMeter) {
                return (EnergyMeter) obj;
            }
            if(obj instanceof LinkedHashMap) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                return mapper.convertValue(obj, EnergyMeter.class);
            }
        } catch (Exception ex) {
            System.err.println("Error converting to EnergyMeter: " + ex.getMessage());
            return null;
        }
        return null;
    }

}
