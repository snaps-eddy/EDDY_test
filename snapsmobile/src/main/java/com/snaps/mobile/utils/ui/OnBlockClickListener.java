package com.snaps.mobile.utils.ui;

import android.view.View;

import com.snaps.common.utils.ui.UIUtil;

public abstract class OnBlockClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
        UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);
        onBlockClick(v);
    }

    public abstract void onBlockClick(View v);

}
