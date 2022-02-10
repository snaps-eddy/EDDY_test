package com.snaps.mobile.order.order_v2.task.upload_task;

import android.app.Activity;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public abstract class SnapsOrderBaseTask {
    private SnapsOrderAttribute attribute = null;

    private SnapsOrderActivityBridge activityBridge = null;

    protected SnapsOrderBaseTask(SnapsOrderAttribute attribute) {
        this.setAttribute(attribute);
    }

    public SnapsOrderAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(SnapsOrderAttribute attribute) {
        this.attribute = attribute;
    }

    public Activity getActivity() {
        return getAttribute() != null ? getAttribute().getActivity() : null;
    }

    public boolean isEditMode() {
        return getAttribute() != null && getAttribute().isEditMode();
    }

    public ArrayList<MyPhotoSelectImageData> getImageList() {
        return getActivityBridge() != null ? getActivityBridge().getUploadImageList() : null;
    }

    public ArrayList<SnapsPage> getPageList() {
        return getAttribute() != null ? getAttribute().getPageList() : null;
    }

    public ArrayList<SnapsPage> getBackPageList() {
        return getAttribute() != null ? getAttribute().getBackPageList() : null;
    }

    public ArrayList<SnapsPage> getHiddenPageList() {
        return getAttribute() != null ? getAttribute().getHiddenPageList() : null;
    }

    public SnapsTemplate getTemplate() {
        return getAttribute() != null ? getAttribute().getSnapsTemplate() : null;
    }

    public SnapsOrderActivityBridge getActivityBridge() {
        return activityBridge;
    }

    public void setActivityBridge(SnapsOrderActivityBridge activityBridge) {
        this.activityBridge = activityBridge;
    }

    public void finalizeInstance() throws Exception {
        attribute = null;
        activityBridge = null;
    }

}


