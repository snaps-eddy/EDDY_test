package com.snaps.mobile.utils.select_product_junction.junctions;

import android.app.Activity;

import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.utils.select_product_junction.SnapsProductAttribute;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Const_PRODUCT.class
})
public class SnapsSelectProductJunctionForNewPhoneCaseTest {

    private SnapsSelectProductJunctionForNewPhoneCase phoneCaseJunction;

    @Mock
    public Activity activity;

    @Mock
    public SnapsProductAttribute attribute;

    @Before
    public void before() {
        // 객체생성의 책임은 팩토리가 가지고 있기에 그냥 new 로 객체 생성해서 테스트.
        phoneCaseJunction = new SnapsSelectProductJunctionForNewPhoneCase();
        mockStatic(Const_PRODUCT.class);
    }

    @Test
    public void test_init_phonecase() {
        assertThat(phoneCaseJunction).isNotNull();
    }

    /**
     * newphonecase junction 으로 오는 상품은 새로운 하드케이스, 범퍼케이스, uv 케이스이다.
     * uv 케이스는 파라미터중 paperCode, frametype, backType 이 필요하다.
     * 하드케이스와 범퍼 케이스는 paperCode 값만 있으면 된다.
     * 하지만 프론트엔드에서 파라미터가 넘어올 떄, 하드케이스, 범퍼케이스임에도 frametype, backtype 이 넘어오는 경우가 있어서
     * 이를 제거하는 로직이 junction 에 추가되었고 이를 테스트 하는 코드.
     */
    @Test
    public void GIVEN_new_uv_phone_case_WHEN_startMakeProduct_THEN_should_startActivity() {
        // given
        given(Const_PRODUCT.isUvPhoneCaseProduct()).willReturn(true);
        HashMap<String, String> mockMap = new HashMap<>();
        mockMap.put("backType", "404001");
        mockMap.put("frametype", "404001");
        given(attribute.getUrlData()).willReturn(mockMap);

        // when
        phoneCaseJunction.startMakeProduct(activity, attribute);

        // then
        assertThat(attribute.getUrlData().get("backType")).isEqualTo("404001");
        assertThat(attribute.getUrlData().get("frametype")).isEqualTo("404001");
    }

    @Test
    public void GIVEN_not_uv_phone_case_WHEN_startMakeProduct_THEN_should_remove_backtype_and_frametype() {
        // given
        given(Const_PRODUCT.isPrintPhoneCaseProduct()).willReturn(true);
        HashMap<String, String> mockMap = new HashMap<>();
        mockMap.put("backType", "404001");
        mockMap.put("frametype", "404001");
        given(attribute.getUrlData()).willReturn(mockMap);

        // when
        phoneCaseJunction.startMakeProduct(activity, attribute);

        // then
        assertThat(attribute.getUrlData().get("backType")).isNull();
        assertThat(attribute.getUrlData().get("frametype")).isNull();
    }
}
