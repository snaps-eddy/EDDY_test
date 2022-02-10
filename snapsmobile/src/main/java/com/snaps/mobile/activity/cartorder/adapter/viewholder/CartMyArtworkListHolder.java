package com.snaps.mobile.activity.cartorder.adapter.viewholder;

import android.widget.ImageView;
import android.widget.TextView;

public class CartMyArtworkListHolder {
	public ImageView imgCartSelect;
	public TextView btnCartDelete;
	public ImageView imgCartProduct;
	public TextView txtCartName;
	public TextView txtCartType;
	public TextView txtCartAmount;
	public TextView txtCartSellAmount;
	public TextView PhotoCountChange;
	
	public CartMyArtworkListHolder(ImageView imgCartSelect, TextView btnCartDelete, ImageView imgCartProduct, TextView txtCartName, TextView txtCartType, TextView txtCartAmount, TextView txtCartSellAmount,TextView photochange) {
		this.imgCartSelect = imgCartSelect;
		this.btnCartDelete = btnCartDelete;
		this.imgCartProduct = imgCartProduct;
		this.txtCartName = txtCartName;
		this.txtCartType = txtCartType;
		this.txtCartAmount = txtCartAmount;
		this.txtCartSellAmount = txtCartSellAmount;
		this.PhotoCountChange = photochange;
	}
}
