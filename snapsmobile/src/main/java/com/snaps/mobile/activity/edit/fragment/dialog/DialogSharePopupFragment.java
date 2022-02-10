//package com.snaps.mobile.activity.edit.fragment.dialog;
//
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//
//import com.snaps.common.utils.log.Dlog;
//import com.snaps.mobile.R;
//import com.snaps.mobile.activity.edit.EditActivity;
//
//import errorhandle.logger.SnapsLogger;
//import errorhandle.logger.model.SnapsLoggerClass;
//
///**
// *
// * com.snaps.kakao.activity.edit.fragment
// * DialogSharePopupFragment.java
// *
// * @author ParkJaeMyung
// * @Date : 2013. 5. 25.
// * @Version :
// */
//public class DialogSharePopupFragment extends DialogFragment implements View.OnClickListener {
//	private static final String TAG = DialogSharePopupFragment.class.getSimpleName();
//	public static DialogSharePopupFragment newInstance()
//	{
//		return new DialogSharePopupFragment();
//	}
//
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));
//
//		setStyle( DialogFragment.STYLE_NO_TITLE , 0 );
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//
//		View v = inflater.inflate( R.layout.dialog_share_popup_edit_fragment , container , false );
//		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT ));
//
//		Window window = getDialog().getWindow();
//		window.setGravity( Gravity.TOP | Gravity.RIGHT );
//
//		WindowManager.LayoutParams params = window.getAttributes();
//		params.dimAmount = 0.0f;
//
//		window.setAttributes( params );
//
//		View btn_share_kakaotalk = v.findViewById( R.id.btn_share_kakaotalk );
//		btn_share_kakaotalk.setOnClickListener( this );
//
//		return v;
//	}
//
//	@Override
//	public void onClick(View v) {
//	}
//}
/**
 * @Marko
 * 참조하지 않는 클래스라 주석처리.
 */
