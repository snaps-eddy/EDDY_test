package com.snaps.mobile.activity.home.utils.push_handlers;

import android.app.Activity;

import java.util.LinkedList;

/**
 * Created by ysjeong on 2017. 8. 24..
 */

public class SnapsPushHandler implements ISnapsPushHandlerIterator {

    private LinkedList<ISnapsPushHandler> pushHandlers;

    public static SnapsPushHandler createInstanceWithIntent(Activity activity, SnapsPushHandleData pushHandleData) {
        SnapsPushHandler pushHandler = new SnapsPushHandler();
        pushHandler.initialize(activity, pushHandleData);
        return pushHandler;
    }

    @Override
    public void initialize(Activity activity, SnapsPushHandleData pushHandleData) {
        if (pushHandlers == null) pushHandlers = new LinkedList<>();
        pushHandlers.clear();
        pushHandlers.add(new SnapsPushHandlerForLogin(activity, pushHandleData));
        pushHandlers.add(new SnapsPushHandlerForGoCart(activity, pushHandleData));
        pushHandlers.add(new SnapsPushHandlerForPhotoPrintUploadImageFailed(activity, pushHandleData));
        pushHandlers.add(new SnapsPushHandlerForKakaoEvent(activity, pushHandleData));
        pushHandlers.add(new SnapsPushHandlerForCMD(activity, pushHandleData));
    }

    @Override
    public boolean hasNext() {
        return pushHandlers != null && !pushHandlers.isEmpty();
    }

    @Override
    public ISnapsPushHandler next() {
        return pushHandlers == null || pushHandlers.isEmpty() ? null : pushHandlers.poll();
    }

    @Override
    public void releaseInstance() {
        if (pushHandlers != null && !pushHandlers.isEmpty()) {
            pushHandlers.clear();
            pushHandlers = null;
        }
    }
}
