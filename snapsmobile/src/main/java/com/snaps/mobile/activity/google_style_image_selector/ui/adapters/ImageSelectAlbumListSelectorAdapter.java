package com.snaps.mobile.activity.google_style_image_selector.ui.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.ui.IAlbumData;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.google_style_image_selector.interfaces.IImageSelectStateChangedListener;
import com.snaps.mobile.activity.google_style_image_selector.ui.fragments.sns.strategies.ImageSelectSNSPhotoForGooglePhoto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ysjeong on 16. 3. 9..
 */
public class ImageSelectAlbumListSelectorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<IAlbumData> albumList;
    private IImageSelectStateChangedListener stateChangedListener = null;

    public ImageSelectAlbumListSelectorAdapter(Context context, IImageSelectStateChangedListener stateChangedListener) {
        this.context = context;
        this.stateChangedListener = stateChangedListener;
    }

    public void setData(ArrayList<IAlbumData> newList) {
        if (newList == null) return;

        albumList = (ArrayList<IAlbumData>) newList.clone();
        
        notifyDataSetChanged();
    }

    public ArrayList<IAlbumData> getAlbumCursors() {
        return albumList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_select_album_list_selector_item, parent, false);
        return new ImageSelectAdapterHolders.AlbumListSelectorItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder == null) return;

        final IAlbumData albumCursor = getItem(position);
        if (albumCursor == null) return;

        ImageSelectAdapterHolders.AlbumListSelectorItemHolder listHolder = (ImageSelectAdapterHolders.AlbumListSelectorItemHolder) holder;

        TextView tvTitle = listHolder.getTitle();
        TextView tvCounter = listHolder.getCounter();
        ImageView ivThumbnail = listHolder.getThumbnail();
        View parentView = listHolder.getParentView();

        if (tvTitle != null)
            tvTitle.setText(albumCursor.getAlbumName());

        String photoCnt = albumCursor.getPhotoCnt();
        boolean isAllPhotos = ImageSelectSNSPhotoForGooglePhoto.ALL_PHOTO_DUMMY_COUNT.equals(photoCnt);  //전체 사진
        if (isAllPhotos) {
            photoCnt = "";
        }

        if (tvCounter != null) {
            tvCounter.setText(photoCnt);
        }

        if (ivThumbnail != null) {
            if (isAllPhotos) {
                ImageLoader.with(context).load(R.drawable.icon_google_photo_all_photos).centerCrop().into(ivThumbnail);
            }
            else {
                ivThumbnail.setImageBitmap(null);
                String albumThumnbail = albumCursor.getAlbumThumnbail();
                ImageLoader.with(context).load(albumThumnbail).centerCrop().into(ivThumbnail);
            }
        }

        if (parentView != null) {
            parentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (stateChangedListener != null)
                        stateChangedListener.onSelectedAlbumList(albumCursor);
                }
            });
        }
    }

    protected IAlbumData getItem(int pos) {
        if(albumList == null || albumList.size() <= pos) return null;
        return albumList.get(pos);
    }

    @Override
    public int getItemCount() {
        return albumList != null ? albumList.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(IAlbumData contents) {
        insert(contents, albumList.size());
    }

    public void insert(IAlbumData contents, int position) {
        albumList.add(position, contents);
        notifyItemInserted(position);
    }

    public void remove(int position) {
        if (albumList == null || albumList.size() <= position || position < 0) return;

        albumList.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        if( albumList == null || albumList.size() < 1 ) return;

        int size = albumList.size();
        albumList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void addAll(IAlbumData[] contentses) {
        int startIndex = albumList.size();
        albumList.addAll(startIndex, Arrays.asList(contentses));
        notifyItemRangeInserted(startIndex, contentses.length);
    }

    public void addAll(List<IAlbumData> contentses) {
        int startIndex = albumList.size();
        albumList.addAll(startIndex, contentses);
        notifyItemRangeInserted(startIndex, contentses.size());
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

        if (holder == null || !(holder instanceof ImageSelectAdapterHolders.AlbumListSelectorItemHolder)) return;

        ImageSelectAdapterHolders.AlbumListSelectorItemHolder photoHolder = (ImageSelectAdapterHolders.AlbumListSelectorItemHolder) holder;

        if (photoHolder.getThumbnail() != null) {
            ImageLoader.clear(context, photoHolder.getThumbnail());
        }
    }
}
