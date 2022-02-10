//package com.snaps.mobile.activity.themebook;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageView;
//
//import com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants;
//import com.snaps.common.utils.constant.Config;
//import com.snaps.common.utils.constant.Const_EKEY;
//import com.snaps.common.utils.constant.SnapsConfigManager;
//import com.snaps.common.utils.imageloader.ImageLoader;
//import com.snaps.common.utils.ui.DataTransManager;
//import com.snaps.common.utils.ui.MessageUtil;
//import com.snaps.common.utils.ui.StringUtil;
//import com.snaps.mobile.R;
//import com.snaps.mobile.activity.google_style_image_selector.activities.ImageSelectActivityV2;
//import com.snaps.mobile.activity.google_style_image_selector.datas.ImageSelectIntentData;
//import com.snaps.mobile.activity.home.fragment.GoHomeOpserver;
//import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
//import com.snaps.mobile.utils.smart_snaps.SmartSnapsManager;
//
//import errorhandle.CatchActivity;
//import errorhandle.logger.SnapsLogger;
//import errorhandle.logger.model.SnapsLoggerClass;
//
//import static com.snaps.common.data.smart_snaps.interfacies.SmartSnapsConstants.URL_RECOMMEND_BOOK_INFO_PAGE;
//
//public class SmartRecommendBookThemeSelectActivity extends CatchActivity implements View.OnClickListener, GoHomeOpserver.OnGoHomeOpserver {
//
//	private SmartSnapsConstants.eSmartAnalysisPhotoBookThemeType themeType = null;
//
//	private boolean isFirstLoad = false;
//
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));
//
//		setContentView(R.layout.smart_snaps_analysis_theme_type_select_activity);
//
//		initialize();
//
//		if (SnapsConfigManager.isAutoLaunchProductMakingMode()) {
//			setThemeType(SmartSnapsConstants.eSmartAnalysisPhotoBookThemeType.FAMILY);
//		}
//	}
//
//	private void initialize() {
//		isFirstLoad = true;
//
//		initTitleUI();
//
//		addListeners();
//	}
//
//	private void initTitleUI() {
//		ImageView btnInfo = (ImageView) findViewById(R.id.btnTitleInfo);
//		btnInfo.setVisibility(View.VISIBLE);
//
//		btnInfo.setOnClickListener( new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				showProductInfoPage( URL_RECOMMEND_BOOK_INFO_PAGE );
//			}
//		} );
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//
//		if (isFirstLoad) {
//			isFirstLoad = false;
//			loadImageResources();
//		}
//	}
//
//	private void loadImageResources() {
//		ImageView travelBg = findViewById(R.id.smart_snaps_analysis_theme_type_select_activity_travel_bg_iv);
//
//		ImageView babyBg = findViewById(R.id.smart_snaps_analysis_theme_type_select_activity_baby_bg_iv);
//
//		ImageView familyBg = findViewById(R.id.smart_snaps_analysis_theme_type_select_activity_family_bg_iv);
//
//		ImageView coupleBg = findViewById(R.id.smart_snaps_analysis_theme_type_select_activity_couple_bg_iv);
//
//		ImageLoader.with(this).load(R.drawable.img_auto_type_travel).placeholder(R.drawable.color_drawable_eeeeee).into(travelBg);
//
//		ImageLoader.with(this).load(R.drawable.img_auto_type_baby).placeholder(R.drawable.color_drawable_eeeeee).into(babyBg);
//
//		ImageLoader.with(this).load(R.drawable.img_auto_type_family).placeholder(R.drawable.color_drawable_eeeeee).into(familyBg);
//
//		ImageLoader.with(this).load(R.drawable.img_auto_type_couple).placeholder(R.drawable.color_drawable_eeeeee).into(coupleBg);
//	}
//
//	private void addListeners() {
//		findViewById(R.id.smart_snaps_analysis_theme_type_select_activity_travel_btn).setOnClickListener(this);
//		findViewById(R.id.smart_snaps_analysis_theme_type_select_activity_couple_btn).setOnClickListener(this);
//		findViewById(R.id.smart_snaps_analysis_theme_type_select_activity_baby_btn).setOnClickListener(this);
//		findViewById(R.id.smart_snaps_analysis_theme_type_select_activity_family_btn).setOnClickListener(this);
//
//		GoHomeOpserver.addGoHomeListener(this);
//	}
//
//	private void showProductInfoPage( String url ) {
//		if ( StringUtil.isEmpty(url) ) return;
//
////		if( !url.contains("http") && !url.contains("snapsapp") )
////			url = SnapsAPI.WEB_DOMAIN() + url;
//
//		SnapsShouldOverrideUrlLoader urlLoader = new SnapsShouldOverrideUrlLoader(this, SnapsShouldOverrideUrlLoader.WEB);
//		urlLoader.shouldOverrideUrlLoading( url );
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		PhotobookCommonUtils.initProductEditInfo();
//
//		DataTransManager.releaseInstance();
//	}
//
//	@Override
//	public void onGoHome() {
//		finish();
//	}
//
//	@Override
//	public void onClick(View v) {
//		if (v == null) return;
//
//		if (v.getId() == R.id.smart_snaps_analysis_theme_type_select_activity_travel_btn) {
//			setThemeType(SmartSnapsConstants.eSmartAnalysisPhotoBookThemeType.TRAVEL);
//		} else if (v.getId() == R.id.smart_snaps_analysis_theme_type_select_activity_couple_btn) {
//			setThemeType(SmartSnapsConstants.eSmartAnalysisPhotoBookThemeType.COUPLE);
//		} else if (v.getId() == R.id.smart_snaps_analysis_theme_type_select_activity_baby_btn) {
//			setThemeType(SmartSnapsConstants.eSmartAnalysisPhotoBookThemeType.BABY);
//		} else if (v.getId() == R.id.smart_snaps_analysis_theme_type_select_activity_family_btn) {
//			setThemeType(SmartSnapsConstants.eSmartAnalysisPhotoBookThemeType.FAMILY);
//		} else if (v.getId() == R.id.btnTitleLeftBack || (v.getId() == R.id.btnTitleLeftBackLy)
//				|| v.getId() == R.id.btnTitleLeft || (v.getId() == R.id.btnTitleLeftLy)) {
//			finish();
//		}
//	}
//
//	private void setThemeType(SmartSnapsConstants.eSmartAnalysisPhotoBookThemeType themeType) {
//		this.themeType = themeType;
//
//		startImageSelectActivityWithSmartSelectType();
//	}
//
//	private void startImageSelectActivityWithSmartSelectType() {
//		if (themeType == null) {
//			MessageUtil.toast(this, R.string.smart_analysis_theme_no_selected);
//			return;
//		}
//
//		SmartSnapsManager smartSnapsManager = SmartSnapsManager.getInstance();
//		smartSnapsManager.setSmartSnapsImageSelectType(SmartSnapsConstants.eSmartSnapsImageSelectType.SMART_ANALYSIS_PRODUCT);
//		smartSnapsManager.setSmartAnalysisPhotoBookThemeType(themeType);
//
//		Config.setTMPL_CODE(themeType.getTemplateCode());
//
//		Intent intent = new Intent(this, ImageSelectActivityV2.class);
//		ImageSelectIntentData intentDatas = new ImageSelectIntentData.Builder()
//				.setSmartSnapsImageSelectType(SmartSnapsConstants.eSmartSnapsImageSelectType.SMART_ANALYSIS_PRODUCT)
//				.setHomeSelectProduct(Config.SELECT_SMART_ANALYSIS_PHOTO_BOOK)
//				.setHomeSelectProductCode(Config.getPROD_CODE())
//				.setHomeSelectKind("").create();
//
//		Bundle bundle = new Bundle();
//		bundle.putSerializable(Const_EKEY.IMAGE_SELECT_INTENT_DATA_KEY, intentDatas);
//		intent.putExtras(bundle);
//		startActivity(intent);
//	}
//}