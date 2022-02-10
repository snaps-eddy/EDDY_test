package com.snaps.mobile.activity.google_style_image_selector.ui.adapters.phone_strategies;


import android.graphics.Point;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.snaps.common.data.img.ExifUtil;
import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.image.ResolutionUtil;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.activities.processors.ImageSelectUIProcessor;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectPhonePhotoInfo;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectUIPhotoFilter;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectFragmentItemClickListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.performs.ImageSelectPerformForKTBook;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray.ImageSelectTrayBaseAdapter;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.ImageSelectFragmentPhotoBaseSpacingItemDecoration;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.SquareRelativeLayout;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import font.FTextView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ysjeong on 2017. 1. 4..
 */

public abstract class GooglePhotoStyleAdapterStrategyBase {
    private static final String TAG = GooglePhotoStyleAdapterStrategyBase.class.getSimpleName();
    protected ImageSelectActivityV2 activityV2;
    protected AdapterAttribute attribute; //UI처리를 위한 속성들(가로 모드인지, 컬럼 수...등)

    protected ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> photoCursorList; //현재 Adatper에 셋팅된 list(Depth마다 Section이 처리 되어야 하기 때문에 데이터 구조가 다르다.)

    protected Set<View> setThumbnailHistory = null;    //메모리 관리를 위해 bind된 thumbnail뷰는 담아 두었다가, 해제 시킨다.

    protected ImageSelectFragmentPhotoBaseSpacingItemDecoration spacingItemDecoration = null;

    protected IImageSelectFragmentItemClickListener fragmentItemClickListener = null;

    public GooglePhotoStyleAdapterStrategyBase(ImageSelectActivityV2 activityV2, AdapterAttribute attribute, IImageSelectFragmentItemClickListener fragmentItemClickListener) {
        this.activityV2 = activityV2;
        this.attribute = attribute;
        this.setThumbnailHistory = new HashSet<>();
        this.fragmentItemClickListener = fragmentItemClickListener;
    }

    public abstract void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position);

    public abstract ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> convertPhotoPhotoList(ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> list);

    public abstract RecyclerView.ItemDecoration getItemDecoration();

    public abstract int getOptimumThumbnailDimension();

    public AdapterAttribute getAttribute() {
        return attribute;
    }

    protected boolean isLandscapeMode() {
        return attribute != null && attribute.isLandscapeMode();
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HEADER.ordinal()) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_select_act_header_decoration_layout, parent, false);
            setGrayAreaViewVisibleState(view);
            return new ImageSelectAdapterHolders.GooglePhotoStyleHeaderHolder(view);
        }
        return null;
    }

    private void setGrayAreaViewVisibleState(View inflateView) {
        if (inflateView == null || activityV2 == null) return;
//        if (!activityV2.isSingleChooseType() && !activityV2.isMultiChooseType()) {
        if (isShowTrayView()) {
            View grayAreaView = inflateView.findViewById(R.id.top_gray_layout);
            if (grayAreaView != null) grayAreaView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isShowTrayView() {
        if (activityV2 == null || activityV2.getUIProcessor() == null) return false;
        return activityV2.getUIProcessor().isExistTrayView();
    }

    public ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> getPhotoItemList() {
        return photoCursorList;
    }

    public ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH getUIDepth() {
        return attribute != null ? attribute.uiDepth : ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH.DEPTH_DAY;
    }

    public int getItemViewType(int position) {
        if (position == 0)
            return ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HEADER.ordinal();
        GalleryCursorRecord.PhonePhotoFragmentItem item = getItem(position);
        return (item != null ? item.getHolderType().ordinal() : -1);
    }

    public GalleryCursorRecord.PhonePhotoFragmentItem getItem(int position) { //헤더가 있으니까 -1
        final int ITEM_POSITION = position - 1;
        ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> photoList = getPhotoItemList();
        if (photoList != null && photoList.size() > ITEM_POSITION) {
            return photoList.get(ITEM_POSITION);
        }
        return null;
    }

    public int getItemCount() {
        ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> photoList = getPhotoItemList();
        return photoList != null ? photoList.size() + 1 : 1;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    public void setHidden(boolean isHidden) {
        if (attribute == null) return;
        attribute.setHidden(isHidden);
    }

    public boolean isHidden() {
        if (attribute == null) return false;
        return attribute.isHidden();
    }

    public void addHistory(View view) {
        if (setThumbnailHistory != null) setThumbnailHistory.add(view);
    }

    protected void putSectionDateInfo(ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder holder, GalleryCursorRecord.PhonePhotoFragmentItem item) {
        if (holder == null || item == null) return;
        LinearLayout linearLayout = holder.getParentLayout();
        if (linearLayout != null) {
            linearLayout.setTag(item.getPhotoTakenYear() + "." + item.getPhotoTakenMonth() + "." + item.getPhotoTakenDay());
        }
    }

    public void releaseInstance() {
        if (photoCursorList != null) {
            photoCursorList.clear();
            photoCursorList = null;
        }

        if (spacingItemDecoration != null) {
            spacingItemDecoration = null;
        }

        if (fragmentItemClickListener != null) {
            fragmentItemClickListener = null;
        }

        releaseHistory(true);
    }

    public void releaseHistory(boolean isFinalize) {
        if (setThumbnailHistory != null) {
            for (View view : setThumbnailHistory) {
                //TODO  만약 리사이클 비트맵 사용으로 인해 오류가 발생한다면 아래 코드 삭제..
                try {
                    if (view != null) {
                        View thumbnailView = view.findViewById(R.id.imgDetail);
                        if (thumbnailView != null) {
                            ImageLoader.clear(activityV2, thumbnailView);
                        }

                        if (isFinalize) {
                            ViewUnbindHelper.unbindReferences(view, null, false);
                        } else {
                            if (thumbnailView != null) {
                                ViewUnbindHelper.unbindReferences(thumbnailView, null, false);
                            }
                        }
                    }
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }

            if (isFinalize) {
                setThumbnailHistory.clear();
            }
        }
    }

    public GridLayoutManager.SpanSizeLookup getScalableSpanSizeLookUp() {
        return scalableSpanSizeLookUp;
    }

    public void setAttribute(AdapterAttribute attribute) {
        this.attribute = attribute;
    }

    protected ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> getCopiedPhotoList(ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> list) {
        if (list == null) return null;

        ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> copiedList = new ArrayList<>();
        GalleryCursorRecord.PhonePhotoFragmentItem copyItem = null;
        for (GalleryCursorRecord.PhonePhotoFragmentItem item : list) {
            if (item == null) continue;

            copyItem = new GalleryCursorRecord.PhonePhotoFragmentItem();
            copyItem.set(item);
            copiedList.add(copyItem);
        }

        return copiedList;
    }

    protected int getColumnCount() {
        return attribute != null ? attribute.getColumnCount() : 1;
    }

    protected void setHolderLayoutParams(ImageSelectAdapterHolders.PhotoFragmentItemHolder holder, GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem) {
        //TODO ..Hook
    }

    private void setAdditionInfo(GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem) {
        if (phonePhotoItem == null) return;
        MyPhotoSelectImageData imageData = phonePhotoItem.getImgData();
        if (imageData != null) {
            imageData.photoTakenDateTime = phonePhotoItem.getPhotoTakenTime();
            imageData.setExifInfo(phonePhotoItem.getExifInfo());
        }
    }

    protected void processThumbnailHolder(final ImageSelectAdapterHolders.PhotoFragmentItemHolder holder, int position) {
        if (holder == null || getItemCount() <= position) return;

        addHistory(holder.getRootView());

        GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem = getItem(position);
        if (phonePhotoItem == null) return;

        setHolderLayoutParams(holder, phonePhotoItem); //Hook

        phonePhotoItem.setViewHolder(holder);
        phonePhotoItem.setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_THUMBNAIL);
        phonePhotoItem.setListPosition(position);

        String mapKey = phonePhotoItem.getImageKey();
        holder.setImgData(phonePhotoItem.getImgData());
        holder.setMapKey(mapKey);
        holder.setPhonePhotoItem(phonePhotoItem);

        boolean shouldHideCheckIcon = false;
        if (isSmartAnalysisProductSelectType()) {
            FTextView imgLabel = holder.getImgLabel();
            if (imgLabel != null) {
                if (ImageSelectUtils.isContainsInImageHolder(mapKey) && isSelectedCoverImage(mapKey)) {
                    imgLabel.setVisibility(View.VISIBLE);
                    shouldHideCheckIcon = true;
                } else {
                    imgLabel.setVisibility(View.GONE);
                }
            }
        }

        ImageView ivCheckIcon = holder.getCheckIcon();
        if (ivCheckIcon != null) {
            if (shouldHideCheckIcon) {
                ivCheckIcon.setImageResource(0);
                ivCheckIcon.setVisibility(View.GONE);
            } else {
                if (ImageSelectUtils.isContainsInImageHolder(mapKey)) {
                    ivCheckIcon.setImageResource(R.drawable.img_image_select_fragment_checked);
                    ivCheckIcon.setVisibility(View.VISIBLE);
                } else {
                    ivCheckIcon.setImageResource(0);
                    ivCheckIcon.setVisibility(View.GONE);
                }
            }
        }

        // 이미지 크기 구하기
        if (!phonePhotoItem.isVerificationImageRatio() || phonePhotoItem.getImgOutWidth() < 1 || phonePhotoItem.getImgOutHeight() < 1) {
            phonePhotoItem.setVerificationImageRatio(true);
            //스크린샷 이미지인 경우 미디어스토어의 가로,세로 정보가 잘못되는 경우가 발생하는 듯 하다. 매번 가로/세로 구하게 원복
            int[] bitmapSize = CropUtil.getBitmapFilesLength(phonePhotoItem.getPhotoOrgPath());
            if (bitmapSize != null && bitmapSize.length > 1) {
                int outWidth = bitmapSize[0];
                int outHeight = bitmapSize[1];

                phonePhotoItem.setImageDimension(outWidth, outHeight);
            }
        }

        if (phonePhotoItem.shouldGetSmartAnalysisExifInfo()) {
            setSmartAnalysisFromExifToPhotoItem(phonePhotoItem);
        }

        //사진 구성하는 단계에서 이미 set되었지만, 혹시 모르니..
        if (holder.getImgData() == null) {
            holder.setImgData(mapKey,
                    Const_VALUES.SELECT_PHONE,
                    phonePhotoItem.getPhoneDetailId(),
                    phonePhotoItem.getPhoneDetailName(),
                    phonePhotoItem.getPhotoOrgPath(),
                    phonePhotoItem.getPhotoOrgPath(),
                    phonePhotoItem.getPhoneDetailOrientation(),
                    String.valueOf(phonePhotoItem.getImgOutWidth()),
                    String.valueOf(phonePhotoItem.getImgOutHeight()));

            setAdditionInfo(phonePhotoItem);
        } else {
            setAdditionInfo(phonePhotoItem);
        }

        Point photoFilter = new Point(-1, -1);
        ImageSelectUIPhotoFilter photoFilterInfo = attribute.getPhotoFilter();
        if (photoFilterInfo == null) {
            photoFilter = new Point(-1, -1);
        }
        else {
            photoFilter = photoFilterInfo.getPhotoFilterPoint();
        }

        boolean isUnderPixel = false;

        ImageView ivUnderPixel = holder.getNoPrintIcon();
        if (ivUnderPixel != null) {
            if (Config.isSnapsPhotoPrint() || Config.isThemeBook() || Config.isIdentifyPhotoPrint() || Const_PRODUCT.isNewYearsCardProduct() || Const_PRODUCT.isCardProduct() || Const_PRODUCT.isFrameProduct()) {
                int width = 0;
                int height = 0;

                if (phonePhotoItem.getImgOutWidth() >= phonePhotoItem.getImgOutHeight()) {
                    width = phonePhotoItem.getImgOutWidth();
                    height = phonePhotoItem.getImgOutHeight();

                } else {
                    height = phonePhotoItem.getImgOutWidth();
                    width = phonePhotoItem.getImgOutHeight();
                }

                if (width < photoFilter.x || height < photoFilter.y) {
                    isUnderPixel = true;
                }
            } else if (Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct()) {
                SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
                SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
                DataTransManager dataTransManager = DataTransManager.getInstance();
                if (dataTransManager != null) {
                    SnapsLayoutControl snapsLayoutControl = dataTransManager.getSnapsLayoutControl();
                    try {
                        if (snapsLayoutControl != null && snapsTemplate != null)
                            isUnderPixel = ResolutionUtil.isEnableResolution(Float.parseFloat(snapsTemplate.info.F_PAGE_MM_WIDTH), Integer.parseInt(snapsTemplate.info.F_PAGE_PIXEL_WIDTH), snapsLayoutControl.width, phonePhotoItem.getImgData());
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                }
            } else if (Config.isSnapsSticker() && photoFilter.x != -1 && photoFilter.y != -1) {
                int width = phonePhotoItem.getImgOutWidth();
                int height = phonePhotoItem.getImgOutHeight();
                if (phonePhotoItem.getPhoneDetailOrientation() == 90 || phonePhotoItem.getPhoneDetailOrientation() == 270) {
                    width = phonePhotoItem.getImgOutHeight();
                    height = phonePhotoItem.getImgOutWidth();
                }

                if (width < photoFilter.x || height < photoFilter.y) {
                    isUnderPixel = true;
                }
            } else {
                ivUnderPixel.setVisibility(View.GONE);
            }

            if (isUnderPixel) {
                ivUnderPixel.setImageResource(R.drawable.img_tray_noprint_icon);
                ivUnderPixel.setVisibility(View.VISIBLE);
                holder.setDisableClick(true);
                phonePhotoItem.getImgData().isNoPrint = true;
            } else {
                ivUnderPixel.setImageResource(0);
                ivUnderPixel.setVisibility(View.GONE);
                holder.setDisableClick(false);
                phonePhotoItem.getImgData().isNoPrint = false;
            }
        }

        ImageView ivSelector = holder.getSelector();
        if (ivSelector != null) {
            if (ImageSelectUtils.isContainsInImageHolder(mapKey)) {
                ivSelector.setBackgroundResource(R.drawable.shape_red_e36a63_fill_solid_border_rect);
                ivSelector.setVisibility(View.VISIBLE);
            } else if (isUnderPixel) {
                ivSelector.setBackgroundResource(R.drawable.shape_none_line_fill_solid_border_rect);
                ivSelector.setVisibility(View.VISIBLE);
            } else {
                ivSelector.setBackgroundResource(0);
                ivSelector.setVisibility(View.GONE);
            }
        }

        SquareRelativeLayout parentView = holder.getParentView();
        if (parentView != null) {
            parentView.setHolder(holder);
        }

        ImageSelectPhonePhotoInfo photoInfo = phonePhotoItem.getPhotoInfo();
        if (photoInfo != null) {
            String uri = photoInfo.getThumbnailPath();
            ImageView ivThumbnail = holder.getThumbnail();
            if (ivThumbnail != null) {
                int emptyImageRes = R.drawable.color_drawable_eeeeee;

                ImageView.ScaleType scaleType = attribute != null && attribute.getUiDepth() == ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH.DEPTH_STAGGERED ? ImageView.ScaleType.FIT_XY : ImageView.ScaleType.CENTER_CROP;

                ivThumbnail.setScaleType(scaleType);

                ImageSelectUtils.loadImage(activityV2, uri, getOptimumThumbnailDimension(), emptyImageRes, ivThumbnail, scaleType);
            }
        }
    }

    private boolean isSelectedCoverImage(String mapKey) {
        if (activityV2 == null) return false;
        ImageSelectUIProcessor uiProcessor = activityV2.getUIProcessor();
        return uiProcessor != null && uiProcessor.isContainCoverImageKey(mapKey);
    }

    private boolean isSmartAnalysisProductSelectType() {
        ImageSelectUIProcessor uiProcessor = activityV2 != null ? activityV2.getUIProcessor() : null;
        return uiProcessor != null && uiProcessor.isSmartRecommendBookProduct();
    }

    private void setSmartAnalysisFromExifToPhotoItem(GalleryCursorRecord.PhonePhotoFragmentItem photoItem) {
        try {
            photoItem.setExifInfo(ExifUtil.getExifInfoWithFilePath(photoItem.getPhotoOrgPath()));
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    protected GridLayoutManager.SpanSizeLookup scalableSpanSizeLookUp = new GridLayoutManager.SpanSizeLookup() {
        @Override
        public int getSpanSize(int position) {
            int viewType = getItemViewType(position);

            if (viewType == ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_YEAR_SECTION.ordinal()
                    || viewType == ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_MONTH_SECTION.ordinal()
                    || viewType == ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_COMMON_DATE_SECTION.ordinal()) {
                return getColumnCount();
            } else if (viewType == ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HEADER.ordinal())
                return getColumnCount();
            else
                return getThumbnailSpanSizeLookupCount(position);
        }
    };

    protected int getThumbnailSpanSizeLookupCount(int position) {
        return 1;
    }

    protected boolean checkAllSelectPictureSection(int position) {
        final int ITEM_POSITION = position;
        ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> photoList = getPhotoItemList();
        for (int i = ITEM_POSITION; i <= photoList.size() - 1; i++) {
            GalleryCursorRecord.PhonePhotoFragmentItem item = photoList.get(i);
            if (item.getHolderType() != ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_THUMBNAIL) {
                break;
            }

            String mapKey = item.getImageKey();
            if (!ImageSelectUtils.isContainsInImageHolder(mapKey)) {
                return false;
            }
        }

        return true;
    }

    protected boolean selectOrDeselectPictureSection(int position, final boolean isClick) {
        final ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> photoList = totalSelectImageCount(position);
        final ImageSelectUIProcessor uiProcessor = activityV2.getUIProcessor();
        int maxImageCount = uiProcessor.getCurrentMaxImageCount();
        int currentImageCount = uiProcessor.getCurrentImageCount();
        int selectImggeCount = photoList.size();
        final int selectImageCount = maxImageCount - currentImageCount;

        boolean shouldCheckForMaxImageCount = !activityV2.isMultiChooseType() && !Config.isSmartSnapsRecommendLayoutPhotoBook();
        if (shouldCheckForMaxImageCount && isClick && maxImageCount < currentImageCount + selectImggeCount) {
            if (Config.isPhotobooks() && !Config.isKTBook()) {
                if (uiProcessor.isSmartSelectType()) {
                    MessageUtil.toast(activityV2, R.string.disable_add_photo);
                    selectOrDeselect(uiProcessor, photoList, isClick, selectImageCount);

                } else {
                    MessageUtil.alertnoTitle(activityV2, activityV2.getString(R.string.page_add_pay_msg), new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK) {
                                selectOrDeselect(uiProcessor, photoList, isClick, -1, true);
                            } else {
                                selectOrDeselect(uiProcessor, photoList, isClick, selectImageCount);
                            }
                        }
                    });
                }
            } else if (Config.isSnapsPhotoPrint()) {
                selectOrDeselect(uiProcessor, photoList, isClick);
            } else {
                //KT 북
                if (Config.isKTBook()) {
                    selectOrDeselect(uiProcessor, photoList, isClick, selectImageCount);
                    MessageUtil.toast(getApplicationContext(), getApplicationContext().getString(R.string.select_some_photos, ImageSelectPerformForKTBook.MAX_KT_BOOK_IMAGE_COUNT));
                    return false;
                }

                String msg = String.format(activityV2.getString(R.string.select_excess_picture_except_picture), maxImageCount + "");

                selectOrDeselect(uiProcessor, photoList, isClick, selectImageCount);
                MessageUtil.alertnoTitleOneBtn(activityV2, msg, null);

                return false;
            }
        } else {
            selectOrDeselect(uiProcessor, photoList, isClick);
        }
        return true;
    }

    private void selectOrDeselect(ImageSelectUIProcessor uiProcessor, ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> photoList, boolean isClick) {
        selectOrDeselect(uiProcessor, photoList, isClick, -1, false);
    }

    private void selectOrDeselect(ImageSelectUIProcessor uiProcessor, ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> photoList, boolean isClick, int position) {
        selectOrDeselect(uiProcessor, photoList, isClick, position, false);
    }

    private void selectOrDeselect(ImageSelectUIProcessor uiProcessor, ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> photoList,
                                  boolean isClick, int position, boolean isAgreeAddPages) {
        if (photoList == null || photoList.size() == 0 || position == 0) return;
        int size = position;
        if (position == -1) {
            size = photoList.size();
        }
        int count = 0;
        for (int i = 0; i <= photoList.size() - 1; i++) {
            GalleryCursorRecord.PhonePhotoFragmentItem item = photoList.get(i);
            boolean first = false;
            boolean last = false;
            if (i == 0) {
                first = true;
            }

            if (count == size - 1) {
                last = true;
            }
            checkPhotoItem(item);
            String mapKey = item.getImageKey();
            if (isClick) {
                if (!ImageSelectUtils.isContainsInImageHolder(mapKey)) {
                    ImageSelectUtils.putSelectedImageData(mapKey, item.getImgData());

                    //bug fix
                    if (isAgreeAddPages) {
                        uiProcessor.getTrayAdapter().getPageCountInfo().setAddedPage(true);
                    }

                    boolean insertedPhoto = uiProcessor.tryInsertImageDataToHolderNoAnimation(item, last);
                    if (!insertedPhoto) {
                        break;
                    }
                    count++;
                }
            } else {
                ImageSelectUtils.removeSelectedImageData(mapKey);
                ImageSelectTrayBaseAdapter trayBaseAdapter = uiProcessor.getTrayAdapter();
                if (trayBaseAdapter != null)
                    trayBaseAdapter.removeSelectedImageArray(mapKey, first, last);
                count++;
            }

            if (count == size)
                break;

        }
        uiProcessor.notifyListUpdateListener(null);
    }


    private void checkPhotoItem(GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem) {
        if (!phonePhotoItem.isVerificationImageRatio() || phonePhotoItem.getImgOutWidth() < 1 || phonePhotoItem.getImgOutHeight() < 1) {
            phonePhotoItem.setVerificationImageRatio(true);

            int outWidth = phonePhotoItem.getImgOutWidth();
            int outHeight = phonePhotoItem.getImgOutHeight();
            if (outWidth < 1 || outHeight < 1) {
                int[] bitmapSize = CropUtil.getBitmapFilesLength(phonePhotoItem.getPhotoOrgPath());
                if (bitmapSize != null && bitmapSize.length > 1) {
                    outWidth = bitmapSize[0];
                    outHeight = bitmapSize[1];
                }
            }
            phonePhotoItem.setImageDimension(outWidth, outHeight);
        }

        if (phonePhotoItem.shouldGetSmartAnalysisExifInfo()) {
            setSmartAnalysisFromExifToPhotoItem(phonePhotoItem);
        }
    }

    private ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> totalSelectImageCount(int position) {
        ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> originalPhotoList = getPhotoItemList();
        ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> selectPhotoList = new ArrayList<>();
        for (int i = position; i <= originalPhotoList.size() - 1; i++) {
            GalleryCursorRecord.PhonePhotoFragmentItem item = originalPhotoList.get(i);
            if (originalPhotoList.get(i).getHolderType() != ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_THUMBNAIL) {
                break;
            }
            selectPhotoList.add(item);
        }

        return selectPhotoList;
    }

    public static class AdapterAttribute {
        private ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH uiDepth;
        private boolean isHidden;
        private boolean isEnableGroupSelect;
        private boolean isLandscapeMode;
        private int columnCount = 0;
        private ImageSelectUIPhotoFilter photoFilter;

        private AdapterAttribute(Builder builder) {
            if (builder == null) return;
            this.uiDepth = builder.uiDepth;
            this.isLandscapeMode = builder.isLandscapeMode;
            this.columnCount = builder.columnCount;
            this.isHidden = builder.isHidden;
            this.isEnableGroupSelect = builder.isEnableGroupSelect;
            this.photoFilter = builder.photoFilter;
        }

        public ImageSelectUIPhotoFilter getPhotoFilter() {
            return photoFilter;
        }

        public boolean isEnableGroupSelect() {
            return isEnableGroupSelect;
        }

        public boolean isHidden() {
            return isHidden;
        }

        public void setHidden(boolean hidden) {
            isHidden = hidden;
        }

        public ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH getUiDepth() {
            return uiDepth;
        }

        public boolean isLandscapeMode() {
            return isLandscapeMode;
        }

        public int getColumnCount() {
            return columnCount;
        }

        public static class Builder {
            private ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH uiDepth;
            private boolean isEnableGroupSelect;
            private boolean isLandscapeMode;
            private boolean isHidden;
            private int columnCount;
            private ImageSelectUIPhotoFilter photoFilter;

            public Builder setPhotoFilter(ImageSelectUIPhotoFilter photoFilter) {
                this.photoFilter = photoFilter;
                return this;
            }

            public Builder setEnableGroupSelect(boolean enableGroupSelect) {
                isEnableGroupSelect = enableGroupSelect;
                return this;
            }

            public Builder setHidden(boolean hidden) {
                isHidden = hidden;
                return this;
            }

            public Builder setColumnCount(int columnCount) {
                this.columnCount = columnCount;
                return this;
            }

            public Builder setUiDepth(ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH uiDepth) {
                this.uiDepth = uiDepth;
                return this;
            }

            public Builder setLandscapeMode(boolean landscapeMode) {
                isLandscapeMode = landscapeMode;
                return this;
            }

            public AdapterAttribute create() {
                return new AdapterAttribute(this);
            }
        }
    }
}
