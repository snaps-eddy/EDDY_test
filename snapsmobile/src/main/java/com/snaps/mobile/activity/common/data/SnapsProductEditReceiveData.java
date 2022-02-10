package com.snaps.mobile.activity.common.data;

import android.content.Intent;

import com.snaps.common.structure.control.SnapsControl;

/**
 * Created by ysjeong on 2017. 10. 18..
 */

public class SnapsProductEditReceiveData {
    private Intent intent;
    private SnapsControl snapsControl;

    public static SnapsProductEditReceiveData createReceiveData(Intent intent, SnapsControl control) {
        SnapsProductEditReceiveData receiveData = new SnapsProductEditReceiveData();
        receiveData.setIntent(intent);
        receiveData.setSnapsControl(control);
        return receiveData;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public SnapsControl getSnapsControl() {
        return snapsControl;
    }

    public void setSnapsControl(SnapsControl snapsControl) {
        this.snapsControl = snapsControl;
    }
}
