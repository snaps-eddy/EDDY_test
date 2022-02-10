package com.snaps.mobile.activity.google_style_image_selector.activities.processors.etc;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies.ImageSelectUIProcessorStrategyFactory;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectListAnimationListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items.ImageSelectTrayCellItem;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;
import com.snaps.mobile.utils.custom_layouts.ARelativeLayoutParams;

import java.util.LinkedList;

import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.INVALID_TRAY_CELL_ID;

/**
 * Created by ysjeong on 2016. 12. 15..
 */

public class ImageSelectUIAnimation implements ISnapsHandler {
    private static final String TAG = ImageSelectUIAnimation.class.getSimpleName();
    private SnapsHandler mHandler = null;
    private SparseArray<Object> attributes = null;

    private final int HANDLER_ATTRIBUTE_KEY_IMAGE_VIEW = 0;
    private final int HANDLER_ATTRIBUTE_KEY_ROOT_VIEW = 1;

    private static final long LIMIT_SUCCESSIVE_CLICK_TIME = 200; //연속 클릭을 막기 위함.(사진 선택 후 해당 시간동안 다른 아이템 클릭을 못한다..연속으로 누르면 버그가 있다.)

    public static final int ANIM_TIME_TRAY_ALL_VIEW = 300; //전체 보기 뷰가 왔다 갔다 하는 시간
    public static final int ANIM_TIME_ALBUM_LIST_SELECTOR = 150; //트레이 선택 했을 때, 전체 보기 버튼 영역이 내려오는 시간
    public static final int ANIM_TIME_FRAGMENT_TO_TRAY = 200; //사진 선택했을때, 트레이로 날아가는 시간

    private boolean isActiveAnim = false;

    private long lPrevPerformedTime = 0l;

    public ImageSelectUIAnimation() {
        mHandler = new SnapsHandler(this);
        attributes = new SparseArray<>();
    }

    public void releaseInstance() {
        if (mHandler != null) {
            mHandler.releaseInstance();
        }

        if (attributes != null) {
            attributes.clear();
            attributes = null;
        }
    }

    public boolean isEnableClick() {
        return System.currentTimeMillis() - lPrevPerformedTime > LIMIT_SUCCESSIVE_CLICK_TIME;
    }

    //선택한 사진이 Tray를 향해 날아가는 애미메이션
    public void startFragmentToTrayAnimation(Activity activity, ImageSelectUIAnimation.TemplateToTrayAnimBuilder builder, IImageSelectListAnimationListener animationListener) throws Exception {
        if (builder == null || activity == null) return;

        //연속으로 누르는 동작으로 생기는 애니메이션 오류를 대비해 그냥 애니메이션이 종료되었다고 간주한다.
        if (isActiveAnim) {
            if (animationListener != null) {
                animationListener.onFinishedTrayInsertAnimation();
            }
            return;
        }

        //트레이의 offset구하기
        TemplateToTrayAnimInfo animInfo = createAnimationView(activity, builder);
        if (animInfo != null) {
            startFragmentToTrayAnimation(animInfo, builder, animationListener);
        } else {
            if (animationListener != null) {
                animationListener.onFinishedTrayInsertAnimation();
            }
        }
    }

    private boolean isSkipAnimation(Activity activity, TemplateToTrayAnimInfo animInfo) {
        if (activity == null || animInfo == null) return false;

        try {
            Resources resources = activity.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();

            boolean isLargeWidth = (animInfo.getTargetOffsetRect().width() / animInfo.getScaleX()) >= (metrics.widthPixels - 100);
            boolean isLargeHeight = (animInfo.getTargetOffsetRect().height() / animInfo.getScaleY()) >= (metrics.heightPixels / 2);

            if (isLargeWidth || isLargeHeight) { //너무 큰 이미지는 애니메이션을 생략한다.
                return true;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return false;
    }

    private void startFragmentToTrayAnimation(TemplateToTrayAnimInfo animInfo, final  ImageSelectUIAnimation.TemplateToTrayAnimBuilder builder, final IImageSelectListAnimationListener animationListener) throws Exception {
        if (builder == null || animInfo == null) return;

        final ImageView imageView = builder.getTempImageView();
        final RelativeLayout rootLayout = builder.getRootLayout();
        Rect targetOffsetRect = animInfo.getTargetOffsetRect();
        if (targetOffsetRect == null || imageView == null) return;

        isActiveAnim = true;
        lPrevPerformedTime = System.currentTimeMillis();

        float targetOffsetX = targetOffsetRect.left;
        float targetOffsetY = targetOffsetRect.top;

        if (builder.getUiType() != ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.TEMPLATE //TODO ...다음에 Dummy가 안 생기는 것 같다..
                && builder.getUiType() != ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.EMPTY
                && builder.getUiType() != ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.SINGLE_CHOOSE
                && builder.getUiType() != ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.MULTI_CHOOSE
                && builder.getUiType() != ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.IDENTIFY_PHOTO) {
            ImageSelectTrayCellItem cellItem = builder.getCellItem();
            if (cellItem != null) {
                if (cellItem.getCellState() != ISnapsImageSelectConstants.eTRAY_CELL_STATE.EMPTY_DUMMY
                        && (builder.getUiType() == ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.SMART_ANALYSIS && cellItem.getCellId() != INVALID_TRAY_CELL_ID) ) //애니 커버 홀더 예외 처리
                targetOffsetX += (targetOffsetRect.width() + UIUtil.convertDPtoPX(animInfo.getActivity(), 8));
            }
        }

        AnimationSet animSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.f, animInfo.getScaleX(), 1.f, animInfo.getScaleY());
        scaleAnimation.setDuration(ANIM_TIME_FRAGMENT_TO_TRAY);

        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, targetOffsetX,
                Animation.ABSOLUTE, -getStatusBarHeight(), Animation.ABSOLUTE, targetOffsetY);
        translateAnimation.setDuration(ANIM_TIME_FRAGMENT_TO_TRAY);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                try {
                    //바로 해제 시키면 날아가다가 사라져서 약간의 딜레이 후에 해제 시킨다.
                    requestClearAnimation(imageView, rootLayout);

                    if (animationListener != null) {
                        animationListener.onFinishedTrayInsertAnimation();
                    }

                    isActiveAnim = false;
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });

        animSet.addAnimation(scaleAnimation);
        animSet.addAnimation(translateAnimation);
        animSet.setInterpolator(AnimationUtils.loadInterpolator(animInfo.getActivity(), android.R.anim.accelerate_decelerate_interpolator)); //점점 느리게..(기획 요청)
        animSet.setFillEnabled(true);

        imageView.startAnimation(animSet);
    }

    private void requestClearAnimation(ImageView imageView, RelativeLayout rootLayout) {
        if (attributes != null) {
            attributes.clear();
            attributes.put(HANDLER_ATTRIBUTE_KEY_IMAGE_VIEW, imageView);
            attributes.put(HANDLER_ATTRIBUTE_KEY_ROOT_VIEW, rootLayout);

            if (mHandler != null) {
                mHandler.addAttributes(attributes);
                mHandler.sendEmptyMessageDelayed(HANDLER_MSG_CLEAR_ANIMATION, 50);
            }
        }
    }

    private int getStatusBarHeight() {
        Context context = ContextUtil.getContext();
        if (context == null) return 0;

        Resources resources = context.getResources();
        if (resources == null) return 0;

        int result = 0;
        try {
            int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId);
            }
            return result;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return 0;
    }

    private Rect getTargetOffsetRect(ImageSelectUIAnimation.TemplateToTrayAnimBuilder builder) {
        if (builder == null) return null;

        Rect targetOffsetRect = new Rect();

        //기본 첫번째 위치
        int defaultOffsetX = UIUtil.convertDPtoPX(ContextUtil.getContext(), 16);
        int defaultOffsetY = UIUtil.convertDPtoPX(ContextUtil.getContext(), 48);
        int defaultOffsetDimens = UIUtil.convertDPtoPX(ContextUtil.getContext(), 76);
        targetOffsetRect.set(defaultOffsetX, defaultOffsetY, defaultOffsetX + defaultOffsetDimens, defaultOffsetY + defaultOffsetDimens);

        ImageSelectTrayCellItem cellItem = builder.getCellItem();
        if (cellItem == null) return targetOffsetRect;

        ImageSelectAdapterHolders.TrayThumbnailItemHolder targetHolder = cellItem.getHolder();
        if (targetHolder != null) {
            RelativeLayout trayView = targetHolder.getParentView();
            if (trayView != null) {
                trayView.getGlobalVisibleRect(targetOffsetRect); //FIXME 위치가 정확한가?

                targetOffsetRect.offset(0, -getStatusBarHeight());
            }
        }

        switch (builder.getUiType()) {
            case TEMPLATE:
                break;
            case EMPTY:
            case IDENTIFY_PHOTO:
                if (cellItem.getCellState() != ISnapsImageSelectConstants.eTRAY_CELL_STATE.EMPTY_DUMMY) {
                    //다음 자리..
                    int margin = UIUtil.convertDPtoPX(ContextUtil.getContext(), 8);
                    targetOffsetRect.left += (targetOffsetRect.width() + margin);
                }
                break;
        }

        return targetOffsetRect;
    }

    private TemplateToTrayAnimInfo createAnimationView(Activity activity, ImageSelectUIAnimation.TemplateToTrayAnimBuilder builder) {
        if(builder == null || activity == null) return null;

        ImageView ivTempAnimView = builder.getTempImageView();
        RelativeLayout lyRootLayout = builder.getRootLayout();
        ImageSelectAdapterHolders.PhotoFragmentItemHolder fragmentViewHolder = builder.getFragmentViewHolder();

        if (lyRootLayout == null) return null;

        //트레이(이동할 타겟)의 offset구하기
        Rect trayOffsetRect = getTargetOffsetRect(builder);

        //만약 애니메이션이 정상적으로 끝나지 않았을 경우를 대비해서..
        if (ivTempAnimView != null) {
            ivTempAnimView.setImageBitmap(null);
            lyRootLayout.removeView(ivTempAnimView);
            BitmapUtil.bitmapRecycle(builder.getTempBitmap());
        }

        //애니메이션으로 보여줄 ImageView 생성
        ivTempAnimView = new ImageView(activity);
        ARelativeLayoutParams layoutParams = new ARelativeLayoutParams(new ARelativeLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        //선택한 아이템의 offset 및 size
        Rect fragmentItemOffset = new Rect();

        //전달할 애니메이션 정보
        TemplateToTrayAnimInfo animInfo = new TemplateToTrayAnimInfo();
        animInfo.setActivity(activity);

        ImageView thumbnailView = fragmentViewHolder.getThumbnail();
        if (thumbnailView != null) {
            thumbnailView.getGlobalVisibleRect(fragmentItemOffset);
            float fromX = fragmentItemOffset.left;
            float fromY = fragmentItemOffset.top - getStatusBarHeight();

            layoutParams.width = thumbnailView.getMeasuredWidth();
            layoutParams.height = thumbnailView.getMeasuredHeight();

            //화면에 짤리는 부분에 위치하고 있다면 살짝 올려 준다.
            if (fromY + layoutParams.height > UIUtil.getScreenHeight(activity)) {
                fromY = UIUtil.getScreenHeight(activity) - layoutParams.height;
            }

            layoutParams.leftMargin = (int) fromX;
            layoutParams.topMargin = (int) fromY;

            final float THUMBNAIL_DIMENS = activity.getResources().getDimension(R.dimen.tray_cell_dimens);

            //translate 이동량 구하기
            Rect toRect = new Rect();
            toRect.left = (int) (trayOffsetRect.left - fromX);
            toRect.top =  (int) (trayOffsetRect.top  - fromY);
            toRect.right = (int) (toRect.left + THUMBNAIL_DIMENS);
            toRect.bottom = (int) (toRect.top + THUMBNAIL_DIMENS);

            int targetImageSize = 0;
            ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH uiDepth = null;
            ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
            if (imageSelectManager != null) {
                targetImageSize = imageSelectManager.getCurrentUIDepthThumbnailSize();
                uiDepth = imageSelectManager.getCurrentUIDepth();
            }

            animInfo.setTargetOffsetRect(toRect);
            animInfo.setScaleX(THUMBNAIL_DIMENS / (float)layoutParams.width);
            animInfo.setScaleY(THUMBNAIL_DIMENS / (float)layoutParams.height);

            if (isSkipAnimation(activity, animInfo)) {
                return null;
            }

            ivTempAnimView.setLayoutParams(layoutParams);

            ImageView.ScaleType scaleType = uiDepth != null && uiDepth == ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH.DEPTH_STAGGERED ? ImageView.ScaleType.FIT_XY : ImageView.ScaleType.CENTER_CROP;
            ivTempAnimView.setScaleType(scaleType);

            String imagePath = "";
            GalleryCursorRecord.PhonePhotoFragmentItem photoItem = fragmentViewHolder.getPhonePhotoItem();
            if (photoItem != null && photoItem.getPhotoInfo() != null) {
                imagePath = photoItem.getPhotoInfo().getThumbnailPath();
            } else {
                MyPhotoSelectImageData imageData = fragmentViewHolder.getImgData();
                if (imageData != null) {
                    imagePath = StringUtil.isEmpty(imageData.THUMBNAIL_PATH) ? ImageUtil.getImagePath(activity, imageData) : imageData.THUMBNAIL_PATH;
                }
            }

            //만약, 이미지 불러올수가 없다면, 캡쳐라도 뜬다.
            if (StringUtil.isEmpty(imagePath)) {
                thumbnailView.setDrawingCacheEnabled(true);
                Bitmap bmp = thumbnailView.getDrawingCache();
                ivTempAnimView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                if (bmp != null && !bmp.isRecycled()) {
                    Bitmap copiedBitmap = CropUtil.getInSampledBitmapCopy(bmp, Bitmap.Config.RGB_565);
                    if (copiedBitmap != null && !copiedBitmap.isRecycled()) {
                        ivTempAnimView.setImageBitmap(copiedBitmap);
                        builder.setTempBitmap(copiedBitmap);
                    }
                }
                thumbnailView.setDrawingCacheEnabled(false);
            }

            ImageSelectUtils.loadImage(activity, imagePath, targetImageSize, ivTempAnimView, scaleType);

            lyRootLayout.addView(ivTempAnimView);
            builder.setTempImageView(ivTempAnimView);
        }

        return animInfo;
    }


    public void startVerticalTranslateAnimation(View targeView, boolean show, long animTime) throws Exception {
        startVerticalTranslateAnimation(targeView, null, show, animTime);
    }

    public void startVerticalTranslateAnimation(View targetView, ImageView arrowImg, boolean show, long animTime) throws Exception {
        if (targetView == null || isActiveAnim) return;

        if (show) {
            if (targetView.isShown()) return;
            TranslateAnimation transAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f);
            startTranslateAnimation(targetView, arrowImg, transAnim, true, animTime);
        } else {
            if (!targetView.isShown()) return;
            TranslateAnimation transAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.f, Animation.RELATIVE_TO_SELF, 0.f, Animation.RELATIVE_TO_SELF, 0.f, Animation.RELATIVE_TO_SELF, -1f);
            startTranslateAnimation(targetView, arrowImg, transAnim, false, animTime);
        }
    }

    private void startTranslateAnimation(final View view, final ImageView arrowImg, TranslateAnimation transAnim, final boolean show, long animTime) throws Exception {
        isActiveAnim = true;
        lPrevPerformedTime = System.currentTimeMillis();

        transAnim.setFillAfter(true);
        transAnim.setDuration(animTime);
        transAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (show) {
                    if (view != null)
                        view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                isActiveAnim = false;
                if (!show) {
                    if (view != null) {
                        view.clearAnimation();
                        view.setVisibility(View.INVISIBLE);
                    }
                }

                if (arrowImg != null) {
                    int arrowImgRes = show ? R.drawable.img_triangle_up : R.drawable.img_triangle_down;
                    arrowImg.setImageResource(arrowImgRes);
                }
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
        });

        view.startAnimation(transAnim);
    }

    public static class TemplateToTrayAnimBuilder {
        private RelativeLayout rootLayout;
        private ImageView tempImageView;
        private Bitmap tempBitmap;
        private ImageSelectAdapterHolders.PhotoFragmentItemHolder fragmentViewHolder;
        private ImageSelectTrayCellItem cellItem;
        private ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE uiType;

        public ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE getUiType() {
            return uiType;
        }

        public TemplateToTrayAnimBuilder setTempBitmap(Bitmap tempBitmap) {
            this.tempBitmap = tempBitmap;
            return this;
        }

        public TemplateToTrayAnimBuilder setUiType(ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE uiType) {
            this.uiType = uiType;
            return this;
        }

        public TemplateToTrayAnimBuilder setFragmentViewHolder(ImageSelectAdapterHolders.PhotoFragmentItemHolder fragmentViewHolder) {
            this.fragmentViewHolder = fragmentViewHolder;
            return this;
        }

        public TemplateToTrayAnimBuilder setCellItem(ImageSelectTrayCellItem cellItem) {
            this.cellItem = cellItem;
            return this;
        }

        public TemplateToTrayAnimBuilder setRootLayout(RelativeLayout rootLayout) {
            this.rootLayout = rootLayout;
            return this;
        }

        public TemplateToTrayAnimBuilder setTempImageView(ImageView tempImageView) {
            this.tempImageView = tempImageView;
            return this;
        }

        public RelativeLayout getRootLayout() {
            return rootLayout;
        }

        public ImageView getTempImageView() {
            return tempImageView;
        }

        public Bitmap getTempBitmap() {
            return tempBitmap;
        }

        public ImageSelectAdapterHolders.PhotoFragmentItemHolder getFragmentViewHolder() {
            return fragmentViewHolder;
        }

        public ImageSelectTrayCellItem getCellItem() {
            return cellItem;
        }

        public TemplateToTrayAnimBuilder create() {
            return this;
        }
    }

    public class TemplateToTrayAnimInfo {
        Activity activity = null;
        Rect targetOffsetRect = null;
        float scaleX = 1.f;
        float scaleY = 1.f;

        public Rect getTargetOffsetRect() {
            return targetOffsetRect;
        }

        public void setTargetOffsetRect(Rect targetOffsetRect) {
            this.targetOffsetRect = targetOffsetRect;
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

        public Activity getActivity() {
            return activity;
        }

        public void setActivity(Activity activity) {
            this.activity = activity;
        }
    }

    private final int HANDLER_MSG_CLEAR_ANIMATION = 0;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLER_MSG_CLEAR_ANIMATION:
                try {
                    ImageView ivAnimView = null;
                    RelativeLayout rootView = null;

                    if (mHandler != null) {
                        LinkedList<SparseArray<?>> attributeList = mHandler.getAttributeList();
                        if (attributeList != null && !attributeList.isEmpty()) {
                            SparseArray<?> attribute = attributeList.poll();
                            if (attribute != null) {
                                Object objImageView = attribute.get(HANDLER_ATTRIBUTE_KEY_IMAGE_VIEW);
                                if (objImageView != null && objImageView instanceof  ImageView)
                                    ivAnimView = (ImageView) objImageView;

                                Object objRootView = attribute.get(HANDLER_ATTRIBUTE_KEY_ROOT_VIEW);
                                if (objRootView != null && objRootView instanceof RelativeLayout)
                                    rootView = (RelativeLayout) objRootView;
                            }
                        }
                    }

                    if (ivAnimView != null) {
                        ivAnimView.clearAnimation();

                        try {
                            ViewUnbindHelper.unbindReferences(ivAnimView, null, false);
                        } catch (Exception e) {
                            Dlog.e(TAG, e);
                        }

                        if (rootView != null) {
                            rootView.removeView(ivAnimView);
                        }
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                break;
        }
    }
}
