package com.snaps.kakao.utils.share;

import android.content.Intent;
import android.net.Uri;

import com.snaps.common.utils.ui.IPostingResult;
import com.snaps.kakao.utils.share.SNSShareContentsStruct.SNSShareContents;
import com.snaps.kakao.utils.share.SNSShareContentsStruct.SNSShareContentsBand;

class SNSShareBand extends SNSShareParent {

	SNSShareBand(SNSShareContents contents) {
		super(contents);
	}

	@Override
	public void post(IPostingResult listener) {
		if(getContext() == null || getContents() == null || !(getContents() instanceof SNSShareContentsBand)) return;
		
		SNSShareContentsBand contents = (SNSShareContentsBand) getContents();

		if(SNSShareUtil.isInstalledApp(getContext(), ISNSShareConstants.PACKAGE_NAME_BAND)) {
			Uri uri = Uri.parse("bandapp://create/post?text=" + contents.getLink() 	// 글 본문 (utf-8 urlencoded)
					+ "&route=" + contents.getDomain());

			Intent intent = new Intent();
			intent = new Intent(Intent.ACTION_VIEW, uri);
			
			getContext().startActivity(intent);
			
			if(listener != null) 
				listener.OnPostingComplate(true, null);
		} else {
			SNSShareUtil.gotoGooglePlay(getContext(), ISNSShareConstants.PACKAGE_NAME_BAND);
		}
	}
}
