package com.snaps.mobile.activity.book;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.storybook.IStoryDataStrategy.eSTORY_DATA_SORT_TYPE;
import com.snaps.common.storybook.StoryData;
import com.snaps.common.storybook.StoryData.ImageInfo;
import com.snaps.common.storybook.StoryData.StoryCommentData;
import com.snaps.common.storybook.StoryData.StoryLikeData;
import com.snaps.common.storybook.StoryDataType;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.SnapsTemplateInfo;
import com.snaps.common.structure.control.SnapsBgControl;
import com.snaps.common.structure.control.SnapsClipartControl;
import com.snaps.common.structure.control.SnapsControl;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Const_KAKAKAO;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.StoryBookStringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.activity.book.SNSBookRecorder.SNSBookInfo;
import com.snaps.mobile.activity.book.StoryBookChapterRuler.StoryChapterInfo;
import com.snaps.mobile.utils.ui.ArrayListUtil;
import com.snaps.mobile.utils.ui.CalcViewRectUtil;
import com.snaps.mobile.utils.ui.MathUtils;
import com.snaps.mobile.utils.ui.MultiLineTextData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import errorhandle.logger.Logg;

public class StoryBookMakerStyleNew extends StoryBookDataManager {
	private static final String TAG = StoryBookMakerStyleNew.class.getSimpleName();
	final int DATE_CONTROL_HEIGHT = 24;
	final int MAX_INNER_PAGE = 197;//17;
	private final int STICK_FIX_MARGIN_VALUE = 8;
	String defaultProfileImageUrl = SnapsAPI.DOMAIN(false) + DEFAULT_PROFILE_IMG_PATH;
	boolean isMaxPage = false;

	/***
	 * 챕터 정보를 저장하는 함수
	 */
	ArrayList<StoryBookChapter> chapters = new ArrayList<StoryBookChapter>();

	/**
	 * 템플릿 정보를 저장하는 함수.
	 */
	SnapsTemplateInfo templateInfo = null;

	public StoryBookMakerStyleNew(Context con, StoryDataType type) {
		super(type);
		context = con;
		isStoryBackground = true;
	}

	@Override
	public SnapsTemplate setCoverTemplate(SnapsTemplate template) {
		SnapsPage coverPage = getPage(template, StoryPageType.COVER_PAGE);
		setDataControls(coverPage, null);
		
		//커버 이미지는 편집이 가능하다.
		setCoverEditable(coverPage);
		
		// 책등에 타이틀 넣기. 소프트 커버인 경우 다르게 처리를 해야 한다.
		template.setSNSBookStick(setCoverSpineText(coverPage));

		// auraorderText 설정..
		coverPage.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);

		return template;
	}
	
	private void setCoverEditable(SnapsPage page) {
		// 이미지..
		for (SnapsControl c : page.getLayerLayouts()) {
			if (c instanceof SnapsLayoutControl) {
				SnapsLayoutControl control = (SnapsLayoutControl) c;
				control.isSnsBookCover = true;
			}
		}
	}

	@Override
	public SnapsTemplate setTitleTemplate(SnapsTemplate template) {
		SnapsPage titlePage = getPage(template, StoryPageType.TITLE_PAGE);
		setDataControls(titlePage, null);
		setTotalBestImageData(titlePage);

		// auraorderText 설정..
		titlePage.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);

		SnapsPage _totalPage = getPage(template, StoryPageType.TOTAL_PAGE);
		setDataControls(_totalPage, null);

		setTotalBestImageData(_totalPage);

		setChapterPageAttr(_totalPage);

		// auraorderText 설정..
		_totalPage.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);

		ArrayList<SnapsPage> makedPages = new ArrayList<SnapsPage>();
		makedPages.add(getPage(template, StoryPageType.COVER_PAGE)); // 커버
		makedPages.add(getPage(template, StoryPageType.INDEX_PAGE)); // 인덱스
		makedPages.add(getPage(template, StoryPageType.TITLE_PAGE)); // 토탈
		makedPages.add(_totalPage); // total

		if (createdPageList != null && createdPageList.size() > 0)
			makedPages.addAll(createdPageList);
		makedPages.add(getPage(template, StoryPageType.FRIENDS_PAGE));

		template.getPages().clear();
		template.getPages().addAll(makedPages);
		createdPageList.clear();

		// 전체 페이지 수를 넘기는 함수.
		totalPage = template.getPages().size();

		return template;
	}

	@Override
	public SnapsTemplate setIndexTemplate(SnapsTemplate template) {
		SnapsPage indexPage = getPage(template, StoryPageType.INDEX_PAGE);
		setDataControls(indexPage, null);
		StoryBookIndexMaker maker = new StoryBookIndexMaker(indexPage, chapters, dataManager);
		maker.makeIndex();

		// auraorderText 설정..
		indexPage.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);

		return template;
	}

	@Override
	public SnapsTemplate setPageTemplate(SnapsTemplate template) {

		templateInfo = template.info;

		// 친구리스트 페이지 설정..
		setDataFriendPage(template.getPages());

		// 내지 만들기
		makePage(template);
		return template;
	}

	@Override
	int makeDatePart(StoryData data, StoryChapterInfo chapterInfo, boolean isSizeCheck) {
		// topbar
		// 아이콘

		// 사용안함
		// 삭제 예정.
		return 0;
	}

	@Override
	public SNSBookInfo getInfo() throws Exception {
		SNSBookInfo info = new SNSBookInfo();

		ImageInfo imgInfo = dataManager.getUserImageUrl();
		if (imgInfo != null)
			info.setThumbUrl(imgInfo.original);

		info.setUserName(dataManager.getUserName());

		String period = getStoryPeriod("YYYY.MM.DD - YYYY.MM.DD");
		info.setPeriod(period);

		info.setPageCount(totalPage + "");
		info.setMaxPageEdited( isMaxPage );

		return info;
	}

	@Override
	void makeChapterControl(StoryChapterInfo chapterInfo, eStoryBookChapter kind) {
		// TODO Auto-generated method stub

	}

	@Override
	void removeRightControls() {
		// TODO Auto-generated method stub

	}

	/**
	 * snsproperty 타임에 따라 텍스틑 값을 가져오는 함수.
	 * 
	 * @param snsproperty
	 * @param format
	 * @return
	 */
	String getTextData(String snsproperty, String format, StoryBookChapter chapter) {

		if (chapter != null) {
			if (snsproperty.equals("chapter")) {
				return String.format(Locale.getDefault(), "%02d", chapter.getChapterNumber() + 1);
			} else if (snsproperty.equals("startyear")) {
				return StoryBookStringUtil.covertKakaoDate(format, dataManager.getStory(chapter.getStartStoryIndex()).createdAt, null);
			} else if (snsproperty.equals("startmonth")) {
				return StoryBookStringUtil.covertKakaoDate(format, dataManager.getStory(chapter.getStartStoryIndex()).createdAt, null);
			} else if (snsproperty.equals("endyear")) {
				return StoryBookStringUtil.covertKakaoDate(format, dataManager.getStory(chapter.getEndStoryIndex()).createdAt, null);
			} else if (snsproperty.equals("endmonth")) {
				return StoryBookStringUtil.covertKakaoDate(format, dataManager.getStory(chapter.getEndStoryIndex()).createdAt, null);
			} else if (snsproperty.equals("totalcomplexpost")) {
				int a = chapter.getStartStoryIndex();
				int complexCnt = 0;
				for (; a <= chapter.getEndStoryIndex(); a++) {
					StoryData d = dataManager.getStory(a);
					if (d.images.size() > 0 && d.content.length() > 0) {
						complexCnt++;
					}
				}

				return complexCnt + "";

			} else if (snsproperty.equals("totalwritingpost")) {
				int a = chapter.getStartStoryIndex();
				int writingCnt = 0;
				for (; a <= chapter.getEndStoryIndex(); a++) {
					StoryData d = dataManager.getStory(a);
					if ((d.images == null || d.images.size() == 0) && d.content.length() > 0) {
						writingCnt++;
					}
				}

				return writingCnt + "";

			} else if (snsproperty.equals("totalphotopost")) {
				int a = chapter.getStartStoryIndex();
				int writingCnt = 0;
				for (; a <= chapter.getEndStoryIndex(); a++) {
					StoryData d = dataManager.getStory(a);
					if (d.images.size() > 0 && d.content.length() < 1) {
						writingCnt++;
					}
				}

				return writingCnt + "";
			} else if (snsproperty.equals("totalwritingphotopost")) {
				int a = chapter.getStartStoryIndex();
				int writingCnt = 0;
				for (; a <= chapter.getEndStoryIndex(); a++) {
					StoryData d = dataManager.getStory(a);
					if (d.images.size() > 0 && d.content.length() > 0) {
						writingCnt++;
					}
				}

				return writingCnt + "";
			} else if (snsproperty.equals("totalpost")) {
				// 글 + 사진 + 글/사진
				int a = chapter.getStartStoryIndex();
				int writingCnt = 0;
				for (; a <= chapter.getEndStoryIndex(); a++) {
					StoryData d = dataManager.getStory(a);
					if ((d.images != null && d.images.size() > 0) || d.content.length() > 0) {
						writingCnt++;
					}
				}

				return writingCnt + "";
			} else if (snsproperty.equals("totalfeelingreplies")) {
				int a = chapter.getStartStoryIndex();
				int feelAndRelpyCnt = 0;
				for (; a <= chapter.getEndStoryIndex(); a++) {
					StoryData d = dataManager.getStory(a);
					feelAndRelpyCnt += d.likeCount;
					feelAndRelpyCnt += d.commentCount;
				}

				return feelAndRelpyCnt + "";
			} else if (snsproperty.equals("totalreplies")) {
				int a = chapter.getStartStoryIndex();
				int relpyCnt = 0;
				for (; a <= chapter.getEndStoryIndex(); a++) {
					StoryData d = dataManager.getStory(a);
					relpyCnt += d.commentCount;
				}
				return relpyCnt + "";
			} else if (snsproperty.equals("totalfeeling")) {
				int a = chapter.getStartStoryIndex();
				int feelCnt = 0;
				for (; a <= chapter.getEndStoryIndex(); a++) {
					StoryData d = dataManager.getStory(a);
					feelCnt += d.likeCount;
				}

				return feelCnt + "";

			} else if (snsproperty.equals("replies")) {
				int bestStoryIndex = chapter.getBestStoryIndex();
				if (bestStoryIndex == -1) {
					bestStoryIndex = getBestStoryIndex(chapter.getStartStoryIndex(), chapter.getEndStoryIndex());
					chapter.setBestStoryIndex(bestStoryIndex);
				}

				return dataManager.getStory(bestStoryIndex).commentCount + "";

			} else if (snsproperty.equals("feeling")) {
				int bestStoryIndex = chapter.getBestStoryIndex();
				if (bestStoryIndex == -1) {
					bestStoryIndex = getBestStoryIndex(chapter.getStartStoryIndex(), chapter.getEndStoryIndex());
					chapter.setBestStoryIndex(bestStoryIndex);
				}

				return dataManager.getStory(bestStoryIndex).likeCount + "";

			} else if (snsproperty.equals("text")) {
				int bestStoryIndex = chapter.getBestStoryIndex();
				if (bestStoryIndex == -1) {
					bestStoryIndex = getBestStoryIndex(chapter.getStartStoryIndex(), chapter.getEndStoryIndex());
					chapter.setBestStoryIndex(bestStoryIndex);
				}

				return dataManager.getStory(bestStoryIndex).content;
			} else if (snsproperty.equals("period")) {
				return StoryBookStringUtil.covertKakaoDate(format, dataManager.getStory(chapter.getStartStoryIndex()).createdAt, dataManager.getStory(chapter.getEndStoryIndex()).createdAt);
//				return StoryBookStringUtil.covertKakaoDate(format, dataManager.getStory(chapters.get(0).getStartStoryIndex()).createdAt, dataManager.getStory(chapters.get(chapters.size()-1).getEndStoryIndex()).createdAt);
			} else if (snsproperty.equals("postdate")) {
				return StoryBookStringUtil.covertKakaoDate(format, dataManager.getStory(chapter.getBestStoryIndex()).createdAt, null);
			} else if (snsproperty.equals("inner_month")) {
				// return StoryBookStringUtil.covertKakaoDate(format, rightUpDate, null);
			} else if (snsproperty.equals("totalphoto")) {
				int a = chapter.getStartStoryIndex();
				int photoCnt = 0;
				for (; a <= chapter.getEndStoryIndex(); a++) {
					StoryData d = dataManager.getStory(a);
					photoCnt += d.images.size();
				}
				return photoCnt + "";
			} else if (snsproperty.equals("photo")) {
				int bestStoryIndex = chapter.getBestStoryIndex();
				if (bestStoryIndex == -1) {
					bestStoryIndex = getBestStoryIndex(chapter.getStartStoryIndex(), chapter.getEndStoryIndex());
					chapter.setBestStoryIndex(bestStoryIndex);
				}

				ArrayList<ImageInfo> images = dataManager.getStory(bestStoryIndex).images;

				return images != null ? (images.size() + "") : "0";
			} else if (snsproperty != null && snsproperty.length() > 0) {
				Dlog.d("getTextData() snsproperty : " + snsproperty);
			}
		} else {
			if (snsproperty.equals("title")) {
				return getProjectTitle();
			} else if(snsproperty.equals("period")) {
				return StoryBookStringUtil.covertKakaoDate(format, dataManager.getStory(chapters.get(0).getStartStoryIndex()).createdAt, dataManager.getStory(chapters.get(chapters.size()-1).getEndStoryIndex()).createdAt);
			} else {
				return dataManager.getTextData(snsproperty, format);
			}
		}

		return null;
	}

	String getStoryPeriod(String format) throws Exception {
		StoryBookChapter cS = chapters.get(0);
		StoryBookChapter cE = chapters.get(chapters.size()-1);
		int start = cS.getStartStoryIndex();
		int end = cE.getEndStoryIndex();

		String startDate = dataManager.getStory(start).createdAt;
		String endDate = dataManager.getStory(end).createdAt;

		return StoryBookStringUtil.covertKakaoDate(format, startDate, endDate);
	}

	/***
	 * snsproperty 타임에 따라 텍스틑 값을 가져오는 함수.
	 * 
	 * @param data
	 * @param snsproperty
	 * @param format
	 * @return
	 */
	String getTextData(StoryData data, String snsproperty, String format) {
		if (snsproperty.equals("chapter")) {

		} else if (snsproperty.equals("startyear")) {

		} else if (snsproperty.equals("startmonth")) {

		} else if (snsproperty.equals("endyear")) {

		} else if (snsproperty.equals("endmonth")) {

		} else if (snsproperty.equals("text")) {

		}

		else if (snsproperty.equals("story_date")) {
			return StoryBookStringUtil.covertKakaoDateBySnsProperty(snsproperty, data.createdAt, null);
		} else if (snsproperty.equals("story_time")) {
			return StoryBookStringUtil.covertKakaoDateBySnsProperty(snsproperty, data.createdAt, null);
		}

		return "";
	}

	ImageInfo getImageData(String snsProperty, StoryBookChapter chapter) {
		if (chapter == null) {
			return dataManager.getImageData(snsProperty);
		} else {
			if (snsProperty.equals("best")) {
				int bestStoryIndex = chapter.getBestStoryIndex();
				if (bestStoryIndex == -1) {
					bestStoryIndex = getBestStoryIndex(chapter.getStartStoryIndex(), chapter.getEndStoryIndex());
					chapter.setBestStoryIndex(bestStoryIndex);
				}

				StoryData bestStory = dataManager.getStory(bestStoryIndex);
				if (bestStory != null && bestStory.images != null && !bestStory.images.isEmpty())
					return bestStory.images.get(0);
			}

			return null;
		}
	}

	/***
	 * 페이지에 데이터를 채우는 함수.
	 * 
	 * @param page
	 */
	void setDataControls(SnapsPage page, StoryBookChapter chapter) {
		// 이미지..
		for (SnapsControl c : page.getLayerLayouts()) {
			if (c instanceof SnapsLayoutControl) {
				SnapsLayoutControl control = (SnapsLayoutControl) c;
			
				ImageInfo imgInfo = getImageData(control.getSnsproperty(), chapter);
				if (imgInfo != null) {
					MyPhotoSelectImageData myPhotoImageData = new MyPhotoSelectImageData();
					myPhotoImageData.KIND = Const_VALUES.SELECT_KAKAO;
					myPhotoImageData.PATH = imgInfo.original;
					myPhotoImageData.THUMBNAIL_PATH = imgInfo.medium;
					// 원본 이미지 w, h 추가
					myPhotoImageData.F_IMG_WIDTH = imgInfo.getOriginWidth();
					myPhotoImageData.F_IMG_HEIGHT = imgInfo.getOriginHeight();
					myPhotoImageData.cropRatio = control.getRatio();

					control.imgData = myPhotoImageData;
					control.imagePath = myPhotoImageData.PATH;
					control.thumPath = myPhotoImageData.THUMBNAIL_PATH;
					control.imageLoadType = myPhotoImageData.KIND;
					control.angle = "0";
				}
			}
		}

		// 텍스트..
		for (SnapsControl t : page.getLayerControls()) {
			if (t instanceof SnapsTextControl) {
				String data = null;
				if (t.getSnsproperty().length() != 0)
					data = getTextData(t.getSnsproperty(), t.getFormat(), chapter);

				if (data != null && !data.equals("")) {
					((SnapsTextControl) t).text = data;
				} else if (!((SnapsTextControl) t).emptyText.equals(""))
					((SnapsTextControl) t).text = ((SnapsTextControl) t).emptyText;
			}
		}

		// 스틱 형태 처리(releative)
		for (SnapsControl t : page.getLayerControls()) {

			if (t instanceof SnapsClipartControl || t instanceof SnapsTextControl) {
				String coordX = getCoordinateByStick(page, t);
				if (coordX != null) {
					t.x = coordX;
				}
			}
		}
	}

	int getCoordOffsetControl(SnapsControl offsetControl, SnapsControl control, boolean isLeft) throws NumberFormatException {
		if (offsetControl == null)
			return 0;

		int offset = Integer.parseInt(control.x);

		float coordControlRight = Float.parseFloat(offsetControl.x) + Float.parseFloat(offsetControl.width);
		float offsetControlSize = 0.f;

		if (isLeft) {
			if (offsetControl instanceof SnapsTextControl) {
				SnapsTextControl textControl = (SnapsTextControl) offsetControl;
				offsetControlSize = UIUtil.convertPixelsToSp(context, Float.parseFloat(textControl.format.fontSize)); // 3정도
																														// 보정을 해
																														// 줘야
																														// 맞음..
				int fontTotalSize = (int) (offsetControlSize * textControl.text.length());
				offset = (int) (coordControlRight - fontTotalSize);
			} else {
				offset = (int) (Float.parseFloat(offsetControl.x));
			}

			offset -= Float.parseFloat(control.width);
		} else {
			if (offsetControl instanceof SnapsTextControl) {
				SnapsTextControl textControl = (SnapsTextControl) offsetControl;
				offsetControlSize = UIUtil.convertPixelsToSp(context, Float.parseFloat(textControl.format.fontSize)); // 3정도
																														// 보정을 해
																														// 줘야
																														// 맞음..
				int fontTotalSize = (int) (offsetControlSize * textControl.text.length());
				offset = (int) (Float.parseFloat(offsetControl.x) + fontTotalSize);
			} else {
				offset = (int) coordControlRight;
			}
		}

		return offset;
	}

	String getCoordinateByStick(SnapsPage page, SnapsControl control) {
		// offsetControl 기준 컨트롤
		// control 따라 다니는 컨트롤..

		if (control == null)
			return null;

		String targetName = control.stick_target;
		String direction = control.stick_dirction;
		String margin = control.stick_margin;

		if (targetName == null || direction == null || margin == null || targetName.length() < 1 || direction.length() < 1 || margin.length() < 1)
			return null;

		// 템플릿별 예외처리..
		if (targetName.contains("11-p4"))
			return null;

		SnapsControl offsetControl = findStickOffsetTargetControl(page, targetName);

		if (offsetControl == null)
			return null;

		// 강제 보정치.
		int adjust = 0;

		if (targetName.contains("12-p4")) {
			// ordinary 디자인 챕터에 텍스트 겹침 해소용으로 추가.
			// stories
			if (targetName.equals("12-p4-3") || targetName.equals("12-p4-6")) {
				control.x = control.getIntX() - (offsetControl.getIntWidth() / 5) * (4 - ((SnapsTextControl) offsetControl).text.length()) + "";
				return null;
			} else if (control.id.equals("12-p4-3") || control.id.equals("12-p4-6") || control.id.equals("12-p4-5"))
				return null;

			else if (targetName.equals("12-p4-2") || targetName.equals("12-p4-1")) {
				// offset의 길이에 따라.. 조절을 해야 함..
				int length = ((SnapsTextControl) offsetControl).text.length();
				adjust = (7 - length) * -5;
			}

		}

		// 2번째 디자인..
		else if (targetName.contains("10-p4-1")) {
			offsetControl.width = offsetControl.getIntWidth() + control.getIntWidth() + "";
			((SnapsTextControl) offsetControl).text = ((SnapsTextControl) offsetControl).text + "  " + ((SnapsTextControl) control).text;
			((SnapsTextControl) control).text = "";
			return null;
		}

		int coord = 0;
		boolean isLeftOfOffset = direction.trim().equalsIgnoreCase("left");
		try {
			coord = getCoordOffsetControl(offsetControl, control, isLeftOfOffset);
			if (isLeftOfOffset) {
				coord -= UIUtil.convertDPtoPX(context, (int) Float.parseFloat(margin) + STICK_FIX_MARGIN_VALUE);
			} else {
				coord += UIUtil.convertDPtoPX(context, (int) Float.parseFloat(margin) + STICK_FIX_MARGIN_VALUE);
			}
		} catch (NumberFormatException e) {
			Dlog.e(TAG, e);
		}

		return String.valueOf(coord + adjust);
	}

	SnapsControl findStickOffsetTargetControl(SnapsPage page, String name) {
		if (page == null || name == null)
			return null;
		for (SnapsControl t : page.getLayerControls()) {
			if (t != null && t.id != null && t.id.trim().equals(name))
				return t;
		}
		return null;
	}

	/***
	 * 책등 텍스트를 설정하는 함수..
	 * 
	 * @param coverPage
	 */
	SnapsTextControl setCoverSpineText(SnapsPage coverPage) {
		// 텍스트..
		for (SnapsControl t : coverPage.getTextControlList()) {
			SnapsTextControl tControl = (SnapsTextControl) t;
			if (tControl != null && tControl.format.verticalView.equals("true")) {
				String period = getTextData("period", "YYYY.MM.DD - YYYY.MM.DD", null);
				tControl.text = period + " " + getProjectTitle();
				tControl.format.auraOrderFontSize = "14";
				return tControl;
			}
		}

		return null;
	}

	/***
	 * 전체 Best 이미지 데이터를 넣는 함수.
	 * 
	 * @param page
	 */
	void setTotalBestImageData(SnapsPage page) {

		if (!page.getSnsproperty().equals("total"))
			return;

		// best 이미지 데이터 구하기
		ArrayList<StoryData> bestStoryData = dataManager.getSortedStories(eSTORY_DATA_SORT_TYPE.POPULAR);
		ArrayList<ImageInfo> bestImage = new ArrayList<ImageInfo>();
		for (StoryData d : bestStoryData) {
			for (ImageInfo i : d.images)
				bestImage.add(i);

		}

		// 데이터 설정.
		ArrayList<SnapsControl> bestLayouts = page.getLayoutListByProperty("best");
		int index = 0;
		for (SnapsControl c : bestLayouts) {
			SnapsLayoutControl control = (SnapsLayoutControl) c;
			ImageInfo imgInfo = null;

			try {
				imgInfo = bestImage.get(index);
			} catch (Exception e) {
				continue;
			}

			MyPhotoSelectImageData myPhotoImageData = new MyPhotoSelectImageData();
			myPhotoImageData.KIND = Const_VALUES.SELECT_KAKAO;
			myPhotoImageData.PATH = imgInfo.original;
			myPhotoImageData.THUMBNAIL_PATH = imgInfo.medium;
			// 원본 이미지 w, h 추가
			myPhotoImageData.F_IMG_WIDTH = imgInfo.getOriginWidth();
			myPhotoImageData.F_IMG_HEIGHT = imgInfo.getOriginHeight();
			myPhotoImageData.cropRatio = control.getRatio();

			control.imgData = myPhotoImageData;
			control.imagePath = myPhotoImageData.PATH;
			control.thumPath = myPhotoImageData.THUMBNAIL_PATH;
			control.imageLoadType = myPhotoImageData.KIND;
			control.angle = "0";

			index++;

		}
	}

	/***
	 * 친구 리스트 페이지 데이터를 설정하는 함수..
	 * 
	 * @param pages
	 */
	void setDataFriendPage(ArrayList<SnapsPage> pages) {
		for (SnapsPage page : pages) {
			if (page.getSnsproperty().equals("friend")) {
				setFriendListPage(page);
			}
		}

	}

	void setFriendListPage(SnapsPage friendPage) {
		ArrayList<String> friendList = dataManager.getFrientImageList();
		int index = 0;

		ArrayList<SnapsLayoutControl> deleteList = new ArrayList<SnapsLayoutControl>();

		Collections.reverse(friendPage.getLayoutList());
		for (SnapsControl c : friendPage.getLayoutList()) {
			SnapsLayoutControl control = (SnapsLayoutControl) c;
			if (control.getSnsproperty().equals("friend")) {
				String friendImageUrl = null;
				try {
					friendImageUrl = friendList.get(index);
					index++;
				} catch (Exception e) {
					continue;
				}

				MyPhotoSelectImageData imgData = new MyPhotoSelectImageData();
				imgData.KIND = Const_VALUES.SELECT_KAKAO;
				imgData.PATH = friendImageUrl;
				imgData.THUMBNAIL_PATH = friendImageUrl;
				control.imgData = imgData;
				control.angle = "0";
				control.imagePath = imgData.PATH;
				control.imageLoadType = imgData.KIND;
			}
		}

		// url이 null인경우 제외 시킨다.
		for (SnapsControl c : friendPage.getLayoutList()) {
			SnapsLayoutControl control = (SnapsLayoutControl) c;
			if (control.imagePath == null || control.imagePath.equals("")) {
				deleteList.add(control);
			}
		}

		if (deleteList.size() > 0) {
			friendPage.getLayoutList().removeAll(deleteList);
		}
	}

	/***
	 * 페이지 타입에 따라 페이지를 가져오는 함수..
	 * 
	 * @param template
	 * @param type
	 * @return
	 */
	SnapsPage getPage(SnapsTemplate template, StoryPageType type) {
		return template.getPages().get(type.getIndex());
	}

	SnapsClipartControl getClipart(SnapsPage page, String snsproperty) {

		for (SnapsControl c : page.getClipartControlList()) {
			SnapsClipartControl cc = (SnapsClipartControl) c;
			if (cc.getSnsproperty().equals(snsproperty))
				return cc;
		}

		return null;
	}

	SnapsTextControl getTextControl(SnapsPage page, String snsproperty) {

		for (SnapsControl c : page.getTextControlList()) {
			SnapsTextControl cc = (SnapsTextControl) c;
			if (cc.getSnsproperty().equals(snsproperty))
				return cc;
		}

		return null;
	}

	SnapsClipartControl chapterSticker = null;
	SnapsClipartControl topBar = null;
	SnapsClipartControl camera = null;
	SnapsClipartControl text = null;

	SnapsTextControl dateText = null;
	SnapsTextControl timeText = null;

	SnapsPage themaPage = null;
	SnapsPage page = null;
	SnapsPage innerPage = null;
	SnapsPage lastPage = null;

	// 우측 상단에 들어가 날짜 저
	String rightUpDate = null;

	// 챕터는 무조건 1번만 생성을 한다.
	boolean isChapter = true;

	/***
	 * 내지 페이지구성.
	 * 
	 * @param template
	 */
	void makePage(SnapsTemplate template) {
		/***
		 * 내지 페이지 구성하기.. 테마페이지 임시 작성 챕터페이지 챕터 임시 작성 페이지 상단부 구성 페이지 하단부 구성.. 테마페이지 작성 챕터페이지 작성
		 */

		themaPage = getPage(template, StoryPageType.THEMA_PAGE);
		page = getPage(template, StoryPageType.PAGE_PAGE);
		innerPage = getPage(template, StoryPageType.INNER_PAGE);
		lastPage = getPage(template, StoryPageType.LAST_PAGE);

		chapterSticker = (SnapsClipartControl) page.getClipartControlList().get(0);
		topBar = getClipart(page, "sticker_topbar");
		camera = getClipart(page, "sticker_camera");
		text = getClipart(page, "sticker_text");

		dateText = getTextControl(page, "story_date");
		timeText = getTextControl(page, "story_time");

		// 작은 챕터 페이지 만들기.

		int total = dataManager.getStoryCount();
		int startStory = 0;

 		initPage();
		while (true && total > 0) {
			startStory = makeChapterPage(template, startStory);
			if (onPageMakeListener != null) {
				onPageMakeListener.update(50 + (50 * (startStory / (float) total)));
			}

			boolean isContinue = initPage();
			if (!isContinue || total <= startStory) {
				if (onPageMakeListener != null) {
					onPageMakeListener.update(100);
				}
				break;
			}
		}
	}

	/***
	 * 페이지 만들기 전에 초기화 하는 함수.
	 * 
	 * @return
	 */
	boolean initPage() {

		boolean isResult = true;
		// 만들어진 페이지 추가 및 마지막 페이지 배경 수정.
		if (tempPageList != null && tempPageList.size() > 0) {

			// 마지막 1페이지가 비는 경우 리소스 추가,
			if (!isChapter) {
				SnapsPage p = tempPageList.get(tempPageList.size() - 1);
				if (!p.isExistControls()) {
					p.getBgList().clear();
					p.addBg((SnapsBgControl) lastPage.getBgList().get(0));
					p.removeText("inner_month");
					p.removeText("inner_month");
				}
			}

			// 맥스 페이지 적용
			// 맥스 페이지가 넘어가면 마지막 챕터를 제외 시킨다.
			if (createdPageList.size() + tempPageList.size()  + 1<= MAX_INNER_PAGE) {
				createdPageList.addAll(tempPageList);
			} else
				isResult = false;

			tempPageList.clear();
		}

		tempPageList = new ArrayList<SnapsPage>();

		workPage = null;
		isChapter = true;
		cPage = 0;
		cSide = 0;

		return isResult;
	}

	/***
	 * 한 챕터를 구성하는 함수
	 * 
	 * @param template
	 * @param startStory
	 * @return
	 */

	int makeChapterPage(SnapsTemplate template, int startStory) {
		StoryBookChapter chapter = new StoryBookChapter();
		chapter.setChapterNumber(chapters.size());
		chapter.setStartStoryIndex(startStory);
		chapter.setChapterStartIndex(createdPageList.size());

		// 시작월
		String startMonth = null;
		ArrayList<HashMap<String, Integer>> pageInfo = new ArrayList<HashMap<String, Integer>>();
		boolean isMonths = false;
		// 마지막 스토리 제외.
		int pageCnt = 0;

		int i = startStory;
		for (; dataManager.getStoryCount() > i; i++) {
			// 포스팅이 시작하는 페이지
			dataManager.getStory(i).startPage = cPage;

			// 월이 바뀌는지 체크.
			String storyMonth = StoryBookStringUtil.getMonthByKakaoDate(dataManager.getStory(i).createdAt);
			dataManager.getStory(i).storyDate = storyMonth;
			// 월이 바뀐경우
			if (startMonth != null && !startMonth.equals(storyMonth)) {

				// 복수개월이 포함이 되었는지 체크.
				isMonths = pageInfo.size() > 0;
				pageCnt = cPage * 2;

				// 한달
				if (!isMonths) {
					if (pageCnt >= 20) {
						break;
					}
				}// 두달이상.
				else {
					// 한챕터가 21p ~ 31p가 경우 챕터 생성.
					if (pageCnt >= 20) {
						break;
					}
				}

				HashMap<String, Integer> pInfo = new HashMap<String, Integer>();
				// 월별로 페이지 정보를 저장을 한다.
				// 1월 20p 이런씩으로
				pInfo.put(storyMonth, cPage);
				pageInfo.add(pInfo);
				startMonth = storyMonth;

			} else {

				startMonth = storyMonth;
			}

			// 우측 상단에 들어가 날짜 저장.
			rightUpDate = dataManager.getStory(i).createdAt;
			
			StoryData data = dataManager.getStory(i);
			if(cHeight > (ED_HEIGHT - BOTTOM_MARGIN) / 3)
			{
				int storyHeight = DATE_CONTROL_HEIGHT;
				storyHeight += makeNotePart(workPage, data, true);
				storyHeight += makeImagePart(data, true);
				//날짜와 텍스트 3줄이 들어가지 않으면 무조건 다음페이지로 이동.
				if (getRestHeightAtSide() < storyHeight /*&& storyHeight <= ENABLE_MAX_HEIGHT*/ ) {
					checkInsidePage(0, true);
				}
				
				makeDatePart(workPage, data, isChapter);
				makeNotePart(workPage, data, false);
			} else {
				
				boolean isExistTextContents = data != null && data.content != null && data.content.trim().length() > 0;
				int storyHeight = 0;

				//텍스트가 존재 하는 경우
				if(isExistTextContents) {
					storyHeight = DATE_CONTROL_HEIGHT;
					storyHeight += makeNotePart(workPage, data, true);
					
					if (storyHeight > getRestHeightAtSide()) {
						checkInsidePage(0, true);
					}
					
					// 날짜부분 만들기
					makeDatePart(workPage, data, isChapter);
					// 텍스트 만들기
					makeNotePart(workPage, data, false);
					
					storyHeight = makeImagePart(data, true);
					if (storyHeight > getRestHeightAtSide()) {
						checkInsidePage(0, true);
					}
				} else {
					storyHeight = DATE_CONTROL_HEIGHT;
					storyHeight += makeImagePart(data, true);
					if (storyHeight > getRestHeightAtSide()) {
						checkInsidePage(0, true);
					}
					
					// 날짜부분 만들기
					makeDatePart(workPage, data, isChapter);
				}
			}

			// 이미지 넣기
			makeImagePart(data, false);
			
			// 느낌
			makeResponsePart(data);

			// 댓글 만들
			makeCommentPart(data);

			// 포스트가 끝나는 페이
			dataManager.getStory(i).endPage = cPage;
			dataManager.getStory(i).cSide = cSide;

			cHeight += 20;
		}

		pageCnt = cPage * 2;
		// 한달
		if (!isMonths) {
			// 한달치 챕터가 40p 이상인 경우.
			if (pageCnt > 40) {
				// 챕터 영역 구하기
				int pPage = pageCnt;
				// 절반이 나누어질 페이지 구하기.
				pPage = MathUtils.getHalfNumber(pPage, 40);

				// pPage까지 스토리 인덱스를 가져온다.
				int idx = chapter.getStartStoryIndex();
				int enableChapterPage = 0;
				String baseDate = "";
				for (; dataManager.getStoryCount() > idx; idx++) {
					StoryData d = getStory(idx);
					if (d.endPage * 2 > pPage)
						break;

					enableChapterPage = d.endPage;
					baseDate = d.createdAt;
				}

				// 다음 챕터 만들기에서 사용한 스토리 인텍스.
				i = idx;

				// 페이지 제거
				if (tempPageList != null) {
					tempPageList = ArrayListUtil.removeArrayList(tempPageList, enableChapterPage);
				}

				// 마지막페이지 컨트롤 제거하기
				// 다른챕터 컨트롤 삭제..
				tempPageList.get(tempPageList.size() - 1).removeControls(baseDate);
			}

		}// 두달이상..
		else {
			if (pageCnt > 31) {
				// 복수월인경우 페이지가 31p가 넘어가면 현재달은 빼고 챕터를 만든다.
				// 이전월까지 한챕터로 만든다.

				// 이전월 스토리 아이디를 구한다.
				int enableChapterPage = 0;
				String baseDate = "";
				int a = i - 1;
				for (; a >= 0; a--) {
					StoryData d = getStory(a);
					if (!d.storyDate.equals(startMonth)) {
						enableChapterPage = d.endPage;
						baseDate = d.createdAt;
						cSide = d.cSide;
						i = a;
						break;
					}
				}

				// 다음 챕터 만들기에서 사용한 스토리 인텍스.
				i = a + 1;

				// 페이지 제거
				if (tempPageList != null) {
					tempPageList = ArrayListUtil.removeArrayList(tempPageList, enableChapterPage);
				}

				// 마지막페이지 컨트롤 제거하기
				// 다른챕터 컨트롤 삭제..
				tempPageList.get(tempPageList.size() - 1).removeControls(baseDate);

			}
		}

		// 페이지 추가 및 챕터 정보 추가.
		chapter.setEndStoryIndex(i - 1);

		// 맥스페이지 적용..
		if (createdPageList.size() + tempPageList.size() + 1 <= MAX_INNER_PAGE) {

			chapters.add(chapter);

			// 페이지 구성전에 테마페이지 추가
			SnapsPage theme = getPage(template, StoryPageType.THEMA_PAGE);
			theme = theme.copyPage(0);
			setDataControls(theme, chapter);

			// 텍스트 만들기
			setChapterPageAttr(theme);

			// auraorderText 설정..
			theme.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);
			createdPageList.add(theme);

			// 챕터페이지 설정..
			SnapsPage chapterPage = tempPageList.get(0);
			setDataControls(chapterPage, chapter);

		} else {
			// 최대페이지가 넘은경우
			isMaxPage = true;
		}


		pageInfo.clear();
		pageInfo = null;

		int nextStoryIndex = chapter.getEndStoryIndex() + 1;

		return nextStoryIndex;
	}

	void setChapterPageAttr(SnapsPage page) {
		if (page == null)
			return;

		// 라인텍스트 정보를 가져온다.
		int limitSize = 0;
		float fontRatio = 0.f;
		limitSize = POSTING_WIDTH - 30;
		fontRatio = 1.0f;

		for (SnapsControl t : page.getLayerControls()) {
			if (t instanceof SnapsTextControl && t.getSnsproperty().equalsIgnoreCase("text")) {
				SnapsTextControl note = ((SnapsTextControl) t);
				note.format.fontSize = "10";
				note.format.auraOrderFontSize = note.format.fontSize;
				note.format.align = "left";
				note.format.bold = "false";
				note.format.italic = "false";
				note.format.underline = "false";
				note.format.fontFace = "스냅스 윤고딕 700";// "스냅스 윤고딕 230";
				note.format.alterFontFace = "스냅스 윤고딕 700";// "스냅스 윤고딕 230";
				note.lineSpcing = Const_KAKAKAO.LINE_SPACING;
				note._controlType = SnapsControl.CONTROLTYPE_TEXT;
				note.format.verticalView = "false";

				MultiLineTextData textData = CalcViewRectUtil.getTextControlRect(context, note.format.fontFace, note.format.fontSize, limitSize, note.text, fontRatio, FontUtil.TEXT_TYPE_CHAPTER);

				if (textData == null)
					return;

				if (note != null) {
					String sText = "";
					int curHeight = 0;
					int PADDING_BOTTOM = textData.getTextLineHeight(note, 3);
					int maxHeight = note.getIntHeight() - PADDING_BOTTOM;

					for (int i = 0; i < textData.getLineTexts().size(); i++) {
						if (!sText.equals(""))
							sText += "\n";

						String curLineText = textData.getLineTexts().get(i);

						curHeight = textData.getTextLineHeight(note, i + 1);

						if (curHeight > maxHeight) {
							if (t.getTextType().equals("1")) {

								if (curLineText.length() > 27) // 가로로 대충 28자 정도
																// 들어감 ...을 넣으면
																// 넘어가니까 잘라준다.
									curLineText = curLineText.substring(0, 27);

								curLineText += "...";
							}

							sText += curLineText;
							break;
						}

						sText += curLineText;
					}

					note.text = sText;
					note.format.fontSize = Float.parseFloat(note.format.fontSize) * NOTE_FONT_RATIO + "";
				}
				break;
			}
		}
	}

	/***
	 * 날짜부분 만들기..
	 * 
	 * @param page
	 * @param data
	 * @param isChapter
	 */
	void makeDatePart(SnapsPage page, StoryData data, boolean isChapter) {

		// 날짜영역이 들어가는지 체크
		checkInsidePage(DATE_CONTROL_HEIGHT, false);
		// 스토리가 들어가는 처음 페이지 설정.
		data.startPage = cPage;

		// top bar 만들기
		SnapsClipartControl topbar = makeStickerControl(topBar);
		topbar.x = cStart + "";
		topbar.y = cHeight + "";
		topbar.width = "137";

		// 아이콘 만들기
		SnapsClipartControl icon = null;
		int offset = 0;// 카메라 아이콘과 텍스트 아이콘이 다른다.. 젠장..
		if (data.images.size() > 0)
			icon = makeStickerControl(camera);
		else {
			icon = makeStickerControl(text);
			offset = 2;
		}

		icon.x = cStart + "";
		icon.y = cHeight + offset + "";

		// 날짜 구성
		String text = getTextData(data, dateText.getSnsproperty(), null);
		SnapsTextControl date = makeTextControl(dateText, text, false);
		date.format.auraOrderFontSize = Float.parseFloat(date.format.fontSize) * Const_PRODUCT.AURATEXT_RATION + "";
		date.format.fontSize = "6";

		date.x = icon.getIntX() + icon.getIntWidth() + 3 + "";
		date.y = cHeight + 7 + "";

		// 시간 구성
		String tTime = getTextData(data, timeText.getSnsproperty(), null);
		SnapsTextControl time = makeTextControl(timeText, tTime, false);
		time.format.auraOrderFontSize = Float.parseFloat(time.format.fontSize) * Const_PRODUCT.AURATEXT_RATION + "";
		time.x = date.getIntX() + date.getIntWidth() + "";
		time.y = cHeight + 8 + "";
		time.format.fontSize = "4";

		// 식별자 저장.
		topbar.identifier = data.createdAt;
		icon.identifier = data.createdAt;
		date.identifier = data.createdAt;
		time.identifier = data.createdAt;

		// 컨트롤 추가.
		workPage.addControl(topbar);
		workPage.addControl(icon);
		workPage.addControl(date);
		workPage.addControl(time);

		cHeight += (topbar.getIntHeight() + icon.getIntHeight() + 7);
	}

	/***
	 * 텍스트 부분 만들기
	 * 
	 * @param page
	 * @param data
	 */
	int makeNotePart(SnapsPage page, StoryData data, boolean isSizeCheck) {
		if (data.content == null || data.content.length() <= 0)
			return 0;

		// 라인텍스트 정보를 가져온다.
		int limitSize = 0;
		float fontRatio = 0.f;
		if (Build.VERSION.SDK_INT > 20) { // 롤리팝 이상 체크

			limitSize = POSTING_WIDTH - 30;
			fontRatio = 1.0f;// NOTE_FONT_RATIO;
		} else {
			limitSize = POSTING_WIDTH - 30;
			fontRatio = 1.0f;// NOTE_FONT_RATIO;
		}
		
		noteTextControl = makeNoteControl();
		MultiLineTextData textData = CalcViewRectUtil.getTextControlRect(context, noteTextControl.format.fontFace, noteTextControl.format.fontSize, limitSize, data.content, fontRatio,
				FontUtil.TEXT_TYPE_CONTENTS);

		if (textData == null)
			return 0;

		SnapsTextControl note = null;
		note = makeNoteControl();
		note.lineSpcing = Const_KAKAKAO.LINE_SPACING;
		String sText = "";

		if(isSizeCheck)
			return textData.getText3LineHeight(note);



		// 현재 남아있는 영역에 텍스트가 들어갈수 있는지 확인한다.
		// 없으면 다음쪽으로 넘긴다.
		int enableHeight = getRestHeightAtSide();
		if (textData.getTextHeight() > enableHeight) {
			checkInsidePage(0, true);
		}

		// 콘텐츠가 들어갈수 있는경우...
		if (enableHeight >= textData.getTextTotalHeight(note)) {
			for (int i = 0; i < textData.getLineTexts().size(); i++) {
				if (!sText.equals(""))
					sText += "\n";
				sText += textData.getLineTexts().get(i);
			}
			note = makeNoteControl();
			note.lineSpcing = Const_KAKAKAO.LINE_SPACING;
			note.text = sText;
			note.x = cStart + 5 + "";
			note.y = cHeight + "";
			note.height = textData.getTextTotalHeight(note) + "";
			note.format.fontSize = Float.parseFloat(note.format.fontSize) * NOTE_FONT_RATIO + "";

			CalcViewRectUtil.makeLineText(note, textData.getLineTexts(), textData.getTextHeight());
			// 식별자 저장.
			note.identifier = data.createdAt;
			workPage.addControl(note);
			cHeight += note.getIntHeight();
			cHeight += NOTE_DOWN_MARGIN;

		} else { // 가능영역이 작은경우...
			int startLine = 0;
			while (true) {
				enableHeight = getRestHeightAtSide();
				if (startLine >= textData.getLineTexts().size())
					break;

				note = makeNoteControl();
				note.lineSpcing = Const_KAKAKAO.LINE_SPACING;
				startLine = textData.getExtractTextByHeight(startLine, enableHeight, note);
				note.x = cStart + 5 + "";
				note.y = cHeight + "";
				note.format.fontSize = Float.parseFloat(note.format.fontSize) * NOTE_FONT_RATIO + "";

				note.identifier = data.createdAt;
				workPage.addControl(note);
				cHeight += note.getIntHeight();
				cHeight += NOTE_DOWN_MARGIN;

				// 남머지 텍스트가 있으면 쪽을 옮긴다.
				if (startLine < textData.getLineTexts().size())
					checkInsidePage(0, true);
			}
		}

		return 0;
	}

	int makeImagePart(StoryData data, boolean isSizeCheck) {
		if (data.images == null || data.images.size() <= 0)
			return 0;

		for (ImageInfo info : data.images) {

			// 이미지 데이터 생성..
			MyPhotoSelectImageData imgData = new MyPhotoSelectImageData();
			imgData.KIND = Const_VALUES.SELECT_KAKAO;
			imgData.FB_OBJECT_ID = data.id;
			imgData.PATH = info.original;
			imgData.THUMBNAIL_PATH = info.small;
			imgData.KAKAOBOOK_DATE = data.createdAt;
			// 원본 이미지 w, h 추가
			imgData.F_IMG_WIDTH = info.getOriginWidth();
			imgData.F_IMG_HEIGHT = info.getOriginHeight();

			// 영역체크..
			// 현재 최대이미지 높
			String maxHeight = "410";

			SnapsLayoutControl layoutControl = SnapsLayoutControl.makeImageLayoutControl();// makeImageLayoutControl();
			Rect rc = CalcViewRectUtil.getLayoutControlRect(layoutControl.width, maxHeight, imgData.F_IMG_WIDTH, imgData.F_IMG_HEIGHT);

			// 이미지 크기가 정해진 크기보다 1cm 미만으로 차이가 날때는 그냥 이미지를 중간 정렬을 해서 배치를 한다.

			int imgHeight = rc.height();

			if(isSizeCheck)
				return imgHeight;

			boolean isNewSide = checkInsidePage(imgHeight, false);

			layoutControl.imgData = imgData;
			layoutControl.angle = "0";
			layoutControl.imagePath = imgData.PATH;
			layoutControl.imageLoadType = imgData.KIND;
			layoutControl.y = cHeight + ""; // 새로운 열로 이동시 상단 마진을 없앤다.
			layoutControl.x = cStart + "";
			layoutControl.height = rc.height() + "";

			layoutControl.identifier = data.createdAt;
			workPage.addLayout(layoutControl);

			cHeight += rc.height();
			cHeight += 1;// calc.calcMM(1);

		}

		return 0;
	}

	void makeResponsePart(StoryData data) {
		final int PAGE_REPLY_HEIGHT = 16;

		// 영역체크..
		checkInsidePage(PAGE_REPLY_HEIGHT, false);

		// 느낌 스티커
		SnapsClipartControl feelSticker = SnapsClipartControl.setFeelSticker();
		feelSticker.x = cStart + 5 + "";// (cStart + calc.calcMM(5)) + "";
		feelSticker.y = (cHeight + PAGE_REPLY_HEIGHT / 2 - feelSticker.getIntHeight() / 2) + "";// (cHeight +
																								// (calc.calcMM(PAGE_REPLY_HEIGHT) -
																								// 0) / 2) + "";
		feelSticker.identifier = data.createdAt;
		workPage.addControl(feelSticker);

		// 느낌수 텍스트
		SnapsTextControl feelCountText = SnapsTextControl.getSnapsText(10, 5, 7, "스냅스 윤고딕 700");
		feelCountText = makeTextControl(feelCountText, data.likeCount + "", true, REPLY_FONT_RATIO, FontUtil.TEXT_TYPE_CONTENTS);
		feelCountText.format.auraOrderFontSize = "7";
		feelCountText.x = feelSticker.getIntX() + feelSticker.getIntWidth() + 1 + "";
		feelCountText.y = (cHeight + PAGE_REPLY_HEIGHT / 2 - feelCountText.getIntHeight() / 2) + "";
		feelCountText.text = data.likeCount + "";
		feelCountText.identifier = data.createdAt;
		workPage.addControl(feelCountText);

		// 댓글 갯수 스트커
		SnapsClipartControl commentSticker = SnapsClipartControl.setRelySticker();// makeStickerControl(ballonCilpart);
		commentSticker.x = feelCountText.getIntX() + feelCountText.getIntWidth() + 2 + "";
		commentSticker.y = (cHeight + PAGE_REPLY_HEIGHT / 2 - commentSticker.getIntHeight() / 2) + "";
		commentSticker.identifier = data.createdAt;
		workPage.addControl(commentSticker);

		// 댓글수 텍스트
		SnapsTextControl commentCountText = SnapsTextControl.getSnapsText(10, 5, 7, "스냅스 윤고딕 700");
		commentCountText = makeTextControl(commentCountText, data.commentCount + "", true, REPLY_FONT_RATIO, FontUtil.TEXT_TYPE_CONTENTS);
		commentCountText.format.auraOrderFontSize = "7";
		commentCountText.x = commentSticker.getIntX() + commentSticker.getIntWidth() + 1 + "";
		commentCountText.y = (cHeight + PAGE_REPLY_HEIGHT / 2 - commentCountText.getIntHeight() / 2) + "";
		commentCountText.identifier = data.createdAt;
		workPage.addControl(commentCountText);

		if (data.likes != null) {
			int imageGap = 3;
			int startPos = POSTING_WIDTH - 7; // cStart +
												// calc.calcMM(STORY_WIDTH_MM -
												// 10 - imageGap);
			if (data.likes.size() > 5)
				startPos = cStart + POSTING_WIDTH - 9 - 10 - 11 * 4 - 3 * 4;// 9
																			// -
																			// 10
																			// -
																			// 11
																			// *
																			// 5
																			// -
																			// 3
																			// *
																			// 4;
			else
				startPos = cStart + POSTING_WIDTH - 7 - 11 * data.likeCount - 3 * (data.likeCount - 1);

			// 느낌이 5개 초과하면 etc가 추가되어야 한다...
			if (data.likes.size() > 5) {
				SnapsClipartControl more = SnapsClipartControl.setEtcSticker();// makeStickerControl(etcCilpart);
				more.x = cStart + POSTING_WIDTH - 5 - more.getIntWidth() - 4 - 1 + "";// cStart + calc.calcMM(STORY_WIDTH_MM - 8);
				more.y = cHeight + PAGE_REPLY_HEIGHT / 2 - more.getIntHeight() / 2 + "";
				workPage.addControl(more);

				// 추가 갯수를 넣는다.
				SnapsTextControl etc = SnapsTextControl.getSnapsText(5, 3, 6, "스냅스 윤고딕 700");// etcCountTextControl.copyControl();
				etc.format.auraOrderFontSize = "6";
				etc.x = cStart + POSTING_WIDTH - 5 - 4 + "";// startPos +
															// more.getIntWidth()
															// + 1 + "";
				etc.y = cHeight + PAGE_REPLY_HEIGHT / 2 - etc.getIntHeight() / 2 + "";// (cHeight + (calc.calcMM(PAGE_REPLY_HEIGHT) -
																						// etc.getIntHeight()) / 2) + "";
				etc.text = data.likeCount - 5 + "";
				etc.identifier = data.createdAt;
				workPage.addControl(etc);

				startPos -= (more.getIntWidth() + imageGap / 2);
				startPos -= 10;
			}

			int thumbCount = 0;
			for (StoryLikeData like : data.likes) {
				if (thumbCount >= 5)
					break;
				SnapsLayoutControl profileImage = SnapsLayoutControl.getProfileImageLayoutControl();// makeLayoutCotrol(feelActor1ImageLayoutControl);
				// 이미지 데이터 생성..
				MyPhotoSelectImageData imgData = new MyPhotoSelectImageData();
				imgData.KIND = Const_VALUES.SELECT_KAKAO;
				imgData.FB_OBJECT_ID = data.id;

				try {
					String thumbNail = like.actor.profileThumbnailUrl;
					imgData.PATH = thumbNail.equals("") ? defaultProfileImageUrl : thumbNail;
					imgData.THUMBNAIL_PATH = imgData.PATH;
					profileImage.imgData = imgData;
					profileImage.angle = "0";
					profileImage.imagePath = imgData.PATH;
					profileImage.imageLoadType = imgData.KIND;

					profileImage.x = startPos + "";
					profileImage.y = cHeight + PAGE_REPLY_HEIGHT / 2 - profileImage.getIntHeight() / 2 + "";// (cHeight +
																											// (calc.calcMM(PAGE_REPLY_HEIGHT)
																											// -
																											// feelActor1ImageLayoutControl.getIntHeight())
																											// / 2) +
																											// "";

					profileImage.identifier = data.createdAt;
					workPage.addLayout(profileImage);
				} catch (Exception e) {
					// TODO: handle exception
				}

				// 프로필 이미지 위에 스티커를 넣어준다.
				SnapsClipartControl feelClipart = SnapsClipartControl.getStickerControl(like.emotion);
				feelClipart.width = "6";
				feelClipart.height = "6";
				feelClipart.x = (profileImage.getIntX() + profileImage.getIntWidth() - feelClipart.getIntWidth() / 2) + "";
				feelClipart.y = (profileImage.getIntY() + profileImage.getIntHeight() - feelClipart.getIntHeight()) + "";

				feelClipart.identifier = data.createdAt;
				workPage.addControl(feelClipart);

				// 프로필 이미지들간 margin
				startPos += (imageGap + profileImage.getIntWidth());
				thumbCount++;
			}
		}

		cHeight += PAGE_REPLY_HEIGHT;// calc.calcMM(PAGE_REPLY_HEIGHT + 2);

	}

	void makeCommentPart(StoryData data) {
		if (data.comments == null || data.comments.isEmpty())
			return;

		setStartStoryBackground(data.createdAt);

		for (StoryCommentData comment : data.comments) {
			// 프로필 사진
			MyPhotoSelectImageData imgData = new MyPhotoSelectImageData();
			imgData.KIND = Const_VALUES.SELECT_KAKAO;
			imgData.FB_OBJECT_ID = data.id;

			try {
				String thumbNail = comment.writer.profileThumbnailUrl;
				imgData.PATH = thumbNail.equals("") ? defaultProfileImageUrl : thumbNail;
				imgData.THUMBNAIL_PATH = imgData.PATH;
			} catch (Exception e) {
				// TODO: handle exception
			}
			SnapsLayoutControl profileImage = SnapsLayoutControl.getProfileImageLayoutControl();// makeLayoutCotrol(feelActor1ImageLayoutControl);
			profileImage.width = "12";
			profileImage.height = "12";
			profileImage.imgData = imgData;
			profileImage.angle = "0";
			profileImage.imagePath = imgData.PATH;
			profileImage.imageLoadType = imgData.KIND;

			profileImage.x = cStart + 4 + "";// (cStart +
												// calc.calcMM(START_MARGIN_MM))
												// + "";
			profileImage.y = cHeight + 4 + "";// cHeight +
												// calc.calcMM(REPLY_UP_MARGIN_MM)
												// + "";

			// 이름.
			SnapsTextControl displayName = SnapsTextControl.getSnapsText(35, 4, 4, "스냅스 윤고딕 700");// ;
			displayName = makeTextControl(displayName, comment.writer.displayName, true, 1.0f, FontUtil.TEXT_TYPE_COMMENT);
			displayName.format.auraOrderFontSize = "6";
			displayName.y = cHeight + 4 + "";// cHeight +
												// calc.calcMM(REPLY_UP_MARGIN_MM)
												// + "";
			displayName.x = profileImage.getIntX() + profileImage.getIntWidth() + 4 + "";// calc.calcMM(RELPY_START_MARGIN_MM) + "";
			displayName.height = "4";
			displayName.width = "15";

			// 댓글.
			SnapsTextControl commentText = SnapsTextControl.getSnapsText(111, 4, 4, "스냅스 윤고딕 700");//
			commentText = makeTextControl(commentText, comment.text, false, REPLY_FONT_RATIO, FontUtil.TEXT_TYPE_COMMENT);
			commentText.format.auraOrderFontSize = "6";
			commentText.x = displayName.x;
			commentText.y = displayName.getIntY() + displayName.getIntHeight() + 1 + "";// + calc.calcMM(1) + "";

			// 영역체크
			// 영역 재설정..
			if (checkInsidePage(commentText.getIntY() + commentText.getIntHeight() - cHeight, false)) {
				// setEndStoryBackground(cHeight + 4);
				setStartStoryBackground(data.createdAt);
			}

			profileImage.x = cStart + 4 + "";// (cStart +
												// calc.calcMM(START_MARGIN_MM))
												// + "";
			profileImage.y = cHeight + 4 + "";// cHeight +
												// calc.calcMM(REPLY_UP_MARGIN_MM)
												// + "";

			// 이름.
			displayName.y = profileImage.y;// cHeight +
											// calc.calcMM(REPLY_UP_MARGIN_MM) +
											// "";
			displayName.x = profileImage.getIntX() + profileImage.getIntWidth() + 4 + "";// calc.calcMM(RELPY_START_MARGIN_MM) + "";

			// 댓글.
			commentText.x = displayName.x;// profileImage.getIntX() +
											// profileImage.getIntWidth() +
											// calc.calcMM(START_MARGIN_MM) +
											// "";
			commentText.y = displayName.getIntY() + displayName.getIntHeight() + 1 + "";// + calc.calcMM(1) + "";

			profileImage.identifier = data.createdAt;
			displayName.identifier = data.createdAt;
			commentText.identifier = data.createdAt;

			workPage.addLayout(profileImage);
			workPage.addControl(displayName);
			workPage.addControl(commentText);

			// 다음에 올 컨트롤 Y위치 설정..
			cHeight = (profileImage.getIntY() + profileImage.getIntHeight() > commentText.getIntY() + commentText.getIntHeight()) ? profileImage.getIntY() + profileImage.getIntHeight() : commentText
					.getIntY() + commentText.getIntHeight();
		}

		setEndStoryBackground(cHeight + 4);
	}

	SnapsLayoutControl commentBG = null;
	String bgColor = "FFF7F8F8";

	void setStartStoryBackground(String identifier) {
		SnapsLayoutControl l = new SnapsLayoutControl();
		l.x = "" + cStart;
		l.y = cHeight + "";
		l.width = "137";
		l.type = "webitem";
		l.angle = "0";
		l.tempImageColor = "FFF7F8F8";
		l.bgColor = "FFF7F8F8";
		l.regName = "background";
		l.identifier = identifier;
		l.imagePath = "";
		commentBG = l;
		workPage.addLayout(commentBG);
	}

	void setEndStoryBackground(int height) {
		if (commentBG != null) {
			commentBG.height = (height - commentBG.getIntY()) + "";
		}

		commentBG = null;

	}

	/***
	 * 
	 * @return
	 */
	SnapsTextControl makeNoteControl() {
		SnapsTextControl textControl = new SnapsTextControl();
		textControl._controlType = SnapsControl.CONTROLTYPE_TEXT;
		textControl.format.verticalView = "false";
		textControl.width = "127";
		textControl.height = "0";

		textControl.format.fontFace = "스냅스 윤고딕 700";// "스냅스 윤고딕 230";
		textControl.format.alterFontFace = "스냅스 윤고딕 700";// "스냅스 윤고딕 230";
		textControl.format.fontSize = "6";
		textControl.format.auraOrderFontSize = "8";// textControl.format.fontSize;
		textControl.format.fontColor = "000000";// "37D3C2";
		textControl.format.baseFontColor = textControl.format.fontColor;// "37D3C2";
		textControl.format.align = "left";
		textControl.format.bold = "false";
		textControl.format.italic = "false";
		textControl.format.underline = "false";

		return textControl;
	}

	/***
	 * 현재 페이지에 내용이 들어갈수 있는지 체크하는 함수. 내용이 들어가지 못하면 새페이지를 구성을 한다.
	 * 
	 * @param addHeight
	 *            추가되는 페이지 크기
	 * @param isForce
	 *            강제로 페이지를 넘길지 여부
	 * @return
	 */
	boolean checkInsidePage(int addHeight, boolean isForce) {
		// cPage 페이지수
		// cSide 쪽수 (카스는 4쪽이다.)
		boolean isNewPage = false;

		if (workPage == null || (cHeight + addHeight) > (ED_HEIGHT - BOTTOM_MARGIN) || isForce) {
			isNewPage = true;
		}

		// 충분하지 않으면
		if (isNewPage) {
			// 스토리에 배경을 넣어준다.
			// 페이지가 넘어가는 경우...
			setEndStoryBackground(cHeight + 4);

			if (cSide >= 3 || workPage == null) {
				// 챕터가 있는 페이지가 아님 설정.
				if (workPage != null)
					isChapter = false;

				cStart = START_MARGIN;
				if (workPage == null)
					cHeight = TOP_MARGIN_WITHCATEGORY;
				else
					cHeight = TOP_MARGIN;

				// 스냅스 페이지를 만들다.
				workPage = makeSnapsPage(templateInfo, isChapter);

				setRightUpDate();

				cSide = 0;
				cPage++;
				tempPageList.add(workPage);

			} else {

				cSide++;

				if (cSide == 1) {// 2쪽
					cStart = START_MARGIN + POSTING_WIDTH + MIDDLE_MARGIN;
					if (isChapter)
						cHeight = TOP_MARGIN_WITHCATEGORY;
					else
						cHeight = TOP_MARGIN;

				} else if (cSide == 2) {// 3쪽
					cStart = START_MARGIN + POSTING_WIDTH * 2 + MIDDLE_MARGIN + SIDE_MARGIN * 2;
					cHeight = TOP_MARGIN;

				} else if (cSide == 3) {// 4쪽
					cStart = START_MARGIN + POSTING_WIDTH * 3 + MIDDLE_MARGIN * 2 + SIDE_MARGIN * 2;
					cHeight = TOP_MARGIN;
				}

			}
		}

		return isNewPage;
	}

	ArrayList<SnapsPage> tempPageList = null;
	ArrayList<SnapsPage> createdPageList = new ArrayList<SnapsPage>();

	SnapsPage makeSnapsPage(SnapsTemplateInfo info, boolean isChapter) {
		SnapsPage p = null;
		if (isChapter) {
			// 챕터 페이지를 복제한다.
			p = page.copyPage(0);
			// 챕터를 제외한 컨트롤 삭제..
			p.removeSticker("sticker_topbar");
			p.removeSticker("sticker_camera");
			p.removeSticker("sticker_text");

			p.removeText("story_date");
			p.removeText("story_time");
			p.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);
		} else {
			p = innerPage.copyPage(0);
			p.width = info.F_PAGE_PIXEL_WIDTH;
			p.height = info.F_PAGE_PIXEL_HEIGHT;
		}

		return p;
	}

	/**
	 * 
	 * @return
	 */
	SnapsLayoutControl makeImageLayout() {
		return null;
	}

	/***
	 * 이미지가 있는 스토리중에 베스트 스토리 인덱스를 구하는 함수.
	 * 
	 * @param sIndex
	 * @param endIndex
	 * @return
	 */
	int getBestStoryIndex(int sIndex, int endIndex) {
		int a = sIndex;
		int tempBestCnt = 0;
		int tempBestStoryIndex = -1;
		for (; a <= endIndex; a++) {
			StoryData d = dataManager.getStory(a);
			int temp = d.likeCount + d.commentCount;
			if (d.images.size() == 0)
				temp = 0;
			if (tempBestCnt < temp) {
				tempBestCnt = temp;
				tempBestStoryIndex = a;
			}
		}

		// 만약, 댓글도 없고 좋아요도 없으면 에러가 나기 때문에..중간에 있는 스토리를 베스트라 친다.ㅠㅠ
		if (tempBestStoryIndex < 0) {
			tempBestStoryIndex = Math.max(0, (sIndex + endIndex) / 2);
		}

		return tempBestStoryIndex;
	}

	/***
	 * 우측 상단 텍스트 설정하는 함수.
	 */
	void setRightUpDate() {
		if (workPage != null) {
			ArrayList<SnapsTextControl> controls = workPage.getTextControls("inner_month");
			for (SnapsTextControl control : controls) {
				if (control != null)
					try {
						control.text = StoryBookStringUtil.covertKakaoDate(control.getFormat(), rightUpDate, null);
						control.format.auraOrderFontSize = Float.parseFloat(control.format.fontSize) * Const_PRODUCT.AURATEXT_RATION + "";
					} catch (Exception e) {
						Dlog.e(TAG, e);
					}
			}
		}
	}

}
