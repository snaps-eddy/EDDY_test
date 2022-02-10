package com.snaps.common.structure;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

import com.snaps.common.utils.ISnapsHandler;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

/**
 * Created by ysjeong on 16. 5. 13..
 */
public class SnapsHandler extends Handler {
    public interface MainThreadHandleImp {
        void handle();
    }

    private WeakReference<ISnapsHandler> mHandler = null;

    private LinkedList<SparseArray<?>> attributeList = null;

    public static void handleOnMainThread(final MainThreadHandleImp handler) {
        Handler mainLooper = new Handler(Looper.getMainLooper());
        mainLooper.post(new Runnable() {
            @Override
            public void run() {
                if (handler != null)
                    handler.handle();
            }
        });
    }

    public SnapsHandler(ISnapsHandler activity) {
        mHandler = new WeakReference<>(activity);
        attributeList = new LinkedList<>();
    }

    public SnapsHandler(Looper looper, ISnapsHandler handler) {
        super(looper);
        mHandler = new WeakReference<>(handler);
        attributeList = new LinkedList<>();
    }

    public void releaseInstance() {
        if (attributeList != null) {
            attributeList.clear();
            attributeList = null;
        }
    }

    public void addAttributes(SparseArray<?> attributes) {
        if (attributes == null) return;

        if (attributeList != null) {
            attributeList.add(attributes.clone());
        }
    }

    public LinkedList<SparseArray<?>> getAttributeList() {
        return attributeList;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (mHandler == null) return;

        ISnapsHandler handler = mHandler.get();
        if (handler != null)
            handler.handleMessage(msg);
    }

    public void destroy() {
        if (mHandler != null) {
            mHandler.clear();
            mHandler = null;
        }
    }
}
