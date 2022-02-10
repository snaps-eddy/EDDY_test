package com.snaps.mobile.activity.hamburger_menu.ui_by_language;

import android.view.View;

import com.snaps.mobile.R;

/**
 * Created by ysjeong on 16. 8. 16..
 */
public class SnapsHamburgerMenuUISetterForEng implements ISnapsHamburgerMenuUIByLanguageStrategy {
    @Override
    public View getConverterView(View parentView) {
        if (parentView == null) return null;

        View couponeLy =  parentView.findViewById(R.id.activity_hamburger_menu_coupon_ly);
        if (couponeLy != null) couponeLy.setVisibility(View.GONE);

        View diaryMenu =  parentView.findViewById(R.id.activity_hamburger_diary_utv);
        if (diaryMenu != null) diaryMenu.setVisibility(View.GONE);

        View noticeLy =  parentView.findViewById(R.id.activity_hamburger_menu_notice_ly);
        if (noticeLy != null) noticeLy.setVisibility(View.GONE);

        View eventLy =  parentView.findViewById(R.id.activity_hamburger_event_utv);
        if (eventLy != null) eventLy.setVisibility(View.GONE);

        return parentView;
    }
}
