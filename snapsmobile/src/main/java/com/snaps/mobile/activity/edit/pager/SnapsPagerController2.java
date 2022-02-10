
package com.snaps.mobile.activity.edit.pager;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.imp.ISnapsPageItemInterface;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.mobile.activity.common.data.SnapsPageEditRequestInfo;
import com.snaps.mobile.activity.common.interfacies.SnapsEditActExternalConnectionBridge;
import com.snaps.mobile.activity.edit.PagerContainer;
import com.snaps.mobile.activity.edit.fragment.canvas.SimplePhotoBookCanvasFragment;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsCanvasFragment;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsCanvasFragmentFactory;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;

import java.util.ArrayList;

/**
 * com.snaps.kakao.activity.edit.pager SnapsLoadPager.java
 *
 * @author JaeMyung Park
 * @Date : 2013. 6. 21.
 * @Version :
 */
public class SnapsPagerController2 extends BaseSnapsPagerController {
    private static final String TAG = SnapsPagerController2.class.getSimpleName();
    private FragmentActivity _activity;
    private ArrayList<Fragment> _canvasList;
    private ArrayList<SnapsPage> _pageList;
    private PagerContainer _mContainer;
    private InterceptTouchableViewPager _mViewPager;
    private int _pagerSelected;

    public SnapsPageAdapter pageAdapter = null;

    private boolean _isLandscapeMode = false;

    private boolean _isPreview = false;

    private SnapsCommonResultListener<SnapsPageEditRequestInfo> itemClickListener = null;

//    public SnapsPagerController2(FragmentActivity activity, DialogDefaultProgress progress, int pageContainerID, int viewPagerID) {
//        _activity = activity;
//
//        _mContainer = (PagerContainer) getActivity().findViewById(pageContainerID);
//        _mViewPager = (InterceptTouchableViewPager) _mContainer.findViewById(viewPagerID);
//    }

    /**
     * @Marko
     * @DoneRefactoring
     */
    public SnapsPagerController2(FragmentActivity activity, PagerContainer pageContainer, InterceptTouchableViewPager viewPager) {
        _activity = activity;

        _mContainer = pageContainer;
        _mViewPager = viewPager;
    }

    public void setItemClickListener(SnapsCommonResultListener<SnapsPageEditRequestInfo> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setBackgroundColor(int color) {
        if (_mContainer != null)
            _mContainer.setBackgroundColor(color);
    }

    public ViewPager getViewPager() {
        return _mViewPager;
    }

    public PagerContainer getContainer() {
        return _mContainer;
    }

    public void setIsPreview(boolean _isPreview) {
        this._isPreview = _isPreview;
    }

    public void close() {
        try {
            if (_pageList != null) {
                for (int i = 0; i < _pageList.size(); i++)
                    _pageList.get(i).close();

                _pageList.clear();
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
     * 페이지 로드
     *
     * @param pageList   SnapsPageList
     * @param canvasList Fragment List
     * @param t          Top Margin
     * @param b          Bottom Margin
     * @param m          Left,Right Margin
     */
    public void loadPage(ArrayList<SnapsPage> pageList, ArrayList<Fragment> canvasList, int t, int b, int m) {
        loadPage(pageList, canvasList, t, b, m, false);
    }

    public void loadPage(ArrayList<SnapsPage> pageList, ArrayList<Fragment> canvasList, int t, int b, int m, boolean isLandScapeView) {

        if (canvasList != null && !canvasList.isEmpty()) {
            for (Fragment fragment : canvasList) {
                if (fragment instanceof SnapsCanvasFragment) {
                    ((SnapsCanvasFragment) fragment).destroyCanvas();
                }
            }
        }

        if (getActivity() == null) return; // 오류 예외처리.

        _isLandscapeMode = isLandScapeView;
        if (isLandScapeView)
            setLandscapePage(pageList, canvasList, t, b, m);
        else
            setPortraitPage(pageList, canvasList, t, b, m);
    }

    private void setLandscapePage(ArrayList<SnapsPage> pageList, ArrayList<Fragment> canvasList, int t, int b, int m) {
        _canvasList = canvasList;
        _pageList = pageList;

//		int screenWidth = UIUtil.getScreenHeight(getActivity());
//		int screenHeight = UIUtil.getScreenWidth(getActivity());

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) _mViewPager.getLayoutParams();

//		int width = screenWidth;// - UIUtil.convertDPtoPX(_activity, 106);//좌측 썸네일 바..;
//		int height = screenHeight - UIUtil.convertDPtoPX(_activity, 40);//상단바
//
//		if(Config.isExistThumbnailEditView() && !_isPreview) {
//			width = screenWidth - UIUtil.convertDPtoPX(_activity, 106);
//		}
//
//		Logg.d("width=" + width + " height=" + height);

        /****
         * 핵심...
         */
        params.width = FrameLayout.LayoutParams.MATCH_PARENT;
        params.height = FrameLayout.LayoutParams.MATCH_PARENT;

        pageAdapter = new SnapsPageAdapter(getActivity().getSupportFragmentManager());
        pageAdapter.setViewPager(_mViewPager);
        pageAdapter.setData(_pageList);
        _mViewPager.setAdapter(pageAdapter);
    }

    private void setPortraitPage(ArrayList<SnapsPage> pageList, ArrayList<Fragment> canvasList, int t, int b, int m) {
        _canvasList = canvasList;
        _pageList = pageList;

        /****
         * 핵심...
         */
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) _mViewPager.getLayoutParams();
        params.width = FrameLayout.LayoutParams.MATCH_PARENT;
        params.height = FrameLayout.LayoutParams.MATCH_PARENT;

        pageAdapter = new SnapsPageAdapter(getActivity().getSupportFragmentManager());
        pageAdapter.setViewPager(_mViewPager);
        pageAdapter.setData(_pageList);
        _mViewPager.setAdapter(pageAdapter);
        _mViewPager.setLayoutParams(params);
    }

    public void setPagerCurrentItem(int idx) {
        if (_mViewPager != null)
            _mViewPager.setCurrentItem(idx);
    }

    public void setPagerSelected(int pos) {
        _pagerSelected = pos;
    }

    public int getPagerSelected() {
        return _pagerSelected;
    }

    public void refresh(ArrayList<SnapsPage> pageList) {
        pageAdapter = new SnapsPageAdapter(getActivity().getSupportFragmentManager());
        _pageList = pageList;
        pageAdapter.setData(_pageList);
        _mViewPager.setAdapter(pageAdapter);
    }

    /**
     * com.snaps.kakao.activity.edit.pager SnapsLoadPager.java
     *
     * @author JaeMyung Park
     * @Date : 2013. 6. 21.
     * @Version :
     */
    public class SnapsPageAdapter extends FragmentStatePagerAdapter {

        public ArrayList<SnapsPage> pageList = null;
        private int mCount = 0;
        private InterceptTouchableViewPager viewPager = null;

        public SnapsPageAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setViewPager(InterceptTouchableViewPager viewPager) {
            this.viewPager = viewPager;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            if (Const_PRODUCT.isSNSBook(Config.getPROD_CODE())) {
                if (position < _canvasList.size())
                    fragment = _canvasList.get(position);
            }

            if (fragment == null) {
                fragment = new SnapsCanvasFragmentFactory().createCanvasFragment(Config.getPROD_CODE());
                if (fragment != null) {
                    SnapsCanvasFragment snapsCanvasFragment = (SnapsCanvasFragment) fragment;

                    if (_activity instanceof SnapsEditActExternalConnectionBridge)
                        snapsCanvasFragment.setOnViewpagerListener(((SnapsEditActExternalConnectionBridge) _activity).getProductEditorAPI());
                    else if (_activity instanceof ISnapsPageItemInterface)
                        snapsCanvasFragment.setOnViewpagerListener((ISnapsPageItemInterface) _activity);

                    snapsCanvasFragment.setViewPager(viewPager);
                    snapsCanvasFragment.setLandscapeMode(_isLandscapeMode);
                    snapsCanvasFragment.setIsPreview(_isPreview);
                    snapsCanvasFragment.setItemClickListener(itemClickListener);
                } else {
                    //Null이면, 이미 상태가 정상이 아니라고 보고 앱을 강제 종료 시킨다.
                    DataTransManager.notifyAppFinish(_activity);
                    return new SimplePhotoBookCanvasFragment(); //crash가 발생하지 않도록 더미 프레그먼트 생성
                }
            }

            Bundle arg = new Bundle();
            arg.putInt("index", position);

            if (_isPreview) {
                arg.putBoolean("pageSave", false);
                arg.putBoolean("pageLoad", false);
                arg.putBoolean("preThumbnail", false);
                arg.putBoolean("visibleButton", false);
            } else {
                arg.putBoolean("pageLoad", true);
                arg.putBoolean("pageSave", false);
            }

            if (fragment.getArguments() != null)
                fragment.getArguments().clear();

            fragment.setArguments(arg);

            if (Const_PRODUCT.isSNSBook(Config.getPROD_CODE())) {
                if (!_canvasList.contains(fragment)) {
                    _canvasList.add(fragment);
                }
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            try {
                super.destroyItem(container, position, object);
                Dlog.d("SnapsPageAdapter.destroyItem() position:" + position);
                if (object != null) {
                    SnapsCanvasFragment scf = (SnapsCanvasFragment) object;
                    scf.destroyCanvas();
                    ViewUnbindHelper.unbindReferences(scf.getView(), null, false);
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void setData(ArrayList<SnapsPage> pageList) {
            this.pageList = pageList;
            mCount = pageList == null ? 0 : pageList.size();
            try {
                notifyDataSetChanged();
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }
}
