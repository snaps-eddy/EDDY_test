package com.snaps.common.utils.log;

/**
 * Created by ysjeong on 2017. 11. 20..
 */

public interface SnapsInterfaceLogListener {
    void onSnapsInterfacePreRequest(String url);
    void onSnapsInterfaceResult(int httpResponseStatusCode, String responseText);
    void onSnapsInterfaceException(Exception e);
}
