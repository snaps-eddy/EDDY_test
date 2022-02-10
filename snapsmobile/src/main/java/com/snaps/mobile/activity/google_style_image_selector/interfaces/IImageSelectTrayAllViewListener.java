package com.snaps.mobile.activity.google_style_image_selector.interfaces;

/**
 * Created by ysjeong on 2016. 12. 15..
 */

public interface IImageSelectTrayAllViewListener {
    void onOccurredAnyMotion(ISnapsImageSelectConstants.eTRAY_CELL_STATE cellState); //클릭하거나, 페이지를 추가하거나 기타 등등 아무 동작을 했을 경우 호출
}
