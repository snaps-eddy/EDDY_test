package com.snaps.kakao.utils.share;

import android.content.Intent;
import android.net.Uri;

import com.snaps.common.utils.ui.IPostingResult;
import com.snaps.kakao.utils.share.SNSShareContentsStruct.SNSShareContents;

class SNSShareLine extends SNSShareParent {
	
	SNSShareLine(SNSShareContents contents) {
		super(contents);
	}

	private final String LINE_SHARE_SCHEMA = "line://msg/text/";
	
	@Override
	public void post(IPostingResult listener) {
		if(getContext() == null || getContents() == null) return;

		if(SNSShareUtil.isInstalledApp(getContext(), ISNSShareConstants.PACKAGE_NAME_LINE)) {
			String text = LINE_SHARE_SCHEMA + getContents().getLink();
			text = text.replaceAll("\n", "");
			Intent itt = new Intent();
			itt.setAction(Intent.ACTION_VIEW);
			itt.setData(Uri.parse(text));
			getContext().startActivity(itt);
			
			if(listener != null) 
				listener.OnPostingComplate(true, null);
		} else {
			SNSShareUtil.gotoGooglePlay(getContext(), ISNSShareConstants.PACKAGE_NAME_LINE);
		}
	}
}
