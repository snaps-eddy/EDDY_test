package com.snaps.mobile.activity.cartorder.adapter.viewholder;

import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.utils.net.xml.bean.Xml_MyArtwork.MyArtworkData;
import com.snaps.mobile.activity.cartorder.photocount.EditLayout;

public class CartListHolder {
	public ImageView imgCartSelect;
	public ImageView imgCartProduct;
	public TextView txtCartName;
	public TextView txtCartType;
	public EditLayout editCartCount;
	public TextView txtCartAmount;
	public TextView txtCartSellAmount;
	public ImageView btnCartDelete;
	public TextView txtDiscountAmount;
	public TextView btnCoupon;
	public boolean isPhotoPrint;
	
	public MyArtworkData cartData;
	
	public CartListHolder(ImageView imgCartSelect, ImageView imgCartProduct, TextView txtCartName, TextView txtCartType, EditLayout editCartCount, 
			TextView txtCartAmount, TextView txtCartSellAmount,ImageView btnCartDelete, TextView txtDiscountAmount, TextView btnCoupon , boolean isphotoprint) {
		this.imgCartSelect = imgCartSelect;
		this.imgCartProduct = imgCartProduct;
		this.txtCartName = txtCartName;
		this.txtCartType = txtCartType;
		this.editCartCount = editCartCount;
		this.txtCartAmount = txtCartAmount;
		this.txtCartSellAmount = txtCartSellAmount;
		this.btnCartDelete = btnCartDelete;
		this.txtDiscountAmount = txtDiscountAmount;
		this.btnCoupon = btnCoupon;
		this.isPhotoPrint = isphotoprint;
	}
}
