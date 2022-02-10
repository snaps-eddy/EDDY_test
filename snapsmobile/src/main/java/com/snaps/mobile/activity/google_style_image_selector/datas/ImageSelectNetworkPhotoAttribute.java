package com.snaps.mobile.activity.google_style_image_selector.datas;

import com.snaps.common.utils.ui.IAlbumData;

/**
 * Created by ysjeong on 2016. 12. 21..
 */

public class ImageSelectNetworkPhotoAttribute {
    int page = 0;
    private String nextKey = null;
    private IAlbumData albumCursorInfo = null;

    public IAlbumData getAlbumCursorInfo() {
        return albumCursorInfo;
    }

    public void setAlbumCursorInfo(IAlbumData albumCursorInfo) {
        this.albumCursorInfo = albumCursorInfo;
    }

    public String getNextKey() {
        return nextKey;
    }

    public void setNextKey(String nextKey) {
        this.nextKey = nextKey;
        this.page = 0;
    }

    public void addPageCount() {
        this.page++;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
