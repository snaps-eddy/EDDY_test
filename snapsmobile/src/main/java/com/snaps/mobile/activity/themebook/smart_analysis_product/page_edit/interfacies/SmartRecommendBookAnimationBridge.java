package com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.interfacies;

import android.app.Activity;

import com.snaps.common.structure.SnapsTemplate;

public interface SmartRecommendBookAnimationBridge {
    Activity getActivity();
    SnapsTemplate getTemplate();
    void refreshUI();
}
