package com.bitmark.apiservice.utils;

import java.util.Queue;
import java.util.concurrent.*;

/**
 * @author Hieu Pham
 * @since 9/6/18
 * Email: hieupham@bitmark.com
 * Copyright © 2018 Bitmark. All rights reserved.
 */

public class BackgroundJobScheduler {

    private final ThreadPoolExecutor executorService;

    public BackgroundJobScheduler() {
        this(5);
    }

    public BackgroundJobScheduler(int threadCount) {
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
        executorService = new ThreadPoolExecutor(
                1,
                threadCount,
                30,
                TimeUnit.SECONDS,
                taskQueue,
                new BackgroundThreadFactory()
        );
    }

    public void execute(Runnable runnable) {
        executorService.execute(runnable);
    }

    public void shutdown() {
        if (executorService.isShutdown() || executorService.isTerminated()) {
            return;
        }
        executorService.shutdown();
    }

    public int getActiveTaskCount() {
        return executorService.getActiveCount();
    }

    public Queue<Runnable> getTaskQueue() {
        return executorService.getQueue();
    }

    private static final class BackgroundThreadFactory
            implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r);
        }
    }
}
