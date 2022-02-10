package com.snaps.mobile.activity.themebook.holder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.snaps.mobile.R;

import font.FTextView;

public class SmartRecommendBookEditListPageHolder extends RecyclerView.ViewHolder {

    private SnapsCanvasContainerLayout canvasParentLayout;
    private FTextView label;

    public SmartRecommendBookEditListPageHolder(View itemView) {
        super(itemView);

        canvasParentLayout = (SnapsCanvasContainerLayout) itemView.findViewById(R.id.smart_snaps_analysis_product_edit_list_page_fragment_ly);
        label = (FTextView) itemView.findViewById(R.id.smart_snaps_analysis_product_edit_list_page_label_tv);
    }

    public SnapsCanvasContainerLayout getCanvasParentLayout() {
        return canvasParentLayout;
    }

    public FTextView getLabel() {
        return label;
    }
}
