package com.snaps.mobile.service.ai;

/**
 * 현재 remote service로 구현해서 앱의 메모리 및 SharedPreferences에 접근 불가능 하다.
 * remote service가 아닌것으로 구현해도 원하는 값의 변경에 리스너를 추가 할수도 없는 구조이다.
 * 그래서 아래와 같이 필요한 값만 따로 빼두고 저쪽에서 폴링해서 값 변경시 알려주는 형태로 구현한다.
 */
class AppConfigClone {
    private static final String TAG = AppConfigClone.class.getSimpleName();
    private volatile boolean mIsAllowUploadMobileNetwork;

    private AppConfigClone() {
        //방어적으로 초기값 설정
        mIsAllowUploadMobileNetwork = false;
    }

    public static AppConfigClone getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final AppConfigClone INSTANCE = new AppConfigClone();
    }

    /**
     * 모바일 네트워크 사진 업로드 허용 설정
     *
     * @param isAllow
     */
    public void setAllowUploadMobileNetwork(boolean isAllow) {
        if (mIsAllowUploadMobileNetwork == isAllow) return;
        mIsAllowUploadMobileNetwork = isAllow;
    }

    /**
     * 모바일 네트워크 사진 업로드 허용 유무
     *
     * @return
     */
    public boolean isAllowUploadMobileNetwork() {
        return mIsAllowUploadMobileNetwork;
    }
}
