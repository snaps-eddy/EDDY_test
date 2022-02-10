package com.snaps.mobile.utils.network.retrofit2.interfacies;

public interface SnapsNetworkConstants {

    String PARAM_NORMAL_TYPE = "SNAPS";
    String PARAM_NORMAL_SNS_TYPE = "EMAIL";

    String NEW_BILLING_KR_INIPAY_URL = "/mw/v3/order/sttlRequest.jsp";

    String URL_TEXT_TO_IMAGE_DOMAIN = "https://text2.snaps.com";

    String DOMAIN_API_DEVELOP = "https://dev-api.snaps.com";
//    String DOMAIN_WEB_DEVELOP = "https://dev-m.snaps.com";

    String DOMAIN_API_STAGE = "https://stg-api.snaps.com";
//    String DOMAIN_WEB_STAGE = "https://stg-m.snaps.com";

    String DOMAIN_API_REAL = "https://api.snaps.com";
//    String DOMAIN_WEB_REAL = "https://m.snaps.com";

//    String QA_DOMAIN = "http://qa2-m.snaps.kr"; // "http://qa2-m.snaps.kr";

    String[] TOKEN_EXPIRED_ERR_CODES = { "A400200", "A400201", "A400202" };

    String DESIGN_TEMPLATE_CLASS_CODE = "045020";

    String BEARER_PREFIX = "Bearer ";
}
