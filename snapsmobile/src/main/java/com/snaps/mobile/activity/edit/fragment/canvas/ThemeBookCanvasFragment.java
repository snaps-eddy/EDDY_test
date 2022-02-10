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
            Dlog.e(TAG, "이미지 저장 실패" + " : " + index, e);
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
                        if (page.type.equalsIgnoreCase("cover")) {// 커버-제목
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
                        } else if (page.type.equalsIgnoreCase("title")) {// 속지-이름

                        } else if (page.type.equalsIgnoreCase("page")) {// 페이지-날짜,내용

                        }
                    }
                }
            }

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

    }

    /**
     * 페이지 내 이미지 로딩이 완료되었을 때 호출
     */
    @Override
    public void onImageLoadComplete(final int page) {
        // 앱이 pause되면 작업을 중지하고, resume 시 재작업하도록 상태표시함.
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
        if (TextUtils.isEmpty(product)) { //일기를 위한 로직
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
                        //아크릴 상품은 이미지 로딩후 칼선을 만드는데 시간이 걸린다. 그래서 sleep 시간을 더 준다.
                        //만약 시간을 더 주지 않으면 칼선이 없는 이미지만 만들어지는 경우가 자주 발생한다.
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

                // 앱이 pause되면 작업을 중지하고, resume 시 재작업하도록 상태표시함.
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
                // 대표썸네일을 만든다
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

                    // 대표 썸네일을 저장한다. 콜라주는 이미지 반만...
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
                    // 페이지에 썸네일을 넣는다.
                    canvas.getSnapsPage().thumbnailPath = filePath;
                    setPageThumbnail(page, filePath);
                } else {
                    setPageThumbnailFail(page);
                }

                Dlog.d("loadThumbNail() 이미지 저장 결과:" + isResult + ", page:" + page);

            }
        });

    }

}
