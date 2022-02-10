//package com.snaps.mobile.activity.google_style_image_selector.ui.adapters.tray;
//
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.snaps.common.utils.ui.UIUtil;
//import com.snaps.mobile.R;
//import com.snaps.mobile.activity.diary.customview.SnapsRecyclerView;
//import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
//import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
//import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectUITrayControl;
//import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;
//import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.items.ImageSelectTrayCellItem;
//
//import java.util.ArrayList;
//
///**
// * Created by ysjeong on 16. 3. 9..
// */
//public class ImageSelectTrayTransparencyPhotoCardAllViewAdapter extends ImageSelectTrayEmptyShapeAdapter {
//
//    public ImageSelectTrayTransparencyPhotoCardAllViewAdapter(ImageSelectActivityV2 imageSelectActivityV2, ImageSelectIntentData intentData) {
//        super(imageSelectActivityV2, intentData);
//        this.imageSelectActivityV2 = imageSelectActivityV2;
//    }
//
//    //일반 썸네일 및 템플릿
//    public RecyclerView.ViewHolder getThumbnailHolder(ViewGroup parent) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_select_all_view_empty_thumbnail_item, parent, false);
//        return new ImageSelectAdapterHolders.TrayThumbnailItemHolder(view);
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return getThumbnailHolder(parent);
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        ImageSelectTrayCellItem cellItem = getTrayCellItem(position);
//        if (cellItem == null || cellItem.getCellState() == null) return -1;
//        return cellItem.getCellState().ordinal();
//    }
//
//    public void setTrayAllViewList(ArrayList<ImageSelectTrayCellItem> allViewList, int defaultSelectedId) {
//        this.trayCellItemList = allViewList;
//
//        notifyDataSetChanged();
//
//        refreshCounterInfo();
//
//        if (defaultSelectedId >= 0) {
//            selectTrayItem(defaultSelectedId, false);
//            scrollToCenterTrayView(findCellPositionByCellId(defaultSelectedId));
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
//        final ImageSelectTrayCellItem cellItem = getTrayCellItem(position);
//        if (cellItem == null) return;
//        onBindViewThumbnailHolder(holder, cellItem);
//    }
//
//    //트레이를 선택했을 때
//    @Override
//    public void onClickedTrayItem(ImageSelectTrayCellItem item) {
//
//        if (item == null) return;
//        int id = item.getCellId();
//        selectTrayItem(id, false);
//
//        if (getTrayAllViewListener() != null)
//            getTrayAllViewListener().onOccurredAnyMotion(item.getCellState());
//    }
//
//    @Override
//    protected void scrollToLastItem() {
//        if (trayControl != null) {
//            final SnapsRecyclerView recyclerView = trayControl.getTrayThumbRecyclerView();
//            if (recyclerView != null) {
//                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
//                layoutManager.scrollToPosition(getItemCount()-1);
//            }
//        }
//    }
//
//    @Override
//    public void scrollToCenterTrayView(int position) {
//        if (position < 0 || position >= getItemCount()) return;
//
//        if (trayControl != null) {
//            SnapsRecyclerView recyclerView = trayControl.getTrayThumbRecyclerView();
//            if (recyclerView != null) {
//                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
//
//                int centerPos = UIUtil.getScreenHeight(imageSelectActivityV2) / 2;
//                centerPos -= imageSelectActivityV2.getResources().getDimension(R.dimen.tray_cell_dimens);
//
//                layoutManager.scrollToPositionWithOffset(position, centerPos);
//            }
//        }
//    }
//
//    // 카운터 정보 갱신
//    @Override
//    public void refreshCounterInfo() {
//        ImageSelectUITrayControl trayControl = getTrayControl();
//        if (trayControl == null || trayCellItemList == null) return;
//
//        int photoCount = 0;
//        for (ImageSelectTrayCellItem cellItem : trayCellItemList) {
//            if (cellItem != null) {
//                if (cellItem.isPlusBtn()) continue;
//
//                if (cellItem.getImageKey() != null && cellItem.getImageKey().length() > 0)
//                    photoCount++;
//            }
//        }
//
//        if (pageCountInfo != null) {
//            pageCountInfo.setCurrentSelectedImageCount(photoCount);
//        }
//
//        TextView tvLeftCountView = trayControl.getLeftCountView();
//        if (tvLeftCountView != null) {
//            tvLeftCountView.setText(String.valueOf(photoCount));
//        }
//    }
//}
