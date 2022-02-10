package com.snaps.mobile.edit_activity_tools.adapter;

import android.view.View;

/**
 * Created by kimduckwon on 2017. 11. 6..
 */

public class NewYearsCardActivityThumbnailHolder extends CardShapeActivityThumbnailHolder {

    public NewYearsCardActivityThumbnailHolder(View itemView) {
        super(itemView);
        rightThumbnailView.getRootLayout().setVisibility(View.GONE);
        thumbnailDivideLine.setVisibility(View.GONE);
        counterView.setVisibility(View.VISIBLE);
    }

}
