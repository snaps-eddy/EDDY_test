package com.snaps.common.customui.sticky;

import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.astuetz.PagerSlidingTabStripForSticky;
import com.kmshack.newsstand.ScrollTabHolder;
import com.snaps.common.model.WebViewPage;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.fragment.WebViewFragmentForMenuScrollableUI;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;
import com.snaps.mobile.component.CustomSensitivityViewPager;
import com.snaps.mobile.component.EndlessPagerAdapter;
import com.snaps.mobile.component.ProgressWebView;
import com.snaps.mobile.component.SnapsWebviewProcess;
import com.snaps.mobile.interfaces.OnPageLoadListener;
import com.snaps.mobile.interfaces.OnPageScrollListener;
import com.snaps.mobile.interfaces.OnWebViewCreateListener;

import java.util.ArrayList;

@Deprecated
public class StickyStyleWebViewController implements OnPageScrollListener, ScrollTabHolder, ViewPager.OnPageChangeListener {

	private int m_iPagerOldPosition = 0;

	private SnapsBaseFragmentActivity activity;

	private FrameLayout menuLayout;
//	private ProgressBar progressBar;

	private PagerSlidingTabStripForSticky mPagerSlidingTabStrip;
	private CustomSensitivityViewPager mViewPager;
	private EndlessPagerAdapter mPagerAdapter;

	private FragmentStatePagerAdapter singleItemAdapter;
	private WebViewFragmentForMenuScrollableUI singleFragment;
	private ProgressWebView singleProgressWebview;

	private ArrayList<WebViewPage> pages = new ArrayList<WebViewPage>();

	private OnPageLoadListener pageLoadListener;
	private SnapsWebviewProcess webviewProcess;

	private boolean scrollFromUser = true;

	private SnapsStickyLayout mStickyView;
	private StickyControls mStickyControls;
	private SnapsMenuManager.UIStickyInfo mStickyInfo;

	public StickyStyleWebViewController(SnapsBaseFragmentActivity activity) {
		this.activity = activity;
	}

	public void initialize() {
		initialize(PagerSlidingTabStripForSticky.TAB_TYPE_DEFAULT);
	}

	public void initialize(int tabType) {
		if (mPagerSlidingTabStrip != null)
			mPagerSlidingTabStrip.setTabType(tabType);

		if (mViewPager != null)
//			mViewPager.setOffscreenPageLimit(pages != null ? pages.size() - 1 : 1);
			mViewPager.setOffscreenPageLimit(2);

		if (pages.size() < 1)
			;
		else if (pages.size() < 2) {
//			(activity.findViewById(R.id.TopLine)).setVisibility(View.VISIBLE);

			if (menuLayout != null)
				menuLayout.setVisibility(View.GONE);
			singleItemAdapter = new FragmentStatePagerAdapter(activity.getSupportFragmentManager()) {
				@Override
				public int getCount() {
					return 1;
				}

				@Override
				public Fragment getItem(int arg0) {

					//아래 코드는 매번 프래그먼트를 새로 생성하기 때문에 아래로 이동 함.
//					singleFragment = (WebViewFragmentForMenuScrollableUI) new WebViewFragmentForMenuScrollableUI(pages.get(arg0).url);
//					singleProgressWebview = new ProgressWebView(activity);
//					singleProgressWebview.setPageScrollListner(StickyStyleWebViewController.this);
////					progressBar.setTag(singleProgressWebview.toString());
////					singleProgressWebview.setHorizontalProgressBar(progressBar);
//					if (webviewProcess != null)
//						singleProgressWebview.addWebviewProcess(webviewProcess);
//					if (pageLoadListener != null)
//						singleProgressWebview.setOnPageLoadListener(pageLoadListener);
//
//					singleFragment.setProgressWebView(singleProgressWebview);
					return singleFragment;
				}
			};

//			singleFragment = (WebViewFragmentForMenuScrollableUI) new WebViewFragmentForMenuScrollableUI(pages.get(0).url);
			singleFragment = WebViewFragmentForMenuScrollableUI.newInstance(pages.get(0).url);
			singleProgressWebview = new ProgressWebView(activity);
			singleProgressWebview.setPageScrollListner(StickyStyleWebViewController.this);
//					progressBar.setTag(singleProgressWebview.toString());
//					singleProgressWebview.setHorizontalProgressBar(progressBar);
			if (webviewProcess != null)
				singleProgressWebview.addWebviewProcess(webviewProcess);
			if (pageLoadListener != null)
				singleProgressWebview.setOnPageLoadListener(pageLoadListener);

			singleFragment.setProgressWebView(singleProgressWebview);

			mViewPager.setAdapter(singleItemAdapter);
		} else {
			if (mPagerAdapter == null)
				mPagerAdapter = new EndlessPagerAdapter(activity.getSupportFragmentManager(), activity, pages, mViewPager);
			mPagerAdapter.setTabHolderScrollingContent(this);

			mViewPager.setAdapter(mPagerAdapter);
			int halfInt = EndlessPagerAdapter.DUMMY_COUNT / 2;
			mViewPager.setCurrentItem(halfInt - halfInt % pages.size());
			setEventWhenWebviewCreate();
			loadWebView(halfInt - halfInt % pages.size(), true);

			mPagerSlidingTabStrip.setViewPager(mViewPager);
			mPagerSlidingTabStrip.setOnPageChangeListener(this);
		}

		mViewPager.addOnPageChangeListener(mPageChangeListener);

		boolean isExistStripBar = pages.size() > 1;

		mStickyView.setStickyControls(mStickyControls);
		mStickyView.setEnablePagerStrip(isExistStripBar);
		mStickyView.initStickyControls(mStickyInfo);
	}

	private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			super.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}

		@Override
		public void onPageSelected(int position) {
			super.onPageSelected(position);
			if (mStickyView != null) {
				mStickyView.initLastOffset(m_iPagerOldPosition, position);
//				mStickyView.initStickyControls("^^ " + position, "!!!!", "info");
			}

			m_iPagerOldPosition = position;
		}

		@Override
		public void onPageScrollStateChanged(int state)
		{
			super.onPageScrollStateChanged(state);
		}
	};

	public void reload() {
		if (singleFragment != null)
			singleFragment.setProgressWebView(singleProgressWebview);
		else if (mPagerAdapter != null) {
			loadWebView(mViewPager.getCurrentItem());
		}
	}

	public void refresh() {
		if (mPagerAdapter != null) { // TODO 우선 리프레시는 홈화면만. 다른화면도 필요하다 하면 singleFragment로 하면 됨.
			mPagerAdapter.reset();
			loadWebView(mViewPager.getCurrentItem());
		}
	}

	public void setEventWhenWebviewCreate() {
		if (handler == null)
			handler = new Handler();
		handler.post(checkWebViewCreated);
	}

	private Handler handler;
	private Runnable checkWebViewCreated = new Runnable() {
		@Override
		public void run() {
			if (mPagerAdapter != null && mPagerAdapter.getCurrentFragment() != null && mPagerAdapter.getCurrentFragment().getProgressWebView() != null) {
				if (handler != null)
					handler.removeCallbacks(this);
				handler = null;
				ProgressWebView currentWebView = mPagerAdapter.getCurrentFragment().getProgressWebView();
				if (currentWebView != null) {
					currentWebView.setPageScrollListner(StickyStyleWebViewController.this);
//					progressBar.setTag(currentWebView.toString());
//					currentWebView.setHorizontalProgressBar(progressBar);
					if (webviewProcess != null)
						currentWebView.addWebviewProcess(webviewProcess);
					if (pageLoadListener != null)
						currentWebView.setOnPageLoadListener(pageLoadListener);
				}
			} else
				handler.postDelayed(this, 50);
		}
	};

	public void loadWebView(int current) {
		loadWebView(current, false);
	}

	public void loadWebView(int current, boolean delayed) {
		if (mPagerAdapter != null)
			mPagerAdapter.attachWebView(current, delayed);
	}

	public ProgressWebView getSingleProgressWebview() {
		return singleProgressWebview;
	}

	public ProgressWebView getCurrentProgressWebView() {
		if (mPagerAdapter == null || mPagerAdapter.getCurrentFragment() == null)
			return null;
		return mPagerAdapter.getCurrentFragment().getProgressWebView();
	}

	public void setOnPageLoadListener(OnPageLoadListener pageLoadListener) {
		this.pageLoadListener = pageLoadListener;
	}

	public void setWebViewProcess(SnapsWebviewProcess process) {
		this.webviewProcess = process;
	}

	public SnapsWebviewProcess getWebViewProcess() {
		return webviewProcess;
	}

//	public void setViews(RelativeLayout titleLayout, FrameLayout menuLayout, CustomSensitivityViewPager viewPager, ProgressBar progressBar) {
//		this.titleLayout = titleLayout;
//		this.menuLayout = menuLayout;
//		this.mViewPager = viewPager;
//		this.progressBar = progressBar;
//	}

	public void setStickyControls(SnapsStickyLayout stickyView,
								  StickyControls stickyControls) {
		if (stickyControls == null) return;

		this.mStickyView = stickyView;
		this.mStickyControls = stickyControls;
		this.mViewPager = stickyControls.getViewPager();
		this.mPagerSlidingTabStrip = stickyControls.getPagerSlidingTabStripForSticky();
	}

	public void addPage(WebViewPage page) {
		if (pages == null)
			pages = new ArrayList<WebViewPage>();
		pages.add(page);
	}

	public int getTabCount() {
		return pages == null ? 0 : pages.size();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		loadWebView(arg0);
		ProgressWebView currentWebView = null;
		if (singleProgressWebview != null)
			currentWebView = singleProgressWebview;
		else if (mPagerAdapter != null && mPagerAdapter.getCurrentFragment() != null)
			currentWebView = mPagerAdapter.getCurrentFragment().getProgressWebView();

		if (mPagerAdapter != null)
			mPagerAdapter.dettachWebView(arg0);

		if (currentWebView != null)
			executeWhenWebviewAttached(currentWebView);
		else {
			if (mPagerAdapter != null && mPagerAdapter.getCurrentFragment() != null) {
				mPagerAdapter.getCurrentFragment().setWebViewCreateListener(new OnWebViewCreateListener() {
					@Override
					public void onWebViewCreated(ProgressWebView webview) {
						executeWhenWebviewAttached(webview);
					}
				});

			}
		}
	}

	private void executeWhenWebviewAttached(ProgressWebView currentWebView) {
		if (currentWebView == null)
			return;
		fitWebViewScroll();
		currentWebView.setPageScrollListner(this);
//		progressBar.setTag(currentWebView.toString());
//		currentWebView.setHorizontalProgressBar(progressBar);
		if (webviewProcess != null)
			currentWebView.addWebviewProcess(webviewProcess);
		if (pageLoadListener != null)
			currentWebView.setOnPageLoadListener(pageLoadListener);
	}

	private void fitWebViewScroll() {
		//del
//		LinearLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) titleLayout.getLayoutParams();
//		int topMargin = rParams.topMargin;
//
//		ProgressWebView currentWebView = mPagerAdapter.getCurrentFragment().getProgressWebView();
//		if (currentWebView != null) {
//			if (!currentWebView.isScrollable()) {
//				showTitleLayout();
//			} else if (currentWebView.getWebViewScrollY() < -topMargin) {
//				scrollFromUser = false;
//				currentWebView.scrollTo(currentWebView.getWebViewScrollX(), -topMargin);
//			} else if (currentWebView.getRealScrollPos() != currentWebView.getScrollPos()) {
//				scrollFromUser = false;
//				currentWebView.scrollTo(currentWebView.getWebViewScrollX(), currentWebView.getScrollPos() - topMargin);
//
//			}
//		}
	}

	private void showTitleLayout() {
		//del
//		RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) titleLayout.getLayoutParams();
//		rParams.topMargin = 0;
//		titleLayout.setLayoutParams(rParams);
	}

	@Override
	public void adjustScroll(int scrollHeight) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
	}

	@Override
	public boolean onScrollChanged(int l, int t, int oldl, int oldt) {
//		if (!scrollFromUser) {
//			scrollFromUser = true;
//			return false;
//		}
//		return changeTitleState(t, oldt);
		return false;
	}

    @Override
    public boolean onScrollChanged(int dx, int dy) {
        return false;
    }

    private boolean changeTitleState(int t, int oldt) {
//		RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) titleLayout.getLayoutParams();
//		int oldMargin = rParams.topMargin;
//		int newMargin;
//
//		ProgressWebView wv = null;
//		if (singleItemAdapter != null)
//			wv = singleProgressWebview;
//		else if (mPagerAdapter != null || mPagerAdapter.getCurrentFragment() != null)
//			wv = mPagerAdapter.getCurrentFragment().getProgressWebView();
//		if (wv == null)
//			return false;
//		if (wv.getWebViewScrollY() < 1)
//			rParams.topMargin = 0;
//		else {
//			rParams.topMargin -= (t - oldt);
//			if (rParams.topMargin < -titleLayout.getHeight())
//				rParams.topMargin = -titleLayout.getHeight();
//			else if (rParams.topMargin > 0)
//				rParams.topMargin = 0;
//		}
//		newMargin = rParams.topMargin;
//
//		boolean changed = oldMargin != newMargin;
//
//		int newScrollPos = wv.getRealScrollPos() + newMargin;
//		wv.setScrollPos(newScrollPos < 0 ? 0 : newScrollPos);
//
//		if (changed)
//			titleLayout.setLayoutParams(rParams);
//		return changed;
		return false;
	}

	public boolean gotoTab(int index) {
		int tabCnt = getTabCount();

		
		if (tabCnt == 0)// 탭이 생성이 되지 않을경우 예외처리..
			return false;
		
		int currentPos = mViewPager.getCurrentItem();
		mViewPager.setCurrentItem(currentPos - currentPos % tabCnt + index);
		return true;
	}

	public SnapsMenuManager.UIStickyInfo getStickyInfo() {
		return mStickyInfo;
	}

	public void setStickyInfo(SnapsMenuManager.UIStickyInfo mStickyInfo) {
		this.mStickyInfo = mStickyInfo;
	}

	public void setDrawPagerStripUnderline(boolean isDraw) {
		if (mPagerSlidingTabStrip == null) return;
		mPagerSlidingTabStrip.setIsDrawUnderline(isDraw);
	}
}
