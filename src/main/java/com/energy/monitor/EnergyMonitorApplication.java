package com.energy.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EnergyMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnergyMonitorApplication.class, args);
    }

}
