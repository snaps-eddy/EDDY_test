package com.snaps.mobile.activity.diary.interfaces;

/**
 * Created by ysjeong on 16. 3. 29..
 */
public interface ISnapsDiaryTutorialListener {

    public static final int SNAPS_DIARY_TUTORIAL_BTN_01 = 0;
    public static final int SNAPS_DIARY_TUTORIAL_BTN_02 = 1;
    public static final int SNAPS_DIARY_CLOSE           = 2;

    void onClosedTutorialView(int btnType);
}
