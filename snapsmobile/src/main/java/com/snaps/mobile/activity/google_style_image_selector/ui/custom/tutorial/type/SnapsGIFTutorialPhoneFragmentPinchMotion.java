package com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.type;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.GIFTutorialView;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.tutorial.interfacies.SnapsGIFTutorialListener;

public class SnapsGIFTutorialPhoneFragmentPinchMotion extends SnapsGIFTutorialBase {
    public SnapsGIFTutorialPhoneFragmentPinchMotion(ViewGroup parent, GIFTutorialView.Builder attribute, SnapsGIFTutorialListener gifTutorialListener) {
        super(parent, attribute, gifTutorialListener);
    }

    @Override
    public void initializeWithBuilder(GIFTutorialView.Builder builder) {
		LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = layoutInflater.inflate(R.layout.img_sel_phone_frg_tutorial, getParent());

		View parentLayout = view.findViewById(R.id.img_sel_frg_tutorial_parent_layout);
		if (parentLayout != null) {
			parentLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
				    if (getGifTutorialListener() != null)
                        getGifTutorialListener().closeTutorial();
				}
			});
		}

		TextView tvAlbumDesc = (TextView) view.findViewById(R.id.img_sel_frg_tutorial_album_desc_tv);
		if (tvAlbumDesc != null) {
			if (builder.getTutorialType() == ISnapsImageSelectConstants.eTUTORIAL_TYPE.PHONE_FRAGMENT_PINCH_MOTION) {
				tvAlbumDesc.setVisibility(View.VISIBLE);
			}
		}

		ImageView ivGIF = (ImageView) view.findViewById(R.id.img_sel_frg_tutorial_gif_iv);
		if (ivGIF != null) {
            if (getGifTutorialListener() != null)
                getGifTutorialListener().startAnimation(ivGIF);
		}

		TextView tvGif = (TextView) view.findViewById(R.id.img_sel_frg_tutorial_title_tv);
		if (tvGif != null) {
			if (!StringUtil.isEmpty(builder.getTitle())) {
				tvGif.setText(builder.getTitle());
			}
		}
    }
}
