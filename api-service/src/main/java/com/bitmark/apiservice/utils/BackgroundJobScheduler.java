package com.bitmark.apiservice.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Hieu Pham
 * @since 9/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class BackgroundJobScheduler {

    private static final int MAX_POOL_SIZE = 10;

    private static final int ALIVE_TIME = 60;

    private static final TimeUnit ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private static final ThreadFactory THREAD_FACTORY;

    private static final ThreadPoolExecutor executor;

    static {
        THREAD_FACTORY = new PriorityThreadFactory(Thread.NORM_PRIORITY);
        executor = new ThreadPoolExecutor(0, MAX_POOL_SIZE, ALIVE_TIME, ALIVE_TIME_UNIT,
                new LinkedBlockingQueue<>(), THREAD_FACTORY);
    }

    public void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    public void shutdown() {
        if (executor.isShutdown() || executor.isTerminating() || executor.isTerminated()) return;
        executor.shutdown();
    }

    static final class PriorityThreadFactory implements ThreadFactory {

        private final int priority;

        PriorityThreadFactory(int priority) {
            this.priority = priority;
        }

        @Override
        public Thread newThread(Runnable r) {
            final Thread thread = new Thread(r);
            thread.setPriority(priority);
            return thread;
        }
    }
}
