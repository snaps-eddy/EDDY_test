package com.snaps.mobile.order.order_v2.util.org_image_upload.threadpool_util;

import android.os.Process;
import androidx.annotation.NonNull;

import com.snaps.common.utils.log.Dlog;

import java.util.concurrent.ThreadFactory;

/**
 * Created by ysjeong on 2017. 4. 3..
 */
public class PriorityThreadFactory implements ThreadFactory {
    private static final String TAG = PriorityThreadFactory.class.getSimpleName();
    private final int mThreadPriority;

    public PriorityThreadFactory(int threadPriority) {
        mThreadPriority = threadPriority;
    }

    @Override
    public Thread newThread(@NonNull final Runnable runnable) {
        Runnable wrapperRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Process.setThreadPriority(mThreadPriority);
                } catch (Throwable t) {
                    Dlog.e(TAG, t);
                }
                runnable.run();
            }
        };
        return new Thread(wrapperRunnable);
    }

}