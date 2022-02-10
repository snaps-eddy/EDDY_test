package com.snaps.mobile.component;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.R;

public class SnapsNoPrintDialog extends Dialog implements android.view.View.OnClickListener{

	Context mContext;
	TextView mDialogNoPrintTxt;
	ImageView mDialogNoPrintCheckImg;
	TextView mDialogCheckTxt;
	TextView mBtn;

	OnclickBtn onclickBtn = null;
	public boolean mIsCheck = false;

	public SnapsNoPrintDialog(Context context) {
		super(context);

		mContext = context;
	}

	public SnapsNoPrintDialog(Context context,OnclickBtn onclickBtn) {
		super(context);
		mContext = context;
		this.onclickBtn=onclickBtn;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.custom_noprint_dialog);
		

		mDialogNoPrintTxt = (TextView) findViewById(R.id.noprint_dialog_txt);
		String msg=getContext().getResources().getString(R.string.photo_print_not_recommended_comment)+"\n"+getContext().getResources().getString(R.string.another_picture_recommended);
		mDialogNoPrintTxt.setText(msg);
		mDialogNoPrintTxt.setTextColor(Color.rgb(50, 50, 50));
		

		mDialogNoPrintCheckImg = (ImageView) findViewById(R.id.noprint_dialog_check_img);
		mDialogNoPrintCheckImg.setOnClickListener(this);

		mDialogCheckTxt = (TextView) findViewById(R.id.noprint_dialog_check_txt);
		mDialogCheckTxt.setTextColor(Color.rgb(50, 50, 50));

		mBtn = (TextView) findViewById(R.id.noprint_dialog_btn);
		mBtn.setTextColor(Color.rgb(239, 65, 35));
		mBtn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		
		if(v.getId() == R.id.noprint_dialog_btn)
		{
			if(mIsCheck == true)
			{
				Setting.set(mContext, "noprint", true);
			}
			if(onclickBtn != null){
				onclickBtn.onClick();
			}
			dismiss();
			
		}else if(v.getId() == R.id.noprint_dialog_check_img)
		{
			mIsCheck = !mIsCheck;
			mDialogNoPrintCheckImg.setImageResource(mIsCheck ? R.drawable.btn_check_on : R.drawable.btn_check_off);
			
			
		}
		
	}
	public interface OnclickBtn{
		void onClick();
	}
}
