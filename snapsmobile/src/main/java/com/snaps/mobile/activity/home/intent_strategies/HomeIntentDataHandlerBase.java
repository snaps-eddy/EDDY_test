package com.snaps.mobile.activity.home.intent_strategies;

import android.app.Activity;
import android.content.Intent;

import com.snaps.mobile.activity.home.model.HomeIntentHandleData;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public abstract class HomeIntentDataHandlerBase implements HomeIntentDataImp {
    private Activity activity;
    private HomeIntentHandleData intentHandleData;

    public HomeIntentDataHandlerBase(Activity activity, HomeIntentHandleData intentHandleData) {
        this.activity = activity;
        this.intentHandleData = intentHandleData;
    }

    public Activity getActivity() {
        return activity;
    }

    public HomeIntentHandleData getIntentHandleData() {
        return intentHandleData;
    }

    public Intent getIntent() {
        return intentHandleData != null ? intentHandleData.getIntent() : null;
    }
}
