package com.snaps.mobile.activity.google_style_image_selector.interfaces;

import com.snaps.common.utils.ui.IAlbumData;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2016. 12. 7..
 */

public interface IImageSelectAlbumHandler {
	void setBaseAlbumIfExistAlbumList(ArrayList<IAlbumData> list); //기본으로 선택할 앨범을 설정해 준다..

	void loadImageIfExistCreateAlbumList(IImageSelectGetAlbumListListener albumListListener); //앨범이 존재한다면 앨범부터 부르고 없으면 그냥 이미지 로딩

	IAlbumData getCurrentAlbumCursor();

	boolean isExistAlbumList();
}
