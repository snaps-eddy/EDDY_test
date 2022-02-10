package com.snaps.common.data.between;

import com.google.gson.annotations.SerializedName;

public class BWProfileMainResponse extends BaseResponse {

	private static final long serialVersionUID = -2496733948587447568L;

	public enum BWProfileGender {
		MALE, FEMALE
	}

	@SerializedName("id")
	private String id;

	@SerializedName("profile_photo")
	private BWProfilePhotoResponse profile_photos;

	@SerializedName("nickname")
	private String nickname;

	/**
	 * MALE or FEMALE
	 */
	@SerializedName("gender")
	private String gender;
	
	public void set(BWProfileMainResponse response){
		if(response == null) return;
		
		this.id = response.id;
		
		if(response.profile_photos != null) {
			profile_photos = new BWProfilePhotoResponse();
			profile_photos.set(response.profile_photos);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public BWProfilePhotoResponse getProfile_photos() {
		return profile_photos;
	}

	public void setProfile_photos(BWProfilePhotoResponse profile_photos) {
		this.profile_photos = profile_photos;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
}
