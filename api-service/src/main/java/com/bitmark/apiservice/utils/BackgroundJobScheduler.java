/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.apiservice.utils;

import java.util.Queue;
import java.util.concurrent.*;

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
