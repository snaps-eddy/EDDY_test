package com.snaps.mobile.edit_activity_tools.adapter;


import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsProductEditInfo;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditorAPI;
import com.snaps.mobile.activity.edit.spc.SnapsCanvasFactory;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbnailUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ysjeong on 16. 5. 11..
 */
public abstract class CardShapeActivityThumbnailAdapter extends BaseEditActivityThumbnailAdapter {
    private static final String TAG = CardShapeActivityThumbnailAdapter.class.getSimpleName();

    protected Map<Integer, CardShapeActivityThumbnailHolder> m_mapThumbnailHolders = null;
    protected SnapsProductEditorAPI productEditorAPI;
    protected ArrayList<SnapsPage> m_arrPageList = null;
    protected Activity activity = null;
    protected boolean isLandscapeMode = false;
    protected boolean prevOrientationMode = isLandscapeMode;
    protected Rect mThumbnailRect = null;
    protected SnapsProductEditInfo productEditInfo = null;

    @Override
    public void setSnapsProductEditorAPI(SnapsProductEditorAPI productEditorAPI) {
        this.productEditorAPI = productEditorAPI;
    }

    public void releaseInstance() {
        try {
            if (m_mapThumbnailHolders != null) {
                for (int key : m_mapThumbnailHolders.keySet()) {
                    CardShapeActivityThumbnailHolder holder = m_mapThumbnailHolders.get(key);
                    if (holder == null || holder.getLeftThumbnailView() == null || holder.getRightThumbnailView() == null) continue;

                    destroyThumbnailViewWithHolder(holder);
                }

                m_mapThumbnailHolders.clear();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void destroyThumbnailViewWithHolder(CardShapeActivityThumbnailHolder holder) {
        if (holder == null) return;

        for (CardShapeThumbnailChildView.eCARD_SHAPE_THUMBNAIL_PAGE_TYPE pageType : CardShapeThumbnailChildView.eCARD_SHAPE_THUMBNAIL_PAGE_TYPE.values()) {
            CardShapeThumbnailChildView thumbnailChildView = getThumbnailView(holder, pageType);
            if (thumbnailChildView == null) break;
            SnapsPageCanvas canvas = thumbnailChildView.getCanvas();
            if (canvas != null) canvas.onDestroyCanvas();
            ViewUnbindHelper.unbindReferences(thumbnailChildView.getRootLayout(), null, false);
        }
    }

    protected CardShapeThumbnailChildView getThumbnailView(CardShapeActivityThumbnailHolder holder, CardShapeThumbnailChildView.eCARD_SHAPE_THUMBNAIL_PAGE_TYPE leftOfRight) {
        if (holder == null) return null;
        switch (leftOfRight) {
            case CARD_SHAPE_THUMBNAIL_TYPE_PAGE_LEFT:
                return holder.getLeftThumbnailView();
            case CARD_SHAPE_THUMBNAIL_TYPE_PAGE_RIGHT:
                return holder.getRightThumbnailView();
        }
        return null;
    }

    public CardShapeActivityThumbnailAdapter(Activity activity, SnapsProductEditInfo productEditInfo) {
        setHasStableIds(true); // this is required for D&D feature.
        this.activity = activity;
        this.m_mapThumbnailHolders = new LinkedHashMap<>();
        this.productEditInfo = productEditInfo;
    }

    @Override
    public long getItemId(int position) {
        int pos = position*2;
        return m_arrPageList != null ? m_arrPageList.get(pos).getPageID() : 0; // need to return stable (= not change even after reordered) value
    }

    public abstract CardShapeActivityThumbnailHolder onCreateViewHolder(ViewGroup parent, int viewType);

    public View getThumbnailRootView(int position) throws Exception {
        CardShapeActivityThumbnailHolder holder = m_mapThumbnailHolders.get(position);
        return holder.getRootLayout();
    }

    public View getThumbnailCountView(int position) throws Exception {
        CardShapeActivityThumbnailHolder holder = m_mapThumbnailHolders.get(position);
        return holder.getCounterView();
    }

    public Rect getThumbnailSizeRect(int position) {
        if (m_arrPageList == null || m_arrPageList.size() <= position) return null;

//        if (!isDiffPageSizeProduct && mThumbnailRect != null && (prevOrientationMode == isLandscapeMode))
        if (mThumbnailRect != null && (prevOrientationMode == isLandscapeMode))
            return mThumbnailRect;

        prevOrientationMode = isLandscapeMode;
        mThumbnailRect = EditActivityThumbnailUtils.getRectCardShapeThumbnailViewSizeOffsetPage(activity, isLandscapeMode, m_arrPageList.get(position));
        return mThumbnailRect;
    }

    protected void setThumbnailDimensions(CardShapeActivityThumbnailHolder holder, Rect thumbnailRect) {
        if (thumbnailRect != null) {
            ViewGroup.LayoutParams rootParams = holder.getRootLayout().getLayoutParams();
            int marginExpandedThumbnailAreaWidth = thumbnailRect.width() + UIUtil.convertDPtoPX(activity, 44);
            if (marginExpandedThumbnailAreaWidth != rootParams.width) {
                rootParams.width = marginExpandedThumbnailAreaWidth;
                holder.getRootLayout().setLayoutParams(rootParams);
            }

            int halfRectWidth = thumbnailRect.width() / 2;
            int thumbnailHeight = (int) (thumbnailRect.height());

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

    protected void putThumbnailHolder(int position, CardShapeActivityThumbnailHolder holder) {
        if (m_mapThumbnailHolders != null) {
            m_mapThumbnailHolders.put(position, holder);
        }
    }

    protected void setThumbnailDivideLineState(CardShapeActivityThumbnailHolder holder, int position) {
        if (holder == null || holder.getThumbnailDivideLine() == null) return;
        holder.getThumbnailDivideLine().setVisibility(position == 0 ? View.GONE : View.VISIBLE);
    }

    protected int getCurrentPageIndex() {
        return productEditInfo != null ? productEditInfo.getCurrentPageIndex() : 0;
    }

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
                int thumbnailOffsetPageWidth = thumbnailView.getThumbnailRect().width() / 2;
                int thumbnailOffsetPageHeight = (int) (thumbnailView.getThumbnailRect().height()); //FIXME 시안 나오면 거기에 맞춰서 작업하자.

                float ratioThumbnailOffsetWH = thumbnailOffsetPageWidth / (float) thumbnailOffsetPageHeight;

                //조금 작게 설정해야 썸네일이 안 잘리고 잘 그려진다..특히 달력은 아래 shadow2가 덮고 있어서 더 작게 설정해야 한다.
                thumbnailOffsetPageHeight -= 10;
                thumbnailOffsetPageWidth -= (10 * ratioThumbnailOffsetWH);

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
            thumbnailView.getOutline().setBackgroundResource(R.drawable.shape_image_border_photo_card_select);
        } else {
            thumbnailView.getOutline().setBackgroundResource(R.drawable.shape_image_photo_card_border);
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

    @Override
    public int getItemCount() {
        return m_arrPageList != null ? m_arrPageList.size() / 2 : 0;
    }

    public void refreshThumbnailsLineAndText(int position) {
        if (m_mapThumbnailHolders == null) return;

        try {
            boolean isRightPage = position % 2 == 1;
            for (int key : m_mapThumbnailHolders.keySet()) {
                if ((key == position) || (isRightPage && position -1 == key))
                    continue;
                CardShapeActivityThumbnailHolder holder = m_mapThumbnailHolders.get(key);
                bottomViewItemClick(position, holder, false);
                bottomViewItemClick(position+1, holder, false);
            }

            bottomViewItemClick(position, m_mapThumbnailHolders.get((isRightPage ? position-1 : position)), true);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private CardShapeActivityThumbnailHolder getThumbnailHolder(int position) {
        if (m_mapThumbnailHolders == null || !m_mapThumbnailHolders.containsKey(position)) return null;
        return m_mapThumbnailHolders.get(position);
    }

    public void setIsLandscapeMode(boolean isLandscape) {
        isLandscapeMode = isLandscape;
    }

    public void setProductEditorAPI(SnapsProductEditorAPI productEditorAPI) {
        this.productEditorAPI = productEditorAPI;
    }

    public void setData(ArrayList<SnapsPage> pageList) {
        this.m_arrPageList = pageList;
//        isDiffPageSizeProduct = isDiffThumbnailSize();

        try {
            notifyDataSetChangedWithHandler();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void notifyDataSetChangedWithHandler() throws Exception {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    notifyDataSetChanged();
                } catch (Exception e) { Dlog.e(TAG, e); }
            }
        });
    }

    protected abstract void bottomViewItemClick(int position, CardShapeActivityThumbnailHolder holder, boolean isClick);

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        try {
            notifyItemRemovedWithHandler(position);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void notifyItemRemovedWithHandler(final int position) throws Exception {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    notifyItemRemoved(position);
                } catch (Exception e) { Dlog.e(TAG, e); }
            }
        });
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
        CardShapeActivityThumbnailHolder holder = (CardShapeActivityThumbnailHolder) viewHolder;
        super.onViewRecycled(holder);

        if (holder == null) return;

        for (CardShapeThumbnailChildView.eCARD_SHAPE_THUMBNAIL_PAGE_TYPE pageType : CardShapeThumbnailChildView.eCARD_SHAPE_THUMBNAIL_PAGE_TYPE.values()) {
            CardShapeThumbnailChildView thumbnailChildView = getThumbnailView(holder, pageType);
            if (thumbnailChildView == null) break;
            if (thumbnailChildView.getCanvas() != null && !thumbnailChildView.getCanvas().isShown()) {
                thumbnailChildView.getCanvas().releaseReferences();

                BitmapUtil.drawableRecycle(thumbnailChildView.getOutline());
                BitmapUtil.drawableRecycle(thumbnailChildView.getWarnining());
            }
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
    }


}