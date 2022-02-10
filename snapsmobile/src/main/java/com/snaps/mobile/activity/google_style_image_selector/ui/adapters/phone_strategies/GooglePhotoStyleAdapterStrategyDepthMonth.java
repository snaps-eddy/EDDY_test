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

public class GooglePhotoStyleAdapterStrategyDepthMonth extends GooglePhotoStyleAdapterStrategyBase {

    public GooglePhotoStyleAdapterStrategyDepthMonth(ImageSelectActivityV2 activityV2, AdapterAttribute attribute, IImageSelectFragmentItemClickListener fragmentItemClickListener) {
        super(activityV2, attribute, fragmentItemClickListener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType < 0) return null;

        ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE[] holerTypes = ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.values();
        switch (holerTypes[viewType]) {
            case HOLDER_TYPE_COMMON_DATE_SECTION: return getDateSectionViewHolder(parent);
            case HOLDER_TYPE_THUMBNAIL: return getThumbnailViewHolder(parent);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public RecyclerView.ItemDecoration getItemDecoration() {
        if (spacingItemDecoration == null) {
            int margin = (int) activityV2.getResources().getDimension(R.dimen.image_select_fragment_item_margin_depth_month);
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

        int prevMonth = -1;
        int prevYear = -1;

        for (int ii = 0; ii < copiedList.size(); ii++) {
            GalleryCursorRecord.PhonePhotoFragmentItem currentPhonePhotoItem = copiedList.get(ii);
            if (currentPhonePhotoItem == null) continue;

            currentPhonePhotoItem.setUiDepth(ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH.DEPTH_MONTH);

            int yearOfCurrentItem = currentPhonePhotoItem.getPhotoTakenYear();
            int monthOfCurrentItem = currentPhonePhotoItem.getPhotoTakenMonth();

            if ((monthOfCurrentItem != prevMonth) || (yearOfCurrentItem != prevYear)) {
                prevMonth = monthOfCurrentItem;
                prevYear = yearOfCurrentItem;
                GalleryCursorRecord.PhonePhotoFragmentItem monthSection = new GalleryCursorRecord.PhonePhotoFragmentItem();

                monthSection.setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_COMMON_DATE_SECTION);
                monthSection.setPhotoInfo(currentPhonePhotoItem.getPhotoInfo());
                photoCursorList.add(monthSection);
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
        return ImageSelectUtils.getUIDepthOptimumThumbnailDimension(activityV2, ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH.DEPTH_MONTH, isLandscapeMode());
    }

    private void processSectionHolder(final ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder holder, final int position) {
        GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem = getItem(position);
        if (phonePhotoItem == null) return;

        putSectionDateInfo(holder, phonePhotoItem); //섹션에 날짜 정보를 넣는다.

        phonePhotoItem.setViewHolder(holder);
        phonePhotoItem.setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_COMMON_DATE_SECTION);

        Calendar currentCalendar = Calendar.getInstance();
        final int CURRENT_YEAR = currentCalendar.get(Calendar.YEAR);

        boolean isWithYear = CURRENT_YEAR != phonePhotoItem.getPhotoTakenYear(); //올해 찍은 사진은 월만 표시한다.

        String szSectionTitle = isWithYear ? StringUtil.getYearAndMonthWithText(phonePhotoItem.getPhotoTakenYear(), phonePhotoItem.getPhotoTakenMonth()) : StringUtil.getMonthWithText(phonePhotoItem.getPhotoTakenMonth());

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
