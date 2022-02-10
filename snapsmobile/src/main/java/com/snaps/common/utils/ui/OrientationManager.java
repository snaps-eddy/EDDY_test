package com.snaps.common.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.OrientationEventListener;

import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.themebook.OrientationChecker;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.util.ArrayList;

public class OrientationManager extends OrientationEventListener {
    private static final String TAG = OrientationManager.class.getName();

    private OrientationChecker orientationChecker = null;

    private int previousAngle;
    private int previousOrientation = Configuration.ORIENTATION_PORTRAIT;
    private Context context;
    private static OrientationManager instance;

    private ArrayList<OrientationChangeListener> arrOpservers = null;

    private boolean isLandScapeMode = false;
    private boolean isBlockRotate = true;

    private OrientationManager(Context context) {
        super(context);
        this.context = context;
        this.arrOpservers = new ArrayList<>();
    }

    public static void finalizeInstance() {
        if (instance != null) {
            if (instance.arrOpservers != null) {
                instance.arrOpservers.clear();
                instance.arrOpservers = null;
            }

            if (instance.context != null) {
                instance.context = null;
            }

            if (instance.orientationChecker != null) {
                instance.orientationChecker = null;
            }
        }
    }

    public static void fixCurrentOrientation(Activity activity) {
        try {
            OrientationManager orientationManager = getInstance(activity);
            orientationManager.setBlockRotate(true);
            orientationManager.setEnableOrientationSensor(false);

            UIUtil.fixCurrentOrientationAndReturnBoolLandScape(activity);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static OrientationManager getInstance(Context context) {
        if (instance == null) {
            instance = new OrientationManager(context);
        }
        return instance;
    }

    public void init(Activity activity) {
        if (activity == null) return;

        setOrientationChecker(new OrientationChecker(activity));

        if (SmartSnapsManager.shouldSmartSnapsSearchingOnActivityCreate()) {
            if (shouldSmartSnapsAnimationLandScape()) {
                setLandScapeMode(true);
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                UIUtil.updateFullscreenStatus(activity, true);

                SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
                smartSnapsManager.setScreenRotationLock(true);
            } else {
                setLandScapeMode(false);
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        } else {
            setLandScapeMode(false);
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setBlockRotate(true);
        setEnableOrientationSensor(false);
    }

    private boolean shouldSmartSnapsAnimationLandScape() {
        try {
            SnapsTemplateManager templateManager = SnapsTemplateManager.getInstance();
            return templateManager.isMultiPageProduct() && templateManager.getPageWidth() > templateManager.getPageHeight() && !Const_PRODUCT.isDesignNoteProduct();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    public void setEnableOrientationSensor(boolean enable) {
        if (enable) {
            enable();
        } else {
            disable();
        }
    }

    public int getOrientation() {
        return previousOrientation;
    }

    public void setOrientation(int orientation) {
        this.previousOrientation = orientation;
    }

    public void notifyOpservers(int state) {
        if (arrOpservers == null || arrOpservers.isEmpty()) return;

        for (OrientationChangeListener ls : arrOpservers) {
            if (ls != null)
                ls.onOrientationChanged(state);
        }
    }

    @Override
    public void onOrientationChanged(int orientation) {

        if (context == null || orientation == -1) return;

        try {
            if (orientation != Configuration.ORIENTATION_SQUARE
                    && orientation != previousOrientation) {
                previousOrientation = orientation;
                notifyOpservers(orientation);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void addOrientationOpserver(OrientationChangeListener ls) {
        if (arrOpservers != null && !arrOpservers.contains(ls)) {
            arrOpservers.add(ls);
        }
    }

    public void removeOpserver(OrientationChangeListener ls) {
        if (arrOpservers != null) {
            arrOpservers.remove(ls);
        }
    }

    public boolean isLandScapeMode() {
        return isLandScapeMode;
    }

    public void setLandScapeMode(boolean landScapeMode) {
        isLandScapeMode = landScapeMode;
    }

    public boolean isBlockRotate() {
        return isBlockRotate;
    }

    public void setBlockRotate(boolean blockRotate) {
        isBlockRotate = blockRotate;
    }

    public OrientationChecker getOrientationChecker() {
        return orientationChecker;
    }

    public void setOrientationChecker(OrientationChecker orientationChecker) {
        this.orientationChecker = orientationChecker;
    }

    public interface OrientationChangeListener {
        void onOrientationChanged(int newOrientation);
    }
}