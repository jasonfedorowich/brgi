package com.bragi.bragi.retry;

import java.util.concurrent.Callable;

public class RunnableRetry extends Retry{

    private Runnable runnable;


    public static class RunnableRetryBuilder extends RetryBuilder{
        private Runnable runnable;

        protected RunnableRetryBuilder(Runnable runnable){
            this.runnable = runnable;
        }

        @Override
        public Retry build() {
            return new RunnableRetry(this);
        }
    }

    public RunnableRetry(RunnableRetryBuilder builder) {
        super(builder);
        this.runnable = builder.runnable;
    }

    @Override
    protected void retry() throws Exception {
        this.runnable.run();
    }

    @Override
    public Retry subscribe() {
        return this.retryStrategy.executeAndRetry();
    }

    @Override
    public <V> V getResult() {
        return null;
    }
}
