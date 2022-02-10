package com.snaps.mobile.activity.selectimage.fragment;

import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.snaps.mobile.R;
import com.snaps.mobile.activity.selectimage.adapter.viewholder.ImageDetailHolder;


/**
 * Created by ifunbae on 2016. 12. 15..
 */

public class DetailFragment extends Fragment{
    //사진선택시 깜밖이는걸 없애기위해 선언
    protected ImageDetailHolder oldImageDetailHolder = null;


    public void setOldImageDetailHolder(ImageDetailHolder oldImageDetailHolder) {
        this.oldImageDetailHolder = oldImageDetailHolder;
    }

    protected void unCheckImageDetailHolder() {
        if (oldImageDetailHolder != null) {
            if (oldImageDetailHolder.imgChoiceBgWhite != null)
                oldImageDetailHolder.imgChoiceBgWhite.setVisibility(View.GONE);
            oldImageDetailHolder.imgChoiceBg.setVisibility(View.GONE);
        }
    }


    protected void oneNotifyDataUnCheck(int position, GridView gridView) {
        if (position < 0)
            return;
        int firstVisiblePosition = gridView.getFirstVisiblePosition();

        for (int k = 0; k < gridView.getChildCount(); k++) {
            int current = firstVisiblePosition + k;

            if (current == position) {
                View view = gridView.getChildAt(k);

                if (view != null) {
                    ImageView imgChoiceBg = (ImageView) view.findViewById(R.id.imgChoiceBg);
                    ImageView imgChoiceBgWhite = (ImageView) view.findViewById(R.id.imgChoiceBgWhite);
                    imgChoiceBg.setVisibility(View.GONE);
                    imgChoiceBgWhite.setVisibility(View.GONE);
                }

                break;
            }
        }
    }
}
