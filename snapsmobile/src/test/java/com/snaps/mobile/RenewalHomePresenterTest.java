package com.snaps.mobile;

import android.content.Context;

import com.snaps.common.push.PushManager;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.mobile.activity.home.RenewalHomeContract;
import com.snaps.mobile.activity.home.RenewalHomePresenter;
import com.snaps.mobile.activity.home.SharedPreferenceRepository;
import com.snaps.mobile.service.ai.DeviceManager;

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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RenewalHomePresenterTest {

    @Mock
    private SharedPreferenceRepository spRepository;

    @Mock
    private DeviceManager deviceManager;

    @Mock
    private RenewalHomeContract.View view;

    @Mock
    private Context context;

    @Mock
    private PushManager pushService;

    private RenewalHomePresenter presenter;

    @Before
    public void setUp() {
        presenter = spy(new RenewalHomePresenter(spRepository, deviceManager, pushService));
        presenter.setView(view);
    }

    @Test
    public void test_create_presenter() {
        assertThat(presenter, is(notNullValue()));
    }

    @Test
    public void test_check_device_permission_READ_PHOTO() throws JSONException {
        JSONObject testJSON = new JSONObject();
        testJSON.put("permissionName", "READ_PHOTO");
        String jsURL = String.format("javascript:%s(\"%s\", '%s')", "setAppPermissionYN", "READ_PHOTO", "N");

        presenter.onCheckDevicePermission(testJSON.toString());

        verify(deviceManager).isGantedPermissionReadExternalStorage();
        verify(deviceManager).isGantedPermissionWriteExternalStorage();
        verify(view).loadWebPage(eq(jsURL));
    }

    @Test
    public void test_check_device_permission_PUSH_MESSAGE() throws JSONException {
        JSONObject testJSON = new JSONObject();
        testJSON.put("permissionName", "PUSH_MESSAGE");
        String jsURL = String.format("javascript:%s(\"%s\", '%s')", "setAppPermissionYN", "PUSH_MESSAGE", "N");

        presenter.onCheckDevicePermission(testJSON.toString());

        verify(spRepository).getBoolean(eq(Const_VALUE.KEY_GCM_PUSH_RECEIVE));
        verify(view).loadWebPage(eq(jsURL));
    }

    @Test
    public void test_check_device_permission_WIFI() throws JSONException {
        JSONObject testJSON = new JSONObject();
        testJSON.put("permissionName", "WIFI");
        String jsURL = String.format("javascript:%s(\"%s\", '%s')", "setAppPermissionYN", "WIFI", "N");

        presenter.onCheckDevicePermission(testJSON.toString());

        verify(deviceManager).isWiFiConnected();
        verify(view).loadWebPage(eq(jsURL));
    }

    @Test
    public void test_check_device_wrong_parameters() {
        presenter.onCheckDevicePermission(" ");

        verify(view, never()).loadWebPage(anyString());

        presenter.onCheckDevicePermission("");

        verify(view, never()).loadWebPage(anyString());

        presenter.onCheckDevicePermission(null);

        verify(view, never()).loadWebPage(anyString());
    }

    @Test
    public void test_set_user_info_isUseLTE() throws JSONException {
        JSONObject testJSON = new JSONObject();
        testJSON.put("isUseLTE", false); //LoginManager 쪽 테스트 불가.

        presenter.onSetUserInfo(testJSON.toString());

        verify(view).putUserInfo(testJSON.toString());
        verify(view).syncWebViewCookie();
    }

    @Test
    public void test_set_user_info_isUseLTE_wrong_parameters() {
        presenter.onSetUserInfo(" ");

        verify(view, never()).putUserInfo(" ");
        verify(view, never()).syncWebViewCookie();

        presenter.onSetUserInfo("");

        verify(view, never()).putUserInfo("");
        verify(view, never()).syncWebViewCookie();

        presenter.onSetUserInfo(null);

        verify(view, never()).putUserInfo(null);
        verify(view, never()).syncWebViewCookie();
    }

    //        view.loadWebPage(externalURL, 500L);
//        view.showToast("Not Event target !");
    @Test
    public void test_on_get_external_url() {
        String prmchnlcode = "KOR0031";
        String deviceCode = "KOR0031";
        String url = "http://kr.snaps.com";

        presenter.onGetExternalURL(url, prmchnlcode, deviceCode);

        verify(view).loadWebPage(url, 500L);
    }

    @Test
    public void test_on_get_external_url_not_matched() {
        String prmchnlcode = "KOR0031";
        String deviceCode = "JPN0031";
        String url = "http://jp.snaps.com";

        presenter.onGetExternalURL(url, prmchnlcode, deviceCode);

        verify(view).showAlertDialog(R.string.deeplink_error_language_different);
    }

    @Test
    public void given_json_param_phone_case_when_select_product() {
        // given
        String rawJsonParams = "{\"prmChnlCode\":\"KOR0031\",\"prmLangCode\":\"KOR\",\"prmProdCode\":\"00802700020020\",\"prmTmplCode\":\"045021017371\",\"prmTmplId\":\"BGS20MT_full\",\"prmPaperCode\":\"160001\",\"prmGlossyType\":\"M\",\"frameid\":\"045014000254\",\"productCode\":\"00802700020020\",\"paperCode\":\"160001\",\"glossytype\":\"M\",\"papertype\":\"160001\",\"projectCount\":1}";

        // when
        presenter.onSelectProduct(rawJsonParams, false);

        // then
        then(view).should().loadProductPage("snapsapp://selectProduct?projectCount=1&paperCode=160001&frameid=045014000254&prmTmplCode=045021017371&prmPaperCode=160001&prmTmplId=BGS20MT_full&productCode=00802700020020&papertype=160001&prmChnlCode=KOR0031&prmLangCode=KOR&prmProdCode=00802700020020&prmGlossyType=M&glossytype=M&");
    }
}
