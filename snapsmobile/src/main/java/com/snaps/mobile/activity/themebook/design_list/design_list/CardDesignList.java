package com.snaps.mobile.activity.themebook.design_list.design_list;

import android.content.Intent;
import android.os.Bundle;

import com.snaps.common.model.NativeProductListPage;
import com.snaps.common.model.WebViewPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.themebook.design_list.adapter.BaseThemeDesignListAdapter;
import com.snaps.mobile.activity.themebook.design_list.NewThemeDesignListActivity;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsProductListParams;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductDesignCategory;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductDesignItem;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductDesignList;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductSizeItem;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductSizeList;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsBaseProductListItem;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsProductGridShapeListItem;
import com.snaps.mobile.product_native_ui.ui.recoder.SnapsProductPriceListItem;
import com.snaps.mobile.product_native_ui.util.SnapsProductNativeUIUtil;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.SELECT_MODE.SINGLE_SELECT_CHANGE_DESIGN;

/**
 * Created by kimduckwon on 2017. 12. 1..
 */

public class CardDesignList extends BaseThemeDesignList{
    private static final String CARD_CLASS_CODE = "004000000";
    private boolean loadComplete = false;
    private int selectCount = 0;
    private ArrayList<NativeProductListPage> nativeListPages = null;
    private ArrayList<String> selectTempleteCodes = null;
    public CardDesignList(NewThemeDesignListActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void performNextButton() {
        if (selectCount == 0) {
            MessageUtil.toast(activity,activity.getString(R.string.theme_page_select));
        } else {
            selectTempleteCodes = getSelectTempleteCodes();
            setResultData();
        }
    }

    private ArrayList<String> getSelectTempleteCodes() {
        List<SnapsBaseProductListItem> listItems = getSelectData();
        ArrayList<String> list = new ArrayList<String>();
        if(listItems == null || listItems.size() == 0) return null;

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

    private void setResultData() {
        if(selectTempleteCodes == null) return;

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Const_EKEY.NEW_YEARS_CARD_SELECT_TEMPLETE_CODE,selectTempleteCodes);
        bundle.putInt(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.NEW_YEARS_CARD.ordinal());
        intent.putExtras(bundle);
        activity.setResult(RESULT_OK,intent);
        activity.finish();
    }

    @Override
    public boolean isSuccessLoadDesignList() {
        return loadComplete;
    }

    @Override
    public void loadDesignList() {
        SnapsProductListParams productListParams = new SnapsProductListParams();
        productListParams.setClssCode(Config.getCHANNEL_CODE()+CARD_CLASS_CODE);
        productListParams.setProdCode(Config.getPROD_CODE());
        productListParams.setProductSubList(null);
        SnapsProductNativeUIUtil.requestCardProductListDesignList(activity, productListParams, new SnapsProductNativeUIUtil.ISnapsProductNativeUIInterfaceCallback() {
            @Override
            public void onNativeProductInfoInterfaceResult(boolean result, SnapsProductNativeUIBaseResultJson resultObj) {
                loadComplete = result;
                if (result) {
                    initProductList(resultObj);
                } else {

                }

            }
        });
    }

    private void initProductList(SnapsProductNativeUIBaseResultJson jsonObj) {
        if (jsonObj == null ) {
            activity.finish();
            return;
        }
        nativeListPages = new ArrayList<NativeProductListPage>();
        NativeProductListPage listPage;
        if (jsonObj instanceof SnapsProductDesignList) {
            SnapsProductDesignList productList = (SnapsProductDesignList) jsonObj;
            List<SnapsProductDesignCategory> categories = productList.getProductList();
            if (categories != null) {
                for (SnapsProductDesignCategory category : categories) {
                    if (category == null) continue;

                    listPage = new NativeProductListPage(category);
                    listPage.setTitle(category.getCATEGORY_NAME());
                    listPage.setIsBadgeExist(category.isNEW());

                    nativeListPages.add(listPage);
                }
            }
        } else if (jsonObj instanceof SnapsProductSizeList) {
            SnapsProductSizeList sizeList = (SnapsProductSizeList) jsonObj;
            listPage = new NativeProductListPage(sizeList);
            listPage.setTitle("");
            nativeListPages.add(listPage);
        }
    }

    @Override
    public List<WebViewPage> setPage() {

        ArrayList<WebViewPage> pages = new ArrayList<WebViewPage>();
        for (NativeProductListPage category : nativeListPages) {
            pages.add(new WebViewPage(category.getTitle(), ""));
        }

        return pages;
    }

    @Override
    public int getLimitViewCount() {
        return nativeListPages.size();
    }

    BaseThemeDesignListAdapter.CountListener countListener  = new BaseThemeDesignListAdapter.CountListener() {
        @Override
        public void count(int count) {
            selectCount = count;
        }
    };

    @Override
    public BaseThemeDesignListAdapter.DesignListAdapterAttribute getAttribute(int position) {
        mode = SINGLE_SELECT_CHANGE_DESIGN;
        BaseThemeDesignListAdapter.DesignListAdapterAttribute attribute = new  BaseThemeDesignListAdapter.DesignListAdapterAttribute.Builder()
                .setListItems(setListItems(nativeListPages.get(position).getProductList()))
                .setMode(mode)
                .setMaxCount(1)
                .setLandScapeMode(m_isLandScapeMode)
                .setCountListener(countListener)
                .setSpanCount(m_isLandScapeMode? 4 : 2)
                .create();;
        return attribute;
    }

    public ArrayList<SnapsBaseProductListItem>  setListItems(SnapsProductNativeUIBaseResultJson productObject) {
        if (productObject == null) return null;


        ArrayList<SnapsBaseProductListItem> listItems = new ArrayList<>();

        //상품 디자인 리스트
        if (productObject instanceof SnapsProductDesignCategory) {
            List<SnapsProductDesignItem> items = ((SnapsProductDesignCategory) productObject).getITEMS();
            if (items != null) {
                for (SnapsProductNativeUIBaseResultJson designItem : items) {
                    if (designItem == null || !(designItem instanceof SnapsProductDesignItem)) continue;
                    SnapsProductGridShapeListItem item = new SnapsProductGridShapeListItem((SnapsProductDesignItem) designItem);
                    listItems.add(item);
                }


            }
        } else if (productObject instanceof SnapsProductSizeList) {
            List<SnapsProductSizeItem> items = ((SnapsProductSizeList) productObject).getSize();
            if (items != null) {
                for (SnapsProductNativeUIBaseResultJson sizeItem : items) {
                    if (sizeItem == null || !(sizeItem instanceof SnapsProductSizeItem)) continue;
                    SnapsProductPriceListItem item = new SnapsProductPriceListItem((SnapsProductSizeItem) sizeItem);
                    listItems.add(item);
                }
            }
        }
        return  listItems;
    }
    @Override
    public void getIntent() {

    }
}
