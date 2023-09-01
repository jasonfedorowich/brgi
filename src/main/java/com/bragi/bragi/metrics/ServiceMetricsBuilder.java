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

    private Histogram grpcLatencyHistogram;
    private Counter grpcErrorCounter;

    private Histogram restLatencyHistogram;

    private Counter restErrorCounter;



    public ServiceMetricsBuilder(CollectorRegistry collectorRegistry){
        grpcLatencyHistogram = Histogram.build()
                .name("brgi_service_latency")
                .help("Histogram for grpc latency")
                .labelNames("grpc_method")
                .create()
                .register(collectorRegistry);

        grpcErrorCounter = Counter
                .build()
                .name("brgi_service_error_counter")
                .help("Counter for grpc errors")
                .labelNames("grpc_method")
                .create()
                .register(collectorRegistry);

        restLatencyHistogram = Histogram.build()
                .name("brgi_rest_service_latency")
                .help("Histogram for rest latency")
                .labelNames("method")
                .create()
                .register(collectorRegistry);

        restErrorCounter = Counter
                .build()
                .name("brgi_rest_service_error_counter")
                .help("Counter for rest errors")
                .labelNames("method")
                .create()
                .register(collectorRegistry);



    }

}
