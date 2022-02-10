package com.snaps.kakao.utils.share;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.IPostingResult;
import com.snaps.kakao.utils.kakao.KaKaoUtil;
import com.snaps.kakao.utils.share.SNSShareContentsStruct.SNSShareContents;

public class SNSShareFactory {
	private static final String TAG = SNSShareFactory.class.getSimpleName();

	public static Object createInstance(byte type, SNSShareContents contents, IPostingResult listener) {
		KaKaoUtil kUtil = null;
		try {
			switch (type) {
			case ISNSShareConstants.SNS_TYPE_BAND:
				return new SNSShareBand(contents);
			case ISNSShareConstants.SNS_TYPE_FACEBOOK:
				return new SNSShareFacebook(contents);
			case ISNSShareConstants.SNS_TYPE_LINE:
				return new SNSShareLine(contents);
			case ISNSShareConstants.SNS_TYPE_KAKAO_STORY:
				kUtil = new KaKaoUtil();
				kUtil.createKakaoInstance(contents.getContext());
				return kUtil;
			case ISNSShareConstants.SNS_TYPE_KAKAO_TALK:
				kUtil = new KaKaoUtil();
				kUtil.createKakaoInstance(contents.getContext());
				return kUtil;
			}
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return null;
	}
}
