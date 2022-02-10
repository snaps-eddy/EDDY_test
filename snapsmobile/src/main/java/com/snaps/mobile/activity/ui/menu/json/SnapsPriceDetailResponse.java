package com.snaps.mobile.activity.ui.menu.json;

import com.google.gson.annotations.SerializedName;
import com.snaps.common.data.between.BaseResponse;

public class SnapsPriceDetailResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3783798163098988337L;
	
	@SerializedName("PRICEKEY")
	private String priceKey;
	
	@SerializedName("ISSALE")
	private String isSale;
	
	@SerializedName("SALEPRICE")
	private String salePrice;
	
	@SerializedName("PRICE")
	private String price;
	
	@SerializedName("SALEIMG")
	private String saleImg;
	
	@SerializedName("SALEIMGISUPTO")
	private String saleImgIsUpto;

	public String getPriceKey() {
		return priceKey;
	}

	public void setPriceKey(String priceKey) {
		this.priceKey = priceKey;
	}
	
	public boolean isSale() {
		return isSale != null && isSale.equalsIgnoreCase("true");	
	}
	
	public String getIsSale() {
		return isSale;
	}

	public void setIsSale(String isSale) {
		this.isSale = isSale;
	}

	public String getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getSaleImg() {
		return saleImg;
	}

	public void setSaleImg(String saleImg) {
		this.saleImg = saleImg;
	}

	public boolean isUptoSaleImg() {
		return getSaleImgIsUpto() != null && getSaleImgIsUpto().equalsIgnoreCase("true");
	}
	
	public String getSaleImgIsUpto() {
		return saleImgIsUpto;
	}

	public void setSaleImgIsUpto(String saleImgIsUpto) {
		this.saleImgIsUpto = saleImgIsUpto;
	}
}
