package com.snaps.mobile.order.order_v2.datas;

import android.app.Activity;
import androidx.fragment.app.Fragment;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.SnapsTemplate;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.mobile.activity.card.SnapsTextOptions;
import com.snaps.mobile.activity.edit.pager.BaseSnapsPagerController;

import java.util.ArrayList;

/**
 * Created by ysjeong on 2017. 3. 28..
 */

public class SnapsOrderAttribute {
	private Activity activity;
	private SnapsTemplate snapsTemplate;
	private ArrayList<SnapsPage> pageList;
	private ArrayList<SnapsPage> backPageList;
	private ArrayList<SnapsPage> hiddenPageList;
	private BaseSnapsPagerController pagerController;
	private ArrayList<Fragment> canvasList;
	private SnapsTextOptions textOptions;

	private boolean isEditMode;

	private SnapsOrderAttribute(Builder builder) {
		this.activity = builder.activity;
		this.snapsTemplate = builder.snapsTemplate;
		this.pageList = builder.pageList;
		this.backPageList = builder.backPageList;
		this.hiddenPageList = builder.hiddenPageList;
		this.pagerController = builder.pagerController;
		this.canvasList = builder.canvasList;
		this.textOptions = builder.textOptions;
		this.isEditMode = builder.isEditMode;
	}

	public boolean isEditMode() {
		return isEditMode;
	}

	public void setIsEditMode(boolean isEditMode) {
		this.isEditMode = isEditMode;
	}

	public Activity getActivity() {
		return activity;
	}

	public SnapsTemplate getSnapsTemplate() {
		return snapsTemplate;
	}

	public ArrayList<SnapsPage> getPageList() {
		return pageList;
	}

	public ArrayList<SnapsPage> getBackPageList() {
		return backPageList;
	}

	public ArrayList<SnapsPage> getHiddenPageList() {
		return hiddenPageList;
	}

	public BaseSnapsPagerController getPagerController() {
		return pagerController;
	}

	public ArrayList<Fragment> getCanvasList() {
		return canvasList;
	}

	public SnapsTextOptions getTextOptions() {
		return textOptions;
	}

	public static class Builder {
		private Activity activity;
		private SnapsTemplate snapsTemplate;
		private ArrayList<SnapsPage> pageList;
		private ArrayList<SnapsPage> backPageList;
		private ArrayList<SnapsPage> hiddenPageList;
		private ArrayList<MyPhotoSelectImageData> imageList;
		private BaseSnapsPagerController pagerController;
		private ArrayList<Fragment> canvasList;
		private SnapsTextOptions textOptions;

		private boolean isEditMode;

		public Builder setImageList(ArrayList<MyPhotoSelectImageData> imageList) {
			this.imageList = imageList;
			return this;
		}

		public Builder setEditMode(boolean editMode) {
			isEditMode = editMode;
			return this;
		}

		public Builder setActivity(Activity activity) {
			this.activity = activity;
			return this;
		}

		public Builder setSnapsTemplate(SnapsTemplate snapsTemplate) {
			this.snapsTemplate = snapsTemplate;
			return this;
		}

		public Builder setPageList(ArrayList<SnapsPage> pageList) {
			this.pageList = pageList;
			return this;
		}

		public Builder setBackPageList(ArrayList<SnapsPage> backPageList) {
			this.backPageList = backPageList;
			return this;
		}

		public Builder setHiddenPageList(ArrayList<SnapsPage> hiddenPageList) {
			this.hiddenPageList = hiddenPageList;
			return this;
		}

		public Builder setPagerController(BaseSnapsPagerController pagerController) {
			this.pagerController = pagerController;
			return this;
		}

		public Builder setCanvasList(ArrayList<Fragment> canvasList) {
			this.canvasList = canvasList;
			return this;
		}

		public Builder setTextOptions(SnapsTextOptions textOptions) {
			this.textOptions = textOptions;
			return this;
		}

		public SnapsOrderAttribute create() {
			return new SnapsOrderAttribute(this);
		}
	}
}
