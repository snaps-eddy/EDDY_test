package com.snaps.common.structure;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.order.order_v2.datas.SnapsOrderAttribute;
import com.snaps.mobile.order.order_v2.interfacies.SnapsOrderActivityBridge;
import com.snaps.mobile.order.order_v2.task.prepare_process.SnapsOrderPrepareBaseHandler;
import com.snaps.mobile.order.order_v2.task.prepare_process.SnapsOrderPrepareHandlerFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Const_PRODUCT.class,
        Config.class,
        PhotobookCommonUtils.class
//        PhotobookCommonUtils.findEmptyPageIdxWithPageList(attribute.getPageList())
})
public class NewPhoneCaseMakeOptionXMLTest {

    private SnapsOrderPrepareBaseHandler orderPrepareHandler;

    @Mock
    SnapsOrderAttribute orderAttribute;

    @Mock
    SnapsOrderActivityBridge orderActivityBridge;

    SnapsTemplate snapsTemplate;

    @Before
    public void before() {
        snapsTemplate = new SnapsTemplate();

        given(orderAttribute.getSnapsTemplate()).willReturn(snapsTemplate);

        mockStatic(Const_PRODUCT.class);
        mockStatic(PhotobookCommonUtils.class);
    }

    @Test
    public void GIVEN_has_empty_image_and_new_phone_case_WHEN_setProductYN_THEN_getF_PRO_YORN_should_return_Y() {
        // given
        given(PhotobookCommonUtils.findEmptyPageIdxWithPageList(any())).willReturn(0);
        given(Const_PRODUCT.isUvPhoneCaseProduct()).willReturn(true);
        orderPrepareHandler = SnapsOrderPrepareHandlerFactory.createOrderPrepareHandler(orderAttribute, orderActivityBridge);

        // when
        orderPrepareHandler.setProductYN();

        // then
        assertThat(snapsTemplate.getF_PRO_YORN()).isEqualTo("Y");
    }

    @Test
    public void GIVEN_has_not_empty_image_and_new_phone_case_WHEN_setProductYN_THEN_getF_PRO_YORN_should_return_Y() {
        // given
        given(PhotobookCommonUtils.findEmptyPageIdxWithPageList(any())).willReturn(-1);
        given(Const_PRODUCT.isPrintPhoneCaseProduct()).willReturn(true);
        orderPrepareHandler = SnapsOrderPrepareHandlerFactory.createOrderPrepareHandler(orderAttribute, orderActivityBridge);

        // when
        orderPrepareHandler.setProductYN();

        // then
        assertThat(snapsTemplate.getF_PRO_YORN()).isEqualTo("Y");
    }

    @Test
    public void GIVEN_has_empty_image_and_legacy_phone_case_WHEN_setProductYN_THEN_getF_PRO_YORN_should_return_N() {
        // given
        given(PhotobookCommonUtils.findEmptyPageIdxWithPageList(any())).willReturn(0);
        given(Const_PRODUCT.isLegacyPhoneCaseProduct()).willReturn(true);
        orderPrepareHandler = SnapsOrderPrepareHandlerFactory.createOrderPrepareHandler(orderAttribute, orderActivityBridge);

        // when
        orderPrepareHandler.setProductYN();

        // then
        assertThat(snapsTemplate.getF_PRO_YORN()).isEqualTo("N");
    }

    @Test
    public void GIVEN_has_not_empty_image_and_legacy_phone_case_WHEN_setProductYN_THEN_getF_PRO_YORN_should_return_Y() {
        // given
        given(PhotobookCommonUtils.findEmptyPageIdxWithPageList(any())).willReturn(-1);
        given(Const_PRODUCT.isLegacyPhoneCaseProduct()).willReturn(true);
        orderPrepareHandler = SnapsOrderPrepareHandlerFactory.createOrderPrepareHandler(orderAttribute, orderActivityBridge);

        // when
        orderPrepareHandler.setProductYN();

        // then
        assertThat(snapsTemplate.getF_PRO_YORN()).isEqualTo("Y");
    }
}
