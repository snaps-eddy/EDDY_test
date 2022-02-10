package com.snaps.mobile.activity.edit.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.book.SNSBookFragmentActivity;
import com.snaps.mobile.activity.book.SNSBookRecorder.SNSBookInfo;
import com.snaps.mobile.tutorial.SnapsTutorialAttribute;
import com.snaps.mobile.tutorial.SnapsTutorialConstants;
import com.snaps.mobile.tutorial.new_tooltip_tutorial.SnapsTutorialUtil;

public class DialogSNSBookLoadComplateView extends Dialog implements DialogInterface.OnKeyListener {
    private static final String TAG = DialogSNSBookLoadComplateView.class.getSimpleName();
    final int[] RECT_PIXEL_SIZE = {70, 70, 70, 70};

    // image
    private SNSBookInfo snsBookInfo = null;
    private Activity activity = null;
    private int type;
    private TextView tvOkBtn = null;
    private Context context = null;

    public DialogSNSBookLoadComplateView(Activity activity, Context context, int type) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        this.type = type;
        this.context = context;
        this.activity = activity;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setCancelable(false);

        int layoutId = R.layout.dialog_sns_book_load_complate;

        /** Design the dialog in main.xml file */
        setContentView(layoutId);

        tvOkBtn = (TextView) findViewById(R.id.dialog_sns_book_load_complate_ok_btn);
        tvOkBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    DialogSNSBookLoadComplateView.this.dismiss();
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        });
    }

    public SNSBookInfo getDatas() {
        return snsBookInfo;
    }

    public void setDatas(SNSBookInfo snsBookInfo) {
        if (snsBookInfo == null)
            return;

        this.snsBookInfo = snsBookInfo;

        String thumbUrl = snsBookInfo.getThumbUrl();
        String userName = snsBookInfo.getUserName();
        String period = snsBookInfo.getPeriod();
        String coverType = snsBookInfo.getCoverType();
        String paperType = snsBookInfo.getPaperType();
        String pageCount = snsBookInfo.getPageCount();
        String priceOrigin = snsBookInfo.getPriceOrigin();
        String priceSale = snsBookInfo.getPriceSale();

        ImageView ivThumbnail = (ImageView) findViewById(R.id.dialog_sns_book_load_complate_img);

        TextView tvUserName = (TextView) findViewById(R.id.dialog_sns_book_load_complate_name_tv);
        TextView bookTypeName = (TextView) findViewById(R.id.dialog_sns_book_type_text);
        TextView tvPeriod = (TextView) findViewById(R.id.dialog_sns_book_load_complate_period_tv);
        TextView tvCoverType = (TextView) findViewById(R.id.dialog_sns_book_load_complate_cover_type_tv);
        TextView tvPageCount = (TextView) findViewById(R.id.dialog_sns_book_load_complate_page_count_tv);
        TextView tvPriceOrigin = (TextView) findViewById(R.id.dialog_sns_book_load_complate_price_origin_tv);
        TextView tvPriceSale = (TextView) findViewById(R.id.dialog_sns_book_load_complate_price_sale_tv);

        ivThumbnail.getLayoutParams().width = UIUtil.convertDPtoPX(context, RECT_PIXEL_SIZE[type]);
        ivThumbnail.getLayoutParams().height = UIUtil.convertDPtoPX(context, RECT_PIXEL_SIZE[type]);

        if (thumbUrl != null) {
//			if (getContext() != null && getContext() instanceof Activity) {
//				ImageLoader.asyncDisplayCircleCropImage((Activity)getContext(), thumbUrl, ivThumbnail);
//			}
            if (activity != null && activity instanceof Activity) {
                ImageLoader.asyncDisplayCircleCropImage(activity, thumbUrl, ivThumbnail);
            }
        }

        if (userName != null)
            tvUserName.setText(userName);

        if (period != null)
            tvPeriod.setText(period);

        if (coverType != null) {

            if (paperType != null) {
                tvCoverType.setText(coverType.replace(",", "") + "/" + paperType);
            } else {
                tvCoverType.setText(coverType.replace(",", ""));
            }

        }
        int typeTextResId = R.string.snaps_diary_book;
        //findViewById( R.id.profile_layout ).setBackgroundColor( profileBgColor );
        ((TextView) findViewById(R.id.dialog_sns_book_type_text)).setText(context.getString(typeTextResId));

        if (pageCount != null)
            tvPageCount.setText(pageCount + "page");

        if (priceOrigin != null) {
            if (priceSale != null && priceOrigin.equals(priceSale)) {
                tvPriceOrigin.setVisibility(View.GONE);
            } else {
                tvPriceOrigin.setText(priceOrigin);
                tvPriceOrigin.setPaintFlags(tvPriceOrigin.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }

        if (priceSale != null)
            tvPriceSale.setText(priceSale);

    }

    public void showDialog() {
        if (context == null
                || (context instanceof Activity && ((Activity) context).isFinishing()) || (Build.VERSION.SDK_INT >= 17 && ((Activity) context).isDestroyed()))
            return;
        this.show();
    }

    @Override
    public void show() {
        super.show();
        SnapsTutorialUtil.showTooltipDialog(getWindow(), activity, new SnapsTutorialAttribute.Builder().setText(getContext().getString(R.string.tutorial_preview_touch))
                .setViewPosition(SnapsTutorialConstants.eTUTORIAL_VIEW_POSITION.TOP)
                .setTopMargin(UIUtil.convertDPtoPX(getContext(), -6))
                .setTargetView(tvOkBtn).create());
    }

    @Override
    public void onBackPressed() {
        // Back Key 방지.
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return false;
    }

}
