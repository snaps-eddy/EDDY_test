package com.snaps.common.structure;

import com.google.gson.Gson;
import com.snaps.common.data.parser.GetTemplateXMLHandler;
import com.snaps.common.structure.SnapsTemplateInfo.COVER_TYPE;
import com.snaps.common.structure.vo.AccessoriesOption;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.StringWriter;

/**
 * com.snaps.kakao.structure SnapsOptionXML.java
 *
 * @author JaeMyung Park
 * @Date : 2013. 5. 27.
 * @Version :
 */
public class SnapsOptionXML extends SnapsXML {
    private static final String TAG = SnapsOptionXML.class.getSimpleName();

    public SnapsOptionXML(XmlSerializer xml) {
        super(xml);
    }

    public SnapsOptionXML(StringWriter writer) {
        super(writer);
    }

    public SnapsOptionXML(FileOutputStream file) {
        super(file);
    }

    /**
     * @param template
     * @return
     */
    public XmlSerializer getAlbumInfoXm_option(SnapsTemplate template) {
        return null;
    }

    /**
     * @param temp
     * @return
     */
    public SnapsXML getOptionORDR(SnapsTemplate temp) {
        try {
            // 고정값
            this.addTag(null, "F_ORDR_CODE", "");
            this.addTag(null, "F_CP_CODE", temp.info.F_CP_CODE);
            // 프로젝트 코드
            this.addTag(null, "F_ALBM_ID", Config.getPROJ_CODE());
            // 주문 가격
            this.addTag(null, "F_ORDR_AMNT", temp.priceList.get(0).F_SELL_PRICE);
            this.addTag(null, "F_DLVR_AMNT", temp.info.F_DLVR_PRICE);
            // 할인 금액
            this.addTag(null, "F_DC_AMNT", "0");
            this.addTag(null, "F_DC_TYPE", "DLVR");
            // 프로젝트 코드
            this.addTag(null, "F_PROJ_CODE", Config.getPROJ_CODE());
            // 상품 가격
            this.addTag(null, "F_PROJ_PRICE", temp.priceList.get(0).F_SELL_PRICE);

            String quantity = Config.getQUANTITY();
            if (quantity != null && quantity.length() > 0) {
                this.addTag(null, "F_ORDR_CNTS", quantity);
            } else {
                if (temp.saveInfo.orderCount == null || temp.saveInfo.orderCount.length() == 0) {
                    temp.saveInfo.orderCount = "1";
                }
                this.addTag(null, "F_ORDR_CNTS", temp.saveInfo.orderCount);
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return this;
    }

    /**
     * @param temp
     * @param projname
     * @return
     */
    public SnapsXML getOptionMST(SnapsTemplate temp, String projname) {
        try {
            // 프로젝트 코드
            this.addTag(null, "F_PROJ_CODE", Config.getPROJ_CODE());
            // 상품 코드
            this.addTag(null, "F_PROD_CODE", temp.info.F_PROD_CODE);
            // 고정값
            this.addTag(null, "F_UUSER_ID", "");
            // 고정값
            this.addTag(null, "F_OPTN_CODE", "001");
            // 프로젝트 제목
            this.addTag(null, "F_PROJ_NAME", projname);

            // 상품 타입.

            if (Config.isSnapsSticker())
                this.addTag(null, "type", "sticker_kit");
            else if (Config.isThemeBook())
                this.addTag(null, "type", "photo_book");
            else if (Const_PRODUCT.isSNSBook())
                this.addTag(null, "type", "photo_book");
            else
                this.addTag(null, "type", temp.info.F_PROD_TYPE);

            // 추가한 페이지.
            this.addTag(null, "F_ADD_CNTS", temp.getF_ADD_PAGE() + "");
            // 프로젝트 금액.
            this.addTag(null, "F_PROJ_AMNT", temp.priceList.get(0).F_SELL_PRICE);
            this.addTag(null, "F_DISC_RATE", temp.priceList.get(0).F_DISC_RATE);

            // Glossy Type 무조건 M으로 변경. 2013.06.11 수정.
            if (Const_PRODUCT.isMetalFrame() || Const_PRODUCT.isPolaroidProduct() || Const_PRODUCT.isWalletProduct()) {
                // 메탈액자는 유광으로 설정..
                this.addTag(null, "F_GLOSSY_TYPE", "G");
            } else if (Config.isSnapsSticker()
                    || Const_PRODUCT.isTtabujiProduct()
                    || Const_PRODUCT.isTumblerProduct()
                    || Const_PRODUCT.isDesignNoteProduct()
                    || Config.isCalendar()
                    || Const_PRODUCT.isDIYStickerProduct()) {
                String glossyType = Config.getGLOSSY_TYPE();
                if (glossyType == null || glossyType.length() < 1)
                    glossyType = "M";

                this.addTag(null, "F_GLOSSY_TYPE", glossyType);
            } else if (Const_PRODUCT.isLegacyPhoneCaseProduct() || Const_PRODUCT.isUvPhoneCaseProduct() || Const_PRODUCT.isPrintPhoneCaseProduct()) {
                this.addTag(null, "F_GLOSSY_TYPE", temp.info.F_GLOSSY_TYPE);
            } else if (Const_PRODUCT.isAirpodsCaseProduct()) {
                SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
                this.addTag(null, "F_GLOSSY_TYPE", snapsProductOption.get(SnapsProductOption.KEY_GLOSSY_TYPE));
            } else {
                String glossyType = Config.getGLOSSY_TYPE();
                if (glossyType == null || glossyType.length() < 1)
                    glossyType = "M";

                this.addTag(null, "F_GLOSSY_TYPE", glossyType);
            }

//			if (Const_PRODUCT.isMetalFrame())// 탁상용 제품으로 표기..
//				this.addTag(null, "F_PAPER_CODE", "312001");
//			else
            if (Config.isSnapsSticker() || Const_PRODUCT.isTtabujiProduct() || Const_PRODUCT.isTumblerProduct()
                    || Config.isCalendar() || Const_PRODUCT.isNewWalletProduct() || Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isDIYStickerProduct() || Const_PRODUCT.isMetalFrame())
                this.addTag(null, "F_PAPER_CODE", Config.getPAPER_CODE());
            else
                this.addTag(null, "F_PAPER_CODE", temp.info.F_PAPER_CODE);

            // 포켓북 완료 유무.
//            this.addTag(null, "F_PRO_YORN", temp.getF_PRO_YORN());

            if (Const_PRODUCT.isAirpodsCaseProduct() || Const_PRODUCT.isBudsCaseProduct()) {
                this.addTag(null, "F_PRO_YORN", "Y");
            } else {
                // 포켓북 완료 유무.
                this.addTag(null, "F_PRO_YORN", temp.getF_PRO_YORN());
            }

            // 고정값
//			Logg.d("F_AFFX_NAME : 190002_" + Config.getAPP_VERSION());

            //플라로이드와 지갑용 사진은 제외 요청 : 20150604 솔루션 2팀
            if (Const_PRODUCT.isPolaroidProduct() || Const_PRODUCT.isWalletProduct() || Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct() || Config.isIdentifyPhotoPrint()) {
                this.addTag(null, "F_AFFX_NAME", "");
            } else {
                if (Const_PRODUCT.isFrameProduct()) {
                    this.addTag(null, "F_AFFX_NAME", temp.info.F_FRAME_ID + "||" + temp.info.F_PAGE_MM_WIDTH + "x" + temp.info.F_PAGE_MM_HEIGHT);
                } else {
                    this.addTag(null, "F_AFFX_NAME", "190002_" + Config.getAPP_VERSION());// 아이폰, 안드로이드 구분(190001_버전 iOS, 190002_버전 Android)
                }
            }

            // 고정값
            this.addTag(null, "F_AFFX_PRICE", "0");
            // 고정값
            this.addTag(null, "F_DLVR_CODE", "");
            // 고정값
            this.addTag(null, "F_DLVR_PRICE", "0");
            // 사용자가 사용한 이미지 카운트.
//			this.addTag(null, "F_USE_PIC_CNTS", String.valueOf(temp.delimgList.size()));
            this.addTag(null, "F_USE_PIC_CNTS", String.valueOf(temp.getImageCountOnAllPages()));

            this.addTag(null, "F_FRAME_TYPE", temp.info.F_FRAME_TYPE);
            // 레더커버인경우 커버 컬러 코드를 넣어준다.
            if (temp.info.F_COVER_TYPE != null && temp.info.F_COVER_TYPE.equals("leather"))
                this.addTag(null, "F_FRAME_ID", Config.getTMPL_COVER());
            else {
                // 원목액자 용...
                this.addTag(null, "F_FRAME_ID", temp.info.F_FRAME_ID);
            }

            this.addTag(null, "F_HPPN_TYPE", "190002");

            this.addTag(null, "F_APP_VER", Config.getAPP_VERSION());

            this.addTag(null, "F_SPINE_VER", MenuDataManager.getInstance().getMenuData().spineInfoVersion);

            if (temp.info.getCoverType() == COVER_TYPE.HARD_COVER) {
                this.addTag(null, "F_SPINE_NUM", temp.info.F_COVEREDGE_TYPE);
            } else {
                this.addTag(null, "F_SPINE_NUM", "0");
            }
            if (Const_PRODUCT.isBoardFrameProduct() || Const_PRODUCT.isMetalFrame()) {
                this.addTag(null, "F_BACK_TYPE", Config.getBACK_TYPE());

            }

            // 스냅스AI 추가 정보
            if (Config.getAI_IS_RECOMMENDAI() || Config.getAI_IS_SELFAI()) {
                this.addTag(null, "F_RECOMMEND_SEQ", Config.getAI_RECOMMENDREQ());
            }

            if (Const_PRODUCT.isAcrylicKeyringProduct()) {
                SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
                this.addTag(null, "F_BACK_TYPE", snapsProductOption.get(SnapsProductOption.KEY_KEYING_TYPE));
            }

            //에어팟 케이스, 에어팟 프로 케이스, 버즈 케이스 - 사용자가 선택한 케이스 칼라 정보
            if (Const_PRODUCT.isAirpodsCaseProduct() || Const_PRODUCT.isBudsCaseProduct() || Const_PRODUCT.isTinCaseProduct()) {
                SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
                String colorCode = snapsProductOption.get(SnapsProductOption.KEY_CASE_COLOR);
                this.addTag(null, "F_BACK_TYPE", colorCode);
            }

            // 반사 슬로건 류 - 사용자 or 전달받은 backtype 기록
            if (Const_PRODUCT.isMagicalReflectiveSloganProduct() || Const_PRODUCT.isHolographySloganProduct() || Const_PRODUCT.isReflectiveSloganProduct()) {
                SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
                String gradientType = snapsProductOption.get(SnapsProductOption.KEY_GRADIENT_TYPE);
                this.addTag(null, "F_BACK_TYPE", gradientType);
            }

            if (Const_PRODUCT.isUvPhoneCaseProduct()) {
                SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
                String deviceColorCode = snapsProductOption.get(SnapsProductOption.KEY_PHONE_CASE_DEVICE_COLOR);
                this.addTag(null, "F_BACK_TYPE", deviceColorCode);
            }


        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return this;
    }

    /**
     * @param temp
     * @param year
     * @param sqnc
     * @return
     */
    public SnapsXML getOptionDTL(SnapsTemplate temp, String year, String sqnc) {
        try {
            // 프로젝트 코드
            this.addTag(null, "F_PROJ_CODE", Config.getPROJ_CODE());
            // 상품 코드
            this.addTag(null, "F_PROD_CODE", temp.info.F_PROD_CODE);
            // 고정값
            this.addTag(null, "F_PAGE_NUM", "1");
            // 제품 코드
            this.addTag(null, "F_TMPL_CODE", temp.info.F_TMPL_CODE);
            this.addTag(null, "F_TMPL_SUB", temp.info.F_TMPL_SUB);
            // 커버 썸네일 Year
            this.addTag(null, "F_IMGX_YEAR", year);
            // 커버 썸네일 sqnc
            this.addTag(null, "F_IMGX_SQNC", sqnc);

            if (Config.isCalendar()) {
                this.addTag(null, "F_CAL_START", String.format("%d%02d", GetTemplateXMLHandler.getStartYear(), GetTemplateXMLHandler.getStartMonth()));
                this.addTag(null, "F_CAL_END", String.format("%d%02d", GetTemplateXMLHandler.getExpectEndYear(), GetTemplateXMLHandler.getExpectEndMonth()));
            }

            //아크릴 키링, 아크릴 등신대 - 이미지에 해당하는 아크릴 면적 정보 (가로, 세로)
            if (Const_PRODUCT.isAcrylicKeyringProduct() || Const_PRODUCT.isAcrylicStandProduct()) {
                SnapsProductOption snapsProductOption = SnapsTemplateManager.getInstance().getSnapsTemplate().getProductOption();
                this.addTag(null, "F_CAL_START", snapsProductOption.get(SnapsProductOption.KEY_MM_WIDTH));
                this.addTag(null, "F_CAL_END", snapsProductOption.get(SnapsProductOption.KEY_MM_HEIGHT));
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return this;
    }

    public SnapsXML writeAccessories(SnapsTemplate template) {

        SnapsProductOption productOption = template.getProductOption();
        String accessoriesRawText = productOption.getAccessoriesRawText();

        try {

            Gson gson = new Gson();
            JSONArray jsonAcc = new JSONArray(accessoriesRawText);

            for (int i = 0; i < jsonAcc.length(); i++) {

                Object obj = jsonAcc.get(i);
                AccessoriesOption ao = gson.fromJson(obj.toString(), AccessoriesOption.class);

                this.startTag(null, "ACCESSORY");

                this.addTag(null, "ACC_TMPL_CD", ao.getTemplateCode());
                this.addTag(null, "ACC_PROJ_CNT", String.valueOf(ao.getQuantity()));
                this.addTag(null, "ACC_PROD_CD", ao.getProductCode());

                this.endTag(null, "ACCESSORY");

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return this;
    }
}
