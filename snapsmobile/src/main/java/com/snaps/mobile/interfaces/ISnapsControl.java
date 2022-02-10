package com.snaps.mobile.interfaces;

import android.view.View;

import com.snaps.common.structure.control.SnapsControl;

/**
 * Created by ifunbae on 2017. 1. 18..
 */

/***
 * setTag getTag를 없애기 위해 만듦
 */
public interface ISnapsControl {
    SnapsControl getSnapsControl();

    void setSnapsControl(SnapsControl snapsControl);

    View getView();
}
