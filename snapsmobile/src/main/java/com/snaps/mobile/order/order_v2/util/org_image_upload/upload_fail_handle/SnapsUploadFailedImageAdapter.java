package com.snaps.mobile.order.order_v2.util.org_image_upload.upload_fail_handle;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.data.img.MyPhotoSelectImageData;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.ui.adapters.ImageSelectAdapterHolders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SnapsUploadFailedImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final String TAG = SnapsUploadFailedImageAdapter.class.getSimpleName();
	private Context context;
	private ArrayList<MyPhotoSelectImageData> arrImageList;

	private boolean isLandscapeMode = false;
	private int holderDimens = 0;

	public SnapsUploadFailedImageAdapter(Context context, boolean isLandscapeMode) {
		this.context = context;

		initImageList();

		setLandscapeMode(isLandscapeMode);
	}

	public void setLandscapeMode(boolean landscapeMode) {
		isLandscapeMode = landscapeMode;
	}

	public void setData(ArrayList<MyPhotoSelectImageData> newList) {
		if (newList == null) return;

		initImageList();

		arrImageList = (ArrayList<MyPhotoSelectImageData>) newList.clone();

		notifyDataSetChanged();
	}

	private RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_failed_image_holder_item, parent, false);

		int columnCount = (isLandscapeMode ? Const_VALUE.IMAGE_GRID_COLS_LANDSCAPE : Const_VALUE.IMAGE_GRID_COLS);

		holderDimens = isLandscapeMode ? UIUtil.getScreenHeight(context) : UIUtil.getScreenWidth(context);
		holderDimens -= parent.getContext().getResources().getDimension(R.dimen.upload_failed_image_adapter_side_margin);
		holderDimens /= columnCount;

		RelativeLayout parentView = (RelativeLayout) view.findViewById(R.id.upload_failed_image_holder_parent);
		GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) parentView.getLayoutParams();
		lp.width = holderDimens;
		lp.height = holderDimens;
		parentView.setLayoutParams(lp);

		return new UploadFailedImageHolder(view);
	}

	@Override
	public void onViewRecycled(RecyclerView.ViewHolder holder) {
		super.onViewRecycled(holder);

		if (holder == null || !(holder instanceof ImageSelectAdapterHolders.PhotoFragmentItemHolder)) return;

		ImageSelectAdapterHolders.PhotoFragmentItemHolder photoHolder = (ImageSelectAdapterHolders.PhotoFragmentItemHolder) holder;

		if (photoHolder.getThumbnail() != null) {
			ImageLoader.clear(context, photoHolder.getThumbnail());
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return getItemViewHolder(parent);
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
		if (holder == null || getItemCount() <= position) return;

		final UploadFailedImageHolder photoHolder = (UploadFailedImageHolder) holder;

		MyPhotoSelectImageData photoSelectImageData = getPhotoItem(position);

		ImageView ivThumbnail = photoHolder.getThumbnail();
		if (ivThumbnail != null) {
			ivThumbnail.setImageBitmap(null);
			int emptyImageRes = ivThumbnail.getDrawable() != null ? R.drawable.color_drawable_eeeeee : 0;
			try {
				String imagePath = ImageUtil.getImagePath(context, photoSelectImageData);
				ImageLoader.with( context ).load( imagePath ).override(holderDimens, holderDimens).asBitmap().placeholder( emptyImageRes ).into( photoHolder.getThumbnail() );
			} catch (OutOfMemoryError e) {
				Dlog.e(TAG, e);
			}
		}
	}

	protected MyPhotoSelectImageData getPhotoItem(int pos) {
		if(arrImageList == null || arrImageList.size() <= pos) return null;
		return arrImageList.get(pos);
	}

	@Override
	public int getItemCount() {
		return arrImageList != null ? arrImageList.size() : 0;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void clear() {
		int size = arrImageList.size();
		arrImageList.clear();
		notifyItemRangeRemoved(0, size);
	}

	public void addAll(MyPhotoSelectImageData[] contentses) {
		int startIndex = arrImageList.size();
		arrImageList.addAll(startIndex, Arrays.asList(contentses));
		notifyItemRangeInserted(startIndex, contentses.length);
	}

	public void addAll(List<MyPhotoSelectImageData> contentses) {
		int startIndex = arrImageList.size();
		arrImageList.addAll(startIndex, contentses);
		notifyItemRangeInserted(startIndex, contentses.size());
	}

	private void initImageList() {
		if (arrImageList != null)
			arrImageList.clear();
		else
			arrImageList = new ArrayList<>();
	}

	public static class UploadFailedImageHolder extends RecyclerView.ViewHolder {
		private ImageView thumbnail;
		private MyPhotoSelectImageData imgData;

		public UploadFailedImageHolder(View itemView) {
			super(itemView);
			thumbnail = (ImageView) itemView.findViewById(R.id.upload_failed_image_holder_thumbnail_iv);
		}

		public ImageView getThumbnail() {
			return thumbnail;
		}

		public MyPhotoSelectImageData getImgData() {
			return imgData;
		}

		public void setImgData(MyPhotoSelectImageData imgData) {
			this.imgData = imgData;
		}
	}
}



