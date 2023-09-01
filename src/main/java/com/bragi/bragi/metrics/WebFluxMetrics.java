package com.bragi.bragi.metrics;

import java.util.Arrays;

public class WebFluxMetrics {

    public static void recordMetric(ServiceMetrics serviceMetrics, String method, long latency){
        serviceMetrics.recordRestLatency(method, latency);
    }

    public static void recordError(ServiceMetrics serviceMetrics, String method){
        serviceMetrics.incrementRestErrorCount(method);
    }
}
