package com.snaps.mobile.activity.themebook;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.model.NativeProductListPage;
import com.snaps.common.structure.photoprint.GridSpacingItemDecoration;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
import com.snaps.mobile.activity.themebook.adapter.NewYearsCardAdapter;
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

import errorhandle.CatchActivity;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

import static com.snaps.mobile.activity.themebook.ThemeProductListActivity.SELECT_MODE.MULTI_SELECT_FRIST_ADD_DESIGN;

/**
 * Created by kimduckwon on 2017. 11. 9..
 */

public class ThemeProductListActivity extends CatchActivity{
    private static final String JAPAN_NEW_YEARS_CARD_CLASS_CODE = "JPN0031004010000";
    private RecyclerView recyclerView;
    private NewYearsCardAdapter adapter;
    private TextView textViewCount;
    private ArrayList<NativeProductListPage> nativeListPages = null;
    private ArrayList<String> selectTempleteCodes = null;
    private SELECT_MODE mode = MULTI_SELECT_FRIST_ADD_DESIGN;
    private int selectMaxCount = 10;
    private int selectCount = 0;
    private boolean m_isLandScapeMode = false;
    public enum SELECT_MODE{
        SINGLE_SELECT_CHANGE_DESIGN, MULTI_SELECT_ADD_DESIGN, MULTI_SELECT_FRIST_ADD_DESIGN
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        m_isLandScapeMode = UIUtil.fixCurrentOrientationAndReturnBoolLandScape(this);
        if (m_isLandScapeMode) {
            UIUtil.updateFullscreenStatus(this, true);
        } else {
            UIUtil.updateFullscreenStatus(this, false);
        }
        setContentView(R.layout.activity_theme_product);
        init();
        getData();
        loadProductTemplate();
    }

    private void init() {
        TextView textViewNextBtn = (TextView)findViewById(R.id.ThemebtnTopNext);
        textViewNextBtn.setText(getString(R.string.confirm));
        textViewNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNextBtn();
            }
        });
        RelativeLayout relativeLayoutBackBtn = (RelativeLayout)findViewById(R.id.ThemeTitleLeftLy);
        relativeLayoutBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView textViewTitle = (TextView)findViewById(R.id.ThemeTitleText);
        textViewTitle.setText(getString(R.string.select_design));
        textViewCount = (TextView)findViewById(R.id.ThemeSelectCountText);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,m_isLandScapeMode ? 4 : 2);
        int gridSpacing = UIUtil.convertDPtoPX(this, 4);
        GridSpacingItemDecoration gridSpacingItemDecoration = new GridSpacingItemDecoration(gridSpacing,gridSpacing,gridSpacing,gridSpacing,0,0);
        recyclerView.addItemDecoration(gridSpacingItemDecoration);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void getData() {
        if (getIntent().hasExtra(Const_EKEY.NEW_YEARS_CARD_MAX_COUNT)) {
            selectMaxCount = getIntent().getIntExtra(Const_EKEY.NEW_YEARS_CARD_MAX_COUNT,10);
        }
        if (getIntent().hasExtra(Const_EKEY.NEW_YEARS_CARD_MODE)) {
            mode = (SELECT_MODE)getIntent().getSerializableExtra(Const_EKEY.NEW_YEARS_CARD_MODE);
        }
    }

    private void setNextBtn() {
        if (selectCount == 0) {
            MessageUtil.toast(this,getString(R.string.theme_page_select));
        } else {
            selectTempleteCodes = adapter.getSelectData();
            goToEditActivity();
        }
    }

    private void goToEditActivity() {
        switch (mode) {
            case SINGLE_SELECT_CHANGE_DESIGN:
                singleselectChangeDesign();
                break;
            case MULTI_SELECT_ADD_DESIGN:
                multiselectAddDesign();
                break;
            case MULTI_SELECT_FRIST_ADD_DESIGN:
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
        setResult(RESULT_OK,intent);
        finish();
    }


    private void multiselectFirstAddDesign() {
        if(selectTempleteCodes == null) return;

        Intent intent = new Intent(this, SnapsEditActivity.class);
        ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                .setHomeSelectProduct(Config.SELECT_SNAPS_NEW_YEARS_CARD)
                .setHomeSelectProductCode(Config.getPROD_CODE())
                .setHomeSelectKind("").create();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
        bundle.putStringArrayList(Const_EKEY.NEW_YEARS_CARD_SELECT_TEMPLETE_CODE,selectTempleteCodes);
        bundle.putInt(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.NEW_YEARS_CARD.ordinal());
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private void loadProductTemplate() {

        SnapsProductListParams productListParams = new SnapsProductListParams();
        productListParams.setClssCode(JAPAN_NEW_YEARS_CARD_CLASS_CODE);
        productListParams.setProdCode(null);
        productListParams.setProductSubList(null);
        SnapsProductNativeUIUtil.requestProductList(this, false, productListParams, new SnapsProductNativeUIUtil.ISnapsProductNativeUIInterfaceCallback() {
            @Override
            public void onNativeProductInfoInterfaceResult(boolean result, SnapsProductNativeUIBaseResultJson resultObj) {
                if (result) {
                    initProductList(resultObj);
                } else {

                }

            }
        });
    }

    public void onClick(View v) {
        if (v.getId() == R.id.ThemeTitleLeft || v.getId() == R.id.ThemeTitleLeftLy) {// 뒤로
            finish();
        }
    }
    private void initProductList(SnapsProductNativeUIBaseResultJson jsonObj) {
        if (jsonObj == null ) {
            finish();
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

        NewYearsCardAdapter.CountListener countListener  = new NewYearsCardAdapter.CountListener() {
            @Override
            public void count(int count) {
                selectCount = count;
                if(count == 0) {
                    textViewCount.setText("");
                } else {
                    textViewCount.setText("( "+count+getString(R.string.number)+" )");
                }

            }
        };

        NewYearsCardAdapter.NewYearsCardAdapterAttribute attribute = new NewYearsCardAdapter.NewYearsCardAdapterAttribute.Builder()
                .setListItems(setListItems(nativeListPages.get(0).getProductList()))
                .setMode(mode)
                .setMaxCount(selectMaxCount)
                .setLandScapeMode(m_isLandScapeMode)
                .setCountListener(countListener)
                .create();

        adapter = new NewYearsCardAdapter(this, attribute);

        recyclerView.setAdapter(adapter);
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



}
