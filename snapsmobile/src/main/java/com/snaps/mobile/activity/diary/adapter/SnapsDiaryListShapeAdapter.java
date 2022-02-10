package com.snaps.mobile.activity.diary.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.transition.Transition;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.customview.SnapsDiaryLimitLineTextView;
import com.snaps.mobile.activity.diary.interfaces.IOnSnapsDiaryItemSelectedListener;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryHeaderClickListener;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryListItem;
import com.snaps.mobile.activity.diary.recoder.SnapsDiarySquareImageView;
import com.snaps.mobile.utils.ui.SnapsImageViewTarget;

import java.util.ArrayList;

public class SnapsDiaryListShapeAdapter extends SnapsDiaryBaseAdapter {

    public SnapsDiaryListShapeAdapter(Context context, IOnSnapsDiaryItemSelectedListener listener, ISnapsDiaryHeaderClickListener stripListener) {
        super(context, listener, stripListener);
    }

    public void setData(ArrayList<SnapsDiaryListItem> newList) {
        this.data = (ArrayList<SnapsDiaryListItem>)newList.clone();
        if(!isExistHeader())
            data.add(0, new SnapsDiaryListItem(SnapsDiaryListItem.ITEM_TYPE_HEADER));

        notifyDataSetChanged();
    }

    public void addMoreData(ArrayList<SnapsDiaryListItem> newList) {
        this.data = (ArrayList<SnapsDiaryListItem>)newList.clone();
        if(!isExistHeader())
            data.add(0, new SnapsDiaryListItem(SnapsDiaryListItem.ITEM_TYPE_HEADER));

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snaps_diary_list_item, parent, false);
        return new ListShapeItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);

        if(data == null || data.size() <= position) return;

        if (!(holder instanceof ListShapeItemHolder)) return;

        final ListShapeItemHolder itemHolder = (ListShapeItemHolder) holder;

        SnapsDiaryListItem diary = data.get(position);
        itemHolder.tvDate.setText(diary.getFormattedDate());

        itemHolder.tvRegisteredDate.setText(diary.getFormattedRegisteredDate());

        int iconWeatherResId = diary.getWeatherEnum().getIconResId(true);
        if (iconWeatherResId > 0 ) {
            itemHolder.ivWheather.setVisibility(View.VISIBLE);
            itemHolder.ivWheather.setImageResource(iconWeatherResId);
        } else {
            itemHolder.ivWheather.setVisibility(View.GONE);
        }

        int iconFeelsResId = diary.getFeelsEnum().getIconResId(true);
        if (iconFeelsResId > 0) {
            itemHolder.ivFeels.setVisibility(View.VISIBLE);
            itemHolder.ivFeels.setImageResource(iconFeelsResId);
        } else {
            itemHolder.ivFeels.setVisibility(View.GONE);
        }

        itemHolder.lySelectItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDiaryItemSelectedListener != null)
                    onDiaryItemSelectedListener.onDiaryItemSelected(getItem(position), position, false);
            }
        });

        itemHolder.lyParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDiaryItemSelectedListener != null)
                    onDiaryItemSelectedListener.onDiaryItemSelected(getItem(position), position, true);
            }
        });

        if (isLastItem(position)) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) itemHolder.lyParent.getLayoutParams();
            layoutParams.bottomMargin = UIUtil.convertDPtoPX(context, 10);
            itemHolder.lyParent.setLayoutParams(layoutParams);
        }

        if (itemHolder.squareImageView != null && itemHolder.squareImageView.getImageView() != null) {
            ImageView ivThumbnail = itemHolder.squareImageView.getImageView();
            ivThumbnail.setImageBitmap(null);

            final String URL = SnapsAPI.DOMAIN() + diary.getThumbnail();
            SnapsImageViewTarget bitmapImageViewTarget = new SnapsImageViewTarget(context, ivThumbnail) {
                @Override
                public void onResourceReady(Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    super.onResourceReady(resource, transition);

                    computeSizeThumbnailView(itemHolder.squareImageView);
                }

                @Override
                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                    super.onLoadFailed(errorDrawable);
                }
            };

            ImageLoader.asyncDisplayImage(context, URL, bitmapImageViewTarget);

            RelativeLayout.LayoutParams imageViewLayoutParams = (RelativeLayout.LayoutParams) ivThumbnail.getLayoutParams();
            if (diary.isForceMoreText() || (diary.getContents() != null && diary.getContents().trim().length() > 0)) {
                itemHolder.tvContents.setIsForceMoreText(diary.isForceMoreText());
                itemHolder.tvContents.setText(diary.getContents());
                itemHolder.tvContents.setVisibility(View.VISIBLE);
                imageViewLayoutParams.bottomMargin = UIUtil.convertDPtoPX(context, 14);
                ivThumbnail.setLayoutParams(imageViewLayoutParams);
            } else {

                itemHolder.tvContents.setVisibility(View.GONE);
                imageViewLayoutParams.bottomMargin = UIUtil.convertDPtoPX(context, 24);
                ivThumbnail.setLayoutParams(imageViewLayoutParams);
            }
        }
    }

    private void computeSizeThumbnailView(SnapsDiarySquareImageView squareImageView) {
        if(squareImageView == null || squareImageView.getImageView() == null || (squareImageView.getHeight() > 0 && squareImageView.getWidth() > 0)) return;
        ImageView imageView = squareImageView.getImageView();
        Drawable drawable = imageView.getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() < 1) return;

        float ratio = drawable.getIntrinsicHeight() / (float)drawable.getIntrinsicWidth();
        if(ratio <= 0.f) return;

        int width = imageView.getMeasuredWidth();
        if(width == 0) return;

        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        squareImageView.setWidth(width);
        squareImageView.setHeight((int) (width * ratio));
        layoutParams.height = squareImageView.getHeight();
        imageView.setLayoutParams(layoutParams);

        imageView.invalidate();
    }

    private boolean isLastItem(int position) {
        return data != null && data.size() - 1 == position;
    }

    public static class ListShapeItemHolder extends RecyclerView.ViewHolder {
        public RelativeLayout lyParent;
        public TextView tvDate;
        public TextView tvRegisteredDate;
        public SnapsDiaryLimitLineTextView tvContents;
        public ImageView ivWheather;
        public ImageView ivFeels;
        public RelativeLayout lySelectItem;
        public SnapsDiarySquareImageView squareImageView;

        public ListShapeItemHolder(View itemView) {
            super(itemView);

            lyParent = (RelativeLayout) itemView.findViewById(R.id.snaps_diary_list_item_parent_ly);
            tvDate = (TextView) itemView.findViewById(R.id.snaps_diary_list_item_date_tv);
            tvRegisteredDate = (TextView) itemView.findViewById(R.id.snaps_diary_list_item_regist_date_tv);
            tvContents = (SnapsDiaryLimitLineTextView) itemView.findViewById(R.id.snaps_diary_list_item_contents_tv);

            ivWheather = (ImageView) itemView.findViewById(R.id.snaps_diary_list_item_wheater_iv);
            ivFeels = (ImageView) itemView.findViewById(R.id.snaps_diary_list_item_feels_iv);
            lySelectItem = (RelativeLayout) itemView.findViewById(R.id.snaps_diary_list_item_select_ly);

            ImageView ivThumbnail = (ImageView) itemView.findViewById(R.id.snaps_diary_list_item_thumb_iv);
            squareImageView = new SnapsDiarySquareImageView();
            squareImageView.setImageView(ivThumbnail);
        }
    }

    @Override
    protected void clearImageResource(RecyclerView.ViewHolder holder) throws Exception {
        if (holder == null || !(holder instanceof ListShapeItemHolder)) return;

        ListShapeItemHolder photoHolder = (ListShapeItemHolder) holder;

        if (photoHolder.squareImageView.getImageView() != null) {
            ImageLoader.clear(context, photoHolder.squareImageView.getImageView());
        }
    }
}
