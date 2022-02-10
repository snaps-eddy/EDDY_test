package com.snaps.common.data.img;

import com.snaps.common.utils.ui.IAlbumData;

/***
 * 카카오,페이스북, 인스타,구글포토에서 같이 사용을 하기 위해 만들었다...
 * 
 * @author yeonsungbae
 *
 */
public class MyNetworkAlbumData implements IAlbumData {
	public String USER_ID = "";
	public String ALBUM_ID = "";
	public String THUMBNAIL_IMAGE_URL = "";
	public String ALBUM_NAME = "";
	public String PHOTO_CNT = "";

	@Override
	public String getUserId() {
		return USER_ID;
	}

	@Override
	public String getAlbumId() {
		return ALBUM_ID;
	}

	@Override
	public String getAlbumUrl() {
		return ALBUM_ID;
	}

	@Override
	public String getAlbumName() {
		return ALBUM_NAME;
	}

	@Override
	public String getAlbumThumnbail() {
		return THUMBNAIL_IMAGE_URL;
	}

	@Override
	public String getPhotoCnt(){
		return PHOTO_CNT;
	}

	
}
