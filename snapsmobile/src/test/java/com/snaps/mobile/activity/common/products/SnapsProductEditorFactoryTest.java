package com.snaps.mobile.activity.common.products;

import android.content.Intent;
import androidx.fragment.app.FragmentActivity;

import com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants;
import com.snaps.mobile.activity.common.interfacies.SnapsProductEditorAPI;
import com.snaps.mobile.activity.common.products.single_page_product.NewPhoneCaseEditor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.snaps.mobile.activity.common.interfacies.SnapsProductEditConstants.eSnapsProductKind.NEW_PHONE_CASE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(PowerMockRunner.class)
@PrepareForTest(
        SnapsProductEditorFactory.class
)
public class SnapsProductEditorFactoryTest {

    @Mock
    public FragmentActivity fragmentActivity;

    @Test
    public void test_create_product_editor() {
        // given
        Intent intent = PowerMockito.mock(Intent.class);
        given(intent.getIntExtra(SnapsProductEditConstants.EXTRA_NAME_PRODUCT_KIND, -1)).willReturn(NEW_PHONE_CASE.ordinal());
        given(fragmentActivity.getIntent()).willReturn(intent);

        // when
        SnapsProductEditorAPI editor = SnapsProductEditorFactory.createProductEditor(fragmentActivity);

        // then
        assertThat(editor).isNotNull();
        assertThat(editor).isInstanceOf(NewPhoneCaseEditor.class);
    }


}
