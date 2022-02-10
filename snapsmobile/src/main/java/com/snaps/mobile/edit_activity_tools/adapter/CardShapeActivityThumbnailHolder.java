package com.snaps.mobile.edit_activity_tools.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.mobile.R;

import font.FTextView;

/**
 * Created by ysjeong on 16. 5. 11..
 */
public abstract class CardShapeActivityThumbnailHolder extends RecyclerView.ViewHolder {
    protected LinearLayout rootLayout;
    protected CardShapeThumbnailChildView leftThumbnailView, rightThumbnailView;
    protected FTextView counterView;
    protected View thumbnailDivideLine;
    protected LinearLayout bottomTextLayout;
    protected FTextView bottomFrontTextView, bottomBackTextView;

    public CardShapeActivityThumbnailHolder(View itemView) {
        super(itemView);

        rootLayout = (LinearLayout) itemView.findViewById(R.id.root_layout);

        leftThumbnailView = new CardShapeThumbnailChildView();
        leftThumbnailView.setRootLayout((RelativeLayout) itemView.findViewById(R.id.root_layout_left));
        leftThumbnailView.setImgLayout((RelativeLayout) itemView.findViewById(R.id.item_lay_left));
        leftThumbnailView.setCanvasParentLy((RelativeLayout) itemView.findViewById(R.id.item_left));
        leftThumbnailView.setOutline((ImageView) itemView.findViewById(R.id.item_outline_left));
        leftThumbnailView.setWarnining((ImageView) itemView.findViewById(R.id.iv_warning_left));
        leftThumbnailView.setIntroindex((TextView) itemView.findViewById(R.id.itemintroindex_left));
        leftThumbnailView.setLeftIndex((TextView) itemView.findViewById(R.id.itemleft_left));
        leftThumbnailView.setRightIndex((TextView) itemView.findViewById(R.id.itemright_left));
        leftThumbnailView.setProgressBar((ProgressBar) itemView.findViewById(R.id.thumbanail_progress_left));

        rightThumbnailView = new CardShapeThumbnailChildView();
        rightThumbnailView.setRootLayout((RelativeLayout) itemView.findViewById(R.id.root_layout_right));
        rightThumbnailView.setImgLayout((RelativeLayout) itemView.findViewById(R.id.item_lay_right));
        rightThumbnailView.setCanvasParentLy((RelativeLayout) itemView.findViewById(R.id.item_right));
        rightThumbnailView.setOutline((ImageView) itemView.findViewById(R.id.item_outline_right));
        rightThumbnailView.setWarnining((ImageView) itemView.findViewById(R.id.iv_warning_right));
        rightThumbnailView.setIntroindex((TextView) itemView.findViewById(R.id.itemintroindex_right));
        rightThumbnailView.setLeftIndex((TextView) itemView.findViewById(R.id.itemleft_right));
        rightThumbnailView.setRightIndex((TextView) itemView.findViewById(R.id.itemright_right));
        rightThumbnailView.setProgressBar((ProgressBar) itemView.findViewById(R.id.thumbanail_progress_right));

        counterView = (FTextView) itemView.findViewById(R.id.photo_card_thumbnail_count_tv);

        thumbnailDivideLine = itemView.findViewById(R.id.photo_card_thumbnail_divide_line);

        bottomTextLayout = (LinearLayout) itemView.findViewById(R.id.new_wallet_bottomview_item_text_layout);

        bottomFrontTextView = (FTextView) itemView.findViewById(R.id.new_wallet_bottomview_item_front_tv);
        bottomBackTextView = (FTextView) itemView.findViewById(R.id.new_wallet_bottomview_item_back_tv);
    }

    public LinearLayout getRootLayout() {
        return rootLayout;
    }

    public CardShapeThumbnailChildView getLeftThumbnailView() {
        return leftThumbnailView;
    }

    public CardShapeThumbnailChildView getRightThumbnailView() {
        return rightThumbnailView;
    }

    public FTextView getCounterView() {
        return counterView;
    }

    public View getThumbnailDivideLine() {
        return thumbnailDivideLine;
    }

    public LinearLayout getBottomTextLayout() {
        return bottomTextLayout;
    }

    public FTextView getBottomFrontTextView() {
        return bottomFrontTextView;
    }

    public FTextView getBottomBackTextView() {
        return bottomBackTextView;
    }
}