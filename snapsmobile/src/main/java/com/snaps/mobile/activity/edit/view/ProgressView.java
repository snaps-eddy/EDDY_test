//package com.snaps.mobile.activity.edit.view;
//
//import android.animation.ObjectAnimator;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager.BadTokenException;
//import android.view.animation.DecelerateInterpolator;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.snaps.common.utils.log.Dlog;
//import com.snaps.mobile.R;
//import com.snaps.mobile.activity.edit.view.custom_progress.SnapsTimerProgressView;
//
//public class ProgressView extends Dialog implements DialogInterface.OnKeyListener, Runnable, android.view.View.OnClickListener {
//	private static final String TAG = ProgressView.class.getSimpleName();
//	TextView title_text;
//	TextView per_text;
//	TextView per_text_1;
//	TextView per_text_2;
//	TextView page_text;
//
//	ProgressBar bar;
//	ProgressBar bar_1;
//	ProgressBar bar_2;
//	ProgressBar loading;
//
//	RelativeLayout progress_1;
//	RelativeLayout progress_2;
//
//	int bar_value = 0;
//	int bar_value_1 = 0;
//	int bar_value_2 = 0;
//	String bar_count = "";
//
//	static ProgressView _instance;
//	Thread progress_thread;
//
//	public static final String VIEW_PROGRESS = "view_progress";
//	public static final String VIEW_LOADING = "view_loading";
//
//	final int duration = 100;
//	boolean isDestroyed = false;
//
//	/**
//	 *
//	 * Get Instance
//	 * @param context
//	 * @return
//	 */
//	public static ProgressView getInstance ( Context context ) {
//		if ( _instance == null ) {
//			_instance = new ProgressView( context );
//		}
//		return _instance;
//	}
//
//	public ProgressView(Context context) {
//		super(context, R.style.transparentView);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		this.setCancelable( false );
//		/** Design the dialog in main.xml file */
//		setContentView(R.layout.dialog_progress_custom_bar);
//
//		title_text = ( TextView ) this.findViewById( R.id.progress_title );
//		per_text = ( TextView ) this.findViewById( R.id.progress_text );
//		bar = ( ProgressBar ) this.findViewById( R.id.progress );
//		loading = ( ProgressBar ) this.findViewById( R.id.loading );
//		page_text = ( TextView ) this.findViewById( R.id.page_text );
//
//		per_text_1 = ( TextView ) this.findViewById( R.id.progress_text_1 );
//		bar_1 = ( ProgressBar ) this.findViewById( R.id.progress_1 );
//
//		per_text_2 = ( TextView ) this.findViewById( R.id.progress_text_2 );
//		bar_2 = ( ProgressBar ) this.findViewById( R.id.progress_2 );
//
//		progress_1 = ( RelativeLayout ) this.findViewById( R.id.progress_view_1 );
//		progress_2 = ( RelativeLayout ) this.findViewById( R.id.progress_view_2 );
//
//		isDestroyed = false;
//	}
//
//	/**
//	 * Instance 제거
//	 */
//	public static void destroy () {
//		if(_instance != null)
//			_instance.isDestroyed = true;
//		_instance = null;
//	}
//
//
//	@Override
//	public void run() {
//		while ( !isSuspended() ) {
//			try {
//				Thread.sleep( duration );
//			} catch ( InterruptedException e ) {
//				Dlog.e(TAG, e);
//			}
//
//			try {
//				per_text.post( progress_run );
//			} catch (OutOfMemoryError e) {
//				Dlog.e(TAG, e);
//			}
//		}
//	}
//
//	public boolean isSuspended() {
//		if(_instance != null && _instance.isDestroyed) return true;
//		return !isShowing();
//	}
//
//	Runnable progress_run = new Runnable() {
//        public void run()
//        {
//            // 현재 표시할 값이 최대값을 넘으면 최대값으로 대체한다.
//            if( bar_value > 100 ) bar_value = 100;
//            // 대화상자의 프로그레스바에 표시할 값을 설정한다.
//            per_text.setText( String.valueOf(  bar_value  ) + "%" );
//
//            if ( bar_value == 0 ) {
//            	bar.setProgress( bar_value );
//            } else {
//            	objectAnimation( bar  , "progress" , bar_value );
//            }
//
//            if ( page_text.getVisibility() == View.VISIBLE ) {
//            	page_text.setText( bar_count );
//            }
//        }
//    };
//
//
//    /**
//     * Object Animation 설정.
//     * @param obj
//     * @param propertyName
//     * @param value
//     */
//    void objectAnimation ( Object obj , String propertyName , int value ) {
//    	 ObjectAnimator animation = ObjectAnimator.ofInt( obj , propertyName , value );
//         animation.setDuration( duration );
//         animation.setInterpolator( new DecelerateInterpolator() );
//         animation.start();
//    }
//
//	/**
//	 *
//	 * 팝업 로드
//	 * @param mode
//	 */
//	public void load( String mode ) {
//		if ( mode.equalsIgnoreCase( VIEW_PROGRESS ) ) {
//			// 프로그레스 팝업.
//			loading.setVisibility( View.GONE );
//			bar.setVisibility( View.VISIBLE );
//			per_text.setVisibility( View.VISIBLE );
//
//			if ( progress_thread == null ) {
//				progress_thread = new Thread( this );
//				progress_thread.start();
//			}
//
//		} else if ( mode.equalsIgnoreCase( VIEW_LOADING ) ) {
//			// 단순 로딩 팝업.
//			per_text.setVisibility( View.GONE );
//			bar.setVisibility( View.GONE );
//			loading.setVisibility( View.VISIBLE );
//		}
//
//		try {
//			if ( ! this.isShowing() ) {
//				this.show();
//			}
//		} catch (BadTokenException e) {
//			Dlog.e(TAG, e);
//		}
//
//		page_text.setVisibility( View.GONE );
//
//		bar_value = 0;
//		bar.setProgress( 0 );
//	}
//
//	/**
//	 *
//	 * Progress Bar value 설정.
//	 * @param value
//	 */
//	public void setValue ( final int value ) {
//		bar_value = value;
//	}
//
//	public void closeProgress ( int value ) {
//
//		switch( value ) {
//			case 1 :
//				progress_1.setVisibility( View.GONE );
//			break;
//
//			case 2 :
//				progress_2.setVisibility( View.GONE );
//			break;
//		}
//
//
//	}
//
//	public void showProgress () {
//		progress_1.setVisibility( View.VISIBLE );
//		progress_2.setVisibility( View.VISIBLE );
//	}
//
//	/**
//	 *
//	 * Progress Bar value 설정.
//	 * @param value
//	 */
//	public void setValue1 ( final int value ) {
//		bar_value_1 = value;
//	}
//
//
//	/**
//	 *
//	 * Progress Bar value 설정.
//	 * @param value
//	 */
//	public void setValue2 ( final int value ) {
//		bar_value_2 = value;
//	}
//
//	/**
//	 *
//	 * Title Message 설정.
//	 * @param message
//	 */
//	public void setMessage ( String message ) {
//		title_text.setText( message );
//	}
//
//	/**
//	 * Page Count 설정.
//	 */
//	public void setPageCount () {
//		page_text.setVisibility( View.VISIBLE );
//	}
//	/**
//	 * @param pagetxt
//	 */
//	public void setPageCount ( String count ) {
//		bar_count = count;
//	}
//	/**
//	 *
//	 * 팝업 Unload
//	 */
//	public void Unload() {
//		try {
//			dismiss();
//			progress_thread = null;
//		} catch (Exception e) {
//			Dlog.e(TAG, e);
//		}
//	}
//
//	@Override
//	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//		return false;
//	}
//
//	@Override
//	public void onBackPressed() {
//		// Back Key 방지.
//	}
//
//	@Override
//	public void onClick(View arg0) {
//		try {
//			SnapsTimerProgressView.destroyProgressView();
//			dismiss();
//		} catch (Exception e) {
//			Dlog.e(TAG, e);
//		}
//	}
//}
