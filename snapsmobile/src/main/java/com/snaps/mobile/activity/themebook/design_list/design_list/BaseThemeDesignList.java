package com.snaps.mobile.activity.themebook.design_list.design_list;

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
import com.kmshack.newsstand.ScrollTabHolderFragment;
import com.snaps.common.model.WebViewPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;

import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.home.fragment.FragmentViewPager;
import com.snaps.mobile.activity.themebook.design_list.adapter.BaseThemeDesignListAdapter;
import com.snaps.mobile.activity.themebook.design_list.Interface.ThemeDesignListAPI;
import com.snaps.mobile.activity.themebook.design_list.NewThemeDesignListActivity;
import com.snaps.mobile.activity.themebook.design_list.NewThemeDesignListFragment;
import com.snaps.mobile.component.CustomSensitivityViewPager;
import com.snaps.mobile.component.EndlessPagerBaseAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.SELECT_MODE.MULTI_SELECT_FIRST_ADD_DESIGN;

/**
 * Created by kimduckwon on 2017. 11. 29..
 */

public abstract class BaseThemeDesignList extends ThemeDesignListAPI implements ViewPager.OnPageChangeListener {

    public enum eDesignPhotoCnt {
        CURRENT,
        PHOTO_01_OR_02,
        PHOTO_03_OR_04,
        PHOTO_05_OR_06,
        PHOTO_07_OR_MORE;

        public static int[] getValue(BaseThemeDesignList.eDesignPhotoCnt design) {
            switch (design) {
                case PHOTO_01_OR_02:
                    return new int[]{1, 2};
                case PHOTO_03_OR_04:
                    return new int[]{3, 4};
                case PHOTO_05_OR_06:
                    return new int[]{5, 6};
                case PHOTO_07_OR_MORE:
                    return new int[]{7};
                case CURRENT:
                default:
                    return null;
            }
        }
    }

    BaseThemeDesignList.SELECT_MODE mode = MULTI_SELECT_FIRST_ADD_DESIGN;
    NewThemeDesignListActivity activity = null;
    Map<BaseThemeDesignList.eDesignPhotoCnt, List> mapDesigns = null;
    Map<Integer, NewThemeDesignListFragment> mapListFragment;
    public DialogDefaultProgress pageProgress;
    private PagerSlidingTabStripForSticky mPagerSlidingTabStrip;
    boolean m_isLandScapeMode = false;
    private TextView mNextBtn;
    private ImageView mPreBtn;
    private TextView mThemeTitle;
    public TextView textViewCount;
    private CustomSensitivityViewPager mPager = null;
    private DesignListPageAdapter mAdapter = null;
    private int currentPosition = 0;

    public enum SELECT_MODE {
        SINGLE_SELECT_CHANGE_DESIGN, SINGLE_SELECT_ADD_DESIGN, MULTI_SELECT_ADD_DESIGN, MULTI_SELECT_FIRST_ADD_DESIGN
    }

    BaseThemeDesignList(NewThemeDesignListActivity fragmentActivity) {
        this.activity = fragmentActivity;
    }

    @Override
    public abstract void performNextButton();

    @Override
    public abstract boolean isSuccessLoadDesignList();

    @Override
    public abstract void loadDesignList();

    public abstract List<WebViewPage> setPage();

    @Override
    public abstract int getLimitViewCount();

    @Override
    public abstract BaseThemeDesignListAdapter.DesignListAdapterAttribute getAttribute(int position);

    @Override
    public abstract void getIntent();

    @Override
    public void onCreate() {
        getIntent();
        init();
        setTitleText();
    }

    private void init() {

        m_isLandScapeMode = UIUtil.fixCurrentOrientationAndReturnBoolLandScape(activity);

        if (m_isLandScapeMode) {
            UIUtil.updateFullscreenStatus(activity, true);
        } else {
            UIUtil.updateFullscreenStatus(activity, false);
        }

        activity.setContentView(setContentLayoutView());
//        pageProgress = new DialogDefaultProgress(activity);

        mPagerSlidingTabStrip = (PagerSlidingTabStripForSticky) activity.findViewById(R.id.activity_theme_design_list_pager_strip);

        mapDesigns = new HashMap<>();
        mapListFragment = new HashMap<>();

        mPager = (CustomSensitivityViewPager) activity.findViewById(R.id.design_pager);

        mThemeTitle = (TextView) activity.findViewById(R.id.ThemeTitleText);

        mNextBtn = (TextView) activity.findViewById(R.id.ThemebtnTopNext);
        mNextBtn.setText(activity.getString(R.string.confirm));
        mPreBtn = (ImageView) activity.findViewById(R.id.ThemeTitleLeft);
        textViewCount = (TextView) activity.findViewById(R.id.ThemeSelectCountText);

        if (activity.findViewById(R.id.ThemeTitleLeftLy) != null) {
            activity.findViewById(R.id.ThemeTitleLeftLy).setOnClickListener(this);
        }

        mPreBtn.setOnClickListener(this);

        mNextBtn.setOnClickListener(this);

        loadDesignTemplate();
    }


    public int setContentLayoutView() {
        return R.layout.activity_theme_design_list;
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void loadDesignTemplate() {
        ATask.executeVoidDefProgress(activity, new ATask.OnTask() {

            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                loadDesignList();
            }


            @Override
            public void onPost() {
                if (isSuccessLoadDesignList()) {
                    setDesignDataList();
                } else {
                    Toast.makeText(activity, R.string.loading_fail, Toast.LENGTH_LONG).show();
                }
            }

            private void setDesignDataList() {
                setLayoutState();
                if (isSinglePage()) {
                    setSingleDesignDataList();
                } else {
                    setMultiDesignDataList();
                }
            }
        });
    }

    ;

    @Override
    public void setTitleText() {
        if (Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isTransparencyPhotoCardProduct()) {
            mThemeTitle.setText(activity.getString(R.string.change_design_text));
        } else {
            mThemeTitle.setText(activity.getString(R.string.design_list));
        }
    }

    @Override
    public void performBackButton() {
        activity.finish();
    }

    ;

    @Override
    public void setLayoutState() {
        if (isSinglePage()) {
            setSinglePageLayout();
        } else {
            setMultiPageLayout();
        }
    }

    @Override
    public boolean isSinglePage() {
        //KT 북 - 디자인 변경 화면 디자인 리스트 축소
        if (Config.isKTBook()) {
            return true;
        }
        return Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isNewYearsCardProduct()
                || Const_PRODUCT.isStikerGroupProduct() || Const_PRODUCT.isPosterGroupProduct() || Const_PRODUCT.isAccordionCardProduct()
                || Const_PRODUCT.isSloganProduct() || Const_PRODUCT.isBabyNameStikerGroupProduct() || Const_PRODUCT.isMiniBannerProduct()
                || Const_PRODUCT.isTransparencyPhotoCardProduct() || Const_PRODUCT.isLegacyPhoneCaseProduct();
    }

    @Override
    public boolean isMultiPage() {
        return !isSinglePage();
    }

    @Override
    public void setSinglePageLayout() {

        View stripLayout = activity.findViewById(R.id.activity_theme_design_list_strip_area_ly);
        stripLayout.setVisibility(View.GONE);
        mPager.setOffscreenPageLimit(1);
    }

    @Override
    public void setMultiPageLayout() {
        if (Config.useKorean())
            mPagerSlidingTabStrip.setTabType(PagerSlidingTabStripForSticky.TAB_TYPE_DEFAULT);
        else
            mPagerSlidingTabStrip.setTabType(PagerSlidingTabStripForSticky.TAB_TYPE_EXPANDED);

        mPager.setOffscreenPageLimit(getLimitViewCount() - 1);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ThemebtnTopNext) {
            performNextButton();
        } else if (view.getId() == R.id.ThemeTitleLeftLy || view.getId() == R.id.ThemeTitleLeft) {
            performBackButton();
        }
    }

    @Override
    public void setSingleDesignDataList() {
        mAdapter = new DesignListPageAdapter(activity.getSupportFragmentManager(), activity, setPage(), mPager);
        mPager.setAdapter(mAdapter);
    }

    ;

    @Override
    public void setMultiDesignDataList() {
        putDesignListToMap();
        mAdapter = new DesignListPageAdapter(activity.getSupportFragmentManager(), activity, setPage(), mPager);
        mPager.setAdapter(mAdapter);
        mPagerSlidingTabStrip.setViewPager(mPager);
        mPagerSlidingTabStrip.setOnPageChangeListener(this);
    }

    ;

    public void putDesignListToMap() {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void adjustScroll(int scrollHeight) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
    }

    @Override
    public List getSelectData() {
        if (mAdapter == null) return null;
        NewThemeDesignListFragment fragment = (NewThemeDesignListFragment) mAdapter.getItem(currentPosition);
        if (fragment == null) {
            return null;
        } else {
            return fragment.getSelectData();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {

    }

    public class DesignListPageAdapter extends EndlessPagerBaseAdapter {
        private NewThemeDesignListActivity activity;

        private FragmentManager fm;
        private FragmentViewPager viewPager;
        private FragmentTransaction transaction;

        private List<WebViewPage> pages;

        public DesignListPageAdapter(FragmentManager fm, NewThemeDesignListActivity activity, List<WebViewPage> pages, FragmentViewPager viewPager) {
            super(fm);

            this.activity = activity;
            this.fm = fm;
            this.pages = pages;
            this.viewPager = viewPager;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (fm == null) return null;

            if (transaction == null) transaction = fm.beginTransaction();

            String name = FragmentViewPager.makeFragmentName(container.getId(), position);
            Fragment fragment = fm.findFragmentByTag(name);
            if (fragment != null) transaction.attach(fragment);
            else {
                fragment = (ScrollTabHolderFragment) super.instantiateItem(container, position);
                transaction.add(container.getId(), fragment, FragmentViewPager.makeFragmentName(container.getId(), position));
            }

            return fragment;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            if (fm == null) return;

            if (transaction == null) transaction = fm.beginTransaction();
            transaction.detach((Fragment) object);
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
            return getDataCount();
        }

        public int getDataCount() {
            return pages == null ? 0 : pages.size();
        }

        @Override
        public Fragment getItem(final int position) {
            if(mapListFragment == null) return null;
            if (mapListFragment.containsKey(position)) return mapListFragment.get(position);

            NewThemeDesignListFragment listFragment = NewThemeDesignListFragment.newInstance(activity, getAttribute(position));
            mapListFragment.put(position, listFragment);
            return listFragment;
        }
    }
}
