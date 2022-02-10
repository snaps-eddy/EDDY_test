package com.snaps.kakao.utils.share;

import android.content.Context;

import com.snaps.common.utils.ui.IFBActivityResult;
import com.snaps.common.utils.ui.IPostingResult;
import com.snaps.kakao.utils.share.SNSShareContentsStruct.SNSShareContents;

public abstract class SNSShareParent {

	private Context context = null;
	private SNSShareContents contents = null;
	
	SNSShareParent(SNSShareContents contents) {
		this.contents = contents;
		if(contents != null)
			this.context = contents.getContext();
	}
	
	public abstract void post(IPostingResult listener);
	
	public IFBActivityResult postFB(IPostingResult listener) {
		return null;
	}

	public Context getContext() {
		return context;
	}

	public SNSShareContents getContents() {
		return contents;
	}
}
