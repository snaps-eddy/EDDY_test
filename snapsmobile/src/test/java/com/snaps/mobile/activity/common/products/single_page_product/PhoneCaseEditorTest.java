package com.snaps.mobile.activity.common.products.single_page_product;

import androidx.fragment.app.FragmentActivity;

import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.activity.common.products.base.PhoneCaseEditorHandler;
import com.snaps.mobile.activity.common.products.base.SnapsProductBaseEditorHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Const_PRODUCT.class
})
public class PhoneCaseEditorTest {

    private PhoneCaseEditor editor;

    @Mock
    private FragmentActivity fragmentActivity;

    @Before
    public void before() {
        editor = new PhoneCaseEditor(fragmentActivity);

        mockStatic(Const_PRODUCT.class);
    }

    @Test
    public void init_creation() {
        assertThat(editor).isNotNull();
    }

    @Test
    public void GIVEN_legacy_prodcut_WHEN_getBaseEditorHandler_THEN_should_return_PhoneCaseEditorHandler() {
        // given
        given(Const_PRODUCT.isLegacyPhoneCaseProduct()).willReturn(true);

        // when
        SnapsProductBaseEditorHandler baseeditor = editor.getBaseEditorHandler();

        // then
        assertThat(baseeditor).isInstanceOf(PhoneCaseEditorHandler.class);
    }

    @Test
    public void GIVEN_new_print_prodcut_WHEN_getBaseEditorHandler_THEN_should_return_SnapsProductBaseEditorHandler() {
        // given
        given(Const_PRODUCT.isLegacyPhoneCaseProduct()).willReturn(false);

        // when
        SnapsProductBaseEditorHandler baseeditor = editor.getBaseEditorHandler();

        // then
        assertThat(baseeditor).isNotInstanceOf(PhoneCaseEditorHandler.class);
    }

}
