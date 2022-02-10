package com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.customview.SnapsRecyclerView;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityTrayAllView;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectPublicMethods;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items.ImageSelectTrayCellItem;

import java.util.ArrayList;

/**
 * Created by ysjeong on 16. 3. 9..
 */
public class ImageSelectTrayTemplateShapeAllViewAdapter extends ImageSelectTrayTemplateShapeAdapter {

    public ImageSelectTrayTemplateShapeAllViewAdapter(Context context, IImageSelectPublicMethods imageSelectPublicMethods) {
        super(context, imageSelectPublicMethods);
    }

    //일반 썸네일 및 템플릿
    public RecyclerView.ViewHolder getThumbnailHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_select_all_view_thumbnail_item, parent, false);
        return new ImageSelectAdapterHolders.TrayThumbnailItemHolder(view);
    }

    //섹션 구분 타이틀
    public RecyclerView.ViewHolder getSectionTitleHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_select_tray_section_title_item, parent, false);
        return new ImageSelectAdapterHolders.TraySectionTitleItemHolder(view);
    }

    //섹션 구분 라인
    public RecyclerView.ViewHolder getSectionLineHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_select_tray_section_line_item, parent, false);
        return new ImageSelectAdapterHolders.TraySectionLineItemHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ISnapsImageSelectConstants.eTRAY_CELL_STATE.SECTION_TITLE.ordinal()) {
            return getSectionTitleHolder(parent);
        } else if (viewType == ISnapsImageSelectConstants.eTRAY_CELL_STATE.SECTION_LINE.ordinal()) {
            return getSectionLineHolder(parent);
        } else {
            return getThumbnailHolder(parent);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ImageSelectTrayCellItem cellItem = getTrayCellItem(position);
        if (cellItem == null || cellItem.getCellState() == null) return -1;
        return cellItem.getCellState().ordinal();
    }

    public void setTrayAllViewList(ArrayList<ImageSelectTrayCellItem> allViewList, int defaultSelectedId) {
        this.trayCellItemList = allViewList;

        notifyDataSetChanged();

        refreshCounterInfo();

        if (defaultSelectedId >= 0) {
            selectTrayItem(defaultSelectedId, false);
            scrollToCenterTrayView(findCellPositionByCellId(defaultSelectedId));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ImageSelectTrayCellItem cellItem = getTrayCellItem(position);
        if (cellItem == null) return;

        switch (cellItem.getCellState()) {
            case SECTION_TITLE:
                onBindViewSectionTitleHolder(holder, cellItem);
                break;
            case SECTION_LINE:
                break;
            case PHOTO_THUMBNAIL:
            case TEMPLATE:
            case PLUS_BUTTON:
                onBindViewThumbnailHolder(holder, cellItem);
                break;
            case EMPTY:
                break;
        }
    }

    //섹션 타이틀
    private void onBindViewSectionTitleHolder(final RecyclerView.ViewHolder holder, final ImageSelectTrayCellItem cellItem) {
        if (cellItem == null || holder == null) return;

        ImageSelectAdapterHolders.TraySectionTitleItemHolder trayHolder = (ImageSelectAdapterHolders.TraySectionTitleItemHolder) holder;

        //텍스트
        if (trayHolder.getLabel() != null) {
            TextView labelView = trayHolder.getLabel();
            String labelText = cellItem.getLabel();
            if (labelText != null)
                labelView.setText(labelText);
            else
                labelView.setText("");
        }
    }

    //트레이를 선택했을 때
    @Override
    public void onClickedTrayItem(ImageSelectTrayCellItem item) {
        if (item == null) return;
        ISnapsImageSelectConstants.eTRAY_CELL_STATE cellState = item.getCellState();
        int id = item.getCellId();

        switch (cellState) {
            case PLUS_BUTTON:
                tryAddPage(null, null);
                break;
            case TEMPLATE:
                selectTrayItem(id, false);
                break;
            case PHOTO_THUMBNAIL:
                selectTrayItem(id, false);
                break;
        }

        if (getTrayAllViewListener() != null)
            getTrayAllViewListener().onOccurredAnyMotion(item.getCellState());
    }

    @Override
    protected void scrollToLastItem() {
        if (trayControl != null) {
            final SnapsRecyclerView recyclerView = trayControl.getTrayThumbRecyclerView();
            if (recyclerView != null) {
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                layoutManager.scrollToPosition(getItemCount() - 1);
            }
        }
    }

    @Override
    public void scrollToCenterTrayView(int position) {
        if (!isValidScrollToCenterTrayView(position)) return;

        if (trayControl != null) {
            SnapsRecyclerView recyclerView = trayControl.getTrayThumbRecyclerView();
            if (recyclerView != null) {
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

                int centerPos = UIUtil.getScreenHeight(context) / 2;
                centerPos -= context.getResources().getDimension(R.dimen.tray_cell_dimens);

                layoutManager.scrollToPositionWithOffset(position, centerPos);
            }
        }
    }

    @Override
    public void addSection(String sectionLabel, int layoutCnt) {
        if (imageSelectPublicMethods == null || trayCellItemList == null) return;
        //section 삽입
        ImageSelectTrayCellItem section = new ImageSelectTrayCellItem(context, ISnapsImageSelectConstants.INVALID_VALUE, ISnapsImageSelectConstants.eTRAY_CELL_STATE.SECTION_TITLE);
        section.setLabel(sectionLabel);
        trayCellItemList.add(trayCellItemList.size() - layoutCnt, section);

        if (Config.isCheckPlusButton()) {
            trayCellItemList.add(new ImageSelectTrayCellItem(context, ISnapsImageSelectConstants.INVALID_VALUE, ISnapsImageSelectConstants.eTRAY_CELL_STATE.SECTION_LINE));
        }
    }
}
