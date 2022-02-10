package com.snaps.mobile.activity.book;

import java.util.ArrayList;

import com.snaps.common.storybook.StoryData;
import com.snaps.common.storybook.StoryData.ImageInfo;

public class StoryBookChapter {

	/**
	 * 챕터 번호.
	 */
	int chapterNumber = 0;

	/**
	 * 챕터가 들어가는 인덱스 저장
	 */
	int chapterStartIndex = 0;

	/**
	 * 테마페이지가 들어가는 인덱스 저장
	 */
	int themaIndex = 0;

	/**
	 * 시작하는 스토리 월.. 월에대한 챕터 rule 적용을 위해..
	 */
	int startStoryMonth = 0;

	/**
	 * 챕터 시작하는 스토리 인덱스
	 */
	int startStoryIndex = 0;

	/**
	 * 챕터 끝나는 스토리 인덱스
	 */
	int endStoryIndex = 0;

	/**
	 * 베스트 스토리 인덱스.
	 */
	int bestStoryIndex = -1;

	/**
	 * 챕터에서 best 이미지 리스트
	 */
	ArrayList<ImageInfo> chapterBestImages = null;

	/**
	 * 챕터에 들어가는 스토리 리스트..
	 */
	ArrayList<StoryData> chapterStoryList = null;
	

	/***
	 * 이미지 데이터를 가져오는 함수..
	 * 
	 * @param snsproperty
	 * @return
	 */

	public ImageInfo getImageData(String snsproperty) {
		return null;
	}

	/***
	 * 친구 이미지 리스트를 가져오는 함수 느낌 친구 + 댓글친구.
	 * 
	 * @return
	 */
	public ArrayList<String> getFrientImageList() {
		return null;
	}

	/***
	 * 인덱스 페이지에 들어가는 페이지 번호를 구하는 함수..
	 * 
	 * @return
	 */
	public int getIndexPage() {
		return chapterStoryList.get(0).startPage;
	}

	public int getStartStoryIndex() {
		return startStoryIndex;
	}

	public void setStartStoryIndex(int startStoryIndex) {
		this.startStoryIndex = startStoryIndex;
	}

	public int getEndStoryIndex() {
		return endStoryIndex;
	}

	public void setEndStoryIndex(int endStoryIndex) {
		this.endStoryIndex = endStoryIndex;
	}

	public int getChapterNumber() {
		return chapterNumber;
	}

	public void setChapterNumber(int chapterNumber) {
		this.chapterNumber = chapterNumber;
	}

	public int getBestStoryIndex() {
		return bestStoryIndex;
	}

	public void setBestStoryIndex(int bestStoryIndex) {
		this.bestStoryIndex = bestStoryIndex;
	}

	public int getChapterStartIndex() {
		return chapterStartIndex;
	}

	public void setChapterStartIndex(int chapterStartIndex) {
		this.chapterStartIndex = chapterStartIndex;
	}

}
