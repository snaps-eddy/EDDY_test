package com.snaps.mobile.activity.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.snaps.common.utils.constant.Const_EKEY;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsProductListParams;

public class ListSubActivity extends ListBaseActivity {
    public static Intent getIntent(Context context, String title, boolean isSizeType, SnapsProductListParams params) {
        Intent intent = new Intent(context, ListSubActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable(Const_EKEY.NATIVE_UI_PARAMS, params);
        bundle.putString(Const_EKEY.WEBVIEW_TITLE, title);
        bundle.putBoolean(Const_EKEY.NATIVE_UI_SIZE_TYPE, isSizeType);
        intent.putExtras(bundle);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return intent;
    }

    @SuppressLint("InflateParams")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
    }
}
