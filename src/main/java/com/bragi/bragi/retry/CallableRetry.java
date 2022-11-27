package com.bragi.bragi.retry;

import java.util.concurrent.Callable;

public class CallableRetry<V> extends Retry{

    private Callable<V> callable;
    private V result;

    public static class CallableRetryBuilder<V> extends RetryBuilder{
        private Callable<V> callable;
        protected CallableRetryBuilder(Callable<V> callable){
            this.callable = callable;
        }

        @Override
        public Retry build() {
            return new CallableRetry<>(this);
        }
    }

    public CallableRetry(CallableRetryBuilder<V> builder) {
        super(builder);
        this.callable = builder.callable;

    }

    @Override
    public Retry subscribe() {
        return this.retryStrategy.executeAndRetry();
    }

    @Override
    protected void retry() throws Exception {
        this.result = callable.call();
    }

    @Override
    public V getResult() {
       return this.result;
    }
}
