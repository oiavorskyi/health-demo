package com.example.healthdemo;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DependencyHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        if (!Dependency.isUp) {
            int sampleErrorCode = 42;
            return Health.down()
                    .withDetail("The dependency is DOWN", 42)
                    .build();
        }

        return Health.up().build();
    }
}
