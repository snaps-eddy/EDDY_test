package com.snaps.mobile.activity.ui.menu.json;

import android.content.Context;

import com.snaps.common.data.between.BaseResponse;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.system.SystemUtil;
import com.snaps.common.utils.ui.StringUtil;

public class SnapsStoreProductResponse extends BaseResponse {
	private static final String TAG = SnapsStoreProductResponse.class.getSimpleName();

	private static final long serialVersionUID = 3833645865598906672L;
	
	private String F_CLSS_CODE = "";
	
	private String name;
	
	private String subName;
	
	private String priceKey;
	
	private String saleImg;
	
	private String imgUrl;
	
	private String nextPageUrl;

	private String infoUrl;
	
	private String itemOrder;
	
	private String clickable;
	
	private String useYorn;
	
	private SnapsStoreProductEmergency emergency;
	
	private SnapsPriceDetailResponse priceInfo;
	
	public String getUseYorn() {
		return useYorn;
	}

	public void setUseYorn(String useYorn) {
		this.useYorn = useYorn;
	}

	public boolean isUse() {
		return getUseYorn() != null && (getUseYorn().equalsIgnoreCase("c") || getUseYorn().equalsIgnoreCase("y"));
	}
	
	public boolean isCommingSoon() {
		return getUseYorn() != null && getUseYorn().equalsIgnoreCase("c");
	}

	public SnapsPriceDetailResponse getPriceInfo() {
		return priceInfo;
	}

	public void setPriceInfo(SnapsPriceDetailResponse priceInfo) {
		this.priceInfo = priceInfo;
	}

	public boolean isClickable() {
		if(clickable != null && clickable.equalsIgnoreCase("false") || isCommingSoon()) return false;
		return true;
	}
	
	public int getOrder() {
		if(getItemOrder() != null && getItemOrder().length() > 0) {
			try {
				return Integer.parseInt(getItemOrder());
			} catch (NumberFormatException e) {
				Dlog.e(TAG, e);
			}
		}
		return 0;
	}
	
	public String getF_CLSS_CODE() {
		return F_CLSS_CODE;
	}

	public void setF_CLSS_CODE(String f_CLSS_CODE) {
		F_CLSS_CODE = f_CLSS_CODE;
	}

	public String getClickable() {
		return clickable;
	}

	public void setClickable(String clickable) {
		this.clickable = clickable;
	}

	public SnapsStoreProductEmergency getEmergency() {
		return emergency;
	}

	public void setEmergency(SnapsStoreProductEmergency emergency) {
		this.emergency = emergency;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubName() {
		return subName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}

	public String getPriceKey() {
		return priceKey;
	}

	public void setPriceKey(String priceKey) {
		this.priceKey = priceKey;
	}

	public String getSaleImg() {
		return saleImg;
	}

	public void setSaleImg(String saleImg) {
		this.saleImg = saleImg;
	}

	public String getImgAssetFileName() {
		return getF_CLSS_CODE() + getItemOrder() + ".png";
	}
	
	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getNextPageUrl() {
		return nextPageUrl;
	}

	public void setNextPageUrl(String nextPageUrl) {
		this.nextPageUrl = nextPageUrl;
	}

	public String getInfoUrl() {
		return infoUrl;
	}

	public void setInfoUrl(String infoUrl) {
		this.infoUrl = infoUrl;
	}

	public String getItemOrder() {
		return itemOrder;
	}

	public void setItemOrder(String itemOrder) {
		this.itemOrder = itemOrder;
	}
	
	
	public static class SnapsStoreProductEmergency {
		private String use;
		private String title;
		private String msg;
		private String version;
		
		public boolean isEmergency(Context context) {
			if(use != null && use.equalsIgnoreCase("true")) {
				int verCheck = StringUtil.compareVersion(SystemUtil.getAppVersion(context), getVersion());
				return verCheck < 1;
			}
			return false;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getVersion() {
			return version;
		}
		public void setVersion(String version) {
			this.version = version;
		}
		public String getUse() {
			return use;
		}
		public void setUse(String use) {
			this.use = use;
		}
		public String getMsg() {
			return msg;
		}
		public void setMsg(String msg) {
			this.msg = msg;
		}
	}
}
