package com.snaps.mobile.activity.edit.view.custom_progress.progress_views;

import android.app.Activity;

import com.snaps.mobile.activity.edit.view.custom_progress.progress_caculate.SnapsTimerProgressBaseCalculator;

/**
 * Created by ysjeong on 2017. 4. 12..
 */

public interface SnapsProgressViewAPI {
    enum eTimerProgressTaskType {
        TASK_TYPE_GET_PROJECT_CODE(5),
        TASK_TYPE_SET_BASE_OPTIONS(5),
        TASK_TYPE_UPLOAD_MAIN_THUMBNAIL(8),
        TASK_TYPE_UPLOAD_ORG_IMG(80),
        TASK_TYPE_HANDLE_XML(2);

        int weight = 0; //0 to 100

        eTimerProgressTaskType(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return weight;
        }
    }

    void showProgress();
    void hideProgress();
    void setMessage(String message);
    void releaseInstance();
    void setActivity(Activity activity);

    SnapsTimerProgressBaseCalculator getProgressCalculator();
}
