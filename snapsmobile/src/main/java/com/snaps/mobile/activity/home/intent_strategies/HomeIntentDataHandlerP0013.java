package com.snaps.mobile.activity.home.intent_strategies;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.home.RenewalHomeActivity;
import com.snaps.mobile.activity.home.model.HomeIntentHandleData;
import com.snaps.mobile.activity.webview.UIWebviewActivity;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;
import com.snaps.mobile.utils.ui.UrlUtil;

import static com.snaps.common.utils.constant.ISnapsConfigConstants.SNAPS_EXEC_GOTO_HOST;
import static com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventBaseHandler.CART_PRODUCT_LOAD_URL;
import static com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsWebEventBaseHandler.SCHMA;

/**
 * Created by ysjeong on 2017. 8. 17..
 */

public class HomeIntentDataHandlerP0013 extends HomeIntentDataHandlerBase {
    public HomeIntentDataHandlerP0013(Activity activity, HomeIntentHandleData intentHandleData) {
        super(activity, intentHandleData);
    }

    @Override
    public void performGoToFunction() throws Exception {
        int _cart_count = Setting.getInt(getActivity(), Const_VALUE.KEY_CART_COUNT);
        String cartCount = Integer.toString(_cart_count);
        String url = SnapsTPAppManager.getCartListUrl(getActivity(), getActivity().getString(R.string.cart), cartCount);
        Intent intent = RenewalHomeActivity.getIntent(getActivity(), getActivity().getString(R.string.cart), url);

        String cartUrl = createSnapsSchemeUrlWithIntent(getIntent());
        intent.putExtra(CART_PRODUCT_LOAD_URL, cartUrl);
        getActivity().startActivity(intent);
    }

    private String createSnapsSchemeUrlWithIntent(Intent intent) {
        if (intent == null) return "";

        if (isExecGotoIntent(intent)) {
            String dataString = intent.getDataString();
            if (StringUtil.isEmpty(dataString)) return "";

            Uri uri = intent.getData();
            if (uri == null) return "";

            String host = uri.getHost();
            if (host == null || !host.equalsIgnoreCase(SNAPS_EXEC_GOTO_HOST)) return "";

            if (StringUtil.isEmpty(dataString) || !dataString.contains("?")) return "";

            dataString = dataString.substring(dataString.indexOf("?")+1);

            return SCHMA + dataString;
        }

        return UrlUtil.getFullUrlFromIntent(getIntent());
    }

    private boolean isExecGotoIntent(Intent intent) {
        return intent != null && intent.getData() != null && intent.getData().getHost() != null && intent.getData().getHost().equalsIgnoreCase(SNAPS_EXEC_GOTO_HOST);
    }
}
