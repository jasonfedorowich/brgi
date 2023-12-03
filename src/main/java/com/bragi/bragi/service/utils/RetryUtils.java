package com.bragi.bragi.service.utils;


import com.bragi.bragi.retry.Retry;
import com.bragi.bragi.service.config.RetryConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

@Slf4j
public class RetryUtils {

    //todo add error for nosuchelementexception
    public static <R> R getResultFromRetry(Callable<R> callable, String messageBefore, String messageDuring,
                                    int maxAttempts,
                                    long maxWaitBetween,
                                    long minWaitBetween){
        return Retry.fromCallable(callable)
                .setPredicate((t)-> true)
                .setRetryAttempts(maxAttempts)
                .setOnCompleteFunction((t)-> new RuntimeException())
                .setMaxWait(maxWaitBetween)
                .setMinWait(minWaitBetween)
                .setOnBefore(retry -> log.info(messageBefore, retry.getRetryAttempts()))
                .setOnRetry(retry -> log.info(messageDuring, retry.getAttemptNumber()))
                .build()
                .subscribe()
                .getResult();
    }
}
