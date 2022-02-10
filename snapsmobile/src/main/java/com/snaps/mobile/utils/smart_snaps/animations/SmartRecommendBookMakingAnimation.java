package com.snaps.mobile.utils.smart_snaps.animations;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.smart_analysis_product.page_edit.data.SmartRecommendBookMakingAnimationViews;
import com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_recommend_layout_making.ISmartRecommendLayoutMakingAnimationImp;
import com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_recommend_layout_making.SmartRecommendLayoutMakingAnimationAttribute;
import com.snaps.mobile.utils.smart_snaps.animations.strategies.smart_recommend_layout_making.SmartRecommendLayoutMakingAnimationFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import errorhandle.logger.Logg;

public class SmartRecommendBookMakingAnimation implements ISnapsHandler {
    private static final String TAG = SmartRecommendBookMakingAnimation.class.getSimpleName();

    private static final long TIME_OF_SHOW_MEMORIES_IMAGE = 1000;
    private static final long TIME_OF_SHOW_USERNAME_VIEW = 1300;
    private static final long TIME_OF_DURING_USERNAME_VIEW = 2200;
    private static final long TIME_OF_DURING_MEMORIES_VIEW = 3000;
    private static final long TIME_OF_DESC_TEXT_VIEW_SWIPE = 3500;

    private static final int MAX_SAVE_ANIMATION_COUNT = 10;

    private ImageView centerViewA;
    private ImageView centerViewB;
    private ImageView memoriesImage;
    private TextView userNameView;
    private TextView descTextViewA;
    private TextView descTextViewB;
    private LinkedList<Integer> shuffleIndexList = null;
    private ArrayList<MyPhotoSelectImageData> selectImageList = null;
    private boolean isStop = false;
    private Activity activity = null;
    private SnapsHandler snapsHandler = null;
    private int descTextCount = 0;

    private LinkedList<Integer> animationTimeLoop = new LinkedList<Integer>(Arrays.asList(2500, 2500, 2000, 2000, 1800, 1800, 1800));
    private LinkedList<ISmartRecommendLayoutMakingAnimationImp> animations = new LinkedList<>();

    private boolean toggleImageViewA = false;
    private boolean toggleDescTextA = false;
    private SmartRecommendLayoutMakingAnimationFactory.eSmartRecommendLayoutMakingAnimationType prevAnimationType = null;

    public static SmartRecommendBookMakingAnimation createAnimationWithImageView(Activity activity, SmartRecommendBookMakingAnimationViews animationViews) {
        SmartRecommendBookMakingAnimation analysisMakingAnimation = new SmartRecommendBookMakingAnimation();
        analysisMakingAnimation.setActivity(activity);
        analysisMakingAnimation.setCenterViewA(animationViews.getCenterViewA());
        analysisMakingAnimation.setCenterViewB(animationViews.getCenterViewB());
        analysisMakingAnimation.setMemoriesImage(animationViews.getMemoriesImage());
        analysisMakingAnimation.setUserNameView(animationViews.getUserNameView());
        analysisMakingAnimation.setDescTextViewA(animationViews.getDescTextViewA());
        analysisMakingAnimation.setDescTextViewB(animationViews.getDescTextViewB());
        return analysisMakingAnimation;
    }

    private SmartRecommendBookMakingAnimation() {
        snapsHandler = new SnapsHandler(this);
        shuffleIndexList = new LinkedList<>();
        DataTransManager dataTransManager = DataTransManager.getInstance();
        if (dataTransManager != null) {
            selectImageList = dataTransManager.getPhotoImageDataList();
            if (selectImageList != null) {
                int imageCnt = selectImageList.size();
                Random random = new Random();
                while (shuffleIndexList.size() < imageCnt) {
                    int randomIdx = random.nextInt(imageCnt);
                    if (!shuffleIndexList.contains(randomIdx)) {
                        shuffleIndexList.add(randomIdx);
                    }
                }
            }
        }
    }

    public void releaseInstance() {
        if (shuffleIndexList != null) {
            shuffleIndexList.clear();
            shuffleIndexList = null;
        }

        if (animations != null) {
            animations.clear();
        }

        if (activity != null) {
            if (centerViewA != null) {
                ImageLoader.clear(activity, centerViewA);
            }

            if (centerViewB != null) {
                ImageLoader.clear(activity, centerViewB);
            }
            activity = null;
        }
    }

    private String nextImagePath() {
        if (shuffleIndexList == null || shuffleIndexList.isEmpty()) return null;
        int idx = shuffleIndexList.poll();
        shuffleIndexList.add(idx);
        if (selectImageList != null && selectImageList.size() > idx) {
            MyPhotoSelectImageData imageData = selectImageList.get(idx);
            if (imageData != null){
                return !StringUtil.isEmpty(imageData.LOCAL_THUMBNAIL_PATH) ? imageData.LOCAL_THUMBNAIL_PATH : imageData.PATH;
            }
        }
        return null;
    }

    public void startAnimation() {
        showNextImage();

        startMemoriesTextAnimation();

        startUserNameTextAnimation();

        startDescTextAnimation();
    }

    public boolean isStop() {
        return isStop;
    }

    public void restartAnimation() {
        isStop = false;
        if (!isActiveAnimation()) return;

        removeAllHandleMsg();

        showNextImage();

        startDescTextAnimation();
    }

    public void requestRestartAnimation() {
        if (snapsHandler == null) return;
        snapsHandler.sendEmptyMessageDelayed(RESTART_ANIMATION,3000);
    }

    private void startHideTextAnimation(final View hideView, long animationDuring, final SnapsCommonResultListener<Void> completeListener) {
        ValueAnimator viewParamsAnimator = ValueAnimator.ofFloat(1f, 0.f);
        viewParamsAnimator.setDuration(animationDuring);
        viewParamsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!isActiveAnimation()) {
                    if (hideView != null) {
                        hideView.setAlpha(0);
                    }

                    if (completeListener != null) {
                        completeListener.onResult(null);
                    }
                    return;
                }

                if (hideView != null && animation != null) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    hideView.setAlpha(animatedValue);

                    if (animatedValue == 0 && completeListener != null) {
                        completeListener.onResult(null);
                    }
                }
            }
        });

        viewParamsAnimator.start();
    }

    private void startShowTextAnimation(final TextView showView, long animationDuring) {
        updateDescText(showView);

        ValueAnimator viewParamsAnimator = ValueAnimator.ofFloat(0.f, 1.f);
        viewParamsAnimator.setDuration(animationDuring);
        viewParamsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (showView != null && animation != null) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    showView.setAlpha(animatedValue);
                }
            }
        });

        viewParamsAnimator.start();
    }

    private void showNextImage() {
        if (!isActiveAnimation()) return;

        final String nextLoadImagePath = nextImagePath();
        Dlog.d("showNextImage() imagePath:" + nextLoadImagePath);
        if (StringUtil.isEmpty(nextLoadImagePath)) return;

        final long ANIMATION_TIME = getLoopAnimationTime();
        final int IMAGE_LOAD_SIZE = Math.max(480, (int) (UIUtil.getScreenWidth(activity) * .35f));

        toggleImageViewA = !toggleImageViewA;
        final ImageView visibleImageView = toggleImageViewA ? centerViewA : centerViewB;
        final ImageView hideImageView = toggleImageViewA ? centerViewB : centerViewA;

        visibleImageView.setAlpha(1.f);
        startHideTextAnimation(hideImageView, (ANIMATION_TIME - 1000), new SnapsCommonResultListener<Void>() {
            @Override
            public void onResult(Void aVoid) {
                try {
                    if (isActiveAnimation()) {
                        ImageLoader.with( activity ).override(IMAGE_LOAD_SIZE, IMAGE_LOAD_SIZE).load( nextLoadImagePath ).into(hideImageView);
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });

        if (visibleImageView.getDrawable() == null) {
            final String fistLoadImagePath = nextImagePath();
            if (!StringUtil.isEmpty(fistLoadImagePath)) {
                ImageLoader.with( activity ).override(IMAGE_LOAD_SIZE, IMAGE_LOAD_SIZE).load( fistLoadImagePath ).into(visibleImageView);
            }
        }

        performAnimation(visibleImageView, ANIMATION_TIME);

        if (isActiveAnimation()) {
            if (snapsHandler != null)
                snapsHandler.sendEmptyMessageDelayed(SHOW_NEXT_IMAGE, ANIMATION_TIME);
        }
    }

    private void removeAllHandleMsg() {
        if (snapsHandler == null) return;
        snapsHandler.removeMessages(SHOW_NEXT_IMAGE);
        snapsHandler.removeMessages(SHOW_MEMORIES_IMAGE);
        snapsHandler.removeMessages(SHOW_USER_NAME_VIEW);
        snapsHandler.removeMessages(SWIPE_DESC_TEXT);
        snapsHandler.removeMessages(RESTART_ANIMATION);
    }

    private void performAnimation(final ImageView visibleImageView, long animationTime) {
        if (!isActiveAnimation()) return;

        SmartRecommendLayoutMakingAnimationAttribute animationAttribute
                = new SmartRecommendLayoutMakingAnimationAttribute.Builder(activity).setImageView(visibleImageView).setAnimationTime(animationTime).setPrevAnimation(prevAnimationType).create();
        ISmartRecommendLayoutMakingAnimationImp animationImp =
                SmartRecommendLayoutMakingAnimationFactory.createRandomAnimation(animationAttribute);
        if (animationImp != null) {
            prevAnimationType = animationImp.getAnimationType();
            animationImp.startAnimation();

            addAnimation(animationImp);
        }
    }

    private void addAnimation(ISmartRecommendLayoutMakingAnimationImp animationImp) {
        if (animations == null) return;

        if (animations.size() > MAX_SAVE_ANIMATION_COUNT) {
            animations.poll();
        }

        animations.add(animationImp);
    }

    public void stopAllAnimations() {
        isStop = true;

        removeAllHandleMsg();

        if (animations != null) {
            try {
                synchronized (animations) {
                    for (ISmartRecommendLayoutMakingAnimationImp animationImp : animations) {
                        if (animationImp == null) continue;

                        animationImp.stop();
                    }
                }
            } catch (Exception e) { Dlog.e(TAG, e); }
        }
    }

    private void showMemoriesImage() {
        if (memoriesImage == null) return;
        memoriesImage.setVisibility(View.VISIBLE);

        int imageWidth = memoriesImage.getMeasuredWidth() > 0 ? memoriesImage.getMeasuredWidth() : UIUtil.getScreenWidth(activity);
        int imageHeight = memoriesImage.getMeasuredHeight() > 0 ? memoriesImage.getMeasuredHeight() : imageWidth;

        final int centerX = imageWidth/2;
        final int centerY = imageHeight/2;
        memoriesImage.setPivotX(centerX);
        memoriesImage.setPivotY(centerY);

        ValueAnimator viewParamsAnimator = ValueAnimator.ofFloat(0f, 1f);
        viewParamsAnimator.setDuration(TIME_OF_DURING_MEMORIES_VIEW);
        viewParamsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (memoriesImage != null && animation != null) {
                    float value = (float) animation.getAnimatedValue();
                    float scale = 0.9f + (0.1f * value);

                    memoriesImage.setScaleX(scale);
                    memoriesImage.setScaleY(scale);

                    float alpha = 1f - value;
                    memoriesImage.setAlpha(alpha);
                }
            }
        });

        viewParamsAnimator.start();
    }

    private void showUserNameView() {
        if (userNameView == null) return;
        userNameView.setVisibility(View.VISIBLE);

        final int centerX = userNameView.getMeasuredWidth()/2;
        final int centerY = userNameView.getMeasuredHeight()/2;
        userNameView.setPivotX(centerX);
        userNameView.setPivotY(centerY);

        ValueAnimator viewParamsAnimator = ValueAnimator.ofFloat(0f, 1f);
        viewParamsAnimator.setDuration(TIME_OF_DURING_USERNAME_VIEW);
        viewParamsAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (userNameView != null && animation != null) {
                    float value = (float) animation.getAnimatedValue();
                    float alpha = 1f - value;
                    userNameView.setAlpha(alpha);
                }
            }
        });

        viewParamsAnimator.start();
    }

    private void swipeDescText() {
        if (activity == null || descTextViewA == null || descTextViewB == null || !isActiveAnimation()) return;

        toggleDescTextA = !toggleDescTextA;

        TextView hideView = toggleDescTextA ? descTextViewA : descTextViewB;
        TextView showView = toggleDescTextA ? descTextViewB : descTextViewA;

        startHideTextAnimation(hideView, 500, null);
        startShowTextAnimation(showView, 1000);

        startDescTextAnimation();
    }

    private void updateDescText(TextView textView) {
        if (textView == null || activity == null) return;
        final int TEXT_COUNT = ++descTextCount%4;
        switch (TEXT_COUNT) {
            case 0:
                textView.setText(activity.getString(R.string.auto_recommand_making_photobook_wait_msg));
                break;
            case 1:
                textView.setText(activity.getString(R.string.smart_analysis_book_making_desc_b));
                break;
            case 2:
                textView.setText(activity.getString(R.string.smart_analysis_book_making_desc_c));
                break;
            default:
                textView.setText(activity.getString(R.string.smart_analysis_book_making_desc_d));
                break;
        }
    }

    private void startMemoriesTextAnimation() {
        if (snapsHandler != null)
            snapsHandler.sendEmptyMessageDelayed(SHOW_MEMORIES_IMAGE, TIME_OF_SHOW_MEMORIES_IMAGE);
    }

    private void startUserNameTextAnimation() {
        if (snapsHandler != null)
            snapsHandler.sendEmptyMessageDelayed(SHOW_USER_NAME_VIEW, TIME_OF_SHOW_USERNAME_VIEW);
    }

    private void startDescTextAnimation() {
        if (snapsHandler != null)
            snapsHandler.sendEmptyMessageDelayed(SWIPE_DESC_TEXT, TIME_OF_DESC_TEXT_VIEW_SWIPE);
    }

    private long getLoopAnimationTime() {
        int returnTime = animationTimeLoop.poll();
        animationTimeLoop.add(returnTime);
        return returnTime;
    }

    private boolean isActiveAnimation() {
        return !isStop && activity != null && !activity.isFinishing();
    }

    public void setCenterViewA(ImageView centerViewA) {
        this.centerViewA = centerViewA;
    }

    public void setCenterViewB(ImageView centerViewB) {
        this.centerViewB = centerViewB;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setMemoriesImage(ImageView memoriesImage) {
        this.memoriesImage = memoriesImage;
    }

    public void setUserNameView(TextView userNameView) {
        this.userNameView = userNameView;
    }

    public TextView getDescTextViewA() {
        return descTextViewA;
    }

    public void setDescTextViewA(TextView descTextViewA) {
        this.descTextViewA = descTextViewA;
    }

    public TextView getDescTextViewB() {
        return descTextViewB;
    }

    public void setDescTextViewB(TextView descTextViewB) {
        this.descTextViewB = descTextViewB;
    }

    private static final int SHOW_NEXT_IMAGE = 0;
    private static final int SHOW_MEMORIES_IMAGE = 1;
    private static final int SHOW_USER_NAME_VIEW = 2;
    private static final int SWIPE_DESC_TEXT = 3;
    private static final int RESTART_ANIMATION = 4;

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_NEXT_IMAGE:
                if (!isActiveAnimation()) return;
                showNextImage();
                break;
            case SHOW_MEMORIES_IMAGE:
                if (!isActiveAnimation()) return;
                showMemoriesImage();
                break;
            case SHOW_USER_NAME_VIEW:
                if (!isActiveAnimation()) return;
                showUserNameView();
                break;
            case SWIPE_DESC_TEXT:
                if (!isActiveAnimation()) return;
                swipeDescText();
                break;
            case RESTART_ANIMATION:
                restartAnimation();
                break;
        }
    }
}
