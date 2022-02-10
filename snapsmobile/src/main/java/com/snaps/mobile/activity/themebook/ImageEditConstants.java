package com.snaps.mobile.activity.themebook;

/**
 * Created by ysjeong on 2017. 6. 20..
 */

public interface ImageEditConstants {
    long LIMIT_EFFECT_CASH_MAX_SIZE = 100 * 1024 * 1024; // effect 파일은 최대 100MB 까지만 보관
    long ORGANIZED_EFFECT_CASH_SIZE = 20 * 1024 * 1024; // 100MB가 초과하면 20메가만 남겨놓고 오래된 파일 삭제

    byte SAVE_TYPE_PREV = 101;
    byte SAVE_TYPE_NEXT = 102;
    byte SAVE_TYPE_FINISH = 103;

    int ANIM_TIME = 80;

    float VALUE_BOTTOM_VIEW_MARGIN = 13;
    float VALUE_BOTTOM_VIEW_PADDING = 12.5f;
}
