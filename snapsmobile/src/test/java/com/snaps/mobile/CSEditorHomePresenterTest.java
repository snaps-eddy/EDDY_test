package com.snaps.mobile;

import android.content.Context;
import android.content.Intent;

import com.snaps.mobile.cseditor.CSEditorContract;
import com.snaps.mobile.cseditor.CSEditorHomePresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CSEditorHomePresenterTest {

    CSEditorHomePresenter presenter;

    @Mock
    CSEditorContract.View view;

    @Mock
    Context context;

    @Mock
    CSEditorContract.GetProjectDetailIntractor ittractor;

    @Before
    public void init() {
        presenter = new CSEditorHomePresenter(context, ittractor);
        presenter.setView(view);
    }

    @Test
    public void test_init() {
        assertThat(presenter, is(notNullValue()));
    }

    @Test
    public void test_choose_product() {
        presenter.onChooseProduct(0);

        verify(view).finishActivity(any(Intent.class));
    }


}
