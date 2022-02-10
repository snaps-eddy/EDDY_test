package com.snaps.mobile.activity.intro.fragment;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.ISnapsConfigConstants;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.CNetStatus;
import com.snaps.common.utils.net.http.HttpReq;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.hamburger_menu.interfacies.ISnapsHamburgerMenuListener;
import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
import com.snaps.mobile.activity.intro.VerifyAgreementDialog;
import com.snaps.mobile.utils.pref.StringCrypto;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;

public class VerifyPhoneFragment extends Fragment implements View.OnFocusChangeListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
	private static final String TAG = VerifyPhoneFragment.class.getSimpleName();
	private static final String PARAMS_LANG_KR = "kr";
	private static final String PARAMS_LANG_EN = "en";
	private static final String PARAMS_LANG_JP = "jp";
	private static final String PARAMS_LANG_CH = "ch";

	static final String VERIFY_NUMBER_RESULT_NOTKEY="NOTKEY";
	static final String VERIFY_NUMBER_RESULT_EXISTENCENUMBER="EXISTENCENUMBER";
	static final String VERIFY_NUMBER_RESULT_DEVICE="DEVICE";
	static final String VERIFY_NUMBER_RESULT_OLDUSER="OLDUSER";
	static final String VERIFY_NUMBER_RESULT_AUTHUSER="AUTHUSER";
	static final String VERIFY_NUMBER_RESULT_TERMINATION="TERMINATION";
	static final String VERIFY_NUMBER_RESULT_EXISTENCEUSER="EXISTENCEUSER";
	static final String VERIFY_NUMBER_RESULT_SUCCESS="SUCCESS";

	private ISnapsHamburgerMenuListener menuClickListener = null;

	private EditText inputPhoneNumber, inputVerifyNumber;

	private CheckBox privacyInfoCheckBtn;

	private font.FTextView confirmBtn, textAgreement, editPhoneNumber, notMacthNumber ;

	private LinearLayout requestVerifyNumberBtn;

	LinearLayout topLayout, noVerifyLayout, completeLayout, editBtn;

	ImageView eventImage, backBtn, closeBtn;

	TextView titleText;

	View emptyView;

	String userNo;

	View couponSelectLayout, couponSelectLayout01, couponSelectLayout02, couponSelectLayout03;
	TextView tvCouponTitle, tvCouponName01,tvCouponName02,tvCouponName03;
	ImageView checkBoxCoupon01,checkBoxCoupon02,checkBoxCoupon03;

	String couponCode;

	boolean sending=false;

	boolean isEnable=false;

	boolean isNewMember = false;

	public static VerifyPhoneFragment newInstance(ISnapsHamburgerMenuListener listener) {
		VerifyPhoneFragment fragment = new VerifyPhoneFragment();
		fragment.menuClickListener = listener;
		return fragment;
	}

	public VerifyPhoneFragment() {}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_verify_phone, container, false);
		userNo = SnapsLoginManager.getUUserNo(getContext());
		privacyInfoCheckBtn = (CheckBox) v.findViewById(R.id.privacy_info_agreement_checkbox_btn);
		privacyInfoCheckBtn.setOnCheckedChangeListener(this);

		couponSelectLayout = v.findViewById(R.id.fragment_verify_coupon_select_layout);
		couponSelectLayout01 = v.findViewById(R.id.fragment_verify_coupon_select_01_layout);
		couponSelectLayout02 = v.findViewById(R.id.fragment_verify_coupon_select_02_layout);
		couponSelectLayout03 = v.findViewById(R.id.fragment_verify_coupon_select_03_layout);
		tvCouponTitle = (TextView) v.findViewById(R.id.fragment_verify_coupon_select_title_tv);
		tvCouponName01 = (TextView) v.findViewById(R.id.fragment_verify_coupon_select_01_checkbox_title);
		tvCouponName02 = (TextView) v.findViewById(R.id.fragment_verify_coupon_select_02_checkbox_title);
		tvCouponName03 = (TextView) v.findViewById(R.id.fragment_verify_coupon_select_03_checkbox_title);
		checkBoxCoupon01 = (ImageView) v.findViewById(R.id.fragment_verify_coupon_select_01_checkbox);
		checkBoxCoupon02 = (ImageView) v.findViewById(R.id.fragment_verify_coupon_select_02_checkbox);
		checkBoxCoupon03 = (ImageView) v.findViewById(R.id.fragment_verify_coupon_select_03_checkbox);

		tvCouponName01.setOnClickListener(this);
		tvCouponName02.setOnClickListener(this);
		tvCouponName03.setOnClickListener(this);

		checkBoxCoupon01.setOnClickListener(this);
		checkBoxCoupon02.setOnClickListener(this);
		checkBoxCoupon03.setOnClickListener(this);

		confirmBtn = (font.FTextView) v.findViewById(R.id.fragment_verify_phone_confirm_btn);
		confirmBtn.setOnClickListener(this);

		inputPhoneNumber = (EditText) v.findViewById(R.id.fragment_verify_phone_phone_number_et);
		inputVerifyNumber = (EditText) v.findViewById(R.id.fragment_verify_phone_verify_number_et);

		inputPhoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
		inputPhoneNumber.addTextChangedListener(phoneNumberTextWatcher);
		inputPhoneNumber.setFilters(new InputFilter[]{filterNumber});

		inputVerifyNumber.setInputType(InputType.TYPE_CLASS_PHONE);
		inputVerifyNumber.addTextChangedListener(verifyNumberTextWatcher);
		inputVerifyNumber.setFilters(new InputFilter[]{filterNumber});

		requestVerifyNumberBtn  = (LinearLayout) v.findViewById(R.id.fragment_verify_phone_request_verify_number_btn);
		requestVerifyNumberBtn.setOnClickListener(this);

		eventImage = (ImageView)v.findViewById(R.id.fragment_verify_phone_event_img);
		topLayout = (LinearLayout)v.findViewById(R.id.fragment_verify_phone_topLayout);
		backBtn = (ImageView)v.findViewById(R.id.fragment_verify_phone_back_iv);
		backBtn.setOnClickListener(this);

		closeBtn = (ImageView)v.findViewById(R.id.fragment_verify_phone_close_iv);
		editPhoneNumber = (font.FTextView)v.findViewById(R.id.fragment_verify_phone_phone_number_text);
		titleText = (TextView)v.findViewById(R.id.fragment_verify_phone_title);
		emptyView = v.findViewById(R.id.emptyView);
		noVerifyLayout =(LinearLayout) v.findViewById(R.id.fragment_verify_no_certification);
		completeLayout=(LinearLayout) v.findViewById(R.id.fragment_verify_complete_layout);
		editBtn = (LinearLayout) v.findViewById(R.id.fragment_verify_phone_request_edit_btn);
		editBtn.setOnClickListener(this);
		textAgreement = (font.FTextView) v.findViewById(R.id.txtJoinAgreement);
		notMacthNumber = (font.FTextView)v.findViewById(R.id.fragment_verify_not_complete_);
		String agreementText = getString(R.string.certification_agree);
		String agreementUrl = "";
		agreementText = agreementText.replace(agreementText, getAHrefText(agreementText, agreementUrl));
		textAgreement.setText(Html.fromHtml(agreementText));
		textAgreement.setLinkTextColor(Color.parseColor("#999999"));
		textAgreement.setOnClickListener(this);
		setCopyCancel();
		if("Y".equals(SnapsTPAppManager.isVerify(getContext()))){
				setLayout(true);
		}
		return v;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.fragment_verify_phone_request_verify_number_btn) { //인증번호 요청
			requestVerifyNumber();
		} else if (v.getId() == R.id.fragment_verify_phone_confirm_btn) { //최종적으로 확인 버튼
			if(isEnable) {
				if (!isSelectedCoupon()) {
					MessageUtil.toast(getActivity(), R.string.select_coupon_kind);
					return;
				}
				registerUserVerifyInfo();
			}else{
				if(!privacyInfoCheckBtn.isChecked()){
					MessageUtil.alertnoTitleOneBtn(getActivity(), getString(R.string.plz_agree_terms), new ICustomDialogListener() {
						@Override
						public void onClick(byte clickedOk) {
							privacyInfoCheckBtn.requestFocus();
						}
					});
				}
			}
		} else if (v.getId() == R.id.fragment_verify_phone_request_edit_btn) { //수정 버튼
			editVerityNumber();
		} else if (v.getId() == R.id.fragment_verify_phone_back_iv) { //수정 버튼
            if (menuClickListener != null)
			    menuClickListener.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_BACK);
		} else if (v.getId() == R.id.txtJoinAgreement) { //수정 버튼
			setAgreementDialog();
		} else if (v.getId() == R.id.fragment_verify_coupon_select_01_checkbox_title || v.getId() == R.id.fragment_verify_coupon_select_01_checkbox) {
			checkCouponBtnEnableState(1);
		} else if (v.getId() == R.id.fragment_verify_coupon_select_02_checkbox_title || v.getId() == R.id.fragment_verify_coupon_select_02_checkbox) {
			checkCouponBtnEnableState(2);
		}  else if (v.getId() == R.id.fragment_verify_coupon_select_03_checkbox_title || v.getId() == R.id.fragment_verify_coupon_select_03_checkbox) {
			checkCouponBtnEnableState(3);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == null) return;

		if (buttonView.getId() == R.id.privacy_info_agreement_checkbox_btn) {
			checkConfirmBtnEnableState();
		}
	}

	private boolean isSelectedCoupon() {
		if (isNewMember) {
			return !StringUtil.isEmpty(couponCode);
		} else {
			couponCode = "";
			return true;
		}
	}

	private void setAgreementDialog(){
		VerifyAgreementDialog dialog = new VerifyAgreementDialog(getContext());
		dialog.show();
	}

	private void setLayout(boolean value){
		if(noVerifyLayout == null ||completeLayout == null || editPhoneNumber == null) return;

		if(value){
			noVerifyLayout.setVisibility(View.GONE);
			completeLayout.setVisibility(View.VISIBLE);
			editPhoneNumber.setText(SnapsTPAppManager.getVerifyPhoneNumber(getContext()));

		}else{
			noVerifyLayout.setVisibility(View.VISIBLE);
			completeLayout.setVisibility(View.GONE);
		}
	}

	private void setCopyCancel(){
		if(inputPhoneNumber == null || inputVerifyNumber == null) return;
		inputPhoneNumber.setLongClickable(false);
		inputVerifyNumber.setLongClickable(false);
	}

	public void completedVerify() {
		if(inputPhoneNumber==null) return;
		Setting.set(getActivity(), Const_VALUE.KEY_USER_AUTH, "Y");
		Setting.set(getActivity(), Const_VALUE.KEY_USER_PHONENUMBER,inputPhoneNumber.getText().toString());
		if (menuClickListener != null) {
            menuClickListener.onHamburgerMenuPostMsg(ISnapsHamburgerMenuListener.MSG_MOVE_TO_COMPLETED_VERIFY);
		}
	}

	private void registerUserVerifyInfo() {
		ATask.executeVoidWithThreadPoolBooleanDefProgress(getActivity(), new ATask.OnTaskResult() {
			String resultMsg="";
			@Override
			public void onPre() {}

			@Override
			public boolean onBG() {
				String params = StringCrypto.convertStrToAES(getUserId(), getRemovedHyphenPhoneNumberText(), getVerifyNumberText(), getDeviceId(), couponCode);
				resultMsg = HttpReq.regVerifyNumber(params, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
				return !StringUtil.isSnapsServerError(resultMsg);
			}

			@Override
			public void onPost(boolean result) {
				if (result) {
					parserJson(resultMsg);
				} else {
					MessageUtil.toast(getActivity(), R.string.failed_server_state_error_plz_retry);
				}
			}
		});
	}

	private void parserJson(String resultMsg){
		String result="";
		String msg="";
		String changeMsg="";
		try {
			JSONObject jsonObject = new JSONObject(resultMsg);
			result=jsonObject.getString("RESULT");
			msg=jsonObject.getString("MSG");
		}catch (Exception e){
			Dlog.e(TAG, e);
		}
		if(!TextUtils.isEmpty(resultMsg)){
			try {
				changeMsg=String.format("%s년 %s월",msg.substring(0,4),msg.substring(5,7));
			}catch (Exception e){
				Dlog.e(TAG, e);
			}

		}
		setVerifyNumberResultType(result,changeMsg);
	}

	private void editVerityNumber(){
		setLayout(false);
		if(inputPhoneNumber == null)return;
		inputPhoneNumber.setText(SnapsTPAppManager.getVerifyPhoneNumber(getContext()));
	}

	private void requestVerifyNumber() {

		if(!checkPhoneNumber(getRemovedHyphenPhoneNumberText())){
			MessageUtil.toast(getActivity(),getActivity().getString(R.string.is_not_valid_phone_number_length));
			return;
		}

		if (!CNetStatus.getInstance().isAliveNetwork(getActivity())) {
			MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getString(R.string.common_network_error_msg), null);
			return;
		}

		if(sending){
			MessageUtil.toast(getActivity(),getActivity().getString(R.string.certification_number_sending));
			return;
		}

		ATask.executeVoidWithThreadPoolBooleanDefProgress(getActivity(), new ATask.OnTaskResult() {

			@Override
			public void onPre() {}

			@Override
			public boolean onBG() {
				String params = StringCrypto.convertStrToAES(getUserId(), getRemovedHyphenPhoneNumberText(), getDeviceId());
				return HttpReq.regVerifySend(params, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
			}

			@Override
			public void onPost(boolean result) {
				if (result) {
					MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getString(R.string.success_send_verify_number),null);
					changeRequestBtnTextToRetryText();
					setTimer();
				} else {
					MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getString(R.string.is_not_valid_phone_number_length), null);
				}
			}
		});
	}

	public void setVerifyNumberResultType(final String type,final String msg) {
		setVerifyNumberResultType(type);
	}
	public void setVerifyNumberResultType(final String type){
		switch (type){
			case VERIFY_NUMBER_RESULT_NOTKEY:
				MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getString(R.string.is_not_valid_verify_number), null);
				break;
			case VERIFY_NUMBER_RESULT_EXISTENCENUMBER:
				MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getString(R.string.certification_existencenumber), null);
				break;
			case VERIFY_NUMBER_RESULT_DEVICE:
			case VERIFY_NUMBER_RESULT_OLDUSER:
			case VERIFY_NUMBER_RESULT_AUTHUSER:
			case VERIFY_NUMBER_RESULT_TERMINATION :
			case VERIFY_NUMBER_RESULT_EXISTENCEUSER:
				MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getString(R.string.certification_number_complete), new ICustomDialogListener() {
					@Override
					public void onClick(byte clickedOk) {
						completedVerify();
					}
				});
				break;
			case VERIFY_NUMBER_RESULT_SUCCESS:
				MessageUtil.alertnoTitleOneBtn(getActivity(), getActivity().getString(R.string.certification_push_coupon), new ICustomDialogListener() {
					@Override
					public void onClick(byte clickedOk) {
						completedVerify();
					}
				});

				break;
		}
	}

	private void setTimer(){
		if(!sending) {
			sending = true;
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					sending = false;
				}
			}, 5000);
		}
	}

	private void changeRequestBtnTextToRetryText() {
		if (requestVerifyNumberBtn == null) return;
	}

	private String getRemovedHyphenPhoneNumberText() {
		if (inputPhoneNumber == null) return null;
		return inputPhoneNumber.getText().toString().replace("-", "");
	}

	private boolean isValidPhoneNumberLength() {
		String removedHyphenPhoneNumber = getRemovedHyphenPhoneNumberText();
		return !StringUtil.isEmpty(removedHyphenPhoneNumber) && removedHyphenPhoneNumber.length() == 11;
	}

	private String getVerifyNumberText() {
		if (inputVerifyNumber == null) return null;
		return inputVerifyNumber.getText().toString();
	}

	private void checkConfirmBtnEnableState() {
		if (confirmBtn == null) return;
		isEnable = !StringUtil.isEmpty(getVerifyNumberText()) && isValidPhoneNumberLength() && privacyInfoCheckBtn.isChecked();
		confirmBtn.setBackgroundResource(isEnable ? R.drawable.selector_red_btn : R.drawable.selector_black_btn);
	}

	private void checkCouponBtnEnableState(int index) {
		switch (index) {
			case 1:
				checkBoxCoupon01.setImageResource(R.drawable.btn_push_check_on);
				checkBoxCoupon02.setImageResource(R.drawable.btn_push_check_off);
				checkBoxCoupon03.setImageResource(R.drawable.btn_push_check_off);
				couponCode = (String) checkBoxCoupon01.getTag();
				break;
			case 2:
				checkBoxCoupon01.setImageResource(R.drawable.btn_push_check_off);
				checkBoxCoupon02.setImageResource(R.drawable.btn_push_check_on);
				checkBoxCoupon03.setImageResource(R.drawable.btn_push_check_off);
				couponCode = (String) checkBoxCoupon02.getTag();
				break;
			case 3:
				checkBoxCoupon01.setImageResource(R.drawable.btn_push_check_off);
				checkBoxCoupon02.setImageResource(R.drawable.btn_push_check_off);
				checkBoxCoupon03.setImageResource(R.drawable.btn_push_check_on);
				couponCode = (String) checkBoxCoupon03.getTag();
				break;
			default:
				checkBoxCoupon01.setImageResource(R.drawable.btn_push_check_off);
				checkBoxCoupon02.setImageResource(R.drawable.btn_push_check_off);
				checkBoxCoupon03.setImageResource(R.drawable.btn_push_check_off);
				couponCode = null;
				break;
		}
	}

	private String getDeviceId(){
		return SystemUtil.getDeviceId(getContext());
	}

	private String getUserId(){
		return Setting.getString(getContext(), Const_VALUE.KEY_SNAPS_USER_ID, "");
	}

	private InputFilter filterNumber = new InputFilter() {
		@Override
		public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
			Pattern ps = Pattern.compile("^[0-9 | -]+$");
			if (!ps.matcher(charSequence).matches()) {
				return "";
			}
			return null;
		}
	};

	private TextWatcher verifyNumberTextWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			checkConfirmBtnEnableState();
		}
	};

	private PhoneNumberCheckWatcher phoneNumberCheckWatcher = new PhoneNumberCheckWatcher();

	private class PhoneNumberCheckWatcher extends PhoneNumberFormattingTextWatcher{
		@Override
		public synchronized void afterTextChanged(Editable s) {
			super.afterTextChanged(s);
			checkConfirmBtnEnableState();
		}
	}

	private TextWatcher phoneNumberTextWatcher = new TextWatcher() {
		private boolean isFormatting;
		private boolean deletingHyphen;
		private int hyphenStart;
		private boolean deletingBackward;
		@Override
		public void afterTextChanged(Editable text) {
			if (isFormatting)
				return;

			isFormatting = true;

			// If deleting hyphen, also delete character before or after it
			if (deletingHyphen && hyphenStart > 0) {
				if (deletingBackward) {
					if (hyphenStart - 1 < text.length()) {
						text.delete(hyphenStart - 1, hyphenStart);
					}
				} else if (hyphenStart < text.length()) {
					text.delete(hyphenStart, hyphenStart + 1);
				}
			}
			if (text.length() == 3 || text.length() == 8) {
				text.append('-');
			}
			isFormatting = false;

			checkConfirmBtnEnableState();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			if (isFormatting)
				return;

			// Make sure user is deleting one char, without a selection
			final int selStart = Selection.getSelectionStart(s);
			final int selEnd = Selection.getSelectionEnd(s);
			if (s.length() > 1 // Can delete another character
					&& count == 1 // Deleting only one character
					&& after == 0 // Deleting
					&& s.charAt(start) == '-' // a hyphen
					&& selStart == selEnd) { // no selection
				deletingHyphen = true;
				hyphenStart = start;
				// Check if the user is deleting forward or backward
				if (selStart == start + 1) {
					deletingBackward = true;
				} else {
					deletingBackward = false;
				}
			} else {
				deletingHyphen = false;
			}
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}
	};

	private String getAHrefText(String text, String url) {
		StringBuilder builder = new StringBuilder();
		builder.append("<a href=").append(url).append(">").append(text).append("</a>");
		return builder.toString();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {

	}

	private String getLangStrValue() {
		String chnCode = Config.getCHANNEL_CODE();
		if (chnCode == null) return PARAMS_LANG_KR;

		if (chnCode.equalsIgnoreCase(ISnapsConfigConstants.CHANNEL_SNAPS_GLOBAL_ENG))
			return PARAMS_LANG_EN;
		else if (chnCode.equalsIgnoreCase(ISnapsConfigConstants.CHANNEL_SNAPS_GLOBAL_JPN))
			return PARAMS_LANG_JP;
		else if (chnCode.equalsIgnoreCase(ISnapsConfigConstants.CHANNEL_SNAPS_GLOBAL_CHN))
			return PARAMS_LANG_CH;
		return PARAMS_LANG_KR;
	}

	private boolean checkPhoneNumber(String phoneNumber){
		if(!Pattern.matches("^\\s*(010|011|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$", phoneNumber))
		{
			return false;
		}
		return  true;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public void setNewMember(boolean newMember) {
		isNewMember = newMember;
	}

	public void setMenuClickListener(ISnapsHamburgerMenuListener menuClickListener) {
		this.menuClickListener = menuClickListener;
	}

	public ISnapsHamburgerMenuListener getMenuClickListener() {
		return menuClickListener;
	}
}
