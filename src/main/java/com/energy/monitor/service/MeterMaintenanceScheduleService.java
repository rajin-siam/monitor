package com.energy.monitor.service;

import com.energy.monitor.dto.MeterMaintenanceScheduleDto;
import com.energy.monitor.model.MeterMaintenanceSchedule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class MeterMaintenanceScheduleService {

    private final RedisJsonService redisJsonService;
    private final ObjectMapper objectMapper;
    private static final String KEY_PREFIX = "maintenance:schedule:";

    public MeterMaintenanceScheduleService(RedisJsonService redisJsonService) {
        this.redisJsonService = redisJsonService;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public MeterMaintenanceSchedule createSchedule(MeterMaintenanceScheduleDto dto) {
        var schedule = new MeterMaintenanceSchedule();
        schedule.setId(UUID.randomUUID().toString());
        schedule.setMeterId(dto.getMeterId());
        schedule.setMeterType(dto.getMeterType());
        schedule.setMaintenanceType(dto.getMaintenanceType());
        schedule.setScheduledDate(dto.getScheduledDate());
        schedule.setStatus(dto.getStatus());
        schedule.setAssignedTechnician(dto.getAssignedTechnician());
        schedule.setDescription(dto.getDescription());
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setUpdatedAt(LocalDateTime.now());

        saveSchedule(schedule);
        return schedule;
    }

    public MeterMaintenanceSchedule getSchedule(String id) {
        String key = KEY_PREFIX + id;
        String jsonData = redisJsonService.getJsonValue(key);
        
        if (jsonData == null) {
            return null;
        }

        try {
            return objectMapper.readValue(jsonData, MeterMaintenanceSchedule.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize maintenance schedule", e);
            return null;
        }
    }

    public MeterMaintenanceSchedule updateSchedule(String id, MeterMaintenanceScheduleDto dto) {
        var existingSchedule = getSchedule(id);
        if (existingSchedule == null) {
            return null;
        }

        existingSchedule.setMeterId(dto.getMeterId());
        existingSchedule.setMeterType(dto.getMeterType());
        existingSchedule.setMaintenanceType(dto.getMaintenanceType());
        existingSchedule.setScheduledDate(dto.getScheduledDate());
        existingSchedule.setStatus(dto.getStatus());
        existingSchedule.setAssignedTechnician(dto.getAssignedTechnician());
        existingSchedule.setDescription(dto.getDescription());
        existingSchedule.setUpdatedAt(LocalDateTime.now());

        saveSchedule(existingSchedule);
        return existingSchedule;
    }

    public boolean deleteSchedule(String id) {
        String key = KEY_PREFIX + id;
        try {
            redisJsonService.deleteJsonValueByKey(key);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete maintenance schedule: {}", id, e);
            return false;
        }
    }

    private void saveSchedule(MeterMaintenanceSchedule schedule) {
        try {
            String key = KEY_PREFIX + schedule.getId();
            String jsonData = objectMapper.writeValueAsString(schedule);
            var triple = Triple.of(0L, key, jsonData);
            redisJsonService.saveJsonValue(List.of(triple));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize maintenance schedule", e);
            throw new RuntimeException("Failed to save maintenance schedule", e);
        }
    }
}