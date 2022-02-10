package com.snaps.mobile.tutorial.new_tooltip_tutorial;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.mobile.R;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;
import com.snaps.mobile.utils.custom_layouts.ZoomViewCoordInfo;

import errorhandle.logger.Logg;

import static com.snaps.mobile.tutorial.SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.ACCORDION_CARD;
import static com.snaps.mobile.tutorial.SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.ACCORDION_CARD_LANDSCAPE;
import static com.snaps.mobile.tutorial.SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOOK_ITEM;
import static com.snaps.mobile.tutorial.SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOOK_ITEM_LANDSCAPE_WITH_THUMNAILBAR;
import static com.snaps.mobile.tutorial.SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.BOTTOM_NOT_TAIL;
import static com.snaps.mobile.tutorial.SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.SMART_RECOMMEND_BOOK_MAIN_PAGE_COVER;

/**
 * Created by kimduckwon on 2017. 9. 14..
 */

public class TooltipView extends LinearLayout{
    private static final String TAG = TooltipView.class.getSimpleName();
    private TextView textViewMsg = null;
    private LinearLayout linearLayout = null;
    private View view = null;
    private ResultPositionListener resultPosition = null;
    private SnapsTutorialAttribute attribute = null;

    private TooltipView(Context context, SnapsTutorialAttribute attribute) {
        super(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.tooltip_view,this,false);
        textViewMsg = (TextView)view.findViewById(R.id.textViewMsg);
        this.attribute = attribute;
        addView(view);
    }

    public static TooltipView createTooltipView(Context context, SnapsTutorialAttribute attribute) {
        return new TooltipView(context, attribute);
    }

    public void setResultPositionListner(ResultPositionListener resultPosition) {
        this.resultPosition = resultPosition;
    }

    public void showTooltip() throws Exception {
        showTooltip(attribute.getTargetView(), attribute.getViewPosition(), attribute.getText(), attribute.getLeftMargin(), attribute.getTopMargin());
    }

    private void setType(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION type) {
        if(linearLayout != null) {
            linearLayout.setVisibility(View.GONE);
        }
        switch (type) {
            case TOP:
                linearLayout = (LinearLayout)view.findViewById(R.id.pointTop);
                break;

            case CENTER:
                linearLayout = (LinearLayout)view.findViewById(R.id.pointBottom);
                break;

            case BOTTOM:
            case BOTTOM_NOT_TAIL:
                linearLayout = (LinearLayout)view.findViewById(R.id.pointBottom);
                break;

            case TOAST_POSITION:
                linearLayout = null;
                break;
            case BOOK_ITEM_LANDSCAPE_WITH_THUMNAILBAR:
            case BOOK_ITEM:
            case SMART_RECOMMEND_BOOK_MAIN_PAGE_COVER:
                linearLayout = (LinearLayout)view.findViewById(R.id.pointTop);
                break;

            case ACCORDION_CARD:
            case ACCORDION_CARD_LANDSCAPE:
                linearLayout = (LinearLayout)view.findViewById(R.id.pointBottom);
                break;
        }

        if(linearLayout != null) {
            if (type == BOTTOM_NOT_TAIL) {
                linearLayout.setVisibility(View.INVISIBLE);
            } else {
                linearLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setText(String msg) {
        textViewMsg.setText(msg);
    }

    private void setMovePoint(int offsetLeft,int offsetRight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)linearLayout.getLayoutParams();
        params.leftMargin = offsetLeft;
        params.rightMargin = offsetRight;
        linearLayout.setLayoutParams(params);
    }

    private void showTooltip(View targetView, SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION type, String msg, int leftMargin, int topMargin) throws Exception {
        setType(type);
        setText(msg);

        Rect viewRect = new Rect();

        targetView.getGlobalVisibleRect(viewRect);
        Dlog.d("showTooltip() resulttootip : " + viewRect.left + ", " + viewRect.top);
        boolean shouldSkipConvert = attribute != null && attribute.isForceSetTargetView();
        if(!shouldSkipConvert
                && (type == SMART_RECOMMEND_BOOK_MAIN_PAGE_COVER || type == BOOK_ITEM || type == BOOK_ITEM_LANDSCAPE_WITH_THUMNAILBAR || type == ACCORDION_CARD || type == ACCORDION_CARD_LANDSCAPE)) {
            DataTransManager transMan = DataTransManager.getInstance();
            if(transMan != null) {
                ZoomViewCoordInfo coordInfo = transMan.getZoomViewCoordInfo();
                boolean isLandscape = type != SMART_RECOMMEND_BOOK_MAIN_PAGE_COVER && type != BOOK_ITEM && type != ACCORDION_CARD;
                viewRect = coordInfo.covertItemRectForTutorialTooltip(targetView, isLandscape, SMART_RECOMMEND_BOOK_MAIN_PAGE_COVER == type);
            }
            Dlog.d("showTooltip() resulttootipInvers : " + viewRect.left + ", " + viewRect.top);
        }
        measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int targetViewWidth = viewRect.right - viewRect.left;
        int targetViewHeight = viewRect.bottom - viewRect.top;

        int tooltipWidth = getMeasuredWidth();
        int tooltipHeight = getMeasuredHeight();


        int x = ((viewRect.left) + targetViewWidth / 2) - (tooltipWidth / 2);
        x += leftMargin;

        int y = 0;
        switch(type){
            case TOP:
                y = (viewRect.top) + targetViewHeight;
                break;
            case CENTER:
                y = viewRect.top;
                break;
            case BOTTOM:
            case BOTTOM_NOT_TAIL:
                y = (viewRect.bottom) - (targetViewHeight + tooltipHeight);
                break;
            case TOAST_POSITION:
                y = (viewRect.bottom) - (113 + tooltipHeight);
                break;
            case BOOK_ITEM:
            case SMART_RECOMMEND_BOOK_MAIN_PAGE_COVER:
                y = ((viewRect.top) + targetViewHeight/2);
                break;
            case BOOK_ITEM_LANDSCAPE_WITH_THUMNAILBAR:
                y = ((viewRect.top) + targetViewHeight/2);
                break;
            case ACCORDION_CARD:
            case ACCORDION_CARD_LANDSCAPE:
                y = ((viewRect.top) - tooltipHeight);
                break;
        }

        int rightMoveMargin = 0;
        int leftMoveMargin = 0;
        if(x < 0) {
            rightMoveMargin = Math.abs(x);
            x = 0;
        }else if(x + tooltipWidth > screenWidth) {
            leftMoveMargin = ((x + tooltipWidth) - screenWidth);
            x = x - ((x + tooltipWidth) - screenWidth);
        }else {

        }

        setMovePoint(leftMoveMargin, rightMoveMargin);
        if(topMargin != 0) {
            y += topMargin;
        }
        resultPosition.result(x,y);
    }

    public interface ResultPositionListener {
        void result(int x,int y);
    }


}
