package com.snaps.mobile.activity.detail;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.snaps.common.utils.animation.SnapsAnimationHandler;
import com.snaps.common.utils.animation.SnapsFrameAnimation;
import com.snaps.common.utils.animation.frame_animation.SnapsFrameAnimationResFactory;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.DataTransManager;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.SnsFactory;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;
import com.snaps.mobile.activity.detail.layouts.AmountLayout;
import com.snaps.mobile.activity.detail.layouts.ColorPickerLayout;
import com.snaps.mobile.activity.detail.layouts.DatePickerLayout;
import com.snaps.mobile.activity.detail.layouts.DetailLayout;
import com.snaps.mobile.activity.detail.layouts.FrameTypeLayout;
import com.snaps.mobile.activity.detail.layouts.InputLayout;
import com.snaps.mobile.activity.detail.layouts.LinkedLayout;
import com.snaps.mobile.activity.detail.layouts.MonthPickerLayout;
import com.snaps.mobile.activity.detail.layouts.PageTypeLayout;
import com.snaps.mobile.activity.detail.layouts.PremiumLayout;
import com.snaps.mobile.activity.detail.layouts.PriceLayout;
import com.snaps.mobile.activity.detail.layouts.SelectorLayout;
import com.snaps.mobile.activity.detail.layouts.SelectorNormalOptionLayout;
import com.snaps.mobile.activity.detail.layouts.ThumbnailLayout;
import com.snaps.mobile.activity.detail.layouts.TitleLayout;
import com.snaps.mobile.activity.google_style_image_selector.utils.ImageSelectUtils;
import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
import com.snaps.mobile.activity.themebook.PhotobookCommonUtils;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsProductListParams;
import com.snaps.mobile.activity.webview.PopupWebviewActivity;
import com.snaps.mobile.activity.webview.ZoomProductWebviewActivity;
import com.snaps.mobile.base.SnapsBaseFragmentActivity;
import com.snaps.mobile.component.SnapsNativeListViewProcess;
import com.snaps.mobile.product_native_ui.interfaces.ISnapsProductOptionCellConstants;
import com.snaps.mobile.product_native_ui.json.SnapsProductNativeUIBaseResultJson;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductDetail;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductNormalOption;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductNormalOptionItem;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionBaseCell;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionCommonValue;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionDetailValue;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductOptionPrice;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductPremium;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductRoot;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductThumbnail;
import com.snaps.mobile.product_native_ui.util.SnapsNativeUIManager;
import com.snaps.mobile.product_native_ui.util.SnapsProductNativeUIUtil;
import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;
import com.snaps.mobile.utils.ui.UrlUtil;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import errorhandle.SnapsAssert;
import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;
import errorhandle.logger.web.WebLogConstants;
import errorhandle.logger.web.request.WebLogRequestBuilder;
import font.FTextView;

import static com.snaps.mobile.activity.detail.layouts.PageTypeLayout.ACCORDION_CARD;
import static com.snaps.mobile.activity.detail.layouts.PageTypeLayout.ID_PHOTO;
import static com.snaps.mobile.activity.detail.layouts.PageTypeLayout.NOTE_TYPE;
import static com.snaps.mobile.activity.detail.layouts.PageTypeLayout.SPRING_NOTE_TYPE;


public class DetailProductNativeActivity extends SnapsBaseFragmentActivity implements LayoutRequestReciever, View.OnClickListener, GoHomeOpserver.OnGoHomeOpserver {
    private static final String TAG = DetailProductNativeActivity.class.getSimpleName();

    private static final String INTENT_KEY_LIST_PARAMETERS = "intent_key_list_parameters";

    private static final String[][] prmcTmplCode = { // 다크브라운, 라이트브라운, 레드, 그레이, 블랙, 에메랄드
        { "045003017192", "045003017194", "045003017193", "045003017801", "045003017190", "045003017191" }, // SQ
        { "045003017197", "045003017199", "045003017198", "045003017802", "045003017195", "045003017196" }, // A4
        { "045003017202", "045003017204", "045003017203", "045003017803", "045003017200", "045003017201" } // WD
    };
    private static final ArrayList<String> typeString = new ArrayList<String>() {{
        add("SQ");
        add("A4");
        add("WD");
    }};

    private IKakao kakao = null;
    private IFacebook facebook = null;

    private Integer lastCreatedIndex = Integer.MIN_VALUE;

    private HashMap<Integer, LinkedLayout> layoutMap;
    private HashMap<String, String> selectedDatas; // 선택된 값
    private HashMap<String, String> requiredDatas; // 필수 값

    private LinearLayout thumbLayout, normalOptContainer, prodOptLayout, premiumLayout, detailLayout;
    private ScrollView scrollView;
    private WebView popupWebView;
    private boolean makeButtonEnable;
    private boolean isViewPagerTouched;
    private float firstPosX = 0, firstPosY = 0;
    private int scrollCheckCount = -1;
    private static final int SCROLL_TYPE_CHECK_COUNT = 5;

    private int detailLayoutId = 0, thumbnailLayoutId = 0, leatherCoverColorIndex = -1;

    private PointF ptActionDown = new PointF();
    private String productCode;
    private String classCode;
    private String thumbnailType = "";
    private String infoUrl;

    private SnapsFrameAnimation frameAnimation = null;

    public static Intent getIntent( Context context, SnapsProductListParams params ) {
        Intent intent = new Intent( context, DetailProductNativeActivity.class );
        Bundle bundle = new Bundle();
        bundle.putSerializable(INTENT_KEY_LIST_PARAMETERS, params);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
			getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
					WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		}

		super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

		setContentView(R.layout.activity_native_detail);

        if (!SnapsTPAppManager.isThirdPartyApp(this)) {
            if (Config.isFacebookService()) {
                facebook = SnsFactory.getInstance().queryInteface();
                facebook.init(this);
            }

            kakao = SnsFactory.getInstance().queryIntefaceKakao();
        }

        requestData();

        GoHomeOpserver.addGoHomeListener(this);
	}

    @Override
    public void onGoHome() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImageSelectUtils.initPhotoLastSelectedHistory();
        PhotobookCommonUtils.initProductEditInfo();
        DataTransManager.releaseCloneImageSelectDataHolder();
        SmartSnapsManager.finalizeInstance();

        startPreloadThumbImageAnimation();

        restartThumbnailImageLoad();
    }

    private void startPreloadThumbImageAnimation() {
        try {
            ImageView preloadThumbImage = (ImageView) findViewById( R.id.preload_thumb_image );
            frameAnimation = SnapsAnimationHandler.startFrameAnimation(this, preloadThumbImage, SnapsFrameAnimationResFactory.eSnapsFrameAnimation.NATIVE_DETAIL_DEFAULT_PAGE);
        } catch (Exception e) { Dlog.e(TAG, e); }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (frameAnimation != null) {
            frameAnimation.release();
        }
    }

    private void requestData() {
        if (getIntent() != null) {
            getIntent().getExtras().setClassLoader(SnapsProductListParams.class.getClassLoader());
            final SnapsProductListParams params = (SnapsProductListParams) getIntent().getSerializableExtra( INTENT_KEY_LIST_PARAMETERS );
            classCode = params.getClssCode();
            productCode = params.getProdCode();
            SnapsProductNativeUIUtil.requestProductDetail(this, params, new SnapsProductNativeUIUtil.ISnapsProductNativeUIInterfaceCallback() {
                @Override
                public void onNativeProductInfoInterfaceResult(boolean result, SnapsProductNativeUIBaseResultJson resultObj) {
                    ( findViewById(R.id.root) ).setVisibility( result ? View.VISIBLE : View.GONE );
                    ( findViewById(R.id.pre_load_area) ).setVisibility( result ? View.GONE : View.VISIBLE );
                    ( findViewById(R.id.network_error_layout) ).setVisibility( result ? View.GONE : View.VISIBLE );
                    if( result ) init( resultObj, params.getTitle(), params.getInfoUrl(), params.isOuter() );
                }
            } );

            addSnapsLogFromParams(params);

            try {
                SnapsLogger.sendWebLog(WebLogRequestBuilder.createBuilderWithLogName(WebLogConstants.eWebLogName.v1_product_click)
                        .appendPayload(WebLogConstants.eWebLogPayloadType.PRODUCT_CLICK, params.getDetailInterfaceUrl()));
            }catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
    }

    private void addSnapsLogFromParams(SnapsProductListParams params) {
        if (params == null) return;

        try {
            SnapsLogger.appendTextLog("*** Detail title : ", params.getTitle());
            SnapsLogger.appendTextLog("*** Detail interface : ", params.getDetailInterfaceUrl());
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void init( SnapsProductNativeUIBaseResultJson obj, String templateTitle, String infoUrl, boolean isPremium ) {
        SnapsNativeUIManager menuDataManager = SnapsNativeUIManager.getInstance();
        if (menuDataManager != null) {
            menuDataManager.initNativeListViewProcess(this);
        }

        boolean showInfoButton = !StringUtil.isEmpty( infoUrl );
        this.infoUrl = infoUrl;

        ImageView infoView = (ImageView) findViewById( R.id.btnTitleInfo );
        infoView.setVisibility(showInfoButton ? View.VISIBLE : View.GONE);
        infoView.setClickable(showInfoButton);
        infoView.setOnClickListener(showInfoButton ? this : null);
        infoView.setAlpha( 1f );
        infoView.setColorFilter( 0xFF000000 );

        findViewById( R.id.btnPremium ).setVisibility(isPremium ? View.VISIBLE : View.GONE);

        ( (FTextView) findViewById(R.id.txtTitleText) ).setText(StringUtil.isEmpty(templateTitle) ? "" : templateTitle);
        Config.setPROD_NAME(templateTitle);

        setMakeButtonClickable( false );

        scrollView = (ScrollView) findViewById( R.id.scroll_view );
        thumbLayout = (LinearLayout) findViewById( R.id.thumbnail_layout );
        normalOptContainer = (LinearLayout) findViewById( R.id.normal_option_container );
        prodOptLayout = (LinearLayout) findViewById( R.id.product_opt_layout );
        premiumLayout = (LinearLayout) findViewById( R.id.premium_layout );
        detailLayout = (LinearLayout) findViewById( R.id.detail_layout );

        layoutMap = new HashMap<Integer, LinkedLayout>();
        selectedDatas = new HashMap<String, String>();

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    return postTouchEventToThumbLayout(event);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
                return false;
            }
        });

        SnapsProductRoot root = (SnapsProductRoot) obj;
        List<SnapsProductNormalOption> normalOptions = new ArrayList<SnapsProductNormalOption>();
        List<SnapsProductNormalOption> tempNormalOptions = root.getNormalOptionList();
        for( SnapsProductNormalOption option : tempNormalOptions ) {
            option.deleteNullObjects();
            if( option != null )
                normalOptions.add( option );
        }

        LinkedLayout layout;
        int layoutId = 0;
        if( normalOptions != null && normalOptions.size() > 0 ) {
            LinearLayout normalOptLayout;
            List<SnapsProductNormalOptionItem> optionList;
            String title;
            for( int i = 0; i < normalOptions.size(); ++i ) {
                normalOptLayout = new LinearLayout( this );
                normalOptLayout.setOrientation( LinearLayout.VERTICAL );
                normalOptContainer.addView( normalOptLayout );
                title = normalOptions.get(i).getTitle();
                optionList = normalOptions.get( i ).getItems();
                if( !StringUtil.isEmpty(title) )
                    layoutId = createNextLayout( normalOptLayout, TitleLayout.createInstance(this, this), title, 0 );

                for( int j = 0; j < optionList.size(); ++j )
                    layoutId = createNextLayout( normalOptLayout, getLayoutByCellType(optionList.get(j).getCellType()), optionList.get(j), layoutId );

                if( layoutMap.containsKey(layoutId) )
                    layoutMap.get( layoutId ).setBottomLineVisibility( false );
            }
        }
        else {
            normalOptContainer.setVisibility( View.GONE );
            prodOptLayout.setPadding( 0, 0, 0, 0 );
        }

        SnapsProductPremium premium = root.getPremium();
        if( premium != null && premium.getItems() != null && premium.getItems().size() > 0 ) {
            premiumLayout.setVisibility(View.VISIBLE);
            layout = PremiumLayout.createInstance( this, this );
            layout.draw( premiumLayout, premium, 0, 0 );
        }
        else premiumLayout.setVisibility( View.GONE );

        SnapsProductDetail detail = root.getDetail();
        if( detail != null && detail.getItems() != null && detail.getItems().size() > 0 ) {
            detailLayoutId = createLayoutId();
            layout = DetailLayout.createInstance( this, this );
            layout.draw( detailLayout, detail, 0, detailLayoutId );
            layoutMap.put( detailLayoutId, layout );
        }
        else detailLayout.setVisibility( View.GONE );

        SnapsProductThumbnail thumbnail = root.getThumnail();
        if( thumbnail != null )
            thumbnailLayoutId = createNextLayout(thumbLayout, ThumbnailLayout.createInstance(this, this, classCode), thumbnail, 0);
        else {
            thumbLayout.setVisibility( View.GONE );
            findViewById( R.id.thumbnail_fake_layout ).setVisibility( View.GONE );
        }

        SnapsProductOptionBaseCell productOption = root.getProductOptionControl();
        if( productOption != null ) {
            if( !StringUtil.isEmpty(productOption.getTitle()) )
                layoutId = createNextLayout( prodOptLayout, TitleLayout.createInstance(this, this), productOption.getTitle(), 0);

            if( !StringUtil.isEmpty(productOption.getCellType()))
                createNextLayout(prodOptLayout, getLayoutByCellType(productOption.getCellType()), productOption, layoutId);
            else if( productOption.getValueList() != null && productOption.getValueList().size() > 0 && !StringUtil.isEmpty(productOption.getValueList().get(0).getCmd()) ) {
                itemSelected( productOption.getValueList().get(0), prodOptLayout, 0 );
                setMakeButtonClickable( checkAllSelected() );
            }
        }
        else prodOptLayout.setVisibility( View.GONE );

        final View makeButton = findViewById( R.id.make_button );
        if(!TextUtils.isEmpty(productCode) && Const_PRODUCT.isAccessoryProductGroup(productCode)) {
            ((TextView)makeButton).setText(getString(R.string.accessory_go_to_cart));
        }
        ValueAnimator anim = ValueAnimator.ofInt( UIUtil.convertDPtoPX(this, -48), 0 );
        anim.setDuration( 1000 );
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) makeButton.getLayoutParams();
                params.bottomMargin = (Integer) animation.getAnimatedValue();
                makeButton.setLayoutParams( params );
            }
        });
        anim.start();
    }

    private boolean postTouchEventToThumbLayout(MotionEvent event) throws Exception {
        float posX, posY, thumbX, thumbY;
        posX = event.getX();
        posY = event.getY();
        thumbX = thumbLayout.getX();
        thumbY = thumbLayout.getY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isViewPagerTouched = posX > thumbX && posX < thumbX + thumbLayout.getWidth() && posY > thumbY && posY < thumbY + thumbLayout.getHeight();
                firstPosX = event.getX();
                firstPosY = event.getY();
                scrollCheckCount = 0;

                ptActionDown.set(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                if (checkClickAction(event) && isThumbnailAreaClicked(event)) {
                    handleThumbnailViewOnClick();
                }
                postTouchOtherEventToThumbLayout(event);
                break;
            default:
                postTouchOtherEventToThumbLayout(event);
                break;
        }

        if( isViewPagerTouched )
            thumbLayout.dispatchTouchEvent( event );

        return isViewPagerTouched && scrollCheckCount < 0;
    }

    private void handleThumbnailViewOnClick() {
        try {
            ThumbnailLayout thumbnailLayout = getThumbnailLayout();
            if (thumbnailLayout != null)
                thumbnailLayout.startProductZoomActivity();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void postTouchOtherEventToThumbLayout(MotionEvent event) {
        if( scrollCheckCount > -1 && isViewPagerTouched ) {
            scrollCheckCount++;
            if (scrollCheckCount > 3) {
                scrollCheckCount = -1;
                if (Math.abs(event.getX() - firstPosX) < Math.abs(event.getY() - firstPosY)) {
                    MotionEvent tempEvent = MotionEvent.obtain( event.getDownTime(), event.getEventTime(), MotionEvent.ACTION_CANCEL, event.getX(), event.getY(), event.getMetaState() );
                    thumbLayout.dispatchTouchEvent( tempEvent );
                    isViewPagerTouched = false;
                    firstPosX = 0;
                    firstPosY = 0;
                }
            }
        }
    }

    private boolean checkClickAction(MotionEvent event) {
        if (event == null || ptActionDown == null) return false;
        float moveX = Math.abs(ptActionDown.x - event.getX());
        float moveY = Math.abs(ptActionDown.y - event.getY());
        return moveX < 20 && moveY < 20;
    }

    private boolean isThumbnailAreaClicked(MotionEvent event) {
        if (thumbLayout == null || scrollView == null) return false;
        int thumbnailViewBottomOffsetY = (int) (thumbLayout.getY() + thumbLayout.getHeight());
        boolean isHideThumbnailAreaByScrollView = scrollView.getScrollY() > thumbnailViewBottomOffsetY/3;
        return !isHideThumbnailAreaByScrollView && event != null && event.getY() <= thumbnailViewBottomOffsetY;
    }

    private void setMakeButtonClickable( final boolean flag ) {
        if( makeButtonEnable == flag ) return;
        makeButtonEnable = flag;

        final View makeButton = findViewById( R.id.make_button );
        makeButton.setBackgroundColor( ContextCompat.getColor(this, flag ? R.color.detail_page_make_button_inactivated : R.color.detail_page_make_button_activated) );
        makeButton.setTag( flag );

        final float[] from = new float[3], to = new float[3];
        Color.colorToHSV( ContextCompat.getColor(this, flag ? R.color.detail_page_make_button_inactivated : R.color.detail_page_make_button_activated), from );
        Color.colorToHSV( ContextCompat.getColor(this, flag ? R.color.detail_page_make_button_activated : R.color.detail_page_make_button_inactivated), to );

        final float[] hsv  = new float[3];
        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                hsv[0] = from[0] + (to[0] - from[0])*animation.getAnimatedFraction();
                hsv[1] = from[1] + (to[1] - from[1])*animation.getAnimatedFraction();
                hsv[2] = from[2] + (to[2] - from[2])*animation.getAnimatedFraction();

                makeButton.setBackgroundColor(Color.HSVToColor(hsv));
            }
        });
        anim.start();
    }

    @Override
    public void onClick( View v ) {
        if( v.getId() == R.id.btnTitleLeft )
            onBackPressed();
        else if( v.getId() == R.id.make_button ) {
            if( findViewById(R.id.dim_area).getVisibility() == View.VISIBLE ) return;

            boolean flag = v.getTag() != null && (boolean)v.getTag();
            if( flag ) {
                SnapsNativeUIManager menuDataManager = SnapsNativeUIManager.getInstance();
                if( menuDataManager != null) {
                    SnapsNativeListViewProcess listViewProcess = menuDataManager.getNativeListViewProcess(this);
                    if (listViewProcess != null) {
                        if (selectedDatas != null && selectedDatas.containsKey("cmd") && !StringUtil.isEmpty(selectedDatas.get("cmd"))) {
                            String finalCommand = null;
                            try {
                                finalCommand = makeCommand( selectedDatas.get("cmd") );
                                listViewProcess.shouldOverrideUrlLoading( null, finalCommand );
                            } catch (Exception e) {
                                Dlog.e(TAG, e);
                            }
                        }
                    }
                }

            }
            else {
                if(requiredDatas == null) return;
                int messageStrResId = -1;
                if( requiredDatas.containsKey("startDate") && StringUtil.isEmpty(requiredDatas.get("startDate")) )
                    messageStrResId = R.string.selectStartYearMonthDay;
                else if( requiredDatas.containsKey("endDate") && StringUtil.isEmpty(requiredDatas.get("endDate")) )
                    messageStrResId = R.string.selectEndYearMonthDay;
                else if( requiredDatas.containsKey("year") && StringUtil.isEmpty(requiredDatas.get("year")) || requiredDatas.containsKey("month") && StringUtil.isEmpty(requiredDatas.get("month")) )
                    messageStrResId = R.string.selectMonthStart;
                else if( requiredDatas.containsKey("title") && StringUtil.isEmpty(requiredDatas.get("title")) )
                    messageStrResId = R.string.enterCoverTitle;
                else if( requiredDatas.containsKey("page_type_index") && StringUtil.isEmpty(requiredDatas.get("page_type_index")) )
                    messageStrResId = R.string.selectInnerPaperType;
                else if( requiredDatas.containsKey("card_type_index") && StringUtil.isEmpty(requiredDatas.get("card_type_index")) || requiredDatas.containsKey("glossy_type_index") && StringUtil.isEmpty(requiredDatas.get("glossy_type_index")) )
                    messageStrResId = R.string.select_type;
                else if( requiredDatas.containsKey("frame_type_index") && StringUtil.isEmpty(requiredDatas.get("frame_type_index")) )
                    messageStrResId = R.string.foundation_option_select;
                if( messageStrResId > -1 )
                    MessageUtil.alert( this, messageStrResId );
            }

        }
        else if( v.getId() == R.id.btn_retry )
            requestData();
        else if( v.getId() == R.id.btnTitleInfo ) {
            if( !StringUtil.isEmpty(infoUrl) ) // 다시한번 확인
                openUrl( infoUrl, true );
        }
        else if( v.getId() == R.id.dim_area )
            setDimVisiblity( false );
    }

    private String makeCommand( String cmd ) throws Exception {
        if( requiredDatas == null ) return cmd;

        Uri uri = Uri.parse( cmd );

        ArrayList<BasicNameValuePair> newParams = new ArrayList<BasicNameValuePair>();
        for( String key : requiredDatas.keySet() ) {
            if( "page_type_index".equalsIgnoreCase(key) && requiredDatas.containsKey("page_type_index") ) {
                if (StringUtil.isEmpty(requiredDatas.get("page_type_index"))) continue;;
                switch ( Integer.parseInt(requiredDatas.get("page_type_index")) ) {
                    case 0:
                        newParams.add( new BasicNameValuePair("paperCode", "160001"));
                        break;
                    case 1:
                        newParams.add( new BasicNameValuePair("paperCode", "160002"));
                        break;
                    case 2:
                        newParams.add( new BasicNameValuePair("paperCode", "160008"));
                        break;
                }
            } else if("card_type_index".equalsIgnoreCase(key) && requiredDatas.containsKey("card_type_index") ) {
                if (StringUtil.isEmpty(requiredDatas.get("card_type_index"))) continue;;
                switch ( Integer.parseInt(requiredDatas.get("card_type_index")) ) {
                    case 0:
                        newParams.add( new BasicNameValuePair("frameType", "385001"));
                        break;
                    case 1:
                        newParams.add( new BasicNameValuePair("frameType", "385002"));
                        break;
                }
            } else if("glossy_type_index".equalsIgnoreCase(key) && requiredDatas.containsKey("glossy_type_index") ) {
                if (StringUtil.isEmpty(requiredDatas.get("glossy_type_index"))) continue;
                switch (Integer.parseInt(requiredDatas.get("glossy_type_index"))) {
                    case 0:
                        newParams.add(new BasicNameValuePair("glossytype", "G"));
                        break;
                    case 1:
                        newParams.add(new BasicNameValuePair("glossytype", "M"));
                        break;
                }
            }else if( "leather_color_index".equalsIgnoreCase(key) && requiredDatas.containsKey("leather_color_index") ) {
                if (StringUtil.isEmpty(requiredDatas.get("leather_color_index"))) continue;;
                if (leatherCoverColorIndex > -1 && leatherCoverColorIndex < 6 && typeString.indexOf(thumbnailType) > -1)
                    newParams.add(new BasicNameValuePair("prmcTmplCode", prmcTmplCode[typeString.indexOf(thumbnailType)][leatherCoverColorIndex]));
            }
            else {
                if (requiredDatas.containsKey(key)) {
                    newParams.add( new BasicNameValuePair(key, requiredDatas.get(key)) );
                }
            }
        }

        uri = UrlUtil.replaceUriParameter( uri, newParams );

        return uri.toString();
    }

    private LinkedLayout getLayoutByCellType( String cellType ) {
        if( "combo".equalsIgnoreCase(cellType) )
            return SelectorNormalOptionLayout.createInstance( this, this );
        else if( "comboBox".equalsIgnoreCase(cellType) )
            return SelectorLayout.createInstance( this, this );
        else if( "paperType".equalsIgnoreCase(cellType) )
            return PageTypeLayout.createInstance( this, this, false,null );
        else if( "paperTypeForSelect".equalsIgnoreCase(cellType) )
            return PageTypeLayout.createInstance( this, this, true,null );
        else if( "note_paperType".equalsIgnoreCase(cellType) )
            return PageTypeLayout.createInstance( this, this, false,NOTE_TYPE );
        else if( "springnote_paperType".equalsIgnoreCase(cellType) )
            return PageTypeLayout.createInstance( this, this, false,SPRING_NOTE_TYPE );
        else if( "accordion_cardType".equalsIgnoreCase(cellType) )
            return PageTypeLayout.createInstance( this, this, false,ACCORDION_CARD );
        else if( "id_photoType".equalsIgnoreCase(cellType) )
            return PageTypeLayout.createInstance( this, this, false,ID_PHOTO );
        else if( "frameType".equalsIgnoreCase(cellType) )
            return FrameTypeLayout.createInstance( this, this, FrameTypeLayout.FRAME_TYPE );
        else if( "cuttingType".equalsIgnoreCase(cellType) )
            return FrameTypeLayout.createInstance( this, this, FrameTypeLayout.CUTTING_TYPE );
        else if( "leatherCover".equalsIgnoreCase(cellType) )
            return ColorPickerLayout.createInstance( this, this );
        else if( "date".equalsIgnoreCase(cellType) )
            return DatePickerLayout.createInstance( this, this );
        else if( "start_month".equalsIgnoreCase(cellType) )
            return MonthPickerLayout.createInstance( this, this );
        else if( "title".equalsIgnoreCase(cellType) )
            return InputLayout.createInstance( this, this );
        else if( "price".equalsIgnoreCase(cellType) )
            return PriceLayout.createInstance( this, this );
        else if( "card_price".equalsIgnoreCase(cellType) )
            return PriceLayout.createInstance( this, this );
        else if( "accessory_price".equalsIgnoreCase(cellType) )
            return PriceLayout.createInstance( this, this );
        else if( "counter".equalsIgnoreCase(cellType) )
            return AmountLayout.createInstance( this, this );
        else
            return null;
    }

    private int createLayoutId() {
        synchronized ( lastCreatedIndex ) {
            lastCreatedIndex ++;
            if( lastCreatedIndex == 0 ) lastCreatedIndex ++;
            return lastCreatedIndex;
        }
    }

    private boolean checkAllSelected() {
        if( requiredDatas == null ) return true;

        for( String key : requiredDatas.keySet() ) {
            if( StringUtil.isEmpty(requiredDatas.get(key)) )
                return false;
        }
        return true;
    }

    @Override
    public int createNextLayout( ViewGroup parent, String type, Object data, int headViewId) {
        return createNextLayout( parent, getLayoutByCellType(type), data, headViewId );
    }

    @Override
    public int createNextLayout( ViewGroup parent, LinkedLayout layout, Object data, int parentId ) {
        int newId = createLayoutId();
        layout.draw( parent, data, parentId, newId );
        layoutMap.put( newId, layout );
        return newId;
    }

    @Override
    public void removeLayout( int id ) {
        if( layoutMap == null || !layoutMap.containsKey(id) ) return;

        LinkedLayout item = layoutMap.get( id );
        if( item == null ) return;
        item.destroy();
        ( (ViewGroup) item.getParent() ).removeView( item );

        layoutMap.remove(id);
    }

    /**
     * 선택된 데이터를 갱신.
     * @param attribute
     * @param value
     * @param required
     */
    @Override
    public void itemSelected( String attribute, String value, boolean required ) {
        if( StringUtil.isEmpty(attribute)  ) return;

        if( required ) {
            if( requiredDatas == null ) requiredDatas = new HashMap<String, String>();
            requiredDatas.remove( attribute );
            requiredDatas.put( attribute, value );
        }
        else {
            if (selectedDatas == null) selectedDatas = new HashMap<String, String>();
            selectedDatas.remove(attribute);
            selectedDatas.put(attribute, value);
        }

        if( detailLayoutId != 0 && layoutMap.containsKey(detailLayoutId) )
            ( (DetailLayout) layoutMap.get(detailLayoutId) ).refresh( selectedDatas );

        setMakeButtonClickable( checkAllSelected() );
    }

    /**
     * 상세 레이아웃을 갱신.
     * @param value
     */
    @Override
    public int itemSelected( SnapsProductOptionCommonValue value, ViewGroup parent, int headViewId ) {
        int tailViewId = 0;

        itemSelected( "cmd", value.getCmd(), false );

        SnapsProductOptionPrice price = value.getPrice();
        if( price != null && price.getValues() != null && !StringUtil.isEmpty(price.getValues().getPrice()) )
            tailViewId = createNextLayout( parent, price.getCellType(), price, headViewId );

        SnapsProductOptionDetailValue detailValue = value.getDetailValue();
        if( detailValue != null ) {
            String leatherColorIdxStr = detailValue.getLeatherCover();
            int tempColorIdx = StringUtil.isEmpty(leatherColorIdxStr) ? -1 : (int) Double.parseDouble(leatherColorIdxStr);
            itemSelected(ISnapsProductOptionCellConstants.KEY_PRODUCT_SIZE, detailValue.getProductSize(), false);
            itemSelected(ISnapsProductOptionCellConstants.KEY_FRAME_SIZE, detailValue.getFrameSize(), false);
            itemSelected(ISnapsProductOptionCellConstants.KEY_USE_IMAGE_CNT, detailValue.getUseImageCnt(), false);
            itemSelected(ISnapsProductOptionCellConstants.KEY_ENABLEPAGE, detailValue.getEnablePage(), false);
            itemSelected(ISnapsProductOptionCellConstants.KEY_PAGE_PRICE, detailValue.getPage_price(), false);
            itemSelected(ISnapsProductOptionCellConstants.KEY_PHOTO_SIZE, detailValue.getPhotoSize(), false);
            itemSelected(ISnapsProductOptionCellConstants.KEY_PRODUCT_MATERIAL, detailValue.getProductMaterial(), false);
            itemSelected(ISnapsProductOptionCellConstants.KEY_PRODUCT_VOLUME, detailValue.getProductVolumn(), false);
            itemSelected(ISnapsProductOptionCellConstants.KEY_LEATHER_COVER, StringUtil.isEmpty(leatherColorIdxStr) ? "" : tempColorIdx + "", false);

            String type = detailValue.getProdForm();
            if ( !StringUtil.isEmpty(type) && layoutMap.containsKey(thumbnailLayoutId) ) {
                thumbnailType = type;
                leatherCoverColorIndex = !selectedDatas.containsKey(ISnapsProductOptionCellConstants.KEY_LEATHER_COVER) ? -1 : tempColorIdx;
                ((ThumbnailLayout) layoutMap.get(thumbnailLayoutId)).setThumbnailTypeIndex( thumbnailType, leatherCoverColorIndex );
            }
        }

        checkSpace();
        return tailViewId;
    }

    private void checkSpace() {
        final RelativeLayout bottomSpace = (RelativeLayout) findViewById( R.id.scroll_bottom_space );
        if( bottomSpace == null ) return;

        bottomSpace.getViewTreeObserver().addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16)
                    bottomSpace.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    bottomSpace.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int screenH = UIUtil.getScreenHeight( DetailProductNativeActivity.this );
                int parentHeight = ( (ViewGroup)bottomSpace.getParent() ).getHeight(); // scroll container의 높이
                int parentHeightExcludeBottomSpace = bottomSpace.getTop(); // space를 제외한 scroll container의 높이
                int thumbHeight = UIUtil.convertDPtoPX( DetailProductNativeActivity.this, 418 );
                int titleHeight = UIUtil.convertDPtoPX( DetailProductNativeActivity.this, 48 );
                int statusBarHeight = 0;
                int resourceId = DetailProductNativeActivity.this.getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0)
                    statusBarHeight = DetailProductNativeActivity.this.getResources().getDimensionPixelSize( resourceId );
                if( parentHeight != 0 && titleHeight + parentHeight - thumbHeight < screenH ) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bottomSpace.getLayoutParams();
                    params.height = screenH - titleHeight - parentHeightExcludeBottomSpace + thumbHeight + UIUtil.convertDPtoPX( DetailProductNativeActivity.this, 8 ) - statusBarHeight;
                    bottomSpace.setLayoutParams( params );
                }
            }
        } );
    }

    private void setDimVisiblity( boolean flag) {
        View dimArea = findViewById( R.id.dim_area );
        dimArea.setVisibility( flag ? View.VISIBLE : View.GONE );
        dimArea.setClickable( flag );
        dimArea.setOnClickListener( flag ? this : null );

        if( !flag && popupWebView != null ) {
            ( (ViewGroup)dimArea ).removeView( popupWebView );
            popupWebView.setOnTouchListener( null );
            popupWebView.destroy();
            popupWebView = null;
        }
    }

    @Override
    public String getSelectedValue( String attributeName ) {
        String value = "";
        if( selectedDatas != null && selectedDatas.containsKey(attributeName) )
            return selectedDatas.get( attributeName );

        if( requiredDatas != null && requiredDatas.containsKey(attributeName) )
            return requiredDatas.get( attributeName );

        return value;
    }

    @Override
    public void openZoomUrl( String url ) {
        suspendThumbnailImageLoad();

        HashMap<String, String> params = UrlUtil.getParameters( url );
        if( !params.containsKey("orientation") ) return;

        url = appendSlideNoWithUrl(url);

        Intent intent = ZoomProductWebviewActivity.getIntent(this, "", url.contains("http") ? url : SnapsAPI.WEB_DOMAIN() + url, true);
        intent.putExtra( "orientation", params.get("orientation") );
        startActivity(intent);
    }

    private void suspendThumbnailImageLoad() {
        try {
            ThumbnailLayout thumbnailLayout = getThumbnailLayout();
            if (thumbnailLayout != null)
                thumbnailLayout.suspendImageLoad();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private void restartThumbnailImageLoad() {
        try {
            ThumbnailLayout thumbnailLayout = getThumbnailLayout();
            if (thumbnailLayout != null)
                thumbnailLayout.restartImageLoad();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    private String appendSlideNoWithUrl(String url) {
        if (StringUtil.isEmpty(url)) return url;
        String pageNumber = "0";
        try {
            pageNumber = getPageNumberStr();
        } catch (Exception e) {
            SnapsAssert.assertException(this, e);
            Dlog.e(TAG, e);
        }
        return String.format("%s&slideNo=%s", url, pageNumber);
    }

    private String getPageNumberStr() throws Exception {
        ThumbnailLayout thumbnailLayout = getThumbnailLayout();
        return thumbnailLayout != null ? String.valueOf(getThumbnailLayout().getViewPagerCurrentPosition()) : "";
    }

    private ThumbnailLayout getThumbnailLayout() throws Exception {
        if (thumbLayout == null) return null;
        return (ThumbnailLayout) thumbLayout.getChildAt(0);
    }

    @Override
    public void openUrl(String url, boolean fullScreen) {
        if( !url.contains("http") && !url.contains("snapsapp") )
            url = SnapsAPI.WEB_DOMAIN() + url;
        if( fullScreen ) {
            SnapsNativeUIManager menuDataManager = SnapsNativeUIManager.getInstance();
            if( menuDataManager != null ) {
                SnapsNativeListViewProcess listViewProcess = menuDataManager.getNativeListViewProcess(this);
                if (listViewProcess != null)
                    listViewProcess.shouldOverrideUrlLoading( null, url );
            }
            else {
                Intent intent = PopupWebviewActivity.getIntent(this, url);
                startActivity(intent);
            }
        }
        else {
            RelativeLayout dimArea = (RelativeLayout) findViewById( R.id.dim_area );
            if( dimArea != null ) {
                popupWebView = new WebView( this );
                popupWebView.setOnTouchListener( new View.OnTouchListener() {
                    public final static int FINGER_RELEASED = 0;
                    public final static int FINGER_TOUCHED = 1;
                    public final static int FINGER_UNDEFINED = 2;

                    private int fingerState = FINGER_RELEASED;

                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                if (fingerState == FINGER_RELEASED) fingerState = FINGER_TOUCHED;
                                else fingerState = FINGER_UNDEFINED;
                                break;

                            case MotionEvent.ACTION_UP:
                                if(fingerState == FINGER_TOUCHED) {
                                    fingerState = FINGER_RELEASED;
                                    setDimVisiblity( false );
                                }
                                else fingerState = FINGER_UNDEFINED;
                                break;

                            case MotionEvent.ACTION_MOVE:
                                if (fingerState == FINGER_RELEASED || fingerState == FINGER_TOUCHED) fingerState = FINGER_TOUCHED;
                                else fingerState = FINGER_UNDEFINED;
                                break;

                            default:
                                fingerState = FINGER_UNDEFINED;

                        }
                        return false;
                    }
                } );
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
                params.addRule( RelativeLayout.CENTER_IN_PARENT );
                params.addRule( Gravity.CENTER );
                params.setMargins( UIUtil.convertDPtoPX(this, 30), 0, UIUtil.convertDPtoPX(this, 30), 0 );
                popupWebView.setLayoutParams( params );
                dimArea.addView( popupWebView );
                popupWebView.loadUrl( url );

                setDimVisiblity( true );
            }
        }
    }

    @Override
    public void onEditTextFocused(final EditText v) {
        if( scrollView == null || isFinishing() ) return;

        final View makingButton = findViewById(R.id.make_button);
        final int makingButtonOriginPos = makingButton.getTop();

        makingButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if( makingButtonOriginPos != makingButton.getTop() ) { // 이동했을 때 처리.
                    if (Build.VERSION.SDK_INT < 16)
                        makingButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    else
                        makingButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    final int editTextBottom = getGlobalPosition( v ) + v.getHeight() - scrollView.getScrollY();
                    final int makingButtonTop = getGlobalPosition( makingButton );
                    if( editTextBottom > makingButtonTop ) {
                        DetailProductNativeActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                scrollView.smoothScrollTo( 0, scrollView.getScrollY() + editTextBottom - makingButtonTop  );
                            }
                        });
                    }
                }
            }
        });
    }

    private int getGlobalPosition( View v ) {
        int position = v.getTop();
        ViewParent parent = v.getParent();
        while( parent instanceof ViewGroup ) {
            position += ( (ViewGroup) parent ).getTop();
            parent = parent.getParent();
        }
        return position;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ViewGroup rootview = (ViewGroup) findViewById(android.R.id.content);
        if( rootview != null ) UIUtil.clearImage( this, rootview, false );
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (facebook != null)
            facebook.onActivityResult(this, requestCode, resultCode, data);
    }
}
