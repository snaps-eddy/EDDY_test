package com.snaps.mobile.activity.themebook.design_list;

import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kmshack.newsstand.ScrollTabHolderFragment;
import com.snaps.common.structure.photoprint.GridSpacingItemDecoration;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.themebook.design_list.adapter.BaseThemeDesignListAdapter;
import com.snaps.mobile.activity.themebook.design_list.adapter.ThemeDesignListAdapterFactory;

import java.util.List;

/**
 * Created by kimduckwon on 2017. 11. 29..
 */

public class NewThemeDesignListFragment extends ScrollTabHolderFragment {

    boolean m_isLandScapeMode = false;
    public DialogDefaultProgress pageProgress;
    int selectedIndex = -1; // -1선택이 되어있지 않을상태... 최소 0시작

    NewThemeDesignListActivity designListActivity = null;
    BaseThemeDesignListAdapter designListAdapter = null;
    BaseThemeDesignListAdapter.DesignListAdapterAttribute attribute;

    public static NewThemeDesignListFragment newInstance(NewThemeDesignListActivity designListActivity, BaseThemeDesignListAdapter.DesignListAdapterAttribute attribute) {
        NewThemeDesignListFragment fragment = new NewThemeDesignListFragment();
        fragment.designListActivity = designListActivity;
        fragment.attribute = attribute;
        return fragment;
    }

    public NewThemeDesignListFragment() {
    }

    public void set(boolean landscapeMode, DialogDefaultProgress pageProgress) {
        this.m_isLandScapeMode = landscapeMode;
        this.pageProgress = pageProgress;
    }

    public void setSelectedIndex(int idx) {
        this.selectedIndex = idx;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void notifyDataSetChanged() {
        if (designListAdapter != null)
            designListAdapter.notifyDataSetChanged();
    }

    public List getSelectData() {
        if (designListAdapter == null) {
            return null;
        }

        return designListAdapter.getSelectList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theme_design_list, null);
        if (attribute != null) {
            RecyclerView designListView = (RecyclerView) view.findViewById(R.id.themeCoverList);
            int spacing = UIUtil.convertDPtoPX(getContext(), attribute.getLayoutSpacing());
            int padding = UIUtil.convertDPtoPX(getContext(), attribute.getLayoutPadding());

            GridSpacingItemDecoration gridSpacingItemDecoration = new GridSpacingItemDecoration(spacing, spacing, spacing, spacing, padding, padding);
            designListView.setLayoutManager(new GridLayoutManager(getContext(), attribute.getSpanCount()));
            designListView.addItemDecoration(gridSpacingItemDecoration);
            designListView.setPadding(padding, padding, padding, padding);
            if (Const_PRODUCT.isNewYearsCardProduct() || Const_PRODUCT.isCardProduct() || Const_PRODUCT.isStikerGroupProduct()
                    || Const_PRODUCT.isPosterGroupProduct() || Const_PRODUCT.isAccordionCardProduct() || Const_PRODUCT.isSloganProduct()
                    || Const_PRODUCT.isBabyNameStikerGroupProduct() || Const_PRODUCT.isMiniBannerProduct() || Const_PRODUCT.isTransparencyPhotoCardProduct()) {
                designListView.setBackgroundColor(Color.WHITE);
            }
            designListAdapter = ThemeDesignListAdapterFactory.createAdapter(getActivity(), attribute);
            designListView.setAdapter(designListAdapter);
        }

        return view;
    }

    @Override
    public void adjustScroll(int scrollHeight) {

    }
}
