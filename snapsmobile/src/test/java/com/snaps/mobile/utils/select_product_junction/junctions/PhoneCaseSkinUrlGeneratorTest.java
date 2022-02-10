package com.snaps.mobile.utils.select_product_junction.junctions;

import com.snaps.mobile.utils.select_product_junction.PhoneCaseSkinUrlGenerator;
import com.snaps.mobile.utils.select_product_junction.SkinType;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
public class PhoneCaseSkinUrlGeneratorTest {

//    케이스 스킨 용 : https://www.snaps.com/Upload/Data1/Resource/skin/editor/00802900010001/404003.png
//    디바이스 스킨: https://www.snaps.com/Upload/Data1/Resource/skin/device/00802900010001/145041.png
//    신마스크 스킨: https://www.snaps.com/Upload/Data1/Resource/scene_mask/phone_case/galaxy-note-10-plus.svg
//    디자인리스트 & 장바구니용 케이스 스킨: https://www.snaps.com/Upload/Data1/Resource/skin/designList/00802900010001/404003.png

    @Test
    public void GIVEN_uv_case_WHEN_generateSkinUrl_THEN_url_should_not_null() {
        // given
        String productCode = "00802900010001";
        String caseCode = "404003";
        String deviceColorCode = "145051";

        // when
        String caseSkinUrl = PhoneCaseSkinUrlGenerator.generateSkinUrl(SkinType.Case, productCode, caseCode);
        String deviceSkinUrl = PhoneCaseSkinUrlGenerator.generateSkinUrl(SkinType.Device, productCode, deviceColorCode);
        String cartSkinUrl = PhoneCaseSkinUrlGenerator.generateSkinUrl(SkinType.CartThumnailCase, productCode, caseCode);


        // then
        assertThat(caseSkinUrl).isEqualTo("/Upload/Data1/Resource/skin/editor/00802900010001/404003.png");
        assertThat(deviceSkinUrl).isEqualTo("/Upload/Data1/Resource/skin/device/00802900010001/145051.png");
        assertThat(cartSkinUrl).isEqualTo("/Upload/Data1/Resource/skin/designList/00802900010001/404003.png");
    }

    @Test
    public void GIVEN_hard_case_WHEN_generateSkinUrl_THEN_url_should_not_null() {
        // given
        String productCode = "00802900020001";
        String caseCode = "404001";
        String deviceColorCode = " ";

        // when
        String caseSkinUrl = PhoneCaseSkinUrlGenerator.generateSkinUrl(SkinType.Case, productCode, caseCode);
        String deviceSkinUrl = PhoneCaseSkinUrlGenerator.generateSkinUrl(SkinType.Device, productCode, deviceColorCode);
        String cartSkinUrl = PhoneCaseSkinUrlGenerator.generateSkinUrl(SkinType.CartThumnailCase, productCode, caseCode);


        // then
        assertThat(caseSkinUrl).isEqualTo("/Upload/Data1/Resource/skin/editor/00802900020001/404001.png");
        assertThat(deviceSkinUrl).isNull();
        assertThat(cartSkinUrl).isEqualTo("/Upload/Data1/Resource/skin/designList/00802900020001/404001.png");
    }


    @Test
    public void GIVEN_bumper_case_WHEN_generateSkinUrl_THEN_url_should_not_null() {
        // given
        String productCode = "00802900030001";
        String caseCode = "404002";
        String deviceColorCode = null;

        // when
        String caseSkinUrl = PhoneCaseSkinUrlGenerator.generateSkinUrl(SkinType.Case, productCode, caseCode);
        String deviceSkinUrl = PhoneCaseSkinUrlGenerator.generateSkinUrl(SkinType.Device, productCode, deviceColorCode);
        String cartSkinUrl = PhoneCaseSkinUrlGenerator.generateSkinUrl(SkinType.CartThumnailCase, productCode, caseCode);


        // then
        assertThat(caseSkinUrl).isEqualTo("/Upload/Data1/Resource/skin/editor/00802900030001/404002.png");
        assertThat(deviceSkinUrl).isNull();
        assertThat(cartSkinUrl).isEqualTo("/Upload/Data1/Resource/skin/designList/00802900030001/404002.png");
    }


}
