package com.sitecenter.notification.service.events;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class BlockCallerExecutionPolicy implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            log.debug("waiting for queue...");
            // based on the BlockingQueue documentation below should block until able to place on the queue...
            executor.getQueue().put(r);
        }
        catch (InterruptedException e) {
            throw new RejectedExecutionException("Unexpected InterruptedException while waiting to add Runnable to ThreadPoolExecutor queue...", e);
        }
    }
}