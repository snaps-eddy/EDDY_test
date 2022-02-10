package com.snaps.mobile.activity.google_style_image_selector.interfaces;

import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items.ImageSelectTrayCellItem;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2016. 12. 12..
 */

public interface IImageSelectTrayAllView {
    void setTrayAllViewList(ArrayList<ImageSelectTrayCellItem> allViewList, int defaultSelectedId);
}
