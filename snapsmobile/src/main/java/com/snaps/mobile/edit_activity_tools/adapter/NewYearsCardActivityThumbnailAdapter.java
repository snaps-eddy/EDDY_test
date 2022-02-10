package com.snaps.mobile.edit_activity_tools.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsProductEditInfo;
import com.snaps.mobile.activity.edit.spc.SnapsCanvasFactory;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbnailUtils;

/**
 * Created by kimduckwon on 2017. 11. 6..
 */

public class NewYearsCardActivityThumbnailAdapter extends CardShapeActivityThumbnailAdapter{
    private static final String TAG = NewYearsCardActivityThumbnailAdapter.class.getSimpleName();
    private LinearLayout firstLayout = null;
    private TextView firstCountLayout = null;

    public NewYearsCardActivityThumbnailAdapter(Activity activity, SnapsProductEditInfo productEditInfo) {
        super(activity, productEditInfo);
    }

    @Override
    public CardShapeActivityThumbnailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = isLandscapeMode ? R.layout.new_years_card_renewal_horizotal_dragable : R.layout.new_years_card_bottomview_item_renewal_dragable;
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new NewYearsCardActivityThumbnailHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final int THUMBNAIL_CELL_POSITION = position ; //앞 뒤로 구성되어 있기 때문에 2번째 셀은 3번째 페이지를 바라 보아야 한다

        if (m_arrPageList == null) return;

        final NewYearsCardActivityThumbnailHolder holder = (NewYearsCardActivityThumbnailHolder) viewHolder;

        putThumbnailHolder(THUMBNAIL_CELL_POSITION, holder);

        Rect thumbnailRect = getThumbnailSizeRect(THUMBNAIL_CELL_POSITION);

        setThumbnailDimensions(holder, thumbnailRect);

        setThumbnailDivideLineState(holder, position);

        SnapsPage leftPage = m_arrPageList.get(THUMBNAIL_CELL_POSITION);
        CardShapeThumbnailChildView leftThumbnailView = getThumbnailView(holder, CardShapeThumbnailChildView.eCARD_SHAPE_THUMBNAIL_PAGE_TYPE.CARD_SHAPE_THUMBNAIL_TYPE_PAGE_LEFT);
        leftThumbnailView.setThumbnailRect(thumbnailRect);
        leftThumbnailView.setPage(leftPage);
        setThumbnailHolder(leftThumbnailView, THUMBNAIL_CELL_POSITION);

        holder.getCounterView().setText(String.valueOf(leftPage.getQuantity()));
        if(Const_PRODUCT.POSTER_A2_VERTICAL.equals(Config.getPROD_CODE())|| Const_PRODUCT.POSTER_A2_HORIZONTAL.equals(Config.getPROD_CODE())) {
            holder.getCounterView().setBackgroundColor(Color.parseColor("#ffffff"));
        } else {
            holder.getCounterView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (productEditorAPI != null)
                        productEditorAPI.onThumbnailCountViewClick(holder.getCounterView(), THUMBNAIL_CELL_POSITION);
                }
            });
        }

        leftThumbnailView.getRootLayout().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (productEditorAPI != null)
                    productEditorAPI.onThumbnailViewLongClick(holder.getRootLayout(), THUMBNAIL_CELL_POSITION);
                return false;
            }
        });

        int currentPageIndex = getCurrentPageIndex();

        holder.getCounterView().setTextColor((THUMBNAIL_CELL_POSITION == currentPageIndex) ? Color.argb(255, 227, 106, 99) : Color.argb(255, 153, 153, 153));
        if(position == 0) {
            firstLayout = holder.getRootLayout();
            firstCountLayout = holder.getCounterView();
        }
    }

    protected void bottomViewItemClick(int position, CardShapeActivityThumbnailHolder holder, boolean isClick) {
        if (holder == null || holder.getLeftThumbnailView().getOutline() == null || holder.getLeftThumbnailView().getLeftIndex() == null || holder.getLeftThumbnailView().getRightIndex() == null || holder.getLeftThumbnailView().getIntroindex() == null)
            return;

            int drawable = isClick ?R.drawable.shape_image_border_select : R.drawable.shape_image_border;
            holder.getLeftThumbnailView().getOutline().setBackgroundResource(drawable);
            if (isClick) {
                holder.getCounterView().setTextColor(Color.argb(255, 227, 106, 99));
            } else {
                holder.getCounterView().setTextColor(Color.argb(255, 153, 153, 153));
            }
    }

    @Override
    public void refreshThumbnailsLineAndText(int position) {
        if (m_mapThumbnailHolders == null) return;

        try {

            for (int key : m_mapThumbnailHolders.keySet()) {
                if ((key == position))
                    continue;
                CardShapeActivityThumbnailHolder holder = m_mapThumbnailHolders.get(key);
                bottomViewItemClick(position, holder, false);
            }

            bottomViewItemClick(position, m_mapThumbnailHolders.get(position), true);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public long getItemId(int position) {
        int pos = position;
        return m_arrPageList != null ? m_arrPageList.get(pos).getPageID() : 0; // need to return stable (= not change even after reordered) value
    }

    @Override
    public int getItemCount() {
        return m_arrPageList != null ? m_arrPageList.size() : 0;
    }

    @Override
    protected void setThumbnailDimensions(CardShapeActivityThumbnailHolder holder, Rect thumbnailRect) {
        if (thumbnailRect != null) {

            int rectWidth = (int)(thumbnailRect.width() );
            int thumbnailHeight = (int) (thumbnailRect.height());

            for (CardShapeThumbnailChildView.eCARD_SHAPE_THUMBNAIL_PAGE_TYPE pageType : CardShapeThumbnailChildView.eCARD_SHAPE_THUMBNAIL_PAGE_TYPE.values()) {
                CardShapeThumbnailChildView thumbnailChildView = getThumbnailView(holder, pageType);
                if (thumbnailChildView == null) break;

                ViewGroup.LayoutParams leftRootParams = thumbnailChildView.getRootLayout().getLayoutParams();
                if ( rectWidth != leftRootParams.width) {
                    leftRootParams.width = rectWidth;
                    thumbnailChildView.getRootLayout().setLayoutParams(leftRootParams);
                }

                ViewGroup.LayoutParams leftImgParams = thumbnailChildView.getImgLayout().getLayoutParams();
                if (leftImgParams.width != rectWidth || leftImgParams.height != thumbnailHeight) {
                    leftImgParams.width = rectWidth;
                    leftImgParams.height = thumbnailHeight;
                    thumbnailChildView.getImgLayout().setLayoutParams(leftImgParams);
                }
            }
        }
    }

    @Override
    public Rect getThumbnailSizeRect(int position) {
        if (m_arrPageList == null || m_arrPageList.size() <= position) return null;

        prevOrientationMode = isLandscapeMode;
        mThumbnailRect = EditActivityThumbnailUtils.getRectNewYearsCardThumbanilViewSizeOffsetPage(activity, false, m_arrPageList.get(position));
        return mThumbnailRect;
    }

    @Override
    protected void setThumbnailHolder(final CardShapeThumbnailChildView thumbnailView, final int position) {
        thumbnailView.getCanvasParentLy().removeAllViews();
        SnapsPageCanvas canvas = new SnapsCanvasFactory().createPageCanvas(activity, Config.getPROD_CODE());
        if (canvas != null) {
            canvas.setThumbnailProgress(thumbnailView.getProgressBar());
            canvas.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            canvas.setGravity(Gravity.CENTER);
            canvas.setEnableButton(false);
            thumbnailView.getCanvasParentLy().addView(canvas);

            SnapsPage copied = thumbnailView.getPage().copyPage(99990 + thumbnailView.getPage().getPageID(), true);

            if (thumbnailView.getThumbnailRect() != null) {
                int thumbnailOffsetPageWidth = thumbnailView.getThumbnailRect().width();
                int thumbnailOffsetPageHeight = (int) (thumbnailView.getThumbnailRect().height()); //FIXME 시안 나오면 거기에 맞춰서 작업하자.

                float ratioThumbnailOffsetWH = thumbnailOffsetPageWidth / (float) thumbnailOffsetPageHeight;

//                조금 작게 설정해야 썸네일이 안 잘리고 잘 그려진다..특히 달력은 아래 shadow2가 덮고 있어서 더 작게 설정해야 한다.
                if(!Const_PRODUCT.isNameStickerProduct()) {
                    thumbnailOffsetPageHeight -= 10;
                    thumbnailOffsetPageWidth -= (10 * ratioThumbnailOffsetWH);
                }

                float ratioX = thumbnailOffsetPageWidth / (float)copied.getWidth();
                float ratioY = thumbnailOffsetPageHeight / (float)copied.getHeight();

                copied.setScaledDimensions(ratioX, ratioY);
                canvas.setThumbnailView(ratioX, ratioY);
            }
            canvas.setSnapsPage(copied, position, true, null);
            thumbnailView.setCanvas(canvas);
        }

        if (getCurrentPageIndex() == position) {
            thumbnailView.getOutline().setBackgroundResource(R.drawable.shape_image_border_select);
        } else {
            thumbnailView.getOutline().setBackgroundResource(R.drawable.shape_image_border);
        }

        // 느낌표 추가
        if (thumbnailView.getPage().isExistUploadFailedImage()) {
            thumbnailView.getWarnining().setImageResource(R.drawable.alert_for_upload_failed_org_img);
            thumbnailView.getWarnining().setVisibility(View.VISIBLE);
        } else if (thumbnailView.getPage().isExistResolutionImage()) {
            thumbnailView.getWarnining().setImageResource(R.drawable.alert_01);
            thumbnailView.getWarnining().setVisibility(View.VISIBLE);
        } else
            thumbnailView.getWarnining().setVisibility(View.INVISIBLE);

        thumbnailView.getRootLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productEditorAPI != null)
                    productEditorAPI.onThumbnailViewClick(thumbnailView.getRootLayout(), position);
            }
        });
    }

    public LinearLayout getFirstLayout() {
        return firstLayout;
    }
    public TextView getFirstCountLayout() {
        return firstCountLayout;
    }
}
