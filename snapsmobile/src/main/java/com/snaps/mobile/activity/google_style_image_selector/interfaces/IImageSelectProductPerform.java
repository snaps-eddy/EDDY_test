package com.snaps.mobile.activity.google_style_image_selector.interfaces;

import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;

/**
 * Created by ysjeong on 2016. 11. 21..
 */
public interface IImageSelectProductPerform {

    //상품별 Fragment 생성
    ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT performGetDefaultFragmentType();

    //상품별로 버튼을 눌렀을 때,
    void onClickedNextBtn();

    void moveNextActivity();

}
