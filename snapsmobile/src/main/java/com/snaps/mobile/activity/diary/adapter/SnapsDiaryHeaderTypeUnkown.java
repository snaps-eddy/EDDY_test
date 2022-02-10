package com.snaps.mobile.activity.diary.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryHeaderClickListener;
import com.snaps.mobile.activity.diary.interfaces.ISnapsDiaryHeaderStrategy;

/**
 * Created by ysjeong on 16. 3. 31..
 */
public class SnapsDiaryHeaderTypeUnkown extends SnapsDiaryBaseHeader implements ISnapsDiaryHeaderStrategy {
    public SnapsDiaryHeaderTypeUnkown(Context context, int shape, ISnapsDiaryHeaderClickListener stripListener) {
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
            tvSubject.setText("");

        TextView tvDesc = baseHeaderHolder.getTvDesc();
        if(tvDesc != null)
            tvDesc.setText("");

        TextView tvSubDesc = baseHeaderHolder.getTvSubDesc();
        if(tvSubDesc != null)
            tvSubDesc.setText("");

        RelativeLayout lyStar = baseHeaderHolder.getStarLayout();
        if(lyStar != null)
            lyStar.setVisibility(View.GONE);

        baseHeaderHolder.getBtnGrid().setClickable(false);
        baseHeaderHolder.getBtnList().setClickable(false);
    }
}
