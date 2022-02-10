package com.snaps.common.data.img;

import java.io.Serializable;
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class MyFacebookData implements Parcelable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3332922784970574405L;
	// fb 글정보
	public String postDate = "";
	public String postLikes = "";
	public String postMsg = "";
	public ArrayList<MyFBReply> replyData = new ArrayList<MyFacebookData.MyFBReply>();

	public MyFacebookData(){}
	
	@SuppressWarnings("unchecked")
	public void set(MyFacebookData fbData) {
		if(fbData == null) return;
		
		this.postDate = fbData.postDate;
		this.postLikes = fbData.postLikes;
		this.postMsg = fbData.postMsg;
		
		if(fbData.replyData != null && !fbData.replyData.isEmpty()) {
			this.replyData = (ArrayList<MyFBReply>) fbData.replyData.clone();
		}
	}
	
	public MyFacebookData(Parcel in) {
		readFromParcel(in);
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(postDate);
		dest.writeString(postLikes);
		dest.writeString(postMsg);
		dest.writeTypedList(replyData);
	}
	@SuppressWarnings("unchecked")
	void readFromParcel(Parcel in) {// !! Parcel 객체에 write 하는 순서는 read 하는 순서와 같아야 함. !!
		postDate = in.readString();
		postLikes = in.readString();
		postMsg = in.readString();
		in.readTypedList(replyData, MyFBReply.CREATOR);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public MyFacebookData createFromParcel(Parcel in) {
			return new MyFacebookData(in);
		}
		public MyFacebookData[] newArray(int size) {
			return new MyFacebookData[size];
		}
	};

	public static class MyFBReply implements Parcelable, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5698154002046484181L;
		public String replyProfileImg = "";
		public String replyFBName = "";
		public String replyMsg = "";
		public String replyDate = "";
		public String replyLikes = "";

		public MyFBReply() {}
		public MyFBReply(Parcel in) {
			readFromParcel(in);
		}
		void readFromParcel(Parcel in) {// !! Parcel 객체에 write 하는 순서는 read 하는 순서와 같아야 함. !!
			replyProfileImg = in.readString();
			replyFBName = in.readString();
			replyMsg = in.readString();
			replyDate = in.readString();
			replyLikes = in.readString();
		}
		@Override
		public int describeContents() {
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(replyProfileImg);
			dest.writeString(replyFBName);
			dest.writeString(replyMsg);
			dest.writeString(replyDate);
			dest.writeString(replyLikes);
		}
		
		@SuppressWarnings("rawtypes")
		public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
			public MyFBReply createFromParcel(Parcel in) {
				return new MyFBReply(in);
			}
			public MyFBReply[] newArray(int size) {
				return new MyFBReply[size];
			}
		};

		@Override
		public String toString() {
			return "MyFBReply [replyProfileImg=" + replyProfileImg + ", replyFBName=" + replyFBName + ", replyMsg=" + replyMsg + ", replyDate=" + replyDate + ", replyLikes=" + replyLikes + "]";
		}
	}

	@Override
	public String toString() {
		return "MyFacebookData [postDate=" + postDate + ", postLikes=" + postLikes + ", postMsg=" + postMsg + ", replyData=" + replyData + "]";
	}
}
