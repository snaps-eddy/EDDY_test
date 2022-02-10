package com.snaps.mobile.order;

import android.graphics.Bitmap;

public interface ISnapsOrderEditActivityTemplate {

	public void thumbImageUpload();
	
	public void verifyProjectCode(String projectCode, final boolean isEditMode);

	public void setPageFileOutput(final int index);

	public void setPageBitmap(int pageIdx, Bitmap pageBitmap);

	public void setPageThumbnailFail(final int pageIdx);

	public void setPageThumbnail(final int pageIdx, String filePath);
}
