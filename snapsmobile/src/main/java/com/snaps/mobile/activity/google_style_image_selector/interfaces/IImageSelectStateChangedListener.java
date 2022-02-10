package com.snaps.mobile.activity.google_style_image_selector.interfaces;

import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items.ImageSelectTrayCellItem;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2016. 11. 29..
 */

public interface IImageSelectStateChangedListener {
    enum eCONTROL_TYPE {
        TRAY,
        FRAGMENT,
        ALL_VIEW
    }

    void onRequestedFragmentChange(ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT fragmentType, int selectProdCode); //selectImageSrcFragment에서 항목 선택 시 처리

    void onItemUnSelectedListener(eCONTROL_TYPE controlType, String mapKey); //Item 선택 해제 처리

    void onTrayItemSelected(ImageSelectTrayCellItem item); //트레이 아이템을 선택 했을 때

    void onFragmentItemSelected(ImageSelectAdapterHolders.PhotoFragmentItemHolder fragmentViewHolder); //Fragment에서 아이템을 선택했을 때

    void onRequestedMakeAlbumList(ArrayList<IAlbumData> cursors); //앨범 리스트를 만들어 달라고 요청

    void onRequestRemovePrevAlbumInfo(); //이전 앨범 정보 제거

    void onSelectedAlbumList(IAlbumData cusor); //앨범 리스트를 선택 했을 때 호출 됨.
}
