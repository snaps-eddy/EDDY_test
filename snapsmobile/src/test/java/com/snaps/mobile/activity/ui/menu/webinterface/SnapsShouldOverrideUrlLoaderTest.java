package com.snaps.mobile.activity.ui.menu.webinterface;

import android.app.Activity;
import android.webkit.WebView;

import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventSelectProductHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SnapsWebEventHandlerFactory.class})
public class SnapsShouldOverrideUrlLoaderTest {

    /**
     * 어처구니 없지만, 장바구니 열땐 cartloader, 다른 명령 url 은 urlLoader를 사용한다.
     */
    private SnapsShouldOverrideUrlLoader urlLoader;

    @Mock
    Activity activity;

    @Mock
    SnapsWebEventSelectProductHandler webEventBaseHandler;

    @Before
    public void before() {
        IFacebook iFacebook = mock(IFacebook.class);
        IKakao iKakao = mock(IKakao.class);

        mockStatic(SnapsWebEventHandlerFactory.class);
        given(SnapsWebEventHandlerFactory.createWebEventHandler(eq(activity), any())).willReturn(webEventBaseHandler);

        urlLoader = PowerMockito.spy(new SnapsShouldOverrideUrlLoader(activity, iFacebook, iKakao)); // 일반은 생성자로 생성
    }

    @Test
    public void initialize_url_loader() {
        assertThat(urlLoader).isNotNull();
    }

    @Test
    public void given_select_product() {
        // given
        WebView webView = mock(WebView.class);
        String snapsUrl = "snapsapp://selectProduct?projectCount=1&paperCode=160001&frameid=045014000254&prmTmplCode=045021017371&prmPaperCode=160001&prmTmplId=BGS20MT_full&productCode=00802700020020&papertype=160001&prmChnlCode=KOR0031&prmLangCode=KOR&prmProdCode=00802700020020&prmGlossyType=M&glossytype=M&";

        // when
        urlLoader.shouldOverrideUrlLoading(webView, snapsUrl, null); // Refactor !!! Do not pass android.view

        // then
        then(webEventBaseHandler).should().handleEvent();
    }

}
