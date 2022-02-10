package com.snaps.common.customui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ResizableImageView extends ImageView {

    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override 
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
         Drawable d = getDrawable();

         if(d!=null){
                 // ceil not round - avoid thin vertical gaps along the left/right edges
        	 
        	 int width =0;
             int height = 0;
        	 
        	 if(d.getMinimumHeight() >= MeasureSpec.getSize(heightMeasureSpec) )
        	 {
        		 height = MeasureSpec.getSize(heightMeasureSpec);
        		 width = (int) Math.ceil((float) height * (float) (float) d.getIntrinsicWidth()  / d.getIntrinsicHeight());
        		 
        	 }else
        	 {
        		 width = MeasureSpec.getSize(widthMeasureSpec);
        		 height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
        	 }
        	 
        	 
        	 
        	 
                
                 
                 
                 setMeasuredDimension(width, height);
         }else{
                 super.onMeasure(widthMeasureSpec, heightMeasureSpec);
         }
    }
    
}