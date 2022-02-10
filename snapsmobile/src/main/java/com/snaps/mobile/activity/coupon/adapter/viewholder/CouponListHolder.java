package com.snaps.mobile.activity.coupon.adapter.viewholder;

import android.widget.ImageView;
import android.widget.TextView;

public class CouponListHolder {
	public ImageView imgCheck;
	public TextView uptxtCouponKind;
	public TextView uptxtCoupon;
	public TextView downtxtCoupon;
	
	public CouponListHolder( ImageView imgCheck, TextView uptxtkind , TextView uptxt, TextView downtxt ) {
		this.imgCheck = imgCheck;
		this.uptxtCouponKind = uptxtkind;
		this.uptxtCoupon = uptxt;
		this.downtxtCoupon = downtxt;
	}
}
