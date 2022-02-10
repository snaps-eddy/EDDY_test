package com.snaps.common.utils.ui;

import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;

import com.snaps.common.R;
import com.snaps.common.data.img.BSize;
import com.snaps.common.utils.constant.Config;
import com.snaps.common.utils.constant.Const_PRODUCT;
import com.snaps.common.utils.constant.Const_VALUE;
import com.snaps.common.utils.log.Dlog;
import com.snaps.common.utils.pref.Setting;
import com.vdurmont.emoji.EmojiParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    private static final String TAG = StringUtil.class.getSimpleName();

    public static StringBuilder stringData;

    public static boolean isOnlyWordStr(String str) {
        if (isEmpty(str)) return false;

        for (int ii = 0; ii < str.length(); ii++) {
            char c = str.charAt(ii);
            if (!isGeneralWordChar(c)) return false;
        }
        return true;
    }

    //영어, 한글, 숫자, /, ., _만 true
    private static boolean isGeneralWordChar(char c) {
        return (0xAC00 <= c && c <= 0xD7A3) || (0x3131 <= c && c <= 0x318E) || (0x41 <= c && c <= 0x5A) || (0x61 <= c && c <= 0x7A) || (0x30 <= c && c <= 0x39) || (c == 45) || (c == 46) || (c == 47) || (c == 95);
    }

    public static String getSafeStrIfNotValidReturnSubStr(String str, String subStr) {
        return StringUtil.isEmpty(str) || str.equalsIgnoreCase("null") ? subStr : str;
    }

    public static BSize getBSizeFromArrayStr(String rcStr) throws Exception {
        String[] rc = rcStr.replace(" ", "|").split("\\|");
        return new BSize(Float.parseFloat(rc[2]), Float.parseFloat(rc[3]));
    }

    public static String getAppsFlyerCurrencyCode(Context context) {
        String country = "";
        String currentLang = Setting.getString(context, Const_VALUE.KEY_APPLIED_LANGUAGE);

        switch (currentLang) {
            case "ko":      //한국
                country = "KRW";
                break;
            case "en":     //미국
                country = "USD";
                break;
            case "zh":    //중국
                country = "CNY";
                break;
            case "ja":    //일본
                country = "JPY";
                break;
            default:
                country = "KRW";
                break;
        }
        return country;
    }

    private static boolean isXMLText(String text) {
        return !isEmpty(text) && text.startsWith("<") && text.endsWith(">") && text.contains("xml");
    }

    public static String convertEmojiAliasToUniCode(String aliasStr) {
        if (aliasStr == null || StringUtil.isEmpty(aliasStr)) return "";
        try {
            //만약, 특수문자나 기호 없이 문자열로만 구성되어 있다면, EmojiParser 를 태우지 않는다. 메모리 문제가 있는 듯 하다.
            if (StringUtil.isOnlyWordStr(aliasStr) || isXMLText(aliasStr) || StringUtil.isEmptyAfterTrim(aliasStr))
                return aliasStr;

            String result = EmojiParser.parseToUnicode(aliasStr);
            if (!aliasStr.equalsIgnoreCase(result)) {
                Dlog.d("#### convertEmojiAliasToUniCode : " + aliasStr + " -> " + result);
            }
            return result;
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
        }
        return aliasStr;
    }

    public static String removeInvalidXMLChar(String text) {
        if (isEmpty(text)) return text;

        StringBuilder builder = new StringBuilder();
        for (int ii = 0; ii < text.length(); ii++) {
            char c = text.charAt(ii);
            if (isValidXmlChar(c, text, ii)) {
                builder.append(c);
            }
        }

        return isEmpty(builder.toString()) ? "invalidText" : builder.toString();
    }

    private static boolean isValidXmlChar(char c, String allText, int charIndex) {
        if (isEmpty(allText)) return false;
        return (c >= 0x20 && c <= 0xd7ff) || (c >= 0xe000 && c <= 0xfffd)
                || (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 && (Character.isHighSurrogate(c) && charIndex < allText.length() - 1));
    }

    public static String convertEmojiUniCodeToAlias(String uniCodeStr) {
        if (uniCodeStr == null || StringUtil.isEmpty(uniCodeStr)) return "";
        try {
            //만약, 특수문자나 기호 없이 문자열로만 구성되어 있다면, EmojiParser 를 태우지 않는다. 메모리 문제가 있는 듯 하다.
            if (StringUtil.isOnlyWordStr(uniCodeStr) || isXMLText(uniCodeStr) || StringUtil.isEmptyAfterTrim(uniCodeStr))
                return uniCodeStr;

            String result = EmojiParser.parseToAliases(uniCodeStr);
            if (!uniCodeStr.equalsIgnoreCase(result)) {
                Dlog.d("#### convertEmojiUniCodeToAlias : " + uniCodeStr + " -> " + result);
            }
            return result;
        } catch (OutOfMemoryError e) {
            Dlog.e(TAG, e);
        }
        return uniCodeStr;
    }

    public static String removeQuestionChar(String str) {
        if (isEmpty(str)) return str;
        return str.replace("?", "Q");
    }

    public static String getCurrencyStr(Context context, String value) {
        return getCurrencyStr(context, value, false);
    }

    public static String getCurrencyStr(Context context, String value, boolean minus) {
        double iValue = 0; // 글로벌 진행중 다른 언어의 가격값에 소숫점이 들어가서 변경.
        if (value != null && !"".equals(value)) {
            try {
                iValue = Double.valueOf(value.replaceAll(",", ""));
                if (minus)
                    iValue = -iValue;
            } catch (Exception e) {
                Dlog.e(TAG, e);
            }
        }
        return getCurrencyStr(context, iValue, minus);
    }

    public static String getCurrencyStr(Context context, double value) {
        return getCurrencyStr(context, value, false);
    }

    public static String getCurrencyStr(Context context, double value, boolean minus) {
        if (minus)
            value = -value;
        /*
         * if (Config.CHANNEL_CODE.equals(Config.CHANNEL_SNAPS_JPN)) return new DecimalFormat("###,###").format(value) + context.getString(R.string.comm_currency_ja); else // for kakao, kr버전 return
         * new DecimalFormat("###,###").format(value) + context.getString(R.string.comm_currency_kr);
         */

        // <string name="comm_currency_kr">원</string>
        // <string name="comm_currency_ja">円</string>

//		if (Config.getCHANNEL_CODE() != null && Config.getCHANNEL_CODE().equals(Config.CHANNEL_SNAPS_JPN))
//			return new DecimalFormat("###,###").format(value) + "円";
//		else
//			// for kakao, kr버전
//			return new DecimalFormat("###,###").format(value) + "원";

        if (Config.useKorean()) {
            return new DecimalFormat("###,###").format(value) + "원";
        } else if (Config.useJapanese()) {
            return new DecimalFormat("###,###.#######").format(value) + context.getString(R.string.currency);
        }

        return "$" + new DecimalFormat("###,###.######").format(value);
    }

    public static String getCurrency(Context context, double value) {
        return getCurrency(context, value, false);
    }

    public static String getCurrency(Context context, double value, boolean minus) {
        if (minus)
            value = -value;
        if (Config.getCHANNEL_CODE() != null && Config.getCHANNEL_CODE().equals(Config.CHANNEL_SNAPS_JPN))
            return new DecimalFormat("###,###.######").format(value);
        else
            // for kakao, kr버전
            return new DecimalFormat("###,###.######").format(value);
    }

    public static String getGlobalCurrencyStr(Context context, double value, boolean minus) {
        String strValue = new DecimalFormat("###,###.######").format(value);

        if (minus) strValue = "-" + strValue;

        if ("en".equalsIgnoreCase(Locale.getDefault().getLanguage()) || "zh".equalsIgnoreCase(Locale.getDefault().getLanguage()))
            strValue = context.getString(R.string.currency) + strValue;
        else strValue = strValue + context.getString(R.string.currency);

        return strValue;
    }

    public static String getDateFormat(Context context, String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyyMMdd").parse(dateStr));
        } catch (ParseException e) {
            Dlog.e(TAG, e);
        }
        return dateStr;
    }

    //사진 찍은 날짜가 이상하다면 오늘 날짜로 수정한다.
    public static long fixValidTakenTime(long time) {
        try {
            Calendar photoCalendar = Calendar.getInstance();
            photoCalendar.setTimeInMillis(time);

            Calendar currentCalendar = Calendar.getInstance();

            if (photoCalendar.get(Calendar.YEAR) <= 1970 || (photoCalendar.get(Calendar.YEAR) > currentCalendar.get(Calendar.YEAR))) { //사진에 기록된 날짜가 현재보다 미래일수 없다
                time *= 1000; //sns 사진 중 sec으로 기록 되어 있는 날짜가 있다.
                photoCalendar.setTimeInMillis(time);

                if (photoCalendar.get(Calendar.YEAR) <= 1970 || (photoCalendar.get(Calendar.YEAR) > currentCalendar.get(Calendar.YEAR))) {
                    return 0;
                } else {
                    return photoCalendar.getTimeInMillis();
                }
            }

            return time;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return 0;
    }

    public static String convertTimeLongToStr(long time, String format) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
            return simpleDateFormat.format(new Date(time));
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return "";
    }

    public static String getDateFormatKakao(long lDate) {
        try {
            Date date = new Date(lDate * 1000l);
            return new SimpleDateFormat("yyyy/M/d a h:mm").format(date);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return "";
    }

    public static String getDateFormatKakao2(long lDate) {

        String date = Long.toString(lDate);

        SimpleDateFormat originformat = new SimpleDateFormat("yyyyMMddhhmmss");
        // SimpleDateFormat newformat = new
        // SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat newformat = new SimpleDateFormat("yyyy/M/d a h:mm");

        try {

            Date originDate = originformat.parse(date);
            // GMT => KST 변경...
            originDate = covertUTC2KST(originDate);

            String new_date = newformat.format(originDate);

            return new_date;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return "";
    }

    /***
     * yyyyMMddhhmmss => yyyyMMdd
     *
     * @param lDate
     * @return
     */
    public static String getDateFormatKakao3(long lDate) {

        String date = Long.toString(lDate);
        SimpleDateFormat originformat = new SimpleDateFormat("yyyyMMddhhmmss");
        SimpleDateFormat newformat = new SimpleDateFormat("yyyyMMdd");

        try {

            Date originDate = originformat.parse(date);
            // GMT => KST 변경...
            originDate = covertUTC2KST(originDate);

            String new_date = newformat.format(originDate);

            return new_date;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return "";
    }

    /***
     * 2014-09-01T01:29:17Z => ???? 입력된 포멧으로 이미지를 리턴하는 함수...
     *
     * @param d
     * @param dateFormat
     * @return
     */
    public static String getDateStringByFormat(String d, String dateFormat) {
        return getDateStringByFormat(d, dateFormat, Locale.getDefault());
    }

    public static Calendar getCalendarWidthString(String d) {
        String date = getOnlyNumberString(d);// Long.toString(convertCreateStringToLong(d));
        SimpleDateFormat originformat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());

        try {

            Date originDate = originformat.parse(date);
            // GMT => KST 변경...
            originDate = covertUTC2KST(originDate);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(originDate);
            return calendar;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    public static Calendar convertDateStrToCalendar(String date) {
        if (date == null) return null;
        String[] arrDate = date.split("\\.");
        //yyyy-mm-dd
        if (arrDate == null || arrDate.length < 2) return null;

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.set(Integer.parseInt(arrDate[0]), Integer.parseInt(arrDate[1]) - 1, Integer.parseInt(arrDate[2]));
        } catch (NumberFormatException e) {
            Dlog.e(TAG, e);
            return null;
        }
        return calendar;
    }

    public static String getDateStringByFormat(String d, String dateFormat, Locale locale) {
        String date = Long.toString(convertCreateStringToLong(d));
        SimpleDateFormat originformat = new SimpleDateFormat("yyyyMMddhhmmss", locale);
        SimpleDateFormat newformat = new SimpleDateFormat(dateFormat, locale);

        try {

            Date originDate = originformat.parse(date);
            // GMT => KST 변경...
            originDate = covertUTC2KST(originDate);

            String new_date = newformat.format(originDate);

            return new_date;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return "";
    }

    public static String getDateStringByFormat(long lDate, String dateFormat) {

        String date = Long.toString(lDate);
        SimpleDateFormat originformat = new SimpleDateFormat("yyyyMMddhhmmss");
        SimpleDateFormat newformat = new SimpleDateFormat(dateFormat);

        try {

            Date originDate = originformat.parse(date);
            // GMT => KST 변경...
            originDate = covertUTC2KST(originDate);

            String new_date = newformat.format(originDate);

            return new_date;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return "";
    }

    public static long convertCreateStringToLong(String createAtStr) {
        try {

            // Log.d("zzzzzz", "time = " + createAtStr);// 2014-12-18T02:14:21Z

            // String stepleftString1 = createAtStr.replace("-", "");
            //
            // String stepleftString2 = stepleftString1.replace(":", "");
            //
            // String stepleftString3 = stepleftString2.replace("T", "");
            //
            // String stepleftString4 = stepleftString3.replace("Z", "");

            String dateString = getOnlyNumberString(createAtStr);
            return Long.valueOf(dateString);

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return -1;
    }

    public static Date covertUTC2KST(Date dateUTC) {
        long longUTC = dateUTC.getTime();
        TimeZone zone = TimeZone.getDefault();
        int offset = zone.getOffset(longUTC);

        long longLocal = longUTC + offset;
        Date dateLocal = new Date();
        dateLocal.setTime(longLocal);

        return dateLocal;
    }

    public static String getDateFormatYMD(Context context, String dateStr) {
        /*
         * <string name="comm_dateformat">yyyy년 M월 d일</string> <string name="comm_dateToformat">M월 d일 E요일 a h:mm</string> <string name="comm_dateTo2format">M월 d일 a h:mm</string>
         */

        try {
            if (Config.useKorean()) {
                return new SimpleDateFormat("yyyy년 M월 d일").format(new SimpleDateFormat("yyyyMMdd").parse(dateStr));
            }
            return new SimpleDateFormat("yyyy.M.d").format(new SimpleDateFormat("yyyyMMdd").parse(dateStr));
        } catch (ParseException e) {
            Dlog.e(TAG, e);
        }
        return dateStr;
    }

    /**
     * 날짜 Format 변경.
     *
     * @param date
     * @return
     */
    public static String getDateReplace(Context context, Date date) {
        SimpleDateFormat.getInstance().format(date);
        /*
         * <string name="comm_dateformat">yyyy년 M월 d일</string> <string name="comm_dateToformat">M월 d일 E요일 a h:mm</string> <string name="comm_dateTo2format">M월 d일 a h:mm</string>
         */
        String dateFormat = "yyyy년 M월 d일";
        if (!Config.useKorean()) {
            dateFormat = "yyyy.M.d";
        }

        SimpleDateFormat toFormat = new SimpleDateFormat(dateFormat);
        return toFormat.format(date);
    }

    // new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+SSSS")
    public static long getFBDatetoLong(Context context, String fbDate) {
        String fromFormat = "yyyy-MM-dd'T'HH:mm:ss+SSSS";
        // String toFormat = "M월 d일 E요일 a h:mm";
        // if (Config.CHANNEL_CODE.equals(Config.CHANNEL_SNAPS_JPN))
        // toFormat = "M月 d日 E요일 a h:mm";

        try {
            Date fromDate = new SimpleDateFormat(fromFormat).parse(fbDate);
            return fromDate.getTime();
        } catch (ParseException e) {
            Dlog.e(TAG, e);
        }
        return 0l;
    }

    public static String getFBDatetoFormat(Context context, String fbDate) {
        String fromFormat = "yyyy-MM-dd'T'HH:mm:ss+SSSS";
        // String toFormat = "M월 d일 E요일 a h:mm";
        // if (Config.CHANNEL_CODE.equals(Config.CHANNEL_SNAPS_JPN))
        // toFormat = "M月 d日 E요일 a h:mm";

        try {
            Date fromDate = new SimpleDateFormat(fromFormat).parse(fbDate);
            return new SimpleDateFormat("M월 d일 E요일 a h:mm").format(fromDate);
        } catch (ParseException e) {
            Dlog.e(TAG, e);
        }
        return fbDate;
    }

    public static String getFBDatetoFormatExtraWeek(Context context, String fbDate) {
        String fromFormat = "yyyy-MM-dd'T'HH:mm:ss+SSSS";
        // String toFormat = "M월 d일 a h:mm";
        // if (Config.CHANNEL_CODE.equals(Config.CHANNEL_SNAPS_JPN))
        // toFormat = "M月 d日 a h:mm";

        try {
            Date fromDate = new SimpleDateFormat(fromFormat).parse(fbDate);
            return new SimpleDateFormat("M월 d일 a h:mm").format(fromDate);
        } catch (ParseException e) {
            Dlog.e(TAG, e);
        }
        return fbDate;
    }

    /**
     * 이메일 체크
     *
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email) {
        // "[^A-Za-z0-9\\.\\@_\\-~#]+"
        // "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$"

        Pattern p = Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isContainsUppercaseOrEmptyText(String str) {
        if (str == null || str.length() < 1) return false;
        Pattern p = Pattern.compile(".*[A-Z ].*");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isContainsEmptyText(String str) {
        if (str == null || str.length() < 1) return false;
        Pattern p = Pattern.compile(".*[ ].*");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 패스워드 체크(최소~최대 글자수, 영문,숫자만 포함)
     *
     * @param min
     * @param max
     * @param pwd
     * @return
     */
    public static boolean isValidPwd(int min, int max, String pwd) {
        if (pwd.length() < min || pwd.length() > max)
            return false;
        return true;
    }

    public static boolean isContainLanguageChar(String str) {
        if (isEmpty(str)) return false;

        for (int ii = 0; ii < str.length(); ii++) {
            char c = str.charAt(ii);
            if (isLanguageChar(c)) return true;
        }
        return false;
    }

    private static boolean isLanguageChar(char c) {
        if ((0xAC00 <= c && c <= 0xD7A3) || (0x3131 <= c && c <= 0x318E)) { //한
            return true;
        } else if ((0x3040 <= c && c <= 0x309F) || (0x30A0 <= c && c <= 0x30FF)) {//일
            return true;
        } else if ((0x4E00 <= c && c <= 0x9FFF) || (0x2E00 <= c && c <= 0x2E7F) || (0x3200 <= c && c <= 0x32FF) || (0x3400 <= c && c <= 0x4DBF) || (0x20000 <= c && c <= 0x2A6DF) || (0x2F800 <= c && c <= 0x2FA1F)) {//한자..
            return true;
        } else if (c == ' ') {
            return true;
        }
        return false;
    }

    public static String removeLines(String src, int maxLine) {
        try {
            if (src == null || "".equals(src))
                return src;
            String[] lines = src.split("\n");
            if (lines.length <= maxLine)
                return src;

            String result = "";
            for (int i = 0; i < lines.length; i++) {
                result += lines[i];
                if (i == maxLine - 1)
                    break;
                result += "\n";
            }
            return result;
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return src;
    }

    /**
     * String -> Integer
     *
     * @param value
     * @return
     */
    public static int sToi(String value) {
        int iValue = 0;
        try {
            iValue = Integer.valueOf(value);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return iValue;
    }

    public static String getCurrentDate() {
        String fromFormat = "yyyy-MM-dd HH:mm:ss";

        Date currentDate = new Date();
        return new SimpleDateFormat(fromFormat).format(currentDate);
    }

    public static String convertLongTimeToSmartAnalysisFormat(long time) {
        String fromFormat = "yyyy-MM-dd HH:mm:ss";
        try {
            Date date = new Date(time);
            return new SimpleDateFormat(fromFormat).format(date);
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        return "";
    }

    /***
     * 문자열에서 숫자만 반환하는 함수...
     *
     * @param str
     * @return
     */
    public static String getOnlyNumberString(String str) {
        if (str == null)
            return str;

        StringBuffer sb = new StringBuffer();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char curChar = str.charAt(i);
            if (Character.isDigit(curChar))
                sb.append(curChar);
        }
        return sb.toString();
    }

    /***
     * url 인코딩하는 함수..
     *
     * @param content
     * @param charsetName
     * @return
     */
    public static String getURLDecode(String content, String charsetName) {
        try {
            // "euc-kr"
            // return URLDecoder.decode(content, "utf-8"); // UTF-8
            return URLDecoder.decode(content, charsetName).trim(); // EUC-KR
        } catch (UnsupportedEncodingException e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    public static String getURLEncode(String content, String charsetName) {
        try {
            return URLEncoder.encode(content, charsetName).trim();
        } catch (UnsupportedEncodingException e) {
            Dlog.e(TAG, e);
        }
        return null;
    }

    public static boolean isContainsEmoji(String str) {
        if (str == null || str.length() < 1) return false;
        String filterString = getFilterString(str);
        return filterString != null && !str.equals(filterString);
    }

    public static String trimOnlySuffix(String str) {
        while (!isEmpty(str) && str.endsWith("\n")) {
            str = str.substring(0, str.lastIndexOf("\n"));
        }

        return str;
    }

    /**
     * 이모티콘등 제거하는 함수..
     *
     * @param plainText
     * @return
     */
    public static String getFilterString(String plainText) {
        // [^a-zA-Z0-9!@#$%^&*()\\_+=~`;:'"?.,|/<>\{\[\}\]\s-]/ig
        // String fillter =
        // plainText.replaceAll("[^a-zA-Z0-9!@#[$]%^&[*]\\(\\)\\_[+]=~`;:'\"[?].,[|]/<>\\s[-]\\[\\]\\{\\}]",
        // "");
        // String fillter =
        // plainText.replaceAll("[^a-zA-Z0-9ㄱ-ㅣ가-힣~!@#$%^&*()[?]\\s]", "");

        // String fillter =
        // plainText.replaceAll("[\\u1F60-\\u1F64]|[\\u2702-\\u27B0]|[\\u1F68-\\u1F6C]|[\\u1F30-\\u1F70]|[\\u2600-\\u26ff]",
        // "");

        // String fillter = plainText.replaceAll("[^\\x00-\\x7Fㄱ-ㅣ가-힣]", "");
        // String fillter =
        // plainText.replaceAll("[\ud83d\ude01-\ud83d\ude4f]|[\u263a-\ufe0f]",
        // "");
        // BOM 추가 U+FEFF
        String fillter = plainText.replaceAll("[\u2600-\u26ff\ud83d\ude00-\ud83d\ude4f\uefff\u318D\u119E\u11A2]", "");

        return getFilterString2(fillter);
    }

    public static String getFilterString2(String text) {
        if (text == null || text.length() < 1)
            return text;

        StringBuffer sbResult = new StringBuffer();
        boolean isValidChar = false;
        for (int ii = 0; ii < text.length(); ii++) {
            char c = text.charAt(ii);

            isValidChar = false;

            if (c == ' ' || c == '\n')
                isValidChar = true;

            // eng
            if ((c >= 65 && c <= 90) || (c >= 97 && c <= 122))
                isValidChar = true;

            // han
            if (Character.getType(c) == 5)
                isValidChar = true;

            // number
            if (c >= 48 && c <= 57)
                isValidChar = true;

            // etc
            if ((c >= 33 && c <= 47) || (c >= 58 && c <= 64) || (c >= 91 && c <= 96) || (c >= 123 && c <= 126))
                isValidChar = true;

            if (isValidChar)
                sbResult.append(c);
        }

        return sbResult.toString();
    }

    /***
     * url에서 타이틀을 가져오는 함수...
     *
     * @param url
     * @param key
     * @return
     */
    static public String getTitleAtUrl(String url, String key) {
        return getTitleAtUrl(url, key, false);
    }

    static public String getTitleAtUrl(String url, String key, boolean isFilterOnlyNumber) {
        if (url == null || key == null) return null;
        // http://117.52.102.177:28443/mw/store/product/search.jsp?F_MCLSS_CODE=001008001002#stackNum=0&naviBarTitle=디자인
        // 상세보기

        // var Regexpexec = new
        // RegExp("("+keyName+"=)(.*?)(?=&|$)").exec(value);
        try {

            String regexp = String.format("(%s=)(.*?)(?=&|#|$)", key);
            Pattern p = Pattern.compile(regexp);
            Matcher m = p.matcher(url);

            String ret = "";

            while (m.find()) {
                ret = m.group(2);
            }

            if (isFilterOnlyNumber) { //간혹 width=111?video...이런 식으로 들어오는 경우가 있다.
                return getFilterOnlyNumberString(URLDecoder.decode(ret, "utf-8"));
            } else
                return URLDecoder.decode(ret, "utf-8");
            // return ret;

        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
        //
        // return ret;

        return null;
    }

    public static String getFilterOnlyNumberString(String str) {
        if (str == null) return null;

        StringBuilder result = new StringBuilder();
        for (int ii = 0; ii < str.length(); ii++) {
            char c = str.charAt(ii);
            if (c < '0' || c > '9') {
                break;
            }
            result.append(c);
        }

        return result.toString();
    }

    /***
     * 이미지 url를 ImageData로 변경하는 함수..
     *
     * @param plainText
     * @return
     */
    // static public ImageData covertUrl(String url) {
    // String imgUrl = url;
    // String imgWidth = StringUtil.getTitleAtUrl(imgUrl, "width");
    // String imgHeight = StringUtil.getTitleAtUrl(imgUrl, "height");
    //
    // return new ImageData(imgUrl, imgWidth, imgHeight);
    // }
    public static String CleanInvalidXmlChars(String plainText) {
        if (plainText == null) return "";

        final String SPLITER = "►";
        String[] temp = plainText.split(SPLITER);
        StringBuilder sb = new StringBuilder();
        if (temp.length > 0) {
            for (int i = 0; i < temp.length; ++i) {
                sb.append(doClearChars(temp[i]));
                if (i < temp.length - 1) sb.append(SPLITER);
            }
        }

        return sb.toString();
    }

    public static String doClearChars(String plainText) {
        if (plainText == null)
            return "";


        // 그냥 꼼수다.

        final String EMOJI_RANGE_REGEX = "[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|[\u2600-\u26FF]|[\u2700-\u27BF]";
        final Pattern PATTERN = Pattern.compile(EMOJI_RANGE_REGEX);

        Matcher matcher = PATTERN.matcher(plainText);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);
        plainText = sb.toString();

        /**
         * Finds and removes emojies from @param input
         *
         * @param input the input string potentially containing emojis (comes as unicode stringfied)
         * @return input string with emojis replaced
         */

        // String REX2 = "\\W";
        String REX = "\\p{So}+";//
        final String xml10pattern = "[^" + "\u0009\r\n" + "\u0020-\uD7FF" + "\uE000-\uFFFD" + "\ud800\udc00-\udbff\udfff" + "]";

        final String xml11pattern = "[^" + "\u0001-\uD7FF" + "\uE000-\uFFFD" + "\ud800\udc00-\udbff\udfff" + "]+";

        String fillter = plainText.replaceAll(REX, "");

        fillter = fillter.replaceAll("\ufe0f", "");
        fillter = fillter.replace("\u2028", "\n"); //line seperator
        fillter = fillter.replaceAll(xml10pattern, "");
        fillter = fillter.replaceAll(xml11pattern, "");
        fillter = fillter.replace("(Sticker)", "");
        fillter = fillter.replace("(Image)", "");
        fillter = fillter.replaceAll("[\ud800\udc00-\udbff\udfff]", "");

        if (!Const_PRODUCT.isFacebookPhotobook() && !Const_PRODUCT.isInstagramBook())
            fillter.trim(); // 페북포토북 예외처리.
        return fillter;

        // String result = plainText.replaceAll(xml10pattern, "");
        //
        // return result;
    }

    public static int breakMearsureText(String text, int maxWidth, Paint paint) {
        String fullText = text;
        int width = 0;
        boolean isAgain = false;
        do {
            width = (int) paint.measureText(fullText);
            if (width > maxWidth) {
                fullText = fullText.substring(0, fullText.length() - 1);
                isAgain = true;
            } else
                isAgain = false;

        } while (isAgain);

        return fullText.length();
    }

    /***
     * url에 '?' 있는지 여부에 따라. 파라미터를 붙여주는 함수.
     *
     * @param url
     * @param param
     * @return
     */
    public static String addUrlParameter(String url, String param) {
        if (url.contains("?")) {
            url += "&";
            return url += param;
        } else {
            url += "?";
            return url += param;
        }
    }

    /**
     * null이나 "" 체크
     *
     * @param str
     * @return
     */

    public static boolean isEmpty(String str) {
        return str == null || str.length() < 1;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEmptyAfterTrim(String str) {
        return str == null || str.trim().length() < 1;
    }

    public static boolean isNotEmptyAfterTrim(String str) {
        return !isEmptyAfterTrim(str);
    }

    public static boolean isSafeString(String str) {
        return !isEmpty(str) && !str.equalsIgnoreCase("null");
    }

    public static int isCompareDate(String dst, String src) {
        return 0;
    }

    public static boolean isSnapsServerError(String result) {
        return isEmpty(result) || result.contains("개발팀으로 문의주세요");
    }

    /***
     * 버젼을 비교하는 함수
     *
     * @param appVersion
     * @param updateInfoVersion
     * @return
     */
    public static int compareVersion(String appVersion, String updateInfoVersion) {
        try {
            String appVer[] = appVersion.split("\\.");
            String updateInfoVer[] = updateInfoVersion.split("\\.");

            if (appVer.length != updateInfoVer.length)
                return 99;// 버젼비교 불가능

            if (appVersion.equals(updateInfoVersion))
                return 0; //앱 버젼이 같음..

            for (int i = 0; appVer.length > i; i++) {

                int aNumber = Integer.parseInt(appVer[i]);
                int uNumber = Integer.parseInt(updateInfoVer[i]);
                if (aNumber < uNumber)
                    return -1; // 앱 버젼이 작다..
                else if (aNumber > uNumber)
                    return 1;

            }
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }

        return 1; // 앱 버젼이 크다
    }

    public static String getStrFromJSONObj(JSONObject jsonObj, String tag) throws JSONException {
        if (jsonObj == null || tag == null || jsonObj.isNull(tag) || jsonObj.get(tag) == null)
            return "";
        return jsonObj.getString(tag);
    }

    public static String getFormattedDateString(String dateString, String format1, String format2, Locale locale) {
        String result = "";
        try {
            Calendar date = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(format1, Locale.getDefault());
            date.setTime(sdf.parse(dateString));
            sdf = new SimpleDateFormat(format2, locale);
            result = sdf.format(date.getTime());
        } catch (ParseException e) {
            Dlog.e(TAG, e);
        }
        return result;
    }

    public static String getFormattedDateString(String dateString, String format1, String format2) {
        return getFormattedDateString(dateString, format1, format2, Locale.getDefault());
    }

    public static String getRandomStringId(int length) {
        if (stringData == null || stringData.length() < 1) {
            stringData = new StringBuilder();
            for (char ch = '0'; ch < '9'; ++ch) stringData.append(ch);
            for (char ch = 'a'; ch < 'z'; ++ch) stringData.append(ch);
            for (char ch = 'A'; ch < 'Z'; ++ch) stringData.append(ch);
        }

        Random rnd = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; ++i)
            sb.append(stringData.charAt(rnd.nextInt(stringData.length())));

        return sb.toString();
    }

    //January가 0부터 시작하므로, 유의 해서 사용 할 것.
    public static String getMonthStr(int calendarMonth, boolean isAbbreviation) {
        String yearStr = "";
        switch (calendarMonth) {
            case Calendar.JANUARY:
                yearStr = isAbbreviation ? "Jan" : "January";
                break;
            case Calendar.FEBRUARY:
                yearStr = isAbbreviation ? "Feb" : "February";
                break;
            case Calendar.MARCH:
                yearStr = isAbbreviation ? "Mar" : "March";
                break;
            case Calendar.APRIL:
                yearStr = isAbbreviation ? "Apr" : "April";
                break;
            case Calendar.MAY:
                yearStr = isAbbreviation ? "May" : "May";
                break;
            case Calendar.JUNE:
                yearStr = isAbbreviation ? "Jun" : "June";
                break;
            case Calendar.JULY:
                yearStr = isAbbreviation ? "Jul" : "July";
                break;
            case Calendar.AUGUST:
                yearStr = isAbbreviation ? "Aug" : "August";
                break;
            case Calendar.SEPTEMBER:
                yearStr = isAbbreviation ? "Sep" : "September";
                break;
            case Calendar.OCTOBER:
                yearStr = isAbbreviation ? "Oct" : "October";
                break;
            case Calendar.NOVEMBER:
                yearStr = isAbbreviation ? "Nov" : "November";
                break;
            case Calendar.DECEMBER:
                yearStr = isAbbreviation ? "Dec" : "December";
                break;
        }
        return yearStr;
    }

    public static String getYearWithText(int year) {
        return String.format((Config.useKorean() ? "%d년" : "%d"), year);
    }

    public static String getMonthWithText(int month) {
        return Config.useKorean() ? String.format("%d월", month) : getMonthStr(month, false);
    }

    public static String getYearAndMonthWithText(int year, int month) {
        return Config.useKorean() ? String.format("%d년 %d월", year, month) : String.format("%s %d", getMonthStr(month, false), year);
    }

    public static String getMonthAndDayAndDayOfWeekWithText(int month, int day, String dayOfWeek) {
        return Config.useKorean() ? String.format("%d월 %d일 %s", month, day, dayOfWeek)
                : String.format("%s/%d/%d", dayOfWeek, day, month);
    }

    public static String getYearAndMonthAndDayAndDayOfWeekWithText(int year, int month, int day, String dayOfWeek) {
        return Config.useKorean() ? String.format("%d년 %d월 %d일 %s", year, month, day, dayOfWeek)
                : String.format("%s/%d/%d/%d", dayOfWeek, day, month, year);
    }

    public static String converLanguageCodeToCountryCode(String languageCode) {
        String toCode = languageCode;
        toCode = toCode.replace("ko", "kr");
        toCode = toCode.replace("ja", "jp");
        return toCode;
    }

    public static String convertCountrycodeToLanguageCode(String countryCode) {
        String toCode = countryCode;
        toCode = toCode.replace("kr", "ko");
        toCode = toCode.replace("jp", "ja");
        return toCode;
    }

    public static HashMap<String, String> parseUrl(String url) {
        HashMap<String, String> hashmap = new HashMap<String, String>();

        Uri uri = Uri.parse(url);

        if (uri == null)
            return hashmap;

        String scheme = uri.getScheme(); // ex) "snapsapp://"

        hashmap.put("scheme", scheme);

        String params = uri.getQuery();

        if (params == null)
            return hashmap;

        String[] arParams1 = params.split("&");

        for (String find : arParams1) {
            String[] jsonparam = find.split("=");

            if (jsonparam != null && jsonparam.length > 0) {
                if (jsonparam[0].equalsIgnoreCase("fullurl")) {
                    String value = find.substring(find.indexOf(jsonparam[0]) + jsonparam[0].length() + 1);
                    hashmap.put(jsonparam[0], value);
                    continue;
                }

                if (jsonparam.length == 2)
                    hashmap.put(jsonparam[0], jsonparam[1]);
                else if (jsonparam.length == 1)
                    hashmap.put(jsonparam[0], "");
            }
        }
        return hashmap;
    }

}
