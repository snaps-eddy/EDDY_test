package errorhandle.logger.model;

import android.app.Activity;
import android.app.Fragment;

import com.snaps.common.utils.log.Dlog;

/**
 * Created by ysjeong on 2017. 11. 17..
 */

public class SnapsLoggerClass<T> {
    private static final String TAG = SnapsLoggerClass.class.getSimpleName();
    private T classKind;

    public SnapsLoggerClass(T t) {
        this.classKind = t;
    }

    public String getClassName() throws ClassCastException {
        if (classKind == null) return "";

        if (classKind instanceof Activity) {
            return getClassName(((Activity) classKind).getClass());
        } else if (classKind instanceof Fragment) {
            return getClassName(((Fragment) classKind).getClass());
        } else if (classKind instanceof androidx.fragment.app.Fragment) {
            return getClassName(((androidx.fragment.app.Fragment) classKind).getClass());
        }

        try {
            return classKind.getClass().getName();
        } catch (Exception e) { Dlog.e(TAG, e); }

        return "";
    }

    private String getClassName(Class<?> c) {
        return c.getName();
    }
}
