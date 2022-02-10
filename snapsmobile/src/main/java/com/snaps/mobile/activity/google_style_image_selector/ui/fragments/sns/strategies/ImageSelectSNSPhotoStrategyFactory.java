package com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.strategies;

import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class ImageSelectSNSPhotoStrategyFactory {

    public static ImageSelectSNSPhotoBase createFragmentStrategy(ImageSelectActivityV2 activity,
                                                                      ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT fragment) {
        switch (fragment) {
            case FACE_BOOK_DETAIL :
                return new ImageSelectSNSPhotoForFaceBook(activity);
            case KAKAO_BOOK_DETAIL :
                return new ImageSelectSNSPhotoForKakao(activity);
            case INSTAGRAM_BOOK_DETAIL:
                return new ImageSelectSNSPhotoForInstagram(activity);
            case GOOGLE_PHOTO_DETAIL:
                return new ImageSelectSNSPhotoForGooglePhoto(activity);
            case SNAPS_STICKER:
                return new ImageSelectSNSPhotoForSnapsSticker(activity);
        }
        return null;
    }
}
