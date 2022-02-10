package com.snaps.mobile.utils.select_product_junction;

import com.snaps.mobile.utils.select_product_junction.interfaces.ISnapsProductLauncher;
import com.snaps.mobile.utils.select_product_junction.junctions.ProductCodeMatcher;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForNewPhoneCase;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForPhoneCase;
import com.snaps.mobile.utils.select_product_junction.junctions.SnapsSelectProductJunctionForPoster;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_BUMPER_PHONE_CASE_GROUP;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_HARD_PHONE_CASE_GROUP;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_PRINT_HARD_PHONE_CASE_GROUP;
import static com.snaps.common.utils.constant.Const_PRODUCT.PRODUCT_UV_PHONE_CASE_GROUP;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
public class SnapsSelectProductJunctionFactoryTest {

    private SnapsSelectProductJunctionFactory factory;

    @Before
    public void before() {
        factory = SnapsSelectProductJunctionFactory.getInstance();
    }

    @Test
    public void test_creat_product_launcher() {
        // when
        ISnapsProductLauncher launcher = factory.createProductLauncher("00800800220001");

        // then
        assertThat(launcher).isInstanceOfAny(SnapsSelectProductJunctionForPoster.class);
    }

    // @Marko 2020.08.18 현 시점에 객체 생성 코드를 전부 테스트 하는건 무의미 하다고 보여져서 새로 추가되는 상품코드나 특이한 코드가 발견되면 테스트 케이스 작성하자.
    @Test
    public void given_legacy_hard_phonecase_product_code_then_create_legacy_phonecase_junction() {
        // given
        String newPhoneCaseProductCode = PRODUCT_HARD_PHONE_CASE_GROUP + "0001";

        // when
        ISnapsProductLauncher launcher = factory.createProductLauncher(newPhoneCaseProductCode);

        // then
        assertThat(launcher).isInstanceOf(SnapsSelectProductJunctionForPhoneCase.class);
    }

    @Test
    public void given_legacy_bumper_phonecase_product_code_then_create_legacy_phonecase_junction() {
        // given
        String newPhoneCaseProductCode = PRODUCT_BUMPER_PHONE_CASE_GROUP + "0001";

        // when
        ISnapsProductLauncher launcher = factory.createProductLauncher(newPhoneCaseProductCode);

        // then
        assertThat(launcher).isInstanceOf(SnapsSelectProductJunctionForPhoneCase.class);
    }

    @Test
    public void given_new_phonecase_product_code_then_create_new_phonecase_junction() {
        // given
        String newPhoneCaseProductCode = PRODUCT_UV_PHONE_CASE_GROUP + "0001";

        // when
        ISnapsProductLauncher launcher = factory.createProductLauncher(newPhoneCaseProductCode);

        // then
        assertThat(launcher).isInstanceOf(SnapsSelectProductJunctionForNewPhoneCase.class);
    }

    @Test
    public void given_new_hard_phonecase_product_code_then_create_new_phonecase_junction() {
        // given
        String newPhoneCaseProductCode = PRODUCT_PRINT_HARD_PHONE_CASE_GROUP + "0001";

        // when
        ISnapsProductLauncher launcher = factory.createProductLauncher(newPhoneCaseProductCode);

        // then
        assertThat(launcher).isInstanceOf(SnapsSelectProductJunctionForNewPhoneCase.class);
    }

}
