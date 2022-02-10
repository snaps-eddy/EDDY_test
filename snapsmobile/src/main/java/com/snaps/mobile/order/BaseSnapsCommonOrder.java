package com.snaps.mobile.order;

import android.app.Activity;
import android.content.Context;

import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.mobile.activity.edit.fragment.canvas.SnapsCanvasFragment;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogConfirmFragment;
import com.snaps.mobile.activity.edit.fragment.dialog.DialogInputNameFragment;
import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderConstants;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

public abstract class BaseSnapsCommonOrder implements ISnapsOrderActionListener {

	public ISnapsOrderPrepareOptionStrategy mOrderOption; //장바구니에 담기 전에 필수 요소들을 체크 한다.
	public ISnapsOrderUploaderStrategy mOrderUploader; //실질적으로 업로드를 담당하는 모듈
	public ISnapsOrderStateListener mOrderState; //주문 상태 전달
	public ISnapsOrderUploadBridgeRequest mOrderUploadBridge;

	public DialogConfirmFragment diagConfirm;

	protected Context mContext;
	protected String mOrderCode; //주문 코드
	protected String mUserNo; //주문자 아이디
	protected String mProjectTitle; //프로젝트명
	protected SnapsCanvasFragment mCanvasFragment;

	public BaseSnapsCommonOrder(Context context,
			ISnapsOrderPrepareOptionStrategy orderOption,
			ISnapsOrderStateListener stateListener,
			String orderCode) {
		mContext = context;
		mOrderOption = orderOption;
		mOrderCode = orderCode;
		mOrderState = stateListener;
	}

	/**
	 * 장바구니에 담기 눌렀을 때, 해당 메서드를 호출해야 한다.
	 */
	final public void order(boolean saveMode) {
//		if(mOrderState != null)
//			mOrderState.onOrderStateChanged(ISnapsOrderStateListener.ORDER_STATE_START);

		if (mOrderOption != null) {
			mOrderOption.setSaveMode(saveMode);
		}

		onPrepareOrder();
	}

	/**
	 * upload 모듈이 Activity에 요청 해야 할 일이 있다면 아래 콜백을 통해 통신하자.
	 */
	final public void setOrderUploadCallback(ISnapsOrderUploadBridgeRequest callback) {
		this.mOrderUploadBridge = callback;
	}

	//주문 전 체크 요소들은 각 유형에 맞게 구현할 것.
	abstract protected void onPrepareOrder();

	public DialogInputNameFragment getInputNameDialog() {
		if (mOrderOption != null) {
			return mOrderOption.getInputNameDialog();
		}
		return null;
	}

	public DialogConfirmFragment getResultDialog() {
		return diagConfirm;
	}

	public void setUserNo(String userNo) {
		mUserNo = userNo;
	}

	public void setDummyFragment(SnapsCanvasFragment fragment) {
		mCanvasFragment = fragment;
	}

	/**
	 * Progress Popup 끝내기.
	 */
	protected void progressUnload() {
		SnapsTimerProgressView.destroyProgressView();
	}

	protected boolean isLogin(String userNo) {
		if ((userNo == null || userNo.length() < 1) && !SnapsTPAppManager.isThirdPartyApp(mContext)) { // 로그인 체크
			SnapsLoginManager.startLogInProcess(((Activity) mContext), Const_VALUES.LOGIN_P_RESULT, null, SnapsOrderConstants.LOGIN_REQUSTCODE);
			return false;
		}

		return true;
	}

	protected boolean isValidDate() {
		return mOrderOption != null && mContext != null;
	}

	public String getPJTitle() {
		return mProjectTitle;
	}

	public void setPJTitle(String title) {
		mProjectTitle = title;
	}

	public String getOrderCode() {
		return mOrderCode;
	}
}
