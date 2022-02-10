package com.snaps.mobile.activity.diary.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryHeaderStrategy;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryHeaderClickListener;

/**
 * Created by ysjeong on 16. 3. 31..
 */
public class SnapsDiaryHeaderTypeSuccess extends SnapsDiaryBaseHeader implements ISnapsDiaryHeaderStrategy {
    public SnapsDiaryHeaderTypeSuccess(Context context, int shape, ISnapsDiaryHeaderClickListener stripListener) {
        super(context, shape, stripListener);
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snaps_diary_mission_state_bar_base, parent, false);
        return new SnapsBaseHeaderHolder.TextTypeHeaderHolder(view, parent);
    }

    @Override
    public void setHeaderInfo(final RecyclerView.ViewHolder holder) {
        super.setHeaderInfo(holder);
        if(holder == null || !(holder instanceof SnapsBaseHeaderHolder.TextTypeHeaderHolder)) return;
        SnapsBaseHeaderHolder.TextTypeHeaderHolder baseHeaderHolder = (SnapsBaseHeaderHolder.TextTypeHeaderHolder) holder;

        TextView tvSubject = baseHeaderHolder.getTvSubject();
        if(tvSubject != null)
            tvSubject.setTextColor(Color.argb(255, 229, 71, 54));
            tvSubject.setText(R.string.diary_mission_success_subject);

        TextView tvDesc = baseHeaderHolder.getTvDesc();
        if(tvDesc != null)
            tvDesc.setText(R.string.diary_mission_success_desc);

        TextView tvSubDesc = baseHeaderHolder.getTvSubDesc();
        if(tvSubDesc != null)
            tvSubDesc.setText(R.string.diary_mission_success_sub_desc);

        RelativeLayout lyStar = baseHeaderHolder.getStarLayout();
        if(lyStar != null)
            lyStar.setVisibility(View.VISIBLE);

        baseHeaderHolder.getBtnGrid().setClickable(true);
        baseHeaderHolder.getBtnList().setClickable(true);
    }
}
