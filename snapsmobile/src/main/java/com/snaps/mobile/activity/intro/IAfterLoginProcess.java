package com.snaps.mobile.activity.intro;

/**
 * Created by ysjeong on 16. 6. 8..
 */
public interface IAfterLoginProcess {
    public static int MOVE_TO_HOME = 0;
    public static int MOVE_TO_DIARY_MAIN = 100;

    int getMoveWhere();
}
