package com.snaps.mobile.activity.themebook.design_list;

import android.os.Bundle;

import com.snaps.mobile.activity.themebook.design_list.Interface.ThemeDesignListAPI;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;

import errorhandle.SnapsAssert;

/**
 * Created by kimduckwon on 2017. 11. 29..
 */

public class NewThemeDesignListActivity extends SnapsBaseFragmentActivity {
    private ThemeDesignListAPI themeDesignListAPI;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createProductEditor();
        themeDesignListAPI.onCreate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            themeDesignListAPI.onResume();
        } catch (Exception e) { SnapsAssert.assertException(this,e); }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            themeDesignListAPI.onPause();
        } catch (Exception e) { SnapsAssert.assertException(this,e); }
    }

    private void createProductEditor() {
        themeDesignListAPI = NewThemeDesignListFactory.createDesignList(this);
        SnapsAssert.assertNotNull(themeDesignListAPI);
    }
}
