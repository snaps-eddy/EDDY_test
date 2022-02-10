package com.snaps.mobile.component;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.collection.SparseArrayCompat;
import android.view.View;
import android.view.ViewGroup;

import com.kmshack.newsstand.ScrollTabHolder;
import com.snaps.common.model.WebViewPage;
import com.snaps.mobile.activity.home.fragment.FragmentViewPager;
import com.snaps.mobile.activity.home.fragment.WebViewFragmentForMenuScrollableUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("CommitTransaction")
public class EndlessPagerAdapter extends EndlessPagerBaseAdapter {

	private Activity activity;

	private FragmentManager fm;
	private FragmentViewPager viewPager;
	private FragmentTransaction transaction;

	private ArrayList<WebViewPage> pages;
	private Map<Integer, WebViewFragmentForMenuScrollableUI> items;

	private SparseArrayCompat<ProgressWebView> webviewPool = new SparseArrayCompat<ProgressWebView>();
	private SparseArrayCompat<WebViewFragmentForMenuScrollableUI> attachedFragments = new SparseArrayCompat<WebViewFragmentForMenuScrollableUI>();

	private ScrollTabHolder mListener;

	private Handler loadHandler;

	private int position;

	public EndlessPagerAdapter(FragmentManager fm, Activity activity, ArrayList<WebViewPage> pages, FragmentViewPager viewPager) {
		super(fm);

		this.activity = activity;
		this.fm = fm;
		this.pages = pages;
		this.viewPager = viewPager;
		loadHandler = new Handler();

		this.items = new HashMap<>();
	}

	public void reset() {
		clearWebViews();
		webviewPool = new SparseArrayCompat<ProgressWebView>();
	}

	public Runnable attachWebViews = new Runnable() {
		@Override
		public void run() {
			setProgressWebView(position);
			if (Build.VERSION.SDK_INT != 16) { // build version에 따라 1개만 로딩하도록.
				if (position > 0) {
					setProgressWebView(position - 1);
				}
				if (position < EndlessPagerAdapter.DUMMY_COUNT - 1) {
					setProgressWebView(position + 1);
				}
			}
		}
	};

	private void setProgressWebView(int position) {
		WebViewFragmentForMenuScrollableUI holder = (WebViewFragmentForMenuScrollableUI) instantiateItem(viewPager, position);
		if (holder != null) {
			ProgressWebView wv = webviewPool.get(getWebViewPosition(position));
			if (wv == null) {
				wv = new ProgressWebView(activity);
				webviewPool.put(getWebViewPosition(position), wv);
			}
			wv.setTag(position);
			holder.setProgressWebView(wv);
			attachedFragments.put(position, holder);
		}
	}

	public WebViewFragmentForMenuScrollableUI getCurrentFragment() {
		return (WebViewFragmentForMenuScrollableUI) instantiateItem(viewPager, position);
	}

	private void startLoadWebView(int current, boolean delayed) {
		loadHandler = new Handler();
		this.position = current;

		if (delayed) {
			loadHandler.postDelayed(attachWebViews, 700);
		} else {
			loadHandler.post(attachWebViews);
		}
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (fm == null) {
			return null;
		}

		if (transaction == null) {
			transaction = fm.beginTransaction();
		}

		String name = FragmentViewPager.makeFragmentName(container.getId(), position);
		Fragment fragment = fm.findFragmentByTag(name);
		if (fragment != null) {
			transaction.attach(fragment);
		} else {
			fragment = (WebViewFragmentForMenuScrollableUI) super.instantiateItem(container, position);
			transaction.add(container.getId(), fragment, FragmentViewPager.makeFragmentName(container.getId(), position));
		}

		return fragment;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		if (fm == null) {
			return;
		}

		if (transaction == null) {
			transaction = fm.beginTransaction();
		}
		transaction.detach((Fragment) object);
	}

	public void setTabHolderScrollingContent(ScrollTabHolder listener) {
		mListener = listener;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return pages.get(position).title;
	}

	public boolean isBadgeExist(int position) {
		return pages.get(position).isBadgeExist;
	}

	@Override
	public int getCount() {
		if (getDataCount() < 1) {
			return 0;
		}

		return EndlessPagerAdapter.DUMMY_COUNT;
//		return pages.size();
	}

	public int getDataCount() {
		return pages == null ? 0 : pages.size();
	}

	public int getDataPosition(int position) {
		return position % getDataCount();
	}

	public int getWebViewPosition(int position) {
		return position % (getDataCount() > 3 ? getDataCount() : getDataCount() * 2);
	}

	public Fragment getFragmentForPosition(int position) {

		Fragment fragment = null;
		if (viewPager != null) {
			String tag = FragmentViewPager.makeFragmentName(viewPager.getId(), position);
			fragment = fm.findFragmentByTag(tag);
		}
		return fragment;
	}

	@Override
	public Fragment getItem(final int position) {
		if (items == null) {
			return null;
		}

		if (items.containsKey(position)) {
			return items.get(position);
		} else {
			WebViewFragmentForMenuScrollableUI fragment = WebViewFragmentForMenuScrollableUI.newInstance(pages.get(getDataPosition(position)).url);
			if (mListener != null) {
				fragment.setScrollTabHolder(mListener);
			}
			items.put(position, fragment);
			return fragment;
		}
	}

	public void attachWebView(int currentPos) {
		attachWebView(currentPos, false);
	}

	public void attachWebView(int currentPos, boolean delayed) {
		startLoadWebView(currentPos, delayed);
	}

	public void dettachWebView(int currentPos) {
		int key;
		WebViewFragmentForMenuScrollableUI fragment;
		for (int i = 0; i < attachedFragments.size(); ++i) {
			key = attachedFragments.keyAt(i);
			if (Math.abs(key - currentPos) > (pages.size() > 3 ? 1 : 0)) {
				fragment = attachedFragments.get(key);
				if (fragment != null) {
					fragment.dettachWebView();
					attachedFragments.remove(key);
				}
			}
		}
	}

	public void clearWebViews() {
		int key;
		WebViewFragmentForMenuScrollableUI fragment;
		for (int i = 0; i < attachedFragments.size(); ++i) {
			key = attachedFragments.keyAt(i);
			fragment = attachedFragments.get(key);
			if (fragment != null) {
				fragment.dettachWebView();
			}
		}
	}
}
