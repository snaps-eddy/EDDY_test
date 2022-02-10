package com.snaps.mobile.activity.edit.view;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.snaps.mobile.R;


public class DialogDefaultProgress extends Dialog {

	public DialogDefaultProgress(Context context) {
		super(context , R.style.transparentView );
        setContentView( R.layout.dialog_progress_default );
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.setCancelable( false );
    }
	
	@Override
	public void onBackPressed() {
		// Back Key 방지.
	}
}
