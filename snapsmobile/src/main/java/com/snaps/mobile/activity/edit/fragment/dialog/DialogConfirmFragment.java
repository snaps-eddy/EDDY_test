package com.snaps.mobile.activity.edit.fragment.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

/**
 * 
 * com.snaps.kakao.activity.edit.fragment DialogConfirmFragment.java
 * 
 * @author ParkJaeMyung
 * @Date : 2013. 5. 25.
 * @Version :
 */
public class DialogConfirmFragment extends DialogFragment implements View.OnClickListener {
	private static final String TAG = DialogConfirmFragment.class.getSimpleName();
	public interface IDialogConfirmClickListener {
		void onClick(boolean isOk);
		void onCanceled();
	}

	public static final String DIALOG_TYPE_SAVE_COMPLETE = "DialogSaveComplete";
	public static final String DIALOG_TYPE_ORDER_COMPLETE = "DialogOrderComplete";
	public static final String DIALOG_TYPE_CAPTURE_AGAIN = "DialogCaptureAgain";
	public static final String DIALOG_TYPE_ACCESSORY_SAVE_COMPLETE = "DialogAccessorySaveComplete";
	public static final String DIALOG_TYPE_ACCESSORY_SAVE_FAIL = "DialogAccessorySaveFail";
	public static final String DIALOG_TYPE_PRODUCT_MATCH_FAIL = "DialogProductMatchFail";
	public static final String DIALOG_TYPE_SAVE = "DialogSave";
	boolean isSaveMode = false;

	private IDialogConfirmClickListener clickListener = null;

	/**
	 * 
	 * Instance 생성.
	 * 
	 * @param mode
	 *            Dialog Mode
	 * @return
	 */
	public static DialogConfirmFragment newInstance(String mode, IDialogConfirmClickListener confirmClickListener) {
		DialogConfirmFragment frag = new DialogConfirmFragment();
		Bundle bundle = new Bundle();
		bundle.putString("popup_type", mode);
		frag.setArguments(bundle);
		frag.setClickListener(confirmClickListener);
		return frag;
	}

	public IDialogConfirmClickListener getClickListener() {
		return clickListener;
	}

	public void setClickListener(IDialogConfirmClickListener clickListener) {
		this.clickListener = clickListener;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.TransparentProgressDialog);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		View v = inflater.inflate(R.layout.dialog_confirm_fragment, container, false);

		font.FTextView title = (font.FTextView) v.findViewById(R.id.dialog_title);

		font.FTextView confim = (font.FTextView) v.findViewById(R.id.btn_confim);
		confim.setOnClickListener(this);
		
		font.FTextView cancel = (font.FTextView) v.findViewById(R.id.btn_cancel);
		cancel.setOnClickListener(this);

		LinearLayout cancelLy = (LinearLayout)v.findViewById(R.id.btn_cancel_ly);
		cancelLy.setVisibility(View.GONE);

		String type = getArguments().getString("popup_type");

		if (type.equalsIgnoreCase(DIALOG_TYPE_SAVE_COMPLETE)) {
			// 저장 완료 팝업.
			title.setText(getResources().getString(R.string.dialog_title_save_complete));
			confim.setText(getResources().getString(R.string.dialog_button_confim_save_complete));
		
		} else if (type.equalsIgnoreCase(DIALOG_TYPE_ORDER_COMPLETE)) {
			// 주문 완료 팝업.
			Dlog.d(Dlog.UI_MACRO, "DIALOG_GO_CART");
			title.setText(getResources().getString(R.string.dialog_title_order_complete));
			confim.setText(getResources().getString(R.string.go_to_cart));
			cancel.setText(getResources().getString(R.string.photoprint_edit_contiue));
			cancelLy.setVisibility(View.VISIBLE);
			this.setCancelable(true);
			
		} else if (type.equalsIgnoreCase(DIALOG_TYPE_CAPTURE_AGAIN)) {
			// 다시 저장 팝업.
			title.setText(getResources().getString(R.string.dialog_title_capture_again));
			confim.setText(getResources().getString(R.string.do_save));
			this.setCancelable(false);
		} else if (type.equalsIgnoreCase(DIALOG_TYPE_SAVE)) {
			String msg = "";
			if (Config.isThemeBook() || Config.isSimplePhotoBook() || Config.isSimpleMakingBook() )
				msg = String.format(getResources().getString(R.string.dialog_title_save), getString(R.string.photo_book));
			else if (Const_PRODUCT.isFrameProduct() || Const_PRODUCT.isSinglePageProduct() || Const_PRODUCT.isPackageProduct() || Const_PRODUCT.isCardProduct()) {
				msg = getResources().getString(R.string.dialog_title_save);
			}

			title.setText(msg);
			confim.setText(getString(R.string.confirm));
			isSaveMode = true;
		} else if(type.equalsIgnoreCase(DIALOG_TYPE_ACCESSORY_SAVE_COMPLETE)) {
			title.setText(getResources().getString(R.string.dialog_title_save));
			confim.setText(getResources().getString(R.string.go_to_cart));
			cancel.setText(getResources().getString(R.string.confirm));
			cancelLy.setVisibility(View.VISIBLE);
			this.setCancelable(true);
		} else if(type.equalsIgnoreCase(DIALOG_TYPE_ACCESSORY_SAVE_FAIL)) {
			title.setText(getResources().getString(R.string.accessory_add_cart_fail));
			confim.setVisibility(View.GONE);
			cancel.setText(getResources().getString(R.string.confirm));
			cancelLy.setVisibility(View.VISIBLE);
			this.setCancelable(true);
		} else if(type.equalsIgnoreCase(DIALOG_TYPE_PRODUCT_MATCH_FAIL)) {
			title.setText(getResources().getString(R.string.product_match_fail));
			confim.setVisibility(View.GONE);
			cancel.setText(getResources().getString(R.string.confirm));
			cancelLy.setVisibility(View.VISIBLE);
			this.setCancelable(true);
		}

		setCancelable(false);
		if (getDialog() != null)
			getDialog().setCanceledOnTouchOutside(false);

		return v;
	}

	@Override
	public void onClick(View v) {
		try {
			dismiss();
			v.setTag(getArguments().getString("popup_type"));

			if (getClickListener() != null) {
				if (v.getId() == R.id.btn_confim) {
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
