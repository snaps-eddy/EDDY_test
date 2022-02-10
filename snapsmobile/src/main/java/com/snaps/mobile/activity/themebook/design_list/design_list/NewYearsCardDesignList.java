package com.snaps.mobile.activity.themebook.design_list.design_list;

import android.content.Intent;
import android.os.Bundle;

import com.snaps.common.model.NativeProductListPage;
import com.snaps.common.model.WebViewPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
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
import static com.snaps.mobile.activity.themebook.design_list.design_list.BaseThemeDesignList.SELECT_MODE.MULTI_SELECT_FIRST_ADD_DESIGN;


/**
 * Created by kimduckwon on 2017. 11. 30..
 */

public class NewYearsCardDesignList extends BaseThemeDesignList{
    private static final String JAPAN_NEW_YEARS_CARD_CLASS_CODE = "JPN0031004010000";
    private int selectMaxCount = 10;
    private int selectCount = 0;
    private ArrayList<NativeProductListPage> nativeListPages = null;
    private ArrayList<String> selectTempleteCodes = null;
    private BaseThemeDesignList.SELECT_MODE mode = MULTI_SELECT_FIRST_ADD_DESIGN;
    private boolean loadComplete = false;

    public NewYearsCardDesignList(NewThemeDesignListActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public void performNextButton() {
        if (selectCount == 0) {
            MessageUtil.toast(activity,activity.getString(R.string.theme_page_select));
        } else {
            selectTempleteCodes = getSelectTempleteCodes();
            goToEditActivity();
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
    private void goToEditActivity() {
        switch (mode) {
            case SINGLE_SELECT_CHANGE_DESIGN:
                singleselectChangeDesign();
                break;
            case MULTI_SELECT_ADD_DESIGN:
                multiselectAddDesign();
                break;
            case MULTI_SELECT_FIRST_ADD_DESIGN:
                multiselectFirstAddDesign();
                break;
        }
    }

    private void singleselectChangeDesign() {
        setResultData();
    }

    private void multiselectAddDesign() {
        setResultData();
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


    private void multiselectFirstAddDesign() {
        if(selectTempleteCodes == null) return;

        Intent intent = new Intent(activity, SnapsEditActivity.class);
        ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                .setHomeSelectProduct(Config.SELECT_SNAPS_NEW_YEARS_CARD)
                .setHomeSelectProductCode(Config.getPROD_CODE())
                .setHomeSelectKind("").create();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
        bundle.putStringArrayList(Const_EKEY.NEW_YEARS_CARD_SELECT_TEMPLETE_CODE,selectTempleteCodes);
        bundle.putInt(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.NEW_YEARS_CARD.ordinal());
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    public boolean isSuccessLoadDesignList() {
        return loadComplete;
    }

    @Override
    public void loadDesignList() {
        SnapsProductListParams productListParams = new SnapsProductListParams();
        productListParams.setClssCode(JAPAN_NEW_YEARS_CARD_CLASS_CODE);
        productListParams.setProdCode(null);
        productListParams.setProductSubList(null);
        SnapsProductNativeUIUtil.requestProductListDesignList(activity, false, productListParams, new SnapsProductNativeUIUtil.ISnapsProductNativeUIInterfaceCallback() {
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
            selectCount = count;
            if(count == 0) {
                textViewCount.setText("");
            } else {
                textViewCount.setText("( "+count+activity.getString(R.string.number)+" )");
            }

        }
    };
    @Override
    public BaseThemeDesignListAdapter.DesignListAdapterAttribute getAttribute(int position) {
        BaseThemeDesignListAdapter.DesignListAdapterAttribute attribute = new  BaseThemeDesignListAdapter.DesignListAdapterAttribute.Builder()
                .setListItems(setListItems(nativeListPages.get(0).getProductList()))
                .setMode(mode)
                .setMaxCount(selectMaxCount)
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
        if (activity.getIntent().hasExtra(Const_EKEY.NEW_YEARS_CARD_MAX_COUNT)) {
            selectMaxCount = activity.getIntent().getIntExtra(Const_EKEY.NEW_YEARS_CARD_MAX_COUNT,10);
        }
        if (activity.getIntent().hasExtra(Const_EKEY.NEW_YEARS_CARD_MODE)) {
            mode = (BaseThemeDesignList.SELECT_MODE)activity.getIntent().getSerializableExtra(Const_EKEY.NEW_YEARS_CARD_MODE);
        }
    }
}
