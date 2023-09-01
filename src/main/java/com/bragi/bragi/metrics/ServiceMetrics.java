package com.bragi.bragi.metrics;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.Callable;

@Component
@RequiredArgsConstructor
public class ServiceMetrics {

    private final ServiceMetricsBuilder serviceMetricsBuilder;

    public void incrementErrorCount(String grpcMethod){
        serviceMetricsBuilder.getGrpcErrorCounter()
                .labels(grpcMethod)
                .inc();
    }

    public void recordLatency(String grpcMethod, double latency){
        serviceMetricsBuilder.getGrpcLatencyHistogram()
                .labels(grpcMethod)
                .observe(latency);
    }

    public void recordLatency(String grpcMethod, Runnable runnable){
        serviceMetricsBuilder.getGrpcLatencyHistogram()
                .labels(grpcMethod)
                .time(runnable);
    }

    public <V> void recordLatency(String grpcMethod, Callable<V> callable){
        serviceMetricsBuilder.getGrpcLatencyHistogram()
                .labels(grpcMethod)
                .time(callable);
    }


    public void recordRestLatency(String method, long latency) {
        serviceMetricsBuilder.getRestLatencyHistogram()
                .labels(method)
                .observe(latency);
    }

    public void incrementRestErrorCount(String method) {
        serviceMetricsBuilder.getRestErrorCounter()
                .labels(method)
                .inc();
    }
}
