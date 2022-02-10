package com.snaps.mobile.activity.google_style_image_selector.interfaces;

import com.snaps.common.utils.ui.IAlbumData;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2016. 12. 7..
 */

public interface IImageSelectGetAlbumListListener {
    void onPreprare();
    void onCreatedAlbumList(ArrayList<IAlbumData> list);
}
