package com.bragi.bragi.metrics;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceMetricsTest {

    private ServiceMetrics serviceMetrics;

    @Mock
    private ServiceMetricsBuilder serviceMetricsBuilder;

    @BeforeEach
    void setUp() {
        serviceMetrics = new ServiceMetrics(serviceMetricsBuilder);
    }

    @Test
    void when_incrementErrorCount_success_thenReturns() {
        Counter counter = mock(Counter.class);
        Counter.Child child = mock(Counter.Child.class);

        when(counter.labels(anyString())).thenReturn(child);


        when(serviceMetricsBuilder.getGrpcErrorCounter()).thenReturn(counter);

        serviceMetrics.incrementErrorCount("hello-world");

        verify(child).inc();
    }

    @Test
    void when_recordLatency_success_thenReturns() {

        Histogram histogram = mock(Histogram.class);
        Histogram.Child child = mock(Histogram.Child.class);

        when(histogram.labels(anyString())).thenReturn(child);

        when(serviceMetricsBuilder.getGrpcLatencyHistogram()).thenReturn(histogram);

        serviceMetrics.recordLatency("hello-world", 1.0);

        verify(child).observe(1.0);

    }

    @Test
    void when_testRecordLatencyRunnable_success_thenReturns() {
        AtomicInteger i = new AtomicInteger();
        Runnable runnable = i::getAndIncrement;
        Histogram histogram = mock(Histogram.class);
        Histogram.Child child = mock(Histogram.Child.class);

        when(histogram.labels(anyString())).thenReturn(child);

        when(serviceMetricsBuilder.getGrpcLatencyHistogram()).thenReturn(histogram);

        serviceMetrics.recordLatency("hello-world", runnable);
        runnable.run();

        verify(child).time(runnable);
        assertEquals(1, i.get());

    }

    @Test
    void when_testRecordLatencyCallable_success_thenReturns() throws Exception {
        AtomicInteger i = new AtomicInteger();
        Callable<Integer> callable = i::getAndIncrement;
        Histogram histogram = mock(Histogram.class);
        Histogram.Child child = mock(Histogram.Child.class);

        when(histogram.labels(anyString())).thenReturn(child);

        when(serviceMetricsBuilder.getGrpcLatencyHistogram()).thenReturn(histogram);

        serviceMetrics.recordLatency("hello-world", callable);
        callable.call();

        verify(child).time(callable);
        assertEquals(1, i.get());
    }

    @Test
    void when_recordRestLatency_success_thenReturns(){
        Histogram histogram = mock(Histogram.class);
        Histogram.Child child = mock(Histogram.Child.class);

        when(histogram.labels(anyString())).thenReturn(child);

        when(serviceMetricsBuilder.getRestLatencyHistogram()).thenReturn(histogram);

        serviceMetrics.recordRestLatency("hello", 1000);
        verify(child).observe(1000);

    }

    @Test
    void when_incrementRestErrorCount_success_thenReturns(){
        Counter counter = mock(Counter.class);
        Counter.Child child = mock(Counter.Child.class);

        when(counter.labels(anyString())).thenReturn(child);

        when(serviceMetricsBuilder.getRestErrorCounter()).thenReturn(counter);

        serviceMetrics.incrementRestErrorCount("hello");

        verify(child).inc();

    }
}