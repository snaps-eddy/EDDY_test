package com.snaps.mobile.activity.book;

import java.util.ArrayList;
import java.util.HashMap;

import com.snaps.common.storybook.StoryData.ImageInfo;
import com.snaps.common.utils.ui.StringUtil;

//페이지에 section이 들어갈지 말지를 결정해주는 클래스..
public class StoryBookChapterRuler {

	final int BASIC_PAGE = 150;
	class StoryPageInfo {

		int storyIndex = 0;
		String createAt = "";
		int endPage = 0; // 시작페이지..
		int startPage = 0; // 마지막페이지..
		int likeCount = 0; // 좋아요 갯수..
		// 포스팅 이미지들 url
		ArrayList<ImageInfo> images = null;

		public StoryPageInfo(int storyIndex, String createAt, int startPage, int endPage, int likeCount) {
			this.storyIndex = storyIndex;
			this.createAt = createAt;
			this.startPage = startPage;
			this.endPage = endPage;
			this.likeCount = likeCount;
		}

		public ArrayList<ImageInfo> getImages() {
			return images;
		}

		public void setImages(ArrayList<ImageInfo> images) {
			this.images = images;
		}

		public String getCreateAt() {
			return createAt;
		}

		public void setCreateAt(String createAt) {
			this.createAt = createAt;
		}

		public int getEndPage() {
			return endPage;
		}

		public void setEndPage(int endPage) {
			this.endPage = endPage;
		}

		public int getStartPage() {
			return startPage;
		}

		public void setStartPage(int startPage) {
			this.startPage = startPage;
		}
	}

	class StoryChapterInfo {
		int startStoryIndex = 0;
		int endStoryIndex = 0;
		String startCreateAt = "";
		String endCreateAt = "";
		int indexPage = 0;
		int memories = 0;

		ArrayList<ImageInfo> bestImages = null;

		public StoryChapterInfo(int sIdx, int eIdx, String sCreatAt, String eCreatAt) {
			this.startCreateAt = sCreatAt;
			this.endCreateAt = eCreatAt;
			this.startStoryIndex = sIdx;
			this.endStoryIndex = eIdx;

			bestImages = new ArrayList<ImageInfo>();
		}

		public void addBestImages(ArrayList<ImageInfo> images) {
			bestImages.addAll(images);
		}

		public ArrayList<ImageInfo> getBestImages() {
			return bestImages;
		}

		public String getStartYearText() {
			String year = StringUtil.getDateStringByFormat(startCreateAt, "yyyy");
			return year;
		}

		public String getEndYearText() {
			String year = StringUtil.getDateStringByFormat(endCreateAt, "yyyy");
			return year;
		}

		public String getYearMonthText() {
			String year = StringUtil.getDateStringByFormat(startCreateAt, "yyyy.MM");
			return year;
		}

		public String getTableChapter(int style) {
			String startYear = StringUtil.getDateStringByFormat(startCreateAt, "yyyy");
			String endYear = StringUtil.getDateStringByFormat(endCreateAt, "yyyy");

			String startMonth = StringUtil.getDateStringByFormat(startCreateAt, "M");
			String endMonth = StringUtil.getDateStringByFormat(endCreateAt, "M");

			String tableContent = "";
			if (style == 2) {
				if (startYear.equals(endYear)) {
					tableContent = String.format("%s - %s %s", StoryBookCommonUtil.convertMonthStr(startMonth), StoryBookCommonUtil.convertMonthStr(endMonth), startYear);
				} else {
					tableContent = String.format("%s %s - %s %s", StoryBookCommonUtil.convertMonthStr(startMonth), startYear, StoryBookCommonUtil.convertMonthStr(endMonth), endYear);
				}
			} else if (style == 1 || style == 3) {
				tableContent = String.format("%s - %s", StoryBookCommonUtil.convertMonthStr(startMonth), StoryBookCommonUtil.convertMonthStr(endMonth));
			} else if (style == 4) {
				tableContent = String.format("%s-%s-%s-%s", StoryBookCommonUtil.convertMonthStr(startMonth), StoryBookCommonUtil.convertMonthStr(endMonth), startYear, endYear);
			}

			return tableContent;
		}

		public String getPeriod(int style) {
			if (style == 3) {
				String start = StringUtil.getDateStringByFormat(startCreateAt, "yyyy.MM.dd");
				String end = StringUtil.getDateStringByFormat(endCreateAt, "yyyy.MM.dd");
				return start + " - " + end;
			} else if (style == 4) {

			}

			return "";
		}

		private String getTableContent(int style) {
			String startYear = StringUtil.getDateStringByFormat(startCreateAt, "yyyy");
			String endYear = StringUtil.getDateStringByFormat(endCreateAt, "yyyy");

			String startMonth = StringUtil.getDateStringByFormat(startCreateAt, "M");
			String endMonth = StringUtil.getDateStringByFormat(endCreateAt, "M");

			String tableContent = "";
			if (startYear.equals(endYear)) {
				tableContent = String.format("%s - %s", StoryBookCommonUtil.convertMonthStr(startMonth), StoryBookCommonUtil.convertMonthStr(endMonth));
			} else {
				tableContent = String.format("%s %s - %s", StoryBookCommonUtil.convertMonthStr(startMonth), startYear, StoryBookCommonUtil.convertMonthStr(endMonth));
			}

			return tableContent;
		}

		public String getTableContentPage(int style) {
			if (style == 2)
				return getTableContent(style) + "       · " + indexPage;
			else if (style == 4)
				return getTableContent(style) + "       ······ " + indexPage;
			else
				return getTableContent(style) + "       · " + indexPage;
		}

		public int getIndexPage() {
			return indexPage;
		}

		public void setIndexPage(int indexPage) {

			this.indexPage = indexPage;
		}

		public String getStoryCount() {

			return (endStoryIndex - startStoryIndex) + "";// memories;
		}

		public int getStartStoryIndex() {
			return startStoryIndex;
		}

		public int getEndStoryIndex() {
			return endStoryIndex;
		}
	}

	ArrayList<StoryPageInfo> pageInfos = null;
	ArrayList<StoryChapterInfo> chapters = null;
	// chapter를 나눌때 쓰는 페이지 수..
	int pageUnit = 0; // 10p,
	int totalPage = 0;

	public StoryBookChapterRuler() {
		pageInfos = new ArrayList<StoryBookChapterRuler.StoryPageInfo>();
		chapters = new ArrayList<StoryBookChapterRuler.StoryChapterInfo>();
	}

	public void addStoryPageInfo(int storyIndex, String createAt, int startPage, int endPage, int likeCount, ArrayList<ImageInfo> images) {
		StoryPageInfo info = new StoryPageInfo(storyIndex, createAt, startPage, endPage, likeCount);
		info.setImages(images);
		pageInfos.add(info);
		totalPage = endPage;

	}

	public void splitStoriesByChapter() {
		if (totalPage < BASIC_PAGE)
			pageUnit = 10;
		else
			pageUnit = totalPage / 15;

		// chapter가 필요한 경우에
		int chapterPage = pageUnit;

		String sCreateAt = "";
		String eCreateAt = "";
		int sIndex = 0; // 시작 스토리 아이디
		int eIndex = 0; // 마지막 스토리 아이디.

		HashMap<Integer, Integer> stories = new HashMap<Integer, Integer>();
		for (int i = 0; i < pageInfos.size(); i++) {
			StoryPageInfo info = pageInfos.get(i);
			if (sCreateAt.equals("")) {
				sCreateAt = info.createAt;
				sIndex = i;
				stories.clear();
			}

			eCreateAt = info.createAt;
			eIndex = i;
			stories.put(i, info.likeCount);
			if (info.endPage >= chapterPage || (i == (pageInfos.size() - 1))) {
				StoryChapterInfo cinfo = new StoryChapterInfo(sIndex, eIndex, sCreateAt, eCreateAt);

				ArrayList<Integer> idxs = StoryBookCommonUtil.sortByValue(stories);
				for (Integer idx : idxs) {
					if (pageInfos.get(idx).images != null)
						cinfo.addBestImages(pageInfos.get(idx).images);
				}

				chapters.add(cinfo);
				// 목표페이지 변경..
				chapterPage = info.endPage + pageUnit;
				sCreateAt = "";
			}
		}

	}
	public ArrayList<StoryChapterInfo> getChapterInfo() {
		return chapters;
	}

}
