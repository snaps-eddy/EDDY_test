package com.snaps.mobile.activity.book;

import android.graphics.Rect;

import com.snaps.common.storybook.IStoryDataStrategy;
import com.snaps.common.storybook.StoryData;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.ui.StoryBookStringUtil;

import java.util.ArrayList;
import java.util.Locale;

public class StoryBookIndexMaker {

    final int BAES_PAGE_CNT = 6;

    SnapsPage _page = null;
    SnapsTextControl index_year = null;
    SnapsTextControl month_pagenum = null;
    SnapsTextControl index_chapter = null;
    SnapsTextControl index_pagenum = null;
    SnapsTextControl index_month = null;
    SnapsTextControl index_empty = null;
    IStoryDataStrategy _dataManager = null;

    int type = 0;
    ArrayList<StoryBookChapter> _chapters = null;

    public StoryBookIndexMaker(SnapsPage indexPage, ArrayList<StoryBookChapter> chapters, IStoryDataStrategy dataManager) {
        _page = indexPage;
        _chapters = chapters;
        _dataManager = dataManager;
    }

    public void makeIndex() {

        index_year = _page.getTextControl("index_year");
        index_chapter = _page.getTextControl("index_chapter");
        index_month = _page.getTextControl("index_month");
        index_pagenum = _page.getTextControl("index_pagenum");
        index_empty = _page.getTextControl("index_empty");
        month_pagenum = _page.getTextControl("month_pagenum");
        if (month_pagenum == null)
            month_pagenum = _page.getTextControl("MONTH_PAGENUM");

        _page.removeText(index_year);
        _page.removeText(index_month);
        _page.removeText(index_chapter);
        _page.removeText(index_pagenum);
        _page.removeText(index_empty);
        _page.removeText(month_pagenum);

        if (index_year != null && month_pagenum != null && index_empty != null) {
            makeIndexTypeOne();
        } else if (index_chapter != null && index_month != null && index_pagenum != null) {
            makeIndexTypeTwo();
        }

    }

    /**
     * C 디자인 스타일로 만들기.
     */
    void makeIndexTypeOne() {
        // 045021006151 c 다지인

        Rect indexRect = _page.getIndexRect();
        String valign = _page.vAlign;

        // 텍스트 정렬을 위해서 필요...
        ArrayList<SnapsTextControl> alignText = new ArrayList<SnapsTextControl>();

        int year = 0;
        int height = 0;
        for (StoryBookChapter chapter : _chapters) {
            StoryData d = _dataManager.getStory(chapter.getStartStoryIndex());
            String startDate = d.createdAt;
            String endDate = _dataManager.getStory(chapter.getEndStoryIndex()).createdAt;

            // 년도를 넣을지 체크.
            if (year == 0 || year < StoryBookStringUtil.getYear(startDate)) {
                if (year != 0)
                    height += index_empty.getIntHeight();

                SnapsTextControl yearControl = index_year.copyControl();
                year = StoryBookStringUtil.getYear(startDate);
                yearControl.text = year + "";
                yearControl.x = "0";
                yearControl.y = height + "";
                alignText.add(yearControl);
                height += yearControl.getIntHeight();

            }

            SnapsTextControl indexControl = month_pagenum.copyControl();
            // 챕터 시작하기전 타이틀 + 인덱스 + 토탈 = 5p
            String page = BAES_PAGE_CNT + chapter.getChapterStartIndex() * 2 + "";
            indexControl.text = StoryBookStringUtil.covertKakaoDateBySnsProperty(month_pagenum.getSnsproperty(), startDate, endDate) + " . " + page;
            indexControl.x = "0";
            indexControl.y = height + "";
            alignText.add(indexControl);
            height += indexControl.getIntHeight();
        }

        // 목차를 중간으로 이동을 시킨다.
        if (valign.equals("center")) {
            int startHeight = indexRect.top + (indexRect.height() - height) / 2;
            for (SnapsControl c : alignText) {
                c.y = (c.getIntY() + startHeight) + "";
                c.x = indexRect.left + "";
                _page.addControl(c);
            }
        } else if (valign.equals("top")) {
            int startHeight = indexRect.top;
            for (SnapsControl c : alignText) {
                c.y = (c.getIntY() + startHeight) + "";
                c.x = (indexRect.width() - c.getIntWidth()) / 2 + indexRect.left + "";
                _page.addControl(c);
            }
        }

        alignText.clear();
    }

    /***
     * ?
     */
    void makeIndexTypeTwo() {
        // D 디자
        Rect indexRect = _page.getIndexRect();
        String valign = _page.vAlign;

        // 텍스트 정렬을 위해서 필요...
        ArrayList<SnapsTextControl> alignText = new ArrayList<SnapsTextControl>();

        int height = 0;
        int idx = 1;
        for (StoryBookChapter chapter : _chapters) {
            StoryData d = _dataManager.getStory(chapter.getStartStoryIndex());
            String startDate = d.createdAt;
            String endDate = _dataManager.getStory(chapter.getEndStoryIndex()).createdAt;

            SnapsTextControl textControl = index_chapter.copyControl();
            textControl.text = String.format(Locale.getDefault(), "%02d", idx);
            textControl.x = "0";
            textControl.y = height + "";
            alignText.add(textControl);
            height += 10;//(textControl.getIntHeight());
            idx++;

            SnapsTextControl indexControl = index_month.copyControl();
            indexControl.text = StoryBookStringUtil.covertKakaoDateBySnsProperty("index_month", startDate, endDate);
            indexControl.x = "0";
            indexControl.y = height + "";
            alignText.add(indexControl);

            // 챕터 시작하기전 타이틀 + 인덱스 + 토탈 = 5p
            int pageCnt = BAES_PAGE_CNT + chapter.getChapterStartIndex() * 2;
            SnapsTextControl pageControl = index_pagenum.copyControl();
            pageControl.text = String.format(Locale.getDefault(), "……%d", pageCnt);
            pageControl.x = indexControl.getIntWidth() + "";
            pageControl.y = height + "";
            alignText.add(pageControl);

            height += 12;//indexControl.getIntHeight();
        }

        // 목차를 중간으로 이동을 시킨다.
        if (valign.equals("top")) {
            int startHeight = indexRect.top;
            for (SnapsControl c : alignText) {
                c.y = (c.getIntY() + startHeight) + "";
                c.x = indexRect.left + c.getIntX() + "";
                _page.addControl(c);
            }
        }

        alignText.clear();
    }

}
