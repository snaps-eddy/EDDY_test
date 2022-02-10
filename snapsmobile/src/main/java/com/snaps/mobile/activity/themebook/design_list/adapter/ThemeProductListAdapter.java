package com.snaps.mobile.activity.themebook.design_list.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsBaseProductListItem;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsProductGridShapeListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimduckwon on 2017. 11. 30..
 */

public class ThemeProductListAdapter extends BaseThemeDesignListAdapter{
    private static final String TAG = ThemeProductListAdapter.class.getSimpleName();
    public ThemeProductListAdapter(Context context, DesignListAdapterAttribute adapterAttribute) {
        super(context, adapterAttribute);
    }

    @Override
    public void onBindViewHolder(DesignListHolder holder, int position) {
     try {
             if (listItems == null) return;

             Object object = listItems.get(position);
             if (!(object instanceof SnapsProductGridShapeListItem)) return;
             SnapsProductGridShapeListItem productListItem = (SnapsProductGridShapeListItem) object;
             LinearLayout.LayoutParams mFrameLayoutParams = new LinearLayout.LayoutParams((int) (getGridColumnWidth), (int) (getGridColumnWidth));
             RelativeLayout.LayoutParams mImageLayoutParams = new RelativeLayout.LayoutParams((int) (getGridColumnWidth), (int) (getGridColumnWidth));
             RelativeLayout.LayoutParams mSelectLayoutParams = new RelativeLayout.LayoutParams((int) (getGridColumnWidth), (int) (getGridColumnWidth));
             mImageLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
             holder.layoutImgFrame.setLayoutParams(mFrameLayoutParams);
             holder.imgCoverAlbum.setLayoutParams(mImageLayoutParams);
             mSelectLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
             holder.selectLayout.setLayoutParams(mSelectLayoutParams);

             String path = SnapsAPI.DOMAIN() + productListItem.getThumbnail();
             ImageLoader.with(context).load(path).into(holder.imgCoverAlbum);
             if (productListItem.isSelect()) {
                 holder.imgCoverSelect.setVisibility(View.VISIBLE);
                 holder.imgOutLine.setBackgroundResource(R.drawable.image_border_change_design_item_select);
                 holder.selectLayout.setBackgroundColor(Color.parseColor("#66000000"));
             } else {
                 holder.imgCoverSelect.setVisibility(View.INVISIBLE);
                 holder.selectLayout.setBackgroundColor(Color.parseColor("#00000000"));
                 holder.imgOutLine.setBackgroundResource(R.drawable.image_product_change_design_item_non_select);
             }
     }catch (Exception e) {
         Dlog.e(TAG, e);
     }
    }

    @Override
    public void selectLayout(int position) {
        if(listItems == null) return;

        SnapsBaseProductListItem productListItem = (SnapsBaseProductListItem)listItems.get(position);

        if(productListItem.isSelect()) {
            productListItem.setSelect(false);
            calCount(false);
            notifyItemChanged(position);
        } else {
            if (mode == BaseThemeDesignList.SELECT_MODE.SINGLE_SELECT_CHANGE_DESIGN) {
                setUnselect();
                productListItem.setSelect(true);
                calCount(true);
                notifyDataSetChanged();
            } else {
                if(calCount(true)) {
                    productListItem.setSelect(true);
                    notifyItemChanged(position);
                }
            }
        }

    }

    private void setUnselect() {
        for (int i = 0 ; i < listItems.size() ; i++ ) {
            ((SnapsBaseProductListItem)listItems.get(i)).setSelect(false);
        }
    }
    private boolean calCount(boolean select) {
        if (select) {
            if (maxCount <= selectCount) {
                if(mode != BaseThemeDesignList.SELECT_MODE.SINGLE_SELECT_CHANGE_DESIGN) {
                    MessageUtil.toast(context, context.getString(R.string.more_not_select));
                }
                return false;
            } else {
                selectCount++;
                setCount();
                return true;
            }
        } else {
            selectCount--;
            setCount();
            return true;
        }

    }
    private void setCount() {
        if (countListener == null) return;
        countListener.count(selectCount);
    }

    @Override
    public List getSelectList() {
        List<SnapsBaseProductListItem> list = new ArrayList<>();
        for (int i = 0 ; i < listItems.size() ; i++) {
            SnapsBaseProductListItem item = (SnapsBaseProductListItem)listItems.get(i);
            if(item.isSelect()) {
                list.add(item);
            }
        }
        return list;
    }
}
