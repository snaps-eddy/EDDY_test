package com.snaps.mobile.activity.themebook;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.imageloader.SnapsImageDownloader;
import com.snaps.common.utils.imageloader.filters.ImageEffectBitmap;
import com.snaps.common.utils.imageloader.filters.ImageEffectBitmap.EffectType;
import com.snaps.common.utils.imageloader.filters.ImageFilters;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.thread.ATask.OnTask;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryCommonUtils;
import com.snaps.mobile.component.image_edit_componet.SnapsImageCropView;
import com.snaps.mobile.utils.ui.SnapsBitmapUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

public class SnapsImageEffector {
    private static final String TAG = SnapsImageEffector.class.getSimpleName();

    public interface IEffectApplyListener {
        void onReady();
    }

    public interface IBitmapProcessListener {
        void onBaseBitmapCreated();

        void onBaseBitmapCreated(Bitmap bmp);
    }

    public interface IEffectStatusListener {
        void onChangedStatus(boolean isLoading);
    }

    public static final byte LOAD_TYPE_PREVIEW = 0;
    public static final byte LOAD_TYPE_ROTATE = 1;
    public static final byte LOAD_TYPE_CHANGED_ORIENTATION = 2;

    private final EffectType[] EFFECT_LIST = {EffectType.GRAY_SCALE, EffectType.SEPHIA, EffectType.SHARPEN, EffectType.VINTAGE, EffectType.WARM, EffectType.DAWN, EffectType.AMERALD,
            EffectType.BLACK_CAT, EffectType.FILM,
            EffectType.SNOW, EffectType.OLD_LIGHT, EffectType.AURORA, EffectType.MEMORY, EffectType.WINTER, EffectType.SHADY};

    private IBitmapProcessListener mBaseBitmapCreateListener = null;
    private SnapsImageCropView m_ivPreviewImage = null;
    private Map<EffectType, EffectFilterThumbs> m_mapThumbs = null;
    private Bitmap m_bmBaseResource = null;
    private Bitmap m_bmBaseMiniResource = null;

    private MyPhotoSelectImageData mSelectedImage = null;
    private Map<EffectType, ImageEffectBitmap> m_mapEffectBitmap = null;

    private EffectType mCurretEffectType = EffectType.ORIGIN;

    private Context mContext = null;
    private boolean m_isSuspended = false;
    private boolean m_isCanceled = false;
    private boolean m_isRequestCropFile = false;

    private Map<Integer, EffectApplyTask> mCommitTaskMap = new LinkedHashMap<Integer, EffectApplyTask>();

    private ArrayList<MyPhotoSelectImageData> mCopiedPhotoData = null;

    private int m_iCurIdx = 0;
    private int m_iEachThumbnailSize = 0;
    private int m_iPageIdx = 0;

    private String m_szFileNameSeparator = "";

    private ProgressBar mProgress = null;
    private boolean m_isChangingEffect = false;
    public boolean m_isLoading = false;

    private BaseBitmapSetter mBaseBitmapSetter = null;
    private MainPrevUISetter mMainPrevUISetter = null;

    private IEffectStatusListener mEffectStatusListener = null;

    private Thread mProcessEffectBitmapTask;
    private Thread mFinishCommitTask;
    private Thread mDirectEffect;
    private Thread mCreatePreview;

    private Object effectApplyTaskSyncLocker = new Object();
    private AtomicBoolean isActiveEffectApplyTask = new AtomicBoolean(false);

    private boolean m_isCreatedMainEffectFilter;

    public interface IApplyEffectResultListener {
        public void onResult();
    }

    public class PrecaseBitmap {
        Bitmap bm;
    }

    public void setThumbnailSize(int size) {
        m_iEachThumbnailSize = size;
    }

    public void setBaseBitmapCreateListener(IBitmapProcessListener lis) {
        if (lis == null) return;
        mBaseBitmapCreateListener = lis;
    }

    public SnapsImageEffector(Context context, SnapsImageCropView mainImgView, ProgressBar progress, Map<EffectType, EffectFilterThumbs> thumbs) {

        mContext = context;
        m_ivPreviewImage = mainImgView;
        m_mapThumbs = thumbs;
        m_szFileNameSeparator = String.valueOf(System.currentTimeMillis());
        mProgress = progress;
    }

    public void setResources(SnapsImageCropView mainImgView, ProgressBar progress, Map<EffectType, EffectFilterThumbs> thumbs) {
        m_ivPreviewImage = mainImgView;
        m_mapThumbs = thumbs;
        mProgress = progress;
    }

    public void changeImage(ArrayList<MyPhotoSelectImageData> list, int idx, byte loadType) {
        waitFinishAllThread(list, idx, loadType);
    }

    public void setStatusListener(IEffectStatusListener lis) {
        mEffectStatusListener = lis;
    }

    public void loadImage(MyPhotoSelectImageData imgData, int idx, byte loadType) {
        m_isLoading = true;
        if (mEffectStatusListener != null)
            mEffectStatusListener.onChangedStatus(true);

        if (m_mapEffectBitmap == null)
            m_mapEffectBitmap = new LinkedHashMap<EffectType, ImageEffectBitmap>();
        else if (loadType == LOAD_TYPE_PREVIEW) {
            clearPrevEffects();
            m_mapEffectBitmap = new LinkedHashMap<EffectType, ImageEffectBitmap>();
        }

        m_iCurIdx = idx;
        mSelectedImage = imgData;

        if (mSelectedImage.RESTORE_ANGLE == SnapsImageDownloader.INVALID_ANGLE) {
            if (mSelectedImage.ROTATE_ANGLE == 0 || mSelectedImage.ROTATE_ANGLE == -1)
                mSelectedImage.RESTORE_ANGLE = 0;
            else
                mSelectedImage.RESTORE_ANGLE = -mSelectedImage.ROTATE_ANGLE;
        }

        if (mSelectedImage != null) {
            // 이전, 다음 버튼 눌렀을 때, 효과를 저장 중일 수 있기 때문에 기다려 줌.
            if (mCommitTaskMap != null) {
                EffectApplyTask task = mCommitTaskMap.get(m_iCurIdx);
                if (task != null) {
                    waitSelectedImageProcess(loadType);
                } else {
                    load(loadType);
                }
            }
        }
    }

    public void loadImage(ArrayList<MyPhotoSelectImageData> list, int idx, byte loadType) {

        // MyPhotoSelectImageData 를 복사해서 사용.
        initPhotoDataList(list);

        mSelectedImage = list.get(idx);

        loadImage(mSelectedImage, idx, loadType);
    }

    public void setRequestCropFile(boolean flag) {
        m_isRequestCropFile = flag;
    }

    private void load(byte loadType) {
        mCurretEffectType = ImageEffectBitmap.convertEffectStrToEnumType(mSelectedImage.EFFECT_TYPE);

        // 회전을 눌렀다면, 단순히 이미지만 회전하고 다음 버튼이나, 이전 버튼을 눌렀으면 새로 로드한다.
        executeLoadMainImageTask(loadType);
    }

    public void requestMakeEffectBitmaps(final IEffectApplyListener callback) {
        if (callback == null) return;

        if (isAlreadyMakedEffetBitmap()) {
            callback.onReady();
            return;
        }

        ATask.executeVoidWithThreadPool(new OnTask() {
            @Override
            public void onPre() {
                showProgress(true);
            }

            @Override
            public void onBG() {
                try {
                    makeEffectBitmaps(true);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            @Override
            public void onPost() {
                showProgress(false);
                callback.onReady();
            }
        });
    }

    private void initPhotoDataList(ArrayList<MyPhotoSelectImageData> list) {
        if (list == null || list.isEmpty()) return;

        if (mCopiedPhotoData == null)
            mCopiedPhotoData = new ArrayList<MyPhotoSelectImageData>();
        else if (mCopiedPhotoData.isEmpty())
            mCopiedPhotoData.clear();

        for (MyPhotoSelectImageData orginData : list) {
            MyPhotoSelectImageData copyData = new MyPhotoSelectImageData();
            copyData.set(orginData);
            mCopiedPhotoData.add(copyData);
        }
    }

    private void waitSelectedImageProcess(final byte loadType) {

        ATask.executeVoidWithThreadPool(new OnTask() {

            @Override
            public void onPre() {
                showProgress(true);
            }

            @Override
            public void onPost() {
                showProgress(false);
                load(loadType);
            }

            @Override
            public void onBG() {
                try {
                    if (mCommitTaskMap != null) {
                        EffectApplyTask task = mCommitTaskMap.get(m_iCurIdx);
                        int count = 0;
                        while (task != null && !task.isFinished()) {
                            Thread.sleep(1000);
                            if (count++ > 10)
                                break;
                        }
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });
    }

    private void suspendAllTasks() {
        m_isSuspended = true;

        setCancelTask(true);
    }

    private boolean isCanceledTask() {
        return m_isCanceled;
    }

    private void setCancelTask(boolean cancel) {
        m_isCanceled = cancel;
        if (mProcessEffectBitmapTask != null) {
            mProcessEffectBitmapTask.interrupt();
        }
        if (mFinishCommitTask != null) {
            mFinishCommitTask.interrupt();
        }
        if (mDirectEffect != null) {
            mDirectEffect.interrupt();
        }
        if (mCreatePreview != null) {
            mCreatePreview.interrupt();
        }
        if (mBaseBitmapSetter != null) {
            mBaseBitmapSetter.interrupt();
        }
        if (mMainPrevUISetter != null) {
            mMainPrevUISetter.interrupt();
        }
    }

    public void waitFinishAllThread(final MyPhotoSelectImageData imgData, final int idx, final byte loadType) {
        ATask.executeVoidWithThreadPool(new OnTask() {

            @Override
            public void onPre() {
                showProgress(true);
                setCancelTask(true);
                m_isLoading = true;
                if (mEffectStatusListener != null)
                    mEffectStatusListener.onChangedStatus(true);
            }

            @Override
            public void onPost() {
                showProgress(false);
                setCancelTask(false);
                loadImage(imgData, idx, loadType);
            }

            @Override
            public void onBG() {
                try {
                    joinThreads();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });
    }

    private void joinThreads() throws InterruptedException {

        if (mProcessEffectBitmapTask != null) {
            mProcessEffectBitmapTask.join();
        }

        if (mFinishCommitTask != null) {
            mFinishCommitTask.join();
        }

        if (mDirectEffect != null) {
            mDirectEffect.join();
        }

        if (mCreatePreview != null) {
            mCreatePreview.join();
        }

        if (mBaseBitmapSetter != null) {
            mBaseBitmapSetter.join();
        }

        if (mMainPrevUISetter != null) {
            mMainPrevUISetter.join();
        }
    }

    public void waitFinishAllThread(final ArrayList<MyPhotoSelectImageData> list, final int idx, final byte loadType) {

        ATask.executeVoidWithThreadPool(new OnTask() {

            @Override
            public void onPre() {
                showProgress(true);
                setCancelTask(true);
                m_isLoading = true;
                if (mEffectStatusListener != null)
                    mEffectStatusListener.onChangedStatus(true);
            }

            @Override
            public void onPost() {
                showProgress(false);
                setCancelTask(false);
                loadImage(list, idx, loadType);
            }

            @Override
            public void onBG() {
                try {
                    joinThreads();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });
    }

    private boolean isSuspendedTask() {
        return m_isSuspended;
    }

    private void executeProcessEffectBitmap(final byte LOAD_TYPE) {

        // //하단 필터 뷰를 백그라운드에서 생성하므로, 프로그레스를 보여준다.
        if (LOAD_TYPE == LOAD_TYPE_PREVIEW)
            showFilterViewProgressBar();

        mProcessEffectBitmapTask = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (LOAD_TYPE) {
                        case LOAD_TYPE_PREVIEW:
                            makeEffectBitmaps(false);
                            break;
                        case LOAD_TYPE_CHANGED_ORIENTATION:
                            refreshBitmaps();
                            break;
                        case LOAD_TYPE_ROTATE:
                            rotateEffectBitmaps();
                            break;
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });

        mProcessEffectBitmapTask.start();

        // 선택된 썸네일 테두리 효과
        showOutlineSelectedEffectThumb();
    }

    private void executeLoadMainImageTask(final byte LOAD_TYPE) {

        mBaseBitmapSetter = new BaseBitmapSetter(new IBitmapProcessListener() {
            @Override
            public void onBaseBitmapCreated() {
                Activity act = (Activity) mContext;

                showProgress(false);

                if (act != null) {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (isSuspendedTask())
                                return;

                            // 생성된 베이스 비트맵을 이용하여, 미리 보기 메인 화면 셋팅
                            setMainPreviewImage(LOAD_TYPE);

                            // 속도 이슈 때문에 백그라운드에서 효과 이미지나 다음 이미지들을 생성 해 놓는다.
                            executeLoadSubImageTask(LOAD_TYPE);
                        }
                    });
                }
            }

            @Override
            public void onBaseBitmapCreated(Bitmap bmp) {
            }
        }, LOAD_TYPE);

        mBaseBitmapSetter.start();
    }

    private void executeLoadSubImageTask(final byte LOAD_TYPE) {
        // 백그라운드에서 효과 필터들을 생성
        executeProcessEffectBitmap(LOAD_TYPE);
    }

    private void setBaseBitmap(byte LOAD_TYPE) {
        if (LOAD_TYPE == LOAD_TYPE_CHANGED_ORIENTATION) return; //이미 만들어져 있다.
        try {
            Bitmap bmp = getBaseBitmapPrevView(mSelectedImage, LOAD_TYPE);

            if (!isSuspendedTask() && bmp != null && !bmp.isRecycled()) {
                m_bmBaseResource = bmp;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void setMainPreviewImage(final byte loadType) {

        if (isSuspendedTask() || m_ivPreviewImage == null || m_bmBaseResource == null || m_bmBaseResource.isRecycled() || m_mapThumbs == null)
            return;

        m_isCreatedMainEffectFilter = false;
        mMainPrevUISetter = new MainPrevUISetter(new IBitmapProcessListener() {
            @Override
            public void onBaseBitmapCreated(final Bitmap bmPriview) {
                if (isSuspendedTask() || isCanceledTask()) {
                    m_isLoading = false;
                    showProgress(false);
                    return;
                }

                Activity act = (Activity) mContext;

                if (act != null) {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (isSuspendedTask())
                                return;

                            m_isCreatedMainEffectFilter = true;

                            if (bmPriview != null && !bmPriview.isRecycled()) {
                                m_ivPreviewImage.setImageBitmap(bmPriview);

                                if (loadType == LOAD_TYPE_ROTATE)
                                    m_ivPreviewImage.setRotateMode(true);
                                else
                                    m_ivPreviewImage.setRotateMode(false);

                                // 단말기 화면 크기에 Fit시킨다.
                                m_ivPreviewImage.fitToScreen(bmPriview);

                                // 인화 영역을 설정한다.
                                m_ivPreviewImage.calculatorClipRect();
                            }

                            if (mCurretEffectType != EffectType.ORIGIN) {
                                if (m_mapEffectBitmap != null) {
                                    ImageEffectBitmap effect = m_mapEffectBitmap.get(mCurretEffectType);
                                    if (effect != null) {
                                        if (effect.bitmapThumb != null && !effect.bitmapThumb.isRecycled())
                                            setThumbnailImage(mCurretEffectType, effect.bitmapThumb);
                                        else {
                                            if (m_bmBaseMiniResource != null && !m_bmBaseMiniResource.isRecycled()) {
                                                Bitmap bmp = getEffectAppliedBitmap(mCurretEffectType, m_bmBaseMiniResource);
                                                effect.bitmapThumb = bmp;
                                                setThumbnailImage(mCurretEffectType, bmp);
                                            }
                                        }
                                    }
                                }
                            }

                            m_isLoading = false;

                            // FIT 시키는 시간이 필요하기 때문에 약간의 딜레이를 준다.
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mEffectStatusListener != null) {
                                        mEffectStatusListener.onChangedStatus(false);
                                    }
                                }
                            }, 500);
                        }
                    });
                }

                showProgress(false);
            }

            @Override
            public void onBaseBitmapCreated() {
            }
        }, loadType);

        mMainPrevUISetter.start();
    }

    private String getIfIsExistCacheFileUri(String imgUri) {
        try {
            File cacheFile = Glide.getPhotoCacheDir(ContextUtil.getContext(), imgUri);
            if (cacheFile != null && cacheFile.exists() && cacheFile.length() > 0) {
                return cacheFile.getAbsolutePath();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return imgUri;
    }

    private Bitmap getBaseBitmap(MyPhotoSelectImageData data, int width, int height, byte loadType) {
        Bitmap bmp = null;
        if (data != null) {

            String imgUri = getImagePath(data);

            if (imgUri != null) {

                switch (loadType) {
                    case LOAD_TYPE_ROTATE:
                        if (m_bmBaseResource != null && !m_bmBaseResource.isRecycled()) {
                            bmp = CropUtil.getRotateImage(m_bmBaseResource, 90);
                            mSelectedImage.RESTORE_ANGLE -= 90;
                            if (mSelectedImage.RESTORE_ANGLE <= -360) {
                                mSelectedImage.RESTORE_ANGLE += 360;
                            }
                        }
                        break;
                    case LOAD_TYPE_CHANGED_ORIENTATION:
                        break;
                    case LOAD_TYPE_PREVIEW:
                    default:
                        Bitmap baseBitmap = ImageLoader.syncLoadBitmap(imgUri, width, height, 0);

                        if (baseBitmap != null && !baseBitmap.isRecycled()) {
                            if (data.ROTATE_ANGLE > 0) //0이 아니면 굳이 돌릴 필요가 있나..
                                bmp = CropUtil.getRotateImage(baseBitmap, data.ROTATE_ANGLE);
                            else
                                bmp = baseBitmap;

                            if (m_bmBaseMiniResource != null && !m_bmBaseMiniResource.isRecycled()) {
                                m_bmBaseMiniResource.recycle();
                                m_bmBaseMiniResource = null;
                            }

                            int iMinSize = Math.min(baseBitmap.getWidth(), baseBitmap.getHeight());

                            float fixRatio = (float) m_iEachThumbnailSize / (float) iMinSize;
                            int fixWidth = (int) (baseBitmap.getWidth() * fixRatio);
                            int fixHeight = (int) (baseBitmap.getHeight() * fixRatio);

                            m_bmBaseMiniResource = Bitmap.createScaledBitmap(baseBitmap, (int) fixWidth, (int) fixHeight, true);
                            if (data.ORIGINAL_ROTATE_ANGLE > 0) //0이 아니면 굳이 돌릴 필요가 있나..
                                m_bmBaseMiniResource = CropUtil.getRotateImage(m_bmBaseMiniResource, data.ORIGINAL_ROTATE_ANGLE);
                        }
                        break;
                }
            }
        }

        return bmp;
    }

    private String getImagePath(MyPhotoSelectImageData data) {
        String result = "";
        boolean isExistLocalFile = false;
        try {
            File localFile = new File(data.PATH);
            if (localFile.exists()) {
                isExistLocalFile = true;

                //플립된 이미지는 Glide와의 동기화를 위해 웹에 업로드 된 이미지를 사용한다 FIXME 가능하다면 bitmap 다운 받는 방식을 Glide를 통해 받는 방식으로 바꾸고 아래 코드는 삭제하자
//                boolean isFlippedImage = CropUtil.isFlippedOrientationImage(data.PATH);
//                if (data.KIND == Const_VALUES.SELECT_UPLOAD && isFlippedImage) {
//                    isExistLocalFile = false;
//                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        if (!isExistLocalFile && data.KIND == Const_VALUES.SELECT_UPLOAD) {
            String thumbnailPath = data.getSafetyThumbnailPath();
            if (!StringUtil.isEmpty(thumbnailPath)) {
                if (!thumbnailPath.startsWith("http"))
                    result = SnapsAPI.DOMAIN(false) + data.getSafetyThumbnailPath();
                else
                    result = data.getSafetyThumbnailPath();
            } else {
                result = SnapsAPI.DOMAIN(false) + data.ORIGINAL_PATH; // FIXME 썸네일로 바꿀 수도 있음.
            }
        } else {
            if (new File(data.PATH).exists()) {
                result = data.PATH;

            } else {
                if (data.KIND == Const_VALUES.SELECT_KAKAO || data.KIND == Const_VALUES.SELECT_FACEBOOK || data.KIND == Const_VALUES.SELECT_SDK_CUSTOMER || data.KIND == Const_VALUES.SELECT_INSTAGRAM)
                    result = data.PATH;
                else {
                    if (data.ORIGINAL_PATH != null && data.ORIGINAL_PATH.length() > 0) {
                        result = SnapsAPI.DOMAIN(false) + data.ORIGINAL_PATH;

                    } else {
                        result = data.PATH;
                    }

                }
            }
        }

        result = ImageUtil.getGooglePhotoUrl(result, data);
        return result;
    }

    private Bitmap getBaseBitmapPrevView(MyPhotoSelectImageData data, byte loadType) {
//        int iWidth = Math.min(ImageFilters.PREVIEW_SAMPLE_SIZE, UIUtil.getScreenWidth(mContext));
//
//        int iHeight = Math.min(ImageFilters.PREVIEW_SAMPLE_SIZE, UIUtil.getScreenHeight(mContext));
        int iWidth = Math.min(ImageFilters.getImageEditPreviewBitmapSize(mContext), UIUtil.getScreenWidth(mContext));

        int iHeight = Math.min(ImageFilters.getImageEditPreviewBitmapSize(mContext), UIUtil.getScreenHeight(mContext));

        return getBaseBitmap(data, iWidth, iHeight, loadType);
    }

    private boolean isActiveEffectApplyTask() {
        return isActiveEffectApplyTask.get();
    }

    private Object getEffectApplyTaskSyncLocker() {
        return effectApplyTaskSyncLocker;
    }

    private void notifyEffectApplyTaskSyncLocker() {
        if (isActiveEffectApplyTask()) {
            isActiveEffectApplyTask.set(false);
            synchronized (getEffectApplyTaskSyncLocker()) {
                effectApplyTaskSyncLocker.notify();
            }
        }
    }

    private void lockEffectApplyTask() {
        if (!isActiveEffectApplyTask()) {
            isActiveEffectApplyTask.set(true);
        }
    }

    public void commitEffect(IEffectApplyListener lis) {
        if (m_mapEffectBitmap == null)
            return;

        if (isActiveEffectApplyTask()) {
            synchronized (getEffectApplyTaskSyncLocker()) {
                if (isActiveEffectApplyTask()) {
                    try {
                        getEffectApplyTaskSyncLocker().wait();
                    } catch (InterruptedException e) {
                        Dlog.e(TAG, e);
                    }
                }
            }
        }

        EffectApplyTask effectApplyTask = new EffectApplyTask(m_iCurIdx, lis);
        mCommitTaskMap.put(m_iCurIdx, effectApplyTask);

        effectApplyTask.start();
    }

    public void waitFinishCommitTask(final IApplyEffectResultListener lis) {

        mFinishCommitTask = new Thread(new Runnable() {
            @Override
            public void run() {

                if (isSuspendedTask())
                    return;

                showProgress(true);

                while (!isFinishCommitTasks()) {
                    if (isSuspendedTask())
                        break;
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Dlog.e(TAG, e);
                    }
                }

                if (mContext == null)
                    return;

                Activity act = (Activity) mContext;

                if (act != null) {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (isSuspendedTask())
                                return;

                            showProgress(false);

                            if (lis != null)
                                lis.onResult();
                        }
                    });
                }
            }
        });

        mFinishCommitTask.start();
    }

    private boolean isFinishCommitTasks() {
        if (mCommitTaskMap == null || mCommitTaskMap.isEmpty())
            return true;

        Iterator<Entry<Integer, EffectApplyTask>> itorator = mCommitTaskMap.entrySet().iterator();

        boolean result = true;

        while (itorator.hasNext()) {
            if (isSuspendedTask())
                break;

            Entry<Integer, EffectApplyTask> entry = itorator.next();
            if (entry != null) {
                EffectApplyTask task = entry.getValue();
                if (task != null && !task.isFinished()) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    public void buttonClicked(View v) {
        int id = v.getId();

        if (m_isChangingEffect || mDirectEffect != null && mDirectEffect.getState() == Thread.State.RUNNABLE) {
            showProgress(true);
            return;
        }

        showProgress(false);

        // 원본 이미지로 되돌리기
        if (id == R.id.activity_effectimage_tmb_origin_iv) {
            if (!m_isChangingEffect && m_bmBaseResource != null && !m_bmBaseResource.isRecycled() && m_ivPreviewImage != null)
                m_ivPreviewImage.setImageBitmap(m_bmBaseResource);
            mCurretEffectType = EffectType.ORIGIN;
            setFilterViewClipRect();
            return;
        }

        EffectType effectType = getSelecteEffectType(id);

        applySelectedEffectFilter(effectType);
    }

    public boolean isChangingEffect() {
        return m_isChangingEffect;
    }

    public EffectType getCurrentEffectType() {
        return mCurretEffectType;
    }

    public void setEffectAppliedImage() {
        if (m_mapEffectBitmap != null && m_ivPreviewImage != null) {
            ImageEffectBitmap effect = m_mapEffectBitmap.get(mCurretEffectType);
            if (effect != null && effect.bitmapPreview != null && !effect.bitmapPreview.isRecycled())
                m_ivPreviewImage.setImageBitmap(effect.bitmapPreview);
        }
    }

    private void recycleOtherPrevviews(EffectType curType) {
        if (m_mapEffectBitmap == null)
            return;

        try {
            ImageEffectBitmap effectBitmap = null;
            for (Entry<EffectType, ImageEffectBitmap> entry : m_mapEffectBitmap.entrySet()) {
                EffectType effectType = entry.getKey();

                if (curType == effectType)
                    continue;

                effectBitmap = m_mapEffectBitmap.get(effectType);
                if (effectBitmap == null)
                    continue;

                if (effectBitmap.bitmapPreview != null && !effectBitmap.bitmapPreview.isRecycled()) {
                    effectBitmap.bitmapPreview.recycle();
                    effectBitmap.bitmapPreview = null;
                    effectBitmap.isCreatedPreview = false;
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void applyDirectEffect(final EffectType effectType) {
        if (effectType == null || m_ivPreviewImage == null || m_mapEffectBitmap == null) {
            m_isChangingEffect = false;
            return;
        }

        mDirectEffect = new Thread(new Runnable() {
            @Override
            public void run() {

                if (isSuspendedTask()) {
                    m_isChangingEffect = false;
                    return;
                }

                try {
                    showProgress(true);

                    final ImageEffectBitmap effect = m_mapEffectBitmap.get(effectType);
                    if (effect != null) {
                        synchronized (effect) {

                            if (!effect.isCreatedPreview) {

                                if (m_bmBaseResource != null && !m_bmBaseResource.isRecycled()) {
                                    // synchronized(m_bmBaseResource) {
                                    if (effect.bitmapPreview != null && !effect.bitmapPreview.isRecycled()) {
                                        effect.bitmapPreview.recycle();
                                        effect.bitmapPreview = null;
                                    }

                                    effect.bitmapPreview = getEffectAppliedBitmap(effectType, m_bmBaseResource);
                                    effect.isCreatedPreview = true;
                                }
                            }

                            Activity act = (Activity) mContext;

                            if (act != null) {
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (isSuspendedTask()) {
                                            m_isChangingEffect = false;
                                            showProgress(false);
                                            return;
                                        }

                                        if (m_ivPreviewImage != null && effect != null && effect.bitmapPreview != null && !effect.bitmapPreview.isRecycled())
                                            m_ivPreviewImage.setImageBitmap(effect.bitmapPreview);

                                        if (mProgress != null)
                                            mProgress.setVisibility(View.GONE);

                                        m_isChangingEffect = false;

                                        //메모리 이슈 때문에 다른 프리뷰들은 모두 날려 버린다.
                                        recycleOtherPrevviews(effectType);
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                } finally {
                    showProgress(false);
                    m_isChangingEffect = false;
                }
            }
        });

        mDirectEffect.start();
    }

    public void showProgress(final boolean visible) {
        Activity act = (Activity) mContext;

        if (act != null) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mProgress != null) {
                        if (visible && !mProgress.isShown())
                            mProgress.setVisibility(View.VISIBLE);
                        else if (!visible && mProgress.isShown()) {
                            mProgress.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }

    private void applySelectedEffectFilter(EffectType effectType) {
        if (m_isChangingEffect || m_ivPreviewImage == null || effectType == null || effectType == mCurretEffectType)
            return;

        ImageEffectBitmap effect = null;

        if (m_mapEffectBitmap != null) {
            effect = m_mapEffectBitmap.get(effectType);
        }

        if (effect != null) {

            m_isChangingEffect = true;

            if (effect.isCreatedPreview) {
                if (effect.bitmapPreview != null && !effect.bitmapPreview.isRecycled()) {
                    m_ivPreviewImage.setImageBitmap(effect.bitmapPreview);
                    m_isChangingEffect = false;
                    if (mProgress != null)
                        mProgress.setVisibility(View.GONE);
                } else {
                    applyDirectEffect(effectType);
                }
            } else {
                applyDirectEffect(effectType);
            }

            mCurretEffectType = effectType;

            // 필터 테두리 효과
            setFilterViewClipRect();

            SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.photobook_annie_editphoto_updateFilter)
                    .appendPayload(WebLogConstants.eWebLogPayloadType.FILTER_CODE, ImageEffectBitmap.getEffectTypeWebLogCode(mCurretEffectType))
                    .appendPayload(WebLogConstants.eWebLogPayloadType.TEMPLATE_CODE, Config.getTMPL_CODE())
                    .appendPayload(WebLogConstants.eWebLogPayloadType.IMG_PATH, (mSelectedImage != null ? mSelectedImage.getImagePathForWebLog() : ""))
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PAGE, String.valueOf(m_iPageIdx))
                    .appendPayload(WebLogConstants.eWebLogPayloadType.PROJ_CODE, Config.getPROJ_CODE()));
        }
    }

    private void setFilterViewClipRect() {

        if (m_mapThumbs == null)
            return;

        Activity act = (Activity) mContext;

        if (act != null) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (isSuspendedTask())
                        return;

                    EffectFilterThumbs thumbs = null;
                    Iterator<Entry<EffectType, EffectFilterThumbs>> itorator = m_mapThumbs.entrySet().iterator();

                    try {
                        if (itorator != null) {
                            while (itorator.hasNext()) {

                                if (isSuspendedTask() || isCanceledTask())
                                    break;

                                Entry<EffectType, EffectFilterThumbs> entry = itorator.next();
                                EffectType effectType = entry.getKey();

                                thumbs = m_mapThumbs.get(effectType);
                                if (thumbs != null && thumbs.getOutline() != null) {

                                    if (mCurretEffectType == effectType) {
                                        thumbs.getOutline().setVisibility(View.VISIBLE);
                                        thumbs.getName().setTextColor(Color.parseColor("#e36a63"));
                                    } else {
                                        thumbs.getOutline().setVisibility(View.GONE);
                                        thumbs.getName().setTextColor(Color.parseColor("#555555"));
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }
            });
        }
    }

    private void showFilterViewProgressBar() {

        if (m_mapThumbs == null)
            return;

        EffectFilterThumbs thumbs = null;
        Iterator<Entry<EffectType, EffectFilterThumbs>> itorator = m_mapThumbs.entrySet().iterator();
        try {
            while (itorator.hasNext()) {
                if (isSuspendedTask() || isCanceledTask())
                    break;

                Entry<EffectType, EffectFilterThumbs> entry = itorator.next();
                EffectType effectType = entry.getKey();

                if (effectType == mCurretEffectType)
                    continue;

                thumbs = m_mapThumbs.get(effectType);
                if (thumbs != null) {
                    thumbs.getOutline().setVisibility(View.GONE);
                    thumbs.getImgView().setImageBitmap(null);
                    thumbs.getProgress().setVisibility(View.VISIBLE);
                    thumbs.getName().setTextColor(Color.parseColor("#555555"));
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private EffectType getSelecteEffectType(int id) {
        EffectType effectType = EffectType.ORIGIN;
        if (id == R.id.activity_effectimage_tmb_sherpen_iv) {
            effectType = EffectType.SHARPEN;
        } else if (id == R.id.activity_effectimage_tmb_gray_scale_iv) {
            effectType = EffectType.GRAY_SCALE;
        } else if (id == R.id.activity_effectimage_tmb_sephia_iv) {
            effectType = EffectType.SEPHIA;
        } else if (id == R.id.activity_effectimage_tmb_warm_iv) {
            effectType = EffectType.WARM;
        } else if (id == R.id.activity_effectimage_tmb_dawn_iv) {
            effectType = EffectType.DAWN;
        } else if (id == R.id.activity_effectimage_tmb_emerald_iv) {
            effectType = EffectType.AMERALD;
        } else if (id == R.id.activity_effectimage_tmb_vintage_iv) {
            effectType = EffectType.VINTAGE;
        } else if (id == R.id.activity_effectimage_tmb_black_cat_iv) {
            effectType = EffectType.BLACK_CAT;
        } else if (id == R.id.activity_effectimage_tmb_film_iv) {
            effectType = EffectType.FILM;
        } else if (id == R.id.activity_effectimage_tmb_snow_iv) {
            effectType = EffectType.SNOW;
        } else if (id == R.id.activity_effectimage_tmb_old_light_iv) {
            effectType = EffectType.OLD_LIGHT;
        } else if (id == R.id.activity_effectimage_tmb_aurora_iv) {
            effectType = EffectType.AURORA;
        } else if (id == R.id.activity_effectimage_tmb_memory_iv) {
            effectType = EffectType.MEMORY;
        } else if (id == R.id.activity_effectimage_tmb_winter_iv) {
            effectType = EffectType.WINTER;
        } else if (id == R.id.activity_effectimage_tmb_shady_iv) {
            effectType = EffectType.SHADY;
        }

        return effectType;
    }

    private void clearPrevEffects() {
        if (m_mapEffectBitmap == null)
            return;

        try {
            ImageEffectBitmap effectBitmap = null;

            for (Entry<EffectType, ImageEffectBitmap> entry : m_mapEffectBitmap.entrySet()) {
                EffectType effectType = entry.getKey();

                effectBitmap = m_mapEffectBitmap.get(effectType);
                if (effectBitmap == null)
                    continue;

                EffectFilterThumbs thumb = m_mapThumbs.get(effectType);
                if (thumb != null && thumb.getImgView() != null)
                    thumb.getImgView().setImageBitmap(null);

                if (m_ivPreviewImage != null) {
                    m_ivPreviewImage.setImageBitmap(null);
                }

                if (effectBitmap.bitmapPreview != null && !effectBitmap.bitmapPreview.isRecycled()) {
                    effectBitmap.bitmapPreview.recycle();
                    effectBitmap.bitmapPreview = null;
                }

                if (effectBitmap.bitmapThumb != null && !effectBitmap.bitmapThumb.isRecycled()) {
                    effectBitmap.bitmapThumb.recycle();
                    effectBitmap.bitmapThumb = null;
                }
            }

            if (m_mapEffectBitmap != null && !m_mapEffectBitmap.isEmpty())
                m_mapEffectBitmap.clear();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void showOutlineSelectedEffectThumb() {
        if (m_mapThumbs == null)
            return;

        EffectFilterThumbs thumb = m_mapThumbs.get(mCurretEffectType);
        if (thumb != null && thumb.getOutline() != null) {
            thumb.getOutline().setVisibility(View.VISIBLE);
            thumb.getName().setTextColor(Color.parseColor("#e36a63"));
        }
    }

    // 만들다 중단된 비트맵은 다시 생산을 재게한다.
    private void checkSuspendedMakeBitmap() throws Exception {
        if (isSuspendedTask() || isCanceledTask() || m_bmBaseResource == null || m_bmBaseResource.isRecycled() || EFFECT_LIST == null)
            return;

        ImageEffectBitmap effect = null;

        // 썸네일 용..
        for (EffectType type : EFFECT_LIST) {

            if (isSuspendedTask() || isCanceledTask())
                break;

            ImageEffectBitmap effectBitmap = m_mapEffectBitmap.get(type);
            if (effectBitmap != null)
                continue;

            if (mCurretEffectType == type) {
                Activity act = (Activity) mContext;

                if (act != null) {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (isSuspendedTask())
                                return;

                            EffectFilterThumbs t = m_mapThumbs.get(mCurretEffectType);
                            if (t != null) {
                                t.getProgress().setVisibility(View.GONE);
                            }
                        }
                    });
                }
                continue;
            }

            effect = new ImageEffectBitmap();
            effect.effectType = type;
            if (m_bmBaseMiniResource != null && !m_bmBaseMiniResource.isRecycled()) {
                effect.bitmapThumb = getEffectAppliedBitmap(type, m_bmBaseMiniResource);
            }

            setThumbnailImage(effect.effectType, effect.bitmapThumb);

            if (type == mCurretEffectType)
                continue;

            if (m_mapEffectBitmap != null)
                m_mapEffectBitmap.put(type, effect);
        }
    }

    private boolean isAlreadyMakedEffetBitmap() {
        if (m_mapEffectBitmap == null) return false;

        for (EffectType type : EFFECT_LIST) {
            ImageEffectBitmap effectBitmap = m_mapEffectBitmap.get(type);
            if (effectBitmap == null || !effectBitmap.isCreatedThumbnail) return false;
        }

        return true;
    }

    private void makeEffectBitmaps(boolean isMakeBitmap) throws Exception {
        if (isSuspendedTask() || isCanceledTask() || m_bmBaseResource == null || m_bmBaseResource.isRecycled() || EFFECT_LIST == null)
            return;

        // 원본 썸네일
        setThumbnailImage(EffectType.ORIGIN, m_bmBaseMiniResource);

        ImageEffectBitmap effect = null;

        // 썸네일 용..
        for (EffectType type : EFFECT_LIST) {

            if (isSuspendedTask() || isCanceledTask())
                break;

            if (type == null)
                continue;

            if (mCurretEffectType == type) { // FIXME 가끔 똥글뱅이가 안 멈추는 경우가 있음..확인 필요.
                Activity act = (Activity) mContext;

                if (act != null) {
                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (isSuspendedTask())
                                return;

                            EffectFilterThumbs t = m_mapThumbs.get(mCurretEffectType);
                            if (t != null) {
                                t.getProgress().setVisibility(View.GONE);
                            }
                        }
                    });
                }
                continue;
            }

            effect = new ImageEffectBitmap();
            effect.effectType = type;

            if (isMakeBitmap) {
                if (m_bmBaseMiniResource != null && !m_bmBaseMiniResource.isRecycled()) {
                    effect.bitmapThumb = getEffectAppliedBitmap(type, m_bmBaseMiniResource);
                    if (effect.bitmapThumb != null && !effect.bitmapThumb.isRecycled()) {
                        effect.isCreatedThumbnail = true;
                    }
                }
            } else {
                effect.bitmapThumb = m_bmBaseMiniResource;
                effect.isCreatedThumbnail = false;
            }

            setThumbnailImage(effect.effectType, effect.bitmapThumb);

            if (m_mapEffectBitmap != null)
                m_mapEffectBitmap.put(type, effect);
        }

        if (mCurretEffectType != EffectType.ORIGIN) {
            while (!isSuspendedTask() && !isCanceledTask() && !m_isCreatedMainEffectFilter) {
                Thread.sleep(100);
            }
        }
    }

    private void refreshBitmaps() throws Exception {
        if (isSuspendedTask() || isCanceledTask() || EFFECT_LIST == null)
            return;

        // 원본 썸네일
        setThumbnailImage(EffectType.ORIGIN, m_bmBaseMiniResource);

        if (m_mapEffectBitmap != null) {
            Iterator<Entry<EffectType, ImageEffectBitmap>> iterator = m_mapEffectBitmap.entrySet().iterator();

            while (iterator.hasNext()) {

                if (isSuspendedTask() || isCanceledTask())
                    break;

                Entry<EffectType, ImageEffectBitmap> entry = iterator.next();
                if (entry != null) {
                    ImageEffectBitmap preview = entry.getValue();
                    if (preview != null) {

                        if (preview.bitmapThumb != null && !preview.bitmapThumb.isRecycled()) {
                            setThumbnailImage(preview.effectType, preview.bitmapThumb);
                        } else {
                            if (m_bmBaseMiniResource != null && !m_bmBaseMiniResource.isRecycled()) {
                                preview.bitmapThumb = getEffectAppliedBitmap(preview.effectType, m_bmBaseMiniResource);
                                setThumbnailImage(preview.effectType, preview.bitmapThumb);
                            }
                        }
                    }
                }
            }
        }

        checkSuspendedMakeBitmap();
    }

    private void rotateEffectBitmaps() throws Exception {
        if (isSuspendedTask() || isCanceledTask() || EFFECT_LIST == null)
            return;

        if (m_mapEffectBitmap != null) {
            Iterator<Entry<EffectType, ImageEffectBitmap>> iterator = m_mapEffectBitmap.entrySet().iterator();

            while (iterator.hasNext()) {

                if (isSuspendedTask() || isCanceledTask())
                    break;

                Entry<EffectType, ImageEffectBitmap> entry = iterator.next();
                if (entry != null) {
                    ImageEffectBitmap preview = entry.getValue();
                    if (preview != null) {

                        if (preview.bitmapThumb != null && !preview.bitmapThumb.isRecycled()) {
                            setThumbnailImage(preview.effectType, preview.bitmapThumb);
                        } else {
                            if (m_bmBaseMiniResource != null && !m_bmBaseMiniResource.isRecycled()) {
                                preview.bitmapThumb = getEffectAppliedBitmap(preview.effectType, m_bmBaseMiniResource);
                                setThumbnailImage(preview.effectType, preview.bitmapThumb);
                            }
                        }

                        // 현재 선택된 효과의 프리뷰는 이미 만들어져 있다.
                        if (preview.effectType != mCurretEffectType) {
                            if (preview.isCreatedPreview && preview.bitmapPreview != null && !preview.bitmapPreview.isRecycled())
                                preview.bitmapPreview = CropUtil.getRotateImage(preview.bitmapPreview, 90);
                        }
                    }
                }
            }
        }

        checkSuspendedMakeBitmap();
    }

    private void setThumbnailImage(final EffectType type, final Bitmap bm) {

        if (m_mapThumbs == null || type == null || mContext == null || !(mContext instanceof Activity))
            return;

        Activity act = (Activity) mContext;

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSuspendedTask())
                    return;

                if (m_mapThumbs != null) {
                    EffectFilterThumbs thumbs = m_mapThumbs.get(type);
                    if (thumbs != null && thumbs.getImgView() != null && thumbs.getProgress() != null && bm != null && !bm.isRecycled()) {
                        thumbs.getImgView().setImageBitmap(bm);
                        thumbs.getProgress().setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public Bitmap getEffectAppliedBitmap(EffectType type, Bitmap bmp) {
        Context context = mContext != null ? mContext : ContextUtil.getContext();
        return ImageFilters.getEffectAppliedBitmap(context, type, bmp);
    }

    public void releaseBitmaps() throws Exception {

        suspendAllTasks();

        clearPrevEffects();

        if (m_ivPreviewImage != null) {
            m_ivPreviewImage.setImageBitmap(null);
            m_ivPreviewImage = null;
        }

        if (m_bmBaseResource != null && !m_bmBaseResource.isRecycled()) {
            m_bmBaseResource.recycle();
            m_bmBaseResource = null;
        }

        if (m_bmBaseMiniResource != null && !m_bmBaseMiniResource.isRecycled()) {
            m_bmBaseMiniResource.recycle();
            m_bmBaseMiniResource = null;
        }

        if (mCopiedPhotoData != null && !mCopiedPhotoData.isEmpty()) {
            mCopiedPhotoData.clear();
            mCopiedPhotoData = null;
        }

        if (mCommitTaskMap != null && !mCommitTaskMap.isEmpty()) {
            Iterator<Entry<Integer, EffectApplyTask>> iterator = mCommitTaskMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<Integer, EffectApplyTask> entry = iterator.next();
                if (entry != null) {
                    EffectApplyTask task = entry.getValue();
                    if (task != null && task.bmBaseResource != null && !task.bmBaseResource.isRecycled()) {
                        task.bmBaseResource.recycle();
                        task.bmBaseResource = null;
                    }
                }
            }
        }

        if (m_mapEffectBitmap != null && !m_mapEffectBitmap.isEmpty()) {
            for (EffectType key : m_mapEffectBitmap.keySet()) {
                ImageEffectBitmap effectBitmap = m_mapEffectBitmap.get(key);
                if (effectBitmap != null) {
                    if (effectBitmap.bitmapThumb != null && !effectBitmap.bitmapThumb.isRecycled()) {
                        effectBitmap.bitmapThumb.recycle();
                        effectBitmap.bitmapThumb = null;
                    }

                    if (effectBitmap.bitmapPreview != null && !effectBitmap.bitmapPreview.isRecycled()) {
                        effectBitmap.bitmapPreview.recycle();
                        effectBitmap.bitmapPreview = null;
                    }
                }
            }
        }
    }

    public class BaseBitmapSetter extends Thread {
        IBitmapProcessListener listener;
        byte LOAD_TYPE;

        private boolean isSuspend = false;

        public boolean isSuspend() {
            return isSuspend;
        }

        public void suspendTask() {
            this.isSuspend = true;
        }

        BaseBitmapSetter(IBitmapProcessListener lis, byte type) {
            listener = lis;
            LOAD_TYPE = type;
            setDaemon(true);
        }

        @Override
        public void run() {
            super.run();

            if (isSuspendedTask())
                return;

            showProgress(true);

            // 효과 샘플들을 만들어 낼 베이스 비트맵을 생성한다.
            setBaseBitmap(LOAD_TYPE);

            if (listener != null && !isSuspend())
                listener.onBaseBitmapCreated();
        }
    }

    public class MainPrevUISetter extends Thread {
        IBitmapProcessListener listener;
        Bitmap bmPriview = null;
        byte loadType;

        private boolean isSuspend = false;

        public boolean isSuspend() {
            return isSuspend;
        }

        public void suspendTask() {
            this.isSuspend = true;
        }

        MainPrevUISetter(IBitmapProcessListener lis, byte loadType) {
            listener = lis;
            this.loadType = loadType;

            setDaemon(true);
        }

        @Override
        public void run() {
            super.run();

            if (isSuspendedTask())
                return;

            showProgress(true);

            try {
                // 이미지 효과가 적용된 사진이라면, 해당 필터부터 로딩하여 미리 보기 화면에 적용시킨다.
                if (mCurretEffectType != EffectType.ORIGIN) {
                    ImageEffectBitmap loadBmp;
                    switch (loadType) {
                        case LOAD_TYPE_ROTATE:
                            loadBmp = m_mapEffectBitmap.get(mCurretEffectType);
                            if (loadBmp != null) {
                                synchronized (loadBmp) {
                                    if (loadBmp.bitmapPreview != null && !loadBmp.bitmapPreview.isRecycled()) {
                                        loadBmp.bitmapPreview = CropUtil.getRotateImage(loadBmp.bitmapPreview, 90);
                                    } else {
                                        if (m_bmBaseResource != null && !m_bmBaseResource.isRecycled()) {
                                            loadBmp.bitmapPreview = getEffectAppliedBitmap(mCurretEffectType, m_bmBaseResource);
                                            loadBmp.isCreatedPreview = true;
                                        }
                                    }

                                    bmPriview = loadBmp.bitmapPreview;
                                }
                            }
                            break;
                        case LOAD_TYPE_CHANGED_ORIENTATION:
                            loadBmp = m_mapEffectBitmap.get(mCurretEffectType);
                            if (loadBmp != null)
                                synchronized (loadBmp) {
                                    if (loadBmp.bitmapPreview != null && !loadBmp.bitmapPreview.isRecycled()) {
                                        bmPriview = loadBmp.bitmapPreview;
                                    } else {
                                        if (m_bmBaseResource != null && !m_bmBaseResource.isRecycled()) {
                                            loadBmp.bitmapPreview = getEffectAppliedBitmap(mCurretEffectType, m_bmBaseResource);
                                            loadBmp.isCreatedPreview = true;
                                            bmPriview = loadBmp.bitmapPreview;
                                        }
                                    }
                                }
                            break;
                        case LOAD_TYPE_PREVIEW:
                        default:
                            ImageEffectBitmap effect = new ImageEffectBitmap();
                            effect.effectType = mCurretEffectType;
                            if (m_bmBaseResource != null && !m_bmBaseResource.isRecycled()) {
                                effect.bitmapPreview = getEffectAppliedBitmap(mCurretEffectType, m_bmBaseResource);
                                effect.isCreatedPreview = true;
                            }

                            if (effect.bitmapPreview != null && !effect.bitmapPreview.isRecycled()) {
                                bmPriview = effect.bitmapPreview;

                                if (m_mapEffectBitmap != null)
                                    m_mapEffectBitmap.put(effect.effectType, effect);
                            } else
                                bmPriview = m_bmBaseResource;
                            break;
                    }
                } else
                    bmPriview = m_bmBaseResource;
            } catch (Exception e) {
                bmPriview = m_bmBaseResource;
            }

            if (listener != null && !isSuspend())
                listener.onBaseBitmapCreated(bmPriview);

            if (mBaseBitmapCreateListener != null) {
                mBaseBitmapCreateListener.onBaseBitmapCreated();
            }
        }
    }

    public class EffectApplyTask extends Thread {

        private boolean isFinished = false;

        private String result = null;
        private Bitmap bmBaseResource = null;
        private EffectType effectType = null;
        private MyPhotoSelectImageData imageData = null;
        private IEffectApplyListener listener;

        public EffectApplyTask(int index, IEffectApplyListener lis) {
            listener = lis;
            init();
        }

        private void init() {
            setFinished(false);
            effectType = mCurretEffectType;
            imageData = mSelectedImage;
            setDaemon(true);
        }

        public boolean isFinished() {
            return isFinished;
        }

        public void setFinished(boolean isFinished) {
            this.isFinished = isFinished;
        }

        private String getSafetyImgName() {
            if (imageData == null) return "";
            if (!StringUtil.isEmpty(imageData.F_IMG_NAME)) return imageData.F_IMG_NAME;
            else if (!StringUtil.isEmpty(imageData.FB_OBJECT_ID)) return imageData.FB_OBJECT_ID;
            else if (!StringUtil.isEmpty(imageData.PATH)) {
                String path = imageData.PATH;
                if (path.contains("/")) {
                    if (Config.isFacebook_Photobook()) {
                        return path.substring(path.lastIndexOf("=") + 1);

                    } else if (ImageUtil.isGooglePhotoURL(path)) {
                        return "";

                    } else {
                        return path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));
                    }
                }


            }
            return "";
        }

        private String getExportFileName() {

            m_szFileNameSeparator = String.valueOf(System.currentTimeMillis());

            String szFileName = getSafetyImgName();

            String ext = ".jpg";

            if (szFileName != null && szFileName.contains(".")) {
                String expectedExt = szFileName.substring(szFileName.lastIndexOf("."));
                if (expectedExt.toLowerCase().contains("jpg") || expectedExt.toLowerCase().contains("png")) {
                    ext = expectedExt;
                }
            }

            if (szFileName != null && szFileName.contains("."))
                szFileName = szFileName.substring(0, szFileName.indexOf("."));

            // 폴더 생성.
            Config.setEFFECT_APPLIED_IMG_SAVE_PATH(Config.getExternalCacheDir(mContext) + "/snaps/effect/");

            File tempSavePath = new File(Config.getEFFECT_APPLIED_IMG_SAVE_PATH());

            // thumb path 폴더가 없으면 만든다.
            if (!tempSavePath.exists())
                tempSavePath.mkdirs();

            return Config.getEFFECT_APPLIED_IMG_SAVE_PATH() + szFileName + "_" + effectType.toString() + "_" + m_szFileNameSeparator + ext;
        }

        private String getProfileFileName() {
            Config.setEFFECT_APPLIED_IMG_SAVE_PATH(Config.getExternalCacheDir(mContext) + "/snaps/effect/");
            File tempSavePath = new File(Config.getEFFECT_APPLIED_IMG_SAVE_PATH());
            if (!tempSavePath.exists())
                tempSavePath.mkdirs();

            return Config.getEFFECT_APPLIED_IMG_SAVE_PATH() + SnapsDiaryCommonUtils.getUserProfileCacheFileName(mContext, false);
        }

        @Override
        public void run() {
            super.run();

            lockEffectApplyTask();

            if (isSuspendedTask()) {
                imageData.isApplyEffect = false;
                imageData.EFFECT_PATH = "";
                imageData.EFFECT_THUMBNAIL_PATH = "";
                imageData.EFFECT_TYPE = effectType.toString();
                if (listener != null)
                    listener.onReady();
                setFinished(true);
                notifyEffectApplyTaskSyncLocker();
                return;
            }

            if (EffectType.ORIGIN != effectType) {
                /**
                 * 미리 보기 이미지는 다운샘플링 되어 있으므로 다시 원본 이미지로 파일을 떠내고, 저장된 이미지의 path만 넘긴다.
                 */
                try {
                    result = getExportFileName();

                    File exportFile = new File(result);

                    if (exportFile.exists()) {
                        exportFile.delete();
                        exportFile = null;
                    }

                    ImageEffectBitmap loadBmp = m_mapEffectBitmap.get(effectType);
                    Bitmap exportBitmap = null;
                    if (loadBmp != null
                            && loadBmp.isCreatedPreview
                            && loadBmp.bitmapPreview != null
                            && !loadBmp.bitmapPreview.isRecycled()) {
                        exportBitmap = CropUtil.getInSampledBitmapCopy(loadBmp.bitmapPreview, android.graphics.Bitmap.Config.ARGB_8888);
                    } else {
                        if (m_bmBaseResource != null && !m_bmBaseResource.isRecycled()) {
                            exportBitmap = getEffectAppliedBitmap(effectType, m_bmBaseResource);
                        }
                    }

                    if (exportBitmap != null && !exportBitmap.isRecycled()) {

                        if (m_isRequestCropFile) {
                            setCropedImgData(exportBitmap, imageData);
                        }

                        result = ImageFilters.getAppliedEffectImgFilePath(mContext, exportBitmap, result);
                        if (!exportBitmap.isRecycled()) {
                            exportBitmap.recycle();
                            exportBitmap = null;
                        }
                    }

                    imageData.EFFECT_PATH = result;
                    imageData.EFFECT_THUMBNAIL_PATH = result;
                    imageData.EFFECT_TYPE = effectType.toString();
                    imageData.isApplyEffect = true;
                    imageData.isModify = 0;

                } catch (Exception e) {
                    Dlog.e(TAG, e);
                } finally {
                    if (listener != null)
                        listener.onReady();
                    setFinished(true);
                    notifyEffectApplyTaskSyncLocker();
                }
            } else {
                imageData.isApplyEffect = false;
                imageData.EFFECT_PATH = "";
                imageData.EFFECT_THUMBNAIL_PATH = "";
                imageData.EFFECT_TYPE = effectType.toString();

                if (m_isRequestCropFile) {
                    setCropedImgData(m_bmBaseResource, imageData);
                }

                if (listener != null)
                    listener.onReady();
                setFinished(true);
                notifyEffectApplyTaskSyncLocker();
            }
        }

        void setCropedImgData(Bitmap bm, MyPhotoSelectImageData imgData) {
            if (imgData == null || bm == null || bm.isRecycled()) return;

            Bitmap cropedBitmap = SnapsBitmapUtil.processCropInfo(bm, 0, imgData.ADJ_CROP_INFO, BitmapUtil.getImagePath(imgData));

            String cropedFilePath = getProfileFileName();
            File profile = new File(cropedFilePath);
            if (profile.exists()) {
                profile.delete();
            }

            cropedFilePath = ImageFilters.getAppliedEffectImgFilePath(mContext, cropedBitmap, cropedFilePath);
            if (cropedBitmap != null && !cropedBitmap.isRecycled()) {
                cropedBitmap.recycle();
                cropedBitmap = null;
            }

            imgData.PATH = cropedFilePath;
        }
    }

    public void setPageIdx(int m_iPageIdx) {
        this.m_iPageIdx = m_iPageIdx;
    }
}
