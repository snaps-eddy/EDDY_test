package com.snaps.mobile.activity.ui.menu.renewal.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.activity.ui.menu.renewal.model.Layout;
import com.snaps.mobile.activity.ui.menu.renewal.model.Value;

/**
 * Created by songhw on 2016. 8. 2..
 */
public class HomeMenuViewHolder extends RecyclerView.ViewHolder {
    public FrameLayout rootView;
    private Layout layout;
    private Value value;
    private int layoutWidth, pageSpace;
    private int mPosition; // LoopRecyclerViewPager 라이브러리때문에 지우면 안됨.
    private int position = -1;
    private boolean isHomeItem;



    public HomeMenuViewHolder( FrameLayout rootView, boolean isHomeItem ) {
        super(rootView);
        this.rootView = rootView;
        this.isHomeItem = isHomeItem;
    }

    /**
     * 초기화
     */
    public void init( int position, Layout layout, Value value, String subValueId, int[] pageControlRect, int pageControlRealWidth, int pageSpace, boolean needLeftMargin, String targetUrl ) {
        this.position = position;
        this.layout = layout;
        this.value = value == null ? null : value.getSubValue( subValueId );
        this.pageSpace = pageSpace;

        if( !StringUtil.isEmpty(targetUrl) && value != null ) {
            final String clickDataKey = "dynamicClickKey";
            layout.setClick(clickDataKey);
            this.value.addData(clickDataKey, targetUrl);
        }

        int width = MenuDataManager.getScaledValue(rootView.getContext(), layout.getSize()[0], pageControlRealWidth);
        int height = MenuDataManager.getScaledValue( rootView.getContext(), layout.getSize()[1], pageControlRealWidth );
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( width, height );
        if( needLeftMargin ) params.leftMargin = MenuDataManager.getScaledValue( rootView.getContext(), pageSpace, pageControlRealWidth );
        rootView.setLayoutParams(params);

        this.layoutWidth = width;
    }

    public void drawView() {
        MenuDataManager menuDataManager = MenuDataManager.getInstance();
        if( rootView != null && menuDataManager != null ) {
            rootView.removeAllViews();
            MenuDataManager.getInstance().drawMenuLayout(rootView.getContext(), rootView, layout, value, layoutWidth, pageSpace, isHomeItem, layout.getClick());
        }
    }

    public int getPagePosition() { return this.position; }
}
