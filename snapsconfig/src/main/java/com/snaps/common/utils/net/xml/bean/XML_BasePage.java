package com.snaps.common.utils.net.xml.bean;

public abstract class XML_BasePage {
    public String F_SSMPL_URL;
    public String F_MMPL_URL;

    public String F_TMPL_ID;
    public String F_TMPL_CODE;
    public String F_XML_PATH;
    public String F_SEARCH_TAGS;
    public String F_DSPL_NUM;
    public String F_MYITEM_YN;
    public String F_MYITEM_CODE;
    public String F_MYMAKE_ITEM;
    public String F_NEW_YORN;
    public String F_RESIZE_320_URL;
    public String F_MASK_CNT;

    public boolean F_IS_SELECT = false;
    public boolean F_IS_BASE_MULTIFORM = false;

    public int F_SORT_PRIORITY = Integer.MAX_VALUE;
}
