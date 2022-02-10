package com.snaps.common.data.between;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;


public class BWPhotoDataResponse extends BaseResponse {
	
	private static final long serialVersionUID = -2496733948587447568L;
	
	@SerializedName("id")
	private String id;
	
	@SerializedName("from")
	private String from;

	@SerializedName("created_time")
	private long created_time;
	
	@SerializedName("date")
	private String date;
	
	@SerializedName("comment_count")
	private int comment_count;
	
	@SerializedName("like")
	private BWPhotoLikesResponse likes;
	
	@SerializedName("images")
	private List<BWPhotoImagesResponse> images;
	
	private int idx;
	
	public void set(BWPhotoDataResponse photoDatas) {
		if(photoDatas == null) return;
		
		id = photoDatas.id;
		from = photoDatas.from;
		created_time = photoDatas.created_time;
		date = photoDatas.date;
		comment_count = photoDatas.comment_count;
		
		if(photoDatas.likes != null) {
			likes = new BWPhotoLikesResponse();
			likes.set(photoDatas.likes);
		}
		
		if(photoDatas.images != null) {
			images = new ArrayList<BWPhotoImagesResponse>();
			for(BWPhotoImagesResponse img : photoDatas.images) {
				images.add(img);
			}
		}
		
		idx = photoDatas.idx;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public long getCreated_time() {
		return created_time;
	}

	public void setCreated_time(long created_time) {
		this.created_time = created_time;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getComment_count() {
		return comment_count;
	}

	public void setComment_count(int comment_count) {
		this.comment_count = comment_count;
	}

	public BWPhotoLikesResponse getLikes() {
		return likes;
	}

	public void setLikes(BWPhotoLikesResponse likes) {
		this.likes = likes;
	}

	public List<BWPhotoImagesResponse> getImages() {
		return images;
	}

	public void setImages(List<BWPhotoImagesResponse> images) {
		this.images = images;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}
}
