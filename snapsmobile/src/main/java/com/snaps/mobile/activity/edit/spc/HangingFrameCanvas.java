package com.snaps.mobile.activity.edit.spc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.mobile.R;

/**
 * Created by ifunbae on 2016. 9. 23..
 */

public class HangingFrameCanvas extends SnapsPageCanvas {

    public HangingFrameCanvas(Context context) {
        super(context);
    }

    @Override
    protected void loadShadowLayer() {
    }

    @Override
    protected void loadPageLayer() {
    }

    @Override
    protected void loadBonusLayer() {
        bonusLayer.addView(makeFrame());
    }

    @Override
    protected void initMargin() {
        leftMargin = 10;//UIUtil.convertPXtoDP(getContext(),10);
        topMargin = 50;//UIUtil.convertPXtoDP(getContext(),42);
        rightMargin = 10;//UIUtil.convertPXtoDP(getContext(),10);
        bottomMargin = 4;//UIUtil.convertPXtoDP(getContext(),42);
    }

    @Override
    public void onDestroyCanvas() {
        if(shadowLayer != null) {
            Drawable d = shadowLayer.getBackground();
            if (d != null) {
                try {
                    d.setCallback(null);
                } catch (Exception ignore) {
                }
            }
        }
        super.onDestroyCanvas();
    }


    /***
     * 행잉액자 프레임을 만드는 함수.
     * @return
     */
    View makeFrame(){
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.hangingframe, this, false);

        //프레임 아이디에 따라 프레임이 바뀐다
        String frameID = getSnapsPage().info.F_FRAME_ID;

        int topframeID =0;
        int bottomframeID =0;
        if (frameID.equals(Const_PRODUCT.PRODUCT_HANGING_NATURAL)) {
            topframeID = R.drawable.hangingframe_natural_top;
            bottomframeID = R.drawable.hangingframe_natural_boottom;
        } else if (frameID.equals(Const_PRODUCT.PRODUCT_HANGING_BLACK)) {
            topframeID = R.drawable.hangingframe_black_top;
            bottomframeID = R.drawable.hangingframe_black_bottom;
        } else if (frameID.equals(Const_PRODUCT.PRODUCT_HANGING_WALNUT)) {
            topframeID = R.drawable.hangingframe_walnut_top;
            bottomframeID = R.drawable.hangingframe_walnut_bottom;
        }

        view.findViewById(R.id.topFrame).setBackgroundResource(topframeID);
        view.findViewById(R.id.bottomFrame).setBackgroundResource(bottomframeID);

        return view;
    }
}
