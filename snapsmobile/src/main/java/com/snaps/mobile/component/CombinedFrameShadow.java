package com.snaps.mobile.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.spc.ResourceSelector;

/**
 * Created by songhw on 2017. 5. 26..
 */

public class CombinedFrameShadow extends RelativeLayout {
    private int contentWidth, contentHeight, pageWidth, pageHeight, shadowContentSize;
    private int[] shadowOuterSizes;

    private String[] shadowImages;

    private RelativeLayout container;

    private boolean isWoodFrameShadow = false;

    /**
     *
     * @param context
     * @param contentWidth
     * @param contentHeight
     * @param shadowOuterSizes
     * @param imageName String[]{ cornerShadowsName, leftShadowName, rightShadowName, bottomShadowName }
     */
    public CombinedFrameShadow( Context context, int contentWidth, int contentHeight, int pageWidth, int pageHeight, int shadowContentSize, int[] shadowOuterSizes, String[] imageName ) {
        super(context);

        this.contentWidth = contentWidth;
        this.contentHeight = contentHeight;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.shadowContentSize = shadowContentSize;
        this.shadowOuterSizes = shadowOuterSizes;
        this.shadowImages = imageName;

        isWoodFrameShadow = StringUtil.isEmpty( imageName[2] );

        initLayout();

        setPatternShadows();
        setCornerShadows();
    }

    private void initLayout() {
        removeAllViews();

        container = (RelativeLayout) ( (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) ).inflate( R.layout.combined_frame_shadow_layout, null );

        int width, height;
        if( isWoodFrameShadow ) {
            width = pageWidth - shadowContentSize * 2 - shadowOuterSizes[0] - shadowOuterSizes[2];
            height = pageHeight - shadowContentSize * 2 - shadowOuterSizes[1] - shadowOuterSizes[3];
        }
        else {
            if( contentWidth < contentHeight ) {
                height = pageHeight - shadowContentSize * 2 - shadowOuterSizes[1] - shadowOuterSizes[3];
                width = (int)( (float)(height + shadowContentSize * 2) / (float)contentHeight * (float)contentWidth - shadowContentSize * 2 );
            }
            else {
                width = pageWidth - shadowContentSize * 2 - shadowOuterSizes[0] - shadowOuterSizes[2];
                height = (int)( (float)(width + shadowContentSize * 2) / (float)contentWidth * (float)contentHeight - shadowContentSize * 2 );
            }
        }

        setSize( R.id.container, width, height );
        setSize( R.id.left_top_shadow, shadowContentSize + shadowOuterSizes[0], shadowContentSize + shadowOuterSizes[1] );
        setSize( R.id.top_shadow, width, (isWoodFrameShadow ? 0 : shadowContentSize) + shadowOuterSizes[1] );
        setSize( R.id.left_bottom_shadow, shadowContentSize + shadowOuterSizes[0], shadowContentSize + shadowOuterSizes[3] );
        setSize( R.id.right_top_shadow, shadowContentSize + shadowOuterSizes[2], shadowContentSize + shadowOuterSizes[1] );
        setSize( R.id.right_bottom_shadow, shadowContentSize + shadowOuterSizes[2], shadowContentSize + shadowOuterSizes[3] );
        setSize( R.id.left_shadow, shadowContentSize + shadowOuterSizes[0], height - shadowContentSize * (isWoodFrameShadow ? 1 : 0) );
        setSize( R.id.right_shadow, shadowContentSize + shadowOuterSizes[2], height - shadowContentSize * (isWoodFrameShadow ? 1 : 0) );
        setSize( R.id.bottom_shadow, width, shadowContentSize + shadowOuterSizes[3] );

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

    private void setPatternShadows() {
        View leftShadow = container.findViewById( R.id.left_shadow );
        View rightShadow = container.findViewById( R.id.right_shadow );
        View bottomShadow = container.findViewById( R.id.bottom_shadow );
        View topShadow = container.findViewById( R.id.top_shadow );

        // pattern 안주고 걍 fitXY로 맞춰야 더 잘맞는다. 괜한짓함.
        ResourceSelector.setResource( leftShadow, shadowImages[1] );
        if( !isWoodFrameShadow )
            ResourceSelector.setResource( topShadow, shadowImages[2] );
        ResourceSelector.setResource( rightShadow, shadowImages[3] );
        ResourceSelector.setResource( bottomShadow, shadowImages[4] );
    }

    private void setCornerShadows() {
        ResourceSelector.setQuadrisetedBitmaps(
                new View[]{container.findViewById(R.id.left_top_shadow), container.findViewById(R.id.right_top_shadow), container.findViewById(R.id.left_bottom_shadow), container.findViewById(R.id.right_bottom_shadow)},
                shadowImages[0] );
    }
}
