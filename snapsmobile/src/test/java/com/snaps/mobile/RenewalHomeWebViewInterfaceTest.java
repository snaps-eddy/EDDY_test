package com.snaps.mobile;

import com.snaps.mobile.activity.home.RenewalHomePresenter;
import com.snaps.mobile.activity.home.model.RenewalHomeWebViewInterface;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RenewalHomeWebViewInterfaceTest {

    @Mock
    RenewalHomePresenter presenter;

    RenewalHomeWebViewInterface javascriptInterface;

    @Before
    public void setUp() {
        javascriptInterface = new RenewalHomeWebViewInterface(presenter);
    }

    @Test
    public void test_create_interface() {
        assertThat(javascriptInterface, is(notNullValue()));
    }

    @Test
    public void test_select_product() {
        JSONObject testJSON = new JSONObject();

        javascriptInterface.selectProduct(testJSON.toString());

        verify(presenter).onSelectProduct(eq(testJSON.toString()), eq(false));
    }

    @Test
    public void test_preview() {
        JSONObject testJSON = new JSONObject();

        javascriptInterface.preview(testJSON.toString());

        verify(presenter).onSelectProduct(eq(testJSON.toString()), eq(true));
    }

    @Test
    public void test_go_to_dialy_book() {
        javascriptInterface.gotoDailybook();

        verify(presenter).onGoToDailybook();
    }

    @Test
    public void test_go_to_setting() {
        javascriptInterface.gotoSetting();

        verify(presenter).onGoToSetting();
    }

    @Test
    public void test_set_user_info() {
        JSONObject testJSON = new JSONObject();

        javascriptInterface.setUserInfo(testJSON.toString());

        verify(presenter).onSetUserInfo(eq(testJSON.toString()));
    }

    @Test
    public void test_remove_user_info() {
        javascriptInterface.removeUserInfo();

        verify(presenter).onRemoveUserInfo();
    }

    @Test
    public void test_recommand_AI() {
        JSONObject testJSON = new JSONObject();

        javascriptInterface.recommandAI(testJSON.toString());

        verify(presenter).onRecommandAI(eq(testJSON.toString()));
    }

    @Test
    public void test_self_AI() {
        JSONObject testJSON = new JSONObject();

        javascriptInterface.selfAI(testJSON.toString());

        verify(presenter).onSelfAI(eq(testJSON.toString()));
    }

    @Test
    public void test_set_ai_sync() {
        javascriptInterface.setAISync();

        verify(presenter).onSetAISync();
    }

    @Test
    public void test_set_ai_sync_with_lte() {

        JSONObject testJSON = new JSONObject();

        javascriptInterface.setAISyncWithLTE(testJSON.toString());

        verify(presenter).onSetAISyncWithLTE(testJSON.toString());
    }

    @Test
    public void test_check_device_permission() throws JSONException {
        JSONObject testJSON = new JSONObject();
        testJSON.put("permissionName", "WIFI");

        javascriptInterface.checkDevicePermission(testJSON.toString());

        verify(presenter).onCheckDevicePermission(eq(testJSON.toString()));
    }

    @Test
    public void test_request_device_permission() {
        JSONObject testJSON = new JSONObject();

        javascriptInterface.requestDevicePermission(testJSON.toString());

        verify(presenter).onRequestDevicePermission(eq(testJSON.toString()));

    }

    @Test
    public void test_set_badge_count() {
        JSONObject testJSON = new JSONObject();

        javascriptInterface.setBadgeCount(testJSON.toString());

        verify(presenter, never()).onRequestDevicePermission(eq(testJSON.toString()));
    }

    @Test
    public void test_set_background_color() {
        JSONObject testJSON = new JSONObject();

        javascriptInterface.setBackgroundColor(testJSON.toString());

        verify(presenter, never()).onRequestDevicePermission(eq(testJSON.toString()));
    }
}
