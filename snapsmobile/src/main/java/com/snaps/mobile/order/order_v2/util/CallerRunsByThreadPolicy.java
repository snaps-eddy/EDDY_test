package com.snaps.mobile.order.order_v2.util;

import com.snaps.common.utils.log.Dlog;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import errorhandle.logger.Logg;

/**
 * Created by ysjeong on 2018. 3. 2..
 */

public class CallerRunsByThreadPolicy implements RejectedExecutionHandler {
    private static final String TAG = CallerRunsByThreadPolicy.class.getSimpleName();
    private int maxThreadPoolSize = 0;
    public CallerRunsByThreadPolicy(int maxThreadPoolSize) {
        this.maxThreadPoolSize = maxThreadPoolSize;
    }

    /**
     * Executes task r in the caller's thread, unless the executor
     * has been shut down, in which case the task is discarded.
     *
     * @param r the runnable task requested to be executed
     * @param e the executor attempting to execute this task
     */
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        if (!e.isShutdown()) {
            int count = 0;
            while (e.getActiveCount() >= maxThreadPoolSize) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    Dlog.e(TAG, e1);
                }
                Dlog.d("rejectedExecution() count:" + count);
                if (++count > 10) break;
            }

            if (e.getActiveCount() < maxThreadPoolSize)
                e.execute(r);
            else
                ;//discard;
        }
    }
}
