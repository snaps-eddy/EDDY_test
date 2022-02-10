package com.snaps.mobile.activity.hamburger_menu.interfacies;

/**
 * Created by ysjeong on 16. 8. 8..
 */
public interface ISnapsHamburgerMenuListener {

    int MSG_MOVE_TO_MENU_MAIN    = 0;
    int MSG_MOVE_TO_SETTING      = 10;
    int MSG_MOVE_TO_BACK          = 11;
    int MSG_MOVE_TO_LOG_IN       = 12;
    int MSG_MOVE_TO_ORDER        = 13;
    int MSG_MOVE_TO_CART         = 14;
    int MSG_MOVE_TO_COUPON       = 15;
    int MSG_MOVE_TO_HOME         = 16;
    int MSG_MOVE_TO_EVENT        = 17;
    int MSG_MOVE_TO_DIARY        = 18;
    int MSG_MOVE_TO_CUSTOMER     = 19;
    int MSG_MOVE_TO_JOIN         = 20;
    int MSG_MOVE_TO_NOTICE       = 21;
    int MSG_MOVE_TO_PWD_FIND       = 22;
    int MSG_MOVE_TO_MY_SNAPS       = 23;
    int MSG_MOVE_TO_COMPLETED_VERIFY = 24;
    int MSG_MOVE_TO_COMPLETED_REST_ID = 25;

    int MSG_COMPLATE_JOIN          = 1000;
    int MSG_COMPLATE_LOG_IN        = 1001;
    int MSG_COMPLATE_RETIRE        = 1002;
    int MSG_COMPLATE_PWD_RESET     = 1003;
    int MSG_COMPLATE_PWD_FIND      = 1004;
    int MSG_COMPLATE_VERIFI        = 1005;
    int MSG_COMPLATE_VERIFI_POPUP  = 1006;
    int MSG_COMPLATE_JOIN_EVEVT    = 1007;
    int MSG_COMPLATE_REST_ID       = 1008;
    void onHamburgerMenuPostMsg(int what);
}
