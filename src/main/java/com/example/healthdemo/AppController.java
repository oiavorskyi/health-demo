package com.example.healthdemo;

import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    private final ApplicationContext ctx;

    public AppController(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @GetMapping("/emulate-liveness-issue")
    public void emulateLivenessIssue() {
        AvailabilityChangeEvent.publish(ctx, LivenessState.BROKEN);
    }

    @GetMapping("/emulate-liveness-recovery")
    public void emulateLivenessRecovery() {
        AvailabilityChangeEvent.publish(ctx, LivenessState.CORRECT);
    }

    @GetMapping("/emulate-readiness-issue")
    public void emulateReadinessIssue() {
        AvailabilityChangeEvent.publish(ctx, ReadinessState.REFUSING_TRAFFIC);
    }

    @GetMapping("/emulate-readiness-recovery")
    public void emulateReadinessRecovery() {
        AvailabilityChangeEvent.publish(ctx, ReadinessState.ACCEPTING_TRAFFIC);
    }

    @GetMapping("/emulate-dependency-down")
    public void emulateDependencyDown() {
        Dependency.isUp = false;
    }

    @GetMapping("/emulate-dependency-up")
    public void emulateDependencyUp() {
        Dependency.isUp = true;
    }
}
