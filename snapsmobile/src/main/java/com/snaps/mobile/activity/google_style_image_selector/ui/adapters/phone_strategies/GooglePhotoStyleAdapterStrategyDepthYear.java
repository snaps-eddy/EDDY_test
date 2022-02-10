package com.snaps.mobile.activity.google_style_image_selector.ui.adapters.phone_strategies;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectPhonePhotoInfo;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectFragmentItemClickListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.ImageSelectFragmentPhotoBaseSpacingItemDecoration;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.SquareRelativeLayout;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 1. 4..
 */
public class GooglePhotoStyleAdapterStrategyDepthYear extends GooglePhotoStyleAdapterStrategyBase {

    public GooglePhotoStyleAdapterStrategyDepthYear(ImageSelectActivityV2 context, AdapterAttribute attribute, IImageSelectFragmentItemClickListener fragmentItemClickListener) {
        super(context, attribute, fragmentItemClickListener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType < 0) return null;

        ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE[] holderTypes = ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.values();
        switch (holderTypes[viewType]) {
            case HOLDER_TYPE_YEAR_SECTION: return getYearSectionViewHolder(parent);
            case HOLDER_TYPE_MONTH_SECTION: return getMonthSectionViewHolder(parent);
            case HOLDER_TYPE_THUMBNAIL: return getThumbnailViewHolder(parent);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public RecyclerView.ItemDecoration getItemDecoration() {
        if (spacingItemDecoration == null) {
            int margin = (int) activityV2.getResources().getDimension(R.dimen.image_select_fragment_item_margin_depth_year);
            if (attribute != null) {
                spacingItemDecoration = new ImageSelectFragmentPhotoBaseSpacingItemDecoration(activityV2, margin, attribute.getColumnCount());
            }
            else {
                spacingItemDecoration = new ImageSelectFragmentPhotoBaseSpacingItemDecoration(activityV2, margin);
            }
        }
        return spacingItemDecoration;
    }

    @Override
    public ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> convertPhotoPhotoList(ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> list) {
        if (list == null) return null;

        ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> copiedList = getCopiedPhotoList(list);

        photoCursorList = new ArrayList<>();

        int prevYear = -1;
        int prevMonth = -1;

        int inputCount = 0;
        final int MAX_INPUT_COUNT = (ISnapsImageSelectConstants.COLUMN_COUNT_OF_UI_DEPTH_YEAR * 3); //최대 3줄까지만...

        for (int ii = 0; ii < copiedList.size(); ii++) {
            GalleryCursorRecord.PhonePhotoFragmentItem currentPhonePhotoItem = copiedList.get(ii);
            if (currentPhonePhotoItem == null) continue;

            currentPhonePhotoItem.setUiDepth(ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH.DEPTH_YEAR);

            int yearOfCurrentItem = currentPhonePhotoItem.getPhotoTakenYear();
            if (yearOfCurrentItem != prevYear) {
                prevYear = yearOfCurrentItem;
                prevMonth = currentPhonePhotoItem.getPhotoTakenMonth();
                GalleryCursorRecord.PhonePhotoFragmentItem yearSection = new GalleryCursorRecord.PhonePhotoFragmentItem();
                yearSection.setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_YEAR_SECTION);
                yearSection.setPhotoInfo(currentPhonePhotoItem.getPhotoInfo());
                photoCursorList.add(yearSection);
                inputCount = 0;
            }

            int monthOfCurrentItem = currentPhonePhotoItem.getPhotoTakenMonth();
            if (monthOfCurrentItem != prevMonth) {
                prevMonth = monthOfCurrentItem;
                GalleryCursorRecord.PhonePhotoFragmentItem monthSection = new GalleryCursorRecord.PhonePhotoFragmentItem();
                monthSection.setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_MONTH_SECTION);
                monthSection.setPhotoInfo(currentPhonePhotoItem.getPhotoInfo());
                photoCursorList.add(monthSection);
                inputCount = 0;
            }

            if(++inputCount > MAX_INPUT_COUNT) {
                GalleryCursorRecord.PhonePhotoFragmentItem lastItem = photoCursorList.get(photoCursorList.size() - 1);
                if (lastItem != null) {
                    lastItem.addSubKey(currentPhonePhotoItem.getPhoneDetailId());
                }
                continue;
            }

            currentPhonePhotoItem.setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_THUMBNAIL);
            photoCursorList.add(currentPhonePhotoItem);
        }

        return photoCursorList;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder == null || attribute == null) return;

        if (holder instanceof ImageSelectAdapterHolders.GooglePhotoStyleDepthYearsYearSectionHolder) {
            processYearHolder((ImageSelectAdapterHolders.GooglePhotoStyleDepthYearsYearSectionHolder) holder, position);
        } else if (holder instanceof ImageSelectAdapterHolders.GooglePhotoStyleDepthYearsMonthSectionHolder) {
            processMonthHolder((ImageSelectAdapterHolders.GooglePhotoStyleDepthYearsMonthSectionHolder) holder, position);
        } else if (holder instanceof ImageSelectAdapterHolders.PhotoFragmentItemHolder) {
            processThumbnailHolder((ImageSelectAdapterHolders.PhotoFragmentItemHolder) holder, position);
        }
    }

    @Override
    public int getOptimumThumbnailDimension() {
        return ImageSelectUtils.getUIDepthOptimumThumbnailDimension(activityV2, ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH.DEPTH_YEAR, isLandscapeMode());
    }

    private void processYearHolder(ImageSelectAdapterHolders.GooglePhotoStyleDepthYearsYearSectionHolder holder, int position) {
        GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem = getItem(position);
        if (phonePhotoItem == null) return;

        putSectionDateInfo(holder, phonePhotoItem); //섹션에 날짜 정보를 넣는다.

        phonePhotoItem.setViewHolder(holder);
        phonePhotoItem.setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_YEAR_SECTION);

        String szYear = StringUtil.getYearWithText(phonePhotoItem.getPhotoTakenYear());
        String szMonth = StringUtil.getMonthWithText(phonePhotoItem.getPhotoTakenMonth());

        TextView tvYear = holder.getTvSectionTitle();
        tvYear.setText(szYear);

        TextView tvMonth = holder.getTvSectionSub();
        tvMonth.setText(szMonth);
    }

    private RecyclerView.ViewHolder getYearSectionViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.google_photo_style_section_for_depth_01_year_item, parent, false);
        return new ImageSelectAdapterHolders.GooglePhotoStyleDepthYearsYearSectionHolder(view);
    }

    private RecyclerView.ViewHolder getMonthSectionViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.google_photo_style_section_for_depth_01_month_item, parent, false);
        return new ImageSelectAdapterHolders.GooglePhotoStyleDepthYearsMonthSectionHolder(view);
    }

    private RecyclerView.ViewHolder getThumbnailViewHolder(ViewGroup parent) {
        if (attribute == null) return null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_imagedetail_item_for_google_style, parent, false);
        return new ImageSelectAdapterHolders.PhotoFragmentItemHolder(view, activityV2, attribute);
    }

    private void processMonthHolder(ImageSelectAdapterHolders.GooglePhotoStyleDepthYearsMonthSectionHolder holder, int position) {
        GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem = getItem(position);
        if (phonePhotoItem == null) return;

        putSectionDateInfo(holder, phonePhotoItem); //섹션에 날짜 정보를 넣는다.

        phonePhotoItem.setViewHolder(holder);
        phonePhotoItem.setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_MONTH_SECTION);

        String szMonth = StringUtil.getMonthWithText(phonePhotoItem.getPhotoTakenMonth());

        TextView tvMonth = holder.getTvSectionTitle();
        tvMonth.setText(szMonth);
    }

    @Override
    protected void processThumbnailHolder(final ImageSelectAdapterHolders.PhotoFragmentItemHolder holder, int position) {
        if (holder == null) return;

        addHistory(holder.getRootView());

        GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem = getItem(position);
        if (phonePhotoItem == null) return;

        phonePhotoItem.setViewHolder(holder);
        phonePhotoItem.setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_THUMBNAIL);

        //사진 구성하는 단계에서 이미 set되었지만, 혹시 모르니..
        String mapKey = phonePhotoItem.getImageKey();
        holder.setImgData(phonePhotoItem.getImgData());
        holder.setMapKey(mapKey);
        holder.setPhonePhotoItem(phonePhotoItem);

        SquareRelativeLayout parentView = holder.getParentView();
        if (parentView != null) {
            parentView.setHolder(holder);
        }

            ImageSelectPhonePhotoInfo photoInfo = phonePhotoItem.getPhotoInfo();
            if (photoInfo != null) {
                String uri = photoInfo.getThumbnailPath();
                ImageView ivThumbnail = holder.getThumbnail();
                if (ivThumbnail != null) {
                    ivThumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    ImageSelectUtils.loadImage(activityV2, uri, getOptimumThumbnailDimension(), ivThumbnail, ImageView.ScaleType.CENTER_CROP);
                }
            }
     }
}
