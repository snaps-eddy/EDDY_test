package com.snaps.mobile.component;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.NumberPicker;

import com.snaps.common.utils.log.Dlog;

/**
 * Created by ysjeong on 2017. 7. 31..
 */

public class SnapsNumberPicker extends NumberPicker {
    private static final String TAG = SnapsNumberPicker.class.getSimpleName();

    public SnapsNumberPicker(Context context) {
        super(context);
    }

    public SnapsNumberPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SnapsNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void changeDividerColor(int color) {
        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(this, colorDrawable);
                } catch (IllegalArgumentException | Resources.NotFoundException | IllegalAccessException e) {
                    Dlog.e(TAG, e);
                }
                break;
            }
        }
    }
}
