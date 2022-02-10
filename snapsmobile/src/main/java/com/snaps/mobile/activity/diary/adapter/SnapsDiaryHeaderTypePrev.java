package com.snaps.mobile.activity.diary.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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
public class SnapsDiaryHeaderTypePrev extends SnapsDiaryBaseHeader implements ISnapsDiaryHeaderStrategy {
    public SnapsDiaryHeaderTypePrev(Context context, int shape, ISnapsDiaryHeaderClickListener stripListener) {
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
        if(tvSubject != null) {
            tvSubject.setTextColor(Color.argb(255, 51, 51, 51));
            tvSubject.setText(R.string.diary_mission_prev_subject);
        }

        TextView tvDesc = baseHeaderHolder.getTvDesc();
        if(tvDesc != null) {
            String desc = context.getString(R.string.diary_mission_prev_desc);
            String point = context.getString(R.string.diary_mission_prev_desc_point);

            final SpannableStringBuilder sp = new SpannableStringBuilder(desc);
            int pointStart = desc.indexOf(point);
            int pointEnd = pointStart + point.length();
            sp.setSpan(new ForegroundColorSpan(Color.argb(255, 229, 71, 54)), pointStart, pointEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvDesc.setText("");
            tvDesc.append(sp);
        }

        TextView tvSubDesc = baseHeaderHolder.getTvSubDesc();
        if(tvSubDesc != null)
            tvSubDesc.setText(R.string.diary_mission_prev_sub_desc);

        RelativeLayout lyStar = baseHeaderHolder.getStarLayout();
        if(lyStar != null)
            lyStar.setVisibility(View.GONE);

        baseHeaderHolder.getBtnGrid().setClickable(false);
        baseHeaderHolder.getBtnList().setClickable(false);
    }
}
