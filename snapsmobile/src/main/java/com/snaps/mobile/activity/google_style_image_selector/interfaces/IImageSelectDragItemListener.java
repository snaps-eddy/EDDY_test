package com.snaps.mobile.activity.google_style_image_selector.interfaces;

import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;

/**
 * Created by kimduckwon on 2018. 5. 18..
 */

public interface IImageSelectDragItemListener {
     int FIRST_ITEM = 0;
     int DRAG_ITEM = 1;
     int LAST_ITEM = 2;

    void onDragItem(ImageSelectAdapterHolders.PhotoFragmentItemHolder holder,int type);

    void onDragItemEmpty(int type);
}
