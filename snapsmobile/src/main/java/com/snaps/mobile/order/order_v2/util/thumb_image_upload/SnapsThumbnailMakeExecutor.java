package com.snaps.mobile.order.order_v2.util.thumb_image_upload;

import android.content.Context;
import android.os.Process;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.mobile.order.order_v2.util.CallerRunsByThreadPolicy;
import com.snaps.mobile.order.order_v2.util.org_image_upload.threadpool_util.PriorityThreadFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SnapsThumbnailMakeExecutor extends ThreadPoolExecutor {

    private static final int DEFAULT_CORE_POOL_SIZE = 1;
    private static final int DEFAULT_MAXIMUM_POOL_SIZE = 1;
    private static final long DEFAULT_KEEP_ALIVE_TIME = 60; //60초까지 대기
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    private static final  BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(); //Queue pool을 사용함.

    private static final ThreadFactory priorityThreadFactory = new PriorityThreadFactory(Process.THREAD_PRIORITY_DEFAULT);

    public SnapsThumbnailMakeExecutor() {
        super(DEFAULT_CORE_POOL_SIZE, DEFAULT_MAXIMUM_POOL_SIZE, DEFAULT_KEEP_ALIVE_TIME, DEFAULT_TIME_UNIT, sPoolWorkQueue, priorityThreadFactory, new CallerRunsByThreadPolicy(DEFAULT_CORE_POOL_SIZE));
    }

    public void start(Context context, MyPhotoSelectImageData imageData) {
        this.execute(SnapsThumbnailMaker.createThumbnailMakerWithImageData(context, imageData));
    }
}
