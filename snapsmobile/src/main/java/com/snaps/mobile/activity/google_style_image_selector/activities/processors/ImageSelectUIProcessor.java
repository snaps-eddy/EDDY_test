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
     * - if??? ????????? ????????? ?????? ??????, ????????? ????????? ?????? ??? ?????? ImageSelectPerformerFactory?????? ????????? ????????? ?????? ????????????.
     * <p>
     * - Intent??? ???????????? ????????? ?????? ????????????, ImageSelectIntentData??? ?????? ?????? ??????.
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

    //Tray ??????
    private ImageSelectUITrayControl trayControl = null;
    private View lyTrayArea = null;
    private View ivTrayAddBtn = null;
    private ImageSelectTrayBaseAdapter trayAdapter;
    private ImageSelectTraySpacingItemDecoration itemDecoration = null;

    private int limitImageCount = 0; //????????? ?????? ????????? ??? ?????? ???????????? ???

    private IImageSelectUIProcessorStrategy uiProcessorStrategy = null;    //????????? UI ????????? ?????????..

    private boolean isLandScapeMode = false; //?????? ?????? ??????..

    private IImageSelectProductPerform productPerformer = null;    //???????????? ??? ????????? ???????????? ?????????

    private IImageSelectTitleBarListener titleBarListener = null;    //??????, ??????(??????), ????????? ?????? ?????? ??????

    private ImageSelectUIAnimation animationProcessor = null;    //??????????????? ??????

    private ImageSelectUITutorial tutorialProcessor = null;    //???????????? ??????

    private ImageSelectUIFragmentHandler fragmentProcessor = null;    //?????????, SNS????????? ???????????????, ???????????? Fragment??? ????????????.

    private ImageSelectUIAlbumListSelector albumListSelector = null;    //????????? ????????? ???, ???????????? ?????? ?????? UI

    private ImageSelectUIPhotoFilter photoFilter = null;    //????????? ?????? ??????

    private ImageSelectSNSData snsData = null;

    private Set<IImageSelectListUpdateListener> setListUpdateListeners = null;

    private String lastSelectedAlbumId = null;

    private int maxImageCount = 0; // ?????? ????????? ????????? ???????????? (????????? ?????? ????????? ?????? ?????? ????????? ?????? ??????????????????)

    private int currentImageCount = 0;// ?????? ?????? ????????? ????????????

    private static final long MIN_CLICK_INTERVAL = 1000;

    private long mLastClickTime = 0;

    public void createUIProcessor() {
        if (activity == null) return;

        ImageSelectIntentData intentData = getIntentData();
        if (intentData == null) return;

        findAllViewById();

        //?????? ?????? ?????? ?????? ????????? ?????? performer
        IImageSelectProductPerform performer = ImageSelectPerformerFactory.createPerformer(activity, intentData.getHomeSelectProduct());
        setPerformer(performer);

        //?????? ????????? UI ????????? ?????? ?????? ??????...
        uiProcessorStrategy = ImageSelectUIProcessorStrategyFactory.createImageSelectUI(intentData);
        uiProcessorStrategy.initialize(this);

        //????????? ?????????
        initialize();

        uiProcessorStrategy.postInitialized();
    }

    public ImageSelectActivityV2 getActivity() {
        return activity;
    }

    public void initialize() {
        if (activity == null) return;

        //???????????? ????????? ????????? ?????????
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

    //????????? ?????? ???????????? ?????? ???????????? ???????????????, ???????????? ???????????? ?????? ????????? ????????? ???, ???????????? ?????? ??? ???.
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

    //?????? ???????????? ???????????? BackKey??? ????????? ?????? ?????? ?????? ???????????? ???????????? ???
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

    //?????????????????? ????????? ?????? ??? ?????? ?????? ????????? ???, ?????? ????????? ????????????.
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

    //????????? ????????? ????????? ????????????, use recycled bitmap ??? ???????????? ????????? ??? ???????????? ??????.
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
     * Fragment?????? ????????? ???????????? ???, ?????? ?????? ?????? ?????? / ??????????????? ?????? / Holder??? ?????? ????????? ????????????.
     */
    public void tryInsertImageDataToHolder(final ImageSelectAdapterHolders.PhotoFragmentItemHolder fragmentViewHolder) throws Exception {
        if (fragmentViewHolder == null || fragmentViewHolder.getMapKey() == null || fragmentViewHolder.getImgData() == null || isLockAddItem() || activity == null || activity.isFinishing())
            return;

        //?????? ???????????? ???????????? ??????..
        checkCoverImageLabel(fragmentViewHolder);

        ImageSelectTrayCellItem cellItem = findNextEmptyCellItem();

        //cellItem??? ??? ???????????? ?????? ???????????? ?????? ????????? ?????? ????????????.
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
                        //Tray??? ????????? ??????
                        fragmentViewHolder.getImgData().isNoPrint = fragmentViewHolder.isDisableClick();
                        adapter.insertPhotoThumbnailOnTrayItem(new TrayAdapterInsertParam.Builder().setImageMapKey(fragmentViewHolder.getMapKey()).setImageData(fragmentViewHolder.getImgData()).create()); //???????????? ????????? ?????????.
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
                    adapter.insertPhotoThumbnailOnTrayItem(new TrayAdapterInsertParam.Builder().setImageMapKey(fragmentViewHolder.getMapKey()).setImageData(fragmentViewHolder.getImgData()).create()); //???????????? ????????? ?????????.
                }
            } else if (Config.isSimplePhotoBook() || Config.isSimpleMakingBook()) {
                /**
                 * ???????????????, ?????? ?????????????????? ????????? ????????? ????????? ??????.
                 */
                ImageSelectTrayBaseAdapter adapter = getTrayAdapter();
                if (adapter != null) {

                    putSelectedImageData(fragmentViewHolder.getMapKey(), fragmentViewHolder.getImgData());

                    //insertPhotoThumbnailOnTrayItem??? ?????? ???????????? ????????? ????????? confirm??? ?????? ??????.
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

        //????????? ?????? ?????????
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

        //?????? ?????????
        if (trayHolder.getDeleteIcon() != null) {
            trayHolder.getDeleteIcon().setVisibility(cellItem.getCellState() == PHOTO_THUMBNAIL && cellItem.isSelected() ? View.VISIBLE : View.GONE);
        }

        //????????? ?????????
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
        //?????? ???????????? ???????????? ??????..
        RecyclerView.ViewHolder viewHolder = item.getViewHolder();
        if (viewHolder != null && viewHolder instanceof ImageSelectAdapterHolders.PhotoFragmentItemHolder) {
            checkCoverImageLabel((ImageSelectAdapterHolders.PhotoFragmentItemHolder) item.getViewHolder());
        }

        ImageSelectTrayCellItem cellItem = findNextEmptyCellItem();
        //cellItem??? ??? ???????????? ?????? ???????????? ?????? ????????? ?????? ????????????.
        if (cellItem != null) {
            ImageSelectTrayBaseAdapter adapter = getTrayAdapter();
            if (adapter != null) {
                //Tray??? ????????? ??????
                return adapter.insertPhotoThumbnailOnTrayItem(
                        new TrayAdapterInsertParam.Builder().setImageMapKey(item.getImageKey()).setImageData(item.getImgData()).setArrayInsert(true).setArrayInsertAndLastItem(last).create()); //???????????? ????????? ?????????.
            }
        } else {
            /**
             * ???????????????, ?????? ?????????????????? ????????? ????????? ????????? ??????.
             */
            if (Config.isSimplePhotoBook() || Config.isSimpleMakingBook()) {
                ImageSelectTrayBaseAdapter adapter = getTrayAdapter();
                if (adapter != null) {
                    return adapter.insertPhotoThumbnailOnTrayItem(new TrayAdapterInsertParam.Builder().setImageMapKey(item.getImageKey()).setImageData(item.getImgData()).setArrayInsert(true).setArrayInsertAndLastItem(last).create()); //???????????? ????????? ?????????.
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
     * ?????? ??????
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

        //bringToFront ????????? ???????????? ??? ???.
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
            nextButton.setText(activity.getString(R.string.confirm)); // ??????????????????...
            nextButton.setVisibility(View.VISIBLE);// ????????????

            if (Config.SELECT_SINGLE_CHOOSE_TYPE == selectProduct) {
                if (isDiaryProfilePhoto)
                    nextButton.setText(activity.getString(R.string.next));
            } else if (Config.SELECT_SNAPS_DIARY == selectProduct || titleId == R.string.diary_album_title) {
                nextButton.setText(activity.getString(R.string.next));
                nextButton.setVisibility(View.VISIBLE);// ????????????
            }
        }
        // ????????? ????????? ??????...
        else if (titleId == R.string.select_size) {
            nextButton.setText(R.string.product_comment); // ??????????????????...
            nextButton.setVisibility(View.VISIBLE);
        } else {// ?????? ??? ????????????
            if (isSmartRecommendBookProduct()) {
                nextButton.setText(activity.getString(R.string.next));
            }

            nextButton.setVisibility(View.VISIBLE);// ????????????
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

        //FIXME ?????? ????????? ?????? ??????, ????????? ???????????? ?????? ????????? ?????? ??????.
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

    //??? ?????? ???????????? ?????? ???????????? ????????? ??? ????????? ??????
    public void switchAlbumListState() throws Exception {
        if (getAlbumListSelector() == null || getAlbumListSelector().getAlbumListCount() < 1)
            return; //????????? ????????? ????????? ?????? ??????.

        boolean isShow = !lyAlbumListSelector.isShown();
        getAnimationProcessor().startVerticalTranslateAnimation(lyAlbumListSelector, ivTitleTextArrow, isShow, ImageSelectUIAnimation.ANIM_TIME_ALBUM_LIST_SELECTOR);
    }

    //??? ?????? ???????????? ?????? ????????????  ??? ????????? ??????
    public boolean hideAlbumListSelector() throws Exception {
        boolean isShowing = lyAlbumListSelector != null && lyAlbumListSelector.isShown();
        getAnimationProcessor().startVerticalTranslateAnimation(lyAlbumListSelector, ivTitleTextArrow, false, ImageSelectUIAnimation.ANIM_TIME_ALBUM_LIST_SELECTOR);
        return isShowing;
    }

    //?????? Fragment ?????? (???????????? ?????? ????????? ?????? ????????? ????????? ?????? ??? ??????.)
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
            //????????? ????????? ?????? ?????? ?????? ?????????????????? ????????????.
            if (Config.useKorean()) {
                boolean notAgain = Setting.getBoolean(getActivity(), PASSPORT_DIALOG_COMPLETE, false);
                if (!notAgain) {
                    PassPortRuleDialog dialog = new PassPortRuleDialog(getActivity(), true);
                    dialog.show();
                }
            }
        }
    }

    //????????? ????????? ????????????
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

    //?????????, ?????? ???????????? ?????? ???????????? 1?????? ????????? ????????? ??? ??????.
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

        //????????? ???????????? ?????? ????????? ????????????.
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
     * Fragment ??????
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
     * ???????????? ??????
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

        //????????? ?????? ?????? ?????? ??????
        View trayAllViewSelectLy = activity.findViewById(R.id.include_google_photo_style_image_select_only_tray_all_view_ly);

        //????????? ???
        TextView leftCountView = (TextView) activity.findViewById(R.id.google_photo_style_image_select_only_tray_count_left_tv);
        TextView rightCountView = (TextView) activity.findViewById(R.id.google_photo_style_image_select_only_tray_count_right_tv);

        //????????????
        trayAllViewSelectLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity != null) {
                    long currentClickTime = SystemClock.uptimeMillis();
                    long elapsedTime = currentClickTime - mLastClickTime;
                    mLastClickTime = currentClickTime;

                    // ?????? ????????? ??????
                    if (elapsedTime <= MIN_CLICK_INTERVAL) {
                        return;
                    }
                    activity.onClickedTrayAllView();
                }
            }
        });

        //????????? Add ??????
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

        //????????? ?????? ?????? ?????? ??????
        View trayAllViewSelectLy = activity.findViewById(R.id.include_google_photo_style_image_select_tray_all_view_ly);

        //????????? ???
        TextView leftCountView = (TextView) activity.findViewById(R.id.google_photo_style_image_select_tray_count_left_tv);
        TextView rightCountView = (TextView) activity.findViewById(R.id.google_photo_style_image_select_tray_count_right_tv);

        //????????????
        trayAllViewSelectLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity != null) {
                    long currentClickTime = SystemClock.uptimeMillis();
                    long elapsedTime = currentClickTime - mLastClickTime;
                    mLastClickTime = currentClickTime;

                    // ?????? ????????? ??????
                    if (elapsedTime <= MIN_CLICK_INTERVAL) {
                        return;
                    }
                    activity.onClickedTrayAllView();
                }
            }
        });

        //????????? Add ??????
        if (Config.isCheckPlusButton() && !isSmartSelectType() && !isSmartRecommendBookProduct() && !Config.isKTBook()) {
            if (ivTrayAddBtn != null) {
                ivTrayAddBtn.setVisibility(View.VISIBLE);
                // ??? ????????? ????????? GA ??? ????????? ????????? FirebaseAnalytics ???????????????.
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

    //?????? ?????? ????????? ????????? ????????? ???????????? ???, ????????? ????????? ??? ???????????? ????????????.
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

    //    ?????? ?????? ????????? ??? ????????? ??? ?????? ??? ??? ??????..
//    ????????? ?????? ???????????? ????????????.
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
        //?????? ??????????????? ???????????? ????????????.
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
