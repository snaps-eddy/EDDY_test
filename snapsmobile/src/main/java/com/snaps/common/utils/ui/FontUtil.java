package com.snaps.common.utils.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.snaps.common.spc.view.SnapsDiaryTextView;
import com.snaps.common.structure.control.SnapsTextControl;
import com.snaps.common.text.SnapsTextToImageUtil;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.constant.SnapsAPI;
import com.snaps.common.utils.file.DownloadFileAsync;
import com.snaps.common.utils.file.FileUtil;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.net.http.HttpUtil;
import com.snaps.mobile.activity.ui.menu.renewal.MenuDataManager;
import com.vividsolutions.jts.util.Assert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import errorhandle.logger.Logg;

public class FontUtil implements ISnapsFontConstans {
    private static final String TAG = FontUtil.class.getSimpleName();

    private static final String SNAPS_FONT_NAME_JSON_FILE = "snapsFonts.json";
    /**
     * 리뉴얼 및 글로벌에 사용되는 폰트 정의. 메뉴에 사용되는 폰트명과 템플릿에 들어가는 폰트명이 왜인지 모르지만 다르다 파일명은 같음.
     * {폰트명, 웹 저장된 파일명}
     */
    public static final int FONT_NAME = 0;
    public static final int FONT_PATH = 1;

    //한글 UI 폰트
    private static final String[] FONT_FILE_NAME_YOONGOTHIC_B = {"snapsYoonGothicB", "snaps_YoonGothicB.ttf"};
    private static final String[] FONT_FILE_NAME_YOONGOTHIC_M = {"snapsYoonGothicM", "snaps_YoonGothicM.ttf"};
    private static final String[] FONT_FILE_NAME_YOONGOTHIC_R = {"snapsYoonGothicR", "snaps_YoonGothicR.ttf"};

    //일어 UI폰트
    private static final String[] FONT_FILE_NAME_MEIRYO = {"Meiryo", "TEST_MEIRYO.TTC"};  //TODO:: 수정해야 함

    //아래 2개는 현재 사용 안하는 것 같은데...
    //추측해보면 단말기 기본 폰트를 변경한 경우를 대배해서 안드로이드 기본 폰트를 앱이 다운로드 받는듯 하다. 그러나 적용 안되어 있었음
    private static final String[] FONT_FILE_NAME_SOURCE_HAN_SANS_HW_REGULAR_JA = {"SourceHanSansHW-Regular", "SourceHanSansHW-Regular.otf"};
    private static final String[] FONT_FILE_NAME_SOURCE_HAN_SANS_HWSC_REGULAR_CH = {"SourceHanSansHWSC-Regular", "SourceHanSansHWSC-Regular.otf"};

    //아래 폰트는 책등용 폰트로 추정됨
    public static final String[] TEMPLATE_FONT_NAME_ROBOTO_EN = {"Roboto", "Roboto.ttf"};
    //    public static final String[] TEMPLATE_FONT_NAME_SOURCE_HAN_SANS_HW_SC_CH = { "Source Han Sans HW  SC", "SourceHanSansHWSC-Regular.otf" };
//    public static final String[] TEMPLATE_FONT_NAME_SOURCE_HAN_SANS_HW_JA = { "Source Han Sans HW", "SourceHanSansHW-Regular.otf" };
    public static final String[] TEMPLATE_FONT_NAME_SOURCE_HAN_SANS_HW_SC_CH = {"Source Han Sans HW SC Regular", "SourceHanSansHWSC-Regular.otf"};
    public static final String[] TEMPLATE_FONT_NAME_SOURCE_HAN_SANS_HW_JA = {"Source Han Sans HW Regular", "SourceHanSansHW-Regular.otf"};

    public static String FONT_FILE_PATH(Context context) {
        return Const_VALUE.PATH_PACKAGE(context, false) + "/font/";
    }

    public static String getFontFaceByChannel(String origin) {
        if (Config.isCalendar() || Const_PRODUCT.isNewPolaroidPackProduct() || SnapsTextToImageUtil.isSupportEditTextProduct())
            return origin; //달력과 뉴 폴라오리드 킷은 본래 폰트를 사용한다.

        String font = origin;
        if (Config.useEnglish()) font = TEMPLATE_FONT_NAME_ROBOTO_EN[0];
        else if (Config.useChinese()) font = TEMPLATE_FONT_NAME_SOURCE_HAN_SANS_HW_SC_CH[0];
        if (Config.useJapanese()) font = TEMPLATE_FONT_NAME_SOURCE_HAN_SANS_HW_JA[0];
        return font;
    }

    public static final int TEXT_TYPE_CONTENTS = 0;
    public static final int TEXT_TYPE_COMMENT = 1;
    public static final int TEXT_TYPE_CHAPTER = 2;
    public static final int TEXT_TYPE_CARD_TEXT = 10;
    public static final int TEXT_TYPE_CARD_TEXT_VERTICAL = 11;
    public static final int TEXT_TYPE_COMMENT2 = 20;
    public static final int TEXT_TYPE_CONTENTS2 = 21;
    public static final int TEXT_TYPE_DIARY = 30;
    public static final int TEXT_TYPE_PHOTO_CARD_TEXT = 40;


    public static final int TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_A = 100;
    public static final int TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_B = 101;
    public static final int TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_C = 102;
    public static final int TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_D = 103;

    public static final int TEXT_TYPE_INSTAGRAM_BOOK_COVER_ID = 200;
    public static final int TEXT_TYPE_INSTAGRAM_BOOK_COVER_NAME = 201;
    public static final int TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_COMMENT = 202;
    public static final int TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_LOCATION = 203;
    public static final int TEXT_TYPE_INSTAGRAM_BOOK_MAP_PAGE_LOCATION = 204;

    public static final int CHAR_TYPE_HANGUL = 0;
    public static final int CHAR_TYPE_ENG_LOWER = 1;
    public static final int CHAR_TYPE_ENG_UPPER = 2;
    public static final int CHAR_TYPE_NUMBER = 3;
    public static final int CHAR_TYPE_POINT = 4;
    public static final int CHAR_TYPE_SPACE = 5;
    public static final int CHAR_TYPE_ETC = 6;
    public static final int CHAR_TYPE_JPN = 7;
    public static final int CHAR_TYPE_CHN = 8;
    public static final int CHAR_TYPE_SPECIAL_CHAR = 9;

    private static FontUtil gInstance = null;

    private LinkedTreeMap<String, String> mapFonts = null;

    private HashMap<String, Typeface> mapTypeface = null;

    private DownloadFileAsync down;

    private int fontDownloadCount[];

    //private HashMap<String, String> _textListFont = null;
    private List<String> mFontList;

    private FontUtil() {
    }

    private boolean initialize(Context context) {
        mapTypeface = new HashMap<String, Typeface>();

        return insertFontMap(context); // 폰트 페이스와 폰트 이름을 매칭 시켜 놓는다.
    }

    public static void createInstance() {
        if (gInstance == null)
            gInstance = new FontUtil();
    }

    public static void finalizeInstance() {
        if (gInstance != null) {

            if (gInstance.mapFonts != null) {
                gInstance.mapFonts.clear();
                gInstance.mapFonts = null;
            }

            if (gInstance.down != null) {
                gInstance.down = null;
            }

            gInstance = null;
        }
    }

    public static FontUtil getInstance() {
        if (gInstance == null)
            createInstance();
        return gInstance;
    }

    public void initTextListFont() {
        mFontList = new ArrayList<>();
    }

    public List<String> getTextListFontName() {
        return mFontList;
    }

    public static void putUITextListFont(SnapsTextControl textControl, String key, String fontFace) {
        if (textControl == null || textControl.format == null || StringUtil.isEmpty(fontFace) || StringUtil.isEmpty(key))
            return;

        FontUtil fontUtil = getInstance();
        if (fontUtil.mFontList == null)
            fontUtil.mFontList = new ArrayList<>();

        String uiFont = "true".equalsIgnoreCase(textControl.format.bold) ? (fontFace + " Bold") : fontFace;
        if (fontUtil.mFontList.contains(uiFont) == false) {
            fontUtil.mFontList.add(uiFont);
        }
    }

    /**
     * 폰트 파일이 있는지 검사
     */
    public static boolean isExistFontFile(String fontName) {
        if (gInstance == null || fontName == null || fontName.length() < 1)
            return false;

        String sdPath = Const_VALUE.PATH_PACKAGE(ContextUtil.getContext(), false) + "/font";

        File file = new File(sdPath, fontName);

        return file.exists();
    }

    public static String getFontName(Context context, String fontFace) {
        if (getInstance() == null || fontFace == null || gInstance.mapFonts == null) return null;

        checkFontMapData(context);

        if (gInstance.mapFonts.containsKey(fontFace))
            return gInstance.mapFonts.get(fontFace);
        else
            return null;
    }

    private static boolean checkFontMapData(Context context) {
        if (getInstance() == null || gInstance.mapFonts == null) {
            return gInstance.initialize(context);
        }
        return true;
    }

    public static Typeface getFontTypeface(Context context, String fontFace) {
        return getFontTypeface(context, fontFace, false);
    }

    private static Typeface getFontTypeface(Context context, String fontFace, boolean isRetry) {
        if (getInstance() == null)
            return null;

        checkFontMapData(context);

        String fontName = getFontName(context, fontFace);
        if (fontName != null && fontName.length() > 0) {
            String sdPath = FontUtil.FONT_FILE_PATH(context);
            File path = new File(sdPath);
            if (!path.exists()) path.mkdirs();
            File fontFile = new File(sdPath, fontName);
            if (fontFile != null && fontFile.exists())
                try {
                    Typeface typeFace = gInstance.mapTypeface.get(fontFace);
                    if (typeFace == null) {
                        typeFace = Typeface.createFromFile(fontFile);
                        gInstance.mapTypeface.put(fontFace, typeFace);
                    }

                    return (typeFace == null ? Typeface.DEFAULT : typeFace);
                } catch (Exception e) {
                    Dlog.e(TAG, e); // FIXME 특정 폰트는 생성하지 못하는 문제가 있는데, 원인 파익이 안되고 있음(Roboto.ttf)
                }
            else if (!isRetry) {
                if (downloadFontFile(context, fontName)) {
                    return getFontTypeface(context, fontFace, true);
                }
            }
        }
        Dlog.w(TAG, "getFontTypeface() Typeface:" + fontFace);
        return null;
    }

    /**
     * Set 형태로 요청한 폰트 파일들을 모두 다운 받는다.
     */
    public static void downloadFontFiles(Context context, Set<String> fontFaces) {
        if (fontFaces == null || fontFaces.isEmpty() || getInstance() == null)
            return;

        checkFontMapData(context);

        try {
            Iterator<String> iterator = fontFaces.iterator();
            while (iterator.hasNext()) {
                String fontFace = iterator.next();
                if (fontFace != null && fontFace.length() > 0 && !fontFace.equalsIgnoreCase("null")) {
                    String fontName = gInstance.mapFonts.get(fontFace);
                    if (fontName != null && fontName.length() > 0 && !isExistFontFile(fontName))
                        downloadFontFile(context, fontName);
                }
            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    /**
     * 1개의 폰트 파일을 다운 받는다. 폰트 네임으로 호출해야 하기 때문에, 페이스만 알고 있다면 getFontName()을 활용.
     */
    public static boolean downloadFontFile(Context context, String fontName) {
        if (gInstance == null)
            return false;

        String url = FONT_DOWNLOAD_BASE_URL + fontName;
        try {
            gInstance.down = new DownloadFileAsync(context, fontName, url);
            return gInstance.down.syncProcess();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return false;
    }

    private boolean insertFontMap(Context context) {
        String json = FileUtil.getStringFromAsset(context, SNAPS_FONT_NAME_JSON_FILE);
        if (json != null && json.length() > 0) {
            Type type = new TypeToken<LinkedTreeMap<String, String>>() {
            }.getType();
            Gson gson = new Gson();
            try {
                mapFonts = gson.fromJson(json, type);
            } catch (Exception e) {
                Dlog.e(TAG, e);
                return false;
            }

            //폰트맵 파일 정리용
            if (Config.isDevelopVersion()) {
                try {
                    saveFontNameFontFileMappintFile(context);
                } catch (Exception e) {
                    Dlog.e(TAG, e);
                }
            }
        }

        return true;
    }

    private void saveFontNameFontFileMappintFile(Context context) {
        if (mapFonts == null) return;

        Map<String, String> mapFontCopy = new HashMap<>(mapFonts);
        List<String> keyList = new ArrayList<>(mapFontCopy.keySet());
        List<String> korKeyList = new ArrayList<>();
        List<String> engKeyList = new ArrayList<>();
        for(String key : keyList) {
            if (getCharType(key.charAt(0)) == CHAR_TYPE_HANGUL) {
                korKeyList.add(key);
            }
            else {
                engKeyList.add(key);
            }
        }

        Collections.sort(korKeyList);
        Collections.sort(engKeyList);
        keyList.clear();
        keyList.addAll(korKeyList);
        keyList.addAll(engKeyList);

        StringBuilder sb = new StringBuilder();

        for(String key : keyList) {
            String value = mapFontCopy.get(key);
            if (value == null) continue;

            mapFontCopy.remove(key);
            appendFontNameFontFileMapping(sb, key, value);

            for(Map.Entry<String, String> elem : mapFontCopy.entrySet()) {
                if (value.equals(elem.getValue())) {
                    appendFontNameFontFileMapping(sb, elem.getKey(), value);
                    mapFontCopy.remove(elem.getKey());
                    break;
                }
            }
        }

        String sdPath = FontUtil.FONT_FILE_PATH(context);
        File path = new File(sdPath);
        if (!path.exists()) path.mkdirs();

        final String result_ios = sb.toString();
        final String result_android = result_ios.replace("@", "");

        final String resultFileName_ios = "fontMappingTable_ios.txt";
        final String resultFileName_android = "fontMappingTable_android.txt";

        Map<String, String> fileDateInfoMap = new HashMap<>();
        fileDateInfoMap.put(resultFileName_ios, result_ios);
        fileDateInfoMap.put(resultFileName_android, result_android);
        for(Map.Entry<String, String> elem : fileDateInfoMap.entrySet()) {
            File file = new File(sdPath, elem.getKey());
            if (file.isFile()) {
                file.delete();
            }

            FileWriter fw = null;
            try {
                fw = new FileWriter(file);
                fw.write(elem.getValue());
                fw.flush();
            } catch (IOException e) {
                Dlog.e(TAG, e);
            } finally {
                if (fw != null) {
                    try {
                        fw.close();
                    }catch (IOException e) {
                        Dlog.e(TAG, e);
                    }
                }
            }
        }
    }

    private void appendFontNameFontFileMapping(StringBuilder sb, String fontName, String fontFileName) {
        if (sb.length() > 0) {
            sb.append(",").append("\n");
        }

        sb.append("@");
        sb.append("\"").append(fontName).append("\"");
        sb.append(" : ");
        sb.append("@");
        sb.append("\"").append(fontFileName).append("\"");
        return;
    }



    public String findFontFile(Context context, String fontName) {
        checkFontMapData(context);
        if (mapFonts == null || !mapFonts.containsKey(fontName)) return "";
        return mapFonts.get(fontName);
    }

    public static boolean isCharSupportInPhotobook(CharSequence charSequence) {
        if (charSequence == null || charSequence.length() < 1) return false;
        for (int ii = 0; ii < charSequence.length(); ii++) {
            char c = charSequence.charAt(ii);
            if (getCharType(c) == CHAR_TYPE_ETC) return false;
        }

        return true;
    }

    public static byte getCharType(char c) {
        //한글 ( 한글자 || 자음 , 모음 )
        if ((0xAC00 <= c && c <= 0xD7A3) || (0x3131 <= c && c <= 0x318E)) { //한
            return CHAR_TYPE_HANGUL;
        } else if ((0x3040 <= c && c <= 0x309F) || (0x30A0 <= c && c <= 0x30FF)) {//일
            return CHAR_TYPE_JPN;
        } else if ((0x4E00 <= c && c <= 0x9FFF) || (0x2E00 <= c && c <= 0x2E7F) || (0x3200 <= c && c <= 0x32FF) || (0x3400 <= c && c <= 0x4DBF) || (0x20000 <= c && c <= 0x2A6DF) || (0x2F800 <= c && c <= 0x2FA1F)) {//한자..
            return CHAR_TYPE_CHN;
        } else if ((0x41 <= c && c <= 0x5A)) {
            return CHAR_TYPE_ENG_UPPER;
        } else if (0x61 <= c && c <= 0x7A) {
            return CHAR_TYPE_ENG_LOWER;
        } else if (0x30 <= c && c <= 0x39) {
            return CHAR_TYPE_NUMBER;
        } else if (c == ',' || c == '.' || c == '\'') {
            return CHAR_TYPE_POINT;
        } else if (c == ' ') {
            return CHAR_TYPE_SPACE;
        } else if (c == '!' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' || c == '*') {
            return CHAR_TYPE_SPECIAL_CHAR;
        } else {
            return CHAR_TYPE_ETC;
        }
    }

    public static float getBreakTextSize(int textType, byte charType) {
        return getBreakTextSize(textType, charType, 0);
    }

    //FIXME 적용된 폰트에 대한 랜더 테스트 필요 함.
    public static float getBreakTextSize(int textType, byte charType, int textSize) {
        switch (charType) {
            case FontUtil.CHAR_TYPE_HANGUL:
                switch (textType) {
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_ID:
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_NAME:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_LOCATION:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_COMMENT:
                        return 26;//27
                    case TEXT_TYPE_INSTAGRAM_BOOK_MAP_PAGE_LOCATION:
                        return 28;//27
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_A:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_B:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_C:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_D:
                        return 24;//28;
                    case TEXT_TYPE_CONTENTS:
                    case TEXT_TYPE_CONTENTS2:
                    case TEXT_TYPE_CHAPTER:
                        return 27;//28;
                    case TEXT_TYPE_COMMENT:
                    case TEXT_TYPE_COMMENT2:
                        return 17;
                    case TEXT_TYPE_CARD_TEXT_VERTICAL:
                    case TEXT_TYPE_CARD_TEXT:
                        if (textSize < 12) {
                            return Const_PRODUCT.isCardShapeFolder() ? 35 : 35; //현재는 폴더랑 단면이랑 폰트 사이즈, 타입이 같다..
                        } else {
                            return Const_PRODUCT.isCardShapeFolder() ? 70 : 70; //현재는 폴더랑 단면이랑 폰트 사이즈, 타입이 같다..
                        }
                    case TEXT_TYPE_DIARY:
                        return 5f;
                    case TEXT_TYPE_PHOTO_CARD_TEXT:
                        return 70.58f;
                }
                break;
            case FontUtil.CHAR_TYPE_JPN:
            case FontUtil.CHAR_TYPE_CHN:
                switch (textType) {
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_ID:
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_NAME:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_LOCATION:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_COMMENT:
                        return 30;
                    case TEXT_TYPE_INSTAGRAM_BOOK_MAP_PAGE_LOCATION:
                        return 32;
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_A:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_B:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_C:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_D:
                        return 27;
                    case TEXT_TYPE_CONTENTS:
                    case TEXT_TYPE_CONTENTS2:
                    case TEXT_TYPE_CHAPTER:
                        return 31;
                    case TEXT_TYPE_COMMENT:
                    case TEXT_TYPE_COMMENT2:
                        return 20;
                    case TEXT_TYPE_CARD_TEXT_VERTICAL:
                    case TEXT_TYPE_CARD_TEXT:
                        if (textSize == 10) {
                            return Const_PRODUCT.isCardShapeFolder() ? 37 : 37; //현재는 폴더랑 단면이랑 폰트 사이즈, 타입이 같다..
                        } else {
                            return Const_PRODUCT.isCardShapeFolder() ? 74 : 74; //현재는 폴더랑 단면이랑 폰트 사이즈, 타입이 같다..
                        }
                    case TEXT_TYPE_DIARY:
                        return 8f;
                    case TEXT_TYPE_PHOTO_CARD_TEXT:
                        return charType == CHAR_TYPE_CHN ? 85f : 76f;
                }
                break;
            case FontUtil.CHAR_TYPE_ENG_LOWER:
                switch (textType) {
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_ID:
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_NAME:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_LOCATION:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_COMMENT:
                    case TEXT_TYPE_INSTAGRAM_BOOK_MAP_PAGE_LOCATION:
                        return 15.5f; //16
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_A:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_B:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_C:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_D:
                        return 13;//21;
                    case TEXT_TYPE_CONTENTS:
                    case TEXT_TYPE_CONTENTS2:
                    case TEXT_TYPE_CHAPTER:
                        return 17;//21;
                    case TEXT_TYPE_COMMENT:
                    case TEXT_TYPE_COMMENT2:
                        return 12;
                    case TEXT_TYPE_CARD_TEXT_VERTICAL:
                    case TEXT_TYPE_CARD_TEXT:
                        if (textSize == 10) {
                            return Const_PRODUCT.isCardShapeFolder() ? 22 : 22;
                        } else {
                            return Const_PRODUCT.isCardShapeFolder() ? 44 : 44;
                        }
                    case TEXT_TYPE_DIARY:
                        return 3f;
                    case TEXT_TYPE_PHOTO_CARD_TEXT:
                        return 42f;
                }
                break;
            case FontUtil.CHAR_TYPE_ENG_UPPER:
                switch (textType) {
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_ID:
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_NAME:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_LOCATION:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_COMMENT:
                    case TEXT_TYPE_INSTAGRAM_BOOK_MAP_PAGE_LOCATION:
                        return 18;
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_A:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_B:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_C:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_D:
                        return 16;//21;
                    case TEXT_TYPE_CONTENTS:
                    case TEXT_TYPE_CONTENTS2:
                    case TEXT_TYPE_CHAPTER:
                        return 17;//21;
                    case TEXT_TYPE_COMMENT:
                    case TEXT_TYPE_COMMENT2:
                        return 12;
                    case TEXT_TYPE_CARD_TEXT_VERTICAL:
                    case TEXT_TYPE_CARD_TEXT:
                        if (textSize == 10) {
                            return Const_PRODUCT.isCardShapeFolder() ? 23 : 23;
                        } else {
                            return Const_PRODUCT.isCardShapeFolder() ? 46 : 46;
                        }
                    case TEXT_TYPE_DIARY:
                        return 4f;
                    case TEXT_TYPE_PHOTO_CARD_TEXT:
                        return 52f;
                }
                break;
            case FontUtil.CHAR_TYPE_NUMBER:
                switch (textType) {
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_ID:
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_NAME:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_LOCATION:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_COMMENT:
                    case TEXT_TYPE_INSTAGRAM_BOOK_MAP_PAGE_LOCATION:
                        return 15.5f; //15
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_A:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_B:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_C:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_D:
                        return 16;//21;
                    case TEXT_TYPE_CONTENTS:
                    case TEXT_TYPE_CONTENTS2:
                    case TEXT_TYPE_CHAPTER:
                        return 20;
                    case TEXT_TYPE_COMMENT:
                    case TEXT_TYPE_COMMENT2:
                        return 12;
                    case TEXT_TYPE_CARD_TEXT_VERTICAL:
                    case TEXT_TYPE_CARD_TEXT:
                        if (textSize == 10) {
                            return Const_PRODUCT.isCardShapeFolder() ? 19 : 19;
                        } else {
                            return Const_PRODUCT.isCardShapeFolder() ? 38 : 38;
                        }
                    case TEXT_TYPE_DIARY:
                        return 3f;
                    case TEXT_TYPE_PHOTO_CARD_TEXT:
                        return 40f;
                }
                break;
            case FontUtil.CHAR_TYPE_SPACE:
                switch (textType) {
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_ID:
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_NAME:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_LOCATION:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_COMMENT:
                        return 12;
                    case TEXT_TYPE_INSTAGRAM_BOOK_MAP_PAGE_LOCATION:
                        return 9;
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_A:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_B:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_C:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_D:
                        return 8;//21;
                    case TEXT_TYPE_CONTENTS:
                    case TEXT_TYPE_CONTENTS2:
                    case TEXT_TYPE_CHAPTER:
                        return 17;
                    case TEXT_TYPE_COMMENT:
                    case TEXT_TYPE_COMMENT2:
                        return 10;
                    case TEXT_TYPE_CARD_TEXT_VERTICAL:
                    case TEXT_TYPE_CARD_TEXT:
                        if (textSize == 10) {
                            return Const_PRODUCT.isCardShapeFolder() ? 12 : 12;
                        } else {
                            return Const_PRODUCT.isCardShapeFolder() ? 24 : 24;
                        }
                    case TEXT_TYPE_DIARY:
                        return 1.6f;
                    case TEXT_TYPE_PHOTO_CARD_TEXT:
                        return 20f;
                }
                break;
            case FontUtil.CHAR_TYPE_POINT:
                switch (textType) {
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_ID:
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_NAME:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_LOCATION:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_COMMENT:
                    case TEXT_TYPE_INSTAGRAM_BOOK_MAP_PAGE_LOCATION:
                        return 8;
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_A:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_B:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_C:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_D:
                        return 8;//21;
                    case TEXT_TYPE_CONTENTS:
                    case TEXT_TYPE_CONTENTS2:
                    case TEXT_TYPE_CHAPTER:
                        return 12;
                    case TEXT_TYPE_COMMENT:
                    case TEXT_TYPE_COMMENT2:
                        return 7;
                    case TEXT_TYPE_CARD_TEXT_VERTICAL:
                    case TEXT_TYPE_CARD_TEXT:
                        if (textSize == 10) {
                            return Const_PRODUCT.isCardShapeFolder() ? 11 : 11;
                        } else {
                            return Const_PRODUCT.isCardShapeFolder() ? 22 : 22;
                        }
                    case TEXT_TYPE_DIARY:
                        return 1.6f;
                    case TEXT_TYPE_PHOTO_CARD_TEXT:
                        return 26f;
                }
                break;
            case FontUtil.CHAR_TYPE_SPECIAL_CHAR:
                switch (textType) {
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_ID:
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_NAME:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_LOCATION:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_COMMENT:
                    case TEXT_TYPE_INSTAGRAM_BOOK_MAP_PAGE_LOCATION:
                        return 10;
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_A:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_B:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_C:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_D:
                        return 10;//21;
                    case TEXT_TYPE_CONTENTS:
                    case TEXT_TYPE_CONTENTS2:
                    case TEXT_TYPE_CHAPTER:
                        return 14;
                    case TEXT_TYPE_COMMENT:
                    case TEXT_TYPE_COMMENT2:
                        return 9;
                    case TEXT_TYPE_CARD_TEXT_VERTICAL:
                    case TEXT_TYPE_CARD_TEXT:
                        if (textSize == 10) {
                            return Const_PRODUCT.isCardShapeFolder() ? 11 : 11;
                        } else {
                            return Const_PRODUCT.isCardShapeFolder() ? 22 : 22;
                        }
                    case TEXT_TYPE_DIARY:
                        return 3f;
                    case TEXT_TYPE_PHOTO_CARD_TEXT:
                        return 30f;
                }
                break;
            case FontUtil.CHAR_TYPE_ETC:
            default:
                switch (textType) {
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_ID:
                    case TEXT_TYPE_INSTAGRAM_BOOK_COVER_NAME:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_LOCATION:
                    case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_COMMENT:
                    case TEXT_TYPE_INSTAGRAM_BOOK_MAP_PAGE_LOCATION:
                        return 30;
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_A:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_B:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_C:
                    case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_D:
                        return 22;//21;
                    case TEXT_TYPE_CONTENTS:
                    case TEXT_TYPE_CONTENTS2:
                    case TEXT_TYPE_CHAPTER:
                        return 20;
                    case TEXT_TYPE_COMMENT:
                    case TEXT_TYPE_COMMENT2:
                        return 12;
                    case TEXT_TYPE_CARD_TEXT_VERTICAL:
                    case TEXT_TYPE_CARD_TEXT:
                        if (textSize == 10) {
                            return Const_PRODUCT.isCardShapeFolder() ? 33 : 33;
                        } else {
                            return Const_PRODUCT.isCardShapeFolder() ? 66 : 66;
                        }
                    case TEXT_TYPE_DIARY:
                        return 5.f;
                    case TEXT_TYPE_PHOTO_CARD_TEXT:
                        return 74f;
                }
                break;
        }
        return 0;
    }

    public static int customBreakText(String text, int textType) {
        return customBreakText(text, textType, 0);
    }

    public static int customBreakText(String text, int textType, int textSize) {
        if (text == null || text.length() < 1) return 0;

        float MAX_LENGTH = 0;
        switch (textType) {
            case TEXT_TYPE_CONTENTS:
                MAX_LENGTH = Config.isFacebook_Photobook() ? 970 : 850;//690;
                break;
            case TEXT_TYPE_CONTENTS2:
                MAX_LENGTH = Config.isFacebook_Photobook() ? 880 : 850;//690;
                break;
            case TEXT_TYPE_COMMENT:
                MAX_LENGTH = Config.isFacebook_Photobook() ? 650 : 590;
                break;
            case TEXT_TYPE_COMMENT2:
                MAX_LENGTH = Config.isFacebook_Photobook() ? 600 : 590;
                break;
            case TEXT_TYPE_CHAPTER:
                MAX_LENGTH = 720;
                break;
            case TEXT_TYPE_CARD_TEXT:
                MAX_LENGTH = Const_PRODUCT.isCardShapeFolder() ? 1200 : 1570; // 폴더가 0.75정도 더 작다..
                break;
            case TEXT_TYPE_CARD_TEXT_VERTICAL:
                MAX_LENGTH = Const_PRODUCT.isCardShapeFolder() ? 1782 : 2376; // 폴더가 0.75정도 더 작다..
                break;
            case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_A:
                MAX_LENGTH = 160;
                break;
            case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_B:
                MAX_LENGTH = 177;
                break;
            case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_C:
            case TEXT_TYPE_FACEBOOKPHOTOBOOK_FRIEND_NAME_D:
                MAX_LENGTH = 173;
                break;
            case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_COMMENT:
                MAX_LENGTH = 500;
                break;
            case TEXT_TYPE_INSTAGRAM_BOOK_COVER_ID:
                MAX_LENGTH = 242.5f; //242
                break;
            case TEXT_TYPE_INSTAGRAM_BOOK_COVER_NAME:
                MAX_LENGTH = 406; // 410
                break;
            case TEXT_TYPE_INSTAGRAM_BOOK_INNER_PAGE_LOCATION:
                MAX_LENGTH = 400;
                break;
            case TEXT_TYPE_INSTAGRAM_BOOK_MAP_PAGE_LOCATION:
                MAX_LENGTH = 290;
                break;
            case TEXT_TYPE_DIARY:

                MAX_LENGTH = SnapsDiaryTextView.DIARY_CONTENTS_WIDTH_OFFSET;
                break;
            /**
             * FIXME ALIGN LEFT하면 위의 수치가 정확히 맞는데, 랜더에서 센터 정렬을 잘 못하는것 같다.(결과물을 보면 우측으로 치우침..)
             */
//			MAX_LENGTH = Const_PRODUCT.isCardShapeFolder() ? 890 : 1100; //

            case TEXT_TYPE_PHOTO_CARD_TEXT:
                MAX_LENGTH = 1200;
                break;

            default:
                break;
        }

        float length = 0f;
        int result = 0;
        boolean bOverLength = false;

        for (int j = 0; j < text.length(); j++) {
            char c = text.charAt(j);

            byte charType = FontUtil.getCharType(c);
            float breakTextSize = getBreakTextSize(textType, charType, textSize);

            breakTextSize *= fixWeightTextSizeByChar(c);

            length += breakTextSize;

            if (length >= MAX_LENGTH) {
                bOverLength = true;
                result = j;
                break;
            }
        }

        return bOverLength ? result : text.length();
    }

    private static float fixWeightTextSizeByChar(char c) {
        //FIXME 다른 폰트는 검증이 안되서 우선 두 제품의 폰트만 테스트 후 적용 한다
        if (Const_PRODUCT.isCardProduct() || Const_PRODUCT.isPhotoCardProduct() || Const_PRODUCT.isNewWalletProduct()) {
            if (c == 'b' || c == 'd' || c == 'g' || c == 'q' || c == 'p'
                    || c == '4' || c == '6' || c == '8' || c == '0'
                    || c == 'A' || c == 'D' || c == 'G') return 1.2f;
            else if (c == 'i' || c == 'l' || c == 'j' || c == 'f' || c == 'I' || c == '!')
                return .5f;
            else if (c == 'J' || c == 'L') return .85f;
            else if (c == 'm' || c == 'w' || c == 'M' || c == 'O' || c == 'Q' || c == 'W')
                return 1.4f;
        }
        return 1f;
    }


    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////

    public enum eSnapsFonts {
        YOON_GOTHIC_760(FONT_FILE_NAME_YOONGOTHIC_B[0], FONT_FILE_NAME_MEIRYO[0], "", "", ""),
        YOON_GOTHIC_740(FONT_FILE_NAME_YOONGOTHIC_M[0], FONT_FILE_NAME_MEIRYO[0], "", "", ""),
        YOON_GOTHIC_720(FONT_FILE_NAME_YOONGOTHIC_R[0], FONT_FILE_NAME_MEIRYO[0], "", "", ""),
        YOON_GOTHIC_330("스냅스 윤고딕 330", FONT_FILE_NAME_MEIRYO[0], "", "", "");        //이것은 현재 사용하지 않는 햄버거 메뉴에서 사용

        private final String mKorFontName;
        private final String mJpnFontName;
        private final String mEngFontName;
        private final String mChnFontName;
        private final String mEtcFontName;

        eSnapsFonts(String kor, String jpn, String eng, String chn, String etc) {
            mKorFontName = kor;
            mJpnFontName = jpn;
            mEngFontName = eng;
            mChnFontName = chn;
            mEtcFontName = etc;
        }

        public String getFontFileName() {
            if (Config.useKorean()) return mKorFontName;
            if (Config.useJapanese()) return mJpnFontName;
            if (Config.useEnglish()) return mEngFontName;
            if (Config.useChinese()) return mChnFontName;
            return mEtcFontName;
        }
    }

    private static Map<String, Typeface> mTypefaceMap;

    private static List<String[]> getFontInfoList() {
        List<String[]> list = new ArrayList<String[]>();

        if (Config.useKorean()) {
            list.add(FONT_FILE_NAME_YOONGOTHIC_B);
            list.add(FONT_FILE_NAME_YOONGOTHIC_M);
            list.add(FONT_FILE_NAME_YOONGOTHIC_R);
        } else if (Config.useJapanese()) {
            list.add(FONT_FILE_NAME_SOURCE_HAN_SANS_HW_REGULAR_JA);  //사용하지 않는데 일단 받자
            // list.add(FONT_FILE_NAME_MEIRYO);	//TODO::다음에 변경할 폰트
        } else if (Config.useChinese()) {
            list.add(FONT_FILE_NAME_SOURCE_HAN_SANS_HWSC_REGULAR_CH);
        } else {
            //기타의 경우는 그냥 단말기 시스템에 설정된 폰트 사용
        }

        return list;
    }

    public static void loadDownloadFonts(Context context) {
        List<String[]> list = getFontInfoList();
        for (String[] fontInfo : list) {
            addFontTypeface(context, fontInfo[0]);
        }
    }

    private static boolean addFontTypeface(Context context, String fontName) {
        if (mTypefaceMap == null) {
            mTypefaceMap = new HashMap<String, Typeface>();
        } else if (mTypefaceMap.containsKey(fontName)) {
            return true;
        }

        File file = new File(FontUtil.FONT_FILE_PATH(context) + fontName);
        if (!file.exists()) {
            return false;
        }

        Typeface typeFace = null;
        try {
            typeFace = Typeface.createFromFile(file);
            if (typeFace == null) return false;
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return false;
        }

        mTypefaceMap.put(fontName, typeFace);
        return true;
    }

    private static Typeface getFontTypeface(String fontName) {
        if (mTypefaceMap == null) return null;
        return mTypefaceMap.get(fontName);
    }

    public static void applyTextViewTypeface(TextView textView, eSnapsFonts eFont) {
        if (textView == null || eFont == null) return;

        try {
            Typeface typeface = getFontTypeface(eFont.getFontFileName());
            if (typeface == null) return;
            textView.setTypeface(getTypeface(eFont));
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }

    public static Typeface getTypeface(eSnapsFonts eFont) {
        if (eFont == null) return null;
        return getFontTypeface(eFont.getFontFileName());
    }

    public static boolean checkAllFontExist() {
        FontUtil fontUtil = getInstance();
        return fontUtil.fontDownloadCount != null && fontUtil.fontDownloadCount[1] < 1;
    }

    public static int getTotalDownloadFontCount() {
        FontUtil fontUtil = getInstance();
        return fontUtil.fontDownloadCount != null && fontUtil.fontDownloadCount.length > 1 ? fontUtil.fontDownloadCount[1] : 0;
    }

    public static int getCurrentDownloadFontCount() {
        FontUtil fontUtil = getInstance();
        return fontUtil.fontDownloadCount != null && fontUtil.fontDownloadCount.length > 0 ? fontUtil.fontDownloadCount[0] : 0;
    }

    public boolean downloadFont(Context context, HttpUtil.DownloadProgressListener downloadProgressListener) {
        List<String[]> list = getFontInfoList();
        for (String[] fontInfo : list) {
            boolean isSuccess = downloadFontFile(context, fontInfo, downloadProgressListener);
            if (isSuccess) {
                fontDownloadCount[0]++;
            } else {
                return false;
            }
        }
        return true;
    }

    private boolean downloadFontFile(Context context, String[] fileName, HttpUtil.DownloadProgressListener listener) {
        return HttpUtil.saveUrlToFileWithListener(SnapsAPI.MENU_FONT_DOWNLOAD_PATH() + fileName[1], FontUtil.FONT_FILE_PATH(context) + fileName[0], listener);
    }

    public static boolean isShouldDownloadFont() {
        FontUtil fontUtil = getInstance();
        return fontUtil.fontDownloadCount != null && fontUtil.fontDownloadCount.length > 1 && fontUtil.fontDownloadCount[1] > 0;
    }

    public void createShouldDownloadFontCountArr(Context context) {
        fontDownloadCount = new int[]{0, getShouldDownloadFontCount(context)};
    }

    private int getShouldDownloadFontCount(Context context) {
        int count = 0;
        List<String[]> list = getFontInfoList();
        for (String[] fontInfo : list) {
            String fontLocalFileName = fontInfo[0];
            if (isExistFontFile(context, fontLocalFileName) == false) {
                count++;
            }
        }
        return count;
    }

    private boolean isExistFontFile(Context context, String fileName) {
        String path = FONT_FILE_PATH(context) + fileName;
        boolean isExist = false;
        try {
            File file = new File(path);
            isExist = file.isFile();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return isExist;
    }
}
