package com.snaps.kakao.utils.kakao;

import com.kakao.kakaostory.KakaoStoryService;

import com.kakao.kakaostory.callback.StoryResponseCallback;
import com.kakao.kakaostory.response.ProfileResponse;
import com.kakao.kakaostory.response.model.MyStoryImageInfo;
import com.kakao.kakaostory.response.model.MyStoryInfo;
import com.kakao.kakaostory.response.model.StoryComment;
import com.kakao.kakaostory.response.model.StoryLike;
import com.kakao.kakaostory.response.model.StoryProfile;
import com.kakao.network.ErrorResult;
import com.kakao.util.KakaoParameterException;
import com.snaps.common.storybook.IOnStoryDataLoadListener;
import com.snaps.common.storybook.IStoryDataStrategy;
import com.snaps.common.storybook.StoryData;
import com.snaps.common.storybook.StoryData.ActorData;
import com.snaps.common.storybook.StoryData.ImageInfo;
import com.snaps.common.storybook.StoryData.StoryCommentData;
import com.snaps.common.storybook.StoryData.StoryDate;
import com.snaps.common.storybook.StoryData.StoryLikeData;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StoryBookStringUtil;
import com.snaps.common.utils.ui.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KakaoStoryDataManager implements IStoryDataStrategy {
	private static final String TAG = KakaoStoryDataManager.class.getSimpleName();
	private final String DEFAULT_PROFILE_IMG_URL = SnapsAPI.DOMAIN()+"/Upload/Data1/Resource/sticker/edit/Est139_gg.png";
	private int commentCountLimit = 0;
	private int photoCountLimit = 0;

	// 스토리 정보를 저장해놓는 Array
	private StoryProfile profileData = null;
	private IOnStoryDataLoadListener listener = null;
	private ArrayList<com.snaps.common.storybook.StoryData> arrStoryDatas = new ArrayList<StoryData>();
	private int m_iProgressValue = 0;

	// 시작
	private String startDate = "";
	// 종료일.
	private String endDate = "";
	// 댓글 단 친구들를 구하기 위한 해쉬셋...
	private HashSet<String> replyFriends = new HashSet<String>();
	private HashSet<String> friendImageList = new HashSet<String>();

	private String overLimitEndDate = null, overLimitStartDate = null;

	/***
	 * 시작일과 종료일을 넣어서 스토리 리스트를 가져오는 함수..
	 *
	 * @param startDate
	 * @param endDate
	 */
	@Override
	public void getStoryies(String startDate, String endDate, int commentCount, int photoCount, IOnStoryDataLoadListener listener) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.listener = listener;
		this.commentCountLimit = commentCount;
		this.photoCountLimit = photoCount;

		// 데이터 초기화..
		// storyies.clear();
		// 카카오스토리 가져오기 시작..
		arrStoryDatas.clear();

		// 프로필 정보를 가져온다.
		readProfile();

		// getRequestStoryList(""); //프로필 정보 조회 완료 후 시작.
		// 7 + 7
	}

	void getRequestStoryList(String lastid) {
		KakaoStoryService.getInstance().requestGetMyStories(kakaoStoriesResponseCallback,lastid);
	}

	private StoryDate createStoryData(String date) {
		if (date == null || !date.contains("T"))
			return null;

		try {
			String[] arDate = date.split("T");
			date = arDate[0];
			String year = "", month = "", day = "", hour = "", min = "";
			if (date != null && date.contains("-")) {
				String[] arrDate = date.split("-");

				if (arrDate != null && arrDate.length >= 3) {
					year = arrDate[0];
					month = arrDate[1];
					day = arrDate[2];

				}
			}

			date = arDate[1];
			if (date != null && date.contains(":")) {
				String[] arrTime = date.split(":");
				if (arrTime != null && arrTime.length >= 3) {
					hour = arrTime[0];
					min = arrTime[1];
				}
			}
			return new StoryDate(year, month, day, hour, min);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return null;
	}

	public boolean compareKaKaoDate(String startDate, String endDate, String postDate) {
		if (postDate == null || startDate == null || endDate == null)
			return false;

		// yyyyMMdd
		if (startDate.length() != 8 || endDate.length() != 8 || postDate.length() != 8)
			return false;
		try {
			int iPostDate = Integer.parseInt(postDate);
			int iStartDate = Integer.parseInt(startDate);
			int iEndDate = Integer.parseInt(endDate);
			return iPostDate >= iStartDate && iPostDate <= iEndDate;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return false;
	}

	public boolean isBeforeKaKaoDate(String offsetDate, String targetDate) {
		if (offsetDate == null || targetDate == null)
			return false;

		// yyyyMMdd
		if (offsetDate.length() != 8 || targetDate.length() != 8)
			return false;
		try {
			int iOffsetDate = Integer.parseInt(offsetDate);
			int iTargetDate = Integer.parseInt(targetDate);
			return iTargetDate < iOffsetDate;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return false;
	}

	public boolean isAfterKaKaoDate(String offsetDate, String targetDate) {
		if (offsetDate == null || targetDate == null)
			return false;

		// yyyyMMdd
		if (offsetDate.length() != 8 || targetDate.length() != 8)
			return false;
		try {
			int iOffsetDate = Integer.parseInt(offsetDate);
			int iTargetDate = Integer.parseInt(targetDate);
			return iTargetDate > iOffsetDate;
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
		return false;
	}

	private abstract class KakaoStoryResponseCallback<T> extends StoryResponseCallback<T> {

		@Override
		public void onNotKakaoStoryUser() {
			if (listener != null)
				listener.onStoryLoadFail(IOnStoryDataLoadListener.ERR_CODE_FAILED_GET_STORY_LIST);
		}

		@Override
		public void onFailure(ErrorResult errorResult) {
			if (listener != null)
				listener.onStoryLoadFail(IOnStoryDataLoadListener.ERR_CODE_FAILED_GET_STORY_LIST);
		}

		@Override
		public void onSessionClosed(ErrorResult errorResult) {
			if (listener != null)
				listener.onStoryLoadFail(IOnStoryDataLoadListener.ERR_CODE_FAILED_GET_STORY_LIST);
		}

		@Override
		public void onNotSignedUp() {
			if (listener != null)
				listener.onStoryLoadFail(IOnStoryDataLoadListener.ERR_CODE_FAILED_GET_STORY_LIST);
		}
	}
	KakaoStoryResponseCallback kakaoStoryResponseCallback = new KakaoStoryResponseCallback<MyStoryInfo>() {
		@Override
		public void onSuccess(MyStoryInfo info) {

			if (info == null) {
				if (listener != null)
					listener.onStoryLoadFail(IOnStoryDataLoadListener.ERR_CODE_FAILED_GET_STORY_DETAIL);
			}

			setMyStoryData(info);
			String nextid = getNextId(info.getId());
			if (nextid != null) {

				if (listener != null)
					listener.onStoryLoadStateUpdate(IOnStoryDataLoadListener.PROGRESS_TYPE_GET_STORY_DETAIL_INFO, (int) ((++m_iProgressValue / (float) arrStoryDatas.size()) * 100));

				getRequestStoryDetail(nextid);
			} else {
				// 정보가져오기 종료...
				// 스토리는 최신부터 과거로 재정렬
				// 과거에서 최신으로 정렬하기..
				Collections.sort(arrStoryDatas, new KakaoCompare());
				if (listener != null)
					listener.onStoryDetailLoadComplete();
			}
		}
	};
	KakaoStoryResponseCallback kakaoStoriesResponseCallback = new KakaoStoryResponseCallback<List<MyStoryInfo>>() {
		@Override
		public void onSuccess(List<MyStoryInfo> resultObj) {
			try {
				String lastid = "";

				if (resultObj != null && resultObj.size() > 0) {
					if (arrStoryDatas != null) {
						synchronized (arrStoryDatas) {

							com.snaps.common.storybook.StoryData storyData = null;
							int idx = 0;
							for (MyStoryInfo info : resultObj) {

								lastid = info.getId();
								String createAt = info.getCreatedAt();

								String postDate = StringUtil.getDateFormatKakao3(StringUtil.convertCreateStringToLong(createAt));

								boolean isValidDate = compareKaKaoDate(startDate, endDate, postDate);
								// 날짜 비교하기...
								// 해당날짜안에 들어가지 않는 경우..
								if (isValidDate) {

									if (KakaoStoryService.StoryType.NOTE.equals(info.getMediaType()) && KakaoStoryService.StoryType.PHOTO.equals(info.getMediaType()))
										continue;

									storyData = new StoryData();
									storyData.id = info.getId();
									storyData.createdAt = info.getCreatedAt();

									String date = storyData.createdAt; // 2015-01-20T04:45:51Z
									storyData.dateInfo = createStoryData(date);

									storyData.commentCount = info.getCommentCount();
									storyData.likeCount = info.getLikeCount();
									storyData.content = StringUtil.CleanInvalidXmlChars(info.getContent());
									storyData.images = new ArrayList<StoryData.ImageInfo>();

									boolean isExistImage = false;
									List<MyStoryImageInfo> arrStoryImgs = info.getImageInfoList();
									if (arrStoryImgs != null && arrStoryImgs.size() > 0) {
										ImageInfo imgInfo = null;
										int photoIdx = 0;
										for (MyStoryImageInfo storyImg : arrStoryImgs) {

											if (++photoIdx > photoCountLimit)
												break;

											imgInfo = new ImageInfo();
											imgInfo.xlarge = storyImg.getXlarge();
											imgInfo.large = storyImg.getLarge();
											imgInfo.medium = storyImg.getMedium();
											imgInfo.original = storyImg.getXlarge();// storyImg.getOriginal();
											imgInfo.small = storyImg.getSmall();

											imgInfo.setOriginWidth("640");
											imgInfo.setOriginHeight("640");

											if (imgInfo.original != null) {
												if (imgInfo.original.contains("width") && imgInfo.original.contains("height")) {
													imgInfo.setOriginWidth(StringUtil.getTitleAtUrl(imgInfo.original, "width", true));
													imgInfo.setOriginHeight(StringUtil.getTitleAtUrl(imgInfo.original, "height", true));
												}
											}

											storyData.images.add(imgInfo);
											isExistImage = true;
										}
									}

									boolean isEnable = true;

									if (isExistImage && storyData.content.length() > 0) {
										storyData.storyType = "NOTE_PHOTO";
									} else if (isExistImage) {
										storyData.storyType = "PHOTO";
									} else if (storyData.content.length() > 0) {
										storyData.storyType = "NOTE";
									} else {
										storyData.storyType = "NOT SUPPORT";
										isEnable = false;
									}

									if (isBeforeKaKaoDate(startDate, postDate)) {
										isEnable = false;
									}

									if (isEnable && isValidKakaoText(storyData.content))
										arrStoryDatas.add(storyData);
								}

								// 왜 또 한번 비교를?
								// if (postDate.compareTo(startDate) < 0) {
//								if (isBeforeKaKaoDate(startDate, postDate)) { //왜 이런 코드가 들어 있는 지, 이해가 안되서 주석으로 막아 버림. 위쪽에서 담지 않도록 처리하는 방식으로 수정 함.
//									lastid = "";
//									break;
//								}

								if (listener != null)
									listener.onStoryLoadStateUpdate(IOnStoryDataLoadListener.PROGRESS_TYPE_GET_STORY_LIST_INFO, (int) ((idx++ / (float) resultObj.size()) * 100));
							}
						}
					}
				}

				if (!lastid.equals("")) {
					getRequestStoryList(lastid);
				} else {// 리스트 로딩이 완료가 되면 상세 정보를 요청하여 가져온다..
//					// TODO test code
//					int index = 0;
//					while( arrStoryDatas.size() < 200 ) {
//						if( index > arrStoryDatas.size() - 1 ) index = 0;
//						arrStoryDatas.add( arrStoryDatas.get(index) );
//					}
//					// TODO test code end


					if (listener != null)
						listener.onStoryListLoadComplete(arrStoryDatas);
				}
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}
	};
	// 스토리 리스트 정보 handler

	@Override
	public void requestStoriesDetail() {
		m_iProgressValue = 0;
		String firstStoryID = getNextId(null);

		if (firstStoryID == null) {
			if (listener != null)
				listener.onStoryLoadFail(IOnStoryDataLoadListener.ERR_CODE_INVALID_PERIOD);
			return;
		}

		replyFriends.clear();
		getRequestStoryDetail(firstStoryID);
	}

	void getRequestStoryDetail(String myStoryId) {
		try {
			KakaoStoryService.getInstance().requestGetMyStory(kakaoStoryResponseCallback,myStoryId);
		} catch (KakaoParameterException e) {
			Dlog.e(TAG, e);
			if (listener != null)
				listener.onStoryLoadFail(IOnStoryDataLoadListener.ERR_CODE_FAILED_GET_STORY_DETAIL);
		}
	}

	/**
	 * 해당 아이디 다음 스토리 아이디를 반환한다, 없으면 null를 리터를 한다.
	 *
	 * @param storyID
	 * @return
	 */
	String getNextId(String storyID) {

		if (arrStoryDatas == null || arrStoryDatas.isEmpty())
			return null;

		if (storyID == null)
			return arrStoryDatas.get(0).id;

		String nextID = null;
		boolean isFind = false;
		for (StoryData info : arrStoryDatas) {
			if (isFind) {
				nextID = info.id;
				break;
			}

			if (info.id.equals(storyID)) {
				isFind = true;
			}
		}

		return nextID;
	}

	/**
	 * 해당 스토리에 아이디에 상세 데이터를 설정하는 함수..
	 *
	 * @param info
	 */
	void setMyStoryData(MyStoryInfo info) {

		for (StoryData storyData : arrStoryDatas) {
			if (storyData.id.equals(info.getId())) {
				// 상세정보에서만 가져올수 있는 정보를 저장을 한다...
				ArrayList<StoryLikeData> lData = new ArrayList<StoryData.StoryLikeData>();

				// 좋아요 정보...
				if (info.getLikeCount() > 0) {
					for (StoryLike like : info.getLikeList()) {
						StoryData.StoryLikeData l = new StoryLikeData();
						l.setEmoticon(like.getEmoticon().ordinal());
						l.actor = new ActorData(like.getActor().getDisplayName(), like.getActor().getProfileThumbnailUrl());
						lData.add(l);
						// 좋아요를 한 친구 이미지 저
						friendImageList.add(like.getActor().getProfileThumbnailUrl());
					}

					storyData.likeCount = lData.size();
					storyData.likes = lData;
				}

				ArrayList<StoryCommentData> cData = new ArrayList<StoryData.StoryCommentData>();

				//댓글 갯수와 댓글 데이터가 상이해서 방어코드 추가
				List<StoryComment> comments = info.getCommentList();

				// 댓글정보...
				if (comments != null && comments.size()>0) {
					int idx = 0;
					for (StoryComment comment : comments) {

						String actorName = StringUtil.CleanInvalidXmlChars(comment.getWriter().getDisplayName());
						actorName = StringUtil.CleanInvalidXmlChars(actorName);

						String commentText = StringUtil.CleanInvalidXmlChars(comment.getText());

						commentText = commentText.replace("(Sticker)", "");
						commentText = commentText.replace("(Image)", "");

						// 댓글을 한 친구 이미지 저장
						friendImageList.add(comment.getWriter().getProfileThumbnailUrl());

						// 스티커 이미지 제거후에 텍스트가 없으면 댓글 정보를 저장하지 않는다..
						if (commentText.equals(""))
							continue;

						if (++idx > commentCountLimit)
							break;

						StoryCommentData c = new StoryCommentData(commentText, new ActorData(actorName, comment.getWriter().getProfileThumbnailUrl()));
						cData.add(c);
						replyFriends.add(actorName);
					}
					storyData.commentCount = cData.size();
					storyData.comments = cData;
				}
				break;
			}
		}
	}

	/***
	 * 댓글로 사용이 가능한 댓글인지 화인하는 함수...
	 *
	 * @param data
	 * @return
	 */
	boolean isCheckEnableComment(StoryCommentData data) {
		if (data.text.contains("Sticker"))
			;
		return false;
	}

	/**
	 * 카카오 스토리를 정렬하는 함수.. 과거 => 현재
	 *
	 * @arlthor hansang-ug
	 */
	class KakaoCompare implements Comparator<StoryData> {

		@Override
		public int compare(StoryData lhs, StoryData rhs) {
			return lhs.createdAt.compareTo(rhs.createdAt);
		}
	}

	void readProfile() {
		KakaoStoryService.getInstance().requestProfile(new StoryResponseCallback<ProfileResponse>() {

			@Override
			public void onNotKakaoStoryUser() {
				if (listener != null)
					listener.onStoryLoadFail(IOnStoryDataLoadListener.ERR_CODE_FAILED_GET_USER_PROFILE);
			}

			@Override
			public void onSessionClosed(ErrorResult errorResult) {
				if (listener != null)
					listener.onStoryLoadFail(IOnStoryDataLoadListener.ERR_CODE_FAILED_GET_USER_PROFILE);
			}

			@Override
			public void onNotSignedUp() {
				if (listener != null)
					listener.onStoryLoadFail(IOnStoryDataLoadListener.ERR_CODE_FAILED_GET_USER_PROFILE);
			}

			@Override
			public void onSuccess(ProfileResponse result) {
				profileData = result.getProfile();
				if (listener != null)
					listener.onStoryProfileLoadComplete();

				getRequestStoryList("");
			}
		});
	}

	@Override
	public StoryData getStory(int index) {
		if (arrStoryDatas == null || arrStoryDatas.size() <= index || index < 0)
			return null;

		return arrStoryDatas.get(index);
	}

	@Override
	public int getStoryCount() {
		return arrStoryDatas.size();
	}

	@Override
	public String getUserName() {
		if (profileData == null)
			return "";
		return profileData.getNickName();

	}

	@Override
	public ImageInfo getUserImageUrl() {
		if (profileData == null)
			return null;

		String imgUrl = profileData.getProfileImageURL();
		String thumbUrl = profileData.getThumbnailURL();

		if (imgUrl == null || imgUrl.trim().length() < 1) {
			imgUrl = DEFAULT_PROFILE_IMG_URL;
			thumbUrl = DEFAULT_PROFILE_IMG_URL;
		}

		ImageInfo imgInfo = new ImageInfo();
		imgInfo.large = thumbUrl;
		imgInfo.medium = thumbUrl;
		imgInfo.small = thumbUrl;
		imgInfo.original = imgUrl;

		imgInfo.setOriginWidth("640");
		imgInfo.setOriginHeight("640");

		if (imgInfo.original != null) {
			if (imgInfo.original.contains("width") && imgInfo.original.contains("height")) {
				imgInfo.setOriginWidth(StringUtil.getTitleAtUrl(imgUrl, "width", true));
				imgInfo.setOriginHeight(StringUtil.getTitleAtUrl(imgUrl, "height", true));
			}
		}

		return imgInfo;
	}

	// @Override
	// public ImageInfo getUserThumbImageUrl() {
	// if (profileData == null)
	// return null;
	//
	// String imgUrl = profileData.getThumbnailURL();
	// ImageData data = null;
	//
	// if (imgUrl != null && imgUrl.length() > 0) {
	// data = StringUtil.covertUrl(imgUrl);
	// return data;
	// }
	//
	// return null;
	//
	// }

	@Override
	public ImageInfo getCoverBackgroundImage() {
		if (profileData == null)
			return null;

		String imgUrl = profileData.getBgImageURL();
		ImageInfo imgInfo = new ImageInfo();
		imgInfo.large = imgUrl;
		imgInfo.medium = imgUrl;
		imgInfo.small = imgUrl;
		imgInfo.original = imgUrl;

		imgInfo.setOriginWidth("640");
		imgInfo.setOriginHeight("640");

		if (imgInfo.original != null) {
			if (imgInfo.original.contains("width") && imgInfo.original.contains("height")) {
				imgInfo.setOriginWidth(StringUtil.getTitleAtUrl(imgUrl, "width", true));
				imgInfo.setOriginHeight(StringUtil.getTitleAtUrl(imgUrl, "height", true));
			}

		}

		return imgInfo;

	}

	@Override
	public String getStoryPeriod() { //TODO  deprecate 사용하지 마라..
		if (arrStoryDatas == null || arrStoryDatas.isEmpty())
			return null;

		if (overLimitStartDate != null && overLimitEndDate != null) {
			return overLimitStartDate + " - " + overLimitEndDate;
		} else {
			// 2014.12.12 - 2014.12.12
			StoryData fristStory = arrStoryDatas.get(0);
			StoryData lastStory = arrStoryDatas.get(arrStoryDatas.size() - 1);

			String period = "";
			try {
				String startDate = StringUtil.getDateStringByFormat(StringUtil.convertCreateStringToLong(fristStory.createdAt), "yyyy.MM.dd");
				String endDate = StringUtil.getDateStringByFormat(StringUtil.convertCreateStringToLong(lastStory.createdAt), "yyyy.MM.dd");
				period = startDate + " - " + endDate;
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
			return period;
		}
	}

	@Override
	public void setStoryPeriod(String startDate, String endDate) {
		if (endDate == null || startDate == null)
			return;
		try {
			overLimitStartDate = StringUtil.getDateStringByFormat(StringUtil.convertCreateStringToLong(startDate), "yyyy.MM.dd");
			overLimitEndDate = StringUtil.getDateStringByFormat(StringUtil.convertCreateStringToLong(endDate), "yyyy.MM.dd");
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	@Override
	public int getFeelCommentFriendCount() {
		Set<String> hashSet = new HashSet<String>();
		for (StoryData info : arrStoryDatas) {

			if (info.likes != null) {
				for (StoryLikeData l : info.likes) {
					hashSet.add(l.actor.displayName);
				}
			}

			if (info.comments != null) {
				for (StoryCommentData c : info.comments)
					hashSet.add(c.writer.displayName);
			}
		}

		return hashSet.size();
	}

	@Override
	public int getCommentFriendCount() {
		Set<String> hashSet = new HashSet<String>();
		for (StoryData info : arrStoryDatas) {
			if (info.comments != null) {
				for (StoryCommentData c : info.comments)
					hashSet.add(c.writer.displayName);
			}
		}

		return hashSet.size();

	}

	@Override
	public int getFeelTotalCount() {
		int feelCount = 0;
		for (StoryData info : arrStoryDatas) {
			if (info.likes != null)
				feelCount += info.likes.size();
		}

		return feelCount;
	}

	@Override
	public int getCommentTotalCount() {
		int commentCount = 0;
		for (StoryData info : arrStoryDatas) {
			if (info.comments != null)
				commentCount += info.comments.size();
		}
		return commentCount;
	}

	@Override
	public int getNoteStoryCount() {
		int textStory = 0;
		for (StoryData info : arrStoryDatas) {
			if (info.storyType.equals("NOTE"))
				textStory++;
		}
		return textStory;
	}

	@Override
	public int getPhotoStoryCount() {
		int photoStory = 0;
		for (StoryData info : arrStoryDatas) {
			if (info.storyType.equals("PHOTO"))
				photoStory++;
		}
		return photoStory;
	}

	@Override
	public int getPhotoNoteStoryCount() {
		int photoStory = 0;
		for (StoryData info : arrStoryDatas) {
			if (info.storyType.equals("NOTE_PHOTO"))
				photoStory++;
		}
		return photoStory;
	}

	@Override
	public Calendar getUserBirthDayCalendar() {
		if (profileData == null)
			return null;
		return profileData.getBirthdayCalendar();
	}

	@Override
	public ArrayList<StoryData> getStories() {
		return arrStoryDatas;
	}

	@Override
	public ArrayList<StoryData> getSortedStories(eSTORY_DATA_SORT_TYPE sortType) {

		if (arrStoryDatas == null || arrStoryDatas.isEmpty())
			return null;

		@SuppressWarnings("unchecked")
		ArrayList<StoryData> arrList = (ArrayList<StoryData>) arrStoryDatas.clone();

		switch (sortType) {
			case POPULAR:
				Collections.sort(arrList, comparaPopular());
				break;
			case DATE_LASTEST:
				Collections.sort(arrList, comparaLatest());
				break;
			default:
			case NORMAL:
				break;
		}

		return arrList;
	}

	private Comparator<StoryData> comparaPopular() {
		return new Comparator<StoryData>() {
			@Override
			public int compare(StoryData cur, StoryData next) {
				if (cur == null || next == null)
					return 0;
				int curDataPoint = cur.commentCount + cur.likeCount;
				int nextDataPoint = next.commentCount + next.likeCount;
				return curDataPoint > nextDataPoint ? -1 : (curDataPoint < nextDataPoint ? 1 : 0);
			}
		};
	}

	private Comparator<StoryData> comparaLatest() {
		return new Comparator<StoryData>() {
			@Override
			public int compare(StoryData cur, StoryData next) {
				if (cur == null || cur.dateInfo == null || next == null || next.dateInfo == null)
					return 0;
				long curDate = cur.dateInfo.getTime();
				long nextDate = next.dateInfo.getTime();
				return curDate > nextDate ? -1 : (curDate < nextDate ? 1 : 0);
			}
		};
	}

	/**
	 * 스토리 전체 개월수
	 * 시작월에서 끝나는 월로 구한다
	 *
	 * @return
	 */
	private int getStoriesTotalMonthCnt() {
		if (arrStoryDatas == null || arrStoryDatas.isEmpty())
			return 0;

		StoryData startData = arrStoryDatas.get(0);
		StoryData endData = arrStoryDatas.get(arrStoryDatas.size() - 1);

		Calendar sCal = StringUtil.getCalendarWidthString(startData.createdAt);
		Calendar eCal = StringUtil.getCalendarWidthString(endData.createdAt);
		//날짜를 1로 초기화 한다
		sCal.set(Calendar.DAY_OF_MONTH,1);
		eCal.set(Calendar.DAY_OF_MONTH,1);

		//sCal과 eCal 개월차를 구한다.
		return StoryBookStringUtil.getDifferentMonth(sCal, eCal);
//		eCal.
//
//		Calendar.
//
//		Set<String> monthSet = new HashSet<String>();
//		for (StoryData data : arrStoryDatas) {
//			if (data == null || data.dateInfo == null || data.dateInfo.szYear == null || data.dateInfo.szMonth == null)
//				continue;
//			String date = data.dateInfo.szYear + "_" + data.dateInfo.szMonth;
//			monthSet.add(date);
//			Logg.d("aaaa = " + date);
//		}
//
//		return monthSet.size();
	}

	private int getStoriesTotalPhotoCnt() {
		if (arrStoryDatas == null || arrStoryDatas.isEmpty())
			return 0;

		int size = 0;
		for (StoryData data : arrStoryDatas) {
			if (data == null || data.images == null)
				continue;
			size += data.images.size();
		}

		return size;
	}

	private int getStoriesReplyCnt() {
		if (arrStoryDatas == null || arrStoryDatas.isEmpty())
			return 0;

		int size = 0;
		for (StoryData data : arrStoryDatas) {
			if (data == null || data.comments == null)
				continue;
			size += data.comments.size();
		}

		return size;
	}

	private int getStoriesCnt() {
		if (arrStoryDatas == null || arrStoryDatas.isEmpty())
			return 0;
		return arrStoryDatas.size();
	}

	@Override
	public Object getStoryStatics(eSTORY_STATICS_TYPE type) {
		switch (type) {
			case MONTH_TOTAL_CNT_TYPE_INTEGER:
				return getStoriesTotalMonthCnt();
			case PHOTO_TOTAL_CNT_TYPE_INTEGER:
				return getStoriesTotalPhotoCnt();
			case STORY_TOTAL_CNT_TYPE_INTEGER:
				return getStoriesCnt();
			case REPLY_TOTAL_CNT_TYPE_INTEGER:
				return getStoriesReplyCnt();
			default:
				break;
		}
		return null;
	}

	@Override
	public StoryData getBestStoryData() {
		StoryData ret = null;
		ArrayList<StoryData> stories = getSortedStories(eSTORY_DATA_SORT_TYPE.POPULAR);
		for (StoryData d : stories) {
			if (d.images != null && d.images.size() > 0) {
				ret = d;
				break;
			}
		}

		return ret;
	}

	boolean isValidKakaoText(String text) {
		if (text == null || text.trim().length() < 1)
			return true;

		final String[] exceptionStr = {"changed background image", "changed profile photo"};

		for (String excp : exceptionStr) {
			if (text.contains(excp))
				return false;
		}

		return true;
	}

	@Override
	public void removeStories(List<String> removeList) {

		if (removeList == null || removeList.isEmpty())
			return;

		for (String key : removeList) {
			for (int ii = arrStoryDatas.size() - 1; ii >= 0; ii--) {
				StoryData data = arrStoryDatas.get(ii);

				if (data == null)
					continue;

				if (data.id != null && data.id.equals(key)) {
					arrStoryDatas.remove(data);
				}
			}
		}
	}

	@Override
	public void setStoryDataLoadListener(IOnStoryDataLoadListener ls) {
		this.listener = ls;
	}

	@Override
	public ImageInfo getImageData(String snsproperty) {
		// bgImage
		if (snsproperty.equals("main")) {
			return getCoverBackgroundImage();
		} else if (snsproperty.equals("profile")) {
			return getUserImageUrl();
		} else if (snsproperty.equals("best")) {
			// 전체 best 챕터 best
		}
		return null;
	}

	@Override
	public String getTextData(String snsproperty, String format) {
		// page snsproperty type total인경우
		if (snsproperty.equals("birth")) {
			Calendar birthDay = getUserBirthDayCalendar();
			if (birthDay != null) {
				int month = birthDay.get(Calendar.MONTH) + 1;
				int day = birthDay.get(Calendar.DAY_OF_MONTH);
				return String.format("%d월%d일생", month, day);
			} else
				return "";
		} else if (snsproperty.equals("nickname")) {
			return getUserName();
		} else if (snsproperty.equals("totalmonth")) {
			return getStoriesTotalMonthCnt() + "";
		} else if (snsproperty.equals("totalphotos")) {
			return getStoriesTotalPhotoCnt() + "";
		} else if (snsproperty.equals("totalwritingpost")) {
			return getNoteStoryCount() + "";
		} else if (snsproperty.equals("totalphotopost")) {
			return getPhotoStoryCount() + "";
		} else if (snsproperty.equals("totalwritingphotopost")) {
			return getNoteStoryCount() + getPhotoStoryCount() + "";
		} else if (snsproperty.equals("totalcomplexpost")) {
			return getPhotoNoteStoryCount() + "";
		} else if (snsproperty.equals("totalpost")) {
			return getStoriesCnt() + "";
		} else if (snsproperty.equals("totalfriends")) {
			return getCommentFriendCount() + "";
		} else if (snsproperty.equals("totalreplies")) {
			return getCommentTotalCount() + "";
		} else if (snsproperty.equals("totalfeeling")) {
			return getFeelTotalCount() + "";
		} else if (snsproperty.equals("totalfeelingreplies")) {
			return getFeelTotalCount() + getCommentTotalCount() + "";
		} else if (snsproperty.equals("year")) {

		} else if (snsproperty.equals("month")) {

		} else if (snsproperty.equals("month_pagenum")) {

		} else if (snsproperty.equals("pagenum")) {

		} else if (snsproperty.equals("postdate")) {
			return StoryBookStringUtil.covertKakaoDate(format, getBestStoryData().createdAt, null);
		} else if (snsproperty.equals("feeling")) {
			return getBestStoryData().likeCount + "";
		} else if (snsproperty.equals("replies")) {
			return getBestStoryData().commentCount + "";
		} else if (snsproperty.equals("text")) {
			return getBestStoryData().content;
		} else if (snsproperty.equals("period")) {
			String start = "";
			String end = "";
			if (arrStoryDatas.size() > 0) {
				start = arrStoryDatas.get(0).createdAt;
				end = arrStoryDatas.get(arrStoryDatas.size() - 1).createdAt;
			} else
				return null;
			return StoryBookStringUtil.covertKakaoDate(format, start, end);
		} else if (snsproperty.equals("totalphoto")) {
			int totalCnt = 0;
			for (StoryData info : arrStoryDatas) {
				if (info.images != null)
					totalCnt += info.images.size();
			}
			return totalCnt + "";
		} else if (snsproperty.equals("index_chapter")) { //indexPage는 별도로 처리
		} else if (snsproperty.equals("index_month")) {
		} else if (snsproperty.equals("index_pagenum")) {
		} else if (snsproperty.equals("index_year")) {
		} else if (snsproperty.equals("index_empty")) {
		} else if (snsproperty.equals("MONTH_PAGENUM")) {
		} else if (snsproperty.equals("photo")) {
			return getBestStoryData().images != null ? getBestStoryData().images.size() + "" : "0";
		} else if (snsproperty != null && snsproperty.length() > 0) {
		}
		return null;
	}

	@Override
	public ArrayList<String> getFrientImageList() {
		return new ArrayList<String>(friendImageList);
	}

	// 안쓰는듯.
//	@Override
//	public void adjustStory(int startIdx, int lastIdx) {
//		ArrayList<com.snaps.common.storybook.StoryData> tempArray = new ArrayList<StoryData>();
//
//		int idx = 0;
//		for (StoryData d : arrStoryDatas) {
//			if (startIdx <= idx && lastIdx >= idx) {
//				tempArray.add(d);
//				Logg.d("adjustStory add = " + idx);
//			} else
//				Logg.d("adjustStory sub = " + idx);
//
//			idx++;
//		}
//
//		arrStoryDatas = tempArray;
//	}
}