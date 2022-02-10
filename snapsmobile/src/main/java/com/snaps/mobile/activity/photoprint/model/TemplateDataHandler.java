package com.snaps.mobile.activity.photoprint.model;

/**
 * Created by songhw on 2017. 3. 7..
 */

public interface TemplateDataHandler {
    int getBorderThickness(int frameSize);
    String[] getDateStringDatas();
    boolean isMattTypeAvailable();
}
