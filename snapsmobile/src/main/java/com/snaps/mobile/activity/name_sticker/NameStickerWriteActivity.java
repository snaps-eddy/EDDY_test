package com.snaps.mobile.activity.name_sticker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateManager;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.ui.OrientationManager;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.SnapsEditActivity;
import com.snaps.mobile.activity.common.data.SnapsProductEditReceiveData;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.edit.spc.BabyNameStickerWriteCanvas;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.activity.themebook.SnapsTextWriteActivity;
import com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants;
import com.snaps.mobile.component.SnapsBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

import errorhandle.CatchFragmentActivity;
import font.FTextView;

import static com.snaps.mobile.activity.themebook.holder.IPhotobookCommonConstants.REQ_EDIT_TEXT;

public class NameStickerWriteActivity extends CatchFragmentActivity implements GoHomeOpserver.OnGoHomeOpserver, SnapsBroadcastReceiver.ImpSnapsBroadcastReceiver {
    private static final String TAG = NameStickerWriteActivity.class.getSimpleName();
    public static final String TEMPLATE_PATH = "/cache/template/template.xml";
    FrameLayout frameLayout;
    SnapsPageCanvas canvas;
    SnapsBroadcastReceiver receiver = null;
    int control_id = -1;
    boolean edit = false;
    boolean changeMsg = false;
    int pagePosition = 0;
    List<OriginalMsg> originalMsg;
    boolean isComplete = false;
    private FTextView topNextButton;
    private FTextView bottomNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.name_sticker_write_activity);
        getIntentData();
        init();
    }

    private void init() {
        Config.setIsBabyNameStickerEditScreen(false);
        ((FTextView) findViewById(R.id.ThemeTitleText)).setText(getString(R.string.text_input));

        topNextButton = findViewById(R.id.ThemebtnTopNext);
        topNextButton.setText(getString(R.string.next));

        bottomNextButton = findViewById(R.id.next_button);
        bottomNextButton.setVisibility(edit ? View.INVISIBLE : View.VISIBLE);

        frameLayout = findViewById(R.id.testFrameLayout);
        registerClickLayoutActionReceiver();
        GoHomeOpserver.addGoHomeListener(this);
        if (edit) {
            topNextButton.setText(getString(R.string.confirm));
            saveOriginalMsg();
            makeCanvas();
        } else {
            topNextButton.setText(getString(R.string.next));
            loadTemplate();
        }

    }

    private void getIntentData() {
        if (getIntent().hasExtra("edit")) {
            edit = getIntent().getBooleanExtra("edit", true);
            pagePosition = getIntent().getIntExtra("page", 0);
            isComplete = true;
        }
    }

    private void saveOriginalMsg() {
        originalMsg = new ArrayList<>();
        SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
        SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
        if (snapsTemplate == null) return;

        ArrayList<SnapsPage> pages = snapsTemplate.getPages();
        if (pages == null || pages.size() <= pagePosition) return;

        SnapsPage snapsPage = pages.get(pagePosition);
        if (snapsPage == null) return;

        for (SnapsControl control : snapsPage.getLayerControls()) {
            OriginalMsg original = new OriginalMsg();
            if (control instanceof SnapsTextControl) {
                original.regId = control.getControlId();
                original.originalMsg = ((SnapsTextControl) control).text;
                original.color = ((SnapsTextControl) control).format.fontColor;
                original.align = ((SnapsTextControl) control).format.align;
            }
            originalMsg.add(original);
        }
    }

    private void makeCanvas() {
        SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
        SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
        snapsTemplate.setBgClickEnable(1, true);
        canvas = new BabyNameStickerWriteCanvas(this);
        canvas.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        canvas.setGravity(Gravity.CENTER);
        canvas.setId(R.id.fragment_root_view_id);
        canvas.setEnableButton(true);
        canvas.setIsPageSaving(false);
        canvas.setZoomable(false);
        canvas.setIsPreview(false);
        canvas.setSnapsPage(snapsTemplate.getPages().get(pagePosition), 0, true, null);
        frameLayout.addView(canvas);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.ThemeTitleLeftLy) {
            onBackPressed();

        } else if (view.getId() == R.id.ThemebtnTopNext || view.getId() == R.id.next_button) {
            UIUtil.blockClickEvent(topNextButton);
            UIUtil.blockClickEvent(bottomNextButton);
            if (isComplete) {
                if (edit) {
                    if (changeMsg) {
                        makeControlCellEdit();
                    }
                    resultEditAcrivity();
                } else {
                    makeControlCell();
                    nextEditActivity();
                }
            }
        }
    }

    private void recoveryMsg() {
        SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
        SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
        SnapsPage snapsPage = snapsTemplate.getPages().get(pagePosition);
        for (int i = 0; i < snapsPage.getLayerControls().size(); i++) {
            SnapsControl control = snapsPage.getLayerControls().get(i);
            if (control instanceof SnapsTextControl) {
                OriginalMsg msg = originalMsg.get(i);
                ((SnapsTextControl) control).text = msg.originalMsg;
                ((SnapsTextControl) control).format.fontColor = msg.color;
                ((SnapsTextControl) control).format.align = msg.align;
            }
        }

    }

    private void resultEditAcrivity() {
        Intent intent = new Intent();
        intent.putExtra("position", pagePosition);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void nextEditActivity() {
        Intent intent = new Intent(this, SnapsEditActivity.class);
        intent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.BABY_NAME_STICKER.ordinal());
        intent.putExtra("templete", TEMPLATE_PATH);
        startActivity(intent);
        finish();
    }


    @Override
    public void onGoHome() {
        finish();
    }


    @Override
    public void onBackPressed() {
        if (edit && changeMsg) {
            recoveryMsg();
            makeControlCellEdit();

        }
        super.onBackPressed();
    }

    protected void loadTemplate() {

        //Network Disabled
        CNetStatus netStatus = CNetStatus.getInstance();
        if (!netStatus.isAliveNetwork(this)) {
            //템플릿 로딩 실패
            return;
        }

        ImageSelectUtils.requestGetTemplate(this, null, new ImageSelectUtils.IImageSelectUtilsInterfaceCallback() {
            @Override
            public void onPrepare() {
            }

            @Override
            public void onResult(boolean result) {
                if (result) {
                    try {
                        Config.checkServiceThumbnailSimpleFileDir();
                    } catch (Exception e) {
                        Dlog.e(TAG, e);
                    }
                    isComplete = true;
                    makeCanvas();
                }
            }
        });
    }


    private void makeControlCell() {
        SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
        SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
        if (snapsTemplate == null || snapsTemplate.getPages().get(0) == null || TextUtils.isEmpty(snapsTemplate.getPages().get(0).width) || TextUtils.isEmpty(snapsTemplate.getPages().get(0).height))
            return;

        int width = Integer.parseInt(snapsTemplate.getPages().get(0).width);
        int height = Integer.parseInt(snapsTemplate.getPages().get(0).height);
        for (SnapsPage page : snapsTemplate.getPages()) {
            SnapsControl cellControl = page.getLayerControls().get(0);
            int size = page.getLayerControls().size();
            for (int i = 1; i < size; i++) {
                SnapsControl control = page.getLayerControls().get(i);
                if (control instanceof SnapsTextControl) {
                    ((SnapsTextControl) control).isEditedText = true;
                    cellControl.getIntWidth();
                    int cellWidth = cellControl.getIntWidth();
                    int cellHeight = cellControl.getIntHeight();
                    int moveX = control.getIntX() - cellControl.getIntX();
                    int moveY = control.getIntY() - cellControl.getIntY();
                    int originalX = cellControl.getIntX();
                    int originalY = cellControl.getIntY();
                    int x = originalX;
                    int y = originalY;

                    while (true) {
                        SnapsTextControl addControl = ((SnapsTextControl) control).copyControl();
                        addControl.isEditedText = true;
                        if ((x + (cellWidth * 2)) < width) {
                            x += cellWidth;
                            addControl.setX((x + moveX) + "");
                            addControl.setY((y + moveY) + "");
                            page.getLayerControls().add(addControl);
                        } else if ((x + (cellWidth * 2)) > width && (y + (cellHeight * 2)) < (height - 45)) {
                            x = originalX;
                            y += cellHeight;
                            addControl.setX((x + moveX) + "");
                            addControl.setY((y + moveY) + "");
                            page.getLayerControls().add(addControl);
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }

    private void makeControlCellEdit() {
        SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
        SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
        if (snapsTemplate == null || snapsTemplate.getPages().get(pagePosition) == null || TextUtils.isEmpty(snapsTemplate.getPages().get(pagePosition).width) || TextUtils.isEmpty(snapsTemplate.getPages().get(pagePosition).height))
            return;

        int width = Integer.parseInt(snapsTemplate.getPages().get(pagePosition).width);
        int height = Integer.parseInt(snapsTemplate.getPages().get(pagePosition).height);

        SnapsPage page = snapsTemplate.getPages().get(pagePosition);
        SnapsControl cellControl = page.getLayerControls().get(0);
        int size = page.getLayerControls().size();
        for (int i = 1; i < size; i++) {
            SnapsControl control = page.getLayerControls().get(i);
            if (control instanceof SnapsTextControl) {
                ((SnapsTextControl) control).isEditedText = true;
                cellControl.getIntWidth();
                int cellWidth = cellControl.getIntWidth();
                int cellHeight = cellControl.getIntHeight();
                int moveX = control.getIntX() - cellControl.getIntX();
                int moveY = control.getIntY() - cellControl.getIntY();
                int originalX = cellControl.getIntX();
                int originalY = cellControl.getIntY();
                int x = originalX;
                int y = originalY;

                while (true) {
                    SnapsTextControl addControl = ((SnapsTextControl) control).copyControl();
                    addControl.isEditedText = true;
                    if ((x + (cellWidth * 2)) < width) {
                        x += cellWidth;
                        addControl.setX((x + moveX) + "");
                        addControl.setY((y + moveY) + "");
                        page.getLayerControls().add(addControl);
                    } else if ((x + (cellWidth * 2)) > width && (y + (cellHeight * 2)) < (height - 45)) {
                        x = originalX;
                        y += cellHeight;
                        addControl.setX((x + moveX) + "");
                        addControl.setY((y + moveY) + "");
                        page.getLayerControls().add(addControl);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private SnapsControl findSnapsControlWithTempImageViewId(int tempImageViewID) {
        View v = null;
        if (frameLayout != null) {
            v = frameLayout.findViewById(tempImageViewID);
        }
        return PhotobookCommonUtils.getSnapsControlFromView(v);
    }

    private void registerClickLayoutActionReceiver() {
        IntentFilter filter = new IntentFilter(Const_VALUE.CLICK_LAYOUT_ACTION);
        filter.addAction(Const_VALUE.TEXT_TO_IMAGE_ACTION);
        receiver = new SnapsBroadcastReceiver();
        receiver.setImpRecevice(this);
        registerReceiver(receiver, filter);
    }

    private void notifyTextControlFromIntentData(Intent data) {
        if (data == null) return;
        View view = (View) findViewById(control_id);
        if (view != null) {
            SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControlFromView(view);
            if (snapsControl != null && snapsControl instanceof SnapsTextControl) {
                SnapsTextControl control = (SnapsTextControl) snapsControl;
                String str = data.getStringExtra("contentText");
                control.text = str;

                if (str != null) {
                    control.setText(str);
                    int textAlignOrdinal = data.getIntExtra("snapsTextAlign", 0);
                    IPhotobookCommonConstants.SnapsTextAlign align = IPhotobookCommonConstants.SnapsTextAlign.values()[textAlignOrdinal];
                    control.format.align = align.getStr();

                    String textColor = data.getStringExtra("fontColor");
                    if (!StringUtil.isEmpty(textColor))
                        control.format.fontColor = textColor;

                    control.isEditedText = true;
                }
                canvas.changeControlLayer();
                if (edit) {
                    changeMsg = true;
                    editText();
                } else {
                    copyText();
                }

            }
        }
    }

    private void editText() {
        SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
        SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
        SnapsPage originalPage = snapsTemplate.getPages().get(pagePosition);
        SnapsControl cellControl = originalPage.getLayerControls().get(0);
        int x = cellControl.getIntX();
        int y = cellControl.getIntY();
        int width = cellControl.getIntWidth();
        int height = cellControl.getIntHeight();
        Rect rect = new Rect();
        rect.set(x, y, x + width, y + height);
        int size = originalPage.getLayerControls().size();
        ArrayList<SnapsControl> list = new ArrayList<SnapsControl>();
        list.add(cellControl);
        for (int i = 1; i < size; i++) {
            SnapsControl control = originalPage.getLayerControls().get(i);
            if (control instanceof SnapsTextControl) {
                SnapsTextControl textControl = (SnapsTextControl) control;
                Rect textRect = new Rect();
                textRect.set(textControl.getIntX(), textControl.getIntY(), textControl.getIntX() + textControl.getIntWidth(), textControl.getIntY() + textControl.getIntHeight());
                if (rect.contains(textRect)) {
                    list.add(control);
                }
            }
        }
        originalPage.getLayerControls().clear();
        originalPage.getLayerControls().addAll(list);
    }

    private void copyText() {
        SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
        SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
        SnapsPage originalPage = snapsTemplate.getPages().get(0).copyPage(0, true);
        for (int i = 1; i < snapsTemplate.getPages().size(); i++) {
            SnapsPage copyPage = snapsTemplate.getPages().get(i);
            copyPage.getLayerControls().clear();
            copyPage.getLayerControls().addAll(originalPage.getControlList());
        }
    }

    @Override
    public void onReceiveData(Context context, Intent intent) {
        try {
            if (PhotobookCommonUtils.isFromLayoutControlReceiveData(intent)) {
                boolean isLongClick = intent.getBooleanExtra("isLongClick", false);
                if (isLongClick) return;

                control_id = intent.getIntExtra("control_id", -1);
                SnapsControl control = findSnapsControlWithTempImageViewId(control_id);
                if (control == null || control_id == -1) {
                    return;
                }

                if (control instanceof SnapsTextControl) {
                    SnapsProductEditReceiveData editEvent = SnapsProductEditReceiveData.createReceiveData(intent, control);
                    if (editEvent == null) return;


                    SnapsControl snapsControl = PhotobookCommonUtils.getSnapsControl(this, control_id);
                    if (snapsControl == null || !(snapsControl instanceof SnapsTextControl))
                        return;

                    SnapsTextControl textControl = (SnapsTextControl) snapsControl;

                    String currentWrittenText = textControl.text;

                    Intent in = new Intent(this, SnapsTextWriteActivity.class);
                    Bundle bundle = new Bundle();
                    SnapsTemplateManager snapsTemplateManager = SnapsTemplateManager.getInstance();
                    SnapsTemplate snapsTemplate = snapsTemplateManager.getSnapsTemplate();
                    if (snapsTemplate != null && snapsTemplate.info != null && snapsTemplate.info.snapsTextOption != null) {
                        OrientationManager orientationManager = OrientationManager.getInstance(this);
                        snapsTemplate.info.snapsTextOption.initByTextFormat(textControl.format, orientationManager.isLandScapeMode());
                        bundle.putSerializable("snapsTextOption", snapsTemplate.info.snapsTextOption);
                    }
                    bundle.putString("written_text", currentWrittenText);
                    bundle.putBoolean("is_cover_title_edit", false);
                    in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    in.putExtras(bundle);

                    startActivityForResult(in, REQ_EDIT_TEXT);
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_EDIT_TEXT:
                notifyTextControlFromIntentData(data);
                break;
        }
    }

    private class OriginalMsg {
        int regId;
        String originalMsg;
        String color;
        String align;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
