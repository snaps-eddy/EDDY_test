package com.snaps.mobile.utils.network.retrofit2.interfacies.enums;

public enum eSnapsSNSImageOriginCode {
    YO_BOOK("YO_BOOK"),
    FACEBOOK("FACEBOOK"),
    YAHOO_BOOK("YAHOO_BOOK"),
    KAKAOSTORY("KAKAOSTORY"),
    INSTAGRAM("INSTAGRAM"),
    GOOGLE_PHOTO("GOOGLE_PHOTO");

    private String value = "";
    eSnapsSNSImageOriginCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
