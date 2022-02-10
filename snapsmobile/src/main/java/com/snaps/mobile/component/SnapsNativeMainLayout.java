package com.snaps.mobile.component;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.ui.menu.UIResourcesManager;
import com.snaps.mobile.activity.ui.menu.json.SnapsPriceDetailResponse;
import com.snaps.mobile.activity.ui.menu.json.SnapsStoreProductResponse;
import com.snaps.mobile.activity.ui.menu.json.SnapsStoreProductResponse.SnapsStoreProductEmergency;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.interfaces.OnPageScrollListener;

import java.util.List;

import font.FTextView;

public class SnapsNativeMainLayout extends RelativeLayout {
	private static final String TAG = SnapsNativeMainLayout.class.getSimpleName();

	private final int CLICK_ALLOW_PIXEL = UIUtil.convertDPtoPX( //하위 뷰에 클릭 이벤트를 보내 주기 위해 터치 이벤트로 클릭이벤트를 구현함..
			getContext(), 20);
	
	private LayoutInflater mInflater = null;
	private OnPageScrollListener pageScrollListener = null;

	private int scrollPos = 0;

	private ObserveScrollingScrollView scrollViewLayout;
	private boolean isInitializedUI = false;
	
	private Context mContext = null;
	
	private SnapsShouldOverrideUrlLoader shouldOverrideUrlLoader = null;
	private int m_iTouchDownX = 0, m_iTouchDownY = 0;
	
	public SnapsNativeMainLayout(Context context) {
		super(context);
		init(context, null);
	}

	public SnapsNativeMainLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public SnapsNativeMainLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	public int getRealScrollPos() {
		return scrollViewLayout == null ? 0 : scrollViewLayout.getScrollY();
	}
	
	public int getScrollPos() {
		return scrollPos;
	}
	
	public void setScrollPos( int pos ) {
		this.scrollPos = pos;
	}

	public boolean isInitializedUI() {
		return isInitializedUI;
	}
	
	public boolean isScrollable() {
		return ( scrollViewLayout == null || scrollViewLayout.isScrollable() );
	}
	
	public void scrollTo( int x, int y ) {
		if( scrollViewLayout != null ) scrollViewLayout.scrollTo( x, y );
	}
	
	public int getWebViewScrollX() {
		return scrollViewLayout == null ? 0 : scrollViewLayout.getScrollX();
	}
	
	public int getWebViewScrollY() {
		return scrollViewLayout == null ? 0 : scrollViewLayout.getScrollY();
	}
	
	void init(Context context, AttributeSet attrs) {
		this.mContext = context;
		this.shouldOverrideUrlLoader = new SnapsShouldOverrideUrlLoader((Activity)context, SnapsShouldOverrideUrlLoader.NATIVE);
	}
	
	private int getDefaultTopMargin() {
		if(mContext == null) return 0;
		
		Resources res = mContext.getResources();
		return (int) (res.getDimension( R.dimen.home_title_bar_height ) + res.getDimension( R.dimen.home_menu_bar_height ));
	}
	
	private int getDefaultBottomMargin() {
		if(mContext == null) return 0;
		
		Resources res = mContext.getResources();
		return (int) (res.getDimension( R.dimen.home_bottom_margin ));
	}
	
	private void setPriceInfo(SnapsStoreProductResponse menuInfo) {
	}
	
	private void setProductMenus(List<SnapsStoreProductResponse> menus, LinearLayout mainLy) {
		if(menus == null || mainLy == null) return;
		
		if(mainLy.getChildCount() > 0)
			mainLy.removeAllViews();
		
		for(int ii = 0; ii < menus.size(); ii++) {
			final SnapsStoreProductResponse menu = menus.get(ii);
			
			if(!menu.isUse()) continue;
			
			View view = getInflater().inflate(R.layout.snaps_main_store_menu_item, null);
			
			RelativeLayout rootView = (RelativeLayout) view.findViewById(R.id.snaps_main_store_menu_item_layout);
			RelativeLayout.LayoutParams rootViewLayoutParams = (LayoutParams) rootView.getLayoutParams();
			rootViewLayoutParams.width = UIUtil.getScreenWidth(mContext);
			rootViewLayoutParams.height = (int) (UIUtil.getScreenWidth(mContext) / 1.62f);
			rootView.setLayoutParams(rootViewLayoutParams);
			rootView.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_UP:
						int[] dist = calculateDistance(event);
						if(event.getAction() == MotionEvent.ACTION_UP) {
							if(dist[0] < CLICK_ALLOW_PIXEL && dist[1] < CLICK_ALLOW_PIXEL) {
								//클릭 가능 여부
								if(!menu.isClickable()) return false;
								
								//긴급 상황 클릭 시 팝업 처리
								SnapsStoreProductEmergency emergency = menu.getEmergency();
								if(emergency != null && emergency.isEmergency(getContext())) {
									if(emergency.getTitle() != null && emergency.getTitle().length() > 0) {
										MessageUtil.alert(getContext(), emergency.getTitle(), emergency.getMsg());
									} else {
										MessageUtil.alertnoTitleOneBtn((Activity) getContext(), emergency.getMsg(), null);
									}
									return false;
								}
								
								if(shouldOverrideUrlLoader != null) {
									String url = menu.getNextPageUrl();
									shouldOverrideUrlLoader.shouldOverrideUrlLoading(url);
								}
							} 
						}
						break;
					default:
						break;
					}
					
					return true;
				}
			});
			
			if(ii == 0) { //첫번째 리스트 항목은 타이틀 바 만큼 내려줌..
				RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) rootView.getLayoutParams();
				lp.setMargins(0, getDefaultTopMargin(), 0, 0);
				rootView.setLayoutParams(lp);
			} else if(ii == menus.size() - 1) { //마지막 것도 하단에 마진을 조금 줌..
				RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) rootView.getLayoutParams();
				lp.setMargins(0, 0, 0, getDefaultBottomMargin());
				rootView.setLayoutParams(lp);
			}
			
			//priceInfo (sale이나 가격을 셋팅하기 전에 꼭 먼저 셋팅해 주어야 함..)
			setPriceInfo(menu);
			
			//Background
			ImageView ivBG = (ImageView) view.findViewById(R.id.snaps_main_store_menu_item_bg_iv);
			setProductBG(ivBG, menu);
			
			//sale
			ImageView ivSale = (ImageView) view.findViewById(R.id.snaps_main_store_menu_item_sale_iv);
			setProductSale(ivSale, menu);
			
			//info
			ImageView ivInfo = (ImageView) view.findViewById(R.id.snaps_main_store_menu_item_info_iv);
			
			//comming soon 제품..
			if(menu != null && menu.isCommingSoon()) {
				ivInfo.setVisibility(View.GONE);
				ImageView ivCommingSoon = (ImageView) view.findViewById(R.id.snaps_main_store_menu_item_comming_soon_iv);
				ivCommingSoon.setImageResource(R.drawable.img_comming_soon); 
				ivCommingSoon.setVisibility(View.VISIBLE);
			} else
				setProductDetailInfo(ivInfo, menu);
			
			//text
			FTextView tvSalePrice = (FTextView) view.findViewById(R.id.snaps_main_store_menu_item_sale_price_tv);
			FTextView tvOriginPrice = (FTextView) view.findViewById(R.id.snaps_main_store_menu_item_origin_price_tv);
			FTextView tvName = (FTextView) view.findViewById(R.id.snaps_main_store_menu_item_product_name_tv);
			FTextView tvSubName = (FTextView) view.findViewById(R.id.snaps_main_store_menu_item_product_sub_name_tv);
			setProductText(tvSalePrice, tvOriginPrice, tvName, tvSubName, menu);
					
			mainLy.addView(view);
		}
	}
	
	private void setProductBG(ImageView imgView, SnapsStoreProductResponse menuInfo) {
		if(imgView == null || menuInfo == null) return;
		
		try {
			UIResourcesManager.setMainStoreBG(getContext(), imgView, menuInfo);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}
	
	private void setProductSale(ImageView imgView, SnapsStoreProductResponse menuInfo) {
		if(imgView == null || menuInfo == null || menuInfo.isCommingSoon()) return;
		try {
			SnapsPriceDetailResponse priceInfo = menuInfo.getPriceInfo();
			if(priceInfo == null) return;

			if(priceInfo.isSale()) {
				UIResourcesManager.setSaleImage(getContext(), imgView, priceInfo);
			}
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}
	}
	
	private void setProductDetailInfo(ImageView imgView, final SnapsStoreProductResponse menuInfo) {
		if(imgView == null) return;
		try {
			if(menuInfo == null) return;
			
			String url = menuInfo.getInfoUrl();
			if(url == null || url.length() < 1) {
				imgView.setVisibility(View.GONE);
				return;
			}
			
			imgView.setImageResource(R.drawable.img_main_store_detail_info);
			imgView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String url = menuInfo.getInfoUrl();
					if(shouldOverrideUrlLoader != null)
						shouldOverrideUrlLoader.shouldOverrideUrlLoading(url);
				}
			});
		} catch (OutOfMemoryError e) {
			Dlog.e(TAG, e);
		}
	}
	
	private void setProductText(TextView tvSalePrice, TextView tvOriginPrice, TextView tvName, TextView tvSubName, SnapsStoreProductResponse menuInfo) {
		if(tvSalePrice == null || tvOriginPrice == null || tvName == null || tvSubName == null || menuInfo == null) return;
		
		tvName.setText(menuInfo.getName());

		tvSubName.setText(menuInfo.getSubName());

		SnapsPriceDetailResponse priceInfo = menuInfo.getPriceInfo();
		if(priceInfo == null) return;

		String salePrice = "";
		String orgPrice = "";

		if(priceInfo.getSalePrice() != null && priceInfo.getPrice() != null && priceInfo.getSalePrice().equals(priceInfo.getPrice())) {
			orgPrice = StringUtil.getCurrencyStr(getContext(), Double.parseDouble(priceInfo.getPrice())) + "~";
			tvOriginPrice.setText(orgPrice);
			tvOriginPrice.setPaintFlags(tvOriginPrice.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
			tvSalePrice.setVisibility(View.INVISIBLE);
		} else {
			tvOriginPrice.setPaintFlags(tvOriginPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

				try {
				salePrice = StringUtil.getCurrencyStr(getContext(), priceInfo.getSalePrice()) + "~";
				orgPrice = StringUtil.getCurrency(getContext(), Double.parseDouble(priceInfo.getPrice()));
			} catch (NumberFormatException e) {
					Dlog.e(TAG, e);
			}
			tvSalePrice.setText(salePrice);
			tvOriginPrice.setText(orgPrice);
		}
	}
	
	private LayoutInflater getInflater() {
		if(mInflater != null) return mInflater;
		mInflater = LayoutInflater.from(getContext());
		return mInflater;
	}
	
	public void initUI(List<SnapsStoreProductResponse> menu) {
		
		View v = getInflater().inflate(R.layout.custom_snaps_native_main_scrollview, null);

		scrollViewLayout = (ObserveScrollingScrollView) v.findViewById(R.id.snaps_native_main_scrollview);
		scrollViewLayout.setOnScrollListener(new OnPageScrollListener() {
			
			@Override
				public boolean onScrollChanged(int l, int t, int oldl, int oldt) {
					if( pageScrollListener != null ) pageScrollListener.onScrollChanged(0, t, 0, oldt);
					return false;	
				}

            @Override
            public boolean onScrollChanged(int dx, int dy) { return false; }
        });

		LinearLayout mainLy = (LinearLayout) v.findViewById(R.id.snaps_native_main_linearlayout);
		
		if(mainLy.getChildCount() > 0)
			mainLy.removeAllViews();
		
		setProductMenus(menu, mainLy);
				
		addView(v);
		
		isInitializedUI = true;
	}
	
	private int[] calculateDistance(MotionEvent e) {
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			m_iTouchDownX = (int) e.getX();
			m_iTouchDownY = (int) e.getY();
			break;
		case MotionEvent.ACTION_UP:
			int dx = (int) Math.abs(e.getX() - m_iTouchDownX);
			int dy = (int) Math.abs(e.getY() - m_iTouchDownY);
			return new int[] { dx, dy };
		default:
			break;
		}
		return new int[] { 0 , 0 };
	}

	public void setPageScrollListner( OnPageScrollListener listener ) {
		this.pageScrollListener = listener;
	}
}
