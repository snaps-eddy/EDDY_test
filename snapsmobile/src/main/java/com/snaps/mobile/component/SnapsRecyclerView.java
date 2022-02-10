package com.snaps.mobile.component;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by ifunbae on 2016. 12. 28..
 */

public class SnapsRecyclerView extends RecyclerView {
    public SnapsRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SnapsRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SnapsRecyclerView(Context context) {
        super(context);
    }

    /***
     * 롱클릭 가능 여부 설정
     */
    public void enableLongClick(boolean isEnable){

    }
}
