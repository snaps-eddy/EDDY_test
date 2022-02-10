package com.snaps.mobile.activity.google_style_image_selector.activities.processors;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.CustomizeDialog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.common.utils.ui.PassPortRuleDialog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.customview.SnapsRecyclerView;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.etc.ImageSelectUIAlbumListSelector;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.etc.ImageSelectUIAnimation;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.etc.ImageSelectUIFragmentHandler;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.etc.ImageSelectUITutorial;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies.ImageSelectUIProcessorStrategyFactory;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectSNSData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectUIPhotoFilter;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectUITrayControl;
import com.snaps.mobile.activity.google_style_image_selector.datas.TrayAdapterInsertParam;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectListAnimationListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectListUpdateListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectProductPerform;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectPublicMethods;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectTitleBarListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectUIProcessorStrategy;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.performs.ImageSelectPerformerFactory;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items.ImageSelectTrayCellItem;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.ImageSelectTraySpacingItemDecoration;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.TrayLinearLayoutManager;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.GIFTutorialView;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectBaseFragment;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.ImageSelectFragmentFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.sns.googlephoto.GooglePhotoUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import errorhandle.SnapsAssert;
import font.FTextView;

import static com.snaps.common.utils.ui.PassPortRuleDialog.PASSPORT_DIALOG_COMPLETE;
import static com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies.ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.SINGLE_CHOOSE;
import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.INVALID_TRAY_CELL_ID;
import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.eTRAY_CELL_STATE.PHOTO_THUMBNAIL;

/**
 * Created by ysjeong on 2016. 11. 24..
 */

public class ImageSelectUIProcessor {
    private static final String TAG = ImageSelectUIProcessor.class.getSimpleName();

    /**
     * - if문 구분을 최소화 하기 위해, 제품별 동작을 달리 할 경우 ImageSelectPerformerFactory에서 생성한 객체가 각자 담당한다.
     * <p>
     * - Intent로 전달하는 내용이 너무 방대하여, ImageSelectIntentData로 통합 관리 한다.
     */
    public ImageSelectUIProcessor(ImageSelectActivityV2 activity) {
        this.activity = activity;
    }

    private ImageSelectActivityV2 activity = null;

    private ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE imageSelectType;
    final String DIALOG_COMPLETE = "dialog_complete";
    private RelativeLayout titleLayout = null;
    private RelativeLayout onlyTrayAllViewLayout = null;
    private ImageView ivBackKey = null;
    private ImageView ivTitleTextArrow = null;
    private TextView tvBackKey = null;
    private TextView tvTitleText = null;
    private TextView tvNextKey = null;
    private View lyTitleTextArea = null;
    private RelativeLayout lyRootLayout = null;
    private View lyAlbumListSelector = null;
    private FrameLayout lyFrameMain = null;
    private ImageView ivTempAnimView = null;

    //Network error or empty
    private View lyErrorView = null;
    private FTextView tvErrorRetryBtn = null;

    //Tray 관련
    private ImageSelectUITrayControl trayControl = null;
    private View lyTrayArea = null;
    private View ivTrayAddBtn = null;
    private ImageSelectTrayBaseAdapter trayAdapter;
    private ImageSelectTraySpacingItemDecoration itemDecoration = null;

    private int limitImageCount = 0; //제품별 최대 선택할 수 있는 이미지의 수

    private IImageSelectUIProcessorStrategy uiProcessorStrategy = null;    //제품별 UI 형태가 다르다..

    private boolean isLandScapeMode = false; //화면 회전 처리..

    private IImageSelectProductPerform productPerformer = null;    //제품별로 각 동작을 수행하는 처리자

    private IImageSelectTitleBarListener titleBarListener = null;    //백키, 다음(완료), 타이틀 제목 선택 처리

    private ImageSelectUIAnimation animationProcessor = null;    //애니메이션 처리

    private ImageSelectUITutorial tutorialProcessor = null;    //튜토리얼 처리

    private ImageSelectUIFragmentHandler fragmentProcessor = null;    //폰앨범, SNS사진등 선택했을때, 변경되는 Fragment를 처리한다.

    private ImageSelectUIAlbumListSelector albumListSelector = null;    //제목을 눌렀을 때, 내려오는 앨범 선택 UI

    private ImageSelectUIPhotoFilter photoFilter = null;    //해상도 체크 조건

    private ImageSelectSNSData snsData = null;

    private Set<IImageSelectListUpdateListener> setListUpdateListeners = null;

    private String lastSelectedAlbumId = null;

    private int maxImageCount = 0; // 최대 선택할 수있는 이미지수 (포토북 같은 경우는 추가 하기 전까지 맥스 이미지수이다)

    private int currentImageCount = 0;// 현재 까지 들어간 이미지수

    private static final long MIN_CLICK_INTERVAL = 1000;

    private long mLastClickTime = 0;

    public void createUIProcessor() {
        if (activity == null) return;

        ImageSelectIntentData intentData = getIntentData();
        if (intentData == null) return;

        findAllViewById();

        //제품 별로 동작 구분 처리를 위한 performer
        IImageSelectProductPerform performer = ImageSelectPerformerFactory.createPerformer(activity, intentData.getHomeSelectProduct());
        setPerformer(performer);

        //제품 유형별 UI 처리를 달리 하기 위해...
        uiProcessorStrategy = ImageSelectUIProcessorStrategyFactory.createImageSelectUI(intentData);
        uiProcessorStrategy.initialize(this);

        //리소스 초기화
        initialize();

        uiProcessorStrategy.postInitialized();
    }

    public ImageSelectActivityV2 getActivity() {
        return activity;
    }

    public void initialize() {
        if (activity == null) return;

        //트레이가 있다면 트레이 초기화
        if (isExistTrayView()) {
            initTrayControl();
        } else if (isExistOnlyTrayAllViewLayout()) {
            initOnlyTrayAllViewLayout();
        } else {
            hideTrayControl();
        }

        registerListeners();

        initGooglePhotoUtil();
    }

    public void releaseInstance() {
        if (trayControl != null) {
            trayControl.releaseInstance();
        }

        if (setListUpdateListeners != null) {
            setListUpdateListeners.clear();
            setListUpdateListeners = null;
        }

        titleLayout = null;
        onlyTrayAllViewLayout = null;
        ivBackKey = null;
        tvBackKey = null;
        tvTitleText = null;
        tvNextKey = null;
        lyTitleTextArea = null;
        lyRootLayout = null;
        lyAlbumListSelector = null;
        lyFrameMain = null;
        ivTempAnimView = null;
        lyErrorView = null;
        tvErrorRetryBtn = null;
        productPerformer = null;
        titleBarListener = null;
        animationProcessor = null;
        tutorialProcessor = null;
        fragmentProcessor = null;
        albumListSelector = null;
        photoFilter = null;
        snsData = null;
    }


    /**
     * public
     */
    public void refreshRemovedTrayImages() {
        List<String> removedKeyList = findRemovedTrayImageKeyList();
        if (removedKeyList == null) return;

        for (String key : removedKeyList) {
            if (key == null) continue;
            ImageSelectTrayBaseAdapter trayBaseAdapter = getTrayAdapter();
            if (trayBaseAdapter != null)
                trayBaseAdapter.removeSelectedImage(key);
        }
    }

    //트레이 전체 보기에서 다른 트레이를 선택하거나, 페이지를 추가하는 등의 액션이 있었을 때, 트레이를 갱신 해 줌.
    public void refreshTrayCellListByAllViewList() {
        ImageSelectManager manager = ImageSelectManager.getInstance();
        if (manager == null) return;

        ArrayList<ImageSelectTrayCellItem> listItems = manager.getTempTrayCellItemList();
        if (listItems != null) {
            ImageSelectTrayBaseAdapter adapter = getTrayAdapter();
            if (adapter != null) {
                adapter.cloneTrayCellItemList(listItems);
                adapter.notifyDataSetChanged();

                int position = adapter.getSelectedCellItemPosition();
                adapter.scrollToCenterTrayView(position);
                adapter.refreshCounterInfo();
                listItems.clear();
            }
        }
    }

    //사진 인화에서 편집하고 BackKey를 눌러서 다시 사진 선택 화면으로 돌아왔을 때
    public void refreshThumbnailsByPhotoPrintCropInfo(ArrayList<MyPhotoSelectImageData> d) {
        ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
        if (holder != null) {
            ArrayList<MyPhotoSelectImageData> datas = holder.getNormalData();
            if (datas != null && !datas.isEmpty()) {
                for (int i = 0; i < datas.size(); i++) {
                    MyPhotoSelectImageData returnObj = datas.get(i);
                    if (returnObj != null && d != null && d.size() > i)
                        returnObj.CROP_INFO = d.get(i).CROP_INFO;
                }
            }
        }
    }

    //사진인화에서 썸네일 클릭 후 회전 처리 되었을 때, 회전 정보를 반영한다.
    public void refreshThumbnailsByPhotoPrintRotatedInfo(ArrayList<String> tempList, HashMap<String, MyPhotoSelectImageData> tempMap) {
        ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
        if (holder != null) {
            holder.setSelectImgKeyList(tempList);
            holder.setSelectImgMap(tempMap);
            holder.reorderMap();
        }

        updateTitle();

        ImageSelectTrayBaseAdapter trayBaseAdapter = getTrayAdapter();
        if (trayBaseAdapter != null) {
            trayBaseAdapter.notifyDataSetChanged();
        }

        notifyListUpdateListener(null);
    }

    public ImageSelectTrayCellItem findNextEmptyCellItem() {
        ImageSelectTrayBaseAdapter trayBaseAdapter = getTrayAdapter();
        if (trayBaseAdapter != null) {
            return trayBaseAdapter.findNextEmptyCellItem();
        }
        return null;
    }

    //엄청난 속도로 사진을 선택하면, use recycled bitmap 이 발생해서 빠르게 못 누르도록 막음.
    private boolean isLockAddItem() {
        ImageSelectUIAnimation animation = getAnimationProcessor();
        return animation != null && !animation.isEnableClick();
    }

    private void appendCoverImageMapKey(ImageSelectAdapterHolders.PhotoFragmentItemHolder fragmentViewHolder) {
        if (fragmentViewHolder == null) return;

        appendMapKeyOfCover(fragmentViewHolder.getMapKey());
    }

    private boolean shouldHandleCoverImage(ImageSelectAdapterHolders.PhotoFragmentItemHolder fragmentViewHolder) {
        return fragmentViewHolder != null && getImageSelectType() == ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.SMART_ANALYSIS && isEmptyCoverImageKey();
    }

    public void checkCoverImageLabel(ImageSelectAdapterHolders.PhotoFragmentItemHolder fragmentViewHolder) {
        if (!Config.isSmartSnapsRecommendLayoutPhotoBook() || !shouldHandleCoverImage(fragmentViewHolder))
            return;
        appendCoverImageMapKey(fragmentViewHolder);
    }

    private void removeCoverImageMapkey(String mapKeyOfCover) {
        if (getImageSelectType() == ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.SMART_ANALYSIS) {
            SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
            if (smartSnapsManager.removeCoverPhotoMapKey(mapKeyOfCover)) {
                removeCoverHolderImage();
            }
        }
    }

    /**
     * Fragment에서 사진을 선택했을 때, 추가 가능 여부 체크 / 애니메이션 처리 / Holder에 담는 처리를 진행한다.
     */
    public void tryInsertImageDataToHolder(final ImageSelectAdapterHolders.PhotoFragmentItemHolder fragmentViewHolder) throws Exception {
        if (fragmentViewHolder == null || fragmentViewHolder.getMapKey() == null || fragmentViewHolder.getImgData() == null || isLockAddItem() || activity == null || activity.isFinishing())
            return;

        //커버 사진인지 체크하기 위해..
        checkCoverImageLabel(fragmentViewHolder);

        ImageSelectTrayCellItem cellItem = findNextEmptyCellItem();

        //cellItem을 못 찾았다는 것은 트레이가 가득 찼다는 것을 의미한다.
        if (cellItem != null) {
            ImageSelectUIAnimation animation = getAnimationProcessor();
            ImageSelectUIAnimation.TemplateToTrayAnimBuilder builder
                    = new ImageSelectUIAnimation.TemplateToTrayAnimBuilder()
                    .setRootLayout(lyRootLayout)
                    .setTempImageView(ivTempAnimView)
                    .setFragmentViewHolder(fragmentViewHolder)
                    .setUiType(getImageSelectType())
                    .setCellItem(cellItem)
                    .create();

            putSelectedImageData(fragmentViewHolder.getMapKey(), fragmentViewHolder.getImgData());

            animation.startFragmentToTrayAnimation(activity, builder, new IImageSelectListAnimationListener() {
                @Override
                public void onFinishedTrayInsertAnimation() {
                    ImageSelectTrayBaseAdapter adapter = getTrayAdapter();
                    if (adapter != null) {
                        //Tray에 썸네일 생성
                        fragmentViewHolder.getImgData().isNoPrint = fragmentViewHolder.isDisableClick();
                        adapter.insertPhotoThumbnailOnTrayItem(new TrayAdapterInsertParam.Builder().setImageMapKey(fragmentViewHolder.getMapKey()).setImageData(fragmentViewHolder.getImgData()).create()); //트레이에 사진을 넣는다.
                    }
                }
            });
        } else {
            if (isMultiChooseType()) {
                putSelectedImageData(fragmentViewHolder.getMapKey(), fragmentViewHolder.getImgData());
            } else if (isExistOnlyTrayAllViewLayout()) {
                putSelectedImageData(fragmentViewHolder.getMapKey(), fragmentViewHolder.getImgData());

                ImageSelectTrayBaseAdapter adapter = getTrayAdapter();
                if (adapter != null) {
                    adapter.insertPhotoThumbnailOnTrayItem(new TrayAdapterInsertParam.Builder().setImageMapKey(fragmentViewHolder.getMapKey()).setImageData(fragmentViewHolder.getImgData()).create()); //트레이에 사진을 넣는다.
                }
            } else if (Config.isSimplePhotoBook() || Config.isSimpleMakingBook()) {
                /**
                 * 심플북이나, 심플 포토북에서는 페이지 추가의 개념이 있다.
                 */
                ImageSelectTrayBaseAdapter adapter = getTrayAdapter();
                if (adapter != null) {

                    putSelectedImageData(fragmentViewHolder.getMapKey(), fragmentViewHolder.getImgData());

                    //insertPhotoThumbnailOnTrayItem를 타면 페이지를 추가할 것인지 confirm이 처리 된다.
                    adapter.insertPhotoThumbnailOnTrayItem(new TrayAdapterInsertParam.Builder().setImageMapKey(fragmentViewHolder.getMapKey()).setImageData(fragmentViewHolder.getImgData()).create());
                }
            }
        }

        ImageSelectTrayBaseAdapter adapter = getTrayAdapter();
        if (adapter != null && adapter.getSelectedCellItemPosition() > 2) {
            if (activity != null) activity.showTutorialThreeItemAdd();
        }
    }

    private void isNotExistTrayShapeMultiSelectProduct() {

    }

    public void handleOnTrayItemSelected(ImageSelectTrayCellItem item) {
        if (item == null || item.isCoverPhoto()) return;
        if (trayControl != null) {
            ImageSelectAdapterHolders.TrayThumbnailItemHolder singleItemHolder = trayControl.getTraySingleThumbnailItemHolder();
            if (singleItemHolder != null) {
                singleItemHolder.removeDeleteIcon();
            }
        }
    }

    private void onBindSingleThumbnailItemHolder(final RecyclerView.ViewHolder holder, final ImageSelectTrayCellItem cellItem, final MyPhotoSelectImageData imageData) {
        if (cellItem == null || holder == null) return;

        ImageSelectAdapterHolders.TrayThumbnailItemHolder trayHolder = (ImageSelectAdapterHolders.TrayThumbnailItemHolder) holder;
        cellItem.setHolder(trayHolder);

        FTextView holderTextView = trayHolder.getSelectorTextView();
        if (holderTextView != null) holderTextView.setVisibility(View.GONE);

        ImageView selector = trayHolder.getSelector();
        if (selector != null) {
            if (cellItem.isSelected()) {
                selector.setBackgroundResource(cellItem.getCellState() == PHOTO_THUMBNAIL ? R.drawable.shape_red_e36a63_fill_solid_border_rect : R.drawable.shape_red_e36a63_border_rect);
                selector.setVisibility(View.VISIBLE);
            } else if (cellItem.isNoPrint()) {
                selector.setBackgroundColor(Color.parseColor("#66000000"));
                selector.setVisibility(View.VISIBLE);
            } else {
                selector.setVisibility(View.GONE);
            }
        }

        //선택된 사진 섬네일
        ImageView photoThumbnail = trayHolder.getPhotoThumbnail();
        if (photoThumbnail != null) {
            String photoThumbnailPath = imageData.THUMBNAIL_PATH;
            photoThumbnail.setImageBitmap(null);
            photoThumbnail.setVisibility(View.VISIBLE);

            if (photoThumbnailPath != null && photoThumbnailPath.length() > 0) {
                ImageSelectUtils.loadImage(getActivity(), photoThumbnailPath, UIUtil.convertDPtoPX(getActivity(), 50), photoThumbnail, ImageView.ScaleType.CENTER_CROP, true);
                photoThumbnail.setRotation(imageData.ROTATE_ANGLE_THUMB == -1 ? 0 : imageData.ROTATE_ANGLE_THUMB);
            }
        }

        //삭제 아이콘
        if (trayHolder.getDeleteIcon() != null) {
            trayHolder.getDeleteIcon().setVisibility(cellItem.getCellState() == PHOTO_THUMBNAIL && cellItem.isSelected() ? View.VISIBLE : View.GONE);
        }

        //해상도 아이콘
        if (trayHolder.getNoPrintIcon() != null) {
            trayHolder.getNoPrintIcon().setVisibility(cellItem.getCellState() == PHOTO_THUMBNAIL && cellItem.isNoPrint() ? View.VISIBLE : View.GONE);
        }

        if (photoThumbnail != null) {
            photoThumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageSelectTrayBaseAdapter adapter = getTrayAdapter();
                    if (adapter != null) {
                        int cellId = cellItem.getCellId();
                        if (cellId == INVALID_TRAY_CELL_ID)
                            cellId = 0;

                        if (!adapter.isTwiceClickedCell(cellId)) {
                            cellItem.setSelected(true);
                            onBindSingleThumbnailItemHolder(holder, cellItem, imageData);
                        }

                        adapter.selectTrayItem(cellId, false);
                    }
                }
            });
        }

        FTextView imgLabel = trayHolder.getImgLabel();
        if (imgLabel != null) {
            imgLabel.setVisibility(View.VISIBLE);
        }
    }

    public void removeCoverHolderImage() {
        if (trayControl != null) {
            ImageSelectAdapterHolders.TrayThumbnailItemHolder singleItemHolder = trayControl.getTraySingleThumbnailItemHolder();
            if (singleItemHolder != null) {
                singleItemHolder.setHolderStateToDummyItem();
            }
        }
    }

    public boolean tryInsertImageDataToHolderNoAnimation(GalleryCursorRecord.PhonePhotoFragmentItem item, boolean last) {
        if (item == null || item.getImageKey() == null || item.getImgData() == null) return false;
        //커버 사진인지 체크하기 위해..
        RecyclerView.ViewHolder viewHolder = item.getViewHolder();
        if (viewHolder != null && viewHolder instanceof ImageSelectAdapterHolders.PhotoFragmentItemHolder) {
            checkCoverImageLabel((ImageSelectAdapterHolders.PhotoFragmentItemHolder) item.getViewHolder());
        }

        ImageSelectTrayCellItem cellItem = findNextEmptyCellItem();
        //cellItem을 못 찾았다는 것은 트레이가 가득 찼다는 것을 의미한다.
        if (cellItem != null) {
            ImageSelectTrayBaseAdapter adapter = getTrayAdapter();
            if (adapter != null) {
                //Tray에 썸네일 생성
                return adapter.insertPhotoThumbnailOnTrayItem(
                        new TrayAdapterInsertParam.Builder().setImageMapKey(item.getImageKey()).setImageData(item.getImgData()).setArrayInsert(true).setArrayInsertAndLastItem(last).create()); //트레이에 사진을 넣는다.
            }
        } else {
            /**
             * 심플북이나, 심플 포토북에서는 페이지 추가의 개념이 있다.
             */
            if (Config.isSimplePhotoBook() || Config.isSimpleMakingBook()) {
                ImageSelectTrayBaseAdapter adapter = getTrayAdapter();
                if (adapter != null) {
                    return adapter.insertPhotoThumbnailOnTrayItem(new TrayAdapterInsertParam.Builder().setImageMapKey(item.getImageKey()).setImageData(item.getImgData()).setArrayInsert(true).setArrayInsertAndLastItem(last).create()); //트레이에 사진을 넣는다.
                }
            }
        }

        ImageSelectTrayBaseAdapter adapter = getTrayAdapter();
        if (adapter != null && adapter.getSelectedCellItemPosition() > 2) {
            if (activity != null) activity.showTutorialThreeItemAdd();
        }

        return true;
    }

    /**
     * 에러 처리
     */
    public void setTemplateDownloadErrorUIState(IImageSelectPublicMethods.ePHOTO_LIST_ERR_TYPE errType) {
        if (errType == null || lyErrorView == null) return;

        switch (errType) {
            case TEMPLATE_DOWNLOAD_ERROR:
                lyErrorView.setVisibility(View.VISIBLE);
                break;
            case NONE:
                lyErrorView.setVisibility(View.GONE);
                break;
        }
    }

    private void findAllViewById() {
        titleLayout = (RelativeLayout) activity.findViewById(R.id.include_google_photo_style_image_select_title_area_ly);
        onlyTrayAllViewLayout = (RelativeLayout) activity.findViewById(R.id.include_google_photo_style_image_select_only_tray_all_view_ly);
        ivBackKey = (ImageView) activity.findViewById(R.id.google_photo_style_image_select_title_bar_back_iv);
        tvTitleText = (TextView) activity.findViewById(R.id.google_photo_style_image_select_title_bar_title_tv);
        lyTitleTextArea = activity.findViewById(R.id.google_photo_style_image_select_title_bar_title_ly);
        lyRootLayout = (RelativeLayout) activity.findViewById(R.id.google_photo_style_root_ly);
        tvNextKey = (TextView) activity.findViewById(R.id.google_photo_style_image_select_title_bar_next_tv);
        ivTitleTextArrow = (ImageView) activity.findViewById(R.id.google_photo_style_image_select_title_bar_arrow);
        lyFrameMain = (FrameLayout) activity.findViewById(R.id.google_photo_style_image_select_frame_main_ly);
        lyTrayArea = activity.findViewById(R.id.include_google_photo_style_image_select_tray_ly);
        ivTrayAddBtn = activity.findViewById(R.id.google_photo_style_image_select_tray_add_btn);
        lyAlbumListSelector = activity.findViewById(R.id.include_google_photo_style_image_select_album_list_ly);

        lyErrorView = activity.findViewById(R.id.ly_sticky_network_err_parent);
        tvErrorRetryBtn = (FTextView) activity.findViewById(R.id.btn_sticky_network_err_retry);

        //bringToFront 순서를 변경하지 말 것.
        lyAlbumListSelector.bringToFront();
        titleLayout.bringToFront();
        lyErrorView.bringToFront();
    }

    private void setTitleTextControlState() {
        ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT currentFragmentType = getCurrentFragmentType();
        if (currentFragmentType == null) return;

        if (currentFragmentType == ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT.SELECT_IMAGE_SRC) {
            setTitleTextAreaEnable(false);
        } else {
            ImageSelectUIFragmentHandler fragmentHandler = getFragmentProcessor();
            if (fragmentHandler == null) return;

            ImageSelectBaseFragment fragment = fragmentHandler.getCurrentFragment();
            if (fragment == null) return;

            boolean isEnableTitleTextControl = fragment.isExistAlbumList();
            setTitleTextAreaEnable(isEnableTitleTextControl);
        }
    }

    private void setTitleTextAreaEnable(boolean isEnable) {
        if (Config.getAI_IS_SELFAI() && !Config.getAI_SELFAI_EDITTING()) {
            if (ivTitleTextArrow != null) {
                ivTitleTextArrow.setVisibility(View.GONE);
            }
            return;
        }

        if (isEnable) {
            if (ivTitleTextArrow != null) {
                ivTitleTextArrow.setVisibility(View.VISIBLE);
                ivTitleTextArrow.setImageResource(R.drawable.img_triangle_down);
            }
            if (lyTitleTextArea != null) lyTitleTextArea.setClickable(true);
        } else {
            if (ivTitleTextArrow != null) {
                ivTitleTextArrow.setVisibility(View.GONE);
                ivTitleTextArrow.setImageResource(0);
            }
            if (lyTitleTextArea != null) lyTitleTextArea.setClickable(false);
        }
    }

    private void setBackKeyState() {
    }

    private void setNextKeyState(int selectProduct, int titleId, boolean isDiaryProfilePhoto) {
        TextView nextButton = getTvNextKey();

        if (titleId == R.string.choose_photo || titleId == R.string.select_story_to_exclude || titleId == R.string.select_post_to_exclude || titleId == R.string.snaps_diary_remove_post_title) {
            nextButton.setText(activity.getString(R.string.confirm)); // 상품상세보기...
            nextButton.setVisibility(View.VISIBLE);// 완료버튼

            if (Config.SELECT_SINGLE_CHOOSE_TYPE == selectProduct) {
                if (isDiaryProfilePhoto)
                    nextButton.setText(activity.getString(R.string.next));
            } else if (Config.SELECT_SNAPS_DIARY == selectProduct || titleId == R.string.diary_album_title) {
                nextButton.setText(activity.getString(R.string.next));
                nextButton.setVisibility(View.VISIBLE);// 완료버튼
            }
        }
        // 사이즈 선택인 경우...
        else if (titleId == R.string.select_size) {
            nextButton.setText(R.string.product_comment); // 상품상세보기...
            nextButton.setVisibility(View.VISIBLE);
        } else {// 선택 및 기타화면
            if (isSmartRecommendBookProduct()) {
                nextButton.setText(activity.getString(R.string.next));
            }

            nextButton.setVisibility(View.VISIBLE);// 완료버튼
        }

        if (titleId == R.string.cover_design)
            nextButton.setVisibility(View.VISIBLE);
    }

    public void updateTitle() {
        updateTitle(0);
    }

    public void updateTitle(String titleName) {
        updateTitle(titleName, -1);
    }

    public void updateTitle(int titleId) {
        updateTitle(null, titleId);
    }

    @SuppressLint("StringFormatMatches")
    public void updateTitle(String titleName, int titleId) {
        if (activity == null) return;

        String titleCount = null;
        int selectProduct = -1;
        int selectImageCount = ImageSelectUtils.getCurrentSelectedImageCount();
        boolean isDiaryProfilePhoto = false;

        ImageSelectIntentData intentData = activity.getIntentData();
        if (intentData != null) {
            selectProduct = intentData.getHomeSelectProduct();
            isDiaryProfilePhoto = intentData.isDiaryProfilePhoto();
        }

        setNextKeyState(selectProduct, titleId, isDiaryProfilePhoto);

        setBackKeyState();

        setTitleTextControlState();

        //FIXME 대충 만들어 놓은 거라, 충분한 테스트와 코드 수정이 필요 하다.
        if (titleId == 0) {
            if (Config.SELECT_NEW_KAKAOBOOK == selectProduct) {
                titleName = activity.getString(R.string.select_story_to_exclude);
                titleCount = " " + String.format(activity.getString(R.string.photo_count_format1), selectImageCount);
            } else if (Config.SELECT_FACEBOOK_PHOTOBOOK == selectProduct) {
                titleName = activity.getString(R.string.select_post_to_exclude);
                titleCount = " " + String.format(activity.getString(R.string.photo_count_format1), selectImageCount);
            } else if (Config.SELECT_SNAPS_REMOVE_DIARY == selectProduct) {
                titleName = activity.getString(R.string.snaps_diary_remove_post_title);
                titleCount = getMaxImageCount() > 0 ? " " + String.format(activity.getString(R.string.photo_count_format2), selectImageCount, getMaxImageCount()) : "";
            } else if (Config.SELECT_SNAPS_DIARY == selectProduct) {
                titleName = activity.getString(R.string.diary_album_title);
            } else {
                titleCount = "";
            }
        } else if (titleId > 0 && titleName == null) {
            titleName = activity.getString(titleId);
        }

        if (titleName == null || titleName.length() < 1)
            titleName = activity.getString(R.string.choose_photo);


        if (titleCount != null && titleCount.length() > 0) {
            titleName += titleCount;
        }

        tvTitleText.setText(titleName);
    }

    public String getTitleText() {
        return tvTitleText != null ? tvTitleText.getText().toString() : "";
    }

    public boolean isExistTrayView() {
        return uiProcessorStrategy != null && uiProcessorStrategy.isExistTrayView();
    }

    public boolean isExistOnlyTrayAllViewLayout() {
        return uiProcessorStrategy != null && uiProcessorStrategy.isExistOnlyTrayAllViewLayout();
    }

    //폰 사진 상태에서 앨범 리스트를 보였다 안 보였다 처리
    public void switchAlbumListState() throws Exception {
        if (getAlbumListSelector() == null || getAlbumListSelector().getAlbumListCount() < 1)
            return; //앨범이 하나도 없다면 의미 없다.

        boolean isShow = !lyAlbumListSelector.isShown();
        getAnimationProcessor().startVerticalTranslateAnimation(lyAlbumListSelector, ivTitleTextArrow, isShow, ImageSelectUIAnimation.ANIM_TIME_ALBUM_LIST_SELECTOR);
    }

    //폰 사진 상태에서 앨범 리스트를  안 보이게 처리
    public boolean hideAlbumListSelector() throws Exception {
        boolean isShowing = lyAlbumListSelector != null && lyAlbumListSelector.isShown();
        getAnimationProcessor().startVerticalTranslateAnimation(lyAlbumListSelector, ivTitleTextArrow, false, ImageSelectUIAnimation.ANIM_TIME_ALBUM_LIST_SELECTOR);
        return isShowing;
    }

    //기본 Fragment 로딩 (제품별로 처음 나오는 기본 프래그 먼트가 다를 수 있다.)
    public void loadBaseFragment() {
        if (getPerformer() != null) {
            ImageSelectUIFragmentHandler fragmentHandler = getFragmentProcessor();
            if (fragmentHandler != null)
                fragmentHandler.loadFragment(activity, getPerformer().performGetDefaultFragmentType());
        }
        if (Const_PRODUCT.isTransparencyPhotoCardProduct() && getImageSelectType() != SINGLE_CHOOSE) {
            try {
                showPngAndJpgDialog(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        } else if (Config.isPassportPhoto() && getImageSelectType() != SINGLE_CHOOSE) {
            //한국어 일때만 사진 규정 정보 다이얼로그를 표시한다.
            if (Config.useKorean()) {
                boolean notAgain = Setting.getBoolean(getActivity(), PASSPORT_DIALOG_COMPLETE, false);
                if (!notAgain) {
                    PassPortRuleDialog dialog = new PassPortRuleDialog(getActivity(), true);
                    dialog.show();
                }
            }
        }
    }

    //이미지 추가가 가능한지
    public boolean isAddableImage() {
        boolean isAble = true;
        ImageSelectTrayBaseAdapter adapter = getTrayAdapter();
        if (adapter != null) {
            isAble = !adapter.checkExcessMaxPhoto();
        }

        return isAble;
    }

    public ImageSelectTrayBaseAdapter getTrayAdapter() {
        return trayAdapter;
    }

    public View getTitleLayout() {
        return titleLayout;
    }

    public ImageView getIvBackKey() {
        return ivBackKey;
    }

    public TextView getTvNextKey() {
        return tvNextKey;
    }

    public RelativeLayout getOnlyTrayAllViewLayout() {
        return onlyTrayAllViewLayout;
    }

    public ImageSelectUITrayControl getTrayControl() {
        return trayControl;
    }

    public void setTitleBarListener(IImageSelectTitleBarListener listener) {
        this.titleBarListener = listener;
    }

    public void setMaxImageCount() {
        this.limitImageCount = getDefaultLimitImageCount();
    }

    public void setMaxImageCount(int count) {
        this.limitImageCount = count;
    }

    public int getMaxImageCount() {
        return this.limitImageCount;
    }

    public ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE getImageSelectType() {
        return imageSelectType;
    }

    public void setImageSelectType(ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE imageSelectType) {
        this.imageSelectType = imageSelectType;
    }

    public ImageSelectIntentData getIntentData() {
        return activity != null ? activity.getIntentData() : null;
    }

    public int getHomeSelectProdKind() {
        if (getIntentData() == null) return -1;
        return getIntentData().getHomeSelectProduct();
    }

    public boolean isSmartSelectType() {
        return getIntentData() != null && getIntentData().getSmartSnapsImageSelectType() == SmartSnapsConstants.eSmartSnapsImageSelectType.SMART_CHOICE;
    }

    public boolean isSmartRecommendBookProduct() {
        return getIntentData() != null && getIntentData().getSmartSnapsImageSelectType() == SmartSnapsConstants.eSmartSnapsImageSelectType.SMART_RECOMMEND_BOOK_PRODUCT;
    }

    //테마북, 편집 화면에서 진입 했을때는 1장의 사진만 선택할 수 있다.
    public boolean isSingleChooseType() {
        return (getIntentData() != null && getIntentData().isSinglePhotoChoose());
    }

    public boolean isMultiChooseType() {
        return (getIntentData() != null && getIntentData().isMultiPhotoChoose());
    }

    public boolean isLandScapeMode() {
        return isLandScapeMode;
    }

    public void setLandScapeMode(boolean isLandScapeMode) {
        this.isLandScapeMode = isLandScapeMode;
    }

    public IImageSelectProductPerform getPerformer() {
        return productPerformer;
    }

    public void setPerformer(IImageSelectProductPerform performer) {
        this.productPerformer = performer;
    }

    public void makeAlbumListSelector(ArrayList<IAlbumData> cusors) {
        if (cusors == null || cusors.isEmpty()) return;

        //기존에 만들어져 있던 앨범은 제거한다.
        removeAlbumListSelector();

        getAlbumListSelector().makeSelector(cusors);

        initAlbumListSelectorTitle();
    }

    private void initAlbumListSelectorTitle() {
        if (getAlbumListSelector() == null) return;
        ArrayList<IAlbumData> cursors = getAlbumListSelector().getCursors();
        if (cursors == null || cursors.isEmpty()) return;

        int lastSelectedAlbumIdx = ImageSelectUtils.loadLastSelectedPhoneAlbumIndexFromAlbumList(cursors);
        if (lastSelectedAlbumIdx < 0 || lastSelectedAlbumIdx >= cursors.size()) return;

        String title = cursors.get(lastSelectedAlbumIdx).getAlbumName();
        updateTitle(!cursors.isEmpty() ? title : activity.getString(R.string.choose_photo));
    }

    public ImageSelectUIPhotoFilter getPhotoFilterInfo() {
        if (photoFilter == null) {
            photoFilter = new ImageSelectUIPhotoFilter();
            photoFilter.initPhotoFilterInfo(activity, getIntentData());

        }
        return photoFilter;
    }

    public ImageSelectSNSData getSNSData() {
        if (snsData == null) {
            snsData = new ImageSelectSNSData(activity);

        }
        return snsData;
    }

    /**
     * Fragment 관련
     */
    public void changeFragment(ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT fragmentType) {
        getFragmentProcessor().changeFragment(activity, fragmentType);
    }

    public void popFragmentType() {
        getFragmentProcessor().popFragmentType();
    }

    public int getFragmentTypeSize() {
        return getFragmentProcessor().getFragmentTypeSize();
    }

    public ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT getCurrentFragmentType() {
        return getFragmentProcessor().getCurrentFragmentType();
    }

    public ImageSelectFragmentFactory.eIMAGE_SELECT_FRAGMENT getRootFragmentType() {
        return getFragmentProcessor().getRootFragmentType();
    }

    public IAlbumData getCurrentFragmentAlbumData() {
        ImageSelectUIFragmentHandler fragmentHandler = getFragmentProcessor();
        if (fragmentHandler == null) return null;

        ImageSelectBaseFragment currentFragment = getFragmentProcessor().getCurrentFragment();
        if (currentFragment == null) return null;

        return currentFragment.getCurrentAlbumCursor();
    }

    /**
     * 튜토리얼 관련
     */
    public void showTutorial(ISnapsImageSelectConstants.eTUTORIAL_TYPE tutorialType) {
        getTutorialProcessor().showTutorial(tutorialType);
    }

    public void showTutorial(ISnapsImageSelectConstants.eTUTORIAL_TYPE tutorialType, GIFTutorialView.CloseListener closeListener) {
        getTutorialProcessor().showTutorial(tutorialType, closeListener);
    }

    public boolean removeTutorial() {
        return getTutorialProcessor().removeTutorial();
    }

    public void removeAlbumListSelector() {
        if (albumListSelector != null)
            albumListSelector.clearAdapterData();
        albumListSelector = null;

        setTitleTextAreaEnable(false);
    }

    private int getDefaultLimitImageCount() {
        ImageSelectTrayBaseAdapter adapter = getTrayAdapter();
        if (adapter != null)
            adapter.getDefaultLimitImageCount();

        return -1;
    }

    private void registerListeners() {
        if (activity == null) return;

        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            imageSelectManager.setSelectStateChangedListener(activity);
        }

        if (ivBackKey != null) {
            ivBackKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (titleBarListener != null) titleBarListener.onClickedBackKey();
                }
            });
        }

        if (tvBackKey != null) {
            tvBackKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (titleBarListener != null) titleBarListener.onClickedBackKey();
                }
            });
        }

        if (tvNextKey != null) {
            tvNextKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIUtil.blockClickEvent(tvNextKey, UIUtil.DEFAULT_CLICK_BLOCK_TIME);
                    if (titleBarListener != null) titleBarListener.onClickedNextKey();
                }
            });
        }

        if (lyTitleTextArea != null) {
            lyTitleTextArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (titleBarListener != null) titleBarListener.onClickedTitleText();
                }
            });
        }

        if (tvErrorRetryBtn != null) {
            tvErrorRetryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    setTemplateDownloadErrorUIState(IImageSelectPublicMethods.ePHOTO_LIST_ERR_TYPE.NONE);

                    if (uiProcessorStrategy != null)
                        uiProcessorStrategy.initialize(ImageSelectUIProcessor.this);
                }
            });
        }

        setTitleBarListener(activity);
    }

    private void initGooglePhotoUtil() {
        try {
            GooglePhotoUtil.initGoogleSign(activity, new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    Dlog.d("initGooglePhotoUtil() onConnection failed to google sign in");
                }
            });
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(activity, e);
        }
    }

    private void hideTrayControl() {
        if (lyTrayArea != null) {
            lyTrayArea.setVisibility(View.GONE);
        }

        if (lyFrameMain != null) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) lyFrameMain.getLayoutParams();
            layoutParams.topMargin = (int) activity.getResources().getDimension(R.dimen.home_title_bar_height);
            lyFrameMain.setLayoutParams(layoutParams);
        }
        trayControl = new ImageSelectUITrayControl();
    }

    private void initOnlyTrayAllViewLayout() {
        if (uiProcessorStrategy == null || activity == null) return;

        hideTrayControl();

        if (onlyTrayAllViewLayout != null) {
            onlyTrayAllViewLayout.setVisibility(View.VISIBLE);
        }

        trayAdapter = uiProcessorStrategy.createTrayAdapter();
        if (trayAdapter == null) return;

//        SnapsRecyclerView trayRecyclerView = (SnapsRecyclerView) activity.findViewById(R.id.include_google_photo_style_image_select_recycler_view);
//        if (itemDecoration != null)
//            trayRecyclerView.removeItemDecoration(itemDecoration);
//
//        itemDecoration = new ImageSelectTraySpacingItemDecoration(activity, imageSelectType);
//        trayRecyclerView.addItemDecoration(itemDecoration);

        //트레이 전체 보기 클릭 영역
        View trayAllViewSelectLy = activity.findViewById(R.id.include_google_photo_style_image_select_only_tray_all_view_ly);

        //카운터 뷰
        TextView leftCountView = (TextView) activity.findViewById(R.id.google_photo_style_image_select_only_tray_count_left_tv);
        TextView rightCountView = (TextView) activity.findViewById(R.id.google_photo_style_image_select_only_tray_count_right_tv);

        //전체보기
        trayAllViewSelectLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity != null) {
                    long currentClickTime = SystemClock.uptimeMillis();
                    long elapsedTime = currentClickTime - mLastClickTime;
                    mLastClickTime = currentClickTime;

                    // 중복 클릭인 경우
                    if (elapsedTime <= MIN_CLICK_INTERVAL) {
                        return;
                    }
                    activity.onClickedTrayAllView();
                }
            }
        });

        //트레이 Add 버튼
//        if (Config.isCheckPlusButton() && !isSmartSelectType() && !isSmartRecommendBookProduct()) {
//            if (ivTrayAddBtn != null) {
//                ivTrayAddBtn.setVisibility(View.VISIBLE);
//                Answers.getInstance().logCustom(new CustomEvent("PhotoBookAddPage").putCustomAttribute("Select","AddPageButton"));
//                ivTrayAddBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (activity != null)
//                            activity.onClickedTrayAddBtn();
//                    }
//                });
//            }
//        }

//        if (Config.isSmartSnapsRecommendLayoutPhotoBook()) {
//            View trayThumbnailSingleItemView = (View) activity.findViewById(R.id.image_select_tray_thumbnail_single_item_parent_layout);
//            if (trayThumbnailSingleItemView != null) {
//                trayThumbnailSingleItemView.setVisibility(View.VISIBLE);
//                ImageSelectAdapterHolders.TrayThumbnailItemHolder singleItemHolder = new ImageSelectAdapterHolders.TrayThumbnailItemHolder(trayThumbnailSingleItemView);
//                singleItemHolder.setHolderStateToDummyItem();
//                trayControl.setTraySingleThumbnailItemHolder(singleItemHolder);
//            }
//        }

        TextView trayAllViewTitle = (TextView) activity.findViewById(R.id.google_photo_style_image_select_only_tray_title_tv);
        if (trayAllViewTitle != null) {
            if (isSmartSelectType() || isSmartRecommendBookProduct()) {
                trayAllViewTitle.setText(R.string.image_select_tray_all_view_title);
            }
        }

        trayControl.setTrayAllViewSelectLayout(trayAllViewSelectLy);
        trayControl.setLeftCountView(leftCountView);
        trayControl.setRightCountView(rightCountView);
        trayControl.setTrayAdapter(trayAdapter);
        trayAdapter.setTrayControl(trayControl);
        trayAdapter.refreshCounterInfo();
    }

    private void initTrayControl() {
        if (uiProcessorStrategy == null || activity == null) return;

        if (lyTrayArea != null)
            lyTrayArea.setVisibility(View.VISIBLE);

        trayControl = new ImageSelectUITrayControl();
        trayAdapter = uiProcessorStrategy.createTrayAdapter();
        if (trayAdapter == null) return;

        SnapsRecyclerView trayRecyclerView = (SnapsRecyclerView) activity.findViewById(R.id.include_google_photo_style_image_select_recycler_view);
        if (itemDecoration != null)
            trayRecyclerView.removeItemDecoration(itemDecoration);

        itemDecoration = new ImageSelectTraySpacingItemDecoration(activity, imageSelectType);
        trayRecyclerView.addItemDecoration(itemDecoration);

        //트레이 전체 보기 클릭 영역
        View trayAllViewSelectLy = activity.findViewById(R.id.include_google_photo_style_image_select_tray_all_view_ly);

        //카운터 뷰
        TextView leftCountView = (TextView) activity.findViewById(R.id.google_photo_style_image_select_tray_count_left_tv);
        TextView rightCountView = (TextView) activity.findViewById(R.id.google_photo_style_image_select_tray_count_right_tv);

        //전체보기
        trayAllViewSelectLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity != null) {
                    long currentClickTime = SystemClock.uptimeMillis();
                    long elapsedTime = currentClickTime - mLastClickTime;
                    mLastClickTime = currentClickTime;

                    // 중복 클릭인 경우
                    if (elapsedTime <= MIN_CLICK_INTERVAL) {
                        return;
                    }
                    activity.onClickedTrayAllView();
                }
            }
        });

        //트레이 Add 버튼
        if (Config.isCheckPlusButton() && !isSmartSelectType() && !isSmartRecommendBookProduct() && !Config.isKTBook()) {
            if (ivTrayAddBtn != null) {
                ivTrayAddBtn.setVisibility(View.VISIBLE);
                // 이 이벤트 처리를 GA 로 옮기고 싶다면 FirebaseAnalytics 사용해야함.
//                Answers.getInstance().logCustom(new CustomEvent("PhotoBookAddPage").putCustomAttribute("Select", "AddPageButton"));
                ivTrayAddBtn.setOnClickListener(v -> {
                    if (activity != null)
                        activity.onClickedTrayAddBtn();
                });
            }
        }

//        if (Config.isSmartSnapsRecommendLayoutPhotoBook()) {
//            View trayThumbnailSingleItemView = (View) activity.findViewById(R.id.image_select_tray_thumbnail_single_item_parent_layout);
//            if (trayThumbnailSingleItemView != null) {
//                trayThumbnailSingleItemView.setVisibility(View.VISIBLE);
//                ImageSelectAdapterHolders.TrayThumbnailItemHolder singleItemHolder = new ImageSelectAdapterHolders.TrayThumbnailItemHolder(trayThumbnailSingleItemView);
//                singleItemHolder.setHolderStateToDummyItem();
//                trayControl.setTraySingleThumbnailItemHolder(singleItemHolder);
//            }
//        }

        TextView trayAllViewTitle = (TextView) activity.findViewById(R.id.google_photo_style_image_select_tray_title_tv);
        if (trayAllViewTitle != null) {
            if (isSmartSelectType() || isSmartRecommendBookProduct()) {
                trayAllViewTitle.setText(R.string.image_select_tray_all_view_title);
            }
        }

        trayControl.setTrayAllViewSelectLayout(trayAllViewSelectLy);
        trayControl.setLeftCountView(leftCountView);
        trayControl.setRightCountView(rightCountView);
        trayControl.setTrayThumbRecyclerView(trayRecyclerView);
        trayControl.setTrayAdapter(trayAdapter);
        trayAdapter.setTrayControl(trayControl);
        trayAdapter.refreshCounterInfo();

        TrayLinearLayoutManager linearLayoutManager = new TrayLinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        trayRecyclerView.setLayoutManager(linearLayoutManager);
        trayRecyclerView.setAdapter(trayAdapter);
    }

    private ImageSelectUIAnimation getAnimationProcessor() {
        if (animationProcessor == null) animationProcessor = new ImageSelectUIAnimation();
        return animationProcessor;
    }

    private ImageSelectUITutorial getTutorialProcessor() {
        if (tutorialProcessor == null) {
            tutorialProcessor = new ImageSelectUITutorial(activity);
        }
        return tutorialProcessor;
    }

    private ImageSelectUIFragmentHandler getFragmentProcessor() {
        if (fragmentProcessor == null)
            fragmentProcessor = new ImageSelectUIFragmentHandler(activity);
        return fragmentProcessor;
    }

    private ImageSelectUIAlbumListSelector getAlbumListSelector() {
        if (albumListSelector == null) {
            albumListSelector = new ImageSelectUIAlbumListSelector(activity, activity);
        }
        return albumListSelector;
    }

    //사진 인화 등에서 선택한 사진을 삭제했을 때, 삭제된 이미지 키 리스트를 반환한다.
    private List<String> findRemovedTrayImageKeyList() {
        List<String> removedKeyList = new ArrayList<>();

        ImageSelectTrayBaseAdapter adapter = getTrayAdapter();
        if (adapter != null) {
            ArrayList<ImageSelectTrayCellItem> arCellList = adapter.getTrayCellItemList();
            if (arCellList != null) {
                for (ImageSelectTrayCellItem cellItem : arCellList) {
                    if (cellItem == null) continue;

                    if (!ImageSelectUtils.isExistImageKeyFromSelectHolder(cellItem))
                        removedKeyList.add(cellItem.getImageKey());
                }
            }
        }

        return removedKeyList;
    }

    public String getLastSelectedAlbumId() {
        return lastSelectedAlbumId;
    }

    public void setLastSelectedAlbumId(String lastSelectedAlbumId) {
        this.lastSelectedAlbumId = lastSelectedAlbumId;
    }

    public Set<IImageSelectListUpdateListener> getListUpdateListeners() {
        return setListUpdateListeners;
    }

    public void registerListUpdateListener(IImageSelectListUpdateListener listUpdateListener) {
        if (setListUpdateListeners == null) setListUpdateListeners = new HashSet<>();
        setListUpdateListeners.add(listUpdateListener);
    }

    public void unRegisterListUpdateListener(IImageSelectListUpdateListener listUpdateListener) {
        if (setListUpdateListeners == null) setListUpdateListeners = new HashSet<>();
        if (setListUpdateListeners.contains(listUpdateListener))
            setListUpdateListeners.remove(listUpdateListener);
    }

    public void notifyListUpdateListener(String imgKey) {
        ImageSelectManager dataTransManager = ImageSelectManager.getInstance();
        if (dataTransManager != null) {
            Set<IImageSelectListUpdateListener> listUpdateListeners = getListUpdateListeners();
            if (listUpdateListeners != null) {
                for (IImageSelectListUpdateListener listUpdateListener : listUpdateListeners) {
                    if (listUpdateListener != null)
                        listUpdateListener.onUpdatedPhotoList(imgKey);
                }
            }
        }
    }

    public void notifyAlbumListUpdateListener(IAlbumData cursor) {
        ImageSelectManager dataTransManager = ImageSelectManager.getInstance();
        if (dataTransManager != null) {
            Set<IImageSelectListUpdateListener> listUpdateListeners = getListUpdateListeners();
            if (listUpdateListeners != null) {
                for (IImageSelectListUpdateListener listUpdateListener : listUpdateListeners) {
                    if (listUpdateListener != null)
                        listUpdateListener.onChangedAlbumCursor(cursor);
                }
            }
        }
    }

    //    이게 여기 저기서 막 부르는 것 같다 좀 잘 보자..
//    해상도 경고 아이콘도 봐야한다.
    public void putSelectedImageData(String key, MyPhotoSelectImageData imgData) {
        boolean shouldNotifyListUpdate = false;
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            ImageSelectImgDataHolder selectData = imageSelectManager.getImageSelectDataHolder();
            if (selectData != null) {
                shouldNotifyListUpdate = selectData.putData(key, imgData);
            }
        }

        if (shouldNotifyListUpdate)
            notifyListUpdateListener(key);
    }

    public void removeSelectedImageData(String key) {
        //커버 이미지라면 플래그를 삭제한다.
        removeCoverImageMapkey(key);

        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            ImageSelectImgDataHolder selectData = imageSelectManager.getImageSelectDataHolder();
            if (selectData != null && selectData.isSelected(key)) {
                selectData.removeData(key);
            }
        }

        notifyListUpdateListener(key);
    }

    public void refreshPrevSelectedImageList() throws Exception {
        ImageSelectUtils.removeAllImageData();

        DataTransManager dataManager = DataTransManager.getInstance();
        if (dataManager != null) {
            ImageSelectImgDataHolder imageSelectImgDataHolder = dataManager.getImageSelectDataHolder();
            if (imageSelectImgDataHolder != null) {
                ArrayList<MyPhotoSelectImageData> photoList = imageSelectImgDataHolder.getNormalData();
                if (photoList != null && !photoList.isEmpty()) {
                    for (MyPhotoSelectImageData imageData : photoList) {
                        String imgKey = imageData.KIND + "_" + imageData.IMAGE_ID;
                        putSelectedImageData(imgKey, imageData);
                    }
                    notifyListUpdateListener(null);
                }
            }
        }
    }

    public void recoveryPrevSelectedImageList() throws Exception {
        ImageSelectUtils.removeAllImageData();

        DataTransManager dataManager = DataTransManager.getInstance();
        if (dataManager != null) {
            ImageSelectImgDataHolder imageSelectImgDataHolder = dataManager.getImageSelectDataHolder();
            if (imageSelectImgDataHolder != null) {
                ArrayList<MyPhotoSelectImageData> photoList = imageSelectImgDataHolder.getNormalData();
                if (photoList != null && !photoList.isEmpty()) {
                    for (MyPhotoSelectImageData imageData : photoList) {
                        String imgKey = imageData.KIND + "_" + imageData.IMAGE_ID;
                        getTrayAdapter().insertPhotoThumbnailOnTrayItem(new TrayAdapterInsertParam.Builder().setImageMapKey(imgKey).setImageData(imageData).create());
                    }
                    notifyListUpdateListener(null);

                    setTrayScrollToEnd();
                }
            }
        }
    }

    private void setTrayScrollToEnd() {
        try {
            trayControl.getTrayThumbRecyclerView().scrollToPosition(getTrayAdapter().getItemCount() - 1);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public boolean isContainCoverImageKey(String mapKey) {
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        return smartSnapsManager.isContainCoverPhotoMapKey(mapKey);
    }

    public void appendMapKeyOfCover(String mapKeyOfCover) {
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        smartSnapsManager.appendCoverPhotoMapKey(mapKeyOfCover);
    }

    public boolean isEmptyCoverImageKey() {
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        return !smartSnapsManager.isExistCoverPhotoMapKey();
    }

    public void setCurrentMaxImageCount(int maxImageCount) {
        this.maxImageCount = maxImageCount;
    }

    public int getCurrentMaxImageCount() {
        return maxImageCount;
    }

    public void setCurrentImageCount(int currentImageCount) {
        this.currentImageCount = currentImageCount;
    }

    public int getCurrentImageCount() {
        return currentImageCount;
    }

    public void showPngAndJpgDialog(final View.OnClickListener onClickListener) throws Exception {
        boolean complete = Setting.getBoolean(getActivity(), DIALOG_COMPLETE);
        if (complete) {
            onClickListener.onClick(null);
            return;
        }
        ;

        final CustomizeDialog customizeDialog = new CustomizeDialog(getActivity());
        customizeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customizeDialog.setContentView(R.layout.transparency_photo_card_infomation_dialog);

        View confirmBtn = customizeDialog.findViewById(R.id.btn_confim);
        if (confirmBtn != null) {
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customizeDialog.cancel();
                }
            });
        }
        View notAgainBtn = customizeDialog.findViewById(R.id.btn_not_again);
        final ImageView imageButtonNotAgain = customizeDialog.findViewById(R.id.checkBox_not_again);
        if (notAgainBtn != null) {
            notAgainBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageButtonNotAgain.setSelected(!imageButtonNotAgain.isSelected());
                    if (imageButtonNotAgain.isSelected()) {
                        Setting.set(getActivity(), DIALOG_COMPLETE, true);
                    } else {
                        Setting.set(getActivity(), DIALOG_COMPLETE, false);
                    }
                    onClickListener.onClick(v);
                }
            });
        }

        customizeDialog.setCanceledOnTouchOutside(false);
        customizeDialog.setCancelable(false);
        customizeDialog.show();
    }

}
