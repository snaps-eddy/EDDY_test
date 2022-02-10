package com.snaps.mobile.activity.themebook.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.ThemeProductListActivity;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsBaseProductListItem;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsProductGridShapeListItem;

import java.util.ArrayList;

/**
 * Created by kimduckwon on 2017. 11. 8..
 */

public class NewYearsCardAdapter extends RecyclerView.Adapter<NewYearsCardAdapter.NewYearCardHolder>{
    private Context context;
    private ArrayList<SnapsBaseProductListItem> listItems = null;
    private int maxCount = 0;
    private int selectCount = 0;
    private int getGridColumnWidth = 0;
    private int imageWidth = 0;
    private ThemeProductListActivity.SELECT_MODE mode;
    private boolean isLandScapeMode;
    public CountListener countListener = null;

    public NewYearsCardAdapter(Context context, NewYearsCardAdapterAttribute adapterAttribute) {
        this.context = context;
        this.listItems = adapterAttribute.getListItems();
        this.mode = adapterAttribute.getMode();
        this.maxCount = adapterAttribute.getMaxCount();
        this.isLandScapeMode = adapterAttribute.isLandScapeMode();
        this.countListener = adapterAttribute.getCountListener();
        getGridColumnWidth = UIUtil.getGridColumnHeightNewyearsCard(context, isLandScapeMode ? 4 : 2, UIUtil.convertDPtoPX(context,8),  UIUtil.convertDPtoPX(context,24));
        imageWidth = getGridColumnWidth - UIUtil.convertDPtoPX(context, Const_VALUE.IMAGE_COVER_DESIGN_FRAME);
    }

    @Override
    public NewYearCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_themeproduct_item,parent,false);
        return new NewYearCardHolder(view);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onViewRecycled(NewYearCardHolder holder) {
        super.onViewRecycled(holder);

        if (holder == null) return;

        if (holder.imgCoverAlbum != null) {
            ImageLoader.clear(context, holder.imgCoverAlbum);
        }
    }

    @Override
    public void onBindViewHolder(NewYearCardHolder holder, int position) {
        if(listItems == null) return;

        Object object = listItems.get(position);
        if (!(object instanceof SnapsProductGridShapeListItem)) return;
        SnapsProductGridShapeListItem productListItem = (SnapsProductGridShapeListItem) object;
        LinearLayout.LayoutParams mFrameLayoutParams = new LinearLayout.LayoutParams((int) (getGridColumnWidth ), (int) (getGridColumnWidth ));
        RelativeLayout.LayoutParams mImageLayoutParams = new RelativeLayout.LayoutParams((int) (getGridColumnWidth ), (int) (getGridColumnWidth ));
        RelativeLayout.LayoutParams mSelectLayoutParams = new RelativeLayout.LayoutParams((int)(getGridColumnWidth ),(int) (getGridColumnWidth ));
        mImageLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        holder.layoutImgFrame.setLayoutParams(mFrameLayoutParams);
        holder.imgCoverAlbum.setLayoutParams(mImageLayoutParams);
        mSelectLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        holder.selectLayout.setLayoutParams(mSelectLayoutParams);

        String path = SnapsAPI.DOMAIN() + productListItem.getThumbnail();
        ImageLoader.with(context).load(path).into(holder.imgCoverAlbum);
        if(productListItem.isSelect()) {
            holder.imgCoverSelect.setVisibility(View.VISIBLE);
            holder.imgOutLine.setBackgroundResource(R.drawable.image_border_change_design_item_select);
            holder.selectLayout.setBackgroundColor(Color.parseColor("#66000000"));
        } else {
            holder.imgCoverSelect.setVisibility(View.INVISIBLE);
            holder.selectLayout.setBackgroundColor(Color.parseColor("#00000000"));
            holder.imgOutLine.setBackgroundResource(R.drawable.image_product_change_design_item_non_select);
        }

    }

    public interface CountListener {
        void count(int count);
    }

    public ArrayList<String> getSelectData() {
        return getTempleteCode();
    }

    private ArrayList<String> getTempleteCode() {
        ArrayList<String> list = new ArrayList<String>();
        if(listItems.size() == 0) return list;
        
        for(int i = listItems.size() -1 ; i >=0  ; i--) {
            Object object = listItems.get(i);
            if (!(object instanceof SnapsProductGridShapeListItem)) continue;
            SnapsProductGridShapeListItem productListItem = (SnapsProductGridShapeListItem) object;
            if(productListItem.isSelect()) {
                list.add(productListItem.getTmplCode());
            }
        }

        return list;
    }


    public class NewYearCardHolder extends RecyclerView.ViewHolder {
        public ImageView imgCoverAlbum;
        public ImageView imgCoverSelect;
        public ImageView imgOutLine;
        public RelativeLayout selectLayout;
        public RelativeLayout layoutImgFrame;
        public int index;
        public NewYearCardHolder(View itemView) {
            super(itemView);
            imgCoverAlbum = (ImageView)itemView.findViewById(R.id.imgCoverAlbum);
            imgCoverSelect = (ImageView)itemView.findViewById(R.id.img_select);
            imgOutLine = (ImageView)itemView.findViewById(R.id.img_out_line);
            selectLayout = (RelativeLayout)itemView.findViewById(R.id.select_layout);
            layoutImgFrame = (RelativeLayout)itemView.findViewById(R.id.layoutImgFrame);
            selectLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectLayout();

                }
            });
        }

        private void selectLayout() {
            if(listItems == null) return;

            SnapsBaseProductListItem productListItem = listItems.get(getAdapterPosition());

            if(productListItem.isSelect()) {
                productListItem.setSelect(false);
                calCount(false);
                notifyItemChanged(getAdapterPosition());
            } else {
                if(mode == ThemeProductListActivity.SELECT_MODE.SINGLE_SELECT_CHANGE_DESIGN) {
                    setUnselect();
                    productListItem.setSelect(true);
                    calCount(true);
                    notifyDataSetChanged();
                } else {
                    if(calCount(true)) {
                        productListItem.setSelect(true);
                        notifyItemChanged(getAdapterPosition());
                    }
                }
            }

        }
        private void setUnselect() {
            for(int i = 0 ; i<listItems.size() ; i++ ) {
                listItems.get(i).setSelect(false);
            }
        }
        private boolean calCount(boolean select) {
            if(select) {
                if(maxCount <= selectCount) {
                    if(mode != ThemeProductListActivity.SELECT_MODE.SINGLE_SELECT_CHANGE_DESIGN) {
                        MessageUtil.toast(context, context.getString(R.string.more_not_select));
                    }
                    return false;
                }else {
                    selectCount++;
                    setCount();
                    return true;
                }
            }else {
                selectCount--;
                setCount();
                return true;
            }

        }
        private void setCount() {
            if(countListener == null) return;
                countListener.count(selectCount);
        }
    }

    public static class NewYearsCardAdapterAttribute {
        private ArrayList<SnapsBaseProductListItem> listItems;
        private ThemeProductListActivity.SELECT_MODE mode;
        private int maxCount;
        private boolean isLandScapeMode;
        private CountListener countListener;

        public ArrayList<SnapsBaseProductListItem> getListItems() {
            return listItems;
        }

        public ThemeProductListActivity.SELECT_MODE getMode() {
            return mode;
        }

        public int getMaxCount() {
            return maxCount;
        }

        public boolean isLandScapeMode() {
            return isLandScapeMode;
        }

        public CountListener getCountListener() {
            return countListener;
        }

        private NewYearsCardAdapterAttribute(Builder builder) {
            listItems = builder.listItems;
            mode = builder.mode;
            maxCount = builder.maxCount;
            isLandScapeMode = builder.isLandScapeMode;
            countListener = builder.countListener;
        }

        public static class Builder {
            private ArrayList<SnapsBaseProductListItem> listItems;
            private ThemeProductListActivity.SELECT_MODE mode;
            private int maxCount;
            private boolean isLandScapeMode;
            private CountListener countListener;

            public Builder setListItems(ArrayList<SnapsBaseProductListItem> listItems) {
                this.listItems = listItems;
                return this;
            }

            public Builder setMode(ThemeProductListActivity.SELECT_MODE mode) {
                this.mode = mode;
                return this;
            }

            public Builder setMaxCount(int maxCount) {
                this.maxCount = maxCount;
                return this;
            }

            public Builder setLandScapeMode(boolean isLandScapeMode) {
                this.isLandScapeMode = isLandScapeMode;
                return this;
            }

            public Builder setCountListener(CountListener countListener) {
                this.countListener = countListener;
                return this;
            }

            public NewYearsCardAdapterAttribute create() {
                return new NewYearsCardAdapterAttribute(this);
            }
        }
    }
}
