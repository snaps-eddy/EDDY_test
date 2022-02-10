package com.snaps.mobile.activity.home.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.component.FrameLayoutForScrollObserve;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductDesignCategory;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductDesignItem;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductSizeItem;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductSizeList;
import com.snaps.mobile.product_native_ui.ui.SnapsProductListView;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsBaseProductListItem;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsProductGridShapeListItem;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsProductPriceListItem;

import java.util.ArrayList;
import java.util.List;

public class NativeRecyclerViewFragmentForMenuScrollableUI extends NativeFragmentForMenuScrollableUI {
	private ArrayList<SnapsBaseProductListItem> listItems;

	private boolean isSizeType = false;
    private boolean isSingleTab = false;
	private String viewType;
	public NativeRecyclerViewFragmentForMenuScrollableUI() {}
	public void setListItems(SnapsProductNativeUIBaseResultJson productObject, boolean isSingleTab) {
		setListItems(productObject,isSingleTab,"");
	}
	public void setListItems(SnapsProductNativeUIBaseResultJson productObject, boolean isSingleTab,String viewType) {
		if (productObject == null) return;
		this.viewType = viewType;
        this.isSingleTab = isSingleTab;
		listItems = new ArrayList<>();

		//상품 디자인 리스트
		if (productObject instanceof SnapsProductDesignCategory) {
			List<SnapsProductDesignItem> items = ((SnapsProductDesignCategory) productObject).getITEMS();
			if (items != null) {
				for (SnapsProductNativeUIBaseResultJson designItem : items) {
					if (designItem == null || !(designItem instanceof SnapsProductDesignItem)) continue;
					SnapsProductGridShapeListItem item = new SnapsProductGridShapeListItem((SnapsProductDesignItem) designItem);
					listItems.add(item);
				}

				isSizeType = false;
			}
		} else if (productObject instanceof SnapsProductSizeList) {
			List<SnapsProductSizeItem> items = ((SnapsProductSizeList) productObject).getSize();
			if (items != null) {
				for (SnapsProductNativeUIBaseResultJson sizeItem : items) {
					if (sizeItem == null || !(sizeItem instanceof SnapsProductSizeItem)) continue;
					SnapsProductPriceListItem item = new SnapsProductPriceListItem((SnapsProductSizeItem) sizeItem);
					listItems.add(item);
				}

				isSizeType = true;
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

    @Override
    public void setLayout( FrameLayoutForScrollObserve wv ) {
        snapsProductListView = (SnapsProductListView) wv;
        if( container != null && !attached ) attachView();
    }

    @Override
    public FrameLayoutForScrollObserve getLayout() {
        return snapsProductListView;
    }

    @Override
    public void attachView() {
        if( snapsProductListView == null ) return;

		container.removeAllViews();
		if( snapsProductListView.getParent() != null ) ( (ViewGroup) snapsProductListView.getParent() ).removeAllViews();
		container.addView( snapsProductListView );

		if( !snapsProductListView.isInitialized() )
            snapsProductListView.initUI(isSizeType, isSingleTab, listItems,viewType);
        else UIUtil.reloadImage( snapsProductListView );

        if( onScrollViewCreateListener != null ) {
            if( snapsProductListView.isScrollable() )
                onScrollViewCreateListener.onNativeScrollViewCreated( snapsProductListView );
            else {
                snapsProductListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (Build.VERSION.SDK_INT < 16)
                            snapsProductListView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        else
                            snapsProductListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        if (onScrollViewCreateListener != null) onScrollViewCreateListener.onNativeScrollViewCreated( snapsProductListView );
                    }
                });
            }
        }

        snapsProductListView.setOnStickyScrollTouchListener( onStickyScrollTouchListener );

		attached = true;
	}

    @Override
    public void dettachView() {
        if( snapsProductListView != null ) UIUtil.clearImage( getActivity(), snapsProductListView, true );

        if( container != null ) {
            container.removeAllViews();
            container.invalidate();
        }
        attached = false;
    }

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup v = new RelativeLayout( getContext() );
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT );
		v.setLayoutParams( params );
		this.container = v;
		if( snapsProductListView != null && !attached) attachView();
		return v;
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void adjustScroll(int scrollHeight) {}
}
