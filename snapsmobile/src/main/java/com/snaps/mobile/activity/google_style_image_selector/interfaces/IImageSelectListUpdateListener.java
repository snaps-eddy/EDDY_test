package com.snaps.mobile.activity.google_style_image_selector.interfaces;

import com.snaps.common.utils.ui.IAlbumData;

/**
 * Created by ysjeong on 2016. 12. 7..
 */

public interface IImageSelectListUpdateListener {
    void onUpdatedPhotoList(String imageKey);
    void onChangedAlbumCursor(IAlbumData cursor);
}
