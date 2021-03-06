package com.snaps.mobile.activity.edit.fragment.canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.view.ViewGroup.LayoutParams;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.mobile.activity.edit.spc.ThemeBookCanvas;
import com.snaps.mobile.activity.edit.thumbnail_skin.SnapsThumbNailUtil;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderState;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import errorhandle.logger.Logg;

import static com.snaps.common.utils.imageloader.ImageLoader.DEFAULT_BITMAP_CONFIG;

public class ThemeBookCanvasFragment extends SnapsCanvasFragment {

    private static final String TAG = ThemeBookCanvasFragment.class.getSimpleName();

    @Override
    public void makeSnapsCanvas() {
        int index = getArguments().getInt("index");
        try {
            if (canvas == null) {
                canvas = new ThemeBookCanvas(getActivity().getApplicationContext());
                canvas.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

                if (isPreview) {
                    canvas.setZoomable(false);
                    canvas.setIsPreview(true);
                }
            }

            pageLoad = getArguments().getBoolean("pageLoad");

            boolean isPageSaving = getArguments().getBoolean("pageSave", false);
            canvas.setIsPageSaving(isPageSaving);

            if (pageLoad) {
                getEditActBridge().increaseCanvasLoadCompleteCount();
            }

            SnapsPage spcPage = getPageList().get(index);

            canvas.setCallBack(this);
            imageRange(spcPage, index);
            canvas.setSnapsPage(spcPage, index, true, null);
        } catch (Exception e) {
            Dlog.e(TAG, "????????? ?????? ??????" + " : " + index, e);
            setPageThumbnailFail(index);
        }
    }

    @Override
    protected void imageRange(SnapsPage page, int index) {
        try {

            SnapsLayoutControl layout;
            for (int i = 0; i < page.getLayoutList().size(); i++) {
                layout = (SnapsLayoutControl) page.getLayoutList().get(i);
                MyPhotoSelectImageData imgData = layout.imgData;
                if (imgData != null)
                    layout.angle = imgData.ROTATE_ANGLE + "";
            }

            if (page.type.equalsIgnoreCase("cover")) {
                SnapsTextControl textLayout;
                for (int i = 0; i < page.getControlList().size(); i++) {
                    textLayout = (SnapsTextControl) page.getControlList().get(i);
                    if (textLayout != null) {
                        if (page.type.equalsIgnoreCase("cover")) {// ??????-??????
                            if (textLayout.format.verticalView.equalsIgnoreCase("true")) {

                                if (!"".equals(textLayout.width) && !"".equals(textLayout.height)) {
                                    int iWidth = Integer.valueOf(textLayout.width);
                                    int iHeight = Integer.valueOf(textLayout.height);
                                    if (iWidth < iHeight) {
                                        String tmpWidth = textLayout.width;
                                        textLayout.width = textLayout.height;
                                        textLayout.height = tmpWidth;
                                    }
                                }
                            }
                        } else if (page.type.equalsIgnoreCase("title")) {// ??????-??????

                        } else if (page.type.equalsIgnoreCase("page")) {// ?????????-??????,??????

                        }
                    }
                }
            }

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

    }

    /**
     * ????????? ??? ????????? ????????? ??????????????? ??? ??????
     */
    @Override
    public void onImageLoadComplete(final int page) {
        // ?????? pause?????? ????????? ????????????, resume ??? ?????????????????? ???????????????.
        if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_APPLICATION)) {
            SnapsOrderManager.setSnapsOrderStatePauseCode(SnapsOrderState.PAUSE_IMGSAVE);
            return;
        }

        if (getArguments().getBoolean("pageSave")) {
            getArguments().remove("pageSave");
            getArguments().remove("pageLoad");
            getArguments().putBoolean("pageSave", false);
            getArguments().putBoolean("pageLoad", true);

            boolean isPreThumb = getArguments().getBoolean("preThumbnail");
            if (isPreThumb)
                saveLoadImageTask(page);
            getArguments().remove("preThumbnail");
            getArguments().putBoolean("preThumbnail", isPreThumb);
            getArguments().remove("onlyOnePage");

        } else {
            handleDecreaseCanvasLoadCompleteCount(page);
        }
    }

    @Override
    protected void handleDecreaseCanvasLoadCompleteCount(final int page) {
        getEditActBridge().decreaseCanvasLoadCompleteCount();
        if (getEditActBridge().getCanvasLoadCompleteCount() <= 0) {
            if (pageLoad) {
                if (onViewpagerListener != null)
                    onViewpagerListener.onPageLoadComplete(page);
            }
        }
    }

    @Override
    protected void saveLoadImageTask(final int page) {
        final String[] productData = SnapsThumbNailUtil.getThumbNailData(Config.getPROD_CODE(), canvas);
        String product = productData[0];
        final float scale = Float.parseFloat(productData[1]);
        if (TextUtils.isEmpty(product)) { //????????? ?????? ??????
            Dlog.d("saveLoadImageTask() onSkinLoaded() scale:" + scale);
            loadThumbNail(page, scale);
        } else {
            SnapsThumbNailUtil.downSkinImage(getContext(), product, bitmap -> {
                if (productData.length > 2) {
                    int x = Integer.parseInt(productData[2]);
                    int y = Integer.parseInt(productData[3]);
                    Dlog.d("saveLoadImageTask() onSkinLoaded() scale:" + scale + ", x:" + x + ", y:' + y");
                    loadThumbNail(page, bitmap, scale, x, y);
                } else {
                    Dlog.d("saveLoadImageTask() onSkinLoaded() scale:" + scale);
                    loadThumbNail(page, bitmap, scale);
                }
            });
        }
    }

    private void loadThumbNail(final int page, final float scale) {
        loadThumbNail(page, null, scale, 0, 0);
    }

    private void loadThumbNail(final int page, Bitmap skinBitmap, final float scale) {
        loadThumbNail(page, skinBitmap, scale, 0, 0);
    }

    private void loadThumbNail(final int page, final Bitmap skinBitmap, final float scale, final int x, final int y) {
        ATask.executeBoolean(new ATask.OnTaskResult() {

            String filePath = "";
            boolean isResult = true;

            @Override
            public void onPre() {
                Dlog.d("loadThumbNail() onPre()");
            }

            @Override
            public boolean onBG() {
                try {
                    long sleepTime = 100;
                    if (Const_PRODUCT.isAcrylicKeyringProduct() || Const_PRODUCT.isAcrylicStandProduct()) {
                        //????????? ????????? ????????? ????????? ????????? ???????????? ????????? ?????????. ????????? sleep ????????? ??? ??????.
                        //?????? ????????? ??? ?????? ????????? ????????? ?????? ???????????? ??????????????? ????????? ?????? ????????????.
                        sleepTime = 3000;
                    }
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Dlog.e(TAG, e);
                }
                return isResult;
            }

            @Override
            public void onPost(boolean result) {
                Dlog.d("loadThumbNail() onPost()");

                // ?????? pause?????? ????????? ????????????, resume ??? ?????????????????? ???????????????.
                if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_APPLICATION)) {
                    SnapsOrderManager.setSnapsOrderStatePauseCode(SnapsOrderState.PAUSE_IMGSAVE);
                    return;
                }

                Bitmap pageBitmap = getViewBitmapThumbNail(page, scale, x, y);
                Bitmap halfBitmap = null;

                if (pageBitmap == null) {
                    setPageThumbnailFail(page);
                    return;
                }

                if (skinBitmap != null) {
                    if (isBackgroundTransParent()) {
                        Bitmap bitmap = CropUtil.getInSampledBitmapCopy(skinBitmap, DEFAULT_BITMAP_CONFIG, 0);
                        Canvas margeCanvas = new Canvas(bitmap);
                        margeCanvas.drawBitmap(pageBitmap, new Matrix(), null);
                        pageBitmap = bitmap;
                    } else {
                        Bitmap bitmap = CropUtil.getInSampledBitmapCopy(pageBitmap, DEFAULT_BITMAP_CONFIG, 0);
                        Canvas margeCanvas = new Canvas(bitmap);
                        margeCanvas.drawBitmap(skinBitmap, new Matrix(), null);
                        pageBitmap = bitmap;
                    }
                    //BitmapUtil.bitmapRecycle(bitmap);
                }
                // ?????????????????? ?????????
                try {
                    if (page == 0) {
                        halfBitmap = CropUtil.getInSampledBitmapCopy(pageBitmap, DEFAULT_BITMAP_CONFIG, 0);
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }

                FileOutputStream stream = null;
                long stamp = System.currentTimeMillis();

                File file = null;
                try {
                    Config.checkThumbnailFileDir();
                    file = Config.getTHUMB_PATH("thumbnail_" + stamp + ".png");
                    filePath = file.getAbsolutePath();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }

                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                    isResult = false;
                    setPageThumbnailFail(page);
                    return;
                }

                try {
                    stream = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    Dlog.e(TAG, e);
                    isResult = false;
                    if (pageBitmap != null && !pageBitmap.isRecycled()) {
                        BitmapUtil.bitmapRecycle(pageBitmap);

                    }

                    setPageThumbnailFail(page);
                    return;
                }

                if (pageBitmap != null && !pageBitmap.isRecycled()) {
                    pageBitmap.compress(Bitmap.CompressFormat.PNG, 95, stream);
                    BitmapUtil.bitmapRecycle(pageBitmap);
                }

                try {
                    if (stream != null)
                        stream.close();
                } catch (IOException e) {
                    Dlog.e(TAG, e);
                }

                if (page == 0) {

                    // ?????? ???????????? ????????????. ???????????? ????????? ??????...
                    if (halfBitmap == null) {
                        halfBitmap = getHalfPageCanvasBitmap();
                    }
                    isResult = saveLocalThumbnail(getActivity(), halfBitmap);

                    BitmapUtil.bitmapRecycle(halfBitmap);

                    if (PhotobookCommonUtils.shouldUploadFullSizeThumbnailProduct()) {
                        Bitmap fullSizeBitmap = getViewBitmap(page, false);
                        isResult = saveLocalThumbnail(getActivity(), fullSizeBitmap, "fullSizeThumb.jpg");
                        BitmapUtil.bitmapRecycle(fullSizeBitmap);
                    }
                }

                if (isResult) {
                    // ???????????? ???????????? ?????????.
                    canvas.getSnapsPage().thumbnailPath = filePath;
                    setPageThumbnail(page, filePath);
                } else {
                    setPageThumbnailFail(page);
                }

                Dlog.d("loadThumbNail() ????????? ?????? ??????:" + isResult + ", page:" + page);

            }
        });

    }

}
