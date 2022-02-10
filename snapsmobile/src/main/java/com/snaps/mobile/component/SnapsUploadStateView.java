package com.snaps.mobile.component;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.thread.ATask.OnTask;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;
import com.snaps.mobile.interfaces.ImpUploadStateView;
import com.snaps.mobile.order.order_v2.datas.SnapsUploadFailedImagePopupAttribute;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImageDataCollector;
import com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle.SnapsUploadFailedImagePopup;
import com.snaps.mobile.service.SnapsPhotoUploader;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.util.List;

public class SnapsUploadStateView extends RelativeLayout implements ImpUploadStateView {

	SnapsBaseFragmentActivity mActivity;

	RelativeLayout mBottomLayout;
	ProgressBar mProgress;
	TextView mIcon;
	TextView mInfoText;
	TextView mMoreButton; // 더보기 버튼

	public boolean mIsUpload = false;

	String mProjCode = "";

	int mState = 0;
	int mCompleteCnt = 0;
	int mTotalCnt = 0;
	int mErrorCode = 0;

	public SnapsUploadStateView(Context context) {
		super(context);
		mActivity = (SnapsBaseFragmentActivity) context;
		init(context);
	}

	void init(Context context) {
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.include_bottom_bar, this, true);
		view.setOnTouchListener(mTouchListener);
		// 위젯을 찹아서 함수 구현하기..

		mProgress = (ProgressBar) view.findViewById(R.id.include_upload_progress);
		mIcon = (TextView) view.findViewById(R.id.include_notice_img);
		mInfoText = (TextView) view.findViewById(R.id.tv_infoText);
		mMoreButton = (TextView) view.findViewById(R.id.tv_more);
		mMoreButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 자세히 보기.
				if (mMoreButton.getText().equals(mActivity.getString(R.string.photoprint_upload_btn))) {

					if (isOrgImgUploadErrState()) {
						SnapsUploadFailedImagePopupAttribute popupAttribute = SnapsUploadFailedImagePopup.createPhotoPrintUploadFailedImagePopupAttribute(mActivity, getProjCode(), false);

						SnapsUploadFailedImageDataCollector.showUploadFailedOrgImageListPopup(popupAttribute, new SnapsUploadFailedImagePopup.SnapsUploadFailedImagePopupListener() {
							@Override
							public void onShowUploadFailedImagePopup() {}

							@Override
							public void onSelectedUploadFailedImage(List<MyPhotoSelectImageData> uploadFailedImageList) {
								SnapsTPAppManager.gotoCartList(mActivity, 0, mActivity.getString(R.string.cart), "");

								SnapsPhotoUploader uploader = SnapsPhotoUploader.getInstance(mActivity);
								uploader.requestUploadProcess(SnapsPhotoUploader.REQUEST_SAW_UPLOAD_FAILED_ORG_IMAGE_POPUP);
							}
						});
					} else if (!mIsUpload) {
						if (mState == SnapsPhotoUploader.UPLOAD_ERROR) {
							SnapsUploadDialog dlg = new SnapsUploadDialog(mActivity, mErrorCode, mCompleteCnt, mTotalCnt);
							dlg.setProjectCode(getProjCode());
							dlg.setNewPhotoPrint(true);
							dlg.show();
						} else {
							SnapsUploadDialog dlg = new SnapsUploadDialog(mActivity, mState);
							dlg.setProjectCode(getProjCode());
							dlg.setNewPhotoPrint(true);
							dlg.show();
						}

					} else {// 업로드가 완료가 된 경우... 토스트 완료가 되었습니다. 띄우기..
						MessageUtil.toast(mActivity, R.string.photo_upload_complete);
					}
				} else {
					// 다시시도하기...
					mActivity.retryUploadService();
				}

			}
		});

	}

	public boolean isOrgImgUploadErrState() {
		return mErrorCode == SnapsPhotoUploader.UPLOAD_ORIGIN_IMAGE_UPLOAD_ERROR;
	}

	OnTouchListener mTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return true;
		}
	};

	/***
	 * 에러코드를 저장하는 함수
	 * 
	 * @param error
	 */
	public void setErrorCode(int error) {
		mErrorCode = error;
		mState = SnapsPhotoUploader.UPLOAD_ERROR;
	}

	public void chagneButtonTitle(int resID) {
		mMoreButton.setText(resID);
	}

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

	}

	@Override
	public void showProgressbar(int visible) {
		mProgress.setVisibility(visible);
	}

	@Override
	public void showIcon(int resid) {

		mIcon.setBackgroundResource(resid);

	}

	@Override
	public void setInfoText(String text) {
		mInfoText.setText(text);

	}

	@Override
	public void setProgressStateText(float current, float total) {

	}

	@Override
	public void setButtonText(String btnText) {
		mMoreButton.setText(btnText);
	}

	@Override
	public void setVisible(int visible) {
		mBottomLayout.setVisibility(visible);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	/***
	 * 화면을 갱신하는 함수..
	 * 
	 * @param state
	 * @param completeCnt
	 * @param totalCnt
	 */
	public void updateStateInfo(final int state, final int completeCnt, final int totalCnt) {
		mState = state;
		mCompleteCnt = completeCnt;
		mTotalCnt = totalCnt;

		// 에러가 아니면 에러코드를 0으로 초기화..
		if (mState != SnapsPhotoUploader.UPLOAD_ERROR)
			mErrorCode = 0;

		ATask.executeVoid(new OnTask() {

			@Override
			public void onPre() {

			}

			@Override
			public void onPost() {

				switch (state) {
					case SnapsPhotoUploader.UPLOAD_READY :
					case SnapsPhotoUploader.UPLOAD_START :
					case SnapsPhotoUploader.UPLOADING :
					case SnapsPhotoUploader.UPLOAD_END :
						setUploadStartText();
						setUploadingText(completeCnt, totalCnt);
						break;
				}

			}

			@Override
			public void onBG() {

			}
		});
	}

	public void updateStateInfo(int state) {
		setUploadingText(mTotalCnt, mTotalCnt);
	}

	void setUploadStartText() {
		mIcon.setVisibility(View.GONE);
		mProgress.setVisibility(View.VISIBLE);
		mInfoText.setText(R.string.photoprint_start_txt);
		mMoreButton.setText(R.string.photoprint_upload_btn);
	}

	/***
	 * 인포 텍스트를 설정하는 함수...
	 * 
	 * @param completeCnt
	 * @param totalCnt
	 */
	void setUploadingText(int completeCnt, int totalCnt) {
		// 사진 업로드 중 30/500 (80%)
		mInfoText.setText(makeSpannableText(completeCnt, totalCnt));
		mMoreButton.setText(R.string.photoprint_upload_btn);
	}

	void setUploadEndText() {

	}

	/***
	 * 업로드 실패시 나오는 문구...
	 */
	public void setUploadFailText() {
		mIcon.setVisibility(View.VISIBLE);
		mProgress.setVisibility(View.GONE);
		mInfoText.setText(R.string.photoprint_notice_txt);
		mMoreButton.setText(R.string.retry);
	}

	Spannable makeSpannableText(int completeCnt, int totalCnt) {
		int percent = (int) (completeCnt / (float) totalCnt * 100.f);
		String str = mActivity.getString(R.string.uploading_photo);
		String per = "%";
		String infoText = String.format("%s  %d / %d (%d%s)", str, completeCnt, totalCnt, percent, per);

		int end = infoText.indexOf("/");
		int start = infoText.indexOf("  ");

		Spannable sText = new SpannableString(infoText);
		sText.setSpan(new ForegroundColorSpan(mActivity.getResources().getColor(R.color.light_red)), start + 1, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return sText;

	}

	public String getProjCode() {
		return mProjCode;
	}

	public void setProjCode(String mProjCode) {
		this.mProjCode = mProjCode;
	}
}
