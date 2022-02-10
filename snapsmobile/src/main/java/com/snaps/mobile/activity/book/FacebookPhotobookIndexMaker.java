package com.snaps.mobile.activity.book;

import android.graphics.Rect;

import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.ui.StoryBookStringUtil;
import com.snaps.facebook.model.sns.facebook.ChapterData;
import com.snaps.facebook.model.sns.facebook.TimelineData;
import com.snaps.facebook.utils.sns.FacebookUtil.BookMaker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class FacebookPhotobookIndexMaker {

	final int BAES_PAGE_CNT = 6;

	SnapsPage _page = null;
	SnapsTextControl index_year = null;
	SnapsTextControl month_pagenum = null;
	SnapsTextControl index_chapter = null;
	SnapsTextControl index_pagenum = null;
	SnapsTextControl index_period = null;
	SnapsTextControl index_empty = null;
	BookMaker maker = null;

	int type = 0;
	ArrayList<ChapterData> _chapters = null;

	public FacebookPhotobookIndexMaker(SnapsPage indexPage, BookMaker bookMaker) {
		_page = indexPage;
		_chapters = bookMaker.chapterList;
		maker = bookMaker;
	}

	public void makeIndex() {

		//AType
		index_year = _page.getTextControl("index_year");
		month_pagenum = _page.getTextControl("MONTH_PAGENUM");
		
		//BType
		index_chapter = _page.getTextControl("chapter.");
		index_pagenum = _page.getTextControl("pagenum");
		index_period = _page.getTextControl("index_period");
		
		//Common
		index_empty = _page.getTextControl("index_empty");
		
		int indexType = maker.getTemplateType();
		
		// 템플릿 아이디로 구분하도록 변경
		if( indexType == BookMaker.TYPE_A ) drawIndexAType();
		else if( indexType == BookMaker.TYPE_B ) drawIndexBType();
		else if( indexType == BookMaker.TYPE_C || indexType == BookMaker.TYPE_D ) drawIndexCAndDType();
		// 템플릿 아이디로 체크 못할 경우를 대비해 이전 코드도 남김.
		else if( index_year != null && month_pagenum != null && index_empty != null ) drawIndexAType();
		else if( index_chapter != null && index_pagenum != null &&  index_period != null && index_empty != null ) drawIndexBType();

		_page.removeText(index_year);
		_page.removeText(index_chapter);
		_page.removeText(index_pagenum);
		_page.removeText(index_empty);
		_page.removeText(month_pagenum);
		_page.removeText(index_period);
	}
	
	private void drawIndexBType() {
		Rect indexRect = _page.getIndexRect();

		// 텍스트 정렬을 위해서 필요...
		ArrayList<SnapsTextControl> alignText = new ArrayList<SnapsTextControl>();

		int height = 0;
		int chapterIdx = 1;
		for (ChapterData chapter : _chapters) {
			TimelineData timelineStart = chapter.getFirstTimeline();
			TimelineData timelineEnd = chapter.getLastTimeline();
			if(timelineStart == null || timelineEnd == null) continue;
			
			height += index_empty.getIntHeight();

			SnapsTextControl chapterControl = index_chapter.copyControl();
			chapterControl.text = String.format(Locale.getDefault(), "%02d", chapterIdx++) + ".";
			chapterControl.x = chapterControl.getIntX() + "";
			chapterControl.y = height + "";
			alignText.add(chapterControl);
			
			SnapsTextControl periodControl = index_period.copyControl();
			periodControl.text = getBtypePeriod(timelineStart, timelineEnd);
			periodControl.x = periodControl.getIntX() + "";
			periodControl.y = height + "";
			alignText.add(periodControl);
			
			SnapsTextControl pageNumControl = index_pagenum.copyControl();

			pageNumControl.text = "" + maker.getStartPageIndex( chapter ) + "P";
			pageNumControl.x = pageNumControl.getIntX() + "";
			pageNumControl.y = height + "";
			alignText.add(pageNumControl);
			
			height += periodControl.getIntHeight();
		}

		int startHeight = indexRect.top;
		for (SnapsControl c : alignText) {
			c.y = (c.getIntY() + startHeight) + "";
			c.x = c.getIntX() + "";
			_page.addControl(c);
		}

		alignText.clear();	
	}
	
	private void drawIndexCAndDType() {
		Rect indexRect = _page.getIndexRect();

		// 텍스트 정렬을 위해서 필요...
		ArrayList<SnapsTextControl> alignText = new ArrayList<SnapsTextControl>();

		int height = 0;
		int chapterIdx = 1;
		for (ChapterData chapter : _chapters) {
			TimelineData timelineStart = chapter.getFirstTimeline();
			TimelineData timelineEnd = chapter.getLastTimeline();
			if(timelineStart == null || timelineEnd == null) continue;
			
			height += index_empty.getIntHeight();

			SnapsTextControl chapterControl = index_chapter.copyControl();
			chapterControl.text = String.format(Locale.getDefault(), "%02d", chapterIdx++) + ".";
			chapterControl.x = chapterControl.getIntX() + "";
			chapterControl.y = height + "";
			alignText.add(chapterControl);
			
			SnapsTextControl periodControl = index_period.copyControl();
			periodControl.text = getCtypePeriod(timelineStart, timelineEnd);
			periodControl.x = periodControl.getIntX() + "";
			periodControl.y = height + "";
			alignText.add(periodControl);
			
			SnapsTextControl pageNumControl = index_pagenum.copyControl();

			pageNumControl.text = "" + maker.getStartPageIndex( chapter ) + "p";
			pageNumControl.x = pageNumControl.getIntX() + "";
			pageNumControl.y = height + "";
			alignText.add(pageNumControl);
			
			height += periodControl.getIntHeight();
		}

		int startHeight = indexRect.top;
		for (SnapsControl c : alignText) {
			c.y = (c.getIntY() + startHeight) + "";
			c.x = c.getIntX() + "";
			_page.addControl(c);
		}

		alignText.clear();	
	}
	   
	private String getBtypePeriod(TimelineData start, TimelineData end) {
		
		int startYear = start.createDate.get(Calendar.YEAR);
		int endYear = end.createDate.get(Calendar.YEAR);
		String format = startYear == endYear ? "MONTH - MONTH, YYYY" : "MONTH, YYYY - MONTH, YYYY";
		return StoryBookStringUtil.covertKakaoDate(format, start.createDate, end.createDate);
	}
	
	private String getCtypePeriod(TimelineData start, TimelineData end) {
		StringBuilder sb = new StringBuilder();
		int startYear = start.createDate.get( Calendar.YEAR );
		int endYear = end.createDate.get( Calendar.YEAR );
		int startMonth = start.createDate.get( Calendar.MONTH );
		int endMonth = end.createDate.get( Calendar.MONTH );
		sb.append( StoryBookStringUtil.getMonthString(startMonth, false) );
		if( startYear == endYear ) {
			if( startMonth != endMonth ) sb.append( " - " ).append( StoryBookStringUtil.getMonthString(endMonth, false) );
			sb.append( ", " ).append( startYear );
		}
		else sb.append( ", " ).append( startYear ).append( " - " ).append( StoryBookStringUtil.getMonthString(endMonth, false) ).append( ", " ).append( endYear );
		
		return sb.toString();
	}
	
	private void drawIndexAType() {
		Rect indexRect = _page.getIndexRect();
		String valign = _page.vAlign;
		int topYpos = -1, bottomYpos = -1, monthLineHeight = -1;

		// 텍스트 정렬을 위해서 필요...
		ArrayList<SnapsTextControl> alignText = new ArrayList<SnapsTextControl>();

		int year = 0;
		int height = 0;
		for (ChapterData chapter : _chapters) {
			TimelineData timelineStart = chapter.getFirstTimeline();
			TimelineData timelineEnd = chapter.getLastTimeline();
			if(timelineStart == null || timelineEnd == null) continue;
			
			int startYear = timelineStart.createDate.get(Calendar.YEAR);
		
			// 년도를 넣을지 체크.
			if (year == 0 || year < startYear) {
				if (year != 0)
					height += index_empty.getIntHeight();

				SnapsTextControl yearControl = index_year.copyControl();
				year = startYear;
				yearControl.text = year + "";
				yearControl.x = "0";
				yearControl.y = height + "";
				alignText.add(yearControl);
				height += yearControl.getIntHeight();

			}

			SnapsTextControl indexControl = month_pagenum.copyControl();
			indexControl.text = makeIndexString( chapter );
			indexControl.x = "0";
			indexControl.y = height + "";
			alignText.add(indexControl);
			height += indexControl.getIntHeight();
		}

		// 목차를 중간으로 이동을 시킨다.
		if (valign.equals("center")) {
			int startHeight = indexRect.top + (indexRect.height() - height) / 2;
			int topY = -1, bottomY = -1, temp;;
			for (SnapsControl c : alignText) {
				c.y = (c.getIntY() + startHeight) + "";
				c.x = (indexRect.width() - c.getIntWidth()) / 2 + indexRect.left + "";
				_page.addControl(c);
				temp = Integer.parseInt( c.y );
				
				if( topY < 0 || topY > temp ) topY = temp;
				if( bottomY < 0 || bottomY < temp ) bottomY = temp;
				if( monthLineHeight < 0 || monthLineHeight > c.getIntHeight() ) monthLineHeight = c.getIntHeight();
			}
			
			SnapsClipartControl topPoint, bottomPoint;
			SnapsControl control;
			final int margin = 10;
			control = _page.getControlByProperty( "topsticker" );
			if( control instanceof SnapsClipartControl ) {
				topPoint = (SnapsClipartControl) control;
				topPoint.y = "" + ( topY - topPoint.getIntHeight() - margin );
			}
			control = _page.getControlByProperty( "bottomsticker" );
			if( control instanceof SnapsClipartControl ) {
				bottomPoint = (SnapsClipartControl) control;
				bottomPoint.y = "" + ( bottomY + monthLineHeight + margin );
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
	
	private String makeIndexString( ChapterData chapter ) {
		return chapter.getStartMonthStr( false ) + " - " + chapter.getEndMonthStr( false ) + " . " + maker.getStartPageIndex( chapter );
	}
}
