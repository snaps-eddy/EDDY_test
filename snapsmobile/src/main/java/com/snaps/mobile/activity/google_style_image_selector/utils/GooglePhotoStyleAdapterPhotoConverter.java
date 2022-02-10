package com.snaps.mobile.activity.google_style_image_selector.utils;

import android.app.Activity;
import android.util.SparseArray;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.diary.customview.SnapsSuperRecyclerView;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectPhonePhotoAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.phone_strategies.GooglePhotoStyleAdapterStrategyBase;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 1. 9..
 */

public class GooglePhotoStyleAdapterPhotoConverter extends Thread {
    private static final String TAG = GooglePhotoStyleAdapterPhotoConverter.class.getSimpleName();
    public interface IPhotoConverterListener {
        void onFinishedCurrentUIDepthPhotoConvert(); //현재 UI 데이터 컨버팅 완료
        void onFinishedAllPhotoConvert(); //모무 완료
    }

    /**
     * UI Depth 별로 UI가 다르기 때문에 각 UI 별로 데이터 형태를 Converting 해 주고, Adapter 갱신까지 처리하는 클래스.
     */
    private SparseArray<ImageSelectPhonePhotoAdapter> adapters = null;
    private ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH currentDepth = null;
    private SparseArray<GooglePhotoStyleAdapterStrategyBase> adapterStragteis = null;
    private SparseArray<GooglePhotoStyleAdapterStrategyBase.AdapterAttribute> adapterAttributeSparseArray = null;
    private ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> photoList = null;
    private IPhotoConverterListener photoConverterLineter = null;
    private boolean isConverting = false;
    private boolean isSuspend = false;
    private Activity activity = null;

    public GooglePhotoStyleAdapterPhotoConverter(Activity activity, Builder builder) {
        this.activity = activity;
        this.currentDepth = builder.currentDepth;
        this.adapters = builder.adapters;
        this.adapterStragteis = builder.adapterStragteis;
        this.photoList = builder.photoList;
        this.adapterAttributeSparseArray = builder.adapterAttributeSparseArray;
    }

    public void setPhotoConverterLineter(IPhotoConverterListener lineter) {
        this.photoConverterLineter = lineter;
    }

    public void setSuspend(boolean suspend) {
        isSuspend = suspend;
        isConverting = false;
    }

    public boolean isConverting() {
        return isConverting;
    }

    @Override
    public void run() {
        super.run();

        if (isSuspend || isInterrupted()) return;

        try {
           //현재 뎁스부터..
            processUIByDepth(currentDepth);

            ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH[] arDepth = ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH.values();
            for (final ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH depth : arDepth) {
                if (isSuspend || isInterrupted()) break;

                if (depth == currentDepth) continue;

                processUIByDepth(depth);
            }

            isConverting = false;

            if (activity != null && !activity.isFinishing()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (photoConverterLineter != null)
                            photoConverterLineter.onFinishedAllPhotoConvert();
                    }
                });
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
            isConverting = false;
        }
    }

//    private void verificationImageDimensionInfo() throws Exception {
//        if (photoList == null || photoList.isEmpty()) return;
//        for (GalleryCursorRecord.PhonePhotoFragmentItem photoFragmentItem : photoList) {
//            if (photoFragmentItem == null) continue;
//
//            int[] bitmapSize = CropUtil.getBitmapFilesLength(photoFragmentItem.getPhotoOrgPath());
//            if (bitmapSize != null && bitmapSize.length > 1) {
//                int bitmapWidth = bitmapSize[0];
//                int bitmapHeight = bitmapSize[1];
//
//                int outWidth = photoFragmentItem.getImgOutWidth();
//                int outHeight = photoFragmentItem.getImgOutHeight();
//
//                if (bitmapHeight == 0 || outHeight == 0) continue;
//
//                float bitmapRatio = bitmapWidth / (float) bitmapHeight;
//                float outRatio = outWidth / (float) outHeight;
//
//                if (bitmapRatio != outRatio) {
////                    Logg.y("##### diff ratio :" + photoFragmentItem.getPhotoOrgPath());
//                    photoFragmentItem.setImageDimension(bitmapWidth, bitmapHeight);
//
////                    Crashlytics.logException(new SnapsPhotoLoadException("user photo ratio error : bitmapRatio => " + bitmapRatio + ", outRatio => " + outRatio));
//                }
//            }
//        }
//    }

    private void processUIByDepth(final ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH depth) {
        if (depth == null || adapters == null | adapterStragteis == null || adapterAttributeSparseArray == null) return;

        GooglePhotoStyleAdapterStrategyBase adapterStrategy = adapterStragteis.get(depth.ordinal());
        if (adapterStrategy == null) return;

        adapterStrategy.setAttribute(adapterAttributeSparseArray.get(depth.ordinal()));

        final ImageSelectPhonePhotoAdapter currentPhotoAdapter = adapters.get(depth.ordinal());
        if (currentPhotoAdapter == null) return;

        currentPhotoAdapter.setGooglePhotoStyleStrategy(adapterStrategy);
        currentPhotoAdapter.convertPhotoList(photoList);

        if (activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (photoConverterLineter != null)
                        photoConverterLineter.onFinishedCurrentUIDepthPhotoConvert();
                }
            });
        }
    }

    public static class Builder {
        private Activity activity = null;
        private ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH currentDepth = null;
        private SparseArray<ImageSelectPhonePhotoAdapter> adapters = null;
        private SparseArray<SnapsSuperRecyclerView> recyclerViewSparseArray = null;
        private SparseArray<GooglePhotoStyleAdapterStrategyBase> adapterStragteis = null;
        private SparseArray<GooglePhotoStyleAdapterStrategyBase.AdapterAttribute> adapterAttributeSparseArray = null;
        private ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> photoList = null;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setCurrentDepth(ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH currentDepth) {
            this.currentDepth = currentDepth;
            return this;
        }

        public Builder setAdapters(SparseArray<ImageSelectPhonePhotoAdapter> adapters) {
            this.adapters = adapters;
            return this;
        }

        public Builder setRecyclerViewSparseArray(SparseArray<SnapsSuperRecyclerView> recyclerViewSparseArray) {
            this.recyclerViewSparseArray = recyclerViewSparseArray;
            return this;
        }

        public Builder setAdapterStrategies(SparseArray<GooglePhotoStyleAdapterStrategyBase> adapterStragteis) {
            this.adapterStragteis = adapterStragteis;
            return this;
        }

        public Builder setAdapterAttributeSparseArray(SparseArray<GooglePhotoStyleAdapterStrategyBase.AdapterAttribute> adapterAttributeSparseArray) {
            this.adapterAttributeSparseArray = adapterAttributeSparseArray;
            return this;
        }

        public Builder setPhotoList(ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> photoList) {
            this.photoList = photoList;
            return this;
        }

        public GooglePhotoStyleAdapterPhotoConverter create() {
            return new GooglePhotoStyleAdapterPhotoConverter(activity, this);
        }
    }
}
