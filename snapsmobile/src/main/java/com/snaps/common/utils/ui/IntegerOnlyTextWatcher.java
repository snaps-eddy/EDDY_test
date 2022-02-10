package com.snaps.common.utils.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.snaps.common.utils.log.Dlog;

/**
 * Created by songhw on 2017. 3. 9..
 *
 */

public class IntegerOnlyTextWatcher implements TextWatcher {
    private static final String TAG = IntegerOnlyTextWatcher.class.getSimpleName();

    private EditText editText;
    private TextView textView;

    private String newStr;

    private int maxLength;

    public IntegerOnlyTextWatcher( EditText editText, int maxLength ) {
        this.editText = editText;
        this.maxLength = maxLength;
    }

    public IntegerOnlyTextWatcher( TextView textView, int maxLength ) {
        this.textView = textView;
        this.maxLength = maxLength;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        newStr = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if( s.length() < 1 ) {
            newStr = "";
            return;
        }

        if( s.length() > maxLength )
            return;

        try {
            Integer.parseInt( s.toString() );
            newStr = s.toString();
        } catch (NumberFormatException e ) {
            Dlog.e(TAG, e);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if( s.toString().equals(newStr) ) return;

        if( editText != null ) {
            editText.setText(newStr);
            editText.setSelection(editText.length());
        }
        else if( textView != null )
            textView.setText( newStr );
    }
}
