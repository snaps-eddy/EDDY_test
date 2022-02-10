package com.snaps.mobile.activity.themebook.design_list.design_list;

import android.content.Intent;
import com.snaps.common.model.WebViewPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.Xml_ThemePage;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.design_list.NewThemeDesignListActivity;
import com.snaps.mobile.activity.themebook.design_list.adapter.BaseThemeDesignListAdapter;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsBaseProductListItem;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsProductGridShapeListItem;

import java.util.ArrayList;
import java.util.List;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import static android.app.Activity.RESULT_OK;

/**
 * Created by kimduckwon on 2018. 1. 15..
 */

public class StickerDesignList extends BaseThemeDesignList{
    private String STICKER_CLASSCODE = "045020";
    private int selectMaxCount = 1;
    public Xml_ThemePage xmlThemeTotalPage;
    private boolean loadComplete = false;

    public StickerDesignList(NewThemeDesignListActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void performNextButton() {
        Xml_ThemePage.ThemePage pageData = getSelectedPageData();

        if (pageData != null) {
            Intent data = new Intent();

            data.putExtra("pageXMLPATH", pageData.F_XML_PATH);

            activity.setResult(RESULT_OK, data);
            activity.finish();
        } else {
            MessageUtil.toast(activity, R.string.theme_page_select);
        }
    }

    private Xml_ThemePage.ThemePage getSelectedPageData() {
        Xml_ThemePage.ThemePage  pageData = null;
        List dataList = getSelectData();
        if(dataList != null && !dataList.isEmpty()) {
            pageData = (Xml_ThemePage.ThemePage) getSelectData().get(0);
            return pageData;
        } else {
            return null;
        }
    }

    private ArrayList<String> getSelectTempleteCodes() {
        List<SnapsBaseProductListItem> listItems = getSelectData();
        ArrayList<String> list = new ArrayList<String>();
        if(listItems != null && listItems.size() == 0) return null;

        for (int i = listItems.size() -1 ; i >= 0  ; i--) {
            Object object = listItems.get(i);
            if (!(object instanceof SnapsProductGridShapeListItem)) continue;
            SnapsProductGridShapeListItem productListItem = (SnapsProductGridShapeListItem) object;
            if (productListItem.isSelect()) {
                list.add(productListItem.getTmplCode());
            }
        }
        return list;
    }

    @Override
    public boolean isSuccessLoadDesignList() {
        return loadComplete;
    }

    @Override
    public void loadDesignList() {
        xmlThemeTotalPage = GetParsedXml.getPhotoBookPage(Config.getPROD_CODE(), STICKER_CLASSCODE, null, null, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
        if(xmlThemeTotalPage != null) {
            loadComplete = true;
        }
    }

    @Override
    public List<WebViewPage> setPage() {
        List<WebViewPage> pages = new ArrayList<WebViewPage>();
        pages.add(new WebViewPage("",""));
        return pages;
    }

    @Override
    public int getLimitViewCount() {
        return 1;
    }

    BaseThemeDesignListAdapter.CountListener countListener  = new BaseThemeDesignListAdapter.CountListener() {
        @Override
        public void count(int count) {

        }
    };
    @Override
    public BaseThemeDesignListAdapter.DesignListAdapterAttribute getAttribute(int position) {
        BaseThemeDesignListAdapter.DesignListAdapterAttribute attribute = new  BaseThemeDesignListAdapter.DesignListAdapterAttribute.Builder()
                .setListItems(getDesignList())
                .setMode(mode)
                .setMaxCount(selectMaxCount)
                .setLandScapeMode(m_isLandScapeMode)
                .setCountListener(countListener)
                .setSpanCount(m_isLandScapeMode? 4 : 2)
                .create();;
        return attribute;
    }

    private List<Xml_ThemePage.ThemePage> getDesignList() {
        List<Xml_ThemePage.ThemePage> arrDesignList = new ArrayList<Xml_ThemePage.ThemePage>();

        if(!isSuccessLoadDesignList()) return arrDesignList;
        for(Xml_ThemePage.ThemePage page : xmlThemeTotalPage.bgList) {
            if(page == null) continue;
            arrDesignList.add(page);
        }
        return arrDesignList;
    }

    @Override
    public void getIntent() {

    }
}
