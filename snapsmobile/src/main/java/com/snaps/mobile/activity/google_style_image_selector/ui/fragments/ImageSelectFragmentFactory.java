package com.snaps.mobile.activity.google_style_image_selector.ui.fragments;

import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.phone.GooglePhotoStylePhoneFragment;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.ImageSelectSNSPhotoFragment;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.photo_remove.FacebookPhotoBookPhotoRemoveFragment;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.photo_remove.KakaobookBookPhotoRemoveFragment;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.photo_remove.SnapsDiaryBookPhotoRemoveFragment;

/**
 * Created by ysjeong on 2016. 12. 5..
 */

public class ImageSelectFragmentFactory {

    public enum eIMAGE_SELECT_FRAGMENT {
        UNKNOWN,
        SELECT_IMAGE_SRC,
        SNAPS_STICKER,
        PHONE_DETAIL,
        KAKAO_BOOK_ALBUM,
        KAKAO_BOOK_DETAIL,
        FACE_BOOK_ALBUM,
        FACE_BOOK_DETAIL,
        INSTAGRAM_BOOK_DETAIL,
        GOOGLE_PHOTO_DETAIL,
        DIARY_ALBUM;
        
        public static eIMAGE_SELECT_FRAGMENT convertSelectProductToEnum(int selectedProduct) {
            switch (selectedProduct) {
                case Const_VALUES.SELECT_FACEBOOK:// 페북사진
                    return FACE_BOOK_DETAIL;
                case Const_VALUES.SELECT_PHONE:// 폰사진
                    return PHONE_DETAIL;
                case Const_VALUES.SELECT_SNAPS:// snaps사진
                    return SNAPS_STICKER;
                case Const_VALUES.SELECT_KAKAO:// 카카오사진
                    return KAKAO_BOOK_DETAIL;
                case Const_VALUES.SELECT_SDK_CUSTOMER:// sdk 현재는 키즈노트..
                case Const_VALUES.SELECT_INSTAGRAM:// 인스타그램.
                    return INSTAGRAM_BOOK_DETAIL;
                case Const_VALUES.SELECT_GOOGLEPHOTO:// 구글포토
                    return GOOGLE_PHOTO_DETAIL;
            }

            return UNKNOWN;
        }
    }

    /**
     * FIXME Fragment 통합 시키는 작업 필요 함.
     */
    public static ImageSelectBaseFragment createFragment(ImageSelectActivityV2 selectAct, eIMAGE_SELECT_FRAGMENT what) {
        switch (what) {
            case SELECT_IMAGE_SRC :
                return new SelectImageSrcFragment();
            case PHONE_DETAIL:
                return new GooglePhotoStylePhoneFragment();
            case FACE_BOOK_DETAIL:
            case KAKAO_BOOK_DETAIL:
            case INSTAGRAM_BOOK_DETAIL:
            case GOOGLE_PHOTO_DETAIL:
            case SNAPS_STICKER:
                return ImageSelectSNSPhotoFragment.newInstance(selectAct, what);
            case KAKAO_BOOK_ALBUM: //SNS 제외하기 쪽은 구조는 유지하고 RecyclerView로만 교체함.
                return KakaobookBookPhotoRemoveFragment.getInstance();
            case FACE_BOOK_ALBUM:
                return FacebookPhotoBookPhotoRemoveFragment.getInstance();
            case DIARY_ALBUM:
                return SnapsDiaryBookPhotoRemoveFragment.getInstance();
        }

        return null;
    }
}
