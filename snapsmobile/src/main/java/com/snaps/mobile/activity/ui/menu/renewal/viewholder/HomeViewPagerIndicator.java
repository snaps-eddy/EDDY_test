package com.snaps.mobile.activity.ui.menu.renewal.viewholder;

import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.snaps.mobile.activity.ui.menu.renewal.viewpager.HomeViewPagerAdapter;

/**
 * Created by songhw on 2016. 8. 3..
 */
public class HomeViewPagerIndicator implements RecyclerViewPager.OnPageChangedListener {
    private final int selectedColor = 0x99191919;
    private final int normalColor = 0x33191919;

    private FrameLayout parentLayout;
    private HomeViewPagerAdapter adapter;
    private ImageView[] indicatorList;

    private boolean expanded = false;

    /**
     *
     * @param parentLayout
     * @param adapter
     * @param expanded
     */
    public HomeViewPagerIndicator( FrameLayout parentLayout, HomeViewPagerAdapter adapter, boolean expanded) {
        this.parentLayout = parentLayout;
        this.adapter = adapter;
        this.expanded = expanded;
    }

    public void init( int layoutW, int pagerY, int pagerH, int screenW, String align ) {
        LinearLayout indicatorLayout = new LinearLayout( parentLayout.getContext() );
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams( FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT );
        params.topMargin = (int)( (float)(pagerY + pagerH - 28f) / (float)layoutW * (float)screenW );
        if( "right".equalsIgnoreCase(align) ) params.rightMargin = (int)( 28f / (float)layoutW * (float)screenW );
        else if( "left".equalsIgnoreCase(align) ) params.leftMargin = (int)( 28f / (float)layoutW * (float)screenW );
        indicatorLayout.setLayoutParams(params);
        indicatorLayout.setOrientation(LinearLayout.HORIZONTAL);
        indicatorLayout.setGravity("right".equalsIgnoreCase(align) ? Gravity.RIGHT : "left".equalsIgnoreCase(align) ? Gravity.LEFT : Gravity.CENTER_HORIZONTAL);

        int size = (int)( 8f / (float)layoutW * (float)screenW );
        int margin = (int)( 3f / (float)layoutW * (float)screenW );
        indicatorList = new ImageView[expanded ? adapter.getItemCount() / 2 : adapter.getItemCount()];
        for( int i = 0; i < indicatorList.length; ++i ) {
            indicatorList[i] = createIndicatorView(size, margin);
            indicatorLayout.addView( indicatorList[i] );
        }
        parentLayout.addView(indicatorLayout);
        selectView(0, 0);
    }

    private ImageView createIndicatorView( int size, int margin ) {
        ImageView v = new ImageView( parentLayout.getContext() );
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( size, size );
        params.leftMargin = margin;
        params.rightMargin = margin;
        v.setLayoutParams( params );
        v.setImageDrawable( new ColorDrawable(normalColor) );

        return v;
    }

    private void selectView( int before, int after ) {
        if( indicatorList == null || before < 0 || before > indicatorList.length - 1 || after < 0 || after > indicatorList.length - 1 ) return;

        indicatorList[before].setImageDrawable( new ColorDrawable(normalColor) );
        indicatorList[after].setImageDrawable( new ColorDrawable(selectedColor) );
    }

    @Override
    public void OnPageChanged(int before, int after) {
        if( indicatorList == null || indicatorList.length < 1 ) return;

        selectView(before < 0 ? 0 : before % indicatorList.length, after % indicatorList.length);
    }
}
