package com.snaps.mobile.activity.edit.skin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.imageloader.SnapsCustomTargets;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.BSize;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;

import java.io.File;
import java.util.concurrent.ExecutionException;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ysjeong on 2017. 6. 29..
 */

public class SnapsSkinUtil {
    private static final String TAG = SnapsSkinUtil.class.getSimpleName();

    public interface SnapsSkinLoadListener {
        void onSkinLoaded();
    }

    public static void loadSkinImage(SnapsSkinRequestAttribute skinRequestAttribute) throws Exception {
        if (isExistSkinCashFile(skinRequestAttribute)) {
            loadSkinImageFromCash(skinRequestAttribute);
        } else {
            loadSkinImageFromSnapsServer(skinRequestAttribute);
        }
    }

    private static void loadSkinImageFromCash(final SnapsSkinRequestAttribute skinRequestAttribute) throws Exception {
        View targetView = skinRequestAttribute.getSkinBackgroundView();
        File file = getSkinFilePathByRequestAttribute(skinRequestAttribute);
        String cashFilePath = file.getAbsolutePath();

        loadImage(targetView, skinRequestAttribute, cashFilePath);
    }

    private static void loadSkinImageFromSnapsServer(final SnapsSkinRequestAttribute skinRequestAttribute) throws Exception {
        String skinResourceFullUrl = getSnapsSkinResourceUrlWithFileName(skinRequestAttribute);
        View targetView = skinRequestAttribute.getSkinBackgroundView();
        loadImage(targetView, skinRequestAttribute, skinResourceFullUrl);
    }

    private static String getSnapsSkinResourceUrlWithFileName(SnapsSkinRequestAttribute requestAttribute) throws Exception {
        if (requestAttribute == null || StringUtil.isEmpty(requestAttribute.getResourceFileName()))
            return null;
        return SnapsAPI.DOMAIN() + SnapsSkinConstants.SNAPS_SKIN_RESOURCE_URL + requestAttribute.getResourceFileName();
    }

    private static void setSkinBackgroundDrawableByResource(SnapsSkinRequestAttribute skinRequestAttribute, byte[] resource) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeByteArray(resource, 0, resource.length, options);
            Drawable drawable = new BitmapDrawable(skinRequestAttribute.getContext().getResources(), bitmap);

            View targetView = skinRequestAttribute.getSkinBackgroundView();
            targetView.setBackgroundDrawable(drawable);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
        }
    }

    private static void setSkinBackgroundDrawableByResource(SnapsSkinRequestAttribute skinRequestAttribute, Bitmap resource) {
        if (!BitmapUtil.isUseAbleBitmap(resource)) return;
        try {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            Bitmap bitmap = resource.copy(Bitmap.Config.ARGB_8888, false);//BitmapFactory.decodeByteArray( resource, 0, resource.length, options );
            Drawable drawable = new BitmapDrawable(skinRequestAttribute.getContext().getResources(), resource);

            View targetView = skinRequestAttribute.getSkinBackgroundView();
            targetView.setBackgroundDrawable(drawable);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
        }
    }

    private static BSize getViewMeasuredSize(View view) {
        if (view == null) return new BSize(-1, -1);
        int width = view.getMeasuredWidth();
        int height = view.getMeasuredHeight();
        if (width <= 0 || height <= 0) {
            width = -1;
            height = -1;
        }
        return new BSize(width, height);
    }

    private static void loadImage(View targetView, final SnapsSkinRequestAttribute skinRequestAttribute, String targetUri) throws Exception {
        if (targetView instanceof ImageView) { //TODO  FrameLayout으로 되어 있는 부분을 ImageView로 고치는 작업을 진행하자.
            ImageLoader.with(skinRequestAttribute.getContext()).load(targetUri).into(targetView);
        } else {

            BSize size = getViewMeasuredSize(targetView);
            int imageOverrideSize = (int) (Math.max(size.getWidth(), size.getHeight()) * 1.5f);

            ImageLoader.with(skinRequestAttribute.getContext()).load(targetUri).override(imageOverrideSize, imageOverrideSize).skipMemoryCache(false).into(new SnapsCustomTargets<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition transition) {
                    setSkinBackgroundDrawableByResource(skinRequestAttribute, resource);

                    if (skinRequestAttribute.getSkinLoadListener() != null) {
                        skinRequestAttribute.getSkinLoadListener().onSkinLoaded();
                    }
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                }

                @Override
                public void onDestroy() {
                    super.onDestroy();
                }
            });
        }
    }

    private static File getSkinFilePathByRequestAttribute(SnapsSkinRequestAttribute skinRequestAttribute) throws Exception {
        String skinResourceFullUrl = getSnapsSkinResourceUrlWithFileName(skinRequestAttribute);
        String fileName = getFileNameFromUrl(skinResourceFullUrl);
        return new File(Const_VALUE.PATH_PACKAGE(ContextUtil.getContext(), true) + "/skin/", fileName);
    }

    private static boolean isExistSkinCashFile(SnapsSkinRequestAttribute skinRequestAttribute) throws Exception {
        String skinResourceFullUrl = getSnapsSkinResourceUrlWithFileName(skinRequestAttribute);
        String fileName = getFileNameFromUrl(skinResourceFullUrl);
        return new File(Const_VALUE.PATH_PACKAGE(ContextUtil.getContext(), true) + "/skin/", fileName).exists();
    }

    private static void createSkinCashFile(final SnapsSkinRequestAttribute skinRequestAttribute) throws Exception {

        ATask.executeVoidWithThreadPool(new ATask.OnTask() {
            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                try {
                    String skinResourceFullUrl = getSnapsSkinResourceUrlWithFileName(skinRequestAttribute);
                    File file = getSkinFilePathByRequestAttribute(skinRequestAttribute);
                    String cashFilePath = file.getAbsolutePath();
                    HttpUtil.saveUrlToFile(skinResourceFullUrl, cashFilePath);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            @Override
            public void onPost() {
            }
        });
    }

    private static Bitmap getCaseBitmapFromLocalFile(String fileName) {
        if (fileName == null || fileName.length() < 1) return null;
        String filePath = Const_VALUE.PATH_PACKAGE(ContextUtil.getContext(), true) + "/skin/" + fileName;
        return BitmapFactory.decodeFile(filePath);
    }

    private static String getFileNameFromUrl(String url) throws Exception {
        String name = "";
        String[] temp = url.split("/");
        if (temp.length > 0) name = temp[temp.length - 1];

        return name;
    }

    public static Single<Bitmap> getSkinImageRx(SnapsSkinRequestAttribute requestAttribute) {
        return Single.fromCallable(() -> {
            Bitmap bitmap;
            if (isExistSkinCashFile(requestAttribute)) {
                bitmap = getSkinImageFromCash(requestAttribute);
            } else {
                bitmap = getSkinImageFromSnapsServer(requestAttribute);
            }
            return bitmap;
        }).subscribeOn(Schedulers.io());
    }

    private static Bitmap getSkinImageFromCash(final SnapsSkinRequestAttribute skinRequestAttribute) throws Exception {
        File file = getSkinFilePathByRequestAttribute(skinRequestAttribute);
        String cashFilePath = file.getAbsolutePath();
        return getSkin(skinRequestAttribute, cashFilePath);
    }

    private static Bitmap getSkinImageFromSnapsServer(final SnapsSkinRequestAttribute skinRequestAttribute) throws Exception {
        return getSkin(skinRequestAttribute, getSnapsSkinResourceUrlWithFileName(skinRequestAttribute));
    }

    private static Bitmap getSkin(final SnapsSkinRequestAttribute skinRequestAttribute, String targetUri) throws ExecutionException, InterruptedException {
        int requestWidth = skinRequestAttribute.getRequestWidth();
        int requestHeight = skinRequestAttribute.getRequestHeight();

        return ImageLoader.with(skinRequestAttribute.getContext()).load(targetUri).skipMemoryCache(false)
                .override(requestWidth, requestHeight).submit().get();
    }
}
