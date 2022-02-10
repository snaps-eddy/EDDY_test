package com.snaps.mobile.activity.google_style_image_selector.ui.adapters.phone_strategies;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectFragmentItemClickListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.ImageSelectFragmentPhotoBaseSpacingItemDecoration;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by ysjeong on 2017. 1. 4..
 */

public class GooglePhotoStyleAdapterStrategyDepthDay extends GooglePhotoStyleAdapterStrategyBase {
    private final int CURRENT_YEAR;
    private final int CURRENT_MONTH;
    private final int CURRENT_DAY;

    public GooglePhotoStyleAdapterStrategyDepthDay(ImageSelectActivityV2 activityV2, AdapterAttribute attribute, IImageSelectFragmentItemClickListener fragmentItemClickListener) {
        super(activityV2, attribute, fragmentItemClickListener);

        Calendar currentCalendar = Calendar.getInstance();
        CURRENT_YEAR = currentCalendar.get(Calendar.YEAR);
        CURRENT_MONTH = currentCalendar.get(Calendar.MONTH) + 1;
        CURRENT_DAY = currentCalendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType < 0) return null;

        ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE[] holderTypes = ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.values();
        switch (holderTypes[viewType]) {
            case HOLDER_TYPE_COMMON_DATE_SECTION: return getDateSectionViewHolder(parent);
            case HOLDER_TYPE_THUMBNAIL: return getThumbnailViewHolder(parent);
        }

        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public RecyclerView.ItemDecoration getItemDecoration() {
        if (spacingItemDecoration == null) {

            int margin = (int) activityV2.getResources().getDimension(R.dimen.image_select_fragment_item_margin_depth_day);
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

        int prevDay = -1;
        int prevMonth = -1;
        int prevYear = -1;

        for (int ii = 0; ii < copiedList.size(); ii++) {
            GalleryCursorRecord.PhonePhotoFragmentItem currentPhonePhotoItem = copiedList.get(ii);
            if (currentPhonePhotoItem == null) continue;

            currentPhonePhotoItem.setUiDepth(ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH.DEPTH_DAY);

            int yearOfCurrentItem = currentPhonePhotoItem.getPhotoTakenYear();
            int monthOfCurrentItem = currentPhonePhotoItem.getPhotoTakenMonth();
            int dayOfCurrentItem = currentPhonePhotoItem.getPhotoTakenDay();

            if ((dayOfCurrentItem != prevDay) || (monthOfCurrentItem != prevMonth) || (yearOfCurrentItem != prevYear)) {
                prevDay = dayOfCurrentItem;
                prevMonth = monthOfCurrentItem;
                prevYear = yearOfCurrentItem;
                GalleryCursorRecord.PhonePhotoFragmentItem titleSection = new GalleryCursorRecord.PhonePhotoFragmentItem();

                titleSection.setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_COMMON_DATE_SECTION);
                titleSection.setPhotoInfo(currentPhonePhotoItem.getPhotoInfo());
                photoCursorList.add(titleSection);
            }

            currentPhonePhotoItem.setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_THUMBNAIL);
            photoCursorList.add(currentPhonePhotoItem);
        }

        return photoCursorList;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder == null || attribute == null) return;

        if (holder instanceof ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder) {
            processSectionHolder((ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder) holder, position);
        } else if (holder instanceof ImageSelectAdapterHolders.PhotoFragmentItemHolder) {
            processThumbnailHolder((ImageSelectAdapterHolders.PhotoFragmentItemHolder) holder, position);
        }
    }

    @Override
    public int getOptimumThumbnailDimension() {
        return ImageSelectUtils.getUIDepthOptimumThumbnailDimension(activityV2, ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH.DEPTH_DAY, isLandscapeMode());
    }

    private void processSectionHolder(final ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder holder, final int position) {
        GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem = getItem(position);
        if (phonePhotoItem == null) return;

        putSectionDateInfo(holder, phonePhotoItem); //섹션에 날짜 정보를 넣는다.

        phonePhotoItem.setViewHolder(holder);
        phonePhotoItem.setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_COMMON_DATE_SECTION);

        int photoYear = phonePhotoItem.getPhotoTakenYear();
        int photoMonth = phonePhotoItem.getPhotoTakenMonth();
        int photoDay = phonePhotoItem.getPhotoTakenDay();

        boolean isWithYear = CURRENT_YEAR != photoYear; //올해 찍은 사진은 월 + 요일만 표시한다.
        boolean isTakenToday = CURRENT_YEAR == photoYear && CURRENT_MONTH == photoMonth && CURRENT_DAY == photoDay; //오늘 찍은 사진은 오늘로 표기한다.
        boolean isTakenYesterday = CURRENT_YEAR == photoYear && CURRENT_MONTH == photoMonth && (CURRENT_DAY - 1) == photoDay; //어제 찍은 사진은 어제로 표기한다.

        String szSectionTitle = null;
        if (isTakenToday) {
            szSectionTitle = activityV2.getString(R.string.today);
        } else if (isTakenYesterday) {
            szSectionTitle = activityV2.getString(R.string.yesterday);
        } else if (isWithYear) {
            szSectionTitle = StringUtil.getYearAndMonthAndDayAndDayOfWeekWithText(
                    photoYear, photoMonth, photoDay, phonePhotoItem.getPhotoTakenDayOfWeek());
        } else {
            szSectionTitle = StringUtil.getMonthAndDayAndDayOfWeekWithText(
                    photoMonth, photoDay, phonePhotoItem.getPhotoTakenDayOfWeek());
        }

        TextView tvSectionTitle = holder.getTvSectionTitle();
        if (tvSectionTitle != null)
            tvSectionTitle.setText(szSectionTitle);
        if(!activityV2.isSingleChooseType() && Const_PRODUCT.isMultiImageSelectProduct()) {
            holder.getImageSelect().setSelected(checkAllSelectPictureSection(position));
            holder.getLinearLayoutSelect().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.getImageSelect().setSelected(!holder.getImageSelect().isSelected());
                    selectOrDeselectPictureSection(position, holder.getImageSelect().isSelected());
                }
            });
        }
        holder.setGroupId(phonePhotoItem.getGroupKey());
    }

    private RecyclerView.ViewHolder getDateSectionViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.google_photo_style_section_item, parent, false);
        ImageButton imageView = (ImageButton) view.findViewById(R.id.imageButtonSelect) ;
        if(imageView != null) {
            if(!activityV2.isSingleChooseType() && Const_PRODUCT.isMultiImageSelectProduct()) {
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(View.GONE);
            }
        }
        return new ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder(view);
    }

    private RecyclerView.ViewHolder getThumbnailViewHolder(ViewGroup parent) {
        if (attribute == null) return null;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_imagedetail_item_for_google_style, parent, false);
        return new ImageSelectAdapterHolders.PhotoFragmentItemHolder(view, activityV2, attribute);
    }
}
