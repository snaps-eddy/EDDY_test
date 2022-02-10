package com.snaps.mobile.product_native_ui.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.ui.menu.renewal.view.ReloadableImageView;
import com.snaps.mobile.product_native_ui.interfaces.IOnSnapsProductListItemSelectedListener;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsBaseProductListItem;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsProductGridShapeListItem;
import com.snaps.mobile.product_native_ui.util.SnapsNativeUIManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static com.snaps.mobile.product_native_ui.util.SnapsNativeUIManager.PRODUCT_LIST_SORT_TYPE.SORT_BY_NEWEST;
import static com.snaps.mobile.product_native_ui.util.SnapsNativeUIManager.PRODUCT_LIST_SORT_TYPE.SORT_BY_POPULAR;

public class SnapsProductListGridShapeAdapter extends SnapsProductListBaseAdapter {

    private int minGridCount;
    private boolean isLargeThumbnailMode = SnapsNativeUIManager.DEFAULT_LIST_UI_LARGE_VIEW;;

    public SnapsProductListGridShapeAdapter(Context context, IOnSnapsProductListItemSelectedListener listener, boolean isLargeThumbnailMode) {
        super(context, listener);
        setShape(SnapsProductListBaseAdapter.SHAPE_GRID);

        this.isLargeThumbnailMode = isLargeThumbnailMode;

        this.minGridCount = isLargeThumbnailMode ? 3 : 6;
    }

    public void setData(ArrayList<SnapsBaseProductListItem> newList) {
        this.data = (ArrayList<SnapsBaseProductListItem>) newList.clone();
        if(!isExistHeader())
            data.add(0, new SnapsProductGridShapeListItem(SnapsBaseProductListItem.ITEM_TYPE_SORT_HEADER));

        checkMinGripCount();

        notifyDataSetChanged();
    }

    public ArrayList<SnapsBaseProductListItem> getData() {
        return this.data;
    }

    private boolean isHideSortHeader() {
        if(data == null || data.size() < 2) return true;

        SnapsBaseProductListItem listItem = data.get(1);
        if (!(listItem instanceof SnapsProductGridShapeListItem)) return true;
        return ((SnapsProductGridShapeListItem)listItem).getThumbnail() == null;
    }

    public void checkMinGripCount() {
        if(data == null || data.size() >= minGridCount) return;
        int deficientCount = minGridCount - data.size();
        for(int ii = 0; ii < deficientCount; ii++) {
            data.add(new SnapsProductGridShapeListItem(SnapsBaseProductListItem.ITEM_TYPE_DUMMY));
        }
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                isLargeThumbnailMode ? R.layout.snaps_product_list_large_thumbnail_item : R.layout.snaps_product_list_grid_item, parent, false);
        return new GridItemHolder(context, view, isLargeThumbnailMode);
    }

    private RecyclerView.ViewHolder getSortViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snaps_product_list_sort_item, parent, false);
        return new SortItemHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SnapsBaseProductListItem.ITEM_TYPE_SORT_HEADER) {
            return getSortViewHolder(parent);
        } else
            return getItemViewHolder(parent);
    }

    @Override
    protected void clearImageResourceOnViewRecycled(RecyclerView.ViewHolder holder) throws Exception {
        if (holder == null || !(holder instanceof SnapsProductListGridShapeAdapter.GridItemHolder)) return;
        SnapsProductListGridShapeAdapter.GridItemHolder gridItemHolder = (SnapsProductListGridShapeAdapter.GridItemHolder) holder;
        if (gridItemHolder.ivThumbnail == null) return;

        ImageLoader.clear(context, gridItemHolder.ivThumbnail);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);

        if (holder instanceof SnapsProductListGridShapeAdapter.SortItemHolder) { //Grid 형태에서 첫번째 아이템은 sorting 영역이다.
            bindHeader((SnapsProductListGridShapeAdapter.SortItemHolder) holder);
            return;
        }

        if(data == null || data.size() <= position) return;

        if (!(holder instanceof SnapsProductListGridShapeAdapter.GridItemHolder)) return;
        SnapsProductListGridShapeAdapter.GridItemHolder gridItemHolder = (SnapsProductListGridShapeAdapter.GridItemHolder) holder;

        Object object = data.get(position);
        if (!(object instanceof SnapsProductGridShapeListItem)) return;

        SnapsProductGridShapeListItem productListItem = (SnapsProductGridShapeListItem) object;
        if(productListItem.isDummyItem()) { //만약, 아이템이 몇개 안 될 경우, 화면에 꽉차게 보이게 하기 위해 더미뷰를 붙여 넣는다.
            gridItemHolder.lyParent.setBackgroundResource(0);
            gridItemHolder.lyImageArea.setBackgroundColor(0);
            return;
        }

        gridItemHolder.lyParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null)
                    clickListener.onProductListItemSelected(position - 1, getItem(position)); //Grid 형태에서는 1번째 아이템이 Sorting 영역이다.
            }
        });

        gridItemHolder.textView.setText(productListItem.getContents());

        gridItemHolder.ivThumbnail.setImageBitmap(null);
        String path = SnapsAPI.DOMAIN() + productListItem.getThumbnail();
        if (isLargeThumbnailMode && productListItem.getLargeThumbnail() != null && productListItem.getLargeThumbnail().length() > 0) {
            path = SnapsAPI.DOMAIN() + productListItem.getLargeThumbnail();
        }

        gridItemHolder.ivPremiumIcon.setVisibility(productListItem.isPremium() ? View.VISIBLE : View.GONE);
//        Glide.with(context).load(path).animate(R.anim.native_ui_product_list_fade_in).centerCrop().into(gridItemHolder.ivThumbnail);
        ImageLoader.with(context).load(path).animate(R.anim.native_ui_product_list_fade_in).centerCrop().into(gridItemHolder.ivThumbnail);
        gridItemHolder.ivThumbnail.setPath( path );

    }

    public void sortList(SnapsNativeUIManager.PRODUCT_LIST_SORT_TYPE sortType) {
        if (data == null || data.size() < 3) return; //첫번째는 헤더니까, 최소 3개이상이어야 정렬이 의미를 가짐.

        SnapsBaseProductListItem header = null;
        if (isExistHeader()) {
            header = data.remove(0);
        }

        switch (sortType) {
            case SORT_BY_POPULAR :
                Collections.sort(data, new Comparator<SnapsBaseProductListItem>() {
                    @Override
                    public int compare(SnapsBaseProductListItem lhs, SnapsBaseProductListItem rhs) {
                        SnapsProductGridShapeListItem lItem = (SnapsProductGridShapeListItem) lhs;
                        SnapsProductGridShapeListItem rItem = (SnapsProductGridShapeListItem) rhs;
                        if (lItem.isDummyItem()) return 1;

                        return lItem.getPopularNumber() < rItem.getPopularNumber() ? -1 : (lItem.getPopularNumber() > rItem.getPopularNumber() ? 1 : 0);
                    }
                });
                break;
            case SORT_BY_NEWEST :
                Collections.sort(data, new Comparator<SnapsBaseProductListItem>() {
                    @Override
                    public int compare(SnapsBaseProductListItem lhs, SnapsBaseProductListItem rhs) {
                        SnapsProductGridShapeListItem lItem = (SnapsProductGridShapeListItem) lhs;
                        SnapsProductGridShapeListItem rItem = (SnapsProductGridShapeListItem) rhs;
                        if (lItem.isDummyItem()) return 1;

                        return lItem.getRegDateInteger() > rItem.getRegDateInteger() ? -1 : (lItem.getRegDateInteger() < rItem.getRegDateInteger() ? 1 : 0);
                    }
                });
                break;
        }

        data.add(0, header);

        notifyDataSetChanged();
    }

    public static class GridItemHolder extends RecyclerView.ViewHolder {
        public ImageView ivPremiumIcon;
        public ReloadableImageView ivThumbnail;
        public TextView textView;
        public RelativeLayout lyParent;
        public FrameLayout lyImageArea;

        public GridItemHolder(Context context, View itemView, boolean isLargeThumbnailMode) {
            super(itemView);
            ivPremiumIcon = (ImageView) itemView.findViewById(R.id.snaps_product_list_grid_item_premium_icon_iv);
            ivThumbnail = (ReloadableImageView) itemView.findViewById(R.id.snaps_product_list_grid_item_thumb_iv);
            textView = (TextView) itemView.findViewById(R.id.snaps_product_list_grid_item_name_tv);
            lyParent = (RelativeLayout) itemView.findViewById(R.id.snaps_product_list_grid_item_parent_ly);
            lyImageArea = (FrameLayout) itemView.findViewById(R.id.snaps_product_list_grid_item_thumb_ly);

            if (isLargeThumbnailMode) {
                int columnWidth = UIUtil.getCalcWidth((Activity) context, 1, false) - UIUtil.convertDPtoPX(context, 32); // margin 좌우 16
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) lyImageArea.getLayoutParams();
                layoutParams.width = columnWidth;
                layoutParams.height = layoutParams.width;
                lyImageArea.setLayoutParams(layoutParams);

                FrameLayout.LayoutParams imgParams = (FrameLayout.LayoutParams) ivThumbnail.getLayoutParams();
                imgParams.width = columnWidth;
                imgParams.height = layoutParams.width;
                ivThumbnail.setLayoutParams(imgParams);
            }
        }
    }

    private void bindHeader(final SnapsProductListGridShapeAdapter.SortItemHolder holder) {
        if (holder == null || holder.tvNewest == null || holder.tvPopular == null) return;

        holder.tvNewest.setVisibility(isHideSortHeader() ? View.GONE : View.VISIBLE);
        holder.tvPopular.setVisibility(isHideSortHeader() ? View.GONE : View.VISIBLE);

        final SnapsNativeUIManager nativeUIManager = SnapsNativeUIManager.getInstance();
        if (nativeUIManager != null) {
            if (nativeUIManager.getCurrentProductListSortType() == SORT_BY_NEWEST) {
                holder.tvNewest.setTextColor(Color.parseColor("#1a1a1a")); //선택된 색상
                holder.tvPopular.setTextColor(Color.parseColor("#999999"));
            } else {
                holder.tvNewest.setTextColor(Color.parseColor("#999999"));
                holder.tvPopular.setTextColor(Color.parseColor("#1a1a1a"));
            }

            if (nativeUIManager.isGridListLargeView()) {
                holder.ivLargeThumbnailMode.setImageResource(R.drawable.icon_list_large_view_mode_focus);
                holder.ivGridMode.setImageResource(R.drawable.icon_list_grid_view_mode);
            } else {
                holder.ivLargeThumbnailMode.setImageResource(R.drawable.icon_list_large_view_mode);
                holder.ivGridMode.setImageResource(R.drawable.icon_list_grid_view_mode_focus);
            }
        }

        holder.tvNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nativeUIManager == null) return;
                nativeUIManager.notifyInvokeSortItems(SORT_BY_NEWEST);
            }
        });

        holder.tvPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nativeUIManager == null) return;
                nativeUIManager.notifyInvokeSortItems(SORT_BY_POPULAR);
            }
        });

        holder.ivLargeThumbnailMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nativeUIManager == null) return;
                nativeUIManager.notifyViewModeChange(true);
            }
        });

        holder.ivGridMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nativeUIManager == null) return;
                nativeUIManager.notifyViewModeChange(false);
            }
        });
    }

    private boolean isExistHeader() {
        if(data == null || data.size() < 1) return false;

        SnapsBaseProductListItem listItem = data.get(0);
        return listItem != null && listItem.isHeader();
    }

    public static class SortItemHolder extends RecyclerView.ViewHolder {
        public TextView tvPopular;
        public TextView tvNewest;

        public ImageView ivLargeThumbnailMode;
        public ImageView ivGridMode;

        public SortItemHolder(View itemView) {
            super(itemView);
            tvPopular = (TextView) itemView.findViewById(R.id.snaps_product_list_sort_item_popular_tv);
            tvNewest = (TextView) itemView.findViewById(R.id.snaps_product_list_sort_item_newest_tv);

            ivLargeThumbnailMode = (ImageView) itemView.findViewById(R.id.snaps_product_list_sort_item_large_thumbnail_mode_btn);
            ivGridMode = (ImageView) itemView.findViewById(R.id.snaps_product_list_sort_item_grid_mode_btn);
        }
    }
}
