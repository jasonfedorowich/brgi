package com.bragi.bragi.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@ConfigurationProperties(value = "${db.retry.properties}")
@Getter
@Setter
@Validated
public class RetryConfig {

    @Max(5)
    @Min(1)
    int maxAttempts;

    @Min(1)
    @Max(1000)
    long minWaitBetweenMillis;

    @Min(1)
    @Max(10000)
    long maxWaitBetweenMillis;
}
