package com.snaps.mobile.activity.detail.layouts;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.detail.interfaces.LayoutRequestReciever;
import com.snaps.mobile.product_native_ui.json.detail.SnapsProductNormalOptionItem;
import com.snaps.mobile.utils.sns.SNSBookTitleLengthCalculatorBase;
import com.snaps.mobile.utils.sns.SNSBookTitleLengthCalculatorFactory;

/**
 * Created by songhw on 2016. 10. 24..
 * normal option 제목입력
 */
public class InputLayout extends LinkedLayout {
    private static final String TAG = InputLayout.class.getSimpleName();
     private static final int LIMIT_TITLE_TEXT_LENGTH = 15;

    private SnapsProductNormalOptionItem cellData;

    private SNSBookTitleLengthCalculatorBase titleLengthCalculator;

    private InputLayout(Context context) {
        super(context);
    }

    public static InputLayout createInstance(Context context, LayoutRequestReciever reciever) {
        InputLayout instance = new InputLayout(context);
        instance.type = Type.Selector;
        instance.reciever = reciever;
        return instance;
    }

    @Override
    public void draw(ViewGroup parent, Object data, int headViewId, int id) {
        if( !(data instanceof SnapsProductNormalOptionItem) ) return;
        cellData = (SnapsProductNormalOptionItem) data;

        ViewGroup container = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.detail_layout_input, null);
        ( (TextView) container.findViewById(R.id.title) ).setText( cellData.getName() );

        final EditText input = (EditText) container.findViewById( R.id.content );
        if (input != null) {

            input.setHint(cellData.getPlaceHolder());

            titleLengthCalculator = getSNSBookTitleLengthCalculator(cellData); //글자 수를 유동적으로 제한한다. json 파일에 font, maxWidth 정보가 포함되어 있다.
            if (titleLengthCalculator == null) {
                input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(LIMIT_TITLE_TEXT_LENGTH)}); //기본 15자 제한
            }

            input.addTextChangedListener(new TextWatcher() {
                int lastCursorPosition = 0;
                String lastString = "";

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    lastCursorPosition = input.getSelectionStart();
                    lastString = input.getText().toString();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s == null || cellData == null) return;

                    input.removeTextChangedListener(this);

                    if (titleLengthCalculator != null) {
                        if (!titleLengthCalculator.isAllowTitleLength(s.toString(), cellData.getCellMaxWidthInteger())) {
                            input.setText(lastString);
                        }
                     }

                    if (input.getLineCount() > 2) {
                        input.setText(lastString);
                        input.setSelection(lastCursorPosition);
                    } else
                        lastString = input.getText().toString();

                    input.addTextChangedListener(this);

                    if (reciever != null)
                        reciever.itemSelected(cellData.getParameter(), s.toString(), true);
                }
            });

            input.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setEditTextVisible(input);
                }
            });

            input.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus)
                        setEditTextVisible(input);
                }
            });
        }

        if( reciever != null )
            reciever.itemSelected( cellData.getParameter(), "", true );

        addView(container);
        parent.addView(this);
    }

    private SNSBookTitleLengthCalculatorBase getSNSBookTitleLengthCalculator(SnapsProductNormalOptionItem option) {
        if (option == null) return null;
        try {
            return SNSBookTitleLengthCalculatorFactory.createSNSBookTitleLengthCalculator(option.getFont());
        } catch (Exception e) {
            Dlog.e(TAG, e);
            return null;
        }
    }

    private void setEditTextVisible( EditText editText ) {
        if( reciever != null )
            reciever.onEditTextFocused( editText );
    }

    @Override
    public void destroy() {
        super.destroy();

        if (titleLengthCalculator != null)
            titleLengthCalculator.releaseInstance();
    }
}
