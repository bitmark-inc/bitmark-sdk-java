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

    private static int NUMBER_OF_CORES = 5;

    private static final int KEEP_ALIVE_TIME = 30;

    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private final ThreadPoolExecutor executorService;

    private static volatile BackgroundJobScheduler INSTANCE;

    public static BackgroundJobScheduler getInstance() {
        if (INSTANCE == null) {
            synchronized (BackgroundJobScheduler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BackgroundJobScheduler();
                }
            }
        }
        return INSTANCE;
    }

    private BackgroundJobScheduler() {
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
        executorService = new ThreadPoolExecutor(
                NUMBER_OF_CORES,
                NUMBER_OF_CORES * 2,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
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
