package com.snaps.mobile.component;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.collection.SparseArrayCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.kmshack.newsstand.ScrollTabHolder;
import com.snaps.common.model.NativeProductListPage;
import com.snaps.common.model.WebViewPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.activity.home.fragment.FragmentViewPager;
import com.snaps.mobile.activity.home.fragment.NativeFragmentForMenuScrollableUI;
import com.snaps.mobile.activity.home.fragment.NativeRecyclerViewFragmentForMenuScrollableUI;
import com.snaps.mobile.activity.home.fragment.NativeScrollViewFragmentForMenuScrollableUI;
import com.snaps.mobile.activity.home.fragment.NativeWebViewFragmentForMenuScrollableUI;
import com.snaps.mobile.interfaces.OnStickyScrollTouchListener;
import com.snaps.mobile.product_native_ui.ui.SnapsProductListView;
import com.snaps.mobile.product_native_ui.util.SnapsNativeUIManager;

import java.util.ArrayList;

@SuppressLint("CommitTransaction")
public class EndlessPagerForNativeScrollViewAdapter extends EndlessPagerBaseAdapter {
	public static final int DUMMY_COUNT = 2000;

	private Activity activity;
	
	private FragmentManager fm;
	private FragmentViewPager viewPager;
	private FragmentTransaction transaction;
	
	private ArrayList<WebViewPage> pages;
    private ArrayList<NativeProductListPage> nativeListPages;

    private SparseArrayCompat<FrameLayoutForScrollObserve> webviewPool= new SparseArrayCompat<FrameLayoutForScrollObserve>();
	private SparseArrayCompat<NativeFragmentForMenuScrollableUI> attachedFragments= new SparseArrayCompat<NativeFragmentForMenuScrollableUI>();
	
	private ScrollTabHolder mListener;
	private OnStickyScrollTouchListener onStickyScrollTouchListener;
	private Handler loadHandler;
	
	private int position;
    private boolean isListActivity;
    private String viewType;
    public static boolean isFirst;

	public EndlessPagerForNativeScrollViewAdapter(FragmentManager fm, Activity activity, ArrayList pages, FragmentViewPager viewPager, boolean isListActivity ) {
		super(fm);
		this.activity = activity;
		this.fm = fm;
		this.pages = isListActivity ? null : pages;
		this.nativeListPages = isListActivity ? pages : null;
		this.viewPager = viewPager;
		this.isListActivity = isListActivity;
		loadHandler = new Handler();
	}
	public EndlessPagerForNativeScrollViewAdapter(FragmentManager fm, Activity activity, ArrayList pages, FragmentViewPager viewPager, boolean isListActivity,String viewType ) {
		super(fm);
		
		this.activity = activity;
		this.fm = fm;
		this.pages = isListActivity ? null : pages;
        this.nativeListPages = isListActivity ? pages : null;
		this.viewPager = viewPager;
        this.isListActivity = isListActivity;
		this.viewType = viewType;
		loadHandler = new Handler();
	}

	public void reset() {
		clearWebViews();
		webviewPool = new SparseArrayCompat<FrameLayoutForScrollObserve>();
	}
	
	public Runnable attachWebViews = new Runnable() {
		@Override
		public void run() {
			setProgressWebView( position );
			if( Build.VERSION.SDK_INT != 16 ) { // build version에 따라 1개만 로딩하도록.
				if( position > 0 ) setProgressWebView( position - 1 );
				if( position < EndlessPagerForNativeScrollViewAdapter.DUMMY_COUNT - 1 ) setProgressWebView( position + 1 );
			}
		}
	};

	public void refresh() {
		if(webviewPool == null || !Config.useKorean()) return;
		FrameLayoutForScrollObserve wv = webviewPool.get(getWebViewPosition(0));
		if (wv == null) return;

		wv.webView.reFresh();
	}

	private void setProgressWebView( int position ) {

		if(getWebViewPosition(position) == 0 && !isListActivity &&Config.useKorean()) {
			NativeWebViewFragmentForMenuScrollableUI holder = (NativeWebViewFragmentForMenuScrollableUI) instantiateItem(viewPager, position);
			if (holder != null) {
				FrameLayoutForScrollObserve wv = webviewPool.get(getWebViewPosition(position));
				if (wv == null) {
					wv =  new ScalableWebViewLayout(activity);
					webviewPool.put(getWebViewPosition(position), wv);
				}
				wv.setTag(position);
				//TODO  duckwon
				String userNo = Setting.getString(activity, Const_VALUE.KEY_SNAPS_USER_NO);
				String url = SnapsAPI.CRM_WEBVIEW_REAL_DOMAIN + "?channel=ANDROID";
				if(isFirst) {
					long result = System.currentTimeMillis();
					url += "&timeStamp=" +result;
				}
				if (!TextUtils.isEmpty(userNo)) {
					url += "&userNo=" + userNo;
				}
				holder.setUrl(url);
				holder.setLayout(wv);
				attachedFragments.put(position, holder);
			}
		} else {
			NativeFragmentForMenuScrollableUI holder = (NativeFragmentForMenuScrollableUI) instantiateItem(viewPager, position);

			if (holder != null) {
				FrameLayoutForScrollObserve wv = webviewPool.get(getWebViewPosition(position));
				if (wv == null) {
					wv = isListActivity ? new SnapsProductListView(activity) : new ScalableNativeLayout(activity);
					webviewPool.put(getWebViewPosition(position), wv);
				}
				wv.setTag(position);
				if (isListActivity) {
					if (nativeListPages.get(getDataPosition(position)).getDesignCategory() != null) {
						SnapsNativeUIManager snapsNativeUIManager = SnapsNativeUIManager.getInstance();
						if (nativeListPages.get(getDataPosition(position)).getDesignCategory().getVIEW_TYPE().equals("1")) {
							snapsNativeUIManager.setGridListLargeView(true);
						} else {
							snapsNativeUIManager.setGridListLargeView(false);
						}
						((NativeRecyclerViewFragmentForMenuScrollableUI) holder).setListItems(nativeListPages.get(getDataPosition(position)).getProductList(), false, nativeListPages.get(getDataPosition(position)).getDesignCategory().getVIEW_TYPE());
					} else {
						((NativeRecyclerViewFragmentForMenuScrollableUI) holder).setListItems(nativeListPages.get(getDataPosition(position)).getProductList(), false);
					}
				}
				holder.setLayout(wv);
				attachedFragments.put(position, holder);
			}
		}
	}
	
	public NativeFragmentForMenuScrollableUI getCurrentFragment() {
		return (NativeFragmentForMenuScrollableUI) instantiateItem( viewPager, position );
	}
	
	private void startLoadWebView( int current, boolean delayed ) {
		loadHandler = new Handler();
		this.position = current;
		
		if( delayed ) loadHandler.postDelayed( attachWebViews, 700 );
		else loadHandler.post( attachWebViews );
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if( fm == null )  return null;
		
		if (transaction == null) transaction = fm.beginTransaction();

        String name = FragmentViewPager.makeFragmentName(container.getId(), position);
        Fragment fragment = fm.findFragmentByTag(name);
        if ( fragment != null ) transaction.attach( fragment );
        else {
            fragment = (Fragment) super.instantiateItem( container, position );
            transaction.add(container.getId(), fragment, FragmentViewPager.makeFragmentName(container.getId(), position));
        }

		if (fragment != null && fragment instanceof NativeFragmentForMenuScrollableUI)
            ( (NativeFragmentForMenuScrollableUI) fragment ).setOnStickyScrollTouchListener( onStickyScrollTouchListener );
		if (fragment != null && fragment instanceof NativeWebViewFragmentForMenuScrollableUI)
			( (NativeFragmentForMenuScrollableUI) fragment ).setOnStickyScrollTouchListener( onStickyScrollTouchListener );

        return fragment;
	}
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		if( fm == null ) return;
		
		if (transaction == null) transaction = fm.beginTransaction();
		transaction.detach( (Fragment)object );
	}
	
	public void setTabHolderScrollingContent(ScrollTabHolder listener) {
		mListener = listener;
	}

	public void setOnStickyScrollTouchListener( OnStickyScrollTouchListener listener ) {
		this.onStickyScrollTouchListener = listener;
	}
		
	@Override
	public CharSequence getPageTitle(int position) {
		return isListActivity ? nativeListPages.get( position ).getTitle() : pages.get( position ).title;
	}
	
	public boolean isBadgeExist( int position ) {
		return isListActivity ? nativeListPages.get( position ).isBadgeExist() : pages.get( position ).isBadgeExist;
	}

	@Override
	public int getCount() {
		if( getDataCount() < 1 ) return 0;
		return EndlessPagerForNativeScrollViewAdapter.DUMMY_COUNT;
	}
	
	public int getDataCount() {
        return isListActivity ? nativeListPages == null ? 0 : nativeListPages.size() : pages == null ? 0 : pages.size();
	}
	
	public int getDataPosition( int position ) {
		return position % getDataCount();
	}
	
	public int getWebViewPosition( int position ) {
		return position % (getDataCount() > 3 ? getDataCount() : getDataCount() * 2);
	}
	
	@Override
	public Fragment getItem(final int position) {
		NativeFragmentForMenuScrollableUI fragment;
		if(getWebViewPosition(position) == 0 && !isListActivity && Config.useKorean()) {
			fragment = new NativeWebViewFragmentForMenuScrollableUI();
			if (mListener != null) fragment.setScrollTabHolder(mListener);
		} else {
			if (isListActivity) {
				fragment = new NativeRecyclerViewFragmentForMenuScrollableUI();
				if (mListener != null) fragment.setScrollTabHolder(mListener);
			} else {
				fragment = new NativeScrollViewFragmentForMenuScrollableUI();
				((NativeScrollViewFragmentForMenuScrollableUI) fragment).setMenuList(pages.get(getDataPosition(position)).menuList, pages.get(getDataPosition(position)).isHomePage);
				if (mListener != null) fragment.setScrollTabHolder(mListener);
			}
		}

		return fragment;
	}
	
	public void attachWebView( int currentPos, boolean delayed ) {
		startLoadWebView( currentPos, delayed );
	}
	
	public void dettachWebView( int currentPos ) {
		int key;
        NativeFragmentForMenuScrollableUI fragment;
		for( int i = 0; i < attachedFragments.size(); ++i ) {
			key = attachedFragments.keyAt( i );

			// 뷰페이져 특성상 기본적으로 선택된 화면 양쪽이 있어야 하는데 그외에 다른 프래그 먼트는 지워야 한다
			boolean shouldRemoveFragment = Math.abs(key - currentPos) > (getDataCount() > 3 ? 1 : 0);
			if( shouldRemoveFragment ) {
				fragment = attachedFragments.get( key );
				if( fragment != null ) {
                    dettachWebView( fragment );
					attachedFragments.remove( key );
				}
			}
		}
	}
	
	public void clearWebViews() {
		int key;
        NativeFragmentForMenuScrollableUI fragment;
		for( int i = 0; i < attachedFragments.size(); ++i ) {
			key = attachedFragments.keyAt( i );
			fragment = attachedFragments.get( key );
			if( fragment != null ) dettachWebView( fragment );
		}
	}

    private void dettachWebView( NativeFragmentForMenuScrollableUI fragment ) {
        fragment.dettachView();
    }
}
