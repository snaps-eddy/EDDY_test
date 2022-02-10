package com.snaps.kakao.utils.share;

public interface ISNSShareConstants {
	public static final byte SNS_TYPE_FACEBOOK 		= 0;
	public static final byte SNS_TYPE_LINE 			= 1;
	public static final byte SNS_TYPE_BAND 			= 2;
	public static final byte SNS_TYPE_KAKAO_STORY 	= 3;
	public static final byte SNS_TYPE_KAKAO_TALK 	= 4;
	
//	public static final int SNS_SHARE_REQUEST_CODE = 0x16E5; //다른 요청 코드와 겹치지 않게 하기 위해 이상한 숫자로 정했는데...혹시나 겹치지 않게 처리.
	
	public final String PACKAGE_NAME_FACEBOOK 		= "com.facebook.katana";
	public final String PACKAGE_NAME_LINE 			= "jp.naver.line.android";
	public final String PACKAGE_NAME_BAND 			= "com.nhn.android.band";
	public final String PACKAGE_NAME_KAKAO_STORY 	= "com.kakao.story";
	public final String PACKAGE_NAME_KAKAO_TALK 	= "com.kakao.talk";
}
