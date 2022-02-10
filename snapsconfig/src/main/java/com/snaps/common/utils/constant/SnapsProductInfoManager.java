package com.snaps.common.utils.constant;

import android.content.Context;

/**
 * Created by ysjeong on 16. 6. 8..
 *
 * 제품을 만들때 사용할 전역 변수들.
 * 직접 사용해도 상관은 없지만, 될 수 있으며면 Config를 통해 사용하세요.
 */
public class SnapsProductInfoManager {
    private volatile static SnapsProductInfoManager gInstance = null;

    /** Project Data **/
    /** project code */
    private String PROJ_CODE = "";
    /** product code */
    private String PROD_CODE = "";
    /** template code */
    private String TMPL_CODE = "";
    /** product name */
    private String PROD_NAME = "";
    /** project name */
    private String PROJ_NAME = "";
    /** project upload type */
    private String PROJ_UTYPE = "";
    /** coverCode */
    private String TMPL_COVER = "";
    /** cover title */
    private String TMPL_COVER_TITLE = "";
    private String PAPER_CODE = "";
    private String CARD_QUANTITY = "";

    private String QUANTITY = "";

    private String FRAME_TYPE = "";

    private String BACK_TYPE = "";

    private String FRAME_ID = "";

    private String GLOSSY_TYPE = "";

    private String NOTE_PAPER_CODE = "";

    private String DESIGN_ID = "";

    /** 프로젝트 썸네일 시퀀스 **/
    private String YEAR_KEY = "";
    private String SQNC_KEY = "";

    /** 유저가 선택한 Color 값 **/
    private String USER_COVER_COLOR = "";

    private boolean isFromCart = false;

    private Context context = null;

    //템플릿에 이미지가 없는경우 true 기본값 false
    private boolean isPicCntZero = false;



    // 추천AI, 셀프AI관련 정보
    private boolean IS_SELFAI = false;
    private boolean IS_RECOMMENDAI = false;

    private String AI_RECOMMENDREQ = "";
    private String AI_SEARCHTYPE = "";
    private String AI_SEARCHVALUE = "";
    private String AI_SEARCHDATE = "";

    private boolean IS_SELFAI_EDITTING = false;

    public void setAI_SELFAI_EDITTING(boolean edit) { IS_SELFAI_EDITTING = edit; }

    public boolean getAI_SELFAI_EDITTING() { return IS_SELFAI_EDITTING; }

    public void setAI_IS_SELFAI(boolean self) {
        IS_SELFAI = self;
    }

    public boolean getAI_IS_SELFAI() {
        return IS_SELFAI;
    }

    public void setAI_IS_RECOMMENDAI(boolean recommend) {
        IS_RECOMMENDAI = recommend;
    }

    public boolean getAI_IS_RECOMMENDAI() {
        return IS_RECOMMENDAI;
    }

    public void setAI_RECOMMENDREQ(String value) {
        AI_RECOMMENDREQ = value;
    }

    public String getAI_RECOMMENDREQ() {
        return AI_RECOMMENDREQ;
    }

    public void setAI_SEARCHTYPE(String value) {
        AI_SEARCHTYPE = value;
    }

    public String getAI_SEARCHTYPE() {
        return AI_SEARCHTYPE;
    }

    public void setAI_SEARCHVALUE(String value) {
        AI_SEARCHVALUE = value;
    }

    public String getAI_SEARCHVALUE() {
        return AI_SEARCHVALUE;
    }

    public void setAI_SEARCHDATE(String value) {
        AI_SEARCHDATE = value;
    }

    public String getAI_SEARCHDATE() {
        return AI_SEARCHDATE;
    }

    public static void createInstance() {
        synchronized (SnapsProductInfoManager.class) {
            gInstance = new SnapsProductInfoManager();
        }
    }

    public static SnapsProductInfoManager getInstance() {
        if (gInstance ==  null)
            createInstance();
        return gInstance;
    }

    public static void finalizeInstance() {
        if(gInstance != null) {
            gInstance.cleanProductInfo();
            gInstance = null;
        }
    }

    public boolean isFromCart() {
        return isFromCart;
    }

    public void setFromCart(boolean fromCart) {
        isFromCart = fromCart;
    }

    public String getPROJ_CODE() {
        return PROJ_CODE;
    }

    public void setPROJ_CODE(String PROJ_CODE) {
        this.PROJ_CODE = PROJ_CODE;
    }

    public String getPROD_CODE() {
        return PROD_CODE;
    }

    public void setPROD_CODE(String PROD_CODE) {
        this.PROD_CODE = PROD_CODE;
    }

    public String getTMPL_CODE() {
        return TMPL_CODE;
    }

    public void setTMPL_CODE(String TMPL_CODE) {
        this.TMPL_CODE = TMPL_CODE;
    }

    public String getPROD_NAME() {
        return PROD_NAME;
    }

    public void setPROD_NAME(String PROD_NAME) {
        this.PROD_NAME = PROD_NAME;
    }

    public String getPROJ_NAME() {
        return PROJ_NAME;
    }

    public void setPROJ_NAME(String PROJ_NAME) {
        this.PROJ_NAME = PROJ_NAME;
    }

    public String getPROJ_UTYPE() {
        return PROJ_UTYPE;
    }

    public void setPROJ_UTYPE(String PROJ_UTYPE) {
        this.PROJ_UTYPE = PROJ_UTYPE;
    }

    public String getTMPL_COVER() {
        return TMPL_COVER;
    }

    public void setTMPL_COVER(String TMPL_COVER) {
        this.TMPL_COVER = TMPL_COVER;
    }

    public String getTMPL_COVER_TITLE() {
        return TMPL_COVER_TITLE;
    }

    public void setTMPL_COVER_TITLE(String TMPL_COVER_TITLE) {
        this.TMPL_COVER_TITLE = TMPL_COVER_TITLE;
    }

    public String getPAPER_CODE() {
        return PAPER_CODE;
    }

    public void setPAPER_CODE(String PAPER_CODE) {
        this.PAPER_CODE = PAPER_CODE;
    }

    public String getQUANTITY() {
        return QUANTITY;
    }

    public void setQUANTITY(String QUANTITY) {
        this.QUANTITY = QUANTITY;
    }

    public String getCARD_QUANTITY() {
        return CARD_QUANTITY;
    }

    public void setCARD_QUANTITY(String CARD_QUANTITY) {
        this.CARD_QUANTITY = CARD_QUANTITY;
    }

    public String getFRAME_TYPE() {
        return FRAME_TYPE;
    }

    public void setBACK_TYPE(String BACK_TYPE) {
        this.BACK_TYPE = BACK_TYPE;
    }

    public String getBACK_TYPE() {
        return BACK_TYPE;
    }

    public void setFRAME_TYPE(String FRAME_TYPE) {
        this.FRAME_TYPE = FRAME_TYPE;
    }

    public String getFRAME_ID() {
        return FRAME_ID;
    }

    public void setFRAME_ID(String FRAME_ID) {
        this.FRAME_ID = FRAME_ID;
    }

    public String getGLOSSY_TYPE() {
        return GLOSSY_TYPE;
    }

    public void setGLOSSY_TYPE(String GLOSSY_TYPE) {
        this.GLOSSY_TYPE = GLOSSY_TYPE;
    }

    public String getNOTE_PAPER_CODE() {
        return NOTE_PAPER_CODE;
    }

    public void setNOTE_PAPER_CODE(String NOTE_PAPER_CODE) {
        this.NOTE_PAPER_CODE = NOTE_PAPER_CODE;
    }

    public String getDESIGN_ID() {
        return DESIGN_ID;
    }

    public void setDESIGN_ID(String DESIGN_ID) {
        this.DESIGN_ID = DESIGN_ID;
    }

    public String getYEAR_KEY() {
        return YEAR_KEY;
    }

    public void setYEAR_KEY(String YEAR_KEY) {
        this.YEAR_KEY = YEAR_KEY;
    }

    public String getSQNC_KEY() {
        return SQNC_KEY;
    }

    public void setSQNC_KEY(String SQNC_KEY) {
        this.SQNC_KEY = SQNC_KEY;
    }

    public String getUserCoverColor() {
        return USER_COVER_COLOR;
    }

    public void setUserCoverColor(String userCoverColor) {
        USER_COVER_COLOR = userCoverColor;
    }

    public void cleanProductInfo() {
        setPROJ_CODE("");
        setPROD_CODE("");
        setTMPL_CODE("");
        //setPROD_NAME("");
        setPROJ_NAME("");
        setPROJ_UTYPE("");
        setTMPL_COVER("");
        setTMPL_COVER_TITLE("");
        setPAPER_CODE("");
        setCARD_QUANTITY("");
        setFRAME_TYPE("");
        setFRAME_ID("");
        setNOTE_PAPER_CODE("");
        setGLOSSY_TYPE("");
        setDESIGN_ID("");
        setUserCoverColor("");
        setSQNC_KEY("");
        setYEAR_KEY("");
        setPicCntZero(false);
        setBACK_TYPE("");

        setAI_IS_SELFAI(false);
        setAI_IS_RECOMMENDAI(false);
        setAI_RECOMMENDREQ("");
        setAI_SEARCHTYPE("");
        setAI_SEARCHVALUE("");
        setAI_SEARCHDATE("");
        setAI_SELFAI_EDITTING(false);
    }


    public boolean isPicCntZero() {
        return isPicCntZero;
    }

    public void setPicCntZero(boolean picCntZero) {
        isPicCntZero = picCntZero;
    }
}
