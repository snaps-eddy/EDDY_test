package com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.customview.SnapsSuperRecyclerView;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectNetworkPhotoAttribute;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectUIPhotoFilter;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectGetAlbumListListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectLoadPhotosListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectPublicMethods;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectSNSPhotoAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.CustomGridLayoutManager;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.ImageSelectFragmentPhotoBaseSpacingItemDecoration;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectBaseFragment;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.strategies.ImageSelectSNSPhotoBase;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.strategies.ImageSelectSNSPhotoStrategyFactory;

import java.util.ArrayList;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import font.FTextView;

public class ImageSelectSNSPhotoFragment extends ImageSelectBaseFragment implements IImageSelectLoadPhotosListener, OnMoreListener {

    private static final int ITEM_LEFT_TO_LOAD_MORE = 3;

    private SnapsSuperRecyclerView recyclerView = null;

    private ImageSelectSNSPhotoAdapter snsPhotoAdapter = null;

    private ImageSelectSNSPhotoBase photoStrategy = null;

    private ImageSelectFragmentPhotoBaseSpacingItemDecoration itemDecoration = null;

    private ImageSelectNetworkPhotoAttribute loadingAttribute = null;

    public ImageSelectSNSPhotoFragment() {
    }

    public static ImageSelectSNSPhotoFragment newInstance(ImageSelectActivityV2 selectAct, eIMAGE_SELECT_FRAGMENT targetFragment) {
        ImageSelectSNSPhotoFragment fragment = new ImageSelectSNSPhotoFragment();
        Bundle args = new Bundle();
        args.putInt(Const_VALUE.KEY_IMAGE_SELECT_FRG_TYPE, targetFragment.ordinal());
        fragment.setArguments(args);
        fragment.setSelectAct(selectAct);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        if (getArguments() != null) {
            int ordinal = getArguments().getInt(Const_VALUE.KEY_IMAGE_SELECT_FRG_TYPE);
            eIMAGE_SELECT_FRAGMENT[] arFragmentTypes = ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT.values();
            if (arFragmentTypes.length > ordinal && ordinal >= 0) {
                ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT fragmentType = arFragmentTypes[ordinal];
                photoStrategy = ImageSelectSNSPhotoStrategyFactory.createFragmentStrategy(super.imageSelectActivityV2, fragmentType);
            }
        }

        if (photoStrategy == null) {
            return;
        }

        loadingAttribute = new ImageSelectNetworkPhotoAttribute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (super.imageSelectActivityV2 == null) {
            super.imageSelectActivityV2 = (ImageSelectActivityV2) getActivity();
        }

        int iResId = super.imageSelectActivityV2.isLandScapeMode() ? R.layout.fragment_imagedetail_landscape : R.layout.fragment_imagedetail_;

        View inflaterView = inflater.inflate(iResId, container, false);

        //기본 컨트롤 초기화
        initControls(inflaterView);

        //리스너 등록
        registerListeners();

        return inflaterView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (photoStrategy == null || super.imageSelectActivityV2 == null) return;

        //초기화
        photoStrategy.initialize(super.imageSelectActivityV2.getSNSData(), this);

        loadImageIfExistCreateAlbumList(albumListListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (super.imageSelectActivityV2 != null && photoStrategy != null) {

            String currentTitle = super.imageSelectActivityV2.getTitleText();
            if (StringUtil.isEmpty(currentTitle) || currentTitle.equalsIgnoreCase(getString(R.string.choose_photo))) {
                super.imageSelectActivityV2.updateTitle(photoStrategy.getTitleResId());
            }

            if (snsPhotoAdapter != null)
                snsPhotoAdapter.notifyDataSetChanged();
        }
    }

    //사진 로딩 준비 동작
    @Override
    public void onLoadPhotoPreprare() {
        showProgress(true);
    }

    //사진 로딩 완료
    @Override
    public void onFinishedLoadPhoto(eIMAGE_LOAD_RESULT_TYPE resultType) {
        dismissedFDialog();

        setEmptyUIState(IImageSelectPublicMethods.ePHOTO_LIST_ERR_TYPE.NONE);

        String nextKey = loadingAttribute != null ? loadingAttribute.getNextKey() : null;

        setRecyclerViewMoreListener(!StringUtil.isEmpty(nextKey));
        switch (resultType) {
            case NETWORK_ERROR:
                setEmptyUIState(IImageSelectPublicMethods.ePHOTO_LIST_ERR_TYPE.PHOTO_LIST_NETWORK_ERR);
                break;
            case FIRST_LOAD_COMPLATED:
            case NO_MORE:
                break;
            case EMPTY:
                setEmptyUIState(IImageSelectPublicMethods.ePHOTO_LIST_ERR_TYPE.PHOTO_LIST_EMPTY);
                break;
            case REQUEST_MORE_LOAD:
                loadImage(true);
                break;
            case MORE_LOAD_COMPLATE:
                scrollToLastItem();
                break;
        }
        if (super.imageSelectActivityV2 != null)
            super.imageSelectActivityV2.showTutorialSnsPhoto();
    }

    //아이템 갱신 요청
    @Override
    public void onUpdatedPhotoList(String imageKey) {
        if (snsPhotoAdapter == null) return;
        if (imageKey != null) {
            snsPhotoAdapter.notifyDataSetChangedByImageKey(imageKey);
        } else {
            snsPhotoAdapter.notifyDataSetChanged();
        }
    }

    //앨범을 변경 했을 때
    @Override
    public void onChangedAlbumCursor(IAlbumData cursor) {
        if (loadingAttribute == null) {
            return;
        }

        setRecyclerViewMoreListener(false);

        loadingAttribute.setPage(0);

        loadingAttribute.setNextKey(null);

        loadingAttribute.setAlbumCursorInfo(cursor);

        loadImage(false);
    }

    @Override
    public boolean isExistAlbumList() {
        return photoStrategy != null && photoStrategy.isExistAlbumList();
    }

    //스크롤에 의한 추가 로딩 (superRecclerView)
    @Override
    public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
        loadImage(true);
    }

    @Override
    public void setBaseAlbumIfExistAlbumList(ArrayList<IAlbumData> list) {
        if (photoStrategy == null) return;
        photoStrategy.setBaseAlbumIfExistAlbumList(list); //기본 앨범을 첫번째에 배치 시킨다.
    }

    @Override
    public void loadImageIfExistCreateAlbumList(IImageSelectGetAlbumListListener albumListListener) {
        if (photoStrategy == null) return;
        //앨범이 존재한다면 앨범을 만들고, 이미지 로딩을 시작한다. (앨범이 없으면 그냥 바로 이미지 로딩..)
        photoStrategy.loadImageIfExistCreateAlbumList(albumListListener);
    }

    public void setSelectAct(ImageSelectActivityV2 selectAct) {
        super.imageSelectActivityV2 = selectAct;
    }

    private void loadImage(boolean isMoreLoad) {
        if (loadingAttribute == null || snsPhotoAdapter == null) return;

        if (isMoreLoad)
            loadingAttribute.addPageCount();
        else
            snsPhotoAdapter.clear();

        if (photoStrategy != null) {

            photoStrategy.loadImage(loadingAttribute);
        }
    }

    private void scrollToLastItem() {
        //굳이 스크롤을 내리지 않아도 사용성에 문제가 없어 보인다.
    }

    private void setRecyclerViewMoreListener(boolean isExistNext) {
        if (recyclerView == null) return;
        if (isExistNext) {
            recyclerView.setupMoreListener(this, ITEM_LEFT_TO_LOAD_MORE);
        } else {
            recyclerView.removeMoreListener();
            recyclerView.hideMoreProgress();
        }
    }

    private void initControls(View view) {
        recyclerView = (SnapsSuperRecyclerView) view.findViewById(R.id.custom_snaps_native_super_recycler_view);

        lyErrorView = view.findViewById(R.id.ly_sticky_network_err_parent);
        ivErrorImg = (ImageView) view.findViewById(R.id.iv_wifi);
        tvErrorText = (FTextView) view.findViewById(R.id.tv_network_text_title);
        tvErrorTextSub = (FTextView) view.findViewById(R.id.tv_network_text_d);
        tvErrorRetryBtn = (FTextView) view.findViewById(R.id.btn_sticky_network_err_retry);

        ImageSelectUIPhotoFilter photoFilter = null;
        ImageSelectUIProcessor uiProcessor = super.imageSelectActivityV2.getUIProcessor();
        if (uiProcessor != null) {
            photoFilter = uiProcessor.getPhotoFilterInfo();
            if (photoStrategy != null) {
                photoStrategy.setPhotoFilterInfo(photoFilter);

            }
        }

        snsPhotoAdapter = new ImageSelectSNSPhotoAdapter(super.imageSelectActivityV2, super.imageSelectActivityV2, super.imageSelectActivityV2.isLandScapeMode());
        snsPhotoAdapter.setPhotoFilter(photoFilter);
        snsPhotoAdapter.setItemClickListener(this);
        snsPhotoAdapter.setPhotoStrategy(photoStrategy);

        final int COLUMN_COUNT = super.imageSelectActivityV2.isLandScapeMode() ? Const_VALUE.IMAGE_GRID_COLS_LANDSCAPE : Const_VALUE.IMAGE_GRID_COLS;

        if (itemDecoration != null) {
            recyclerView.removeItemDecoration(itemDecoration);
        }

        itemDecoration = new ImageSelectFragmentPhotoBaseSpacingItemDecoration(super.imageSelectActivityV2, UIUtil.convertDPtoPX(super.imageSelectActivityV2, 3));

        CustomGridLayoutManager layoutManager = new CustomGridLayoutManager(super.imageSelectActivityV2, COLUMN_COUNT);
        GridLayoutManager.SpanSizeLookup spanSizeLookups = new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) return COLUMN_COUNT;
                return 1;
            }
        };

        layoutManager.setSpanSizeLookup(spanSizeLookups);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setAdapter(snsPhotoAdapter);

        if (photoStrategy != null)
            photoStrategy.setAdapter(snsPhotoAdapter);

    }

    private void registerListeners() {
        //갱신을 위해..
        super.imageSelectActivityV2.registerListUpdateListener(this);

        //네트워크 에러 발생 시 재시도
        if (tvErrorRetryBtn != null) {
            tvErrorRetryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setEmptyUIState(IImageSelectPublicMethods.ePHOTO_LIST_ERR_TYPE.NONE);

                    boolean shouldLoadAlbum = false;
                    if (loadingAttribute != null && photoStrategy != null) {
                        if (photoStrategy.isExistAlbumList() && loadingAttribute.getAlbumCursorInfo() == null) { //앨범이 있는 상품인데, 앨범 정보가 없다면
                            shouldLoadAlbum = true;
                        }
                    }

                    if (shouldLoadAlbum)
                        loadImageIfExistCreateAlbumList(albumListListener);
                    else
                        loadImage(false);
                }
            });
        }

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                if (imageSelectActivityV2 != null)
                    imageSelectActivityV2.onChangedRecyclerViewScroll();

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });
    }

    //앨범 생성에 대한 결과 리스너
    private IImageSelectGetAlbumListListener albumListListener = new IImageSelectGetAlbumListListener() {
        @Override
        public void onPreprare() {
            if (photoStrategy == null) return;

            if (photoStrategy.isExistAlbumList()) {
                showProgress(true);
            }
        }

        //앨범이 만들어 졌다.
        @Override
        public void onCreatedAlbumList(ArrayList<IAlbumData> list) {
            dismissedFDialog();

            if (photoStrategy != null && photoStrategy.isExistAlbumList()) {
                if (list != null && !list.isEmpty()) {
                    setBaseAlbumIfExistAlbumList(list);

                    if (itemStateChangedListener != null)
                        itemStateChangedListener.onRequestedMakeAlbumList(list);

                    if (loadingAttribute != null)
                        loadingAttribute.setAlbumCursorInfo(list.get(0));
                } else {
                    if (itemStateChangedListener != null)
                        itemStateChangedListener.onRequestRemovePrevAlbumInfo();
                }
            } else {
                if (itemStateChangedListener != null)
                    itemStateChangedListener.onRequestRemovePrevAlbumInfo();
            }

            loadImage(false);
        }
    };

    @Override
    public IAlbumData getCurrentAlbumCursor() {
        if (photoStrategy != null)
            return photoStrategy.getCurrentAlbumCursor();
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (imageSelectActivityV2 != null) {
            super.imageSelectActivityV2.unRegisterListUpdateListener(this);
        }

        if (photoStrategy != null) {
            photoStrategy.suspended();
        }
    }

    @Override
    public void onDestroyView() {
        dismissedFDialog();
        super.onDestroyView();
    }
}