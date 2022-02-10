package com.snaps.mobile.activity.ui.menu.renewal;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.snaps.common.structure.SnapsHandler;
import com.snaps.common.structure.SnapsMaxPageInfo;
import com.snaps.common.utils.ISnapsHandler;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.develop.SnapsDevelopHelper;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.image.AsyncTask;
import com.snaps.common.utils.image.ImageUtil;
import com.snaps.common.utils.imageloader.ImageLoader;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.common.utils.pref.Setting;
import com.snaps.common.utils.thread.ATask;
import com.snaps.common.utils.ui.ContextUtil;
import com.snaps.common.utils.ui.FontUtil;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.common.utils.ui.UIUtil;
import com.snaps.mobile.activity.hamburger_menu.SnapsMenuManager;
import com.snaps.mobile.activity.home.interfacies.ISnapsHomeActStateChangeListener;
import com.snaps.mobile.activity.photoprint.PhotoPrintProductInfo;
import com.snaps.mobile.activity.ui.menu.renewal.model.Category;
import com.snaps.mobile.activity.ui.menu.renewal.model.Image;
import com.snaps.mobile.activity.ui.menu.renewal.model.Layout;
import com.snaps.mobile.activity.ui.menu.renewal.model.LayoutObject;
import com.snaps.mobile.activity.ui.menu.renewal.model.Menu;
import com.snaps.mobile.activity.ui.menu.renewal.model.MenuData;
import com.snaps.mobile.activity.ui.menu.renewal.model.NoticeItem;
import com.snaps.mobile.activity.ui.menu.renewal.model.PageControl;
import com.snaps.mobile.activity.ui.menu.renewal.model.Price;
import com.snaps.mobile.activity.ui.menu.renewal.model.ProductPrice;
import com.snaps.mobile.activity.ui.menu.renewal.model.SubCategory;
import com.snaps.mobile.activity.ui.menu.renewal.model.Text;
import com.snaps.mobile.activity.ui.menu.renewal.model.Value;
import com.snaps.mobile.activity.ui.menu.renewal.view.ReloadableImageView;
import com.snaps.mobile.activity.ui.menu.renewal.view.TouchCustomRecyclerView;
import com.snaps.mobile.activity.ui.menu.renewal.viewholder.HomeViewPagerIndicator;
import com.snaps.mobile.activity.ui.menu.renewal.viewpager.HomeViewPagerAdapter;
import com.snaps.mobile.activity.ui.menu.renewal.viewpager.TouchCustomLoopRecyclerViewPager;
import com.snaps.mobile.activity.ui.menu.renewal.viewpager.TouchCustomRecyclerViewPager;
import com.snaps.mobile.activity.ui.menu.webinterface.SnapsShouldOverrideUrlLoader;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsProductListParams;
import com.snaps.mobile.interfaces.OnDataLoadListener;
import com.snaps.mobile.interfaces.OnLoginStatusChanged;
import com.snaps.mobile.product_native_ui.util.SnapsNativeUIManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import errorhandle.logger.SnapsInterfaceLogDefaultHandler;
import errorhandle.logger.SnapsLogger;

/**
 * Created by songhw on 2016. 7. 26..
 */
public class MenuDataManager implements ISnapsHomeActStateChangeListener, ISnapsHandler {
	private static final String TAG = MenuDataManager.class.getSimpleName();

	public static final String SEPARATOR_STRING = "\\|\\|";

	private static MenuDataManager instance;

	/**
	 * 아래 shouldOverrideUrlLoader는 Home 메뉴의 클릭이벤트 용도로만 사용해야 함. (getter 생성해서 임의로 사용하지 마세요.)
	 */
	private SnapsShouldOverrideUrlLoader shouldOverrideUrlLoder;

	private MenuData menuData = new MenuData();

	private NoticeItem noticeItem; //햄버거 메뉴에 표시할 공지 내용

	public HashMap<String, Price> priceMap; // Price
	public HashMap<String, Typeface> typefaceMap; // Typeface

	private String userNo;

	private float currentScale;

	private OnDataLoadListener dataLoadListener;
	private OnLoginStatusChanged loginStatusListener;

	private Map<ImageView, String> menuImageViews = null;

	private SnapsHandler snapsHandler = null;

	private int maxImageSize = 2048;

	public static MenuDataManager getInstance() {
		if (instance == null) {
			instance = new MenuDataManager();
		}
		return instance;
	}

	private MenuDataManager() {
		snapsHandler = new SnapsHandler(this);
		menuImageViews = new LinkedHashMap<>();
	}

	public void setDataLoadListener(OnDataLoadListener listener) {
		this.dataLoadListener = listener;
	}

	public void setLoginStatusListener(OnLoginStatusChanged listener) {
		this.loginStatusListener = listener;
	}

	public void createShouldOverrideUrlLoader(Activity activity) {
		this.shouldOverrideUrlLoder = new SnapsShouldOverrideUrlLoader(activity, true);
	}

	public void performAnyUrlForTest() {
		if (!Config.isDevelopVersion()) {
			return;
		}
		shouldOverrideUrlLoder.shouldOverrideUrlLoading("aa");
	}

	/**
	 * 초기에 호출. 버전 체크하여 필요하면 새로운 데이터 받음. 데이터 객체화
	 *
	 * @param context
	 */
	public void init(final Activity context) {

		//Image 의 최대 크기는 단말기의 가로 크기로 제한한다.
		maxImageSize = UIUtil.getScreenWidth(context);

		load(context);

		ATask.executeVoid(new ATask.OnTask() {
			boolean flag;

			@Override
			public void onPre() {
			}

			@Override
			public void onBG() {
				flag = getData(context);

				//햄버거 메뉴에 표시할 기본 공지사항도 받아 옴.
				if (flag) {
					//getNoticeInfo();		// jack@snaps.com	필요없음
				}
			}

			@Override
			public void onPost() {
				if (flag) {
					if (dataLoadListener != null) {
						dataLoadListener.onProcessDone();
					}
					// 모든 작업 완료 후 현재 데이터를 로컬에 저장.

					save(context);
				}
			}
		});
	}

	@Override
	public void onHomeActResume() {
		if (snapsHandler != null) {
			snapsHandler.sendEmptyMessage(HANDLE_MSG_ALL_IMAGE_RELOAD);
		}
	}

	@Override
	public void onHomeActPause() {
		if (snapsHandler != null) {
			snapsHandler.sendEmptyMessageDelayed(HANDLE_MSG_ALL_IMAGE_RELEASE, 100);
		}
	}

	private String getVersionString(String verCode, boolean needToReload) {
		if (StringUtil.isEmpty(verCode) || needToReload) {
			verCode = "0.0.0.0";
		}
		return verCode;
	}

	private boolean getData(Context context) {
		boolean needToReloadAll = false;
		try {
			String savedVersion = Setting.getString(context, Const_VALUE.APP_VERSION_FOR_MENU_DATA);
			needToReloadAll = !context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName.equalsIgnoreCase(savedVersion); // 저장된 버전과 현재 앱 버전이 다르면 전체 로드한다.

			if (!Config.isRealServer() || !Config.useKorean()) {
				needToReloadAll = true;
			}
		} catch (PackageManager.NameNotFoundException e) {
			Dlog.e(TAG, e);
		}

		String dataUrl = SnapsAPI.getUiMenuData(
				getVersionString(menuData.categoryVersion, needToReloadAll), getVersionString(menuData.subCategoryVersion, needToReloadAll), getVersionString(menuData.layoutVersion, needToReloadAll),
				getVersionString(menuData.homeValueVersion, needToReloadAll), getVersionString(menuData.menuValueVersion, needToReloadAll),
				getVersionString(menuData.photoPrintVersion, needToReloadAll), getVersionString(menuData.spineInfoVersion, needToReloadAll));

		Dlog.d("getData() dataUrl:" + dataUrl);

		JsonObject parent = new JsonParser().parse(HttpUtil.connectGet(dataUrl, SnapsInterfaceLogDefaultHandler.createDefaultHandler())).getAsJsonObject();
		JsonObject object, tempObject;
		JsonArray array;
		Set<Map.Entry<String, JsonElement>> entrySet;

		// get categories data
		if (parent.has("categories")) {
			object = parent.getAsJsonObject("categories");
			array = object.getAsJsonArray("items");
			menuData.categoryVersion = object.get("version").getAsString();
			menuData.categories = new ArrayList<Category>();
			for (int i = 0; i < array.size(); ++i) {
				menuData.categories.add(new Category(array.get(i).getAsJsonObject()));
			}
			if (object.has("crm_idx") && !StringUtil.isEmpty(object.get("crm_idx").getAsString())) {
				menuData.crmIdx = object.get("crm_idx").getAsInt();
				menuData.menuCrmLogout = menuData.categories.get(0).getMenuList().get(menuData.crmIdx);
			}
			if (object.has("delivery_idx") && !StringUtil.isEmpty(object.get("delivery_idx").getAsString())) {
				menuData.deliveryIdx = object.get("delivery_idx").getAsInt();
			}
			if (object.has("hasHome") && !StringUtil.isEmpty(object.get("hasHome").getAsString())) {
				menuData.isExistHomeMenu = object.get("hasHome").getAsBoolean();
			}
		}

		// get price data
		// price값은 버전 상관없이 호출할 때 마다 받는다.
		object = parent.getAsJsonObject("price");

		priceMap = new HashMap<String, Price>();
		entrySet = object.entrySet();
		for (Map.Entry<String, JsonElement> entry : entrySet) {
			priceMap.put(entry.getKey(), new Price(entry.getValue().getAsJsonObject()));
		}

		// get subCategories data
		if (parent.has("subCategories")) {
			object = parent.getAsJsonObject("subCategories");
			menuData.subCategoryVersion = object.get("version").getAsString();

			menuData.subCategoryMap = new HashMap<String, SubCategory>();
			entrySet = object.entrySet();
			for (Map.Entry<String, JsonElement> entry : entrySet) {
				if ("version".equalsIgnoreCase(entry.getKey())) {
					menuData.subCategoryVersion = entry.getValue().getAsString();
				} else {
					menuData.subCategoryMap.put(entry.getKey(), new SubCategory(entry.getValue().getAsJsonObject()));
				}
			}
		}

		// get layouts data
		if (parent.has("layouts")) {
			object = parent.getAsJsonObject("layouts");

			menuData.layoutMap = new HashMap<String, Layout>();
			entrySet = object.entrySet();
			for (Map.Entry<String, JsonElement> entry : entrySet) {
				if ("version".equalsIgnoreCase(entry.getKey())) {
					menuData.layoutVersion = entry.getValue().getAsString();
				} else {
					menuData.layoutMap.put(entry.getKey(), new Layout(entry.getValue().getAsJsonObject()));
				}
			}
		}

		// get home_values data
		if (parent.has("home_values")) {
			object = parent.getAsJsonObject("home_values");

			menuData.homeValueMap = new HashMap<String, Value>();
			entrySet = object.entrySet();
			for (Map.Entry<String, JsonElement> entry : entrySet) {
				if ("version".equalsIgnoreCase(entry.getKey())) {
					menuData.homeValueVersion = entry.getValue().getAsString();
				} else {
					menuData.homeValueMap.put(entry.getKey(), new Value(entry.getValue().getAsJsonObject()));
				}
			}
		}

		// get menu_values data
		if (parent.has("menu_values")) {
			object = parent.getAsJsonObject("menu_values");

			menuData.menuValueMap = new HashMap<String, Value>();
			entrySet = object.entrySet();
			for (Map.Entry<String, JsonElement> entry : entrySet) {
				if ("version".equalsIgnoreCase(entry.getKey())) {
					menuData.menuValueVersion = entry.getValue().getAsString();
				} else {
					menuData.menuValueMap.put(entry.getKey(), new Value(entry.getValue().getAsJsonObject()));
				}
			}
		}

		if (parent.has("photoPrint")) {
			menuData.photoPrintProductInfoArray = new ArrayList<PhotoPrintProductInfo>();
			object = parent.getAsJsonObject("photoPrint");
			entrySet = object.entrySet();
			for (Map.Entry<String, JsonElement> entry : entrySet) {
				if ("version".equalsIgnoreCase(entry.getKey())) {
					menuData.photoPrintVersion = entry.getValue().getAsString();
				} else {
					menuData.photoPrintProductInfoArray.add(new PhotoPrintProductInfo(entry.getKey(), entry.getValue().getAsJsonObject()));
				}
			}
		}

		if (parent.has("spine_info")) {
			object = parent.getAsJsonObject("spine_info");
			entrySet = object.entrySet();
			menuData.maxPageInfo = new SnapsMaxPageInfo(context);
			for (Map.Entry<String, JsonElement> entry : entrySet) {
				if ("version".equalsIgnoreCase(entry.getKey())) {
					menuData.spineInfoVersion = entry.getValue().getAsString();
					menuData.maxPageInfo.setVersionCode(entry.getValue().getAsString());
				} else {
					menuData.maxPageInfo.addPageInfo(entry.getKey(), entry.getValue().getAsJsonObject());
				}
			}

			if (menuData.maxPageInfo.getPageInfoCount() < 1) {
				if (dataLoadListener != null) {
					dataLoadListener.onGetSpineInfoFailed();
				}
				return false;
			}
		}

		// initFont(context);  //여기서 폰트 객체 생성 안하고 FontUtil에서 하게 수정

		// CRM정보와 배송정보를 인터페이스 호출하여 저장한다.
		createCrmData(context, true);
		saveDeliveryData(context);

		return true;
	}

	private void getNoticeInfo() {
		try {
			String result = HttpUtil.connectGet(SnapsAPI.GET_NOTICE_INFO_URL(), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
			if (result == null) {
				return;
			}

			JsonObject parent = new JsonParser().parse(result).getAsJsonObject();
			noticeItem = new NoticeItem(parent);
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	/**
	 * 기본폰트 typeface를 지정해둠.
	 */

	private void initFont(Context context) {
		typefaceMap = new HashMap<String, Typeface>();
		ArrayList<LayoutObject> objectList;
		LayoutObject object;
		String font;
		Layout layout;
		for (String key : menuData.layoutMap.keySet()) {
			if (StringUtil.isEmpty(key) || !menuData.layoutMap.containsKey(key)) {
				continue;
			}
			layout = menuData.layoutMap.get(key);
			if (layout == null || layout.getObjectList() == null) {
				continue;
			}
			objectList = layout.getObjectList();
			for (int i = 0; i < objectList.size(); ++i) {
				object = objectList.get(i);
				if (object instanceof Text) {
					font = ((Text) object).getFont();
					if (!StringUtil.isEmpty(font) && !typefaceMap.containsKey(font)) {
						putFontfromFile(context, font);
					}
				}
			}
		}
	}



	private void putFontfromFile(Context context, String font) {
		File file = new File(FontUtil.FONT_FILE_PATH(context) + font);
		if (!file.exists()) {
			return;
		}
		Typeface typeFace = Typeface.createFromFile(file);
		if (typeFace != null) {
			if (typefaceMap != null) {
				typefaceMap.put(font, typeFace);
			}
		}
	}


	public void refreshCrm(final Context context) {
		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				createCrmData(context, false);
			}
		});
	}

	private void createCrmData(Context context, boolean isFirstExecute) {
		if (!Config.useKorean()) {
			return;
		}

		String newUserNo = Setting.getString(context, Const_VALUE.KEY_SNAPS_USER_NO);
		if (newUserNo.equals(userNo) || menuData.crmIdx < 0) {
			return;
		}

		userNo = newUserNo;
		// 로그인중이 아닐때.
		if (StringUtil.isEmpty(userNo)) {
			if (loginStatusListener != null && !isFirstExecute) {
				loginStatusListener.changeMenuLayout(new Menu(menuData.categories.get(0).getMenuList().get(menuData.crmIdx).getLayerId(), menuData.categories.get(0).getMenuList().get(menuData.crmIdx).getDataId()), menuData.crmIdx);
			}
			return;
		}

		// 로그인 됐을때
		String data = HttpUtil.connectGet(SnapsAPI.GET_HOME_CRM_INFO_URL() + userNo, SnapsInterfaceLogDefaultHandler.createDefaultHandler());
		if (StringUtil.isEmpty(data)) { // 데이터가 없으면 로그아웃 처리.
			if (loginStatusListener != null && !isFirstExecute) {
				loginStatusListener.changeMenuLayout(new Menu(menuData.categories.get(0).getMenuList().get(menuData.crmIdx).getLayerId(), menuData.categories.get(0).getMenuList().get(menuData.crmIdx).getDataId()), menuData.crmIdx);
			}
			return;
		}

		JsonObject jsonObject = null;
		try {
			jsonObject = new JsonParser().parse(data).getAsJsonObject();
		} catch (Exception e) {
			Dlog.e(TAG, e);
			SnapsLogger.appendTextLog("createCrmData data : " + data);
			SnapsLogger.sendLogException("MenuDataManager/createCrmData", e);
		}

		if (jsonObject == null) {
			return;
		}

		String layoutId = jsonObject.get("crm_layout").getAsString();
		String valueId = jsonObject.get("crm_data").getAsString();
		Layout layout = null;
		Value value = null;
		if (menuData.layoutMap.containsKey(layoutId)) {
			layout = menuData.layoutMap.get(layoutId);
		}
		if (menuData.homeValueMap.containsKey(valueId)) {
			value = menuData.homeValueMap.get(valueId);
		}

		if (layout == null) {
			return;
		}

		ArrayList<LayoutObject> layoutList = layout.getObjectList();
		PageControl control = null;
		for (int i = 0; i < layoutList.size(); ++i) {
			if (layoutList.get(i).getType() == LayoutObject.TYPE_PAGE_CONTROL) {
				control = (PageControl) layoutList.get(i);
				break;
			}
		}

		if (control == null) {
			return;
		}
		control.clearPages();

		if (value != null) {
			value.clearSubData();
		}

		HashMap<String, String> values;
		JsonArray jsonArray = jsonObject.get("pages").getAsJsonArray();
		String subLayoutId = "", subValueId;
		Layout subLayout;
		for (int i = 0; i < jsonArray.size(); ++i) {
			jsonObject = jsonArray.get(i).getAsJsonObject();
			values = new HashMap<String, String>();
			Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
			String clickKey = "";
			for (Map.Entry<String, JsonElement> entry : entrySet) {
				if ("layout".equalsIgnoreCase(entry.getKey())) {
					subLayoutId = entry.getValue().getAsString();
					continue;
				} else if ("click".equalsIgnoreCase(entry.getKey())) {
					clickKey = entry.getKey();
				}

				if (values.containsKey(entry.getKey())) {
					values.remove(entry.getKey());
				}
				values.put(entry.getKey(), entry.getValue().getAsString());
			}

			if (!menuData.layoutMap.containsKey(subLayoutId)) {
				continue;
			}

			subLayout = menuData.layoutMap.get(subLayoutId);
			subLayout.setBgColor(Color.TRANSPARENT); // crm pagecontrol item의 layout bgcolor를 투명하게 변경
			if (!StringUtil.isEmpty(clickKey)) {
				subLayout.setClick(clickKey);
			}

			subValueId = StringUtil.getRandomStringId(10);
			value.addSubData(subValueId, new Value(values));

			control.addPage(new Menu(subLayoutId, subValueId));
		}

		menuData.menuCrmLogin = new Menu(layoutId, valueId);
		if (loginStatusListener != null) {
			loginStatusListener.changeMenuLayout(menuData.menuCrmLogin, menuData.crmIdx);
		}
	}

	private void saveDeliveryData(Context context) {
		if (!Config.useKorean() || menuData.deliveryIdx < 0) {
			return;
		}

		String data = HttpUtil.connectGet(SnapsAPI.GET_HOME_DELIVERY_INFO_URL(), SnapsInterfaceLogDefaultHandler.createDefaultHandler());
		Layout layout = null;
		Value value = null;
		String layoutId = menuData.categories.get(0).getMenuList().get(menuData.deliveryIdx).getLayerId();
		String valueId = menuData.categories.get(0).getMenuList().get(menuData.deliveryIdx).getDataId();
		if (menuData.layoutMap.containsKey(layoutId)) {
			layout = menuData.layoutMap.get(layoutId);
		}
		if (menuData.homeValueMap.containsKey(valueId)) {
			value = menuData.homeValueMap.get(valueId);
		}

		if (layout == null) {
			return;
		}

		ArrayList<LayoutObject> layoutList = layout.getObjectList();
		PageControl control = null;
		for (int i = 0; i < layoutList.size(); ++i) {
			if (layoutList.get(i).getType() == LayoutObject.TYPE_PAGE_CONTROL) {
				control = (PageControl) layoutList.get(i);
				break;
			}
		}

		if (control != null) {
			control.clearPages();
			value.clearSubData();

			HashMap<String, String> values;
			JsonArray jsonArray = new JsonParser().parse(data).getAsJsonArray();
			JsonObject jsonObject;
			for (int i = 0; i < jsonArray.size(); ++i) {
				jsonObject = jsonArray.get(i).getAsJsonObject();
				values = new HashMap<String, String>();
				Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
				String clickKey = "";
				for (Map.Entry<String, JsonElement> entry : entrySet) {
					if ("layout".equalsIgnoreCase(entry.getKey())) {
						layoutId = entry.getValue().getAsString();
						continue;
					} else if ("click".equalsIgnoreCase(entry.getKey())) {
						clickKey = entry.getKey();
					}

					values.put(entry.getKey(), entry.getValue().getAsString());
				}
				if (!StringUtil.isEmpty(clickKey) && menuData.layoutMap.containsKey(layoutId)) {
					menuData.layoutMap.get(layoutId).setClick(clickKey);
				}

				valueId = StringUtil.getRandomStringId(10);
				value.addSubData(valueId, new Value(values));

				if (menuData.layoutMap.containsKey(layoutId)) {
					control.addPage(new Menu(layoutId, valueId));
				}
			}
		}
	}

	/**
	 * 현재 데이터 저장
	 *
	 * @param context
	 */
	private void save(Context context) {
		if (menuData != null) {
			FileUtil.saveToFile(menuData, MenuData.getSaveTargetFile(context));
			try {
				Setting.set(context, Const_VALUE.APP_VERSION_FOR_MENU_DATA, context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName);
			} catch (PackageManager.NameNotFoundException e) {
				Dlog.e(TAG, e);
			}
		}
	}

	/**
	 * 저장된 데이터 불러옴
	 *
	 * @param context
	 */
	private void load(Context context) {
		Object obj = FileUtil.loadfromFile(MenuData.getSaveTargetFile(context));
		if (obj == null || !(obj instanceof MenuData)) {
			menuData = new MenuData();
		} else {
			menuData = (MenuData) obj;
		}
	}

	public void drawMenuLayout(Context context, FrameLayout parent, Menu menu, int screenW, int distance, boolean useHomeValue) {
		if (menu == null) {
			return;
		}

		Layout layout = null;
		Value value = null;
		HashMap<String, Value> valueMap = useHomeValue ? menuData.homeValueMap : menuData.menuValueMap;
		if (menuData.layoutMap.containsKey(menu.getLayerId())) {
			layout = menuData.layoutMap.get(menu.getLayerId());
		}
		if (valueMap.containsKey(menu.getDataId())) {
			value = valueMap.get(menu.getDataId());
		}
		if (parent == null || layout == null) {
			return;
		}

		currentScale = (float) screenW / (float) layout.getSize()[0];
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenW, getScaledValue(layout.getSize()[1]));
		params.bottomMargin = distance;
		parent.setLayoutParams(params);

		drawMenuLayout(context, parent, layout, value, screenW, distance, useHomeValue, layout.getClick());
	}

	public void drawMenuLayout(Context context, FrameLayout parent, final Layout layout, final Value value, int screenW, int distance, boolean useHomeValue, String clickTargetId) {
		if (parent == null || layout == null) {
			return;
		}

		parent.setBackgroundColor(layout.getBgColor());

		final String targetUrl = StringUtil.isEmpty(clickTargetId) || value == null ? "" : value.getStringValue(clickTargetId);
		if (!StringUtil.isEmpty(targetUrl)) {
			setClickEvent(parent, targetUrl);
		} else {
			parent.setOnClickListener(null);
		}

		LayoutObject object;
		for (int i = 0; i < layout.getObjectList().size(); ++i) {
			object = layout.getObjectList().get(i);
			if (object.getType() == LayoutObject.TYPE_IMAGE || object.getType() == LayoutObject.TYPE_PROD_NEW) {
				drawImageView(context, parent, object, value);
			} else if (object.getType() == LayoutObject.TYPE_TEXT) {
				drawTextView(context, parent, object, value);
			} else if (object.getType() == LayoutObject.TYPE_PAGE_CONTROL) {
				PageControl pageControl = (PageControl) object;
				if (pageControl.getPages() == null || pageControl.getPages().size() < 1) {
					continue;
				}

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
				int leftMargin = getScaledValue(pageControl.getRect()[0]);
				int rightMargin = getScaledValue(layout.getSize()[0] - pageControl.getRect()[2] - pageControl.getRect()[0]);
				int topMarin = getScaledValue(pageControl.getRect()[1]);
				if (!pageControl.doPaging()) {
					TouchCustomRecyclerView recyclerView = new TouchCustomRecyclerView(context);
					recyclerView.setPadding(leftMargin, topMarin, rightMargin, 0);
					recyclerView.setLayoutParams(params);

					recyclerView.setLayoutManager(manager);
					recyclerView.setBackgroundColor(Color.TRANSPARENT);
					HomeViewPagerAdapter adapter = new HomeViewPagerAdapter(pageControl, value, layout.getSize()[0], screenW, pageControl.getPageSpace(), useHomeValue, pageControl.doPaging(), targetUrl);
					recyclerView.setAdapter(adapter);
					parent.addView(recyclerView);
				} else if (pageControl.isInfinityPage()) {
					TouchCustomLoopRecyclerViewPager loopRecyclerViewPager = new TouchCustomLoopRecyclerViewPager(context);
					loopRecyclerViewPager.setPadding(leftMargin, topMarin, rightMargin, 0);
					loopRecyclerViewPager.setTriggerOffset(0.01f);
					loopRecyclerViewPager.setSinglePageFling(true);
					loopRecyclerViewPager.setClipToPadding(true);
					loopRecyclerViewPager.setLayoutParams(params);

					loopRecyclerViewPager.setLayoutManager(manager);
					loopRecyclerViewPager.setBackgroundColor(Color.TRANSPARENT);
					HomeViewPagerAdapter adapter = new HomeViewPagerAdapter(pageControl, value, layout.getSize()[0], screenW, pageControl.getPageSpace(), useHomeValue, pageControl.doPaging(), targetUrl);
					loopRecyclerViewPager.setAdapter(adapter);
					parent.addView(loopRecyclerViewPager);

					if (adapter.getPageControl() != null && !adapter.getPageControl().isPageHidden() && adapter.getPageControl().getPages().size() > 1) {
						HomeViewPagerIndicator indicator = new HomeViewPagerIndicator(parent, adapter, adapter.isExpanded());
						loopRecyclerViewPager.addOnPageChangedListener(indicator);
						indicator.init(layout.getSize()[0], adapter.getPageControl().getRect()[1], adapter.getPageControl().getRect()[3], screenW, adapter.getPageControl().getPageAlign());
					}

					if (pageControl.getRolling() > 0) {
						loopRecyclerViewPager.setAutoRolling(pageControl.getRolling());
					}
				} else {
					TouchCustomRecyclerViewPager recyclerViewPager = new TouchCustomRecyclerViewPager(context);
					recyclerViewPager.setPadding(leftMargin, topMarin, rightMargin, 0);
					recyclerViewPager.setTriggerOffset(0.01f);
					recyclerViewPager.setSinglePageFling(true);
					recyclerViewPager.setClipToPadding(true);
					recyclerViewPager.setLayoutParams(params);

					recyclerViewPager.setLayoutManager(manager);
					recyclerViewPager.setBackgroundColor(Color.TRANSPARENT);
					HomeViewPagerAdapter adapter = new HomeViewPagerAdapter(pageControl, value, layout.getSize()[0], screenW, pageControl.getPageSpace(), useHomeValue, pageControl.doPaging(), targetUrl);
					recyclerViewPager.setAdapter(adapter);
					parent.addView(recyclerViewPager);

					if (adapter.getPageControl() != null && !adapter.getPageControl().isPageHidden() && adapter.getPageControl().getPages().size() > 1) {
						HomeViewPagerIndicator indicator = new HomeViewPagerIndicator(parent, adapter, adapter.isExpanded());
						recyclerViewPager.addOnPageChangedListener(indicator);
						indicator.init(layout.getSize()[0], adapter.getPageControl().getRect()[1], adapter.getPageControl().getRect()[3], screenW, adapter.getPageControl().getPageAlign());
					}

					if (pageControl.getRolling() > 0) {
						recyclerViewPager.setAutoRolling(pageControl.getRolling());
					}
				}
			} else if (object.getType() == LayoutObject.TYPE_PROD_PRICE) {
				drawPriceText(context, parent, object, value);
			} else if (object.getType() == LayoutObject.TYPE_DISCOUNT || object.getType() == LayoutObject.TYPE_PERCENT || object.getType() == LayoutObject.TYPE_PRICE) {
				drawPriceText(context, parent, object, value, object.getType());
			}
		}
	}

	public static int getScaledValue(Context context, int origin, int parentW) {
		Point displaySize = new Point();
		((Activity) context).getWindowManager().getDefaultDisplay().getSize(displaySize);
		float screenW = (float) displaySize.x;
		float currentScale = screenW / (float) parentW;
		return (int) ((float) origin * currentScale);
	}

	public static boolean viewPageHorizontallyScrollable() {
		return !TouchCustomRecyclerView.doingTouch && !TouchCustomLoopRecyclerViewPager.doingTouch;
	}

	private int getScaledValue(int origin) {
		return (int) ((float) origin * currentScale);
	}

	private int getDptoPx(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}

	/**
	 * Image 객체로 뷰 제작
	 *
	 * @param context
	 * @param parent
	 * @param object
	 * @param value
	 */
	private void drawImageView(Context context, FrameLayout parent, LayoutObject object, Value value) {
		if (!(object instanceof Image) || value == null) {
			return;
		}

		Image image = (Image) object;
		ReloadableImageView view = new ReloadableImageView(context);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(getScaledValue(image.getRect()[2]), getScaledValue(image.getRect()[3]));
		params.leftMargin = getScaledValue(image.getRect()[0]);
		params.topMargin = getScaledValue(image.getRect()[1]);
		view.setLayoutParams(params);
		view.setScaleType(ImageView.ScaleType.FIT_XY);
		if (image.getValue() != null && image.getValue().length() > 0 && !StringUtil.isEmpty(value.getStringValue(image.getValue()))) {
			String imagePath = value.getStringValue(image.getValue());
			if (!imagePath.startsWith("http")) {
				imagePath = SnapsAPI.DOMAIN() + imagePath;
			}
			setImageViewFromUrl(context, view, imagePath);
		} else {
			view.setBackgroundColor(image.getBgColor());
		}

		if (!StringUtil.isEmpty(image.getClick()) && !StringUtil.isEmpty(value.getStringValue(image.getClick()))) {
			setClickEvent(view, value.getStringValue(image.getClick()));
		}

		parent.addView(view);
	}

	private void putMenuImageView(ImageView imageView, String path) {
		if (menuImageViews == null || imageView == null || path == null || path.length() < 1 || menuImageViews.containsKey(imageView)) {
			return;
		}
		menuImageViews.put(imageView, path);
	}

	private void setClickEvent(View view, final String targetUrl) {
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//duckwon write
				if (SnapsDevelopHelper.isCMDCatchMode()) { //for develop
					if (shouldOverrideUrlLoder != null) {
						MessageUtil.alertForCMDShow(shouldOverrideUrlLoder.getActivity(), targetUrl, new ICustomDialogListener() {
							@Override
							public void onClick(byte clickedOk) {
								if (clickedOk == ICustomDialogListener.OK) {
									loadUrl(targetUrl);
								} else {
									SnapsDevelopHelper.uploadCMDLogToFTPServer(shouldOverrideUrlLoder.getActivity(), targetUrl);
								}
							}
						});
					} else {
						loadUrl(targetUrl);
					}
					return;
				}

				loadUrl(targetUrl);
			}
		});
	}

	private void loadUrl(String targetUrl) {
		if (shouldOverrideUrlLoder != null) {
			shouldOverrideUrlLoder.shouldOverrideUrlLoading(targetUrl);
		}
	}

	/**
	 * Text 객체로 뷰 제작.
	 *
	 * @param object
	 * @return
	 */
	private void drawTextView(Context context, FrameLayout parent, LayoutObject object, Value value) {
		if (!(object instanceof Text)) {
			return;
		}

		Text text = (Text) object;
		TextView view;

		String[] valueStrAry = value == null ? new String[]{text.getValue()} : value.getStringValues(text.getValue());

		if (valueStrAry != null && valueStrAry.length > 0) {
			LinearLayout textContainer = new LinearLayout(context);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(getScaledValue(text.getRect()[2]), getScaledValue(text.getRect()[3]));
			params.leftMargin = getScaledValue(text.getRect()[0]);
			params.topMargin = getScaledValue(text.getRect()[1]);
			textContainer.setLayoutParams(params);
			textContainer.setOrientation(LinearLayout.HORIZONTAL);
			textContainer.setGravity(Gravity.CENTER_VERTICAL | ("center".equalsIgnoreCase(text.getAlign()) ? Gravity.CENTER_HORIZONTAL : Gravity.LEFT));

			LinearLayout.LayoutParams params2;
			for (int i = 0; i < valueStrAry.length; ++i) {
				view = new TextView(context);
				params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
				view.setLayoutParams(params2);
				view.setText(valueStrAry[i]);
				view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, text.getSize());
				view.setGravity(Gravity.CENTER_VERTICAL);
				view.setTextColor(text.getColor()[i]);

				if (!StringUtil.isEmpty(text.getFont()) && isContainTypeFace(text.getFont())) {
					view.setTypeface(typefaceMap.get(text.getFont()));
				}

				if (text.getUnderline() != 0) {
					ImageView underlineView = new ImageView(context);
					Paint paint = new Paint();
					paint.setTextSize(view.getTextSize());
					paint.setTypeface(view.getTypeface());
					int width = (int) paint.measureText(view.getText().toString());
					params = new FrameLayout.LayoutParams(width + getDptoPx(context, 6), getDptoPx(context, 9)); // 밑줄은 텍스트보다 좌우로 3dp씩 더 넓게.
					params.leftMargin = "center".equalsIgnoreCase(text.getAlign()) ? (getScaledValue(text.getRect()[2]) - width) / 2 - getDptoPx(context, 3) + getScaledValue(text.getRect()[0]) : getScaledValue(text.getRect()[0]) - getDptoPx(context, 3);
					params.topMargin = getScaledValue(text.getRect()[1]) + getScaledValue(text.getRect()[3]) - getDptoPx(context, 7); // 아래로 2dp 더.
					underlineView.setLayoutParams(params);
					underlineView.setImageDrawable(new ColorDrawable(text.getUnderline()));
					parent.addView(underlineView);
				}

				textContainer.addView(view);
			}

			if (text.getAdditions() != null && text.getAdditions().length > 0 && value != null) {
				ProductPrice productPrice;
				Price price;
				for (int i = 0; i < text.getAdditions().length; ++i) {
					productPrice = text.getAdditions()[i];
					price = priceMap.get(value.has(productPrice.getPriceKey()) ? value.getStringValue(productPrice.getPriceKey()) : "");
					if (price == null) {
						continue;
					}

					if (productPrice.getType() == LayoutObject.TYPE_PRICE) {
						view = makePriceText(context, Color.parseColor(productPrice.getPriceColors()[productPrice.getPriceColors().length > 2 ? 2 : 0]), text.getSize(), text.getFont());
						view.setText(getSpaceString(productPrice.getSpace()) + StringUtil.getGlobalCurrencyStr(context, price.isSale() ? price.getSalePrice() : price.getPrice(), false) + "~");
						textContainer.addView(view);
					} else if (price.isSale() && (productPrice.getType() == LayoutObject.TYPE_PERCENT || productPrice.getType() == LayoutObject.TYPE_DISCOUNT)) {
						if (productPrice.getType() == LayoutObject.TYPE_PERCENT) {
							view = makePriceText(context, Color.parseColor(productPrice.getPriceColors()[0]), text.getSize(), text.getFont());
							view.setText(getSpaceString(productPrice.getSpace()) + (price.isSalePercentIsUptoValue() ? "~" : "") + price.getSalePercent() + "%");
							textContainer.addView(view);
						} else {
							view = makePriceText(context, Color.parseColor(productPrice.getPriceColors()[1]), text.getSize(), text.getFont());
							view.setText(getSpaceString(productPrice.getSpace()));
							textContainer.addView(view);

							view = makePriceText(context, Color.parseColor(productPrice.getPriceColors()[1]), text.getSize(), text.getFont());
							view.setText(StringUtil.getGlobalCurrencyStr(context, price.getPrice(), false));
							view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
							textContainer.addView(view);
						}
					}
				}
			}

			parent.addView(textContainer);
		}
	}

	private boolean isContainTypeFace(String typeFace) {
		return typefaceMap != null && typefaceMap.containsKey(typeFace);
	}

	private String getSpaceString(int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; ++i) {
			sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 * ProductPrice 객체로 뷰 제작.
	 *
	 * @param object
	 * @return
	 */
	private void drawPriceText(Context context, FrameLayout parent, LayoutObject object, Value value) {
		if (!(object instanceof ProductPrice) || value == null) {
			return;
		}

		ProductPrice text = (ProductPrice) object;
		Price price = priceMap.get(value.has(text.getPriceKey()) ? value.getStringValue(text.getPriceKey()) : "");
		if (price == null) {
			return;
		}

		TextView view;
		LinearLayout textContainer = new LinearLayout(context);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(getScaledValue(text.getRect()[2]), getScaledValue(text.getRect()[3]));
		params.leftMargin = getScaledValue(text.getRect()[0]);
		params.topMargin = getScaledValue(text.getRect()[1]);
		textContainer.setLayoutParams(params);
		textContainer.setOrientation(LinearLayout.HORIZONTAL);

		if (price.isSale()) {

			view = makePriceText(context, Color.parseColor(text.getPriceColors()[1]), text.getSize(), text.getFont());
			view.setText(StringUtil.getGlobalCurrencyStr(context, price.getPrice(), false));
			view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			textContainer.addView(view);
		}

		view = makePriceText(context, Color.parseColor(text.getPriceColors()[text.getPriceColors().length > 2 ? 2 : 0]), text.getSize(), text.getFont());
		view.setText(StringUtil.getGlobalCurrencyStr(context, price.isSale() ? price.getSalePrice() : price.getPrice(), false) + "~");
		textContainer.addView(view);

		parent.addView(textContainer);
	}

	/**
	 * ProductPrice 객체로 뷰 제작.
	 *
	 * @param object
	 * @return
	 */
	private void drawPriceText(Context context, FrameLayout parent, LayoutObject object, Value value, int type) {
		if (!(object instanceof ProductPrice) || value == null) {
			return;
		}

		ProductPrice text = (ProductPrice) object;
		Price price = priceMap.get(value.has(text.getPriceKey()) ? value.getStringValue(text.getPriceKey()) : "");
		if (price == null) {
			return;
		}

		TextView view;
		LinearLayout textContainer = new LinearLayout(context);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(getScaledValue(text.getRect()[2]), getScaledValue(text.getRect()[3]));
		params.leftMargin = getScaledValue(text.getRect()[0]);
		params.topMargin = getScaledValue(text.getRect()[1]);
		textContainer.setLayoutParams(params);
		textContainer.setOrientation(LinearLayout.HORIZONTAL);

		if (type == LayoutObject.TYPE_PRICE) {
			view = makePriceText(context, Color.parseColor(text.getPriceColors()[text.getPriceColors().length > 2 ? 2 : 0]), text.getSize(), text.getFont());
			view.setText(StringUtil.getGlobalCurrencyStr(context, price.isSale() ? price.getSalePrice() : price.getPrice(), false) + "~");
			textContainer.addView(view);
		} else if (price.isSale() && (type == LayoutObject.TYPE_PERCENT || type == LayoutObject.TYPE_DISCOUNT)) {
			if (type == LayoutObject.TYPE_PERCENT) {
				view = makePriceText(context, Color.parseColor(text.getPriceColors()[0]), text.getSize(), text.getFont());
				view.setText((price.isSalePercentIsUptoValue() ? "~" : "") + price.getSalePercent() + "%");
				textContainer.addView(view);
			} else {
				view = makePriceText(context, Color.parseColor(text.getPriceColors()[1]), text.getSize(), text.getFont());
				view.setText(StringUtil.getGlobalCurrencyStr(context, price.getPrice(), false));
				view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				textContainer.addView(view);
			}
		} else {
			return;
		}

		parent.addView(textContainer);
	}

	private TextView makePriceText(Context context, int color, int size, String font) {
		TextView view = new TextView(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
		params.rightMargin = getScaledValue(5);
		view.setLayoutParams(params);
		view.setGravity(Gravity.CENTER_VERTICAL);
		view.setTextColor(color);
		view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
		if (!StringUtil.isEmpty(font) && isContainTypeFace(font)) {
			view.setTypeface(typefaceMap.get(font));
		}

		return view;
	}

	private void setImageViewFromUrl(final Context context, final ReloadableImageView imgView, final String path) {
		if (context == null || imgView == null) {
			return;
		}

		imgView.setPath(path);
		ImageLoader.with(ContextUtil.getContext()).load(path).into(imgView);

		putMenuImageView(imgView, path);
	}

	/**
	 * getters
	 */
	public ArrayList<Category> getCategories() {
		return menuData == null ? null : menuData.categories;
	}

	public HashMap<String, SubCategory> getSubCategories() {
		return menuData == null ? null : menuData.subCategoryMap;
	}

	public HashMap<String, Layout> getLayoutMap() {
		return menuData == null ? null : menuData.layoutMap;
	}

	public HashMap<String, Value> getHomeMap() {
		return menuData == null ? null : menuData.homeValueMap;
	}

	public Menu getMenuCrmLogin() {
		return menuData == null ? null : menuData.menuCrmLogin;
	}

	public Menu getMenuCrmLogout() {
		return menuData == null ? null : menuData.menuCrmLogout;
	}

	public String getUserNo() {
		return this.userNo;
	}

	public int getCrmIdx() {
		return menuData == null ? null : menuData.crmIdx;
	}

	public boolean isExistHomeMenu() {
		return menuData != null && menuData.isExistHomeMenu;
	}

	public MenuData getMenuData() {
		return menuData;
	}

	public Typeface getFontFromAsset(String fontName) {
		if (menuData != null && isContainTypeFace(fontName)) {
			return typefaceMap.get(fontName);
		}
		return null;
	}

	public NoticeItem getNoticeItem() {
		return noticeItem;
	}

	public SubCategory getSubCategoryByF_CLSS_CODE(String clssCode) {
		if (clssCode == null) {
			return null;
		}

		HashMap<String, SubCategory> mapCategory = getSubCategories();
		if (mapCategory == null) {
			return null;
		}

		Set<String> keySet = mapCategory.keySet();
		Iterator<String> keyIterator = keySet.iterator();

		SubCategory subCategory = null;

		while (keyIterator.hasNext()) {
			String key = keyIterator.next();
			if (key != null && key.equalsIgnoreCase(clssCode)) {
				subCategory = mapCategory.get(key);
				break;
			}
		}

		return subCategory;
	}

	public String getCurrentProductInfoUrl() {
		SnapsNativeUIManager nativeUIManager = SnapsNativeUIManager.getInstance();
		SnapsProductListParams productListParams = nativeUIManager.getCurrentProductListParams();
		if (productListParams != null) {
			MenuDataManager menuDataManager = MenuDataManager.getInstance();
			SubCategory subCategory = menuDataManager.getSubCategoryByF_CLSS_CODE(productListParams.getClssCode());
			if (subCategory != null) {
				return subCategory.getInfoUrl();
			}
		}
		return "";
	}

	public void initVersionData(final Context context) {
		if (menuData != null) {
			menuData.categoryVersion = "";
			menuData.subCategoryVersion = "";
			menuData.layoutVersion = "";
			menuData.homeValueVersion = "";
			menuData.menuValueVersion = "";
			menuData.spineInfoVersion = "";
			menuData.photoPrintVersion = "";
		}

		AsyncTask.execute(new Runnable() {
			@Override
			public void run() {
				save(context);
			}
		});
	}

	public static SubCategory findSubcategoryByUrl(String url) {
		if (url == null) {
			return null;
		}

		String decodedUrl = url;
		try {
			decodedUrl = URLDecoder.decode(url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			Dlog.e(TAG, e);
		}

		String classCode = StringUtil.getTitleAtUrl(decodedUrl, "F_CLSS_CODE");
		SubCategory subCategory = getInstance().getSubCategoryByF_CLSS_CODE(classCode);
		if (subCategory != null) {
			SnapsMenuManager menuMan = SnapsMenuManager.getInstance();
			if (menuMan != null) {
				menuMan.setSubCategory(subCategory);
			}
		}
		return subCategory;
	}

	public static void finalizeInstance() {
		instance = null;
	}

	private final int HANDLE_MSG_ALL_IMAGE_RELEASE = 1;
	private final int HANDLE_MSG_ALL_IMAGE_RELOAD = 2;

	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
			case HANDLE_MSG_ALL_IMAGE_RELEASE:
				if (menuImageViews == null) return;
				for (ImageView iv : menuImageViews.keySet()) {
					ImageUtil.recycleBitmap(iv);
				}
				break;
			case HANDLE_MSG_ALL_IMAGE_RELOAD:
				if (menuImageViews == null) return;
				for (ImageView iv : menuImageViews.keySet()) {
					if (iv == null || !menuImageViews.containsKey(iv)) continue; //FIXME ViewPager에서 다시 이미지를 로딩하는 코드가 없어서....
					String path = menuImageViews.get(iv);
					if (path != null && path.length() > 1) {
						//FIXME Picasso로 할건지, ImageLoader로 할건지...
						ImageLoader.with(ContextUtil.getContext()).load(path).into(iv);
					}
				}
				break;
		}
	}
}