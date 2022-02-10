package com.snaps.common.structure.photoprint;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.snaps.mobile.R;
import com.snaps.mobile.activity.photoprint.manager.PhotoPrintDataManager;
import com.snaps.mobile.activity.photoprint.model.PhotoPrintData;

/**
 * Created by songhw on 2017. 3. 9..
 */

public class PhotoPrintDetailEditAdapter extends RecyclerView.Adapter<PhotoPrintListItemHolder> {

    private Context context;

    public PhotoPrintDetailEditAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public PhotoPrintListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        RelativeLayout v = (RelativeLayout) inflater.inflate( R.layout.photo_print_detail_edit_item, null );

        return new PhotoPrintListItemHolder( v );
    }

    @Override
    public void onBindViewHolder(PhotoPrintListItemHolder holder, int position) {
        PhotoPrintData data = PhotoPrintDataManager.getInstance().getData( position );
        if( data != null ) {
            if( holder.isInitialized() )
                holder.refresh( data, position );
            else
                holder.init( data, position );
        }
    }

    @Override
    public void onViewRecycled(PhotoPrintListItemHolder holder) {
        super.onViewRecycled(holder);
        holder.clearImageResource( context, true );
    }

    @Override
    public int getItemCount() {
        return PhotoPrintDataManager.getInstance().getDataCount();
    }

    @Override
    public void onViewDetachedFromWindow(PhotoPrintListItemHolder holder) {
        super.onViewDetachedFromWindow(holder);

    }
}
