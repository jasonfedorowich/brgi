package com.bragi.bragi.metrics;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.Histogram;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

@Component
@RequiredArgsConstructor
public class ServiceMetrics {

    private final ServiceMetricsBuilder serviceMetricsBuilder;

    public void incrementErrorCount(String grpcMethod){
        serviceMetricsBuilder.getErrorCounter()
                .labels(grpcMethod)
                .inc();
    }

    public void recordLatency(String grpcMethod, double latency){
        serviceMetricsBuilder.getLatencyHistogram()
                .labels(grpcMethod)
                .observe(latency);
    }

    public void recordLatency(String grpcMethod, Runnable runnable){
        serviceMetricsBuilder.getLatencyHistogram()
                .labels(grpcMethod)
                .time(runnable);
    }

    public <V> void recordLatency(String grpcMethod, Callable<V> callable){
        serviceMetricsBuilder.getLatencyHistogram()
                .labels(grpcMethod)
                .time(callable);
    }

}
