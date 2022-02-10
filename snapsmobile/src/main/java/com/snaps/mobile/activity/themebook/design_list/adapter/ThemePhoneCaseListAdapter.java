package com.snaps.mobile.activity.themebook.design_list.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.bean.Xml_ThemePage;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;

import java.util.ArrayList;
import java.util.List;

public class ThemePhoneCaseListAdapter extends BaseThemeDesignListAdapter {

    private static final String TAG = ThemePhoneCaseListAdapter.class.getSimpleName();

    ThemePhoneCaseListAdapter(Context context, DesignListAdapterAttribute adapterAttribute) {
        super(context, adapterAttribute);
    }

    @Override
    public void setGetGridColumnWidth() {
        getGridColumnWidth = UIUtil.getGridColumnHeightNewyearsCard(context, isLandScapeMode ? 4 : 2, UIUtil.convertDPtoPX(context, isLandScapeMode ? 8 : 16), UIUtil.convertDPtoPX(context, 24));
        imageWidth = getGridColumnWidth - UIUtil.convertDPtoPX(context, Const_VALUE.IMAGE_COVER_DESIGN_FRAME);
    }

    @Override
    public DesignListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_photocard_change_design_item, parent, false);
        return new DesignListHolder(view);
    }

    @Override
    public void onBindViewHolder(DesignListHolder holder, int position) {
        try {
            if (listItems == null) return;
            Object object = listItems.get(position);
            if (!(object instanceof Xml_ThemePage.ThemePage)) return;
            Xml_ThemePage.ThemePage productListItem = (Xml_ThemePage.ThemePage) object;
            LinearLayout.LayoutParams mFrameLayoutParams = new LinearLayout.LayoutParams(getGridColumnWidth, (int) (getGridColumnWidth / 0.66f));
            RelativeLayout.LayoutParams mImageLayoutParams = new RelativeLayout.LayoutParams(getGridColumnWidth, (int) (getGridColumnWidth / 0.66f));
            RelativeLayout.LayoutParams mSelectLayoutParams = new RelativeLayout.LayoutParams(getGridColumnWidth, (int) (getGridColumnWidth / 0.66f));
            mImageLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            holder.layoutImgFrame.setLayoutParams(mFrameLayoutParams);
            holder.imgCoverAlbum.setLayoutParams(mImageLayoutParams);
            mSelectLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            holder.selectLayout.setLayoutParams(mSelectLayoutParams);

            String path = SnapsAPI.DOMAIN() + productListItem.F_SSMPL_URL;
            ImageLoader.with(context).load(path).into(holder.imgCoverAlbum);

            if (productListItem.F_IS_SELECT) {
                holder.imgCoverSelect.setVisibility(View.VISIBLE);
                holder.imgOutLine.setBackgroundResource(isLandScapeMode ? R.drawable.shape_image_border_change_design_item_select_for_landscape : R.drawable.shape_image_border_change_design_item_select);
            } else {
                holder.imgCoverSelect.setVisibility(View.INVISIBLE);
                holder.imgOutLine.setBackgroundColor(0);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void selectLayout(int position) {
        if (position < 0) return;
        if (listItems == null || listItems.size() <= position) return;

        Xml_ThemePage.ThemePage productListItem = (Xml_ThemePage.ThemePage)listItems.get(position);

        if(productListItem.F_IS_SELECT) {
            productListItem.F_IS_SELECT = false;
            notifyItemChanged(position);
        } else {
            setUnselect();
            productListItem.F_IS_SELECT = true;
            notifyDataSetChanged();
        }
    }

    private void setUnselect() {
        for(int i = 0 ; i < listItems.size() ; i++ ) {
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
