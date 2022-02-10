package com.snaps.common.data.smart_snaps.interfacies;

/**
 * Created by ysjeong on 2018. 1. 16..
 */

public interface ISmartSnapImgDataAnimationState {
	void setAnimationStateToStart();

	boolean isActiveAnimation();

	void onRequestedAnimation();

	void suspendAnimation();
}
