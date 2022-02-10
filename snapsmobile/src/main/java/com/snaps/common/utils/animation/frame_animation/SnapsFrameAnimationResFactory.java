package com.snaps.common.utils.animation.frame_animation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class SnapsFrameAnimationResFactory {
    public enum eSnapsFrameAnimation {
        SMART_SNAPS_TUTORIAL_CENTER_FACE,
        SMART_SNAPS_TUTORIAL_DATE,
        SMART_SNAPS_TUTORIAL_ROTATION,
        IMAGE_SELECT_TUTORIAL_DRAG,
        IMAGE_SELECT_NEW_PINCH,
        IMAGE_SELECT_PINCH,
        RECOMMEND_BOOK_MAIN_ACT_PINCH,
        NATIVE_DETAIL_DEFAULT_PAGE,
        SMART_RECOMMEND_BOOK_TUTORIAL_DRAG_N_DROP,
        SMART_RECOMMEND_BOOK_TUTORIAL_SWIPE_PAGE,
        KT_BOOK_EDITOR, //KT 북
        ACRYLIC_KEYING_EDITOR,
        ACRYLIC_STAND_EDITOR,
    }

    private static SnapsFrameAnimationBaseRes createAnimationRes(eSnapsFrameAnimation animation) {
        switch (animation) {
            case SMART_SNAPS_TUTORIAL_CENTER_FACE:
                return new SnapsFrameAnimationResSmartSnapsCenterFace();
            case SMART_SNAPS_TUTORIAL_DATE:
                return new SnapsFrameAnimationResSmartSnapsDate();
            case SMART_SNAPS_TUTORIAL_ROTATION:
                return new SnapsFrameAnimationResSmartSnapsRotation();
            case IMAGE_SELECT_TUTORIAL_DRAG:
                return new SnapsFrameAnimationResImageSelectTutorialDrag();
            case IMAGE_SELECT_NEW_PINCH:
                return new SnapsFrameAnimationResImageSelectTutorialNewPinch();
            case IMAGE_SELECT_PINCH:
                return new SnapsFrameAnimationResImageSelectTutorialPinch();
            case NATIVE_DETAIL_DEFAULT_PAGE:
                return new SnapsFrameAnimationResNativeDetailDefaultPage();
            case SMART_RECOMMEND_BOOK_TUTORIAL_DRAG_N_DROP:
                return new SnapsFrameAnimationResSmartSnapsTutorialDragNDrop();
            case SMART_RECOMMEND_BOOK_TUTORIAL_SWIPE_PAGE:
                return new SnapsFrameAnimationResSmartSnapsTutorialSwipePage();
            case RECOMMEND_BOOK_MAIN_ACT_PINCH:
                return new SnapsFrameAnimationResRecommendBookMainListPinchTutorial();
            case KT_BOOK_EDITOR:    //KT 북
                return new SnapsFrameAnimationResKTBookEditorTutorial();
            case ACRYLIC_KEYING_EDITOR:
                return new SnapsFrameAnimationResAcrylicKeyringEditorTutorial();
            case ACRYLIC_STAND_EDITOR:
                return new SnapsFrameAnimationResAcrylicStandEditorTutorial();
        }
        return null;
    }

    public static SnapsFrameAnimationResInfo getAnimationResInfo(eSnapsFrameAnimation animation) throws Exception {
        SnapsFrameAnimationBaseRes animationBaseRes = SnapsFrameAnimationResFactory.createAnimationRes(animation);
        if (animationBaseRes == null) return null;

        LinkedHashMap<Integer, Integer> resourceMap = animationBaseRes.getResourceMap();
        if (resourceMap == null) return null;

        int[] arResIds = getResIdArray(resourceMap);
        int[] arDuring = getDuringArray(resourceMap);
        if (arResIds == null || arDuring == null) return null;

        return SnapsFrameAnimationResInfo.createResInfo(arResIds, arDuring);
    }

    private static int[] getResIdArray(Map<Integer, Integer> hashMap) throws Exception {
        if (hashMap == null) return null;
        Set<Integer> resSet = hashMap.keySet();
        Integer[] targetArray = resSet.toArray(new Integer[resSet.size()]);
        int[] result = new int[resSet.size()];
        for (int ii = 0; ii <targetArray.length; ii++) {
            result[ii] = targetArray[ii];
        }
        return result;
    }

    private static int[] getDuringArray(Map<Integer, Integer> hashMap) throws Exception {
        if (hashMap == null) return null;
        Collection<Integer> resSet = hashMap.values();
        Integer[] targetArray = resSet.toArray(new Integer[resSet.size()]);
        int[] result = new int[resSet.size()];
        for (int ii = 0; ii <targetArray.length; ii++) {
            result[ii] = targetArray[ii];
        }
        return result;
    }
}
