package com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial;

import android.content.Context;
import android.os.Message;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.animation.SnapsAnimationHandler;
import com.snaps.common.utils.animation.SnapsFrameAnimation;
import com.snaps.common.utils.animation.frame_animation.SnapsFrameAnimationResFactory;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.interfacies.SnapsGIFTutorialListener;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.type.SnapsGIFTutorialFactory;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

public class GIFTutorialView extends RelativeLayout implements ISnapsHandler {
	private static final String TAG = GIFTutorialView.class.getSimpleName();
	private static final long TUTORIAL_SHOW_TIME = 8000;

	private Context context;
	private Builder attribute;
	private boolean isFinishing;

	private SnapsFrameAnimation frameAnimation;

	private CloseListener closeListener = null;

	public GIFTutorialView(Context context, Builder attribute) {
		super(context);
		init(context, attribute);
	}

	public GIFTutorialView(Context context, Builder attribute,CloseListener closeListener) {
		super(context);
		this.closeListener = closeListener;
		init(context, attribute);
	}

	public void closeTutorial() {
		if (isFinishing) return;
		isFinishing = true;
		try {
			((ViewGroup) getParent()).removeView(this);
		} catch (Exception e) { Dlog.e(TAG, e); }

		if(closeListener !=null) {
			closeListener.close();
		}
	}

	void init(Context context, Builder attribute) {
		if (attribute == null) return;

		this.attribute = attribute;
		this.context = context;
		this.isFinishing = false;

		SnapsGIFTutorialFactory.createGIFTutorialHandler(this, attribute, new SnapsGIFTutorialListener() {
			@Override
			public void startAnimation(ImageView imageView) {
				GIFTutorialView.this.startAnimation(imageView);
			}

			@Override
			public void closeTutorial() {
				GIFTutorialView.this.closeTutorial();
			}
		});

		SnapsHandler snapsHandler = new SnapsHandler(this);
		snapsHandler.sendEmptyMessageDelayed(MSG_CLOSE_TUTORIAL, TUTORIAL_SHOW_TIME);
	}

	private void startAnimation(ImageView imageView) {
		if (attribute.getAnimation() == null || imageView == null) return;

		frameAnimation = SnapsAnimationHandler.startFrameAnimation(getContext(), imageView, attribute.getAnimation());
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

		if (frameAnimation != null) {
			frameAnimation.release();
		}

		ISnapsImageSelectConstants.eTUTORIAL_TYPE tutorialType = attribute.getTutorialType();
		if (tutorialType != null) {
			switch (tutorialType) {
				case PHONE_FRAGMENT_PINCH_MOTION: ImageSelectUtils.setShownDatePhoneFragmentTutorial(context, ISnapsImageSelectConstants.eTUTORIAL_TYPE.PHONE_FRAGMENT_PINCH_MOTION); break;
				case SMART_RECOMMEND_BOOK_CHANGE_IMAGE_BY_DRAG_N_DROP: ImageSelectUtils.setShownDatePhoneFragmentTutorial(context, ISnapsImageSelectConstants.eTUTORIAL_TYPE.SMART_RECOMMEND_BOOK_CHANGE_IMAGE_BY_DRAG_N_DROP); break;
				case SMART_RECOMMEND_BOOK_SWIPE_PAGE: ImageSelectUtils.setShownDatePhoneFragmentTutorial(context, ISnapsImageSelectConstants.eTUTORIAL_TYPE.SMART_RECOMMEND_BOOK_SWIPE_PAGE); break;
			}
		}
	}

	public static class Builder {
		private ISnapsImageSelectConstants.eTUTORIAL_TYPE tutorialType;
		private String title;
		private SnapsFrameAnimationResFactory.eSnapsFrameAnimation animation;

		public ISnapsImageSelectConstants.eTUTORIAL_TYPE getTutorialType() {
			return tutorialType;
		}

		public String getTitle() {
			return title;
		}

		public SnapsFrameAnimationResFactory.eSnapsFrameAnimation getAnimation() {
			return animation;
		}

		public Builder setTutorialType(ISnapsImageSelectConstants.eTUTORIAL_TYPE tutorialType) {
			this.tutorialType = tutorialType;
			return this;
		}

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setAnimation(SnapsFrameAnimationResFactory.eSnapsFrameAnimation animation) {
			this.animation = animation;
			return this;
		}

		public Builder create() {
			return this;
		}
	}

	public interface CloseListener {
		void close();
	}
}
