package com.bragi.bragi.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class ServiceMetricsBuilder {

    private Histogram latencyHistogram;
    private Counter errorCounter;

    public ServiceMetricsBuilder(CollectorRegistry collectorRegistry){

        latencyHistogram = Histogram.build()
                .labelNames("grpc_method")
                .create()
                .register(collectorRegistry);

        errorCounter = Counter
                .build()
                .labelNames("grpc_method")
                .create()
                .register(collectorRegistry);
    }

}
