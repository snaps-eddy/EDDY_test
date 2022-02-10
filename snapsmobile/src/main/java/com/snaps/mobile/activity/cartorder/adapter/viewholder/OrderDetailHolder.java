package com.snaps.mobile.activity.cartorder.adapter.viewholder;

import android.widget.ImageView;
import android.widget.TextView;

public class OrderDetailHolder {
	public ImageView imgOrder;
	public TextView imgProdSelect;
	public TextView txtOrderName;
	public TextView txtProdName;
	public TextView txtOrderBill;
	public ImageView imgOrderArrow;
	
	public OrderDetailHolder(ImageView imgOrder,TextView imgProdSelect, TextView txtOrderName, TextView txtProdName, TextView txtOrderBill , ImageView imgOrderArrow) {
		this.imgOrder = imgOrder;
		this.imgProdSelect = imgProdSelect;
		this.txtOrderName = txtOrderName;
		this.txtProdName = txtProdName;
		this.txtOrderBill = txtOrderBill;
		this.imgOrderArrow = imgOrderArrow;
	}
}
