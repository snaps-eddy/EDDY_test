package com.snaps.mobile.activity.themebook.adapter;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.data.model.SnapsCommonResultListener;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.xml.bean.XML_BasePage;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SmartRecommendBookEditorBottomLayoutListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final String TAG = SmartRecommendBookEditorBottomLayoutListAdapter.class.getSimpleName();

	private static final float ASPECT_RATIO_OF_PHOTO_BOOK_LAYOUT = 0.492f; //애니 북의 가로 : 세로 비율

	private ArrayList<XML_BasePage> layoutList;

	private boolean isLandscapeMode = false;
	private SnapsCommonResultListener<XML_BasePage> itemClickListener = null;
	private Activity activity = null;

	public SmartRecommendBookEditorBottomLayoutListAdapter(Activity activity, boolean isLandscapeMode) {
		this.activity = activity;
		initLayoutItemList();

		setLandscapeMode(isLandscapeMode);
	}

	public void setItemClickListener(SnapsCommonResultListener<XML_BasePage> itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	public void setLandscapeMode(boolean landscapeMode) {
		isLandscapeMode = landscapeMode;
	}

	public void setData(ArrayList<XML_BasePage> newList) {
		if (newList == null) return;

		initLayoutItemList();

        notifyData(newList);
	}

	public void notifyData(ArrayList<XML_BasePage> newList) {
		if (newList == null) return;
        layoutList = newList;

        notifyDataSetChanged();
    }

	private RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.smart_snaps_analysis_edit_bottom_layout_fragment_item, parent, false);

		RelativeLayout parentView = (RelativeLayout) view.findViewById(R.id.layoutImgFrame);
        ViewGroup.LayoutParams lp = parentView.getLayoutParams();
		lp.width = UIUtil.convertDPtoPX(activity, 138);
		lp.height = (int) (lp.width * ASPECT_RATIO_OF_PHOTO_BOOK_LAYOUT);

		parentView.setLayoutParams(lp);

		return new LayoutDesignItemHolder(view);
	}

	@Override
	public void onViewRecycled(RecyclerView.ViewHolder holder) {
		super.onViewRecycled(holder);

		if (holder == null || !(holder instanceof LayoutDesignItemHolder)) return;

		LayoutDesignItemHolder photoHolder = (LayoutDesignItemHolder) holder;

		if (photoHolder.getIvCoverThumbnail() != null) {
			ImageLoader.clear(activity, photoHolder.getIvCoverThumbnail());
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return getItemViewHolder(parent);
	}

	public ArrayList<XML_BasePage> getPhotoItemList() {
		return layoutList;
	}

	public XML_BasePage getItem(int position) {
		return getPhotoItemList().get(position);
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
		if (holder == null || !(holder instanceof LayoutDesignItemHolder) || getItemCount() <= position) return;

		final LayoutDesignItemHolder photoHolder = (LayoutDesignItemHolder) holder;

		final XML_BasePage coverData = getItem(position);
		if (coverData == null) return;

		View parentView = photoHolder.getLyParent();
		if (parentView != null) {
			parentView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (itemClickListener != null) {
						itemClickListener.onResult(coverData);
					}
				}
			});
		}

		if (coverData.F_IS_SELECT) {
			photoHolder.getIvOutLine().setBackgroundResource(R.drawable.image_border_change_design_item_select);
		} else {
			photoHolder.getIvOutLine().setBackgroundResource(0);
		}

		if (coverData.F_IS_BASE_MULTIFORM) {
			photoHolder.getBaseLabel().setVisibility(View.VISIBLE);
		} else {
			photoHolder.getBaseLabel().setVisibility(View.GONE);
		}

		String thumbUrl =  SnapsAPI.DOMAIN(false) + coverData.F_SSMPL_URL;

		ImageView ivThumbnail = photoHolder.getIvCoverThumbnail();
		if (ivThumbnail != null) {
			ivThumbnail.setImageBitmap(null);
			int emptyImageRes = ivThumbnail.getDrawable() != null ? R.drawable.color_drawable_eeeeee : 0;
			try {
				ImageLoader.with( activity ).load( thumbUrl ).placeholder( emptyImageRes ).into( photoHolder.getIvCoverThumbnail() );
			} catch (OutOfMemoryError e) {
				Dlog.e(TAG, e);
			}
		}
	}

	protected XML_BasePage getPhotoItem(int pos) {
		if(layoutList == null || layoutList.size() <= pos) return null;
		return layoutList.get(pos);
	}

	@Override
	public int getItemCount() {
		return layoutList != null ? layoutList.size() : 0;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void clear() {
		int size = layoutList.size();
		layoutList.clear();
		notifyItemRangeRemoved(0, size);
	}

	public void addAll(XML_BasePage[] contentses) {
		int startIndex = layoutList.size();
		layoutList.addAll(startIndex, Arrays.asList(contentses));
		notifyItemRangeInserted(startIndex, contentses.length);
	}

	public void addAll(List<XML_BasePage> contentses) {
		int startIndex = layoutList.size();
		layoutList.addAll(startIndex, contentses);
		notifyItemRangeInserted(startIndex, contentses.size());
	}

	private void initLayoutItemList() {
		if (layoutList != null)
			layoutList.clear();
		else
			layoutList = new ArrayList<>();
	}

	public static class LayoutDesignItemHolder extends RecyclerView.ViewHolder {
		private ImageView ivCoverThumbnail;
		private ImageView ivCheckBox;
		private ImageView ivOutLine;
		private RelativeLayout lyParent;
		private View baseLabel;

		LayoutDesignItemHolder(View itemView) {
			super(itemView);
			this.ivCoverThumbnail = (ImageView) itemView.findViewById(R.id.imgCoverAlbum);
			this.ivCheckBox = (ImageView) itemView.findViewById(R.id.img_select);
			this.ivOutLine = (ImageView) itemView.findViewById(R.id.img_out_line);
			this.lyParent = (RelativeLayout) itemView.findViewById(R.id.select_layout);
			this.baseLabel = itemView.findViewById(R.id.base_label);
		}

		public View getBaseLabel() {
			return baseLabel;
		}

		public ImageView getIvCoverThumbnail() {
			return ivCoverThumbnail;
		}

		public ImageView getIvCheckBox() {
			return ivCheckBox;
		}

		public ImageView getIvOutLine() {
			return ivOutLine;
		}

		public RelativeLayout getLyParent() {
			return lyParent;
		}
	}
}