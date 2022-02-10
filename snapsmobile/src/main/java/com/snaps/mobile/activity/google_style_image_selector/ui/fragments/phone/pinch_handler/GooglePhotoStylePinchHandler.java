package com.snaps.mobile.activity.google_style_image_selector.ui.fragments.phone.pinch_handler;

import android.graphics.Color;
import android.graphics.Rect;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.customview.SnapsSuperRecyclerView;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.GooglePhotoStyleAnimViewsMapInfo;
import com.snaps.mobile.activity.google_style_image_selector.datas.GooglePhotoStyleAnimationHolder;
import com.snaps.mobile.activity.google_style_image_selector.datas.GooglePhotoStyleAnimationHolderSet;
import com.snaps.mobile.activity.google_style_image_selector.datas.GoogleStyleAnimationAttribute;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectAnimationFinishListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectPhonePhotoAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.GooglePhotoStyleAnimationView;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.SquareRelativeLayout;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.phone.GooglePhotoStylePhoneFragmentProcessor;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by ysjeong on 2017. 1. 3..
 */
public class GooglePhotoStylePinchHandler extends UIPinchMotionDetector {
    private static final String TAG = GooglePhotoStylePinchHandler.class.getSimpleName();

    private static final float ANIMATION_START_OFFSET_FACTOR = .2f;

    private final int RECYCLER_VIEW_DECORATION_VIEW_HEIGHT;

    /**
     * 애니메이션 처리는 GooglePhotoStyleAnimationView 에서..
     */
    private Set<GooglePhotoStyleAnimationView> animationViewSets = null;

    private Map<String, GooglePhotoStyleAnimationHolder> mapCurrentHolders = null;

    private Map<String, GooglePhotoStyleAnimationHolder> mapPrevTargetHolders = null;
    private Map<String, GooglePhotoStyleAnimationHolder> mapNextTargetHolders = null;

    private Map<String, GooglePhotoStyleAnimationHolderSet> mapMergedAnimationHolders = null;

    private GooglePhotoStyleAnimViewsMapInfo mapInfo = null;

    private boolean isAnimationFinished = true; //애미메이션이 완전히 종료 되었는지

    private eGOOGLE_STYLE_DEPTH animationTargetUIDepth = null;
    private View decorationHeaderView;
    private int currentRecyclerViewDecorationViewHeight = 0;

    public GooglePhotoStylePinchHandler(ImageSelectActivityV2 activity, GooglePhotoStylePhoneFragmentProcessor processor) {
        super(activity, processor);

        this.mapInfo = new GooglePhotoStyleAnimViewsMapInfo();
        this.animationViewSets = new HashSet<>();
        this.RECYCLER_VIEW_DECORATION_VIEW_HEIGHT = UIUtil.convertDPtoPX(activity, 8);
    }

    @Override
    public void releaseInstance() {
        super.releaseInstance();

        if (mapCurrentHolders != null) {
            mapCurrentHolders.clear();
            mapCurrentHolders = null;
        }

        if (mapNextTargetHolders != null) {
            mapNextTargetHolders.clear();
            mapNextTargetHolders = null;
        }

        if (mapPrevTargetHolders != null) {
            mapPrevTargetHolders.clear();
            mapPrevTargetHolders = null;
        }

        if (mapMergedAnimationHolders != null) {
            mapMergedAnimationHolders.clear();
            mapMergedAnimationHolders = null;
        }

        if (mapInfo != null) {
            mapInfo.clear();
            mapInfo = null;
        }

        recycleAnimationBitmaps(); //리사이클을 시키고 clear 할 것. (순서 바꾸면 안됨)

        if (animationViewSets != null) {
            animationViewSets.clear();
            animationViewSets = null;
        }
    }

    public void recycleAnimationBitmaps() {
        if (animationViewSets == null)  return;
        for (GooglePhotoStyleAnimationView animationView : animationViewSets) {
            if (animationView != null) {
                animationView.setSuspended(true);

                if (animationView.getCurrentImageView() != null)
                    ImageLoader.clear(activity, animationView.getCurrentImageView());

                if (animationView.getTargetImageView() != null)
                    ImageLoader.clear(activity, animationView.getTargetImageView());

                //TODO  만약 리사이클 비트맵 사용으로 인해 오류가 발생한다면 아래 코드 삭제..
                try {
                    ViewUnbindHelper.unbindReferences(animationView.getCurrentHolderView(), null, false);

                    ViewUnbindHelper.unbindReferences(animationView.getTargetHolderView(), null, false);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        }
    }

    private int[] getViewOffsetOnScreen(View measureViewOffsetOnScreen) {
        if (measureViewOffsetOnScreen == null) return null;
        int[] targetViewLocation = new int[2];
        measureViewOffsetOnScreen.getLocationOnScreen(targetViewLocation);
        return targetViewLocation;
    }

    //Pinch 타켓의 offset이 안 맞는 경우가 있다. 이럴 경우는 firstVisibleItem끼리 맞춰준다.
    private SquareRelativeLayout checkValidOffsetInterval(SquareRelativeLayout targetView, eMOTION_STATE motionType) {
        if (targetView == null) return null;

        int[] currentViewLocation = getViewOffsetOnScreen(getCurrentAnimationOffsetView());
        int[] targetViewLocation = getViewOffsetOnScreen(targetView);

        if (currentViewLocation == null || targetViewLocation == null) return null;

        int offsetYInterval = Math.abs(targetViewLocation[1] - currentViewLocation[1]);
        if (offsetYInterval > VALID_OFFSET_INTERVAL) {
            targetView = findTargetViewByRecyclerViewFirstVisiblePosition(motionType);
        }

        return targetView;
    }

    private void showAnimationLayout(boolean isShow) {
        //현재 recyclerView 감춤.
        final SnapsSuperRecyclerView currentRecyclerView = getCurrentRecyclerView();
        if (currentRecyclerView != null) {
            currentRecyclerViewDecorationViewHeight = Math.min(RECYCLER_VIEW_DECORATION_VIEW_HEIGHT, (RECYCLER_VIEW_DECORATION_VIEW_HEIGHT - getCurrentRecyclerViewComputeVerticalScrollOffset()));
            currentRecyclerView.setVisibility(isShow ? View.INVISIBLE : View.VISIBLE);
        }

        //이전 애니메이션이 혹시라도 남아 있다면, 제거
        clearPrevAnimationViews();

        //애니메이션 뷰 보여줌.
        FrameLayout animationLayout = getAnimationLayout();
        if (animationLayout != null) {
            animationLayout.setVisibility(isShow ? View.VISIBLE : View.GONE);

            if (currentRecyclerViewDecorationViewHeight > 0) {
                decorationHeaderView = new View(activity);
                decorationHeaderView.setLayoutParams(new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, currentRecyclerViewDecorationViewHeight));
                decorationHeaderView.setBackgroundColor(Color.parseColor("#fff4f4f4"));
                animationLayout.addView(decorationHeaderView);
            }
        }
    }

    private boolean startAnimationByPinchGesture(float scaleFactor, eMOTION_STATE motionState) {
        if (getMotionState() == motionState || !isFindScaleOffsetView() || isActiveAnimation() || isPhotoDataConverting()) return false;

        if (Math.abs(1.f - scaleFactor) < ANIMATION_START_OFFSET_FACTOR) return false;

        SquareRelativeLayout targetOffsetView =
                motionState == eMOTION_STATE.SCALE_PREV_DEPTH ? getPrevTargetAnimationOffsetView() : getNextTargetAnimationOffsetView();

        if (targetOffsetView == null) {
            scrollToPositionWithTargetOffset(motionState);
        }

        if (targetOffsetView != null)
            targetOffsetView = checkValidOffsetInterval(targetOffsetView, motionState);

        if (targetOffsetView == null) {
            return false;
        }

        if (System.currentTimeMillis() - lastScrollToPositionWithOffsetTime < SCROLL_TO_OFFSET_DELAY_TIME) { //scrollToPositionWithOffset가 동작할 수 있는 시간을 벌어 준다.
            return false;
        }

        int position = targetOffsetView.getArrIdxOnAdapter();
        if (position < 0) {
            return false; //실패
        }

        setMotionState(motionState);

        showAnimationLayout(true);

        if (tryDrawAnimationViews(motionState)) {
            setLastMotionHistory(motionState);
            startAnimation(motionState);
        } else {
            showAnimationLayout(false);
        }

        return true;
    }

    @Override
    public boolean scalePrevDepth(float scaleFactor) {
        return startAnimationByPinchGesture(scaleFactor, eMOTION_STATE.SCALE_PREV_DEPTH);
    }

    @Override
    public boolean scaleNextDepth(float scaleFactor) {
        return startAnimationByPinchGesture(scaleFactor, eMOTION_STATE.SCALE_NEXT_DEPTH);
    }

    public boolean isActiveAnimation() {
        return !isAnimationFinished;
    }

    private void startAnimation(final UIPinchMotionDetector.eMOTION_STATE motionType) {
        final eGOOGLE_STYLE_DEPTH targetDepth = motionType == UIPinchMotionDetector.eMOTION_STATE.SCALE_NEXT_DEPTH ? getNextTargetDepth() : getPrevTargetDepth();

        if (animationViewSets == null) return;

        removeDateScrollBar();
        setLockPinchMotion(true);
        setDataScrollBarLock();
        isAnimationFinished = false;
        animationTargetUIDepth = targetDepth;

        boolean isYearUIDepth = (motionType == eMOTION_STATE.SCALE_PREV_DEPTH && targetDepth == eGOOGLE_STYLE_DEPTH.DEPTH_YEAR) //년도는 사진이 많아서 버벅임이 있다.
                || (motionType == eMOTION_STATE.SCALE_NEXT_DEPTH && getCurrentDapth() == eGOOGLE_STYLE_DEPTH.DEPTH_YEAR);

        //애니메이션 시작
        for (GooglePhotoStyleAnimationView animationView : animationViewSets) {
            if (animationView == null) continue;

            animationView.startAnimation(animationFinishListener, isYearUIDepth);
        }
    }

    private IImageSelectAnimationFinishListener animationFinishListener = new IImageSelectAnimationFinishListener() {
        @Override
        public void onFinishAnimation() {
            if (!isAnimationFinished) {
                isAnimationFinished = true;

                if (getGooglePhotoStyleUIProcessor() != null && animationTargetUIDepth != null) {
                    getGooglePhotoStyleUIProcessor().changeUIDepth(animationTargetUIDepth);
                }

                if (snapsHandler != null)
                    snapsHandler.sendEmptyMessageDelayed(HANDLER_MSG_CLEAR_ANIMATIONS, 300);
            }
        }

        @Override
        public void onProgressAnimation(float value) {
            if (decorationHeaderView != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) decorationHeaderView.getLayoutParams();
                layoutParams.height = (int) (currentRecyclerViewDecorationViewHeight * (1 - value));
                decorationHeaderView.setLayoutParams(layoutParams);
                decorationHeaderView.setAlpha(1 - value);
            }
        }
    };

    @Override
    protected void clearAnimations() {

        recycleAnimationBitmaps();

        if (mapInfo != null)
            mapInfo.clear();

        if (animationViewSets != null)
            animationViewSets.clear();
        if (mapCurrentHolders != null)
            mapCurrentHolders.clear();
        if (mapNextTargetHolders != null)
            mapNextTargetHolders.clear();

        if (getAnimationLayout() != null) {
            getAnimationLayout().removeAllViews();
            getAnimationLayout().setVisibility(View.GONE);
        }

        setLockPinchMotion(false);
    }

    // 현재 보여지고 있는 뷰들의 위치와 크기 정보를 map형태로 생성한다.
    private Map<String, GooglePhotoStyleAnimationHolder> makeAnimationHolders(GridLayoutManager layoutManager, ImageSelectPhonePhotoAdapter adapter) {

        Map<String, GooglePhotoStyleAnimationHolder> mapHolders = new LinkedHashMap<>();
        GooglePhotoStyleAnimViewsMapInfo animationViewMapInfo = new GooglePhotoStyleAnimViewsMapInfo();

        int firstVisiblePosition = Math.max(0, layoutManager.findFirstVisibleItemPosition() - 1);
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition() - 1;

        for (int ii = firstVisiblePosition; ii <= lastVisibleItemPosition; ii++) {
            GalleryCursorRecord.PhonePhotoFragmentItem photoItem = adapter.getPhotoItem(ii);
            if (photoItem == null) continue;

            putAnimationViewMapInfo(mapHolders, photoItem, animationViewMapInfo);
        }

        return mapHolders;
    }

    /**
     *
     * @param mapHolders : 아이템의 좌표와 Holder를 담는다.
     * @param photoItem : adapter 에 보여지고 있는 각 아이템
     * @param animationViewsMapInfo : row, column 처리를 위한..
     */
    private void putAnimationViewMapInfo(Map<String, GooglePhotoStyleAnimationHolder> mapHolders,
                                         GalleryCursorRecord.PhonePhotoFragmentItem photoItem,
                                         GooglePhotoStyleAnimViewsMapInfo animationViewsMapInfo) {
        if (mapHolders == null || animationViewsMapInfo == null || photoItem == null) return;

        RecyclerView.ViewHolder viewHolder = photoItem.getViewHolder();
        if (viewHolder == null) return;

        View parentView = null;
        eGOOGLE_STYLE_HOLDER_TYPE holderType = photoItem.getHolderType();

        if (viewHolder instanceof ImageSelectAdapterHolders.GooglePhotoStyleDepthYearsYearSectionHolder) {
            ImageSelectAdapterHolders.GooglePhotoStyleDepthYearsYearSectionHolder sectionHolder = (ImageSelectAdapterHolders.GooglePhotoStyleDepthYearsYearSectionHolder) viewHolder;
            parentView = sectionHolder.getParentLayout();
        } else if (viewHolder instanceof ImageSelectAdapterHolders.GooglePhotoStyleDepthYearsMonthSectionHolder) {
            ImageSelectAdapterHolders.GooglePhotoStyleDepthYearsMonthSectionHolder sectionHolder = (ImageSelectAdapterHolders.GooglePhotoStyleDepthYearsMonthSectionHolder) viewHolder;
            parentView = sectionHolder.getParentLayout();
        } else if (viewHolder instanceof ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder) {
            ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder sectionHolder = (ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder) viewHolder;
            parentView = sectionHolder.getParentLayout();
        } else if (viewHolder instanceof ImageSelectAdapterHolders.PhotoFragmentItemHolder) {
            ImageSelectAdapterHolders.PhotoFragmentItemHolder photoFragmentItemHolder = (ImageSelectAdapterHolders.PhotoFragmentItemHolder) viewHolder;
            parentView = photoFragmentItemHolder.getParentView();
        }

        if (parentView == null) return;

        int[] offsetViewLocation = new int[2];
        parentView.getLocationOnScreen(offsetViewLocation);

        int currentX = offsetViewLocation[0];
        int currentY = offsetViewLocation[1] - getRecyclerViewBaseOffsetY();

        if (currentY != animationViewsMapInfo.getPrevY()) {
            animationViewsMapInfo.addRow();
            animationViewsMapInfo.setColumn(0);
            animationViewsMapInfo.setPrevY(currentY);
            animationViewsMapInfo.setPrevX(Integer.MIN_VALUE);
        } else if (currentX != animationViewsMapInfo.getPrevX()) {
            animationViewsMapInfo.addColumn();
            animationViewsMapInfo.setPrevX(currentX);
        }

        GooglePhotoStyleAnimationHolder animationHolder = new GooglePhotoStyleAnimationHolder();
        animationHolder.setViewHolder(viewHolder);
        animationHolder.setParentView(parentView);
        animationHolder.setHolderType(holderType);

        int viewWidth = parentView.getMeasuredWidth();
        int viewHeight = parentView.getMeasuredHeight();

        animationHolder.setViewRect(new Rect(currentX, currentY, currentX + viewWidth, currentY + viewHeight));

        if (photoItem.getUiDepth() == getCurrentDapth()) {
            GalleryCursorRecord.PhonePhotoFragmentItem item = getCurrentAnimationOffsetItem();
            if (item != null && item.getPhoneDetailId() == photoItem.getPhoneDetailId()) {
                setCurrentAnimationOffsetRow(animationViewsMapInfo.getRow());
            }
        } else if (photoItem.getUiDepth() == getNextTargetDepth()) {
            GalleryCursorRecord.PhonePhotoFragmentItem item = getNextTargetAnimationOffsetItem();
            if (item != null && item.getPhoneDetailId() == photoItem.getPhoneDetailId()) {
                setNextTargetAnimationOffsetRow(animationViewsMapInfo.getRow());
            }
        } else if (photoItem.getUiDepth() == getPrevTargetDepth()) {
            GalleryCursorRecord.PhonePhotoFragmentItem item = getPrevTargetAnimationOffsetItem();
            if (item != null && item.getPhoneDetailId() == photoItem.getPhoneDetailId()) {
                setPrevTargetAnimationOffsetRow(animationViewsMapInfo.getRow());
            }
        }

        mapHolders.put(ImageSelectUtils.getHolderKeyByRowAndColumn(animationViewsMapInfo.getRow(), animationViewsMapInfo.getColumn()),
                animationHolder);
    }

    //애니메이션 뷰들이 위치한 좌표를 계산해서 mapCurrentHolders, mapTargetHolders에 담아준다.
    @Override
    public void tryCalculateAnimationViewsOffset(UIPinchMotionDetector.eMOTION_STATE motionType) {

        clearMapInfo();

        SnapsSuperRecyclerView currentRecyclerView = getCurrentRecyclerView();
        ImageSelectPhonePhotoAdapter currentAdapter = getCurrentAdapter();
        if (currentAdapter != null) {
            GridLayoutManager currentLayoutManager = (GridLayoutManager) currentRecyclerView.getLayoutManager();
            setMapCurrentHolders(makeAnimationHolders(currentLayoutManager, currentAdapter));
        }

        if (motionType == eMOTION_STATE.SCALE_NEXT_DEPTH) {
            ImageSelectPhonePhotoAdapter nextTargetAdapter = getNextTargetAdapter();
            SnapsSuperRecyclerView nextTargetRecyclerView = getNextTargetRecyclerView();
            if (nextTargetRecyclerView != null) {
                GridLayoutManager nextTargetLayoutManager = (GridLayoutManager) nextTargetRecyclerView.getLayoutManager();
                setMapNextTargetHolders(makeAnimationHolders(nextTargetLayoutManager, nextTargetAdapter));
            }
        } else if (motionType == eMOTION_STATE.SCALE_PREV_DEPTH) {
            ImageSelectPhonePhotoAdapter prevTargetAdapter = getPrevTargetAdapter();
            SnapsSuperRecyclerView prevTargetRecyclerView = getPrevTargetRecyclerView();
            if (prevTargetRecyclerView != null) {
                GridLayoutManager prevTargetLayoutManager = (GridLayoutManager) prevTargetRecyclerView.getLayoutManager();
                setMapPrevTargetHolders(makeAnimationHolders(prevTargetLayoutManager, prevTargetAdapter));
            }
        }
    }

    //holder들의 영역 계산이 정상적으로 완료 되었는지 검사
    private boolean isPreparedMapHolder(UIPinchMotionDetector.eMOTION_STATE motionType) {
        switch (motionType) {
            case SCALE_NEXT_DEPTH:
                return mapInfo != null && mapInfo.getCurrentAnimationOffsetRow() >= 0 && mapCurrentHolders != null && !mapCurrentHolders.isEmpty() && mapNextTargetHolders != null && !mapNextTargetHolders.isEmpty();
            case SCALE_PREV_DEPTH:
                return mapInfo != null && mapInfo.getCurrentAnimationOffsetRow() >= 0 && mapCurrentHolders != null && !mapCurrentHolders.isEmpty() && mapPrevTargetHolders != null && !mapPrevTargetHolders.isEmpty();
        }
        return false;
    }

    //mapHolders 에 그린 맵에 해당하는 row가 섹션인지 검사
    private int checkSectionRow(int row, Map<String, GooglePhotoStyleAnimationHolder> mapHolders) {
        String currentOffsetRowKey = ImageSelectUtils.getHolderKeyByRowAndColumn(row, 0);
        if (currentOffsetRowKey == null)
            return ROW_TYPE_CHECK_RESULT_NONE;

        GooglePhotoStyleAnimationHolder holder = mapHolders.get(currentOffsetRowKey);
        if (holder == null) return ROW_TYPE_CHECK_RESULT_NONE;

        return holder.isSectionHolder() ?ROW_TYPE_CHECK_RESULT_SECTION : ROW_TYPE_CHECK_RESULT_THUMBNAIL;
    }

    private boolean addAllAnimationViewsByMergedAnimationHolder(UIPinchMotionDetector.eMOTION_STATE motionType) {
        if (mapMergedAnimationHolders == null || mapMergedAnimationHolders.isEmpty()) return false;

        Set<String> keySet = mapMergedAnimationHolders.keySet();

        for (String key : keySet) {
            if (key == null) continue;

            GooglePhotoStyleAnimationHolderSet holderSet = mapMergedAnimationHolders.get(key);
            addAnimationView(motionType, holderSet);
        }

        return true;
    }

    private int getSectionLayoutResIdBySectionType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE holderType) {
        if (holderType == null) return -1;
        switch (holderType) {
            case HOLDER_TYPE_YEAR_SECTION: return R.layout.google_photo_style_section_for_depth_01_year_item;
            case HOLDER_TYPE_MONTH_SECTION: return R.layout.google_photo_style_section_for_depth_01_month_item;
            default: return R.layout.google_photo_style_section_item;
        }
    }

    private void addAnimationSectionView(int row, GooglePhotoStyleAnimationHolder startAnimationHolder, GooglePhotoStyleAnimationHolder targetAnimationHolder) {
        if (startAnimationHolder == null || targetAnimationHolder == null) return;
        GoogleStyleAnimationAttribute animationAttribute = new GoogleStyleAnimationAttribute();
        animationAttribute.setStartViewRect(startAnimationHolder.getViewRect());
        animationAttribute.setTargetViewRect(targetAnimationHolder.getViewRect());
        View currentHolderView = null;
        View targetHolderView = null;

        int layoutId = getSectionLayoutResIdBySectionType(startAnimationHolder.getHolderType());
        if (layoutId < 0) return;

        currentHolderView = LayoutInflater.from(activity).inflate(layoutId, null, false);
        animationAttribute.setStartHolderView(currentHolderView);

        layoutId = getSectionLayoutResIdBySectionType(targetAnimationHolder.getHolderType());
        if (layoutId < 0) return;

        targetHolderView = LayoutInflater.from(activity).inflate(layoutId, null, false);
        animationAttribute.setTargetHolderView(targetHolderView);

        ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder currentViewHolder = (ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder) startAnimationHolder.getViewHolder();
        ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder targetViewHolder = (ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder) targetAnimationHolder.getViewHolder();

        animationAttribute.setStartViewRect(startAnimationHolder.getViewRect());
        animationAttribute.setTargetViewRect(targetAnimationHolder.getViewRect());

        makeAnimationSection(true, animationAttribute, currentViewHolder);
        makeAnimationSection(false, animationAttribute, targetViewHolder);

        animationAttribute.setAnimationObjectType(startAnimationHolder.getAnimationObjectType());

        addAnimationSet(row, animationAttribute);
    }

    //섹션 뷰 생성
    private void makeAnimationSection(boolean isStartAnimView, GoogleStyleAnimationAttribute animationAttribute, ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder sectionItem) {
        if (sectionItem == null || animationAttribute == null) return;

        View holderView = isStartAnimView ? animationAttribute.getStartHolderView() : animationAttribute.getTargetHolderView();
        if (holderView == null) return;

        ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder itemHolder = new ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder(holderView);

        TextView tvSectionTitle = itemHolder.getTvSectionTitle();
        if (tvSectionTitle != null) {
            String text = sectionItem.getTvSectionTitle() != null ? sectionItem.getTvSectionTitle().getText().toString() : "";
            tvSectionTitle.setText(text);
        }

        TextView tvSubTitle = itemHolder.getTvSectionSub();
        if (tvSubTitle != null) {
            String text = sectionItem.getTvSectionSub() != null ? sectionItem.getTvSectionSub().getText().toString() : "";
            tvSubTitle.setText(text);
        }

        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (isStartAnimView ? animationAttribute.getStartHeight() : animationAttribute.getTargetHeight()));
        holderView.setLayoutParams(linearLayoutParams);
    }

    //썸네일 뷰 생성
    private void makeAnimationThumbnail(boolean isStartAnimView, GoogleStyleAnimationAttribute animationAttribute, ImageSelectAdapterHolders.PhotoFragmentItemHolder photoItemHolder, UIPinchMotionDetector.eMOTION_STATE motionType) {
        if (photoItemHolder == null || animationAttribute == null) return;

        ImageSelectAdapterHolders.PhotoFragmentItemHolder itemHolder = new ImageSelectAdapterHolders.PhotoFragmentItemHolder(isStartAnimView ? animationAttribute.getStartHolderView() : animationAttribute.getTargetHolderView());

        //년도 단위는 체크박스가 없다.
        boolean isYearDepth = (motionType == eMOTION_STATE.SCALE_PREV_DEPTH && getPrevTargetDepth() == eGOOGLE_STYLE_DEPTH.DEPTH_YEAR)
                                || ((motionType == eMOTION_STATE.SCALE_NEXT_DEPTH && getCurrentDapth() == eGOOGLE_STYLE_DEPTH.DEPTH_YEAR));

        if (!isYearDepth) {
            ImageView ivCheckIcon = itemHolder.getCheckIcon();
            if (ivCheckIcon != null) {
                if (ImageSelectUtils.isContainsInImageHolder(photoItemHolder.getMapKey())) {
                    ivCheckIcon.setImageResource(R.drawable.img_image_select_fragment_checked);
                    ivCheckIcon.setVisibility(View.VISIBLE);
                } else {
                    ivCheckIcon.setImageResource(0);
                    ivCheckIcon.setVisibility(View.GONE);
                }
            }

            ImageView ivUnderPixel = itemHolder.getNoPrintIcon();
            if (ivUnderPixel != null) {
                if (photoItemHolder.isDisableClick()) {
                    ivUnderPixel.setImageResource(R.drawable.img_tray_noprint_icon);
                    ivUnderPixel.setVisibility(View.VISIBLE);
                } else {
                    ivUnderPixel.setImageResource(0);
                    ivUnderPixel.setVisibility(View.GONE);
                }
            }

            ImageView ivSelector = itemHolder.getSelector();
            if (ivSelector != null) {
                if (ImageSelectUtils.isContainsInImageHolder(photoItemHolder.getMapKey()))  {
                    ivSelector.setBackgroundResource(R.drawable.shape_red_e36a63_fill_solid_border_rect);
                    ivSelector.setVisibility(View.VISIBLE);
                    animationAttribute.setSelected(true);
                } else if (photoItemHolder.isDisableClick()) {
                    ivSelector.setBackgroundResource(R.drawable.shape_none_line_fill_solid_border_rect);
                    ivSelector.setVisibility(View.VISIBLE);
                    animationAttribute.setSelected(true);
                } else {
                    ivSelector.setBackgroundResource(0);
                    ivSelector.setVisibility(View.GONE);
                }
            }
        }

        int width = animationAttribute.getStartWidth();
        int height = animationAttribute.getStartHeight();

        ImageView thumbnailImageView = itemHolder.getThumbnail();
        if (thumbnailImageView != null) {
            RelativeLayout.LayoutParams imageViewLayoutParams = new RelativeLayout.LayoutParams(width, height);
            thumbnailImageView.setLayoutParams(imageViewLayoutParams);

            if (isStartAnimView) {
                animationAttribute.setStartImageView(thumbnailImageView);
                if (photoItemHolder.getPhonePhotoItem() != null && photoItemHolder.getPhonePhotoItem().getPhotoInfo() != null) {
                    animationAttribute.setStartImagePath(photoItemHolder.getPhonePhotoItem().getPhotoInfo().getThumbnailPath());
                    if (!StringUtil.isEmpty(animationAttribute.getStartImagePath())) {
                        animationAttribute.setStartImageSize(getOptimumThumbnailSize(motionType));

                        ImageView.ScaleType scaleType = getCurrentDapth() == eGOOGLE_STYLE_DEPTH.DEPTH_STAGGERED ? ImageView.ScaleType.FIT_XY : ImageView.ScaleType.CENTER_CROP;
                        thumbnailImageView.setScaleType(scaleType);

                        ImageSelectUtils.loadImage(activity, animationAttribute.getStartImagePath(), animationAttribute.getStartImageSize(), thumbnailImageView, scaleType);
                    }
                }
            } else {
                animationAttribute.setTargetImageView(thumbnailImageView);
                if (photoItemHolder.getPhonePhotoItem() != null && photoItemHolder.getPhonePhotoItem().getPhotoInfo() != null) {
                    animationAttribute.setTargetImagePath(photoItemHolder.getPhonePhotoItem().getPhotoInfo().getThumbnailPath());
                    if (!StringUtil.isEmpty(animationAttribute.getTargetImagePath())) {
                        animationAttribute.setTargetImageSize(getOptimumThumbnailSize(motionType));

                        eGOOGLE_STYLE_DEPTH targetUIDepth = motionType == eMOTION_STATE.SCALE_PREV_DEPTH ? getPrevTargetDepth() : getNextTargetDepth();
                        ImageView.ScaleType scaleType = targetUIDepth == eGOOGLE_STYLE_DEPTH.DEPTH_STAGGERED ? ImageView.ScaleType.FIT_XY : ImageView.ScaleType.CENTER_CROP;
                        thumbnailImageView.setScaleType(scaleType);

                        ImageSelectUtils.loadImage(activity, animationAttribute.getTargetImagePath(), animationAttribute.getTargetImageSize(), thumbnailImageView, scaleType);
                    }
                }
            }
        }
    }

    private int getOptimumThumbnailSize(UIPinchMotionDetector.eMOTION_STATE motionType) {
        int targetImageSize = 0;
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            targetImageSize = imageSelectManager.getCurrentUIDepthThumbnailSize();
        }

        if ((motionType == eMOTION_STATE.SCALE_NEXT_DEPTH && getCurrentDapth() == eGOOGLE_STYLE_DEPTH.DEPTH_YEAR)
                || (motionType == eMOTION_STATE.SCALE_PREV_DEPTH && getPrevTargetDepth() == eGOOGLE_STYLE_DEPTH.DEPTH_YEAR)) {
            targetImageSize = ImageSelectUtils.getUIDepthOptimumThumbnailDimension(activity, eGOOGLE_STYLE_DEPTH.DEPTH_YEAR, activity.isLandScapeMode());
        }
        return targetImageSize;
    }

    private void addAnimationThumbnailView(int row,
                                           UIPinchMotionDetector.eMOTION_STATE motionType,
                                           GooglePhotoStyleAnimationHolder startAnimationHolder,
                                           GooglePhotoStyleAnimationHolder targetAnimationHolder) {
        if (startAnimationHolder == null || targetAnimationHolder == null) return;

        ImageSelectAdapterHolders.PhotoFragmentItemHolder currentViewHolder = (ImageSelectAdapterHolders.PhotoFragmentItemHolder) startAnimationHolder.getViewHolder();
        ImageSelectAdapterHolders.PhotoFragmentItemHolder targetViewHolder = (ImageSelectAdapterHolders.PhotoFragmentItemHolder) targetAnimationHolder.getViewHolder();

        GoogleStyleAnimationAttribute animationAttribute = new GoogleStyleAnimationAttribute();
        animationAttribute.setStartViewRect(startAnimationHolder.getViewRect());
        animationAttribute.setTargetViewRect(targetAnimationHolder.getViewRect());

        animationAttribute.setStartHolderView(getInflaterView());
        animationAttribute.setTargetHolderView(getInflaterView());

        makeAnimationThumbnail(true, animationAttribute, currentViewHolder, motionType); //startAnimThumbnail
        makeAnimationThumbnail(false, animationAttribute, targetViewHolder, motionType); //targetAnimThumbnail

        animationAttribute.setDummyStartView(startAnimationHolder.isDummyStartView() || targetAnimationHolder.isDummyStartView());
        animationAttribute.setDummyTargetView(startAnimationHolder.isDummyTargetView() || targetAnimationHolder.isDummyTargetView());

        addAnimationSet(row, animationAttribute);
    }

    private void addAnimationSet(int row, GoogleStyleAnimationAttribute animationAttribute) {
        if (animationAttribute == null) return;
        GooglePhotoStyleAnimationView animationView = new GooglePhotoStyleAnimationView.Builder(activity)
                .setCurrentHolderView(animationAttribute.getStartHolderView())
                .setCurrentOffset(animationAttribute.getStartViewRect())
                .setTargetHolderView(animationAttribute.getTargetHolderView())
                .setCurrentImageView(animationAttribute.getStartImageView())
                .setTargetImageView(animationAttribute.getTargetImageView())
                .setTargetOffset(animationAttribute.getTargetViewRect())
                .setCurrentImagePath(animationAttribute.getStartImagePath())
                .setCurrentImageSize(animationAttribute.getStartImageSize())
                .setTargetImagePath(animationAttribute.getTargetImagePath())
                .setTargetImageSize(animationAttribute.getTargetImageSize())
                .setTwoSections(animationAttribute.getAnimationObjectType() == eANIMATION_OBJECT_TYPE.BOTH)
                .setCurrentViewSection(animationAttribute.getAnimationObjectType() == eANIMATION_OBJECT_TYPE.CURRENT)
                .setTargetViewSection(animationAttribute.getAnimationObjectType() == eANIMATION_OBJECT_TYPE.TARGET)
                .setPivotY(row < 0 ? 1 : 0)
                .setDummyStartView(animationAttribute.isDummyStartView())
                .setDummyTargetView(animationAttribute.isDummyTargetView())
                .setSelected(animationAttribute.isSelected())
                .create();

        if (getFrontView() != null && animationView.getContainerLayout() != null) {
            FrameLayout animationLayout = getFrontView().getAnimationLayout();
            if (animationLayout != null) {
                animationLayout.addView(animationView.getContainerLayout());
            }
        }

        animationViewSets.add(animationView);
    }

    private void addAnimationView(UIPinchMotionDetector.eMOTION_STATE motionType, GooglePhotoStyleAnimationHolderSet holderSet) {
        if (holderSet == null) return;

        GooglePhotoStyleAnimationHolder startAnimationHolder = holderSet.getStartAnimationHolder();
        GooglePhotoStyleAnimationHolder targetAnimationHolder = holderSet.getTargetAnimationHolder();

        if (startAnimationHolder == null || targetAnimationHolder == null) {
            return;
        }

        if (startAnimationHolder.isSectionHolder() && targetAnimationHolder.isSectionHolder()) { //섹션은 어차피 섹션끼리 짝이다.
            addAnimationSectionView(holderSet.getRow(), startAnimationHolder, targetAnimationHolder);
        } else {

            if (startAnimationHolder.isSectionHolder() || targetAnimationHolder.isSectionHolder()) {
                return;
            }

            addAnimationThumbnailView(holderSet.getRow(), motionType, startAnimationHolder, targetAnimationHolder);
        }
    }

    private void clearPrevAnimationViews() {
        FrameLayout animationLayout = getAnimationLayout();
        if (animationLayout != null) {
            animationLayout.removeAllViews();
        }
    }

    private GooglePhotoStyleAnimationHolder copyHolderFromPrevColumn(GooglePhotoStyleAnimationHolder copyObjectHolder, GooglePhotoStyleAnimationHolder orgHolder) {
        if (copyObjectHolder == null) return null;

        GooglePhotoStyleAnimationHolder copiedHolder = new GooglePhotoStyleAnimationHolder();

        Rect targetColumnViewRect = copyObjectHolder.getViewRect();
        if (targetColumnViewRect != null) {
            int leftTotalMargin = getFragmentItemMargin();
            int offsetX = targetColumnViewRect.left + targetColumnViewRect.width() + leftTotalMargin;
            Rect targetViewRect = new Rect(offsetX, targetColumnViewRect.top, offsetX + targetColumnViewRect.width(), (targetColumnViewRect.top + targetColumnViewRect.height()));
            copiedHolder.setViewRect(targetViewRect);
            copiedHolder.setViewHolder(orgHolder.getViewHolder());

            copiedHolder.setHolderType(eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_THUMBNAIL);
        }

        return copiedHolder;
    }

    private void putSectionAnimationHolder(eANIMATION_OBJECT_TYPE animationObjectType, Map<String, GooglePhotoStyleAnimationHolder> targetHolders, int currentRow, int targetRow, int putRow) {
        if (mapInfo == null || mapCurrentHolders == null) return;

        String currentRowKey = ImageSelectUtils.getHolderKeyByRowAndColumn(currentRow, 0);

        GooglePhotoStyleAnimationHolder currentAnimationHolder = mapCurrentHolders.get(currentRowKey);
        GooglePhotoStyleAnimationHolder targetAnimationHolder = null;

        if (currentAnimationHolder == null) return;

        switch (animationObjectType) {
            case BOTH: {
                String targetRowKey = ImageSelectUtils.getHolderKeyByRowAndColumn(targetRow, 0);
                targetAnimationHolder = targetHolders.get(targetRowKey);
                currentAnimationHolder.setAnimationObjectType(animationObjectType);
                targetAnimationHolder.setAnimationObjectType(animationObjectType);
                break;
            }
            case CURRENT: {
                targetAnimationHolder = new GooglePhotoStyleAnimationHolder();
                Rect currentHolderRect = currentAnimationHolder.getViewRect();
                if (currentHolderRect != null) {
                    Rect targetViewRect = new Rect(currentHolderRect.left,
                            currentHolderRect.bottom, currentHolderRect.left + currentHolderRect.width(), currentHolderRect.bottom);
                    targetAnimationHolder.setViewRect(targetViewRect);
                    targetAnimationHolder.setHolderType(currentAnimationHolder.getHolderType());
                    currentAnimationHolder.setAnimationObjectType(eANIMATION_OBJECT_TYPE.CURRENT);
                    targetAnimationHolder.setAnimationObjectType(eANIMATION_OBJECT_TYPE.CURRENT);
                }
                break;
            }
            case TARGET: {
                String targetRowKey = ImageSelectUtils.getHolderKeyByRowAndColumn(targetRow, 0);
                targetAnimationHolder = targetHolders.get(targetRowKey);
                currentAnimationHolder = new GooglePhotoStyleAnimationHolder();
                Rect targetHolderRect = targetAnimationHolder.getViewRect();
                if (targetHolderRect != null) {
                    Rect currentViewRect = new Rect(targetHolderRect.left,
                            targetHolderRect.bottom, targetHolderRect.left + targetHolderRect.width(), targetHolderRect.bottom);
                    currentAnimationHolder.setViewRect(currentViewRect);
                    currentAnimationHolder.setHolderType(targetAnimationHolder.getHolderType());
                    currentAnimationHolder.setAnimationObjectType(eANIMATION_OBJECT_TYPE.TARGET);
                    targetAnimationHolder.setAnimationObjectType(eANIMATION_OBJECT_TYPE.TARGET);
                }
                break;
            }
        }

        GooglePhotoStyleAnimationHolderSet animationHolderSet = new GooglePhotoStyleAnimationHolderSet(currentAnimationHolder, targetAnimationHolder, 0);
        String mergedKey = ImageSelectUtils.getHolderKeyByRowAndColumn(putRow, 0); //기준 row가 0이고 위로는 -1, -2... 아래로는 1, 2.....
        targetAnimationHolder.setAnimationObjectType(animationObjectType);
        mapMergedAnimationHolders.put(mergedKey, animationHolderSet);
    }

    private void putAllColumnsMergedAnimationHolder(SparseIntArray targetMapColumnCountInfo, Map<String, GooglePhotoStyleAnimationHolder> targetHolders, int currentRow, int targetRow, int putRow) {
        SparseIntArray currentMapColumnCountInfo = mapInfo.getCurrentMapColumnCountInfo();

        int currentColumnCount = currentMapColumnCountInfo.get(currentRow);
        int targetColumnCount = targetMapColumnCountInfo.get(targetRow);
        final int MAX_COLUMN_COUNT = Math.max(currentColumnCount, targetColumnCount);
        GooglePhotoStyleAnimationHolder currentAnimationHolder = null;
        GooglePhotoStyleAnimationHolder targetAnimationHolder = null;

        for (int ii = 0; ii < MAX_COLUMN_COUNT; ii++) {
            String currentKey = ImageSelectUtils.getHolderKeyByRowAndColumn(currentRow, ii);
            String targetKey = ImageSelectUtils.getHolderKeyByRowAndColumn(targetRow, ii);
            currentAnimationHolder = mapCurrentHolders.get(currentKey);
            targetAnimationHolder = targetHolders.get(targetKey);

            if (currentAnimationHolder == null && targetAnimationHolder == null)  //둘다 없다.
                continue;

            if (currentAnimationHolder != null && targetAnimationHolder == null) { //현재 컬럼이 더 많다.
                currentAnimationHolder = mapCurrentHolders.get(currentKey);
                GooglePhotoStyleAnimationHolder prevTargetAnimationHolder = targetHolders.get(ImageSelectUtils.getHolderKeyByRowAndColumn(targetRow, ii - 1)); //타켓 row의 마지막 컬럼을 복사한다.
                if (prevTargetAnimationHolder != null) {
                    targetAnimationHolder = copyHolderFromPrevColumn(prevTargetAnimationHolder, currentAnimationHolder);
                    targetAnimationHolder.setDummyTargetView(true);
                    String newKey = ImageSelectUtils.getHolderKeyByRowAndColumn(targetRow, ii);
                    targetHolders.put(newKey, targetAnimationHolder);
                }
            } else if (targetAnimationHolder != null && currentAnimationHolder == null) { //타겟 컬럼이 더 많다.
                targetAnimationHolder = targetHolders.get(targetKey);
                GooglePhotoStyleAnimationHolder prevCurrentAnimationHolder = mapCurrentHolders.get(ImageSelectUtils.getHolderKeyByRowAndColumn(currentRow, ii - 1));
                currentAnimationHolder = copyHolderFromPrevColumn(prevCurrentAnimationHolder, targetAnimationHolder);
                currentAnimationHolder.setDummyStartView(true);
                String newKey = ImageSelectUtils.getHolderKeyByRowAndColumn(currentRow, ii);
                mapCurrentHolders.put(newKey, currentAnimationHolder);
            }

            if (currentAnimationHolder != null && targetAnimationHolder != null) {
                GooglePhotoStyleAnimationHolderSet animationHolderSet = new GooglePhotoStyleAnimationHolderSet(currentAnimationHolder, targetAnimationHolder, putRow);
                String mergedKey = ImageSelectUtils.getHolderKeyByRowAndColumn(putRow, ii); //기준 row가 0이고 위로는 -1, -2... 아래로는 1, 2.....
                mapMergedAnimationHolders.put(mergedKey, animationHolderSet);
            }
        }
    }

    private void putDummyRowHolders(SparseIntArray offsetHolderCountInfo,
                                    Map<String, GooglePhotoStyleAnimationHolder> copyObjectHolders, //현재 홀더랑 타켓 홀더의 갯수가 맞지 않아서 생성할 더미
                                    Map<String, GooglePhotoStyleAnimationHolder> inputTargetHolders, //더미를 넣을 타켓
                                    int offsetRow,
                                    int inputRow,
                                    boolean isBelow,
                                    boolean isStartHolder) {
        if (copyObjectHolders == null || offsetHolderCountInfo == null) return;

        int columnCount = offsetHolderCountInfo.get(offsetRow);

        String inputPrevRowOffsetKey = ImageSelectUtils.getHolderKeyByRowAndColumn((isBelow ? inputRow - 1 : inputRow + 1), 0);
        GooglePhotoStyleAnimationHolder inputPrevHolder = inputTargetHolders.get(inputPrevRowOffsetKey);

        Rect offsetRect = new Rect();
        if (inputPrevHolder != null) {
            Rect viewRect = inputPrevHolder.getViewRect();
            if (viewRect != null) {
                offsetRect.set(viewRect);
                int margin = getFragmentItemMargin();
                int offsetY = isBelow ? (margin + viewRect.height()) : -(margin + viewRect.height());
                offsetRect.offset(0, offsetY);
            }
        }

        int prevOffsetX = 0;
        for (int ii = 0; ii < columnCount; ii++) {
            String targetRowOffsetKey = ImageSelectUtils.getHolderKeyByRowAndColumn(offsetRow, ii);
            if (targetRowOffsetKey == null) continue;

            GooglePhotoStyleAnimationHolder copyObjectHolder = copyObjectHolders.get(targetRowOffsetKey);
            if (copyObjectHolder != null) {
                GooglePhotoStyleAnimationHolder copyHolder = copyObjectHolder.copyInstance();
                if (isStartHolder)
                    copyHolder.setDummyStartView(true);
                else
                    copyHolder.setDummyTargetView(true);

                Rect rect = copyHolder.getViewRect();
                if (rect != null) {
                    if ((isBelow && rect.top < offsetRect.top) || (!isBelow && rect.top > offsetRect.top)) {
                        rect.left = prevOffsetX;
                        rect.top = offsetRect.top;
                        rect.right = prevOffsetX + offsetRect.width();
                        rect.bottom = offsetRect.bottom;
                        prevOffsetX = rect.right + getFragmentItemMargin();
                    } else {
                        int margin = getFragmentItemMargin();
                        int height = rect.height() + margin;
                        rect.offset(-UIUtil.getScreenWidth(activity), (isBelow ? height : -height));
                    }
                }

                String inputRowOffsetKey = ImageSelectUtils.getHolderKeyByRowAndColumn(inputRow, ii);
                inputTargetHolders.put(inputRowOffsetKey, copyHolder);
            }
        }
    }

    private void putMergeAnimationHoldersOtherRows(UIPinchMotionDetector.eMOTION_STATE motionState,
                                                   SparseIntArray targetMapColumnCountInfo, Map<String, GooglePhotoStyleAnimationHolder> targetHolders, int currentBaseRow, int targetBaseRow, boolean isBelow) {
        int currentOffsetRow = currentBaseRow;
        int targetOffsetRow =  targetBaseRow;

        int addCount = isBelow ? 1 : -1;

        currentOffsetRow = currentOffsetRow + addCount;
        targetOffsetRow = targetOffsetRow + addCount;

        int currentRowType = checkSectionRow(currentOffsetRow, mapCurrentHolders);
        int targetRowType = checkSectionRow(targetOffsetRow, targetHolders);

        int putRow = isBelow ? 1 : -1;

        do {
            if (currentRowType == ROW_TYPE_CHECK_RESULT_NONE && targetRowType != ROW_TYPE_CHECK_RESULT_NONE) {   //현재 row가 타겟 row보다 모자란 형태
                putDummyRowHolders(targetMapColumnCountInfo, targetHolders, mapCurrentHolders, targetOffsetRow, currentOffsetRow, isBelow, true);
                mapInfo.setCurrentMapColumnCountInfo(mapCurrentHolders.keySet());
                currentRowType = targetRowType;
            } else if (targetRowType == ROW_TYPE_CHECK_RESULT_NONE && currentRowType != ROW_TYPE_CHECK_RESULT_NONE) {   //현재 row가 타겟 row보다 많은 형태
                putDummyRowHolders(mapInfo.getCurrentMapColumnCountInfo(), mapCurrentHolders, targetHolders, currentOffsetRow, targetOffsetRow, isBelow, false);
                if (motionState == UIPinchMotionDetector.eMOTION_STATE.SCALE_NEXT_DEPTH )
                    mapInfo.setNextTargetMapColumnCountInfo(targetHolders.keySet());
                else
                    mapInfo.setPrevTargetMapColumnCountInfo(targetHolders.keySet());
                targetRowType = currentRowType;
            }

            boolean isCurrentRowSection = currentRowType == ROW_TYPE_CHECK_RESULT_SECTION;
            boolean isTargetRowSection = targetRowType == ROW_TYPE_CHECK_RESULT_SECTION;

            boolean isThumbnailRowToThumbnailRow = !isCurrentRowSection && !isTargetRowSection; //썸네일끼리 변환되는 형태
            boolean isSectionRowToSection = isCurrentRowSection && isTargetRowSection; //섹션끼리 변환되는 형태

            boolean isSectionRowToThumbnailRow = isCurrentRowSection && !isTargetRowSection; //섹션이 썸네일 크기만큼 스케일되는 형태

            if (isThumbnailRowToThumbnailRow) {
                putAllColumnsMergedAnimationHolder(targetMapColumnCountInfo, targetHolders, currentOffsetRow, targetOffsetRow, putRow);
                putRow += addCount;
                currentOffsetRow += addCount;
                targetOffsetRow += addCount;
            } else if (isSectionRowToSection) {
                putSectionAnimationHolder(eANIMATION_OBJECT_TYPE.BOTH, targetHolders, currentOffsetRow, targetOffsetRow, putRow);
                putRow += addCount;
                currentOffsetRow += addCount;
                targetOffsetRow += addCount;
            } else if (isSectionRowToThumbnailRow) {
                putSectionAnimationHolder(eANIMATION_OBJECT_TYPE.CURRENT, targetHolders, currentOffsetRow, targetOffsetRow, putRow);
                putRow += addCount;
                currentOffsetRow += addCount;
            } else {//if (isThumbnailRowToSectionRow) {
                //TargetSection을 그려주고
                putSectionAnimationHolder(eANIMATION_OBJECT_TYPE.TARGET, targetHolders, currentOffsetRow, targetOffsetRow, putRow);
                putRow += addCount;
                targetOffsetRow += addCount;

                //현재 썸네일도 그려준다.
                putAllColumnsMergedAnimationHolder(targetMapColumnCountInfo, targetHolders, currentOffsetRow, targetOffsetRow, putRow);
                putRow += addCount;
                currentOffsetRow += addCount;
                targetOffsetRow += addCount;
            }

            currentRowType = checkSectionRow(currentOffsetRow, mapCurrentHolders);
            targetRowType = checkSectionRow(targetOffsetRow, targetHolders);

        } while (currentRowType != ROW_TYPE_CHECK_RESULT_NONE || targetRowType != ROW_TYPE_CHECK_RESULT_NONE);
    }

    //현재 보여지는 홀더와 애니메이션 타겟 홀더를 합친다.
    private void mergeAnimationHolder(UIPinchMotionDetector.eMOTION_STATE motionType) {

        SparseIntArray targetMapColumnCountInfo = null;
        Map<String, GooglePhotoStyleAnimationHolder> targetHolders = null;
        int currentBaseRow = mapInfo.getCurrentAnimationOffsetRow();
        int targetBaseRow = 0;

        if (mapMergedAnimationHolders != null)
            mapMergedAnimationHolders.clear();
        else
            mapMergedAnimationHolders = new HashMap<>();

        if (motionType == UIPinchMotionDetector.eMOTION_STATE.SCALE_NEXT_DEPTH) {
            targetBaseRow = mapInfo.getNextTargetAnimationOffsetRow();
            targetHolders = mapNextTargetHolders;
            targetMapColumnCountInfo = mapInfo.getNextTargetMapColumnCountInfo();
        } else { //if (motionType == UIPinchMotionDetector.eMOTION_STATE.SCALE_PREV_DEPTH) {
            targetBaseRow = mapInfo.getPrevTargetAnimationOffsetRow();
            targetHolders = mapPrevTargetHolders;
            targetMapColumnCountInfo = mapInfo.getPrevTargetMapColumnCountInfo(); //각 row에 몇 개의 column이 있는 지 정보를 가지고 있다.
        }

        //우선 기준이 되는 row부터..
        putAllColumnsMergedAnimationHolder(targetMapColumnCountInfo, targetHolders, currentBaseRow, targetBaseRow, 0);

        //기준보다 아래 있는 row들..
        putMergeAnimationHoldersOtherRows(motionType, targetMapColumnCountInfo, targetHolders, currentBaseRow, targetBaseRow, true);

        //기준보다 위에 있는 row들..
        putMergeAnimationHoldersOtherRows(motionType, targetMapColumnCountInfo, targetHolders, currentBaseRow, targetBaseRow, false);
    }

    //실제 뷰들을 그려 줌.
    private boolean tryDrawAnimationViews(UIPinchMotionDetector.eMOTION_STATE motionType) {

        tryCalculateAnimationViewsOffset(motionType);

        if (!isPreparedMapHolder(motionType)) {
            return false;
        }

        //현재 홀더와 타켓 홀더를 합쳐준다.
        mergeAnimationHolder(motionType);

        //실제 뷰들을 그린다. (addView)
        return addAllAnimationViewsByMergedAnimationHolder(motionType);
    }

    private boolean isPhotoDataConverting() {
        return getGooglePhotoStyleUIProcessor() != null && getGooglePhotoStyleUIProcessor().isPhotoListConvertingOnThread();
    }

    private int getCurrentAnimationOffsetRow() {
        if (mapInfo == null) return -1;
        return mapInfo.getCurrentAnimationOffsetRow();
    }

    private void setCurrentAnimationOffsetRow(int offsetRow) {
        if (mapInfo == null) return;
        mapInfo.setCurrentAnimationOffsetRow(offsetRow);
    }

    private void setNextTargetAnimationOffsetRow(int offsetRow) {
        if (mapInfo == null) return;
        mapInfo.setNextTargetAnimationOffsetRow(offsetRow);
    }

    private void setPrevTargetAnimationOffsetRow(int offsetRow) {
        if (mapInfo == null) return;
        mapInfo.setPrevTargetAnimationOffsetRow(offsetRow);
    }

    private int getTargetAnimationOffsetRow(UIPinchMotionDetector.eMOTION_STATE motionState) {
        if (mapInfo == null) return -1;
        return motionState == UIPinchMotionDetector.eMOTION_STATE.SCALE_NEXT_DEPTH ? mapInfo.getNextTargetAnimationOffsetRow() : mapInfo.getPrevTargetAnimationOffsetRow();
    }

    private Map<String, GooglePhotoStyleAnimationHolder> getMapCurrentHolders() {
        return mapCurrentHolders;
    }

    private void clearMapInfo() {
        if (mapInfo != null)
            mapInfo.clear();
    }

    public Map<String, GooglePhotoStyleAnimationHolder> getMapPrevTargetHolders() {
        return mapPrevTargetHolders;
    }

    private Map<String, GooglePhotoStyleAnimationHolder> getMapNextTargetHolders() {
        return mapNextTargetHolders;
    }

    private void setMapCurrentHolders(Map<String, GooglePhotoStyleAnimationHolder> mapCurrentHolders) {
        if (this.mapCurrentHolders != null)
            this.mapCurrentHolders.clear();

        this.mapCurrentHolders = mapCurrentHolders;

        if (mapCurrentHolders != null && mapInfo != null) {
            mapInfo.setCurrentMapColumnCountInfo(mapCurrentHolders.keySet());
        }
    }

    private void setMapNextTargetHolders(Map<String, GooglePhotoStyleAnimationHolder> mapNextTargetHolders) {
        if (this.mapNextTargetHolders != null)
            this.mapNextTargetHolders.clear();

        this.mapNextTargetHolders = mapNextTargetHolders;

        if (getMapNextTargetHolders() != null && mapInfo != null)
            mapInfo.setNextTargetMapColumnCountInfo(getMapNextTargetHolders().keySet());
    }

    private void setMapPrevTargetHolders(Map<String, GooglePhotoStyleAnimationHolder> mapPrevTargetHolders) {
        if (this.mapPrevTargetHolders != null)
            this.mapPrevTargetHolders.clear();

        this.mapPrevTargetHolders = mapPrevTargetHolders;

        if (getMapPrevTargetHolders() != null && mapInfo != null)
            mapInfo.setPrevTargetMapColumnCountInfo(getMapPrevTargetHolders().keySet());
    }

    private int getFragmentItemMargin() {
        int id = -1;
        switch (getCurrentDapth()) {
            case DEPTH_DAY: id = R.dimen.image_select_fragment_item_margin_depth_day; break;
            case DEPTH_YEAR: id = R.dimen.image_select_fragment_item_margin_depth_year; break;
            case DEPTH_MONTH: id = R.dimen.image_select_fragment_item_margin_depth_month; break;
            case DEPTH_STAGGERED: id = R.dimen.image_select_fragment_item_margin_depth_staggered; break;
        }
        return id != 1 ? (int) activity.getResources().getDimension(id) : 0;
    }

    private void scrollToPositionWithTargetOffset(final eMOTION_STATE motionState, final SnapsSuperRecyclerView targetRecyclerView, final ImageSelectPhonePhotoAdapter targetAdapter) {
        if (targetRecyclerView == null || targetAdapter == null) return;

        SquareRelativeLayout scaleOffsetView = getCurrentAnimationOffsetView(); //핀치 동작을 할 때, 중앙에 위치해서 스케일의 기준이 되는 뷰
        if (scaleOffsetView == null) return;

        int[] offsetViewLocation = new int[2];

        scaleOffsetView.getLocationOnScreen(offsetViewLocation);

        final int targetViewOffsetY = offsetViewLocation[1] - getRecyclerViewBaseOffsetY();

        GalleryCursorRecord.PhonePhotoFragmentItem currentAnimationOffsetItem = getCurrentAnimationOffsetItem();
        if (currentAnimationOffsetItem != null) {
            long offsetViewImageId = currentAnimationOffsetItem.getPhoneDetailId();
            int photoArrIdxOnNextTargetAdapter = targetAdapter.findArrIndexOnAdapterByImageId(offsetViewImageId); //애니메이션이 종료 되었을 때, offset뷰가 도착하게 될 뷰의 위치를 맞춰준다.

            final GridLayoutManager targetLayoutManager = (GridLayoutManager) targetRecyclerView.getLayoutManager();
            if (targetLayoutManager != null)
                targetLayoutManager.scrollToPositionWithOffset(photoArrIdxOnNextTargetAdapter + 1, Math.max(0, targetViewOffsetY)); //헤더 때문
            lastScrollToPositionWithOffsetTime = System.currentTimeMillis();

            setTargetItem(motionState, targetAdapter, photoArrIdxOnNextTargetAdapter);
        }
    }

    //변할 예정인 뷰의 대상 Adapter position을 찾아서, 현재 뷰의 Offset과 맞춰준다.
    @Override
    public void scrollToPositionWithTargetOffset(eMOTION_STATE motionState) {
        switch (motionState) {
            case IDLE: //양쪽 다 찾아준다.
                scrollToPositionWithTargetOffset(eMOTION_STATE.SCALE_NEXT_DEPTH, getNextTargetRecyclerView(), getNextTargetAdapter());
                scrollToPositionWithTargetOffset(eMOTION_STATE.SCALE_PREV_DEPTH, getPrevTargetRecyclerView(), getPrevTargetAdapter());
                break;
            case SCALE_NEXT_DEPTH:
                scrollToPositionWithTargetOffset(motionState, getNextTargetRecyclerView(), getNextTargetAdapter());
                break;
            case SCALE_PREV_DEPTH:
                scrollToPositionWithTargetOffset(motionState, getPrevTargetRecyclerView(), getPrevTargetAdapter());
                break;
        }
    }

    private void setTargetItem(eMOTION_STATE motionState, ImageSelectPhonePhotoAdapter photoAdapter, int photoArrIdxOnTargetAdapter) {
        if (photoArrIdxOnTargetAdapter >= 0 && photoArrIdxOnTargetAdapter < photoAdapter.getPhotoItemCount()) {
            GalleryCursorRecord.PhonePhotoFragmentItem targetPhotoItem = photoAdapter.getPhotoItem(photoArrIdxOnTargetAdapter); //targetAdapter.findItemByImageKey(key);
            if (targetPhotoItem != null) {
                if (motionState == eMOTION_STATE.SCALE_PREV_DEPTH)
                    setPrevTargetAnimationOffsetItem(targetPhotoItem);
                else if (motionState == eMOTION_STATE.SCALE_NEXT_DEPTH)
                    setNextTargetAnimationOffsetItem(targetPhotoItem);

                RecyclerView.ViewHolder viewHolder = targetPhotoItem.getViewHolder();
                if (viewHolder != null && viewHolder instanceof ImageSelectAdapterHolders.PhotoFragmentItemHolder) {
                    SquareRelativeLayout targetView = ((ImageSelectAdapterHolders.PhotoFragmentItemHolder) viewHolder).getParentView();
                    if (targetView != null) {
                        targetView.setArrIdxOnAdapter(photoArrIdxOnTargetAdapter);

                        if (motionState == eMOTION_STATE.SCALE_PREV_DEPTH)
                            setPrevTargetAnimationOffsetView(targetView);
                        else if (motionState == eMOTION_STATE.SCALE_NEXT_DEPTH)
                            setNextTargetAnimationOffsetView(targetView);
                    }
                }
            }
        }
    }
}