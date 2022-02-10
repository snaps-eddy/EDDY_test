package com.snaps.common.utils.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;

import com.snaps.common.utils.log.Dlog;

import errorhandle.logger.Logg;

/**
 * 자동 저장 관련
 */
public class OrientationSensorManager {
    private static final String TAG = OrientationSensorManager.class.getSimpleName();

    public interface OrientationChangeListener {
        void onOrientationChanged(int orientation);
    }

    private static final long MIN_ORIENTATION_CHANGE_ALLOW_TERM = 1000;

    private volatile static OrientationSensorManager gInstance = null;

    private boolean isBlockSensorEvent = false;
    private boolean isInitialized = false;

    private int lastScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    private int prevOrientation = 0;
    private int orientation = 0;
    private SensorEventListener sensorEventListener;
    private SensorManager sensorManager;

    private OrientationChangeListener orientationChangeListener = null;
    private long lastOrientationChangedTime = 0l;

    public static void createInstance() {
        synchronized (OrientationSensorManager.class) {
            gInstance = new OrientationSensorManager();
        }
    }

    public static OrientationSensorManager getInstance() {
        if (gInstance == null) createInstance();
        return gInstance;
    }

    public static void finalizeInstance() {
        if (gInstance != null) {
            gInstance = null;
        }
    }

    private OrientationSensorManager() {
    }

    public void init(Context context, OrientationSensorManager.OrientationChangeListener orientationChangeListener) {
        prevOrientation = 0;
        orientation = 0;
        isBlockSensorEvent = true;

        registerOrientationSensorListener(context);
        setOrientationChangeListener(orientationChangeListener);

        isInitialized = true;
    }

    public boolean isAllowOrientationChangeTime() {
        return System.currentTimeMillis() - lastOrientationChangedTime > MIN_ORIENTATION_CHANGE_ALLOW_TERM;
    }

    public void updateLastOrientationChangeTime() {
        this.lastOrientationChangedTime = System.currentTimeMillis();
    }

    public void registerOrientationSensorListener(Context context) {
        sensorEventListener = new OrientationSensorListener();
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public static void resume(OrientationChangeListener listener) {  //require resume
        OrientationSensorManager orientationSensorManager = getInstance();
        if (!orientationSensorManager.isInitialized) return;
        try {
            SensorManager sensorManager = orientationSensorManager.sensorManager;
            if (sensorManager != null && orientationSensorManager.sensorEventListener != null) {
                sensorManager.registerListener(orientationSensorManager.sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            }

            orientationSensorManager.setOrientationChangeListener(listener);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static void pause() {  //require pause
        OrientationSensorManager orientationSensorManager = getInstance();
        if (!orientationSensorManager.isInitialized) return;

        try {
            SensorManager sensorManager = orientationSensorManager.sensorManager;
            if (sensorManager != null && orientationSensorManager.sensorEventListener != null) {
                sensorManager.unregisterListener(orientationSensorManager.sensorEventListener);
            }

            orientationSensorManager.setOrientationChangeListener(null);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void setBlockSensorEvent(boolean blockSensorEvent) {
        isBlockSensorEvent = blockSensorEvent;
    }

    public void setOrientationChangeListener(OrientationChangeListener sensorEventListener) {
        this.orientationChangeListener = sensorEventListener;
    }

    private class OrientationSensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (isBlockSensorEvent) return;

            try {
                float x = event.values[0];
                float y = event.values[1];

                if (x < 5 && x > -5 && y > 5)
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT; //0
                else if (x < -5 && y < 5 && y > -5)
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE; //90
                else if (x < 5 && x > -5 && y < -5)
                    orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT; //180
                else if (x > 5 && y < 5 && y > -5)
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE; //270
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }

            if (orientation != prevOrientation) {
                prevOrientation = orientation;

                Dlog.d("onSensorChanged() orientation:" + orientation);
                if (orientationChangeListener != null) {
                    orientationChangeListener.onOrientationChanged(orientation);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    public int getOrientation() {
        return orientation;
    }

    public int getLastScreenOrientation() {
        return lastScreenOrientation;
    }

    public void setLastScreenOrientation(int lastScreenOrientation) {
        this.lastScreenOrientation = lastScreenOrientation;
    }

    public static boolean isActiveAutoRotation(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
    }
}

