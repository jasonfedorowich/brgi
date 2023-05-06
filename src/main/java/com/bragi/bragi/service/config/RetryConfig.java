package com.bragi.bragi.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@ConfigurationProperties(prefix = "retry")
@Configuration
@Getter
@Setter
@Validated
public class RetryConfig {

    @Max(5)
    @Min(1)
    int maxAttempts = 3;

    @Min(1)
    @Max(1000)
    long minWaitBetweenMillis = 500;

    @Min(1)
    @Max(10000)
    long maxWaitBetweenMillis = 5000;
}
