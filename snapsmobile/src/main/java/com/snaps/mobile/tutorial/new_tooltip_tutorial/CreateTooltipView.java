package com.snaps.mobile.tutorial.new_tooltip_tutorial;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;

import java.util.Timer;



/**
 * Created by kimduckwon on 2017. 9. 26..
 */

public class CreateTooltipView {
    private static final String TAG = CreateTooltipView.class.getSimpleName();

    private Activity activity = null;
    private Window window = null;
    private Handler handler = null;
    private TooltipView tooltipView = null;

    public CreateTooltipView(Activity activity, Context context, final SnapsTutorialAttribute attribute){
        this.activity = activity;
        tooltipView = TooltipView.createTooltipView(context, attribute);
        tooltipView.setResultPositionListner(new TooltipView.ResultPositionListener() {
            @Override
            public void result(int x, int y) {
                if( attribute.getShowResultListener() != null) {
                    if(attribute.getShowResultListener().result()) {
                        showTooltip(x, y);
                    }

                } else {
                    showTooltip(x, y);
                }
            }
        });
        showTooltip();
    }

    public CreateTooltipView(Window window, Context context, SnapsTutorialAttribute attribute){
        this.window = window;
        tooltipView = TooltipView.createTooltipView(context, attribute);
        tooltipView.setResultPositionListner(new TooltipView.ResultPositionListener() {
            @Override
            public void result(int x, int y) {
                showTooltip(x,y);
            }
        });
        showTooltip();
    }

    private void showTooltip() {
        try {
            tooltipView.showTooltip();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static CreateTooltipView createTooltipView(Activity activity, Context context, SnapsTutorialAttribute attribute) {
        return new CreateTooltipView(activity,context,attribute);
    }

    public static CreateTooltipView createTooltipView(Window window, Context context, SnapsTutorialAttribute attribute) {
        return new CreateTooltipView(window,context,attribute);
    }

    private void showTooltip(int x, int y){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = y;
        layoutParams.leftMargin = x;
        if(activity == null){
            window.addContentView(tooltipView,layoutParams);
        }else{
            activity.addContentView(tooltipView,layoutParams);
        }
        startTimer();
    }

    private void startTimer() {
        if(handler !=null){
            handler = null;
        }
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tooltipView.setVisibility(View.INVISIBLE);
            }
        },8 * 1000);
    }

    public void clearTooltipView() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                tooltipView.setVisibility(View.INVISIBLE);
            }
        });
    }
}
