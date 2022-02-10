package com.snaps.mobile.activity.edit.view.custom_progress;

import android.app.Activity;
import androidx.annotation.NonNull;

import com.snaps.mobile.activity.edit.view.custom_progress.progress_views.SnapsProgressViewAPI;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_views.SnapsTimerProgressException;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_views.SnapsTimerProgressForTasks;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_views.SnapsTimerProgressForLoading;
import com.snaps.mobile.activity.edit.view.custom_progress.progress_views.SnapsTimerProgressForUpload;

/**
 * Created by ysjeong on 2017. 4. 12..
 */

public class SnapsTimerProgressViewFactory {

    public enum eTimerProgressType {
        PROGRESS_TYPE_LOADING,
        PROGRESS_TYPE_TASKS,
        PROGRESS_TYPE_UPLOAD
    }

    public static SnapsProgressViewAPI createProgressView(@NonNull Activity activity, @NonNull eTimerProgressType progressType) throws SnapsTimerProgressException {
        switch (progressType) {
            case PROGRESS_TYPE_LOADING: return new SnapsTimerProgressForLoading(activity, progressType);
            case PROGRESS_TYPE_TASKS: return new SnapsTimerProgressForTasks(activity, progressType);
            case PROGRESS_TYPE_UPLOAD: return new SnapsTimerProgressForUpload(activity, progressType);
        }
        throw new SnapsTimerProgressException("not defined progress type.");
    }
}
