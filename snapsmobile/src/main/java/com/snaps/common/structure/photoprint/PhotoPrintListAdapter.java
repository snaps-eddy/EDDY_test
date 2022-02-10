package com.snaps.common.structure.photoprint;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.photoprint.manager.PhotoPrintDataManager;
import com.snaps.mobile.activity.photoprint.model.PhotoPrintData;

/**
 * Created by songhw on 2017. 3. 9..
 */

public class PhotoPrintListAdapter extends RecyclerView.Adapter<PhotoPrintListItemHolder> {
    private View.OnClickListener itemClickListener;

    private boolean isLargeViewMode;

    private Context context;

    public PhotoPrintListAdapter( Context context, boolean isLargeViewMode, View.OnClickListener itemClickListener ) {
        this.context = context;
        this.isLargeViewMode = isLargeViewMode;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public PhotoPrintListItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if( viewType == 0 )
            return new PhotoPrintListItemHolder( getHeaderView(parent.getContext()), isLargeViewMode, true );

        if (isLargeViewMode) {
            if( viewType == PhotoPrintDataManager.getInstance().getDataCount() + 1 )
                return new PhotoPrintListItemHolder( getFooterView(parent.getContext(), isLargeViewMode, viewType), isLargeViewMode, true );
        } else {
            if( viewType == PhotoPrintDataManager.getInstance().getDataCount() + 1 || viewType == PhotoPrintDataManager.getInstance().getDataCount() + 2)
                return new PhotoPrintListItemHolder( getFooterView(parent.getContext(), isLargeViewMode, viewType), isLargeViewMode, true );
        }

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        RelativeLayout v = (RelativeLayout) inflater.inflate( isLargeViewMode ? R.layout.photo_print_item_large : R.layout.photo_print_item_small, null );
        int[] layoutSizes = PhotoPrintDataManager.getLayoutSize( parent.getContext() );
        int layoutSize = layoutSizes[ isLargeViewMode ? 0 : 1 ];
        ViewGroup.LayoutParams params = v.getLayoutParams();
        if( params == null )
            params = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, layoutSize + UIUtil.convertDPtoPX(parent.getContext(), 51) );
        else {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = layoutSize + UIUtil.convertDPtoPX( parent.getContext(), 51 );
        }
        v.setLayoutParams( params );

        RelativeLayout.LayoutParams rParams = (RelativeLayout.LayoutParams) v.findViewById( R.id.image_total_area ).getLayoutParams();
        if( rParams == null )
            rParams = new RelativeLayout.LayoutParams( layoutSize, layoutSize );
        else {
            rParams.width = layoutSize;
            rParams.height = layoutSize;
        }
        v.findViewById( R.id.image_total_area ).setLayoutParams( rParams );
        v.setOnClickListener( itemClickListener );

        return new PhotoPrintListItemHolder( v, isLargeViewMode, false );
    }

    public static RelativeLayout getHeaderView( Context context ) {
        RelativeLayout relativeLayout = new RelativeLayout( context );
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, UIUtil.convertDPtoPX(context, 109) );
        relativeLayout.setLayoutParams( params );

        return relativeLayout;
    }

    public static RelativeLayout getFooterView( Context context, boolean isLargeViewMode, int itemPosition  ) {
        int indicatorHeight = PhotoPrintDataManager.getInstance().getIndicatorHeight();
        RelativeLayout relativeLayout = new RelativeLayout( context );
        int dataCount = PhotoPrintDataManager.getInstance().getDataCount();
        int screenHeight = UIUtil.getScreenHeight( context ) - UIUtil.convertDPtoPX( context, 48 ) - indicatorHeight;
        int[] size = PhotoPrintDataManager.getLayoutSize( context );
        int leftSpace = screenHeight;
        if( isLargeViewMode )
            leftSpace -= ( size[0] + UIUtil.convertDPtoPX(context, 51) ) * dataCount + ( UIUtil.convertDPtoPX(context, 8) ) * ( dataCount + 2 );
        else {
            int rowCount = dataCount / 2;
            leftSpace -= ( size[1] + UIUtil.convertDPtoPX(context, 51) ) * rowCount + ( UIUtil.convertDPtoPX(context, 8) ) * ( rowCount + 2 );
        }
        leftSpace -= UIUtil.convertDPtoPX( context, 4 );

        if (!isLargeViewMode && itemPosition % 2 == 0) {
            leftSpace = Math.min(size[1], leftSpace);
        }

        if( leftSpace < 0 || dataCount <= 2)
            leftSpace = 0;

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, leftSpace  );
        relativeLayout.setLayoutParams( params );

        return relativeLayout;
    }

    @Override
    public void onBindViewHolder(PhotoPrintListItemHolder holder, int position) {
        int realPos = position - 1;
        PhotoPrintDataManager photoPrintDataManager = PhotoPrintDataManager.getInstance();
        PhotoPrintData data = null;
        if( realPos > -1 && realPos < photoPrintDataManager.getDataCount() ) {
            data = photoPrintDataManager.getData( realPos );
        }

        if( holder.isInitialized() )
            holder.refresh( data, realPos );
        else
            holder.init( data, realPos );
    }

    @Override
    public void onViewRecycled(PhotoPrintListItemHolder holder) {
        super.onViewRecycled(holder);
        holder.clearImageResource( context, true );
    }

    @Override
    public int getItemCount() {
        int itemCount = PhotoPrintDataManager.getInstance().getDataCount();
        if (isLargeViewMode) return itemCount + 2;

        return itemCount % 2 == 1 ? itemCount + 3 : itemCount + 2;
    }

    @Override
    public void onViewDetachedFromWindow(PhotoPrintListItemHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }
}
