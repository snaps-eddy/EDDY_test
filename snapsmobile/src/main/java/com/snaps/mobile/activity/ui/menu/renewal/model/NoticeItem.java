package com.snaps.mobile.activity.ui.menu.renewal.model;

import com.google.gson.JsonObject;

/**
 * Created by songhw on 2016. 7. 26..
 */
public class NoticeItem {
    private String title;
    private String seq;


    public NoticeItem(JsonObject jsonObject) {
        if (jsonObject == null) return;
        if (jsonObject.has("F_TITLE"))
            this.title = jsonObject.get( "F_TITLE" ).getAsString();
        if (jsonObject.has("F_SEQ"))
            this.seq = jsonObject.get( "F_SEQ" ).getAsString();
    }

    /**
     * getters
     */
    public String getTitle() { return this.title; }
    public String getSeq() { return this.seq; }
}
