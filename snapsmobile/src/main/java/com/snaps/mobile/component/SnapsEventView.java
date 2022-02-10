package com.snaps.mobile.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.CropUtil;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.thread.ATask.OnTask;
import com.snaps.mobile.R;
import com.snaps.mobile.interfaces.ImpSnapsEvent;

public class SnapsEventView extends RelativeLayout {

	public static final String COUPON_INSERT_REG_FLAG_CODE = "204001";
	public static final String COUPON_INSERT_EVENT_CODE = "314100"; //"314004";
	
	ImpSnapsEvent mEventListener = null;
	Context mContext;
	ImageView eventImage;
	ImageView closeBtn;
	LinearLayout bgLayout;
	
	private Bitmap eventBitmap;

	public SnapsEventView(Context context) {
		super(context);
		init(context);
	}

	void init(Context context) {
		mContext = context;
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = layoutInflater.inflate(R.layout.login_event_view, this, true);
		
		closeBtn = (ImageView) view.findViewById( R.id.iv_close );
		eventImage = (ImageView) view.findViewById( R.id.iv_event_image );
		bgLayout = (LinearLayout) view.findViewById( R.id.popup_bg );
		
		closeBtn.setOnClickListener(mClickListener);
		bgLayout.setOnClickListener(mClickListener);
		
		
		eventImage = (ImageView) view.findViewById( R.id.iv_event_image );
		eventImage.getViewTreeObserver().addOnGlobalLayoutListener( globalLayoutListener );
	}
	
	private OnGlobalLayoutListener globalLayoutListener = new OnGlobalLayoutListener() {			
		@Override
		public void onGlobalLayout() {
			final int w = eventImage.getWidth();
			if( w > 0 ) {
				ATask.executeVoidWithThreadPool( new OnTask() {
					@Override
					public void onPre() { eventImage.getViewTreeObserver().removeGlobalOnLayoutListener( globalLayoutListener ); }
					
					@Override
					public void onPost() {
						eventImage.setImageBitmap( eventBitmap );
					}
					
					@Override
					public void onBG() {
						final String eventFilePath = Setting.getString( mContext, Const_VALUE.KEY_EVENT_FILE_PATH, "" );
						if( eventFilePath != null && eventFilePath.length() > 0 ) {
							eventBitmap = CropUtil.loadImage2(SnapsAPI.DOMAIN() + eventFilePath, w, -1, 1);
						}
					}
				} );
			}
		}
	};

	OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			removeEventView();
			if (mEventListener != null) {
				mEventListener.onEventClose();
			}

		}
	};

	public void removeEventView() {
		ViewGroup vg = ((ViewGroup) getParent());
		if (vg != null)
			vg.removeView(this);
		
		if( eventBitmap != null ) {
			eventBitmap.recycle();
			eventBitmap = null;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public void setEventListener(ImpSnapsEvent mEventListener) {
		this.mEventListener = mEventListener;
	}
}
