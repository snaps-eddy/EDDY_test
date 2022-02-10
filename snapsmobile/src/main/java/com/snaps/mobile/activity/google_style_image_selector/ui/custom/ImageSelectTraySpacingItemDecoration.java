package com.snaps.mobile.activity.google_style_image_selector.ui.custom;

/**
 * Created by ysjeong on 16. 4. 19..
 */

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies.ImageSelectUIProcessorStrategyFactory;

public class ImageSelectTraySpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spaceRight;
    private ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE uiType;

    public ImageSelectTraySpacingItemDecoration(Context context, ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE uiType) {
        spaceRight = UIUtil.convertDPtoPX(context, 8);
        this.uiType = uiType;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position

        if (position == 0) {
            if (outRect.left != spaceRight)
                outRect.left = spaceRight;
        }

        if (outRect.right != spaceRight)
            outRect.right = spaceRight;
    }

}
