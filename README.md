## Custom health indicators with Spring Boot Actuators

In this project we add custom health indicator to the Spring Boot Actuator
endpoint `/actuator/health` and demonstrate how to leverage built-in `k8s`
probes exposed by Actuator to indicated changes in the application health to the
deployment platform.

Start with
the [HealthDemoApplicationTests](src/test/java/com/example/healthdemo/HealthDemoApplicationTests.java)
to explore how the different actuator endpoints can be used and then look
through the main sources.

In the production code be cautious when deciding what checks to include into
readiness and liveness probes. You might want to use the following guidance:

- Use liveness probe to indicate that the instance of the application cannot run
  anymore and should be decommissioned by the runtime platform. It is probably
  not a good idea to include state of the external dependencies in the liveness
  check as it would result in the k8s terminating all instance of your
  application whenever external dependency is down.
- Use readiness probe to indicate that the instance of application cannot handle
  traffic at the moment. For example, you might decide that lack of access to
  the external database is a reason to take fail the readiness probe. Note, that
  in case of shared dependency, this could cause k8s to take *all* instances of
  you application out of rotation rendering your service completely unavailable.
  On the other hand, if the particular instance has too much of the requests to
  process it could be a good decision to temporarily take it out of rotation by
  failing the readiness probe.
- Use general health indicators for monitoring purposes rather than to make an
  automated scaling decisions.

Please read through the following sections of the Spring Boot documentation to
get better understanding of the demonstrated approach:

- [Application Availability](https://docs.spring.io/spring-boot/docs/2.5.7/reference/html/features.html#features.spring-application.application-availability)
- [Application Events and Listeners](https://docs.spring.io/spring-boot/docs/2.5.7/reference/html/features.html#features.spring-application.application-events-and-listeners)
- [Health Information](https://docs.spring.io/spring-boot/docs/2.5.7/reference/html/actuator.html#actuator.endpoints.health)
- [Kubernetes Probes](https://docs.spring.io/spring-boot/docs/2.5.7/reference/html/actuator.html#actuator.endpoints.kubernetes-probes)

For details of configuring readiness and liveness probes in k8s refer to the the
corresponding [section](https://kubernetes.io/docs/tasks/configure-pod-container/configure-liveness-readiness-startup-probes/)
in the k8s documentation.