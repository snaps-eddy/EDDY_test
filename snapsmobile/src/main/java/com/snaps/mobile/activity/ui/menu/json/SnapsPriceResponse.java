package com.snaps.mobile.activity.ui.menu.json;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;
import com.snaps.common.data.between.BaseResponse;

public class SnapsPriceResponse extends BaseResponse {

	private static final long serialVersionUID = -5956615762145044698L;
	@SerializedName("store_price")
	private List<SnapsPriceDetailResponse> arrPrices = null;
	
	private Map<String, SnapsPriceDetailResponse> mapPrices = null;
	
	private Map<String, SnapsPriceDetailResponse> getPriceInfo() {
		if(mapPrices != null) return mapPrices;
		
		if(arrPrices == null) return null;
		
		mapPrices = new LinkedHashMap<String, SnapsPriceDetailResponse>();
		
		for(SnapsPriceDetailResponse price : arrPrices) {
			if(price == null) continue;
			String key = price.getPriceKey();
			mapPrices.put(key, price);
		}
		return mapPrices;
	}
	
	public SnapsPriceDetailResponse getPriceInfo(String key) {
		Map<String, SnapsPriceDetailResponse> mapPrice = getPriceInfo();  
		if(mapPrice == null || !mapPrice.containsKey(key)) return null;
		
		return mapPrice.get(key);
	}
}
