package com.bragi.bragi.retry;

import io.grpc.StatusRuntimeException;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class Retry {

    private static final int MIN_WAIT = 20;
    private static final int MAX_WAIT = 200;
    private static final int ATTEMPTS = 3;
    @Getter
    protected Predicate<Throwable> predicate;

    @Getter
    @Setter
    protected int retryAttempts;

    @Getter
    protected Function<Throwable, RuntimeException> onErrorMapper;

    @Setter
    @Getter
    protected Throwable error;

    protected final RetryStrategy retryStrategy;

    @Getter
    private Consumer<Retry> beforeRetry;

    @Getter
    private Consumer<Retry> onRetry;

    @Getter
    private long minWaitBetweenRetry;

    @Getter
    private long maxWaitBetweenRetry;

    @Setter
    @Getter
    private int attemptNumber;


    public abstract static class RetryBuilder {
        private Predicate<Throwable> predicate = (throwable -> true);
        private int retryAttempts = ATTEMPTS;

        private long minWaitBetweenRetry = MIN_WAIT;

        private long maxWaitBetweenRetry = MAX_WAIT;

        private Function<Throwable, RuntimeException> onErrorMapper = (throwable -> new RuntimeException());

        private Consumer<Retry> beforeRetry = (r)->{};

        private Consumer<Retry> onRetry = (r) ->{};

        protected RetryBuilder(){
        }

        public RetryBuilder setPredicate(Predicate<Throwable> predicate){
            this.predicate = predicate;
            return this;
        }

        public RetryBuilder setOnCompleteFunction(Function<Throwable, RuntimeException> onCompleteFunction){
            this.onErrorMapper = onCompleteFunction;
            return this;
        }

        public RetryBuilder setRetryAttempts(int retryAttempts){
            this.retryAttempts = retryAttempts;
            return this;
        }

        public RetryBuilder setMinWait(long minWait){
            this.minWaitBetweenRetry = minWait;
            return this;
        }

        public RetryBuilder setMaxWait(long maxWait){
            this.maxWaitBetweenRetry = maxWait;
            return this;
        }

        public RetryBuilder setOnBefore(Consumer<Retry> onBefore){
            this.beforeRetry = onBefore;
            return this;
        }


        public RetryBuilder setOnRetry(Consumer<Retry> onRetry){
            this.onRetry = onRetry;
            return this;
        }
        public abstract Retry build();

    }


    protected Retry(RetryBuilder builder){
        this.onErrorMapper = builder.onErrorMapper;
        this.retryAttempts = builder.retryAttempts;
        this.predicate = builder.predicate;
        this.maxWaitBetweenRetry = builder.maxWaitBetweenRetry;
        this.minWaitBetweenRetry = builder.minWaitBetweenRetry;
        this.onRetry = builder.onRetry;
        this.beforeRetry = builder.beforeRetry;
        retryStrategy = new RetryStrategy(this);

    }

    public static <V> CallableRetry.CallableRetryBuilder<V> fromCallable(Callable<V> callable){
        return new CallableRetry.CallableRetryBuilder<>(callable);
    }

    public static RunnableRetry.RunnableRetryBuilder fromRunnable(Runnable runnable){
        return new RunnableRetry.RunnableRetryBuilder(runnable);
    }

    protected abstract void retry() throws Exception;
    public abstract Retry subscribe();

    public abstract <V> V getResult();
}
