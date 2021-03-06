package com.snaps.mobile.utils.network.provider;

import android.content.Context;

import com.snaps.mobile.utils.network.retrofit2.data.request.body.SnapsRetrofitRequestBaseBody;
import com.snaps.mobile.utils.network.retrofit2.interfacies.enums.eSnapsRetrofitRequestParams;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class SnapsRetrofitRequestBuilder {
	private Context context;
	private SnapsRetrofitRequestBaseBody requestBody;
	private Scheduler subscribeOn;
	private Scheduler observeOn;
	private Map<eSnapsRetrofitRequestParams, Object> dynamicParamsMap;
	private HashMap<String, String> simplePostParamMap;
	private String baseUrl;
	//    private Map<String, File> multipartFileMap;
//    private Map<String, String> multipartFormMap;
	private boolean isShowProgressOnNetworking;

	private SnapsRetrofitRequestBuilder(Builder builder) {
		this.context = builder.context;
		this.requestBody = builder.requestBody;
		this.subscribeOn = builder.subscribeOn;
		this.observeOn = builder.observeOn;
		this.dynamicParamsMap = builder.dynamicParamsMap;
		this.baseUrl = builder.baseUrl;
//        this.multipartFileMap = builder.multipartFileMap;
//        this.multipartFormMap = builder.multipartFormMap;
		this.simplePostParamMap = builder.simplePostParamMap;
		this.isShowProgressOnNetworking = builder.isShowProgressOnNetworking;
	}

	public Context getContext() {
		return context;
	}

	public boolean isShowProgressOnNetworking() {
		return isShowProgressOnNetworking;
	}

	/*
		public Map<String, String> getMultipartFormMap() {
			return multipartFormMap;
		}

		public Map<String, File> getMultipartFileMap() {
			return multipartFileMap;
		}
	*/
	public SnapsRetrofitRequestBaseBody getRequestBody() {
		return requestBody;
	}

	public Scheduler getSubscribeOn() {
		return subscribeOn;
	}

	public Scheduler getObserveOn() {
		return observeOn;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public HashMap<String, String> getSimplePostParamMap() {
		return simplePostParamMap;
	}

	public String getDynamicParamsStrValue(eSnapsRetrofitRequestParams params) {
		if (dynamicParamsMap == null || !dynamicParamsMap.containsKey(params)) {
			return "";
		}
		Object object = dynamicParamsMap.get(params);
		if (!(object instanceof String)) {
			return "";
		}
		return (String) dynamicParamsMap.get(params);
	}

	public int getDynamicParamsIntValue(eSnapsRetrofitRequestParams params) {
		if (dynamicParamsMap == null || !dynamicParamsMap.containsKey(params)) {
			return -1;
		}
		Object object = dynamicParamsMap.get(params);
		if (!(object instanceof Integer)) {
			return -1;
		}
		return (Integer) dynamicParamsMap.get(params);
	}

	public static class Builder {
		private Context context;
		private SnapsRetrofitRequestBaseBody requestBody;
		private Scheduler subscribeOn; //?????? ???????????? ????????? ???????????? Thread ??????
		private Scheduler observeOn; //???????????? ????????? Thread ??????
		private Map<eSnapsRetrofitRequestParams, Object> dynamicParamsMap;
		private String baseUrl;
		//        private Map<String, File> multipartFileMap;
//        private Map<String, String> multipartFormMap;
		private HashMap<String, String> simplePostParamMap;
		private boolean isShowProgressOnNetworking;

		//use createBuilder
		private Builder(Context context) {
			this.context = context;
		}

		private Builder showProgressOnNetworking() {
			isShowProgressOnNetworking = true;
			return this;
		}

		public Builder setRequestBody(SnapsRetrofitRequestBaseBody requestBody) {
			this.requestBody = requestBody;
			return this;
		}

		public Builder setSyncScheduler() {
			setObserveOn(Schedulers.trampoline());
			setSubscribeOn(Schedulers.trampoline());
			return this;
		}

		/**
		 * Schedulers.io() - ?????? I/O??? ????????? ???????????? ????????? ????????? ?????? ?????? ?????????????????????. ???????????? ????????? ??? CachedThreadPool??? ???????????????. API ?????? ??? ??????????????? ????????? ?????? ??? ???????????????.
		 * <p>
		 * Schedulers.computation() : ????????? ????????? ????????? ???????????? ?????? ????????? ?????? ???????????????.
		 * <p>
		 * HandlerScheduler.from(handler) : ?????? ????????? ??????
		 * <p>
		 * AndroidSchedulers.mainThread() : UI Thread
		 * <p>
		 * Schedulers.trampoline() : Queue??? ?????? ????????? ????????? ?????? Thread?????? ?????? ??????.
		 */
		private Builder setSubscribeOn(Scheduler subscribeOn) { //??? ??? ????????? ???????????? ????????? ???????????? private?????? ???????????????.
			this.subscribeOn = subscribeOn;
			return this;
		}

		private Builder setObserveOn(Scheduler observeOn) { //??? ??? ????????? ???????????? ????????? ???????????? private?????? ???????????????.
			this.observeOn = observeOn;
			return this;
		}

		public Builder appendDynamicParam(eSnapsRetrofitRequestParams params, String strValue) {
			if (this.dynamicParamsMap == null) {
				this.dynamicParamsMap = new HashMap<>();
			}
			this.dynamicParamsMap.put(params, strValue);
			return this;
		}

		public Builder appendDynamicParam(eSnapsRetrofitRequestParams params, int intValue) {
			if (this.dynamicParamsMap == null) {
				this.dynamicParamsMap = new HashMap<>();
			}
			this.dynamicParamsMap.put(params, intValue);
			return this;
		}

		public Builder appendSimplePostParam(String params, String value) {
			if (this.simplePostParamMap == null) {
				this.simplePostParamMap = new HashMap<>();
			}
			this.simplePostParamMap.put(params, value);
			return this;
		}

		/*
				public Builder appendMultipartFile(String name, File file) {
					if (this.multipartFileMap == null) this.multipartFileMap = new HashMap<>();
					this.multipartFileMap.put(name, file);
					return this;
				}

				public Builder appendMultipartFormData(String name, String value) {
					if (this.multipartFormMap == null) this.multipartFormMap = new HashMap<>();
					this.multipartFormMap.put(name, value);
					return this;
				}

				public Builder appendMultipartFormData(String name, int value) {
					if (this.multipartFormMap == null) this.multipartFormMap = new HashMap<>();
					this.multipartFormMap.put(name, String.valueOf(value));
					return this;
				}
		*/
		public Builder setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
			return this;
		}

		public SnapsRetrofitRequestBuilder create() {
			return new SnapsRetrofitRequestBuilder(this);
		}
	}

	public static Builder createBuilder(Context context) {
		return new SnapsRetrofitRequestBuilder.Builder(context);
	}

	public static Builder createBuilderWithProgress(Context context) {
		return new SnapsRetrofitRequestBuilder.Builder(context).showProgressOnNetworking();
	}
}
