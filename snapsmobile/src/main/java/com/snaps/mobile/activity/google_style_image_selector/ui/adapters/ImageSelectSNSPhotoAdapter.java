package com.snaps.mobile.activity.google_style_image_selector.ui.adapters;

import android.content.Context;
import android.graphics.Point;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.snaps.common.data.img.ImageSelectSNSImageData;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectPhonePhotoFragmentData;
import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectUIPhotoFilter;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectFragmentItemClickListener;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectSNSPhotoStrategy;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.ISnapsImageSelectConstants;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectManager;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageSelectSNSPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
	private static final String TAG = ImageSelectSNSPhotoAdapter.class.getSimpleName();
	private Context context;
	private ImageSelectActivityV2 activityV2;
	private ArrayList<ImageSelectSNSImageData> arrImageList;

	private boolean isLandscapeMode = false;
	private int holderDimens = 0;
	private ImageSelectUIPhotoFilter photoFilter = null;
	private IImageSelectFragmentItemClickListener itemClickListener = null;

	private IImageSelectSNSPhotoStrategy photoStrategy = null;

	public ImageSelectSNSPhotoAdapter(Context context, ImageSelectActivityV2 activityV2, boolean isLandscapeMode) {
		this.context = context;
		this.activityV2 = activityV2;
		initTrayItemList();

		setLandscapeMode(isLandscapeMode);
	}

	public void setPhotoStrategy(IImageSelectSNSPhotoStrategy photoStrategy) {
		this.photoStrategy = photoStrategy;
	}

	public void setItemClickListener(IImageSelectFragmentItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	public void setLandscapeMode(boolean landscapeMode) {
		isLandscapeMode = landscapeMode;
	}

	public void setPhotoFilter(ImageSelectUIPhotoFilter filter) {
		this.photoFilter = filter;
	}

	public void setData(ArrayList<ImageSelectSNSImageData> newList) {
		if (newList == null) return;

		initTrayItemList();

		arrImageList = (ArrayList<ImageSelectSNSImageData>) newList.clone();

		notifyDataSetChanged();
	}

	public void notifyDataSetChangedByImageKey(String imageKey) {
		if (imageKey == null || imageKey.length() < 1) return;
		for (int ii = 0; ii < getPhotoItemCount(); ii++) {
			ImageSelectSNSImageData snsImageData = getPhotoItem(ii);
			if (snsImageData == null) continue;

			if (imageKey.equalsIgnoreCase(getMapKey(snsImageData))) {
				notifyItemChanged(ii + 1);
				break;
			}
		}
	}

	private RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_imagedetail_item, parent, false);

		int columnCount = (isLandscapeMode ? Const_VALUE.IMAGE_GRID_COLS_LANDSCAPE : Const_VALUE.IMAGE_GRID_COLS);

		holderDimens = isLandscapeMode ? UIUtil.getScreenHeight(context) : UIUtil.getScreenWidth(context);
		holderDimens /= columnCount; //FIXME decoration을 빼버리고 여기서 처리해도 될것 같다..

		RelativeLayout parentView = (RelativeLayout) view.findViewById(R.id.imgParent);
		GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) parentView.getLayoutParams();
		lp.width = holderDimens;
		lp.height = holderDimens;

		parentView.setLayoutParams(lp);

		ImageSelectManager imageSelectManager = ImageSelectManager.getInstance();
		if (imageSelectManager != null) {
			ImageSelectPhonePhotoFragmentData photoFragmentData = imageSelectManager.getPhonePhotoFragmentDatas();
			if (photoFragmentData != null) {
				photoFragmentData.setCurrentUIDepthThumbnailSize(holderDimens);
			}
		}

		return new ImageSelectAdapterHolders.PhotoFragmentItemHolder(view);
	}

	@Override
	public void onViewRecycled(RecyclerView.ViewHolder holder) {
		super.onViewRecycled(holder);

		if (holder == null || !(holder instanceof ImageSelectAdapterHolders.PhotoFragmentItemHolder)) return;

		ImageSelectAdapterHolders.PhotoFragmentItemHolder photoHolder = (ImageSelectAdapterHolders.PhotoFragmentItemHolder) holder;

		if (photoHolder.getThumbnail() != null) {
			ImageLoader.clear(activityV2, photoHolder.getThumbnail());
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		if (viewType == ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HEADER.ordinal()) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_select_act_header_decoration_layout, parent, false);
			setGrayAreaViewVisibleState(view);
			return new ImageSelectAdapterHolders.GooglePhotoStyleHeaderHolder(view);
		}
		return getItemViewHolder(parent);
	}

	private void setGrayAreaViewVisibleState(View inflateView) {
		if (inflateView == null || activityV2 == null) return;
		if (!activityV2.isSingleChooseType() && !activityV2.isMultiChooseType()) {
			View grayAreaView = inflateView.findViewById(R.id.top_gray_layout);
			if (grayAreaView != null) grayAreaView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) return ISnapsImageSelectConstants.eGOOGLE_STYLE_HOLDER_TYPE.HEADER.ordinal();
		return -1;
	}

	public ArrayList<ImageSelectSNSImageData> getPhotoItemList() {
		return arrImageList;
	}

	public ImageSelectSNSImageData getItem(int position) { //헤더가 있으니까 -1
		final int ITEM_POSITION = position - 1;
		ArrayList<ImageSelectSNSImageData>photoList = getPhotoItemList();
		if (photoList != null && photoList.size() > ITEM_POSITION) {
			return photoList.get(ITEM_POSITION);
		}
		return null;
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
		if (holder == null || !(holder instanceof ImageSelectAdapterHolders.PhotoFragmentItemHolder) || getItemCount() <= position) return; //Header는 0번

		final ImageSelectAdapterHolders.PhotoFragmentItemHolder photoHolder = (ImageSelectAdapterHolders.PhotoFragmentItemHolder) holder;

		ImageSelectSNSImageData imgData = getItem(position);
		if (imgData == null) return;

		String thumbnailIimgUrl = imgData.getThumbnailImageUrl();
		String imgUrl = imgData.getOrgImageUrl();
		long lCreateAt = imgData.getlCreateAt();
		String imgWidth = imgData.getOrgImageWidth();
		String imgHeight = imgData.getOrgImageHeight();

		boolean showNoPrintImage = false;
		int width = StringUtil.isEmpty(imgWidth ) ? 0 : Integer.parseInt( imgWidth );
		int height = StringUtil.isEmpty(imgHeight ) ? 0 : Integer.parseInt( imgHeight );

		Point ptFilterInfo = new Point(-1, -1);
		if (photoFilter != null)
			ptFilterInfo = photoFilter.getPhotoFilterPoint();

		if( width > 0 && height > 0 ) {
			if( width < height ) {
				int temp = width;
				width = height;
				height = temp;
			}

			if (Config.isSnapsPhotoPrint()) {
				if (width < ptFilterInfo.x || height < ptFilterInfo.y) {
					showNoPrintImage = true;
					photoHolder.setDisableClick(true);
				}
				else
					photoHolder.setDisableClick(false);
			} else if (Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct() ) {
				int minWdith = activityV2.getIntentData().getRecommendWidth();
				int minHeight = activityV2.getIntentData().getRecommendHeight();
				if (width < minWdith|| height < minHeight) {
					showNoPrintImage = true;
					photoHolder.setDisableClick(true);
				} else
					photoHolder.setDisableClick(false);
			} else if (Config.isSnapsSticker() && ptFilterInfo.x != -1 && ptFilterInfo.y != -1) {
				if (width >= ptFilterInfo.x && height >= ptFilterInfo.y) {
					photoHolder.setDisableClick(false);
				} else {
					showNoPrintImage = true;
					photoHolder.setDisableClick(true);
				}
			}
		}

		ImageView ivNoPrint = photoHolder.getNoPrintIcon();
		if (ivNoPrint != null) {
			if (showNoPrintImage) {
				ivNoPrint.setImageResource(R.drawable.img_tray_noprint_icon);
				ivNoPrint.setVisibility(View.VISIBLE);
			} else {
				ivNoPrint.setImageResource(0);
				ivNoPrint.setVisibility(View.GONE);
			}
		}

		String mapKey = getMapKey(imgData);

		ImageView ivSelector = photoHolder.getSelector();
		if (ivSelector != null) {
			if (ImageSelectUtils.isContainsInImageHolder(mapKey)) {
				ivSelector.setBackgroundResource(R.drawable.shape_red_e36a63_fill_solid_border_rect);
				ivSelector.setVisibility(View.VISIBLE);
			} else if (showNoPrintImage) {
				ivSelector.setBackgroundResource(R.drawable.shape_none_line_fill_solid_border_rect);
				ivSelector.setVisibility(View.VISIBLE);
			} else {
				ivSelector.setBackgroundResource(0);
				ivSelector.setVisibility(View.GONE);
			}
		}

		ImageView ivCheckIcon = photoHolder.getCheckIcon();
		if (ivCheckIcon != null) {
			if (ImageSelectUtils.isContainsInImageHolder(mapKey)) {
				ivCheckIcon.setImageResource(R.drawable.img_image_select_fragment_checked);
				ivCheckIcon.setVisibility(View.VISIBLE);
			} else {
				ivCheckIcon.setImageResource(0);
				ivCheckIcon.setVisibility(View.GONE);
			}
		}

		photoHolder.setImgData(mapKey, getSNSTypeCode(), imgData.getId(), getObjectId(imgUrl), imgUrl, thumbnailIimgUrl, lCreateAt, imgWidth, imgHeight, imgData.getMineType());

		View parentView = photoHolder.getParentView();
		if (parentView != null) {
			parentView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (itemClickListener != null) {
						itemClickListener.onClickFragmentItem(photoHolder);
					}
				}
			});
		}

		ImageView ivThumbnail = photoHolder.getThumbnail();
		if (ivThumbnail != null) {
			ivThumbnail.setImageBitmap(null);
			int emptyImageRes = ivThumbnail.getDrawable() != null ? R.drawable.color_drawable_eeeeee : 0;
			try {
				ImageLoader.with( context ).load( thumbnailIimgUrl ).override(holderDimens, holderDimens).asBitmap().placeholder( emptyImageRes ).into( photoHolder.getThumbnail() );
			} catch (OutOfMemoryError e) {
				Dlog.e(TAG, e);
			}
		}
	}

	protected ImageSelectSNSImageData getPhotoItem(int pos) {
		if(arrImageList == null || arrImageList.size() <= pos) return null;
		return arrImageList.get(pos);
	}

	@Override
	public int getItemCount() {
		return arrImageList != null ? arrImageList.size() + 1 : 1;
	}

	public int getPhotoItemCount() {
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

	public void addAll(ImageSelectSNSImageData[] contentses) {
		int startIndex = arrImageList.size();
		arrImageList.addAll(startIndex, Arrays.asList(contentses));
		notifyItemRangeInserted(startIndex + 1, contentses.length); //헤더 때문에
	}

	public void addAll(List<ImageSelectSNSImageData> contentses) {
		int startIndex = arrImageList.size();
		arrImageList.addAll(startIndex, contentses);
		notifyItemRangeInserted(startIndex + 1, contentses.size()); //헤더 때문에
	}

	private void initTrayItemList() {
		if (arrImageList != null)
			arrImageList.clear();
		else
			arrImageList = new ArrayList<>();
	}

	private String getMapKey(ImageSelectSNSImageData data) {
		if (photoStrategy != null) {
			photoStrategy.getMapKey(data);  //이거 원래 리턴해야 하는데 리턴하면 사이드가...
		}

		return data != null ? data.getId() : "";
	}

	private String getObjectId(String id) {
		if (photoStrategy != null) {
			photoStrategy.getObjectId(id);	//이거 원래 리턴해야 하는데 리턴하면 사이드가...
		}

		return "";
	}

	private int getSNSTypeCode() {
		if (photoStrategy != null) {
			photoStrategy.getSNSTypeCode(); //이거 원래 리턴해야 하는데 리턴하면 사이드가...
		}
		return Const_VALUES.SELECT_KAKAO;
	}
}



