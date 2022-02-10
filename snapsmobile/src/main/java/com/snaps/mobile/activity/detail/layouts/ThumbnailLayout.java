package com.snaps.mobile.activity.detail.layouts;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.SynchronizedImageLoader;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;
import com.snaps.mobile.activity.google_style_image_selector.ui.custom.CustomLinearLayoutManager;
import com.snaps.mobile.activity.ui.menu.renewal.viewpager.TouchCustomRecyclerViewPager;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductThumbnail;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductThumbnailItem;
import com.snaps.mobile.utils.ui.UrlUtil;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by songhw on 2016. 10. 24..
 */
public class ThumbnailLayout extends LinkedLayout {
    private static final String TAG = ThumbnailLayout.class.getSimpleName();

    private static final String PHOTOBOOK = "KOR0031002001000";
    private static final String FACEBOOK_PHOTOBOOK = "KOR0031002008000";
    private static final String KAKAOSTORY_BOOK = "KOR0031002006000";
    private static final String THEMEBOOK = "KOR0031002002000";
    private static final String DIARY_BOOK = "KOR0031999999999";
    private static final String CALENDAR_TABLE_ORIGINAL_HORIZONTAL = "KOR0031001009000";
    private static final String CALENDAR_TABLE_ORIGINAL_VERTICAL = "KOR0031001010000";
    private static final String CALENDAR_TABLE_MINI = "KOR0031001011000";
    private static final String CALENDAR_TABLE_LARGE = "KOR0031001012000";
    private static final String CALENDAR_TABLE_SMALL_HORIZONTAL = "KOR0031001013000";
    private static final String CALENDAR_TABLE_SMALL_VERTICAL = "KOR0031001014000";
    private static final String CALENDAR_WALL_HANGING = "KOR0031007002000";
    private static final String CALENDAR_SCHEDULER = "KOR0031007003000";

    private static final int TYPE_CALENDAR_TABLE_ORIGINAL_HORIZONTAL = 0;
    private static final int TYPE_CALENDAR_TABLE_ORIGINAL_VERTICAL = 1;
    private static final int TYPE_CALENDAR_TABLE_MINI = 2;
    private static final int TYPE_CALENDAR_TABLE_LARGE = 3;
    private static final int TYPE_CALENDAR_TABLE_SMALL_HORIZONTAL = 4;
    private static final int TYPE_CALENDAR_TABLE_SMALL_VERTICAL = 5;
    private static final int TYPE_CALENDAR_WALL_HANGING = 6;
    private static final int TYPE_CALENDAR_SCHEDULER = 7;

    private TextView pagerTextView;
    private SnapsProductThumbnail cellData;
    private TouchCustomRecyclerViewPager viewPager;
    private ThumbnailAdapter adapter;
    private String currentThumbType = "";
    private int leatherCoverIndex = -1;
    private int currentPageIndex = 0;

    private boolean isPhotobookType = false;
    private boolean isCalendarType = false;
    private int calendarType = -1;

    private LinkedList<SynchronizedImageLoader> synchronizedImageLoaders = null;

    private ThumbnailLayout(Context context) {
        super(context);
    }

    public static ThumbnailLayout createInstance(Context context, LayoutRequestReciever reciever, String classCode) {
        ThumbnailLayout instance = new ThumbnailLayout(context);
        instance.type = Type.Thumbnail;
        instance.reciever = reciever;
        instance.synchronizedImageLoaders = new LinkedList<>();

        if( !StringUtil.isEmpty(classCode) ) {
            if( classCode.equalsIgnoreCase(PHOTOBOOK) || classCode.equalsIgnoreCase(FACEBOOK_PHOTOBOOK) || classCode.equalsIgnoreCase(KAKAOSTORY_BOOK) || classCode.equalsIgnoreCase(THEMEBOOK) || classCode.equalsIgnoreCase(DIARY_BOOK) )
                instance.isPhotobookType = true;
            else {
                switch (classCode) {
                    case CALENDAR_TABLE_ORIGINAL_HORIZONTAL: instance.calendarType = TYPE_CALENDAR_TABLE_ORIGINAL_HORIZONTAL; break;
                    case CALENDAR_TABLE_ORIGINAL_VERTICAL: instance.calendarType = TYPE_CALENDAR_TABLE_ORIGINAL_VERTICAL; break;
                    case CALENDAR_TABLE_MINI: instance.calendarType = TYPE_CALENDAR_TABLE_MINI; break;
                    case CALENDAR_TABLE_LARGE: instance.calendarType = TYPE_CALENDAR_TABLE_LARGE; break;
                    case CALENDAR_TABLE_SMALL_HORIZONTAL: instance.calendarType = TYPE_CALENDAR_TABLE_SMALL_HORIZONTAL; break;
                    case CALENDAR_TABLE_SMALL_VERTICAL: instance.calendarType = TYPE_CALENDAR_TABLE_SMALL_VERTICAL; break;
                    case CALENDAR_WALL_HANGING: instance.calendarType = TYPE_CALENDAR_WALL_HANGING; break;
                    case CALENDAR_SCHEDULER: instance.calendarType = TYPE_CALENDAR_SCHEDULER; break;
                }

                instance.isCalendarType = instance.calendarType > -1;
            }
        }
        return instance;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (synchronizedImageLoaders != null) {
            while (!synchronizedImageLoaders.isEmpty()) {
                SynchronizedImageLoader synchronizedImageLoader = synchronizedImageLoaders.poll();
                if (synchronizedImageLoader != null) {
                    synchronizedImageLoader.suspendTask();
                    synchronizedImageLoader.unbindImageViewReferences();
                }
            }
        }
    }

    public void suspendImageLoad() {
        if (synchronizedImageLoaders != null) {
            for (SynchronizedImageLoader synchronizedImageLoader : synchronizedImageLoaders) {
                if (synchronizedImageLoader != null) {
                    synchronizedImageLoader.suspendTask();
                }
            }
        }
    }

    public void restartImageLoad() {
        if (synchronizedImageLoaders != null) {
            for (SynchronizedImageLoader synchronizedImageLoader : synchronizedImageLoaders) {
                if (synchronizedImageLoader != null) {
                    synchronizedImageLoader.restart();
                }
            }
        }
    }

    @Override
    public void draw(ViewGroup parent, Object data, int headViewId, int id) {
        if( !(data instanceof SnapsProductThumbnail) ) return;
        cellData = (SnapsProductThumbnail) data;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, UIUtil.convertDPtoPX(getContext(), 328));
        CustomLinearLayoutManager manager = new CustomLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        RelativeLayout container = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.detail_layout_thumbnail, null);
        viewPager = (TouchCustomRecyclerViewPager) container.findViewById(R.id.horizontal_pager);
        viewPager.setPadding(UIUtil.convertDPtoPX(getContext(),16), 0, UIUtil.convertDPtoPX(getContext(),16), 0);
        viewPager.setTriggerOffset(0.01f);
        viewPager.setSinglePageFling(true);
        viewPager.setClipToPadding(true);
        viewPager.setLayoutParams(params);

        viewPager.setLayoutManager(manager);
        viewPager.setBackgroundColor(Color.TRANSPARENT);

        pagerTextView = (TextView) container.findViewById(R.id.text_pager);

        ( container.findViewById(R.id.zoom_button) ).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtil.blockClickEvent(v, UIUtil.DEFAULT_CLICK_BLOCK_TIME);

                startProductZoomActivity();
            }
        });

        addView(container);
        parent.addView(this);
    }

    public void startProductZoomActivity() {
        String newZoomUrl = null;

        try {
            newZoomUrl = combineZoomUrl();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        if( !StringUtil.isEmpty(newZoomUrl) ) {
            if (reciever != null)
                reciever.openZoomUrl( newZoomUrl );
        }
    }

    public int getViewPagerCurrentPosition() {
        return viewPager != null ? viewPager.getCurrentPosition() : 0;
    }

    public void setViewPagerPosition(int position) {
        if (viewPager == null) return;
        viewPager.smoothScrollToPosition(position);
    }

    private String combineZoomUrl() throws Exception {
        StringBuilder sb = new StringBuilder();
        if (reciever != null) {
            String cmd = reciever.getSelectedValue("cmd");
            if (!StringUtil.isEmpty(cmd)) {
                HashMap<String, String> params = UrlUtil.getParameters(cmd);
                sb.append(cellData.getThumbnailItem(currentThumbType).getZoomUrl());
                sb.append("&F_PROD_CODE=").append(params.containsKey("productCode") ? params.get("productCode") : "");
                sb.append("&F_TMPL_CODE=").append(params.containsKey("prmTmplCode") ? params.get("prmTmplCode") : "");
                sb.append("&F_PAPER_CODE=").append(params.containsKey("paperCode") ? params.get("paperCode") : "160001");
                sb.append("&F_TMPL_ID=").append(params.containsKey("prmTmplId") ? params.get("prmTmplId") : "");
            }
        }

        return sb.toString();
    }

    public void setThumbnailTypeIndex( String thumbnailId, int leatherCoverIndex ) {
        if(TextUtils.isEmpty(thumbnailId) || thumbnailId == null ||  (thumbnailId.equalsIgnoreCase(currentThumbType) && leatherCoverIndex == this.leatherCoverIndex) ) return;
        currentThumbType = thumbnailId;
        this.leatherCoverIndex = leatherCoverIndex;

        adapter = new ThumbnailAdapter( currentThumbType );
        adapter.preload();
        viewPager.swapAdapter( adapter, true );
        viewPager.scrollToPosition( currentPageIndex );

        refreshPageTextView( 1, adapter.getItemCount() );
        viewPager.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int i, int i1) {
                refreshPageTextView( i1 + 1, adapter.getItemCount() );
                currentPageIndex = i1;
            }
        });
    }

    private void refreshPageTextView( int position, int total ) {
        if( pagerTextView == null ) return;

        SpannableStringBuilder ssb = new SpannableStringBuilder( position + " / " + total );
        ssb.setSpan( new ForegroundColorSpan(0xFF191919), 0, (position + "").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
        pagerTextView.setText( ssb );
    }


    private class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailHolder> {
        private int[] weightH, weightV;
        private SnapsProductThumbnailItem item;

        private final String LEATHER_COVER_URL_BASE = "/mw/resources/img/store/product/skin/leather_";
        private final String LEATHER_COVER_URL_FRONT = "_front.png";
        private final String LEATHER_COVER_URL_BACK = "_back.png";
        private String[] leatherCoverColor = { "_dbrown", "_lbrown", "_red", "_gray", "_black", "_emerald" };
        private String type;

        public ThumbnailAdapter( String type ) {
            this.type = type;
            item = cellData.getThumbnailItem( type );
            if(item != null) {
                float areaW = item.getSize().get(0);
                float areaH = item.getSize().get(1);

                if (areaW > areaH) {
                    weightH = new int[]{0, 1, 0};
                    weightV = new int[]{(int) (areaW - areaH), (int) areaH * 2, (int) (areaW - areaH)};
                } else {
                    weightV = new int[]{0, 1, 0};
                    weightH = new int[]{(int) (areaH - areaW), (int) areaW, (int) (areaH - areaW)};
                }
            }
        }

        public void preload() {
            if(item != null) {
                int count = item.getItems().size();
                int screenW = UIUtil.getScreenWidth(getContext());
                for (int i = 0; i < count; ++i) {
                    if (item.getSkin() != null && item.getSkin().size() > i)
                        ImageLoader.with(getContext()).load(item.getSkin().get(i)).downloadOnly(screenW, screenW);
                    if (item.getItems() != null && item.getItems().size() > i)
                        ImageLoader.with(getContext()).load(item.getItems().get(i)).downloadOnly(screenW, screenW);
                }
            }
        }

        @Override
        public ThumbnailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RelativeLayout container = (RelativeLayout) ( LayoutInflater.from( getContext() ).inflate(R.layout.detail_layout_thumbnail_holder, null) );
            return new ThumbnailHolder( container );
        }

        @Override
        public void onBindViewHolder(final ThumbnailHolder holder, int position) {
            if( weightH == null || weightV == null || weightH.length < 2 || weightV.length < 2 ) return;

            SynchronizedImageLoader synchronizedImageLoader = new SynchronizedImageLoader();
            if (synchronizedImageLoaders != null)
                synchronizedImageLoaders.add(synchronizedImageLoader);

            holder.getShadow().setImageDrawable( null );
            holder.getSkin().setImageDrawable( null );
            holder.getImage().setImageDrawable( null );

            LinearLayout.LayoutParams lParams;
            holder.getContainerH().setWeightSum( weightH[0] + weightH[1] + weightH[2] );

            lParams = (LinearLayout.LayoutParams) holder.getLeftSpace().getLayoutParams();
            lParams.weight = weightH[0];
            holder.getLeftSpace().setLayoutParams(lParams);

            lParams = (LinearLayout.LayoutParams) holder.getRightSpace().getLayoutParams();
            lParams.weight = weightH[2];
            holder.getRightSpace().setLayoutParams( lParams );

            lParams = (LinearLayout.LayoutParams) holder.getContainerV().getLayoutParams();
            lParams.weight = weightH[1];
            holder.getContainerV().setLayoutParams(lParams);
            holder.getContainerV().setWeightSum( weightV[0] + weightV[1] + weightV[2] );

            lParams = (LinearLayout.LayoutParams) holder.getTopSpace().getLayoutParams();
            lParams.weight = weightV[0];
            holder.getTopSpace().setLayoutParams(lParams);

            lParams = (LinearLayout.LayoutParams) holder.getBottomSpace().getLayoutParams();
            lParams.weight = weightV[2];
            holder.getBottomSpace().setLayoutParams( lParams );

            lParams = (LinearLayout.LayoutParams) holder.getContainer().getLayoutParams();
            lParams.weight = weightV[1];
            holder.getContainer().setLayoutParams(lParams);

            if( position > -1 && item.getSkin() != null && item.getSkin().size() > position && !StringUtil.isEmpty(item.getSkin().get(position)) ) {
                holder.getSkin().setVisibility( View.VISIBLE );
                synchronizedImageLoader.add( SnapsAPI.DOMAIN() + item.getSkin().get(position), holder.getSkin(), true );
            }
            else {
                holder.getSkin().setImageBitmap(null);
                holder.getSkin().setVisibility( View.GONE );
            }

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.getImage().getLayoutParams();
            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) holder.getSkin().getLayoutParams();
            LinearLayout.LayoutParams param3 = (LinearLayout.LayoutParams) holder.getContentLayout().getLayoutParams();
            if( isCalendarType && calendarType > -1 ) { // 달력은 스킨의 margin값을 하드코딩하여 스킨과 맞춘다
                switch (calendarType) {
//                    case TYPE_CALENDAR_TABLE_ORIGINAL_HORIZONTAL: params2.setMargins( 0, UIUtil.convertDPtoPX(getContext(), 9), 0, UIUtil.convertDPtoPX(getContext(), -9) ); break;
//                    case TYPE_CALENDAR_TABLE_ORIGINAL_VERTICAL: params2.setMargins( 0, UIUtil.convertDPtoPX(getContext(), 50), 0, UIUtil.convertDPtoPX(getContext(), 32) ); break;
//                    case TYPE_CALENDAR_TABLE_MINI: params2.setMargins( 0, UIUtil.convertDPtoPX(getContext(), 50), 0, UIUtil.convertDPtoPX(getContext(), 32) ); break;
//                    case TYPE_CALENDAR_TABLE_LARGE: params2.setMargins( 0, UIUtil.convertDPtoPX(getContext(), 9), 0, UIUtil.convertDPtoPX(getContext(), -9) ); break;
//                    case TYPE_CALENDAR_TABLE_SMALL_HORIZONTAL: params2.setMargins( 0, UIUtil.convertDPtoPX(getContext(), 10), 0, UIUtil.convertDPtoPX(getContext(), -10) ); break;
//                    case TYPE_CALENDAR_TABLE_SMALL_VERTICAL: params2.setMargins( 0, UIUtil.convertDPtoPX(getContext(), 66), 0, UIUtil.convertDPtoPX(getContext(), 45) ); break;
//                    case TYPE_CALENDAR_WALL_HANGING: params2.setMargins( 0, UIUtil.convertDPtoPX(getContext(), 35), 0, UIUtil.convertDPtoPX(getContext(), 35) ); break;
//                    case TYPE_CALENDAR_SCHEDULER: params2.setMargins( 0, UIUtil.convertDPtoPX(getContext(), 0), 0, UIUtil.convertDPtoPX(getContext(), 0) ); break;
                    case TYPE_CALENDAR_TABLE_ORIGINAL_HORIZONTAL: params2.setMargins( 0, UIUtil.convertDPtoPX(getContext(), 9), 0, UIUtil.convertDPtoPX(getContext(), -9) ); break;
                    case TYPE_CALENDAR_TABLE_ORIGINAL_VERTICAL: params2.setMargins( 0, UIUtil.convertDPtoPX(getContext(), 33), 0, UIUtil.convertDPtoPX(getContext(), 25) ); break;
                    case TYPE_CALENDAR_TABLE_MINI: params2.setMargins( 0, UIUtil.convertDPtoPX(getContext(), 33), 0, UIUtil.convertDPtoPX(getContext(), 25) ); break;
                    case TYPE_CALENDAR_TABLE_LARGE: params2.setMargins( 0, UIUtil.convertDPtoPX(getContext(), 9), 0, UIUtil.convertDPtoPX(getContext(), -9) ); break;
                    case TYPE_CALENDAR_TABLE_SMALL_HORIZONTAL: params2.setMargins( 0, UIUtil.convertDPtoPX(getContext(), 5), 0, UIUtil.convertDPtoPX(getContext(), -8) ); break;
                    case TYPE_CALENDAR_TABLE_SMALL_VERTICAL: params2.setMargins( 0, UIUtil.convertDPtoPX(getContext(), 42), 0, UIUtil.convertDPtoPX(getContext(), 30) ); break;
                    case TYPE_CALENDAR_WALL_HANGING: params2.setMargins( 0, UIUtil.convertDPtoPX(getContext(), 23), 0, UIUtil.convertDPtoPX(getContext(), 23) ); break;
                    case TYPE_CALENDAR_SCHEDULER: params2.setMargins( 0, UIUtil.convertDPtoPX(getContext(), 0), 0, UIUtil.convertDPtoPX(getContext(), 0) ); break;
                }
                holder.getSkin().setScaleType( ImageView.ScaleType.FIT_XY );
                param3.setMargins(UIUtil.convertDPtoPX(getContext(), 60),UIUtil.convertDPtoPX(getContext(), 60),UIUtil.convertDPtoPX(getContext(), 60),UIUtil.convertDPtoPX(getContext(), 60));
            }
            else {
                params.setMargins(0, 0, 0, 0);
                params2.setMargins(0, 0, 0, 0);
                holder.getSkin().setScaleType( ImageView.ScaleType.FIT_CENTER );
                if(isPhotobookType) {
                    param3.setMargins(UIUtil.convertDPtoPX(getContext(), 15),UIUtil.convertDPtoPX(getContext(), 15),UIUtil.convertDPtoPX(getContext(), 15),UIUtil.convertDPtoPX(getContext(), 15));
                }
            }
            holder.getImage().setLayoutParams( params );
            holder.getSkin().setLayoutParams( params2 );
            holder.getContentLayout().setLayoutParams(param3);

            // 레더커버일 경우 처음과 마지막 썸네일을 교체
            final boolean needShadow = item != null && item.getItems() != null && isPhotobookType;
            if( leatherCoverIndex > -1 && (position == 0 || item.getItems().size() - 1 == position) ) {
                StringBuilder sb = new StringBuilder();
                sb.append( SnapsAPI.DOMAIN() ).append( LEATHER_COVER_URL_BASE ).append( "WD".equalsIgnoreCase(type) ? "WIDE" : type ).append( leatherCoverColor[leatherCoverIndex] ).append( position == 0 ? LEATHER_COVER_URL_FRONT : LEATHER_COVER_URL_BACK );
                if( needShadow )
                    synchronizedImageLoader.addShadow( holder.getShadow(), isPhotobookType );

                synchronizedImageLoader.add( sb.toString(), holder.getImage(), false );
            }
            else {
//                if( needShadow )
//                    synchronizedImageLoader.addShadow( holder.getShadow(), isPhotobookType );

                synchronizedImageLoader.add( SnapsAPI.DOMAIN() + item.getItems().get(position), holder.getImage(), false );
            }

            synchronizedImageLoader.start( getContext() );
        }

        @Override
        public int getItemCount() {
            return item == null || item.getItems() == null ? 0 : item.getItems().size();
        }

        @Override
        public void onViewRecycled(ThumbnailHolder holder) {
            super.onViewRecycled(holder);
            UIUtil.clearImage( getContext(), holder.container, false );
        }
    }

    private class ThumbnailHolder extends RecyclerView.ViewHolder {
        private LinearLayout containerV, containerH, contentLayout;
        private RelativeLayout leftSpace, rightSpace, topSpace, bottomSpace, container;
        private ImageView image, skin, shadow;

        public ThumbnailHolder(View itemView) {
            super(itemView);

            containerV = (LinearLayout) itemView.findViewById( R.id.container_v );
            containerH = (LinearLayout) itemView.findViewById( R.id.container_h );
            contentLayout = (LinearLayout) itemView.findViewById(R.id.contentLayout);

            leftSpace = (RelativeLayout) itemView.findViewById( R.id.left_space );
            rightSpace = (RelativeLayout) itemView.findViewById( R.id.right_space );
            topSpace = (RelativeLayout) itemView.findViewById( R.id.top_space );
            bottomSpace = (RelativeLayout) itemView.findViewById( R.id.bottom_space );
            container = (RelativeLayout) itemView.findViewById( R.id.container );
            shadow = (ImageView) itemView.findViewById( R.id.shadow );

            image = (ImageView) itemView.findViewById( R.id.image );
            skin = (ImageView) itemView.findViewById( R.id.skin );
        }

        public LinearLayout getContainerV() { return containerV; }
        public LinearLayout getContainerH() { return containerH; }
        public LinearLayout getContentLayout() { return contentLayout; }
        public RelativeLayout getLeftSpace() { return leftSpace; }
        public RelativeLayout getRightSpace() { return rightSpace; }
        public RelativeLayout getTopSpace() { return topSpace; }
        public RelativeLayout getBottomSpace() { return bottomSpace; }
        public RelativeLayout getContainer() { return container; }
        public ImageView getShadow() { return shadow; }
        public ImageView getImage() { return image; }
        public ImageView getSkin() { return skin; }
    }
}
