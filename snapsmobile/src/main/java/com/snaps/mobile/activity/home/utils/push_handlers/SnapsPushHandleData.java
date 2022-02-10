package com.snaps.mobile.activity.home.utils.push_handlers;

import android.content.Intent;

import com.snaps.mobile.activity.home.ui_strategies.HomeUIHandler;
import com.snaps.mobile.activity.home.utils.SnapsEventHandler;
import com.snaps.common.structure.SnapsHandler;

/**
 * Created by ysjeong on 2017. 8. 24..
 */

public class SnapsPushHandleData {
    private Intent intent;
    private HomeUIHandler homeUIHandler;
    private SnapsEventHandler eventHandler;
    private SnapsHandler snapsHandler;

    private SnapsPushHandleData(Builder builder) {
        this.intent = builder.intent;
        this.homeUIHandler = builder.homeUIHandler;
        this.eventHandler = builder.eventHandler;
        this.snapsHandler = builder.snapsHandler;
    }

    public Intent getIntent() {
        return intent;
    }

    public HomeUIHandler getHomeUIHandler() {
        return homeUIHandler;
    }

    public SnapsEventHandler getEventHandler() {
        return eventHandler;
    }

    public SnapsHandler getSnapsHandler() {
        return snapsHandler;
    }

    public static class Builder {
        private Intent intent;
        private HomeUIHandler homeUIHandler;
        private SnapsEventHandler eventHandler;
        private SnapsHandler snapsHandler;

        public Builder setIntent(Intent intent) {
            this.intent = intent;
            return this;
        }

        public Builder setHomeUIHandler(HomeUIHandler homeUIHandler) {
            this.homeUIHandler = homeUIHandler;
            return this;
        }

        public Builder setEventHandler(SnapsEventHandler eventHandler) {
            this.eventHandler = eventHandler;
            return this;
        }

        public Builder setSnapsHandler(SnapsHandler snapsHandler) {
            this.snapsHandler = snapsHandler;
            return this;
        }

        public SnapsPushHandleData create() {
            return new SnapsPushHandleData(this);
        }
    }
}
