package com.snaps.mobile.activity.common.interfacies;

import android.content.Intent;
import android.content.res.Configuration;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ysjeong on 2017. 10. 12..
 */

public interface SnapsProductEditActivityDelegate {
    void setPageThumbnail(int pageIdx, String filePath);

    void setPageThumbnailFail(int pageIdx);

    void onConfigurationChanged(Configuration newConfig);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void onBackPressed();

    void onCreate();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();

    void dispatchTouchEvent(MotionEvent ev);

    void onClick(View v);
}
