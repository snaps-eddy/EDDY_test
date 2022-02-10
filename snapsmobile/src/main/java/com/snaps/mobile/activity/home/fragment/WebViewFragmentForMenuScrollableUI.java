package com.snaps.mobile.activity.home.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kmshack.newsstand.ScrollTabHolderFragment;
import com.snaps.mobile.R;
import com.snaps.mobile.component.ProgressWebView;
import com.snaps.mobile.component.SnapsWebviewProcess;
import com.snaps.mobile.interfaces.OnWebViewCreateListener;

public class WebViewFragmentForMenuScrollableUI extends ScrollTabHolderFragment {
	private ProgressWebView progressWebView;
	private ViewGroup container;
	private SnapsWebviewProcess  webviewProcess;
	
	private OnWebViewCreateListener listener;
	
	private String url;;
	
	private boolean webviewAttached = false;

	public static WebViewFragmentForMenuScrollableUI newInstance(String url) {
		WebViewFragmentForMenuScrollableUI fragment = new WebViewFragmentForMenuScrollableUI();
		fragment.url = url;
		return fragment;
	}

	public WebViewFragmentForMenuScrollableUI() {}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void setProgressWebView( ProgressWebView wv ) {
		this.progressWebView = wv;
		if( container != null && !webviewAttached ) attachWebView();
	}
	
	public ProgressWebView getProgressWebView() {
		return this.progressWebView;
	}
	
	
	public void setWebViewCreateListener( OnWebViewCreateListener listener ) {
		this.listener = listener;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dettachWebView();
	}

	private void attachWebView() {
		container.removeAllViews();
		if( progressWebView.getParent() != null ) ( (ViewGroup) progressWebView.getParent() ).removeAllViews();
		container.addView( progressWebView );
		
		if( listener != null ) listener.onWebViewCreated( progressWebView );
		if( webviewProcess != null ) progressWebView.addWebviewProcess( webviewProcess );
		if( !progressWebView.isLoadedWebView() ) progressWebView.loadUrl( url );
		webviewAttached = true;
	}

	public void dettachWebView() {
		if( container != null ) {
			container.removeAllViews();
			container.invalidate();
		}
		
		progressWebView = null;
		webviewProcess = null;
		listener = null;
		webviewAttached = false;
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate( R.layout.fragment_webview_for_menu_scrollable_ui, null );
		this.container = v;
		if( progressWebView != null && !webviewAttached ) attachWebView();
		return v;
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume(); 
	}

	@Override
	public void adjustScroll(int scrollHeight) {}
}
