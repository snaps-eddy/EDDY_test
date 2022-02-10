package com.snaps.mobile.utils.select_product_junction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(PowerMockRunner.class)
public class PhoneCaseSkinUrlGeneratorTest {

    @Test
    public void test_generate_device_skin_url() {
        //given
        String deviceColorCode = "TestDeviceColorCode";
        String productCode = "TestProductCode";
        String expectedResult = "/Upload/Data1/Resource/skin/device/" + productCode + "/" + deviceColorCode + ".png";

        //when
        String optionCode = PhoneCaseSkinUrlGenerator.makeOptionCode(SkinType.Device, null, null, deviceColorCode);
        String result = PhoneCaseSkinUrlGenerator.generateSkinUrl(SkinType.Device, productCode, optionCode);

        //then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void test_generate_print_hard_case_skin_url_for_editor() {
        //given
        String caseCode = "TestCaseCode";
        String productCode = "TestProductCode";
        String expectedResult = "/Upload/Data1/Resource/skin/editor/" + productCode + "/" + caseCode + ".png";

        //when
        String optionCode = PhoneCaseSkinUrlGenerator.makeOptionCode(SkinType.Case, caseCode, null, null);
        String result = PhoneCaseSkinUrlGenerator.generateSkinUrl(SkinType.Case, productCode, optionCode);

        //then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void test_generate_print_hard_case_skin_url_for_cartthumbnail() {
        //given
        String caseCode = "TestCaseCode";
        String productCode = "TestProductCode";
        String expectedResult = "/Upload/Data1/Resource/skin/detail2/" + productCode + "/" + caseCode + ".png";

        //when
        String optionCode = PhoneCaseSkinUrlGenerator.makeOptionCode(SkinType.CartThumnailCase, caseCode, null, null);
        String result = PhoneCaseSkinUrlGenerator.generateSkinUrl(SkinType.CartThumnailCase, productCode, optionCode);

        //then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void test_generate_uv_top_skin() {
        //given
        String caseCode = "TestCaseCode";
        String productCode = "TestProductCode";
        String deviceColorCode = "TestDeviceColorCode";
        String expectedResult = "/Upload/Data1/Resource/skin/editorTop/" + productCode + "/" + deviceColorCode + "_" + caseCode + ".png";

        //when
        String optionCode = PhoneCaseSkinUrlGenerator.makeOptionCode(SkinType.TopSkin, caseCode, null, deviceColorCode);
        String result = PhoneCaseSkinUrlGenerator.generateSkinUrl(SkinType.TopSkin, productCode, optionCode);

        //then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    public void test_generate_uv_top_skin_for_cartthumbnail() {
        //given
        String caseCode = "TestCaseCode";
        String caseColorCode = "TestCaseColorCode";
        String productCode = "TestProductCode";
        String deviceColorCode = "TestDeviceColorCode";
        String expectedResult = "/Upload/Data1/Resource/skin/detailTop/" + productCode + "/" + deviceColorCode + "_" + caseCode + "_" + caseColorCode + ".png";

        //when
        String optionCode = PhoneCaseSkinUrlGenerator.makeOptionCode(SkinType.CartThumnailTopSkin, caseCode, caseColorCode, deviceColorCode);
        String result = PhoneCaseSkinUrlGenerator.generateSkinUrl(SkinType.CartThumnailTopSkin, productCode, optionCode);

        //then
        assertThat(result).isEqualTo(expectedResult);
    }


}
