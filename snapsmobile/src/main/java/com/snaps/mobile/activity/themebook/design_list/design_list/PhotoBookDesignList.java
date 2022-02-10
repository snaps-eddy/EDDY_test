package com.snaps.mobile.activity.themebook.design_list.design_list;

import android.content.Intent;

import com.snaps.common.model.WebViewPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUES;
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

/**
 * Created by kimduckwon on 2017. 11. 30..
 */

public class PhotoBookDesignList extends BaseThemeDesignList {
    private static final String TAG = PhotoBookDesignList.class.getSimpleName();
    public Xml_ThemePage xmlThemeCurrentDesignPage;
    public Xml_ThemePage xmlThemeTotalPage;
    public String mParamSide = null;
    public float mRatio = 0.0f;
    private boolean loadComplete = false;

    public PhotoBookDesignList(NewThemeDesignListActivity fragmentActivity) {
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

    @Override
    public boolean isSuccessLoadDesignList() {
        return loadComplete;
    }

    @Override
    public void loadDesignList() {
        String prmTmplClssCode = "045020";
            xmlThemeCurrentDesignPage = GetParsedXml.getPhotoBookPage(Config.getPROD_CODE(), prmTmplClssCode, Config.getTMPL_CODE(), mParamSide, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            xmlThemeTotalPage = GetParsedXml.getPhotoBookPage(Config.getPROD_CODE(), prmTmplClssCode, null, mParamSide, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
        if(xmlThemeCurrentDesignPage != null) {
            loadComplete = true;
        }
    }

    @Override
    public List<WebViewPage> setPage() {
        //KT 북 - 디자인 리스트 제한
        if (Config.isKTBook()) {
            ArrayList<WebViewPage> pages = new ArrayList<WebViewPage>();
            pages.add(new WebViewPage("", ""));
            return pages;
        }

        String currentDesign = activity.getString(R.string.current_design);
        String oneTwoPagerCount = "1~2" + activity.getString(R.string.paper_count_unit);
        String threeFourPagerCount = "3~4" + activity.getString(R.string.paper_count_unit);
        String fiveSixPagerCount = "5~6" + activity.getString(R.string.paper_count_unit);
        String sevenPagerCount = "7" + activity.getString(R.string.paper_count_unit) + activity.getString(R.string.more_than);

        ArrayList<WebViewPage> pages = new ArrayList<WebViewPage>();
        pages.add(new WebViewPage(currentDesign, ""));
        pages.add(new WebViewPage(oneTwoPagerCount, ""));
        pages.add(new WebViewPage(threeFourPagerCount, ""));
        pages.add(new WebViewPage(fiveSixPagerCount, ""));
        pages.add(new WebViewPage(sevenPagerCount, ""));
        return pages;
    }

    @Override
    public int getLimitViewCount() {
        return 5;
    }

    BaseThemeDesignListAdapter.CountListener countListener  = new BaseThemeDesignListAdapter.CountListener() {
        @Override
        public void count(int count) {
        }
    };

    @Override
    public BaseThemeDesignListAdapter.DesignListAdapterAttribute getAttribute(int position) {
        BaseThemeDesignListAdapter.DesignListAdapterAttribute attribute = new  BaseThemeDesignListAdapter.DesignListAdapterAttribute.Builder()
                .setListItems(getDesignList(getDesign(position)))
                .setMode(mode)
                .setMaxCount(1)
                .setLandScapeMode(m_isLandScapeMode)
                .setCountListener(countListener)
                .setSpanCount(m_isLandScapeMode? 3 : 2)
                .setLayoutSpacing(8)
                .setLayoutPadding(8)
                .setRatio(mRatio)
                .create();;
        return attribute;
    }

    private List<Xml_ThemePage.ThemePage> getDesignList(com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt design) {
        List<Xml_ThemePage.ThemePage> arrDesignList = new ArrayList<Xml_ThemePage.ThemePage>();

        if(!isSuccessLoadDesignList()) return arrDesignList;

        if(design == com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.CURRENT) {
            for(Xml_ThemePage.ThemePage page : xmlThemeCurrentDesignPage.bgList) {
                if(page == null) continue;
                arrDesignList.add(page);
            }
        } else {
            for(Xml_ThemePage.ThemePage page : xmlThemeTotalPage.bgList) {
                if(page == null) continue;

                int pageCnt = 0;
                try {
                    if(page.F_MASK_CNT != null && page.F_MASK_CNT.trim().length() > 0)
                        pageCnt = Integer.parseInt(page.F_MASK_CNT);
                } catch (NumberFormatException e) {
                    Dlog.e(TAG, e);
                }

                int[] values = null;
                switch (design) {
                    case PHOTO_01_OR_02:
                    case PHOTO_03_OR_04:
                    case PHOTO_05_OR_06:
                        values = com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.getValue(design);
                        if (values != null) {
                            for(int value : values) {
                                if(value == pageCnt)
                                    arrDesignList.add(page);
                            }
                        }
                        break;
                    case PHOTO_07_OR_MORE:
                        if(pageCnt >= com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.getValue(com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.PHOTO_07_OR_MORE)[0]) {
                            arrDesignList.add(page);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        return arrDesignList;
    }

    @Override
    public void putDesignListToMap() {
        if (mapDesigns == null) return;
        mapDesigns.put(com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.CURRENT, getDesignList(com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.CURRENT));

        if (isMultiPage()) {
            mapDesigns.put(com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.PHOTO_01_OR_02, getDesignList(com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.PHOTO_01_OR_02));
            mapDesigns.put(com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.PHOTO_03_OR_04, getDesignList(com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.PHOTO_03_OR_04));
            mapDesigns.put(com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.PHOTO_05_OR_06, getDesignList(com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.PHOTO_05_OR_06));
            mapDesigns.put(com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.PHOTO_07_OR_MORE, getDesignList(com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.PHOTO_07_OR_MORE));
        }
    }

    public com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt getDesign(int position) {
        com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt design = null;
        switch (position) {
            case 1:
                design = com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.PHOTO_01_OR_02;
                break;
            case 2:
                design = com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.PHOTO_03_OR_04;
                break;
            case 3:
                design = com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.PHOTO_05_OR_06;
                break;
            case 4:
                design = com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.PHOTO_07_OR_MORE;
                break;
            default:
                design = com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.eDesignPhotoCnt.CURRENT;
                break;
        }
        return design;
    }

    private List<Xml_ThemePage.ThemePage> getDesignList() {
        List<Xml_ThemePage.ThemePage> arrDesignList = new ArrayList<Xml_ThemePage.ThemePage>();
        for(Xml_ThemePage.ThemePage page : xmlThemeCurrentDesignPage.bgList) {
            if(page == null) continue;
            arrDesignList.add(page);
        }
        return arrDesignList;
    }

    @Override
    public void getIntent() {
        mRatio = activity.getIntent().getFloatExtra("pageRatio", 0.0f);
        mParamSide = activity.getIntent().getStringExtra("prmSide");
    }
}
