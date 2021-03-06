package com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.image.ResolutionConstants;
import com.snaps.common.utils.image.ResolutionUtil;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryRecyclerCustomAdapter;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectImgDataHolder;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectUITrayControl;
import com.snaps.mobile.activity.google_style_image_selector.datas.TrayAdapterInsertParam;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectPublicMethods;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectStateChangedListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectTrayAllViewHook;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items.ImageSelectTrayCellItem;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.component.SnapsTrayLayoutView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.eTRAY_CELL_STATE.PHOTO_THUMBNAIL;
import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.eTRAY_CELL_STATE.PLUS_BUTTON;
import static com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants.eTRAY_CELL_STATE.TEMPLATE;

/**
 * Created by ysjeong on 16. 3. 9..
 */
public class ImageSelectTrayTemplateShapeAdapter extends ImageSelectTrayBaseAdapter implements ISnapsDiaryRecyclerCustomAdapter, IImageSelectTrayAllViewHook {

    private static final String TAG = ImageSelectTrayTemplateShapeAdapter.class.getSimpleName();

    public ImageSelectTrayTemplateShapeAdapter(ImageSelectActivityV2 imageSelectActivityV2) {
        this(imageSelectActivityV2, imageSelectActivityV2);
    }

    protected ImageSelectTrayTemplateShapeAdapter(Context context, IImageSelectPublicMethods imageSelectPublicMethods) {
        super(context, imageSelectPublicMethods);
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
        boolean isExistText = Config.isThemeBook() || Config.isSimplePhotoBook() || Config.isCalendar() || Config.isSimpleMakingBook() || Const_PRODUCT.isDesignNoteProduct() || Const_PRODUCT.isAccordionCardProduct() || Const_PRODUCT.isNewWalletProduct();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_select_tray_thumbnail_item, parent, false);

        //???????????? ?????? ???????????? ????????? ???????????? ??? ?????????.
        if (!isExistText) {
            SnapsTrayLayoutView trayTemplateView = (SnapsTrayLayoutView) view.findViewById(R.id.imgSelectTrayLayoutView);
            if (trayTemplateView != null) {
                trayTemplateView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }

        return new ImageSelectAdapterHolders.TrayThumbnailItemHolder(view);
    }

    @Override
    public void setData(ArrayList<SnapsPage> newList) {
        super.setData(newList);

        selectTrayItem(0, false);

        refreshCounterInfo();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ImageSelectTrayCellItem cellItem = getTrayCellItem(position);
        onBindViewThumbnailHolder(holder, cellItem);
    }

    //???????????? ???????????? ???
    @Override
    public void onClickedTrayItem(ImageSelectTrayCellItem item) {
        if (item == null) return;
        ISnapsImageSelectConstants.eTRAY_CELL_STATE cellState = item.getCellState();
        int id = item.getCellId();

        switch (cellState) {
            case PLUS_BUTTON:
                tryAddPage(null, null);
                break;
            case TEMPLATE:
                selectTrayItem(id, false);
                sendTrayItemSelectEvent(item);
                break;
            case PHOTO_THUMBNAIL:
                selectTrayItem(id, false);
                sendTrayItemSelectEvent(item);
                break;
        }
    }

    @Override
    protected ISnapsImageSelectConstants.eTRAY_CELL_STATE getDefaultTrayState() {
        return TEMPLATE;
    }

    public void onBindViewThumbnailHolder(final RecyclerView.ViewHolder holder, final ImageSelectTrayCellItem cellItem) {
        if (cellItem == null || holder == null) return;

        ImageSelectAdapterHolders.TrayThumbnailItemHolder trayHolder = (ImageSelectAdapterHolders.TrayThumbnailItemHolder) holder;
        setPlusBtnState(cellItem.isPlusBtn(), trayHolder);

        if (cellItem.isPlusBtn()) return;
        cellItem.setHolder(trayHolder);
        cellItem.setNoPrint(imageResolutionCheck(cellItem));
        //??????????????? ???
        ImageView selector = trayHolder.getSelector();
        if (selector != null) {
            if (cellItem.isSelected()) {
                selector.setBackgroundResource(cellItem.getCellState() == PHOTO_THUMBNAIL ? R.drawable.shape_red_e36a63_fill_solid_border_rect : R.drawable.shape_red_e36a63_border_rect);
                selector.setVisibility(View.VISIBLE);
            } else if (cellItem.isNoPrint() && cellItem.getCellState() == PHOTO_THUMBNAIL) {
                selector.setBackgroundColor(Color.parseColor("#66000000"));
                selector.setVisibility(View.VISIBLE);
            } else {
                selector.setVisibility(View.GONE);
            }
        }

        switch (cellItem.getCellState()) {
            case PHOTO_THUMBNAIL: //???????????? ????????? ????????? ?????? ??????
                setPhotoThumbnailShapeHolder(trayHolder);

                if (cellItem.isSelected() && cellItem.isNoPrint()) {
                    MessageUtil.noPrintToast(context, ResolutionConstants.NO_PRINT_TOAST_OFFSETY_BASIC, ResolutionConstants.NO_PRINT_TOAST_OFFSETY_BASIC);
                    //MessageUtil.toast(context,R.string.photoprint_noprint_message,Gravity.CENTER);
                }

                //?????? ?????????
                if (trayHolder.getDeleteIcon() != null) {
                    trayHolder.getDeleteIcon().setVisibility(cellItem.isSelected() ? View.VISIBLE : View.GONE);
                }

                //????????? ?????????
                if (trayHolder.getNoPrintIcon() != null) {
                    trayHolder.getNoPrintIcon().setVisibility(cellItem.isNoPrint() ? View.VISIBLE : View.GONE);
                }


                //????????? ?????? ?????????
                ImageView photoThumbnail = trayHolder.getPhotoThumbnail();
                if (photoThumbnail != null) {
                    photoThumbnail.setImageBitmap(null);
                    photoThumbnail.setVisibility(View.VISIBLE);

                    MyPhotoSelectImageData imageData = ImageSelectUtils.getSelectedImageData(cellItem.getImageKey());
                    if (imageData != null) {
                        String photoThumbnailPath = imageData.THUMBNAIL_PATH;
                        if (photoThumbnailPath != null && photoThumbnailPath.length() > 0) {
                            ImageSelectUtils.loadImage(context, photoThumbnailPath, UIUtil.convertDPtoPX(context, 50), photoThumbnail, ImageView.ScaleType.CENTER_CROP, true);
                            photoThumbnail.setRotation(imageData.ROTATE_ANGLE_THUMB == -1 ? 0 : imageData.ROTATE_ANGLE_THUMB);
                        }
                    }
                }

                break;
            case TEMPLATE: //???????????? ????????? ?????? ???????????? ????????? ?????? ??????
                setTemplateShapeHolder(trayHolder);

                //?????????
                TextView labelView = trayHolder.getLabel();
                if (labelView != null) {
                    String labelText = cellItem.getLabel();
                    if (labelText != null)
                        labelView.setText(labelText);
                    else
                        labelView.setText("");
                }

                //????????? ?????????
                SnapsTrayLayoutView trayThumbnail = trayHolder.getTrayThumbnail();
                if (trayThumbnail != null)
                    trayThumbnail.init(cellItem.getSnapsPage(), cellItem.getLastIdx(), cellItem.isDrawHalfLine());
                break;
        }

        if (trayHolder.getParentView() != null) {
            trayHolder.getParentView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickedTrayItem(cellItem);
                }
            });
        }
    }

    protected void setTemplateShapeHolder(ImageSelectAdapterHolders.TrayThumbnailItemHolder trayHolder) {
        if (trayHolder == null) return;

        if (trayHolder.getDeleteIcon() != null)
            trayHolder.getDeleteIcon().setVisibility(View.GONE);
        if (trayHolder.getNoPrintIcon() != null)
            trayHolder.getNoPrintIcon().setVisibility(View.GONE);
        if (trayHolder.getPhotoThumbnail() != null)
            trayHolder.getPhotoThumbnail().setVisibility(View.GONE);
        if (trayHolder.getLabel() != null)
            trayHolder.getLabel().setVisibility(View.VISIBLE);
        if (trayHolder.getTrayThumbnail() != null)
            trayHolder.getTrayThumbnail().setVisibility(View.VISIBLE);
    }

    protected void setPhotoThumbnailShapeHolder(ImageSelectAdapterHolders.TrayThumbnailItemHolder trayHolder) {
        if (trayHolder == null) return;

        if (trayHolder.getTrayThumbnail() != null)
            trayHolder.getTrayThumbnail().setVisibility(View.GONE);
        if (trayHolder.getLabel() != null)
            trayHolder.getLabel().setVisibility(View.GONE);
    }

    /**
     * ?????? ????????? ??? ?????? ?????????
     *
     * @return ?????? ???????????? ??????????????? true ??????
     */
    @Override
    public boolean checkExcessMaxPage() {
        if (Config.isActiveImageAutoSelectFunction()) return false;

        ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
        if (holder != null) {

            int innerPageMaxNumber = calculateInnerPageNumberWhenAdded();
            String maxPage = ImageSelectUtils.getCurrentPaperCodeMaxPage();
            int limitInnerPageCount = (2 * (StringUtil.isEmpty(maxPage) ? 1 : Integer.parseInt(maxPage)) + (Config.isSimpleMakingBook() ? 0 : 1)); //???????????? 150?????? ????????? ?????? ??????.

            if ((Config.isSimplePhotoBook() || Config.isSimpleMakingBook()) && innerPageMaxNumber > limitInnerPageCount) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean checkExcessMaxPhoto() {
        if (Config.isActiveImageAutoSelectFunction()) return false;

        int maxSelectImgs = getDefaultLimitImageCount();
//        int maxCount = Config.isCheckPlusButton() ? maxSelectImgs + 1 : maxSelectImgs;
        int maxCount = maxSelectImgs;

        ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
        if (holder != null) {

            int selectedPhotoCount = holder.getMapSize();

            if (maxSelectImgs > 0 && selectedPhotoCount >= maxCount) { // ?????????// ??????

                // ???????????? ??????????????? ??????
                if (Const_PRODUCT.isFrameProduct() || Const_PRODUCT.isPolaroidProduct() || Const_PRODUCT.isWalletProduct() || Const_PRODUCT.isSinglePageProduct() || Const_PRODUCT.isPackageProduct()
                        || Const_PRODUCT.isCardProduct() || Const_PRODUCT.isSnapsDiary() || SnapsDiaryDataManager.isAliveSnapsDiaryService() || Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isDesignNoteProduct() || Const_PRODUCT.isAccordionCardProduct()) {
                    return true;
                }

                int maxInnerPageNumberWhenAdded = calculateInnerPageNumberWhenAdded();
                String maxPage = ImageSelectUtils.getCurrentPaperCodeMaxPage();
                int limitInnerPageCount = (2 * (StringUtil.isEmpty(maxPage) ? 1 : Integer.parseInt(maxPage)) + (Config.isSimpleMakingBook() ? 0 : 1)); //???????????? 150?????? ????????? ?????? ??????.

                if (Config.isSimplePhotoBook() || Config.isSimpleMakingBook()) {
                    int limitPhotoCount = pageCountInfo != null ? pageCountInfo.getTotalTemplateImageCount() : 0;
                    if (selectedPhotoCount > limitPhotoCount && maxInnerPageNumberWhenAdded > limitInnerPageCount)
                        return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean checkExcessMaxPhotoForDragging() {
        if (Config.isActiveImageAutoSelectFunction()) return false;

        int maxSelectImgs = getDefaultLimitImageCount();

        ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
        if (holder != null) {

            int selectedPhotoCount = holder.getMapSize();

            if (maxSelectImgs > 0 && selectedPhotoCount > maxSelectImgs) { // ?????????// ??????
                return true;
            }
        }

        return false;
    }

    //???????????? ????????? Item ???????????? ?????????.
    @Override
    protected void initTrayCells(ArrayList<SnapsPage> pageList) {
        for (int pageIdx = 0; pageIdx < pageList.size(); pageIdx++) {
            SnapsPage page = pageList.get(pageIdx);
            insertNewTemplateTrayCell(page, pageIdx, true);
        }

        //plus ????????? ???????????? ?????? ???????????? ?????? ???.
//        if (Config.isCheckPlusButton()) {
//            if (trayCellItemList != null)
//                trayCellItemList.add(new ImageSelectTrayCellItem(context, ISnapsImageSelectConstants.INVALID_VALUE, ISnapsImageSelectConstants.eTRAY_CELL_STATE.PLUS_BUTTON));
//        }
    }

    // ????????? ?????? ??????
    @Override
    public void refreshCounterInfo() {
        ImageSelectUITrayControl trayControl = getTrayControl();
        if (trayControl == null || trayCellItemList == null) return;

        int photoCount = 0;
        int totalTemplateCount = 0;
        for (ImageSelectTrayCellItem cellItem : trayCellItemList) {
            if (cellItem != null) {
                if (cellItem.isPlusBtn()) continue;

                if (cellItem.getImageKey() != null && cellItem.getImageKey().length() > 0)
                    photoCount++;

                if (cellItem.getCellState() == ISnapsImageSelectConstants.eTRAY_CELL_STATE.PHOTO_THUMBNAIL
                        || cellItem.getCellState() == ISnapsImageSelectConstants.eTRAY_CELL_STATE.TEMPLATE)
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

        TextView tvRightCountView = trayControl.getRightCountView();
        if (tvRightCountView != null) {
            tvRightCountView.setVisibility(View.VISIBLE);
            tvRightCountView.setText("/" + String.valueOf(totalTemplateCount));
        }
        ImageSelectUIProcessor uiProcessor = imageSelectPublicMethods.getUIProcessor();
        if (uiProcessor != null) {
            uiProcessor.setCurrentImageCount(photoCount);
            uiProcessor.setCurrentMaxImageCount(totalTemplateCount);
        }
    }

    @Override
    public void performClickTrayAddBtn() {
        tryAddPage(null, null);
    }

    /**
     * ????????? ?????? ????????????.
     *
     * @return ????????? Cell??? ????????? ????????????.
     */
    protected int insertNewTemplateTrayCell(SnapsPage page, int pageIdx, boolean isSortLayoutList) {
        if (page == null || page.subType.compareTo("schedule_memo") == 0)
            return 0;

        //????????? ?????? ????????? ?????? ??????.
        if (Const_PRODUCT.isPackageProduct() && (page.side == null || page.side.compareTo("back") == 0 || page.type == null || page.type.compareTo("hidden") == 0))
            return 0;

        //?????? ???????????? ????????? ???????????? ????????? ??????.
        if (Config.isSimpleMakingBook() && page.type.equalsIgnoreCase("cover"))
            return 0;
        if (Const_PRODUCT.isNewWalletProduct() && page.type.compareTo("hidden") == 0)
            return 0;
        //???????????? ????????????
        if (isSortLayoutList) {
            Collections.sort(page.getLayoutList(), PhotobookCommonUtils.myComparator);
        }

        int layoutCnt = page.getLayoutList().size();

        //????????? ????????? ?????? ?????????
        String addedLabel = "";
        for (int layoutIdx = layoutCnt - 1; layoutIdx >= 0; layoutIdx--) {

            SnapsLayoutControl c = (SnapsLayoutControl) page.getLayoutList().get(layoutIdx);

            if (!c.type.equals("browse_file"))
                continue;

            ImageSelectTrayCellItem cellItem = new ImageSelectTrayCellItem(context, getOnlyThumbnailCellCount(), ISnapsImageSelectConstants.eTRAY_CELL_STATE.TEMPLATE);
            cellItem.setSnapsPage(page, pageIdx + pageCountInfo.getTotalPageCount(), layoutIdx, pageCountInfo.getTotalPageCount());
            addTrayItem(cellItem);

            addedLabel = cellItem.getLabel();
        }

        addSection(addedLabel, layoutCnt);

        if (Config.isCalendar()) {
            if (Config.isWoodBlockCalendar()) {
                pageCountInfo.addTotalPageCount();
            } else {
                if (page.side.compareTo("back") == 0) {
                    pageCountInfo.addTotalPageCount();
                }
            }
        } else {
            pageCountInfo.addTotalPageCount();
        }

        return layoutCnt;
    }

    //Hook ?????? ??????????????? ??????...
    @Override
    public void addSection(String sectionLabel, int layoutCnt) {
    }

    protected void addNewTemplatePageOnTray(String imageKey, MyPhotoSelectImageData imageData) {
        addNewTemplatePageOnTray(imageKey, imageData, false);
    }

    //????????? ?????? (*********** ?????? ?????? ???????????? ???????????? ?????? tryAddPage??? ????????????. ************)
    protected void addNewTemplatePageOnTray(String imageKey, MyPhotoSelectImageData imageData, boolean array) {
        SnapsTemplateManager templeteManager = SnapsTemplateManager.getInstance();
        if (templeteManager == null || pageCountInfo == null) return;

        SnapsTemplate template = templeteManager.getSnapsTemplate();
        if (template == null || template.getPages() == null) return;


        ArrayList<Integer> addIdxList = ImageSelectUtils.getAddPageIdxs();
        if (addIdxList != null) {
            addIdxList.add(pageCountInfo.getLastSnapsPageIdx());
        }

        //??????, ????????? ???????????? ???????????? ???????????? ?????? ????????? ???.
        if (pageCountInfo.getLastSnapsPageIdx() >= template.getPages().size())
            pageCountInfo.setLastSnapsPageIdx(2);

        if (pageCountInfo.getLastSnapsPageIdx() >= template.getPages().size()) return;

        int lastItemIdx = trayCellItemList.size() - 1;

        ImageSelectTrayCellItem plusBtn = null;
        if (trayCellItemList.size() > 1) {
            plusBtn = trayCellItemList.get(lastItemIdx);
            if (plusBtn.isPlusBtn()) {
                remove(lastItemIdx);
            }
        }

        SnapsPage page = template.getPages().get(pageCountInfo.getLastSnapsPageIdx());
        insertNewTemplateTrayCell(page, template.getPages().size() + pageCountInfo.getAddedPageCount(), false);

        if (plusBtn != null && plusBtn.isPlusBtn()) {
            insert(plusBtn, trayCellItemList.size());
        }

        notifyDataSetChanged();

        scrollToLastItem();

        refreshCounterInfo();

        //?????? ????????? ???????????? ??????????????? ????????? ????????? ?????? ????????? ????????????.
        if (pageCountInfo.isAddedPage() && (Config.isSimpleMakingBook() || Config.isSimplePhotoBook()) && imageKey != null) {
            insertPhotoThumbnailOnTrayItem(new TrayAdapterInsertParam.Builder().setImageMapKey(imageKey).setImageData(imageData).create());
        }

        if (!array)
            MessageUtil.toast(getApplicationContext(), context.getString(R.string.page_added));

        pageCountInfo.addLastSnapsPageIdx();
        pageCountInfo.addAddedPageCount();
    }

    @Override
    public void removeSelectedImage(String key) {
        ImageSelectTrayCellItem removeItem = findCellByImageKey(key);

        if (removeItem != null) {
            removeItem.setImageKey(""); //????????? ?????? ??????

            removeItem.setCellState(getDefaultTrayState()); //?????????(??????) ????????? ?????????..

            notifyItemChangedByCellId(removeItem.getCellId()); //????????? ??????

            selectTrayItem(removeItem.getCellId(), true); //????????? ???????????? ????????? ???.
        }

        refreshCounterInfo();
    }

    @Override
    public void removeSelectedImageArray(String key, boolean first, boolean last) {
        ImageSelectTrayCellItem removeItem = findCellByImageKey(key);

        if (removeItem != null) {
            removeItem.setImageKey(""); //????????? ?????? ??????

            removeItem.setCellState(getDefaultTrayState()); //?????????(??????) ????????? ?????????..

//                notifyItemChangedByCellId(removeItem.getCellId()); //????????? ??????
            if (last)
                notifyDataSetChanged();
            if (first)
                selectTrayItem(removeItem.getCellId(), true); //????????? ???????????? ????????? ???.

        }

        if (last)
            refreshCounterInfo();
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

        ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
        if (holder != null)
            imageData.selectIdx = holder.getMapSize();

        imageData.isDelete = false;
    }

    @Override
    public boolean insertPhotoThumbnailOnTrayItem(TrayAdapterInsertParam trayAdapterInsertParam) {
        if (trayAdapterInsertParam == null) return false;

        String key = trayAdapterInsertParam.getImageMapKey();
        MyPhotoSelectImageData imageData = trayAdapterInsertParam.getImageData();

        int insertTargetCellId = findNextEmptyCellId(0);
        if (insertTargetCellId >= 0) {
            ImageSelectTrayCellItem cellItem = getTrayCellItemById(insertTargetCellId);
            if (cellItem != null) {
                cellItem.setImageKey(key);
                cellItem.setCellState(PHOTO_THUMBNAIL);
                cellItem.setSelected(false);
                cellItem.setNoPrint(imageData != null && imageData.isNoPrint);

                if (trayAdapterInsertParam.isArrayInsert()) {
                    if (trayAdapterInsertParam.isArrayInsertAndLastItem()) {
                        notifyDataSetChanged();
                    }
                } else {
                    notifyItemChangedByCellId(cellItem.getCellId());
                }
            }

            //????????? ???????????? ?????? ?????? ??????
            if (trayAdapterInsertParam.isArrayInsert()) {
                if (trayAdapterInsertParam.isArrayInsertAndLastItem()) {
                    setSelectedImageInfo(key);
                }
            } else {
                setSelectedImageInfo(key);
                imageSelectPublicMethods.putSelectedImageData(key, imageData);
            }

            insertTargetCellId = findNextEmptyCellId(insertTargetCellId + 1);
            if (insertTargetCellId >= 0) {
                selectTrayItem(insertTargetCellId, true);
            }

            refreshCounterInfo();

            createThumbnailCacheWithImageData(imageData);
        } else {
            //????????? ???????????? ?????? ?????? ?????? ??????
            if (insertTargetCellId == ISnapsImageSelectConstants.IS_CONTAINS_PHOTO_ON_ALL_TRAY) {
                tryAddPage(key, imageData);
            } else {
                imageSelectPublicMethods.removeSelectedImageData(key);
            }
        }
        return true;
    }

    /**
     * ????????? ????????? ??????
     */
    @Override
    public void selectTrayItem(int id, boolean isScrollToCenter) {
        ImageSelectTrayCellItem selectedCell = getTrayCellItemById(id);
        if (selectedCell == null || selectedCell.getCellState() == PLUS_BUTTON) return;

        boolean isTwiceClicked = isTwiceClickedCell(id); //?????? ???????????? 2?????? ?????? ??? ?????????

        //???????????? ?????? Tray ?????? ??????
        if (!isTwiceClicked) {
            int prevSelectedId = -1;
            for (int ii = 0; ii < getItemCount(); ii++) { //????????? ???????????? ?????? ?????? ?????????.
                ImageSelectTrayCellItem cellItem = getTrayCellItem(ii);
                if (cellItem != null && cellItem.isSelected()) {
                    prevSelectedId = cellItem.getCellId();
                    cellItem.setSelected(false);
                    break;
                }
            }

            if (prevSelectedId >= 0) {
                notifyItemChangedByCellId(prevSelectedId);
            }

            if (isScrollToCenter)
                scrollToCenterTrayView(id);
        }

        IImageSelectStateChangedListener listener = null;
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            listener = imageSelectManager.getSelectStateChangedListener();
        }

        switch (selectedCell.getCellState()) {
            case TEMPLATE:
                selectedCell.setSelected(true);
                if (!isTwiceClicked)
                    notifyItemChangedByCellId(id);
                break;
            case PHOTO_THUMBNAIL:
                if (isTwiceClicked) { //?????? ??? ?????? ????????? ?????? ?????? ?????? ??????
                    //tray??? ????????? ?????? ??????
                    final String IMAGE_KEY = selectedCell.getImageKey();

                    if (isTrayAllViewMode())
                        removeSelectedImage(IMAGE_KEY);

                    //Fragemt?????? ?????? ??????
                    if (imageSelectManager != null) {
                        if (listener != null)
                            listener.onItemUnSelectedListener(IImageSelectStateChangedListener.eCONTROL_TYPE.TRAY, IMAGE_KEY);
                    }
                } else {
                    selectedCell.setSelected(true);
                    notifyItemChangedByCellId(id);
                }
                break;
        }
    }

    //?????? ?????? ?????? ????????? ????????????.
    @Override
    public int getEmptyCellCount() {
        if (trayCellItemList == null) return 0;

        int emptyCellCnt = 0;
        for (ImageSelectTrayCellItem cellItem : trayCellItemList) {
            if (cellItem != null && !cellItem.isPlusBtn() && (cellItem.getImageKey() == null || cellItem.getImageKey().length() < 1))
                emptyCellCnt++;
        }
        return emptyCellCnt;
    }

    //?????? ?????? ???????????? ????????????.
    private int findNextEmptyCellId(int startIdx) {
        if (trayCellItemList == null) return ISnapsImageSelectConstants.INVALID_VALUE;

        for (int ii = startIdx; ii < trayCellItemList.size(); ii++) {
            ImageSelectTrayCellItem cellItem = trayCellItemList.get(ii);
            if (cellItem != null && !cellItem.isPlusBtn()) {
                if (startIdx == 0) {  //?????? ????????? ???????????? ????????? ?????????
                    if (cellItem.isSelected() && (cellItem.getImageKey() == null || cellItem.getImageKey().length() < 1)) {
                        return cellItem.getCellId();
                    }
                } else { //???????????? ??????????????? ?????? ?????? ????????????, ??? ????????? ?????? ??????.
                    if (cellItem.getImageKey() == null || cellItem.getImageKey().length() < 1) {
                        return cellItem.getCellId();
                    }
                }
            }
        }

        //?????? ???????????? ???????????????.. ???????????? ?????? ??? ?????????.
        for (ImageSelectTrayCellItem cellItem : trayCellItemList) {
            if (cellItem != null && !cellItem.isPlusBtn() && (cellItem.getImageKey() == null || cellItem.getImageKey().length() < 1))
                return cellItem.getCellId();
        }

        return ISnapsImageSelectConstants.IS_CONTAINS_PHOTO_ON_ALL_TRAY; //?????? ???????????? ????????? ?????? ?????? ??????
    }

    @Override
    public ImageSelectTrayCellItem findNextEmptyCellItem() {
        int id = findNextEmptyCellId(0);
        if (id >= 0) {
            return getTrayCellItemById(id);
        }
        return null;
    }

    protected void setPlusBtnState(boolean isVisible, ImageSelectAdapterHolders.TrayThumbnailItemHolder trayHolder) {
        if (trayHolder.getImgSelectTrayPlusBtn() != null) {
            ImageView plusBtnView = trayHolder.getImgSelectTrayPlusBtn();
            plusBtnView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            plusBtnView.setImageResource(isVisible ? R.drawable.simple_plus : 0);

            if (trayHolder.getParentView() != null) {
                trayHolder.getParentView().setVisibility(isVisible ? View.GONE : View.VISIBLE);
            }

            trayHolder.getImgSelectTrayPlusBtn().setOnClickListener(v -> onClickedTrayItem(new ImageSelectTrayCellItem(context, ISnapsImageSelectConstants.INVALID_VALUE, PLUS_BUTTON)));
        }
    }

    //FIXME ?????????, ?????? ????????? ?????? ????????? ????????? ??? ?????? ????????? ???????????? ?????? ????????? ?????? ??????.
    private int calculateInnerPageNumberWhenAdded() {
        if (pageCountInfo == null) return 0;
        SnapsTemplateManager templeteManager = SnapsTemplateManager.getInstance();
        if (templeteManager == null) return 0;
        SnapsTemplate template = templeteManager.getSnapsTemplate();
        if (template == null || template.getPages() == null) return 0;

        int nextPageNumber = (pageCountInfo.getTotalPageCount() * 2) - 1;

        if (Config.isSimpleMakingBook()) {
            nextPageNumber += 1; //??????
        }

        return nextPageNumber;
    }

    protected void tryAddPage(final String imageKey, final MyPhotoSelectImageData imageData) {
        if (checkExcessMaxPage()) {
            imageSelectPublicMethods.removeSelectedImageData(imageKey);
            ImageSelectUtils.showDisableAddPhotoMsg((Activity) context);
            return;
        }

        if (isPageSyncLock()) return;
        setPageSyncLock(true);

        if (Config.isActiveImageAutoSelectFunction()) {
            if (pageCountInfo != null)
                pageCountInfo.setAddedPage(true);
            addNewTemplatePageOnTray(imageKey, imageData);
            setPageSyncLock(false);
        } else if (pageCountInfo != null && pageCountInfo.isAddedPage()) {
            addNewTemplatePageOnTray(imageKey, imageData);
            setPageSyncLock(false);
        } else {
            imageSelectPublicMethods.removeSelectedImageData(imageKey);

            MessageUtil.alertnoTitle((Activity) context, context.getString(R.string.page_add_pay_msg), clickedOk -> {
                if (clickedOk == ICustomDialogListener.OK) {
                    // ??? ????????? ????????? GA ??? ????????? ????????? FirebaseAnalytics ???????????????.
//                    Answers.getInstance().logCustom(new CustomEvent("PhotoBookAddPage").putCustomAttribute("Select", "AddPageOK"));
                    addNewTemplatePageOnTray(imageKey, imageData);
                    if (pageCountInfo != null)
                        pageCountInfo.setAddedPage(true);
                } else {
                    // ??? ????????? ????????? GA ??? ????????? ????????? FirebaseAnalytics ???????????????.
//                    Answers.getInstance().logCustom(new CustomEvent("PhotoBookAddPage").putCustomAttribute("Select", "AddPageCancel"));
                    MessageUtil.toast(getApplicationContext(), context.getString(R.string.cancel_msg));
                }
                setPageSyncLock(false);
            });
        }
    }

    protected void tryAddPageArray(@NonNull Activity activity, final String imageKey, final MyPhotoSelectImageData imageData) {
        if (checkExcessMaxPage()) {
            imageSelectPublicMethods.removeSelectedImageData(imageKey);
            ImageSelectUtils.showDisableAddPhotoMsg(activity);
            return;
        }
        if (isPageSyncLock()) return;
        setPageSyncLock(true);
        addNewTemplatePageOnTray(imageKey, imageData, true);
        setPageSyncLock(false);
    }

    private boolean imageResolutionCheck(ImageSelectTrayCellItem item) {
        if (item == null) return false;
        if (item.getSnapsPage() == null) return false;
        if (item.getSnapsPage().getLayoutList() == null) return false;

        int idx = 0;
        SnapsLayoutControl snapsControl = null;

        for (SnapsControl control : item.getSnapsPage().getLayoutList()) {
            boolean isDrawFill = false;
            if (idx == item.getLastIdx()) {
                isDrawFill = true;
                snapsControl = (SnapsLayoutControl) control;
                break;
            }
            idx++;
        }

        if (snapsControl != null) {
            SnapsTemplate template = SnapsTemplateManager.getInstance().getSnapsTemplate();
            MyPhotoSelectImageData imageData = ImageSelectUtils.getSelectedImageData(item.getImageKey());
            snapsControl.imgData = imageData;
            try {
                return ResolutionUtil.isEnableResolution(Float.parseFloat(template.info.F_PAGE_MM_WIDTH), Integer.parseInt(template.info.F_PAGE_PIXEL_WIDTH), snapsControl);
            } catch (Exception e) {
                //SnapsAssert.assertException(context, e);
                Dlog.e(TAG, e);
            }
        }
        return false;
    }

}
