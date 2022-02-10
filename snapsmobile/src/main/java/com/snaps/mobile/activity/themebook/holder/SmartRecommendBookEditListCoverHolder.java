package com.snaps.mobile.activity.themebook.holder;

import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.snaps.mobile.R;

import font.FTextView;

public class SmartRecommendBookEditListCoverHolder extends RecyclerView.ViewHolder {

    private FTextView pageCountView;
    private FTextView photoCountView;
    private FTextView editBtn;
    private ViewPager coverViewPager;
    private LinearLayout indicatorLayout;

    public SmartRecommendBookEditListCoverHolder(View itemView) {
        super(itemView);

        pageCountView = (FTextView) itemView.findViewById(R.id.smart_snaps_analysis_product_edit_list_cover_page_info_tv);
        photoCountView = (FTextView) itemView.findViewById(R.id.smart_snaps_analysis_product_edit_list_cover_photo_info_tv);
        coverViewPager = (ViewPager) itemView.findViewById(R.id.smart_snaps_analysis_product_edit_list_cover_view_pager);
        editBtn = (FTextView) itemView.findViewById(R.id.smart_snaps_analysis_product_edit_list_cover_edit_btn);
        indicatorLayout = (LinearLayout) itemView.findViewById(R.id.smart_snaps_analysis_product_edit_list_cover_indicator_layout);
    }

    public FTextView getPageCountView() {
        return pageCountView;
    }

    public FTextView getPhotoCountView() {
        return photoCountView;
    }

    public LinearLayout getIndicatorLayout() {
        return indicatorLayout;
    }

    public ViewPager getCoverViewPager() {
        return coverViewPager;
    }

    public FTextView getEditBtn() {
        return editBtn;
    }
}
