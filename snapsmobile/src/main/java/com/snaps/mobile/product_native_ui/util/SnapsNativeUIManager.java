package com.snaps.mobile.product_native_ui.util;

import android.app.Activity;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.ui.IFacebook;
import com.snaps.common.utils.ui.IKakao;
import com.snaps.common.utils.ui.SnsFactory;
import com.snaps.mobile.activity.ui.menu.webinterface.web_event_handlers.SnapsProductListParams;
import com.snaps.mobile.component.SnapsNativeListViewProcess;
import com.snaps.mobile.product_native_ui.interfaces.ISnapsProductListOpserver;
import com.snaps.mobile.utils.thirdparty.SnapsTPAppManager;

import java.util.HashSet;
import java.util.Set;

import static com.snaps.mobile.product_native_ui.util.SnapsNativeUIManager.PRODUCT_LIST_SORT_TYPE.SORT_BY_POPULAR;

/**
 * StickyStyleNativeActivity와 생명주기를 같이 한다. (Native UI 를 구성할 때 사용)
 */
public class SnapsNativeUIManager {

	public static final boolean DEFAULT_LIST_UI_LARGE_VIEW = false; //디자인 리스트 화면에서 상품 크게 보기 모드를 기본으로 할지

	private static volatile SnapsNativeUIManager gInstance = null;

	public enum PRODUCT_LIST_SORT_TYPE {
		SORT_BY_POPULAR,
		SORT_BY_NEWEST
    }

	private Set<ISnapsProductListOpserver> listSortOpservers = null;    //ViewPager에서 Grid 타입일 경우, 정렬 기준을 변경 했을 때, 모든 Fragment의 정렬 기준도 함께 바꾸어 줘야 한다.
	private PRODUCT_LIST_SORT_TYPE currentProductListSortType = SORT_BY_POPULAR;
	private boolean isGridListLargeView = SnapsNativeUIManager.DEFAULT_LIST_UI_LARGE_VIEW;; //그리드 뷰 크게 보기

	private SnapsNativeListViewProcess nativeListViewProcess = null;    //Native product List 및 detail에서 사용.

	private SnapsProductListParams currentProductListParams = null;

	public static void createInstance() {
		if (gInstance ==  null) {
			synchronized (SnapsNativeUIManager.class) {
				if (gInstance ==  null) {
					gInstance = new SnapsNativeUIManager();
				}
			}
		}
	}

	public static SnapsNativeUIManager getInstance() {
		if(gInstance ==  null)
			createInstance();

		return gInstance;
	}

	public static void finalizeInstance() {
		if (gInstance != null) {
			gInstance.unRegiterListSortAllOpserver();
		}
		gInstance = null;
	}

	private SnapsNativeUIManager() {
		this.listSortOpservers = new HashSet<>();
	}

	public void unRegiterListSortAllOpserver() {
		if (listSortOpservers != null) {
			listSortOpservers.clear();
			listSortOpservers = null;
		}
	}

	public void registeListSortOpserver(ISnapsProductListOpserver opserver) {
		if (listSortOpservers != null)
			listSortOpservers.add(opserver);
	}

	public void notifyInvokeSortItems(PRODUCT_LIST_SORT_TYPE sortType) {
		if (getCurrentProductListSortType() == sortType) return;
		setCurrentProductListSortType(sortType);

		if (listSortOpservers == null) return;

		for (ISnapsProductListOpserver opserver : listSortOpservers) {
			if (opserver == null) continue;
			opserver.onRequestedProductListSort(sortType);
		}
	}

	public void notifyViewModeChange(boolean isGridListLargeView) {
		if (isGridListLargeView() == isGridListLargeView) return;
		setGridListLargeView(isGridListLargeView);

		if (listSortOpservers == null) return;

		for (ISnapsProductListOpserver opserver : listSortOpservers) {
			if (opserver == null) continue;
			opserver.onRequestedGridViewModeChange(isGridListLargeView);
		}
	}

	public PRODUCT_LIST_SORT_TYPE getCurrentProductListSortType() {
		return currentProductListSortType;
	}

	public void setCurrentProductListSortType(PRODUCT_LIST_SORT_TYPE currentProductListSortType) {
		this.currentProductListSortType = currentProductListSortType;
	}

	public SnapsNativeListViewProcess getNativeListViewProcess(Activity activity) {
		if (activity == null) return null;

		if(nativeListViewProcess == null)
			initNativeListViewProcess(activity);

		if(nativeListViewProcess == null)
			return null;

		nativeListViewProcess.setActivity(activity);

		return nativeListViewProcess;
	}

	public void initNativeListViewProcess(Activity activity) {
		IFacebook facebook = null;
		IKakao kakao = null;

		if (!SnapsTPAppManager.isThirdPartyApp(activity)) {
			if (Config.isFacebookService()) {
				facebook = SnsFactory.getInstance().queryInteface();
				facebook.init(activity);
			}
			kakao = SnsFactory.getInstance().queryIntefaceKakao();
		}

		final SnapsNativeListViewProcess process = new SnapsNativeListViewProcess(activity, facebook, kakao);
		setNativeListViewProcess(process);
	}

	public void setNativeListViewProcess(SnapsNativeListViewProcess nativeListViewProcess) {
		this.nativeListViewProcess = nativeListViewProcess;
	}

	public boolean isGridListLargeView() {
		return isGridListLargeView;
	}

	public void setGridListLargeView(boolean gridListLargeView) {
		isGridListLargeView = gridListLargeView;
	}

	public SnapsProductListParams getCurrentProductListParams() {
		return currentProductListParams;
	}

	public void setCurrentProductListParams(SnapsProductListParams currentProductListParams) {
		this.currentProductListParams = currentProductListParams;
	}
}
