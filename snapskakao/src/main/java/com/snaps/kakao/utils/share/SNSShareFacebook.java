package com.snaps.kakao.utils.share;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.IFBActivityResult;
import com.snaps.common.utils.ui.IPostingResult;
import com.snaps.kakao.utils.share.SNSShareContentsStruct.SNSShareContents;
import com.snaps.kakao.utils.share.SNSShareContentsStruct.SNSShareContentsFaceBook;

class SNSShareFacebook extends SNSShareParent implements IFBActivityResult {
	private static final String TAG = SNSShareFacebook.class.getSimpleName();

	private final String FACEBOOK_SHARE_URL = "https://www.facebook.com/sharer/sharer.php?u=";
	private CallbackManager callbackManager;
	private ShareDialog shareDialog;
	private IPostingResult listener = null;
	
	private boolean isValidRequest = false;
	
	SNSShareFacebook(SNSShareContents contents) {
		super(contents);
		
		initFacebookSDK();
	}
	
	public IFBActivityResult postFB(IPostingResult listener) {
		if (getContext() == null || getContents() == null)
			return null;

		this.listener = listener;
		
		if (ShareDialog.canShow(ShareLinkContent.class)) {
			ShareLinkContent linkContent = new ShareLinkContent.Builder()
					.setContentTitle(getContents().getSubject())
					.setContentDescription(
							((SNSShareContentsFaceBook)getContents()).getDescription())
					.setContentUrl(
							Uri.parse(getContents().getLink()))
					.build();

			shareDialog.show(linkContent, ShareDialog.Mode.AUTOMATIC);
			
			isValidRequest = true;
		} else {
			if (listener != null)
				listener.OnPostingComplate(false, null);
		}
		
		return this;
	}

	@Override
	public void post(IPostingResult listener) {
		Intent intent = new Intent();
		
		if (SNSShareUtil.isInstalledApp(getContext(),
				ISNSShareConstants.PACKAGE_NAME_FACEBOOK)) {
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, getContents().getSubject());
			intent.putExtra(Intent.EXTRA_TEXT, getContents().getLink());
			intent.setPackage(ISNSShareConstants.PACKAGE_NAME_FACEBOOK);
			getContext().startActivity(intent);
	
			if (listener != null)
				listener.OnPostingComplate(true, null);
	
		} else {
			try {
				// WEB 페이스북 공유
				String sharerUrl = FACEBOOK_SHARE_URL + getContents().getLink();
				intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
				getContext().startActivity(intent);
			} catch (Exception e) {
				Dlog.e(TAG, e);
				SNSShareUtil.gotoGooglePlay(getContext(),
						ISNSShareConstants.PACKAGE_NAME_FACEBOOK);
			}
		}
	}
	
	@Override
	public void onFBActivityResult(int requestCode, int resultCode, Intent data) {
		if(callbackManager != null)
			callbackManager.onActivityResult(requestCode, resultCode, data);		
	}

	private void initFacebookSDK() {
		if(getContext() == null || !(getContext() instanceof Activity)) return;
		
		FacebookSdk.sdkInitialize(getContext());
		
        callbackManager = CallbackManager.Factory.create();
        
        shareDialog = new ShareDialog((Activity) getContext());
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
			@Override
			public void onCancel() {
				if(!isValidRequest) return;
				
				if (listener != null)
					listener.OnPostingComplate(false, null);
				
				SNSShareUtil.releaseCallback();
				isValidRequest = false;
			}
			@Override
			public void onError(FacebookException arg0) {
				if(!isValidRequest) return;
				
				if (listener != null)
					listener.OnPostingComplate(false, arg0.toString());
				
				SNSShareUtil.releaseCallback();
				isValidRequest = false;
			}
			@Override
			public void onSuccess(Sharer.Result arg0) {
				if(!isValidRequest) return;
				
				if (listener != null)
					listener.OnPostingComplate(true, null);
				
				SNSShareUtil.releaseCallback();
				isValidRequest = false;
			}
        });
	}
}
