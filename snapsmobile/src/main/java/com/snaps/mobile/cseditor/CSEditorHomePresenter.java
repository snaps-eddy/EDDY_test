package com.snaps.mobile.cseditor;

import android.content.Context;
import android.content.Intent;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.ISnapsConfigConstants;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.snaps.mobile.cseditor.api.response.ResponseGetProjectDetail;
import com.snaps.mobile.cseditor.model.SnapsSchemeURL;

import static com.snaps.mobile.cseditor.CSEditorContract.KEY_CURRENT_PROJECT_SCHEME;

public class CSEditorHomePresenter implements CSEditorContract.Presenter {

    private CSEditorContract.View view;
    private Context mContext;
    private CSEditorContract.GetProjectDetailIntractor intractor;

    public ProductItem[] products = new ProductItem[]{
            new ProductItem("씰 스티커 스탠다드-무광", "{\"prmTmplCode\":\"045021035620\", \"paperCode\":\"160034\", " +
                    "\"prmProdCode\":\"00801200040001\",\"prmTmplId\":\"marvin_TS__361\",\"productCode\":\"00801200040001\",\"projectCount\":1, \"glossytype\":\"M\"," +
                    "\"papertype\":\"160034\",\"prmChnlCode\":\"KOR0031\",\"prmGlossyType\":\"M\",\"prmLangCode\":\"KOR\",\"prmPaperCode\":\"160034\"}"),

            new ProductItem("씰 스티커 스탠다드-유광", "{\"prmTmplCode\":\"045021035620\", \"paperCode\":\"160034\", " +
                    "\"prmProdCode\":\"00801200040001\",\"prmTmplId\":\"marvin_TS__361\",\"productCode\":\"00801200040001\",\"projectCount\":1, \"glossytype\":\"G\"," +
                    "\"papertype\":\"160034\",\"prmChnlCode\":\"KOR0031\",\"prmGlossyType\":\"G\",\"prmLangCode\":\"KOR\",\"prmPaperCode\":\"160034\"}"),

            new ProductItem("씰 스티커 투명", "{\"prmTmplCode\":\"045021035620\", \"paperCode\":\"160035\", " +
                    "\"prmProdCode\":\"00801200040001\",\"prmTmplId\":\"marvin_TS__361\",\"productCode\":\"00801200040001\",\"projectCount\":1, \"glossytype\":\"A\"," +
                    "\"papertype\":\"160035\",\"prmChnlCode\":\"KOR0031\",\"prmGlossyType\":\"M\",\"prmLangCode\":\"KOR\",\"prmPaperCode\":\"160035\"}"),

            new ProductItem("씰 스티커 투명-오로라", "{\"prmTmplCode\":\"045021035620\", \"paperCode\":\"160035\", " +
                    "\"prmProdCode\":\"00801200040001\",\"prmTmplId\":\"marvin_TS__361\",\"productCode\":\"00801200040001\",\"projectCount\":1, \"glossytype\":\"A\"," +
                    "\"papertype\":\"160035\",\"prmChnlCode\":\"KOR0031\",\"prmGlossyType\":\"A\",\"prmLangCode\":\"KOR\",\"prmPaperCode\":\"160035\"}"),

            new ProductItem("씰 스티커 투명-스파클", "{\"prmTmplCode\":\"045021035620\", \"paperCode\":\"160035\", " +
                    "\"prmProdCode\":\"00801200040001\",\"prmTmplId\":\"marvin_TS__361\",\"productCode\":\"00801200040001\",\"projectCount\":1, \"glossytype\":\"S\"," +
                    "\"papertype\":\"160035\",\"prmChnlCode\":\"KOR0031\",\"prmGlossyType\":\"S\",\"prmLangCode\":\"KOR\",\"prmPaperCode\":\"160035\"}"),

            new ProductItem("씰 스티커 리무버블-오로라", "{\"prmTmplCode\":\"045021035620\", \"paperCode\":\"160036\", " +
                    "\"prmProdCode\":\"00801200040001\",\"prmTmplId\":\"marvin_TS__361\",\"productCode\":\"00801200040001\",\"projectCount\":1, \"glossytype\":\"A\"," +
                    "\"papertype\":\"160036\",\"prmChnlCode\":\"KOR0031\",\"prmGlossyType\":\"A\",\"prmLangCode\":\"KOR\",\"prmPaperCode\":\"160036\"}"),

            new ProductItem("씰 스티커 리무버블-스파클", "{\"prmTmplCode\":\"045021035620\", \"paperCode\":\"160036\", " +
                    "\"prmProdCode\":\"00801200040001\",\"prmTmplId\":\"marvin_TS__361\",\"productCode\":\"00801200040001\",\"projectCount\":1, \"glossytype\":\"S\"," +
                    "\"papertype\":\"160036\",\"prmChnlCode\":\"KOR0031\",\"prmGlossyType\":\"S\",\"prmLangCode\":\"KOR\",\"prmPaperCode\":\"160036\"}"),

    };

    public CSEditorHomePresenter(Context context, CSEditorContract.GetProjectDetailIntractor intractor) {
        this.mContext = context;
        this.intractor = intractor;
    }

    @Override
    public void setView(CSEditorContract.View view) {
        this.view = view;
    }

    @Override
    public void makeResultIntent(String projectCode, String productCode, String templateCode) {
        /**
         * 사진 인화는 template code 가 비어있다.
         */
        if (projectCode.isEmpty() || productCode.isEmpty()) {
            return;
        }

        SnapsSchemeURL url = new SnapsSchemeURL(projectCode, productCode, templateCode);
        String impliedURL = url.getImpliedURL();

        Intent returnIntent = new Intent();
        returnIntent.putExtra(CSEditorContract.CS_EDITOR_RESULT_SCHEME, impliedURL);

        saveCurrentProjectSchema(impliedURL);
        view.finishActivity(returnIntent);
    }

    @Override
    public void onClickGetProjectDetail(String projectCode) {
        if (projectCode == null || projectCode.isEmpty()) {
            return;
        }

        view.showProgressBar();

        intractor.requestGetProjectDetail(projectCode, new CSEditorContract.GetProjectDetailIntractor.OnFinishedListener() {
            @Override
            public void onFinished(ResponseGetProjectDetail projectDetail) {
                if (projectDetail != null) {
                    logPathInfo(projectDetail.getXmlPath());
                    makeResultIntent(projectDetail.getProjectCode(), projectDetail.getProductCode(), projectDetail.getTemplateCode());
                }
                view.hideProgressBar();
            }

            @Override
            public void onFailure(Throwable t) {
                view.hideProgressBar();
            }
        });
    }

    private void logPathInfo(String xmlPath) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");

        String domain = SnapsAPI.DOMAIN();

        String saveXml = domain + xmlPath + ISnapsConfigConstants.SAVE_XML_FILE_NAME;
        sb.append(ISnapsConfigConstants.SAVE_XML_FILE_NAME).append(" : ").append(saveXml).append("\n");

        String auraOrderXml = domain + xmlPath + ISnapsConfigConstants.AURA_ORDER_XML_FILE_NAME;
        sb.append(ISnapsConfigConstants.AURA_ORDER_XML_FILE_NAME).append(" : ").append(auraOrderXml).append("\n");

        String prjOptionXml = domain + xmlPath + ISnapsConfigConstants.OPTION_XML_FILE_NAME;
        sb.append(ISnapsConfigConstants.OPTION_XML_FILE_NAME).append(" : ").append(prjOptionXml).append("\n");

        String ftpPath = xmlPath.replace("/Upload/", "/_mount/");
        sb.append("FTP Path : ").append(ftpPath).append("\n");

        Dlog.d(Dlog.PRE_FIX_CS + sb.toString());
    }

    @Override
    public void onViewReady() {
        String lastScheme = Setting.getString(mContext, KEY_CURRENT_PROJECT_SCHEME, null);
        if (lastScheme == null) {
            return;
        }

        SnapsSchemeURL lastURL = new SnapsSchemeURL(lastScheme);
        view.setLastProjectData(lastURL.getProjectCode());
    }

    @Override
    public void onClickGoToScheme(String scheme) {
        if (scheme == null || scheme.trim().length() < 1) {
            return;
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra(CSEditorContract.TEST_SCHEME_TEST_URL, scheme);

        saveCurrentProjectSchema(scheme);
        view.finishActivity(returnIntent);
    }

    private void saveCurrentProjectSchema(String impliedURL) {
        Setting.set(mContext, KEY_CURRENT_PROJECT_SCHEME, impliedURL);
    }

    @Override
    public void onChangeUseSmartSearch(boolean useSmartSearch) {
        Config.setUseDrawSmartSnapsSearchArea(useSmartSearch);
    }

    @Override
    public void onChangeUseUndefinedFontSearch(boolean useUndefinedFontSearch) {
        Config.setUseDrawUndefinedFontSearchArea(useUndefinedFontSearch);
    }

    @Override
    public void onClickMakeProduct() {
        if (products == null) {
            return;
        }

        String[] itemStrs = new String[products.length];

        for (int i = 0; i < products.length; i++) {
            itemStrs[i] = products[i].productLabel;
        }

        view.showProductList(itemStrs);
    }

    @Override
    public void onChooseProduct(int which) {
        if (products == null || products.length < which) {
            return;
        }

        ProductItem choosedItem = products[which];

        Intent returnIntent = new Intent();
        returnIntent.putExtra(CSEditorContract.SELECT_PRODUCT_SCHEME, choosedItem.productCommand);
        view.finishActivity(returnIntent);
    }

    private static class ProductItem {
        String productLabel;
        String productCommand;

        ProductItem(String productLabel, String productCommand) {
            this.productLabel = productLabel;
            this.productCommand = productCommand;
        }
    }
}
