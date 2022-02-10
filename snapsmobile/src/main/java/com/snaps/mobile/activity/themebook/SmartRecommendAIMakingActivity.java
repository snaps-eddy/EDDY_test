package com.snaps.mobile.activity.themebook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import errorhandle.CatchFragmentActivity;
import errorhandle.logger.Logg;
import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import font.FProgressDialog;

import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.GetParsedXml;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.common.SmartRecommendBookMainActivity;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectPhonePhotoData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectPhonePhotoFragmentData;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.google_style_image_selector.utils.PhonePhotosLoader;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderGetPROJCodeTaskImp;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderResultListener;
import com.snaps.mobile.order.order_v2.task.upload_task.handler.SnapsOrderTaskHandlerFactory;
import com.snaps.mobile.utils.smart_snaps.analysis.data.SmartSnapsAnalysisTaskState;
import com.snaps.mobile.utils.smart_snaps.analysis.interfacies.SmartSnapsAnalysisListener;
import com.snaps.mobile.utils.smart_snaps.analysis.interfacies.SmartSnapsAnalysisTaskImp;
import com.snaps.mobile.utils.smart_snaps.analysis.task.SmartRecommendBookTaskFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SmartRecommendAIMakingActivity extends CatchFragmentActivity implements ISnapsHandler, PhonePhotosLoader.IPhonePhotoLoadListener {
    private static final String TAG = SmartRecommendAIMakingActivity.class.getSimpleName();
    private Activity mActivity = null;
    private PhonePhotosLoader phonePhotosLoader = null;

    private FProgressDialog mProgressDialog;
    private String mFaceDetectionInfoStr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        showProgressDialog();
        ImageSelectPhonePhotoData phonePhotoData = getPhonePhotoData();

        if (phonePhotoData == null || !phonePhotoData.isExistPhotoPhotoData()) {   // GooglePhotoStylePhoneFragment 에서 가져옴
            final ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
            if (imageSelectManager == null) return;

            startLoadPhoneImage();
        } else {
            onFinishPhonePhotoLoad();
        }

    }

    private void startLoadPhoneImage() {
        ATask.executeVoid(new ATask.OnTask() {
            @Override
            public void onPre() {
                ImageSelectUtils.removeAllImageData();
            }

            @Override
            public void onBG() {
                ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
                if (imageSelectManager != null) {
                    imageSelectManager.createPhonePhotoDatas(mActivity, (SmartRecommendAIMakingActivity)mActivity);
                }
            }

            @Override
            public void onPost() {
            }
        });
    }

    private ImageSelectPhonePhotoData getPhonePhotoData() {
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager == null) return null;

        imageSelectManager.waitIfCreatingPhotoDataList(); //synchronized

        ImageSelectPhonePhotoFragmentData photoFragmentData = imageSelectManager.getPhonePhotoFragmentDatas();
        if (photoFragmentData != null) {
            return photoFragmentData.getPhonePhotoData();
        }
        return null;
    }

    private void getProjectCode() {

        if (Config.isValidProjCode()) {
            getFaceDetection();
        } else {
            try {
                SnapsOrderGetPROJCodeTaskImp handler = SnapsOrderTaskHandlerFactory.createGetPROJCodeTaskWithAttribute(this);
                handler.getProjectCode(new SnapsOrderResultListener() {
                    @Override
                    public void onSnapsOrderResultSucceed(Object resultMsgObj) {
                        if (Config.isValidProjCode()) {
                            getFaceDetection();
                        } else {

                        }
                    }

                    @Override
                    public void onSnapsOrderResultFailed(Object resultMsgObj, SnapsOrderConstants.eSnapsOrderType orderType) {
                        handleOnFailedGetTemplate();
                    }
                });
            } catch(Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {

        }
    }

    private void startEditActivity() {
        hideProgressDialog();
        Intent intent = new Intent(this, SmartRecommendBookMainActivity.class);
//		saveIntent.putExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, SnapsProductEditConstants.eSnapsProductKind.SMART_ANALYSIS_PHOTO_BOOK.ordinal());
//		saveIntent.putExtra("templete", TEMPLATE_PATH);
        intent.putExtra("faceInfo", mFaceDetectionInfoStr);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);

        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy() {
        ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
        if (imageSelectManager != null) {
            imageSelectManager.suspendCreatingPhonePhotoData();
        }

        super.onDestroy();
    }

    @Override
    public void onFinishPhonePhotoLoad() {
        getProjectCode();
    }

    private void getFaceDetection() {
        ATask.executeVoidDefProgress(this, new ATask.OnTask() {
            @Override
            public void onPre() {
            }

            @Override
            public void onBG() {
                mFaceDetectionInfoStr = GetParsedXml.getFaceDetection(SystemUtil.getDeviceId(mActivity), SnapsLoginManager.getUUserNo(mActivity), Config.getAI_RECOMMENDREQ(),  SnapsInterfaceLogDefaultHandler.createDefaultHandler());
            }

            @Override
            public void onPost() {
                if(mFaceDetectionInfoStr != null)
                    startEditActivity();
                else
                    handleOnFailedGetTemplate();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideProgressDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new FProgressDialog(this);
            mProgressDialog.setCancelable(false);
        }

        if (!isFinishing())
            mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    public void handleOnFailedGetTemplate() {
        hideProgressDialog();
        MessageUtil.alertnoTitleOneBtn(mActivity, mActivity.getString(R.string.kakao_book_make_err_template_download), new ICustomDialogListener() {
            @Override
            public void onClick(byte clickedOk) {
                finish();
            }
        });
    }
}
