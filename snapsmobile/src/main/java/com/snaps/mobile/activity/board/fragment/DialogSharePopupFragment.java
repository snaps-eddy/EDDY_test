package com.snaps.mobile.activity.board.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.snaps.common.utils.log.Dlog;
import com.snaps.mobile.R;
import com.snaps.mobile.activity.board.BaseMyArtworkDetail;

import errorhandle.logger.SnapsLogger;
import errorhandle.logger.model.SnapsLoggerClass;

public class DialogSharePopupFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = DialogSharePopupFragment.class.getSimpleName();

    public static DialogSharePopupFragment newInstance() {
        return new DialogSharePopupFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SnapsLogger.appendClassTrackingLog(new SnapsLoggerClass<>(this));

        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dialog_share_popup_fragment, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int paddBottom = 0, paddRight = 0;
        BaseMyArtworkDetail baseMyArt = (BaseMyArtworkDetail) getActivity();
        paddBottom = baseMyArt.getSharePaddingBottom();
        paddRight = baseMyArt.getSharePaddingRight();
        Dlog.d("onCreateView()" + "paddBottom:" + paddBottom + ", paddRight:" + paddRight);

        FrameLayout layoutShare = (FrameLayout) v.findViewById(R.id.layoutShare);
        layoutShare.setPadding(0, paddBottom, paddRight, 0);

        Window window = getDialog().getWindow();
        window.setGravity(Gravity.TOP | Gravity.RIGHT);

        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = 0.0f;

        window.setAttributes(params);

        View btn_share_kakaotalk = v.findViewById(R.id.btn_share_kakaotalk);
        btn_share_kakaotalk.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        try {
            ((BaseMyArtworkDetail) getActivity()).onClick(v);
            dismiss();
        } catch (Exception e) {
            Dlog.e(TAG, e);
        }
    }
}
