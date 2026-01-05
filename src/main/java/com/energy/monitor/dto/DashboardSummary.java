package com.energy.monitor.dto;

import lombok.Data;

@Data
public class DashboardSummary {

    private Integer totalMeters;
    private Integer solarMeters;
    private Integer gridMeters;
    private Integer hybridMeters;

    private Double totalConsumption;
    private Double totalProduction;
    private Double averageVoltage;

    private Double efficiencyPercentage;
}
