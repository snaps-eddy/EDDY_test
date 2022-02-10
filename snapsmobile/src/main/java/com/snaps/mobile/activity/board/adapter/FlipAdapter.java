package com.snaps.mobile.activity.board.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.snaps.common.data.bitmap.PageBitmap;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.board.BaseMyArtworkDetail;

import errorhandle.logger.Logg;

public class FlipAdapter extends BaseAdapter {
    private static final String TAG = FlipAdapter.class.getSimpleName();
    LayoutInflater inflater;
    BaseMyArtworkDetail baseAct;
    int pageCount;

    public FlipAdapter(BaseMyArtworkDetail baseAct, int pageCount) {
        this.baseAct = baseAct;
        this.pageCount = pageCount;
        inflater = LayoutInflater.from(baseAct);
    }

    @Override
    public int getCount() {
        return pageCount;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Dlog.d("getView() position:" + position);

        PageBitmap pageBitmap = baseAct.getFlipPageBitmap(position);
        if (pageBitmap == null)
            return null;

        convertView = inflater.inflate(R.layout.flip_page, parent, false);
        ImageView imgFlip = (ImageView) convertView.findViewById(R.id.imgFlip);
        imgFlip.setImageBitmap(pageBitmap.all);

        return convertView;
    }

}
