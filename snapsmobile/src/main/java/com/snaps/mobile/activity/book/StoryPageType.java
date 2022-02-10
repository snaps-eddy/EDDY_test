package com.snaps.mobile.activity.book;

public enum StoryPageType {
    COVER_PAGE(0), INDEX_PAGE(1), TITLE_PAGE(2), TOTAL_PAGE(3), THEMA_PAGE(4), PAGE_PAGE(5), INNER_PAGE(6), LAST_PAGE(7), FRIENDS_PAGE(8);

    private final int index;

    StoryPageType(int index) {
        this.index = index;
    }

    // 객체 필드를 리턴함
    public int getIndex() {
        return index;
    }
}
