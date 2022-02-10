package com.snaps.mobile.utils.network.retrofit2.interfacies.enums;

public enum eSnapsPageResourceRequestType {
    BASIC("BASIC"),
    LAYOUT("LAYOUT"),
    BACKGROUND("BACKGROUND"),
    LAYOUT_COVER("LAYOUT_COVER"),
    LAYOUT_TITLE("LAYOUT_TITLE"),
    MULTI_TEMPLATE("MULTI_TEMPLATE"),
    FORM_TEMPLATE("FORM_TEMPLATE"),
    MULTIFORM_TEMPLATE("MULTIFORM_TEMPLATE"),
    ACCESSORY_TEMPLATE("ACCESSORY_TEMPLATE"),
    ACCESSORY_PRODUCT("ACCESSORY_PRODUCT"),
    AI_COVER("AI_COVER"),
    AI_TITLE("AI_TITLE"),
    AI_PAGE("AI_PAGE");

    private String value = "";
    eSnapsPageResourceRequestType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
