package com.snaps.mobile.cseditor;

import android.content.Intent;

import com.snaps.mobile.cseditor.api.response.ResponseGetProjectDetail;

public interface CSEditorContract {

    String CS_EDITOR_RESULT_SCHEME = "EDITOR_SCHEMA";
    String TEST_SCHEME_TEST_URL = "test_scheme_test_url";
    String KEY_CURRENT_PROJECT_SCHEME = "key_current_project_schema";
    String SELECT_PRODUCT_SCHEME = "javascript_command_select_product_scheme";

    interface View extends BaseView<Presenter> {

        void finishActivity(Intent returnIntent);

        void setLastProjectData(String lastProjectCode);

        void showProgressBar();

        void hideProgressBar();

        void showProductList(CharSequence[] productLabels);
    }

    interface Presenter extends BasePresenter {

        void makeResultIntent(String projectCode, String productCode, String templateCode);

        void onClickGetProjectDetail(String projectCode);

        void onViewReady();

        void onClickGoToScheme(String scheme);

        void onChangeUseSmartSearch(boolean useSmartSearch);

        void onChangeUseUndefinedFontSearch(boolean useUndefinedFontSearch);

        void onClickMakeProduct();

        void onChooseProduct(int which);
    }

    interface BaseView<T> {
    }

    interface BasePresenter {

        //직접 Context를 전달해주는 것 보다, PrefUtil Manager 클래스를 넣어주는 것이 더 좋지만 구조상 Context를 일단 전달.
        void setView(CSEditorContract.View view);

    }

    interface GetProjectDetailIntractor {

        interface OnFinishedListener {

            void onFinished(ResponseGetProjectDetail projectDetail);

            void onFailure(Throwable t);

        }

        void requestGetProjectDetail(String projectCode, OnFinishedListener onFinishedListener);
    }

}


