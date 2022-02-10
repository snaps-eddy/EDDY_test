package com.snaps.mobile.utils.network.retrofit2.interfacies.enums;

public enum eSnapsPageType {
    COVER("cover"),
    PAGE("page"),
    TITLE("title"); //FIXME...

    private String value = "";
    eSnapsPageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
