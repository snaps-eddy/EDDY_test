package com.snaps.mobile.activity.edit.fragment.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.imp.ISnapsPageItemInterface;
import com.snaps.common.imp.iSnapsPageCanvasInterface;
import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.spc.view.CustomImageView;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.text.SnapsTextToImageView;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsPageEditRequestInfo;
import com.snaps.mobile.activity.common.interfacies.SnapsEditActExternalConnectionBridge;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.edit.spc.DIYStickerPageCanvas;
import com.snaps.mobile.activity.edit.spc.NewPhoneCaseCanvas;
import com.snaps.mobile.activity.edit.spc.SealStickerPageCanvas;
import com.snaps.mobile.activity.edit.spc.SnapsCanvasFactory;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderState;
import com.snaps.mobile.utils.custom_layouts.InterceptTouchableViewPager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import errorhandle.SnapsAssert;

import static com.snaps.common.utils.imageloader.ImageLoader.DEFAULT_BITMAP_CONFIG;
import static com.snaps.common.utils.imageloader.ImageLoader.MAX_DOWN_SAMPLE_RATIO;

/**
 * com.snaps.kakao.activity.edit.fragment SnapsCanvasFragment.java
 *
 * @author ParkJaeMyung
 * @Date : 2013. 5. 25.
 * @Version :
 */
public abstract class SnapsCanvasFragment extends Fragment implements iSnapsPageCanvasInterface {
    private static final String TAG = SnapsCanvasFragment.class.getSimpleName();
    protected SnapsPageCanvas canvas = null;
    protected InterceptTouchableViewPager viewPager = null;
    protected boolean isLandscapeMode = false;
    protected boolean isPreview = false;

    /**
     * ????????? ?????? ??????. ????????? ????????? ?????????. ?????? ????????? ??????
     **/
    protected boolean pageLoad;

    float scaleX = 1.f;
    float scaleY = 1.f;

    protected ISnapsPageItemInterface onViewpagerListener = null;

    protected SnapsCommonResultListener<SnapsPageEditRequestInfo> itemClickListener = null;

    /**
     * Canvas ??????.
     */
    public abstract void makeSnapsCanvas();

    /**
     * ????????? ??????
     *
     * @param index
     */
    protected abstract void imageRange(SnapsPage page, int index);

    /**
     * ?????? ????????? ?????? Task
     *
     * @param page
     */
    protected abstract void saveLoadImageTask(final int page);


    public void destroyCanvas() {
        if (canvas != null) {
            canvas.onDestroyCanvas();
            canvas.suspendLayerLoad();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (viewPager == null && Const_PRODUCT.isFreeSizeProduct()) {
            //???????????? ???????????? ???????????? ????????? ???????????? ????????? ?????? ???????????? ?????? view ??? ????????????
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_pagecanvas2, container, false);
            canvas = new SnapsCanvasFactory().createPageCanvas(getActivity(), Config.getPROD_CODE());
            canvas.setId(R.id.fragment_root_view_id);
            rootView.addView(canvas);
            return rootView;
        }
//        if (Const_PRODUCT.isLegacyPhoneCaseProduct() || Const_PRODUCT.isUvPhoneCaseProduct() || Const_PRODUCT.isPrintPhoneCaseProduct() || Const_PRODUCT.isSealStickerProduct()) { // @Marko ??? ????????? ????????? ????????? ????????????.
//            rootView = (ViewGroup) inflater.inflate(R.layout.fragment_pagecanvas2, container, false);
//        } else {
//            rootView = (ViewGroup) inflater.inflate(R.layout.fragment_pagecanvas, container, false);
//        }
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_pagecanvas, container, false);
        canvas = new SnapsCanvasFactory().createPageCanvas(getActivity(), Config.getPROD_CODE());

        if (canvas != null) {
            canvas.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
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
        }

        return rootView;
    }

    public void setOnViewpagerListener(ISnapsPageItemInterface onViewpagerListener) {
        this.onViewpagerListener = onViewpagerListener;
    }

    @Override
    public void onDestroy() {
        destroyCanvas();
        canvas = null;
        onViewpagerListener = null;

        super.onDestroy();
        ViewUnbindHelper.unbindReferences(getView());
    }

    /**
     * SnapsControl ?????????.
     */
    protected final static Comparator<SnapsControl> myComparator = new Comparator<SnapsControl>() {
        private final Collator _collator = Collator.getInstance();

        @Override
        public int compare(SnapsControl p, SnapsControl n) {
            return _collator.compare(p.regValue, n.regValue);
        }
    };

    /**
     * ?????? ?????? ????????? ??????.
     *
     * @param page
     * @return Boolean ?????? ??????.
     */
    public boolean setViewBitmapToFile(final int page, Boolean bg) {
        View v;

        if (Config.isSnapsSticker()) {
            v = canvas.getPageContainer();
        } else if (Config.isThemeBook()) {
            v = getView();
        } else {
            v = getView();
        }

        Bitmap orgBmp = CropUtil.getInSampledBitmap(v.getWidth(), v.getHeight(), DEFAULT_BITMAP_CONFIG);
        Canvas cvs = new Canvas(orgBmp);

        if (bg)
            cvs.drawColor(Color.WHITE);

        v.draw(cvs);

        // ?????????????????? ????????? ????????? ?????????..
        Bitmap bmp = CropUtil.getInSampledScaleBitmap(orgBmp, canvas.getEDWidth(), canvas.getEDHeight());
        if (orgBmp != bmp && orgBmp != null && !orgBmp.isRecycled()) {
            orgBmp.recycle();
            orgBmp = null;
        }

        FileOutputStream stream = null;

        try {
            File file = null;

            try {
                file = Config.getTHUMB_PATH("thumbnail_" + page + ".png");
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }

            try {
                if (file != null && !file.exists()) {
                    file.createNewFile();
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }

            stream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 95, stream);
            bmp.recycle();
            bmp = null;
        } catch (FileNotFoundException e) {
            Dlog.e(TAG, e);
            return false;
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                Dlog.e(TAG, e);
            }
        }

        return true;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    /**
     * ???????????? bitmap?????? ???????????? ??????
     *
     * @return
     */
    protected Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    public Bitmap getInSampledBitmap(int width, int height) throws IOException {
        return getInSampledBitmap(width, height, 1);
    }

    public Bitmap getInSampledBitmap(int width, int height, int samplingRatio) throws IOException {
        Bitmap imgBitmap;

        try {
            if (width < 1 || height < 1) return null;

            imgBitmap = Bitmap.createBitmap(width, height, DEFAULT_BITMAP_CONFIG);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            samplingRatio *= 2;

            //?????? ????????? ???????????? ?????? ??? ?????? ????????? ????????? ??????.
            if (samplingRatio <= MAX_DOWN_SAMPLE_RATIO) {
                return getInSampledBitmap(width / samplingRatio, height / samplingRatio, samplingRatio);
            } else {
                return null;
            }
        }
        return imgBitmap;
    }

    public Bitmap getInSampledBitmap(Bitmap bm, int x, int y, int width, int height) throws IOException {
        return getInSampledBitmap(bm, x, y, width, height, 1);
    }

    public Bitmap getInSampledBitmap(Bitmap bm, int x, int y, int width, int height, int samplingRatio) throws IOException {
        Bitmap imgBitmap = null;

        try {
            imgBitmap = Bitmap.createBitmap(bm, x, y, width, height);
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            samplingRatio *= 2;

            //?????? ????????? ???????????? ?????? ??? ?????? ????????? ????????? ??????.
            if (samplingRatio <= MAX_DOWN_SAMPLE_RATIO) {
                return getInSampledBitmap(bm, x, y, width / 2, height / 2, samplingRatio);
            } else {
                return null;
            }
        }
        return imgBitmap;
    }

    public Bitmap getViewBitmapThumbNail(final int page, float scale, int x, int y) {
        if (canvas == null || (page > 0 && !SnapsOrderManager.isUploadingProject())) {
            return null;    //?????? ????????? ??? ???, ????????? ??? ??????.
        }

        float cartThumbnailStandard = 408.0f;

        Bitmap bmp = null;
        try {
            View v;
            SnapsPageCanvas rootView = null;
            Dlog.d("getViewBitmapThumbNail() page:" + page);
            View root = canvas.findViewById(R.id.fragment_root_view_id);
            if (root != null && root instanceof SnapsPageCanvas) {
                rootView = (SnapsPageCanvas) canvas.findViewById(R.id.fragment_root_view_id);
            }
            v = canvas.getPageContainer();
            Bitmap orgBmp = null;
            Bitmap bgBmp = null;

            int width = 480;
            int height = 480;
            if (Const_PRODUCT.isCartThumb720x720()) {
                width = 720;
                height = 720;
            }
            bgBmp = getInSampledBitmap(width, height);

            if (Const_PRODUCT.isMetalFrame() || Const_PRODUCT.isWoodFrame() || Const_PRODUCT.isMarvelFrame() || Const_PRODUCT.isWoodBlockProduct() || Config.isWoodBlockCalendar()) {
                View shadowView = rootView.getChildAt(0);
                orgBmp = getInSampledBitmap(shadowView.getWidth(), shadowView.getHeight());
                Canvas cvs = new Canvas(orgBmp);
//					shadowView.draw(cvs);

                for (int ii = 0; ii < rootView.getChildCount(); ii++) {
                    View childView = rootView.getChildAt(ii);

                    if (Const_PRODUCT.isWoodBlockProduct() || Config.isWoodBlockCalendar()) {
                        if (ii == rootView.getChildCount() - 1) continue;
                    } else {
                        if (childView == shadowView) continue;
                    }
                    RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) childView.getLayoutParams();
                    cvs.translate(params.leftMargin, params.topMargin);
                    childView.draw(cvs);
                    cvs.translate(-params.leftMargin, -params.topMargin);
                }

            } else if (Const_PRODUCT.isAccordionCardProduct()) {
                orgBmp = getInSampledBitmap(canvas.getOrgWidth(), canvas.getOrgHeight());
                Canvas cvs = new Canvas(orgBmp);
                v.draw(cvs);
                Bitmap converted = Bitmap.createBitmap(orgBmp, 0, 0, orgBmp.getWidth() / 6, orgBmp.getHeight());
                orgBmp = CropUtil.getInSampledBitmapCopy(converted, DEFAULT_BITMAP_CONFIG, 0);
                BitmapUtil.bitmapRecycle(converted);
            } else if (Const_PRODUCT.isDIYStickerProduct() || Const_PRODUCT.isSealStickerProduct()) {
                View shadowView = rootView.getChildAt(0);

                if (Const_PRODUCT.isDIYStickerProduct()) {
                    ((DIYStickerPageCanvas) canvas).setHidenButton();
                } else if (Const_PRODUCT.isSealStickerProduct()) {
                    ((SealStickerPageCanvas) canvas).setHidenButton();
                }

                v = canvas.getPageContainer();
                orgBmp = getInSampledBitmap(shadowView.getWidth(), shadowView.getHeight());
                Canvas cvs = new Canvas(orgBmp);
                shadowView.draw(cvs);
                v.draw(cvs);

                if (Const_PRODUCT.isDIYStickerProduct()) {
                    ((DIYStickerPageCanvas) canvas).setShownButton();
                } else if (Const_PRODUCT.isSealStickerProduct()) {
                    ((SealStickerPageCanvas) canvas).setShownButton();
                }

            } else {

                orgBmp = getInSampledBitmap(canvas.getOrgWidth(), canvas.getOrgHeight());
                Canvas cvs = new Canvas(orgBmp);

                if (Const_PRODUCT.isDesignNoteProduct() || Const_PRODUCT.isPhotoMugCupProduct() || Config.isPhotobooks() || Config.isSNSBook()) {
                    v.draw(cvs);

                    Bitmap converted = Bitmap.createBitmap(orgBmp, orgBmp.getWidth() / 2, 0, orgBmp.getWidth() / 2, orgBmp.getHeight());
                    orgBmp = CropUtil.getInSampledBitmapCopy(converted, DEFAULT_BITMAP_CONFIG, 0);
                    BitmapUtil.bitmapRecycle(converted);

                } else if (Const_PRODUCT.isAcrylicStandProduct() || Const_PRODUCT.isAcrylicKeyringProduct()) {

                    View multiplierView = canvas.getPageMultiFlyLayer();
                    if (multiplierView != null) {
                        multiplierView.draw(cvs);
                        v.draw(cvs);
                    }

                    if (orgBmp.getHeight() > orgBmp.getWidth()) {
                        scale = cartThumbnailStandard / (float) orgBmp.getHeight();
                    } else {
                        scale = cartThumbnailStandard / (float) orgBmp.getWidth();
                    }

                } else if (Const_PRODUCT.isAirpodsCaseProduct() || Const_PRODUCT.isBudsCaseProduct() || Const_PRODUCT.isTinCaseProduct()) {
                    //???????????? ????????? ?????? ????????? ????????? ??????????????? ????????? ???????????? ???????????? ????????? ??????
                    List<View> childViewList = getAllChildrenBFS(v);
                    List<CustomImageView> customImageViewList = new ArrayList<>();
                    for (View view : childViewList) {
                        if (view instanceof CustomImageView) {
                            CustomImageView customImageView = (CustomImageView) view;
                            if (customImageView.getLayoutControl().imgData == null) {
                                customImageViewList.add(customImageView);
                                view.setVisibility(View.INVISIBLE);    //?????????
                            }
                        }
                    }

                    v.draw(cvs);

                    for (CustomImageView customImageView : customImageViewList) {
                        customImageView.setVisibility(View.VISIBLE);    //??????
                    }
                } else if (Const_PRODUCT.isReflectiveSloganProduct() || Const_PRODUCT.isHolographySloganProduct() ||
                        Const_PRODUCT.isMagicalReflectiveSloganProduct()) {
                    //?????? ????????? ?????? ?????? ?????????????????? ????????? ???????????? ?????? ??????????????? ????????? ????????? ?????? ??????
                    //??????! ?????? ???????????? ??????????????? ?????????!!
                    List<View> childViewList = getAllChildrenBFS(v);
                    List<SnapsTextToImageView> snapsTextToImageViewList = new ArrayList<>();
                    for (View view : childViewList) {
                        if (view instanceof SnapsTextToImageView) {
                            SnapsTextToImageView snapsTextToImageView = (SnapsTextToImageView) view;
                            SnapsTextControl snapsTextControl = snapsTextToImageView.getAttribute().getSnapsTextControl();
                            if (!snapsTextControl.isEditedText) {
                                snapsTextToImageViewList.add(snapsTextToImageView);
                                snapsTextToImageView.setVisibleOutLine(false);
                            }
                        }
                    }

                    //?????? ?????????, ???????????? ?????????, ????????? ?????? ???????????? ???????????? ???????????? ?????? 408??? ????????? ????????? ????????? ????????? ?????? ?????? scale ?????? ???????????? ?????????
                    scale = cartThumbnailStandard / orgBmp.getWidth();
                    v.draw(cvs);

                    //??????
                    for (SnapsTextToImageView snapsTextToImageView : snapsTextToImageViewList) {
                        snapsTextToImageView.setVisibleOutLine(true);
                    }
                } else if (Const_PRODUCT.isBabyNameStikerGroupProduct()) {
                    // @Marko 2020.08.21 ???????????????????????? ????????? ???????????? ?????? ???????????? ????????? ?????????. ????????? scale ??? ?????? ???????????? ??????????????? ??????.
                    // ???????????? ?????? ???????????? ????????? ???????????? 330f ??????. ?????? ???????????? ?????? ???????????? ???????????????(???????????? ??????????????? ????????? ????????? ??????(??????,??????)??? ??????????????? ??? ?????? ?????? ???????????????.
                    scale = 330f / orgBmp.getWidth();
                    v.draw(cvs);

                } else {
                    v.draw(cvs);
                }
            }
            orgBmp = CropUtil.getInSampledBitmapCopy(orgBmp, DEFAULT_BITMAP_CONFIG, 1, scale);
            Canvas cvs2 = new Canvas(bgBmp);

            //???????????? ????????? ????????? ?????? ???????????? ?????? ??????
            //????????? ????????? ??????, ?????? ???????????? ????????? ???????????? ????????? ?????????.
            //???????????? ?????? png??? ?????? ??? ??? ?????????
            if (!isBackgroundTransParent()) {
                cvs2.drawRGB(250, 250, 250);
            }

            if (Const_PRODUCT.isMetalFrame() || Const_PRODUCT.isWoodFrame() || Const_PRODUCT.isMarvelFrame()) {
                orgBmp = ImageUtil.applyShadowOnImage(orgBmp);
            }
            cvs2.drawBitmap(orgBmp, ((bgBmp.getWidth() / 2) - (orgBmp.getWidth() / 2)) + x, ((bgBmp.getHeight() / 2) - orgBmp.getHeight() / 2) + y, null);
            bmp = bgBmp;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return bmp;
    }

    protected boolean isBackgroundTransParent() {
        if (Const_PRODUCT.isAirpodsCaseProduct()) return true;
        if (Const_PRODUCT.isBudsCaseProduct()) return true;
        if (Const_PRODUCT.isTinCaseProduct()) return true;

        return false;
    }

    private List<View> getAllChildrenBFS(View v) {
        List<View> visited = new ArrayList<View>();
        List<View> unvisited = new ArrayList<View>();
        unvisited.add(v);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            visited.add(child);
            if (!(child instanceof ViewGroup)) continue;
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) unvisited.add(group.getChildAt(i));
        }

        return visited;
    }


//    public static Bitmap addShadow(final Bitmap bm, final int dstHeight, final int dstWidth, float dx, float dy) {
////        final Bitmap mask = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
//		Paint ptBlur = new Paint();
//		int[] offsetXY = new int[2];
//		final Bitmap mask = bm.extractAlpha(ptBlur, offsetXY);
//
//        final Matrix scaleToFit = new Matrix();
//        final RectF src = new RectF(0, 0, bm.getUserSelectWidth(), bm.getHeight());
//        final RectF dst = new RectF(0, 0, dstWidth - dx, dstHeight - dy);
//        scaleToFit.setRectToRect(src, dst, Matrix.ScaleToFit.CENTER);
//
//        final Matrix dropShadow = new Matrix(scaleToFit);
//        dropShadow.postTranslate(dx, dy);
//
//        final Canvas maskCanvas = new Canvas(mask);
//        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        maskCanvas.drawBitmap(bm, scaleToFit, paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
//		paint.setShadowLayer(5.0f, 3.0f, 3.0f, Color.parseColor("#33000000"));
//        maskCanvas.drawBitmap(bm, dropShadow, paint);
//
////        final BlurMaskFilter filter = new BlurMaskFilter(size, BlurMaskFilter.Blur.NORMAL);
//       // paint.reset();
////        paint.setAntiAlias(true);
////        paint.setColor(color);
////        paint.setMaskFilter(filter);
////        paint.setFilterBitmap(true);
//
//        final Bitmap ret = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);
//        final Canvas retCanvas = new Canvas(ret);
//        retCanvas.drawBitmap(mask, 0,  0, paint);
//        retCanvas.drawBitmap(bm, scaleToFit, null);
//        mask.recycle();
//        return ret;
//    }

    //?????? ??????
    public Bitmap getViewBitmap(final int page, Boolean bg) {
        if (canvas == null || (page > 0 && !SnapsOrderManager.isUploadingProject()))
            return null;    //?????? ????????? ??? ???, ????????? ??? ??????.

        Bitmap bmp = null;
        try {
            View v = null;
            SnapsPageCanvas rootView = null;
            Dlog.d("getViewBitmap() page:" + page);

            boolean isFlag = false;// 1000*1000????????? ???????????? ???????????? ????????????..
            if (SnapsDiaryDataManager.isAliveSnapsDiaryService()) {
                v = canvas.getPageContainer();
            } else {

                View root = canvas.findViewById(R.id.fragment_root_view_id);
                if (root != null && root instanceof SnapsPageCanvas) {
                    rootView = (SnapsPageCanvas) canvas.findViewById(R.id.fragment_root_view_id);
                }

                isFlag = true;

//				?????? ??? ?????? ????????? 0?????? ????????????..
                //TODO  ?????? ???????????? ???????????? ?????? ????????? ???????????????, ?????? ????????? ???????????? ?????? ?????? ????????? ??????...
                if (rootView != null && (rootView.getWidth() < 1 || rootView.getHeight() < 1)) {
                    rootView = null;
                    v = getView();
                    isFlag = false;
                } else
                    v = getView();
            }

            Bitmap orgBmp = null;

            if (rootView != null) {
                if (rootView.getChildCount() > 0) {

                    View shadowView = null;

                    if (Config.isCalendar()) {  //????????? ???????????? shadow2?????? ??? ???????????? ??????????????? ???????????? ???????????? ??????...
                        if (rootView.getChildCount() > 0) {
                            shadowView = rootView.getChildAt(rootView.getChildCount() - 1);
                        }

                        if (shadowView != null && shadowView.getWidth() > 0 && shadowView.getHeight() > 0) {
                            orgBmp = getInSampledBitmap(shadowView.getWidth(), shadowView.getHeight());
                            if (orgBmp == null) return null;

                            Canvas cvs = new Canvas(orgBmp);

                            for (int ii = 0; ii < rootView.getChildCount(); ii++) {
                                View childView = rootView.getChildAt(ii);
                                if (childView == shadowView) continue;

                                RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) childView.getLayoutParams();
                                cvs.translate(params.leftMargin, params.topMargin);
                                childView.draw(cvs);
                                cvs.translate(-params.leftMargin, -params.topMargin);
                            }

                            shadowView.draw(cvs);
                        } else {
                            if (rootView.getWidth() > 0 && rootView.getHeight() > 0) {
                                orgBmp = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), DEFAULT_BITMAP_CONFIG);
                                Canvas cvs = new Canvas(orgBmp);
                                rootView.draw(cvs);
                            }
                        }
                    } else if (Const_PRODUCT.isFrameProduct()) {
                        //TODO  ?????? ????????? ????????? ?????? ??? ????????? ????????? ?????? ?????????...
                        shadowView = rootView.getChildAt(0);
                        if (shadowView != null) {
                            orgBmp = getInSampledBitmap(shadowView.getWidth(), shadowView.getHeight());
                            Canvas cvs = new Canvas(orgBmp);
                            shadowView.draw(cvs);
                            for (int ii = 0; ii < rootView.getChildCount(); ii++) {
                                View childView = rootView.getChildAt(ii);
                                if (childView == shadowView) continue;

                                RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) childView.getLayoutParams();
                                cvs.translate(params.leftMargin, params.topMargin);
                                childView.draw(cvs);
                                cvs.translate(-params.leftMargin, -params.topMargin);
                            }
                        } else {
                            float scale = 1f;
                            try {
                                View frameView = rootView.getChildAt(rootView.getChildCount() - 1); //????????? ?????????
                                if (frameView.getMeasuredHeight() > frameView.getMeasuredWidth())
                                    scale = 1.35f;
                                else if (frameView.getMeasuredHeight() == frameView.getMeasuredWidth())
                                    scale = Const_PRODUCT.isMarvelFrame() ? 1.38f : 1.41f;
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                            }

                            if (rootView.getWidth() > 0 && rootView.getHeight() > 0) {
                                orgBmp = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), DEFAULT_BITMAP_CONFIG);
                                Canvas cvs = new Canvas(orgBmp);

                                cvs.scale(scale, scale, cvs.getWidth() / 2, cvs.getHeight() / 2);
                                rootView.draw(cvs);
                            }
                        }
                    } else if (Const_PRODUCT.isPostCardProduct() || Const_PRODUCT.isDesignNoteProduct() || Const_PRODUCT.isCardProduct()) {
                        shadowView = rootView.getChildAt(rootView.getChildCount() - 1); //????????? ??? ?????? ????????? ?????? ????????? ????????? ?????????
                        if (shadowView.getWidth() > 0 && shadowView.getHeight() > 0) {
                            orgBmp = getInSampledBitmap(shadowView.getWidth(), shadowView.getHeight());
                            Canvas cvs = new Canvas(orgBmp);
                            if (!Const_PRODUCT.isDesignNoteProduct()) {
                                shadowView.draw(cvs);
                            }

                            for (int ii = 0; ii < rootView.getChildCount(); ii++) {
                                View childView = rootView.getChildAt(ii);
                                if (childView == shadowView) continue;

                                RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) childView.getLayoutParams();
                                cvs.translate(params.leftMargin, params.topMargin);

                                float scaleX = childView.getScaleX();
                                float scaleY = childView.getScaleY();
                                cvs.scale(scaleX, scaleY, cvs.getWidth() / 2, cvs.getHeight() / 2);

                                childView.draw(cvs);

                                cvs.translate(-params.leftMargin, -params.topMargin);
                            }
                            if (Const_PRODUCT.isDesignNoteProduct()) {
                                shadowView.draw(cvs);
                            }
                        }
                    } else if (Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isTransparencyPhotoCardProduct()) {
                        shadowView = rootView.getChildAt(rootView.getChildCount() - 1); //????????? ??? ?????? ????????? ?????? ????????? ????????? ?????????
                        if (shadowView.getWidth() > 0 && shadowView.getHeight() > 0) {
                            orgBmp = getInSampledBitmap(shadowView.getWidth(), shadowView.getHeight());
                            Canvas cvs = new Canvas(orgBmp);
                            shadowView.draw(cvs);

                            for (int ii = 0; ii < rootView.getChildCount(); ii++) {
                                View childView = rootView.getChildAt(ii);

                                RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) childView.getLayoutParams();

                                float translateDX = params.leftMargin;
                                float translateDY = params.topMargin;
                                if (childView == shadowView) {
                                    translateDX = -Const_PRODUCT.PHOTO_CARD_MARGIN_LIST[0];
                                    translateDY = -Const_PRODUCT.PHOTO_CARD_MARGIN_LIST[1];
                                }
                                cvs.translate(translateDX, translateDY);

                                float scaleX = childView.getScaleX();
                                float scaleY = childView.getScaleY();
                                cvs.scale(scaleX, scaleY, cvs.getWidth() / 2, cvs.getHeight() / 2);

                                childView.draw(cvs);

                                cvs.translate(-translateDX, -translateDY);
                            }
                        }
                    } else if (Const_PRODUCT.isStikerGroupProduct()) {
                        ImageView testView2 = new ImageView(getContext());
                        Glide.with(getContext()).load("https://www.dropbox.com/s/j7nj1xwg0ky2lj3/sticker_roundsticker.png?dl=1").listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        }).into(testView2);

                        View testView = canvas.getPageContainer();
                        orgBmp = getInSampledBitmap(testView.getWidth(), testView.getHeight());
                        Canvas cvs = new Canvas(orgBmp);
                        testView.draw(cvs);
//							testView2.draw(cvs);


                    } else if (Const_PRODUCT.isUvPhoneCaseProduct()) {

                        orgBmp = ((NewPhoneCaseCanvas) canvas).getThumbnailBitmap();

                    } else {
                        shadowView = rootView.getChildAt(0);
                        if (shadowView.getWidth() > 0 && shadowView.getHeight() > 0) {
                            orgBmp = getInSampledBitmap(shadowView.getWidth(), shadowView.getHeight());
                            Canvas cvs = new Canvas(orgBmp);
                            shadowView.draw(cvs);

                            for (int ii = 0; ii < rootView.getChildCount(); ii++) {
                                View childView = rootView.getChildAt(ii);
                                if (childView == shadowView) continue;

                                RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) childView.getLayoutParams();
                                cvs.translate(params.leftMargin, params.topMargin);
                                childView.draw(cvs);
                                cvs.translate(-params.leftMargin, -params.topMargin);  //test
                            }
                        }
                    }
                } else {
                    if (rootView.getWidth() > 0 && rootView.getHeight() > 0) {
                        orgBmp = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), DEFAULT_BITMAP_CONFIG);
                        Canvas cvs = new Canvas(orgBmp);
                        rootView.draw(cvs);
                    }
                }
            } else if (isFlag) {

                Bitmap tempBmp = getInSampledBitmap(v.getWidth(), v.getHeight());
                Canvas cvs = new Canvas(tempBmp);

                v.draw(cvs);

                orgBmp = getInSampledBitmap(tempBmp, (v.getWidth() - canvas.getEDWidth()) / 2, (v.getHeight() - canvas.getEDHeight()) / 2, canvas.getEDWidth(), canvas.getEDHeight());

                if (orgBmp != tempBmp) {
                    if (tempBmp != null && !tempBmp.isRecycled()) {
                        tempBmp.recycle();
                        tempBmp = null;
                    }
                }

            } else {

                orgBmp = getInSampledBitmap(canvas.getEDWidth(), canvas.getEDHeight());

                Canvas cvs = new Canvas(orgBmp);

                if (bg)
                    cvs.drawColor(Color.WHITE);

                v.draw(cvs);
            }

            // ?????????????????? ????????? ????????? ?????????..
            bmp = orgBmp;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return bmp;
    }

    /**
     * PageCanvas Bitmap
     *
     * @return
     */
    protected Bitmap getPageCanvasBitmap() {
        return getPageCanvasBitmap(1);
    }

    protected Bitmap getPageCanvasBitmap(int sampleRat) {
        View v = canvas.getPageContainer();

        Bitmap orgBmp = null;

        try {
            orgBmp = Bitmap.createBitmap(v.getWidth() / sampleRat, v.getHeight() / sampleRat, DEFAULT_BITMAP_CONFIG);
            // ?????????????????? ????????? ????????? ?????????..
            Canvas cvs = new Canvas(orgBmp);
            cvs.drawColor(Color.WHITE);
            v.draw(cvs);

        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
            sampleRat *= 2;
            if (sampleRat <= MAX_DOWN_SAMPLE_RATIO)
                return getPageCanvasBitmap(sampleRat * 2);
            else
                return null;
        }

        return orgBmp;
    }

    /**
     * Half PageCanvas Bitmap
     *
     * @return
     */
    protected Bitmap getHalfPageCanvasBitmap() {
        View v = canvas.getPageContainer();

        Bitmap orgBmp = CropUtil.getInSampledBitmap(v.getWidth(), v.getHeight(), DEFAULT_BITMAP_CONFIG);
        // ?????????????????? ????????? ????????? ?????????..
        Canvas cvs = new Canvas(orgBmp);
        v.draw(cvs);

        Bitmap converted = Bitmap.createBitmap(orgBmp, v.getWidth() / 2, 0, v.getWidth() / 2, v.getHeight());
        if (orgBmp != converted && orgBmp != null && !orgBmp.isRecycled()) {
            orgBmp.recycle();
            orgBmp = null;
        }

        orgBmp = converted;

        return orgBmp;
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
            saveLoadImageTask(page);

            getArguments().remove("pageSave");
            getArguments().remove("pageLoad");

            getArguments().putBoolean("pageSave", false);
            getArguments().putBoolean("pageLoad", true);
        } else {
            handleDecreaseCanvasLoadCompleteCount(page);
        }
    }

    @Override
    public void onImageLoadStart() {
    }

    /**
     * ???????????? ???????????? ????????? ?????? ?????? ???????????? ????????????.
     *
     * @param orgBmp
     */
    protected boolean saveLocalThumbnail(Context context, Bitmap orgBmp) {
        return saveLocalThumbnail(context, orgBmp, "thumb.jpg");
    }

    protected boolean saveLocalThumbnail(Context context, Bitmap orgBmp, String outputFileName) {
//		Bitmap thumb = BitmapUtil.saveLocalThumbnail(context, orgBmp, orgBmp.getUserSelectWidth(), orgBmp.getHeight());
//		Bitmap thumb = orgBmp.copy(Bitmap.Config.ARGB_8888,true);
        Bitmap thumb = CropUtil.getInSampledBitmapCopy(orgBmp, Bitmap.Config.ARGB_8888);

        try {
            // ?????? ???????????? ????????????.
            File file = null;
            try {
                file = Config.getTHUMB_PATH(outputFileName);
                if (file != null && !file.exists()) {
                    file.createNewFile();
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }

            FileOutputStream stream = new FileOutputStream(file);
            if (thumb != null && !thumb.isRecycled()) {
                thumb.compress(Bitmap.CompressFormat.PNG, 95, stream);
                thumb.recycle();
                thumb = null;
            }

            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                Dlog.e(TAG, e);
            }
        } catch (FileNotFoundException e) {
            Dlog.e(TAG, e);
            return false;
        }
        return true;
    }

    public void setViewPager(InterceptTouchableViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public void setLandscapeMode(boolean isLandscapeMode) {
        this.isLandscapeMode = isLandscapeMode;
    }

    public void setIsPreview(boolean isPreview) {
        this.isPreview = isPreview;
    }

    protected ArrayList<SnapsPage> getPageList() {
        SnapsAssert.assertNotNull(getEditActBridge());
        return getEditActBridge().getPageList();
    }

    protected void setPageThumbnailFail(int index) {
        getEditActBridge().setPageThumbnailFail(index);
    }

    protected void setPageThumbnail(final int pageIdx, String filePath) {
        getEditActBridge().setPageThumbnail(pageIdx, filePath);
    }

    protected SnapsTemplate getSnapsTemplate() {
        return getEditActBridge().getTemplate();
    }

    protected ArrayList<MyPhotoSelectImageData> getGalleryList() {
        return getEditActBridge().getGalleryList();
    }

    protected void handleDecreaseCanvasLoadCompleteCount(final int page) {
        getEditActBridge().decreaseCanvasLoadCompleteCount();
        if (getEditActBridge().getCanvasLoadCompleteCount() <= 0) {
            if (pageLoad) {
                getEditActBridge().pageProgressUnload();
            }
        }
    }

    protected void handleIncreaseCanvasLoadCompleteCount() {
        getEditActBridge().increaseCanvasLoadCompleteCount();
    }

    protected void setPageFileOutput(final int index) {
        getEditActBridge().setPageFileOutput(index);
    }

    protected SnapsEditActExternalConnectionBridge getEditActBridge() {
        SnapsAssert.assertNotNull(getActivity());
        return (SnapsEditActExternalConnectionBridge) getActivity();
    }

    public void setItemClickListener(SnapsCommonResultListener<SnapsPageEditRequestInfo> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
