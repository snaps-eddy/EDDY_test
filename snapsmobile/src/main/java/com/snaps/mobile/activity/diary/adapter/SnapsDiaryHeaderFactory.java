package com.snaps.mobile.activity.diary.adapter;

import android.content.Context;

import com.snaps.mobile.activity.diary.SnapsDiaryConstants;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryHeaderClickListener;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryHeaderStrategy;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUserInfo;

/**
 * Created by ysjeong on 16. 4. 8..
 */
public class SnapsDiaryHeaderFactory {

    public static ISnapsDiaryHeaderStrategy createHeader(Context context, int shape, ISnapsDiaryHeaderClickListener stripListener) {
        ISnapsDiaryHeaderStrategy headerStrategy;
        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        SnapsDiaryUserInfo userInfo = dataManager.getSnapsDiaryUserInfo();
        if (userInfo != null) {
            SnapsDiaryConstants.eMissionState STATE = userInfo.getMissionStateEnum();
            switch (STATE) {
                case PREV:
                    headerStrategy = new SnapsDiaryHeaderTypePrev(context, shape, stripListener);
                    break;
                case ING :
                    headerStrategy = new SnapsDiaryHeaderTypeIng(context, shape, stripListener);
                    break;
                case SUCCESS:
                    headerStrategy = new SnapsDiaryHeaderTypeSuccess(context, shape, stripListener);
                    break;
                case FAILED:
                    headerStrategy = new SnapsDiaryHeaderTypeFailed(context, shape, stripListener);
                    break;
                default:
                case UNKNOWN:
                    headerStrategy = new SnapsDiaryHeaderTypeUnkown(context, shape, stripListener);
                    break;
            }
        } else
            headerStrategy = new SnapsDiaryHeaderTypeUnkown(context, shape, stripListener);

        return headerStrategy;
    }
}
