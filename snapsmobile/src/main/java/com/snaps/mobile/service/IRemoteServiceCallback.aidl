package com.snaps.mobile.service;

interface IRemoteServiceCallback {
	// 업로드 진행도 
	void uploadState(int state, int complete, int total);
	// 에러 발생..
	void errorFire(int errCode,int complete, int totals); 
}