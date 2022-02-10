package com.snaps.common.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region.Op;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.ui.menu.renewal.view.ReloadableImageView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Stack;

import static android.graphics.Bitmap.Config.ALPHA_8;
import static android.graphics.Bitmap.Config.ARGB_8888;

@SuppressWarnings("deprecation")
public class UIUtil {
    private static final String TAG = UIUtil.class.getSimpleName();

    public static void performWeakVibration(Context context, long during) {
        if (context == null) return;
        try {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) vibrator.vibrate(during);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    //해상도가 특이한 단말기..
    public static boolean isUnusualResolutionDevice(Activity activity) {
        if (activity == null) return false;
        return (getScreenWidth(activity) == 1080 && getScreenHeight(activity) == 1776)
                || (getScreenWidth(activity) == 1536 && getScreenHeight(activity) == 1952);
    }

    public static int getImgChgDialogWidth(Activity act) {
        double screenWidth = UIUtil.getScreenWidth(act);
        return (int) (screenWidth * ((double) 3 / (double) 5));
    }

    public static int getCalcMyartworkWidth(Context context) {
        return UIUtil.getScreenHeight(context) - convertDPtoPX(context, 50) * 2 - convertDPtoPX(context, 60) * 2;
    }

    public static int getCalcMyartworkWidthVerticalMode(Context context) {
        return UIUtil.getScreenWidth(context);
    }

    public static int getCalcMyartworkWidthForPostCard(Context context) {
        return UIUtil.getScreenHeight(context);
    }

    public static int getCalcMyartworkWidth2(Context context) {
        return UIUtil.getScreenHeight(context) - convertDPtoPX(context, 80) * 2;
    }

    public static int getCalcMyartworkLayout(Context context) {
        return UIUtil.getScreenHeight(context) - convertDPtoPX(context, 50) * 2;
    }

    public static int getCalcSrcImgThumbSize(Context context) {
        return UIUtil.getScreenWidth(context) / 2;
    }

    public static int getGridColumnHeight(Activity act, int numColumns) {
        return getGridColumnHeight(act, numColumns, false);
    }

    public static int getGridColumnHeight(Activity act, int numColumns, boolean isLandScapeMode) {
        int mImageThumbSpacing = act.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        int screenWidth = (isLandScapeMode ? UIUtil.getScreenHeight(act) : UIUtil.getScreenWidth(act));
        return (screenWidth / numColumns) - mImageThumbSpacing;
    }

    public static int getGridColumnHeight2(Activity act, int numColumns) {
        return getGridColumnHeight2(act, numColumns, false);
    }

    public static int getIndicatorHeight(Activity act) {
        Rect rectangle = new Rect();
        Window window = act.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        return contentViewTop - statusBarHeight;
    }

    public static int getGridColumnHeight2(Activity act, int numColumns, boolean isLandScapeMode) {
//		int mImageThumbSpacing = act.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
        int screenWidth = (isLandScapeMode ? UIUtil.getScreenHeight(act) : UIUtil.getScreenWidth(act));
        return (screenWidth / numColumns);
    }

    public static int getGridColumnForKakaoBookHeight(Activity act, int numColumns) {
        int mImageThumbSpacing = act.getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing_for_kakaobook);
        int screenWidth = UIUtil.getScreenWidth(act) - UIUtil.convertDPtoPX(act, 20);
        return (screenWidth / numColumns) - mImageThumbSpacing;
    }

    public static int getGridColumnHeight(Activity act, int numColumns, int mImageThumbSpacing) {
        int screenWidth = UIUtil.getScreenWidth(act);
        return (screenWidth / numColumns) - mImageThumbSpacing;
    }

    public static int getGridColumnHeight(Context act, int numColumns, int mImageThumbSpacing, int extraMargin) {
        int screenWidth = UIUtil.getScreenWidth(act) - extraMargin;
        return (screenWidth / numColumns) - mImageThumbSpacing;
    }

    public static int getGridColumnHeightNewyearsCard(Context act, int numColumns, int imageThumbSpacing, int extraMargin) {
        int screenWidth = UIUtil.getCurrentScreenWidth(act) - extraMargin;
        return (screenWidth / numColumns) - imageThumbSpacing;
    }

    public static int getGridColumnHeight(Context act, int numColumns, int mImageThumbSpacing, int extraMargin, boolean isLandScapeMode) {
        int screenWidth = (isLandScapeMode ? UIUtil.getScreenHeight(act) : UIUtil.getScreenWidth(act)) - extraMargin;
        return (screenWidth / numColumns) - mImageThumbSpacing;
    }

    public static int getCalcWidth(Activity act, int numColumns) {
        int screenWidth = UIUtil.getScreenWidth(act);
        return (screenWidth / numColumns);
    }

    public static int getCalcWidth(Activity act, int numColumns, boolean isLandScapeMode) {
        int screenWidth = isLandScapeMode ? UIUtil.getScreenHeight(act) : UIUtil.getScreenWidth(act);
        return (screenWidth / numColumns);
    }

    public static int getCalcOneGridHeight(Activity act, int numColumns, int spacing) {
        int notSpacingWidth = UIUtil.getScreenWidth(act) - (spacing * (numColumns + 1));
        return (notSpacingWidth / numColumns) + (spacing * 2);
    }

    public static int getCalcOneGridImageWidth(Activity act, int numColumns, int spacing) {
        int notSpacingWidth = UIUtil.getScreenWidth(act) - (spacing * (numColumns + 1));
        return (notSpacingWidth / numColumns);
    }

    public static void hideKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);// 키보드닫기
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);// 키보드닫기
    }

    public static void showKeyboardForced(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        imm.showSoftInputFromInputMethod(editText.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);
    }

    public static void showKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInputFromInputMethod(editText.getWindowToken(), 0);
    }

    public static int getStatusBarHeight() throws Exception {
        int result = 0;
        int resourceId = ContextUtil.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ContextUtil.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    public static int getRealScreenHeight(Context context) {
        int height = 0;
        try {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            final DisplayMetrics metrics = new DisplayMetrics();
            Display display = wm.getDefaultDisplay();
            Method mGetRawH = null;

            try {
                // For JellyBean 4.2 (API 17) and onward
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    display.getRealMetrics(metrics);

                    height = metrics.heightPixels;
                } else {
                    mGetRawH = Display.class.getMethod("getRawHeight");
                    try {
                        height = (Integer) mGetRawH.invoke(display);
                    } catch (IllegalArgumentException e) {
                        Dlog.e(TAG, e);
                    } catch (IllegalAccessException e) {
                        Dlog.e(TAG, e);
                    } catch (InvocationTargetException e) {
                        Dlog.e(TAG, e);
                    }
                }
            } catch (NoSuchMethodException e3) {
                Dlog.e(TAG, e3);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return getScreenHeight(context);
        }

        return removedNavigationHeight(context, height);
    }

    private static int removedNavigationHeight(Context context, int height) {
        try {
            return height - getNavigationBarHeight(context);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return height;
    }

    private static int getNavigationBarHeight(Context context) throws Exception {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && !hasMenuKey) {
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private static int getNavBarHeight(Context c) throws Exception {
        int result = 0;
        boolean hasMenuKey = ViewConfiguration.get(c).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        if (!hasMenuKey && !hasBackKey) {
            //The device has a navigation bar
            Resources resources = c.getResources();

            int orientation = resources.getConfiguration().orientation;
            int resourceId;
            if (isTablet(c)) {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_height_landscape", "dimen", "android");
            } else {
                resourceId = resources.getIdentifier(orientation == Configuration.ORIENTATION_PORTRAIT ? "navigation_bar_height" : "navigation_bar_width", "dimen", "android");
            }

            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    private static boolean isTablet(Context c) throws Exception {
        return (c.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private static int sCacheScreenWidth = Integer.MIN_VALUE;

    public static int getScreenWidth(Context context) {
        if (sCacheScreenWidth != Integer.MIN_VALUE) {
            return sCacheScreenWidth;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int displayWidth = display.getWidth();
        int displayHeight = display.getHeight();
        sCacheScreenWidth = displayWidth > displayHeight ? displayHeight : displayWidth;
        return sCacheScreenWidth;
    }

    private static int sCacheScreenHeight = Integer.MIN_VALUE;

    public static int getScreenHeight(Context context) {
        if (sCacheScreenHeight != Integer.MIN_VALUE) {
            return sCacheScreenHeight;
        }
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        int displayWidth = display.getWidth();
        int displayHeight = display.getHeight();
        sCacheScreenHeight = displayWidth < displayHeight ? displayHeight : displayWidth;
        return sCacheScreenHeight;
    }

    public static int getCurrentScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getWidth();
    }

    public static int getCurrentScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return display.getHeight();
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        if (context == null || context.getResources() == null) return null;

        if (Config.isWQHDResolutionDevice()) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            displayMetrics.density = 4.f;
            return displayMetrics;
        }

        return context.getResources().getDisplayMetrics();
    }

    public static int convertDPtoPX(Context context, int dp) {
        try {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getDisplayMetrics(context));
        } catch (NullPointerException np) {
            Dlog.e(TAG, np);
        }
        return 0;
    }

    public static int convertDPtoPX(Context context, float dp) {
        try {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getDisplayMetrics(context));
        } catch (NullPointerException np) {
            Dlog.e(TAG, np);
        }
        return 0;
    }

    public static int convertPXtoDP(Context context, int px) {
        try {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, getDisplayMetrics(context));
        } catch (NullPointerException np) {
            Dlog.e(TAG, np);
        }
        return 0;
    }

    public static float convertPixelsToSp(Context context, Float px) {
        try {
            float scaledDensity = getDisplayMetrics(context).scaledDensity;
            return px / scaledDensity;
        } catch (NullPointerException np) {
            Dlog.e(TAG, np);
        }
        return 0;
    }

    /**
     * 기준이 되는 가로, 혹은 세로 값을 기준으로 좌표 계산 등에 필요한 rate 구함.
     *
     * @param dataSize
     * @param imageSize
     * @return
     */
    public static float getRate(float[] dataSize, float[] imageSize) {
        float temp;
        if ((dataSize[0] - dataSize[1]) * (imageSize[0] - imageSize[1]) < 0) { // 방향이 다르면 맞춰줌.
            temp = dataSize[0];
            dataSize[0] = dataSize[1];
            dataSize[1] = temp;
        }

        int index = 0;
        if (imageSize[0] / imageSize[1] > dataSize[0] / dataSize[1]) // 가로, 세로 중 기준을 정함.
            index = 1;

        return imageSize[index] / dataSize[index];
    }

    public static float convertSpToPixels(Context context, Float sp) {
        try {
            float scaledDensity = getDisplayMetrics(context).scaledDensity;
            return sp * scaledDensity;
        } catch (NullPointerException np) {
            Dlog.e(TAG, np);
        }
        return 0;
    }

    public static int getAlpha(int opacity) {
        int res = (int) (256 * ((double) opacity / (double) 100) - 1);
        return res < 0 ? 0 : res;
    }

    /***
     * 리소스 이름을 가지고 리소스 아이디를 가져온다.
     *
     * @param context
     * @param stringRes
     * @return
     */
    public static int string2DrawableResID(Context context, String stringRes) {
        // String resName = "@drawable/imgEnd";
        String packName = context.getPackageName(); // 패키지명
        int resID = context.getResources().getIdentifier(stringRes, "drawable", packName);

        return resID;
    }

    /**
     * 현재 단말기의 오리엔테이션을 고정시킨다.(가로 모드일경우 true를 리턴함.)
     *
     * @param act
     * @return
     */
    public static boolean fixOrientation(Activity act, int orientation) {

        if (orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                || orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return false;
        } else {
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            else if (orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
                act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            return true;
        }
    }

    /**
     * 현재 단말기의 오리엔테이션을 고정시킨다.(가로 모드일경우 true를 리턴함.)
     *
     * @param act
     * @return
     */
    public static boolean fixCurrentOrientationAndReturnBoolLandScape(Activity act) {
        int curOrientation = getScreenOrientation(act);

        if (curOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                || curOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
            act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return false;
        } else {
            if (curOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            else if (curOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
                act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            return true;
        }
    }

    public static boolean isCurrentLandScapeOrientation(Activity act) {
        int curOrientation = getScreenOrientation(act);
        return !(curOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                || curOrientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
    }

    public static int getScreenOrientation(Activity act) {
        if (act == null) return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        int rotation = act.getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        } else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    public static void updateFullscreenStatus(Activity act, boolean bUseFullscreen) {
        if (act == null || !(act instanceof Activity)) return;

        if (bUseFullscreen) {
            act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            act.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            act.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public static void clipPathSupportICS(Context context, Canvas c, Path p) {
        if (context == null || c == null || p == null) return;
        try {
            c.clipPath(p);
        } catch (UnsupportedOperationException e) {
            Dlog.e(TAG, e);
            if (Build.VERSION.SDK_INT >= 11) {
                try {
                    final int LAYER_TYPE_SOFRWARE = context.getClass().getField("LAYER_TYPE_SOFTWARE").getInt(context);
                    Method layoutTypeM = context.getClass().getMethod("setLayerType", Integer.TYPE, Paint.class);
                    layoutTypeM.invoke(context, LAYER_TYPE_SOFRWARE, null);
                    c.clipPath(p);
                } catch (Throwable e1) {
                    Dlog.e(TAG, e1);
                }
            }
        }
    }

    public static void clipPathSupportICS(ImageView iv, Canvas c, Path p, Op o) {
        if (iv == null || c == null || p == null) return;
        try {
            c.clipPath(p, o);
        } catch (UnsupportedOperationException e) {
            Dlog.e(TAG, e);
            if (Build.VERSION.SDK_INT >= 11) {
                try {
                    iv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    c.clipPath(p, o);
                } catch (Throwable e2) {
                    Dlog.e(TAG, e2);
                }
            }
        }
    }


    /***
     * 300dpi => 72dpi의 폰트크기로 변환
     * @param context
     * @param fontSize
     * @return
     */
    public static int getFontSize(Context context, int fontSize) {
        try {
            float dpi = getDisplayMetrics(context).densityDpi;
            return (int) ((fontSize * dpi / 72) / (300 / dpi));
        } catch (NullPointerException np) {
            Dlog.e(TAG, np);
        }
        return 0;
    }

    public static void reloadImage(ViewGroup parent) {
        View child;
        for (int i = 0; i < parent.getChildCount(); ++i) {
            child = parent.getChildAt(i);
            if (child instanceof ViewGroup) reloadImage((ViewGroup) child);
            else if (child instanceof ReloadableImageView) ((ReloadableImageView) child).reload();
            ;
        }
    }

    public static void clearImage(Context context, ViewGroup parent, boolean prepareReload) {
        if (context == null) return;
        //FIXME 메모리 이슈때문에 추가한 코드로 보이는데, 홈 메뉴에서 이미지가 사라지는 오류가 있다. 시간 여유를 가지고 원인을 파악해서 수정해야 한다
        View child;
        for (int i = 0; i < parent.getChildCount(); ++i) {
            child = parent.getChildAt(i);
            if (!prepareReload) child.setBackgroundDrawable(null);

            if (child instanceof ViewGroup) clearImage(context, (ViewGroup) child, prepareReload);
            else if (child instanceof ReloadableImageView) {
                ((ReloadableImageView) child).clear();
                ((ReloadableImageView) child).setImageDrawable(null);
            } else if (!prepareReload && child instanceof ImageView) {
                ImageLoader.clear(context, child);
                ((ImageView) child).setImageDrawable(null);
            }
        }
    }

    /**
     * ImageFull or PaperFull, parentView와 childView의 사이즈 기준으로 childView의 size 및 position을 구함.
     *
     * @param isImageFull
     * @param parentSize
     * @param childSize   비율도 ok
     * @return int[]{x, y, w, h}
     */
    public static int[] getPosByImageType(boolean isImageFull, int[] parentSize, int[] childSize) {
        float x, y, w, h;
        float[] parentSizeF = new float[]{(float) parentSize[0], (float) parentSize[1]};
        float[] childSizeF = new float[]{(float) childSize[0], (float) childSize[1]};
        float parentRate = parentSizeF[0] / parentSizeF[1];
        float childRate = childSizeF[0] / childSizeF[1];
        if ((isImageFull && childRate > parentRate) || (!isImageFull && childRate < parentRate)) {
            w = parentSizeF[0];
            h = parentSizeF[0] / childSizeF[0] * childSizeF[1];
            x = 0;
            y = (parentSizeF[1] - h) / 2;
        } else {
            w = parentSizeF[1] / childSizeF[1] * childSizeF[0];
            h = parentSizeF[1];
            x = (parentSizeF[0] - w) / 2;
            y = 0;
        }

        int intW, intH, intX, intY;
        if (isImageFull) {
            intW = (int) Math.ceil(w);
            intH = (int) Math.ceil(h);
            intX = (int) Math.ceil(x);
            intY = (int) Math.ceil(y);
        } else {
            intW = (int) w;
            intH = (int) h;
            intX = (int) x;
            intY = (int) y;
        }

        return new int[]{intX, intY, intW, intH};
    }

    /**
     * view의 실제 x, y값을 구함. view가 화면에 그려진 이후에 사용가능.
     *
     * @param view
     * @return
     */
    public static float[] getRealPos(View view) {
        if (view == null)
            return new float[]{0, 0};

        return new float[]{(float) getRelativeLeft(view), (float) getRelativeTop(view)};
    }

    private static int getRelativeLeft(View myView) {
        if (myView.getParent() == null || !(myView.getParent() instanceof View))
            return 0;
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    private static int getRelativeTop(View myView) {
        if (myView.getParent() == null || !(myView.getParent() instanceof View))
            return 0;
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }

    public static void clearImageResource(Context context, ImageView image) {
        if (image != null) {
            if (image.getDrawable() instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();
                bitmapDrawable.getBitmap().recycle();
            }

            image.setImageDrawable(null);
            ImageLoader.clear(context, image);
        }
    }

    public static void applyLanguage(Context context, String lang, boolean isFromAppSetting) {
        try {
            if (isFromAppSetting)
                Setting.set(context, Const_VALUE.KEY_APPLIED_LANGUAGE, lang); // 설정의 선택으로 전환한 내용이 있을 경우, 현재 셋팅하는 언어 저장.

            Resources res = context.getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            Locale locale = new Locale(lang);
            if (Build.VERSION.SDK_INT < 17)
                conf.locale = locale;
            else
                conf.setLocale(locale);
            res.updateConfiguration(conf, dm);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * @param context
     */
    public static void applyLanguage(Context context) {
        try {
            String newLang = Setting.getString(context, Const_VALUE.KEY_APPLIED_LANGUAGE);
            if (StringUtil.isEmpty(newLang)) {
                newLang = Setting.getString(context, Const_VALUE.KEY_LANGUAGE);
                if (StringUtil.isEmpty(newLang))
                    newLang = Locale.getDefault().getLanguage();
            }

            newLang = SnapsLanguageUtil.getConvertedOldSnapsLanguageCode(newLang);

            Resources res = context.getResources();
            // Change locale settings in the app.
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            Locale locale = new Locale(newLang);
            if (Build.VERSION.SDK_INT < 17)
                conf.locale = locale;
            else
                conf.setLocale(locale);
            res.updateConfiguration(conf, dm);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static final long DEFAULT_CLICK_BLOCK_TIME = 2000;

    public static void blockClickEvent(final View view, final long seconds) {
        if (view == null) return;
        view.setClickable(false);

        Handler delayHandler = new Handler(Looper.getMainLooper());
        delayHandler.postDelayed(() -> {
            view.setClickable(true);
        }, seconds);
    }

    public static void blockClickEvent(final View view) {
        blockClickEvent(view, DEFAULT_CLICK_BLOCK_TIME);
    }

    public static int convertDPtoPXBabyNameSticker(Context context, int dp) {
        try {
            return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getDisplayMetrics(context)) * getProduct());
        } catch (NullPointerException np) {
            Dlog.e(TAG, np);
        }
        return 0;
    }

    private static float getProduct() {
        if (Config.isBabyNameStickerEditScreen()) return 1f;
        switch (Config.getPROD_CODE()) {
            case Const_PRODUCT.BABY_NAME_STICKER_MINI:
                return 4.66f;
            case Const_PRODUCT.BABY_NAME_STICKER_SMALL:
                return 3.73f;
            case Const_PRODUCT.BABY_NAME_STICKER_MEDIUM:
                return 2.91f;
            case Const_PRODUCT.BABY_NAME_STICKER_LARGE:
                return 2.17f;
        }
        return 1f;
    }


    public static void drawKnifeOutLine(final Bitmap bitmap, final int width, final int maskSize, final int lineColor, final FinishIntArrayListener finishBitmapListener) {
        new Thread() {
            @Override
            public void run() {
                int[] bitmapIntArray = getIntArray(bitmap);
                int[] grayScaleIntArray = getGrayScaleInt(bitmapIntArray);
                int[] erosionIntArray = morphologyIntArrayDilation(grayScaleIntArray, width, maskSize);
                finishBitmapListener.finish(drawInOrOutLine(erosionIntArray, width, lineColor));
            }
        }.start();
    }

    public static int[] getIntArray(Bitmap oribitmap) {
        int[] inttArray = new int[oribitmap.getWidth() * oribitmap.getHeight()];
        oribitmap.getPixels(inttArray, 0, oribitmap.getWidth(), 0, 0, oribitmap.getWidth(), oribitmap.getHeight());
        return inttArray;
    }

    public static Bitmap intArrayToBitmap(int[] intArray, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, ARGB_8888);
        bitmap.setPixels(intArray, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static int[] getGrayScaleInt(final int[] bitmapInt) {
        int[] grayScleInt = new int[bitmapInt.length];
        for (int x = 0; x < bitmapInt.length; x++) {
            int pixelColor = bitmapInt[x];
            int pixelAlpha = Color.alpha(pixelColor);
            if (pixelAlpha > 125) {
                grayScleInt[x] = Color.BLACK;
            } else {
                grayScleInt[x] = Color.TRANSPARENT;
            }
        }
        return grayScleInt;
    }

    private static int[] morphologyIntArrayDilation(final int[] intArray, final int width, int maskSize) {
        int[] tempArray = new int[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            int color = intArray[i];
            if (color == 0) {
                if (customDilationIntArray(intArray, width, i, maskSize)) {
                    tempArray[i] = Color.BLACK;
                } else {
                    tempArray[i] = Color.TRANSPARENT;
                }
            } else {
                tempArray[i] = Color.RED;
            }
        }
        return tempArray;
    }

    private static boolean customDilationIntArray(int[] intArray, int wdith, int x, int maskSize) {
        int size = maskSize - 2;
        int pixelCount = intArray.length;

        for (int count = -size; count <= size; count++) {

            int startEx = x - (wdith * count) - size;
            int endEx = x - (wdith * count) + size;

            if (startEx < 0) {
                startEx = 0;
            }

            if (endEx >= pixelCount) {
                endEx = pixelCount - 1;
            }

            for (int eX = startEx; eX <= endEx; eX++) {
                if (eX == x) {
                    continue;
                }
                if (intArray[eX] != 0) {
                    return true;
                }
            }
        }

        return false;
    }

    private static int[] drawInOrOutLine(final int[] intArray, final int width, int color) {
        int[] outLineData = new int[intArray.length];
        int[] labelingData = new int[intArray.length];
        Stack<Integer> stack = new Stack();
        stack.push(width);
        while (!stack.isEmpty()) {
            int point = stack.pop();
            int[] checkMask = {point - width, point + width, point - 1, point + 1};
            if (checkMask[0] < 0 || checkMask[1] >= intArray.length || checkMask[2] < 0 || checkMask[3] >= intArray.length)
                continue;
            int maskTotal = checkMask.length;
            for (int i = 0; i < maskTotal; i++) {
                int dataNum = checkMask[i];
                if (intArray[dataNum] == Color.TRANSPARENT) {
                    if (labelingData[dataNum] != 1) {
                        labelingData[dataNum] = 1;
                        stack.push(dataNum);
                    }
                } else if (intArray[dataNum] == Color.BLACK) {
                    if (labelingData[dataNum] != 2) {
                        labelingData[dataNum] = 2;
                    }
                }
            }
        }

        //diy스티커 #ff000fd
        for (int i = 0; i < labelingData.length; i++) {
            if (labelingData[i] == 2) {
                outLineData[i] = color;
            } else {

            }
        }
        return outLineData;
    }

    public interface FinishIntArrayListener {
        void finish(int[] outLineBitmapArray);
    }


    /**
     * https://stackoverflow.com/questions/17783467/drawing-an-outer-shadow-when-drawing-an-image
     *
     * @param bm
     * @param color
     * @param size
     * @param dx
     * @param dy
     * @return
     */
    public static Bitmap addShadow(final Bitmap bm, int color, int size, float dx, float dy) {
        if (bm == null) {
            return null;
        }

        final int dstWidth = bm.getWidth();
        final int dstHeight = bm.getHeight();
        final Bitmap mask = Bitmap.createBitmap(dstWidth, dstHeight, ALPHA_8);

        final Matrix scaleToFit = new Matrix();
        final RectF src = new RectF(0, 0, bm.getWidth(), bm.getHeight());
        final RectF dst = new RectF(0, 0, dstWidth - dx, dstHeight - dy);
        scaleToFit.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);

        final Matrix dropShadow = new Matrix(scaleToFit);
        dropShadow.postTranslate(dx, dy);

        final Canvas maskCanvas = new Canvas(mask);
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskCanvas.drawBitmap(bm, scaleToFit, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
        maskCanvas.drawBitmap(bm, dropShadow, paint);

        final BlurMaskFilter filter = new BlurMaskFilter(size, BlurMaskFilter.Blur.NORMAL);
        paint.reset();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setMaskFilter(filter);
        paint.setFilterBitmap(true);

        final Bitmap ret = Bitmap.createBitmap(dstWidth, dstHeight, ARGB_8888);
        final Canvas retCanvas = new Canvas(ret);
        retCanvas.drawBitmap(mask, 0, 0, paint);
        retCanvas.drawBitmap(bm, scaleToFit, null);
        mask.recycle();
        return ret;
    }
}
