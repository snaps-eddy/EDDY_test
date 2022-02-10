package com.snaps.mobile.utils.sns;

/**
 * Created by ysjeong on 2017. 2. 27..
 */

/**
 * SNS 북 제목 길이 제한
 */
public class SNSBookTitleLengthCalculatorFactory {
    public static SNSBookTitleLengthCalculatorBase createSNSBookTitleLengthCalculator(String fontFace) {
        if (fontFace == null) return null;

        if (fontFace.equalsIgnoreCase("YGO330")) {
            return new SNSBookTitleLengthCalculatorForYGO330();
        } else if (fontFace.equalsIgnoreCase("YMjO130")) {
            return new SNSBookTitleLengthCalculatorForYMJO130();
        }

        return null;
    }
}
