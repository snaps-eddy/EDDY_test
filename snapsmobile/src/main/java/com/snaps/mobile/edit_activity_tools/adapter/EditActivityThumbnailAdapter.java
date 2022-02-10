package com.snaps.mobile.edit_activity_tools.adapter;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;
import com.snaps.common.utils.ui.BitmapUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsProductEditInfo;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditorAPI;
import com.snaps.mobile.activity.edit.spc.SnapsCanvasFactory;
import com.snaps.mobile.edit_activity_tools.utils.EditActivityThumbnailUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by ysjeong on 16. 5. 11..
 */
public class EditActivityThumbnailAdapter extends BaseEditActivityThumbnailAdapter {
    private static final String TAG = EditActivityThumbnailAdapter.class.getSimpleName();

    private Map<Integer, EditActivityThumbnailHolder> m_mapThumbnailHolders = null;
    private SnapsProductEditorAPI snapsProductEditorAPI;
    private ArrayList<SnapsPage> m_arrPageList = null;
    private Activity activity = null;
    private boolean isLandscapeMode = false;
    private boolean prevOrientationMode = isLandscapeMode;
    private Rect mThumbnailRect = null;
    private boolean isDiffPageSizeProduct = false; //페이지의 크기가 일정하지 않은 제품
    private SnapsProductEditInfo productEditInfo = null;

    private int m_iLongClickedItemId = -1;
    private int m_iMovedPosition = -1;
    private RelativeLayout selectLayout = null;

    public void releaseInstance() {
        try {
            if (m_mapThumbnailHolders != null) {

                for (int key : m_mapThumbnailHolders.keySet()) {
                    EditActivityThumbnailHolder holder = m_mapThumbnailHolders.get(key);
                    if (holder == null || holder.canvas == null) continue;
                    holder.canvas.onDestroyCanvas();
                    ViewUnbindHelper.unbindReferences(holder.rootLayout, null, false);
                }

                m_mapThumbnailHolders.clear();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public Map<Integer, EditActivityThumbnailHolder> getThumbnailHolders() {
        return m_mapThumbnailHolders;
    }

    public EditActivityThumbnailAdapter(Activity activity, SnapsProductEditInfo editInfo) {
        init(activity, editInfo);
    }

    private void init(Activity activity, SnapsProductEditInfo editInfo) {
        setHasStableIds(true); // this is required for D&D feature.
        this.activity = activity;
        this.m_mapThumbnailHolders = new LinkedHashMap<>();
        setProductEditInfo(editInfo);
    }

    @Override
    public long getItemId(int position) {
        return m_arrPageList != null ? m_arrPageList.get(position).getPageID() : 0; // need to return stable (= not change even after reordered) value
    }

    @Override
    public EditActivityThumbnailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = isLandscapeMode ? R.layout.bottomview_item_renewal_horizontal_dragable : R.layout.bottomview_item_renewal_dragable;

        if (Config.isCalendarMini(Config.getPROD_CODE()) || Config.isCalendarNormalVert(Config.getPROD_CODE()) || Const_PRODUCT.isCardProduct()) {
        } else if (Const_PRODUCT.isPackageProduct()) {
            layoutId = R.layout.bottomview_item_package_kit_renewal_dragable;
        }

        View v = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);

        return new EditActivityThumbnailHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (m_arrPageList == null || m_arrPageList.size() <= position) return;

        final EditActivityThumbnailHolder holder = (EditActivityThumbnailHolder) viewHolder;

        if (m_mapThumbnailHolders != null) {
            m_mapThumbnailHolders.put(position, holder);
        }

        Rect thumbnailRect = getThumbnailSizeRect(position);

        if (thumbnailRect != null) {
            ViewGroup.LayoutParams rootParams = holder.rootLayout.getLayoutParams();
            if (thumbnailRect.width() != rootParams.width) {
                rootParams.width = thumbnailRect.width();
                holder.rootLayout.setLayoutParams(rootParams);
            }

            ViewGroup.LayoutParams imgParams = holder.imgLayout.getLayoutParams();
            if (imgParams.width != thumbnailRect.width() || imgParams.height != thumbnailRect.height()) {
                imgParams.width = thumbnailRect.width();
                imgParams.height = thumbnailRect.height();
                holder.imgLayout.setLayoutParams(imgParams);
            }
        }

        SnapsPage page = m_arrPageList.get(position);
        if (page == null) return;

        holder.canvasParentLy.removeAllViews();
        SnapsPageCanvas canvas = new SnapsCanvasFactory().createPageCanvas(activity, Config.getPROD_CODE());
        if (canvas != null) {
            canvas.setThumbnailProgress(holder.progressBar);
            canvas.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            canvas.setGravity(Gravity.CENTER);
            canvas.setEnableButton(false);
            holder.canvasParentLy.addView(canvas);

            SnapsPage copied = page.copyPage(99990 + page.getPageID(), true);

            if (thumbnailRect != null) {
                int thumbnailOffsetPageWidth = thumbnailRect.width();
                int thumbnailOffsetPageHeight = thumbnailRect.height();

                float ratioThumbnailOffsetWH = thumbnailOffsetPageWidth / (float) thumbnailOffsetPageHeight;

                //조금 작게 설정해야 썸네일이 안 잘리고 잘 그려진다..특히 달력은 아래 shadow2가 덮고 있어서 더 작게 설정해야 한다.
                if (Config.isCalendar()) {
                    thumbnailOffsetPageHeight -= 30;
                    thumbnailOffsetPageWidth -= (30 * ratioThumbnailOffsetWH);
                } else {
                    thumbnailOffsetPageHeight -= 12;
                    thumbnailOffsetPageWidth -= (12 * ratioThumbnailOffsetWH);
                }

                float ratioX = thumbnailOffsetPageWidth / (float) copied.getWidth();
                float ratioY = thumbnailOffsetPageHeight / (float) copied.getHeight();

                copied.setScaledDimensions(ratioX, ratioY);
                canvas.setThumbnailView(ratioX, ratioY);
            }

            canvas.setSnapsPage(copied, position, true, null);
            holder.canvas = canvas;
        }

        int currentPageIndex = getProductEditInfo() != null ? getProductEditInfo().getCurrentPageIndex() : 0;

        EditActivityThumbnailUtils.setDragViewsText(activity, getItemCount(),
                currentPageIndex,
                position,
                holder.introindex,
                holder.leftIndex,
                holder.rightIndex);

        setTextSizeWithViewHolder(holder);

        if (currentPageIndex == position) {
            holder.outline.setBackgroundResource(R.drawable.shape_image_border_select);
            selectLayout = holder.rootLayout;
        } else {
            holder.outline.setBackgroundResource(R.drawable.shape_image_border);
        }

        // 느낌표 추가
        if (page.isExistUploadFailedImage()) {
            holder.warnining.setImageResource(R.drawable.alert_for_upload_failed_org_img);
            holder.warnining.setVisibility(View.VISIBLE);
        } else if (page.isExistResolutionImage()) {
            holder.warnining.setImageResource(R.drawable.alert_01);
            holder.warnining.setVisibility(View.VISIBLE);
        } else
            holder.warnining.setVisibility(View.INVISIBLE);

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snapsProductEditorAPI != null)
                    snapsProductEditorAPI.onThumbnailViewClick(holder.rootLayout, position);
            }
        });
    }

    private void setTextSizeWithViewHolder(EditActivityThumbnailHolder holder) {
        if (holder == null) return;

        if (Config.isCalendar()) {
            if (holder.introindex != null) {
                String text = holder.introindex.getText().toString();
                if (text.length() >= 6)
                    holder.introindex.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                else
                    holder.introindex.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
            }
        }
    }

    public Rect getThumbnailSizeRect(int position) {
        if (m_arrPageList == null || m_arrPageList.size() <= position) return null;

        if (!isDiffPageSizeProduct && mThumbnailRect != null && (prevOrientationMode == isLandscapeMode))
            return mThumbnailRect;

        prevOrientationMode = isLandscapeMode;
        mThumbnailRect = EditActivityThumbnailUtils.getRectThumbanilViewSizeOffsetPage(activity, isLandscapeMode, m_arrPageList.get(position));
        return mThumbnailRect;
    }

    public boolean isDiffThumbnailSize() {
        if (m_arrPageList == null || m_arrPageList.size() < 2) return false;

        int prevW = 0;
        int prevH = 0;

        try {
            prevW = Integer.parseInt(m_arrPageList.get(0).width);
            prevH = Integer.parseInt(m_arrPageList.get(0).height);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        for (int ii = 1; ii < m_arrPageList.size(); ii++) {
            SnapsPage page = m_arrPageList.get(ii);
            try {
                int w = Integer.parseInt(m_arrPageList.get(ii).width);
                int h = Integer.parseInt(m_arrPageList.get(ii).height);

                if (w != prevW || h != prevH) {
                    return true;
                }
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }

        return false;
    }

    public View getSelectLayout() {
        return selectLayout;
    }

    @Override
    public int getItemCount() {
        return m_arrPageList != null ? m_arrPageList.size() : 0;
    }

    public void refreshThumbnailsLineAndText(int position) {
        if (m_mapThumbnailHolders == null) return;

        try {

            for (int key : m_mapThumbnailHolders.keySet()) {
                if (key == position)
                    continue;
                EditActivityThumbnailHolder holder = m_mapThumbnailHolders.get(key);
                bottomViewItemClick(holder, false);
            }

            bottomViewItemClick(m_mapThumbnailHolders.get(position), true);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private EditActivityThumbnailHolder getThumbnailHolder(int position) {
        if (m_mapThumbnailHolders == null || !m_mapThumbnailHolders.containsKey(position))
            return null;
        return m_mapThumbnailHolders.get(position);
    }

    public void setIsLandscapeMode(boolean isLandscape) {
        isLandscapeMode = isLandscape;
    }

    @Override
    public void setSnapsProductEditorAPI(SnapsProductEditorAPI snapsProductEditorAPI) {
        this.snapsProductEditorAPI = snapsProductEditorAPI;
    }

    public void setData(ArrayList<SnapsPage> pageList) {
        this.m_arrPageList = pageList;
        isDiffPageSizeProduct = isDiffThumbnailSize();
        notifyDataSetChanged();
    }

    private void bottomViewItemClick(EditActivityThumbnailHolder holder, boolean isClick) {
        if (holder == null || holder.outline == null || holder.leftIndex == null || holder.rightIndex == null || holder.introindex == null)
            return;

        int drawable = isClick ? R.drawable.shape_image_border_select : R.drawable.shape_image_border;
        holder.outline.setBackgroundResource(drawable);

        if (isClick) {
            int textColor = Color.parseColor("#e36a63");
            holder.leftIndex.setTextColor(textColor);
            holder.rightIndex.setTextColor(textColor);
            holder.introindex.setTextColor(textColor);
            selectLayout = holder.rootLayout;
        } else {
            int textColor = Color.parseColor("#999999");
            holder.leftIndex.setTextColor(textColor);
            holder.rightIndex.setTextColor(textColor);
            holder.introindex.setTextColor(textColor);
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        m_iMovedPosition = toPosition;
        Collections.swap(m_arrPageList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        notifyItemRemoved(position);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
        EditActivityThumbnailHolder holder = (EditActivityThumbnailHolder) viewHolder;
        super.onViewRecycled(holder);

        if (holder == null) return;
        if (holder.canvas != null && !holder.canvas.isShown()) {
            holder.canvas.releaseReferences();

            BitmapUtil.drawableRecycle(holder.outline);
            BitmapUtil.drawableRecycle(holder.warnining);
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            m_iLongClickedItemId = (int) viewHolder.getItemId();
            m_iMovedPosition = m_iLongClickedItemId;
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            if (snapsProductEditorAPI != null) {
                EditActivityThumbnailHolder holder = getThumbnailHolder(m_iMovedPosition);
                if (holder != null)
                    snapsProductEditorAPI.onRearrange(holder.rootLayout, m_iLongClickedItemId, m_iMovedPosition);
            }
        }
    }

    public SnapsProductEditInfo getProductEditInfo() {
        return productEditInfo;
    }

    public void setProductEditInfo(SnapsProductEditInfo productEditInfo) {
        this.productEditInfo = productEditInfo;
    }
}