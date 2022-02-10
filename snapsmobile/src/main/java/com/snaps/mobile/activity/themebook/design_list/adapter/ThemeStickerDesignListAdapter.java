package com.snaps.mobile.activity.themebook.design_list.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.bean.Xml_ThemePage;
import com.snaps.mobile.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kimduckwon on 2018. 1. 16..
 */

public class ThemeStickerDesignListAdapter extends BaseThemeDesignListAdapter{
    private static final String TAG = ThemeStickerDesignListAdapter.class.getSimpleName();

    public ThemeStickerDesignListAdapter(Context context, DesignListAdapterAttribute adapterAttribute) {
        super(context, adapterAttribute);
    }

    @Override
    public void onBindViewHolder(DesignListHolder holder, int position) {
        try {
            if (listItems == null) return;

            Object object = listItems.get(position);
            if (!(object instanceof Xml_ThemePage.ThemePage)) return;
            Xml_ThemePage.ThemePage productListItem = (Xml_ThemePage.ThemePage) object;
            LinearLayout.LayoutParams mFrameLayoutParams = new LinearLayout.LayoutParams((int) (getGridColumnWidth), (int) (getGridColumnWidth));
            RelativeLayout.LayoutParams mImageLayoutParams = new RelativeLayout.LayoutParams((int) (getGridColumnWidth), (int) (getGridColumnWidth));
            RelativeLayout.LayoutParams mSelectLayoutParams = new RelativeLayout.LayoutParams((int) (getGridColumnWidth), (int) (getGridColumnWidth));
            mImageLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            holder.layoutImgFrame.setLayoutParams(mFrameLayoutParams);
            holder.imgCoverAlbum.setLayoutParams(mImageLayoutParams);
            mSelectLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            holder.selectLayout.setLayoutParams(mSelectLayoutParams);

            String path = SnapsAPI.DOMAIN() + productListItem.F_SSMPL_URL;
            ImageLoader.with(context).load(path).into(holder.imgCoverAlbum);
            if (productListItem.F_IS_SELECT) {
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
        if (position < 0) return;
        if(listItems == null || listItems.size() <= position) return;

        Xml_ThemePage.ThemePage productListItem = (  Xml_ThemePage.ThemePage) listItems.get(position);

        if (productListItem.F_IS_SELECT) {
            productListItem.F_IS_SELECT = false;
            notifyItemChanged(position);
        } else {
            setUnSelect();
            productListItem.F_IS_SELECT = true;
            notifyDataSetChanged();
        }
    }

    private void setUnSelect() {
        for (int i = 0 ; i < listItems.size() ; i++ ) {
            ((Xml_ThemePage.ThemePage)listItems.get(i)).F_IS_SELECT = false;
        }
    }

    @Override
    public List getSelectList() {
        List<Xml_ThemePage.ThemePage> list = new ArrayList<>();
        for(int i = 0 ; i < listItems.size() ; i++) {
            Xml_ThemePage.ThemePage item = (Xml_ThemePage.ThemePage)listItems.get(i);
            if(item.F_IS_SELECT) {
                list.add(item);
            }
        }
        return list;
    }
}
