package com.snaps.mobile.activity.cartorder.adapter.viewholder;

import android.widget.ImageView;
import android.widget.TextView;

public class OrderListHolder {
	public TextView txtOrderName;
	public ImageView imgOrder;
	public TextView txtOrderCode;
	public TextView txtOrderStatus;
	public TextView txtOrderDate;
	public TextView txtOrderBtn;
	
	public OrderListHolder(TextView txtOrderName ,ImageView imgOrder, TextView txtOrderCode, TextView txtOrderStatus, TextView txtOrderDate, TextView txtOrderBtn) {
		this.txtOrderName = txtOrderName;
		this.imgOrder = imgOrder;
		this.txtOrderCode = txtOrderCode;
		this.txtOrderStatus = txtOrderStatus;
		this.txtOrderDate = txtOrderDate;
		this.txtOrderBtn = txtOrderBtn;
	}
	
}
