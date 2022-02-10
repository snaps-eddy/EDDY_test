package com.snaps.mobile.activity.diary.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.net.xml.bean.Xml_ThemePage;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.adapter.SnapsDiaryPhotoTemplateListAdapter;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryUploadOpserver;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryWriteInfo;
import com.snaps.mobile.activity.edit.view.DialogDefaultProgress;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;

import java.util.ArrayList;
import java.util.List;

import errorhandle.CatchFragmentActivity;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;


public class SnapsDiarySelectPhotoTemplateActivity extends CatchFragmentActivity implements ISnapsDiaryUploadOpserver, View.OnClickListener {
    private static final String TAG = SnapsDiarySelectPhotoTemplateActivity.class.getSimpleName();
    private static final int GRID_COLUMN_COUNT = 3;

    private List<Xml_ThemePage.ThemePage> m_arrTemplateList = null;

    private GridView gridTemplateList;
    public Xml_ThemePage mTemplate;
    private SnapsDiaryPhotoTemplateListAdapter templateGridAdapter;
    private SnapsDiaryPhotoTemplateListAdapter.DesignHolder vh;

    public DialogDefaultProgress pageProgress;

    int selectedIndex = -1; // -1선택이 되어있지 않을상태... 최소 0시작
    SnapsDiaryPhotoTemplateListAdapter.DesignHolder selectedView = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        setContentView(R.layout.snaps_diary_select_photo_template_layout);

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        dataManager.registDiaryUploadObserver(this);

        initControls();

        getTemplateList();
    }

    @Override
    public void onFinishDiaryUpload(boolean isIssuedInk, boolean isNewWrite) {
        finish();
    }

    private void initControls() {
        pageProgress = new DialogDefaultProgress(this);

        m_arrTemplateList = new ArrayList<Xml_ThemePage.ThemePage>();

        gridTemplateList = findViewById(R.id.snaps_diary_select_photo_template_grid);

        TextView themeTitle = findViewById(R.id.ThemeTitleText);
        themeTitle.setText(R.string.diary_select_photo_template_title);

        if (findViewById(R.id.ThemeTitleLeftLy) != null)
            findViewById(R.id.ThemeTitleLeftLy).setOnClickListener(this);

        TextView tvNext = findViewById(R.id.ThemebtnTopNext);
        tvNext.setText(getString(R.string.next));
        tvNext.setOnClickListener(this);
    }

    private void initGridView() {

        if (!m_arrTemplateList.isEmpty())
            m_arrTemplateList.clear();

        for (Xml_ThemePage.ThemePage page : mTemplate.bgList) {
            if (page == null) continue;
            m_arrTemplateList.add(page);
        }

        templateGridAdapter = new SnapsDiaryPhotoTemplateListAdapter(SnapsDiarySelectPhotoTemplateActivity.this);
        templateGridAdapter.setProgress(pageProgress);

        int columnWidth = UIUtil.getCalcWidth(SnapsDiarySelectPhotoTemplateActivity.this, GRID_COLUMN_COUNT, false);
        gridTemplateList.setNumColumns(GRID_COLUMN_COUNT);
        gridTemplateList.setColumnWidth(columnWidth);
        templateGridAdapter.setGridColumnWidth(false);
        gridTemplateList.setAdapter(templateGridAdapter);

        gridTemplateList.setFocusable(false);
        gridTemplateList.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        gridTemplateList.setOnItemClickListener((parent, view, position, id) -> {

            vh = (SnapsDiaryPhotoTemplateListAdapter.DesignHolder) view.getTag();

            Xml_ThemePage.ThemePage d = getTemplateItem(position);

            // 기존에 선택이 되었던걸 비선택을 한다.
            if (position != selectedIndex && selectedIndex >= 0) {
                Xml_ThemePage.ThemePage seletedData = getTemplateItem(selectedIndex);
                seletedData.F_IS_SELECT = false;
            }

            // 선택이 되어있으면 비선택
            if (d.F_IS_SELECT) {
                d.F_IS_SELECT = false;
                selectedIndex = -1;
            } else {
                d.F_IS_SELECT = true;
                selectedIndex = position;

                selectedView = vh;
            }
            gridTemplateList.setAdapter(templateGridAdapter); //일부 단말기에서 notifyDataSet가 동작을 안해서 setAdapter로 바꿈.
        });

        SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
        final SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
        if (writeInfo.getTemplateGridPosition() >= 0) {
            gridTemplateList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < 16)
                        gridTemplateList.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    else
                        gridTemplateList.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    getTemplateItem(writeInfo.getTemplateGridPosition()).F_IS_SELECT = true;
                    selectedIndex = writeInfo.getTemplateGridPosition();
                    templateGridAdapter.notifyDataSetChanged();
                }
            });

        }
    }

    private void getTemplateList() {
        ATask.executeVoidDefProgress(SnapsDiarySelectPhotoTemplateActivity.this, new ATask.OnTask() {
            final String DIARY_LAYOUT_TEMPLATE_PATH = "/cache/template/diary_layout_template.xml";

            @Override
            public void onPre() {
                String templatePrePath = Const_VALUE.PATH_PACKAGE(SnapsDiarySelectPhotoTemplateActivity.this, false);
                SnapsDiaryDataManager.getInstance().setLayoutTemplateCachePath(templatePrePath + DIARY_LAYOUT_TEMPLATE_PATH);
            }

            @Override
            public void onBG() {
                ///servlet/Command.do?part=mall.smartphotolite.SmartPhotoLiteInterface&cmd=getMultiFormList&prmProdCode=00800600070008&prmChnlCode=KOR0033&prmTmplClssCode=045002&prmPageType=page
                mTemplate = GetParsedXml.getDiaryLayoutList(Const_PRODUCT.PRODUCT_SNAPS_DIARY_SOFT, "045002", "", null, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            }

            @Override
            public void onPost() {
                if (mTemplate != null) {
                    initGridView();
                } else {
                    MessageUtil.alert(SnapsDiarySelectPhotoTemplateActivity.this, R.string.diary_list_load_retry_msg, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK) {
                                getTemplateList();
                            } else {
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (templateGridAdapter != null) {
            templateGridAdapter.destroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    Xml_ThemePage.ThemePage getSelectedPageData() {
        if (selectedIndex == -1)
            return null;
        return getTemplateItem(selectedIndex);
    }

    public Xml_ThemePage.ThemePage getTemplateItem(int pos) {
        if (m_arrTemplateList == null) return null;
        return m_arrTemplateList.get(pos);
    }

    public List<Xml_ThemePage.ThemePage> getDesignList() {
        if (m_arrTemplateList == null) return null;
        return m_arrTemplateList;
    }


    @Override
    public void onClick(View v) {
        UIUtil.blockClickEvent(v, 1000L);
        int id = v.getId();
        try {
            if (v.getId() == R.id.ThemeTitleLeftLy || v.getId() == R.id.ThemeTitleLeft) {
                onBackPressed();
            } else if (id == R.id.ThemebtnTopNext) {
                startSelectPhotoActivity();
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void startSelectPhotoActivity() {
        Xml_ThemePage.ThemePage pageData = getSelectedPageData();
        if (pageData != null) {
            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            SnapsDiaryWriteInfo writeInfo = dataManager.getWriteInfo();
            writeInfo.setTemplateGridPosition(selectedIndex);

            Config.setTMPL_CODE(pageData.F_TMPL_CODE);

            Intent ittNextStep = new Intent(this, ImageSelectActivityV2.class);
            ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
                    .setHomeSelectProduct(Config.SELECT_SNAPS_DIARY)
                    .setHomeSelectProductCode(Config.getPROD_CODE())
                    .setHomeSelectKind("")
                    .setDiaryXMLPath(pageData.F_XML_PATH).create();

            Bundle bundle = new Bundle();
            bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
            ittNextStep.putExtras(bundle);

            startActivity(ittNextStep);
        } else {
            MessageUtil.toast(getApplicationContext(), R.string.diary_select_photo_template_no_select_msg);
        }
    }
}