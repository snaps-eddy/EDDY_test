package com.snaps.mobile.utils.sns.googlephoto.model;

import com.snaps.common.utils.ui.IImageData;

import java.util.ArrayList;

public class GooglePhotoImageListModel {

    private String nextKey = null;

    private ArrayList<IImageData> list = null;

    public String getNextKey() {
        return nextKey;
    }

    public void setNextKey(String nextKey) {
        this.nextKey = nextKey;
    }

    public ArrayList<IImageData> getList() {
        return list;
    }

    public void setList(ArrayList<IImageData> list) {
        this.list = list;
    }
}
