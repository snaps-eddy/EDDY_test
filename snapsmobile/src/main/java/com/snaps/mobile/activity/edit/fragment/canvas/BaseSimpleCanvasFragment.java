package com.snaps.mobile.activity.edit.fragment.canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsPageEditRequestInfo;
import com.snaps.mobile.activity.edit.spc.base.SceneCapturable;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderState;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.snaps.common.utils.imageloader.ImageLoader.DEFAULT_BITMAP_CONFIG;

/**
 * 이전에 큰 덩어리였던 CanvasFragment 리팩토링을
 * 위해 조금씩 필요 기능을 분리하기 위한 작업
 * 이 클래스를 확장한 클래스는 NewPhoneCase, SealSticker 이다.
 */
public abstract class BaseSimpleCanvasFragment extends SnapsCanvasFragment {
    protected static int CART_THUMB_WIDTH_HEIGHT = 720;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean isFragmentForCartThumbnail = viewPager == null;
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_pagecanvas2, container, false);

        if (isFragmentForCartThumbnail) {
            canvas = provideCanvasView(true);
            canvas.setId(R.id.fragment_root_view_id);
            canvas.setEnableButton(false);
            rootView.addView(canvas);
            makeSnapsCanvas();
            return rootView;
        }

        canvas = provideCanvasView(false);
        canvas.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        canvas.setGravity(Gravity.CENTER);
        canvas.setId(R.id.fragment_root_view_id);
        rootView.addView(canvas);

        boolean isVisibleButton = getArguments().getBoolean("visibleButton", true);
        canvas.setEnableButton(isVisibleButton);

        boolean isPageSaving = getArguments().getBoolean("pageSave", false);
        canvas.setIsPageSaving(isPageSaving);

        if (isPreview) {
            canvas.setZoomable(false);
            canvas.setIsPreview(true);
        }

        canvas.setLandscapeMode(isLandscapeMode);
        canvas.setSnapsPageClickListener(view -> {
            if (itemClickListener != null && canvas != null) {
                itemClickListener.onResult(new SnapsPageEditRequestInfo.Builder().setPageIndex(canvas.getPageNumber()).create());
            }
        });

        makeSnapsCanvas();

        if (viewPager != null) {
            canvas.setViewPager(viewPager);
            viewPager.addCanvas(canvas);
            viewPager.setPreventViewPagerScroll(canvas.isPreventViewPagerScroll());
        }
        return rootView;
    }

    protected abstract SnapsPageCanvas provideCanvasView(Boolean isCartThumbnail);

    protected void loadCartSkinBitmap() {}

    @Override
    public void makeSnapsCanvas() {

        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }

        int index = bundle.getInt("index");
        try {
            pageLoad = bundle.getBoolean("pageLoad");

            boolean isPageSaving = bundle.getBoolean("pageSave", false);
            canvas.setIsPageSaving(isPageSaving);

//          다시 그리는 이유가, 이미지가 변경되었거나 했을 때 적용하려고 다시한번 handleIncreaseCanvasLoadCompleteCount 하는 듯 하다.
//            if (isPageSaving) {
//                canvas.onFinishImageLoad();
//                return;
//            }

            if (pageLoad) {
                handleIncreaseCanvasLoadCompleteCount();
            }

            SnapsPage spcPage = getPageList().get(index);
            canvas.setCallBack(this);
            imageRange(spcPage, index);
            canvas.setSnapsPage(spcPage, index, true, null);

        } catch (Exception e) {
            Dlog.e(e);
            setPageThumbnailFail(index);
        }
    }

    @Override
    protected void imageRange(SnapsPage page, int index) {
        SnapsLayoutControl layout;
        for (int i = 0; i < page.getLayoutList().size(); i++) {
            layout = (SnapsLayoutControl) page.getLayoutList().get(i);
            MyPhotoSelectImageData imgData = layout.imgData;
            if (imgData != null) {
                layout.angle = imgData.ROTATE_ANGLE + "";
            }
        }
    }

    @Override
    protected void saveLoadImageTask(final int page) {
        prepareMakeCartThumbnail();

    }

    private void prepareMakeCartThumbnail() {
        ATask.executeBoolean(new ATask.OnTaskResult() {

            String filePath = "";
            boolean isResult = true;

            @Override
            public void onPre() {
                Dlog.d("loadThumbNail() onPre()");
            }

            @Override
            public boolean onBG() {
                if (canvas instanceof SceneCapturable) {
                    loadCartSkinBitmap();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Dlog.e(e);
                }
                return isResult;
            }

            @Override
            public void onPost(boolean result) {
                // 앱이 pause되면 작업을 중지하고, resume 시 재작업하도록 상태표시함.
                if (SnapsOrderManager.getSnapsOrderStatePauseCode().equalsIgnoreCase(SnapsOrderState.PAUSE_APPLICATION)) {
                    SnapsOrderManager.setSnapsOrderStatePauseCode(SnapsOrderState.PAUSE_IMGSAVE);
                    return;
                }

                Bitmap pageBitmap = captureCanvasView();

                if (pageBitmap == null) {
                    setPageThumbnailFail(0);
                    return;
                }

                FileOutputStream stream;
                long stamp = System.currentTimeMillis();

                File file = null;
                try {
                    Config.checkThumbnailFileDir();
                    file = Config.getTHUMB_PATH("thumbnail_" + stamp + ".png");
                    filePath = file.getAbsolutePath();
                } catch (Exception e) {
                    Dlog.e(e);
                }

                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                } catch (Exception e) {
                    Dlog.e(e);
                    isResult = false;
                    setPageThumbnailFail(0);
                    return;
                }

                try {
                    stream = new FileOutputStream(file);

                } catch (FileNotFoundException e) {
                    Dlog.e(e);
                    isResult = false;
                    if (!pageBitmap.isRecycled()) {
                        BitmapUtil.bitmapRecycle(pageBitmap);
                    }
                    setPageThumbnailFail(0);
                    return;
                }

                isResult = saveLocalThumbnail(getActivity(), pageBitmap);
                if (!pageBitmap.isRecycled()) {
                    pageBitmap.compress(Bitmap.CompressFormat.PNG, 95, stream);
                    BitmapUtil.bitmapRecycle(pageBitmap);
                }

                try {
                    stream.close();
                } catch (IOException e) {
                    Dlog.e(e);
                }

                Bitmap fullSizeBitmap = captureCurrentView();
                isResult = saveLocalThumbnail(getActivity(), fullSizeBitmap, "fullSizeThumb.jpg");
                BitmapUtil.bitmapRecycle(fullSizeBitmap);

                if (isResult) {
                    canvas.getSnapsPage().thumbnailPath = filePath;
                    setPageThumbnail(0, filePath);
                } else {
                    setPageThumbnailFail(0);
                }
            }
        });
    }

    protected Bitmap captureCanvasView() {
        if (canvas == null) {
            return null;    //화면 전환이 될 때, 발생할 수 있다.
        }

        Bitmap bmp = null;
        try {
            Bitmap bgBmp = getInSampledBitmap(CART_THUMB_WIDTH_HEIGHT, CART_THUMB_WIDTH_HEIGHT);
            Bitmap orgBmp = captureCurrentView();
            float scaleByHeight = (float)CART_THUMB_WIDTH_HEIGHT / orgBmp.getHeight();

            orgBmp = CropUtil.getInSampledBitmapCopy(orgBmp, DEFAULT_BITMAP_CONFIG, 1, scaleByHeight);
            if (orgBmp == null) {
                return null;
            }

            Canvas cvs2 = new Canvas(bgBmp);
            cvs2.drawRGB(250, 250, 250);
            cvs2.drawBitmap(orgBmp, ((bgBmp.getWidth() / 2.f) - (orgBmp.getWidth() / 2.f)), ((bgBmp.getHeight() / 2.f) - orgBmp.getHeight() / 2.f), null);

            bmp = bgBmp;
        } catch (Exception e) {
            Dlog.e(e);
        }
        return bmp;
    }

    @Nullable
    protected Bitmap captureCurrentView() {
        if (canvas instanceof SceneCapturable) {
            return ((SceneCapturable) canvas).getThumbnailBitmap();
        } else {
            return null;
        }
    }
}
