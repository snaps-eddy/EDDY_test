package com.snaps.mobile.activity.edit.fragment.canvas;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.mobile.activity.edit.spc.NewPhoneCaseCanvas;

public class NewPhoneCaseCanvasFragment extends BaseSimpleCanvasFragment {

    @Override
    protected SnapsPageCanvas provideCanvasView(Boolean isCartThumbnail) {
        return new NewPhoneCaseCanvas(getActivity(), isCartThumbnail);
    }
}
