package com.snaps.mobile.activity.home.model;

import android.content.Intent;

import com.snaps.mobile.activity.home.ui_strategies.HomeUIHandler;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class HomeIntentHandleData {
    private Intent intent;
    private HomeUIHandler homeUIHandler;

    private HomeIntentHandleData(Builder builder) {
        this.intent = builder.intent;
        this.homeUIHandler = builder.homeUIHandler;
    }

    public Intent getIntent() {
        return intent;
    }

    public HomeUIHandler getHomeUIHandler() {
        return homeUIHandler;
    }

    public static class Builder {
        private Intent intent;
        private HomeUIHandler homeUIHandler;

        public Builder setIntent(Intent intent) {
            this.intent = intent;
            return this;
        }

        public Builder setHomeUIHandler(HomeUIHandler homeUIHandler) {
            this.homeUIHandler = homeUIHandler;
            return this;
        }

        public HomeIntentHandleData create() {
            return new HomeIntentHandleData(this);
        }
    }
}
