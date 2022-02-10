package com.snaps.mobile.activity.google_style_image_selector.ui.adapters;

import android.content.Context;
import android.net.Uri;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.Const_VALUES;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.book.SNSBookFragmentActivity;
import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectFragmentItemClickListener;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.photo_remove.KakaobookBookPhotoRemoveFragment;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PhotoArrayForSNSBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = PhotoArrayForSNSBookAdapter.class.getSimpleName();

    private ImageSelectActivityV2 selectAct;
    protected ArrayList<HashMap<String, String>>  arrDataList;
    private int type;
    private IImageSelectFragmentItemClickListener itemClickListener = null;

    public static PhotoArrayForSNSBookAdapter getInstance( Context context, ArrayList<HashMap<String,String>> strings, int type ) {
		PhotoArrayForSNSBookAdapter adapter = new PhotoArrayForSNSBookAdapter(context, strings );
		adapter.type = type;
		return adapter;
	}

    private PhotoArrayForSNSBookAdapter(Context context, ArrayList<HashMap<String, String>> Strings) {
		this.arrDataList = Strings;
		selectAct = (ImageSelectActivityV2)context;
	}

    public void setItemClickListener(IImageSelectFragmentItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setData(ArrayList<HashMap<String, String>> newList) {
        if (newList == null) return;

		initTrayItemList();

        arrDataList = (ArrayList<HashMap<String, String>>) newList.clone();

        notifyDataSetChanged();
    }

    public void notifyDataSetChangedByImageKey(String imageKey) {
        if (imageKey == null || imageKey.length() < 1) return;
        for (int ii = 0; ii < getItemCount(); ii++) {
            HashMap<String, String> snsImageData = getItem(ii);
            if (snsImageData == null) continue;
            String key = snsImageData.get(KakaobookBookPhotoRemoveFragment.TAG_ID);
            if (imageKey.equalsIgnoreCase(key)) {
                notifyItemChanged(ii);
                break;
            }
        }
    }

    public RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_imagedetail_item, parent, false);

        int columnCount = Const_VALUE.IMAGE_GRID_COLS;

        int holderDimens = UIUtil.getScreenWidth(selectAct);
        holderDimens /= columnCount; //FIXME decoration을 빼버리고 여기서 처리해도 될것 같다..

        RelativeLayout parentView = (RelativeLayout) view.findViewById(R.id.imgParent);
        GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) parentView.getLayoutParams();
        lp.width = holderDimens;
        lp.height = holderDimens;

        ImageView icon = (ImageView) view.findViewById(R.id.imgChoiceBg);
        if (icon != null) {
            icon.setImageResource(R.drawable.img_tray_delete_icon);
        }

        parentView.setLayoutParams(lp);

        return new ImageSelectAdapterHolders.PhotoFragmentItemHolder(view);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return getItemViewHolder(parent);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

        if (holder == null || !(holder instanceof ImageSelectAdapterHolders.PhotoFragmentItemHolder)) return;

        ImageSelectAdapterHolders.PhotoFragmentItemHolder photoHolder = (ImageSelectAdapterHolders.PhotoFragmentItemHolder) holder;

        if (photoHolder.getThumbnail() != null)
            ImageLoader.clear(selectAct, photoHolder.getThumbnail());
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder == null || getItemCount() <= position) return;

        final ImageSelectAdapterHolders.PhotoFragmentItemHolder vh = (ImageSelectAdapterHolders.PhotoFragmentItemHolder) holder;

        HashMap<String, String> data = getItem(position);
        if (data == null) return;

        String osType = "", imgUrl = "", thumbnailImgUrl = "", width = "", height = "";
        thumbnailImgUrl =  data.get(KakaobookBookPhotoRemoveFragment.TAG_Thumbnail);

        if( type == SNSBookFragmentActivity.TYPE_KAKAO_STORY ) {
            imgUrl = data.get(KakaobookBookPhotoRemoveFragment.TAG_Image);
            width = data.get(KakaobookBookPhotoRemoveFragment.TAG_Width);
            height =  data.get(KakaobookBookPhotoRemoveFragment.TAG_Height);
        } else if ( type == SNSBookFragmentActivity.TYPE_DIARY) {
            osType = data.get(KakaobookBookPhotoRemoveFragment.TAG_OS_TYPE);
            vh.setOsType(osType);
        }

        String mapKey = data.get(KakaobookBookPhotoRemoveFragment.TAG_ID);

        TextView tvDetail = vh.getContent();
        ImageView ivDetail = vh.getThumbnail();
        ImageView ivNoPrint = vh.getNoPrintIcon();
        ImageView ivSelector = vh.getSelector();
        ImageView ivCheckIcon = vh.getCheckIcon();

        View parentView = vh.getParentView();
        if (parentView != null) {
            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onClickFragmentItem(vh);
                    }
                }
            });
        }

        if( StringUtil.isEmpty(thumbnailImgUrl) || (!StringUtil.isEmpty(imgUrl) && imgUrl.equals("content")) ) {
            String content = data.get(KakaobookBookPhotoRemoveFragment.TAG_NAME);
            if (tvDetail != null) {
                tvDetail.setVisibility(View.VISIBLE);
                tvDetail.setText(content);
            }

            if (ivDetail != null)
                ivDetail.setVisibility(View.GONE);
        } else {
            tvDetail.setVisibility(View.GONE);
            ivDetail.setVisibility(View.VISIBLE);
            try {
                ImageLoader.with(selectAct).load(thumbnailImgUrl).into(ivDetail);
            } catch (OutOfMemoryError e) {
                Dlog.e(TAG, e);
            }
        }

        if (ivSelector != null) {
            if (ImageSelectUtils.isContainsInImageHolder(mapKey)) {
                ivSelector.setBackgroundResource(R.drawable.shape_red_e36a63_fill_solid_border_rect);
                ivSelector.setVisibility(View.VISIBLE);
            } else {
                ivSelector.setBackgroundResource(0);
                ivSelector.setVisibility(View.GONE);
            }
        }

        if (ivCheckIcon != null) {
            if (ImageSelectUtils.isContainsInImageHolder(mapKey)) {
                ivCheckIcon.setImageResource(R.drawable.img_image_select_fragment_checked);
                ivCheckIcon.setVisibility(View.VISIBLE);
            } else {
                ivCheckIcon.setImageResource(0);
                ivCheckIcon.setVisibility(View.GONE);
            }
        }

        int imageType = Const_VALUES.SELECT_KAKAO;
        switch ( type ){
            case SNSBookFragmentActivity.TYPE_KAKAO_STORY:
                imageType = Const_VALUES.SELECT_KAKAO;
                break;
            case SNSBookFragmentActivity.TYPE_FACEBOOK_PHOTOBOOK:
                imageType = Const_VALUES.SELECT_FACEBOOK;
                break;
            case SNSBookFragmentActivity.TYPE_DIARY:
                imageType = Const_VALUES.SELECT_PHONE;
                break;
        }

        Uri thumbnailUri = null;
        if( !StringUtil.isEmpty(thumbnailImgUrl) ) thumbnailUri = Uri.parse( thumbnailImgUrl );

        if( !StringUtil.isEmpty(imgUrl) && !StringUtil.isEmpty(width) && !StringUtil.isEmpty(height) )
            vh.setImgData(mapKey, imageType, position, Uri.parse(imgUrl).getLastPathSegment(), imgUrl, thumbnailImgUrl, width , height);
        else
            vh.setImgData(mapKey, imageType, position, (thumbnailUri != null ? thumbnailUri.getLastPathSegment() : ""), thumbnailImgUrl, thumbnailImgUrl);

        if (ivNoPrint != null)
            ivNoPrint.setVisibility(View.GONE);
    }

    protected HashMap<String, String> getItem(int pos) {
        if (arrDataList == null || arrDataList.size() <= pos) return null;
        return arrDataList.get(pos);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return arrDataList != null ? arrDataList.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(HashMap<String, String> contents) {
        insert(contents, arrDataList.size());
    }

    public void insert(HashMap<String, String> contents, int position) {
        arrDataList.add(position, contents);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        if (arrDataList == null || arrDataList.size() <= position || position < 0) return;

        arrDataList.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        int size = arrDataList.size();
        arrDataList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void addAll(List<HashMap<String, String>> contentses) {
        int startIndex = arrDataList.size();
        arrDataList.addAll(startIndex, contentses);
        notifyItemRangeInserted(startIndex, contentses.size());
    }

    public ArrayList<HashMap<String, String>> getTrayCellItemList() {
        return this.arrDataList;
    }

    protected void initTrayItemList() {
        if (arrDataList != null)
            arrDataList.clear();
        else
            arrDataList = new ArrayList<>();
    }
}