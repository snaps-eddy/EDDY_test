package com.snaps.mobile.edit_activity_tools.adapter;

import android.view.View;

/**
 * Created by ysjeong on 16. 5. 11..
 */
public class PhotoCardActivityThumbnailHolder extends CardShapeActivityThumbnailHolder {
    public PhotoCardActivityThumbnailHolder(View itemView) {
        super(itemView);

        counterView.setVisibility(View.VISIBLE);
    }
}