package com.snaps.instagram.utils.instagram;

public interface OAuthDialogListener {
	public abstract void onComplete(String accessToken);
	public abstract void onError(String error);
}