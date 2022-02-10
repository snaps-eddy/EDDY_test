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
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsProductEditInfo;
import com.snaps.mobile.activity.edit.spc.SnapsCanvasFactory;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbnailUtils;

/**
 * Created by ysjeong on 16. 5. 11..
 */
public class SloganActivityThumbnailAdapter extends CardShapeActivityThumbnailAdapter {
    private LinearLayout firstLayout = null;
    private TextView firstCountLayout = null;
    public SloganActivityThumbnailAdapter(Activity activity, SnapsProductEditInfo productEditInfo) {
        super(activity, productEditInfo);
    }

    @Override
    public CardShapeActivityThumbnailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = R.layout.slogan_bottomview_item_renewal_dragable ;
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new SloganActivityThumbnailHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final int THUMBNAIL_CELL_POSITION = position * 2; //앞 뒤로 구성되어 있기 때문에 2번째 셀은 3번째 페이지를 바라 보아야 한다

        if (m_arrPageList == null || m_arrPageList.size() <= THUMBNAIL_CELL_POSITION) return;

        final SloganActivityThumbnailHolder holder = (SloganActivityThumbnailHolder) viewHolder;

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
                    productEditorAPI.onThumbnailViewLongClick(holder.getRootLayout(), THUMBNAIL_CELL_POSITION +1);
                return false;
            }
        });

        int currentPageIndex = getCurrentPageIndex();

        holder.getCounterView().setTextColor((THUMBNAIL_CELL_POSITION == currentPageIndex
                || THUMBNAIL_CELL_POSITION+1 == currentPageIndex) ? Color.argb(255, 229, 71, 54) : Color.argb(186, 186, 186, 186));
        if(position == 0) {
            firstLayout = holder.getRootLayout();
            firstCountLayout = holder.getCounterView();
        }
    }

    @Override
    public Rect getThumbnailSizeRect(int position) {
        if (m_arrPageList == null || m_arrPageList.size() <= position) return null;

//        if (!isDiffPageSizeProduct && mThumbnailRect != null && (prevOrientationMode == isLandscapeMode))
        if (mThumbnailRect != null && (prevOrientationMode == isLandscapeMode))
            return mThumbnailRect;

        prevOrientationMode = isLandscapeMode;
        mThumbnailRect = EditActivityThumbnailUtils.getRectSloganThumbnailViewSizeOffsetPage(activity, isLandscapeMode, m_arrPageList.get(position));
        return mThumbnailRect;
    }

    @Override
    protected void setThumbnailDimensions(CardShapeActivityThumbnailHolder holder, Rect thumbnailRect) {
        if (thumbnailRect != null) {
            ViewGroup.LayoutParams rootParams = holder.getRootLayout().getLayoutParams();
//            int marginExpandedThumbnailAreaWidth = thumbnailRect.width() + UIUtil.convertDPtoPX(activity, 44);
//            if (marginExpandedThumbnailAreaWidth != rootParams.width) {
//                rootParams.width = marginExpandedThumbnailAreaWidth;
//                holder.getRootLayout().setLayoutParams(rootParams);
//            }

            int halfRectWidth = thumbnailRect.width() ;
            int thumbnailHeight = (int) (thumbnailRect.height() / 2);

            for (CardShapeThumbnailChildView.eCARD_SHAPE_THUMBNAIL_PAGE_TYPE pageType : CardShapeThumbnailChildView.eCARD_SHAPE_THUMBNAIL_PAGE_TYPE.values()) {
                CardShapeThumbnailChildView thumbnailChildView = getThumbnailView(holder, pageType);
                if (thumbnailChildView == null) break;

                ViewGroup.LayoutParams leftRootParams = thumbnailChildView.getRootLayout().getLayoutParams();
                if ( halfRectWidth != leftRootParams.width) {
                    leftRootParams.width = halfRectWidth;
                    thumbnailChildView.getRootLayout().setLayoutParams(leftRootParams);
                }

                ViewGroup.LayoutParams leftImgParams = thumbnailChildView.getImgLayout().getLayoutParams();
                if (leftImgParams.width != halfRectWidth || leftImgParams.height != thumbnailHeight) {
                    leftImgParams.width = halfRectWidth;
                    leftImgParams.height = thumbnailHeight;
                    thumbnailChildView.getImgLayout().setLayoutParams(leftImgParams);
                }
            }
        }
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
                    int thumbnailOffsetPageWidth = thumbnailView.getThumbnailRect().width() ;
                    int thumbnailOffsetPageHeight = (int) (thumbnailView.getThumbnailRect().height() / 2); //FIXME 시안 나오면 거기에 맞춰서 작업하자.

                    float ratioThumbnailOffsetWH = thumbnailOffsetPageWidth / (float) thumbnailOffsetPageHeight;

                    //조금 작게 설정해야 썸네일이 안 잘리고 잘 그려진다..특히 달력은 아래 shadow2가 덮고 있어서 더 작게 설정해야 한다.
//                    thumbnailOffsetPageHeight -= 10;
//                    thumbnailOffsetPageWidth -= (10 * ratioThumbnailOffsetWH);

                    float ratioX = thumbnailOffsetPageWidth / (float)copied.getWidth();
                    float ratioY = thumbnailOffsetPageHeight / (float)copied.getHeight();

                    copied.setScaledDimensions(ratioX, ratioY);
                    canvas.setThumbnailView(ratioX, ratioY);
                }

                canvas.setSnapsPage(copied, position, true, null);
                thumbnailView.setCanvas(canvas);
            }

//        EditActivityThumbnailUtils.setDragViewsText(getItemCount(),
//                activity.mNowPage,
//                position,
//                thumbnailView.getIntroindex(),
//                thumbnailView.getLeftIndex(),
//                thumbnailView.getRightIndex());

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

    protected void bottomViewItemClick(int position, CardShapeActivityThumbnailHolder holder, boolean isClick) {
        if (holder == null || holder.getRightThumbnailView().getOutline() == null || holder.getRightThumbnailView().getLeftIndex() == null || holder.getRightThumbnailView().getRightIndex() == null || holder.getRightThumbnailView().getIntroindex() == null
                || holder.getLeftThumbnailView().getOutline() == null || holder.getLeftThumbnailView().getLeftIndex() == null || holder.getLeftThumbnailView().getRightIndex() == null || holder.getLeftThumbnailView().getIntroindex() == null)
            return;

        CardShapeThumbnailChildView leftThumbnailView = getThumbnailView(holder, CardShapeThumbnailChildView.eCARD_SHAPE_THUMBNAIL_PAGE_TYPE.CARD_SHAPE_THUMBNAIL_TYPE_PAGE_LEFT);
        CardShapeThumbnailChildView rightThumbnailView = getThumbnailView(holder, CardShapeThumbnailChildView.eCARD_SHAPE_THUMBNAIL_PAGE_TYPE.CARD_SHAPE_THUMBNAIL_TYPE_PAGE_RIGHT);

        if (position % 2 == 0) {
            int drawable = isClick ? R.drawable.shape_image_border_select : R.drawable.shape_image_border;
            holder.getLeftThumbnailView().getOutline().setBackgroundResource(drawable);

            if (isClick) {
                holder.getCounterView().setTextColor(Color.argb(255, 229, 71, 54));

                rightThumbnailView.getOutline().setBackgroundResource(R.drawable.shape_image_border);
            } else {
                holder.getCounterView().setTextColor(Color.argb(186, 186, 186, 186));
            }
        } else {
            int drawable = isClick ? R.drawable.shape_image_border_select : R.drawable.shape_image_border;
            rightThumbnailView.getOutline().setBackgroundResource(drawable);

            if (isClick) {
                holder.getCounterView().setTextColor(Color.argb(255, 229, 71, 54));

                leftThumbnailView.getOutline().setBackgroundResource(R.drawable.shape_image_border);
            } else {
                holder.getCounterView().setTextColor(Color.argb(186, 186, 186, 186));
            }
        }
    }

    public LinearLayout getFirstLayout() {
        return firstLayout;
    }
    public TextView getFirstCountLayout() {
        return firstCountLayout;
    }
}