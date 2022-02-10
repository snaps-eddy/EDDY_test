package com.snaps.mobile.activity.photoprint.model;

/**
 * Created by songhw on 2017. 3. 7..
 */

public interface ActivityActionListener {
    void showDeleteButton( boolean show );
    void changeListMode( boolean isLargeItemMode );
    void refreshListItems();
    void editLayoutFinished();
    void enableScroll( boolean flag, boolean showDimArea );
    boolean isMenuHided();
    void showMenu();
    void hideMenu();
    void closeTutorial();
    void showApplyChangeButtonLayout( boolean show );
}
