package com.snaps.mobile.activity.detail.layouts;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductDetail;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductDetailItem;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

/**
 * Created by songhw on 2016. 10. 24..
 */
public class DetailLayout extends LinkedLayout {
    private static final String TAG = DetailLayout.class.getSimpleName();

    private SnapsProductDetail detail;
    private ViewGroup parent;

    private DetailLayout(Context context) {
        super(context);
    }

    public static DetailLayout createInstance(Context context, LayoutRequestReciever reciever) {
        DetailLayout instance = new DetailLayout(context);
        instance.type = Type.Selector;
        instance.reciever = reciever;
        return instance;
    }

    @Override
    public void draw(ViewGroup parent, Object data, int headViewId, int id) {
        if( !(data instanceof SnapsProductDetail) ) return;
        detail = (SnapsProductDetail) data;
        this.parent = parent;

        String name = detail.getTitle();
        List<SnapsProductDetailItem> items = detail.getItems();

        LayoutInflater inflater = LayoutInflater.from( getContext() );
        ViewGroup container = (ViewGroup) inflater.inflate(R.layout.detail_layout_detail, this);
        ( (TextView) container.findViewById(R.id.title) ).setText( name );
        LinearLayout contentLayout = (LinearLayout) container.findViewById( R.id.container );
        LinearLayout infoLayout = (LinearLayout) container.findViewById( R.id.info_container );
        LinearLayout layout;
        SnapsProductDetailItem item;
        TextView content;
        boolean isLinkText;
        String strikeText;
        boolean hasWaringText = false;
        for( int i = 0; i < items.size(); ++i ) {
            item = items.get(i);

            if( "warning_text".equalsIgnoreCase(item.getCellType()) && reciever != null ) {
                reciever.createNextLayout( infoLayout, InfoLayout.createInstance(getContext(), reciever), item, id );
                hasWaringText = true;
                continue;
            }
            else if( "label_image".equalsIgnoreCase(item.getCellType()) ) {
                LinearLayout imageLayout = (LinearLayout) inflater.inflate( R.layout.detail_layout_detail_image, null );
                contentLayout.addView( imageLayout );
                ImageLoader.with( getContext() ).load( SnapsAPI.DOMAIN(true) + item.getValue() ).into( (ImageView) imageLayout.findViewById(R.id.image) );
                continue;
            }

            layout = (LinearLayout) inflater.inflate( R.layout.detail_layout_detail_item, null );

            ( (TextView)layout.findViewById(R.id.content_title) ).setText( item.getName() );

            if( !StringUtil.isEmpty(item.getValue()) ) {
                content = (TextView) layout.findViewById(R.id.content);
                isLinkText = item.getCellType().contains("link") && !StringUtil.isEmpty(item.getLinkText()) && !StringUtil.isEmpty(item.getLinkUrl());
                String keyValue  = item.getValue().replace( "/n", "\n" ).replace( "//n", "\n" ).replace( "\\n", "\n" );
                content.setText( isLinkText ? keyValue + " " + item.getLinkText() : keyValue );
                strikeText = "";
                if (item.getValue().contains("|"))
                    strikeText = item.getValue().split("\\|")[1];

                if (isLinkText)
                    setLinkText(content, item.getLinkText(), item.getLinkUrl());
                else if ("label_page_price".equalsIgnoreCase(item.getCellType()) && !StringUtil.isEmpty(strikeText)) {
                    content.setText(item.getValue().split("\\|")[0]);
                    setStrikeText(content, strikeText);
                }
            }

            contentLayout.addView( layout );
        }

        if( !hasWaringText )
            infoLayout.setVisibility( View.GONE );
        parent.addView(this);
    }

    private void refresh() {
        removeAllViews();
        parent.removeView( this );
        draw( parent, detail, headViewId, id );
    }

    public void refresh( HashMap<String, String> datas ) {
        if( datas == null ) return;

        String attribute;
        SnapsProductDetailItem item;
        boolean isChanged = false;
        for( int i = 0; i < detail.getItems().size(); ++i ) {
            item = detail.getItems().get(i);
            attribute = item.getAttribute();
            if( StringUtil.isEmpty(attribute) || !datas.containsKey(attribute) ) continue;
            String data = datas.get( attribute );
            if( !StringUtil.isEmpty(data) )
                data = data.replace( "\\n", "\n" ).replace( "//n", "\n" ).replace( "/n", "\n" );
            item.setValue( data );
            isChanged = true;
        }

        if( isChanged )
            refresh();
    }

    private void setLinkText( TextView v, String str, String url ) {
        if( v == null || v.getText() == null ) return;
        int startIdx = v.getText().toString().indexOf(str);
        int endIdx = startIdx + str.length();
        if( endIdx > v.getText().length() ) return;

        MyCustomSpannable spannable = new MyCustomSpannable( url );
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append( v.getText() );
        sb.setSpan(spannable, startIdx, endIdx, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        v.setText(sb);
        v.setMovementMethod( LinkMovementMethod.getInstance() );
    }

    private void setStrikeText( TextView v, String str ) {
        if( v == null || v.getText() == null ) return;
        int startIdx = v.getText().toString().indexOf(str);
        int endIdx = startIdx + str.length();
        if( startIdx < 0 || endIdx > v.getText().length() ) return;

        StrikethroughSpan spannable = new StrikethroughSpan();
        SpannableStringBuilder sb = new SpannableStringBuilder();
        sb.append( v.getText() );
        sb.setSpan(spannable, startIdx, endIdx  , Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        v.setText(sb);
        v.setMovementMethod( LinkMovementMethod.getInstance() );
    }

    private class MyCustomSpannable extends ClickableSpan {
        private String url;
        public MyCustomSpannable( String url ) {
            try {
                this.url = url.startsWith( "snapsapp" ) ? url : ("snapsapp://openAppPopup?openUrl=" + URLEncoder.encode(url, "UTF-8") );
            } catch (UnsupportedEncodingException e) {
                Dlog.e(TAG, e);
                this.url = url;
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(0xFF999999);
            ds.setUnderlineText(true);
        }

        @Override
        public void onClick(View widget) {
            if( reciever != null && !StringUtil.isEmpty(url) )
                reciever.openUrl( url, true );

        }

        public String getUrl() {
            return url;
        }
    }
}
