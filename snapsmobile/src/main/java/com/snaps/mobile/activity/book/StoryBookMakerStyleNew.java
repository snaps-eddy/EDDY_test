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
	 * ?????? ????????? ???????????? ??????
	 */
	ArrayList<StoryBookChapter> chapters = new ArrayList<StoryBookChapter>();

	/**
	 * ????????? ????????? ???????????? ??????.
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
		
		//?????? ???????????? ????????? ????????????.
		setCoverEditable(coverPage);
		
		// ????????? ????????? ??????. ????????? ????????? ?????? ????????? ????????? ?????? ??????.
		template.setSNSBookStick(setCoverSpineText(coverPage));

		// auraorderText ??????..
		coverPage.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);

		return template;
	}
	
	private void setCoverEditable(SnapsPage page) {
		// ?????????..
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

		// auraorderText ??????..
		titlePage.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);

		SnapsPage _totalPage = getPage(template, StoryPageType.TOTAL_PAGE);
		setDataControls(_totalPage, null);

		setTotalBestImageData(_totalPage);

		setChapterPageAttr(_totalPage);

		// auraorderText ??????..
		_totalPage.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);

		ArrayList<SnapsPage> makedPages = new ArrayList<SnapsPage>();
		makedPages.add(getPage(template, StoryPageType.COVER_PAGE)); // ??????
		makedPages.add(getPage(template, StoryPageType.INDEX_PAGE)); // ?????????
		makedPages.add(getPage(template, StoryPageType.TITLE_PAGE)); // ??????
		makedPages.add(_totalPage); // total

		if (createdPageList != null && createdPageList.size() > 0)
			makedPages.addAll(createdPageList);
		makedPages.add(getPage(template, StoryPageType.FRIENDS_PAGE));

		template.getPages().clear();
		template.getPages().addAll(makedPages);
		createdPageList.clear();

		// ?????? ????????? ?????? ????????? ??????.
		totalPage = template.getPages().size();

		return template;
	}

	@Override
	public SnapsTemplate setIndexTemplate(SnapsTemplate template) {
		SnapsPage indexPage = getPage(template, StoryPageType.INDEX_PAGE);
		setDataControls(indexPage, null);
		StoryBookIndexMaker maker = new StoryBookIndexMaker(indexPage, chapters, dataManager);
		maker.makeIndex();

		// auraorderText ??????..
		indexPage.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);

		return template;
	}

	@Override
	public SnapsTemplate setPageTemplate(SnapsTemplate template) {

		templateInfo = template.info;

		// ??????????????? ????????? ??????..
		setDataFriendPage(template.getPages());

		// ?????? ?????????
		makePage(template);
		return template;
	}

	@Override
	int makeDatePart(StoryData data, StoryChapterInfo chapterInfo, boolean isSizeCheck) {
		// topbar
		// ?????????

		// ????????????
		// ?????? ??????.
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
	 * snsproperty ????????? ?????? ????????? ?????? ???????????? ??????.
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
				// ??? + ?????? + ???/??????
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
	 * snsproperty ????????? ?????? ????????? ?????? ???????????? ??????.
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
	 * ???????????? ???????????? ????????? ??????.
	 * 
	 * @param page
	 */
	void setDataControls(SnapsPage page, StoryBookChapter chapter) {
		// ?????????..
		for (SnapsControl c : page.getLayerLayouts()) {
			if (c instanceof SnapsLayoutControl) {
				SnapsLayoutControl control = (SnapsLayoutControl) c;
			
				ImageInfo imgInfo = getImageData(control.getSnsproperty(), chapter);
				if (imgInfo != null) {
					MyPhotoSelectImageData myPhotoImageData = new MyPhotoSelectImageData();
					myPhotoImageData.KIND = Const_VALUES.SELECT_KAKAO;
					myPhotoImageData.PATH = imgInfo.original;
					myPhotoImageData.THUMBNAIL_PATH = imgInfo.medium;
					// ?????? ????????? w, h ??????
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

		// ?????????..
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

		// ?????? ?????? ??????(releative)
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
				offsetControlSize = UIUtil.convertPixelsToSp(context, Float.parseFloat(textControl.format.fontSize)); // 3??????
																														// ????????? ???
																														// ??????
																														// ??????..
				int fontTotalSize = (int) (offsetControlSize * textControl.text.length());
				offset = (int) (coordControlRight - fontTotalSize);
			} else {
				offset = (int) (Float.parseFloat(offsetControl.x));
			}

			offset -= Float.parseFloat(control.width);
		} else {
			if (offsetControl instanceof SnapsTextControl) {
				SnapsTextControl textControl = (SnapsTextControl) offsetControl;
				offsetControlSize = UIUtil.convertPixelsToSp(context, Float.parseFloat(textControl.format.fontSize)); // 3??????
																														// ????????? ???
																														// ??????
																														// ??????..
				int fontTotalSize = (int) (offsetControlSize * textControl.text.length());
				offset = (int) (Float.parseFloat(offsetControl.x) + fontTotalSize);
			} else {
				offset = (int) coordControlRight;
			}
		}

		return offset;
	}

	String getCoordinateByStick(SnapsPage page, SnapsControl control) {
		// offsetControl ?????? ?????????
		// control ?????? ????????? ?????????..

		if (control == null)
			return null;

		String targetName = control.stick_target;
		String direction = control.stick_dirction;
		String margin = control.stick_margin;

		if (targetName == null || direction == null || margin == null || targetName.length() < 1 || direction.length() < 1 || margin.length() < 1)
			return null;

		// ???????????? ????????????..
		if (targetName.contains("11-p4"))
			return null;

		SnapsControl offsetControl = findStickOffsetTargetControl(page, targetName);

		if (offsetControl == null)
			return null;

		// ?????? ?????????.
		int adjust = 0;

		if (targetName.contains("12-p4")) {
			// ordinary ????????? ????????? ????????? ?????? ??????????????? ??????.
			// stories
			if (targetName.equals("12-p4-3") || targetName.equals("12-p4-6")) {
				control.x = control.getIntX() - (offsetControl.getIntWidth() / 5) * (4 - ((SnapsTextControl) offsetControl).text.length()) + "";
				return null;
			} else if (control.id.equals("12-p4-3") || control.id.equals("12-p4-6") || control.id.equals("12-p4-5"))
				return null;

			else if (targetName.equals("12-p4-2") || targetName.equals("12-p4-1")) {
				// offset??? ????????? ??????.. ????????? ?????? ???..
				int length = ((SnapsTextControl) offsetControl).text.length();
				adjust = (7 - length) * -5;
			}

		}

		// 2?????? ?????????..
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
	 * ?????? ???????????? ???????????? ??????..
	 * 
	 * @param coverPage
	 */
	SnapsTextControl setCoverSpineText(SnapsPage coverPage) {
		// ?????????..
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
	 * ?????? Best ????????? ???????????? ?????? ??????.
	 * 
	 * @param page
	 */
	void setTotalBestImageData(SnapsPage page) {

		if (!page.getSnsproperty().equals("total"))
			return;

		// best ????????? ????????? ?????????
		ArrayList<StoryData> bestStoryData = dataManager.getSortedStories(eSTORY_DATA_SORT_TYPE.POPULAR);
		ArrayList<ImageInfo> bestImage = new ArrayList<ImageInfo>();
		for (StoryData d : bestStoryData) {
			for (ImageInfo i : d.images)
				bestImage.add(i);

		}

		// ????????? ??????.
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
			// ?????? ????????? w, h ??????
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
	 * ?????? ????????? ????????? ???????????? ???????????? ??????..
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

		// url??? null????????? ?????? ?????????.
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
	 * ????????? ????????? ?????? ???????????? ???????????? ??????..
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

	// ?????? ????????? ????????? ?????? ???
	String rightUpDate = null;

	// ????????? ????????? 1?????? ????????? ??????.
	boolean isChapter = true;

	/***
	 * ?????? ???????????????.
	 * 
	 * @param template
	 */
	void makePage(SnapsTemplate template) {
		/***
		 * ?????? ????????? ????????????.. ??????????????? ?????? ?????? ??????????????? ?????? ?????? ?????? ????????? ????????? ?????? ????????? ????????? ??????.. ??????????????? ?????? ??????????????? ??????
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

		// ?????? ?????? ????????? ?????????.

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
	 * ????????? ????????? ?????? ????????? ?????? ??????.
	 * 
	 * @return
	 */
	boolean initPage() {

		boolean isResult = true;
		// ???????????? ????????? ?????? ??? ????????? ????????? ?????? ??????.
		if (tempPageList != null && tempPageList.size() > 0) {

			// ????????? 1???????????? ?????? ?????? ????????? ??????,
			if (!isChapter) {
				SnapsPage p = tempPageList.get(tempPageList.size() - 1);
				if (!p.isExistControls()) {
					p.getBgList().clear();
					p.addBg((SnapsBgControl) lastPage.getBgList().get(0));
					p.removeText("inner_month");
					p.removeText("inner_month");
				}
			}

			// ?????? ????????? ??????
			// ?????? ???????????? ???????????? ????????? ????????? ?????? ?????????.
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
	 * ??? ????????? ???????????? ??????
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

		// ?????????
		String startMonth = null;
		ArrayList<HashMap<String, Integer>> pageInfo = new ArrayList<HashMap<String, Integer>>();
		boolean isMonths = false;
		// ????????? ????????? ??????.
		int pageCnt = 0;

		int i = startStory;
		for (; dataManager.getStoryCount() > i; i++) {
			// ???????????? ???????????? ?????????
			dataManager.getStory(i).startPage = cPage;

			// ?????? ???????????? ??????.
			String storyMonth = StoryBookStringUtil.getMonthByKakaoDate(dataManager.getStory(i).createdAt);
			dataManager.getStory(i).storyDate = storyMonth;
			// ?????? ????????????
			if (startMonth != null && !startMonth.equals(storyMonth)) {

				// ??????????????? ????????? ???????????? ??????.
				isMonths = pageInfo.size() > 0;
				pageCnt = cPage * 2;

				// ??????
				if (!isMonths) {
					if (pageCnt >= 20) {
						break;
					}
				}// ????????????.
				else {
					// ???????????? 21p ~ 31p??? ?????? ?????? ??????.
					if (pageCnt >= 20) {
						break;
					}
				}

				HashMap<String, Integer> pInfo = new HashMap<String, Integer>();
				// ????????? ????????? ????????? ????????? ??????.
				// 1??? 20p ???????????????
				pInfo.put(storyMonth, cPage);
				pageInfo.add(pInfo);
				startMonth = storyMonth;

			} else {

				startMonth = storyMonth;
			}

			// ?????? ????????? ????????? ?????? ??????.
			rightUpDate = dataManager.getStory(i).createdAt;
			
			StoryData data = dataManager.getStory(i);
			if(cHeight > (ED_HEIGHT - BOTTOM_MARGIN) / 3)
			{
				int storyHeight = DATE_CONTROL_HEIGHT;
				storyHeight += makeNotePart(workPage, data, true);
				storyHeight += makeImagePart(data, true);
				//????????? ????????? 3?????? ???????????? ????????? ????????? ?????????????????? ??????.
				if (getRestHeightAtSide() < storyHeight /*&& storyHeight <= ENABLE_MAX_HEIGHT*/ ) {
					checkInsidePage(0, true);
				}
				
				makeDatePart(workPage, data, isChapter);
				makeNotePart(workPage, data, false);
			} else {
				
				boolean isExistTextContents = data != null && data.content != null && data.content.trim().length() > 0;
				int storyHeight = 0;

				//???????????? ?????? ?????? ??????
				if(isExistTextContents) {
					storyHeight = DATE_CONTROL_HEIGHT;
					storyHeight += makeNotePart(workPage, data, true);
					
					if (storyHeight > getRestHeightAtSide()) {
						checkInsidePage(0, true);
					}
					
					// ???????????? ?????????
					makeDatePart(workPage, data, isChapter);
					// ????????? ?????????
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
					
					// ???????????? ?????????
					makeDatePart(workPage, data, isChapter);
				}
			}

			// ????????? ??????
			makeImagePart(data, false);
			
			// ??????
			makeResponsePart(data);

			// ?????? ??????
			makeCommentPart(data);

			// ???????????? ????????? ??????
			dataManager.getStory(i).endPage = cPage;
			dataManager.getStory(i).cSide = cSide;

			cHeight += 20;
		}

		pageCnt = cPage * 2;
		// ??????
		if (!isMonths) {
			// ????????? ????????? 40p ????????? ??????.
			if (pageCnt > 40) {
				// ?????? ?????? ?????????
				int pPage = pageCnt;
				// ????????? ???????????? ????????? ?????????.
				pPage = MathUtils.getHalfNumber(pPage, 40);

				// pPage?????? ????????? ???????????? ????????????.
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

				// ?????? ?????? ??????????????? ????????? ????????? ?????????.
				i = idx;

				// ????????? ??????
				if (tempPageList != null) {
					tempPageList = ArrayListUtil.removeArrayList(tempPageList, enableChapterPage);
				}

				// ?????????????????? ????????? ????????????
				// ???????????? ????????? ??????..
				tempPageList.get(tempPageList.size() - 1).removeControls(baseDate);
			}

		}// ????????????..
		else {
			if (pageCnt > 31) {
				// ?????????????????? ???????????? 31p??? ???????????? ???????????? ?????? ????????? ?????????.
				// ??????????????? ???????????? ?????????.

				// ????????? ????????? ???????????? ?????????.
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

				// ?????? ?????? ??????????????? ????????? ????????? ?????????.
				i = a + 1;

				// ????????? ??????
				if (tempPageList != null) {
					tempPageList = ArrayListUtil.removeArrayList(tempPageList, enableChapterPage);
				}

				// ?????????????????? ????????? ????????????
				// ???????????? ????????? ??????..
				tempPageList.get(tempPageList.size() - 1).removeControls(baseDate);

			}
		}

		// ????????? ?????? ??? ?????? ?????? ??????.
		chapter.setEndStoryIndex(i - 1);

		// ??????????????? ??????..
		if (createdPageList.size() + tempPageList.size() + 1 <= MAX_INNER_PAGE) {

			chapters.add(chapter);

			// ????????? ???????????? ??????????????? ??????
			SnapsPage theme = getPage(template, StoryPageType.THEMA_PAGE);
			theme = theme.copyPage(0);
			setDataControls(theme, chapter);

			// ????????? ?????????
			setChapterPageAttr(theme);

			// auraorderText ??????..
			theme.setTextControlFont(Const_PRODUCT.AURATEXT_RATION);
			createdPageList.add(theme);

			// ??????????????? ??????..
			SnapsPage chapterPage = tempPageList.get(0);
			setDataControls(chapterPage, chapter);

		} else {
			// ?????????????????? ????????????
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

		// ??????????????? ????????? ????????????.
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
				note.format.fontFace = "????????? ????????? 700";// "????????? ????????? 230";
				note.format.alterFontFace = "????????? ????????? 700";// "????????? ????????? 230";
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

								if (curLineText.length() > 27) // ????????? ?????? 28??? ??????
																// ????????? ...??? ?????????
																// ??????????????? ????????????.
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
	 * ???????????? ?????????..
	 * 
	 * @param page
	 * @param data
	 * @param isChapter
	 */
	void makeDatePart(SnapsPage page, StoryData data, boolean isChapter) {

		// ??????????????? ??????????????? ??????
		checkInsidePage(DATE_CONTROL_HEIGHT, false);
		// ???????????? ???????????? ?????? ????????? ??????.
		data.startPage = cPage;

		// top bar ?????????
		SnapsClipartControl topbar = makeStickerControl(topBar);
		topbar.x = cStart + "";
		topbar.y = cHeight + "";
		topbar.width = "137";

		// ????????? ?????????
		SnapsClipartControl icon = null;
		int offset = 0;// ????????? ???????????? ????????? ???????????? ?????????.. ??????..
		if (data.images.size() > 0)
			icon = makeStickerControl(camera);
		else {
			icon = makeStickerControl(text);
			offset = 2;
		}

		icon.x = cStart + "";
		icon.y = cHeight + offset + "";

		// ?????? ??????
		String text = getTextData(data, dateText.getSnsproperty(), null);
		SnapsTextControl date = makeTextControl(dateText, text, false);
		date.format.auraOrderFontSize = Float.parseFloat(date.format.fontSize) * Const_PRODUCT.AURATEXT_RATION + "";
		date.format.fontSize = "6";

		date.x = icon.getIntX() + icon.getIntWidth() + 3 + "";
		date.y = cHeight + 7 + "";

		// ?????? ??????
		String tTime = getTextData(data, timeText.getSnsproperty(), null);
		SnapsTextControl time = makeTextControl(timeText, tTime, false);
		time.format.auraOrderFontSize = Float.parseFloat(time.format.fontSize) * Const_PRODUCT.AURATEXT_RATION + "";
		time.x = date.getIntX() + date.getIntWidth() + "";
		time.y = cHeight + 8 + "";
		time.format.fontSize = "4";

		// ????????? ??????.
		topbar.identifier = data.createdAt;
		icon.identifier = data.createdAt;
		date.identifier = data.createdAt;
		time.identifier = data.createdAt;

		// ????????? ??????.
		workPage.addControl(topbar);
		workPage.addControl(icon);
		workPage.addControl(date);
		workPage.addControl(time);

		cHeight += (topbar.getIntHeight() + icon.getIntHeight() + 7);
	}

	/***
	 * ????????? ?????? ?????????
	 * 
	 * @param page
	 * @param data
	 */
	int makeNotePart(SnapsPage page, StoryData data, boolean isSizeCheck) {
		if (data.content == null || data.content.length() <= 0)
			return 0;

		// ??????????????? ????????? ????????????.
		int limitSize = 0;
		float fontRatio = 0.f;
		if (Build.VERSION.SDK_INT > 20) { // ????????? ?????? ??????

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



		// ?????? ???????????? ????????? ???????????? ???????????? ????????? ????????????.
		// ????????? ??????????????? ?????????.
		int enableHeight = getRestHeightAtSide();
		if (textData.getTextHeight() > enableHeight) {
			checkInsidePage(0, true);
		}

		// ???????????? ???????????? ????????????...
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
			// ????????? ??????.
			note.identifier = data.createdAt;
			workPage.addControl(note);
			cHeight += note.getIntHeight();
			cHeight += NOTE_DOWN_MARGIN;

		} else { // ??????????????? ????????????...
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

				// ????????? ???????????? ????????? ?????? ?????????.
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

			// ????????? ????????? ??????..
			MyPhotoSelectImageData imgData = new MyPhotoSelectImageData();
			imgData.KIND = Const_VALUES.SELECT_KAKAO;
			imgData.FB_OBJECT_ID = data.id;
			imgData.PATH = info.original;
			imgData.THUMBNAIL_PATH = info.small;
			imgData.KAKAOBOOK_DATE = data.createdAt;
			// ?????? ????????? w, h ??????
			imgData.F_IMG_WIDTH = info.getOriginWidth();
			imgData.F_IMG_HEIGHT = info.getOriginHeight();

			// ????????????..
			// ?????? ??????????????? ???
			String maxHeight = "410";

			SnapsLayoutControl layoutControl = SnapsLayoutControl.makeImageLayoutControl();// makeImageLayoutControl();
			Rect rc = CalcViewRectUtil.getLayoutControlRect(layoutControl.width, maxHeight, imgData.F_IMG_WIDTH, imgData.F_IMG_HEIGHT);

			// ????????? ????????? ????????? ???????????? 1cm ???????????? ????????? ????????? ?????? ???????????? ?????? ????????? ?????? ????????? ??????.

			int imgHeight = rc.height();

			if(isSizeCheck)
				return imgHeight;

			boolean isNewSide = checkInsidePage(imgHeight, false);

			layoutControl.imgData = imgData;
			layoutControl.angle = "0";
			layoutControl.imagePath = imgData.PATH;
			layoutControl.imageLoadType = imgData.KIND;
			layoutControl.y = cHeight + ""; // ????????? ?????? ????????? ?????? ????????? ?????????.
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

		// ????????????..
		checkInsidePage(PAGE_REPLY_HEIGHT, false);

		// ?????? ?????????
		SnapsClipartControl feelSticker = SnapsClipartControl.setFeelSticker();
		feelSticker.x = cStart + 5 + "";// (cStart + calc.calcMM(5)) + "";
		feelSticker.y = (cHeight + PAGE_REPLY_HEIGHT / 2 - feelSticker.getIntHeight() / 2) + "";// (cHeight +
																								// (calc.calcMM(PAGE_REPLY_HEIGHT) -
																								// 0) / 2) + "";
		feelSticker.identifier = data.createdAt;
		workPage.addControl(feelSticker);

		// ????????? ?????????
		SnapsTextControl feelCountText = SnapsTextControl.getSnapsText(10, 5, 7, "????????? ????????? 700");
		feelCountText = makeTextControl(feelCountText, data.likeCount + "", true, REPLY_FONT_RATIO, FontUtil.TEXT_TYPE_CONTENTS);
		feelCountText.format.auraOrderFontSize = "7";
		feelCountText.x = feelSticker.getIntX() + feelSticker.getIntWidth() + 1 + "";
		feelCountText.y = (cHeight + PAGE_REPLY_HEIGHT / 2 - feelCountText.getIntHeight() / 2) + "";
		feelCountText.text = data.likeCount + "";
		feelCountText.identifier = data.createdAt;
		workPage.addControl(feelCountText);

		// ?????? ?????? ?????????
		SnapsClipartControl commentSticker = SnapsClipartControl.setRelySticker();// makeStickerControl(ballonCilpart);
		commentSticker.x = feelCountText.getIntX() + feelCountText.getIntWidth() + 2 + "";
		commentSticker.y = (cHeight + PAGE_REPLY_HEIGHT / 2 - commentSticker.getIntHeight() / 2) + "";
		commentSticker.identifier = data.createdAt;
		workPage.addControl(commentSticker);

		// ????????? ?????????
		SnapsTextControl commentCountText = SnapsTextControl.getSnapsText(10, 5, 7, "????????? ????????? 700");
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

			// ????????? 5??? ???????????? etc??? ??????????????? ??????...
			if (data.likes.size() > 5) {
				SnapsClipartControl more = SnapsClipartControl.setEtcSticker();// makeStickerControl(etcCilpart);
				more.x = cStart + POSTING_WIDTH - 5 - more.getIntWidth() - 4 - 1 + "";// cStart + calc.calcMM(STORY_WIDTH_MM - 8);
				more.y = cHeight + PAGE_REPLY_HEIGHT / 2 - more.getIntHeight() / 2 + "";
				workPage.addControl(more);

				// ?????? ????????? ?????????.
				SnapsTextControl etc = SnapsTextControl.getSnapsText(5, 3, 6, "????????? ????????? 700");// etcCountTextControl.copyControl();
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
				// ????????? ????????? ??????..
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

				// ????????? ????????? ?????? ???????????? ????????????.
				SnapsClipartControl feelClipart = SnapsClipartControl.getStickerControl(like.emotion);
				feelClipart.width = "6";
				feelClipart.height = "6";
				feelClipart.x = (profileImage.getIntX() + profileImage.getIntWidth() - feelClipart.getIntWidth() / 2) + "";
				feelClipart.y = (profileImage.getIntY() + profileImage.getIntHeight() - feelClipart.getIntHeight()) + "";

				feelClipart.identifier = data.createdAt;
				workPage.addControl(feelClipart);

				// ????????? ??????????????? margin
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
			// ????????? ??????
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

			// ??????.
			SnapsTextControl displayName = SnapsTextControl.getSnapsText(35, 4, 4, "????????? ????????? 700");// ;
			displayName = makeTextControl(displayName, comment.writer.displayName, true, 1.0f, FontUtil.TEXT_TYPE_COMMENT);
			displayName.format.auraOrderFontSize = "6";
			displayName.y = cHeight + 4 + "";// cHeight +
												// calc.calcMM(REPLY_UP_MARGIN_MM)
												// + "";
			displayName.x = profileImage.getIntX() + profileImage.getIntWidth() + 4 + "";// calc.calcMM(RELPY_START_MARGIN_MM) + "";
			displayName.height = "4";
			displayName.width = "15";

			// ??????.
			SnapsTextControl commentText = SnapsTextControl.getSnapsText(111, 4, 4, "????????? ????????? 700");//
			commentText = makeTextControl(commentText, comment.text, false, REPLY_FONT_RATIO, FontUtil.TEXT_TYPE_COMMENT);
			commentText.format.auraOrderFontSize = "6";
			commentText.x = displayName.x;
			commentText.y = displayName.getIntY() + displayName.getIntHeight() + 1 + "";// + calc.calcMM(1) + "";

			// ????????????
			// ?????? ?????????..
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

			// ??????.
			displayName.y = profileImage.y;// cHeight +
											// calc.calcMM(REPLY_UP_MARGIN_MM) +
											// "";
			displayName.x = profileImage.getIntX() + profileImage.getIntWidth() + 4 + "";// calc.calcMM(RELPY_START_MARGIN_MM) + "";

			// ??????.
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

			// ????????? ??? ????????? Y?????? ??????..
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

		textControl.format.fontFace = "????????? ????????? 700";// "????????? ????????? 230";
		textControl.format.alterFontFace = "????????? ????????? 700";// "????????? ????????? 230";
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
	 * ?????? ???????????? ????????? ???????????? ????????? ???????????? ??????. ????????? ???????????? ????????? ??????????????? ????????? ??????.
	 * 
	 * @param addHeight
	 *            ???????????? ????????? ??????
	 * @param isForce
	 *            ????????? ???????????? ????????? ??????
	 * @return
	 */
	boolean checkInsidePage(int addHeight, boolean isForce) {
		// cPage ????????????
		// cSide ?????? (????????? 4?????????.)
		boolean isNewPage = false;

		if (workPage == null || (cHeight + addHeight) > (ED_HEIGHT - BOTTOM_MARGIN) || isForce) {
			isNewPage = true;
		}

		// ???????????? ?????????
		if (isNewPage) {
			// ???????????? ????????? ????????????.
			// ???????????? ???????????? ??????...
			setEndStoryBackground(cHeight + 4);

			if (cSide >= 3 || workPage == null) {
				// ????????? ?????? ???????????? ?????? ??????.
				if (workPage != null)
					isChapter = false;

				cStart = START_MARGIN;
				if (workPage == null)
					cHeight = TOP_MARGIN_WITHCATEGORY;
				else
					cHeight = TOP_MARGIN;

				// ????????? ???????????? ?????????.
				workPage = makeSnapsPage(templateInfo, isChapter);

				setRightUpDate();

				cSide = 0;
				cPage++;
				tempPageList.add(workPage);

			} else {

				cSide++;

				if (cSide == 1) {// 2???
					cStart = START_MARGIN + POSTING_WIDTH + MIDDLE_MARGIN;
					if (isChapter)
						cHeight = TOP_MARGIN_WITHCATEGORY;
					else
						cHeight = TOP_MARGIN;

				} else if (cSide == 2) {// 3???
					cStart = START_MARGIN + POSTING_WIDTH * 2 + MIDDLE_MARGIN + SIDE_MARGIN * 2;
					cHeight = TOP_MARGIN;

				} else if (cSide == 3) {// 4???
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
			// ?????? ???????????? ????????????.
			p = page.copyPage(0);
			// ????????? ????????? ????????? ??????..
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
	 * ???????????? ?????? ??????????????? ????????? ????????? ???????????? ????????? ??????.
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

		// ??????, ????????? ?????? ???????????? ????????? ????????? ?????? ?????????..????????? ?????? ???????????? ???????????? ??????.??????
		if (tempBestStoryIndex < 0) {
			tempBestStoryIndex = Math.max(0, (sIndex + endIndex) / 2);
		}

		return tempBestStoryIndex;
	}

	/***
	 * ?????? ?????? ????????? ???????????? ??????.
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
