package com.snaps.mobile.activity.photoprint;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.snaps.common.utils.constant.Config;
import com.snaps.mobile.R;

import font.FTextView;

public class PhotoPrintProductAdapter extends ArrayAdapter<PhotoPrintProductInfo> {
	LayoutInflater mInflater = null;
	SelectPhotoPrintActivity mActivity;

	public PhotoPrintProductAdapter(SelectPhotoPrintActivity activity, ArrayList<PhotoPrintProductInfo> data) {
		super(activity, 0, data);
		mActivity = activity;

		mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {

		View view = null;

		if (v == null) {
			view = mInflater.inflate(R.layout.photoitem, null);
		} else {
			view = v;
		}

		PhotoPrintProductInfo dataInfo = getItem(position);

		ImageView thumbnailView = (ImageView) view.findViewById(R.id.iv_thumbnail);
		FTextView productName = (FTextView) view.findViewById(R.id.tv_productName);
		FTextView productSize = (FTextView) view.findViewById(R.id.tv_productSize);
		FTextView productPrice = (FTextView) view.findViewById(R.id.tv_price);
		FTextView productSellPrice = (FTextView) view.findViewById(R.id.tv_sell_price);
		ImageView popoularView = (ImageView) view.findViewById(R.id.iv_popular);

		thumbnailView.setImageResource(dataInfo.productThumbnail);
		productName.setText(dataInfo.productName);
		productName.setTextColor(Color.rgb(61, 61, 61));

		productSize.setText(dataInfo.productHeigth + " X " + dataInfo.productWidth + " cm");
		productSize.setTextColor(Color.rgb(115, 99, 87));

		productPrice.setText(dataInfo.productOrgPrice + mActivity.getResources().getString(R.string.currency));
		productPrice.setTextColor(Color.rgb(140, 140, 140));
		
				
		
		productSellPrice.setText(dataInfo.productSellPrice + mActivity.getResources().getString(R.string.currency));
		productSellPrice.setTextColor(Color.rgb(239, 65, 35));
		
		
		
		if(Config.IS_PHOTOPRINT_SELL == true)
		{
			productPrice.setPaintFlags(productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			productPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
			productSellPrice.setVisibility(View.VISIBLE);
		}else
		{
			productPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			productSellPrice.setVisibility(View.GONE);
		}

		if (dataInfo.isPopular.equals("true")) {
			popoularView.setVisibility(View.VISIBLE);
		} else {
			popoularView.setVisibility(View.INVISIBLE);
		}

		return view;

	}

}
