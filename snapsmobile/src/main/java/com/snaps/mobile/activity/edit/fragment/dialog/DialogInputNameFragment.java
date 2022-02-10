package com.snaps.mobile.activity.edit.fragment.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderSaveToBasketAlertAttribute;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

/**
 * 
 * com.snaps.kakao.activity.edit.fragment DialogInputNameFragment.java
 * 
 * @author JaeMyung Park
 * @Date : 2013. 6. 11.
 * @Version :
 */
public class DialogInputNameFragment extends DialogFragment implements View.OnClickListener {
	private static final String TAG = DialogInputNameFragment.class.getSimpleName();
	private boolean isShowingDialog = false;
	private SnapsOrderSaveToBasketAlertAttribute alertAttribute = null;

	public interface IDialogInputNameClickListener {
		void onClick(boolean isOk);
		void onCanceled();
	}

	private EditText editText;
	private boolean isSave = false;

	private IDialogInputNameClickListener clickListener = null;

	/**
	 * 
	 * Instance 생성.
	 *            Dialog Mode
	 * @return
	 */
	public static DialogInputNameFragment newInstance(String value, IDialogInputNameClickListener listener) {
		DialogInputNameFragment frag = new DialogInputNameFragment();
		frag.setClickListener(listener);
		Bundle arg = new Bundle();
		arg.putString("code", value);
		frag.setArguments(arg);

		return frag;
	}

	public static DialogInputNameFragment newInstanceSave(String value, IDialogInputNameClickListener listener) {
		DialogInputNameFragment frag = new DialogInputNameFragment();
		frag.setClickListener(listener);

		Bundle arg = new Bundle();
		arg.putString("code", value);
		frag.setArguments(arg);
		frag.isSave = true;

		return frag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.TransparentProgressDialog);

		setShowingDialog(true);

		setCancelable(false);

		if (getDialog() != null)
			getDialog().setCanceledOnTouchOutside(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		setShowingDialog(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		View v = inflater.inflate(R.layout.dialog_input_name, container, false);
		
		LinearLayout lyOk = (LinearLayout) v.findViewById(R.id.button_input_name);
		LinearLayout lyCancel = (LinearLayout) v.findViewById(R.id.button_name_close);
		
		lyOk.setOnClickListener(this);
		lyCancel.setOnClickListener(this);

		setDialogTextByAlertAttribute(v);

		return v;
	}

	private void setDialogTextByAlertAttribute(View inflateView) {
		if (inflateView == null || getAlertAttribute() == null) return;

		TextView titleView = (TextView) inflateView.findViewById(R.id.input_name_title);
		TextView subTitleView = (TextView) inflateView.findViewById(R.id.custom_dialog_sub_title);
		TextView additionMainTitleView = (TextView) inflateView.findViewById(R.id.custom_dialog_addition_main_title);
		TextView additionSubTitleView = (TextView) inflateView.findViewById(R.id.custom_dialog_addition_sub_title);

		TextView cancelView = (TextView) inflateView.findViewById(R.id.dialog_input_name_cancel_btn_tv);
		TextView confirmView = (TextView) inflateView.findViewById(R.id.dialog_input_name_confirm_btn_tv);
		LinearLayout lyOk = (LinearLayout) inflateView.findViewById(R.id.button_input_name);

		if (!StringUtil.isEmpty(getAlertAttribute().getTitleText())) {
			titleView.setText(getAlertAttribute().getTitleText());
		} else if (getAlertAttribute().getTitleResId() > 0)
			titleView.setText(getAlertAttribute().getTitleResId());

		if (getAlertAttribute().getCancelBtnResId() > 0)
			cancelView.setText(getAlertAttribute().getCancelBtnResId());

		if (getAlertAttribute().getConfirmBtnResId() > 0) {
			confirmView.setText(getAlertAttribute().getConfirmBtnResId());
		} else {
			lyOk.setVisibility(View.GONE);
		}

		if (getAlertAttribute().getSubTitleResId() > 0) {
			subTitleView.setVisibility(View.VISIBLE);
			subTitleView.setText(getAlertAttribute().getSubTitleResId());
		} else {
			subTitleView.setVisibility(View.GONE);
		}

		String additionMainText = getAlertAttribute().getAdditionTitleText();
		if (!StringUtil.isEmpty(additionMainText)) {
			additionMainTitleView.setVisibility(View.VISIBLE);
			additionMainTitleView.setText(additionMainText);
		} else {
			additionMainTitleView.setVisibility(View.GONE);
		}

		String additionSubText = getAlertAttribute().getAdditionSubText();
		if (!StringUtil.isEmpty(additionSubText)) {
			additionSubTitleView.setVisibility(View.VISIBLE);
			additionSubTitleView.setText(additionSubText);
		} else {
			additionSubTitleView.setVisibility(View.GONE);
		}
	}

	public IDialogInputNameClickListener getClickListener() {
		return clickListener;
	}

	public void setClickListener(IDialogInputNameClickListener clickListener) {
		this.clickListener = clickListener;
	}

	public boolean isShowingDialog() {
		return isShowingDialog;
	}

	public void setShowingDialog(boolean showingDialog) {
		isShowingDialog = showingDialog;
	}

	public SnapsOrderSaveToBasketAlertAttribute getAlertAttribute() {
		return alertAttribute;
	}

	public void setAlertAttribute(SnapsOrderSaveToBasketAlertAttribute alertAttribute) {
		this.alertAttribute = alertAttribute;
	}

	@Override
	public void onClick(View v) {
		try {
			dismiss();

			if (getClickListener() != null) {
				if (v.getId() == R.id.button_input_name) {
					getClickListener().onClick(true);
				} else {
					getClickListener().onClick(false);
				}
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}
}
