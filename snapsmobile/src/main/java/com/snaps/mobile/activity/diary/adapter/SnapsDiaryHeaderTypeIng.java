package com.snaps.mobile.activity.diary.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.SnapsDiaryDataManager;
import com.snaps.mobile.activity.diary.customview.SnapsMapStyleResourceImageView;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryHeaderClickListener;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryHeaderStrategy;
import com.snaps.mobile.activity.diary.recoder.SnapsDiaryUserInfo;

/**
 * Created by ysjeong on 16. 3. 31..
 */
public class SnapsDiaryHeaderTypeIng extends SnapsDiaryBaseHeader implements ISnapsDiaryHeaderStrategy {
    SnapsMapStyleResourceImageView ivInk;
    public SnapsDiaryHeaderTypeIng(Context context, int shape, ISnapsDiaryHeaderClickListener stripListener) {
        super(context, shape, stripListener);
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snaps_diary_mission_state_bar_base, parent, false);
        return new SnapsBaseHeaderHolder.ImgTypeHeaderHolder(view, parent);
    }

    @Override
    public void setHeaderInfo(final RecyclerView.ViewHolder holder) {
        super.setHeaderInfo(holder);
        if(holder == null || !(holder instanceof SnapsBaseHeaderHolder.ImgTypeHeaderHolder)) return;
        SnapsBaseHeaderHolder.ImgTypeHeaderHolder imgHeaderHolder = (SnapsBaseHeaderHolder.ImgTypeHeaderHolder) holder;

        TextView tvMissionState = imgHeaderHolder.getTvMissionState();
        if(tvMissionState != null) {
            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            SnapsDiaryUserInfo userInfo = dataManager.getSnapsDiaryUserInfo();
            if(userInfo != null && userInfo.getRemainDays() != null && userInfo.getRemainDays().length() > 0) {
                String d_day = String.format("D - %s", userInfo.getRemainDays());
                tvMissionState.setText(d_day);
            }
        }

        ivInk = imgHeaderHolder.getIvInk();
        if (ivInk != null) {
            SnapsDiaryDataManager dataManager = SnapsDiaryDataManager.getInstance();
            SnapsDiaryUserInfo userInfo = dataManager.getSnapsDiaryUserInfo();
            if (userInfo != null) {
                int inkCount = userInfo.getCurrentInkCount();
                ivInk.setCroppedImage(inkCount);
            }
        }

        imgHeaderHolder.getBtnGrid().setClickable(true);
        imgHeaderHolder.getBtnList().setClickable(true);
    }

    @Override
    public void destoryView() {
        if(ivInk != null)
            ivInk.releaseBitmap();
    }
}
