package com.snaps.mobile.edit_activity_tools.adapter;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaps.common.structure.page.SnapsPage;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsProductEditInfo;

/**
 * Created by ysjeong on 16. 5. 11..
 */
public class PhotoCardActivityThumbnailAdapter extends CardShapeActivityThumbnailAdapter {

    public PhotoCardActivityThumbnailAdapter(Activity activity, SnapsProductEditInfo productEditInfo) {
        super(activity, productEditInfo);
    }

    @Override
    public CardShapeActivityThumbnailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = isLandscapeMode ? R.layout.photo_card_bottomview_item_renewal_horizontal_dragable : R.layout.photo_card_bottomview_item_renewal_dragable;
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new PhotoCardActivityThumbnailHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final int THUMBNAIL_CELL_POSITION = position * 2; //앞 뒤로 구성되어 있기 때문에 2번째 셀은 3번째 페이지를 바라 보아야 한다

        if (m_arrPageList == null || m_arrPageList.size() <= THUMBNAIL_CELL_POSITION) return;

        final PhotoCardActivityThumbnailHolder holder = (PhotoCardActivityThumbnailHolder) viewHolder;

        putThumbnailHolder(THUMBNAIL_CELL_POSITION, holder);

        Rect thumbnailRect = getThumbnailSizeRect(THUMBNAIL_CELL_POSITION);

        setThumbnailDimensions(holder, thumbnailRect);

        setThumbnailDivideLineState(holder, position);

        SnapsPage leftPage = m_arrPageList.get(THUMBNAIL_CELL_POSITION);
        CardShapeThumbnailChildView leftThumbnailView = getThumbnailView(holder, CardShapeThumbnailChildView.eCARD_SHAPE_THUMBNAIL_PAGE_TYPE.CARD_SHAPE_THUMBNAIL_TYPE_PAGE_LEFT);
        leftThumbnailView.setThumbnailRect(thumbnailRect);
        leftThumbnailView.setPage(leftPage);
        setThumbnailHolder(leftThumbnailView, THUMBNAIL_CELL_POSITION);

        SnapsPage rightPage = m_arrPageList.get(THUMBNAIL_CELL_POSITION + 1);
        CardShapeThumbnailChildView rightThumbnailView = getThumbnailView(holder, CardShapeThumbnailChildView.eCARD_SHAPE_THUMBNAIL_PAGE_TYPE.CARD_SHAPE_THUMBNAIL_TYPE_PAGE_RIGHT);
        rightThumbnailView.setThumbnailRect(thumbnailRect);
        rightThumbnailView.setPage(rightPage);
        setThumbnailHolder(rightThumbnailView, THUMBNAIL_CELL_POSITION+1);

        holder.getCounterView().setText(String.valueOf(leftPage.getQuantity()));

        holder.getCounterView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productEditorAPI != null)
                    productEditorAPI.onThumbnailCountViewClick(holder.getCounterView(), THUMBNAIL_CELL_POSITION);
            }
        });

        leftThumbnailView.getRootLayout().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (productEditorAPI != null)
                    productEditorAPI.onThumbnailViewLongClick(holder.getRootLayout(), THUMBNAIL_CELL_POSITION);
                return false;
            }
        });
        rightThumbnailView.getRootLayout().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (productEditorAPI != null)
                    productEditorAPI.onThumbnailViewLongClick(holder.getRootLayout(), THUMBNAIL_CELL_POSITION);
                return false;
            }
        });

        int currentPageIndex = getCurrentPageIndex();

        holder.getCounterView().setTextColor((THUMBNAIL_CELL_POSITION == currentPageIndex
                || THUMBNAIL_CELL_POSITION+1 == currentPageIndex) ? Color.argb(255, 229, 71, 54) : Color.argb(186, 186, 186, 186));
    }

    protected void bottomViewItemClick(int position, CardShapeActivityThumbnailHolder holder, boolean isClick) {
        if (holder == null || holder.getRightThumbnailView().getOutline() == null || holder.getRightThumbnailView().getLeftIndex() == null || holder.getRightThumbnailView().getRightIndex() == null || holder.getRightThumbnailView().getIntroindex() == null
                || holder.getLeftThumbnailView().getOutline() == null || holder.getLeftThumbnailView().getLeftIndex() == null || holder.getLeftThumbnailView().getRightIndex() == null || holder.getLeftThumbnailView().getIntroindex() == null)
            return;

        CardShapeThumbnailChildView leftThumbnailView = getThumbnailView(holder, CardShapeThumbnailChildView.eCARD_SHAPE_THUMBNAIL_PAGE_TYPE.CARD_SHAPE_THUMBNAIL_TYPE_PAGE_LEFT);
        CardShapeThumbnailChildView rightThumbnailView = getThumbnailView(holder, CardShapeThumbnailChildView.eCARD_SHAPE_THUMBNAIL_PAGE_TYPE.CARD_SHAPE_THUMBNAIL_TYPE_PAGE_RIGHT);

        if (position % 2 == 0) {
            int drawable = isClick ? R.drawable.shape_image_border_photo_card_select : R.drawable.shape_image_photo_card_border;
            holder.getLeftThumbnailView().getOutline().setBackgroundResource(drawable);

            if (isClick) {
                holder.getCounterView().setTextColor(Color.argb(255, 229, 71, 54));

                rightThumbnailView.getOutline().setBackgroundResource(R.drawable.shape_image_photo_card_border);
            } else {
                holder.getCounterView().setTextColor(Color.argb(186, 186, 186, 186));
            }
        } else {
            int drawable = isClick ? R.drawable.shape_image_border_photo_card_select : R.drawable.shape_image_photo_card_border;
            rightThumbnailView.getOutline().setBackgroundResource(drawable);

            if (isClick) {
                holder.getCounterView().setTextColor(Color.argb(255, 229, 71, 54));

                leftThumbnailView.getOutline().setBackgroundResource(R.drawable.shape_image_photo_card_border);
            } else {
                holder.getCounterView().setTextColor(Color.argb(186, 186, 186, 186));
            }
        }
    }
}