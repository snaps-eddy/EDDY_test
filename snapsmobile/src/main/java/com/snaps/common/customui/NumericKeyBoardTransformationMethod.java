package com.snaps.common.customui;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

/**
 * Created by songhw on 2017. 3. 16..
 * 숫자전용 키보드 사용하기 위한 클래스 (패스워드 인풋의 *를 없앰)
 */

public class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return source;
    }
}
