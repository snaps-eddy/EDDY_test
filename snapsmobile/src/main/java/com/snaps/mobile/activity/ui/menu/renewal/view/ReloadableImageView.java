package com.snaps.mobile.activity.ui.menu.renewal.view;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.snaps.common.utils.imageloader.ImageLoader;

/**
 * Created by songhw on 2016. 12. 29..
 */

public class ReloadableImageView extends ImageView {
    private String path;
    private boolean isCleared;

    public ReloadableImageView(Context context) {
        super(context);
    }

    public ReloadableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReloadableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPath( String path ) {
        isCleared = false;
        this.path = path;
    }

    public void reload() {
        isCleared = false;

        if( getContext() instanceof Activity ) {
            Activity activity = (Activity) getContext();
            if( activity.isFinishing() || (Build.VERSION.SDK_INT >= 17 && activity.isDestroyed()) )
                return;
        }
        ImageLoader.with( getContext() ).load( path ).into( this );
    }

    public void clear() {
        isCleared = true;
        ImageLoader.clear( getContext(), this );
    }
}
