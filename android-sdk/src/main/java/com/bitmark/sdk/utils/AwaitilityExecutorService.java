/**
 * SPDX-License-Identifier: ISC
 * Copyright © 2014-2019 Bitmark. All rights reserved.
 * Use of this source code is governed by an ISC
 * license that can be found in the LICENSE file.
 */
package com.bitmark.sdk.utils;

import com.bitmark.sdk.utils.error.TaskExecuteException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class AwaitilityExecutorService {

    private final ThreadPoolExecutor executor;

    private final AtomicBoolean processing = new AtomicBoolean(false);

    public AwaitilityExecutorService() {
        this(5);
    }

    public AwaitilityExecutorService(int concurrentTaskCount) {
        executor = new ThreadPoolExecutor(
                1,
                concurrentTaskCount,
                30,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                (ThreadFactory) Thread::new
        );
    }

    public <T, R> R execute(
            Task<T, R> task,
            int timeoutSec,
            boolean shutdownWhenDone
    )
            throws InterruptedException {
        List<R> result = execute(new ArrayList<Task<T, R>>() {{
            add(task);
        }}, timeoutSec, shutdownWhenDone);
        return result.isEmpty() ? null : result.get(0);
    }

    public <T, R> List<R> execute(
            List<Task<T, R>> tasks,
            int timeoutSec,
            boolean shutdownWhenDone
    )
            throws InterruptedException {
        if (processing.get()) {
            throw new IllegalStateException("the executor is busy to do more");
        }
        CountDownLatch latch = new CountDownLatch(tasks.size());
        List<R> result = new ArrayList<>();
        for (Task<T, R> t : tasks) {
            executor.execute(() -> {
                try {
                    result.add(t.run());
                    latch.countDown();
                } catch (Throwable e) {
                    latch.countDown();
                    throw new TaskExecuteException(e);
                }
            });
        }
        latch.await(timeoutSec, TimeUnit.SECONDS);
        processing.set(false);
        if (shutdownWhenDone) {
            shutdown();
        }
        return result;
    }

    public void shutdown() {
        if (!executor.isShutdown() && !executor.isTerminating() && !executor.isTerminated()) {
            executor.shutdown();
        }
    }

    public void shutdownAndWaitForTerminating(int timeoutSec)
            throws InterruptedException {
        shutdown();
        executor.awaitTermination(timeoutSec, TimeUnit.SECONDS);
    }
}
