package com.snaps.common.storybook;

import com.snaps.common.utils.log.Dlog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;

public class StoryData {
	private static final String TAG = StoryData.class.getSimpleName();
	public String id;
	public String createdAt;
	public Integer commentCount;
	public Integer likeCount;
	public String content;
	public ArrayList<ImageInfo> images;
	public StoryDate dateInfo;
	public ArrayList<StoryLikeData> likes;
	public ArrayList<StoryCommentData> comments;
	public String storyType;

	// 스토리가 시작되는 페이지
	public int startPage = -1;
	// 스토리가 끝나는 페이지
	public int endPage = -1;

	public String storyDate = "";

	// 스토리가 끝나는 면(4면 중)
	public int cSide = -1;

	/**
	 * 이미지 정보.
	 * 
	 * @author hansang-ug
	 *
	 */
	public static class ImageInfo {
		public String original;
		public String xlarge;
		public String large;
		public String medium;
		public String small;
		
		// 페이스북 포토북용 변수 2개 추가.
		public String targetId;
		public String targetUrl;
		

		private String originWidth;
		private String originHeight;

		public String getOriginWidth() {
			return originWidth;
		}

		public void setOriginWidth(String originWidth) {
			this.originWidth = originWidth;
		}

		public String getOriginHeight() {
			return originHeight;
		}

		public void setOriginHeight(String originHeight) {
			this.originHeight = originHeight;
		}
	}

	// /**
	// * 댓글 정보.
	// *
	// * @author hansang-ug
	// *
	// */
	// public static class CommentData {
	// public String text;
	// public ActorData writer;
	// }

	/**
	 * 사용장 정보.
	 * 
	 * @author hansang-ug
	 *
	 */
	public static class ActorData {
		public String displayName;
		public String profileThumbnailUrl;

		public ActorData(String displayName, String profileThumbnailUrl) {
			this.displayName = displayName;
			this.profileThumbnailUrl = profileThumbnailUrl;
		}
	}

	/**
	 * 날짜 정보.
	 * 
	 * @author hansang-ug
	 *
	 */
	public static class StoryDate implements Comparator<StoryDate> {
		public String szYear;
		public String szMonth;
		public String szDay;
		public String szHour;
		public String szMin;
		public int iYear;
		public int iMonth;

		public int iDay;
		public int iHour;
		public int iMin;
		public String tableContents;

		public StoryDate(String year, String month, String day, String hour, String min) {
			szYear = year;
			szMonth = month;
			szDay = day;
			szHour = hour;
			szMin = min;

			try {
				iYear = Integer.parseInt(szYear);
				iMonth = Integer.parseInt(szMonth);
				iDay = Integer.parseInt(szDay);
				iHour = Integer.parseInt(szHour);
				iMin = Integer.parseInt(szMin);

				tableContents = TableContents.convertMonthToStr(iMonth);

			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}

		public Calendar toCalendar() {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, iYear);
			cal.set(Calendar.MONTH, iMonth - 1);
			cal.set(Calendar.DAY_OF_MONTH, iDay);
			cal.set(Calendar.HOUR_OF_DAY, iHour);
			cal.set(Calendar.MINUTE, iMin);
			return cal;
		}

		public long getTime() {
			@SuppressWarnings("deprecation")
			Date date = new Date(iYear, iMonth, iDay, iHour, iMin);
			return date.getTime();
		}

		@Override
		public int compare(StoryDate cur, StoryDate next) {
			if (cur == null || next == null)
				return 0;

			long curCal = cur.getTime();
			long nextCal = next.getTime();

			return curCal > nextCal ? -1 : (curCal < nextCal ? 1 : 0);
		}
	}

	public static class StoryLikeData {
		public Emotion emotion;
		public ActorData actor;

		/**
		 * 느낌 값
		 */
		public static enum Emotion {
			/**
			 * 좋아요
			 */
			LIKE("LIKE"),
			/**
			 * 멋져요
			 */
			COOL("COOL"),
			/**
			 * 기뻐요
			 */
			HAPPY("HAPPY"),
			/**
			 * 슬퍼요
			 */
			SAD("SAD"),
			/**
			 * 힘내요
			 */
			CHEER_UP("CHEER_UP"),
			/**
			 * 정의되지 않은 느낌
			 */
			NOT_DEFINED("NOT_DEFINED");

			final String papiEmotion;

			Emotion(final String papiEmotion) {
				this.papiEmotion = papiEmotion;
			}

			public static Emotion getEmotion(final String emotionString) {
				for (Emotion emotion : Emotion.values()) {
					if (emotion.papiEmotion.equals(emotionString))
						return emotion;
				}
				return NOT_DEFINED;
			}
			// }
		}

		public void setEmoticon(int index) {
			emotion = Emotion.values()[index];
		}
	}

	public static class StoryCommentData {
		public String text;
		public ActorData writer;

		public StoryCommentData(String text, ActorData writer) {
			this.text = text;
			this.writer = writer;
		}

	}

	/**
	 * FIXME 목차 구하는 알고리즘에 맞도록 구조 변경이 필요 함.
	 */
	public static class TableContents {

		// FIXME 임시로 그냥 월별로 분류하도록 구현함.
		public enum TABLE_CONTENTS_TYPE {
			PART_FOREM_01_TILL_04, // 1월~4월
			PART_FOREM_05_TILL_08, // 5월~8월
			PART_FOREM_09_TILL_12 // 9월~12월
		}

		public static String convertTypeToStr(TABLE_CONTENTS_TYPE type) {
			switch (type) {
			case PART_FOREM_01_TILL_04:
				return "JANUARY - APRIL";
			case PART_FOREM_05_TILL_08:
				return "MAY - AUGUST";
			case PART_FOREM_09_TILL_12:
				return "SEPTEMBER - DECEMBER";
			default:
				return "";
			}
		}

		public static String convertMonthToStr(int month) {
			switch (month) {
			case 1:
			case 2:
			case 3:
			case 4:
				return convertTypeToStr(TABLE_CONTENTS_TYPE.PART_FOREM_01_TILL_04);
			case 5:
			case 6:
			case 7:
			case 8:
				return convertTypeToStr(TABLE_CONTENTS_TYPE.PART_FOREM_05_TILL_08);
			case 9:
			case 10:
			case 11:
			case 12:
				return convertTypeToStr(TABLE_CONTENTS_TYPE.PART_FOREM_09_TILL_12);
			default:
				return "";
			}
		}

		private int[] arrCountInfo = null;

		private LinkedHashMap<TABLE_CONTENTS_TYPE, Integer> mapResult = null;

		public TableContents() {
			mapResult = new LinkedHashMap<StoryData.TableContents.TABLE_CONTENTS_TYPE, Integer>();
			arrCountInfo = new int[3]; // FIXME enum 카운트와 동기화 시켜야 함.
		}

		public int getCount(TABLE_CONTENTS_TYPE type) {
			if (mapResult != null && mapResult.containsKey(type))
				return mapResult.get(type);
			return 0;
		}

		public void putData(int month) {
			switch (month) {
			case 1:
			case 2:
			case 3:
			case 4:
				mapResult.put(TABLE_CONTENTS_TYPE.PART_FOREM_01_TILL_04, arrCountInfo[0]++);
				break;
			case 5:
			case 6:
			case 7:
			case 8:
				mapResult.put(TABLE_CONTENTS_TYPE.PART_FOREM_05_TILL_08, arrCountInfo[1]++);
				break;
			case 9:
			case 10:
			case 11:
			case 12:
				mapResult.put(TABLE_CONTENTS_TYPE.PART_FOREM_09_TILL_12, arrCountInfo[2]++);
				break;

			default:
				break;
			}

		}
	}

}
