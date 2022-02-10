package com.snaps.mobile.edit_activity_tools.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.spc.SnapsPageCanvas;
import com.snaps.mobile.R;

/**
 * Created by ysjeong on 16. 5. 11..
 */
public class EditActivityThumbnailHolder extends RecyclerView.ViewHolder {
    RelativeLayout rootLayout;
    RelativeLayout imgLayout;
    RelativeLayout canvasParentLy;
    SnapsPageCanvas canvas;
    ImageView outline;
    ImageView warnining;
    TextView introindex;
    TextView leftIndex;
    TextView rightIndex;

    ProgressBar progressBar;

    public EditActivityThumbnailHolder(View itemView) {
        super(itemView);
        rootLayout = (RelativeLayout) itemView.findViewById(R.id.root_layout);
        imgLayout = (RelativeLayout) itemView.findViewById(R.id.item_lay);
        canvasParentLy = (RelativeLayout) itemView.findViewById(R.id.item);
        outline = (ImageView) itemView.findViewById(R.id.item_outline);
        warnining = (ImageView) itemView.findViewById(R.id.iv_warning);
        introindex = (TextView) itemView.findViewById(R.id.itemintroindex);
        leftIndex = (TextView) itemView.findViewById(R.id.itemleft);
        rightIndex = (TextView) itemView.findViewById(R.id.itemright);
        progressBar = (ProgressBar) itemView.findViewById(R.id.thumbanail_progress);
    }

    public SnapsPageCanvas getCanvas() {
        return canvas;
    }
}