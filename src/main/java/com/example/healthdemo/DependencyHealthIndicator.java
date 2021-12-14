package com.example.healthdemo;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Custom health indicator that checks the state of the external dependency
 * and reports it to Spring Boot monitoring infrastructure. It is enough to
 * include bean implementing {@link HealthIndicator} to Spring context for
 * Spring Actuator to start polling it and including the result into the
 * overall health of the application.
 * <p>
 * It is also possible to include specific custom health indicators in
 * application readiness probe exposed at {@code /actuator/health/readiness}
 * endpoint, by adding its name to {@code management.endpoint.health.group.readiness.include}
 * property. See {@code application.properties} file in this project for
 * an example.
 * <p>
 * For more details see <a href=https://docs.spring.io/spring-boot/docs/2.5.7/reference/html/actuator.html#actuator.endpoints.health>documentation</a>
 *
 * @see HealthIndicator
 */
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
