package com.snaps.mobile.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.snaps.common.utils.ui.UIUtil;

public class SnapsBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UIUtil.applyLanguage(this);
        super.onCreate(savedInstanceState);
    }
}
