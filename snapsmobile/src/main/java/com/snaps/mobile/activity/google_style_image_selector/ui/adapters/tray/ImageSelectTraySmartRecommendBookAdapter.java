package com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryRecyclerCustomAdapter;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.strategies.ImageSelectUIProcessorStrategyFactory;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectUITrayControl;
import com.snaps.mobile.activity.google_style_image_selector.datas.TrayAdapterInsertParam;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectPublicMethods;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectStateChangedListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items.ImageSelectTrayCellItem;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;

import java.io.File;
import java.util.ArrayList;

import font.FTextView;

import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.SMART_SNAPS_ANALYSIS_PHOTO_BOOK_MAX_PHOTO_COUNT;
import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.SMART_SNAPS_ANALYSIS_PHOTO_BOOK_MIN_PHOTO_COUNT;
import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.eTRAY_CELL_STATE.EMPTY;
import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.eTRAY_CELL_STATE.PHOTO_THUMBNAIL;
import static com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils.getSelectImageHolder;

/**
 * Created by ysjeong on 16. 3. 9..
 */
public class ImageSelectTraySmartRecommendBookAdapter extends ImageSelectTrayBaseAdapter implements ISnapsDiaryRecyclerCustomAdapter {
    private static final String TAG = ImageSelectTraySmartRecommendBookAdapter.class.getSimpleName();
    private ImageSelectTrayCellItem photoPrintDummyView = null;

    public ImageSelectTraySmartRecommendBookAdapter(ImageSelectActivityV2 imageSelectActivityV2) {
        this(imageSelectActivityV2, imageSelectActivityV2);
    }

    protected ImageSelectTraySmartRecommendBookAdapter(Context context, IImageSelectPublicMethods imageSelectPublicMethods) {
        super(context, imageSelectPublicMethods);

        initTrayItemList();

        //트레이를 향해 날아가는 애니메이션을 처리하기 위해 아무것도 없을 때는 더미를 하나 깔아 놓음.
        setDummyItemState();
    }

    @Override
    public void setTrayAllViewList(ArrayList<ImageSelectTrayCellItem> allViewList, int defaultSelectedId) {
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_select_tray_thumbnail_item, parent, false);
        return new ImageSelectAdapterHolders.TrayThumbnailItemHolder(view);
    }

    @Override
    public void setData(ArrayList<SnapsPage> newList) {
        super.setData(newList);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ImageSelectTrayCellItem cellItem = getTrayCellItem(position);
        onBindViewThumbnailHolder(holder, cellItem);
    }

    private void setDummyHolder(ImageSelectAdapterHolders.TrayThumbnailItemHolder holder) {
        if (holder == null) return;

        TextView photoThumbnailTextView = holder.getSelectorTextView();
        if (photoThumbnailTextView != null) {
//            photoThumbnailTextView.setBackgroundResource(R.drawable.img_tray_cover_empty_holder);
//            photoThumbnailTextView.setText(R.string.cover);
            photoThumbnailTextView.setVisibility(View.GONE);
        }

        if (holder.getSelector() != null)
            holder.getSelector().setVisibility(View.GONE);

        if (holder.getDeleteIcon() != null)
            holder.getDeleteIcon().setVisibility(View.GONE);

        if (holder.getNoPrintIcon() != null)
            holder.getNoPrintIcon().setVisibility(View.GONE);

        if (holder.getImgLabel() != null)
            holder.getImgLabel().setVisibility(View.GONE);

        if (holder.getPhotoThumbnail() != null)
            holder.getPhotoThumbnail().setVisibility(View.GONE);

        if (holder.getParentView() != null) {
            holder.getParentView().setOnClickListener(null);
        }
    }

    @Override
    public int getEmptyCellCount() {
        return 0;
    }

    @Override
    public ImageSelectTrayCellItem findNextEmptyCellItem() {
        return null;
//        if (trayCellItemList == null || trayCellItemList.size() == 0) return null;
//        if (!isExistPhotoOnCover()) return getItem(0);
//        return getItem(trayCellItemList.size() - 1);
    }

    public void onBindViewThumbnailHolder(RecyclerView.ViewHolder holder, final ImageSelectTrayCellItem cellItem) {
        if (cellItem == null || holder == null) return;

        ImageSelectAdapterHolders.TrayThumbnailItemHolder trayHolder = (ImageSelectAdapterHolders.TrayThumbnailItemHolder) holder;

        cellItem.setHolder(trayHolder);

//        if (cellItem.getCellState() == EMPTY_DUMMY) {
//            setDummyHolder(trayHolder);
//            return;
//        }

        FTextView holderTextView = trayHolder.getSelectorTextView();
        if (holderTextView != null) holderTextView.setVisibility(View.GONE);

        //선택되었는 지
        ImageView selector = trayHolder.getSelector();
        if (selector != null) {
            if (cellItem.getCellState() == PHOTO_THUMBNAIL) {
                if (cellItem.isSelected()) {
                    selector.setBackgroundResource(cellItem.getCellState() == PHOTO_THUMBNAIL ? R.drawable.shape_red_e36a63_fill_solid_border_rect : R.drawable.shape_red_e36a63_border_rect);
                    selector.setVisibility(View.VISIBLE);
                } else if (cellItem.isNoPrint()) {
                    selector.setBackgroundColor(Color.parseColor("#66000000"));
                    selector.setVisibility(View.VISIBLE);
                } else {
                    selector.setBackgroundResource(R.drawable.shape_gray_eeeeee_transparent_solid_border_rect);
                    selector.setVisibility(View.VISIBLE);
                }
            } else {
                selector.setVisibility(View.GONE);
            }
        }

        //선택된 사진 섬네일
        ImageView photoThumbnail = trayHolder.getPhotoThumbnail();
        if (photoThumbnail != null) {
            if (cellItem.getCellState() == PHOTO_THUMBNAIL) {
                MyPhotoSelectImageData imageData = ImageSelectUtils.getSelectedImageData(cellItem.getImageKey());
                if (imageData != null) {
                    String photoThumbnailPath = imageData.THUMBNAIL_PATH;
                    photoThumbnail.setImageBitmap(null);
                    photoThumbnail.setVisibility(View.VISIBLE);

                    if (photoThumbnailPath != null && photoThumbnailPath.length() > 0) {
                        ImageSelectUtils.loadImage(context, photoThumbnailPath, UIUtil.convertDPtoPX(context, 50), photoThumbnail, ImageView.ScaleType.CENTER_CROP, true);
                        photoThumbnail.setRotation(imageData.ROTATE_ANGLE_THUMB == -1 ? 0 : imageData.ROTATE_ANGLE_THUMB);
                    }
                }
            } else {
                photoThumbnail.setVisibility(View.GONE);
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

        if (trayHolder.getParentView() != null) {
            trayHolder.getParentView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickedTrayItem(cellItem);
                }
            });
        }

        FTextView imgLabel = trayHolder.getImgLabel();
        if (imgLabel != null) {
            if (cellItem.isCoverPhoto()) {
                imgLabel.setVisibility(View.VISIBLE);
            } else {
                imgLabel.setVisibility(View.GONE);
            }
        }
    }

    private boolean isSelectedCoverImage(String mapKey) {
        if (imageSelectPublicMethods == null) return false;
        ImageSelectUIProcessor uiProcessor = imageSelectPublicMethods.getUIProcessor();
        return uiProcessor != null && uiProcessor.isContainCoverImageKey(mapKey);
    }

    //트레이를 선택했을 때
    @Override
    public void onClickedTrayItem(ImageSelectTrayCellItem item) {
        if (item == null) return;
        ISnapsImageSelectConstants.eTRAY_CELL_STATE cellState = item.getCellState();
        if (cellState != PHOTO_THUMBNAIL) return;

        selectTrayItem(item.getCellId(), false);
        sendTrayItemSelectEvent(item);
    }

    @Override
    protected ISnapsImageSelectConstants.eTRAY_CELL_STATE getDefaultTrayState() {
        return EMPTY;
    }

    /**
     * 최대 추가할 수 있는 페이지
     *
     * @return 최대 페이지를 초과한다면 true 반환
     */
    @Override
    public boolean checkExcessMaxPage() {
        return checkExcessMaxPhoto();
    }

    @Override
    public boolean checkExcessMaxPhotoForDragging() {
        return checkExcessMaxPhoto();
    }

    @Override
    public boolean checkExcessMaxPhoto() {
        if (Config.isActiveImageAutoSelectFunction()) return false;

        ImageSelectImgDataHolder holder = getSelectImageHolder();
        if (holder != null) {
            int maxCount = imageSelectPublicMethods != null && imageSelectPublicMethods.getUIProcessor() != null ? imageSelectPublicMethods.getUIProcessor().getMaxImageCount() : 0;
            if (holder.getMapSize() > maxCount) { // 데이터// 추가
                MessageUtil.toast(context, R.string.disable_add_photo);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void initTrayCells(ArrayList<SnapsPage> pageList) {
    }

    //트레이 좌측 하단에 카운터 정보
    @Override
    public void refreshCounterInfo() {
        ImageSelectUITrayControl trayControl = getTrayControl();
        if (trayControl == null || trayCellItemList == null) return;

        int photoCount = 0;
        int totalTemplateCount = 0;
        for (ImageSelectTrayCellItem cellItem : trayCellItemList) {
            if (cellItem != null) {
                if (cellItem.isPlusBtn() || cellItem.isDummyItem()) continue;

                if (cellItem.getImageKey() != null && cellItem.getImageKey().length() > 0)
                    photoCount++;
                totalTemplateCount++;
            }
        }

        if (pageCountInfo != null) {
            pageCountInfo.setTotalTemplateImageCount(totalTemplateCount);
            pageCountInfo.setCurrentSelectedImageCount(photoCount);
        }


        TextView tvLeftCountView = trayControl.getLeftCountView();
        if (tvLeftCountView != null) {
            tvLeftCountView.setText(String.valueOf(photoCount));
        }

//        TextView tvRightCountView = trayControl.getRightCountView();
//        if (tvRightCountView != null) {
//            tvRightCountView.setVisibility(View.VISIBLE);
//            tvRightCountView.setText("/" + String.valueOf(SmartSnapsConstants.SMART_SNAPS_ANALYSIS_PHOTO_BOOK_MAX_PHOTO_COUNT));
//        }

        setDummyItemState();
    }

    private void setDummyItemState() {
        boolean shouldAddDummyItem = !isExistPhotoOnCover() && !isExistDummyView() && !isTrayAllViewMode();

        if (trayCellItemList == null) return;

        if (shouldAddDummyItem) {
            if (photoPrintDummyView == null) {
                photoPrintDummyView = new ImageSelectTrayCellItem(context, ISnapsImageSelectConstants.INVALID_VALUE, ISnapsImageSelectConstants.eTRAY_CELL_STATE.EMPTY_DUMMY);
            }

            trayCellItemList.add(0, photoPrintDummyView);

            refreshCellItemId();

            notifyDataSetChanged();
        } else if (isExistPhotoOnCover()) {
            if (photoPrintDummyView != null) {
                trayCellItemList.remove(photoPrintDummyView);
                photoPrintDummyView = null;

                refreshCellItemId();

                notifyDataSetChanged();
            } else {
                refreshCellItemId();
            }
        } else {
            refreshCellItemId();
        }
    }

    @Override
    public boolean isExistPhotoOnCover() {
        if (trayCellItemList == null || trayCellItemList.isEmpty()) return false;

        for (ImageSelectTrayCellItem cellItem : trayCellItemList) {
            if (cellItem == null) continue;
            if (cellItem.isCoverPhoto()) return true;
        }

        return false;
    }

    @Override
    public boolean isExistDummyView() {
        if (trayCellItemList == null || trayCellItemList.isEmpty()) return false;

        for (ImageSelectTrayCellItem cellItem : trayCellItemList) {
            if (cellItem == null) continue;
            if (cellItem.getCellState() == ISnapsImageSelectConstants.eTRAY_CELL_STATE.EMPTY_DUMMY) return true;
        }

        return false;
    }

    /**
     * 새로운 셀을 생성한다.
     *
     * @return 추가한 cell의 id를 반환한다.
     */
    private int createNewCell() {
        ImageSelectTrayCellItem cellItem = new ImageSelectTrayCellItem(context, getOnlyThumbnailCellCount(), ISnapsImageSelectConstants.eTRAY_CELL_STATE.EMPTY);
        addTrayItem(cellItem);

        return cellItem.getCellId();
    }

    @Override
    protected int getOnlyThumbnailCellCount() {
        if (trayCellItemList == null) return 0;
        int result = 0;
        for (ImageSelectTrayCellItem cellItem : trayCellItemList) {
            if (cellItem == null) continue;

            if (cellItem.getCellState() == ISnapsImageSelectConstants.eTRAY_CELL_STATE.EMPTY_DUMMY)
                return 0;

            if (cellItem.getCellState() == ISnapsImageSelectConstants.eTRAY_CELL_STATE.PHOTO_THUMBNAIL
                    || cellItem.getCellState() == ISnapsImageSelectConstants.eTRAY_CELL_STATE.TEMPLATE)
                result++;
        }
        return result;
    }

    protected void addTrayItem(ImageSelectTrayCellItem item) {
        if (trayCellItemList == null)
            initTrayItemList();

        int insertPosition = !isExistPhotoOnCover() ? 0 : trayCellItemList.size();
        insert(item, insertPosition);
    }

    private void setSelectedImageInfo(String key) {
        MyPhotoSelectImageData imageData = ImageSelectUtils.getSelectedImageData(key);
        if (imageData == null) return;
        try {
            if (Const_VALUES.SELECT_PHONE == imageData.KIND) {// local file
                if (imageData.PATH != null && imageData.PATH.length() > 0 && !imageData.PATH.startsWith("http")) {
                    imageData.F_IMG_FILESIZE = new File(imageData.PATH).length();
                    imageData.ROTATE_ANGLE = CropUtil.getExifOrientation(imageData.PATH);
                }
                Dlog.d("setSelectedImageInfo() image path:" + imageData.PATH + ", ROTATE_ANGLE:" + imageData.ROTATE_ANGLE);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        ImageSelectImgDataHolder holder = getSelectImageHolder();
        if (holder != null)
            imageData.selectIdx = isExistPhotoOnCover() ? holder.getMapSize() : 0;

        imageData.isDelete = false;
    }

    @Override
    public boolean insertPhotoThumbnailOnTrayItem(TrayAdapterInsertParam trayAdapterInsertParam) {
        if (trayAdapterInsertParam == null) return false;

        String key = trayAdapterInsertParam.getImageMapKey();
        MyPhotoSelectImageData imageData = trayAdapterInsertParam.getImageData();

        if (checkExcessMaxPhoto()) {
            imageSelectPublicMethods.removeSelectedImageData(key);
            return false;
        }

        if (isPageSyncLock()) return false;
        setPageSyncLock(true);

        int cellItemId = createNewCell();
        if (cellItemId >= 0) {
            setSelectedImageInfo(key);

            ImageSelectTrayCellItem cellItem = getTrayCellItemById(cellItemId);
            if (cellItem != null) {
                cellItem.setImageKey(key);
                cellItem.setCellState(PHOTO_THUMBNAIL);
                cellItem.setNoPrint(imageData.isNoPrint);
                cellItem.setCoverPhoto(isSelectedCoverImage(key));

                if (trayAdapterInsertParam.isArrayInsert()) {
                    if (trayAdapterInsertParam.isArrayInsertAndLastItem()) {
                        notifyDataSetChanged();
                        scrollToCenterTrayView(cellItem.getCellId());

                        checkRecommendBookImageSelectTutorial();
                    }
                } else {
                    notifyItemChanged(cellItem.getCellId());
                    scrollToCenterTrayView(cellItem.getCellId());

                    checkRecommendBookImageSelectTutorial();
                }
            }

            imageSelectPublicMethods.putSelectedImageData(key, imageData);

            refreshCounterInfo();
        }

        createThumbnailCacheWithImageData(imageData);

        setPageSyncLock(false);
        return true;
    }

    private void checkRecommendBookImageSelectTutorial() {
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        int count = imageSelectManager.getSelectedImageCount();
        if (count >= SMART_SNAPS_ANALYSIS_PHOTO_BOOK_MIN_PHOTO_COUNT && count <= SMART_SNAPS_ANALYSIS_PHOTO_BOOK_MAX_PHOTO_COUNT) {
            showRecommendBookImageSelectTutorial();
        }
    }

    private void showRecommendBookImageSelectTutorial() {
        SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
        if (smartSnapsManager == null || smartSnapsManager.isShownImageSelectTutorial()) return;
        smartSnapsManager.setShownImageSelectTutorial(true);
        SnapsTutorialUtil.showCustomTutorial((Activity) context, new SnapsTutorialAttribute.Builder().setCustomTutorialType(SnapsTutorialAttribute.eCustomTutorialType.RECOMMEND_BOOK_IMAGE_SELECT).create(), null);
    }

    private void refreshCellItemId() {
        if (trayCellItemList == null) return;

        for (int ii = 0; ii < trayCellItemList.size(); ii++) {
            ImageSelectTrayCellItem cellItem = trayCellItemList.get(ii);

            cellItem.setCellId(ii);
            MyPhotoSelectImageData imageData = ImageSelectUtils.getSelectedImageData(cellItem.getImageKey());
            if (imageData != null) {
                imageData.selectIdx = ii;
            }
        }
    }

    @Override
    public void removeSelectedImage(String key) {
        ImageSelectTrayCellItem removeItem = findCellByImageKey(key);
        if (removeItem != null) {
            removeByCellId(removeItem.getCellId());

            setDummyItemState();

            refreshCellItemId();

            notifyDataSetChanged();

            scrollToCenterTrayView(removeItem.getCellId());
        }

        refreshCounterInfo();
    }

    @Override
    public void removeSelectedImageArray(String key, boolean first, boolean last) {
        ImageSelectTrayCellItem removeItem = findCellByImageKey(key);
        if (removeItem != null) {
            removeCoverImageMapkey(removeItem.getImageKey());

            removeByCellId(removeItem.getCellId());

            setDummyItemState();

            refreshCellItemId();

            if (last)
                notifyDataSetChanged();

            if (first)
                scrollToCenterTrayView(removeItem.getCellId());
        }

        refreshCounterInfo();
    }

    private void removeCoverImageMapkey(String mapKeyOfCover) {
        if (imageSelectPublicMethods == null || imageSelectPublicMethods.getUIProcessor() == null) return;
        ImageSelectUIProcessor uiProcessor = imageSelectPublicMethods.getUIProcessor();
        if (uiProcessor.getImageSelectType() == ImageSelectUIProcessorStrategyFactory.eIMAGE_SELECT_UI_TYPE.SMART_ANALYSIS) {
            SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
            if (smartSnapsManager.removeCoverPhotoMapKey(mapKeyOfCover)) {
                uiProcessor.removeCoverHolderImage();
            }
        }
    }

    /**
     * 트레이 아이템 클릭
     */
    @Override
    public void selectTrayItem(int id, boolean isScrollToCenter) {
        ImageSelectTrayCellItem selectedCell = getTrayCellItemById(id);
        if (selectedCell == null || selectedCell.getCellState() != PHOTO_THUMBNAIL) return;

        boolean isTwiceClicked = isTwiceClickedCell(id); //같은 트레이를 2번째 클릭 한 것인지

        //선택되어 있는 Tray 선택 해제
        if (!isTwiceClicked) {
            int prevSelectedId = -1;
            for (int ii = 0; ii < getItemCount(); ii++) { //기존에 선택되어 있는 셀을 찾는다.
                ImageSelectTrayCellItem cellItem = getTrayCellItemById(ii);
                if (cellItem != null && cellItem.isSelected()) {
                    prevSelectedId = cellItem.getCellId();
                    cellItem.setSelected(false);
                    break;
                }
            }

            if (prevSelectedId >= 0) {
                notifyItemChanged(prevSelectedId);
            }

            if (isScrollToCenter)
                scrollToCenterTrayView(id);
        }

        if (isTwiceClicked) { //두분 째 클릭 되었을 때는 선택 해제 처리
            //트레이 이미지 정보 제거
            if (isTrayAllViewMode())
                removeSelectedImage(selectedCell.getImageKey());

            //Fragment쪽도 지우도록 요청
            ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
            if (imageSelectManager != null) {
                IImageSelectStateChangedListener listener = imageSelectManager.getSelectStateChangedListener();
                if (listener != null)
                    listener.onItemUnSelectedListener(IImageSelectStateChangedListener.eCONTROL_TYPE.TRAY, selectedCell.getImageKey());
            }
        } else {
            selectedCell.setSelected(true);
            notifyItemChanged(id);
        }
    }

    @Override
    public void performClickTrayAddBtn() {
    }
}
