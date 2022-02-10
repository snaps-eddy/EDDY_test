package com.snaps.mobile.activity.common.products.single_page_product;

import androidx.fragment.app.FragmentActivity;

import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.utils.constant.Config;
import com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditor;
import com.snaps.mobile.cseditor.CSEditorHomePresenter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Config.class
})
public class NewPhoneCaseEditorTest {

    private NewPhoneCaseEditor editor;

    @Mock
    private FragmentActivity fragmentActivity;

    @Before
    public void before() {
        editor = PowerMockito.spy(new NewPhoneCaseEditor(fragmentActivity));

        mockStatic(Config.class);
    }

    @Test
    public void test_init_base_editor() {
        assertThat(editor).isNotNull();
    }

    @Test
    public void GIVEN_WHEN_THEN_should() throws Exception {
        // given
        SnapsTemplate snapsTemplate = mock(SnapsTemplate.class);
        doReturn(snapsTemplate).when((SnapsProductBaseEditor) editor).loadTemplate(any());
        given(Config.isFromCart()).willReturn(false);
        CSEditorHomePresenter presenter = mock(CSEditorHomePresenter.class);

        // when
        SnapsTemplate template = editor.loadTemplate("");

        // then
        assertThat(template).isEqualTo(snapsTemplate);
    }

}
