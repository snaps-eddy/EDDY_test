package com.snaps.mobile.activity.edit.fragment.canvas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.structure.control.SnapsLayoutControl;
import com.snaps.common.structure.page.SnapsPage;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.edit.spc.SnapsCanvasFactory;

import io.reactivex.plugins.RxJavaPlugins;

public abstract class BaseSloganCanvasFragment extends ThemeBookCanvasFragment {

    private static final String TAG = BaseSloganCanvasFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (viewPager == null) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_pagecanvas2, container, false);
            canvas = new SnapsCanvasFactory().createPageCanvas(getActivity(), Config.getPROD_CODE());
            canvas.setId(R.id.fragment_root_view_id);
            rootView.addView(canvas);
            return rootView;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void imageRange(SnapsPage page, int index) {
        try {
            for (int i = 0; i < page.getLayoutList().size(); i++) {
                SnapsLayoutControl layout = (SnapsLayoutControl) page.getLayoutList().get(i);
                MyPhotoSelectImageData imgData = layout.imgData;
                if (imgData != null)
                    layout.angle = imgData.ROTATE_ANGLE + "";
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}
