package com.snaps.mobile.activity.google_style_image_selector.ui.fragments.phone;

import android.content.res.Resources;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.customview.SnapsSuperRecyclerView;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies.ImageSelectUIProcessorStrategyFactory;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectPhonePhotoData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectPhonePhotoFragmentData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectUIPhotoFilter;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectDragItemListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectFragmentItemClickListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH;
import com.snaps.mobile.activity.google_style_image_selector.performs.ImageSelectPerformForKTBook;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectPhonePhotoAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.phone_strategies.GooglePhotoStyleAdapterStrategyBase;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.phone_strategies.GooglePhotoStyleAdapterStrategyFactory;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.CustomGridLayoutManager;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.DateDisplayScrollBar;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.GooglePhotoStyleFrontView;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.SquareRelativeLayout;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.phone.pinch_handler.GooglePhotoStylePinchHandler;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.phone.pinch_handler.UIPinchMotionDetector;
import com.snaps.mobile.activity.google_style_image_selector.utils.GooglePhotoStyleAdapterPhotoConverter;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ysjeong on 2017. 1. 3..
 */
public class GooglePhotoStylePhoneFragmentProcessor implements GooglePhotoStyleAdapterPhotoConverter.IPhotoConverterListener {
    private static final String TAG = GooglePhotoStylePhoneFragmentProcessor.class.getSimpleName();

    public static final eGOOGLE_STYLE_DEPTH DEFAULT_UI_DEPTH = eGOOGLE_STYLE_DEPTH.DEPTH_DAY;

    private SparseArray<ImageSelectPhonePhotoAdapter> arPhotoAdapters = null;

    private SparseArray<SnapsSuperRecyclerView> arRecyclerViews = null;

    private SparseArray<GooglePhotoStyleAdapterStrategyBase> arAdapterStrategies = null;

    private ArrayList<ImageSelectAdapterHolders.PhotoFragmentItemHolder> dragItemList = null;

    private GooglePhotoStyleFrontView googlePhotoStyleFrontView = null;

    private eGOOGLE_STYLE_DEPTH currentDepth = DEFAULT_UI_DEPTH;

    private ImageSelectUIPhotoFilter photoFilterInfo = null;

    private DateDisplayScrollBar dateDisplayScrollBar = null;

    private ImageSelectPhonePhotoData photoURIData = null;

    private IAlbumData currentAlbumData = null;

    private ImageSelectActivityV2 activity = null;

    private IImageSelectFragmentItemClickListener fragmentItemClickListener = null;

    private IImageSelectDragItemListener dragItemListener = null;

    private UIPinchMotionDetector pinchMotionHandler = null;

    private GooglePhotoStyleAdapterPhotoConverter photoListConverter = null;

    private GooglePhotoStylePhoneFragmentProcessor(Builder builder, ImageSelectActivityV2 activity) {
        if (builder == null) return;

        this.activity = activity;

        this.googlePhotoStyleFrontView = builder.googlePhotoStyleFrontView;

        this.arRecyclerViews = new SparseArray<>();
        this.arRecyclerViews.put(eGOOGLE_STYLE_DEPTH.DEPTH_YEAR.ordinal(), builder.phonePhotoRecyclerViewDapthYear);
        this.arRecyclerViews.put(eGOOGLE_STYLE_DEPTH.DEPTH_MONTH.ordinal(), builder.phonePhotoRecyclerViewDepthMonth);
        this.arRecyclerViews.put(eGOOGLE_STYLE_DEPTH.DEPTH_DAY.ordinal(), builder.phonePhotoRecyclerViewDepthDay);
        this.arRecyclerViews.put(eGOOGLE_STYLE_DEPTH.DEPTH_STAGGERED.ordinal(), builder.phonePhotoRecyclerViewDepthStaggered);

        this.arPhotoAdapters = new SparseArray<>();
        this.arPhotoAdapters.put(eGOOGLE_STYLE_DEPTH.DEPTH_YEAR.ordinal(), new ImageSelectPhonePhotoAdapter(activity));
        this.arPhotoAdapters.put(eGOOGLE_STYLE_DEPTH.DEPTH_MONTH.ordinal(), new ImageSelectPhonePhotoAdapter(activity));
        this.arPhotoAdapters.put(eGOOGLE_STYLE_DEPTH.DEPTH_DAY.ordinal(), new ImageSelectPhonePhotoAdapter(activity));
        this.arPhotoAdapters.put(eGOOGLE_STYLE_DEPTH.DEPTH_STAGGERED.ordinal(), new ImageSelectPhonePhotoAdapter(activity));

        this.setItemClickListener(builder.fragmentItemClickListener);
        this.setDragItemListener(builder.dragItemListener);

        this.dateDisplayScrollBar = builder.dateDisplayScrollBar;

        setScrollListener();

        createUIDepthAttributes();

        setControlsBaseAttribute(); //기본적으로 recyclerView와 Adpater 속성을 셋팅한다.

        this.pinchMotionHandler = new GooglePhotoStylePinchHandler(activity, this); //핀치 줌
        this.pinchMotionHandler.setFragmentItemClickListener(fragmentItemClickListener);
        this.pinchMotionHandler.setDragItemListener(dragItemListener);
    }

    public boolean isActivePinchAnimation() {
        return pinchMotionHandler != null && pinchMotionHandler.isActiveAnimation();
    }

    public void removeDateScrollBar() {
        if (this.dateDisplayScrollBar == null) return;
        try {
            this.dateDisplayScrollBar.forceHideScroll();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public void setDataScrollBarLock() {
        if (this.dateDisplayScrollBar == null) return;
        dateDisplayScrollBar.setActivePinchAnimation(true);
    }

    public void requestUnlockPinchMotionAfterDelay() {
        if (dateDisplayScrollBar != null) {
            dateDisplayScrollBar.requestUnlockPinchMotionAfterDelay();
        }
    }

    private void setScrollListener() {
        for (int i = 0; i < arRecyclerViews.size(); ++i)
            setScrollListener(arRecyclerViews.get(i));
    }

    private void setScrollListener(final SnapsSuperRecyclerView snapsSuperRecyclerView) {
        snapsSuperRecyclerView.getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                dateDisplayScrollBar.onRecieveListTouchEvent(snapsSuperRecyclerView);
            }
        });
    }

    //데이터 컨버팅(현재 UI에 대한)이 종료 되었을 때 호출 된다.
    @Override
    public void onFinishedCurrentUIDepthPhotoConvert() {
        //앨범이 변경되었을 경우, 기존에 뿌려지고 있던 데이터 메모리 해제
        SparseArray<ImageSelectPhonePhotoAdapter> arAdapters = getPhotoAdapters();
        if (arAdapters != null) {
            eGOOGLE_STYLE_DEPTH[] arDepth = eGOOGLE_STYLE_DEPTH.values();
            for (eGOOGLE_STYLE_DEPTH depth : arDepth) {
                ImageSelectPhonePhotoAdapter adapter = arAdapters.get(depth.ordinal());
                if (adapter != null) {
                    adapter.releaseHistory(false);
                }
            }
        }

        //아답타 set
        initPhotoAdapterByUIDepth(currentDepth);
    }

    //데이터 컨버팅(4가지 UI에 대한)이 모두 종료 되었을 때 호출 된다.
    @Override
    public void onFinishedAllPhotoConvert() {
        notifyHiddenAdpaters();
    }

    //년도별 UIDepth에서 사진을 선택하면 다음 UIDepth를 보여준다.
    public void switchUIForNextDepth(ImageSelectAdapterHolders.PhotoFragmentItemHolder offsetViewHolder) {
        if (offsetViewHolder == null || pinchMotionHandler == null || pinchMotionHandler.isLockPinchMotion())
            return;

        SquareRelativeLayout parentView = offsetViewHolder.getParentView();
        if (parentView != null) {
            pinchMotionHandler.switchUIForNextDepth(parentView);
        }
    }

    private void setControlsBaseAttribute() {
        if (activity == null || arPhotoAdapters == null || arAdapterStrategies == null || arRecyclerViews == null)
            return;

        eGOOGLE_STYLE_DEPTH[] arDepth = eGOOGLE_STYLE_DEPTH.values();
        boolean isLandScapeMode = activity.isLandScapeMode();
        for (eGOOGLE_STYLE_DEPTH depth : arDepth) {
            final int COLUMN_COUNT = getColumnCountByUIDepth(depth, isLandScapeMode);

            ImageSelectPhonePhotoAdapter adapter = arPhotoAdapters.get(depth.ordinal());
            GooglePhotoStyleAdapterStrategyBase strategy = arAdapterStrategies.get(depth.ordinal());
            SnapsSuperRecyclerView recyclerView = arRecyclerViews.get(depth.ordinal());
            adapter.setGooglePhotoStyleStrategy(strategy);

            CustomGridLayoutManager gridLayoutManager = new CustomGridLayoutManager(activity, COLUMN_COUNT);
            GridLayoutManager.SpanSizeLookup spanSizeLookups = adapter.getScalableSpanSizeLookUp();
            if (spanSizeLookups != null)
                gridLayoutManager.setSpanSizeLookup(spanSizeLookups);

            recyclerView.setLayoutManager(gridLayoutManager);

            RecyclerView.ItemDecoration itemDecorations = adapter.getItemDecoration();
            if (itemDecorations != null) {
                recyclerView.setItemDecoration(itemDecorations);
            }

            recyclerView.setAdapter(adapter);
        }
    }

    public GooglePhotoStyleFrontView getFrontView() {
        return googlePhotoStyleFrontView;
    }

    public void releaseInstace() {
        //여기서 해제 하지 않고 ImageSelectV2가 종료될때 해제한다. 속도 문제 때문에...폰 사진은 계속 들고 있는다.

        if (googlePhotoStyleFrontView != null) {
            googlePhotoStyleFrontView.releaseInstance();
        }

        if (pinchMotionHandler != null) {
            pinchMotionHandler.releaseInstance();
            pinchMotionHandler = null;
        }

        if (photoListConverter != null) {
            photoListConverter.setSuspend(true);
            photoListConverter.interrupt();
            photoListConverter = null;
        }

        if (arPhotoAdapters != null) {
            eGOOGLE_STYLE_DEPTH[] arDepth = eGOOGLE_STYLE_DEPTH.values();
            for (eGOOGLE_STYLE_DEPTH depth : arDepth) {
                ImageSelectPhonePhotoAdapter adapter = arPhotoAdapters.get(depth.ordinal());
                if (adapter != null) {
                    adapter.releaseInstance();
                }
            }

            arPhotoAdapters.clear();
            arPhotoAdapters = null;
        }

        if (arRecyclerViews != null) {
            arRecyclerViews.clear();
            arRecyclerViews = null;
        }

        if (arAdapterStrategies != null) {
            arAdapterStrategies.clear();
            arAdapterStrategies = null;
        }

        if (dateDisplayScrollBar != null) {
            dateDisplayScrollBar.releaseInstance();
        }

        if (fragmentItemClickListener != null) {
            fragmentItemClickListener = null;
        }
    }

    //사진 크기 측정이 종료 되었을 때, 아답타내에 데이터 정보를 갱신 해 준다.
    public void notifyPhotoDimensionInfoInAdapters() {
        SparseArray<ImageSelectPhonePhotoAdapter> arAdapters = getPhotoAdapters();
        if (arAdapters == null) return;

        //현재 Depth부터
        final ImageSelectPhonePhotoAdapter currentPhotoAdapter = arAdapters.get(currentDepth.ordinal());
        if (currentPhotoAdapter != null) {
            boolean isChanged = currentPhotoAdapter.notifyPhotoDimensionInfo(getCurrentPhotoList());

            if (isChanged && activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentPhotoAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        eGOOGLE_STYLE_DEPTH[] arDepth = eGOOGLE_STYLE_DEPTH.values();
        for (eGOOGLE_STYLE_DEPTH depth : arDepth) {
            if (currentDepth == depth) continue;
            final ImageSelectPhonePhotoAdapter adapter = arAdapters.get(depth.ordinal());
            if (adapter != null) {
                boolean isChanged = adapter.notifyPhotoDimensionInfo(getCurrentPhotoList());
                if (isChanged && activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }
    }

    public void setItemClickListener(IImageSelectFragmentItemClickListener itemClickListener) {
        if (itemClickListener == null) return;
        this.fragmentItemClickListener = itemClickListener;
    }

    public void setDragItemListener(IImageSelectDragItemListener dragItemListener) {
        if (dragItemListener == null) return;
        this.dragItemListener = dragItemListener;
    }

    public ImageSelectPhonePhotoAdapter getCurrentPhotoAdapter() {
        SparseArray<ImageSelectPhonePhotoAdapter> arAdapters = getPhotoAdapters();
        if (arAdapters == null || currentDepth == null || currentDepth.ordinal() < 0 || currentDepth.ordinal() >= arAdapters.size())
            return null;
        return arAdapters.get(currentDepth.ordinal());
    }

    public SnapsSuperRecyclerView getCurrentPhotoRecyclerView() {
        SparseArray<SnapsSuperRecyclerView> recyclerViews = getRecyclerViews();
        if (recyclerViews == null || currentDepth == null || currentDepth.ordinal() < 0 || currentDepth.ordinal() >= recyclerViews.size())
            return null;
        return recyclerViews.get(currentDepth.ordinal());
    }

    public void changeAlbum(IAlbumData albumData) throws Exception {
        this.currentAlbumData = albumData;

        convertPhotoListOnThread(); //앨범이 변경되면 데이터 재구성 onFinishedAllPhotoConvert 콜백에서 UI 처리가 완료 된다.
    }

    //Depth를 변경할 때 호출
    public void changeUIDepth(eGOOGLE_STYLE_DEPTH targetDepth) {
        initPhotoAdapterByUIDepth(targetDepth);
        if (Config.isRealServer(activity)) {
            setFabricCheck(targetDepth);
        }

    }

    //구글 스타일 Ui 얼마나 쓰는지 알기 위해
    private void setFabricCheck(eGOOGLE_STYLE_DEPTH targetDepth) {
        if (!Config.isFabricUse()) return;
        try {
            switch (targetDepth) {
                case DEPTH_YEAR:
                    // 이 이벤트 처리를 GA 로 옮기고 싶다면 FirebaseAnalytics 사용해야함.
//                    Answers.getInstance().logCustom(new CustomEvent("GroupTypeEight"));
                    break;
                case DEPTH_MONTH:
                    // 이 이벤트 처리를 GA 로 옮기고 싶다면 FirebaseAnalytics 사용해야함.
//                    Answers.getInstance().logCustom(new CustomEvent("GroupTypeFour"));
                    break;
                case DEPTH_DAY:
                    // 이 이벤트 처리를 GA 로 옮기고 싶다면 FirebaseAnalytics 사용해야함.
//                    Answers.getInstance().logCustom(new CustomEvent("GroupTypeThree"));
                    break;
                case DEPTH_STAGGERED:
                    // 이 이벤트 처리를 GA 로 옮기고 싶다면 FirebaseAnalytics 사용해야함.
//                    Answers.getInstance().logCustom(new CustomEvent("GroupTypeTwo"));
                    break;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void createUIDepthAttributes() {
        this.arAdapterStrategies = new SparseArray<>();

        eGOOGLE_STYLE_DEPTH[] arDepth = eGOOGLE_STYLE_DEPTH.values();
        for (eGOOGLE_STYLE_DEPTH depth : arDepth) {
            boolean isHiddenAdapter = currentDepth != depth;
            arAdapterStrategies.put(depth.ordinal(),
                    GooglePhotoStyleAdapterStrategyFactory.createAdapterStrategyByDepth(activity, getAdapterAttribute(depth, isHiddenAdapter), fragmentItemClickListener));
        }
    }

    private GooglePhotoStyleAdapterStrategyBase.AdapterAttribute getAdapterAttribute(eGOOGLE_STYLE_DEPTH depth, boolean isHidden) {
        if (activity == null) return null;

        boolean isLandscapeMode = activity.isLandScapeMode();
        int columnCountByUIDepth = getColumnCountByUIDepth(depth, isLandscapeMode);

        return new GooglePhotoStyleAdapterStrategyBase.AdapterAttribute.Builder()
                .setLandscapeMode(isLandscapeMode)
                .setUiDepth(depth)
                .setColumnCount(columnCountByUIDepth)
                .setHidden(isHidden)
                .setEnableGroupSelect(isEnableGroupSelectByUIDepth(depth))
                .setPhotoFilter(activity.getPhotoFilterInfo())
                .create();
    }

    public SparseArray<GooglePhotoStyleAdapterStrategyBase.AdapterAttribute> getAdaptersBaseAttributeSparseArray() {
        SparseArray<GooglePhotoStyleAdapterStrategyBase.AdapterAttribute> arAttributes = new SparseArray<>();
        eGOOGLE_STYLE_DEPTH[] arDepth = eGOOGLE_STYLE_DEPTH.values();
        for (eGOOGLE_STYLE_DEPTH depth : arDepth) {
            GooglePhotoStyleAdapterStrategyBase.AdapterAttribute attribute = getAdapterAttribute(depth, currentDepth != depth);
            arAttributes.put(depth.ordinal(), attribute);
        }
        return arAttributes;
    }

    private void setOptimumThumbnailSize() {
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null)
            imageSelectManager.setCurrentUIDepthThumbnailSize(activity, currentDepth, activity.isLandScapeMode());
    }

    //UI Depth에 따른 데이터 설정.
    public void initPhotoAdapterByUIDepth(eGOOGLE_STYLE_DEPTH targetDepth) {
        if (activity == null) return;

        currentDepth = targetDepth;

        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            ImageSelectPhonePhotoFragmentData phonePhotoFragmentData = imageSelectManager.getPhonePhotoFragmentDatas();
            phonePhotoFragmentData.setCurrentUIDepth(currentDepth);
        }

        setOptimumThumbnailSize(); //각 Depth에 맞는 최적화된 썸네일 사이즈 설정

        if (pinchMotionHandler != null) { //핀치 디텍터 초기화
            pinchMotionHandler.reset();
        }

        SparseArray<SnapsSuperRecyclerView> arRecyclerViews = getRecyclerViews();
        SparseArray<ImageSelectPhonePhotoAdapter> arAdapters = getPhotoAdapters();
        if (arRecyclerViews == null || arAdapters == null) return;

        //현재 Depth부터
        SnapsSuperRecyclerView currentRecyclerView = arRecyclerViews.get(currentDepth.ordinal());
        if (currentRecyclerView != null) {
            currentRecyclerView.setVisibility(View.VISIBLE);

            ImageSelectPhonePhotoAdapter currentPhotoAdapter = arAdapters.get(currentDepth.ordinal());
            if (currentPhotoAdapter != null) {
                currentPhotoAdapter.setHidden(false);
                currentPhotoAdapter.notifyDataSetChanged();
            }
        }

        // 다른 Depth의 뷰는 Hidden
        eGOOGLE_STYLE_DEPTH[] arDepth = eGOOGLE_STYLE_DEPTH.values();
        for (eGOOGLE_STYLE_DEPTH depth : arDepth) {
            if (currentDepth == depth) continue;

            SnapsSuperRecyclerView recyclerView = arRecyclerViews.get(depth.ordinal());
            if (recyclerView != null) {
                recyclerView.setVisibility(View.INVISIBLE);
            }
        }

        requestUnlockPinchMotionAfterDelay();
    }

    public void notifyHiddenAdpaters() {
        SparseArray<ImageSelectPhonePhotoAdapter> arAdapters = getPhotoAdapters();
        if (arAdapters == null) return;

        eGOOGLE_STYLE_DEPTH[] arDepth = eGOOGLE_STYLE_DEPTH.values();
        for (eGOOGLE_STYLE_DEPTH depth : arDepth) {
            if (currentDepth == depth) continue;
            ImageSelectPhonePhotoAdapter adapter = arAdapters.get(depth.ordinal());
            if (adapter != null) {
                GooglePhotoStyleAdapterStrategyBase.AdapterAttribute attribute = adapter.getAttribute();
                if (attribute != null) {
                    attribute.setHidden(true);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    //현재 Depth Adpater에서 그려지고 있는 데이터 리스트 반환
    public ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> getCurrentPhotoList() {
        if (currentAlbumData == null || photoURIData == null) return null;

        String albumId = currentAlbumData.getAlbumId();

        return photoURIData.getPhotoListByAlbumId(albumId);
    }

    public void updatedPhotoList(String imageKey) {
        ImageSelectPhonePhotoAdapter currentAdapter = getCurrentPhotoAdapter();
        if (currentAdapter != null) {
            if (imageKey != null && imageKey.length() > 0) {
                currentAdapter.notifyDataSetChangedByImageKey(imageKey);
                currentAdapter.notifyDataSetChangedSection(imageKey);
            } else {
                currentAdapter.notifyDataSetChanged();
            }
        }
    }

    public void initPhotoURIData(ImageSelectPhonePhotoData photoURIData) throws Exception {
        setPhotoURIData(photoURIData);

        setDefaultAlbumCursor();
    }

    public void setPhotoURIData(ImageSelectPhonePhotoData photoURIData) {
        this.photoURIData = photoURIData;
    }

    public ImageSelectPhonePhotoData getPhotoURIData() {
        return photoURIData;
    }

    public ImageSelectUIPhotoFilter getPhotoFilterInfo() {
        return photoFilterInfo;
    }

    public void setPhotoFilterInfo(ImageSelectUIPhotoFilter photoFilterInfo) {
        this.photoFilterInfo = photoFilterInfo;
    }

    //기본 앨범 리스트(모든 사진)
    public void setDefaultAlbumCursor() throws Exception {
        ImageSelectPhonePhotoData phonePhotoData = getPhotoURIData();
        if (phonePhotoData != null) {
            ArrayList<IAlbumData> resultList = phonePhotoData.getArrCursor();
            if (resultList != null && !resultList.isEmpty()) {
                int lastSelectedAlbumIdx = ImageSelectUtils.loadLastSelectedPhoneAlbumIndexFromAlbumList(resultList);
                if (lastSelectedAlbumIdx < 0) return;

                IAlbumData albumData = resultList.get(lastSelectedAlbumIdx);
                if (albumData != null) {
                    this.currentAlbumData = albumData;
                }
            }
        }
    }

    public eGOOGLE_STYLE_DEPTH getCurrentDepth() {
        return currentDepth;
    }

    private int getColumnCountByUIDepth(eGOOGLE_STYLE_DEPTH depth, boolean isLandscapeMode) {
        if (depth == null) return 0;
        switch (depth) {
            case DEPTH_YEAR:
                return isLandscapeMode ? ISnapsImageSelectConstants.COLUMN_COUNT_OF_LANDSCAPE_UI_DEPTH_YEAR : ISnapsImageSelectConstants.COLUMN_COUNT_OF_UI_DEPTH_YEAR;
            case DEPTH_MONTH:
                return isLandscapeMode ? ISnapsImageSelectConstants.COLUMN_COUNT_OF_LANDSCAPE_UI_DEPTH_MONTH : ISnapsImageSelectConstants.COLUMN_COUNT_OF_UI_DEPTH_MONTH;
            case DEPTH_DAY:
                return isLandscapeMode ? ISnapsImageSelectConstants.COLUMN_COUNT_OF_LANDSCAPE_UI_DEPTH_DAY : ISnapsImageSelectConstants.COLUMN_COUNT_OF_UI_DEPTH_DAY;
            case DEPTH_STAGGERED:
                if (activity != null) {
                    Resources resources = activity.getResources();
                    if (resources != null) {
                        DisplayMetrics metrics = resources.getDisplayMetrics();
                        return metrics != null ? metrics.widthPixels : ISnapsImageSelectConstants.COLUMN_COUNT_OF_UI_DEPTH_STAGGERED;
                    }
                }
                return ISnapsImageSelectConstants.COLUMN_COUNT_OF_UI_DEPTH_STAGGERED;
        }
        return 0;
    }

    //그룹 선택 : depth02 ~ depth04 까지 지원, 단일 사진 선택시 없음. ** 기획 변경으로 인해 그룹 선택 기능이 제거 되었다.
    private boolean isEnableGroupSelectByUIDepth(eGOOGLE_STYLE_DEPTH depth) {
        return !(activity == null || depth == null || activity.isSingleChooseType() || depth == eGOOGLE_STYLE_DEPTH.DEPTH_YEAR);
    }

    //각 UI에 맞는 List 형태로 데이터를 가공한다. (시간이 오래 걸릴 수 있으니, 별도의 쓰레드에서 처리한다.)
    public void convertPhotoListOnThread() throws Exception {
        if (photoListConverter != null && photoListConverter.getState() == Thread.State.RUNNABLE) {
            photoListConverter.interrupt();
            try {
                photoListConverter.join();
            } catch (InterruptedException e) {
                Dlog.e(TAG, e);
            }
        }

        photoListConverter = new GooglePhotoStyleAdapterPhotoConverter.Builder(activity)
                .setAdapters(getPhotoAdapters())
                .setAdapterStrategies(getAdapterStrategies())
                .setRecyclerViewSparseArray(getRecyclerViews())
                .setPhotoList(getCurrentPhotoList())
                .setAdapterAttributeSparseArray(getAdaptersBaseAttributeSparseArray())
                .setCurrentDepth(currentDepth)
                .create();

        photoListConverter.setPhotoConverterLineter(this);

        photoListConverter.start();
    }

    public boolean isPhotoListConvertingOnThread() {
        return photoListConverter != null && photoListConverter.isConverting();
    }

    public SparseArray<SnapsSuperRecyclerView> getRecyclerViews() {
        return arRecyclerViews;
    }

    public SparseArray<ImageSelectPhonePhotoAdapter> getPhotoAdapters() {
        return arPhotoAdapters;
    }

    public SparseArray<GooglePhotoStyleAdapterStrategyBase> getAdapterStrategies() {
        return arAdapterStrategies;
    }

    private int standardPosition;
    private int firstPosition;
    private int lastPosition;
    private boolean isSelectMode;

    public void selectDragItem(ImageSelectAdapterHolders.PhotoFragmentItemHolder holder, int type) {
        int listPosition = holder.getPhonePhotoItem().getListPosition();
        switch (type) {
            case IImageSelectDragItemListener.FIRST_ITEM:
                isSelectMode = !ImageSelectUtils.isContainsInImageHolder(holder.getMapKey());
                standardPosition = listPosition;
                firstPosition = standardPosition;
                lastPosition = standardPosition;
                setDragItem(isSelectMode, listPosition);
                break;
            case IImageSelectDragItemListener.DRAG_ITEM:
                boolean isTopDrag = standardPosition > listPosition;
                if (isTopDrag && firstPosition >= listPosition) {
                    firstPosition = listPosition;
                } else if (!isTopDrag && lastPosition <= listPosition) {
                    lastPosition = listPosition;
                } else {
                }
                setDragItem(isSelectMode, listPosition);
                break;
            case IImageSelectDragItemListener.LAST_ITEM:
                setTrayItem(isSelectMode);
                break;
        }

    }

    public void selectDragItemEmpty() {
        setTrayItem(isSelectMode);
    }

    private void setTrayItem(final boolean isSelectMode) {
        final ImageSelectUIProcessor uiProcessor = activity.getUIProcessor();
        int maxImageCount = uiProcessor.getCurrentMaxImageCount();
        int currentImageCount = uiProcessor.getCurrentImageCount();
        int selectImggeCount = getTotalDragItemCount();
        final int selectImageCount = maxImageCount - currentImageCount;
        boolean shouldCheckForMaxImageCount = !activity.isMultiChooseType() && !Config.isSmartSnapsRecommendLayoutPhotoBook();
        if (shouldCheckForMaxImageCount && isSelectMode && maxImageCount < currentImageCount + selectImggeCount) {
            if (Config.isPhotobooks() && !Config.isKTBook()) {
                MessageUtil.alertnoTitle(activity, activity.getString(R.string.page_add_pay_msg), clickedOk -> {
                    if (clickedOk == ICustomDialogListener.OK) {
                        trayItem(uiProcessor, isSelectMode, -1);
                    } else {
                        trayItem(uiProcessor, isSelectMode, selectImageCount);
                    }
                });
            } else if (Config.isSnapsPhotoPrint()) {
                trayItem(uiProcessor, isSelectMode, -1);
            } else {
                //KT 북
                if (Config.isKTBook()) {
                    trayItem(uiProcessor, isSelectMode, selectImageCount);
                    MessageUtil.toast(getApplicationContext(), getApplicationContext().getString(R.string.select_some_photos, ImageSelectPerformForKTBook.MAX_KT_BOOK_IMAGE_COUNT));
                    return;
                }
                String msg = String.format(activity.getString(R.string.select_excess_picture_except_picture), maxImageCount + "");
                trayItem(uiProcessor, isSelectMode, selectImageCount);
                MessageUtil.alertnoTitleOneBtn(activity, msg, null);
            }

        } else {
            trayItem(uiProcessor, isSelectMode, -1);
        }
    }

    private void trayItem(ImageSelectUIProcessor uiProcessor, boolean isClick, int position) {
        try {
            removeOverCountItem(uiProcessor);

            insertCoverPhotoIfEmpty(uiProcessor);

            int count = 0;
            if (standardPosition > firstPosition) { //뒤에서부터 앞으로 드래깅 한 평태
                for (int i = lastPosition; i >= firstPosition; i--) {
                    count = tryInsertImageDataToHolderNoAnimation(uiProcessor, i, position, count);
                }
            } else if (standardPosition == firstPosition) {
                int startPosition = Math.min(firstPosition, lastPosition);
                int endPosition = Math.max(firstPosition, lastPosition);

                for (int i = startPosition; i <= endPosition; i++) {
                    count = tryInsertImageDataToHolderNoAnimation(uiProcessor, i, position, count);
                }
            } else { //앞에서 뒤로 드래깅 한 형태
                for (int i = firstPosition; i <= lastPosition; i++) {
                    count = tryInsertImageDataToHolderNoAnimation(uiProcessor, i, position, count);
                }
            }

            uiProcessor.notifyListUpdateListener(null);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void removeOverCountItem(ImageSelectUIProcessor uiProcessor) throws Exception {
        ImageSelectTrayBaseAdapter trayBaseAdapter = uiProcessor.getTrayAdapter();
        if (trayBaseAdapter == null || !trayBaseAdapter.checkExcessMaxPhotoForDragging()) return;

        int removeCount = 0;
        if (standardPosition > firstPosition) { //뒤에서부터 앞으로 드래깅 한 평태
            for (int i = firstPosition; i <= lastPosition; i++) {
                if (!checkSelectImageData(uiProcessor, i)) {
                    removeSelectedImageData(i);
                }
                ++removeCount;
                if (!trayBaseAdapter.checkExcessMaxPhotoForDragging()) break;
            }
            firstPosition += removeCount;
        } else { //앞에서 뒤로 드래깅 한 형태
            for (int i = lastPosition; i >= firstPosition; i--) {
                if (!checkSelectImageData(uiProcessor, i)) {
                    removeSelectedImageData(i);
                }
                ++removeCount;
                if (!trayBaseAdapter.checkExcessMaxPhotoForDragging()) break;
            }
            lastPosition -= removeCount;
        }
        if (!Const_PRODUCT.isDIYStickerProduct()) {
            MessageUtil.toast(getApplicationContext(), R.string.disable_add_photo);
        }
    }

    private void insertCoverPhotoIfEmpty(ImageSelectUIProcessor uiProcessor) {
        if (!Config.isSmartSnapsRecommendLayoutPhotoBook() || !shouldHandleCoverImage(uiProcessor))
            return;

        try {
            GalleryCursorRecord.PhonePhotoFragmentItem item = getCurrentPhotoAdapter().getPhotoItem(standardPosition - 1);
            if (item == null) return;

            checkCoverImageLabel(uiProcessor, item.getImgData());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private boolean shouldHandleCoverImage(ImageSelectUIProcessor uiProcessor) {
        if (uiProcessor == null) return false;
        return uiProcessor.getImageSelectType() == ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.SMART_ANALYSIS && uiProcessor.isEmptyCoverImageKey();
    }

    private void checkCoverImageLabel(ImageSelectUIProcessor uiProcessor, MyPhotoSelectImageData imageData) throws Exception {
        if (uiProcessor == null || imageData == null) return;
        uiProcessor.appendMapKeyOfCover(imageData.getImageSelectMapKey());
    }

    private boolean checkSelectImageData(ImageSelectUIProcessor uiProcessor, int position) {
        ImageSelectTrayBaseAdapter trayBaseAdapter = uiProcessor.getTrayAdapter();
        ImageSelectPhonePhotoAdapter currentPhotoAdapter = getCurrentPhotoAdapter();
        if (currentPhotoAdapter == null) return false;

        GalleryCursorRecord.PhonePhotoFragmentItem item = currentPhotoAdapter.getPhotoItem(position - 1);
        if (item == null) return false;

        String mapKey = item.getImageKey();
        if (mapKey != null && trayBaseAdapter != null && trayBaseAdapter.checkSelectImage(mapKey)) {
            return true;
        }
        return false;
    }

    private void removeSelectedImageData(int i) throws Exception {
        GalleryCursorRecord.PhonePhotoFragmentItem item = getCurrentPhotoAdapter().getPhotoItem(i - 1);
        if (item == null) return;
        String mapKey = item.getImageKey();
        activity.removeSelectedImageData(mapKey);
    }

    private int tryInsertImageDataToHolderNoAnimation(ImageSelectUIProcessor uiProcessor, int i, int position, int count) {
        GalleryCursorRecord.PhonePhotoFragmentItem item = getCurrentPhotoAdapter().getPhotoItem(i - 1);
        if (item == null) return count;

        boolean first = false;
        boolean last = false;
        if (i == firstPosition) {
            first = true;
        }
        if (i == lastPosition) {
            last = true;
        }
        if (item.getHolderType() != ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_THUMBNAIL) {
            return count;
        }
        String mapKey = item.getImageKey();
        ImageSelectTrayBaseAdapter trayAdapter = uiProcessor.getTrayAdapter();
        if (isSelectMode) {
            if (position != -1) {
                if (trayAdapter != null && !trayAdapter.checkSelectImage(mapKey)) {
                    if (count < position) {
                        uiProcessor.tryInsertImageDataToHolderNoAnimation(item, last);
                        count++;
                    } else {
                        ImageSelectUtils.removeSelectedImageData(mapKey);
                    }
                }


            } else {
                if (trayAdapter != null && !trayAdapter.checkSelectImage(mapKey)) {
                    uiProcessor.tryInsertImageDataToHolderNoAnimation(item, last);
                }
            }
        } else {
            ImageSelectTrayBaseAdapter trayBaseAdapter = uiProcessor.getTrayAdapter();
            if (trayBaseAdapter != null)
                trayBaseAdapter.removeSelectedImageArray(mapKey, first, last);
        }
        return count;
    }

    private int getTotalDragItemCount() {
        int count = 0;
        for (int i = firstPosition; i <= lastPosition; i++) {
            GalleryCursorRecord.PhonePhotoFragmentItem item = getCurrentPhotoAdapter().getPhotoItem(i - 1);
            if (item == null) continue;
            if (item.getHolderType() != ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_THUMBNAIL) {
                continue;
            }
            count++;
        }
        return count;
    }

    private void setDragItem(boolean isSelectMode, int position) {
        final ImageSelectUIProcessor uiProcessor = activity.getUIProcessor();
        for (int i = firstPosition; i <= lastPosition; i++) {
            //GalleryCursorRecord.PhonePhotoFragmentItem item = getCurrentPhotoAdapter().getPhotoItem(i - 1);  //원본 코드
            //
            //bug fix - Crashlytics
            //TODO::getCurrentPhotoAdapter() 이걸 loop밖으로 빼고 싶은데.. 사이드가 예측 불가.
            ImageSelectPhonePhotoAdapter adapter = getCurrentPhotoAdapter();
            if (adapter == null) {
                continue;
            }
            GalleryCursorRecord.PhonePhotoFragmentItem item = adapter.getPhotoItem(i - 1);
            if (item == null || item.getHolderType() != ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_THUMBNAIL) {
                continue;
            }

            String mapKey = item.getImageKey();
            if (isSelectMode) {
                if (!ImageSelectUtils.isContainsInImageHolder(mapKey)) {
                    ImageSelectUtils.putSelectedImageData(mapKey, item.getImgData());
                }
            } else {
                ImageSelectUtils.removeSelectedImageData(mapKey);
            }
        }
        uiProcessor.notifyListUpdateListener(null);
    }

    public static class Builder {
        private SnapsSuperRecyclerView phonePhotoRecyclerViewDapthYear = null;
        private SnapsSuperRecyclerView phonePhotoRecyclerViewDepthMonth = null;
        private SnapsSuperRecyclerView phonePhotoRecyclerViewDepthDay = null;
        private SnapsSuperRecyclerView phonePhotoRecyclerViewDepthStaggered = null;
        private GooglePhotoStyleFrontView googlePhotoStyleFrontView = null;
        private DateDisplayScrollBar dateDisplayScrollBar;
        private IImageSelectFragmentItemClickListener fragmentItemClickListener = null;
        private IImageSelectDragItemListener dragItemListener = null;

        public Builder setFragmentItemClickListener(IImageSelectFragmentItemClickListener fragmentItemClickListener) {
            this.fragmentItemClickListener = fragmentItemClickListener;
            return this;
        }

        public Builder setSelectDragItemListener(IImageSelectDragItemListener dragItemListener) {
            this.dragItemListener = dragItemListener;
            return this;
        }

        public Builder setGooglePhotoStyleFrontView(GooglePhotoStyleFrontView googlePhotoStyleFrontView) {
            this.googlePhotoStyleFrontView = googlePhotoStyleFrontView;
            return this;
        }

        public Builder setPhonePhotoRecyclerViewDepthStaggered(SnapsSuperRecyclerView phonePhotoRecyclerViewDepthStaggered) {
            this.phonePhotoRecyclerViewDepthStaggered = phonePhotoRecyclerViewDepthStaggered;
            return this;
        }

        public Builder setPhonePhotoRecyclerViewDepthYear(SnapsSuperRecyclerView phonePhotoRecyclerViewDapthYear) {
            this.phonePhotoRecyclerViewDapthYear = phonePhotoRecyclerViewDapthYear;
            return this;
        }

        public Builder setPhonePhotoRecyclerViewDepthMonth(SnapsSuperRecyclerView phonePhotoRecyclerViewDepthMonth) {
            this.phonePhotoRecyclerViewDepthMonth = phonePhotoRecyclerViewDepthMonth;
            return this;
        }

        public Builder setPhonePhotoRecyclerViewDepthDay(SnapsSuperRecyclerView phonePhotoRecyclerViewDepthDay) {
            this.phonePhotoRecyclerViewDepthDay = phonePhotoRecyclerViewDepthDay;
            return this;
        }

        public Builder setDateDisplayScrollBar(DateDisplayScrollBar dateDisplayScrollBar) {
            this.dateDisplayScrollBar = dateDisplayScrollBar;
            return this;
        }

        public GooglePhotoStylePhoneFragmentProcessor create(ImageSelectActivityV2 activity) {
            return new GooglePhotoStylePhoneFragmentProcessor(this, activity);
        }
    }
}