package com.snaps.mobile.activity.edit.pager;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.common.imp.ISnapsPageItemInterface;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.Scale;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.PagerContainer;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsCanvasFragment;
import com.snaps.mobile.activity.edit.fragment.canvas.StickerCanvasFragment;
import com.snaps.mobile.activity.edit.fragment.canvas.ThemeBookCanvasFragment;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;

import java.util.ArrayList;

/**
 * 
 * com.snaps.kakao.activity.edit.pager SnapsLoadPager.java
 * 
 * @author JaeMyung Park
 * @Date : 2013. 6. 21.
 * @Version :
 */
public class SnapsPagerController extends BaseSnapsPagerController implements ViewPager.OnPageChangeListener {
	private static final String TAG = SnapsPagerController.class.getSimpleName();
	private FragmentActivity _activity;
	private DialogDefaultProgress _progress;
	private ArrayList<Fragment> _canvasList;
	private ArrayList<SnapsPage> _pageList;
	private TextView _viewCount;
	private PagerContainer _mContainer;
	private ViewPager _mViewPager;
	private int _pagerSelected;

	/** layout Change Button */
	private TextView _btnTopLayoutChange;
	/** case Change Button */
	private TextView _btnTopCaseChange;

	public SnapsPageAdapter pageAdapter = null;

	public SnapsPagerController(FragmentActivity activity, DialogDefaultProgress progress) {
		_activity = activity;
		_progress = progress;

		_mContainer = (PagerContainer) getActivity().findViewById(R.id.pager_container);
		_mViewPager = _mContainer.getViewPager();

		_btnTopCaseChange = (TextView) getActivity().findViewById(R.id.btnTopCaseChange);
		_btnTopLayoutChange = (TextView) getActivity().findViewById(R.id.btnTopLayoutChange);
		_viewCount = (TextView) getActivity().findViewById(R.id.view_count);

		_mViewPager.setOnPageChangeListener(this);
	}

	public void close() {
		try {
			if (_pageList != null) {
				for (int i = 0; i < _pageList.size(); i++)
					_pageList.get(i).close();
			}
			_canvasList = null;
			_pageList = null;
			_mViewPager.setAdapter(null);
			_activity = null;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	private FragmentActivity getActivity() {
		return _activity;
	}

	/**
	 * 
	 * 페이지 로드
	 * 
	 * @param pageList
	 *            SnapsPageList
	 * @param canvasList
	 *            Fragment List
	 * @param t
	 *            Top Margin
	 * @param b
	 *            Bottom Margin
	 * @param m
	 *            Left,Right Margin
	 */
	public void loadPage(ArrayList<SnapsPage> pageList, ArrayList<Fragment> canvasList, int t, int b, int m) {

		//FIXME 핀치 줌 적용 필요 함..
		
		
		_canvasList = canvasList;
		_pageList = pageList;

		int screenWidth = UIUtil.getScreenWidth(getActivity());
		int screenHeight = UIUtil.getScreenHeight(getActivity());

		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) _mViewPager.getLayoutParams();
		int margin = UIUtil.convertDPtoPX(getActivity(), m);

		int toppx = t;
		int bottompx = b;

		if (Config.isThemeBook()) {
			params.width = screenWidth - UIUtil.convertDPtoPX(_activity, 15) * 2;
			params.height = (int) (params.width / 1.4f);
		} else {
			params.width = screenHeight;
			params.height = screenWidth - toppx - bottompx;
			if (Config.isSnapsSticker())
				params.height -= UIUtil.convertDPtoPX(_activity, 50);
		}

		_mViewPager.setLayoutParams(params);

		LinearLayout.LayoutParams containParams = (LinearLayout.LayoutParams) _mContainer.getLayoutParams();
		if (Config.isThemeBook()) {
		} else {
			containParams.height = params.height;
			_mContainer.setLayoutParams(containParams);
		}

		int width = 0;
		int height = 0;

		// 그림자 효과 적용하여 크기 조절.

		 if (Config.isSnapsSticker()) {
			width = pageList.get(0).getWidth() + Config.STICKER_MARGIN_LIST[0] + Config.STICKER_MARGIN_LIST[2];
			height = Integer.parseInt(pageList.get(0).height) + Config.STICKER_MARGIN_LIST[1] + Config.STICKER_MARGIN_LIST[3];
		} else {
			width = pageList.get(0).getWidth();
			height = Integer.parseInt(pageList.get(0).height);
		}

		/****
		 * 핵심...
		 */
		// 초기 _mViewPager 스케일 값
		float screen_scale = Scale.initScale(_mViewPager, width, height);

		Dlog.d("loadPage() screen_scale:" + screen_scale);

		params.width = width;
		params.height = height;

		// _mViewPager 크기를 Canvas 크기로 변경.
		_mViewPager.setLayoutParams(params);

		// _mViewPager 스케일 조절
		_mViewPager.setScaleX(screen_scale);
		_mViewPager.setScaleY(screen_scale);

		pageAdapter = new SnapsPageAdapter(getActivity().getSupportFragmentManager());


		_mViewPager.setAdapter(pageAdapter);
		_mViewPager.setOffscreenPageLimit(3);
		_mViewPager.setPageMargin(margin);

		onPageSelected(0);
	}

	@Override
	public void onPageScrollStateChanged(int page) {
		_mContainer.onPageScrollStateChanged(page);
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		_mContainer.onPageScrolled(arg0, arg1, arg2);
		_mContainer.invalidate();
	}

	@Override
	public void onPageSelected(int index) {
		// Color 변경 버튼 커버에서만 존재한다.
		if (Config.isSnapsSticker()) {
			stickerPageSelected(index);
		}

		_pagerSelected = index;
	}

	public int getPagerSelected() {
		return _pagerSelected;
	}

	/**
	 * @param index
	 */
	private void businessCard(int index) {

		String str = "";

		switch (index) {
			case 0 :
				_btnTopCaseChange.setVisibility(View.INVISIBLE);
				str = getActivity().getResources().getString(R.string.front_page);
				break;

			case 1 :
				_btnTopCaseChange.setVisibility(View.INVISIBLE);
				str = getActivity().getResources().getString(R.string.back_page);
				break;

			case 2 :
				_btnTopCaseChange.setVisibility(View.VISIBLE);
				str = getActivity().getResources().getString(R.string.item_case);
				break;
		}

		_viewCount.setText(str);
	}

	/**
	 * @param index
	 */
	private void collagePageSelected(int index) {

		SnapsPage page = _pageList.get(index);

		if (page.type.equalsIgnoreCase("cover")) {
			_btnTopLayoutChange.setVisibility(View.VISIBLE);
			_viewCount.setText(getActivity().getResources().getString(R.string.cover));
		} else if (page.type.equalsIgnoreCase("title")) {
			_btnTopLayoutChange.setVisibility(View.VISIBLE);
			_viewCount.setText(getActivity().getResources().getString(R.string.inner_title_page));
		} else {
			_btnTopLayoutChange.setVisibility(View.INVISIBLE);
			_viewCount.setText((index - 1) + " / " + (_pageList.size() - 2));
		}
	}

	private void fbbookPageSelected(int index) {// TODO fb : 하단의 커버,속지,페이지번호 라벨

		SnapsPage page = _pageList.get(index);

		if (page.type.equalsIgnoreCase("cover")) {// 커버 라벨
			_btnTopCaseChange.setVisibility(View.VISIBLE);
			_viewCount.setVisibility(View.VISIBLE);
			_viewCount.setText(getActivity().getResources().getString(R.string.cover));
		} else if (page.type.equalsIgnoreCase("title")) {// 속지 라벨
			_btnTopCaseChange.setVisibility(View.GONE);
			_viewCount.setVisibility(View.VISIBLE);
			_viewCount.setText(getActivity().getResources().getString(R.string.inner_title_page));
		} else {// 넘버 라벨
			_btnTopCaseChange.setVisibility(View.GONE);
			_viewCount.setVisibility(View.VISIBLE);
			_viewCount.setText((index - 1) + " / " + (_pageList.size() - 2));
		}
	}

	/**
	 * @param index
	 */
	private void stickerPageSelected(int index) {
		SnapsPage page = _pageList.get(index);

		if (page.type.equalsIgnoreCase("cover")) {
			_viewCount.setText(getActivity().getResources().getString(R.string.cover));
		} else {
			_viewCount.setText(index + " / " + (_pageList.size() - 1));
		}
	}

	/**
	 * 
	 * com.snaps.kakao.activity.edit.pager SnapsLoadPager.java
	 * 
	 * @author JaeMyung Park
	 * @Date : 2013. 6. 21.
	 * @Version :
	 */
	public class SnapsPageAdapter extends FragmentPagerAdapter {
		int mCount = 0;

		public SnapsPageAdapter(FragmentManager fm) {
			super(fm);
			mCount = _pageList.size();
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;

			if (position < _canvasList.size())
				fragment = _canvasList.get(position);
			if (fragment == null) {
				if (Config.isSnapsSticker())
					fragment = new StickerCanvasFragment();
				else if (Config.isThemeBook()) {
					fragment = new ThemeBookCanvasFragment();
					((ThemeBookCanvasFragment) fragment).setOnViewpagerListener((ISnapsPageItemInterface) _activity);
				}
			}

			if (fragment != null) {

				Bundle arg = new Bundle();
				arg.putInt("index", position);
				arg.putBoolean("pageLoad", true);
				arg.putBoolean("pageSave", false);
				fragment.setArguments(arg);

				if (!_progress.isShowing()) {
				}

				_canvasList.add(position, fragment);
			}

			return fragment;
		}

		@Override
		public int getCount() {
			Dlog.d("SnapsPageAdapter.getCount() page count:" + _pageList.size());
			return _pageList.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
			Dlog.d("SnapsPageAdapter.destroyItem() position:" + position);
			SnapsCanvasFragment scf = (SnapsCanvasFragment) object;
			ViewUnbindHelper.unbindReferences(scf.getView(), null, false);
			System.gc();
		}

	}
}
