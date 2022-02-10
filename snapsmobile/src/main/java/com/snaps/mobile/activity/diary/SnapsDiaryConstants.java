package com.snaps.mobile.activity.diary;

import com.snaps.mobile.R;

/**
 * Created by ysjeong on 16. 3. 7..
 */
public abstract class SnapsDiaryConstants {

    public static boolean IS_SUPPORT_IOS_VERSION = false; //아이폰에서 올린 글 호환 여부

    public static boolean IS_QA_VERSION = false; //************** 릴리즈 할때 반드시 false!!!!!! ***********************/
    public static final boolean DESIGN_TEST_FUNCTION = false;  //************** 릴리즈 할때 반드시 false!!!!!! ***********************/

     public enum eMissionState {
        UNKNOWN,
        PREV,
        ING,
        FAILED,
        SUCCESS
    }

    public enum eWeather {
        NONE(""),
        SUNSHINE("359001"), //맑음
        CLOUDY("359002"), //구름
        WIND("359003"), //바람
        RAINY("359004"), //비
        SNOWY("359005"), //눈
        DUST_STORM("359006"), //황사
        LIGHTNING("359007"), //천둥
        FOG("359008"); //안개

        String str = "";

        eWeather(String str) {
            this.str = str;
        }

        public String getCode() {
            return str;
        }

        public static eWeather converterStrToEnum(String code) {
            if(code == null) return NONE;
            else if(code.equals("359001")) return SUNSHINE;
            else if(code.equals("359002")) return CLOUDY;
            else if(code.equals("359003")) return WIND;
            else if(code.equals("359004")) return RAINY;
            else if(code.equals("359005")) return SNOWY;
            else if(code.equals("359006")) return DUST_STORM;
            else if(code.equals("359007")) return LIGHTNING;
            else if(code.equals("359008")) return FOG;
            return NONE;
        }

        public int getTextResId() {
            switch (this) {
                case SUNSHINE : return R.string.diary_weather_text_sunshine;
                case CLOUDY : return R.string.diary_weather_text_cloudy;
                case WIND : return R.string.diary_weather_text_windy;
                case RAINY : return R.string.diary_weather_text_rainy;
                case SNOWY : return R.string.diary_weather_text_snowy;
                case DUST_STORM : return R.string.diary_weather_text_dust_storm;
                case LIGHTNING : return R.string.diary_weather_text_lightning;
                case FOG: return R.string.diary_weather_text_fog;
            }
            return 0;
        }

        public int getIconResId(boolean focus) {
            switch (this) {
                case SUNSHINE : return focus ? R.drawable.img_diary_weather_sunshine_focus : R.drawable.img_diary_weather_sunshine;
                case CLOUDY : return focus ? R.drawable.img_diary_weather_cloudy_focus : R.drawable.img_diary_weather_cloudy;
                case WIND : return focus ? R.drawable.img_diary_weather_wind_focus : R.drawable.img_diary_weather_wind;
                case RAINY : return focus ? R.drawable.img_diary_weather_rainy_focus : R.drawable.img_diary_weather_rainy;
                case SNOWY : return focus ? R.drawable.img_diary_weather_snowy_focus : R.drawable.img_diary_weather_snowy;
                case DUST_STORM : return focus ? R.drawable.img_diary_weather_dust_storm_focus : R.drawable.img_diary_weather_dust_storm;
                case LIGHTNING : return focus ? R.drawable.img_diary_weather_lightning_focus : R.drawable.img_diary_weather_lightning;
                case FOG: return focus ? R.drawable.img_diary_weather_fog_focus : R.drawable.img_diary_weather_fog;
            }
            return 0;
        }
    }

    public enum eFeeling {
        NONE(""),
        HAPPY("360001"), //행복
        NO_FEELING("360002"), //보통
        FUNNY("360003"), //즐거움
        THANKS("360004"), //감사
        MISFORTUNE("360005"), //불행
        SAD("360006"), //슬픔
        ANGRY("360007"), //화남
        TIRED("360008"); //피곤

        String str = "";

        eFeeling(String str) {
            this.str = str;
        }

        public String getCode() {
            return str;
        }

        public static eFeeling converterStrToEnum(String code) {
            if(code == null) return NONE;
            else if(code.equals("360001")) return HAPPY;
            else if(code.equals("360002")) return NO_FEELING;
            else if(code.equals("360003")) return FUNNY;
            else if(code.equals("360004")) return THANKS;
            else if(code.equals("360005")) return MISFORTUNE;
            else if(code.equals("360006")) return SAD;
            else if(code.equals("360007")) return ANGRY;
            else if(code.equals("360008")) return TIRED;
            return NONE;
        }

        public int getTextResId() {
            switch (this) {
                case HAPPY : return R.string.diary_feels_text_happy;
                case NO_FEELING : return R.string.diary_feels_text_normal;
                case FUNNY : return R.string.diary_feels_text_funny;
                case THANKS : return R.string.diary_feels_text_thanks;
                case MISFORTUNE : return R.string.diary_feels_text_misfortune;
                case SAD : return R.string.diary_feels_text_sad;
                case ANGRY : return R.string.diary_feels_text_angry;
                case TIRED : return R.string.diary_feels_text_tired;
            }
            return -1;
        }

        public int getIconResId(boolean focus) {
            switch (this) {
                case HAPPY : return focus ? R.drawable.img_diary_feels_happy_focus: R.drawable.img_diary_feels_happy;
                case NO_FEELING : return focus ? R.drawable.img_diary_feels_normal_focus : R.drawable.img_diary_feels_normal;
                case FUNNY : return focus ? R.drawable.img_diary_feels_funny_focus : R.drawable.img_diary_feels_funny;
                case THANKS : return focus ? R.drawable.img_diary_feels_thanks_focus : R.drawable.img_diary_feels_thanks;
                case MISFORTUNE : return focus ? R.drawable.img_diary_feels_misfortune_focus : R.drawable.img_diary_feels_misfortune;
                case SAD : return focus ? R.drawable.img_diary_feels_sad_focus: R.drawable.img_diary_feels_sad;
                case ANGRY : return focus ? R.drawable.img_diary_feels_angry_focus : R.drawable.img_diary_feels_angry;
                case TIRED : return focus ? R.drawable.img_diary_feels_tired_focus : R.drawable.img_diary_feels_tired;
            }
            return -1;
        }
    }

    public static boolean isOSTypeEqualsAndroid(String osType) {
        return osType == null || osType.equalsIgnoreCase(SnapsDiaryConstants.CODE_OS_TYPE_ANDROID) || osType.equalsIgnoreCase("null");
    }

    public static final int MIN_DIARY_PAGE_COUNT_FOR_PUBLISH = 20;

    public static final int INVALID_INK_CNT = 9999;

    public static final int REQUEST_CODE_SELECT_PHOTOS = 10;
    public static final int REQUEST_CODE_SELECT_ONE_PHOTO = 11;
    public static final int REQUEST_CODE_EDIT_PHOTO = 12;
    public static final int REQUEST_CODE_DIARY_UPDATE = 100;

    public static final int RESULT_CODE_DIARY_DELETED = 9101;
    public static final int RESULT_CODE_DIARY_UPDATED = 9102;

    public static final String EXTRAS_BOOLEAN_EDITED_DATE = "EXTRAS_BOOLEAN_EDITED_DATE";

    public static  final byte EDIT_MODE_NEW_WRITE = 0;
    public static  final byte EDIT_MODE_DETAIL_VIEW = 1;
    public static  final byte EDIT_MODE_MODIFY = 2;

    public static final String INTERFACE_CODE_MISSION_STATE_ING = "362001";
    public static final String INTERFACE_CODE_MISSION_STATE_SUCCESS = "362002";
    public static final String INTERFACE_CODE_MISSION_STATE_FAILED = "362003";
    public static final String INTERFACE_CODE_MISSION_STATE_RETRY = "362004";

    public static final String ERR_CODE_FAIL_SHORT_INK = "10"; //잉크 부족 실패 코드
    public static final String ERR_CODE_PASSED_EXPIRATION = "11"; //기한 만료

    public static final String CODE_OS_TYPE_IOS = "190001";
    public static final String CODE_OS_TYPE_ANDROID = "190002";

    public static final String DIARY_BOOK_F_CLSS_CODE = "KOR0031999999999";
}
