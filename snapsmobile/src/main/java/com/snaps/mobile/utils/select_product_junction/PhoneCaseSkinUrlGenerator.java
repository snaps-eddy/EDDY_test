package com.snaps.mobile.utils.select_product_junction;

import androidx.annotation.Nullable;

import com.bumptech.glide.load.engine.Resource;
import com.snaps.common.utils.ui.StringUtil;

import java.util.Locale;

public class PhoneCaseSkinUrlGenerator {

    private static final String DEVICE_SKIN_URL_FORMAT = "/Upload/Data1/Resource/skin/device/%s/%s.png";

    private static final String CASE_SKIN_URL_FORMAT = "/Upload/Data1/Resource/skin/editor/%s/%s.png";
    private static final String CART_CASE_SKIN_URL_FORMAT = "/Upload/Data1/Resource/skin/detail2/%s/%s.png";

    private static final String TOP_SKIN_URL_FORMAT = "/Upload/Data1/Resource/skin/editorTop/%s/%s.png";
    private static final String CART_TOP_SKIN_URL_FORMAT = "/Upload/Data1/Resource/skin/detailTop/%s/%s.png";

    @Nullable
    public static String generateSkinUrl(SkinType aCase, String productCode, String optionCode) {
        if (StringUtil.isEmptyAfterTrim(productCode) || StringUtil.isEmptyAfterTrim(optionCode)) {
            return null;
        }

        switch (aCase) {
            case Device:
                return String.format(Locale.ROOT, DEVICE_SKIN_URL_FORMAT, productCode, optionCode);
            case Case:
                return String.format(Locale.ROOT, CASE_SKIN_URL_FORMAT, productCode, optionCode);
            case CartThumnailCase:
                return String.format(Locale.ROOT, CART_CASE_SKIN_URL_FORMAT, productCode, optionCode);
            case TopSkin:
                return String.format(Locale.ROOT, TOP_SKIN_URL_FORMAT, productCode, optionCode);
            case CartThumnailTopSkin:
                return String.format(Locale.ROOT, CART_TOP_SKIN_URL_FORMAT, productCode, optionCode);
            default:
                return null;
        }
    }

    public static String makeOptionCode(SkinType type, String caseCode, String caseColorCode, String deviceColorCode) {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case Case:
            case CartThumnailCase:
                sb.append(caseCode);
                if (caseColorCode != null) {
                    sb.append("_").append(caseColorCode);
                }
                return sb.toString();

            case Device:
                return sb.append(deviceColorCode).toString();

            case TopSkin:
            case CartThumnailTopSkin:
                sb.append(deviceColorCode);
                if (caseCode != null) {
                    sb.append("_").append(caseCode);
                }
                if (caseColorCode != null) {
                    sb.append("_").append(caseColorCode);
                }
                return sb.toString();
            default:
                return null;
        }

    }
}
