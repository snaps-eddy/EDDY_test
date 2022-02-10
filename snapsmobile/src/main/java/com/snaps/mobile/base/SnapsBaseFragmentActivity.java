package com.snaps.mobile.base;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.thread.ATask.OnTask;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.home.ui_strategies.HomeUIHandler;
import com.snaps.mobile.activity.photoprint.SelectPhotoPrintActivity;
import com.snaps.mobile.component.SnapsBroadcastReceiver;
import com.snaps.mobile.component.SnapsUploadStateView;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;
import com.snaps.mobile.service.SnapsPhotoUploader;

import errorhandle.CatchFragmentActivity;
import errorhandle.logger.Logg;

public class SnapsBaseFragmentActivity extends CatchFragmentActivity implements SnapsBroadcastReceiver.ImpSnapsBroadcastReceiver {
    private static final String TAG = SnapsBaseFragmentActivity.class.getSimpleName();

    public interface ISnapsUploadStateViewCreateListener {
        void onCreateUploadStateView();
    }

    SnapsUploadStateView mView = null;
    final int mStateViewID = 99999;
    // 업로드표시 하단바 마진값..
    int mUploadStateViewBottomMargin = 0;

    int mScreenHeight = 0;
    int mStatusbarHeight = 0;
    int mViewHeight = 0;

    boolean mIsCallPositionUpdate = false; // 업로드 하단 뷰 위치 변경 여부..
    SnapsBroadcastReceiver receiver = null;

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(SnapsPhotoUploader.SEND_UPLOADER_ACTION);
        receiver = new SnapsBroadcastReceiver();
        receiver.setImpRecevice(this);
        registerReceiver(receiver, filter);

        // 업로드 상태를 확인해서 하단에 진행뷰 표시..
        ATask.executeVoid(new OnTask() {

            @Override
            public void onPre() {
            }

            @Override
            public void onPost() {
                if (!SnapsPhotoUploader.getInstance(getApplicationContext()).isFinishedUpload())
                    SnapsPhotoUploader.getInstance(getApplicationContext()).requestUploadProgressInfo(); //UI Thread 접근
            }

            @Override
            public void onBG() {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Dlog.e(TAG, e);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SnapsPhotoUploader.getInstance(getApplicationContext()).isFinishedUpload()) {
            if (mView != null) {
                ViewGroup vg = ((ViewGroup) mView.getParent());
                if (vg != null)
                    vg.removeView(mView);
                mView = null;
                SnapsPhotoUploader.getInstance(getApplicationContext()).initState();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    SnapsUploadStateView makeUploadStateView() {
        SnapsUploadStateView view = new SnapsUploadStateView(this);
        view.setId(mStateViewID);

        SnapsPhotoUploader uploader = SnapsPhotoUploader.getInstance(this);
        if (uploader != null) {
            view.setProjCode(uploader.getCurrentProjCode());
        }

        return view;

    }

    /***
     * 하단 업데이트 뷰를 만드는 함수..
     */
    protected void addUploadStateView(final ISnapsUploadStateViewCreateListener uploadStateViewCreateListener) {
        ATask.executeVoid(new OnTask() {

            @Override
            public void onPre() {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPost() {

                try {
                    // 업로드 뷰를 보여야할지 말지 결정하는 함수..
                    if (!isVisibleUploadStateView())
                        return;
                    View v = findViewById(mStateViewID);

                    if (v != null)
                        return;

                    mView = makeUploadStateView();

                    float scale = getResources().getDisplayMetrics().density;
                    mViewHeight = (int) (48 * scale + 0.5f);
                    mScreenHeight = UIUtil.getScreenHeight(SnapsBaseFragmentActivity.this);

                    if (Build.MODEL.startsWith("LG-F200"))
                        mStatusbarHeight = 44;
                    else
                        mStatusbarHeight = getStatusBarHeight();

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mViewHeight);

                    ViewGroup rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                    if (rootView != null) {
                        rootView = (ViewGroup) rootView.getChildAt(0);
                        rootView.addView(mView, layoutParams);
                        mView.setY(mScreenHeight - mViewHeight - mStatusbarHeight - getUploadStatViewHOffset());
                        bottomUploadViewVisible(View.GONE);
                    }

                    if (uploadStateViewCreateListener != null) {
                        uploadStateViewCreateListener.onCreateUploadStateView();
                    }

                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }

            }

            @Override
            public void onBG() {
                // TODO Auto-generated method stub

            }
        });

    }

    protected void removeUploadStateView() {

        ATask.executeVoid(new OnTask() {

            @Override
            public void onPre() {
                if (mView != null)
                    // 일단 뷰에 업로드가 완료된걸.. 알려준다.
                    mView.mIsUpload = true;
            }

            @Override
            public void onPost() {
                if (mView != null) {

                    ViewGroup vg = ((ViewGroup) mView.getParent());
                    if (vg != null)
                        vg.removeView(mView);
                    mView = null;
                }

            }

            @Override
            public void onBG() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Dlog.e(TAG, e);
                }
            }
        });

    }

    /***
     * 스테이터스바 높이를 구하는 함수..
     *
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if ((this instanceof RenewalHomeActivity)) {

            }
        }
    }

    /***
     * 업로드 뷰를 보이거나 안보이게 하는 함수...
     *
     * @param visible
     */
    void bottomUploadViewVisible(int visible) {
        if (mView == null)
            return;

        if (visible == View.VISIBLE) {
            mView.bringToFront();
        } else if (visible == View.INVISIBLE) {

        } else if (visible == View.GONE) {

        }

        mView.setVisibility(visible);
    }

    /***
     * upload service 재시도 요청...
     */
    public void retryUploadService() {
        SnapsPhotoUploader uploader = SnapsPhotoUploader.getInstance(getApplicationContext());
        uploader.requestUploadProcess(SnapsPhotoUploader.REQUEST_UPLOAD_RETRY);
    }

    /***
     * 화면에 보여야 할지 말아야 할지 설정하는 함수...
     *
     * @return
     */
    boolean isVisibleUploadStateView() {
        // 사진 인화 화면에서는 업로드 뷰가 보이면 안된다.
        if (this instanceof RenewalHomeActivity)
            return true;
        else if (this instanceof ImageSelectActivityV2)
            return true;
        else if (this instanceof SelectPhotoPrintActivity)
            return true;
        return false;
    }

    /***
     * 화면에 따라. 업로드 뷰의 위치를 조절하기위해 필요...
     *
     * @return
     */
    int getUploadStatViewHOffset() {
        if (this instanceof RenewalHomeActivity)
            return 0;
        else if (this instanceof ImageSelectActivityV2) {
            // this.getFragmentManager().findFragmentById(id)
        }

        return 0;
    }

    /***
     * 하단 업로드 뷰가 있다면 화면에 따라 위치가 조절이 필요한 경우 호출을 한다..
     */
    protected void updateUploadViewPosition(float standard) {
        if (mView != null) {
            // 3dp
            float scale = getResources().getDisplayMetrics().density;
            int bottomMargin = (int) (3 * scale + 0.5f);

            mView.setY(standard - mViewHeight /*- mStatusbarHeight*/ - bottomMargin);
        }

    }

    @Override
    public void onReceiveData(Context context, Intent intent) {
        int cmd = intent.getIntExtra("cmd", -1);
        int state = intent.getIntExtra("state", -1);
        int complete = intent.getIntExtra("completeCount", -1);
        int total = intent.getIntExtra("totalCount", -1);
        Dlog.d("onReceiveData() cmd:" + cmd + ", state:" + state + ", complete:" + complete + ", total:" + total);

        if (cmd == 0) {
            // 업로드 뷰 추
            if (mView == null)
                addUploadStateView(null);
            switch (state) {
                case SnapsPhotoUploader.UPLOAD_READY:
                case SnapsPhotoUploader.UPLOAD_CANCEL:
                    removeUploadStateView();
                    break;
                case SnapsPhotoUploader.UPLOAD_START:
                case SnapsPhotoUploader.UPLOADING:

                    // 상태를 업로드 한다.
                    if (mView != null) {
                        mView.updateStateInfo(state, complete, total);
                        bottomUploadViewVisible(View.VISIBLE);
                    }
                    break;
                case SnapsPhotoUploader.UPLOAD_END:
                    // 100%로가 되면 업로드 상태뷰를 삭제한다.
                    if (mView != null) {
                        mView.updateStateInfo(state);
                    }

                    removeUploadStateView();

                    if (SnapsBaseFragmentActivity.this instanceof RenewalHomeActivity) {
                        RenewalHomeActivity homeActivity = (RenewalHomeActivity) SnapsBaseFragmentActivity.this;
                        HomeUIHandler homeUIHandler = homeActivity.getHomeUIHandler();
                        if (homeUIHandler != null) {
                            homeUIHandler.refreshDataAndUI();
                        }
                    }

                    break;
                case SnapsPhotoUploader.UPLOAD_ERROR:
                    break;

                default:
                    break;
            }
        } else if (cmd == 1) {
            final int errCode = intent.getIntExtra("errCode", -1);
            final String projCode = intent.getStringExtra("projCode");
            Dlog.d("onReceiveData() projCode:" + projCode + ", errCode:" + errCode);
            // 업데이트 하단 상태뷰가 없는 경우 생성을 한다.
            if (mView == null) {
                addUploadStateView(new ISnapsUploadStateViewCreateListener() {
                    @Override
                    public void onCreateUploadStateView() {
                        showUploadStateView(errCode, projCode);
                    }
                });
            } else {
                showUploadStateView(errCode, projCode);
            }
        }
    }

    private void showUploadStateView(int errCode, String projCode) {
        if (mView == null) return;

        try {
            mView.setUploadFailText();
            bottomUploadViewVisible(View.VISIBLE);
            mView.setErrorCode(errCode);
            mView.setProjCode(projCode);

            switch (errCode) {

                case SnapsPhotoUploader.UPLOAD_CART_IMAGE_NOT_FOUND_ERROR:
                case SnapsPhotoUploader.UPLOAD_ORIGIN_IMAGE_NOT_FOUND_ERROR:
                case SnapsPhotoUploader.UPLOAD_XML_CREATE_ERROR:
                case SnapsPhotoUploader.UPLOAD_XML_ERROR:
                    // 크리티컬 에러인 경우 버튼을 자세히로 바꾸고 취소를 할수 있는 다이얼 로그를 띄운다.
                    mView.setUploadFailText();
                    mView.chagneButtonTitle(R.string.photoprint_upload_btn);
                    break;
                case SnapsPhotoUploader.UPLOAD_NETWORK_ERROR:
                    // 실패 메세지 띄우기..
                    mView.setUploadFailText();
                    bottomUploadViewVisible(View.VISIBLE);

                    SnapsOrderManager.reportErrorLog("failed snaps photo print upload", SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_XML);
                    break;
                case SnapsPhotoUploader.UPLOAD_RETRY_ERROR:
                    mView.chagneButtonTitle(R.string.photoprint_upload_btn);
                    break;
                case SnapsPhotoUploader.UPLOAD_ORIGIN_IMAGE_UPLOAD_ERROR:
                    mView.chagneButtonTitle(R.string.photoprint_upload_btn);
                    int failedCount = SnapsUploadFailedImageDataCollector.getFailedImageDataCount(projCode);
                    if (failedCount > 0) {
                        mView.setInfoText(String.format(getString(R.string.photo_print_org_img_upload_failed_noti_msg), failedCount));
                    }
                    break;
                case SnapsPhotoUploader.UPLOAD_IMAGE_SIZE_INFO_ERROR:
                    mView.chagneButtonTitle(R.string.photoprint_upload_btn);
                    failedCount = SnapsUploadFailedImageDataCollector.getFailedImageDataCount(projCode);
                    if (failedCount > 0)
                        mView.setInfoText(String.format(getString(R.string.photo_print_org_img_upload_failed_noti_msg), failedCount));

                    mView.setUploadFailText();
                    bottomUploadViewVisible(View.VISIBLE);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}
