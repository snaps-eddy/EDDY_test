package com.snaps.mobile.component;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.thread.ATask.OnTask;
import com.snaps.mobile.R;
import com.snaps.mobile.order.order_v2.SnapsOrderManager;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;
import com.snaps.mobile.service.SnapsPhotoUploader;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import errorhandle.logger.Logg;

public class SnapsUploadDialog extends Dialog implements android.view.View.OnClickListener, SnapsBroadcastReceiver.ImpSnapsBroadcastReceiver {
	private static final String TAG = SnapsUploadDialog.class.getSimpleName();

	LayoutInflater mInflater;

	TextView mDialogCloseImg;
	TextView mDialogImg;
	TextView mDialogTxt;

	ImageView mDialogProcessImg1;
	ImageView mDialogProcessImg2;
	ImageView mDialogProcessImg3;

	ProgressBar mDialogProgressbar;
	TextView mDialogProgressCount;
	TextView mDialogProgressPercent;

	ImageView mDialogCheckImg;
	TextView mDialogProcessTxt;
	TextView mDialogProcessCompleteBtn;

	TextView mDialogProcessMessage;

	// ????????? ????????? ????????? ???????????? ??? view
	LinearLayout mProgressImgLayout;
	RelativeLayout mProgressBarLayout;
	RelativeLayout mProgressCheckLayout;

	LinearLayout mErrorRestartLayout;
	LinearLayout mErrorFailLayout;

	TextView mErrorMsgTxt1;
	TextView mErrorMsgTxt2;
	TextView mDialogRestartBtn;
	TextView mDialogCancelBtn;

	TextView mErrorFailMsgTxt;
	TextView mErrorFailBtn;

	Context mContext;

	public int mTotalCount = 360;
	public int mInCreaseCount = 0;
	public int mPercent = 0;

	private Timer mTimer;
	private Handler mTimerhandler;
	private int mValue = 0;

	boolean mIsCheck = false;
	boolean moveToCart = false;
	boolean isNewPhotoPrint = false;

	final static public String GOTO_CART = "goto_cart";

	int mState = 0;
	int mCompleteCnt = 0;
	int mTotalCnt = 0;

	String projectCode = "";

	SnapsBroadcastReceiver receiver = null;

	public SnapsUploadDialog(Context context, int state) {
		super(context);
		mContext = context;
		mState = state;
	}

	public SnapsUploadDialog(Context context, int state, int complete, int total) {
		super(context);
		mContext = context;
		mState = state;
		mCompleteCnt = complete;
		mTotalCnt = total;
	}

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public boolean isNewPhotoPrint() {
		return isNewPhotoPrint;
	}

	public void setNewPhotoPrint(boolean newPhotoPrint) {
		isNewPhotoPrint = newPhotoPrint;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.custom_upload_dialog);
		// ??????????????? ?????? ??????
		getWindow().getAttributes().windowAnimations = R.style.upload_DialogAnimation;
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		// ?????? ??????...
		mDialogCloseImg = (TextView) findViewById(R.id.upload_dialog_close_img);
		mDialogCloseImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

			}
		});

		mDialogImg = (TextView) findViewById(R.id.upload_dialog_img);
		mDialogTxt = (TextView) findViewById(R.id.upload_dialog_txt);
		mDialogTxt.setTextColor(Color.rgb(110, 78, 79));

		if (SnapsTPAppManager.isThirdPartyApp(mContext)) {
			mDialogImg.setBackgroundResource(R.drawable.img_upload_for_kidsnote);
			mDialogTxt.setTextColor(Color.rgb(72, 136, 195));
		}

		mDialogProcessImg1 = (ImageView) findViewById(R.id.upload_dialog_processing_img1);
		mDialogProcessImg2 = (ImageView) findViewById(R.id.upload_dialog_processing_img2);
		mDialogProcessImg3 = (ImageView) findViewById(R.id.upload_dialog_processing_img3);

		mDialogProgressCount = (TextView) findViewById(R.id.upload_dialog_process_count);

		mDialogProgressbar = (ProgressBar) findViewById(R.id.upload_dialog_processbar);
		mDialogProgressbar.setMax(100);

		mDialogProgressPercent = (TextView) findViewById(R.id.upload_dialog_process_percent);

		mDialogCheckImg = (ImageView) findViewById(R.id.upload_dialog_processing_check_img);
		mDialogCheckImg.setOnClickListener(this);

		mDialogProcessTxt = (TextView) findViewById(R.id.upload_dialog_processing_txt);
		mDialogProcessCompleteBtn = (TextView) findViewById(R.id.upload_dialog_processing_complete_btn);
		mDialogProcessCompleteBtn.setVisibility(View.INVISIBLE);
		mDialogProcessCompleteBtn.setOnClickListener(this);

		mDialogProcessMessage = (TextView) findViewById(R.id.upload_dialog_message);
		mDialogProcessMessage.setTextColor(Color.rgb(153, 134, 117));

		// ????????? ????????? ????????? ???????????? ??? view setting
		mProgressImgLayout = (LinearLayout) findViewById(R.id.upload_dialog_processing_img_layout);
		mProgressBarLayout = (RelativeLayout) findViewById(R.id.upload_dialog_process_out_layout);
		mProgressCheckLayout = (RelativeLayout) findViewById(R.id.upload_dialog_processing_check_layout);

		mErrorRestartLayout = (LinearLayout) findViewById(R.id.error_restart_layout);
		mErrorRestartLayout.setVisibility(View.GONE);
		mErrorMsgTxt1 = (TextView) findViewById(R.id.upload_error_msg1);
		mErrorMsgTxt1.setTextColor(Color.rgb(153, 134, 117));

		mErrorMsgTxt2 = (TextView) findViewById(R.id.upload_error_msg2);
		mErrorMsgTxt2.setTextColor(Color.rgb(153, 134, 117));

		// ???????????? ??????
		mDialogRestartBtn = (TextView) findViewById(R.id.upload_error_restart_btn);
		mDialogRestartBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SnapsPhotoUploader uploader = SnapsPhotoUploader.getInstance(mContext);
				uploader.requestUploadProcess(SnapsPhotoUploader.REQUEST_UPLOAD_RETRY);
				showNormal();
			}
		});

		// ?????? ??????
		mDialogCancelBtn = (TextView) findViewById(R.id.upload_error_cancel_btn);
		mDialogCancelBtn.setTextColor(Color.rgb(110, 78, 79));
		mDialogCancelBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				SnapsPhotoUploader uploader = SnapsPhotoUploader.getInstance(mContext);
				uploader.requestUploadProcess(SnapsPhotoUploader.REQUEST_UPLOAD_CANCEL);
				dismiss();
			}
		});

		mErrorFailLayout = (LinearLayout) findViewById(R.id.error_fail_layout);
		mErrorFailLayout.setVisibility(View.GONE);
		mErrorFailMsgTxt = (TextView) findViewById(R.id.upload_error_fail_msg);
		mErrorFailMsgTxt.setTextColor(Color.rgb(153, 134, 117));

		// ?????? ?????? ??????
		mErrorFailBtn = (TextView) findViewById(R.id.upload_error_fail_btn);
		mErrorFailBtn.setTextColor(Color.rgb(110, 78, 79));
		mErrorFailBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// ?????? ????????? ?????? ?????? ?????????????????? ????????? ????????????.... dismiss
				// ?????? ?????? ??????...
				SnapsPhotoUploader uploader = SnapsPhotoUploader.getInstance(mContext);
				uploader.requestUploadProcess(SnapsPhotoUploader.REQUEST_UPLOAD_CANCEL);
				dismiss();
			}
		});

		// ??????????????? ???????????? ????????? ??????.
		mIsCheck = Setting.getBoolean(getContext(), GOTO_CART);
		mDialogCheckImg.setImageResource(mIsCheck ? R.drawable.btn_check_on : R.drawable.btn_check_off);

		// ?????? ????????? ?????? ????????? ????????? ????????? ????????? ??????.

		switch (mState) {
			case SnapsPhotoUploader.UPLOAD_READY:
			case SnapsPhotoUploader.UPLOAD_START:
			case SnapsPhotoUploader.UPLOADING:
				// ?????? ??????...
				showNormal();
				break;
			case SnapsPhotoUploader.UPLOAD_END:
				uploadComplete();
				break;
			case SnapsPhotoUploader.UPLOAD_CANCEL:
				break;
			case SnapsPhotoUploader.UPLOAD_CART_IMAGE_NOT_FOUND_ERROR:
			case SnapsPhotoUploader.UPLOAD_ORIGIN_IMAGE_NOT_FOUND_ERROR:
			case SnapsPhotoUploader.UPLOAD_XML_CREATE_ERROR:
			case SnapsPhotoUploader.UPLOAD_XML_ERROR:
				// ???????????? ????????? ?????? ????????? ???????????? ????????? ????????? ?????? ?????? ????????? ????????? ?????????.
				showErrorFail();

				break;
			case SnapsPhotoUploader.UPLOAD_RETRY_ERROR:
			case SnapsPhotoUploader.UPLOAD_NETWORK_ERROR:
				showErrorRestart();
				progressbarUpdate(mCompleteCnt, mTotalCnt);

				SnapsOrderManager.reportErrorLog("failed snaps photo print upload", SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_XML);
			default:
				break;
		}

		Dlog.d("onCreate()");
	}

	public boolean isMoveToCart() {
		return moveToCart;
	}

	// ????????? ???????????? ??????????????? ??????????????? ????????? ????????? ???????????? ?????? ????????? ????????? ?????? ?????? ???????????? ????????? ????????????.
	// ????????? ????????? ?????? ??? 2?????? ????????? ?????? ????????? ?????????.

	/***
	 * ???????????? ????????? ?????? ??????????????? ????????? ???????????????
	 */
	public void showErrorRestart() {
		mDialogTxt.setText(String.format(mContext.getString(R.string.photoprint_notice_txt)));
		mProgressImgLayout.setVisibility(View.GONE);
		mProgressCheckLayout.setVisibility(View.GONE);
		mDialogProcessCompleteBtn.setVisibility(View.GONE);
		mErrorFailLayout.setVisibility(View.GONE);
		mErrorRestartLayout.setVisibility(View.VISIBLE);
	}

	private void showUploadFailedOrgImgPopup() {
		mDialogTxt.setText(String.format(mContext.getString(R.string.photoprint_notice_txt)));
		mProgressImgLayout.setVisibility(View.GONE);
		mProgressCheckLayout.setVisibility(View.GONE);
		mDialogProcessCompleteBtn.setVisibility(View.GONE);
		mProgressBarLayout.setVisibility(View.GONE);
		mErrorRestartLayout.setVisibility(View.GONE);
		mErrorFailLayout.setVisibility(View.GONE);

		dismiss();
	}

	/***
	 * ????????? ??? ??????????????? ????????? ??? ??????... ????????? ????????? xml??? ????????? ????????????.. ??????..
	 */
	public void showErrorFail() {
		mDialogTxt.setText(String.format(mContext.getString(R.string.photoprint_notice_txt)));
		mProgressImgLayout.setVisibility(View.GONE);
		mProgressCheckLayout.setVisibility(View.GONE);
		mDialogProcessCompleteBtn.setVisibility(View.GONE);
		mProgressBarLayout.setVisibility(View.GONE);
		mErrorRestartLayout.setVisibility(View.GONE);
		mErrorFailLayout.setVisibility(View.VISIBLE);
	}

	// ?????? ??????
	public void showNormal() {
		mDialogTxt.setText(String.format(mContext.getString(R.string.uploading)));
		mProgressImgLayout.setVisibility(View.VISIBLE);
		// mProgressCheckLayout.setVisibility(View.VISIBLE);
		mProgressBarLayout.setVisibility(View.VISIBLE);
		mErrorFailLayout.setVisibility(View.GONE);
		mErrorRestartLayout.setVisibility(View.GONE);
	}

	public void uploadComplete() {
		mTimer.cancel();
		mDialogProcessImg1.setImageResource(R.drawable.img_flow_point_off);
		mDialogProcessImg2.setImageResource(R.drawable.img_flow_point_off);
		mDialogProcessImg3.setImageResource(R.drawable.img_flow_point_off);
		mDialogTxt.setText(String.format(mContext.getString(R.string.upload_complete)));
		mDialogCheckImg.setVisibility(View.GONE);
		mDialogProcessTxt.setVisibility(View.GONE);
		mDialogProcessCompleteBtn.setVisibility(View.VISIBLE);
		// gotoCartActivity();

	}

	@Override
	public void onClick(View v) {

		// ????????? ?????? ?????? ??????...
		if (v.getId() == R.id.upload_dialog_processing_check_img) {
			mIsCheck = !mIsCheck;
			mDialogCheckImg.setImageResource(mIsCheck ? R.drawable.btn_check_on : R.drawable.btn_check_off);
			// ??????????????? ????????????.
			Setting.set(getContext(), GOTO_CART, mIsCheck);

		} else if (v.getId() == R.id.upload_dialog_processing_complete_btn) {

			goToCartList();
			dismiss();
		}
	}

	/***
	 * ???????????????????????? ????????????...
	 */
	public void goToCartList() {

		String naviBarTitle = "";
		String cartCount = Integer.toString(Setting.getInt(mContext, Const_VALUE.KEY_CART_COUNT));
		try {
			naviBarTitle = URLEncoder.encode(mContext.getString(R.string.cart), "utf-8");
		} catch (UnsupportedEncodingException e) {
			Dlog.e(TAG, e);
		}

		moveToCart = true;
		SnapsTPAppManager.gotoCartList(mContext,
				Integer.parseInt(cartCount),
				naviBarTitle,
				SnapsTPAppManager.getBaseQuary(mContext), isNewPhotoPrint());
	}

	public void ProgressIng() {
		mTimer = new Timer(true); // ?????? ???????????? ??? ??? ?????? ????????? ????????????.
		mTimerhandler = new Handler();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				mTimerhandler.post(new Runnable() {
					public void run() {
						Dlog.d("ProgressIng() timer is active : ProgressIng");
						// ????????? ????????? ?????????.
						mValue++;

						if (mValue % 3 == 0) {
							mDialogProcessImg1.setImageResource(R.drawable.img_flow_point_on);
							mDialogProcessImg2.setImageResource(R.drawable.img_flow_point_off);
							mDialogProcessImg3.setImageResource(R.drawable.img_flow_point_off);
						} else if (mValue % 3 == 1) {
							mDialogProcessImg1.setImageResource(R.drawable.img_flow_point_off);
							mDialogProcessImg2.setImageResource(R.drawable.img_flow_point_on);
							mDialogProcessImg3.setImageResource(R.drawable.img_flow_point_off);

						} else if (mValue % 3 == 2) {
							mDialogProcessImg1.setImageResource(R.drawable.img_flow_point_off);
							mDialogProcessImg2.setImageResource(R.drawable.img_flow_point_off);
							mDialogProcessImg3.setImageResource(R.drawable.img_flow_point_on);
						}
					}
				});
			}
		}, 500, 500);
	}

	public void progressbarUpdate(int completeCnt, int totalCnt) {
		int per = 0;
		float a = ((float) completeCnt / (float) totalCnt);
		per = (int) (a * 100.f);
		mDialogProgressCount.setText(makeSpannableText(Integer.toString(completeCnt) + " / " + Integer.toString(totalCnt)));
		mPercent = per;
		mDialogProgressPercent.setText(Integer.toString(per) + "%");
		mDialogProgressbar.setProgress(per);
	}

	Spannable makeSpannableText(String text) {

		// 12 / 12

		int end = text.indexOf("/");
		int start = 0;

		Spannable sText = new SpannableString(text);
		sText.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.light_red)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return sText;

	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mContext.unregisterReceiver(receiver);

	}

	@Override
	public void show() {
		super.show();

	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		ProgressIng();
		moveToCart = false;
		IntentFilter filter = new IntentFilter(SnapsPhotoUploader.SEND_UPLOADER_ACTION);
		receiver = new SnapsBroadcastReceiver();
		receiver.setImpRecevice(this);
		mContext.registerReceiver(receiver, filter);

		// ????????? ????????? ???????????? ????????? ????????? ??????..
		ATask.executeVoid(new OnTask() {

			@Override
			public void onPre() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPost() {

			}

			@Override
			public void onBG() {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					Dlog.e(TAG, e);
				}

				SnapsPhotoUploader.getInstance(mContext).requestUploadProgressInfo();

			}
		});

	}

	@Override
	public void dismiss() {
		mTimer.cancel();
		super.dismiss();
	}

	/****
	 * ????????? ????????? ????????????.. ????????? ?????? ????????? ???????????????????????? ??????..
	 */
	void gotoCartActivity() {
		ATask.executeVoid(new OnTask() {

			@Override
			public void onPre() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPost() {
				if (Setting.getBoolean(mContext, GOTO_CART)) {
					Setting.set(getContext(), GOTO_CART, false);
					goToCartList();
					dismiss();
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

	private void handleOnUploadFinish() {
		if (SnapsUploadFailedImageDataCollector.isExistFailedImageData(getProjectCode())) {
			showUploadFailedOrgImgPopup();
		} else {
			uploadComplete();
		}
	}

	@Override
	public void onReceiveData(Context context, Intent intent) {
		int cmd = intent.getIntExtra("cmd", -1);

		int state = intent.getIntExtra("state", -1);
		int complete = intent.getIntExtra("completeCount", -1);
		int total = intent.getIntExtra("totalCount", -1);

		if (cmd == 0) {
			switch (state) {
				case SnapsPhotoUploader.UPLOAD_READY:
				case SnapsPhotoUploader.UPLOAD_CANCEL:
					break;
				case SnapsPhotoUploader.UPLOAD_START:
				case SnapsPhotoUploader.UPLOADING:
					progressbarUpdate(complete, total);
					if (total > 0 && complete >= total) {
						handleOnUploadFinish();
					}
					break;
				case SnapsPhotoUploader.UPLOAD_END:
					handleOnUploadFinish();
					break;
				case SnapsPhotoUploader.UPLOAD_ERROR:

					break;

				default:
					break;
			}
		} else if (cmd == 1) {
			int errCode = intent.getIntExtra("errCode", -1);
			Dlog.w(TAG, "onReceiveData() errCode:" + errCode);
			// ???????????? ?????? ???????????? ?????? ?????? ????????? ??????.
			switch (errCode) {

				case SnapsPhotoUploader.UPLOAD_CART_IMAGE_NOT_FOUND_ERROR:
				case SnapsPhotoUploader.UPLOAD_ORIGIN_IMAGE_NOT_FOUND_ERROR:
				case SnapsPhotoUploader.UPLOAD_XML_CREATE_ERROR:
				case SnapsPhotoUploader.UPLOAD_XML_ERROR:
					// ???????????? ????????? ?????? ????????? ???????????? ????????? ????????? ?????? ?????? ????????? ????????? ?????????.
					showErrorFail();
					break;
				case SnapsPhotoUploader.UPLOAD_RETRY_ERROR:
				case SnapsPhotoUploader.UPLOAD_NETWORK_ERROR:
					// ?????? ????????? ?????????..
					showErrorRestart();

					SnapsOrderManager.reportErrorLog("failed snaps photo print upload : " + (errCode == 10 ? "networkErr" : "retryErr"), SnapsOrderConstants.eSnapsOrderType.ORDER_TYPE_UPLOAD_XML);
					break;
				case SnapsPhotoUploader.UPLOAD_ORIGIN_IMAGE_UPLOAD_ERROR:
					showUploadFailedOrgImgPopup();
					break;
				default:
					break;
			}
		}
	}

}
