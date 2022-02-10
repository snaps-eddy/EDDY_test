package com.snaps.mobile.activity.book;

public enum FacebookPhotobookPageType {
	COVER(0), INDEX(1), PICTURE(2), SUMMARY(3), CHAPTER(4), PAGE_CHAPTER(5), PAGE_INNER(6), PAGE_LAST(7), FRIENDS_PAGE(8);

	private final int index;

	FacebookPhotobookPageType(int index) {
		this.index = index;
	}

	// 객체 필드를 리턴함
	public int getIndex() {
		return index;
	}
}
