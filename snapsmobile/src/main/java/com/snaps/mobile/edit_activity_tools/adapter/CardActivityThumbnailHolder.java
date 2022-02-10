package com.snaps.mobile.edit_activity_tools.adapter;

import android.view.View;

import com.snaps.mobile.R;

/**
 * Created by kimduckwon on 2017. 11. 27..
 */

public class CardActivityThumbnailHolder extends CardShapeActivityThumbnailHolder {
    View leftMargin;
    View rightMargin;
    public CardActivityThumbnailHolder(View itemView) {
        super(itemView);
        leftMargin = itemView.findViewById(R.id.viewLeftMargin);
        rightMargin = itemView.findViewById(R.id.viewRightMargin);
        counterView.setVisibility(View.VISIBLE);
    }
}
