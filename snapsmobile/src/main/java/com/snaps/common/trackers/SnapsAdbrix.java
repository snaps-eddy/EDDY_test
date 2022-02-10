//package com.snaps.common.trackers;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.net.UrlQuerySanitizer;
//
//import com.igaworks.IgawCommon;
//import com.igaworks.IgawReceiver;
//import com.igaworks.adbrix.IgawAdbrix;
//import com.igaworks.commerce.IgawCommerce;
//import com.igaworks.commerce.IgawCommerceProductCategoryModel;
//import com.igaworks.commerce.IgawCommerceProductModel;
//import com.igaworks.interfaces.DeferredLinkListener;
//import com.igaworks.liveops.IgawLiveOps;
//import com.igaworks.liveops.livepopup.LiveOpsDeepLinkEventListener;
//import com.igaworks.liveops.livepopup.LiveOpsPopupResourceEventListener;
//import com.snaps.common.utils.log.Dlog;
//import com.snaps.mobile.activity.home.utils.SnapsLoginManager;
//
///**
// * Created by songhw on 2016. 5. 10..
// */
//public class SnapsAdbrix {
//	private static final String TAG = SnapsAdbrix.class.getSimpleName();
//
//	private static boolean isUse = false;
//
//	public static void setEnable(boolean flag) {
//		isUse = flag;
//	}
//
//	/**
//	 * 인스톨정보 전달
//	 *
//	 * @param con
//	 * @param intent
//	 */
//	public static boolean sendInstallInfo(Context con, Intent intent) {
//		if (!isUse) {
//			return false; // adbrix 사용중이 아니라면 false return
//		}
//
//		new IgawReceiver().onReceive(con, intent);
//		return true;
//	}
//
//	/**
//	 * 딥링크 리스너 등록
//	 *
//	 * @param act
//	 */
//	public static boolean setDeferredLinkListener(final Activity act) {
//		if (!isUse) {
//			return false;
//		}
//
//		IgawCommon.setDeferredLinkListener(act, new DeferredLinkListener() {
//			@Override
//			public void onReceiveDeeplink(String s) {
//				android.util.Log.i("IGAWORKS", "Deeplink: " + s);
//				Intent i = new Intent(Intent.ACTION_VIEW);
//				i.addCategory(Intent.CATEGORY_BROWSABLE);
//				i.setData(Uri.parse(s));
//				act.startActivity(i);
//			}
//		});
//		return true;
//	}
//
//	/**
//	 * 첫 액티비티에서 한번 실행해줌
//	 *
//	 * @param act
//	 */
//	public static void startAdbrix(Activity act) {
//		if (!isUse) {
//			return;
//		}
//		IgawCommon.startApplication(act);
//	}
//
//	/**
//	 * 분석용 액티비티의 onResume에서 실행
//	 *
//	 * @param act
//	 */
//	public static void startSession(Activity act) {
//		if (!isUse) {
//			return;
//		}
//		IgawCommon.startSession(act);
//	}
//
//	/**
//	 * 분석용 액티비티의 onPause에서 실행
//	 */
//	public static void endSession() {
//		if (!isUse) {
//			return;
//		}
//		IgawCommon.endSession();
//	}
//
//	/**
//	 * 회원가입 완료
//	 */
//	public static void joinComplete() {
//		if (!isUse) {
//			return;
//		}
//		IgawAdbrix.retention("joinComplete");
//	}
//
//	/**
//	 * 구매전환 완료 카운트
//	 */
//	public static void orderComplete() {
//		if (!isUse) {
//			return;
//		}
//		IgawAdbrix.retention("orderComplete");
//	}
//
//	private static IgawCommerce.Currency convertSttlCrrToIgawCurr(String sttlcrr) {
//		if (sttlcrr == null) {
//			return IgawCommerce.Currency.KR_KRW;
//		}
//
//		if (sttlcrr.equalsIgnoreCase("JPY")) {
//			return IgawCommerce.Currency.JP_JPY;
//		} else if (sttlcrr.equalsIgnoreCase("CNY")) {
//			return IgawCommerce.Currency.CN_CNY;
//		} else if (sttlcrr.equalsIgnoreCase("USD")) {
//			return IgawCommerce.Currency.US_USD;
//		} else {
//			return IgawCommerce.Currency.KR_KRW;
//		}
//	}
//
//	/**
//	 * 구매 완료 금액 범위
//	 */
//	public static void buyAmount(Context con, String url) {
//		if (!isUse) {
//			return;
//		}
//		UrlQuerySanitizer urlQuerySanitizer = new UrlQuerySanitizer(url);
//		String amount = urlQuerySanitizer.getValue("totalPrice");
//		String orderId = urlQuerySanitizer.getValue("orderCode");
//		String sttlCrrnc = urlQuerySanitizer.getValue("sttlCrrnc");
//
//		double dAmount = Double.parseDouble(amount.replaceAll(",", ""));
//
//		IgawCommerceProductModel productModel = IgawCommerceProductModel.create(
//				"",
//				"",
//				dAmount,
//				0.0,
//				1,
//				convertSttlCrrToIgawCurr(sttlCrrnc),
//				IgawCommerceProductCategoryModel.create("category01"),
//				null);
//
//		IgawAdbrix.purchase(con,
//				orderId,
//				productModel,
//				IgawCommerce.IgawPaymentMethod.MobilePayment);
//	}
//
//	public static void initIgawLiveOps(Context context, LiveOpsDeepLinkEventListener liveOpsDeepLinkEventListener) {
//		try {
//			if (SnapsLoginManager.isLogOn(context)) {
//				IgawCommon.setUserId(context, SnapsLoginManager.getUUserNo(context));
//				IgawLiveOps.initialize(context);
//
//				loadIgawPopup(context, liveOpsDeepLinkEventListener);
//			}
//		} catch (Exception e) {
//			Dlog.e(TAG, e);
//		}
//	}
//
//	private static void loadIgawPopup(final Context context, final LiveOpsDeepLinkEventListener liveOpsDeepLinkEventListener) {
//		//공지팝업 데이터로드
//		IgawLiveOps.requestPopupResource(context, new LiveOpsPopupResourceEventListener() {
//			@Override
//			public void onReceiveResource(boolean isReceive) {
//				if (isReceive) {
//					showIgawLiveNoticePopup(context, liveOpsDeepLinkEventListener);
//				}
//			}
//		});
//	}
//
//	private static void showIgawLiveNoticePopup(Context context, LiveOpsDeepLinkEventListener liveOpsDeepLinkEventListener) {
//		if (context == null || !(context instanceof Activity)) {
//			return;
//		}
//		IgawLiveOps.showPopUp((Activity) context, "snaps_android_igaw_notice_popup_space_key", liveOpsDeepLinkEventListener);
//	}
//}
