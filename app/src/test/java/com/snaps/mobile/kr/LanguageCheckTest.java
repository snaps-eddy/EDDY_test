package com.snaps.mobile.kr;

import android.content.Context;
import android.content.SharedPreferences;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.ui.SnapsLanguageUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(MockitoJUnitRunner.class)
public class LanguageCheckTest {

    @Mock
    Context context;

    @Mock
    SharedPreferences sp;

    @Before
    public void setUp() {
        when(context.getSharedPreferences((Config.isSnapsBitween() ? "snapsBetweenSetting" : "snapsSetting"), Context.MODE_PRIVATE)).thenReturn(sp);
    }

    @Test
    public void test_applied_korean() {
        when(sp.getString(eq(Const_VALUE.KEY_APPLIED_LANGUAGE), any())).thenReturn(Locale.KOREAN.getLanguage());

        boolean isApplied = SnapsLanguageUtil.isAppliedServiceableLanguage(context);

        assertThat(isApplied, is(true));

    }

    @Test
    public void test_applied_jpn() {
        when(sp.getString(eq(Const_VALUE.KEY_APPLIED_LANGUAGE), any())).thenReturn(Locale.JAPANESE.getLanguage());

        boolean isApplied = SnapsLanguageUtil.isAppliedServiceableLanguage(context);

        assertThat(isApplied, is(true));

    }

    @Test
    public void test_applied_chn() {
        when(sp.getString(eq(Const_VALUE.KEY_APPLIED_LANGUAGE), any())).thenReturn(Locale.CHINESE.getLanguage());

        boolean isApplied = SnapsLanguageUtil.isAppliedServiceableLanguage(context);

        assertThat(isApplied, is(false));

    }

    @Test
    public void test_applied_eng() {
        when(sp.getString(eq(Const_VALUE.KEY_APPLIED_LANGUAGE), any())).thenReturn(Locale.CHINESE.getLanguage());

        boolean isApplied = SnapsLanguageUtil.isAppliedServiceableLanguage(context);

        assertThat(isApplied, is(false));
    }

    @Test
    public void test_appied_null(){
        when(sp.getString(eq(Const_VALUE.KEY_APPLIED_LANGUAGE), any())).thenReturn(null);

        boolean isApplied = SnapsLanguageUtil.isAppliedServiceableLanguage(context);

        assertThat(isApplied, is(false));
    }

    @Test
    public void test_appied_empty(){
        when(sp.getString(eq(Const_VALUE.KEY_APPLIED_LANGUAGE), any())).thenReturn("");

        boolean isApplied = SnapsLanguageUtil.isAppliedServiceableLanguage(context);

        assertThat(isApplied, is(false));
    }

    @Test
    public void test_applied_wrong_type(){
        when(sp.getString(eq(Const_VALUE.KEY_APPLIED_LANGUAGE), any())).thenReturn("234354-2rds");

        boolean isApplied = SnapsLanguageUtil.isAppliedServiceableLanguage(context);

        assertThat(isApplied, is(false));
    }

}