package com.snaps.mobile.activity.ui.menu.renewal.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

/**
 * Created by songhw on 2016. 7. 27..
 */
public class PageControl extends LayoutObject {
    private int rolling, pageSpace;
    private String pageAlign;
    private boolean isPageHidden = false;
    private boolean isInfinityPage = false;
    private boolean doPaging = false;
    private ArrayList<Menu> pages;

    public PageControl() {}

    public PageControl( JsonObject jsonObject ) {
        type = TYPE_PAGE_CONTROL;

        setRectFromJson( jsonObject );

        rolling = jsonObject.has( "rolling" ) ? jsonObject.get( "rolling" ).getAsInt() : -1;
        pageSpace = jsonObject.has( "pageSpace" ) ? jsonObject.get( "pageSpace" ).getAsInt() : -1;
        pageAlign = jsonObject.has( "pageAlign" ) ? jsonObject.get( "pageAlign" ).getAsString() : "";
        isPageHidden = jsonObject.has( "pageHidden" ) ? jsonObject.get( "pageHidden" ).getAsBoolean() : false;
        isInfinityPage = jsonObject.has( "pageInfinity" ) ? jsonObject.get( "pageInfinity" ).getAsBoolean() : false;
        doPaging = jsonObject.has( "paging" ) ? jsonObject.get( "paging" ).getAsBoolean() : false;
        pages = new ArrayList<Menu>();
        if( jsonObject.has("pages") ) {
            JsonArray array = jsonObject.get( "pages" ).getAsJsonArray();
            for( int i = 0; i < array.size(); ++i )
                pages.add( new Menu(array.get(i).getAsJsonObject()) );
        }
    }

    public PageControl clone() {
        PageControl instance = new PageControl();
        instance.type = TYPE_PAGE_CONTROL;
        instance.rect = rect;
        instance.value = value;
        instance.rolling = rolling;
        instance.pageSpace = pageSpace;
        instance.pageAlign = pageAlign;
        instance.isPageHidden = isPageHidden;
        instance.isInfinityPage = isInfinityPage;
        instance.doPaging = doPaging;
        if( pages != null ) {
            instance.pages = new ArrayList<Menu>();
            for( int i = 0; i < pages.size(); ++i )
                instance.pages.add( pages.get(i).clone() );
        }
        return instance;
    }

    /**
     * getters
     */
    public int getRolling() { return rolling; }
    public int getPageSpace() { return pageSpace; }
    public boolean isPageHidden() { return isPageHidden; }
    public boolean isInfinityPage() { return isInfinityPage; }
    public boolean doPaging() { return doPaging; }
    public String getPageAlign() { return pageAlign;}
    public ArrayList<Menu> getPages() { return pages;}

    /**
     * setters
     */
    public void addPage( Menu page ) {
        if( pages == null ) pages = new ArrayList<Menu>();
        pages.add( page );
    }

    public void clearPages() { this.pages = new ArrayList<Menu>();}
}
