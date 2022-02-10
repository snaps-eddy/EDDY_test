package com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers;

import android.app.Activity;
import android.content.Intent;
import android.net.UrlQuerySanitizer;

import errorhandle.logger.Logg;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;

import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.DetailProductNativeActivity;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.list.ListActivity;
import com.snaps.mobile.activity.list.ListSubActivity;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.snaps.mobile.activity.ui.menu.renewal.model.SubCategory;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.webview.PopupWebviewActivity;
import com.snaps.mobile.product_native_ui.util.SnapsProductNativeUIUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by ysjeong on 16. 8. 12..
 */
public class SnapsRenewalWebEventHandler extends SnapsWebEventBaseHandler {
    private static final String TAG = SnapsRenewalWebEventHandler.class.getSimpleName();
    private int type;

    public SnapsRenewalWebEventHandler(Activity activity, SnapsShouldOverrideUrlLoader.SnapsShouldHandleData handleDatas, int type) {
        super(activity, handleDatas);
        this.type = type;
    }

    @Override
    public boolean handleEvent() {
        UrlQuerySanitizer sanitizer;
        String url, title = "", value = "";
        HashMap<String, String> parameters;

        switch( type ) {
            case TYPE_LIST:
            case TYPE_SIZE:
            {
                if (urlData == null || urlData.isEmpty()) break;
                String clssCode = urlData.get(CLSS_CODE);
                if (StringUtil.isEmpty(clssCode))
                    clssCode = urlData.get(SUB_CATEGORY);
                if (StringUtil.isEmpty(clssCode))
                    clssCode = urlData.get(F_CLSS_CODE);

                String prodCode = urlData.get(PRODUCT_CODE);
                if (StringUtil.isEmpty(prodCode))
                    prodCode = urlData.get(F_PROD_CODE);

                String productSubList = urlData.get(PRODUCT_SUB_LIST);
                SnapsProductListParams productListParams = new SnapsProductListParams();
                productListParams.setClssCode(clssCode);
                productListParams.setProdCode(prodCode);
                productListParams.setProductSubList(productSubList);
                startProductListActivity(type, productListParams);
                return true;
            }
            case TYPE_DETAIL:
            {
                if (urlData == null || urlData.isEmpty()) break;
                SnapsProductListParams params = new SnapsProductListParams();
                String prodCode = urlData.get( F_PROD_CODE );
                String clssCode = urlData.get(CLSS_CODE);
                String tmplCode = urlData.get(TEMPLATE_CODE);
                String isPremium = urlData.get( F_OUTER_YORN );
                if( StringUtil.isEmpty(clssCode) ) clssCode = urlData.get( F_CLSS_CODE );
                if( StringUtil.isEmpty(tmplCode) ) tmplCode = urlData.get( F_TMPL_CODE );

                params.setClssCode(clssCode);
                params.setTemplateCode(tmplCode);
                if( !StringUtil.isEmpty(prodCode) ) params.setProdCode( prodCode );
                if( !StringUtil.isEmpty(isPremium) ) params.setOuter( "Y".equalsIgnoreCase(isPremium) );

                SubCategory subCategory = MenuDataManager.getInstance().getSubCategoryByF_CLSS_CODE( clssCode );
                if( subCategory == null )
                    subCategory = SnapsMenuManager.getInstance().getSubCategory();
                if (subCategory != null) {
                    params.setTitle( subCategory.getTitle() );
                    params.setInfoUrl( subCategory.getInfoUrl() );
                }
                params.setDetailInterfaceUrl( SnapsProductNativeUIUtil.getProductDetailPageUrl(params) );

                startProductDetailActivity(params);
                return true;
            }
            case TYPE_ALERT:
                if( handleDatas == null || StringUtil.isEmpty(handleDatas.getUrl()) ) break;
                parameters = parseEncodedParameters( handleDatas.getUrl() );
                title = parameters.get( "title" );
                value = parameters.get( "value" );
                if( StringUtil.isEmpty(value) ) break;

                try {
                    title = StringUtil.isEmpty( title ) ? "" : URLDecoder.decode( title, "UTF-8" );
                    value = URLDecoder.decode( value, "UTF-8" );
                } catch (UnsupportedEncodingException e) {
                    Dlog.e(TAG, e);
                }

                MessageUtil.alert( activity, title == null ? "" : title, value );
                return true;
        }
        return false;
    }

    private HashMap<String, String> parseEncodedParameters( String url ) {
        HashMap map = new HashMap<String, String>();
        if( url.contains("?") ) {
            if( url.indexOf("?") > url.length() - 2 ) return map;
            url = url.substring( url.indexOf("?") + 1, url.length() );
        }

        StringTokenizer tokenizer = new StringTokenizer(url, "&");
        while(tokenizer.hasMoreElements()) {
            String attributeValuePair = tokenizer.nextToken();
            if (attributeValuePair.length() > 0) {
                int assignmentIndex = attributeValuePair.indexOf('=');
                if ( assignmentIndex > 0 && assignmentIndex < attributeValuePair.length() - 1 )
                    map.put( attributeValuePair.substring(0, assignmentIndex), attributeValuePair.substring(assignmentIndex + 1, attributeValuePair.length()) );
            }
        }
        return map;
    }

    private void startProductListActivity(int cmdType, SnapsProductListParams listParams) {
        MenuDataManager dataManager = MenuDataManager.getInstance();
        if (dataManager == null || listParams == null) return;

        String title = null;
        if (listParams.isProductSubList()) {
            title = activity.getString(R.string.select_design);
        } else {
            SubCategory subCategory = dataManager.getSubCategoryByF_CLSS_CODE(listParams.getClssCode());
            if (subCategory != null) {
                SnapsMenuManager menuMan = SnapsMenuManager.getInstance();
                if(menuMan != null) {
                    if( activity != null )
                        SnapsWebEventBaseHandler.sendPageEventTracker( activity, SnapsAPI.DOMAIN() + subCategory.getNextPageUrl() );
                    menuMan.setSubCategory(subCategory);
                }
            }

            if (subCategory == null || subCategory.getTitle() == null || subCategory.getTitle().length() < 1)
                title = activity.getString(R.string.select_design);
        }

        boolean isSizeType = cmdType == TYPE_SIZE;

        Intent itt = null;
        if (listParams.isProductSubList())
            itt = ListSubActivity.getIntent(activity, title, isSizeType, listParams);
        else
            itt = ListActivity.getIntent(activity, title, isSizeType, listParams);

        activity.startActivity(itt);

        if(activity != null && (activity instanceof PopupWebviewActivity) ) activity.finish();
    }

    private void startProductDetailActivity(SnapsProductListParams listParams) {
        MenuDataManager dataManager = MenuDataManager.getInstance();
        if (dataManager == null || listParams == null) return;

        String titleStr = listParams.getTitle();

        if (titleStr == null || titleStr.length() < 1)
            titleStr = activity.getString(R.string.select_design);

        SubCategory subCategory = dataManager.getSubCategoryByF_CLSS_CODE(listParams.getClssCode());
        if (subCategory != null) {
            SnapsMenuManager menuManager = SnapsMenuManager.getInstance();
            menuManager.setSubCategory(subCategory);
        }

        activity.startActivity(DetailProductNativeActivity.getIntent(activity, listParams));

        if(activity != null && (activity instanceof PopupWebviewActivity) ) activity.finish();
    }

    @Override
    public void printClassName() {
        Dlog.d("printClassName() className:" + getClass().getName());
    }

}
