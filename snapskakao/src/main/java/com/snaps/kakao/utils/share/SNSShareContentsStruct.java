package com.snaps.kakao.utils.share;

import android.content.Context;

import com.snaps.common.utils.log.Dlog;

public abstract class SNSShareContentsStruct {
	private static final String TAG = SNSShareContentsStruct.class.getSimpleName();

	public static class SNSShareContents {
		
		protected Context context;
		protected String subject = "";
		protected String link = "";

		public SNSShareContents(Context context) {
			this.context = context;
		}
		
		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public Context getContext() {
			return context;
		}

		public void setContext(Context context) {
			this.context = context;
		}
	}
	
	public static class SNSShareContentsFaceBook extends SNSShareContents {
		public SNSShareContentsFaceBook(Context context) {
			super(context);
		}
		
		private String description = "";

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
	}

	public static class SNSShareContentsLine extends SNSShareContents {
		public SNSShareContentsLine(Context context) {
			super(context);
		}
	}

	public static class SNSShareContentsKakaoTalk extends SNSShareContents {
		public SNSShareContentsKakaoTalk(Context context) {
			super(context);
		}

		private String imgWidth = "";
		private String imgHeight = "";
		private String imgUrl = "";
		private String btnTitle = "";

		public String getImgWidth() {
			return imgWidth;
		}

		public void setImgWidth(String imgWidth) {
			this.imgWidth = imgWidth;
		}

		public String getImgHeight() {
			return imgHeight;
		}

		public void setImgHeight(String imgHeight) {
			this.imgHeight = imgHeight;
		}

		public String getImgUrl() {
			return imgUrl;
		}

		public void setImgUrl(String imgUrl) {
			this.imgUrl = imgUrl;
		}

		public String getBtnTitle() {
			return btnTitle;
		}

		public void setBtnTitle(String btnTitle) {
			this.btnTitle = btnTitle;
		}

		public int getImgWidthInteger() {
			try {
				return Integer.parseInt(imgWidth);
			} catch (NumberFormatException e) {
				Dlog.e(TAG, e);
			}
			return 0;
		}

		public int getImgHeightInteger() {
			try {
				return Integer.parseInt(imgHeight);
			} catch (NumberFormatException e) {
				Dlog.e(TAG, e);
			}
			return 0;
		}
	}

	public static class SNSShareContentsKakaoStory extends SNSShareContents {
		
		public enum eSnsShareKakaoStoryType {
			ATYPE("A"), //글을 수정할 수 없고, 앱 링크가 가능하며 피드를 받을 수 있는 형태
			BTYPE("B"); //글 수정 가능하고 피드 X(카카오 스토리를 호출하는 형태)
			
			String type = "";
			eSnsShareKakaoStoryType(String type) {
				this.type = type;
			}
			
			public boolean isEqualsType(String type) {
				if(type == null) return false;
				return this.type != null && this.type.equals(type.toUpperCase());
			}
		}
		
		public SNSShareContentsKakaoStory(Context context) {
			super(context);
		}

		private eSnsShareKakaoStoryType ePostType = eSnsShareKakaoStoryType.ATYPE;
		private String imgWidth = "";
		private String imgHeight = "";
		private String imgUrl = "";
		private String urlText = "";
		private String description = "";

		public eSnsShareKakaoStoryType getPostType() {
			return ePostType;
		}

		public void setPostType(eSnsShareKakaoStoryType eType) {
			this.ePostType = eType;
		}
		
		public void setPostType(String eType) {
			if(eType != null) {
				eSnsShareKakaoStoryType[] values = eSnsShareKakaoStoryType.values();
				for(eSnsShareKakaoStoryType value : values) {
					if(value.type.equals(eType)) {
						this.ePostType = value;
						break;
					}
				}
			}
		}

		public String getImgWidth() {
			return imgWidth;
		}

		public void setImgWidth(String imgWidth) {
			this.imgWidth = imgWidth;
		}

		public String getImgHeight() {
			return imgHeight;
		}

		public void setImgHeight(String imgHeight) {
			this.imgHeight = imgHeight;
		}

		public String getImgUrl() {
			return imgUrl;
		}

		public void setImgUrl(String imgUrl) {
			this.imgUrl = imgUrl;
		}

		public String getUrlText() {
			return urlText;
		}

		public void setUrlText(String urlText) {
			this.urlText = urlText;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public int getImgWidthInteger() {
			try {
				return Integer.parseInt(imgWidth);
			} catch (NumberFormatException e) {
				Dlog.e(TAG, e);
			}
			return 0;
		}

		public int getImgHeightInteger() {
			try {
				return Integer.parseInt(imgHeight);
			} catch (NumberFormatException e) {
				Dlog.e(TAG, e);
			}
			return 0;
		}
	}

	public static class SNSShareContentsBand extends SNSShareContents {
		public SNSShareContentsBand(Context context) {
			super(context);
		}

		private String domain = "";

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}
	}
}
