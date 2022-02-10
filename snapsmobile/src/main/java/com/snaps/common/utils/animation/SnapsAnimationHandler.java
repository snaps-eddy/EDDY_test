package com.snaps.common.utils.animation;

import android.content.Context;
import android.widget.ImageView;

import com.snaps.common.utils.animation.frame_animation.SnapsFrameAnimationResFactory;
import com.snaps.common.utils.animation.frame_animation.SnapsFrameAnimationResInfo;
import com.snaps.common.utils.log.Dlog;

public class SnapsAnimationHandler {
	private static final String TAG = SnapsAnimationHandler.class.getSimpleName();

	public static SnapsFrameAnimation startFrameAnimation(
			Context context,
			ImageView imageView,
			SnapsFrameAnimationResFactory.eSnapsFrameAnimation animation)
	{
		return startFrameAnimation(context, imageView, animation, false);
	}

	public static SnapsFrameAnimation startFrameAnimation(
			Context context,
			ImageView imageView,
			SnapsFrameAnimationResFactory.eSnapsFrameAnimation animation,
			boolean isRepeat)
	{
		try {
			SnapsFrameAnimationResInfo resInfo = SnapsFrameAnimationResFactory.getAnimationResInfo(animation);
			if (resInfo == null) {
				return null;
			}
			resInfo.setRepeat(isRepeat);

			int[] arAnimationRes = resInfo.getResIds();
			int[] arAnimationDuring = resInfo.getDuring();
			if (arAnimationRes == null || arAnimationRes.length < 1 || arAnimationDuring == null) {
				return null;
			}

			imageView.setBackgroundResource(arAnimationRes[0]);

			SnapsFrameAnimation frameAnimation = new SnapsFrameAnimation(imageView, arAnimationRes, arAnimationDuring, resInfo.isRepeat());
			frameAnimation.startAnimation(context);
			return frameAnimation;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		} catch (OutOfMemoryError e2) {
			Dlog.e(TAG, e2);
		}
		return null;
	}
}
