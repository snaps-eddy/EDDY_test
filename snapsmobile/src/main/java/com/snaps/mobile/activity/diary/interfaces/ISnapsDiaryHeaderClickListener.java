package com.snaps.mobile.activity.diary.interfaces;

import android.widget.ImageView;

/**
 * Created by ysjeong on 16. 3. 31..
 */
public interface ISnapsDiaryHeaderClickListener {
    void onStripClick(int shape);
    void onThumbnailClick(ImageView targetView);
}
