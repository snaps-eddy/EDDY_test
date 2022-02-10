package com.snaps.mobile.kr;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.ads.conversiontracking.AdWordsConversionReporter;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.TrackerName;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.component.SnapsBroadcastReceiver;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.util.HashMap;

/**
 * Created by ysjeong on 2017. 8. 16..
 */

public class SnapsThirdPartyTrackers {
    private static final String PROPERTY_ID = "UA-62310709-2";
    private HashMap<TrackerName, Tracker> trackers = null;
    private SnapsBroadcastReceiver receiver = null;

    public static SnapsThirdPartyTrackers createInstanceWithRegisterReceiver(Context context, SnapsBroadcastReceiver.ImpSnapsBroadcastReceiver impSnapsBroadcastReceiver) throws Exception {

        SnapsThirdPartyTrackers snapsThirdPartyTrackers = new SnapsThirdPartyTrackers();

        snapsThirdPartyTrackers.trackers = new HashMap<TrackerName, Tracker>();

        // Tracker receiver등록.
        snapsThirdPartyTrackers.receiver = new SnapsBroadcastReceiver();
        snapsThirdPartyTrackers.receiver.setImpRecevice(impSnapsBroadcastReceiver);
        IntentFilter filter = new IntentFilter(Const_VALUE.SNAPS_ACTIVITY_TRACKER_ACTION);
        filter.addAction(Const_VALUE.SNAPS_EVENT_TRACKER_ACTION);

        context.registerReceiver(snapsThirdPartyTrackers.receiver, filter);

        return snapsThirdPartyTrackers;
    }

    public static void registerAdWords(Context context) {
        AdWordsConversionReporter.reportWithConversionId(context,
                Const_VALUE.GOOGLE_ADWORDS_CONVERSION_ID, Const_VALUE.GOOGLE_ADWORDS_CONVERSION_LABEL, Const_VALUE.GOOGLE_ADWORDS_CONVERSION_VALUE, false);
    }

//    public static void registerIgawAutoSessionTracker(Application applicaton) {
//        IgawCommon.autoSessionTracking(applicaton);
//    }

    public void initGoogleAnalyticsTracker(Context context) {
        getTracker(context, TrackerName.GLOBAL_TRACKER).enableAutoActivityTracking(
                true);

        getTracker(context, TrackerName.APP_TRACKER).enableAdvertisingIdCollection(true);
    }

    private synchronized Tracker getTracker(Context context, TrackerName trackerId) {
        if (!trackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics
                    .newTracker(PROPERTY_ID)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics
                    .newTracker(R.xml.global_tracker) : analytics
                    .newTracker(R.xml.ecommerce_tracker);
            trackers.put(trackerId, t);
        }

        return trackers.get(trackerId);
    }

    public void handleReceiveData(Context context, Intent intent) {
        if (SnapsTPAppManager.isThirdPartyApp(context) || !Setting.getBoolean(context, Const_VALUE.KEY_SEND_GOOGLE_ANALYTICS_DATA))
            return;

        if (intent.getAction()
                .equals(Const_VALUE.SNAPS_ACTIVITY_TRACKER_ACTION)) {
            Tracker t = getTracker(context, TrackerName.APP_TRACKER);
            t.setScreenName(intent.getStringExtra("activity_name"));
            t.send(new HitBuilders.AppViewBuilder().build());
        } else if (intent.getAction().equals(
                Const_VALUE.SNAPS_EVENT_TRACKER_ACTION)) {
            Tracker t = getTracker(context, TrackerName.APP_TRACKER);
            t.send(new HitBuilders.EventBuilder(intent
                    .getStringExtra("event_category"), intent
                    .getStringExtra("event_action")).build());
        }
    }

    public SnapsBroadcastReceiver getReceiver() {
        return receiver;
    }
}
