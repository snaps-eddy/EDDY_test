package com.snaps.mobile.activity.photoprint;


import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.JsonObject;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;

/***
 * 사진인화 사이즈별 상품 정보
 * 
 * @author jines100
 * 
 */
public class PhotoPrintProductInfo implements Parcelable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String productName; // 상품명
	public String productOrgPrice; // 상품 가격
	public String productSellPrice; // 상품 가격
	public String productCode; // 상품 코드
	public String productSize; // 상품 코드
    public String productCM; // 상품 코드
	public int productThumbnail; // 상품 썸네일 패스...

	public float productWidth; // 상품 넓이
	public float productHeigth; // 상품 높이

	public float productLimitWidth; // 상품 넓이
	public float productLimitHeigth; // 상품 높이

	public String isPopular; // 인기상품 여부

	public PhotoPrintProductInfo() {

	}

    public PhotoPrintProductInfo( String productCode, JsonObject jsonObject) {
        this.productCode = productCode;
        this.productName = jsonObject.has( "F_PROD_NAME" ) ? jsonObject.get( "F_PROD_NAME" ).getAsString() : "";
        this.productOrgPrice = jsonObject.has( "F_ORG_PRICE" ) ? jsonObject.get( "F_ORG_PRICE" ).getAsString() : "";
        this.productSellPrice = jsonObject.has( "F_SELL_PRICE" ) ? jsonObject.get( "F_SELL_PRICE" ).getAsString() : "";
        this.productSize = jsonObject.has( "F_PROD_SIZE" ) ? jsonObject.get( "F_PROD_SIZE" ).getAsString() : "";
        this.productCM = jsonObject.has( "F_PROD_CM" ) ? jsonObject.get( "F_PROD_CM" ).getAsString() : "";
        this.productThumbnail = 0; // 안쓰는 값인듯.

        this.isPopular = jsonObject.has( "F_FAVORITE" ) ? jsonObject.get( "F_FAVORITE" ).getAsString() : "";

        float a, b;
        a = jsonObject.has( "F_PROD_CM_WIDTH" ) ? jsonObject.get( "F_PROD_CM_WIDTH" ).getAsFloat() : 0;
        b = jsonObject.has( "F_PROD_CM_HEIGHT" ) ? jsonObject.get( "F_PROD_CM_HEIGHT" ).getAsFloat() : 0;
        this.productWidth = Math.max( a, b );
        this.productHeigth = Math.min( a, b );

        a = jsonObject.has( "F_PROD_LIMIT_WIDTH" ) ? jsonObject.get( "F_PROD_LIMIT_WIDTH" ).getAsFloat() : 0;
        b = jsonObject.has( "F_PROD_LIMIT_HEIGHT" ) ? jsonObject.get( "F_PROD_LIMIT_HEIGHT" ).getAsFloat() : 0;
        this.productLimitWidth = Math.max( a, b );
        this.productLimitHeigth = Math.min( a, b );
    }

	public PhotoPrintProductInfo(String productName, String productOrgPrice, String productSellPrice, String productCode, String productSize, int productThumbnail, float productWidth,
			float productHeigth, float productlimitWidth, float productlimitHeigth, String isPopular) {
		this.productName = productName;
		this.productOrgPrice = productOrgPrice;
		this.productSellPrice = productSellPrice;
		this.productCode = productCode;
		this.productSize = productSize;
		this.productThumbnail = productThumbnail;
		this.productWidth = productWidth;
		this.productHeigth = productHeigth;
		this.productLimitWidth = productlimitWidth;
		this.productLimitHeigth = productlimitHeigth;
		this.isPopular = isPopular;
	}

	public PhotoPrintProductInfo(Parcel in) {
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(productName);
		dest.writeString(productOrgPrice);
		dest.writeString(productSellPrice);
		dest.writeString(productCode);
		dest.writeString(productSize);
		dest.writeInt(productThumbnail);

		dest.writeFloat(productWidth);
		dest.writeFloat(productHeigth);

		dest.writeFloat(productLimitWidth);
		dest.writeFloat(productLimitHeigth);

		dest.writeString(isPopular);
	}

	void readFromParcel(Parcel in) {
		productName = in.readString();
		productOrgPrice = in.readString();
		productSellPrice = in.readString();
		productCode = in.readString();
		productSize = in.readString();
		productThumbnail = in.readInt();

		productWidth = in.readFloat();
		productHeigth = in.readFloat();

		productLimitWidth = in.readFloat();
		productLimitHeigth = in.readFloat();

		isPopular = in.readString();
	}

	public static final Parcelable.Creator<PhotoPrintProductInfo> CREATOR = new Parcelable.Creator<PhotoPrintProductInfo>() {

		@Override
		public PhotoPrintProductInfo createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new PhotoPrintProductInfo(source);
		}

		@Override
		public PhotoPrintProductInfo[] newArray(int size) {
			// TODO Auto-generated method stub
			return new PhotoPrintProductInfo[size];
		}
	};

	public float getWRatio() {
		return productWidth / productHeigth;
	}

	public float getHRatio() {
		return productHeigth / productWidth;
	}

	static public PhotoPrintProductInfo getPhotoPrintTemplate(Activity activity, String productCode) {
        ArrayList<PhotoPrintProductInfo> datas = getPhotoPrintTemplate( activity );
		for (PhotoPrintProductInfo item : datas) {
			if (productCode.equals(item.productCode)) {
				return item;
			}
		}

		return null;
	}

	/***
	 * 사진인화 해상도 정보를 가져오는 함수..
	 * 
	 * @return
	 */
	static public ArrayList<PhotoPrintProductInfo> getPhotoPrintTemplate(Activity activity) {
        return MenuDataManager.getInstance().getMenuData().photoPrintProductInfoArray;
	}
}
