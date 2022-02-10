package com.snaps.mobile.activity.google_style_image_selector.ui.adapters.phone_strategies;

import android.content.res.Resources;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.ui.BSize;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectGroupIndexInfo;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectFragmentItemClickListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.ImageSelectFragmentPhotoBaseSpacingItemDecoration;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.SquareRelativeLayout;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.google_style_image_selector.utils.StaggeredLayoutCalculator;
import com.snaps.mobile.activity.selectimage.adapter.GalleryCursorRecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by ysjeong on 2017. 1. 4..
 */

public class GooglePhotoStyleAdapterStrategyDepthStaggered extends GooglePhotoStyleAdapterStrategyBase {

    public GooglePhotoStyleAdapterStrategyDepthStaggered(ImageSelectActivityV2 activityV2, AdapterAttribute attribute, IImageSelectFragmentItemClickListener fragmentItemClickListener) {
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
            int margin = (int) activityV2.getResources().getDimension(R.dimen.image_select_fragment_item_margin_depth_staggered);
            spacingItemDecoration = new ImageSelectFragmentPhotoBaseSpacingItemDecoration(activityV2, margin);
        }
        return spacingItemDecoration;
    }

    @Override
    public ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> convertPhotoPhotoList(ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> list) {
        if (list == null) return null;

        ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> copiedList = getCopiedPhotoList(list);

        photoCursorList = new ArrayList<>();
        LinkedHashMap<String, ImageSelectGroupIndexInfo> mapPhotoGroup = new LinkedHashMap<>();

        int prevDay = -1;
        int prevMonth = -1;
        int prevYear = -1;

        ImageSelectGroupIndexInfo groupIndex = null;

        for (int ii = 0; ii < copiedList.size(); ii++) {
            GalleryCursorRecord.PhonePhotoFragmentItem currentPhonePhotoItem = copiedList.get(ii);
            if (currentPhonePhotoItem == null) continue;

            currentPhonePhotoItem.setUiDepth(ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH.DEPTH_STAGGERED);

            int yearOfCurrentItem = currentPhonePhotoItem.getPhotoTakenYear();
            int monthOfCurrentItem = currentPhonePhotoItem.getPhotoTakenMonth();
            int dayOfCurrentItem = currentPhonePhotoItem.getPhotoTakenDay();
            String groupKey = ImageSelectUtils.getPhonePhotoGroupKey(getUIDepth(), currentPhonePhotoItem);

            if ((dayOfCurrentItem != prevDay) || (monthOfCurrentItem != prevMonth) || (yearOfCurrentItem != prevYear)) {
                prevDay = dayOfCurrentItem;
                prevMonth = monthOfCurrentItem;
                prevYear = yearOfCurrentItem;
                GalleryCursorRecord.PhonePhotoFragmentItem titleSection = new GalleryCursorRecord.PhonePhotoFragmentItem();

                titleSection.setGroupKey(groupKey);
                titleSection.setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_COMMON_DATE_SECTION);
                titleSection.setPhotoInfo(currentPhonePhotoItem.getPhotoInfo());
                photoCursorList.add(titleSection);

                groupIndex = new ImageSelectGroupIndexInfo();
                groupIndex.setStartIdx(photoCursorList.size());
            }

            currentPhonePhotoItem.setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_THUMBNAIL);
            currentPhonePhotoItem.setGroupKey(groupKey);
            photoCursorList.add(currentPhonePhotoItem);

            if (groupIndex != null) {
                int cursorIndex = photoCursorList.size() - 1;
                String mapKey = ImageSelectUtils.getPhonePhotoMapKey(currentPhonePhotoItem.getPhoneDetailId());
                groupIndex.addPhotoIndex(cursorIndex, mapKey);
                groupIndex.setEndIdx(cursorIndex);
                if (groupKey != null)
                    mapPhotoGroup.put(groupKey, groupIndex);
            }
        }

        calculateStaggeredLayoutByPhotoGroup(mapPhotoGroup);

        return photoCursorList;
    }

    private void calculateStaggeredLayoutByPhotoGroup(LinkedHashMap<String, ImageSelectGroupIndexInfo> mapPhotoGroup) {
        if (mapPhotoGroup == null || photoCursorList == null) return;

        Set<String> keySet = mapPhotoGroup.keySet();
        ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> convertList = null;   //new ArrayList<>();
        for (String key : keySet) {
            if (key == null) continue;
            ImageSelectGroupIndexInfo groupIndexInfo = mapPhotoGroup.get(key);
            if (groupIndexInfo == null) continue;

            convertList = new ArrayList<>();
            for (int ii = groupIndexInfo.getStartIdx(); ii <= groupIndexInfo.getEndIdx(); ii++) {
                if (photoCursorList.size() <= ii || ii < 0) continue;
                GalleryCursorRecord.PhonePhotoFragmentItem item = photoCursorList.get(ii);
                convertList.add(item);
            }

            if (!convertList.isEmpty())
                calculateStaggeredLayout(convertList);
        }

        mapPhotoGroup.clear();
    }

    private void calculateStaggeredLayout(ArrayList<GalleryCursorRecord.PhonePhotoFragmentItem> taskList) {
        if (activityV2 == null) return;

        // 셀사이즈 구하기
        new StaggeredLayoutCalculator(activityV2, taskList);
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
        return ImageSelectUtils.getUIDepthOptimumThumbnailDimension(activityV2, ISnapsImageSelectConstants.eGOOGLE_STYLE_DEPTH.DEPTH_STAGGERED, isLandscapeMode());
    }

    @Override
    protected int getColumnCount() {
        //화면 픽셀을 초과해선 안된다.
        int columnCount = attribute != null ? attribute.getColumnCount() : 0;

        if (activityV2 != null) {
            Resources resources = activityV2.getResources();
            if (resources != null) {
                DisplayMetrics metrics = resources.getDisplayMetrics();
                int screenWidth = metrics != null ? metrics.widthPixels : 0;
                return Math.min(columnCount, screenWidth);
            }
        }

        return attribute != null ? attribute.getColumnCount() : 1;
    }

    @Override
    protected int getThumbnailSpanSizeLookupCount(int position) {
        GalleryCursorRecord.PhonePhotoFragmentItem item = getItem(position);
        if (item != null) {
            int maxScreenWidth = 0;
            BSize bSize = item.getConvertedStaggeredSize();
            if (bSize != null) {
                //화면 픽셀을 초과해선 안된다.
                int columnCount = attribute != null ? attribute.getColumnCount() : 0;
                maxScreenWidth = Math.min(columnCount, (int) bSize.getWidth());
            }

            return maxScreenWidth > 0 ? maxScreenWidth : 1;
        }

        return 1;
    }

    private void processSectionHolder(final ImageSelectAdapterHolders.GooglePhotoStyleSectionHolder holder,final int position) {
        GalleryCursorRecord.PhonePhotoFragmentItem phonePhotoItem = getItem(position);
        if (phonePhotoItem == null) return;

        putSectionDateInfo(holder, phonePhotoItem); //섹션에 날짜 정보를 넣는다.

        phonePhotoItem.setViewHolder(holder);
        phonePhotoItem.setHolderType(ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HOLDER_TYPE_COMMON_DATE_SECTION);

        Calendar currentCalendar = Calendar.getInstance();
        final int CURRENT_YEAR = currentCalendar.get(Calendar.YEAR);
        final int CURRENT_MONTH = currentCalendar.get(Calendar.MONTH) + 1;
        final int CURRENT_DAY = currentCalendar.get(Calendar.DAY_OF_MONTH);

        boolean isWithYear = CURRENT_YEAR != phonePhotoItem.getPhotoTakenYear(); //올해 찍은 사진은 월 + 요일만 표시한다.
        boolean isTakenToday = CURRENT_YEAR == phonePhotoItem.getPhotoTakenYear()
                && CURRENT_MONTH == phonePhotoItem.getPhotoTakenMonth() && CURRENT_DAY == phonePhotoItem.getPhotoTakenDay(); //오늘 찍은 사진은 오늘로 표기한다.
        boolean isTakenYesterday = CURRENT_YEAR == phonePhotoItem.getPhotoTakenYear()
                && CURRENT_MONTH == phonePhotoItem.getPhotoTakenMonth() && (CURRENT_DAY - 1) == phonePhotoItem.getPhotoTakenDay(); //어제 찍은 사진은 어제로 표기한다.

        String szSectionTitle = null;
        if (isTakenToday) {
            szSectionTitle = activityV2.getString(R.string.today);
        } else if (isTakenYesterday) {
            szSectionTitle = activityV2.getString(R.string.yesterday);
        } else if (isWithYear) {
            szSectionTitle = StringUtil.getYearAndMonthAndDayAndDayOfWeekWithText(
                    phonePhotoItem.getPhotoTakenYear(), phonePhotoItem.getPhotoTakenMonth(), phonePhotoItem.getPhotoTakenDay(), phonePhotoItem.getPhotoTakenDayOfWeek());
        } else {
            szSectionTitle = StringUtil.getMonthAndDayAndDayOfWeekWithText(
                    phonePhotoItem.getPhotoTakenMonth(), phonePhotoItem.getPhotoTakenDay(), phonePhotoItem.getPhotoTakenDayOfWeek());
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
        return new ImageSelectAdapterHolders.PhotoFragmentItemHolder(view);
    }

    @Override
    protected void setHolderLayoutParams(final ImageSelectAdapterHolders.PhotoFragmentItemHolder holder, GalleryCursorRecord.PhonePhotoFragmentItem item) {
        if (holder == null || item == null) return;

        SquareRelativeLayout parentView = holder.getParentView();
        if (parentView != null) {
            BSize bSize = item.getConvertedStaggeredSize();
            if (bSize != null) {
                GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) parentView.getLayoutParams();
                lp.height = (int) bSize.getHeight();
                parentView.setLayoutParams(lp);
            }
        }
    }
}
