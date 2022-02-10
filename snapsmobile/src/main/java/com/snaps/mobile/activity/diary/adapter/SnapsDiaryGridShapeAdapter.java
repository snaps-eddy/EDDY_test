package com.snaps.mobile.activity.diary.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.interfaces.IOnSnapsDiaryItemSelectedListener;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryHeaderClickListener;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListItem;

import java.util.ArrayList;

public class SnapsDiaryGridShapeAdapter extends SnapsDiaryBaseAdapter {

    //리스트와 그리드간 화면이 전환될 때, 탭의 위치를 유지하게 위해 최소 12개의 그리드를 채워준다.
    private final int MIN_GRID_COUNT = 12;

    public SnapsDiaryGridShapeAdapter(Context context, IOnSnapsDiaryItemSelectedListener listener, ISnapsDiaryHeaderClickListener stripListener) {
        super(context, listener, stripListener);
        setShape(SnapsDiaryBaseAdapter.SHAPE_GRID);
    }

    public void setData(ArrayList<SnapsDiaryListItem> newList) {
        this.data = (ArrayList<SnapsDiaryListItem>) newList.clone();
        if(!isExistHeader())
            data.add(0, new SnapsDiaryListItem(SnapsDiaryListItem.ITEM_TYPE_HEADER));

        checkMinGripCount();

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snaps_diary_grid_item, parent, false);
        return new GridItemHolder(context, view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);

        if(data == null || data.size() <= position) return;

        if (!(holder instanceof SnapsDiaryGridShapeAdapter.GridItemHolder)) return;

        SnapsDiaryGridShapeAdapter.GridItemHolder gridItemHolder = (SnapsDiaryGridShapeAdapter.GridItemHolder) holder;

        SnapsDiaryListItem diary = data.get(position);
        if(diary.isDummyItem()) {
            gridItemHolder.lyParent.setBackgroundResource(0);
            return;
        }

        gridItemHolder.ivThumbnail.setImageBitmap(null);
        String path = SnapsAPI.DOMAIN() + diary.getThumbnail();

        ImageLoader.with(context).load(path).centerCrop().into(gridItemHolder.ivThumbnail);

        gridItemHolder.lyParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDiaryItemSelectedListener != null)
                    onDiaryItemSelectedListener.onDiaryItemSelected(getItem(position), position, true);
            }
        });
    }

    public void checkMinGripCount() {
        if(data == null || data.size() >= MIN_GRID_COUNT) return;
        int deficientCount = MIN_GRID_COUNT - data.size();
        for(int ii = 0; ii < deficientCount; ii++) {
            data.add(new SnapsDiaryListItem(SnapsDiaryListItem.ITEM_TYPE_DUMMY));
        }
    }

    @Override
    protected void clearImageResource(RecyclerView.ViewHolder holder) throws Exception {
        if (holder == null || !(holder instanceof GridItemHolder)) return;

        GridItemHolder photoHolder = (GridItemHolder) holder;

        if (photoHolder.ivThumbnail != null) {
            ImageLoader.clear(context, photoHolder.ivThumbnail);
        }
    }

    public static class GridItemHolder extends RecyclerView.ViewHolder {
        public ImageView ivThumbnail;
        public RelativeLayout lyParent;

        public GridItemHolder(Context context, View itemView) {
            super(itemView);
            ivThumbnail = (ImageView) itemView.findViewById(R.id.snaps_diary_grid_item_thumb_iv);
            lyParent = (RelativeLayout) itemView.findViewById(R.id.snaps_diary_grid_item_parent_ly);

            int columnWidth = UIUtil.getCalcWidth((Activity) context, SnapsDiaryBaseAdapter.GRID_COLUMN_COUNT, false);
            GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) lyParent.getLayoutParams();
            layoutParams.width = columnWidth;
            layoutParams.height = layoutParams.width;

            lyParent.setLayoutParams(layoutParams);
        }
    }
}
