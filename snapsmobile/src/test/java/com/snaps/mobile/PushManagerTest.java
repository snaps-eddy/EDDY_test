package com.snaps.mobile;

import android.content.Context;

import com.snaps.common.push.GCMContent;
import com.snaps.common.push.PushManager;
import com.snaps.common.push.PushNotificationService;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.service.ai.DeviceManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Setting.class,
        Dlog.class
})
public class PushManagerTest {

    private PushManager pushManager;

    @Mock
    private Context context;

    @Mock
    private DeviceManager deviceManager;

    @Mock
    private PushNotificationService pushNotificationService;

    @Before
    public void before() {
        pushManager = PowerMockito.spy(new PushManager(context));

        PowerMockito.mockStatic(Setting.class);
        PowerMockito.mockStatic(Dlog.class);
    }

    @Test
    public void test_init_push_manager() {
        assertThat(pushManager).isNotNull();
    }

    @Test
    public void given_null_message_when_onMessage_then_return_process() {
        // given
        given(Setting.getBoolean(context, Const_VALUE.KEY_GCM_PUSH_RECEIVE, false)).willReturn(true);

        // when
        pushManager.onMessage(null, deviceManager, pushNotificationService);

        // then
        verifyStatic(Dlog.class);
        Dlog.w(anyString(), eq("Ignored message, MessageDataMap is null."));
    }

    @Test
    public void given_empty_message_when_onMessage_then_return_process() {
        // given
        Map<String, String> messageData = new HashMap<>();
        given(Setting.getBoolean(context, Const_VALUE.KEY_GCM_PUSH_RECEIVE, false)).willReturn(true);

        // when
        pushManager.onMessage(messageData, deviceManager, pushNotificationService);

        // then
        verifyStatic(Dlog.class);
        Dlog.w(anyString(), eq("Failed parsing message data."));
    }

    @Test
    public void given_disallow_push_empty_message_when_onMessage_then_return_process() {
        // given
        Map<String, String> messageData = new HashMap<>();
        given(Setting.getBoolean(context, Const_VALUE.KEY_GCM_PUSH_RECEIVE, false)).willReturn(false);

        // when
        pushManager.onMessage(messageData, deviceManager, pushNotificationService);

        // then
        verifyStatic(Dlog.class);
        Dlog.w(anyString(), eq("Device not allow push message."));
    }

    @Test
    public void given_analysis_complete_allow_push_app_foreground_when_onmessage_then_show_toast() {
        // given
        String popMessage = "[2026년 12월, 남극] 그날의 추억을 확인하세요., message=[2026년 12월, 남극] 그날의 추억을 확인하세요.";
        given(Setting.getBoolean(context, Const_VALUE.KEY_GCM_PUSH_RECEIVE, false)).willReturn(true);
        given(deviceManager.isAppForeground()).willReturn(true);
        Map<String, String> messageData = new HashMap<>();
        messageData.put("targetUrl", "P0014");
        messageData.put("resendNo", "0");
        messageData.put("imgInclude", "N");
        messageData.put("title", "스냅스 AI 추천 포토북");
        messageData.put("popMessage", popMessage);
        messageData.put("message", "[2026년 12월, 남극] 그날의 추억을 확인하세요.");
        messageData.put("rcvType", "221006");
        messageData.put("badgeCount", "0");
        messageData.put("msgType", "1");
        messageData.put("targetFullUrl", "snapsapp://cmd=gotoPage&pageNum=P0014&url=https://kr.snaps.com/ai/intro");

        // when
        pushManager.onMessage(messageData, deviceManager, pushNotificationService);

        // then
        then(pushNotificationService).should().showToast(popMessage);
    }

    @Test
    public void given_analysis_complete_allow_push_app_background_when_onmessage_then_notify_statusbar() {
        // givne
        given(Setting.getBoolean(context, Const_VALUE.KEY_GCM_PUSH_RECEIVE, false)).willReturn(true);
        given(deviceManager.isAppForeground()).willReturn(false);
        Map<String, String> messageData = new HashMap<>();
        messageData.put("targetUrl", "P0014");
        messageData.put("resendNo", "0");
        messageData.put("imgInclude", "N");
        messageData.put("title", "스냅스 AI 추천 포토북");
        messageData.put("popMessage", "[2026년 12월, 남극] 그날의 추억을 확인하세요., message=[2026년 12월, 남극] 그날의 추억을 확인하세요.");
        messageData.put("message", "[2026년 12월, 남극] 그날의 추억을 확인하세요.");
        messageData.put("rcvType", "221006");
        messageData.put("badgeCount", "0");
        messageData.put("msgType", "1");
        messageData.put("targetFullUrl", "snapsapp://cmd=gotoPage&pageNum=P0014&url=https://kr.snaps.com/ai/intro");

        // when
        pushManager.onMessage(messageData, deviceManager, pushNotificationService);

        // then
        then(pushNotificationService).should().notifyInStatusBar(any(GCMContent.class), eq(true));
    }

    @Test
    public void given_deeplink_allow_push_when_onmessage_then_should_notify_statusbar() {
        // given
        given(Setting.getBoolean(context, Const_VALUE.KEY_GCM_PUSH_RECEIVE, false)).willReturn(true);
        Map<String, String> messageData = new HashMap<>();
        messageData.put("targetUrl", "P0014");
        messageData.put("resendNo", "0");
        messageData.put("imgInclude", "N");
        messageData.put("title", "스냅스 AI 추천 포토북");
        messageData.put("message", "[2026년 12월, 남극] 그날의 추억을 확인하세요.");
        messageData.put("rcvType", "221001");
        messageData.put("badgeCount", "0");
        messageData.put("msgType", "1");
        messageData.put("targetFullUrl", "snapsapp://cmd=gotoPage&pageNum=P0014&url=https://stg-kr.snaps.com/overview/product");

        // when
        pushManager.onMessage(messageData, deviceManager, pushNotificationService);

        // then
        then(pushNotificationService).should().notifyInStatusBar(any(GCMContent.class), eq(true));
    }

    @Test
    public void given_silent_push_allow_push_when_onmessage_then_should_not_do() {
        // given
        given(Setting.getBoolean(context, Const_VALUE.KEY_GCM_PUSH_RECEIVE, false)).willReturn(true);
        Map<String, String> messageData = new HashMap<>();
        messageData.put("targetUrl", "");
        messageData.put("resendNo", "0");
        messageData.put("imgInclude", "N");
        messageData.put("title", "");
        messageData.put("message", "");
        messageData.put("rcvType", "221999");
        messageData.put("badgeCount", "0");
        messageData.put("msgType", "1");
        messageData.put("targetFullUrl", "");

        // when
        pushManager.onMessage(messageData, deviceManager, pushNotificationService);

        // then
        then(pushNotificationService).should(never()).showToast(any());
    }

}
