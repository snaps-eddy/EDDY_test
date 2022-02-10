package com.snaps.mobile.activity.diary.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.mobile.R;
import com.snaps.mobile.activity.diary.customview.SnapsMapStyleResourceImageView;

/**
 * Created by ysjeong on 16. 4. 6..
 */
public abstract class SnapsBaseHeaderHolder extends RecyclerView.ViewHolder {
    private LinearLayout parentLy;
    private ImageView ivThumbnail;
    private ImageView ivThumbnailMask;
    private TextView tvUserName;

    private ImageButton btnList;
    private ImageButton btnGrid;

    public LinearLayout getParentLy() {
        return parentLy;
    }

    public ImageView getIvThumbnail() {
        return ivThumbnail;
    }

    public ImageView getIvThumbnailMask() {
        return ivThumbnailMask;
    }

    public TextView getTvUserName() {
        return tvUserName;
    }

    public ImageButton getBtnList() {
        return btnList;
    }

    public ImageButton getBtnGrid() {
        return btnGrid;
    }

    public SnapsBaseHeaderHolder(View itemView) {
        super(itemView);
        parentLy = (LinearLayout) itemView.findViewById(R.id.snaps_diary_mission_sate_bar_layout);
        ivThumbnail = (ImageView) itemView.findViewById(R.id.snaps_diary_mission_sate_bar_thumbnail_iv);
        ivThumbnailMask = (ImageView) itemView.findViewById(R.id.snaps_diary_mission_sate_bar_thumbnail_mask_iv);
        tvUserName = (TextView) itemView.findViewById(R.id.snaps_diary_mission_name_tv);
        btnList = (ImageButton) itemView.findViewById(R.id.snaps_diary_mission_sate_bar_strip_list_btn);
        btnGrid = (ImageButton) itemView.findViewById(R.id.snaps_diary_mission_sate_bar_strip_grid_btn);
    }

    public static class TextTypeHeaderHolder extends SnapsBaseHeaderHolder {

        private TextView tvSubject;
        private TextView tvDesc;
        private TextView tvSubDesc;
        private RelativeLayout lyStar;

        public TextView getTvSubject() {
            return tvSubject;
        }

        public TextView getTvDesc() {
            return tvDesc;
        }

        public TextView getTvSubDesc() {
            return tvSubDesc;
        }

        public RelativeLayout getStarLayout() {
            return lyStar;
        }

        public TextTypeHeaderHolder(View itemView, ViewGroup parent) {
            super(itemView);
            FrameLayout contents = (FrameLayout) itemView.findViewById(R.id.snaps_diary_mission_state_layout);
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snaps_diary_mission_state_bar_type_text, parent, false);
            tvSubject = (TextView) view.findViewById(R.id.snaps_diary_mission_text_subject_tv);
            tvDesc = (TextView) view.findViewById(R.id.snaps_diary_mission_text_desc_tv);
            tvSubDesc = (TextView) view.findViewById(R.id.snaps_diary_mission_text_sub_desc_tv);
            lyStar = (RelativeLayout) view.findViewById(R.id.snaps_diary_mission_sate_bar_star_layout);
            contents.addView(view);
        }
    }

    public static class ImgTypeHeaderHolder extends SnapsBaseHeaderHolder {

        private TextView tvMissionState;
        private SnapsMapStyleResourceImageView ivInk;

        public TextView getTvMissionState() {
            return tvMissionState;
        }

        public SnapsMapStyleResourceImageView getIvInk() {
            return ivInk;
        }

        public ImgTypeHeaderHolder(View itemView, ViewGroup parent) {
            super(itemView);
            FrameLayout contents = (FrameLayout) itemView.findViewById(R.id.snaps_diary_mission_state_layout);
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snaps_diary_mission_state_bar_type_ink, parent, false);
            tvMissionState = (TextView) view.findViewById(R.id.snaps_diary_mission_state_tv);
            ivInk = (SnapsMapStyleResourceImageView) view.findViewById(R.id.snaps_diary_mission_ink_iv);
            contents.addView(view);
        }
    }
}