package com.snaps.mobile.activity.themebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStripForSticky;
import com.kmshack.newsstand.ScrollTabHolder;
import com.kmshack.newsstand.ScrollTabHolderFragment;
import com.snaps.common.model.WebViewPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.Xml_ThemePage;
import com.snaps.common.utils.net.xml.bean.Xml_ThemePage.ThemePage;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.home.fragment.FragmentViewPager;
import com.snaps.mobile.activity.themebook.holder.ThemeCoverHolder;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;
import com.snaps.mobile.component.CustomSensitivityViewPager;
import com.snaps.mobile.component.EndlessPagerBaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class ThemeDesignListActivity extends SnapsBaseFragmentActivity implements ViewPager.OnPageChangeListener, ScrollTabHolder, View.OnClickListener {
	private static final String TAG = ThemeDesignListActivity.class.getSimpleName();
	public enum eDesignPhotoCnt {
		CURRENT,
		PHOTO_01_OR_02,
		PHOTO_03_OR_04,
		PHOTO_05_OR_06,
		PHOTO_07_OR_MORE;
		
		public static int[] getValue(eDesignPhotoCnt design) {
			switch (design) {
			case PHOTO_01_OR_02:
				return new int[] { 1, 2 };
			case PHOTO_03_OR_04:
				return new int[] { 3, 4 };
			case PHOTO_05_OR_06:
				return new int[] { 5, 6 };
			case PHOTO_07_OR_MORE:
				return new int[] { 7 };
			case CURRENT:
			default :
				return null;
			}
		}
	}
	
	private static final int VIEW_COUNT = 5;
	
	private eDesignPhotoCnt mCurDesignCnt = eDesignPhotoCnt.CURRENT;
	private Map<eDesignPhotoCnt, List<ThemePage>> mapDesigns = null;
	
	private TextView mNextBtn;
	private ImageView mPreBtn;
	private TextView mThemeTitle;

	public Xml_ThemePage xmlThemeCurrentDesignPage;
	public Xml_ThemePage xmlThemeTotalPage;
	public boolean isSelect = false;
	private ThemeCoverHolder vh;
	public float mRatio = 0.0f;
	public String mParamSide = null;
	
	private CustomSensitivityViewPager mPager = null;
	private DesignListPageAdapter mAdapter = null;
	
	boolean m_isLandScapeMode = false;

	public DialogDefaultProgress pageProgress;
	private PagerSlidingTabStripForSticky mPagerSlidingTabStrip;

	private Map<eDesignPhotoCnt, ThemeDesignListFragment> mapListFragment;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));
		
		m_isLandScapeMode = UIUtil.fixCurrentOrientationAndReturnBoolLandScape(this);
		if (m_isLandScapeMode) {
			UIUtil.updateFullscreenStatus(this, true);
		} else {
			UIUtil.updateFullscreenStatus(this, false);
		}
		
		setContentView(R.layout.activity_theme_design_list);

		pageProgress = new DialogDefaultProgress(this);

		mPagerSlidingTabStrip = (PagerSlidingTabStripForSticky) findViewById(R.id.activity_theme_design_list_pager_strip);

		mapDesigns = new HashMap<>();
		mapListFragment = new HashMap<>();

		mPager = (CustomSensitivityViewPager) findViewById(R.id.design_pager);

		mThemeTitle = (TextView) findViewById(R.id.ThemeTitleText);
		setTitleText();

		mNextBtn = (TextView) findViewById(R.id.ThemebtnTopNext);
		mNextBtn.setText(getString(R.string.confirm));
		mPreBtn = (ImageView) findViewById(R.id.ThemeTitleLeft);
		
		if(findViewById(R.id.ThemeTitleLeftLy) != null) {
			findViewById(R.id.ThemeTitleLeftLy).setOnClickListener(this);
		}

		if (Config.isSimplePhotoBook() || Config.isSimpleMakingBook()) //FIXME ????
			mRatio = getIntent().getFloatExtra("pageRatio", 0.0f);

		mParamSide = getIntent().getStringExtra("prmSide");

		mPreBtn.setOnClickListener(this);

		mNextBtn.setOnClickListener(this);

		setLayoutState();

		loadDesignTemplate();
	}

	private void setTitleText() {
		if (Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct()) {
			mThemeTitle.setText(getString(R.string.change_design_text));
		} else {
			mThemeTitle.setText(getString(R.string.design_list));
		}
	}

	private boolean isSuccessLoadDesignList() {
		return (isSinglePage() && xmlThemeCurrentDesignPage != null) || (isMultiPage() && xmlThemeCurrentDesignPage != null && xmlThemeTotalPage != null);
	}

	private void loadDesignTemplate() {
		ATask.executeVoidDefProgress(ThemeDesignListActivity.this, new ATask.OnTask() {

			@Override
			public void onPre() {
			}

			@Override
			public void onBG() {
				loadDesignList();
			}

			private void loadDesignList() {
				String prmTmplClssCode = "045020";

				if (isSinglePage()) {
					xmlThemeCurrentDesignPage = GetParsedXml.getPhotoBookPage(Config.getPROD_CODE(), prmTmplClssCode, null, mParamSide, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
				} else {
					xmlThemeCurrentDesignPage = GetParsedXml.getPhotoBookPage(Config.getPROD_CODE(), prmTmplClssCode, Config.getTMPL_CODE(), mParamSide, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
					xmlThemeTotalPage = GetParsedXml.getPhotoBookPage(Config.getPROD_CODE(), prmTmplClssCode, null, mParamSide, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
				}
			}

			@Override
			public void onPost() {
				if (isSuccessLoadDesignList()) {
					setDesignDataList();
				} else {
					Toast.makeText(ThemeDesignListActivity.this, R.string.loading_fail, Toast.LENGTH_LONG).show();
				}
			}

			private void setDesignDataList() {
				if (isSinglePage()) {
					setSingleDesignDataList();
				} else {
					setMultiDesignDataList();
				}
			}

			private void setSingleDesignDataList() {
				String currentDesign = getString(R.string.current_design);

				ArrayList<WebViewPage> pages = new ArrayList<WebViewPage>();
				pages.add(new WebViewPage(currentDesign, ""));

				putDesignListToMap();

				mAdapter = new DesignListPageAdapter(ThemeDesignListActivity.this.getSupportFragmentManager(), ThemeDesignListActivity.this, pages, mPager);
				mPager.setAdapter(mAdapter);

				mCurDesignCnt = eDesignPhotoCnt.CURRENT;
			}

			private void setMultiDesignDataList() {
				String currentDesign = getString(R.string.current_design);
				String oneTwoPagerCount = "1~2" + getString(R.string.paper_count_unit);
				String threeFourPagerCount = "3~4" + getString(R.string.paper_count_unit);
				String fiveSixPagerCount = "5~6" + getString(R.string.paper_count_unit);
				String sevenPagerCount = "7" + getString(R.string.paper_count_unit) + getString(R.string.more_than);

				ArrayList<WebViewPage> pages = new ArrayList<WebViewPage>();
				pages.add(new WebViewPage(currentDesign, ""));
				pages.add(new WebViewPage(oneTwoPagerCount, ""));
				pages.add(new WebViewPage(threeFourPagerCount, ""));
				pages.add(new WebViewPage(fiveSixPagerCount, ""));
				pages.add(new WebViewPage(sevenPagerCount, ""));

				putDesignListToMap();

				mAdapter = new DesignListPageAdapter(ThemeDesignListActivity.this.getSupportFragmentManager(), ThemeDesignListActivity.this, pages, mPager);
				mPager.setAdapter(mAdapter);

				mPagerSlidingTabStrip.setViewPager(mPager);
				mPagerSlidingTabStrip.setOnPageChangeListener(ThemeDesignListActivity.this);

				mCurDesignCnt = eDesignPhotoCnt.CURRENT;
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ThemebtnTopNext) {
			performNextButton();
		} else if (v.getId() == R.id.ThemeTitleLeftLy || v.getId() == R.id.ThemeTitleLeft) {
			performBackButton();
		}
	}

	private void performBackButton() {
		finish();
	}

	private void performNextButton() {
		ThemePage pageData = getSelectedPageData();

		if (pageData != null) {
			Intent data = new Intent();

			data.putExtra("pageXMLPATH", pageData.F_XML_PATH);

			setResult(RESULT_OK, data);
			finish();
		} else {
			MessageUtil.toast(getApplicationContext(), R.string.theme_page_select);
		}
	}

	private void setLayoutState() {
		if (isSinglePage()) {
			setSinglePageLayout();
		} else {
			setMultiPageLayout();
		}
	}

	private boolean isSinglePage() {
		return Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct();
	}

	private boolean isMultiPage() {
		return !isSinglePage();
	}

	private void setSinglePageLayout() {
		View stripLayout = findViewById(R.id.activity_theme_design_list_strip_area_ly);
		stripLayout.setVisibility(View.GONE);
		mPager.setOffscreenPageLimit(1);
	}

	private void setMultiPageLayout() {
		if (Config.useKorean())
			mPagerSlidingTabStrip.setTabType(PagerSlidingTabStripForSticky.TAB_TYPE_DEFAULT);
		else
			mPagerSlidingTabStrip.setTabType(PagerSlidingTabStripForSticky.TAB_TYPE_EXPANDED);

		mPager.setOffscreenPageLimit(VIEW_COUNT - 1);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	public ThemeDesignListFragment getCurrentListFragment() {
		if (mapListFragment == null || !mapListFragment.containsKey(mCurDesignCnt)) return null;
		return mapListFragment.get(mCurDesignCnt);
	}


	@Override
	public void onPageSelected(int pos) {

		//페이저가 넘어갈때는 선택된 아이템 체크를 해제핸다.
		unSelecteAllItem();
		mCurDesignCnt = getDesign(pos);

		ThemeDesignListFragment listFragment = getCurrentListFragment();
		if (listFragment != null)
			listFragment.notifyDataSetChanged();
	}

	@Override
	public void adjustScroll(int scrollHeight) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ThemeDesignListFragment listFragment = getCurrentListFragment();
		if (listFragment != null)
			listFragment.destroy();
	}

	private void unSelecteAllItem() {
		ThemeDesignListFragment listFragment = getCurrentListFragment();
		for (int ii = 0; ii < eDesignPhotoCnt.values().length; ii++) {
			List<ThemePage> list = getDesignListFromMap(eDesignPhotoCnt.values()[ii]);

			if(list == null || list.isEmpty())  return;
			if (listFragment != null)
				listFragment.setSelectedIndex(-1);
			for(ThemePage page : list) {
				if(page != null)
					page.F_IS_SELECT = false;
			}
		}

		if (listFragment != null)
			listFragment.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	/***
	 * 
	 * @return
	 */
	ThemePage getSelectedPageData() {
		int selectedIndex = -1;

		ThemeDesignListFragment listFragment = getCurrentListFragment();
		if (listFragment != null)
			selectedIndex = listFragment.getSelectedIndex();

		if (selectedIndex == -1)
			return null;
		return getDesignItem(mCurDesignCnt, selectedIndex);
	}

	public ThemePage getDesignItem(eDesignPhotoCnt design, int pos) {
		List<ThemePage> arrDesignList = getDesignListFromMap(design);
		return arrDesignList != null  ? arrDesignList.get(pos) : null;
	}

	public List<ThemePage> getCurrentDesignList() {
		return getDesignListFromMap(mCurDesignCnt);
	}

	public List<ThemePage> getDesignListFromMap(eDesignPhotoCnt design) {
		if (mapDesigns == null || !mapDesigns.containsKey(design)) return null;
		return mapDesigns.get(design);
	}

	private List<ThemePage> getDesignList(eDesignPhotoCnt design) {
		List<ThemePage> arrDesignList = new ArrayList<Xml_ThemePage.ThemePage>();

		if(!isSuccessLoadDesignList()) return arrDesignList;

		if(design == eDesignPhotoCnt.CURRENT) {
			for(ThemePage page : xmlThemeCurrentDesignPage.bgList) {
				if(page == null) continue;
				arrDesignList.add(page);
			}
		} else {
			for(ThemePage page : xmlThemeTotalPage.bgList) {
				if(page == null) continue;

				int pageCnt = 0;
				try {
					if(page.F_MASK_CNT != null && page.F_MASK_CNT.trim().length() > 0)
						pageCnt = Integer.parseInt(page.F_MASK_CNT);
				} catch (NumberFormatException e) {
					Dlog.e(TAG, e);
				}

				int[] values = null;
				switch (design) {
					case PHOTO_01_OR_02:
					case PHOTO_03_OR_04:
					case PHOTO_05_OR_06:
						values = eDesignPhotoCnt.getValue(design);
						if (values != null) {
							for(int value : values) {
								if(value == pageCnt)
									arrDesignList.add(page);
							}
						}
						break;
					case PHOTO_07_OR_MORE:
						if(pageCnt >= eDesignPhotoCnt.getValue(eDesignPhotoCnt.PHOTO_07_OR_MORE)[0]) {
							arrDesignList.add(page);
						}
						break;
					default:
						break;
				}
			}
		}

		return arrDesignList;
	}
	
	private void putDesignListToMap() {
		if (mapDesigns == null) return;
		mapDesigns.put(eDesignPhotoCnt.CURRENT, getDesignList(eDesignPhotoCnt.CURRENT));

		if (isMultiPage()) {
			mapDesigns.put(eDesignPhotoCnt.PHOTO_01_OR_02, getDesignList(eDesignPhotoCnt.PHOTO_01_OR_02));
			mapDesigns.put(eDesignPhotoCnt.PHOTO_03_OR_04, getDesignList(eDesignPhotoCnt.PHOTO_03_OR_04));
			mapDesigns.put(eDesignPhotoCnt.PHOTO_05_OR_06, getDesignList(eDesignPhotoCnt.PHOTO_05_OR_06));
			mapDesigns.put(eDesignPhotoCnt.PHOTO_07_OR_MORE, getDesignList(eDesignPhotoCnt.PHOTO_07_OR_MORE));
		}
	}
	
	public eDesignPhotoCnt getDesign(int position) {
		eDesignPhotoCnt design = null;
		switch (position) {
		case 1:
			design = eDesignPhotoCnt.PHOTO_01_OR_02;
			break;
		case 2:
			design = eDesignPhotoCnt.PHOTO_03_OR_04;
			break;
		case 3:
			design = eDesignPhotoCnt.PHOTO_05_OR_06;
			break;
		case 4:
			design = eDesignPhotoCnt.PHOTO_07_OR_MORE;
			break;
		default:
			design = eDesignPhotoCnt.CURRENT;
			break;
		}
		return design;
	}

	public class DesignListPageAdapter extends EndlessPagerBaseAdapter {
		private Activity activity;

		private FragmentManager fm;
		private FragmentViewPager viewPager;
		private FragmentTransaction transaction;

		private ArrayList<WebViewPage> pages;

		public DesignListPageAdapter(FragmentManager fm, Activity activity, ArrayList<WebViewPage> pages, FragmentViewPager viewPager ) {
			super(fm);

			this.activity = activity;
			this.fm = fm;
			this.pages = pages;
			this.viewPager = viewPager;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			if( fm == null )  return null;

			if (transaction == null) transaction = fm.beginTransaction();

			String name = FragmentViewPager.makeFragmentName(container.getId(), position);
			Fragment fragment = fm.findFragmentByTag(name);
			if ( fragment != null ) transaction.attach( fragment );
			else {
				fragment = (ScrollTabHolderFragment) super.instantiateItem( container, position );
				transaction.add(container.getId(), fragment, FragmentViewPager.makeFragmentName(container.getId(), position));
			}

			return fragment;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			if( fm == null ) return;

			if (transaction == null) transaction = fm.beginTransaction();
			transaction.detach( (Fragment)object );
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return pages.get( position ).title;
		}

		public boolean isBadgeExist( int position ) {
			return pages.get( position ).isBadgeExist;
		}

		@Override
		public int getCount() {
			return getDataCount();
		}

		public int getDataCount() {
			return pages == null ? 0 : pages.size();
		}

		@Override
		public Fragment getItem(final int position) {
			eDesignPhotoCnt designPhotoCnt = getDesign(position);

			if(mapListFragment == null) return null;

			if (mapListFragment.containsKey(designPhotoCnt)) return mapListFragment.get(designPhotoCnt);

			ThemeDesignListFragment listFragment = ThemeDesignListFragment.newInstance(ThemeDesignListActivity.this, designPhotoCnt);
			listFragment.set(m_isLandScapeMode, pageProgress, vh);
			mapListFragment.put(designPhotoCnt, listFragment);

			return listFragment;
		}
	}
}
