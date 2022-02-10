package com.snaps.common.utils.ui;

import android.graphics.PointF;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Scale 
{
	public static final float FIX_VALUE = 0.02f;
	public static final float CANVAS_SPACE = 0.08f;
	
	public static float CANVAS_SCALE = 0;
	
	public static float initScale ( float rootW , float rootH , float viewW , float viewH ) {
		
		float xScale = rootW / viewW;
        float yScale = rootH / viewH;
	        
        return Math.min(xScale, yScale);
	}
	
	public static float initScale ( View root , View container ) {
        
        float xScale = (float) root.getLayoutParams().width / container.getLayoutParams().width;
        float yScale = (float) root.getLayoutParams().height / container.getLayoutParams().height;
        
        CANVAS_SCALE = Math.min(xScale, yScale) - FIX_VALUE;
        return CANVAS_SCALE;
	}
	
	
	public static float initScale ( View root , int width , int height ) {
		
		float xScale = (float) root.getLayoutParams().width / (float) width;
        float yScale = (float) root.getLayoutParams().height / (float) height;
		
        CANVAS_SCALE = Math.min(xScale, yScale) - FIX_VALUE;
		return CANVAS_SCALE;
	}
	
	
	public static void scaleViewGroup( View root , float scale  , PointF pivot ) {
		root.setPivotX( pivot.x );
		root.setPivotY( pivot.y );
		root.setScaleX( scale );
		root.setScaleY( scale );
	}
	
    /**
     * 
     * ViewGroup안에 있는 뷰 스케일 조절
     * @param root
     * @param canary
     */
    public static void scaleViewAndChildren(View root, int canary , float scale ) {
        ViewGroup.LayoutParams layoutParams = root.getLayoutParams();

        if(layoutParams.width != ViewGroup.LayoutParams.MATCH_PARENT && layoutParams.width != ViewGroup.LayoutParams.WRAP_CONTENT) {
            layoutParams.width *= scale;
        }
        if(layoutParams.height != ViewGroup.LayoutParams.MATCH_PARENT && layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT) {
            layoutParams.height *= scale;
        }

        if(layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginParams.leftMargin *= scale;
            marginParams.topMargin *= scale;
            marginParams.rightMargin *= scale;
            marginParams.bottomMargin *= scale;
        }
        root.setLayoutParams(layoutParams);

        root.setPadding(
            (int) (root.getPaddingLeft() * scale),
            (int) (root.getPaddingTop() * scale),
            (int) (root.getPaddingRight() * scale),
            (int) (root.getPaddingBottom() * scale)
        );

        if(root instanceof TextView) {
            TextView tv = (TextView)root;
            tv.setTextSize(tv.getTextSize() * scale);
        }
        
        
        if(root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup)root;
            for(int i = 0; i < vg.getChildCount(); i++) {
                scaleViewAndChildren(vg.getChildAt(i), canary + 1 , scale );
            }
        }
    }
}