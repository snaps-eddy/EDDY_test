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

        //텍스트가 없는 제품류는 화면에 썸네일에 꽉 채운다.
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

    //트레이를 선택했을 때
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
        //선택되었는 지
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
            case PHOTO_THUMBNAIL: //트레이에 사진이 들어가 있는 상태
                setPhotoThumbnailShapeHolder(trayHolder);

                if (cellItem.isSelected() && cellItem.isNoPrint()) {
                    MessageUtil.noPrintToast(context, ResolutionConstants.NO_PRINT_TOAST_OFFSETY_BASIC, ResolutionConstants.NO_PRINT_TOAST_OFFSETY_BASIC);
                    //MessageUtil.toast(context,R.string.photoprint_noprint_message,Gravity.CENTER);
                }

                //삭제 아이콘
                if (trayHolder.getDeleteIcon() != null) {
                    trayHolder.getDeleteIcon().setVisibility(cellItem.isSelected() ? View.VISIBLE : View.GONE);
                }

                //해상도 아이콘
                if (trayHolder.getNoPrintIcon() != null) {
                    trayHolder.getNoPrintIcon().setVisibility(cellItem.isNoPrint() ? View.VISIBLE : View.GONE);
                }


                //선택된 사진 섬네일
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
            case TEMPLATE: //트레이에 사진이 없고 템플릿이 그려져 있는 형태
                setTemplateShapeHolder(trayHolder);

                //텍스트
                TextView labelView = trayHolder.getLabel();
                if (labelView != null) {
                    String labelText = cellItem.getLabel();
                    if (labelText != null)
                        labelView.setText(labelText);
                    else
                        labelView.setText("");
                }

                //템플릿 썸네일
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
     * 최대 추가할 수 있는 페이지
     *
     * @return 최대 페이지를 초과한다면 true 반환
     */
    @Override
    public boolean checkExcessMaxPage() {
        if (Config.isActiveImageAutoSelectFunction()) return false;

        ImageSelectImgDataHolder holder = ImageSelectUtils.getSelectImageHolder();
        if (holder != null) {

            int innerPageMaxNumber = calculateInnerPageNumberWhenAdded();
            String maxPage = ImageSelectUtils.getCurrentPaperCodeMaxPage();
            int limitInnerPageCount = (2 * (StringUtil.isEmpty(maxPage) ? 1 : Integer.parseInt(maxPage)) + (Config.isSimpleMakingBook() ? 0 : 1)); //간편북은 150에서 걸리게 하기 위함.

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

            if (maxSelectImgs > 0 && selectedPhotoCount >= maxCount) { // 데이터// 추가

                // 이미지가 다채웟는지 확인
                if (Const_PRODUCT.isFrameProduct() || Const_PRODUCT.isPolaroidProduct() || Const_PRODUCT.isWalletProduct() || Const_PRODUCT.isSinglePageProduct() || Const_PRODUCT.isPackageProduct()
                        || Const_PRODUCT.isCardProduct() || Const_PRODUCT.isSnapsDiary() || SnapsDiaryDataManager.isAliveSnapsDiaryService() || Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isDesignNoteProduct() || Const_PRODUCT.isAccordionCardProduct()) {
                    return true;
                }

                int maxInnerPageNumberWhenAdded = calculateInnerPageNumberWhenAdded();
                String maxPage = ImageSelectUtils.getCurrentPaperCodeMaxPage();
                int limitInnerPageCount = (2 * (StringUtil.isEmpty(maxPage) ? 1 : Integer.parseInt(maxPage)) + (Config.isSimpleMakingBook() ? 0 : 1)); //간편북은 150에서 걸리게 하기 위함.

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

            if (maxSelectImgs > 0 && selectedPhotoCount > maxSelectImgs) { // 데이터// 추가
                return true;
            }
        }

        return false;
    }

    //트레이에 표시할 Item 리스트를 만든다.
    @Override
    protected void initTrayCells(ArrayList<SnapsPage> pageList) {
        for (int pageIdx = 0; pageIdx < pageList.size(); pageIdx++) {
            SnapsPage page = pageList.get(pageIdx);
            insertNewTemplateTrayCell(page, pageIdx, true);
        }

        //plus 버튼이 오버레이 되는 방식으로 변경 됨.
//        if (Config.isCheckPlusButton()) {
//            if (trayCellItemList != null)
//                trayCellItemList.add(new ImageSelectTrayCellItem(context, ISnapsImageSelectConstants.INVALID_VALUE, ISnapsImageSelectConstants.eTRAY_CELL_STATE.PLUS_BUTTON));
//        }
    }

    // 카운터 정보 갱신
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
     * 새로운 셀을 생성한다.
     *
     * @return 추가된 Cell의 갯수를 반환한다.
     */
    protected int insertNewTemplateTrayCell(SnapsPage page, int pageIdx, boolean isSortLayoutList) {
        if (page == null || page.subType.compareTo("schedule_memo") == 0)
            return 0;

        //패키지 킷은 앞뒤로 인쇄 된다.
        if (Const_PRODUCT.isPackageProduct() && (page.side == null || page.side.compareTo("back") == 0 || page.type == null || page.type.compareTo("hidden") == 0))
            return 0;

        //간편 만들기는 커버를 자동으로 생성해 준다.
        if (Config.isSimpleMakingBook() && page.type.equalsIgnoreCase("cover"))
            return 0;
        if (Const_PRODUCT.isNewWalletProduct() && page.type.compareTo("hidden") == 0)
            return 0;
        //템플릿을 정렬한다
        if (isSortLayoutList) {
            Collections.sort(page.getLayoutList(), PhotobookCommonUtils.myComparator);
        }

        int layoutCnt = page.getLayoutList().size();

        //순서를 반대로 해서 그린다
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

    //Hook 하위 클래스에서 사용...
    @Override
    public void addSection(String sectionLabel, int layoutCnt) {
    }

    protected void addNewTemplatePageOnTray(String imageKey, MyPhotoSelectImageData imageData) {
        addNewTemplatePageOnTray(imageKey, imageData, false);
    }

    //페이지 추가 (*********** 바로 해당 메서드를 사용하지 말고 tryAddPage를 사용할것. ************)
    protected void addNewTemplatePageOnTray(String imageKey, MyPhotoSelectImageData imageData, boolean array) {
        SnapsTemplateManager templeteManager = SnapsTemplateManager.getInstance();
        if (templeteManager == null || pageCountInfo == null) return;

        SnapsTemplate template = templeteManager.getSnapsTemplate();
        if (template == null || template.getPages() == null) return;


        ArrayList<Integer> addIdxList = ImageSelectUtils.getAddPageIdxs();
        if (addIdxList != null) {
            addIdxList.add(pageCountInfo.getLastSnapsPageIdx());
        }

        //커버, 내지를 제외하고 페이지를 순서대로 다시 추가해 줌.
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

        //한번 추가가 되었다면 다음부터는 페이지 추가와 함께 사진도 넣어준다.
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
            removeItem.setImageKey(""); //이미지 정보 제거

            removeItem.setCellState(getDefaultTrayState()); //템플릿(공백) 상태로 되돌림..

            notifyItemChangedByCellId(removeItem.getCellId()); //아이템 갱신

            selectTrayItem(removeItem.getCellId(), true); //제거된 아이템에 포커스 줌.
        }

        refreshCounterInfo();
    }

    @Override
    public void removeSelectedImageArray(String key, boolean first, boolean last) {
        ImageSelectTrayCellItem removeItem = findCellByImageKey(key);

        if (removeItem != null) {
            removeItem.setImageKey(""); //이미지 정보 제거

            removeItem.setCellState(getDefaultTrayState()); //템플릿(공백) 상태로 되돌림..

//                notifyItemChangedByCellId(removeItem.getCellId()); //아이템 갱신
            if (last)
                notifyDataSetChanged();
            if (first)
                selectTrayItem(removeItem.getCellId(), true); //제거된 아이템에 포커스 줌.

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

            //선택된 이미지의 기본 정보 셋팅
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
            //사진이 트레이에 모두 들어 있는 상태
            if (insertTargetCellId == ISnapsImageSelectConstants.IS_CONTAINS_PHOTO_ON_ALL_TRAY) {
                tryAddPage(key, imageData);
            } else {
                imageSelectPublicMethods.removeSelectedImageData(key);
            }
        }
        return true;
    }

    /**
     * 트레이 아이템 클릭
     */
    @Override
    public void selectTrayItem(int id, boolean isScrollToCenter) {
        ImageSelectTrayCellItem selectedCell = getTrayCellItemById(id);
        if (selectedCell == null || selectedCell.getCellState() == PLUS_BUTTON) return;

        boolean isTwiceClicked = isTwiceClickedCell(id); //같은 트레이를 2번째 클릭 한 것인지

        //선택되어 있는 Tray 선택 해제
        if (!isTwiceClicked) {
            int prevSelectedId = -1;
            for (int ii = 0; ii < getItemCount(); ii++) { //기존에 선택되어 있는 셀을 찾는다.
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
                if (isTwiceClicked) { //두분 째 클릭 되었을 때는 선택 해제 처리
                    //tray에 이미지 정보 제거
                    final String IMAGE_KEY = selectedCell.getImageKey();

                    if (isTrayAllViewMode())
                        removeSelectedImage(IMAGE_KEY);

                    //Fragemt쪽도 제거 요청
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

    //비어 있는 셀의 갯수를 반환한다.
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

    //비어 있는 트레이를 찾아준다.
    private int findNextEmptyCellId(int startIdx) {
        if (trayCellItemList == null) return ISnapsImageSelectConstants.INVALID_VALUE;

        for (int ii = startIdx; ii < trayCellItemList.size(); ii++) {
            ImageSelectTrayCellItem cellItem = trayCellItemList.get(ii);
            if (cellItem != null && !cellItem.isPlusBtn()) {
                if (startIdx == 0) {  //우선 순위는 사용자가 선택한 트레이
                    if (cellItem.isSelected() && (cellItem.getImageKey() == null || cellItem.getImageKey().length() < 1)) {
                        return cellItem.getCellId();
                    }
                } else { //사용자가 최종적으로 넣은 사진 다음부터, 빈 공간을 찾아 준다.
                    if (cellItem.getImageKey() == null || cellItem.getImageKey().length() < 1) {
                        return cellItem.getCellId();
                    }
                }
            }
        }

        //다시 처음부터 순차적으로.. 빈공간이 있는 지 찾는다.
        for (ImageSelectTrayCellItem cellItem : trayCellItemList) {
            if (cellItem != null && !cellItem.isPlusBtn() && (cellItem.getImageKey() == null || cellItem.getImageKey().length() < 1))
                return cellItem.getCellId();
        }

        return ISnapsImageSelectConstants.IS_CONTAINS_PHOTO_ON_ALL_TRAY; //모든 트레이에 사진이 들어 있는 형태
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

    //FIXME 심플북, 심플 포토북 외에 페이지 추가할 수 있는 상품이 생긴다면 코드 수정이 필요 하다.
    private int calculateInnerPageNumberWhenAdded() {
        if (pageCountInfo == null) return 0;
        SnapsTemplateManager templeteManager = SnapsTemplateManager.getInstance();
        if (templeteManager == null) return 0;
        SnapsTemplate template = templeteManager.getSnapsTemplate();
        if (template == null || template.getPages() == null) return 0;

        int nextPageNumber = (pageCountInfo.getTotalPageCount() * 2) - 1;

        if (Config.isSimpleMakingBook()) {
            nextPageNumber += 1; //심플
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
                    // 이 이벤트 처리를 GA 로 옮기고 싶다면 FirebaseAnalytics 사용해야함.
//                    Answers.getInstance().logCustom(new CustomEvent("PhotoBookAddPage").putCustomAttribute("Select", "AddPageOK"));
                    addNewTemplatePageOnTray(imageKey, imageData);
                    if (pageCountInfo != null)
                        pageCountInfo.setAddedPage(true);
                } else {
                    // 이 이벤트 처리를 GA 로 옮기고 싶다면 FirebaseAnalytics 사용해야함.
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
