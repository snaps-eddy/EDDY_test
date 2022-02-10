package com.snaps.mobile.utils.network.provider;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ICustomDialogListener;
import com.snaps.common.utils.ui.MessageUtil;
import com.snaps.common.utils.ui.StringUtil;
import com.snaps.mobile.utils.network.retrofit2.exception.SnapsNetworkThrowable;
import com.snaps.mobile.utils.network.retrofit2.genetator.SnapsNetworkGenerateBase;
import com.snaps.mobile.utils.network.retrofit2.genetator.etc.SnapsNetworkGenerateImpErr;
import com.snaps.mobile.utils.network.retrofit2.interfacies.ISnapsNetworkAPI;
import com.snaps.mobile.utils.network.retrofit2.interfacies.enums.eSnapsRetrofitAPI;
import com.snaps.mobile.utils.network.retrofit2.util.SnapsRetrofitProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import io.reactivex.disposables.CompositeDisposable;

public class SnapsRetrofit {
	private static final String TAG = SnapsRetrofit.class.getSimpleName();
	/**
	 * SnapsNetworkRequestBuilder 요청에 필요한 파라메터를 전송할 수 있다.
	 */
	public static ISnapsNetworkAPI with(@NonNull eSnapsRetrofitAPI service) {
		return createGeneratorInstance(service);
	}

	private static SnapsNetworkGenerateBase createGeneratorInstance(eSnapsRetrofitAPI retrofitAPI) {
		Class<?> clazz = retrofitAPI.getGenerateClass();
		if (clazz == null) {
			return new SnapsNetworkGenerateImpErr(); //API 작성을 확인 바람.
		}

		Constructor<?> constructor;
		try {
			constructor = clazz.getConstructor();
			Dlog.d("createGeneratorInstance() Retrofit api generated : " + constructor.getName());
			return (SnapsNetworkGenerateBase) constructor.newInstance();
		} catch (NoSuchMethodException e) {
			Dlog.e(TAG, e);
		} catch (IllegalAccessException e) {
			Dlog.e(TAG, e);
		} catch (InstantiationException e) {
			Dlog.e(TAG, e);
		} catch (InvocationTargetException e) {
			Dlog.e(TAG, e);
		}
		return null;
	}

	public static void showErrorMessageToast(Context context, SnapsNetworkThrowable throwable) {
		if (context == null || throwable == null) {
			return;
		}
		try {
			String errorMessage = throwable.getErrorMessage();
			if (StringUtil.isEmpty(errorMessage)) {
				return;
			}
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(() -> MessageUtil.toast(context.getApplicationContext(), errorMessage));
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	public static void showErrorMessageAlert(Context context, SnapsNetworkThrowable throwable, ICustomDialogListener onClick) {
		if (context == null || throwable == null || !(context instanceof Activity)) {
			return;
		}
		try {
			String errorMessage = throwable.getErrorMessage();
			if (StringUtil.isEmpty(errorMessage)) {
				return;
			}
			Handler handler = new Handler(Looper.getMainLooper());
			handler.post(() -> MessageUtil.alertnoTitleOneBtn((Activity) context, errorMessage, onClick));
		} catch (Exception e) {
			Dlog.e(TAG, e);
		}
	}

	//FIXME... Activity가 종료될 때 호출 해서 suspend 시키는 처리가 필요하다.
	public static void disposeOnDestroy(Context context) {
		CompositeDisposable compositeDisposable = SnapsRetrofitProvider.getCompositeDisposable(context);
		if (compositeDisposable != null) {
			compositeDisposable.dispose();

			SnapsRetrofitProvider snapsRetrofitManager = SnapsRetrofitProvider.getInstance();
			snapsRetrofitManager.deleteDisposeWithContext(context);
		}
	}

	public static void finalizeInstance() {
		SnapsRetrofitProvider snapsRetrofitManager = SnapsRetrofitProvider.getInstance();
		if (snapsRetrofitManager != null) {
			try {
				snapsRetrofitManager.disposeAll();
			} catch (Exception e) {
				Dlog.e(TAG, e);
			}
		}
	}

}
