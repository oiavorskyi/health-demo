package com.example.healthdemo;

import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.AvailabilityState;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes endpoints that allow to control health of the application
 * for demo purposes.
 */
@RestController
public class AppController {

    private final ApplicationContext ctx;

    public AppController(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Emulates liveness issue that indicate to the deployment infrastructure that
     * the application cannot be run anymore. When deployed to k8s this will
     * result in the termination of the pod.
     * <p>
     * Leverages built-in Spring Boot event publishing mechanism. For more details
     * see <a href="https://docs.spring.io/spring-boot/docs/2.5.7/reference/html/features.html#features.spring-application.application-availability.managing">documentation</a>.
     *
     * @see AvailabilityChangeEvent#publish(ApplicationContext, AvailabilityState)
     * @see AvailabilityChangeEvent#publish(ApplicationEventPublisher, Object, AvailabilityState)
     */
    @GetMapping("/emulate-liveness-issue")
    public void emulateLivenessIssue() {
        AvailabilityChangeEvent.publish(ctx, LivenessState.BROKEN);
    }

    /**
     * Emulates recovery from the liveness issue.
     * <p>
     * Leverages built-in Spring Boot event publishing mechanism. For more details
     * see <a href="https://docs.spring.io/spring-boot/docs/2.5.7/reference/html/features.html#features.spring-application.application-availability.managing">documentation</a>.
     *
     * @see AvailabilityChangeEvent#publish(ApplicationContext, AvailabilityState)
     * @see AvailabilityChangeEvent#publish(ApplicationEventPublisher, Object, AvailabilityState)
     */
    @GetMapping("/emulate-liveness-recovery")
    public void emulateLivenessRecovery() {
        AvailabilityChangeEvent.publish(ctx, LivenessState.CORRECT);
    }

    /**
     * Emulates an issue that temporary prevents this application from serving
     * traffic. When deployed to k8s this will result in taking this instance
     * from the service load balancing pool.
     */
    @GetMapping("/emulate-readiness-issue")
    public void emulateReadinessIssue() {
        AvailabilityChangeEvent.publish(ctx, ReadinessState.REFUSING_TRAFFIC);
    }

    /**
     * Emulates recovery from the readiness issue.
     * <p>
     * Leverages built-in Spring Boot event publishing mechanism. For more details
     * see <a href="https://docs.spring.io/spring-boot/docs/2.5.7/reference/html/features.html#features.spring-application.application-availability.managing">documentation</a>.
     *
     * @see AvailabilityChangeEvent#publish(ApplicationContext, AvailabilityState)
     * @see AvailabilityChangeEvent#publish(ApplicationEventPublisher, Object, AvailabilityState)
     */
    @GetMapping("/emulate-readiness-recovery")
    public void emulateReadinessRecovery() {
        AvailabilityChangeEvent.publish(ctx, ReadinessState.ACCEPTING_TRAFFIC);
    }

    /**
     * Emulates temporary unavailability of external dependency. The change
     * in the state will be picked up by {@link DependencyHealthIndicator} end
     * contribute to the overall state of the application health exposed by
     * {@code /actuator/health} endpoint.
     */
    @GetMapping("/emulate-dependency-down")
    public void emulateDependencyDown() {
        Dependency.isUp = false;
    }

    /**
     * Emulates recovery of external dependency. The change
     * in the state will be picked up by {@link DependencyHealthIndicator} end
     * contribute to the overall state of the application health exposed by
     * {@code /actuator/health} endpoint.
     */
    @GetMapping("/emulate-dependency-up")
    public void emulateDependencyUp() {
        Dependency.isUp = true;
    }
}
