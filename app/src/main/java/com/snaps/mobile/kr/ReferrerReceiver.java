package com.snaps.mobile.kr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.utils.pref.PrefUtil;

import java.net.URLDecoder;

import errorhandle.logger.SnapsLogger;

public class ReferrerReceiver extends BroadcastReceiver {
	private static final String TAG = ReferrerReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {

		final String action = intent.getAction();
		Bundle extras = intent.getExtras();
		if (extras == null) return;

        String referrerString = extras.getString("referrer");
		SnapsLogger.appendTextLog("referrerString", referrerString);

		// adbrix 인스톨 정보 전달, updateinfo 받기 전이면 intent를 저장해둔다.
//		if( !SnapsAdbrix.sendInstallInfo(context, intent) ) {
//			Setting.set( context, Const_VALUE.KEY_ADBRIX_INTENT, intent.toUri(0) );
//			SnapsLogger.appendTextLog("intent.toUri", intent.toUri(0));
//		}

		if (action != null && TextUtils.equals(action, "com.android.vending.INSTALL_REFERRER") && referrerString != null && referrerString.contains("sendno") ) {
			try {
				final String rawReferrer = intent.getStringExtra("referrer");
				String referrer = URLDecoder.decode(rawReferrer, "UTF-8");
				// Parse parameters
				String sendno = StringUtil.getTitleAtUrl(referrer, "sendno");
				String deviceid = "";// StringUtil.getTitleAtUrl(referrer, "deviceid");
				String eventcode = "";// StringUtil.getTitleAtUrl(referrer, "eventcode");
				PrefUtil.saveKakaoEvent(context, sendno, eventcode, deviceid);

				Intent in = new Intent(Const_VALUE.INSTALL_REFERR);
				in.putExtra("sendno", sendno);
				context.sendBroadcast(in);
		
				String[] temp = referrerString.split( "&" );
				StringBuilder sb = new StringBuilder();
				for( int i = 0; i < temp.length; ++i ) {
					if( !temp[i].contains("sendno") ) {
						if( sb.length() > 0 ) sb.append( "&" );
						sb.append( temp[i] );
					}
				}
				
				sendCampaignIntent( context, intent, sb.toString() );
			} catch (Exception e) {
				Dlog.e(TAG, e);
				SnapsLogger.sendLogException("ReffererReceiver/onReceive", e);
			}
		}
		else if( referrerString != null && referrerString.length() > 0 ){
			sendCampaignIntent( context, intent );
			
			// referrerString에 이벤트 값이 붙어 올때 분리하여 처리.
//			if( referrerString.contains("P0008") ) {
//				int index = referrerString.indexOf( "&cmd" );
//				if( index > -1 ) {
//					String eventString = referrerString.substring( index + 1, referrerString.length() );
//					referrerString = referrerString.substring( 0, index );
//
//					if( eventString.contains("핑크퐁이벤트") ) {
//						Setting.set( context, Const_VALUE.PINKPONG_EVENT_SHOW, true );
//						Setting.set( context, Const_VALUE.PINKPONG_EVENT_DATA, eventString );
//					}
//				}
//			}
		}
	}
	
	private void sendCampaignIntent( Context context, Intent intent, String referrerString ) {
		intent.putExtra( "referrer", referrerString );
		new CampaignTrackingReceiver().onReceive( context, intent );
	}
	
	private void sendCampaignIntent( Context context, Intent intent ) {
		new CampaignTrackingReceiver().onReceive( context, intent );
	}
}
