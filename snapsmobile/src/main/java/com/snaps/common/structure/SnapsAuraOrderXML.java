package com.snaps.common.structure;

import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_ThumbNail;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.ui.ColorUtil;
import com.snaps.common.utils.ui.ProdNameUtil;
import com.snaps.instagram.utils.instagram.Const;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.StringWriter;

import errorhandle.logger.Logg;

/**
 * com.snaps.kakao.structure SnapsAuraOrderXML.java
 *
 * @author JaeMyung Park
 * @Date : 2013. 5. 27.
 * @Version :
 */
public class SnapsAuraOrderXML extends SnapsXML {
    private static final String TAG = SnapsAuraOrderXML.class.getSimpleName();

    public SnapsAuraOrderXML(XmlSerializer xml) {
        super(xml);
    }

    public SnapsAuraOrderXML(StringWriter writer) {
        super(writer);
    }

    public SnapsAuraOrderXML(FileOutputStream file) {
        super(file);
    }

    /**
     * @param template
     */
    public void setAlbumInfoXml_auraorder_item(SnapsTemplate template) {
        try {
            if (Const_PRODUCT.isSNSBook(Config.getPROD_CODE()))
                attribute(null, "type", "mbook");
            else if (Config.isSnapsSticker())
                attribute(null, "type", "sticker_kit");
            else if (Config.isSimplePhotoBook() || Config.isThemeBook() || Config.isSimpleMakingBook()) {
                if (template.info.F_PAPER_CODE.equals("160008"))
                    attribute(null, "type", "luxe_book");
                else
                    attribute(null, "type", "mbook");
            } else if (Config.isCalendar()) {
                attribute(null, "type", "calendar");
            } else if (Config.isIdentifyPhotoPrint() || Const_PRODUCT.isWoodFrame() || Const_PRODUCT.isPolaroidProduct() || Const_PRODUCT.isWalletProduct() || Const_PRODUCT.isInteiorFrame())
                attribute(null, "type", "template");
            else if (Const_PRODUCT.isMousePadProduct())
                attribute(null, "type", "mouse_pad");
            else if (Const_PRODUCT.isLegacyPhoneCaseProduct() || Const_PRODUCT.isUvPhoneCaseProduct() || Const_PRODUCT.isPrintPhoneCaseProduct())
                attribute(null, "type", "phone_case");
            else if (Const_PRODUCT.isDesignNoteProduct())
                attribute(null, "type", "note");
            else if (Const_PRODUCT.isPhotoMugCupProduct())
                attribute(null, "type", "MGC");
            else if (Const_PRODUCT.isTumblerProduct())
                attribute(null, "type", "TUMBLER");
            else if (Const_PRODUCT.isWoodBlockProduct())
                attribute(null, "type", Const_PRODUCT.PRODUCT_NAME_PACKAGE_WOOD_BLOCK);
            else if (Const_PRODUCT.isSquareProduct())
                attribute(null, "type", Const_PRODUCT.PRODUCT_NAME_PACKAGE_SQUARE);
            else if (Const_PRODUCT.isPostCardProduct())
                attribute(null, "type", Const_PRODUCT.PRODUCT_NAME_PACKAGE_POST_CARD);
            else if (Const_PRODUCT.isTtabujiProduct())
                attribute(null, "type", Const_PRODUCT.PRODUCT_NAME_PACKAGE_TTAEBUJI);
            else if (Const_PRODUCT.isPolaroidPackProduct() || Const_PRODUCT.isNewPolaroidPackProduct())
                attribute(null, "type", Const_PRODUCT.PRODUCT_NAME_PACKAGE_POLAROID);
            else if (Const_PRODUCT.isCardProduct())
                attribute(null, "type", Const_PRODUCT.PRODUCT_NAME_CARD);
            else if (Const_PRODUCT.isHangingFrameProduct(Config.getPROD_CODE()))
                attribute(null, "type", Const_PRODUCT.PRODUCT_HANGING_FRAME_TYPE);
            else if (Const_PRODUCT.isPhotoCardProduct())
                attribute(null, "type", Const_PRODUCT.PRODUCT_NAME_PHOTO_CARD);
            else if (Const_PRODUCT.isNewWalletProduct())
                attribute(null, "type", Const_PRODUCT.PRODUCT_NAME_WALLET_PHOTO);
            else if (Const_PRODUCT.isNewYearsCardProduct())
                attribute(null, "type", Const_PRODUCT.PRODUCT_NAME_YEARS_CARD);
            else if (Const_PRODUCT.isTransparencyPhotoCardProduct())
                attribute(null, "type", Const_PRODUCT.PRODUCT_NAME_TRANS_PHOTO_CARD);
//			else if (Const_PRODUCT.isAccordionCardProduct())
//				attribute(null, "type", Const_PRODUCT.PRODUCT_ACCORDION_CARD);
//			else if (Const_PRODUCT.isPosterGroupProduct())
//				attribute(null, "type", Const_PRODUCT.PRODUCT_POSTER);
            else
                attribute(null, "type", template.info.F_PROD_TYPE);

            attribute(null, "id", template.info.F_TMPL_CODE);

            if (Const_PRODUCT.isFrameProduct()) {
                if (!Config.isCalendar())
                    attribute(null, "prod_name", ProdNameUtil.getProdName(template.info.F_PAGE_MM_WIDTH, template.info.F_PAGE_MM_HEIGHT));
                else {
                    if (Config.PRODUCT_CALENDAR_MINI.equalsIgnoreCase(Config.getPROD_CODE()))
                        attribute(null, "prod_name", "PDESK_AX");
                    else if (Config.isCalendarWide(Config.getPROD_CODE()))
                        attribute(null, "prod_name", "WDESK_AX");
                    else if (Config.isCalendarNormal(Config.getPROD_CODE()))
                        attribute(null, "prod_name", "N8 X 6");
                    else if (Config.isCalendarVert(Config.getPROD_CODE()))
                        attribute(null, "prod_name", "HDESK_AX");
                    else if (Config.isCalendarLarge(Config.getPROD_CODE()))
                        attribute(null, "prod_name", "NDESK_A4");
                    else if (Config.isCalendarNormalVert(Config.getPROD_CODE()))
                        attribute(null, "prod_name", "N6 X 8");
                    else if (Config.isCalenderWall(Config.getPROD_CODE())) {
                        //벽걸이
                        if (Const_ThumbNail.PRODUCT_WALL_CALENDAR_LARGE.equals(Config.getPROD_CODE())) {
                            attribute(null, "prod_name", template.info.F_PROD_SIZE.replace("+", " "));
                        } else {
                            attribute(null, "prod_name", "HWALL_A2");
                        }
                    } else if (Config.isCalenderSchedule(Config.getPROD_CODE()))//스케즐러
                        attribute(null, "prod_name", "SDESK_A4");
                    else {
                        if (template.info != null && template.info.F_PROD_SIZE != null) {
                            attribute(null, "prod_name", template.info.F_PROD_SIZE.replace("+", " "));
                        }
                    }
                }
            } else
                attribute(null, "prod_name", template.info.F_PROD_SIZE.replace("+", " "));

            // 액자인경우 prodname을 사이즈에 맞게 넣어준다..
            if (Const_PRODUCT.isFrameProduct() || Const_PRODUCT.isPolaroidProduct() || Const_PRODUCT.isWalletProduct() || Const_PRODUCT.isNewWalletProduct() || Config.isIdentifyPhotoPrint()) {
                attribute(null, "mmWidth", template.info.F_PAGE_MM_WIDTH);
                attribute(null, "mmHeight", template.info.F_PAGE_MM_HEIGHT);
            }

            attribute(null, "prod_code", template.info.F_PROD_CODE);
            attribute(null, "prod_real_name", template.info.F_PROD_NAME);
            attribute(null, "prod_nick_name", template.info.F_PROD_NICK_NAME);

            //Ben
            //기존 상품과 다르게 씰스티커는 글루시 타입을 "matt", "glossy"로 변환해서 기록하지 않는다.
            //전달 받은 값을 그대로 기록한다. 씰 스티커 기준으로 말하면 "G", "M", "A", "S"
            if (Const_PRODUCT.isSealStickerProduct()) {
                Dlog.w(TAG, "SealStickerProduct glss_type:" + template.info.F_GLOSSY_TYPE);
                attribute(null, "glss_type", template.info.F_GLOSSY_TYPE);
            } else {
                // SnapsSelectProductJunctionForSimplePhotoBook 에 Glossy type 설정하는 부분이 있다. 고치려면 두 곳 모두 변경해야 한다.
                if ((Config.isSimplePhotoBook() && !Config.isKTBook() && !Config.isFanBook()) || Config.isSimpleMakingBook() || template.info.F_GLOSSY_TYPE.equals("M") || Const_PRODUCT.isNewYearsCardProduct())
                    attribute(null, "glss_type", "matt");
                else
                    attribute(null, "glss_type", "glossy");
            }

            attribute(null, "edge_type", "noedge");
            attribute(null, "pool_type", "paper");
            attribute(null, "brht_type", "no");
            attribute(null, "show_date", "no");
            attribute(null, "rcmm_yorn", "yes");
            attribute(null, "prnt_cnt", template.saveInfo.orderCount);
            attribute(null, "sell_price", template.priceList.get(0).F_SELL_PRICE);
            attribute(null, "disc_rate", template.priceList.get(0).F_DISC_RATE);
            attribute(null, "prod_type", template.info.F_PROD_TYPE);

            // 디자인 노트 라이/무지 구분자...
            // 20200602 @Marko -> 다른 상품과 다르게 미니배너는 페이퍼 코드를 아우라에 써달라고 요청이 와서 추가함.
            // 20200626 @Marko -> 아크릴 키링도 페이퍼 코드 아우라에 추가해야함.
            // 20200908 @Marko -> 추가되는 폰케이스 및 이전 폰케이스도 추가해야 함.
            if (Const_PRODUCT.isDesignNoteProduct() || Const_PRODUCT.isMiniBannerProduct() || Const_PRODUCT.isAcrylicKeyringProduct()
                    || Const_PRODUCT.isPrintPhoneCaseProduct() || Const_PRODUCT.isUvPhoneCaseProduct() || Const_PRODUCT.isLegacyPhoneCaseProduct()) {
                attribute(null, "paper_type", template.info.F_PAPER_CODE);
            }

            if (Const_PRODUCT.isMiniBannerProduct()) {
                attribute(null, "frame_type", template.info.F_FRAME_TYPE);
            }

            if (Config.isSnapsSticker()) {// TODO kakaobook, fb ??
                if (!Config.getUSER_COVER_COLOR().equalsIgnoreCase("")) {
                    attribute(null, "cover_color", String.valueOf(ColorUtil.getParseColor("#" + Config.getUSER_COVER_COLOR())));
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);

        }
    }
}
