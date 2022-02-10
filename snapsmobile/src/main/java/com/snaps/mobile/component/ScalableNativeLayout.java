package com.snaps.mobile.component;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.activity.ui.menu.renewal.model.Menu;
import com.snaps.mobile.interfaces.OnPageScrollListener;

import java.util.ArrayList;

public class ScalableNativeLayout extends FrameLayoutForScrollObserve {
	private final static int MENU_DISTANCE = 0;

    private LinearLayout layout;

    private boolean isHomeItem = false;

	public ScalableNativeLayout(Context context) {
		super(context);
	}

	public ScalableNativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScalableNativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	private void drawItems(ArrayList<Menu> menus) {
		MenuDataManager menuDataManager = MenuDataManager.getInstance();
		if(menus == null || layout == null || menuDataManager == null ) return;

        Menu menu = null;
		for(int i = 0; i < menus.size(); i++) {
            if( isHome() && i == menuDataManager.getCrmIdx() ) menu = StringUtil.isEmpty( menuDataManager.getUserNo() ) ? menuDataManager.getMenuCrmLogout() : menuDataManager.getMenuCrmLogin();
			else menu = menus.get( i );

            FrameLayout menuLayout = new FrameLayout( context );
            menuDataManager.drawMenuLayout(context, menuLayout, menu, screenW, MENU_DISTANCE, isHomeItem);
			layout.addView( menuLayout );
		}
	}
	
	public void initUI( ArrayList<Menu> menu, boolean isHomeItem ) {
        this.isHomeItem = isHomeItem;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View v = inflater.inflate(R.layout.custom_snaps_native_main_scrollview, null);

		scrollViewLayout = (ObserveScrollingScrollView) v.findViewById(R.id.snaps_native_main_scrollview);
		scrollViewLayout.setOnScrollListener(new OnPageScrollListener() {
			@Override
				public boolean onScrollChanged(int l, int t, int oldl, int oldt) {
					if( pageScrollListener != null ) pageScrollListener.onScrollChanged(0, t, 0, oldt);
					return false;	
				}

            @Override
            public boolean onScrollChanged(int dx, int dy) {
                return false;
            }
        });

		scrollViewLayout.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (onStickyScrollTouchListener != null)
					onStickyScrollTouchListener.onStickyScrollTouch(event, scrollViewLayout);
				return false;
			}
		});

		layout = (LinearLayout) v.findViewById(R.id.snaps_native_main_linearlayout);
        layout.setPadding( layout.getPaddingLeft(), getDefaultTopMargin(), layout.getPaddingRight(), layout.getPaddingBottom() );
		
		if(layout.getChildCount() > 0)
			layout.removeAllViews();
		
		drawItems(menu);
				
		addView(v);

		setInitialized( true );
	}

    public void replaceItem( final Menu menu, final int position ) {
        final MenuDataManager menuDataManager = MenuDataManager.getInstance();
        if(menu == null || layout == null || menuDataManager == null ) return;

        final FrameLayout menuLayout = new FrameLayout( context );
        ( (Activity) context ).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                menuDataManager.drawMenuLayout(context, menuLayout, menu, screenW, MENU_DISTANCE, isHomeItem);
                layout.removeViewAt(position);
                layout.addView(menuLayout, position);
            }
        });
    }

    public boolean isHome() { return this.isHomeItem; }
}
