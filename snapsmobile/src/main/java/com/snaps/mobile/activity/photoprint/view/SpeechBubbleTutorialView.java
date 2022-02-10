package com.snaps.mobile.activity.photoprint.view;

import android.content.Context;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.structure.SnapsHandler;

public class SpeechBubbleTutorialView extends RelativeLayout implements ISnapsHandler {
	private static final String TAG = SpeechBubbleTutorialView.class.getSimpleName();

	private static final long TUTORIAL_SHOW_TIME = 8000;

	private Context context;
	private Builder attribute;
	private boolean isFinishing;

	public SpeechBubbleTutorialView(Context context, Builder attribute) {
		super(context);
		init(context, attribute);
	}

	public void closeTutorial() {
		if (isFinishing || getParent() == null || !(getParent() instanceof ViewGroup) ) return;
		isFinishing = true;
		try {
			((ViewGroup) getParent()).removeView(this);
		} catch (Exception e) { Dlog.e(TAG, e); }
	}

	void init(Context context, Builder attribute) {
		if (attribute == null) return;

		this.attribute = attribute;
		this.context = context;
		this.isFinishing = false;

		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = layoutInflater.inflate(R.layout.photo_print_tutorial_layout, this);

        RelativeLayout textLayout = (RelativeLayout) view.findViewById( R.id.tutorial_text_container );
        TextView text = (TextView) view.findViewById(R.id.tutorial_text);
        if ( text != null && textLayout != null ) {
            if (attribute.getTutorialType() == Builder.TUTORIAL_TYPE.TYPE_PHOTO_PRINT) {
                text.setText( context.getString(R.string.photo_print_area_editable_desc) );
                float screenW = UIUtil.getScreenWidth( context );
                float screenH = UIUtil.getScreenHeight( context );

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textLayout.getLayoutParams();
                params.topMargin = (int)( screenH * 0.19 );
                params.leftMargin = (int)( screenW * 0.205 );
                textLayout.setLayoutParams( params );
                textLayout.setRotation( 180 );
                text.setRotation( 180 );
                textLayout.setVisibility(View.VISIBLE);
            }
        }

		SnapsHandler snapsHandler = new SnapsHandler(this);
		snapsHandler.sendEmptyMessageDelayed(MSG_CLOSE_TUTORIAL, TUTORIAL_SHOW_TIME);
	}

	private static final int MSG_CLOSE_TUTORIAL = 0;

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
			case MSG_CLOSE_TUTORIAL :
				closeTutorial();
				break;
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();

        Builder.TUTORIAL_TYPE tutorialType = attribute.getTutorialType();
		if (tutorialType != null) {
			switch (tutorialType) {
				case TYPE_PHOTO_PRINT: setShownDatePhoneFragmentTutorial(context, tutorialType); break;
			}
		}
	}

    public static void setShownDatePhoneFragmentTutorial(Context context, Builder.TUTORIAL_TYPE tutorialType) {
        if (tutorialType == null) return;
        switch (tutorialType) {
            case TYPE_PHOTO_PRINT: Setting.set(context, tutorialType.getDateSaveName(), String.valueOf(System.currentTimeMillis())); break;
        }
    }

    public static String getShownDatePhoneFragmentTutorial(Context context, Builder.TUTORIAL_TYPE tutorialType) {
        if (tutorialType == null) return null;
        switch (tutorialType) {
            case TYPE_PHOTO_PRINT: return Setting.getString(context, tutorialType.getDateSaveName());
        }
        return null;
    }

	public static class Builder {
        public enum TUTORIAL_TYPE {
            TYPE_PHOTO_PRINT( "tutorial_type_photo_print" );

            private String name;

            TUTORIAL_TYPE(String name) {
                this.name = name;
            }

            public String getName() {
                return name;
            }

            public String getDateSaveName() {
                return name + "_shown_date";
            }

            public int getNoShowDays() {
                int days = 0;
                switch (this) {
                    case TYPE_PHOTO_PRINT: days = 15;
                }
                return days;
            }
        }
		private TUTORIAL_TYPE tutorialType;
		private String title;

		public TUTORIAL_TYPE getTutorialType() {
			return tutorialType;
		}

		public String getTitle() {
			return title;
		}

		public Builder setTutorialType( TUTORIAL_TYPE tutorialType ) {
			this.tutorialType = tutorialType;
			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder create() {
			return this;
		}
	}
}
