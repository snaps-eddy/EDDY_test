package com.snaps.mobile.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.SystemIntentUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.data.SnapsProductEditInfo;
import com.snaps.mobile.activity.common.interfacies.SnapsEditActExternalConnectionBridge;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditorAPI;
import com.snaps.mobile.activity.common.products.SnapsProductEditorFactory;

import java.util.ArrayList;

import errorhandle.CatchFragmentActivity;
import errorhandle.SnapsAssert;

public class SnapsEditActivity extends CatchFragmentActivity implements View.OnClickListener, SnapsEditActExternalConnectionBridge {
    private static final String TAG = SnapsEditActivity.class.getSimpleName();

    private SnapsProductEditorAPI snapsProductEditor = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createProductEditor();

        snapsProductEditor.onCreate();
    }

    private void createProductEditor() {
        snapsProductEditor = SnapsProductEditorFactory.createProductEditor(this);
        SnapsAssert.assertNotNull(snapsProductEditor);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        UIUtil.applyLanguage(this);
        super.onConfigurationChanged(newConfig);

        try {
            snapsProductEditor.onConfigurationChanged(newConfig);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(this, e);
        }
    }

    @Override
    public void onBackPressed() {
        try {
            snapsProductEditor.onBackPressed();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(this, e);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            snapsProductEditor.dispatchTouchEvent(ev);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(this, e);
        }
        // https://stackoverflow.com/questions/16459196/java-lang-illegalargumentexception-pointerindex-out-of-range-exception-dispat
        try {
            return super.dispatchTouchEvent(ev);
        }catch (IllegalArgumentException e) {
            Dlog.e(TAG, e);
            return false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            snapsProductEditor.onPause();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(this, e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            snapsProductEditor.onResume();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(this, e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            snapsProductEditor.onStop();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(this, e);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            snapsProductEditor.onDestroy();

            pageProgressUnload();

            finalizeInstance();
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(this, e);
        }

        super.onDestroy();
    }

    private void finalizeInstance() {
        snapsProductEditor = null;
    }

    @Override
    synchronized public void setPageThumbnail(final int pageIdx, String filePath) {
        SnapsAssert.assertNotNull(snapsProductEditor);
        try {
            snapsProductEditor.setPageThumbnail(pageIdx, filePath);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(this, e);
        }
    }

    synchronized public void setPageThumbnailFail(int pageIdx) {
        SnapsAssert.assertNotNull(snapsProductEditor);
        try {
            snapsProductEditor.setPageThumbnailFail(pageIdx);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(this, e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SnapsAssert.assertNotNull(snapsProductEditor);
        UIUtil.applyLanguage(this);     // preview 화면 갔다가 올때 화면이 회전되면서 리소스가 초기화 돼서 추가 적용
        try {
            snapsProductEditor.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            Dlog.e(TAG, e);
            SnapsAssert.assertException(this, e);
        }
    }

    @Override
    public void onClick(View v) {
        if (snapsProductEditor != null) snapsProductEditor.onClick(v);
    }

    @Override
    public ArrayList<MyPhotoSelectImageData> getGalleryList() {
        return getEditInfo() != null ? getEditInfo().getGalleryList() : null;
    }

    @Override
    public ArrayList<SnapsPage> getPageList() {
        return getEditInfo() != null ? getEditInfo().getPageList() : null;
    }

    @Override
    public SnapsTemplate getTemplate() {
        return getEditInfo() != null ? getEditInfo().getSnapsTemplate() : null;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public int getCanvasLoadCompleteCount() {
        return getEditInfo() != null ? getEditInfo().getLoadCompleteCount() : 0;
    }

    @Override
    public void increaseCanvasLoadCompleteCount() {
        if (getEditInfo() != null) getEditInfo().increasePageAddIndex();
    }

    @Override
    public void decreaseCanvasLoadCompleteCount() {
        if (getEditInfo() != null) getEditInfo().decreasePageAddIndex();
    }

    @Override
    public void pageProgressUnload() {
        if (snapsProductEditor != null) snapsProductEditor.pageProgressUnload();
    }

    @Override
    public void showPageProgress() {
        if (snapsProductEditor != null) snapsProductEditor.showPageProgress();
    }

    @Override
    public void setPageFileOutput(int index) {
        if (snapsProductEditor != null) snapsProductEditor.setPageFileOutput(index);
    }

    @Override
    public SnapsProductEditorAPI getProductEditorAPI() {
        return snapsProductEditor;
    }

    private SnapsProductEditInfo getEditInfo() {
        return snapsProductEditor != null ? snapsProductEditor.getEditInfo() : null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Const_VALUE.REQ_CODE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ;
                } else {
                    MessageUtil.alert(this, getString(R.string.need_to_permission_accept_for_get_phone_pictures), "", R.string.cancel, R.string.confirm_move_to_setting, false, new ICustomDialogListener() {
                        @Override
                        public void onClick(byte clickedOk) {
                            if (clickedOk == ICustomDialogListener.OK) {
                                SystemIntentUtil.showSystemSetting(SnapsEditActivity.this);
                            }
                        }
                    });
                }
                break;
        }
    }
}