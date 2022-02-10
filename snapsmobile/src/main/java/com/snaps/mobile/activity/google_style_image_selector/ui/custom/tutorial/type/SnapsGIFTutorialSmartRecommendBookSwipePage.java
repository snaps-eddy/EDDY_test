package com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.type;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.GIFTutorialView;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.interfacies.SnapsGIFTutorialListener;

public class SnapsGIFTutorialSmartRecommendBookSwipePage extends SnapsGIFTutorialBase {
    public SnapsGIFTutorialSmartRecommendBookSwipePage(ViewGroup parent, GIFTutorialView.Builder attribute, SnapsGIFTutorialListener gifTutorialListener) {
        super(parent, attribute, gifTutorialListener);
    }

    @Override
    public void initializeWithBuilder(GIFTutorialView.Builder builder) {
		LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = layoutInflater.inflate(R.layout.smart_recommend_book_swipe_page_tutorial, getParent());

		View parentLayout = view.findViewById(R.id.smart_recommend_book_swipe_page_tutorial_parent_ly);
		if (parentLayout != null) {
			parentLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				    if (getGifTutorialListener() != null)
                        getGifTutorialListener().closeTutorial();
				}
			});
		}

		ImageView ivGIF = (ImageView) view.findViewById(R.id.smart_recommend_book_swipe_page_tutorial_gif_iv);
		if (ivGIF != null) {
            if (getGifTutorialListener() != null)
                getGifTutorialListener().startAnimation(ivGIF);
		}

		TextView tvGif = (TextView) view.findViewById(R.id.smart_recommend_book_swipe_page_tutorial_desc_tv);
		if (tvGif != null) {
			if (!StringUtil.isEmpty(builder.getTitle())) {
				tvGif.setText(builder.getTitle());
			}
		}
    }
}
