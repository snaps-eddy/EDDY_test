package com.snaps.mobile.activity.home.utils.push_handlers;

import android.app.Activity;

/**
 * Created by ysjeong on 2017. 8. 24..
 */

public interface ISnapsPushHandlerIterator {
    void initialize(Activity activity, SnapsPushHandleData pushHandleData);
    boolean hasNext();
    ISnapsPushHandler next();
    void releaseInstance();
}
