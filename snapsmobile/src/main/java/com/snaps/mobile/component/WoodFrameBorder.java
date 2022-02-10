package com.snaps.mobile.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.mobile.R;

public class WoodFrameBorder extends RelativeLayout {

	ImageView iv_top, iv_left, iv_right, iv_bottom;

	public WoodFrameBorder(Context context, int resID) {
		super(context);
		init(context, resID);
	}

	void init(Context context, int layoutId) {
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(layoutId, this, false);
		iv_top = (ImageView) view.findViewById(R.id.iv_wood_top);
		iv_left = (ImageView) view.findViewById(R.id.iv_wood_left);
		iv_right = (ImageView) view.findViewById(R.id.iv_wood_right);
		iv_bottom = (ImageView) view.findViewById(R.id.iv_wood_bottom);

		addView(view);

	}

	public void adjustFrameBorder(int borderWidth) {
		iv_top.getLayoutParams().height = borderWidth;
		iv_bottom.getLayoutParams().height = borderWidth;
		iv_left.getLayoutParams().width = borderWidth;
		iv_right.getLayoutParams().width = borderWidth;
	}

	public void setBlackFrame() {
		iv_top.setImageResource(R.drawable.wood_black_h);
		iv_bottom.setImageResource(R.drawable.wood_black_h);
		iv_left.setImageResource(R.drawable.wood_black_v);
		iv_right.setImageResource(R.drawable.wood_black_v);
	}

	public void setWalnutFrame() {
		iv_top.setImageResource(R.drawable.wood_walnut_h);
		iv_bottom.setImageResource(R.drawable.wood_walnut_h);
		iv_left.setImageResource(R.drawable.wood_walnut_v);
		iv_right.setImageResource(R.drawable.wood_walnut_v);
	}
}
