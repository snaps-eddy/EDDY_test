package com.snaps.mobile.activity.selectimage.helper;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by ifunbae on 16. 4. 20..
 */
public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

    //단위는 dp로
    int leftOffset = 0;
    int topOffset = 0;
    int rightOffset = 0;
    int bottomOffset = 0;
    boolean isGridView = false;


    public ItemOffsetDecoration(Context context, int[] offset ) {

        leftOffset = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, offset[0], context.getResources().getDisplayMetrics());
        topOffset = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, offset[1], context.getResources().getDisplayMetrics());
        rightOffset = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, offset[2], context.getResources().getDisplayMetrics());
        bottomOffset = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, offset[3], context.getResources().getDisplayMetrics());

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);

        if(position == 0){
            outRect.set(leftOffset, topOffset, leftOffset, bottomOffset);
        }else
            outRect.set(rightOffset, topOffset, leftOffset, bottomOffset);
    }
}
