package com.snaps.common.utils.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Handler;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.astuetz.PagerSlidingTabStripForSticky;
import com.kmshack.newsstand.ScrollTabHolder;
import com.snaps.common.model.NativeProductListPage;
import com.snaps.common.model.WebViewPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.home.fragment.NativeFragmentForMenuScrollableUI;
import com.snaps.mobile.activity.home.fragment.NativeRecyclerViewFragmentForMenuScrollableUI;
import com.snaps.mobile.activity.home.fragment.NativeScrollViewFragmentForMenuScrollableUI;
import com.snaps.mobile.activity.ui.menu.renewal.model.Menu;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;
import com.snaps.mobile.component.CustomSensitivityViewPager;
import com.snaps.mobile.component.EndlessPagerForNativeScrollViewAdapter;
import com.snaps.mobile.component.FrameLayoutForScrollObserve;
import com.snaps.mobile.component.ObserveScrollingNativeWebView;
import com.snaps.mobile.component.ObserveScrollingRecyclerView;
import com.snaps.mobile.component.ObserveScrollingScrollView;
import com.snaps.mobile.component.ScalableNativeLayout;
import com.snaps.mobile.interfaces.OnLoginStatusChanged;
import com.snaps.mobile.interfaces.OnNativeScrollViewCreateListener;
import com.snaps.mobile.interfaces.OnPageScrollListener;
import com.snaps.mobile.interfaces.OnStickyScrollTouchListener;
import com.snaps.mobile.product_native_ui.ui.SnapsProductListView;
import com.snaps.mobile.product_native_ui.util.SnapsNativeUIManager;

import java.util.ArrayList;

public class TabStyleNativeScrollViewController implements OnPageScrollListener, ScrollTabHolder, ViewPager.OnPageChangeListener, OnLoginStatusChanged {
	private SnapsBaseFragmentActivity activity;

	private RelativeLayout titleLayout;
	private FrameLayout menuLayout;

	private PagerSlidingTabStripForSticky mPagerSlidingTabStrip;
	private CustomSensitivityViewPager mViewPager;
	private EndlessPagerForNativeScrollViewAdapter mPagerAdapter;

	private FragmentStatePagerAdapter singleItemAdapter;
	private NativeFragmentForMenuScrollableUI singleFragment;
	private FrameLayoutForScrollObserve singleProgressWebview;

	private ArrayList<WebViewPage> pages = new ArrayList<WebViewPage>();
    private ArrayList<NativeProductListPage> nativeListPages = new ArrayList<NativeProductListPage>();

	private int titleBarMagneticAnimationStartOffsetMargin = 0;

	private boolean isActiveTitleBarMagneticAnimation = false;
    private boolean scrollFromUser = true;
    private boolean isListActivity = false;

	public TabStyleNativeScrollViewController(SnapsBaseFragmentActivity activity) {
		this.activity = activity;
	}

	public void initialize() {
        initialize(PagerSlidingTabStripForSticky.TAB_TYPE_AUTO);
	}

	public void initialize(int tabType) {
		mPagerSlidingTabStrip = (PagerSlidingTabStripForSticky) activity.findViewById(R.id.tabs);
		mPagerSlidingTabStrip.setTabType(tabType);
		mViewPager = (CustomSensitivityViewPager) activity.findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(2);

        if( pages != null && pages.size() > 0 ) {
            initHomePages();
            isListActivity = false;
        }
        else if( nativeListPages != null && nativeListPages.size() > 0 ) {
            initListPages();
            isListActivity = true;
        }
	}

    private void setEnableFling( Object scrollView, boolean flag ) {
        if( scrollView instanceof ObserveScrollingScrollView )
            ( (ObserveScrollingScrollView) scrollView ).setEnableFling( flag );
        else if( scrollView instanceof ObserveScrollingRecyclerView )
            ( (ObserveScrollingRecyclerView) scrollView ).setEnableFling( flag );

    }

    private void initHomePages() {
        if (pages.size() < 2) {
            (activity.findViewById(R.id.TopLine)).setVisibility(View.VISIBLE);

            if (menuLayout != null)
                menuLayout.setVisibility(View.GONE);
            singleItemAdapter = new FragmentStatePagerAdapter(activity.getSupportFragmentManager()) {
                @Override
                public int getCount() {
                    return 1;
                }

                @Override
                public Fragment getItem(int arg0) {
                    singleFragment = new NativeScrollViewFragmentForMenuScrollableUI();
                    ( (NativeScrollViewFragmentForMenuScrollableUI) singleFragment ).setMenuList( pages.get(arg0).menuList, pages.get(arg0).isHomePage );

                    singleProgressWebview = new ScalableNativeLayout( activity );
                    singleProgressWebview.setPageScrollListner(TabStyleNativeScrollViewController.this);

                    singleFragment.setLayout( singleProgressWebview );
                    return singleFragment;
                }
            };
            mViewPager.setAdapter(singleItemAdapter);
        } else {
            if (mPagerAdapter == null)
                mPagerAdapter = new EndlessPagerForNativeScrollViewAdapter(activity.getSupportFragmentManager(), activity, pages, mViewPager, false);
            mPagerAdapter.setTabHolderScrollingContent(this);
            mPagerAdapter.setOnStickyScrollTouchListener(new OnStickyScrollTouchListener() {
                @Override
                public void onStickyScrollTouch(MotionEvent e, ObserveScrollingScrollView scrollView) {
                    if (e != null) {
                        switch (e.getAction()) {
                            case MotionEvent.ACTION_DOWN :
                                setEnableFling( scrollView, true );
                                break;
                            case MotionEvent.ACTION_UP :
                                if( handler == null )
                                    handler = new Handler();

                                scrollView.setTitleAnimation( handler, checkTitleBarMagnetic );
                                currentScrollView = scrollView;
                                handler.postDelayed( checkTitleBarMagnetic, 100 );
                                break;
                            case MotionEvent.ACTION_MOVE :
                                break;
                        }
                    }
                }




                @Override
                public void onStickyScrollTouch(MotionEvent e, ObserveScrollingRecyclerView recyclerView) {}

                @Override
                public void onStickyScrollTouch(MotionEvent e, ObserveScrollingNativeWebView webView) {
                    if (e != null) {
                        switch (e.getAction()) {
                            case MotionEvent.ACTION_DOWN :
                                setEnableFling( webView, true );
                                break;
                            case MotionEvent.ACTION_UP :
                                if( handler == null )
                                    handler = new Handler();

                             //   webView.setTitleAnimation( handler, checkTitleBarMagnetic );
                                currentScrollView = webView;
                                handler.postDelayed( checkTitleBarMagnetic, 100 );
                                break;
                            case MotionEvent.ACTION_MOVE :
                                break;
                        }
                    }
                }
            });

            mViewPager.setAdapter(mPagerAdapter);
            int halfInt = EndlessPagerForNativeScrollViewAdapter.DUMMY_COUNT / 2;
            mViewPager.setCurrentItem(halfInt - halfInt % pages.size());
            setEventWhenWebviewCreate();
            loadWebView(halfInt - halfInt % pages.size(), true);

            mPagerSlidingTabStrip.setViewPager(mViewPager);
            mPagerSlidingTabStrip.setOnPageChangeListener(this);
        }
    }

    private void initListPages() {
        if (nativeListPages.size() < 2) {
            (activity.findViewById(R.id.TopLine)).setVisibility(View.VISIBLE);

            if (menuLayout != null)
                menuLayout.setVisibility(View.GONE);
            singleItemAdapter = new FragmentStatePagerAdapter(activity.getSupportFragmentManager()) {
                @Override
                public int getCount() {
                    return 1;
                }

                @Override
                public Fragment getItem(int arg0) {
                    singleFragment = new NativeRecyclerViewFragmentForMenuScrollableUI();

                    singleProgressWebview = new SnapsProductListView( activity );
                    singleProgressWebview.setPageScrollListner(TabStyleNativeScrollViewController.this);
                    if(nativeListPages.get(arg0).getDesignCategory() != null) {
                        SnapsNativeUIManager snapsNativeUIManager = SnapsNativeUIManager.getInstance();
                        if(nativeListPages.get(arg0).getDesignCategory().getVIEW_TYPE().equals("1")) {
                            snapsNativeUIManager.setGridListLargeView(true);
                        }else {
                            snapsNativeUIManager.setGridListLargeView(false);
                        }
                        ((NativeRecyclerViewFragmentForMenuScrollableUI) singleFragment).setListItems(nativeListPages.get(arg0).getProductList(), true, nativeListPages.get(arg0).getDesignCategory().getVIEW_TYPE());

                    }else {
                        ((NativeRecyclerViewFragmentForMenuScrollableUI) singleFragment).setListItems(nativeListPages.get(arg0).getProductList(), true);
                    }
                    singleFragment.setLayout( singleProgressWebview );
                    return singleFragment;
                }
            };
            mViewPager.setAdapter(singleItemAdapter);
        } else {
            if (mPagerAdapter == null)
                mPagerAdapter = new EndlessPagerForNativeScrollViewAdapter(activity.getSupportFragmentManager(), activity, nativeListPages, mViewPager, true);
            mPagerAdapter.setTabHolderScrollingContent(this);
            mPagerAdapter.setOnStickyScrollTouchListener(new OnStickyScrollTouchListener() {
                @Override
                public void onStickyScrollTouch(MotionEvent e, ObserveScrollingScrollView scrollView) {}

                @Override
                public void onStickyScrollTouch(MotionEvent e, ObserveScrollingRecyclerView recyclerView) {
                    if (e != null) {
                        switch (e.getAction()) {
                            case MotionEvent.ACTION_DOWN :
                                setEnableFling( recyclerView, true );
                                break;
                            case MotionEvent.ACTION_UP :
                                if( handler == null )
                                    handler = new Handler();

                                recyclerView.setTitleAnimation( handler, checkTitleBarMagnetic );
                                currentScrollView = recyclerView;
                                handler.postDelayed( checkTitleBarMagnetic, 100 );
                                break;
                            case MotionEvent.ACTION_MOVE :
                                break;
                        }
                    }
                }

                @Override
                public void onStickyScrollTouch(MotionEvent e, ObserveScrollingNativeWebView webView) {
                    if (e != null) {
                        switch (e.getAction()) {
                            case MotionEvent.ACTION_DOWN :
                                setEnableFling( webView, true );
                                break;
                            case MotionEvent.ACTION_UP :
                                if( handler == null )
                                    handler = new Handler();

                                webView.setTitleAnimation( handler, checkTitleBarMagnetic );
                                currentScrollView = webView;
                                handler.postDelayed( checkTitleBarMagnetic, 100 );
                                break;
                            case MotionEvent.ACTION_MOVE :
                                break;
                        }
                    }
                }
            });

            mViewPager.setAdapter(mPagerAdapter);
            int halfInt = EndlessPagerForNativeScrollViewAdapter.DUMMY_COUNT / 2;
            mViewPager.setCurrentItem(halfInt - halfInt % nativeListPages.size());
            setEventWhenWebviewCreate();
            loadWebView(halfInt - halfInt % nativeListPages.size(), true);

            mPagerSlidingTabStrip.setViewPager(mViewPager);
            mPagerSlidingTabStrip.setOnPageChangeListener(this);
        }
    }

	public void reload() {
		if (singleFragment != null)
            singleFragment.setLayout( singleProgressWebview );
		else if (mPagerAdapter != null)
			loadWebView(mViewPager.getCurrentItem());
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

    private Object currentScrollView;
	private Handler handler;
	private Runnable checkWebViewCreated = new Runnable() {
		@Override
		public void run() {
			if (mPagerAdapter != null && mPagerAdapter.getCurrentFragment() != null && mPagerAdapter.getCurrentFragment().getLayout() != null ) {
				if (handler != null)
					handler.removeCallbacks(this);
				handler = null;

                FrameLayoutForScrollObserve currentWebView = mPagerAdapter.getCurrentFragment().getLayout();
                if (currentWebView != null)
                    currentWebView.setPageScrollListner(TabStyleNativeScrollViewController.this);
			} else
				handler.postDelayed(this, 50);
		}
	};
    private Runnable checkTitleBarMagnetic = new Runnable() {
        @Override
        public void run() {
            setEnableFling( currentScrollView, !checkTitleBarMagnetic() );
        }
    };

	public void loadWebView(int current) {
		loadWebView(current, false);
	}

	public void loadWebView(int current, boolean delayed) {
		if (mPagerAdapter != null)
			mPagerAdapter.attachWebView(current, delayed);
	}

	public void setViews(RelativeLayout titleLayout, FrameLayout menuLayout, CustomSensitivityViewPager viewPager) {
		this.titleLayout = titleLayout;
		this.menuLayout = menuLayout;
		this.mViewPager = viewPager;
	}

	public void addPage(WebViewPage page) {
		if (pages == null)
			pages = new ArrayList<WebViewPage>();
		pages.add(page);
	}

    public void addPage(NativeProductListPage page) {
        if ( nativeListPages == null )
            nativeListPages = new ArrayList<NativeProductListPage>();
        nativeListPages.add(page);
    }

    public void clearPage() {
        pages = null;
        nativeListPages = null;
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

        FrameLayoutForScrollObserve currentWebView = null;
        if (singleProgressWebview != null)
            currentWebView = singleProgressWebview;
        else if (mPagerAdapter != null && mPagerAdapter.getCurrentFragment() != null)
            currentWebView = mPagerAdapter.getCurrentFragment().getLayout();

        if (mPagerAdapter != null)
            mPagerAdapter.dettachWebView(arg0);

        if (currentWebView != null && currentWebView.isInitialized() )
            executeWhenWebviewAttached(currentWebView);
        else {
            if (mPagerAdapter != null && mPagerAdapter.getCurrentFragment() != null) {
                mPagerAdapter.getCurrentFragment().setNativeScrollViewCreateListener(new OnNativeScrollViewCreateListener() {
                    @Override
                    public void onNativeScrollViewCreated(FrameLayoutForScrollObserve view) {
                        executeWhenWebviewAttached( view );
                    }
                });
            }
        }
	}

	private void executeWhenWebviewAttached( FrameLayoutForScrollObserve currentWebView) {
		if (currentWebView == null)
			return;

        currentWebView.setPageScrollListner(this);
		fitWebViewScroll();
    }

	private void fitWebViewScroll() {
		RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) titleLayout.getLayoutParams();
		int topMargin = rParams.topMargin;

        FrameLayoutForScrollObserve currentWebView = mPagerAdapter.getCurrentFragment().getLayout();
		if (currentWebView != null) {
			if (!currentWebView.isScrollable()) {
				showTitleLayout();
			} else if (currentWebView.getWebViewScrollY() < -topMargin) {
				scrollFromUser = false;
				currentWebView.scrollTo(currentWebView.getWebViewScrollY(), -topMargin);
			} else if (currentWebView.getWebViewScrollY() != currentWebView.getScrollPos()) {
				scrollFromUser = false;
				currentWebView.scrollTo(currentWebView.getWebViewScrollY(), currentWebView.getScrollPos() - topMargin);

			}
		}
	}

	private void showTitleLayout() {
		RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) titleLayout.getLayoutParams();
		rParams.topMargin = 0;
		titleLayout.setLayoutParams(rParams);
	}

	@Override
	public void adjustScroll(int scrollHeight) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
	}

	@Override
	public boolean onScrollChanged(int l, int t, int oldl, int oldt) {
		if (!scrollFromUser) {
			scrollFromUser = true;
			return false;
		}
		return changeTitleState(t, oldt);
	}

    @Override
    public boolean onScrollChanged( int dx, int dy ) {
        if (!scrollFromUser) {
            scrollFromUser = true;
            return false;
        }
        return changeTitleState( getScrollingView(), dy );
    }

	private boolean checkTitleBarMagnetic() {
		if (isActiveTitleBarMagneticAnimation)
            return false;

		RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) titleLayout.getLayoutParams();
		if (rParams == null || rParams.topMargin == 0 || rParams.topMargin == -rParams.height) return false;
        boolean isMagneticAnimationToTop = rParams.topMargin < -(rParams.height / 2);
		titleBarMagneticAnimationStartOffsetMargin = rParams.topMargin;
		startTitleBarMagneticAnimation(rParams.topMargin, isMagneticAnimationToTop ?  -rParams.height : 0, currentScrollView);
		return true;
	}

    private boolean isTitleBarAtTop() {
        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) titleLayout.getLayoutParams();
        return rParams != null && rParams.topMargin < -rParams.height + 1;
    }

    private boolean isTitleBarAtBottom() {
        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) titleLayout.getLayoutParams();
        return rParams != null && rParams.topMargin > -1;
    }

	private void startTitleBarMagneticAnimation(int startTopMargin, int targetTopMargin, final Object scrollView) {
		isActiveTitleBarMagneticAnimation = true;
		ValueAnimator animator = ValueAnimator.ofInt(startTopMargin, targetTopMargin);
		animator.setDuration(200);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int value = (int) animation.getAnimatedValue();
                int moveY =  (titleBarMagneticAnimationStartOffsetMargin - value);
				if (scrollView != null) {
                    if( scrollView instanceof ObserveScrollingScrollView )
                        ( (ObserveScrollingScrollView) scrollView ).scrollBy(0, moveY);
                    else if( scrollView instanceof ObserveScrollingRecyclerView )
                        ( (ObserveScrollingRecyclerView) scrollView ).getRecyclerView().scrollBy( 0, moveY );
				}

				titleBarMagneticAnimationStartOffsetMargin = value;
			}
		});
		animator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {}
			@Override
			public void onAnimationEnd(Animator animation) {
                setEnableFling( scrollView, true );
				isActiveTitleBarMagneticAnimation = false;
			}

			@Override
			public void onAnimationCancel(Animator animation) {}

			@Override
			public void onAnimationRepeat(Animator animation) {}
		});
		animator.start();
	}

    private boolean changeTitleState( FrameLayoutForScrollObserve wv, int changedY ) {
        if( wv == null ) return false;

        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) titleLayout.getLayoutParams();
        int oldMargin = rParams.topMargin;
        int newMargin;

        rParams.topMargin -= changedY;
        if (rParams.topMargin < -titleLayout.getHeight())
            rParams.topMargin = -titleLayout.getHeight();
        else if (rParams.topMargin > 0)
            rParams.topMargin = 0;
        newMargin = rParams.topMargin;

        boolean changed = oldMargin != newMargin;

        int newScrollPos = wv.getWebViewScrollY() + newMargin;
        wv.setScrollPos(newScrollPos < 0 ? 0 : newScrollPos);

        if (changed)
            titleLayout.setLayoutParams(rParams);

        return changed;
    }

	private boolean changeTitleState(int t, int oldt) {
        FrameLayoutForScrollObserve wv = getScrollingView();

        if( wv == null ) return false;

        // bounds 무시
        t = t < 0 ? 0 : t > wv.getMaxScrollPosition() ? wv.getMaxScrollPosition() : t;
        oldt = oldt < 0 ? 0 : oldt > wv.getMaxScrollPosition() ? wv.getMaxScrollPosition() : oldt;

        return changeTitleState( wv, t - oldt );
    }

    private FrameLayoutForScrollObserve getScrollingView() {
        FrameLayoutForScrollObserve wv = null;
        if (singleItemAdapter != null)
            wv = singleProgressWebview;
        else if (mPagerAdapter != null && mPagerAdapter.getCurrentFragment() != null)
            wv = mPagerAdapter.getCurrentFragment().getLayout();

        return wv;
    }

	public boolean gotoTab(int index) {
		int tabCnt = getTabCount();

		
		if (tabCnt == 0)// 탭이 생성이 되지 않을경우 예외처리..
			return false;
		
		int currentPos = mViewPager.getCurrentItem();
		mViewPager.setCurrentItem(currentPos - currentPos % tabCnt + index);
		return true;
	}

	public void reFresh() {
	    if(mPagerAdapter == null || !Config.useKorean()) return;
	    mPagerAdapter.refresh();
    }

    @Override
    public void changeMenuLayout(Menu menu, int index) {
        if( isListActivity ) return;

        if( pages != null && pages.size() > 0 && pages.size() < 2 ) ( (ScalableNativeLayout) singleProgressWebview ).replaceItem( menu, index );
        else if( mPagerAdapter != null && mPagerAdapter.getCurrentFragment() != null && mPagerAdapter.getCurrentFragment().getLayout() != null)
            if(mPagerAdapter.getCurrentFragment().getLayout() instanceof ScalableNativeLayout) {
                if( ( (ScalableNativeLayout) mPagerAdapter.getCurrentFragment().getLayout() ).isHome()) {
                    ((ScalableNativeLayout) mPagerAdapter.getCurrentFragment().getLayout()).replaceItem(menu, index);
                }
            }
    }
}
