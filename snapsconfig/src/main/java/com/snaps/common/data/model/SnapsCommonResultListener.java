package com.snaps.common.data.model;

/**
 * Created by ysjeong on 2018. 4. 19..
 */

public abstract class SnapsCommonResultListener<T> {
    public abstract void onResult(T t);

    public void onPrepare() {}

    public void onException(Exception e) {}
}
