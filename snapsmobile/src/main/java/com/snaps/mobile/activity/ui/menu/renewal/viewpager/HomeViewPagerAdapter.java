package com.snaps.mobile.activity.ui.menu.renewal.viewpager;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.snaps.mobile.activity.ui.menu.renewal.viewholder.HomeMenuViewHolder;
import com.snaps.mobile.activity.ui.menu.renewal.model.Menu;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.activity.ui.menu.renewal.model.PageControl;
import com.snaps.mobile.activity.ui.menu.renewal.model.Value;

import java.util.ArrayList;

/**
 * Created by songhw on 2016. 7. 29..
 */
public class HomeViewPagerAdapter extends RecyclerView.Adapter<HomeMenuViewHolder> {
    private PageControl pageControl;
    private Value pagerData;
    private String targetUrl;
    private int layoutW, screenW, pageSpace;
    private boolean isHomeItem, doPaging, expanded;

    public HomeViewPagerAdapter( PageControl pageControl, Value pagerData, int layoutW, int screenW, int pageSpace, boolean isHomeItem, boolean doPaging, String targetUrl ) {
        this.pageControl = pageControl.clone();
        this.pagerData = pagerData;
        this.targetUrl = targetUrl;
        this.layoutW = layoutW;
        this.screenW = screenW;
        this.pageSpace = pageSpace;
        this.isHomeItem = isHomeItem;
        this.doPaging = doPaging;

        expanded = false;
        if( this.pageControl != null && this.pageControl.getPages() != null && pageControl.isInfinityPage() && this.pageControl.getPages().size() == 2 ) // 2개일때 이상동작 해결하기 위해.
            copyDummy();
    }

    private void copyDummy() {
        if( pageControl != null && pageControl.getPages() != null && pageControl.getPages().size() == 2 ) {
            pageControl.getPages().add( pageControl.getPages().get(0) );
            pageControl.getPages().add( pageControl.getPages().get(1) );
            expanded = true;
        }
    }

    @Override
    public HomeMenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout layout = new FrameLayout( parent.getContext() );
        return new HomeMenuViewHolder( layout, isHomeItem );
    }

    @Override
    public void onBindViewHolder(HomeMenuViewHolder holder, final int position) {
        ArrayList<Menu> menus = pageControl.getPages();
        if( menus != null && position < 0  && position > menus.size() - 1 ) return;



        MenuDataManager menuDataManager = MenuDataManager.getInstance();
        final Menu menu = menus.get( position );

        if( holder.getPagePosition() != position) {
            holder.init(position, menuDataManager.getLayoutMap().get(menu.getLayerId()), pagerData, menu.getDataId(), pageControl.getRect(), layoutW, pageSpace, !doPaging && position != 0, targetUrl);
            holder.drawView();
        }
    }

    @Override
    public int getItemCount() {
        return pageControl.getPages() != null ? pageControl.getPages().size() : 0;
    }

    public PageControl getPageControl() { return this.pageControl; }

    public boolean isExpanded() { return this.expanded; }
}
