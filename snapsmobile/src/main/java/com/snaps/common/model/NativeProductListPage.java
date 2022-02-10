package com.snaps.common.model;

import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductDesignCategory;
import com.snaps.mobile.product_native_ui.json.list.SnapsProductSizeList;

public class NativeProductListPage {

	private SnapsProductDesignCategory designCategory = null;
	private SnapsProductSizeList sizeList = null;

	private boolean isProductSizeType = false;
	private boolean isBadgeExist = false;
	private String title = null;

	public NativeProductListPage(boolean isProductSizeType, String title, boolean isBadgeExist) {
		set(isProductSizeType, title, isBadgeExist);
	}

	public void set(boolean isProductSizeType, String title, boolean isBadgeExist) {
		this.title = title;
		this.isBadgeExist = isBadgeExist;
		this.isProductSizeType = isProductSizeType;
		if (isProductSizeType) {
			this.sizeList = new SnapsProductSizeList();

		} else {
			this.designCategory = new SnapsProductDesignCategory();
			this.designCategory.setCATEGORY_NAME(title);
			this.designCategory.setNEW(isBadgeExist);
		}
	}

	public void set(NativeProductListPage page) {
		set(page.isProductSizeType, page.getTitle(), page.isBadgeExist);
	}

	public NativeProductListPage(SnapsProductDesignCategory category) {
		this.designCategory = category;
		this.isProductSizeType = false;
	}

	public NativeProductListPage(SnapsProductSizeList category) {
		this.sizeList = category;
		this.isProductSizeType = true;
	}

	public SnapsProductNativeUIBaseResultJson getProductList() {
		return isProductSizeType ? sizeList : designCategory;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isBadgeExist() {
		return isBadgeExist;
	}

	public void setIsBadgeExist(boolean isBadgeExist) {
		this.isBadgeExist = isBadgeExist;
	}

	public void setDesignCategory(SnapsProductDesignCategory designCategory) {
		this.designCategory = designCategory;
	}

	public SnapsProductDesignCategory getDesignCategory() {
		return designCategory;
	}

	public boolean isProductSizeType() {
		return isProductSizeType;
	}
}
