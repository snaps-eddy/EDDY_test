package com.snaps.mobile.activity.themebook.design_list.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList;

import java.util.List;

/**
 * Created by kimduckwon on 2017. 11. 29..
 */

public abstract class BaseThemeDesignListAdapter extends RecyclerView.Adapter<BaseThemeDesignListAdapter.DesignListHolder>{
    Context context;
    List listItems = null;
    int maxCount = 0;
    int selectCount = 0;
    int getGridColumnWidth = 0;
    int imageWidth = 0;
    float mRatio = 0f;
    BaseThemeDesignList.SELECT_MODE mode;
    boolean isLandScapeMode;
    public CountListener countListener = null;

    public BaseThemeDesignListAdapter(Context context, DesignListAdapterAttribute adapterAttribute) {
        this.context = context;
        this.listItems = adapterAttribute.getListItems();
        this.mode = adapterAttribute.getMode();
        this.maxCount = adapterAttribute.getMaxCount();
        this.isLandScapeMode = adapterAttribute.isLandScapeMode();
        this.mRatio = adapterAttribute.getRatio();
        this.countListener = adapterAttribute.getCountListener();
        setGetGridColumnWidth();
    }

    public void setGetGridColumnWidth() {
        getGridColumnWidth = UIUtil.getGridColumnHeightNewyearsCard(context, isLandScapeMode ? 4 : 2, UIUtil.convertDPtoPX(context,8),  UIUtil.convertDPtoPX(context,24));
        imageWidth = getGridColumnWidth - UIUtil.convertDPtoPX(context, Const_VALUE.IMAGE_COVER_DESIGN_FRAME);
    }

    @Override
    public DesignListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_themeproduct_item,parent,false);
        return new DesignListHolder(view);
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
    public void onViewRecycled(DesignListHolder holder) {
        super.onViewRecycled(holder);

        if (holder == null) return;

        DesignListHolder designListHolder =  holder;

        if (designListHolder.imgCoverAlbum != null) {
            ImageLoader.clear(context, designListHolder.imgCoverAlbum);
        }
    }
    @Override
    public abstract void onBindViewHolder(DesignListHolder holder, int position) ;

    public abstract void selectLayout(int position);

    public abstract List getSelectList();

    public class DesignListHolder extends RecyclerView.ViewHolder {
        public ImageView imgCoverAlbum;
        public ImageView imgCoverSelect;
        public ImageView imgOutLine;
        public RelativeLayout selectLayout;
        public RelativeLayout layoutImgFrame;
        public int index;

        public DesignListHolder(View itemView) {
            super(itemView);
            imgCoverAlbum = (ImageView)itemView.findViewById(R.id.imgCoverAlbum);
            imgCoverSelect = (ImageView)itemView.findViewById(R.id.img_select);
            imgOutLine = (ImageView)itemView.findViewById(R.id.img_out_line);
            selectLayout = (RelativeLayout)itemView.findViewById(R.id.select_layout);
            layoutImgFrame = (RelativeLayout)itemView.findViewById(R.id.layoutImgFrame);
            selectLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectLayout(getAdapterPosition());
                }
            });
        }

    }

    public interface CountListener {
        void count(int count);
    }

    public static class DesignListAdapterAttribute {
        private List listItems;
        private BaseThemeDesignList.SELECT_MODE mode;
        private int maxCount;
        private boolean isLandScapeMode;
        private int spanCount ;
        private int layoutPadding = 12;
        private int layoutSpacing = 4;
        private CountListener countListener;
        private float mRatio = 0.0f;

        public List getListItems() {
            return listItems;
        }

        public BaseThemeDesignList.SELECT_MODE getMode() {
            return mode;
        }

        public int getMaxCount() {
            return maxCount;
        }

        public boolean isLandScapeMode() {
            return isLandScapeMode;
        }

        public int getSpanCount() {
            return spanCount;
        }

        public int getLayoutPadding() {
            return layoutPadding;
        }

        public int getLayoutSpacing() {
            return layoutSpacing;
        }

        public float getRatio() {
            return mRatio;
        }

        public CountListener getCountListener() {
            return countListener;
        }

        private DesignListAdapterAttribute(Builder builder) {
            listItems = builder.listItems;
            mode = builder.mode;
            maxCount = builder.maxCount;
            isLandScapeMode = builder.isLandScapeMode;
            countListener = builder.countListener;
            spanCount = builder.spanCount;
            layoutPadding = builder.layoutPadding;
            layoutSpacing = builder.layoutSpacing;
            mRatio = builder.mRatio;
        }

        public static class Builder {
            private List listItems;
            private BaseThemeDesignList.SELECT_MODE mode;
            private int maxCount;
            private boolean isLandScapeMode;
            private CountListener countListener;
            private int spanCount ;
            private int layoutPadding = 12;
            private int layoutSpacing = 4;
            private float mRatio = 0.0f;

            public Builder setListItems(List listItems) {
                this.listItems = listItems;
                return this;
            }

            public Builder setMode(BaseThemeDesignList.SELECT_MODE mode) {
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

            public Builder setSpanCount(int spanCount) {
                this.spanCount = spanCount;
                return  this;
            }

            public Builder setLayoutPadding(int padding) {
                this.layoutPadding = padding;
                return this;
            }

            public Builder setLayoutSpacing(int spacing) {
                this.layoutSpacing = spacing;
                return this;
            }

            public Builder setRatio(float mRatio) {
                this.mRatio = mRatio;
                return this;
            }

            public DesignListAdapterAttribute create() {
                return new DesignListAdapterAttribute(this);
            }
        }
    }
}
