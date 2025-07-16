package com.hotpot.common.config;

import io.micrometer.observation.ObservationPredicate;
import io.micrometer.tracing.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Peter
 * @date 2023/3/16
 * @description
 */
@Configuration
public class ObservationConfig {



    // This adds the http.url keyvalue to security observations from the root (mvc) observation
    // You add an ignoreSpan=true keyValue instead if you want, or something that can signal to the SpanExportingPredicate what to ignore
//    @Bean
//    ObservationFilter urlObservationFilter() {
//        return context -> {
//            if (context.getName().startsWith("spring.security.")) {
//                Observation.Context root = getRoot(context);
//                if (root.getName().equals("http.server.requests")) {
//                    context.addHighCardinalityKeyValue(Objects.requireNonNull(root.getHighCardinalityKeyValue("http.url")));
//                }
//            }
//
//            return context;
//        };
//    }
//
//    private Observation.Context getRoot(Observation.Context context) {
//        ObservationView parentObservation = context.getParentObservation();
//        if (parentObservation == null) {
//            return context;
//        }
//        else {
//            return getRoot((Observation.Context) parentObservation.getContextView());
//        }
//    }
//
//    // This ignores actuator spans but its logic depends on the ObservationFilter above
//    // Auto-configuration for SpanExportingPredicate was added in 3.1.0-M1
//    // So either you use 3.1.x or you can add the same to your config : https://github.com/spring-projects/spring-boot/pull/34002
//    @Bean
//    SpanExportingPredicate actuatorSpanExportingPredicate() {
//        return span -> !span.getTags().get("http.url").startsWith("/actuator");
//    }

//    @Bean
//    public ObservationPredicate actuatorClientContextPredicate() {
//        return (name, context) -> {
//            if (name.equals("http.client.requests") && context instanceof FeignContext feignContext) {
//                return !feignContext.getCarrier().url().endsWith("/actuator/health");
//            }
//            else {
//                return true;
//            }
//        };
//    }

    @Bean
    public ObservationPredicate actuatorServerContextPredicate() {
        return (name, context) -> {
            if (name.equals("http.server.requests") &&
                    context instanceof org.springframework.http.server.reactive.observation.ServerRequestObservationContext reactiveServerContext) {
                return !reactiveServerContext.getCarrier().getPath().value().startsWith("/actuator");
            } else if (name.equals("http.server.requests") && context instanceof org.springframework.http.server.observation.ServerRequestObservationContext serverContext) {
                return !serverContext.getCarrier().getRequestURI().startsWith("/actuator");
            } else {
                return true;
            }
        };
    }




}
