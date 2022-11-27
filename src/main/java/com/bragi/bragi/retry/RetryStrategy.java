package com.bragi.bragi.retry;

import java.util.concurrent.*;

class RetryStrategy {

    private final Retry retry;

    RetryStrategy(Retry retry){
        this.retry = retry;
    }

    public Retry executeAndRetry() {
        var retryAttempts = retry.getRetryAttempts();
        Throwable error;
        retry.getBeforeRetry().accept(retry);
        do {
            retry.setAttemptNumber(retry.getAttemptNumber() + 1);
            retryAttempts--;
            try{
                retry.getOnRetry().accept(retry);
                retry.retry();
                return retry;
            }catch (Exception e){
                error = e;
            }
            var boundedTime = ThreadLocalRandom.current().nextLong(retry.getMinWaitBetweenRetry(),
                    retry.getMaxWaitBetweenRetry());
            var scheduler = Executors.newSingleThreadScheduledExecutor();
            try {
                scheduler.schedule(()->{
                        }, boundedTime, TimeUnit.MILLISECONDS)
                        .get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }while(retryAttempts != 0 && retry.getPredicate().test(error));
        throw retry.getOnErrorMapper().apply(error);
    }
}
