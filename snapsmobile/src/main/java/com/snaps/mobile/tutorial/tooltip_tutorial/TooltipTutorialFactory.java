package com.snaps.mobile.tutorial.tooltip_tutorial;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.tooltip_tutorial.attributes.TooltipTutorialCreator;
import com.snaps.mobile.tutorial.tooltip_tutorial.attributes.TooltipTutorialForNewYearsCardThumbnailChangeQuantity;
import com.snaps.mobile.tutorial.tooltip_tutorial.attributes.TooltipTutorialForNewYearsCardThumbnailLongClickDelete;
import com.snaps.mobile.tutorial.tooltip_tutorial.attributes.TooltipTutorialForPhotoCardChangeDesign;
import com.snaps.mobile.tutorial.tooltip_tutorial.attributes.TooltipTutorialForPhotoCardThumbnailChangeQuantity;
import com.snaps.mobile.tutorial.tooltip_tutorial.attributes.TooltipTutorialForPhotoCardThumbnailLongClickDelete;

/**
 * Created by ysjeong on 2017. 8. 1..
 */

public class TooltipTutorialFactory {
    public static TooltipTutorialCreator createTooltipTutorial(@NonNull Activity activity, @NonNull SnapsTutorialAttribute attribute) throws Exception {
        switch (attribute.getTutorialId()) {
            case TUTORIAL_ID_TOOLTIP_PHOTO_CARD_CHANGE_DESIGN:
            case TUTORIAL_ID_TOOLTIP_WALLET_PHOTO_CHANGE_DESIGN:
                return TooltipTutorialForPhotoCardChangeDesign.createInstanceWithTutorialAttribute(activity, attribute);
            case TUTORIAL_ID_TOOLTIP_PHOTO_CARD_LONG_CLICK_DELETE:
                return TooltipTutorialForPhotoCardThumbnailLongClickDelete.createInstanceWithTutorialAttribute(activity, attribute);
            case TUTORIAL_ID_TOOLTIP_PHOTO_CARD_CHANGE_QUANTITY:
                return TooltipTutorialForPhotoCardThumbnailChangeQuantity.createInstanceWithTutorialAttribute(activity, attribute);
            case TUTORIAL_ID_TOOLTIP_NEW_YEARS_CARD_CHANGE_QUANTITY:
                return TooltipTutorialForNewYearsCardThumbnailChangeQuantity.createInstanceWithTutorialAttribute(activity, attribute);
            case TUTORIAL_ID_TOOLTIP_NEW_YEARS_CARD_LONG_CLICK_DELETE:
                return TooltipTutorialForNewYearsCardThumbnailLongClickDelete.createInstanceWithTutorialAttribute(activity, attribute);
        }
        return null;
    }
}
