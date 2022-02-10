package com.snaps.mobile.activity.book;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.storybook.IOnStoryDataLoadListener;
import com.snaps.common.storybook.IStoryDataStrategy;
import com.snaps.common.storybook.IStoryDataStrategy.eSTORY_DATA_SORT_TYPE;
import com.snaps.common.storybook.StoryData;
import com.snaps.common.storybook.StoryData.ImageInfo;
import com.snaps.common.storybook.StoryData.StoryCommentData;
import com.snaps.common.storybook.StoryData.StoryLikeData;
import com.snaps.common.storybook.StoryData.StoryLikeData.Emotion;
import com.snaps.common.storybook.StoryDataFactory;
import com.snaps.common.storybook.StoryDataType;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateInfo;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_KAKAKAO;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.activity.book.SNSBookRecorder.SNSBookInfo;
import com.snaps.mobile.activity.book.SNSBookRecorder.SNSBookTemplateBgRes;
import com.snaps.mobile.activity.book.StoryBookChapterRuler.StoryChapterInfo;
import com.snaps.mobile.activity.book.StoryStyleFactory.eStoryDataStyle;
import com.snaps.mobile.utils.ui.CalcMMToPX;
import com.snaps.mobile.utils.ui.CalcViewRectUtil;
import com.snaps.mobile.utils.ui.MultiLineTextData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class StoryBookDataManager {
	final float PXPERMM = 1.580f;
	CalcMMToPX calc = new CalcMMToPX(PXPERMM);
	final int TOP_MARGIN = 39;//calc.calcMM(20.f);
	final int TOP_MARGIN_WITHCATEGORY = 107;//calc.calcMM(60.f);
	final int BOTTOM_MARGIN = 21;//calc.calcMM(15.f);
	// 시작 위 
	final int START_MARGIN = 16;
	// 스토리 1/4 넓이 
	final int POSTING_WIDTH = 137;
	// 스토리 중간 마진 
	final int MIDDLE_MARGIN = 12;
	// 페이지 간 마진.. 
	final int SIDE_MARGIN	= 30;
	
	int cHeight = 0; // 현재 높이..(높이계산용)
	int cStart = 0;
	
	final int ED_WIDTH = 664;
	final int ED_HEIGHT = 470;
	
	
	final int NOTE_UP_MARGIN = 7;
	final int NOTE_DOWN_MARGIN = 7;
	final int IMAGE_GAP = 1;
	
	
	

	private volatile static StoryBookDataManager gInstance = null;

	/**
	 * 데이터 인스턴스 생성은 아래 함수에 의해서만 생성할 것.
	 * 
	 * @param style
	 * @param con
	 * @param storyDataType
	 * @return
	 */
	public static StoryBookDataManager createInstance(eStoryDataStyle style, Context con, StoryDataType storyDataType) {
		return StoryStyleFactory.createStoryData(style, con, storyDataType);
	}

	// 겔러리 처리를 위해 인스턴스를 글로벌로 사용함..
	public StoryBookDataManager(StoryDataType type) {

		if (gInstance != null)
			releaseInstance();

		gInstance = this;

		dataManager = StoryDataFactory.createFactory(type);

		chapterRuler = new StoryBookChapterRuler();
		ENABLE_MAX_HEIGHT = ED_HEIGHT - BOTTOM_MARGIN - TOP_MARGIN;
	}

	public static StoryBookDataManager getInstance() {
		return gInstance;
	}

	public static void releaseInstance() {
		gInstance = null;
	}

	public static final int MULTI_TEMPLATE_COVER_IDX = 0;
	public static final int MULTI_TEMPLATE_INDEX_IDX = 1;
	public static final int MULTI_TEMPLATE_TITLE_IDX = 2;
	public static final int MULTI_TEMPLATE_PAGE_IDX = 3;
	
	final String DEFAULT_PROFILE_IMG_PATH = "/Upload/Data1/Resource/sticker/edit/Est138_gg.png";

	
	final int START_MARGIN_MM = 5;
	final int RELPY_START_MARGIN_MM = 3;
	final int REPLY_UP_MARGIN_MM = 5;
	final int NOTE_UP_MARGIN_MM = 7;
	final int NOTE_DOWN_MARGIN_MM = 7;
	final int STORY_WIDTH_MM = 87;

	int ENABLE_MAX_HEIGHT = 0;

	public enum eStoryBookChapter {
		LEFT_CHAPTER, NO_CHAPTER, RIGHT_CHAPTER
	}

	public interface IOnPageMakeListener {
		public void update(float per);
	}

	public void setOnPageMakeListener(IOnPageMakeListener lis) {
		onPageMakeListener = lis;
	}

	IOnPageMakeListener onPageMakeListener = null;

	int PAGE_ADJUST_TEXTWIDTH = 10;

	float REPLY_FONT_RATIO = 0.55f;// 댓글인경우 괜찮다. 렌더 텍스트 크기를 맞추기 위해 설정..
	final float NOTE_FONT_RATIO = 0.75f;// 댓글인경우 괜찮다. 렌더 텍스트 크기를 맞추기 위해 설정..

	final int BACKGROUND_MARGIN = calc.calcMM(5.f);

	final int DEFAULT_COMMENT_COUNT = 30;
	final int DEFAULT_PHOTO_COUNT = 10;

	String PAGE_LEFT_CATEGORY_BGRESOURCE = "";
	String PAGE_BGRESOURCE = "";
	String PAGE_RIGHT_CATEGORY_BGRESOURCE = "";

	String PAGE_LEFT_CATEGORY_TARGETID = "";
	String PAGE_TARGETID = "";
	String PAGE_RIGHT_CATEGORY_TARGETID = "";

	Context context = null;
	IStoryDataStrategy dataManager = null;

	int commentCountLimit = DEFAULT_COMMENT_COUNT;
	int photoCountLimit = DEFAULT_PHOTO_COUNT;

	// chapter index
	int m_iChapterNumber = 1;

	MyPhotoSelectImageData titleProfileImageData = null;

	protected int cPage = 0; // 현재 페이지..(0~)
	protected int cSide = 0; // 현재 쪽...(0,1,2,3)

	
	int categoryHeight_L = 0;
	int categoryHeight_R = 0;

	// 테스트 데이터..
	int createCategory = 3;// 3페이지마다 카테고리 화면을 만든다..
	SnapsPage workPage = null;

	// 스토리 배경용
	SnapsLayoutControl postBg = null;
	ArrayList<SnapsPage> pages = null;

	// 스토리 배경 유무..
	boolean isStoryBackground = false;

	String pageMonth = "";
	String pageYear = "";
	String currentStoryDate = null;
	String overLimitStoryStartDate = null;
	String overLimitStoryEndDate = null;
	boolean isOverLimitStoryCount = false;

	protected eStoryBookChapter currentChapter;

	/**
	 * 배경 포스트 배경.. 날짜 시간 텍스트 사진 좋아요 느낌 댓글
	 */
	SnapsTemplate pageTemplate;
	SnapsBgControl bgControl;
	// 이미지 컨트롤
	SnapsLayoutControl postImageLayoutControl;
	SnapsLayoutControl feelActor1ImageLayoutControl;

	// common
	SnapsTextControl dateTextControl;
	SnapsTextControl timeTextControl;
	SnapsTextControl noteTextControl;
	SnapsTextControl feelCountTextControl;
	SnapsTextControl commentCountTextControl;
	SnapsTextControl etcCountTextControl;
	SnapsTextControl actorTextControl;
	SnapsTextControl commentTextControl;

	// 스티커들..
	// 느낌 하트
	SnapsClipartControl hartCilpart;
	// 말풍선.
	SnapsClipartControl ballonCilpart;
	// 좋아요
	SnapsClipartControl likeCilpart;
	// 힘내요
	SnapsClipartControl cheerUpCilpart;
	// 기뻐요
	SnapsClipartControl happyCilpart;
	// 슬퍼용
	SnapsClipartControl sadCilpart;
	// 멋져요
	SnapsClipartControl coolCilpart;
	// 그외 +<
	SnapsClipartControl etcCilpart;
	// 라인1
	SnapsClipartControl lineCilpart;
	// 라인2
	SnapsClipartControl line2Cilpart;
	// 타이틀..
	SnapsClipartControl titleCilpart;
	// 디폴트 썸네일
	SnapsClipartControl defaultProfileThumbCilpart;

	StoryBookChapterRuler chapterRuler = null;

	int totalPage = 0; // 페이지 토탈..

	ArrayList<SnapsControl> layouts = null;
	ArrayList<SnapsControl> textControls = null;
	ArrayList<SnapsControl> stickers = null;

	// 테스트 데이터..
	private String templateId = null;

	private String productCode = "";

	private String startDate = "20141006";
	private String endDate = "20150131";

	private String projectTitle = "";
	private String coverType = "";
	private String paperCode = "";

	private int commentCount = 0;
	private int photoCount = 0;
	private int totalPageCount = 0;

	private eStoryDataStyle storybookStyle = eStoryDataStyle.NONE;

	/**
	 * 기간에 맞게 스토리를 가져오는 함수...
	 * 
	 * @param startDate
	 * @param endDate
	 * @param listener
	 */
	public void getStoryies(String startDate, String endDate, int commentCount, int photoCount, IOnStoryDataLoadListener listener) {
		dataManager.getStoryies(startDate, endDate, commentCount, photoCount, listener);
	}

	public StoryData getStory(int index) {
		return dataManager.getStory(index);
	}

	public int getStoryCount() {
		return dataManager.getStoryCount();
	}

	public ArrayList<StoryData> getSortedStories(eSTORY_DATA_SORT_TYPE sortType) {
		return dataManager.getSortedStories(sortType);
	}

	public void removeStories(List<String> removeList) {
		dataManager.removeStories(removeList);
	}

	public void requestStoriesDetail() {
		dataManager.requestStoriesDetail();
	}

	public void setStoryDataLoadListener(IOnStoryDataLoadListener ls) {
		dataManager.setStoryDataLoadListener(ls);
	}

	public abstract SnapsTemplate setCoverTemplate(SnapsTemplate template);
	public abstract SnapsTemplate setTitleTemplate(SnapsTemplate template);
	public abstract SnapsTemplate setIndexTemplate(SnapsTemplate template);
	public abstract SnapsTemplate setPageTemplate(SnapsTemplate template);

	public void setBgResources(SNSBookTemplateBgRes resources) {

		if (resources == null)
			return;

		PAGE_LEFT_CATEGORY_BGRESOURCE = resources.getLeftResPath();
		PAGE_BGRESOURCE = resources.getCenterResPath();
		PAGE_RIGHT_CATEGORY_BGRESOURCE = resources.getRightResPath();

		PAGE_LEFT_CATEGORY_TARGETID = resources.getLeftResId();
		PAGE_TARGETID = resources.getCenterResId();
		PAGE_RIGHT_CATEGORY_TARGETID = resources.getRightResId();
	}

	/**
	 * 날짜부분 만들기..
	 * 
	 * @param data
	 */
	abstract int makeDatePart(StoryData data, StoryChapterInfo chapterInfo, boolean isSizeCheck);
	abstract void makeChapterControl(StoryChapterInfo chapterInfo, eStoryBookChapter kind);
	abstract void removeRightControls();
	public abstract SNSBookInfo getInfo() throws Exception;

	void makeTemplate(ArrayList<StoryData> stories, StoryChapterInfo chapterInfo) {

		ArrayList<StoryData> chapterStoryies = new ArrayList<StoryData>();
		for (int i = chapterInfo.startStoryIndex; i <= chapterInfo.endStoryIndex; i++) {
			chapterStoryies.add(stories.get(i));
		}

		currentStoryDate = stories.get(chapterInfo.startStoryIndex).createdAt;
		setPageMonth();

		makeChapter(chapterInfo);
		chapterInfo.setIndexPage(cPage * 2 + 4 + ((cSide > 1) ? 0 : -1));

		makeTemplate(chapterStoryies, chapterInfo, false);
	}
	
	void checkRestHeight(int storyHeight, StoryChapterInfo chapterInfo) {
		// 남아있는 영역 크기보다 크고 들어갈수 있는 전체 높이보다 작으면 페이지 이동..
		if (getRestHeightAtSide() < storyHeight && storyHeight <= ENABLE_MAX_HEIGHT) {
			// 옆면으로 이동..
			checkEnable(0, chapterInfo, true);
			// 회색배경을 위해.. 마진만큼.. 내린다.
			// cHeight += calc.calcMM(bgMargin);
		}
	}

	boolean isOverLimitPageCount() {
		if (pages == null)
			return false;

		return pages.size() >= StoryBookFragmentActivity.LIMIT_MAX_TWICE_PAGE_COUNT;
	}

	void makeTemplate(ArrayList<StoryData> stories, StoryChapterInfo chapterInfo, boolean isCalcMode) {

		if (isCalcMode) {
			isOverLimitStoryCount = false;
			overLimitStoryStartDate = stories.get(0).createdAt;
		}

		if (isOverLimitStoryCount)
			return;

		int storyIndex = 0;

		String prevStoryDate = null;
		for (StoryData data : stories) {
			int sPage = 0;
			int ePage = 0;
			currentStoryDate = data.createdAt;

			if (!isOverLimitPageCount() && !isCalcMode) {
				prevStoryDate = currentStoryDate; // 실제 페이지를 생성할 때, 마지막으로 유효한 기간을 저장 해 놓음.
			}

			checkEnable(0, chapterInfo);
			sPage = cPage;

			//그냥 빈공간을 만들고 시작하는 경우가 있어서 예외 처리..
			if(cHeight > (ED_HEIGHT - BOTTOM_MARGIN) / 3)
			{
				int storyHeight = makeDatePart(data, chapterInfo, true);
				storyHeight += makeNotePart(data, chapterInfo, true);
				storyHeight += makeImagePart(data, chapterInfo, true);

				// 남아있는 영역 크기보다 크고 들어갈수 있는 전체 높이보다 작으면 페이지 이동..
				if (getRestHeightAtSide() < storyHeight && storyHeight <= ENABLE_MAX_HEIGHT) {
					// 옆면으로 이동..
					checkEnable(0, chapterInfo, true);
				}
				
				makeDatePart(data, chapterInfo, false);

				makeNotePart(data, chapterInfo, false);
				
			} else {
				boolean isExistTextContents = data != null && data.content != null && data.content.trim().length() > 0;
				int storyHeight = 0;
	
				//텍스트가 존재 하는 경우
				if(isExistTextContents) {
					//날짜+노트의 영역 계산(넘어가는지..)
					storyHeight = makeDatePart(data, chapterInfo, true);
					storyHeight += makeNotePart(data, chapterInfo, true);
					checkRestHeight(storyHeight, chapterInfo);
					
					//계산 된 영역을 기준으로 실제 날짜+노트의 위치를 셋팅한다.
					makeDatePart(data, chapterInfo, false);
					makeNotePart(data, chapterInfo, false);
					
					//이미지가 들어갈 영역 계산
					storyHeight = makeImagePart(data, chapterInfo, true);
					checkRestHeight(storyHeight, chapterInfo);
				} else {
					//날짜+이미지 영역 계산
					storyHeight = makeDatePart(data, chapterInfo, true);
					storyHeight += makeImagePart(data, chapterInfo, true);
					checkRestHeight(storyHeight, chapterInfo);
					
					//계산 된 영역을 기준으로 실제 날짜+이미지의 위치를 셋팅한다.
					makeDatePart(data, chapterInfo, false);
				}
			}

			// part 3 이미지
			makeImagePart(data, chapterInfo, false);

			// part 4 느낌
			makeResponsePart(data, chapterInfo);

			// part 5 댓글..
			makeCommentPart(data, chapterInfo);

			boolean isFlag = false;
			if (isStoryBackground)
				isFlag = (data.commentCount == 0) ? true : false;
			// isFlag = (data.commentCount == 0) ? false : true;

			setEndStoryBackground(cHeight, isFlag);

			cHeight += calc.calcMM(5);

			ePage = cPage;

			// 최대 페이지가 넘으면 최대 페이지까지만...
			if (isOverLimitPageCount()) {

				if (pages != null) {
					for (int ii = pages.size() - 1; ii >= StoryBookFragmentActivity.LIMIT_MAX_PAGE_COUNT; ii--) {
						pages.remove(ii);
					}
				}

				cPage = StoryBookFragmentActivity.LIMIT_MAX_PAGE_COUNT;

				if (isCalcMode) {
					if (onPageMakeListener != null) {
						onPageMakeListener.update(50);
					}
				} else {
					isOverLimitStoryCount = true;
					overLimitStoryEndDate = prevStoryDate;

					dataManager.setStoryPeriod(overLimitStoryStartDate, overLimitStoryEndDate);
				}

				break;
			}

			// 하나의 스토리에 대한 페이지 정보를 미리 저장을 한다.
			if (isCalcMode) {
				if (onPageMakeListener != null) {
					onPageMakeListener.update((storyIndex / (float) stories.size()) * 50);
				}
				chapterRuler.addStoryPageInfo(storyIndex, data.createdAt, sPage, ePage, data.likeCount, data.images);
			}

			storyIndex++;
		}
	}

	void makeChapter(StoryChapterInfo chapterInfo) {
		if (workPage == null || (cSide == 2 || cSide == 3)) {
			cHeight = ED_HEIGHT;
			cSide = 3;
			cStart = calc.calcMM(13);
			
			currentChapter = eStoryBookChapter.LEFT_CHAPTER;
		} else {
			// 배경을 바꾸고 챕터 텍스트를 넣는다.
			setPageBgResource(bgControl, eStoryBookChapter.RIGHT_CHAPTER);
			makeChapterControl(chapterInfo, eStoryBookChapter.RIGHT_CHAPTER);
			removeRightControls();
			// 페이지를 넘기기위해...
			cHeight = ED_HEIGHT;
			cSide = 1;
		}
	}

	void clearCurrentPageData() {
		// 데이터 초기화..
		cPage = 0;
		cHeight = 0;
		cStart = 0;
		cSide = 0;
		cStart = 0;
		workPage = null;
		pages.clear();
	}

	/***
	 * 해당 크기가 현재 작업 페이지 에 추가가 가능하지 체크 충분하지가 않다면 새로운 페이지를 생성 및 위치값 초기화..
	 * 
	 * @param addHeight
	 */
	boolean checkEnable(int addHeight, StoryChapterInfo chapterInfo) {
		return checkEnable(addHeight, chapterInfo, false);
	}

	boolean checkEnable(int addHeight, StoryChapterInfo chapterInfo, boolean isForce) {

		boolean isNewPage = false;
		boolean isChange = false;

		if (workPage == null) {
			isNewPage = true;
		}

		if ((cHeight + addHeight) > (ED_HEIGHT - BOTTOM_MARGIN) || isForce) {
			isNewPage = true;
		}

		// 충분하지 않으면
		if (isNewPage) {
			// 스토리에 배경을 넣어준다.
			// 페이지가 넘어가는 경우...
			setEndStoryBackground(ED_HEIGHT - BOTTOM_MARGIN, false);

			if (cSide >= 3 || workPage == null) {

				// 새로운 페이지가 만들어졌을때 달설정..
				setPageMonth();

				// 신규 페이지 만들기
				// 시작 위치 초기화..
				workPage = makeSnapsPage(pageTemplate.getPages().get(MULTI_TEMPLATE_PAGE_IDX), pageTemplate.info, chapterInfo);
				cHeight = categoryHeight_L;
				cStart = calc.calcMM(13);
				cSide = 0;
				cPage++;
				pages.add(workPage);

			} else {
				cSide++;
				if (cSide == 1) {
					cStart = calc.calcMM(13 + STORY_WIDTH_MM + 8);
					cHeight = categoryHeight_L;
				} else if (cSide == 2) {
					cStart = calc.calcMM(13 + STORY_WIDTH_MM + 8 + STORY_WIDTH_MM + 15 + 15);
					cHeight = categoryHeight_R;
				} else if (cSide == 3) {
					cStart = calc.calcMM(13 + STORY_WIDTH_MM + 8 + STORY_WIDTH_MM + 15 + 15 + STORY_WIDTH_MM + 8);
					cHeight = categoryHeight_R;
				}
			}

			setStartStoryBackground();

			isChange = true;
		}

		return isChange;
	}

	void setPageMonth() {

		if (currentStoryDate != null) {
			pageMonth = StringUtil.getDateStringByFormat(currentStoryDate, "MM");
		}

		setPageYear();

	}

	void setPageYear() {
		if (currentStoryDate != null) {
			pageYear = StringUtil.getDateStringByFormat(currentStoryDate, "yyyy");
		}
	}

	int getRestHeightAtSide() {
		int rest = ED_HEIGHT - BOTTOM_MARGIN - cHeight;
		return rest;
	}

	int getMaxHeightAtSide(int side) {
		int height = 0;
		if (side == 0) {
			height = categoryHeight_L;
		} else if (side == 1) {
			height = categoryHeight_L;
		} else if (side == 2) {
			height = categoryHeight_R;
		} else if (side == 3) {
			height = categoryHeight_R;
		}

		return ED_HEIGHT - BOTTOM_MARGIN - height;
	}

	// 페이지 생성..

	/***
	 * 텍스트부분 만들기..
	 * 
	 * @param data
	 */
	int makeNotePart(StoryData data, StoryChapterInfo chapterInfo, boolean isSizeCheck) {
		if (data.content == null || data.content.length() <= 0)
			return 0;

		// 라인텍스트 정보를 가져온다.
		int limitSize = 0;
		float fontRatio = 0.f;
		if (Build.VERSION.SDK_INT > 20) { // 롤리팝 이상 체크

			limitSize = calc.calcMM(STORY_WIDTH_MM) - calc.calcMM(START_MARGIN_MM * 5);
			fontRatio = NOTE_FONT_RATIO;
		} else {
			limitSize = calc.calcMM(STORY_WIDTH_MM) - calc.calcMM(START_MARGIN_MM - 1);
			fontRatio = NOTE_FONT_RATIO;
		}

		MultiLineTextData textData = CalcViewRectUtil.getTextControlRect(context, 
				noteTextControl.format.fontFace, 
				noteTextControl.format.fontSize, 
				limitSize, 
				data.content, 
				fontRatio,
				FontUtil.TEXT_TYPE_CONTENTS);

		if (textData == null)
			return 0;

		SnapsTextControl note = null;
		note = noteTextControl.copyControl();
		note.lineSpcing = Const_KAKAKAO.LINE_SPACING;
		String sText = "";

		if (isSizeCheck) {
			return textData.getTextTotalHeight(note) + calc.calcMM(NOTE_DOWN_MARGIN_MM);
		}

		// 현재 남아있는 영역에 텍스트가 들어갈수 있는지 확인한다.
		// 없으면 다음쪽으로 넘긴다.
		int enableHeight = getRestHeightAtSide();
		if (textData.getTextHeight() > enableHeight) {
			checkEnable(0, chapterInfo, true);
		}

		// 콘텐츠가 들어갈수 있는경우...
		if (enableHeight >= textData.getTextTotalHeight(note)) {
			for (int i = 0; i < textData.getLineTexts().size(); i++) {
				if (!sText.equals(""))
					sText += "\n";
				sText += textData.getLineTexts().get(i);
			}
			note = noteTextControl.copyControl();
			note.lineSpcing = Const_KAKAKAO.LINE_SPACING;
			note.text = sText;
			note.x = (cStart + calc.calcMM(START_MARGIN_MM)) + "";
			note.y = cHeight + "";
			note.height = textData.getTextTotalHeight(note) + "";
			note.format.fontSize = Float.parseFloat(note.format.fontSize) * NOTE_FONT_RATIO + "";

			CalcViewRectUtil.makeLineText(note, textData.getLineTexts(), textData.getTextHeight());
			workPage.addControl(note);
			cHeight += note.getIntHeight();
			cHeight += calc.calcMM(NOTE_DOWN_MARGIN_MM);

		} else { // 가능영역이 작은경우...
			int startLine = 0;
			while (true) {
				enableHeight = getRestHeightAtSide();
				if (startLine >= textData.getLineTexts().size())
					break;

				note = noteTextControl.copyControl();
				note.lineSpcing = Const_KAKAKAO.LINE_SPACING;
				startLine = textData.getExtractTextByHeight(startLine, enableHeight, note);
				note.x = (cStart + calc.calcMM(START_MARGIN_MM)) + "";
				note.y = cHeight + "";
				note.format.fontSize = Float.parseFloat(note.format.fontSize) * NOTE_FONT_RATIO + "";

				workPage.addControl(note);
				cHeight += note.getIntHeight();
				cHeight += calc.calcMM(NOTE_DOWN_MARGIN_MM);

				// 남머지 텍스트가 있으면 쪽을 옮긴다.
				if (startLine < textData.getLineTexts().size())
					checkEnable(0, chapterInfo, true);

			}
		}

		return 0;
	}
	/***
	 * 이미지를 구성하는 함수..
	 * 
	 * @param data
	 */
	int makeImagePart(StoryData data, StoryChapterInfo chapterInfo, boolean isSizeCheck) {
		if (data.images == null || data.images.size() <= 0)
			return 0;

		for (ImageInfo info : data.images) {

			// 이미지 데이터 생성..
			MyPhotoSelectImageData imgData = new MyPhotoSelectImageData();
			imgData.KIND = Const_VALUES.SELECT_KAKAO;
			imgData.FB_OBJECT_ID = data.id;
			// imgData.F_IMG_NAME = displayName;
			imgData.PATH = info.original;
			imgData.THUMBNAIL_PATH = info.small;
			imgData.KAKAOBOOK_DATE = data.createdAt;
			// 원본 이미지 w, h 추가
			imgData.F_IMG_WIDTH = info.getOriginWidth();// StringUtil.getTitleAtUrl(info.original, "width");
			imgData.F_IMG_HEIGHT = info.getOriginHeight();// StringUtil.getTitleAtUrl(info.original, "height");

			// 영역체크..
			// 현재 최대이미지 높
			int maxHeight = getMaxHeightAtSide(cSide);
			String height = maxHeight + "";

			Rect rc = CalcViewRectUtil.getLayoutControlRect(postImageLayoutControl.width, height, imgData.F_IMG_WIDTH, imgData.F_IMG_HEIGHT);

			// 이미지 크기가 정해진 크기보다 1cm 미만으로 차이가 날때는 그냥 이미지를 중간 정렬을 해서 배치를 한다.
			// int restHeight = getRestHeightAtSide(); // 현재 남아있는 높이
			int imgHeight = rc.height();

			// clipRect를 구한다.
			// 첫번째 이미지 높이만 구해서 리턴...
			if (isSizeCheck) {
				return rc.height();
			}

			boolean isNewSide = checkEnable(imgHeight, chapterInfo);
			if (TOP_MARGIN == cHeight) {
			}
			SnapsLayoutControl layoutControl = makeLayoutCotrol(postImageLayoutControl);
			layoutControl.imgData = imgData;
			layoutControl.angle = "0";
			layoutControl.imagePath = imgData.PATH;
			layoutControl.imageLoadType = imgData.KIND;
			layoutControl.y = cHeight + ""; // 새로운 열로 이동시 상단 마진을 없앤다.
			layoutControl.x = cStart + "";
			layoutControl.height = rc.height() + "";

			workPage.addLayout(layoutControl);

			cHeight += rc.height();
			cHeight += calc.calcMM(1);

		}

		cHeight += calc.calcMM(2);
		return 0;
	}
	/***
	 * 
	 * @param data
	 * @return
	 */
	void makeResponsePart(StoryData data, StoryChapterInfo chapterInfo) {
		final int PAGE_REPLY_HEIGHT = 5;

		// 영역체크..
		if (checkEnable(calc.calcMM(PAGE_REPLY_HEIGHT + 2), chapterInfo))
			cHeight += calc.calcMM(3);// BACKGROUND_MARGIN;

		// 느낌 스티커
		SnapsClipartControl feelSticker = makeStickerControl(hartCilpart);
		feelSticker.x = (cStart + calc.calcMM(5)) + "";
		feelSticker.y = (cHeight + (calc.calcMM(PAGE_REPLY_HEIGHT) - 0) / 2) + "";
		feelSticker.setOffsetY(-2);
		workPage.addControl(feelSticker);

		// 느낌수 텍스트
		SnapsTextControl feelCountText = makeTextControl(feelCountTextControl, data.likeCount + "", false, REPLY_FONT_RATIO, FontUtil.TEXT_TYPE_CONTENTS);
		feelCountText.x = feelSticker.getIntX() + feelSticker.getIntWidth() + calc.calcMM(2) + 2 + "";

		feelCountText.y = (cHeight + (calc.calcMM(PAGE_REPLY_HEIGHT) - 0) / 2) + "";
		feelCountText.setOffsetX(-3);
		feelCountText.setOffsetY(-3);
		workPage.addControl(feelCountText);

		// 댓글 갯수 스트커
		SnapsClipartControl commentSticker = makeStickerControl(ballonCilpart);
		commentSticker.x = cStart + ballonCilpart.getIntX() - hartCilpart.getIntX() + 3 + "";
		commentSticker.setOffsetY(-2);
		commentSticker.y = (cHeight + (calc.calcMM(PAGE_REPLY_HEIGHT) - 0) / 2) + "";
		workPage.addControl(commentSticker);

		// 댓글수 텍스트
		SnapsTextControl commentCountText = makeTextControl(feelCountTextControl, data.commentCount + "", false, REPLY_FONT_RATIO, FontUtil.TEXT_TYPE_CONTENTS);
		commentCountText.x = commentSticker.getIntX() + commentSticker.getIntWidth() + 4 + "";
		commentCountText.width = "7";
		commentCountText.height = "5";
		commentCountText.setOffsetX(-2);
		commentCountText.setOffsetY(-3);
		commentCountText.y = (cHeight + (calc.calcMM(PAGE_REPLY_HEIGHT) - 0) / 2) + "";

		workPage.addControl(commentCountText);

		if (data.likes != null) {
			int imageGap = 4;
			int startPos = cStart + calc.calcMM(STORY_WIDTH_MM - 10 - imageGap);
			// 느낌이 5개 초과하면 etc가 추가되어야 한다...
			if (data.likes.size() > 5) {
				SnapsClipartControl more = makeStickerControl(etcCilpart);
				startPos = cStart + calc.calcMM(STORY_WIDTH_MM - 8);
				more.x = startPos + "";
				more.width = "4";
				more.height = "2";
				more.y = (cHeight + (calc.calcMM(PAGE_REPLY_HEIGHT) - more.getIntHeight()) / 2) + "";
				workPage.addControl(more);

				// 추가 갯수를 넣는다.
				SnapsTextControl etc = etcCountTextControl.copyControl();

				etc.x = startPos + more.getIntWidth() + 1 + "";
				etc.height = "4";
				etc.y = (cHeight + (calc.calcMM(PAGE_REPLY_HEIGHT) - etc.getIntHeight()) / 2) + "";
				etc.width = "6";
				etc.text = data.likeCount - 5 + "";
				etc.format.fontSize = 6 * REPLY_FONT_RATIO + "";
				workPage.addControl(etc);

				startPos -= (more.getIntWidth() + imageGap / 2);
				startPos -= 10;
			}

			int thumbCount = 0;
			for (StoryLikeData like : data.likes) {
				if (thumbCount >= 5)
					break;
				SnapsLayoutControl profileImage = makeLayoutCotrol(feelActor1ImageLayoutControl);
				// 이미지 데이터 생성..
				MyPhotoSelectImageData imgData = new MyPhotoSelectImageData();
				imgData.KIND = Const_VALUES.SELECT_KAKAO;
				imgData.FB_OBJECT_ID = data.id;

				try {
					String thumbNail = like.actor.profileThumbnailUrl;
					imgData.PATH = thumbNail.equals("") ? defaultProfileThumbCilpart.resourceURL : thumbNail;
					imgData.THUMBNAIL_PATH = like.actor.profileThumbnailUrl;
					profileImage.imgData = imgData;
					profileImage.angle = "0";
					profileImage.imagePath = imgData.PATH;
					profileImage.imageLoadType = imgData.KIND;

					profileImage.x = startPos + "";
					profileImage.y = (cHeight + (calc.calcMM(PAGE_REPLY_HEIGHT) - feelActor1ImageLayoutControl.getIntHeight()) / 2) + "";

					workPage.addLayout(profileImage);
				} catch (Exception e) {
					// TODO: handle exception
				}

				// 프로필 이미지 위에 스티커를 넣어준다.
				SnapsClipartControl feelClipart = getFeelClipartControl(like.emotion);
				feelClipart.width = "6";
				feelClipart.height = "6";
				feelClipart.x = (profileImage.getIntX() + profileImage.getIntWidth() - feelClipart.getIntWidth() / 2) + "";
				feelClipart.y = (profileImage.getIntY() + profileImage.getIntHeight() - feelClipart.getIntHeight()) + "";

				workPage.addControl(feelClipart);

				// 프로필 이미지들간 margin
				startPos -= (imageGap + feelActor1ImageLayoutControl.getIntWidth());
				thumbCount++;
			}
		}

		// 라인 추가...
		SnapsClipartControl lineClipart = makeStickerControl(lineCilpart);
		lineClipart.x = cStart + "";
		lineClipart.y = (cHeight + calc.calcMM(PAGE_REPLY_HEIGHT)) + "";
		lineClipart.width = calc.calcMM(STORY_WIDTH_MM) + "";
		if (data.commentCount > 0)
			workPage.addControl(lineClipart);

		cHeight += calc.calcMM(PAGE_REPLY_HEIGHT + 2);

	}

	void makeCommentPart(StoryData data, StoryChapterInfo chapterInfo) {
		if (data.comments == null)
			return;

		for (StoryCommentData comment : data.comments) {
			// 프로필 사진
			MyPhotoSelectImageData imgData = new MyPhotoSelectImageData();
			imgData.KIND = Const_VALUES.SELECT_KAKAO;
			imgData.FB_OBJECT_ID = data.id;

			try {
				String thumbNail = comment.writer.profileThumbnailUrl;
				imgData.PATH = thumbNail.equals("") ? defaultProfileThumbCilpart.resourceURL : thumbNail;
				imgData.THUMBNAIL_PATH = comment.writer.profileThumbnailUrl;
			} catch (Exception e) {
				// TODO: handle exception
			}
			SnapsLayoutControl profileImage = makeLayoutCotrol(feelActor1ImageLayoutControl);
			profileImage.imgData = imgData;
			profileImage.angle = "0";
			profileImage.imagePath = imgData.PATH;
			profileImage.imageLoadType = imgData.KIND;

			profileImage.x = (cStart + calc.calcMM(START_MARGIN_MM)) + "";
			profileImage.y = cHeight + calc.calcMM(REPLY_UP_MARGIN_MM) + "";

			// 이름.
			SnapsTextControl displayName = makeTextControl(actorTextControl, comment.writer.displayName, true, 1.0f, FontUtil.TEXT_TYPE_COMMENT);
			displayName.y = cHeight + calc.calcMM(REPLY_UP_MARGIN_MM) + "";
			displayName.x = profileImage.getIntX() + profileImage.getIntWidth() + calc.calcMM(RELPY_START_MARGIN_MM) + "";
			displayName.format.fontSize = Float.parseFloat(displayName.format.fontSize) * REPLY_FONT_RATIO + "";
			displayName.height = "4";

			// 댓글.
			SnapsTextControl commentText = makeTextControl(commentTextControl, comment.text, false, REPLY_FONT_RATIO, FontUtil.TEXT_TYPE_COMMENT);
			commentText.x = displayName.x;
			commentText.y = displayName.getIntY() + displayName.getIntHeight() + "";// + calc.calcMM(1) + "";

			// 회색라인..
			SnapsClipartControl line = makeStickerControl(line2Cilpart);
			line.x = cStart + "";

			// 댓글의 bottom 과 프로필 사진의 bottom 비교 큰쪽을 가지고 맞춘다.
			boolean isProfileLargeBottom = ((profileImage.getIntY() + profileImage.getIntHeight()) > (commentText.getIntY() + commentText.getIntHeight()));
			line.y = ((isProfileLargeBottom ? (profileImage.getIntY() + profileImage.getIntHeight()) : (commentText.getIntY() + commentText.getIntHeight())) + "");

			// 영역체크
			// 영역 재설정..
			checkEnable(line.getIntY() + line.getIntHeight() - cHeight, chapterInfo);

			// 중복 코드를 없에 보자...
			profileImage.x = (cStart + calc.calcMM(START_MARGIN_MM)) + "";
			profileImage.y = cHeight + calc.calcMM(REPLY_UP_MARGIN_MM) + "";

			// 이름.
			displayName.y = profileImage.y;// cHeight + calc.calcMM(REPLY_UP_MARGIN_MM) + "";
			displayName.x = profileImage.getIntX() + profileImage.getIntWidth() + calc.calcMM(RELPY_START_MARGIN_MM) + "";

			// 댓글.
			commentText.x = displayName.x;// profileImage.getIntX() + profileImage.getIntWidth() + calc.calcMM(START_MARGIN_MM) + "";
			commentText.y = displayName.getIntY() + displayName.getIntHeight() + "";// + calc.calcMM(1) + "";

			line.x = cStart + "";

			line.width = calc.calcMM(STORY_WIDTH_MM) + "";

			// 댓글의 bottom 과 프로필 사진의 bottom 비교 큰쪽을 가지고 맞춘다.
			isProfileLargeBottom = ((profileImage.getIntY() + profileImage.getIntHeight()) > (commentText.getIntY() + commentText.getIntHeight()));
			line.y = ((isProfileLargeBottom ? (profileImage.getIntY() + profileImage.getIntHeight()) : (commentText.getIntY() + commentText.getIntHeight())) + "");
			workPage.addLayout(profileImage);
			workPage.addControl(displayName);
			workPage.addControl(commentText);

			// 다음에 올 컨트롤 Y위치 설정..
			cHeight = line.getIntY() + calc.calcMM(2);// line.getIntHeight();
			// 마지막 댓글에는 하단 라인을 제거한다.
			if (comment != data.comments.get(data.commentCount - 1)) {
				workPage.addControl(line);
			} else {
				cHeight += calc.calcMM(1);
			}

		}
	}

	/***
	 * 배경 컨트롤을 만드는 함수..
	 * 
	 * @param bgControl
	 * @return
	 */
	SnapsBgControl makeBg(SnapsBgControl bgControl) {
		SnapsBgControl bg = new SnapsBgControl();
		bg.layerName = bgControl.layerName;
		bg.regName = bgControl.regName;
		bg.regValue = bgControl.regValue;
		bg.type = bgControl.type;
		bg.fit = bgControl.fit;
		bg.bgColor = bgControl.bgColor;
		bg.coverColor = bgControl.coverColor;
		bg.srcTargetType = bgControl.srcTargetType;
		bg.srcTarget = bgControl.srcTarget;
		bg.resourceURL = bgControl.resourceURL;
		bg.getVersion = bgControl.getVersion;
		bg.x = "0";
		bg.y = "0";
		bg.width = pageTemplate.info.F_PAGE_PIXEL_WIDTH;
		bg.height = pageTemplate.info.F_PAGE_PIXEL_HEIGHT;

		// 배경 리소스를 설정을 한다.
		setPageBgResource(bg);

		return bg;
	}

	SnapsClipartControl makeStickerControl(SnapsClipartControl control) {
		SnapsClipartControl clipart = new SnapsClipartControl();
		clipart._controlType = SnapsControl.CONTROLTYPE_STICKER;

		clipart.angle = control.angle;
		clipart.alpha = control.alpha;

		clipart.setX(control.x);
		clipart.y = control.y;
		clipart.width = control.width;
		clipart.height = control.height;
		clipart.resourceURL = control.resourceURL;
		clipart.clipart_id = control.clipart_id;

		return clipart;
	}

	/***
	 * 날짜 텍스트 컨트롤을 만드는 함수.
	 * 
	 * @return
	 */
	SnapsTextControl makeDateTextControl(SnapsTextControl control, String date) {
		SnapsTextControl textControl = control.copyControl();
		textControl.width = String.valueOf(Integer.parseInt(control.width) - 4);

		// 날짜 변환 2012.12.12
		String cDate = StringUtil.getDateStringByFormat(date, "yyyy.MM.dd");

		textControl.text = cDate;
		return textControl;
	}

	/***
	 * 시간 텍스트 컨트롤을 만드는 함수.
	 * 
	 * @return
	 */
	SnapsTextControl makeTimeTextControl(SnapsTextControl control, String time) {
		SnapsTextControl textControl = control.copyControl();

		// 날짜 변환 2012.12.12
		// am 10:10
		String cTime = StringUtil.getDateStringByFormat(time, "a hh:mm", Locale.US);
		textControl.text = cTime.toLowerCase();

		return textControl;
	}

	SnapsTextControl makeTextControl(SnapsTextControl control, String text, boolean fixed) {
		return makeTextControl(control, text, fixed, 1.0f, FontUtil.TEXT_TYPE_CONTENTS);
	}
	
	SnapsTextControl makeTextControl(SnapsTextControl control, String text, boolean fixed, int textType) {
		return makeTextControl(control, text, fixed, 1.0f, textType);
	}
	/***
	 * 텍스트를 생성하는 함수..
	 * 
	 * @param control
	 * @param text
	 * @return
	 */
	SnapsTextControl makeTextControl(SnapsTextControl control, String text, boolean fixed, float fontRaio, int textType) {
		SnapsTextControl textControl = control.copyControl();

		if (textControl != null)
			textControl.text = text;

		if (!fixed) { // 영역이 fix되지 않은 경우 영역을 계산을 한다.
			Rect r = CalcViewRectUtil.getTextControlRect(context,
					control.format.fontFace, control.format.fontSize, 
					(int) Float.parseFloat(control.width) - PAGE_ADJUST_TEXTWIDTH, 
					text, 
					fontRaio,
					textControl,
					textType);
			textControl.height = r.height() + "";
			textControl.format.fontSize = Float.parseFloat(control.format.fontSize) * fontRaio + "";
		}

		return textControl;
	}

	SnapsLayoutControl makeLayoutCotrol(SnapsLayoutControl control) {
		SnapsLayoutControl layout = control.copyControl();
		return layout;
	}

	/***
	 * SnapsPage를 만드는 함수..
	 * 
	 * @param p
	 * @param info
	 * @return
	 */
	SnapsPage makeSnapsPage(SnapsPage p, SnapsTemplateInfo info, StoryChapterInfo chapterInfo) {
		SnapsPage page = new SnapsPage(cPage / 2 + 1, info);
		page.type = p.type;
		page.setWidth(p.getWidth() + "");
		page.height = p.height;

		page.border = p.border;
		page.layout = p.layout;
		page.background = p.background;
		workPage = page;

		// 왼쪽에 챕터가 나오는 경우 챕터 내용을 넣어준다.
		if (currentChapter != eStoryBookChapter.RIGHT_CHAPTER) {
			makeChapterControl(chapterInfo, currentChapter);
		}

		bgControl = makeBg(bgControl);

		page.addBg(bgControl);
		return page;
	}
	
	/***
	 * 배경 리소스에 설정하는 함수.
	 * 
	 * @return
	 */
	void setPageBgResource(SnapsBgControl bgControl) {
		setPageBgResource(bgControl, currentChapter);
		currentChapter = eStoryBookChapter.NO_CHAPTER;
	}

	void setPageBgResource(SnapsBgControl bgControl, eStoryBookChapter chapter) {
		if (chapter == eStoryBookChapter.LEFT_CHAPTER) {
			categoryHeight_L = calc.calcMM(60 + 10);
			categoryHeight_R = TOP_MARGIN;// calc.calcMM(TOP_MARGIN);
			bgControl.srcTarget = PAGE_LEFT_CATEGORY_TARGETID;
			bgControl.resourceURL = PAGE_LEFT_CATEGORY_BGRESOURCE;
		} else if (chapter == eStoryBookChapter.NO_CHAPTER) {
			categoryHeight_L = TOP_MARGIN;// calc.calcMM(TOP_MARGIN);
			categoryHeight_R = TOP_MARGIN;// calc.calcMM(TOP_MARGIN);
			bgControl.srcTarget = PAGE_TARGETID;
			bgControl.resourceURL = PAGE_BGRESOURCE;
		} else if (chapter == eStoryBookChapter.RIGHT_CHAPTER) {
			categoryHeight_L = TOP_MARGIN;// calc.calcMM(TOP_MARGIN);
			categoryHeight_R = calc.calcMM(60 + 10);
			bgControl.srcTarget = PAGE_RIGHT_CATEGORY_TARGETID;
			bgControl.resourceURL = PAGE_RIGHT_CATEGORY_BGRESOURCE;
		}
	}

	protected SnapsClipartControl getFeelClipartControl(Emotion emoticon) {
		switch (emoticon) {
			case LIKE :
				return makeStickerControl(likeCilpart);
			case COOL :
				return makeStickerControl(coolCilpart);
			case HAPPY :
				return makeStickerControl(happyCilpart);
			case SAD :
				return makeStickerControl(sadCilpart);
			case CHEER_UP :
				return makeStickerControl(cheerUpCilpart);
			default :
				break;
		}

		return null;
	}

	SnapsLayoutControl bgStory = null;
	String bgColor = "FFF7F8F8";
	void setStartStoryBackground() {

		if (isStoryBackground) {
			SnapsLayoutControl layout = new SnapsLayoutControl();
			layout.setX("" + cStart);
			layout.y = cHeight /* calc.calcMM(bgMargin) */+ "";
			layout.type = "webitem";
			layout.width = (int) calc.calcMM(STORY_WIDTH_MM) + "";
			layout.angle = "0";
			layout.tempImageColor = bgColor;
			layout.bgColor = bgColor;
			layout.regName = "background";
			bgStory = layout;
			workPage.addLayout(bgStory);
		}

		// cHeight += calc.calcMM(bgMargin);

	}

	void setEndStoryBackground(int height, boolean isMargin) {

		if (!isStoryBackground)
			return;

		if (bgStory != null) {
			bgStory.height = (height - bgStory.getIntY()) + "";
		}

		bgStory = null;

	}

	SnapsLayoutControl getColorLayoutControl(String color) {
		SnapsLayoutControl layout = new SnapsLayoutControl();
		layout.setX("" + cStart);
		layout.y = cHeight - BACKGROUND_MARGIN + "";
		layout.type = "webitem";
		layout.width = (int) calc.calcMM(STORY_WIDTH_MM) + "";
		layout.angle = "0";
		layout.tempImageColor = color;
		layout.bgColor = color;
		layout.regName = "background";

		return layout;
	}

	public String getTemplateId() {
		if (gInstance ==  null)
			return null;
		return gInstance.templateId;
	}

	public void setTemplateId(String templateId) {
		if (gInstance ==  null)
			return;
		gInstance.templateId = templateId;
	}

	public String getProductCode() {
		if (gInstance ==  null)
			return null;
		return gInstance.productCode;
	}

	public void setProductCode(String productCode) {
		if (gInstance ==  null)
			return;
		gInstance.productCode = productCode;
	}

	public String getStartDate() {
		if (gInstance ==  null)
			return null;
		return gInstance.startDate;
	}

	public void setStartDate(String startDate) {
		if (gInstance ==  null)
			return;
		gInstance.startDate = startDate;
	}

	public String getEndDate() {
		if (gInstance ==  null)
			return null;
		return gInstance.endDate;
	}

	public void setEndDate(String endDate) {
		if (gInstance ==  null)
			return;
		gInstance.endDate = endDate;
	}

	public String getProjectTitle() {
		if (gInstance ==  null)
			return null;
		return gInstance.projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		if (gInstance ==  null)
			return;
		// 소프트 커버인경우 사용을 한다.
		Config.setPROJ_NAME(projectTitle);
		gInstance.projectTitle = projectTitle;
	}

	public String getCoverType() {
		if (gInstance ==  null)
			return null;
		return gInstance.coverType;
	}

	public void setCoverType(String coverType) {
		if (gInstance ==  null)
			return;
		gInstance.coverType = coverType;
	}

	public String getPaperCode() {
		if (gInstance ==  null)
			return null;
		return gInstance.paperCode;
	}

	public void setPaperCode(String paperCode) {
		if (gInstance ==  null)
			return;
		gInstance.paperCode = paperCode;
	}

	public int getCommentCount() {
		if (gInstance ==  null)
			return 0;
		return gInstance.commentCount;
	}

	public void setCommentCount(int commentCount) {
		if (gInstance ==  null)
			return;
		gInstance.commentCount = commentCount;
	}

	public int getPhotoCount() {
		if (gInstance ==  null)
			return 0;
		return gInstance.photoCount;
	}

	public void setPhotoCount(int photoCount) {
		if (gInstance ==  null)
			return;
		gInstance.photoCount = photoCount;
	}

	public int getTotalPageCount() {
		if (gInstance ==  null)
			return 0;
		return gInstance.totalPageCount;
	}

	public void setTotalPageCount(int totalPageCount) {
		if (gInstance ==  null)
			return;
		gInstance.totalPageCount = totalPageCount;
	}

	public eStoryDataStyle getStorybookStyle() {
		if (gInstance ==  null)
			return eStoryDataStyle.NONE;
		return gInstance.storybookStyle;
	}

	public void setStorybookStyle(eStoryDataStyle storybookStyle) {
		if (gInstance ==  null)
			return;
		gInstance.storybookStyle = storybookStyle;
	}
}
