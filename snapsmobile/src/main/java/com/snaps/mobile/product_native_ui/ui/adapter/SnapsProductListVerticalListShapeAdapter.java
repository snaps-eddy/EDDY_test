package com.snaps.mobile.product_native_ui.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.recoder.SnapsDiarySquareImageView;
import com.snaps.mobile.product_native_ui.interfaces.IOnSnapsProductListItemSelectedListener;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsBaseProductListItem;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsProductPriceListItem;

import java.util.ArrayList;

public class SnapsProductListVerticalListShapeAdapter extends SnapsProductListBaseAdapter {
    private static final String TAG = SnapsProductListVerticalListShapeAdapter.class.getSimpleName();
    private final int MIN_GRID_COUNT = 7;

    public SnapsProductListVerticalListShapeAdapter(Context context, IOnSnapsProductListItemSelectedListener listener) {
        super(context, listener);
        setShape(SnapsProductListBaseAdapter.SHAPE_LIST);
    }

    public void setData(ArrayList<SnapsBaseProductListItem> newList) {
        this.data = (ArrayList<SnapsBaseProductListItem>) newList.clone();

        checkMinGripCount();

        notifyDataSetChanged();
    }

    public void checkMinGripCount() {
        if (data == null) return;

        if (data.size() >= MIN_GRID_COUNT) {
            data.add(new SnapsProductPriceListItem(SnapsBaseProductListItem.ITEM_TYPE_DUMMY));
            return;
        }

        int deficientCount = MIN_GRID_COUNT - data.size();
        for(int ii = 0; ii < deficientCount; ii++) {
            data.add(new SnapsProductPriceListItem(SnapsBaseProductListItem.ITEM_TYPE_DUMMY));
        }
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snaps_product_list_vertical_list_item, parent, false);
        return new ListShapeItemHolder(view);
    }

    private RecyclerView.ViewHolder getListDummyViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snaps_product_list_dummy_item, parent, false);
        return new ListDummyItemHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (data == null || data.size() <= position) return SnapsBaseProductListItem.ITEM_TYPE_DUMMY;
        SnapsBaseProductListItem item = data.get(position);
        return item != null ? item.getItemType() : SnapsBaseProductListItem.ITEM_TYPE_DUMMY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == SnapsBaseProductListItem.ITEM_TYPE_DUMMY) {
            return getListDummyViewHolder(parent);
        } else
            return getItemViewHolder(parent);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);

        if(data == null || data.size() <= position) return;

        if (!(holder instanceof ListShapeItemHolder)) return;

        final ListShapeItemHolder itemHolder = (ListShapeItemHolder) holder;

        Object object = data.get(position);

        if (!(object instanceof SnapsProductPriceListItem)) return;

        SnapsProductPriceListItem priceListItem = (SnapsProductPriceListItem) object;

        if(priceListItem.isDummyItem()) {
            itemHolder.lyParent.setBackgroundResource(0);
            itemHolder.ivArrow.setVisibility(View.GONE);
            itemHolder.ivUnderLine.setVisibility(View.GONE);
            return;
        }

        itemHolder.lyParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null)
                    clickListener.onProductListItemSelected(position, getItem(position));
            }
        });

        itemHolder.tvName.setText(priceListItem.getName());
        itemHolder.tvSizeDetail.setText(priceListItem.getSize());

        double discountPrice = 0;
        double orgPrice = 0;

        try {
            if (!StringUtil.isEmpty(priceListItem.getDiscountPrice()))
                discountPrice = Double.parseDouble(priceListItem.getDiscountPrice());
            if (!StringUtil.isEmpty(priceListItem.getOrgPrice()))
                orgPrice = Double.parseDouble(priceListItem.getOrgPrice());
        } catch (NumberFormatException e) { Dlog.e(TAG, e); }

        boolean isSamePrice = discountPrice == orgPrice;
        if (isSamePrice) {
            itemHolder.tvOrgPrice.setVisibility(View.GONE);
        } else {
            itemHolder.tvOrgPrice.setVisibility(View.VISIBLE);
            itemHolder.tvOrgPrice.setText(StringUtil.getCurrencyStr(context, orgPrice));
            itemHolder.tvOrgPrice.setPaintFlags(itemHolder.tvOrgPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        itemHolder.tvSalePrice.setText(StringUtil.getCurrencyStr(context, discountPrice));

        itemHolder.ivThumbnail.setImageBitmap(null);
        String path = SnapsAPI.DOMAIN(false) + priceListItem.getThumbnail();
        itemHolder.ivArrow.setVisibility(View.VISIBLE);
        itemHolder.ivUnderLine.setVisibility(View.VISIBLE);

        ImageLoader.with(context).load(path).centerCrop().into(itemHolder.ivThumbnail);
    }

    @Override
    protected void clearImageResourceOnViewRecycled(RecyclerView.ViewHolder holder) throws Exception {
        if (holder == null || !(holder instanceof ListShapeItemHolder)) return;
        ListShapeItemHolder listShapeItemHolder = (ListShapeItemHolder) holder;
        if (listShapeItemHolder.ivThumbnail == null) return;

        ImageLoader.clear(context, listShapeItemHolder.ivThumbnail);
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
        public TextView tvName;
        public TextView tvSizeDetail;
        public TextView tvOrgPrice;
        public TextView tvSalePrice;

        public ImageView ivThumbnail;
        public ImageView ivArrow;
        public ImageView ivUnderLine;
        public LinearLayout lyParent;

        public ListShapeItemHolder(View itemView) {
            super(itemView);
            lyParent = (LinearLayout) itemView.findViewById(R.id.snaps_product_list_vertical_list_item_parent_ly);
            tvName = (TextView) itemView.findViewById(R.id.snaps_product_list_vertical_list_item_title_tv);
            tvSizeDetail = (TextView) itemView.findViewById(R.id.snaps_product_list_vertical_list_item_sub_title_tv);
            tvOrgPrice = (TextView) itemView.findViewById(R.id.snaps_product_list_vertical_list_item_origin_price_tv);
            tvSalePrice = (TextView) itemView.findViewById(R.id.snaps_product_list_vertical_list_item_sale_price_tv);
            ivThumbnail = (ImageView) itemView.findViewById(R.id.snaps_product_list_vertical_list_item_thumbnail_iv);
            ivArrow = (ImageView) itemView.findViewById(R.id.snaps_product_list_vertical_list_item_sale_arrow_iv);
            ivUnderLine = (ImageView) itemView.findViewById(R.id.snaps_product_list_vertical_list_item_sale_under_line_iv);
        }
    }

    public static class ListDummyItemHolder extends RecyclerView.ViewHolder {
        public ListDummyItemHolder(View itemView) {
            super(itemView);
        }
    }
}
