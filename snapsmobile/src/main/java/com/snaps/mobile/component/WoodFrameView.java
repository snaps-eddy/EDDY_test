package com.snaps.mobile.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.spc.ResourceSelector;

/**
 * Created by songhw on 2017. 5. 26..
 */

public class WoodFrameView extends RelativeLayout {
    private static final String TAG = WoodFrameView.class.getSimpleName();

    private int[] margins;
    private int width, height, frameSize;
    private String[] imageName;

    private RelativeLayout container;

    public WoodFrameView(Context context, int[] margins, int width, int height, int frameSize, String[] imageName ) {
        super(context);

        this.margins = margins;
        this.width = width;
        this.height = height;
        this.frameSize = frameSize;
        this.imageName = imageName;

        initLayout();

        setFrameImages();
    }

    public void makeWoodFrameGuide() {
        try {
            View guide = new View(getContext());
            guide.setBackgroundResource(R.drawable.hardcover_spine);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( width, height );
            int marginValue = frameSize > 0 ? frameSize/2 : 0;
            params.setMargins( marginValue, marginValue, marginValue, marginValue);
            guide.setLayoutParams(params);

            container.addView(guide);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void initLayout() {
        removeAllViews();

        container = (RelativeLayout) ( (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) ).inflate( R.layout.wood_frame, null );

        setSize( R.id.top_frame, -1, frameSize );
        setSize( R.id.left_frame, frameSize, -1 );
        setSize( R.id.right_frame, frameSize, -1 );
        setSize( R.id.bottom_frame, -1, frameSize );

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( width, height );
        params.setMargins( margins[0], margins[1], margins[2], margins[3] );
        container.setLayoutParams( params );

        addView( container );
    }

    private void setSize( int resId, int width, int height ) {
        View view = container.findViewById( resId );
        if( view != null ) {
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.width = width;
            params.height = height;
            view.setLayoutParams( params );
        }
    }

    private void setFrameImages() {
        ResourceSelector.setWoodFrames( new View[]{container.findViewById(R.id.left_frame), container.findViewById(R.id.top_frame), container.findViewById(R.id.right_frame), container.findViewById(R.id.bottom_frame)}, imageName, frameSize, width, height, margins );
    }
}
