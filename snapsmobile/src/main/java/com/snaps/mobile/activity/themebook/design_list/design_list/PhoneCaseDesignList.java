package com.snaps.mobile.activity.themebook.design_list.design_list;

import android.content.Intent;

import com.snaps.common.model.WebViewPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.Xml_ThemePage;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.themebook.design_list.NewThemeDesignListActivity;
import com.snaps.mobile.activity.themebook.design_list.adapter.BaseThemeDesignListAdapter;

import java.util.ArrayList;
import java.util.List;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

import static android.app.Activity.RESULT_OK;

public class PhoneCaseDesignList extends BaseThemeDesignList {

    private static final String TAG = PhoneCaseDesignList.class.getSimpleName();

    private Xml_ThemePage xmlThemeCurrentDesignPage = null;
    private boolean loadComplete = false;

    public PhoneCaseDesignList(NewThemeDesignListActivity fragmentActivity) {
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
        Xml_ThemePage.ThemePage pageData;
        List dataList = getSelectData();
        if (dataList != null && dataList.size() != 0) {
            pageData = (Xml_ThemePage.ThemePage) getSelectData().get(0);
            return pageData;
        } else {
            return null;
        }
    }

    @Override
    public boolean isSuccessLoadDesignList() {
        return loadComplete;
    }

    @Override
    public void loadDesignList() {
//        xmlThemeCurrentDesignPage = GetParsedXml.getPhotoBookPage(Config.getPROD_CODE(), null, null, null, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
        // 이제 디자인 요청할 때, 공통템플릿을 받아오도록 변경 (New Phonecase)
        xmlThemeCurrentDesignPage = GetParsedXml.getPhotoBookPage(Const_PRODUCT.DEFAULT_PHONE_CASE, null, null, null, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
        if (xmlThemeCurrentDesignPage != null) {
            loadComplete = true;
        }
    }

    @Override
    public List<WebViewPage> setPage() {
        String currentDesign = activity.getString(R.string.current_design);

        ArrayList<WebViewPage> pages = new ArrayList<WebViewPage>();
        pages.add(new WebViewPage(currentDesign, ""));
        return pages;
    }

    @Override
    public int getLimitViewCount() {
        return 1;
    }

    BaseThemeDesignListAdapter.CountListener countListener = count -> {
        Dlog.d("Count : " + count);
    };

    @Override
    public BaseThemeDesignListAdapter.DesignListAdapterAttribute getAttribute(int position) {
        BaseThemeDesignListAdapter.DesignListAdapterAttribute attribute = new BaseThemeDesignListAdapter.DesignListAdapterAttribute.Builder()
                .setListItems(getDesignList())
                .setMode(mode)
                .setMaxCount(1)
                .setLandScapeMode(m_isLandScapeMode)
                .setCountListener(countListener)
                .setSpanCount(m_isLandScapeMode ? 4 : 2)
                .setLayoutSpacing(m_isLandScapeMode ? 4 : 8)
                .setLayoutPadding(m_isLandScapeMode ? 12 : 8)
                .create();
        ;
        return attribute;
    }

    private List<Xml_ThemePage.ThemePage> getDesignList() {
        List<Xml_ThemePage.ThemePage> arrDesignList = new ArrayList<Xml_ThemePage.ThemePage>();
        for (Xml_ThemePage.ThemePage page : xmlThemeCurrentDesignPage.bgList) {
            if (page == null) continue;
            arrDesignList.add(page);
        }
        return arrDesignList;
    }

    @Override
    public void getIntent() {
    }
}
