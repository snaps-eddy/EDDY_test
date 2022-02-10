package com.snaps.common.utils.animation;

import android.content.Context;
import android.widget.ImageView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.ViewUnbindHelper;

public class SnapsFrameAnimation {
	private static final String TAG = SnapsFrameAnimation.class.getSimpleName();

	private boolean mIsRepeat;

	private AnimationListener mAnimationListener;

	private ImageView mImageView;

	private int[] mFrameRess;

	private int[] mDurations;

	private int mDuration;

	private int mDelay;

	private int mLastFrame;

	private boolean mNext;

	private boolean mPause;

	private int mCurrentSelect;

	private int mCurrentFrame;

	private static final int SELECTED_A = 1;

	private static final int SELECTED_B = 2;

	private static final int SELECTED_C = 3;

	private static final int SELECTED_D = 4;

//    public SnapsFrameAnimation(ImageView iv, int[] frameRes, int duration, boolean isRepeat) {
//        this.mImageView = iv;
//        this.mFrameRess = frameRes;
//        this.mDuration = duration;
//        this.mLastFrame = frameRes.length - 1;
//        this.mIsRepeat = isRepeat;
//        try {
//            play(0);
//        } catch (Exception e) {
//            Dlog.e(TAG, e);
//        }
//    }

	public SnapsFrameAnimation(ImageView iv, int[] frameRess, int[] durations, boolean isRepeat) {
		this.mImageView = iv;
		this.mFrameRess = frameRess;
		this.mDurations = durations;
		this.mLastFrame = frameRess.length - 1;
		this.mIsRepeat = isRepeat;
	}

	public void startAnimation(Context context) {
		try {
			playByDurations(context, 0);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		} catch (OutOfMemoryError e2) {
			Dlog.e(TAG, e2);
		}
	}

	public long getTotalDurations() {
		long total = 0;

		if (mDurations != null) {
			for(int i = 0; i < mDurations.length; i++) {
				total += mDurations[i];
			}
		}

		return total;
	}

//    public SnapsFrameAnimation(ImageView iv, int[] frameRess, int duration, int delay) {
//        this.mImageView = iv;
//        this.mFrameRess = frameRess;
//        this.mDuration = duration;
//        this.mDelay = delay;
//        this.mLastFrame = frameRess.length - 1;
//        try {
//            playAndDelay(0);
//        } catch (Exception e) {
//            Dlog.e(TAG, e);
//        }
//    }
//
//    public SnapsFrameAnimation(ImageView iv, int[] frameRess, int[] durations, int delay) {
//        this.mImageView = iv;
//        this.mFrameRess = frameRess;
//        this.mDurations = durations;
//        this.mDelay = delay;
//        this.mLastFrame = frameRess.length - 1;
//        try {
//            playByDurationsAndDelay(0);
//        } catch (Exception e) {
//            Dlog.e(TAG, e);
//        }
//    }

	private void playByDurationsAndDelay(final Context context, final int i) throws Exception {
		mImageView.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (mPause) {
					mCurrentSelect = SELECTED_A;
					mCurrentFrame = i;
					return;
				}
				if (0 == i) {
					if (mAnimationListener != null) {
						mAnimationListener.onAnimationStart();
					}
				}
				try {
					mImageView.setBackgroundResource(mFrameRess[i]);
				} catch (OutOfMemoryError e) {
					Dlog.e(TAG, e);
					return;
				}
				if (i == mLastFrame) {
					if (mAnimationListener != null) {
						mAnimationListener.onAnimationRepeat();
					}
					mNext = true;
					try {
						playByDurationsAndDelay(context, 0);
					} catch (Exception e) {
						Dlog.e(TAG, e);
					}
				} else {
					try {
						playByDurationsAndDelay(context, i + 1);
					} catch (Exception e) {
						Dlog.e(TAG, e);
					}
				}
			}
		}, mNext && mDelay > 0 ? mDelay : mDurations[i]);

	}

	private void playAndDelay(final Context context, final int i) throws Exception {
		mImageView.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (mPause) {
					if (mPause) {
						mCurrentSelect = SELECTED_B;
						mCurrentFrame = i;
						return;
					}
					return;
				}
				mNext = false;
				if (0 == i) {
					if (mAnimationListener != null) {
						mAnimationListener.onAnimationStart();
					}
				}
//                mImageView.setImageResource(mFrameRess[i]);
				try {
					mImageView.setBackgroundResource(mFrameRess[i]);
				} catch (OutOfMemoryError e) {
					Dlog.e(TAG, e);
					return;
				}
				if (i == mLastFrame) {
					if (mAnimationListener != null) {
						mAnimationListener.onAnimationRepeat();
					}
					mNext = true;
					try {
						playAndDelay(context, 0);
					} catch (Exception e) {
						Dlog.e(TAG, e);
					}
				} else {
					try {
						playAndDelay(context, i + 1);
					} catch (Exception e) {
						Dlog.e(TAG, e);
					}
				}
			}
		}, mNext && mDelay > 0 ? mDelay : mDuration);

	}

	private void playByDurations(final Context context, final int i) throws Exception {
		mImageView.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (mPause) {
					if (mPause) {
						mCurrentSelect = SELECTED_C;
						mCurrentFrame = i;
						return;
					}
					return;
				}
				if (0 == i) {
					if (mAnimationListener != null) {
						mAnimationListener.onAnimationStart();
					}
				}
//                mImageView.setImageResource(mFrameRess[i]);
				//TODO  만약 리사이클 비트맵 사용으로 인해 오류가 발생한다면 아래 코드 삭제..
				try {
					ViewUnbindHelper.unbindReferences(mImageView, null, false);
				} catch (Exception e) {
					Dlog.e(TAG, e);
				}

				try {
					mImageView.setBackgroundResource(mFrameRess[i]);
				} catch (OutOfMemoryError e) {
					Dlog.e(TAG, e);
					return;
				}
				if (i == mLastFrame) {
					if (mIsRepeat) {
						if (mAnimationListener != null) {
							mAnimationListener.onAnimationRepeat();
						}
						try {
							playByDurations(context, 0);
						} catch (Exception e) {
							Dlog.e(TAG, e);
						}
					} else {
						if (mAnimationListener != null) {
							mAnimationListener.onAnimationEnd();
						}
					}
				} else {
					try {
						playByDurations(context, (i + 1));
					} catch (Exception e) {
						Dlog.e(TAG, e);
					}
				}
			}
		}, mDurations[i]);

	}

	private void play(final Context context, final int i) throws Exception {
		mImageView.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (mPause) {
					if (mPause) {
						mCurrentSelect = SELECTED_D;
						mCurrentFrame = i;
						return;
					}
					return;
				}
				if (0 == i) {
					if (mAnimationListener != null) {
						mAnimationListener.onAnimationStart();
					}
				}
//                mImageView.setImageResource(mFrameRess[i]);
				try {
					mImageView.setBackgroundResource(mFrameRess[i]);
				} catch (OutOfMemoryError e) {
					Dlog.e(TAG, e);
					return;
				}
				if (i == mLastFrame) {

					if (mIsRepeat) {
						if (mAnimationListener != null) {
							mAnimationListener.onAnimationRepeat();
						}
						try {
							play(context, 0);
						} catch (Exception e) {
							Dlog.e(TAG, e);
						}
					} else {
						if (mAnimationListener != null) {
							mAnimationListener.onAnimationEnd();
						}
					}

				} else {
					try {
						play(context, i + 1);
					} catch (Exception e) {
						Dlog.e(TAG, e);
					}
				}
			}
		}, mDuration);
	}

	public static interface AnimationListener {

		/**
		 * <p>Notifies the start of the animation.</p>
		 */
		void onAnimationStart();

		/**
		 * <p>Notifies the end of the animation. This callback is not invoked
		 * for animations with repeat count set to INFINITE.</p>
		 */
		void onAnimationEnd();

		/**
		 * <p>Notifies the repetition of the animation.</p>
		 */
		void onAnimationRepeat();
	}

	/**
	 * <p>Binds an animation listener to this animation. The animation listener
	 * is notified of animation events such as the end of the animation or the
	 * repetition of the animation.</p>
	 *
	 * @param listener the animation listener to be notified
	 */
	public void setAnimationListener(AnimationListener listener) {
		this.mAnimationListener = listener;
	}

	public void release() {
		try {
			pauseAnimation();
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	public void pauseAnimation() {
		this.mPause = true;
	}

	public boolean isPause() {
		return this.mPause;
	}

	public void restartAnimation(Context context) {
		try {
			if (mPause) {
				mPause = false;
				switch (mCurrentSelect) {
					case SELECTED_A:
						playByDurationsAndDelay(context, mCurrentFrame);
						break;
					case SELECTED_B:
						playAndDelay(context, mCurrentFrame);
						break;
					case SELECTED_C:
						playByDurations(context, mCurrentFrame);
						break;
					case SELECTED_D:
						play(context, mCurrentFrame);
						break;
					default:
						break;
				}
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}
}
